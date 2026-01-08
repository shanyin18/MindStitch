package com.mindstitch.app.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = Primary,
    onPrimary = OnPrimary,
    primaryContainer = PrimaryLight,
    onPrimaryContainer = OnPrimary,
    secondary = Indigo,
    onSecondary = OnPrimary,
    secondaryContainer = IndigoLight,
    tertiary = Purple,
    onTertiary = OnPrimary,
    tertiaryContainer = PurpleLight,
    background = BackgroundDark,
    onBackground = OnSurfaceDark,
    surface = SurfaceDark,
    onSurface = OnSurfaceDark,
    surfaceVariant = CardDark,
    onSurfaceVariant = OnSurfaceVariantDark,
    outline = BorderDark,
    outlineVariant = BorderDark,
    inverseSurface = SurfaceLight,
    inverseOnSurface = OnSurfaceLight,
    error = Rose,
    onError = OnPrimary,
    errorContainer = RoseLight,
)

private val LightColorScheme = lightColorScheme(
    primary = Primary,
    onPrimary = OnPrimary,
    primaryContainer = PrimarySuperLight,
    onPrimaryContainer = Primary,
    secondary = Indigo,
    onSecondary = OnPrimary,
    secondaryContainer = IndigoLight,
    tertiary = Purple,
    onTertiary = OnPrimary,
    tertiaryContainer = PurpleLight,
    background = BackgroundLight,
    onBackground = OnSurfaceLight,
    surface = SurfaceLight,
    onSurface = OnSurfaceLight,
    surfaceVariant = CardLight,
    onSurfaceVariant = OnSurfaceVariantLight,
    outline = BorderLight,
    outlineVariant = BorderLight,
    inverseSurface = SurfaceDark,
    inverseOnSurface = OnSurfaceDark,
    error = Rose,
    onError = OnPrimary,
    errorContainer = RoseLight,
)

@Composable
fun MindStitchTheme(
    darkTheme: Boolean = true, // 默认使用深色主题以匹配原型
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            window.navigationBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = !darkTheme
                isAppearanceLightNavigationBars = !darkTheme
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
