package com.smesouthafrica.app.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val SME_Green = Color(0xFF1E8E3E)
private val SME_Green_Dark = Color(0xFF156A2E)

private val LightColorScheme = lightColorScheme(
    primary = SME_Green,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD4F0DA),
    secondary = SME_Green_Dark,
    background = Color.White,
    surface = Color.White
)

@Composable
fun SMESouthAfricaTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = LightColorScheme, content = content)
}
