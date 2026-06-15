package com.example.mathia.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mathia.AppColors
import com.example.mathia.StudentViewModel
import com.example.mathia.model.FirebaseStudent
import com.example.mathia.model.Student

@Composable
fun LeaderboardScreen(
    currentStudent: Student,
    onBack: () -> Unit,
    viewModel: StudentViewModel
) {
    var leaderboardList by remember { mutableStateOf<List<FirebaseStudent>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        viewModel.obtenerTodosAlumnos { list ->
            // Filter by student's grade for local competitiveness and sort by stars
            leaderboardList = list.filter { it.grado == currentStudent.grade }
                .sortedByDescending { it.estrellas }
            isLoading = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.Bg)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = AppColors.Purple)
            }
            Text(
                text = "🏆 Tabla de Campeones",
                fontSize = 22.sp,
                fontWeight = FontWeight.Black,
                color = AppColors.Purple
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (isLoading) {
            Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = AppColors.Purple)
            }
        } else {
            // Podium for Top 3 (if we have at least 1 student)
            if (leaderboardList.isNotEmpty()) {
                val top3 = leaderboardList.take(3)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.Bottom
                ) {
                    // 2nd Place (Left)
                    if (top3.size > 1) {
                        PodiumColumn(student = top3[1], place = 2, height = 90.dp, emoji = "🥈", color = Color(0xFFC0C0C0))
                    } else {
                        Spacer(modifier = Modifier.width(90.dp))
                    }

                    // 1st Place (Center)
                    if (top3.isNotEmpty()) {
                        PodiumColumn(student = top3[0], place = 1, height = 120.dp, emoji = "🥇", color = AppColors.Amber)
                    }

                    // 3rd Place (Right)
                    if (top3.size > 2) {
                        PodiumColumn(student = top3[2], place = 3, height = 70.dp, emoji = "🥉", color = Color(0xFFCD7F32))
                    } else {
                        Spacer(modifier = Modifier.width(90.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Ranking List
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    itemsIndexed(leaderboardList) { index, student ->
                        val isSelf = student.pin.toString() == currentStudent.pin
                        val position = index + 1
                        
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(
                                    width = if (isSelf) 2.dp else 0.dp,
                                    color = if (isSelf) AppColors.Purple else Color.Transparent,
                                    shape = RoundedCornerShape(16.dp)
                                ),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelf) AppColors.PurpleLight else AppColors.Gray100.copy(alpha = 0.5f)
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "#$position",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp,
                                        color = if (position <= 3) AppColors.Purple else AppColors.Gray600,
                                        modifier = Modifier.width(36.dp)
                                    )
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .background(Color.White, CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(student.avatar, fontSize = 20.sp)
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(
                                            text = student.nombre,
                                            fontWeight = if (isSelf) FontWeight.Bold else FontWeight.Medium,
                                            fontSize = 14.sp
                                        )
                                        Text(
                                            text = "Nivel ${student.nivel_actual} • XP ${student.xp}",
                                            fontSize = 11.sp,
                                            color = AppColors.Gray600
                                        )
                                    }
                                }
                                Text(
                                    text = "⭐ ${student.estrellas}",
                                    fontWeight = FontWeight.Black,
                                    color = AppColors.Amber,
                                    fontSize = 15.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PodiumColumn(
    student: FirebaseStudent,
    place: Int,
    height: androidx.compose.ui.unit.Dp,
    emoji: String,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom,
        modifier = Modifier.width(95.dp)
    ) {
        Text(student.avatar, fontSize = 32.sp)
        Text(
            text = student.nombre.split(" ").firstOrNull() ?: "",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = AppColors.Gray800,
            textAlign = TextAlign.Center
        )
        Text(
            text = "⭐ ${student.estrellas}",
            fontSize = 11.sp,
            color = AppColors.Gray600,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(height)
                .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                .background(color),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = emoji,
                    fontSize = 24.sp
                )
                Text(
                    text = "${place}°",
                    color = Color.White,
                    fontWeight = FontWeight.Black,
                    fontSize = 18.sp
                )
            }
        }
    }
}
