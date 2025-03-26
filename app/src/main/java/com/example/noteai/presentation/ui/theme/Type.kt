package com.example.noteai.presentation.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.unit.sp
import com.example.noteai.R

// Set of Material typography styles to start with

private val provider = GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage = "com.google.android.gms",
    certificates = R.array.com_google_android_gms_fonts_certs
)

val RubikFontFamily = FontFamily(
    Font(googleFont = GoogleFont("Rubik"), fontProvider = provider, weight = FontWeight.W600),
    Font(googleFont = GoogleFont("Rubik"), fontProvider = provider, weight = FontWeight.W500),
    Font(googleFont = GoogleFont("Rubik"), fontProvider = provider, weight = FontWeight.W400)
)

val PoppinsFontFamily = FontFamily(
    Font(googleFont = GoogleFont("Poppins"), fontProvider = provider, weight = FontWeight.W400),
    Font(googleFont = GoogleFont("Poppins"), fontProvider = provider, weight = FontWeight.W700)
)

val Typography = Typography(
    titleLarge = TextStyle(
        fontFamily = RubikFontFamily,
        fontWeight = FontWeight.W600,
        fontSize = 22.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = PoppinsFontFamily,
        fontWeight = FontWeight.W700,
        fontSize = 20.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = RubikFontFamily,
        fontWeight = FontWeight.W400,
        fontSize = 15.sp
    ),
    bodySmall = TextStyle(
        fontFamily = RubikFontFamily,
        fontWeight = FontWeight.W400,
        fontSize = 10.sp,
    ),
    labelLarge = TextStyle(
        fontFamily = PoppinsFontFamily,
        fontWeight = FontWeight.W400,
        fontSize = 15.sp
    ),
    labelSmall = TextStyle(
        fontFamily = PoppinsFontFamily,
        fontWeight = FontWeight.W400,
        fontSize = 10.sp,
    ),
    headlineLarge = TextStyle(
        fontFamily = RubikFontFamily,
        fontWeight = FontWeight.W500,
        fontSize = 21.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = RubikFontFamily,
        fontWeight = FontWeight.W500,
        fontSize = 21.sp
    )
)