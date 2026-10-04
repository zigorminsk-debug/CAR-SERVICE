package ru.carservice.app

import android.net.Uri
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView

private data class ResearchSource(val title: String, val url: (String) -> String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnlineResearchScreen(vehicle: Vehicle?, subject: String, onBack: () -> Unit) {
    val query = remember(vehicle?.id, subject) {
        buildResearchQuery(vehicle, subject)
    }
    val encodedQuery = remember(query) { Uri.encode(query) }
    val sources = remember(encodedQuery) {
        listOf(
            ResearchSource("Весь интернет") { "https://html.duckduckgo.com/html/?q=$encodedQuery" },
            ResearchSource("Форумы") {
                "https://www.google.com/search?q=${Uri.encode("$query (site:drive2.ru OR site:carmasters.org OR site:reddit.com/r/MechanicAdvice OR site:forum.ee)")}"
            },
            ResearchSource("Мануалы") {
                "https://www.google.com/search?q=${Uri.encode("$query (\"service manual\" OR \"workshop manual\" OR руководство по ремонту)")}"
            },
            ResearchSource("Видео") { "https://www.youtube.com/results?search_query=$encodedQuery" }
        )
    }
    var selectedSource by rememberSaveable(query) { mutableIntStateOf(0) }
    val currentUrl = sources[selectedSource].url(query)

    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp).padding(top = 17.dp, bottom = 17.dp)) {
        BackHeader(title = "Поиск по работе", subtitle = "Результаты открываются в приложении", onBack = onBack)
        Spacer(Modifier.height(14.dp))
        Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(17.dp), colors = CardDefaults.cardColors(containerColor = CarNavy)) {
            Row(modifier = Modifier.padding(15.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Outlined.Search, null, tint = CarAmber, modifier = Modifier.size(22.dp))
                Spacer(Modifier.width(10.dp))
                Column {
                    Text(subject, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Text(
                        vehicle?.let { "${it.title} · ${it.yearLabel} · ${it.engineCode}" } ?: "По всем автомобилям",
                        color = Color.White.copy(alpha = .72f), fontSize = 11.sp
                    )
                }
            }
        }
        Spacer(Modifier.height(11.dp))
        Row(modifier = Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(7.dp)) {
            sources.forEachIndexed { index, source ->
                FilterChip(selected = selectedSource == index, onClick = { selectedSource = index }, label = { Text(source.title) })
            }
        }
        Spacer(Modifier.height(10.dp))
        Surface(color = SoftAmber, shape = RoundedCornerShape(12.dp)) {
            Row(modifier = Modifier.padding(horizontal = 11.dp, vertical = 9.dp), verticalAlignment = Alignment.Top) {
                Icon(Icons.Outlined.Info, null, tint = Color(0xFF9A5B00), modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(7.dp))
                Text(
                    "Поиск собирает открытые материалы, форумы и видео по выбранной конфигурации. Проверяйте советы по VIN, коду двигателя и официальным нормам.",
                    color = Color(0xFF704100), fontSize = 10.sp, lineHeight = 14.sp
                )
            }
        }
        Spacer(Modifier.height(10.dp))
        Box(modifier = Modifier.fillMaxWidth().weight(1f).clip(RoundedCornerShape(16.dp)).background(Color.White)) {
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { context ->
                    WebView(context).apply {
                        webViewClient = WebViewClient()
                        // Static search pages work without JavaScript; keeping it off reduces attack surface.
                        settings.javaScriptEnabled = false
                        settings.domStorageEnabled = true
                        settings.builtInZoomControls = false
                        loadUrl(currentUrl)
                    }
                },
                update = { webView ->
                    if (webView.url != currentUrl) webView.loadUrl(currentUrl)
                }
            )
        }
    }
}

private fun buildResearchQuery(vehicle: Vehicle?, subject: String): String {
    val vehiclePart = vehicle?.let {
        "${it.brand} ${it.model} ${it.yearLabel} ${it.body} ${it.engineCode} ${it.engine}"
    } ?: "автомобиль ремонт"
    return "$vehiclePart $subject ремонт диагностика инструкция форум service manual"
}
