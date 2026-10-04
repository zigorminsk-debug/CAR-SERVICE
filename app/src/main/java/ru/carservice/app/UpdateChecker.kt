package ru.carservice.app

import android.content.Context
import android.content.Intent
import android.net.Uri
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
            }
            try {
                if (connection.responseCode !in 200..299) return@runCatching null
                val json = JSONObject(connection.inputStream.bufferedReader().use { it.readText() })
                val version = json.optString("tag_name").removePrefix("v")
                if (version.isBlank() || !isNewer(version, BuildConfig.VERSION_NAME)) return@runCatching null
                ReleaseInfo(version, json.optString("html_url"), json.optString("body"))
            } finally {
                connection.disconnect()
            }
        }.getOrNull()
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

    fun openRelease(context: Context, release: ReleaseInfo) {
        ContextCompat.startActivity(context, Intent(Intent.ACTION_VIEW, Uri.parse(release.url)), null)
    }
}
