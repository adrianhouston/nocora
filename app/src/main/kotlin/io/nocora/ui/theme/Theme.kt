package io.nocora.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import io.nocora.R
import io.nocora.SettingsRepository

@Composable
fun AppTheme(content: @Composable() () -> Unit) {
    val lightScheme =
        lightColorScheme(
            primary = colorResource(R.color.md_theme_primary),
            onPrimary = colorResource(R.color.md_theme_onPrimary),
            primaryContainer = colorResource(R.color.md_theme_primaryContainer),
            onPrimaryContainer = colorResource(R.color.md_theme_onPrimaryContainer),
            secondary = colorResource(R.color.md_theme_secondary),
            onSecondary = colorResource(R.color.md_theme_onSecondary),
            secondaryContainer = colorResource(R.color.md_theme_secondaryContainer),
            onSecondaryContainer = colorResource(R.color.md_theme_onSecondaryContainer),
            tertiary = colorResource(R.color.md_theme_tertiary),
            onTertiary = colorResource(R.color.md_theme_onTertiary),
            tertiaryContainer = colorResource(R.color.md_theme_tertiaryContainer),
            onTertiaryContainer = colorResource(R.color.md_theme_onTertiaryContainer),
            error = colorResource(R.color.md_theme_error),
            onError = colorResource(R.color.md_theme_onError),
            errorContainer = colorResource(R.color.md_theme_errorContainer),
            onErrorContainer = colorResource(R.color.md_theme_onErrorContainer),
            background = colorResource(R.color.md_theme_background),
            onBackground = colorResource(R.color.md_theme_onBackground),
            surface = colorResource(R.color.md_theme_surface),
            onSurface = colorResource(R.color.md_theme_onSurface),
            surfaceVariant = colorResource(R.color.md_theme_surfaceVariant),
            onSurfaceVariant = colorResource(R.color.md_theme_onSurfaceVariant),
            outline = colorResource(R.color.md_theme_outline),
            outlineVariant = colorResource(R.color.md_theme_outlineVariant),
            scrim = colorResource(R.color.md_theme_scrim),
            inverseSurface = colorResource(R.color.md_theme_inverseSurface),
            inverseOnSurface = colorResource(R.color.md_theme_inverseOnSurface),
            inversePrimary = colorResource(R.color.md_theme_inversePrimary),
            surfaceDim = colorResource(R.color.md_theme_surfaceDim),
            surfaceBright = colorResource(R.color.md_theme_surfaceBright),
            surfaceContainerLowest = colorResource(R.color.md_theme_surfaceContainerLowest),
            surfaceContainerLow = colorResource(R.color.md_theme_surfaceContainerLow),
            surfaceContainer = colorResource(R.color.md_theme_surfaceContainer),
            surfaceContainerHigh = colorResource(R.color.md_theme_surfaceContainerHigh),
            surfaceContainerHighest = colorResource(R.color.md_theme_surfaceContainerHighest),
        )

    val darkScheme =
        darkColorScheme(
            primary = colorResource(R.color.md_theme_primary),
            onPrimary = colorResource(R.color.md_theme_onPrimary),
            primaryContainer = colorResource(R.color.md_theme_primaryContainer),
            onPrimaryContainer = colorResource(R.color.md_theme_onPrimaryContainer),
            secondary = colorResource(R.color.md_theme_secondary),
            onSecondary = colorResource(R.color.md_theme_onSecondary),
            secondaryContainer = colorResource(R.color.md_theme_secondaryContainer),
            onSecondaryContainer = colorResource(R.color.md_theme_onSecondaryContainer),
            tertiary = colorResource(R.color.md_theme_tertiary),
            onTertiary = colorResource(R.color.md_theme_onTertiary),
            tertiaryContainer = colorResource(R.color.md_theme_tertiaryContainer),
            onTertiaryContainer = colorResource(R.color.md_theme_onTertiaryContainer),
            error = colorResource(R.color.md_theme_error),
            onError = colorResource(R.color.md_theme_onError),
            errorContainer = colorResource(R.color.md_theme_errorContainer),
            onErrorContainer = colorResource(R.color.md_theme_onErrorContainer),
            background = colorResource(R.color.md_theme_background),
            onBackground = colorResource(R.color.md_theme_onBackground),
            surface = colorResource(R.color.md_theme_surface),
            onSurface = colorResource(R.color.md_theme_onSurface),
            surfaceVariant = colorResource(R.color.md_theme_surfaceVariant),
            onSurfaceVariant = colorResource(R.color.md_theme_onSurfaceVariant),
            outline = colorResource(R.color.md_theme_outline),
            outlineVariant = colorResource(R.color.md_theme_outlineVariant),
            scrim = colorResource(R.color.md_theme_scrim),
            inverseSurface = colorResource(R.color.md_theme_inverseSurface),
            inverseOnSurface = colorResource(R.color.md_theme_inverseOnSurface),
            inversePrimary = colorResource(R.color.md_theme_inversePrimary),
            surfaceDim = colorResource(R.color.md_theme_surfaceDim),
            surfaceBright = colorResource(R.color.md_theme_surfaceBright),
            surfaceContainerLowest = colorResource(R.color.md_theme_surfaceContainerLowest),
            surfaceContainerLow = colorResource(R.color.md_theme_surfaceContainerLow),
            surfaceContainer = colorResource(R.color.md_theme_surfaceContainer),
            surfaceContainerHigh = colorResource(R.color.md_theme_surfaceContainerHigh),
            surfaceContainerHighest = colorResource(R.color.md_theme_surfaceContainerHighest),
        )

    val isDynamicColor by SettingsRepository(LocalContext.current).isDynamicColor.collectAsState(initial = false)

    val isDarkTheme = isSystemInDarkTheme()

    val colorScheme =
        when {
            isDynamicColor && isDarkTheme -> dynamicDarkColorScheme(LocalContext.current)
            isDynamicColor -> dynamicLightColorScheme(LocalContext.current)
            isDarkTheme -> darkScheme
            else -> lightScheme
        }

    MaterialTheme(colorScheme = colorScheme, content = content)
}
