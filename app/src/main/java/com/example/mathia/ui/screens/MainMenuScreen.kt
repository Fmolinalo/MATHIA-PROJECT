package com.example.mathia.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mathia.AppColors
import com.example.mathia.StudentViewModel
import com.example.mathia.model.DuolingoAlert
import com.example.mathia.model.Student
import com.example.mathia.ui.components.RadarChart
import com.example.mathia.ui.components.StarProgress
import com.example.mathia.ui.components.StreakCalendar

// Data class para representar cada actividad del menú infantil
data class ActivityCard(
    val icon: String,
    val title: String,
    val subtitle: String,
    val op: String,
    val gradientStart: Color,
    val gradientEnd: Color
)

@Composable
fun MainMenuScreen(
    student: Student,
    onPlay: (String) -> Unit,
    onRewards: () -> Unit,
    onExam: () -> Unit,
    onLeaderboard: () -> Unit,
    onLogout: () -> Unit,
    onUpdateStudent: (Student) -> Unit,
    onShowAlert: (DuolingoAlert) -> Unit,
    onProfile: () -> Unit,
    viewModel: StudentViewModel
) {
    var selectedTab by remember { mutableIntStateOf(0) } // 0 = Inicio, 1 = Progreso, 2 = Tienda

    val activities = remember {
        listOf(
            ActivityCard("➕", "Sumas", "¡Suma como un campeón!", "Suma",
                Color(0xFF4FC3F7), Color(0xFF0288D1)),
            ActivityCard("➖", "Restas", "¡Domina la resta!", "Resta",
                Color(0xFF81C784), Color(0xFF388E3C)),
            ActivityCard("✖️", "Multi-\nplicación", "¡Multiplica rápido!", "Multiplicacion",
                Color(0xFFFFB74D), Color(0xFFE65100)),
            ActivityCard("🍕", "Fracciones", "¡Juega con porciones!", "Fracciones",
                Color(0xFFBA68C8), Color(0xFF7B1FA2)),
            ActivityCard("🔢", "Series", "¡Completa el patrón!", "Series",
                Color(0xFFFF8A65), Color(0xFFBF360C)),
            ActivityCard("🧠", "Examen IA", "¡Mide tu nivel!", "Exam",
                Color(0xFF26C6DA), Color(0xFF006064)),
        )
    }

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = Color.White,
                tonalElevation = 8.dp
            ) {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    icon = {
                        Text(
                            "🏠",
                            fontSize = if (selectedTab == 0) 26.sp else 22.sp
                        )
                    },
                    label = {
                        Text(
                            "Inicio",
                            fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 11.sp
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = AppColors.Purple,
                        selectedTextColor = AppColors.Purple,
                        indicatorColor = AppColors.Purple.copy(alpha = 0.12f)
                    )
                )
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    icon = {
                        Text(
                            "📊",
                            fontSize = if (selectedTab == 1) 26.sp else 22.sp
                        )
                    },
                    label = {
                        Text(
                            "Progreso",
                            fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 11.sp
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = AppColors.Purple,
                        selectedTextColor = AppColors.Purple,
                        indicatorColor = AppColors.Purple.copy(alpha = 0.12f)
                    )
                )
                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    icon = {
                        Text(
                            "🏪",
                            fontSize = if (selectedTab == 2) 26.sp else 22.sp
                        )
                    },
                    label = {
                        Text(
                            "Tienda",
                            fontWeight = if (selectedTab == 2) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 11.sp
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = AppColors.Purple,
                        selectedTextColor = AppColors.Purple,
                        indicatorColor = AppColors.Purple.copy(alpha = 0.12f)
                    )
                )
                NavigationBarItem(
                    selected = false,
                    onClick = onRewards,
                    icon = { Text("🏆", fontSize = 22.sp) },
                    label = { Text("Premios", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = AppColors.Purple,
                        indicatorColor = Color.Transparent
                    )
                )
                NavigationBarItem(
                    selected = false,
                    onClick = onLogout,
                    icon = { Text("🚪", fontSize = 22.sp) },
                    label = { Text("Salir", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = AppColors.Purple,
                        indicatorColor = Color.Transparent
                    )
                )
            }
        }
    ) { paddingValues ->
        when (selectedTab) {
            2 -> Box(modifier = Modifier.padding(paddingValues)) {
                ShopTabContent(
                    student = student,
                    onUpdateStudent = onUpdateStudent,
                    onShowAlert = onShowAlert,
                    viewModel = viewModel
                )
            }
            1 -> ProgresoTab(
                student = student,
                paddingValues = paddingValues
            )
            else -> InicioTab(
                student = student,
                activities = activities,
                onPlay = onPlay,
                onExam = onExam,
                onLeaderboard = onLeaderboard,
                onProfile = onProfile,
                paddingValues = paddingValues
            )
        }
    }
}

