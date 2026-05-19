package com.example.mathkids

import android.os.Bundle

import androidx.compose.foundation.layout.aspectRatio
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.mathkids.ui.theme.MathkidsTheme
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Star

import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke

import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

// ─── Data Models ───────────────────────────────────────────────────────────
data class Student(
    val id: Int,
    val name: String,
    val level: Int,
    val stars: Int,
    val accuracy: Int,
    val grade: String,
    val streak: Int,
    val weekData: List<Int>,
    val skills: Map<String, Int>,
    val avatar: String = "🦁"
)

data class Reward(
    val id: Int,
    val name: String,
    val emoji: String,
    val stars: Int,
    val desc: String
)

data class Question(
    val id: Int,
    val level: String,
    val op: String,
    val q: String,
    val a: Int,
    val topic: String
)

// ─── Constants ─────────────────────────────────────────────────────────────
object AppColors {
    val Purple = Color(0xFF7C3AED)
    val PurpleLight = Color(0xFFEDE9FE)
    val PurpleMid = Color(0xFFA78BFA)
    val Pink = Color(0xFFEC4899)
    val PinkLight = Color(0xFFFCE7F3)
    val Green = Color(0xFF10B981)
    val GreenLight = Color(0xFFD1FAE5)
    val Amber = Color(0xFFF59E0B)
    val AmberLight = Color(0xFFFEF3C7)
    val Blue = Color(0xFF3B82F6)
    val BlueLight = Color(0xFFDBEAFE)
    val Red = Color(0xFFEF4444)
    val RedLight = Color(0xFFFEE2E2)
    val Teal = Color(0xFF14B8A6)
    val TealLight = Color(0xFFCCFBF1)
    val Bg = Color(0xFFF5F3FF)
    val White = Color(0xFFFFFFFF)
    val Gray100 = Color(0xFFF3F4F6)
    val Gray200 = Color(0xFFE5E7EB)
    val Gray400 = Color(0xFF9CA3AF)
    val Gray600 = Color(0xFF4B5563)
    val Gray800 = Color(0xFF1F2937)
}

val ADAPTIVE_QUESTIONS = listOf(
    Question(1, "easy", "suma", "3 + 4", 7, "Sumas"),
    Question(2, "easy", "suma", "5 + 2", 7, "Sumas"),
    Question(3, "easy", "resta", "8 - 3", 5, "Restas"),
    Question(4, "easy", "resta", "6 - 2", 4, "Restas"),
    Question(5, "medium", "suma", "13 + 7", 20, "Sumas"),
    Question(6, "medium", "suma", "9 + 8", 17, "Sumas"),
    Question(7, "medium", "resta", "15 - 6", 9, "Restas"),
    Question(8, "medium", "resta", "12 - 5", 7, "Restas"),
    Question(9, "hard", "suma", "24 + 18", 42, "Sumas"),
    Question(10, "hard", "resta", "31 - 14", 17, "Restas")
)

