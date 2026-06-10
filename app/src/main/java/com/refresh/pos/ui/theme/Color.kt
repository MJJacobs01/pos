package com.refresh.pos.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

/**
 * Refined indigo/blue brand palette with a cyan accent (tertiary).
 * Fixed (non-dynamic) so the app looks identical across devices.
 */

// Brand seeds
private val Indigo = Color(0xFF3F51F5)
private val Cyan = Color(0xFF00BCD4)

internal val LightColors = lightColorScheme(
    primary = Indigo,
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFE0E0FF),
    onPrimaryContainer = Color(0xFF00105C),

    secondary = Color(0xFF5B5D72),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFE0E1F9),
    onSecondaryContainer = Color(0xFF181A2C),

    tertiary = Cyan,
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFFACEEF8),
    onTertiaryContainer = Color(0xFF001F24),

    error = Color(0xFFBA1A1A),
    onError = Color(0xFFFFFFFF),
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002),

    background = Color(0xFFFBFBFF),
    onBackground = Color(0xFF1B1B21),
    surface = Color(0xFFFBFBFF),
    onSurface = Color(0xFF1B1B21),
    surfaceVariant = Color(0xFFE3E1EC),
    onSurfaceVariant = Color(0xFF46464F),
    surfaceContainer = Color(0xFFF1F0FA),
    surfaceContainerHigh = Color(0xFFEBEAF4),

    outline = Color(0xFF767680),
    outlineVariant = Color(0xFFC7C5D0),
)

internal val DarkColors = darkColorScheme(
    primary = Color(0xFFBEC2FF),
    onPrimary = Color(0xFF06208E),
    primaryContainer = Color(0xFF2839B0),
    onPrimaryContainer = Color(0xFFE0E0FF),

    secondary = Color(0xFFC4C5DD),
    onSecondary = Color(0xFF2D2F42),
    secondaryContainer = Color(0xFF434559),
    onSecondaryContainer = Color(0xFFE0E1F9),

    tertiary = Color(0xFF50D8E9),
    onTertiary = Color(0xFF00363D),
    tertiaryContainer = Color(0xFF004F58),
    onTertiaryContainer = Color(0xFFACEEF8),

    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6),

    background = Color(0xFF131318),
    onBackground = Color(0xFFE4E1E9),
    surface = Color(0xFF131318),
    onSurface = Color(0xFFE4E1E9),
    surfaceVariant = Color(0xFF46464F),
    onSurfaceVariant = Color(0xFFC7C5D0),
    surfaceContainer = Color(0xFF1F1F26),
    surfaceContainerHigh = Color(0xFF2A2931),

    outline = Color(0xFF90909A),
    outlineVariant = Color(0xFF46464F),
)
