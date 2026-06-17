package com.example.mathia.ui.screens

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mathia.AppColors
import com.example.mathia.model.Student
import com.example.mathia.ui.components.MiniBarChart
import com.example.mathia.ui.components.SkillProgressBar
import com.example.mathia.ui.components.AvatarIcon
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Lightbulb
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun StudentProfileScreen(
    student: Student,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val scrollState = rememberScrollState()

    // Dynamic Achievements
    val achievements = listOf(
        Badge("Primeros Pasos", "¡Resuelve tu primera pregunta de práctica!", Icons.Default.PlayArrow, student.totalQuestions > 0),
        Badge("Velocidad Absoluta", "Tiempo promedio menor a 7s por respuesta.", Icons.Default.Speed, student.tiempoPromedio > 0.0 && student.tiempoPromedio < 7.0),
        Badge("Precisión Divina", "Alcanza un 85% de precisión general.", Icons.Default.Star, student.accuracy >= 85),
        Badge("Práctica Imparable", "Consigue una racha de 3 días de práctica.", Icons.Default.Whatshot, student.streak >= 3),
        Badge("Diagnóstico de Mateo", "Completa la prueba de nivel adaptativa.", Icons.Default.Psychology, student.diagnosticoRealizado),
        Badge("Coleccionista", "Desbloquea al menos 2 avatares en la tienda.", Icons.Default.ShoppingBag, student.unlockedAvatars.size >= 2)
    )

    // Render study calendar for current month (June 2026)
    val calendarDays = remember {
        val list = mutableListOf<String>()
        val cal = Calendar.getInstance()
        cal.set(2026, Calendar.JUNE, 1)
        val maxDays = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
        val firstDayOfWeek = cal.get(Calendar.DAY_OF_WEEK) // 1=Sun, 2=Mon...
        
        // Blank leading cells
        val blanks = if (firstDayOfWeek == Calendar.SUNDAY) 6 else firstDayOfWeek - 2
        for (i in 0 until blanks) {
            list.add("")
        }
        for (d in 1..maxDays) {
            list.add("2026-06-%02d".format(d))
        }
        list
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Perfil de Estudiante", fontWeight = FontWeight.Bold, color = AppColors.Purple) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = AppColors.Purple)
                    }
                },
                actions = {
                    IconButton(onClick = {
                        val shareText = "¡Mira mi progreso en MathIA!\nNivel: ${student.level}\nEstrellas: ${student.stars}\nPrecisión: ${student.accuracy}%\n¡Únete a entrenar con Mateo el Ajolote!"
                        clipboardManager.setText(AnnotatedString(shareText))
                        Toast.makeText(context, "¡Progreso copiado al portapapeles! Listo para compartir.", Toast.LENGTH_SHORT).show()
                    }) {
                        Icon(Icons.Default.Share, "Compartir", tint = AppColors.Purple)
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(AppColors.Bg)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            
            // ─── Header: Avatar & Name Card ───
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(90.dp)
                            .background(AppColors.PurpleLight, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        AvatarIcon(student.avatar, modifier = Modifier.size(54.dp), tint = AppColors.Purple)
                    }
                    Spacer(Modifier.height(12.dp))
                    
                    Text(
                        text = "${student.name} ${student.lastName}",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 24.sp,
                        color = AppColors.Purple,
                        textAlign = TextAlign.Center
                    )
                    
                    Spacer(Modifier.height(8.dp))
                    
                    // Badges details row
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        SuggestionChip(onClick = {}, label = { Text("${student.edad} años") })
                        SuggestionChip(onClick = {}, label = { Text(student.grade) })
                        if (student.seccion.isNotEmpty()) {
                            SuggestionChip(onClick = {}, label = { Text(student.seccion) })
                        }
                    }

                    if (student.colegio.isNotEmpty()) {
                        Spacer(Modifier.height(4.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.School,
                                contentDescription = null,
                                tint = AppColors.Gray500,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "Colegio: ${student.colegio}",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = AppColors.Gray500
                            )
                        }
                    }
                }
            }

            // ─── Gamification status card ───
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.TrendingUp,
                            contentDescription = null,
                            tint = AppColors.Purple,
                            modifier = Modifier.size(20.dp)
                        )
                        Text("Nivel y Experiencia", fontWeight = FontWeight.Bold, color = AppColors.Purple, fontSize = 16.sp)
                    }
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Nivel actual: ${student.level}", fontWeight = FontWeight.Black, fontSize = 18.sp)
                            val nextLevelXP = student.level * 100
                            val prevLevelXP = (student.level - 1) * 100
                            val relativeXP = student.xp - prevLevelXP
                            Text("$relativeXP / 100 XP para el Nivel ${student.level + 1}", fontSize = 12.sp, color = AppColors.Gray500)
                        }
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .background(AppColors.PurpleLight, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.EmojiEvents,
                                contentDescription = null,
                                tint = AppColors.Purple,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                    
                    val pctXP = (student.xp % 100) / 100f
                    LinearProgressIndicator(
                        progress = { pctXP },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                            .clip(RoundedCornerShape(5.dp)),
                        color = AppColors.Purple,
                        trackColor = AppColors.Gray200
                    )

                    Divider(color = AppColors.Gray100, modifier = Modifier.padding(vertical = 4.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(Icons.Default.Star, null, tint = AppColors.Amber, modifier = Modifier.size(14.dp))
                                Text("Estrellas", fontSize = 11.sp, color = AppColors.Gray500)
                            }
                            Text("${student.stars}", fontWeight = FontWeight.Black, fontSize = 18.sp, color = AppColors.Amber)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(Icons.Default.Whatshot, null, tint = AppColors.Pink, modifier = Modifier.size(14.dp))
                                Text("Racha de Estudio", fontSize = 11.sp, color = AppColors.Gray500)
                            }
                            Text("${student.streak} días", fontWeight = FontWeight.Black, fontSize = 18.sp, color = AppColors.Pink)
                        }
                    }
                }
            }

            // ─── Stats summary grid ───
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.Assessment, null, tint = AppColors.Purple, modifier = Modifier.size(20.dp))
                        Text("Estadísticas de Aprendizaje", fontWeight = FontWeight.Bold, color = AppColors.Purple, fontSize = 16.sp)
                    }
                    
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Column {
                            Text("Tiempo Total", fontSize = 11.sp, color = AppColors.Gray500)
                            val totalMin = student.tiempoTotal / 60
                            val totalSec = student.tiempoTotal % 60
                            Text("${totalMin}m ${totalSec}s", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }
                        Column {
                            Text("Tiempo Promedio", fontSize = 11.sp, color = AppColors.Gray500)
                            Text("${student.tiempoPromedio.toInt()}s por respuesta", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }
                    }

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Column {
                            Text("Preguntas Respondidas", fontSize = 11.sp, color = AppColors.Gray500)
                            Text("${student.totalQuestions}", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }
                        Column {
                            Text("Correctas / Incorrectas", fontSize = 11.sp, color = AppColors.Gray500)
                            Text("${student.correctAnswers} / ${student.totalQuestions - student.correctAnswers}", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = AppColors.Green)
                        }
                    }

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Column {
                            Text("Precisión General", fontSize = 11.sp, color = AppColors.Gray500)
                            Text("${student.accuracy}%", fontWeight = FontWeight.Black, fontSize = 18.sp, color = AppColors.Green)
                        }
                        Column {
                            Text("Aula asignada", fontSize = 11.sp, color = AppColors.Gray500)
                            Text(student.classroom.ifEmpty { "1ro A" }, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }
                    }
                }
            }

            // ─── Skills Breakdown ───
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.Star, null, tint = AppColors.Purple, modifier = Modifier.size(20.dp))
                        Text("Precisión por Competencia", fontWeight = FontWeight.Bold, color = AppColors.Purple, fontSize = 16.sp)
                    }
                    student.skills.forEach { (skill, score) ->
                        SkillProgressBar(skill = skill, value = score)
                    }
                }
            }

            // ─── Calendario de estudio ───
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.align(Alignment.Start)
                    ) {
                        Icon(Icons.Default.CalendarMonth, null, tint = AppColors.Purple, modifier = Modifier.size(20.dp))
                        Text(
                            text = "Calendario de Estudio (Junio 2026)",
                            fontWeight = FontWeight.Bold,
                            color = AppColors.Purple,
                            fontSize = 16.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Day of week labels
                    val weekHeaders = listOf("L", "M", "M", "J", "V", "S", "D")
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                        weekHeaders.forEach { header ->
                            Text(
                                header,
                                fontWeight = FontWeight.Bold,
                                color = AppColors.Gray400,
                                fontSize = 12.sp,
                                modifier = Modifier.width(36.dp),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))

                    // Calendar Grid
                    calendarDays.chunked(7).forEach { week ->
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                            week.forEach { dateStr ->
                                if (dateStr.isEmpty()) {
                                    Box(modifier = Modifier.size(36.dp))
                                } else {
                                    val practiced = student.asistencia.contains(dateStr)
                                    val dayNum = dateStr.split("-").last().toInt().toString()
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(
                                                if (practiced) AppColors.GreenLight else Color.Transparent
                                            )
                                            .border(
                                                1.dp,
                                                if (practiced) AppColors.Green else AppColors.Gray200,
                                                RoundedCornerShape(8.dp)
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            dayNum,
                                            fontWeight = if (practiced) FontWeight.ExtraBold else FontWeight.Normal,
                                            color = if (practiced) AppColors.Green else AppColors.Gray600,
                                            fontSize = 12.sp
                                        )
                                    }
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(modifier = Modifier.size(12.dp).background(AppColors.GreenLight, RoundedCornerShape(2.dp)).border(1.dp, AppColors.Green, RoundedCornerShape(2.dp)))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Días practicados con MathIA", fontSize = 11.sp, color = AppColors.Gray600)
                    }
                }
            }

            // ─── Achievements / Badges ───
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.EmojiEvents, null, tint = AppColors.Purple, modifier = Modifier.size(20.dp))
                        Text("Logros e Insignias", fontWeight = FontWeight.Bold, color = AppColors.Purple, fontSize = 16.sp)
                    }
                    
                    achievements.forEach { badge ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(50.dp)
                                    .background(
                                        if (badge.isUnlocked) AppColors.AmberLight else AppColors.Gray100,
                                        CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (badge.isUnlocked) badge.icon else Icons.Default.Lock,
                                    contentDescription = null,
                                    tint = if (badge.isUnlocked) AppColors.Amber else AppColors.Gray400,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    badge.name,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = if (badge.isUnlocked) Color.Black else AppColors.Gray400
                                )
                                Text(
                                    badge.description,
                                    fontSize = 12.sp,
                                    color = AppColors.Gray500
                                )
                            }
                        }
                    }
                }
            }

            // ─── AI Recommendations Card ───
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFAF5FF)),
                border = BorderStroke(1.5.dp, AppColors.Purple.copy(alpha = 0.2f))
            ) {
                Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.Lightbulb, null, tint = AppColors.Purple, modifier = Modifier.size(20.dp))
                        Text("Consejos de Estudio de Mateo", fontWeight = FontWeight.Bold, color = AppColors.Purple, fontSize = 16.sp)
                    }
                    if (student.recomendaciones.isEmpty()) {
                        Text("Mateo está revisando tus estadísticas. ¡Sigue practicando para recibir sugerencias!", fontSize = 13.sp, color = AppColors.Gray600)
                    } else {
                        student.recomendaciones.forEach { rec ->
                            Text("• $rec", fontSize = 13.sp, color = AppColors.Gray800)
                        }
                    }
                }
            }

            // ─── Weekly evolution mini chart ───
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.align(Alignment.Start)
                    ) {
                        Icon(Icons.Default.Assessment, null, tint = AppColors.Purple, modifier = Modifier.size(20.dp))
                        Text("Desempeño Semanal", fontWeight = FontWeight.Bold, color = AppColors.Purple)
                    }
                    MiniBarChart(data = student.weekData, color = AppColors.Purple)
                    Text("Evolución de los últimos 7 días practicados", fontSize = 11.sp, color = AppColors.Gray500)
                }
            }
        }
    }
}

data class Badge(
    val name: String,
    val description: String,
    val icon: ImageVector,
    val isUnlocked: Boolean
)

