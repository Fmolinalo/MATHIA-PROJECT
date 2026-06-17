package com.example.mathia.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.border
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mathia.AppColors
import com.example.mathia.model.Reward
import com.example.mathia.model.Student
import com.example.mathia.ui.components.StarProgress

val APP_REWARDS = listOf(
    Reward(1, "Primer Paso", "🐣", 10, "¡Completa tu primer ejercicio!"),
    Reward(2, "Racha Inicial", "🔥", 30, "3 días seguidos practicando"),
    Reward(3, "Sumador", "➕", 50, "Domina las sumas básicas"),
    Reward(4, "Restador", "➖", 70, "Domina las restas básicas"),
    Reward(5, "Racha Semana", "⚡", 90, "7 días seguidos"),
    Reward(6, "Explorador", "🗺️", 120, "Completa el examen adaptativo"),
    Reward(7, "Calculador Pro", "🧮", 150, "80%+ precisión en 50 ejercicios"),
    Reward(8, "Maestro Ajolote", "🦎", 180, "Alcanza el nivel avanzado"),
    Reward(9, "Leyenda MathIA", "🏆", 210, "¡Nivel máximo alcanzado!")
)

fun getRewardIcon(emojiKey: String): ImageVector {
    return when (emojiKey) {
        "🐣" -> Icons.Default.PlayArrow
        "🔥" -> Icons.Default.Whatshot
        "➕" -> Icons.Default.Add
        "➖" -> Icons.Default.Remove
        "⚡" -> Icons.Default.ElectricBolt
        "🗺️" -> Icons.Default.Map
        "🧮" -> Icons.Default.Calculate
        "🦎" -> Icons.Default.Pets
        "🏆" -> Icons.Default.EmojiEvents
        else -> Icons.Default.EmojiEvents
    }
}

@Composable
fun RewardsScreen(student: Student, onBack: () -> Unit) {
    // Dynamic pulsing border width for unlocked rewards
    val borderTransition = rememberInfiniteTransition(label = "pulse_border")
    val pulsingBorderWidth by borderTransition.animateFloat(
        initialValue = 1f,
        targetValue = 3f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "border_width"
    )

    // Shimmer effect for locked rewards
    val shimmerTransition = rememberInfiniteTransition(label = "shimmer")
    val shimmerOffset by shimmerTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer_offset"
    )

    val shimmerBrush = Brush.linearGradient(
        colors = listOf(
            Color.White.copy(alpha = 0.2f),
            Color.White.copy(alpha = 0.6f),
            Color.White.copy(alpha = 0.2f)
        ),
        start = Offset(shimmerOffset - 300f, shimmerOffset - 300f),
        end = Offset(shimmerOffset, shimmerOffset)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.Bg)
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = AppColors.Purple) }
            Icon(
                imageVector = Icons.Default.EmojiEvents,
                contentDescription = null,
                tint = AppColors.Purple,
                modifier = Modifier.size(24.dp)
            )
            Text("Mis Premios", fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, color = AppColors.Purple)
        }
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                StarProgress(current = student.stars)
            }
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(APP_REWARDS) { r ->
                val unlocked = student.stars >= r.stars

                Card(
                     modifier = Modifier
                        .aspectRatio(1f)
                        .border(
                            width = if (unlocked) pulsingBorderWidth.dp else 0.dp,
                            color = if (unlocked) AppColors.Amber else Color.Transparent,
                            shape = RoundedCornerShape(24.dp)
                        ),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = if (unlocked) 4.dp else 0.dp)
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        Column(
                            modifier = Modifier
                                .padding(8.dp)
                                .fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = getRewardIcon(r.emoji),
                                contentDescription = null,
                                tint = if (unlocked) AppColors.Amber else AppColors.Gray400,
                                modifier = Modifier
                                    .size(40.dp)
                                    .alpha(if (unlocked) 1f else 0.45f)
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(r.name, fontWeight = FontWeight.Bold, fontSize = 13.sp, textAlign = TextAlign.Center)
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(2.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = null,
                                    tint = AppColors.Amber,
                                    modifier = Modifier.size(12.dp)
                                )
                                Text("${r.stars}", fontSize = 11.sp, color = AppColors.Amber, fontWeight = FontWeight.Bold)
                            }
                            if (unlocked) {
                                Spacer(Modifier.height(2.dp))
                                Text("Desbloqueado", fontSize = 10.sp, color = AppColors.Green, fontWeight = FontWeight.Bold)
                            }
                        }

                        if (!unlocked) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(shimmerBrush)
                            )
                        }
                    }
                }
            }
        }
    }
}