// ─── TAB INICIO ──────────────────────────────────────────────────────────────
@Composable
fun InicioTab(
    student: Student,
    activities: List<ActivityCard>,
    onPlay: (String) -> Unit,
    onExam: () -> Unit,
    onLeaderboard: () -> Unit,
    onProfile: () -> Unit,
    paddingValues: PaddingValues
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .background(AppColors.Bg),
        verticalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        // ─── HEADER PREMIUM ─────────────────────────────────────────
        item {
            StudentHeaderPremium(student = student, onProfile = onProfile)
        }

        // ─── SECCIÓN DESAFÍOS ────────────────────────────────────────
        item {
            Spacer(Modifier.height(20.dp))
            Text(
                text = "🎮  ¡Elige tu Desafío!",
                fontWeight = FontWeight.ExtraBold,
                fontSize = 20.sp,
                color = AppColors.Gray800,
                modifier = Modifier.padding(horizontal = 20.dp)
            )
            Spacer(Modifier.height(12.dp))
        }

        // ─── GRILLA 2 COLUMNAS DE ACTIVIDADES ───────────────────────
        item {
            ActivityGrid(
                activities = activities,
                onPlay = onPlay,
                onExam = onExam
            )
        }

        // ─── TABLA DE CAMPEONES ──────────────────────────────────────
        item {
            Spacer(Modifier.height(12.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(
                        Brush.horizontalGradient(
                            listOf(Color(0xFFFFD700), Color(0xFFFFA000))
                        )
                    )
                    .clickable { onLeaderboard() }
                    .padding(20.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text("🏅", fontSize = 40.sp)
                    Column {
                        Text(
                            "Tabla de Campeones",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 18.sp,
                            color = Color.White
                        )
                        Text(
                            "¡Compara tu puntaje con tus amigos!",
                            fontSize = 13.sp,
                            color = Color.White.copy(alpha = 0.85f)
                        )
                    }
                }
            }
            Spacer(Modifier.height(24.dp))
        }

        // ─── SUGERENCIAS DE MATEO ───────────────────────────────────
        item {
            MateoTipsCard(student = student)
            Spacer(Modifier.height(16.dp))
        }

        // ─── MISIONES ───────────────────────────────────────────────
        item {
            MisionesCard(student = student)
            Spacer(Modifier.height(24.dp))
        }
    }
}

