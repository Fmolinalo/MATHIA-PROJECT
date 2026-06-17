package com.example.mathia.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.Canvas
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import com.example.mathia.AppColors
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun StarProgress(current: Int, max: Int = 210) {
    val pct = (current.toFloat() / max).coerceIn(0f, 1f)
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = AppColors.Amber,
                    modifier = Modifier.size(16.dp)
                )
                Text("$current / $max estrellas", fontSize = 12.sp, color = AppColors.Gray600)
            }
            Text("${(pct * 100).toInt()}%", fontSize = 12.sp, color = AppColors.Purple, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { pct },
            modifier = Modifier.fillMaxWidth().height(8.dp),
            color = AppColors.Purple,
            trackColor = AppColors.Gray200
        )
    }
}

@Composable
fun StreakCalendar(streak: Int) {
    val days = listOf("L", "M", "X", "J", "V", "S", "D")
    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        days.forEachIndexed { i, d ->
            val active = i < (streak % 7)
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .background(if (active) AppColors.Amber else AppColors.Gray100, RoundedCornerShape(6.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(d, fontSize = 10.sp, color = if (active) Color.White else AppColors.Gray400, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun InfoBox(text: String, bg: Color, fg: Color) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(bg, RoundedCornerShape(10.dp))
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Text(text, fontSize = 12.sp, color = fg)
    }
}

@Composable
fun MetricCard(value: String, label: String, icon: String, color: Color) {
    Card(
        modifier = Modifier.width(90.dp),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.15f)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(icon, fontSize = 24.sp)
            Spacer(Modifier.height(4.dp))
            Text(value, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = color)
            Text(label, fontSize = 10.sp, color = AppColors.Gray600)
        }
    }
}

@Composable
fun SkillProgressBar(skill: String, value: Int) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(skill, fontSize = 14.sp, fontWeight = FontWeight.Medium)
            Text("$value%", fontSize = 14.sp, color = AppColors.Purple, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { value / 100f },
            modifier = Modifier.fillMaxWidth().height(8.dp),
            color = AppColors.Purple,
            trackColor = AppColors.Gray200
        )
    }
}

@Composable
fun RadarChart(skills: Map<String, Int>, size: Dp = 160.dp) {
    val keys = skills.keys.toList()
    val values = skills.values.toList()
    val n = keys.size

    if (n < 3) {
        // Fallback for too few keys
        Column(
            modifier = Modifier.size(size),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Insuficientes datos", fontSize = 11.sp, color = AppColors.Gray400)
        }
        return
    }

    Canvas(modifier = Modifier.size(size)) {
        val center = Offset(size.toPx() / 2f, size.toPx() / 2f)
        val radius = size.toPx() * 0.38f

        listOf(0.25f, 0.5f, 0.75f, 1f).forEach { lvl ->
            val path = Path()
            for (i in 0 until n) {
                val angle = (2 * PI * i) / n - PI / 2
                val x = center.x + (radius * lvl) * cos(angle).toFloat()
                val y = center.y + (radius * lvl) * sin(angle).toFloat()
                if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
            }
            path.close()
            drawPath(path = path, color = AppColors.Gray200, style = Stroke(1.dp.toPx()))
        }
        for (i in 0 until n) {
            val angle = (2 * PI * i) / n - PI / 2
            drawLine(
                color = AppColors.Gray200,
                start = center,
                end = Offset(center.x + radius * cos(angle).toFloat(), center.y + radius * sin(angle).toFloat()),
                strokeWidth = 1.dp.toPx()
            )
        }
        val skillPath = Path()
        for (i in 0 until n) {
            val angle = (2 * PI * i) / n - PI / 2
            val score = values.getOrNull(i) ?: 0
            val r = (score / 100f) * radius
            val x = center.x + r * cos(angle).toFloat()
            val y = center.y + r * sin(angle).toFloat()
            if (i == 0) skillPath.moveTo(x, y) else skillPath.lineTo(x, y)
            drawCircle(color = AppColors.Purple, radius = 3.dp.toPx(), center = Offset(x, y))
        }
        skillPath.close()
        drawPath(path = skillPath, color = AppColors.Purple.copy(alpha = 0.2f))
        drawPath(path = skillPath, color = AppColors.Purple, style = Stroke(width = 1.5.dp.toPx()))
    }
}

@Composable
fun MiniBarChart(data: List<Int>, color: Color) {
    val max = data.maxOrNull()?.coerceAtLeast(1) ?: 1
    Canvas(modifier = Modifier.width(160.dp).height(60.dp)) {
        val barW = 18.dp.toPx()
        val gap = 4.dp.toPx()
        data.forEachIndexed { i, v ->
            val bh = (v.toFloat() / max) * size.height
            drawRoundRect(
                color = color.copy(alpha = 0.8f),
                topLeft = Offset(i * (barW + gap), size.height - bh),
                size = Size(barW, bh),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(3.dp.toPx())
            )
        }
    }
}