val REWARDS = listOf(
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

val MOCK_STUDENTS = listOf(
    Student(1, "Ana García", 5, 142, 85, "Excelente", 7, listOf(20, 25, 30, 35, 22, 18, 8), mapOf("Sumas" to 85, "Restas" to 70, "Números" to 90, "Lógica" to 60, "Problemas" to 65)),
    Student(2, "Carlos López", 4, 98, 72, "Bien", 3, listOf(15, 20, 18, 25, 30, 10, 5), mapOf("Sumas" to 70, "Restas" to 55, "Números" to 75, "Lógica" to 50, "Problemas" to 45)),
    Student(3, "María Pérez", 6, 201, 92, "Excelente", 14, listOf(30, 35, 40, 38, 28, 20, 12), mapOf("Sumas" to 92, "Restas" to 88, "Números" to 95, "Lógica" to 80, "Problemas" to 78)),
    Student(4, "Juan Díaz", 3, 54, 58, "Regular", 1, listOf(10, 12, 8, 15, 18, 6, 3), mapOf("Sumas" to 60, "Restas" to 40, "Números" to 65, "Lógica" to 35, "Problemas" to 30))
)

// ─── UI Components ─────────────────────────────────────────────────────────

@Composable
fun RadarChart(skills: Map<String, Int>, size: Dp = 160.dp) {
    val keys = skills.keys.toList()
    val values = skills.values.toList()
    val n = keys.size

    Canvas(modifier = Modifier.size(size)) {
        val center = Offset(size.toPx() / 2, size.toPx() / 2)
        val radius = size.toPx() * 0.38f

        // Grid lines
        listOf(0.25f, 0.5f, 0.75f, 1f).forEach { lvl ->
            val path = Path()
            for (i in 0 until n) {
                val angle = (2 * PI * i) / n - PI / 2
                val x = center.x + (radius * lvl) * cos(angle).toFloat()
                val y = center.y + (radius * lvl) * sin(angle).toFloat()
                if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
            }
            path.close()
            drawPath(path, color = AppColors.Gray200, style = Stroke(width = 1.dp.toPx()))
        }

        // Axis lines
        for (i in 0 until n) {
            val angle = (2 * PI * i) / n - PI / 2
            drawLine(
                color = AppColors.Gray200,
                start = center,
                end = Offset(center.x + radius * cos(angle).toFloat(), center.y + radius * sin(angle).toFloat()),
                strokeWidth = 1.dp.toPx()
            )
        }

        // Skills Polygon
        val skillPath = Path()
        for (i in 0 until n) {
            val angle = (2 * PI * i) / n - PI / 2
            val r = (values[i] / 100f) * radius
            val x = center.x + r * cos(angle).toFloat()
            val y = center.y + r * sin(angle).toFloat()
            if (i == 0) skillPath.moveTo(x, y) else skillPath.lineTo(x, y)
            drawCircle(color = AppColors.Purple, radius = 3.dp.toPx(), center = Offset(x, y))
        }
        skillPath.close()
        drawPath(skillPath, color = AppColors.Purple.copy(alpha = 0.2f))
        drawPath(skillPath, color = AppColors.Purple, style = Stroke(width = 1.5.dp.toPx()))
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

@Composable
fun StarProgress(current: Int, max: Int = 210) {
    val pct = (current.toFloat() / max).coerceIn(0f, 1f)
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("⭐ $current / $max estrellas", fontSize = 12.sp, color = AppColors.Gray600)
            Text("${(pct * 100).toInt()}%", fontSize = 12.sp, color = AppColors.Purple, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(4.dp))
        Box(modifier = Modifier.fillMaxWidth().height(16.dp).background(AppColors.Gray200, CircleShape)) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(pct)
                    .fillMaxHeight()
                    .background(
                        Brush.horizontalGradient(listOf(AppColors.Purple, AppColors.Pink)),
                        CircleShape
                    )
            )
        }
    }
}

@Composable
fun StreakCalendar(streak: Int) {
    val days = listOf("L", "M", "X", "J", "V", "S", "D")
    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        days.forEachIndexed { i, d ->
            val active = i < (streak % 7) || (streak > 0 && i < 7) // Simple logic for demo
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

// ─── Main App ──────────────────────────────────────────────────────────────

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MathkidsTheme {
                MainApp()
            }
        }
    }
}

@Composable
fun MainApp() {
    var screen by remember { mutableStateOf("welcome") }
    var loginRole by remember { mutableStateOf("docente") }
    var student by remember { mutableStateOf(MOCK_STUDENTS[0]) }
    var gameOp by remember { mutableStateOf("Suma") }

    Surface(modifier = Modifier.fillMaxSize(), color = AppColors.Bg) {
        when (screen) {
            "welcome" -> WelcomeScreen(
                onCreateProfile = { screen = "create" },
                onLogin = { role ->
                    loginRole = role ?: "docente"
                    screen = "login"
                }
            )
            "login" -> LoginScreen(
                defaultRole = loginRole,
                onBack = { screen = "welcome" },
                onLoggedIn = { role ->
                    if (role == "docente") screen = "teacher_panel"
                    else screen = "parents_panel"
                }
            )
            "create" -> CreateProfileScreen(
                onCreated = { name, avatar ->
                    student = student.copy(name = name, avatar = avatar)
                    screen = "exam"
                }
            )
            "exam" -> AdaptiveExamScreen(
                student = student,
                onFinish = { skills, level ->
                    student = student.copy(skills = skills, level = if (level == "Avanzado") 6 else 5)
                    screen = "menu"
                }
            )
            "menu" -> MainMenuScreen(
                student = student,
                onPlay = { op ->
                    gameOp = op
                    screen = "game"
                },
                onRewards = { screen = "rewards" },
                onExam = { screen = "exam" }
            )
            "game" -> GameScreen(
                student = student,
                operation = gameOp,
                onScore = { pts -> student = student.copy(stars = (student.stars + pts).coerceAtMost(210)) },
                onBack = { screen = "menu" }
            )
            "rewards" -> RewardsScreen(
                student = student,
                onBack = { screen = "menu" }
            )
            "teacher_panel" -> TeacherPanel(onBack = { screen = "welcome" })
            "parents_panel" -> ParentsPanel(student = student, onBack = { screen = "welcome" })
        }
    }
}