// ─── HEADER PREMIUM DEL ESTUDIANTE ──────────────────────────────────────────
@Composable
fun StudentHeaderPremium(student: Student, onProfile: () -> Unit) {
    val infiniteTransition = rememberInfiniteTransition(label = "avatar_pulse")
    val avatarScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.06f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    listOf(AppColors.Purple, AppColors.Purple.copy(alpha = 0.75f))
                )
            )
            .padding(top = 24.dp, bottom = 28.dp, start = 20.dp, end = 20.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            // Avatar + Nombre
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(68.dp)
                        .scale(avatarScale)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.25f))
                        .clickable { onProfile() },
                    contentAlignment = Alignment.Center
                ) {
                    Text(student.avatar, fontSize = 42.sp)
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "¡Hola, ${student.name}! 👋",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                    Text(
                        text = "${student.grade} • ${student.colegio.ifEmpty { "MathIA" }}",
                        fontSize = 13.sp,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }
                // Estrella y racha
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("🔥", fontSize = 20.sp)
                    Text(
                        "${student.streak}",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppColors.Amber
                    )
                }
            }

            // Stats row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                listOf(
                    Triple("⭐", "${student.stars}", "Estrellas"),
                    Triple("⚡", "${student.xp} XP", "Experiencia"),
                    Triple("🏅", "Nv. ${student.level}", "Nivel"),
                    Triple("🎯", "${student.accuracy}%", "Precisión")
                ).forEach { (icon, value, label) ->
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(icon, fontSize = 16.sp)
                        Text(
                            value,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = Color.White
                        )
                        Text(
                            label,
                            fontSize = 10.sp,
                            color = Color.White.copy(alpha = 0.7f)
                        )
                    }
                }
            }

            // Barra de XP
            val xpForNextLevel = 100
            val xpProgress = (student.xp % xpForNextLevel).toFloat() / xpForNextLevel
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        "Nivel ${student.level}",
                        fontSize = 11.sp,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                    Text(
                        "Nivel ${student.level + 1}",
                        fontSize = 11.sp,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }
                LinearProgressIndicator(
                    progress = { xpProgress.coerceIn(0f, 1f) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(10.dp)
                        .clip(RoundedCornerShape(5.dp)),
                    color = AppColors.Amber,
                    trackColor = Color.White.copy(alpha = 0.25f)
                )
                Text(
                    "${student.xp % xpForNextLevel} / $xpForNextLevel XP para el siguiente nivel",
                    fontSize = 10.sp,
                    color = Color.White.copy(alpha = 0.7f),
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

// ─── GRILLA DE ACTIVIDADES 2x3 ───────────────────────────────────────────────
@Composable
fun ActivityGrid(
    activities: List<ActivityCard>,
    onPlay: (String) -> Unit,
    onExam: () -> Unit
) {
    // Fixed height grid (no scrolling dentro de LazyColumn)
    val rowCount = (activities.size + 1) / 2
    Column(
        modifier = Modifier.padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        for (row in 0 until rowCount) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                for (col in 0..1) {
                    val index = row * 2 + col
                    if (index < activities.size) {
                        val activity = activities[index]
                        ActivityCardItem(
                            activity = activity,
                            index = index,
                            modifier = Modifier.weight(1f),
                            onClick = {
                                when (activity.op) {
                                    "Exam" -> onExam()
                                    else -> onPlay(activity.op)
                                }
                            }
                        )
                    } else {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

// ─── CARD INDIVIDUAL DE ACTIVIDAD ────────────────────────────────────────────
@Composable
fun ActivityCardItem(
    activity: ActivityCard,
    index: Int,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "card_${activity.op}")
    val animatedScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.03f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1600 + (index * 200),
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale_${activity.op}"
    )

    Card(
        modifier = modifier
            .height(140.dp)
            .scale(animatedScale)
            .clickable { onClick() },
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.linearGradient(
                        listOf(activity.gradientStart, activity.gradientEnd)
                    )
                )
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    activity.icon,
                    fontSize = 38.sp,
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    activity.title,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 15.sp,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    lineHeight = 18.sp
                )
                Text(
                    activity.subtitle,
                    fontSize = 10.sp,
                    color = Color.White.copy(alpha = 0.85f),
                    textAlign = TextAlign.Center,
                    lineHeight = 13.sp
                )
            }
        }
    }
}

// ─── SUGERENCIAS DE MATEO ────────────────────────────────────────────────────
@Composable
fun MateoTipsCard(student: Student) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFEEF4FF)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("🦎", fontSize = 24.sp)
                Text(
                    "Consejos de Mateo",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 16.sp,
                    color = AppColors.Purple
                )
            }
            Spacer(Modifier.height(10.dp))

            val tips = student.recomendaciones.ifEmpty {
                listOf(
                    "¡Completa desafíos para ganar estrellas y subir de nivel! ⭐",
                    "¡Practica todos los días para mantener tu racha! 🔥"
                )
            }

            tips.take(3).forEachIndexed { i, tip ->
                Row(
                    modifier = Modifier
                        .padding(vertical = 4.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        "${i + 1}.",
                        fontWeight = FontWeight.ExtraBold,
                        color = AppColors.Purple,
                        fontSize = 13.sp,
                        modifier = Modifier.width(20.dp)
                    )
                    Text(
                        text = tip,
                        fontSize = 13.sp,
                        color = AppColors.Gray700,
                        lineHeight = 18.sp
                    )
                }
            }
        }
    }
}

// ─── MISIONES ────────────────────────────────────────────────────────────────
@Composable
fun MisionesCard(student: Student) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                "🎯  Misiones de Hoy",
                fontWeight = FontWeight.ExtraBold,
                fontSize = 16.sp,
                color = AppColors.Gray800
            )

            // Misión diaria
            val dailyTarget = 5
            val dailyPct = (student.dailyMissionProgress.toFloat() / dailyTarget).coerceIn(0f, 1f)
            MisionRow(
                icon = "☀️",
                label = "Diaria: Resuelve 5 desafíos",
                current = student.dailyMissionProgress,
                target = dailyTarget,
                progress = dailyPct,
                color = AppColors.Amber
            )

            // Misión semanal
            val weeklyTarget = 20
            val weeklyPct = (student.weeklyMissionProgress.toFloat() / weeklyTarget).coerceIn(0f, 1f)
            MisionRow(
                icon = "🗓️",
                label = "Semanal: Resuelve 20 desafíos",
                current = student.weeklyMissionProgress,
                target = weeklyTarget,
                progress = weeklyPct,
                color = AppColors.Purple
            )
        }
    }
}

