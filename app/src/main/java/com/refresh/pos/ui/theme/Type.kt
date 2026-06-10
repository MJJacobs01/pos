package com.refresh.pos.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/**
 * Typography tuned on top of the Material3 defaults: stronger titles for a
 * more confident, modern feel; comfortable body metrics. Uses the system
 * font (no bundled font assets).
 */
private val default = Typography()

val AppTypography = Typography(
    headlineSmall = default.headlineSmall.copy(
        fontWeight = FontWeight.SemiBold
    ),
    titleLarge = default.titleLarge.copy(
        fontWeight = FontWeight.SemiBold
    ),
    titleMedium = default.titleMedium.copy(
        fontWeight = FontWeight.SemiBold
    ),
    labelLarge = default.labelLarge.copy(
        fontWeight = FontWeight.SemiBold
    ),
    bodyMedium = default.bodyMedium.copy(
        lineHeight = 22.sp
    ),
    bodySmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 13.sp,
        lineHeight = 18.sp,
        letterSpacing = 0.2.sp
    ),
)
