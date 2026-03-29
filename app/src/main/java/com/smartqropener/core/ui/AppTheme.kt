package com.smartqropener.core.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val SmartColorScheme = darkColorScheme(
    primary = Mint,
    secondary = Amber,
    tertiary = Coral,
    background = Ink,
    surface = Panel,
    surfaceVariant = PanelAlt,
    onPrimary = Ink,
    onSecondary = Ink,
    onTertiary = Ink,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
    onSurfaceVariant = TextSecondary,
)

@Composable
fun SmartQrTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = SmartColorScheme,
        typography = AppTypography,
        content = content,
    )
}

