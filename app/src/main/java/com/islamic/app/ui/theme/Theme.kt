package com.islamic.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = EmeraldPrimary,
    secondary = MintSecondary,
    background = DarkOledBlack,
    surface = DarkSurfaceCard,
    onPrimary = DarkOledBlack,
    onBackground = TextPureWhite,
    onSurface = TextPureWhite
)

@Composable
fun IslamicAppTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = DarkColorScheme, content = content)
}
