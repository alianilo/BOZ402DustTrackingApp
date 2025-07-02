package com.example.dusttrackingapp.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

/**
 * Uygulamanın kök teması.
 *
 * @param darkTheme   Sistem karanlık modunu kullanmak istemiyorsanız manuel belirleyin.
 * @param dynamicColor Android 12+ dinamik renk paletini (Material You) kullanmak için true bırakın.
 */
@Composable
fun DustTrackingTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    /* Dinamik renk destekleniyorsa kullan; değilse statik palete dön. */
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColors
        else      -> LightColors
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography  = DustTrackingTypography,
        shapes      = DustTrackingShapes,
        content     = content
    )
}
