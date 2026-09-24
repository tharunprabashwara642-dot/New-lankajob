package com.example.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

// Primary Brand Color: Trustworthy Sapphire / Corporate Navy
val BrandBlue = Color(0xFF0F52BA)
val BrandBlueDark = Color(0xFF0A3A85)
val BrandBlueLight = Color(0xFF3B82F6)
val BrandBlueContainer = Color(0xFFEBF3FF)
val OnBrandBlueContainer = Color(0xFF0B3B82)

// Slate Neutrals
val Slate950 = Color(0xFF090D16)
val Slate900 = Color(0xFF0F172A)
val Slate800 = Color(0xFF1E293B)
val Slate700 = Color(0xFF334155)
val Slate600 = Color(0xFF475569)
val Slate500 = Color(0xFF64748B)
val Slate400 = Color(0xFF94A3B8)
val Slate300 = Color(0xFFCBD5E1)
val Slate200 = Color(0xFFE2E8F0)
val Slate100 = Color(0xFFF1F5F9)
val Slate50 = Color(0xFFF8FAFC)

// Semantic Accents
val SuccessGreen = Color(0xFF16A34A)
val SuccessContainer = Color(0xFFDCFCE7)
val OnSuccessContainer = Color(0xFF14532D)

val WarningAmber = Color(0xFFD97706)
val WarningContainer = Color(0xFFFEF3C7)
val OnWarningContainer = Color(0xFF78350F)

val ErrorRed = Color(0xFFDC2626)
val ErrorContainer = Color(0xFFFEE2E2)
val OnErrorContainer = Color(0xFF7F1D1D)

val PureWhite = Color(0xFFFFFFFF)

/**
 * Dedicated semantic design tokens for LankaJobs
 */
@Immutable
data class LankaJobsTokens(
    val background: Color,
    val surface: Color,
    val surfaceElevated: Color,
    val primary: Color,
    val primaryContainer: Color,
    val secondary: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val textMuted: Color,
    val border: Color,
    val success: Color,
    val warning: Color,
    val error: Color,
)

val LightTokens = LankaJobsTokens(
    background = Color(0xFFF6F8FC),
    surface = PureWhite,
    surfaceElevated = PureWhite,
    primary = BrandBlue,
    primaryContainer = BrandBlueContainer,
    secondary = Slate800,
    textPrimary = Slate900,
    textSecondary = Slate600,
    textMuted = Slate400,
    border = Slate200,
    success = SuccessGreen,
    warning = WarningAmber,
    error = ErrorRed,
)

val DarkTokens = LankaJobsTokens(
    background = Color(0xFF0B111D),
    surface = Color(0xFF131C2E),
    surfaceElevated = Color(0xFF1A263D),
    primary = Color(0xFF60A5FA),
    primaryContainer = Color(0xFF1E3A8A),
    secondary = Color(0xFF94A3B8),
    textPrimary = Color(0xFFF8FAFC),
    textSecondary = Color(0xFFCBD5E1),
    textMuted = Color(0xFF64748B),
    border = Color(0xFF26354D),
    success = Color(0xFF4ADE80),
    warning = Color(0xFFFBBF24),
    error = Color(0xFFF87171),
)

val LocalLankaJobsTokens = staticCompositionLocalOf { LightTokens }
