package com.example.mathia.ui.theme

import android.view.Gravity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.example.mathia.viewModels.ThemeMode

private val DarkColorScheme = darkColorScheme(
    primary = MathiaNavy,
    secondary = MathiaTeal,
    tertiary = MathiaRed
)

val Purple = Color(0xFFC8A2C8)
val Gray800 = Color(0xFF424242)
val Purple2 = Color(0xFF800080)
val DarrkGray = Color(0xFFA9A9A9)

val Grayligt = Color(0xFFD3D3D3)

val Gray2Dinamico: Color
@Composable
get() = if(MaterialTheme.colorScheme.background == Color(0xFF121212)){
    Color.White
}else{
    Gray800
}


val GrayDinamico: Color
@Composable
get() = if(MaterialTheme.colorScheme.background == Color(0xFF121212)){
    DarrkGray
}else{
    Color.White
}

val Purple2Dinamico: Color
@Composable
get() = if(MaterialTheme.colorScheme.background == Color(0xFF121212)){
    Color.White
}else{
    Purple2
}

val PurpleDinamico: Color
@Composable
 get() = if(MaterialTheme.colorScheme.background == Color(0xFF121212)){
     Color.White
 }else{
     Purple
 }

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
    themeMode: ThemeMode = ThemeMode.SYSTEM, // 1. Recibimos la elección del usuario
    content: @Composable () -> Unit
) {
    // 2. Traducimos la elección a un simple booleano (verdadero/falso)
    val darkTheme = when (themeMode) {
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
    }

    // 3. Mantenemos tus colores intactos
    val colorScheme = if (darkTheme) {
        darkColorScheme(
            background = Color(0xFF121212),
            surface = Color(0xFF1E1E1E),
            primary = Color(0xFFD9303E),
        )
    } else {
        lightColorScheme(
            background = MathiaBg, // Asegúrate de que estas variables estén definidas
            surface = MathiaWhite,
            primary = MathiaRed,
        )
    }

    // 4. Aplicamos el tema
    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}
