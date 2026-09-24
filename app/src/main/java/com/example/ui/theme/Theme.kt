package com.example.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LankaJobsLightColorScheme = lightColorScheme(
    primary = BrandBlue,
    onPrimary = PureWhite,
    primaryContainer = BrandBlueContainer,
    onPrimaryContainer = OnBrandBlueContainer,
    secondary = Slate800,
    onSecondary = PureWhite,
    secondaryContainer = Slate100,
    onSecondaryContainer = Slate900,
    tertiary = WarningAmber,
    onTertiary = PureWhite,
    tertiaryContainer = WarningContainer,
    onTertiaryContainer = OnWarningContainer,
    background = Color(0xFFF6F8FC),
    onBackground = Slate900,
    surface = PureWhite,
    onSurface = Slate900,
    surfaceVariant = Slate100,
    onSurfaceVariant = Slate600,
    outline = Slate200,
    outlineVariant = Slate300,
    error = ErrorRed,
    onError = PureWhite,
    errorContainer = ErrorContainer,
    onErrorContainer = OnErrorContainer
)

private val LankaJobsDarkColorScheme = darkColorScheme(
    primary = Color(0xFF60A5FA),
    onPrimary = Slate950,
    primaryContainer = Color(0xFF1E3A8A),
    onPrimaryContainer = Color(0xFFDBEAFE),
    secondary = Color(0xFF94A3B8),
    onSecondary = Slate950,
    secondaryContainer = Color(0xFF1E293B),
    onSecondaryContainer = Color(0xFFF1F5F9),
    tertiary = WarningAmber,
    onTertiary = PureWhite,
    background = Color(0xFF0B111D),
    onBackground = Color(0xFFF8FAFC),
    surface = Color(0xFF131C2E),
    onSurface = Color(0xFFF8FAFC),
    surfaceVariant = Color(0xFF1E293B),
    onSurfaceVariant = Slate400,
    outline = Color(0xFF26354D),
    outlineVariant = Color(0xFF334155),
    error = Color(0xFFF87171),
    onError = Slate950,
)

/**
 * Access LankaJobs semantic design tokens cleanly:
 * LankaJobsTheme.tokens.background, LankaJobsTheme.tokens.primary, etc.
 */
object LankaJobsTheme {
    val tokens: LankaJobsTokens
        @Composable
        @ReadOnlyComposable
        get() = LocalLankaJobsTokens.current
}

@Composable
fun LankaJobsAppTheme(
    darkTheme: Boolean = false, // Light-first by default as required
    dynamicColor: Boolean = false, // Keep brand integrity consistent across devices
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> LankaJobsDarkColorScheme
        else -> LankaJobsLightColorScheme
    }

    val tokens = if (darkTheme) DarkTokens else LightTokens

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                window.statusBarColor = Color.Transparent.toArgb()
                window.navigationBarColor = Color.Transparent.toArgb()
                val insetsController = WindowCompat.getInsetsController(window, view)
                insetsController.isAppearanceLightStatusBars = !darkTheme
                insetsController.isAppearanceLightNavigationBars = !darkTheme
            }
        }
    }

    CompositionLocalProvider(LocalLankaJobsTokens provides tokens) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}
