package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = InfinityTeal,
    secondary = InfinityPurple,
    tertiary = InfinityPink,
    background = CosmicBackground,
    surface = CosmicSurface,
    surfaceVariant = CosmicSurfaceVariant,
    onBackground = OnCosmicBackground,
    onSurface = OnCosmicSurface,
    onPrimary = CosmicBackground,
    onSecondary = OnCosmicBackground
)

private val LightColorScheme = lightColorScheme(
    primary = InfinityPurple,
    secondary = InfinityTeal,
    tertiary = InfinityPink,
    background = OnCosmicBackground,
    surface = OnCosmicSurface,
    onBackground = CosmicBackground,
    onSurface = CosmicBackground
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Force dark theme by default for premium gamer/tech aesthetic
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