@Composable
fun MisionRow(
    icon: String,
    label: String,
    current: Int,
    target: Int,
    progress: Float,
    color: Color
) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(icon, fontSize = 15.sp)
                Text(label, fontSize = 12.sp, color = AppColors.Gray600)
            }
            Text(
                "$current/$target",
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                color = color
            )
        }
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
            color = color,
            trackColor = AppColors.Gray200
        )
    }
}

// ─── TAB PROGRESO ────────────────────────────────────────────────────────────
@Composable
fun ProgresoTab(student: Student, paddingValues: PaddingValues) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .background(AppColors.Bg)
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { Spacer(Modifier.height(8.dp)) }

        item {
            Text(
                "📊  Mi Progreso",
                fontWeight = FontWeight.ExtraBold,
                fontSize = 20.sp,
                color = AppColors.Gray800
            )
        }

        // Racha de días
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier.padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text("🔥", fontSize = 40.sp)
                    Column {
                        Text(
                            "¡Racha de ${student.streak} días!",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 18.sp,
                            color = AppColors.Amber
                        )
                        Text(
                            "¡No rompas tu racha! Practica hoy.",
                            fontSize = 13.sp,
                            color = AppColors.Gray600
                        )
                        Spacer(Modifier.height(8.dp))
                        StreakCalendar(streak = student.streak)
                    }
                }
            }
        }

        // Barra de progreso de nivel
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    StarProgress(current = student.stars)
                }
            }
        }

        // Mapa de habilidades
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "🗺️  Mapa de Habilidades",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 16.sp,
                        color = AppColors.Gray800
                    )
                    Spacer(Modifier.height(12.dp))
                    val mapSkills = student.skills.ifEmpty {
                        mapOf(
                            "Sumas" to 0,
                            "Restas" to 0,
                            "Multiplicación" to 0,
                            "Fracciones" to 0,
                            "Series" to 0
                        )
                    }
                    RadarChart(skills = mapSkills)

                    Spacer(Modifier.height(12.dp))
                    // Barras individuales de habilidades
                    mapSkills.forEach { (skill, value) ->
                        SkillProgressRow(skill = skill, value = value)
                    }
                }
            }
        }

        // Estadísticas generales
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        "📈  Estadísticas Generales",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 16.sp,
                        color = AppColors.Gray800
                    )
                    Spacer(Modifier.height(14.dp))
                    val stats = listOf(
                        Triple("📝", "Total Preguntas", "${student.totalQuestions}"),
                        Triple("✅", "Respuestas Correctas", "${student.correctAnswers}"),
                        Triple("🎯", "Precisión Global", "${student.accuracy}%"),
                        Triple("📚", "Exámenes Completados", "${student.examsCompleted}"),
                        Triple("⏱️", "Tiempo Promedio / pregunta", "${student.tiempoPromedio.toInt()}s"),
                    )
                    stats.forEach { (icon, label, value) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(icon, fontSize = 16.sp)
                                Text(label, fontSize = 13.sp, color = AppColors.Gray600)
                            }
                            Text(
                                value,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = AppColors.Gray800
                            )
                        }
                        HorizontalDivider(color = AppColors.Gray200, thickness = 0.5.dp)
                    }
                }
            }
        }
        item { Spacer(Modifier.height(8.dp)) }
    }
}

// ─── FILA DE HABILIDAD ───────────────────────────────────────────────────────
@Composable
fun SkillProgressRow(skill: String, value: Int) {
    val color = when {
        value >= 80 -> Color(0xFF4CAF50)
        value >= 50 -> Color(0xFFFFB300)
        else -> Color(0xFFEF5350)
    }
    val emoji = when {
        value >= 80 -> "🟢"
        value >= 50 -> "🟡"
        else -> "🔴"
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(emoji, fontSize = 14.sp)
        Text(
            skill,
            fontSize = 13.sp,
            modifier = Modifier.width(100.dp),
            color = AppColors.Gray700
        )
        LinearProgressIndicator(
            progress = { value / 100f },
            modifier = Modifier
                .weight(1f)
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
            color = color,
            trackColor = AppColors.Gray200
        )
        Text(
            "$value%",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = color,
            modifier = Modifier.width(36.dp),
            textAlign = TextAlign.End
        )
    }
}
