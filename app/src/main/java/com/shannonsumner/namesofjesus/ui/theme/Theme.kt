package com.shannonsumner.namesofjesus.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val ColorScheme = lightColorScheme(
    primary = Accent,
    onPrimary = PopupBackground,
    background = CurtainGreen,
    onBackground = TextPrimary,
    surface = PopupBackground,
    onSurface = TextPrimary,
    outlineVariant = Divider
)

/**
 * The app's theme. The design matches the companion website, so it always uses
 * the same light colors regardless of the system's dark-mode setting.
 */
@Composable
fun WonderfulNamesTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = ColorScheme, content = content)
}
