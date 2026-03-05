package com.gobidev.tmdbv1.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val NetflixColorScheme = darkColorScheme(
    // Primary — Netflix Red for CTAs, active sort icon, TextButton text
    primary = NetflixRed,
    onPrimary = NetflixWhite,
    primaryContainer = NetflixTopBar,       // TopAppBar background (all screens)
    onPrimaryContainer = NetflixWhite,      // TopAppBar title and icons

    // Secondary — used by NavigationBar indicator pill (selected item)
    secondary = NetflixMediumGrey,
    onSecondary = NetflixBlack,
    secondaryContainer = NetflixRed,        // Nav bar selected pill → Netflix Red
    onSecondaryContainer = NetflixWhite,    // Nav bar selected icon/label

    // Tertiary — unused, neutral fallback
    tertiary = NetflixMediumGrey,
    onTertiary = NetflixWhite,
    tertiaryContainer = NetflixSurfaceVariant,
    onTertiaryContainer = NetflixLightGrey,

    // Backgrounds and surfaces
    background = NetflixBlack,
    onBackground = NetflixWhite,
    surface = NetflixSurface,               // Cards, ModalBottomSheet
    onSurface = NetflixWhite,
    surfaceVariant = NetflixSurfaceVariant, // Genre chips, rating panel
    onSurfaceVariant = NetflixMediumGrey,   // Secondary text, unselected nav icons

    // Error
    error = NetflixError,
    onError = NetflixWhite,
    errorContainer = NetflixDarkRed,
    onErrorContainer = NetflixWhite,

    // Structural
    outline = NetflixDarkGrey,
    outlineVariant = NetflixSurfaceVariant,
    scrim = NetflixBlack,
    inverseSurface = NetflixLightGrey,
    inverseOnSurface = NetflixBlack,
    inversePrimary = NetflixDarkRed,
)

@Composable
fun TMDBTheme(content: @Composable () -> Unit) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            // Keep status bar icons light (white) for dark background
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    MaterialTheme(
        colorScheme = NetflixColorScheme,
        typography = Typography,
        content = content
    )
}
