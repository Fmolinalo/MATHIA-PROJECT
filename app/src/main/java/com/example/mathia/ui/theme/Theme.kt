package com.example.mathia.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color // IMPORTANTE: Agregamos el import de Color

private val DarkColorScheme = darkColorScheme(
    primary = MathiaNavy,
    secondary = MathiaTeal,
    tertiary = MathiaRed
)

// 1. MODIFICAMOS EL ESQUEMA CLARO
private val LightColorScheme = lightColorScheme(
    primary = MathiaNavy,
    secondary = MathiaTeal,
    tertiary = MathiaRed,

    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = MathiaNavy, // <-- Texto general en color navy oscuro de la paleta
    onSurface = MathiaNavy     // <-- Texto sobre tarjetas en color navy oscuro de la paleta
)

@Composable
fun MathkidsTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    // 2. FORZAMOS EL TEMA CLARO (Ignoramos dynamicColor y darkTheme)
    // Esto garantiza que el diseño se vea idéntico en todos los celulares
    val colorScheme = LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}