package com.example.mathia

import androidx.compose.ui.graphics.Color

object AppColors {
    // New palette colors
    val MathiaRed = Color(0xFFD9303E)
    val MathiaBurgundy = Color(0xFF732231)
    val MathiaNavy = Color(0xFF231640)
    val MathiaTeal = Color(0xFF11594C)
    val MathiaGold = Color(0xFFD9C771)

    // Mapping existing color names to avoid compilation errors and re-theme dynamically
    val Purple = MathiaNavy
    val PurpleLight = Color(0xFFF5ECE1) // WarmBeige
    val Pink = MathiaRed
    val PinkLight = Color(0xFFFFF0F5)
    val Green = MathiaTeal
    val GreenLight = Color(0xFFE6F9EC)
    val Amber = MathiaGold
    val AmberLight = Color(0xFFFFFCE6)
    val Blue = MathiaNavy
    val Red = MathiaRed
    val Bg = Color(0xFFFDFBF7)
    val White = Color(0xFFFFFFFF)
    
    // Grays mapped to warmer tones
    val Gray100 = Color(0xFFF9F6F0)
    val Gray200 = Color(0xFFEAE3D5)
    val Gray400 = Color(0xFFBCAFA0)
    val Gray500 = Color(0xFF8F7E6D)
    val Gray600 = Color(0xFF6E5F50)
    val Gray700 = Color(0xFF4E3F31)
    val Gray800 = MathiaNavy
}