// ─── Screens ────────────────────────────────────────────────────────────────

@Composable
fun WelcomeScreen(onCreateProfile: () -> Unit, onLogin: (String?) -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color.White)) {
            Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("🧮", fontSize = 40.sp)
                Text("MathIA", fontSize = 30.sp, fontWeight = FontWeight.ExtraBold, color = AppColors.Purple)
                Text("✨ ¡Aprende mates jugando! ✨", fontSize = 13.sp, color = AppColors.Gray600)
            }
        }

        Box(
            modifier = Modifier.size(160.dp).background(AppColors.PinkLight, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text("🦎", fontSize = 90.sp)
        }

        Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color.White)) {
            Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("¡Bienvenidos a MathIA!", fontSize = 22.sp, fontWeight = FontWeight.ExtraBold)
                Text("¡Hola! Soy Mateo, tu amigo ajolote.\n¡Vamos a aprender matemáticas juntos!", 
                    fontSize = 14.sp, color = AppColors.Gray600, textAlign = TextAlign.Center)
            }
        }

        Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Button(onClick = onCreateProfile, modifier = Modifier.fillMaxWidth().height(56.dp), colors = ButtonDefaults.buttonColors(containerColor = AppColors.Purple), shape = RoundedCornerShape(12.dp)) {
                Text("+ Crear Nuevo Perfil", fontWeight = FontWeight.Bold)
            }
            OutlinedButton(onClick = { onLogin(null) }, modifier = Modifier.fillMaxWidth().height(56.dp), shape = RoundedCornerShape(12.dp), border = BorderStroke(1.5.dp, AppColors.Gray200)) {
                Text("→] Iniciar Sesión", color = AppColors.Gray800)
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                TextButton(onClick = { onLogin("docente") }) { Text("Panel Docente", color = AppColors.Purple) }
                Spacer(modifier = Modifier.width(24.dp))
                TextButton(onClick = { onLogin("padres") }) { Text("Panel Padres", color = AppColors.Purple) }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CreateProfileScreen(onCreated: (String, String) -> Unit) {
    var name by remember { mutableStateOf("") }
    var age by remember { mutableStateOf(7) }
    var avatarIdx by remember { mutableStateOf(0) }
    val avatars = listOf("🦸", "👨‍🚀", "🧙‍♀️", "🦄", "🐉", "🦊", "🐼", "🦁", "🐯", "🐨")

    Column(modifier = Modifier.fillMaxSize().padding(24.dp).verticalScroll(rememberScrollState()), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(20.dp)) {
        Text("Crear Perfil", fontSize = 26.sp, fontWeight = FontWeight.ExtraBold, color = AppColors.Purple)
        Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color.White)) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Column {
                    Text("¿Cómo te llamas?", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    TextField(value = name, onValueChange = { name = it }, placeholder = { Text("Tu nombre...") }, modifier = Modifier.fillMaxWidth().padding(top = 6.dp))
                }
                Column {
                    Text("¿Cuántos años tienes?", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    Row(modifier = Modifier.fillMaxWidth().padding(top = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf(6, 7, 8).forEach { a ->
                            Button(onClick = { age = a }, modifier = Modifier.weight(1f), colors = ButtonDefaults.buttonColors(containerColor = if (age == a) AppColors.Pink else AppColors.Gray100)) {
                                Text("$a años", color = if (age == a) Color.White else AppColors.Gray600)
                            }
                        }
                    }
                }
                Column {
                    Text("Elige tu avatar", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    FlowRow(modifier = Modifier.fillMaxWidth().padding(top = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        avatars.forEachIndexed { i, em ->
                            Box(modifier = Modifier.size(48.dp).clip(RoundedCornerShape(12.dp)).background(if (avatarIdx == i) AppColors.AmberLight else AppColors.Gray100).border(2.dp, if (avatarIdx == i) AppColors.Amber else Color.Transparent, RoundedCornerShape(12.dp)).clickable { avatarIdx = i }, contentAlignment = Alignment.Center) {
                                Text(em, fontSize = 24.sp)
                            }
                        }
                    }
                }
                Button(onClick = { if (name.isNotBlank()) onCreated(name, avatars[avatarIdx]) }, modifier = Modifier.fillMaxWidth(), enabled = name.isNotBlank()) {
                    Text("Continuar →")
                }
            }
        }
    }
}

@Composable
fun AdaptiveExamScreen(student: Student, onFinish: (Map<String, Int>, String) -> Unit) {
    var qIdx by remember { mutableStateOf(0) }
    var results by remember { mutableStateOf(listOf<Boolean>()) }
    var done by remember { mutableStateOf(false) }
    val questions = ADAPTIVE_QUESTIONS.take(8)
    val q = questions[qIdx]

    if (done) {
        val correct = results.count { it }
        val pct = (correct * 100) / results.size
        val level = if (pct >= 80) "Avanzado" else if (pct >= 50) "Intermedio" else "Básico"
        val skills = mapOf("Sumas" to (pct + 10).coerceAtMost(100), "Restas" to pct, "Números" to 70, "Lógica" to 55, "Problemas" to 45)
        
        Column(modifier = Modifier.fillMaxSize().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Text("📊 Resultados del Examen", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = AppColors.Purple)
            Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(if (pct >= 80) "🏆" else if (pct >= 50) "⭐" else "📚", fontSize = 48.sp)
                    Text("$pct%", fontSize = 36.sp, fontWeight = FontWeight.Black, color = AppColors.Purple)
                    Text("$correct de ${results.size} correctas", fontSize = 14.sp, color = AppColors.Gray600)
                    Box(modifier = Modifier.padding(top = 12.dp).background(AppColors.PurpleLight, RoundedCornerShape(99.dp)).padding(horizontal = 16.dp, vertical = 6.dp)) {
                        Text("Nivel: $level", color = AppColors.Purple, fontWeight = FontWeight.Bold)
                    }
                }
            }
            Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("🗺️ Mapa de Habilidades", fontWeight = FontWeight.Bold)
                    RadarChart(skills = skills)
                }
            }
            Button(onClick = { onFinish(skills, level) }, modifier = Modifier.fillMaxWidth()) {
                Text("Ir al Menú Principal →")
            }
        }
    } else {
        Column(modifier = Modifier.fillMaxSize().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(20.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Examen Adaptativo", fontWeight = FontWeight.Bold, color = AppColors.Purple)
                Text("${qIdx + 1} / ${questions.size}", fontSize = 13.sp, color = AppColors.Gray400)
            }
            LinearProgressIndicator(progress = { qIdx.toFloat() / questions.size }, modifier = Modifier.fillMaxWidth(), color = AppColors.Purple)
            Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                Column(modifier = Modifier.padding(vertical = 40.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Resuelve:", fontSize = 13.sp, color = AppColors.Gray400)
                    Text("${q.q} = ?", fontSize = 52.sp, fontWeight = FontWeight.Black)
                }
            }
            val options = (listOf(q.a, q.a + 1, q.a - 1, q.a + 2).distinct().take(4)).shuffled()
            Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                options.forEach { opt ->
                    Button(onClick = {
                        results = results + (opt == q.a)
                        if (qIdx + 1 < questions.size) qIdx++ else done = true
                    }, modifier = Modifier.fillMaxWidth().height(60.dp), colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = AppColors.Gray800), border = BorderStroke(2.dp, AppColors.Gray200)) {
                        Text("$opt", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun MainMenuScreen(student: Student, onPlay: (String) -> Unit, onRewards: () -> Unit, onExam: () -> Unit) {
    LazyColumn(modifier = Modifier.fillMaxSize().padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        item {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Box(modifier = Modifier.size(48.dp).background(AppColors.AmberLight, CircleShape), contentAlignment = Alignment.Center) {
                        Text(student.avatar, fontSize = 28.sp)
                    }
                    Column {
                        Text("¡Hola, ${student.name}!", fontWeight = FontWeight.Bold, fontSize = 17.sp)
                        Text("Nivel ${student.level} • ${student.stars} estrellas", fontSize = 12.sp, color = AppColors.Gray400)
                    }
                }
                Button(onClick = onRewards, colors = ButtonDefaults.buttonColors(containerColor = AppColors.Amber)) {
                    Text("🏆 Premios")
                }
            }
        }

        item {
            Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    StarProgress(current = student.stars)
                }
            }
        }

        item {
            Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("🔥", fontSize = 28.sp)
                    Column {
                        Text("¡Racha de ${student.streak} días!", fontWeight = FontWeight.Bold, color = AppColors.Amber)
                        StreakCalendar(streak = student.streak)
                    }
                }
            }
        }

        item {
            Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("🤖", fontSize = 18.sp)
                        Text("Análisis de IA", fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        InfoBox("✅ Excelente en sumas — mejoraste un 15% esta semana", AppColors.GreenLight, AppColors.Green)
                        InfoBox("ℹ️ Mejor momento para aprender: 9AM–11AM", AppColors.BlueLight, AppColors.Blue)
                        InfoBox("⚠️ Área de oportunidad: practicar restas complejas", AppColors.AmberLight, AppColors.Amber)
                    }
                }
            }
        }

        item { Text("Tus Desafíos", fontWeight = FontWeight.Bold, fontSize = 17.sp) }

        items(listOf(
            Triple("📈", "Mejorando en sumas", "Suma"),
            Triple("🎯", "Restas básicas", "Resta"),
            Triple("🧠", "Examen Adaptativo", "Exam")
        )) { (icon, title, op) ->
            Card(modifier = Modifier.fillMaxWidth().clickable { if (op == "Exam") onExam() else onPlay(op) }, colors = CardDefaults.cardColors(containerColor = Color.White)) {
                Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(44.dp).background(AppColors.PurpleLight, RoundedCornerShape(12.dp)), contentAlignment = Alignment.Center) {
                        Text(icon, fontSize = 22.sp)
                    }
                    Column(modifier = Modifier.padding(start = 14.dp).weight(1f)) {
                        Text(title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text("¡Descubre tu nivel!", fontSize = 12.sp, color = AppColors.Gray400)
                    }
                    Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = AppColors.Gray200)
                }
            }
        }

        item {
            Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("🗺️ Mapa de Habilidades", fontWeight = FontWeight.Bold)
                    RadarChart(skills = student.skills)
                }
            }
        }
    }
}

