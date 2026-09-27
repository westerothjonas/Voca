package com.example.voca.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightVocaColorScheme = lightColorScheme(
    primary = VocaPrimary,
    onPrimary = VocaOnPrimary,
    primaryContainer = VocaPrimaryContainer,
    onPrimaryContainer = VocaOnPrimaryContainer,
    secondary = VocaSecondary,
    onSecondary = VocaOnSecondary,
    secondaryContainer = VocaSecondaryContainer,
    onSecondaryContainer = VocaOnSecondaryContainer,
    tertiary = VocaTertiary,
    onTertiary = VocaOnTertiary,
    tertiaryContainer = VocaTertiaryContainer,
    onTertiaryContainer = VocaOnTertiaryContainer,
    background = VocaBackground,
    onBackground = VocaOnBackground,
    surface = VocaSurface,
    onSurface = VocaOnSurface,
    surfaceVariant = VocaSurfaceVariant,
    onSurfaceVariant = VocaOnSurfaceVariant,
    outline = VocaOutline,
    outlineVariant = VocaOutlineVariant,
    surfaceContainerLow = VocaSurfaceContainerLow,
    surfaceContainer = VocaSurfaceContainer,
    surfaceContainerHigh = VocaSurfaceContainerHigh
)

@Composable
fun VocaTheme(
    darkTheme: Boolean = false,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LightVocaColorScheme,
        typography = Typography,
        content = content
    )
}
