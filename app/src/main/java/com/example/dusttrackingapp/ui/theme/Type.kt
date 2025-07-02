package com.example.dusttrackingapp.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/*
    implementation("androidx.compose.ui:ui-text-google-fonts:1.0.0")
 */

val DustTrackingTypography = Typography(
    headlineLarge   = TextStyle(
        fontFamily = FontFamily.Serif,
        fontWeight = FontWeight.SemiBold,
        fontSize   = 28.sp,
        lineHeight = 32.sp,
    ),
    headlineMedium  = TextStyle(
        fontFamily = FontFamily.Serif,
        fontWeight = FontWeight.Medium,
        fontSize   = 24.sp,
        lineHeight = 28.sp,
    ),
    bodyLarge      = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Normal,
        fontSize   = 16.sp,
        lineHeight = 24.sp,
    ),
    labelLarge     = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Medium,
        fontSize   = 14.sp,
        lineHeight = 20.sp,
    ),
    /* Diğer stiller – varsayılan bırakılabilir */
)