@Composable
fun InfoBox(text: String, bg: Color, fg: Color) {
    Box(modifier = Modifier.fillMaxWidth().background(bg, RoundedCornerShape(10.dp)).padding(horizontal = 12.dp, vertical = 8.dp)) {
        Text(text, fontSize = 12.sp, color = fg)
    }
}

@Composable
fun GameScreen(student: Student, operation: String, onScore: (Int) -> Unit, onBack: () -> Unit) {
    var n1 by remember { mutableStateOf((1..15).random()) }
    var n2 by remember { mutableStateOf((1..n1).random()) }
    var userAnswer by remember { mutableStateOf("") }
    var msg by remember { mutableStateOf("¡Resuelve el desafío!") }
    var streak by remember { mutableStateOf(0) }

    val correct = if (operation == "Suma") n1 + n2 else n1 - n2

    Column(modifier = Modifier.fillMaxSize().background(Color(0xFFF0F4C3)).padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back") }
            Text(student.name, fontWeight = FontWeight.Bold)
            Text("⭐ ${student.stars}", color = AppColors.Amber, fontWeight = FontWeight.Bold)
        }

        Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color.White)) {
            Column(modifier = Modifier.padding(vertical = 40.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = if (operation == "Suma") "$n1 + $n2" else "$n1 - $n2", fontSize = 56.sp, fontWeight = FontWeight.Black)
                Text(text = "= ${userAnswer.ifEmpty { "?" }}", fontSize = 40.sp, color = if (userAnswer.isEmpty()) AppColors.Gray400 else AppColors.Purple, fontWeight = FontWeight.Bold)
            }
        }

        Text(msg, fontWeight = FontWeight.Bold, color = AppColors.Gray600)

        val keys = listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "C", "0", "OK")
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            keys.chunked(3).forEach { row ->
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    row.forEach { key ->
                        Button(
                            onClick = {
                                when (key) {
                                    "C" -> userAnswer = ""
                                    "OK" -> {
                                        if (userAnswer.toIntOrNull() == correct) {
                                            val pts = if (streak >= 2) 15 else 10
                                            onScore(pts)
                                            streak++
                                            msg = "¡Excelente ${student.name}! 🎉"
                                            n1 = (1..15).random()
                                            n2 = (1..n1).random()
                                            userAnswer = ""
                                        } else {
                                            msg = "¡Casi! Intenta de nuevo 🤔"
                                            streak = 0
                                            userAnswer = ""
                                        }
                                    }
                                    else -> if (userAnswer.length < 2) userAnswer += key
                                }
                            },
                            modifier = Modifier.weight(1f).height(64.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = when (key) {
                                    "OK" -> AppColors.Green
                                    "C" -> AppColors.Red
                                    else -> Color.White
                                },
                                contentColor = if (key == "OK" || key == "C") Color.White else AppColors.Gray800
                            ),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Text(key, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RewardsScreen(student: Student, onBack: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize().padding(24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back") }
            Text("🏆 Mis Premios", fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, color = AppColors.Purple)
        }
        Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color.White)) {
            Column(modifier = Modifier.padding(16.dp)) {
                StarProgress(current = student.stars)
            }
        }
        LazyVerticalGrid(columns = GridCells.Fixed(2), verticalArrangement = Arrangement.spacedBy(10.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            items(REWARDS) { r ->
                val unlocked = student.stars >= r.stars
                Card(
                    modifier = Modifier.aspectRatio(1f).border(if (unlocked) 2.dp else 0.dp, if (unlocked) AppColors.Amber else Color.Transparent, RoundedCornerShape(16.dp)),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = if (unlocked) 4.dp else 0.dp)
                ) {
                    Column(modifier = Modifier.padding(8.dp).fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                        Text(r.emoji, fontSize = 32.sp, modifier = Modifier.alpha(if (unlocked) 1f else 0.45f))
                        Text(r.name, fontWeight = FontWeight.Bold, fontSize = 13.sp, textAlign = TextAlign.Center)
                        Text("⭐ ${r.stars}", fontSize = 11.sp, color = AppColors.Amber, fontWeight = FontWeight.Bold)
                        if (unlocked) Text("✓ Desbloqueado", fontSize = 10.sp, color = AppColors.Green, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun LoginScreen(defaultRole: String, onBack: () -> Unit, onLoggedIn: (String) -> Unit) {
    var role by remember { mutableStateOf(defaultRole) }
    var email by remember { mutableStateOf("") }
    var pw by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize().padding(32.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(20.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back") }
            Text("Iniciar Sesión", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }

        Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color.White)) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("docente", "padres").forEach { r ->
                        Button(onClick = { role = r }, modifier = Modifier.weight(1f), colors = ButtonDefaults.buttonColors(containerColor = if (role == r) AppColors.Purple else AppColors.Gray100)) {
                            Text(if (r == "docente") "👩‍🏫 Docente" else "👨‍👩‍👧 Padres", fontSize = 14.sp, color = if (role == r) Color.White else AppColors.Gray600)
                        }
                    }
                }
                TextField(value = email, onValueChange = { email = it }, placeholder = { Text("Correo electrónico") }, modifier = Modifier.fillMaxWidth())
                TextField(value = pw, onValueChange = { pw = it }, placeholder = { Text("Contraseña") }, modifier = Modifier.fillMaxWidth(), visualTransformation = PasswordVisualTransformation())
                Button(onClick = { onLoggedIn(role) }, modifier = Modifier.fillMaxWidth()) { Text("Entrar") }
            }
        }
    }
}

@Composable
fun TeacherPanel(onBack: () -> Unit) {
    var selected by remember { mutableStateOf<Student?>(null) }
    
    if (selected != null) {
        Column(modifier = Modifier.fillMaxSize().padding(20.dp).verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                IconButton(onClick = { selected = null }) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back") }
                Text(selected!!.name, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            }
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Uso semanal", fontWeight = FontWeight.Bold)
                    MiniBarChart(data = selected!!.weekData, color = AppColors.Purple)
                }
            }
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Mapa de Habilidades", fontWeight = FontWeight.Bold)
                    RadarChart(skills = selected!!.skills)
                }
            }
        }
    } else {
        LazyColumn(modifier = Modifier.fillMaxSize().padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            item {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back") }
                    Text("Panel del Docente", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                }
            }
            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("🤖", fontSize = 18.sp)
                            Text("Análisis de IA", fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        InfoBox("⚠️ Juan Díaz muestra dificultad en restas. Recomendado: ejercicios básicos", AppColors.AmberLight, AppColors.Amber)
                        Spacer(modifier = Modifier.height(8.dp))
                        InfoBox("✅ María Pérez está superando el promedio del grupo", AppColors.GreenLight, AppColors.Green)
                    }
                }
            }
            items(MOCK_STUDENTS) { s ->
                Card(modifier = Modifier.fillMaxWidth().clickable { selected = s }, colors = CardDefaults.cardColors(containerColor = Color.White)) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Box(modifier = Modifier.size(40.dp).background(AppColors.PurpleLight, CircleShape), contentAlignment = Alignment.Center) { Text("🧑‍🎓") }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(s.name, fontWeight = FontWeight.Bold)
                            Text("Nivel ${s.level} • ${s.stars} estrellas", fontSize = 11.sp, color = AppColors.Gray400)
                        }
                        Text("${s.accuracy}%", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun ParentsPanel(student: Student, onBack: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize().padding(20.dp).verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        IconButton(onClick = onBack) { Text("← Volver", fontSize = 16.sp, color = AppColors.Purple) }
        Text("Panel de Padres", fontSize = 20.sp, fontWeight = FontWeight.ExtraBold)

        Box(modifier = Modifier.fillMaxWidth().background(Brush.linearGradient(listOf(AppColors.Purple, AppColors.Pink)), RoundedCornerShape(16.dp)).padding(20.dp)) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Box(modifier = Modifier.size(44.dp).background(Color.White.copy(alpha = 0.2f), CircleShape), contentAlignment = Alignment.Center) { Text("🧑‍🎓", fontSize = 24.sp) }
                    Column {
                        Text(student.name, fontWeight = FontWeight.Bold, color = Color.White, fontSize = 18.sp)
                        Text("7 años • 2do Grado • Nivel ${student.level}", color = Color.White.copy(alpha = 0.85f), fontSize = 12.sp)
                    }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("⭐ ${student.stars} estrellas", color = Color.White, modifier = Modifier.background(Color.White.copy(alpha = 0.2f), CircleShape).padding(horizontal = 12.dp, vertical = 4.dp), fontSize = 12.sp)
                    Text("🔥 Racha de ${student.streak} días", color = Color.White, modifier = Modifier.background(Color.White.copy(alpha = 0.2f), CircleShape).padding(horizontal = 12.dp, vertical = 4.dp), fontSize = 12.sp)
                }
            }
        }

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Recomendaciones de IA", fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(12.dp))
                InfoBox("✅ Excelente progreso en Sumas", AppColors.GreenLight, AppColors.Green)
                Spacer(modifier = Modifier.height(8.dp))
                InfoBox("⚠️ Área de oportunidad: problemas", AppColors.AmberLight, AppColors.Amber)
            }
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Card(modifier = Modifier.weight(1f)) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("Tiempo", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    MiniBarChart(data = student.weekData, color = AppColors.Purple)
                }
            }
            Card(modifier = Modifier.weight(1f)) {
                Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Habilidades", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    RadarChart(skills = student.skills, size = 100.dp)
                }
            }
        }
    }
}
