package com.smesouthafrica.app.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val SME_Green = Color(0xFF1E8E3E)
private val SME_Green_Light = Color(0xFFD4F0DA)

private val LightScheme = lightColorScheme(
    primary = SME_Green,
    onPrimary = Color.White,
    primaryContainer = SME_Green_Light,
    onPrimaryContainer = Color(0xFF002108),
    secondary = Color(0xFF156A2E),
    background = Color(0xFFFCFDF7),
    onBackground = Color(0xFF1A1C19),
    surface = Color.White,
    onSurface = Color(0xFF1A1C19),
    surfaceVariant = Color(0xFFE0E4DB),
    onSurfaceVariant = Color(0xFF43483E),
    outline = Color(0xFF73796E)
)

private val DarkScheme = darkColorScheme(
    primary = Color(0xFF79DB8C),
    onPrimary = Color(0xFF00390A),
    primaryContainer = Color(0xFF005313),
    onPrimaryContainer = Color(0xFFD4F0DA),
    secondary = Color(0xFFBCD0B3),
    background = Color(0xFF121212),
    onBackground = Color(0xFFE2E3DC),
    surface = Color(0xFF1E1E1E),
    onSurface = Color(0xFFE2E3DC),
    surfaceVariant = Color(0xFF2F362A),
    onSurfaceVariant = Color(0xFFC3C8BB),
    outline = Color(0xFF8D9387),
    surfaceContainer = Color(0xFF252525),
    surfaceContainerHigh = Color(0xFF2D2D2D)
)

@Composable
fun SMESouthAfricaTheme(darkTheme: Boolean, content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = if (darkTheme) DarkScheme else LightScheme, content = content)
}
