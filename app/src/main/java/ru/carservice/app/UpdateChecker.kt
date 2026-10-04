package ru.carservice.app

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.Settings
import android.app.DownloadManager
import androidx.core.content.ContextCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

object UpdateChecker {
    suspend fun latestRelease(): ReleaseInfo? = withContext(Dispatchers.IO) {
        runCatching {
            val connection = (URL("https://api.github.com/repos/${BuildConfig.UPDATE_REPOSITORY}/releases/latest").openConnection() as HttpURLConnection).apply {
                connectTimeout = 4_000
                readTimeout = 4_000
                requestMethod = "GET"
                setRequestProperty("Accept", "application/vnd.github+json")
                setRequestProperty("User-Agent", "CAR-SERVICE/${BuildConfig.VERSION_NAME}")
            }
            try {
                if (connection.responseCode !in 200..299) return@runCatching null
                val json = JSONObject(connection.inputStream.bufferedReader().use { it.readText() })
                val version = json.optString("tag_name").removePrefix("v")
                if (version.isBlank() || !isNewer(version, BuildConfig.VERSION_NAME)) return@runCatching null
                ReleaseInfo(
                    version = version,
                    url = json.optString("html_url"),
                    notes = json.optString("body"),
                    apkUrl = json.optJSONArray("assets")?.let { assets ->
                        (0 until assets.length())
                            .asSequence()
                            .map { assets.optJSONObject(it) }
                            .firstOrNull { it?.optString("name")?.endsWith(".apk", ignoreCase = true) == true }
                            ?.optString("browser_download_url")
                            ?.takeIf { it.isNotBlank() }
                    }
                )
            } finally {
                connection.disconnect()
            }
        }.getOrNull()
    }

    /** Downloads the APK from the GitHub Release and hands it to Android's package installer. */
    fun downloadAndInstall(context: Context, release: ReleaseInfo, onState: (String) -> Unit) {
        val apkUrl = release.apkUrl
        if (apkUrl.isNullOrBlank()) {
            onState("В релизе нет APK-файла — открываем страницу релиза")
            openRelease(context, release)
            return
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && !context.packageManager.canRequestPackageInstalls()) {
            onState("Разрешите установку из этого источника и нажмите «Обновить» ещё раз")
            val settingsIntent = Intent(
                Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES,
                Uri.parse("package:${context.packageName}")
            ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            runCatching { ContextCompat.startActivity(context, settingsIntent, null) }
            return
        }

        val downloadManager = context.getSystemService(DownloadManager::class.java)
        val fileName = "car-service-${release.version}.apk"
        val request = DownloadManager.Request(Uri.parse(apkUrl))
            .setTitle("АвтоСервис ${release.version}")
            .setDescription("Скачивание обновления")
            .setMimeType(APK_MIME)
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setAllowedOverMetered(true)
            .setAllowedOverRoaming(false)
            .setDestinationInExternalFilesDir(context, Environment.DIRECTORY_DOWNLOADS, fileName)

        var downloadId = -1L
        lateinit var receiver: BroadcastReceiver
        receiver = object : BroadcastReceiver() {
            override fun onReceive(receiverContext: Context, intent: Intent) {
                if (intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1L) != downloadId) return
                runCatching { receiverContext.unregisterReceiver(this) }
                val cursor = downloadManager.query(DownloadManager.Query().setFilterById(downloadId))
                val successful = cursor.use {
                    it != null && it.moveToFirst() &&
                        it.getInt(it.getColumnIndexOrThrow(DownloadManager.COLUMN_STATUS)) == DownloadManager.STATUS_SUCCESSFUL
                }
                if (successful) {
                    val downloadedUri = downloadManager.getUriForDownloadedFile(downloadId)
                    if (downloadedUri != null) {
                        onState("Загрузка завершена — открываем установщик")
                        launchInstaller(context, downloadedUri)
                    } else {
                        onState("Не удалось открыть скачанный APK")
                    }
                } else {
                    onState("Не удалось скачать обновление")
                }
            }
        }

        try {
            ContextCompat.registerReceiver(
                context,
                receiver,
                IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE),
                ContextCompat.RECEIVER_NOT_EXPORTED
            )
            downloadId = downloadManager.enqueue(request)
            onState("Скачивание обновления началось")
        } catch (_: Exception) {
            runCatching { context.unregisterReceiver(receiver) }
            onState("Не удалось начать скачивание — открываем страницу релиза")
            openRelease(context, release)
        }
    }

    private fun launchInstaller(context: Context, apkUri: Uri) {
        val installerIntent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(apkUri, APK_MIME)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        runCatching { ContextCompat.startActivity(context, installerIntent, null) }
            .onFailure { /* The release page remains available from the update banner. */ }
    }

    fun openRelease(context: Context, release: ReleaseInfo) {
        ContextCompat.startActivity(context, Intent(Intent.ACTION_VIEW, Uri.parse(release.url)), null)
    }

    private fun isNewer(remote: String, local: String): Boolean {
        fun parts(version: String) = version.split(".").map { it.takeWhile(Char::isDigit).toIntOrNull() ?: 0 }
        val a = parts(remote)
        val b = parts(local)
        for (i in 0 until maxOf(a.size, b.size)) {
            val left = a.getOrElse(i) { 0 }
            val right = b.getOrElse(i) { 0 }
            if (left != right) return left > right
        }
        return false
    }

    private const val APK_MIME = "application/vnd.android.package-archive"
}
