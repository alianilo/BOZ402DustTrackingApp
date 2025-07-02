package com.example.dusttrackingapp.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

/*  ── Işık paleti (statik) ────────────────────────────────────────────── */
private val md_light_primary            = Color(0xFF006875)
private val md_light_onPrimary          = Color.White
private val md_light_primaryContainer   = Color(0xFF97F0FF)
private val md_light_onPrimaryContainer = Color(0xFF001F24)

private val md_light_secondary          = Color(0xFF4A6268)
private val md_light_onSecondary        = Color.White
private val md_light_secondaryContainer = Color(0xFFCCE7EE)
private val md_light_onSecondaryContainer = Color(0xFF051F24)

private val md_light_tertiary           = Color(0xFF5B5C7E)
private val md_light_onTertiary         = Color.White
private val md_light_tertiaryContainer  = Color(0xFFE1E0FF)
private val md_light_onTertiaryContainer = Color(0xFF181B37)

private val md_light_error              = Color(0xFFBA1A1A)
private val md_light_errorContainer     = Color(0xFFFFDAD6)
private val md_light_onError            = Color.White
private val md_light_onErrorContainer   = Color(0xFF410002)

private val md_light_background         = Color(0xFFFBFCFD)
private val md_light_onBackground       = Color(0xFF191C1D)
private val md_light_surface            = Color(0xFFFBFCFD)
private val md_light_onSurface          = Color(0xFF191C1D)
private val md_light_surfaceVariant     = Color(0xFFDBE4E6)
private val md_light_onSurfaceVariant   = Color(0xFF3F484A)
private val md_light_outline            = Color(0xFF6F797B)

/*  ── Karanlık paleti (statik) ────────────────────────────────────────── */
private val md_dark_primary             = Color(0xFF4FD8EB)
private val md_dark_onPrimary           = Color(0xFF00363E)
private val md_dark_primaryContainer    = Color(0xFF004E5A)
private val md_dark_onPrimaryContainer  = Color(0xFF97F0FF)

private val md_dark_secondary           = Color(0xFFF0B8BC)
private val md_dark_onSecondary         = Color(0xFF5C1F25)
private val md_dark_secondaryContainer  = Color(0xFF78353B)
private val md_dark_onSecondaryContainer = Color(0xFFFFDADA)

private val md_dark_tertiary            = Color(0xFFC4C3EA)
private val md_dark_onTertiary          = Color(0xFF2C2F54)
private val md_dark_tertiaryContainer   = Color(0xFF43456B)
private val md_dark_onTertiaryContainer = Color(0xFFE1E0FF)

private val md_dark_error               = Color(0xFFFFB4AB)
private val md_dark_errorContainer      = Color(0xFF93000A)
private val md_dark_onError             = Color(0xFF690005)
private val md_dark_onErrorContainer    = Color(0xFFFFDAD6)

private val md_dark_background          = Color(0xFF191C1D)
private val md_dark_onBackground        = Color(0xFFE2E3E3)
private val md_dark_surface             = Color(0xFF191C1D)
private val md_dark_onSurface           = Color(0xFFE2E3E3)
private val md_dark_surfaceVariant      = Color(0xFF3F484A)
private val md_dark_onSurfaceVariant    = Color(0xFFBFC8CA)
private val md_dark_outline             = Color(0xFF899295)

/*  ── ColorScheme nesneleri ───────────────────────────────────────────── */
val LightColors = lightColorScheme(
    primary            = md_light_primary,
    onPrimary          = md_light_onPrimary,
    primaryContainer   = md_light_primaryContainer,
    onPrimaryContainer = md_light_onPrimaryContainer,
    secondary          = md_light_secondary,
    onSecondary        = md_light_onSecondary,
    secondaryContainer = md_light_secondaryContainer,
    onSecondaryContainer = md_light_onSecondaryContainer,
    tertiary           = md_light_tertiary,
    onTertiary         = md_light_onTertiary,
    tertiaryContainer  = md_light_tertiaryContainer,
    onTertiaryContainer = md_light_onTertiaryContainer,
    error              = md_light_error,
    onError            = md_light_onError,
    errorContainer     = md_light_errorContainer,
    onErrorContainer   = md_light_onErrorContainer,
    background         = md_light_background,
    onBackground       = md_light_onBackground,
    surface            = md_light_surface,
    onSurface          = md_light_onSurface,
    surfaceVariant     = md_light_surfaceVariant,
    onSurfaceVariant   = md_light_onSurfaceVariant,
    outline            = md_light_outline,
)

val DarkColors = darkColorScheme(
    primary            = md_dark_primary,
    onPrimary          = md_dark_onPrimary,
    primaryContainer   = md_dark_primaryContainer,
    onPrimaryContainer = md_dark_onPrimaryContainer,
    secondary          = md_dark_secondary,
    onSecondary        = md_dark_onSecondary,
    secondaryContainer = md_dark_secondaryContainer,
    onSecondaryContainer = md_dark_onSecondaryContainer,
    tertiary           = md_dark_tertiary,
    onTertiary         = md_dark_onTertiary,
    tertiaryContainer  = md_dark_tertiaryContainer,
    onTertiaryContainer = md_dark_onTertiaryContainer,
    error              = md_dark_error,
    onError            = md_dark_onError,
    errorContainer     = md_dark_errorContainer,
    onErrorContainer   = md_dark_onErrorContainer,
    background         = md_dark_background,
    onBackground       = md_dark_onBackground,
    surface            = md_dark_surface,
    onSurface          = md_dark_onSurface,
    surfaceVariant     = md_dark_surfaceVariant,
    onSurfaceVariant   = md_dark_onSurfaceVariant,
    outline            = md_dark_outline,
)
