package com.battlebucks.ui.theme


import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF0D7EF2),
    onPrimary = Color.White,

    background = Color(0xFFF4F5F7),
    onBackground = Color(0xFF111111),

    surface = Color.White,
    onSurface = Color(0xFF111111),

    surfaceVariant = Color(0xFFEDEDED),
    onSurfaceVariant = Color(0xFF666666),
    error = Color(0xFFE53935)
)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF4EA3FF),
    onPrimary = Color.Black,

    background = Color(0xFF101010),
    onBackground = Color(0xFFF5F5F5),

    surface = Color(0xFF1B1B1B),
    onSurface = Color(0xFFF5F5F5),

    surfaceVariant = Color(0xFF2A2A2A),
    onSurfaceVariant = Color(0xFFBDBDBD),

    error = Color(0xFFFF6B6B)
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val view = LocalView.current

    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window

            window.statusBarColor = colorScheme.background.toArgb()
            window.navigationBarColor = colorScheme.background.toArgb()

            val insetsController = WindowCompat.getInsetsController(window, view)
            insetsController.isAppearanceLightStatusBars = !darkTheme
            insetsController.isAppearanceLightNavigationBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
