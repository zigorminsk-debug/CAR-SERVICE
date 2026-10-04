package ru.carservice.app

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val Navy = Color(0xFF102A43)
private val Blue = Color(0xFF2F80ED)
private val Ink = Color(0xFF172B4D)
private val Canvas = Color(0xFFF6F7F9)
private val Muted = Color(0xFF627D98)
private val Line = Color(0xFFD9E2EC)
private val Amber = Color(0xFFFFB547)

val CarNavy = Navy
val CarBlue = Blue
val CarInk = Ink
val CarCanvas = Canvas
val CarMuted = Muted
val CarLine = Line
val CarAmber = Amber
val CardWhite = Color(0xFFFFFFFF)
val SoftBlue = Color(0xFFEAF2FF)
val SoftAmber = Color(0xFFFFF4DE)
val SoftGreen = Color(0xFFE8F7F3)

@Composable
fun CarServiceTheme(content: @Composable () -> Unit) {
    val colors = lightColorScheme(
        primary = Navy,
        onPrimary = Color.White,
        secondary = Blue,
        onSecondary = Color.White,
        background = Canvas,
        onBackground = Ink,
        surface = Color.White,
        onSurface = Ink,
        surfaceVariant = Color(0xFFEDF1F5),
        onSurfaceVariant = Muted,
        outline = Line
    )
    MaterialTheme(colorScheme = colors, content = content)
}
