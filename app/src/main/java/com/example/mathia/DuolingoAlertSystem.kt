package com.example.mathia

import android.os.Build
import androidx.compose.animation.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.ImageLoader
import coil.compose.rememberAsyncImagePainter
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import com.example.mathia.model.AlertType
import com.example.mathia.model.DuolingoAlert

private data class AlertTheme(
    val bg: Color,
    val border: Color,
    val btnBg: Color,
    val btnShadow: Color,
    val btnText: Color,
    val emoji: String
)

@Composable
fun DuolingoAlertDialog(
    alert: DuolingoAlert,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current

    // Determine colors based on alert type using type-safe AlertTheme
    val theme = when (alert.type) {
        AlertType.STREAK -> AlertTheme(
            bg = Color(0xFFFFF2E6),
            border = Color(0xFFFFB266),
            btnBg = Color(0xFFFF8000),
            btnShadow = Color(0xFFCC6600),
            btnText = Color.White,
            emoji = "🔥"
        )
        AlertType.SUCCESS -> AlertTheme(
            bg = Color(0xFFE6F9EC),
            border = Color(0xFF6BCB77),
            btnBg = Color(0xFF4CAF50),
            btnShadow = Color(0xFF388E3C),
            btnText = Color.White,
            emoji = "⭐"
        )
        AlertType.MOTIVATIONAL -> AlertTheme(
            bg = Color(0xFFF2E6FF),
            border = Color(0xFFB366FF),
            btnBg = Color(0xFF7C3AED),
            btnShadow = Color(0xFF5B21B6),
            btnText = Color.White,
            emoji = "✨"
        )
        AlertType.CHALLENGE -> AlertTheme(
            bg = Color(0xFFFFF0F5),
            border = Color(0xFFFF99C8),
            btnBg = Color(0xFFFF5252),
            btnShadow = Color(0xFFD32F2F),
            btnText = Color.White,
            emoji = "⚡"
        )
    }

    val imageLoader = remember(context) {
        ImageLoader.Builder(context).components {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) add(ImageDecoderDecoder.Factory())
            else add(GifDecoder.Factory())
        }.build()
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = theme.bg),
            modifier = Modifier
                .fillMaxWidth()
                .border(3.dp, theme.border, RoundedCornerShape(28.dp))
                .padding(4.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Title Banner with Emoji
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(theme.emoji, fontSize = 24.sp)
                    Text(
                        text = alert.title.uppercase(),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        color = theme.btnBg,
                        textAlign = TextAlign.Center
                    )
                }

                // Mateo speech bubble & GIF
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Speech bubble pointing down
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White, RoundedCornerShape(16.dp))
                            .border(2.dp, theme.border, RoundedCornerShape(16.dp))
                            .padding(16.dp)
                    ) {
                        Text(
                            text = alert.message,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.DarkGray,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    // Mateo GIF representation
                    Image(
                        painter = rememberAsyncImagePainter(
                            model = R.drawable.ajolote,
                            imageLoader = imageLoader
                        ),
                        contentDescription = "Mateo el Ajolote",
                        modifier = Modifier
                            .size(140.dp)
                            .clip(RoundedCornerShape(20.dp))
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                // 3D Styled Chunky Button (Duolingo style)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .background(theme.btnShadow)
                        .clickable { onDismiss() }
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(0.9f)
                            .background(theme.btnBg, RoundedCornerShape(18.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = alert.buttonText.uppercase(),
                            color = theme.btnText,
                            fontWeight = FontWeight.Black,
                            fontSize = 16.sp,
                            letterSpacing = 1.sp
                        )
                    }
                }
            }
        }
    }
}
