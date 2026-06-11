// MEJORA 3: MainActivity.kt
package com.example.mathia

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import com.example.mathia.ui.theme.MathkidsTheme
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// ─── Data Models ───────────────────────────────────────────────────────────
data class Student(
    val id: Int,
    val name: String,
    val lastName: String,
    val grade: String,
    val classroom: String,
    val pin: String,
    val level: Int,
    val stars: Int,
    val totalQuestions: Int = 0,
    val correctAnswers: Int = 0,
    val examsCompleted: Int = 0,
    val accuracy: Int,
    val streak: Int,
    val weekData: List<Int>,
    val skills: Map<String, Int>,
    val avatar: String,
    val diagnosticoRealizado: Boolean = false,
    val tiempoTotal: Long = 0,
    val tiempoPromedio: Double = 0.0,
    val equippedTheme: String = "Lila Clásico",
    val unlockedAvatars: List<String> = listOf("👶"),
    val unlockedThemes: List<String> = listOf("Lila Clásico")
)

data class Reward(
    val id: Int,
    val name: String,
    val emoji: String,
    val stars: Int,
    val desc: String
)

data class ExamResponse(
    val pregunta: String,
    val correcta: Boolean,
    val tiempo: Long,
    val dificultad: Int
)
// ─── Constants ─────────────────────────────────────────────────────────────
object AppColors {
    val Purple = Color(0xFF7C3AED)
    val PurpleLight = Color(0xFFEDE9FE)
    val Pink = Color(0xFFFF6B9D)       // MEJORA 2: Pink infantil modificado
    val PinkLight = Color(0xFFFCE7F3)
    val Green = Color(0xFF6BCB77)      // MEJORA 2: Green infantil modificado
    val GreenLight = Color(0xFFD1FAE5)
    val Amber = Color(0xFFFFD93D)      // MEJORA 2: Amber infantil modificado
    val AmberLight = Color(0xFFFEF3C7)
    val Blue = Color(0xFF3B82F6)
    val Red = Color(0xFFEF4444)
    val Bg = Color(0xFFF0E6FF)         // MEJORA 2: Fondo modificado a Lila Pastel
    val White = Color(0xFFFFFFFF)
    val Gray100 = Color(0xFFF3F4F6)
    val Gray200 = Color(0xFFE5E7EB)
    val Gray400 = Color(0xFF9CA3AF)
    val Gray500 = Color(0xFF6B7280)
    val Gray600 = Color(0xFF4B5563)
    val Gray700 = Color(0xFF374151)
    val Gray800 = Color(0xFF1F2937)
}

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

val MOCK_STUDENTS = mutableListOf(
    Student(
        id = 1,
        name = "Ana",
        lastName = "Pérez",
        grade = "1ro",
        classroom = "1ro A",
        pin = "1234",
        level = 5,
        stars = 142,
        totalQuestions = 50,
        correctAnswers = 42,
        examsCompleted = 3,
        accuracy = 85,
        streak = 7,
        weekData = listOf(20, 25, 30, 35, 22, 18, 8),
        skills = mapOf(
            "Sumas" to 85,
            "Restas" to 70,
            "Números" to 90,
            "Lógica" to 60,
            "Problemas" to 65
        ),
        avatar = "🦄",
        diagnosticoRealizado = true,
        tiempoTotal = 120,
        tiempoPromedio = 12.0,
        equippedTheme = "Lila Clásico",
        unlockedAvatars = listOf("👶", "🦄"),
        unlockedThemes = listOf("Lila Clásico")
    ),
    Student(
        id = 2,
        name = "Carlos",
        lastName = "Díaz",
        grade = "2do",
        classroom = "2do B",
        pin = "5678",
        level = 4,
        stars = 98,
        totalQuestions = 30,
        correctAnswers = 22,
        examsCompleted = 2,
        accuracy = 72,
        streak = 3,
        weekData = listOf(15, 20, 18, 25, 30, 10, 5),
        skills = mapOf(
            "Sumas" to 70,
            "Restas" to 55,
            "Números" to 75,
            "Lógica" to 50,
            "Problemas" to 45
        ),
        avatar = "🐉",
        diagnosticoRealizado = true,
        tiempoTotal = 95,
        tiempoPromedio = 9.5,
        equippedTheme = "Lila Clásico",
        unlockedAvatars = listOf("👶", "🐉"),
        unlockedThemes = listOf("Lila Clásico")
    )
)

// ─── SelectLoginTypeScreen ────────────────────────────────────────────────
@Composable
fun SelectLoginTypeScreen(
    onStudent: () -> Unit,
    onTeacher: () -> Unit,
    onParent: () -> Unit,
    onBack: () -> Unit
) {
    // MEJORA 3: Tarjetas de 100.dp de alto con emoji, título y subtítulo en tonos pastel
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.Bg)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically)
    ) {
        Text(
            text = "¿Quién va a ingresar?",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = AppColors.Purple
        )
        Spacer(Modifier.height(16.dp))

        // Tarjeta del Estudiante (Verde Pastel)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .clickable { onStudent() },
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text("🧒", fontSize = 40.sp)
                Column {
                    Text("Niño / Estudiante", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = AppColors.Gray800)
                    Text("¡Juega, gana estrellas y aprende!", fontSize = 12.sp, color = AppColors.Gray600)
                }
            }
        }

        // Tarjeta del Docente (Azul Pastel)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .clickable { onTeacher() },
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD)),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text("👩‍🏫", fontSize = 40.sp)
                Column {
                    Text("Profesor / Docente", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = AppColors.Gray800)
                    Text("Analiza las estadísticas de tu grupo", fontSize = 12.sp, color = AppColors.Gray600)
                }
            }
        }

        // Tarjeta del Padre (Naranja Pastel)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .clickable { onParent() },
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0)),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text("👨‍👩‍👧", fontSize = 40.sp)
                Column {
                    Text("Padre / Madre", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = AppColors.Gray800)
                    Text("Monitorea los avances de tus hijos", fontSize = 12.sp, color = AppColors.Gray600)
                }
            }
        }

        Spacer(Modifier.height(16.dp))
        OutlinedButton(
            onClick = onBack,
            shape = RoundedCornerShape(50.dp),
            border = BorderStroke(2.dp, AppColors.Purple)
        ) {
            Text("Volver", color = AppColors.Purple, fontWeight = FontWeight.Bold)
        }
    }
}

// ─── LoginScreen ──────────────────────────────────────────────────────────
@Composable
fun LoginScreen(onBack: () -> Unit, onLoginSuccess: (Student) -> Unit, viewModel: StudentViewModel) {
    var pin by remember { mutableStateOf("") }
    var error by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.Bg)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(100.dp)
                .background(AppColors.PinkLight, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text("🧮", fontSize = 50.sp)
        }
        Spacer(Modifier.height(16.dp))
        Text(
            text = "Ingresa tu PIN",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = AppColors.Purple
        )
        Spacer(Modifier.height(24.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally),
                    modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp)
                ) {
                    for (i in 0 until 4) {
                        val isFilled = i < pin.length
                        Box(
                            modifier = Modifier
                                .size(20.dp)
                                .clip(CircleShape)
                                .background(if (isFilled) AppColors.Purple else AppColors.Gray200)
                                .border(1.5.dp, if (isFilled) AppColors.Purple else AppColors.Gray400, CircleShape)
                        )
                    }
                }

                if (error.isNotEmpty()) {
                    Text(error, color = AppColors.Red, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
                if (isLoading) {
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth(), color = AppColors.Purple)
                }

                val keys = listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "C", "0", "OK")
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    keys.chunked(3).forEach { row ->
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            row.forEach { key ->
                                Button(
                                    onClick = {
                                        when (key) {
                                            "C" -> pin = ""
                                            "OK" -> {
                                                if (pin.length == 4) {
                                                    isLoading = true
                                                    error = ""
                                                    viewModel.login(pin) { alumno ->
                                                        isLoading = false
                                                        if (alumno != null) {
                                                            onLoginSuccess(
                                                                Student(
                                                                    id = alumno.pin,
                                                                    name = alumno.nombre.split(" ").firstOrNull() ?: alumno.nombre,
                                                                    lastName = alumno.nombre.split(" ").getOrNull(1) ?: "",
                                                                    grade = alumno.grado,
                                                                    classroom = "",
                                                                    pin = alumno.pin.toString(),
                                                                    level = alumno.nivel_actual,
                                                                    stars = alumno.estrellas,
                                                                    accuracy = alumno.precision.toInt(),
                                                                    streak = 3,
                                                                    weekData = listOf(10, 12, 14, 18, 20, 8, 4),
                                                                    skills = mapOf(
                                                                        "Sumas" to alumno.precision.toInt(),
                                                                        "Restas" to alumno.precision.toInt(),
                                                                        "Números" to alumno.precision.toInt(),
                                                                        "Lógica" to alumno.precision.toInt(),
                                                                        "Problemas" to alumno.precision.toInt(),
                                                                        "Multiplicación" to alumno.precision.toInt(),
                                                                        "Fracciones" to alumno.precision.toInt(),
                                                                        "Series" to alumno.precision.toInt()
                                                                    ),
                                                                    avatar = alumno.avatar,
                                                                    equippedTheme = alumno.equipped_theme,
                                                                    unlockedAvatars = alumno.unlocked_avatars,
                                                                    unlockedThemes = alumno.unlocked_themes
                                                                )
                                                            )
                                                        } else {
                                                            error = "PIN incorrecto"
                                                        }
                                                    }
                                                } else {
                                                    error = "El PIN debe tener 4 dígitos"
                                                }
                                            }
                                            else -> {
                                                if (pin.length < 4) {
                                                    pin += key
                                                }
                                            }
                                        }
                                    },
                                    modifier = Modifier.size(64.dp),
                                    shape = RoundedCornerShape(16.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = when (key) {
                                            "OK" -> AppColors.Green
                                            "C" -> AppColors.Red
                                            else -> Color(0xFFF3F4F6)
                                        },
                                        contentColor = if (key == "OK" || key == "C") Color.White else AppColors.Gray800
                                    ),
                                    contentPadding = PaddingValues(0.dp)
                                ) {
                                    Text(key, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }

                Spacer(Modifier.height(8.dp))
                OutlinedButton(
                    onClick = onBack,
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = RoundedCornerShape(50.dp)
                ) {
                    Text("Volver", color = AppColors.Purple)
                }
            }
        }
    }
}

// ─── RadarChart ────────────────────────────────────────────────────────────
@Composable
fun RadarChart(skills: Map<String, Int>, size: Dp = 160.dp) {
    val keys = skills.keys.toList()
    val values = skills.values.toList()
    val n = keys.size

    Canvas(modifier = Modifier.size(size)) {
        val center = Offset(size.toPx() / 2f, size.toPx() / 2f)
        val radius = size.toPx() * 0.38f

        listOf(0.25f, 0.5f, 0.75f, 1f).forEach { lvl ->
            val path = Path()
            for(i in 0 until n) {
                val angle = (2 * PI * i) / n - PI / 2
                val x = center.x + (radius * lvl) * cos(angle).toFloat()
                val y = center.y + (radius * lvl) * sin(angle).toFloat()
                if(i == 0) path.moveTo(x,y) else path.lineTo(x,y)
            }
            path.close()
            drawPath(path = path, color = AppColors.Gray200, style = Stroke(1.dp.toPx()))
        }
        for(i in 0 until n) {
            val angle = (2 * PI * i)/n - PI/2
            drawLine(color = AppColors.Gray200, start = center,
                end = Offset(center.x + radius * cos(angle).toFloat(), center.y + radius * sin(angle).toFloat()),
                strokeWidth = 1.dp.toPx())
        }
        val skillPath = Path()
        for(i in 0 until n) {
            val angle = (2 * PI * i)/n - PI/2
            val r = (values[i]/100f) * radius
            val x = center.x + r * cos(angle).toFloat()
            val y = center.y + r * sin(angle).toFloat()
            if(i==0) skillPath.moveTo(x,y) else skillPath.lineTo(x,y)
            drawCircle(color = AppColors.Purple, radius = 3.dp.toPx(), center = Offset(x,y))
        }
        skillPath.close()
        drawPath(path = skillPath, color = AppColors.Purple.copy(alpha = 0.2f))
        drawPath(path = skillPath, color = AppColors.Purple, style = Stroke(width = 1.5.dp.toPx()))
    }
}

// ─── MiniBarChart ──────────────────────────────────────────────────────────
@Composable
fun MiniBarChart(data: List<Int>, color: Color) {
    val max = data.maxOrNull()?.coerceAtLeast(1) ?: 1
    Canvas(modifier = Modifier.width(160.dp).height(60.dp)) {
        val barW = 18.dp.toPx()
        val gap = 4.dp.toPx()
        data.forEachIndexed { i, v ->
            val bh = (v.toFloat() / max) * size.height
            drawRoundRect(color = color.copy(alpha = 0.8f),
                topLeft = Offset(i * (barW + gap), size.height - bh),
                size = Size(barW, bh),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(3.dp.toPx()))
        }
    }
}

// ─── StarProgress ──────────────────────────────────────────────────────────
@Composable
fun StarProgress(current: Int, max: Int = 210) {
    val pct = (current.toFloat() / max).coerceIn(0f, 1f)
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("⭐ $current / $max estrellas", fontSize = 12.sp, color = AppColors.Gray600)
            Text("${(pct * 100).toInt()}%", fontSize = 12.sp, color = AppColors.Purple, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.height(4.dp))
        LinearProgressIndicator(progress = { pct }, modifier = Modifier.fillMaxWidth().height(8.dp),
            color = AppColors.Purple, trackColor = AppColors.Gray200)
    }
}

// ─── StreakCalendar ────────────────────────────────────────────────────────
@Composable
fun StreakCalendar(streak: Int) {
    val days = listOf("L", "M", "X", "J", "V", "S", "D")
    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        days.forEachIndexed { i, d ->
            val active = i < (streak % 7)
            Box(modifier = Modifier.size(28.dp).background(if (active) AppColors.Amber else AppColors.Gray100, RoundedCornerShape(6.dp)),
                contentAlignment = Alignment.Center) {
                Text(d, fontSize = 10.sp, color = if (active) Color.White else AppColors.Gray400, fontWeight = FontWeight.Bold)
            }
        }
    }
}

// ─── InfoBox ───────────────────────────────────────────────────────────────
@Composable
fun InfoBox(text: String, bg: Color, fg: Color) {
    Box(modifier = Modifier.fillMaxWidth().background(bg, RoundedCornerShape(10.dp)).padding(horizontal = 12.dp, vertical = 8.dp)) {
        Text(text, fontSize = 12.sp, color = fg)
    }
}

// ─── Components for Teacher Dashboard ──────────────────────────────────────
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

// ─── MainActivity ──────────────────────────────────────────────────────────
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        FirebaseMessaging.getInstance()
            .token
            .addOnSuccessListener { token ->
                println(token)
            }

        setContent {
            MathkidsTheme {
                MainApp()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        NotificationHelper.cancelDailyReminder(this)
    }

    override fun onStop() {
        super.onStop()
        NotificationHelper.scheduleDailyReminder(this)
    }
}

// ─── MainApp ───────────────────────────────────────────────────────────────
@Composable
fun MainApp() {
    var screen by remember { mutableStateOf("welcome") }
    var loginRole by remember { mutableStateOf("") }
    var student by remember { mutableStateOf<Student?>(null) }
    var gameOp by remember { mutableStateOf("Suma") }
    var parentEmail by remember { mutableStateOf("") }
    var loggedInUid by remember { mutableStateOf("") }
    var activeAlert by remember { mutableStateOf<DuolingoAlert?>(null) }
    val viewModel = remember { StudentViewModel() }

    fun logout() { loginRole = ""; student = null; parentEmail = ""; loggedInUid = ""; screen = "welcome" }

    val currentBgColor = remember(student?.equippedTheme) {
        when (student?.equippedTheme) {
            "Verde Menta" -> Color(0xFFE8F5E9)
            "Azul Espacial" -> Color(0xFFE3F2FD)
            "Amarillo Sol" -> Color(0xFFFFFDE7)
            "Rosa Algodón" -> Color(0xFFFCE4EC)
            else -> AppColors.Bg
        }
    }

    Surface(modifier = Modifier.fillMaxSize(), color = currentBgColor) {
        Box(modifier = Modifier.fillMaxSize()) {
            when (screen) {
                "welcome" -> WelcomeScreen(onCreateProfile = { screen = "create" }, onLogin = { screen = "select_login" })
                "select_login" -> SelectLoginTypeScreen(
                    onStudent = { loginRole = "student"; screen = "login" },
                    onTeacher = { loginRole = "docente"; screen = "adult_auth" },
                    onParent = { loginRole = "padres"; screen = "adult_auth" },
                    onBack = { screen = "welcome" }
                )
                "adult_auth" -> AdultAuthScreen(
                    rol = loginRole,
                    onBack = { screen = "select_login" },
                    onLoginSuccess = { role, email, uid ->
                        parentEmail = email
                        loggedInUid = uid
                        if (role == "docente_incomplete") {
                            screen = "complete_teacher_profile"
                        } else if (role == "padres_incomplete") {
                            screen = "complete_parent_profile"
                        } else {
                            screen = if (role == "docente") "teacher_panel" else "parents_panel"
                        }
                    },
                    viewModel = viewModel
                )
                "complete_teacher_profile" -> CompleteTeacherProfileScreen(
                    uid = loggedInUid,
                    email = parentEmail,
                    onCompleteSuccess = { screen = "teacher_panel" },
                    onLogout = { logout() },
                    viewModel = viewModel
                )
                "complete_parent_profile" -> CompleteParentProfileScreen(
                    uid = loggedInUid,
                    email = parentEmail,
                    onCompleteSuccess = { screen = "parents_panel" },
                    onLogout = { logout() },
                    viewModel = viewModel
                )
                "login" -> LoginScreen(
                    onBack = { screen = "select_login" },
                    onLoginSuccess = { loggedInStudent ->
                        student = loggedInStudent
                        activeAlert = DuolingoAlert(
                            title = "¡Bienvenido!",
                            message = "🦎 ¡Hola, ${loggedInStudent.name}! Mateo te da la bienvenida. ¿Listo para entrenar tu mente matemática de hoy?",
                            type = AlertType.MOTIVATIONAL,
                            buttonText = "¡Empezar!"
                        )
                        screen = "menu"
                    },
                    viewModel = viewModel
                )
                "create" -> {
                    val context = LocalContext.current
                    CreateProfileScreen(onCreated = { name, lastName, grade, classroom, avatar, pin ->
                        val newStudent = Student(id = MOCK_STUDENTS.size + 1, name = name, lastName = lastName, grade = grade,
                            classroom = classroom, pin = pin, level = 1, stars = 0, accuracy = 0, streak = 0,
                            weekData = listOf(0,0,0,0,0,0,0), skills = mapOf("Sumas" to 0, "Restas" to 0, "Números" to 0, "Lógica" to 0, "Problemas" to 0),
                            avatar = avatar, equippedTheme = "Lila Clásico", unlockedAvatars = listOf("👶", avatar), unlockedThemes = listOf("Lila Clásico"))
                        val alumnoFirebase = hashMapOf(
                            "nombre" to "$name $lastName",
                            "grado" to grade,
                            "edad" to 6,
                            "nivel_actual" to 1,
                            "precision" to 0.0,
                            "estrellas" to 0,
                            "pin" to pin.toInt(),
                            "padre_email" to parentEmail,
                            "avatar" to avatar,
                            "equipped_theme" to "Lila Clásico",
                            "unlocked_avatars" to listOf("👶", avatar),
                            "unlocked_themes" to listOf("Lila Clásico")
                        )
                        viewModel.crearAlumno(alumnoFirebase, pin) { success ->
                            if (success) { MOCK_STUDENTS.add(newStudent); screen = "welcome" }
                            else Toast.makeText(context, "Error al crear el perfil", Toast.LENGTH_SHORT).show()
                        }
                    }, onBack = { screen = "welcome" })
                }
                "menu" -> student?.let {
                    MainMenuScreen(
                        student = it,
                        onPlay = { op -> gameOp = op; screen = "game" },
                        onRewards = { screen = "rewards" },
                        onExam = { screen = "exam" },
                        onLogout = { logout() },
                        onUpdateStudent = { s -> student = s },
                        onShowAlert = { alert -> activeAlert = alert },
                        viewModel = viewModel
                    )
                }
                "game" -> student?.let {
                    GameScreen(
                        student = it,
                        operation = gameOp,
                        onScore = { puntos ->
                            viewModel.actualizarEstrellas(it.pin, puntos)
                            student = it.copy(stars = it.stars + puntos)
                            activeAlert = DuolingoAlert(
                                title = "¡Excelente!",
                                message = "⭐ ¡Ganaste $puntos estrellas! Mateo está brincando de alegría por ti. ¡Sigue así!",
                                type = AlertType.SUCCESS,
                                buttonText = "¡Genial!"
                            )
                        },
                        onBack = { screen = "menu" }
                    )
                }
                "rewards" -> student?.let { RewardsScreen(it, onBack = { screen = "menu" }) }
                "exam" -> student?.let {
                    AdaptiveExamScreen(
                        student = it,
                        onFinish = { nuevasSkills, nivel, correctas, total ->
                            val estrellasGanadas = correctas * 10
                            val nuevaPrecision = if (it.totalQuestions + total > 0) ((it.accuracy * it.totalQuestions + (correctas * 100 / total)) / (it.totalQuestions + 1)).coerceAtMost(100) else correctas * 100 / total
                            student = it.copy(stars = it.stars + estrellasGanadas, accuracy = nuevaPrecision, totalQuestions = it.totalQuestions + total,
                                correctAnswers = it.correctAnswers + correctas, examsCompleted = it.examsCompleted + 1, skills = nuevasSkills,
                                level = when (nivel) { "Avanzado" -> 3; "Intermedio" -> 2; else -> 1 })
                            
                            activeAlert = DuolingoAlert(
                                title = "Reto Completado",
                                message = "🏆 ¡Terminaste tu examen adaptativo! Tu nivel asignado es $nivel con $correctas de $total respuestas correctas. ¡Ganaste $estrellasGanadas estrellas!",
                                type = AlertType.CHALLENGE,
                                buttonText = "Ver mi Progreso"
                            )
                            screen = "menu"
                        },
                        viewModel = viewModel
                    )
                }
                "teacher_panel" -> TeacherPanel(uid = loggedInUid, onBack = { logout() })
                "parents_panel" -> ParentsPanel(onBack = { logout() }, onCreateStudentProfile = { screen = "create" }, parentEmail = parentEmail, student = student)
                "diagnostico" -> student?.let { DiagnosticoScreen(it, viewModel, onFinish = { screen = "menu" }) }
            }

            // Centralized alert overlay
            activeAlert?.let { alert ->
                DuolingoAlertDialog(
                    alert = alert,
                    onDismiss = { activeAlert = null }
                )
            }
        }
    }
}

// ─── WelcomeScreen ─────────────────────────────────────────────────────────
@Composable
fun WelcomeScreen(onCreateProfile: () -> Unit, onLogin: () -> Unit) {
    val context = LocalContext.current
    val imageLoader = remember(context) {
        ImageLoader.Builder(context).components {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) add(ImageDecoderDecoder.Factory())
            else add(GifDecoder.Factory())
        }.build()
    }

    val infiniteTransition = rememberInfiniteTransition(label = "ajolote_pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    val starOffsets = remember {
        List(12) {
            Offset(
                x = (50..950).random().toFloat(),
                y = (100..1800).random().toFloat()
            )
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            starOffsets.forEach { offset ->
                val size = 10f
                val path = Path().apply {
                    moveTo(offset.x, offset.y - size)
                    lineTo(offset.x + size * 0.3f, offset.y - size * 0.3f)
                    lineTo(offset.x + size, offset.y)
                    lineTo(offset.x + size * 0.3f, offset.y + size * 0.3f)
                    lineTo(offset.x, offset.y + size)
                    lineTo(offset.x - size * 0.3f, offset.y + size * 0.3f)
                    lineTo(offset.x - size, offset.y)
                    lineTo(offset.x - size * 0.3f, offset.y - size * 0.3f)
                    close()
                }
                drawPath(path, AppColors.Amber.copy(alpha = 0.7f))
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("🧮", fontSize = 40.sp)
                    Spacer(Modifier.height(8.dp))
                    Text("MathIA", fontSize = 30.sp, fontWeight = FontWeight.ExtraBold, color = AppColors.Purple)
                    Text("¡Aprende mates jugando!", fontSize = 13.sp, color = AppColors.Gray600)
                }
            }

            Box(
                modifier = Modifier
                    .size(160.dp)
                    .background(AppColors.PinkLight, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = R.drawable.ajolote,
                    imageLoader = imageLoader,
                    contentDescription = "Ajolote",
                    modifier = Modifier
                        .size(120.dp)
                        .scale(scale)
                )
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("¡Bienvenidos a MathIA!", fontSize = 22.sp, fontWeight = FontWeight.ExtraBold)
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "¡Hola! Soy Mateo, tu amigo ajolote.\n¡Vamos a aprender matemáticas juntos!",
                        fontSize = 14.sp,
                        color = AppColors.Gray600,
                        textAlign = TextAlign.Center
                    )
                }
            }

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = onCreateProfile,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AppColors.Purple)
                ) {
                    Text("+ Crear Nuevo Perfil", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
                OutlinedButton(
                    onClick = onLogin,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(50.dp),
                    border = BorderStroke(2.dp, AppColors.Purple)
                ) {
                    Text("→ Iniciar Sesión", color = AppColors.Purple, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }
        }
    }
}

// ─── CreateProfileScreen ───────────────────────────────────────────────────
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CreateProfileScreen(onCreated: (String, String, String, String, String, String) -> Unit, onBack: () -> Unit) {
    var name by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var grade by remember { mutableStateOf("primero") }
    var classroom by remember { mutableStateOf("1ro A") }
    var pin by remember { mutableStateOf("") }
    var avatarIdx by remember { mutableIntStateOf(0) }
    val avatars = listOf("🦸", "👨🚀", "🧙♀️", "🦄", "🐉", "🦊", "🐼", "🦁", "🐯", "🐨")

    Column(modifier = Modifier.fillMaxSize().padding(24.dp).verticalScroll(rememberScrollState()), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(20.dp)) {
        Text("Crear Perfil", fontSize = 26.sp, fontWeight = FontWeight.ExtraBold, color = AppColors.Purple)
        Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Column { Text("¿Cómo te llamas?", fontWeight = FontWeight.Bold); TextField(value = name, onValueChange = { name = it }, placeholder = { Text("Tu nombre...") }, modifier = Modifier.fillMaxWidth()) }
                Column { Text("Apellido", fontWeight = FontWeight.Bold); TextField(value = lastName, onValueChange = { lastName = it }, modifier = Modifier.fillMaxWidth()) }
                Column {
                    Text("Grado", fontWeight = FontWeight.Bold)
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("primero", "segundo", "tercero").forEach { g ->
                            Button(onClick = { grade = g }, modifier = Modifier.weight(1f), colors = ButtonDefaults.buttonColors(containerColor = if (grade == g) AppColors.Pink else AppColors.Gray100)) {
                                Text(g, color = if (grade == g) Color.White else AppColors.Gray600)
                            }
                        }
                    }
                }
                Column { Text("Salón", fontWeight = FontWeight.Bold); TextField(value = classroom, onValueChange = { classroom = it }, placeholder = { Text("Ej: 1ro A") }, modifier = Modifier.fillMaxWidth()) }
                Column {
                    Text("Elige tu avatar", fontWeight = FontWeight.Bold)
                    FlowRow(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        avatars.forEachIndexed { i, em ->
                            Box(modifier = Modifier.size(48.dp).clip(RoundedCornerShape(12.dp)).background(if (avatarIdx == i) AppColors.AmberLight else AppColors.Gray100)
                                .border(2.dp, if (avatarIdx == i) AppColors.Amber else Color.Transparent, RoundedCornerShape(12.dp)).clickable { avatarIdx = i }, contentAlignment = Alignment.Center) {
                                Text(em, fontSize = 24.sp)
                            }
                        }
                    }
                }
                Column { Text("Crea un PIN de 4 dígitos", fontWeight = FontWeight.Bold); TextField(value = pin, onValueChange = { if (it.length <= 4) pin = it }, placeholder = { Text("1234") }, visualTransformation = PasswordVisualTransformation(), modifier = Modifier.fillMaxWidth()) }
                Button(onClick = { if (name.isNotBlank() && lastName.isNotBlank() && pin.length == 4) onCreated(name, lastName, grade, classroom, avatars[avatarIdx], pin) },
                    modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(50.dp), enabled = (name.isNotBlank() && lastName.isNotBlank() && pin.length == 4)) { Text("Continuar →") }
                OutlinedButton(onClick = onBack, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(50.dp)) { Text("Volver") }
            }
        }
    }
}

// ─── AdaptiveExamScreen ────────────────────────────────────────────────────
@Composable
fun AdaptiveExamScreen(student: Student, onFinish: (Map<String, Int>, String, Int, Int) -> Unit, viewModel: StudentViewModel) {
    val repository = remember { QuestionRepository() }
    var questions by remember { mutableStateOf<List<QuestionFirebase>>(emptyList()) }
    var qIdx by remember { mutableIntStateOf(0) }
    var results by remember { mutableStateOf(listOf<Boolean>()) }
    var done by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(true) }
    var respuestas by remember { mutableStateOf(mutableListOf<ExamResponse>()) }
    var questionStartTime by remember { mutableLongStateOf(System.currentTimeMillis()) }

    LaunchedEffect(Unit) {
        val todas = repository.cargarPreguntas(student.grade.lowercase())
        questions = (todas.filter { it.dificultad == 1 }.shuffled().take(4) + todas.filter { it.dificultad == 2 }.shuffled().take(3) + todas.filter { it.dificultad == 3 }.shuffled().take(3)).shuffled()
        isLoading = false
    }

    val tiempoTotal = remember(respuestas) { respuestas.sumOf { it.tiempo } }
    val tiempoPromedio = remember(respuestas) { if (respuestas.isNotEmpty()) respuestas.map { it.tiempo }.average() else 0.0 }

    if (isLoading) { Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }; return }
    if (questions.isEmpty()) { Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Column { Text("No hay preguntas"); Button(onClick = { onFinish(emptyMap(), "Básico", 0, 0) }) { Text("Volver") } } }; return }

    if (!done && qIdx < questions.size) {
        val q = questions[qIdx]
        Column(modifier = Modifier.fillMaxSize().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(20.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Examen Adaptativo", fontWeight = FontWeight.Bold, color = AppColors.Purple)
                Text("${qIdx + 1}/${questions.size}", fontSize = 13.sp, color = AppColors.Gray400)
            }
            LinearProgressIndicator(progress = { qIdx.toFloat() / questions.size }, modifier = Modifier.fillMaxWidth(), color = AppColors.Purple)
            Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                Column(modifier = Modifier.padding(vertical = 40.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Resuelve:", fontSize = 13.sp, color = AppColors.Gray400)
                    Text(q.enunciado, fontSize = 28.sp, fontWeight = FontWeight.Black, textAlign = TextAlign.Center)
                }
            }
            val options = listOf(q.opcionA, q.opcionB, q.opcionC).filter { it.isNotEmpty() }.shuffled()
            Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                options.forEach { opt ->
                    Button(onClick = {
                        val correcta = opt == q.correcta
                        respuestas = respuestas.toMutableList().apply { add(ExamResponse(q.enunciado, correcta, (System.currentTimeMillis() - questionStartTime) / 1000, q.dificultad)) }
                        results = results + correcta
                        questionStartTime = System.currentTimeMillis()
                        qIdx++
                        if (qIdx >= questions.size) done = true
                    }, modifier = Modifier.fillMaxWidth().height(60.dp), shape = RoundedCornerShape(50.dp), colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = AppColors.Gray800), border = BorderStroke(2.dp, AppColors.Gray200)) {
                        Text(opt, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    } else if (done) {
        val correctas = results.count { it }
        val total = questions.size
        val porcentaje = (correctas.toFloat() / total) * 100
        val nivel = when { porcentaje >= 80 -> "Avanzado"; porcentaje >= 50 -> "Intermedio"; else -> "Básico" }
        val nuevasSkills = student.skills.toMutableMap()
        val skillPrincipal = when {
            questions.any { it.enunciado.contains("+") } -> "Sumas"
            questions.any { it.enunciado.contains("-") } -> "Restas"
            else -> "Problemas"
        }
        nuevasSkills[skillPrincipal] = ((student.skills[skillPrincipal] ?: 0) + porcentaje.toInt()) / 2

        Column(modifier = Modifier.fillMaxSize().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("¡Examen Completado!", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = AppColors.Purple)
                    Text("Puntaje: $correctas/$total", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Text("Tiempo total: ${tiempoTotal}s"); Text("Tiempo promedio: ${tiempoPromedio.toInt()}s")
                    Spacer(Modifier.height(24.dp))
                    Button(onClick = {
                        viewModel.guardarDiagnostico(student.pin, correctas, total - correctas, tiempoTotal, tiempoPromedio)
                        viewModel.enviarNotificacionExamen(
                            student.pin,
                            correctas,
                            porcentaje.toInt()
                        )
                        viewModel.notificarPadre(
                            student.pin,
                            student.name,
                            porcentaje.toInt()
                        )
                        viewModel.notificarDocente(
                            student.pin,
                            student.name,
                            porcentaje.toInt()
                        )
                        viewModel.actualizarEstrellas(student.pin, correctas * 10)
                        viewModel.actualizarPrecision(student.pin, porcentaje.toDouble())
                        onFinish(nuevasSkills, nivel, correctas, total)
                    }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(50.dp), colors = ButtonDefaults.buttonColors(containerColor = AppColors.Purple)) { Text("Finalizar") }
                }
            }
        }
    }
}

// ─── MainMenuScreen ────────────────────────────────────────────────────────
@Composable
fun MainMenuScreen(
    student: Student,
    onPlay: (String) -> Unit,
    onRewards: () -> Unit,
    onExam: () -> Unit,
    onLogout: () -> Unit,
    onUpdateStudent: (Student) -> Unit,
    onShowAlert: (DuolingoAlert) -> Unit,
    viewModel: StudentViewModel
) {
    var selectedTab by remember { mutableIntStateOf(0) }

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = Color.White
            ) {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    icon = { Text("🎮", fontSize = 20.sp) },
                    label = { Text("Jugar") }
                )
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    icon = { Text("🏪", fontSize = 20.sp) },
                    label = { Text("Tienda") }
                )
                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = {
                        selectedTab = 2
                        onRewards()
                    },
                    icon = { Text("🏆", fontSize = 20.sp) },
                    label = { Text("Premios") }
                )
                NavigationBarItem(
                    selected = selectedTab == 3,
                    onClick = {
                        selectedTab = 3
                        onLogout()
                    },
                    icon = { Text("👤", fontSize = 20.sp) },
                    label = { Text("Salir") }
                )
            }
        }
    ) { paddingValues ->
        if (selectedTab == 1) {
            Box(modifier = Modifier.padding(paddingValues)) {
                ShopTabContent(
                    student = student,
                    onUpdateStudent = onUpdateStudent,
                    onShowAlert = onShowAlert,
                    viewModel = viewModel
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(Color.Transparent),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
            // MEJORA 3: Header degradado (de lila-morado a rosa) con detalles en blanco
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .background(
                            Brush.linearGradient(
                                colors = listOf(AppColors.Purple, AppColors.Pink)
                            )
                        )
                        .padding(16.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .background(Color.White.copy(alpha = 0.3f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(student.avatar, fontSize = 36.sp)
                        }
                        Column {
                            Text(
                                text = "¡Hola, ${student.name}!",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "Nivel ${student.level} • ${student.stars} estrellas",
                                fontSize = 13.sp,
                                color = Color.White.copy(alpha = 0.8f)
                            )
                        }
                    }
                }
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        StarProgress(current = student.stars)
                    }
                }
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("🔥", fontSize = 28.sp)
                        Spacer(Modifier.width(12.dp))
                        Column {
                            Text("¡Racha de ${student.streak} días!", fontWeight = FontWeight.Bold, color = AppColors.Amber)
                            StreakCalendar(streak = student.streak)
                        }
                    }
                }
            }

            item {
                Text(
                    text = "Tus Desafíos",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }

            items(
                listOf(
                    Triple("📈", "Mejorando sumas", "Suma"),
                    Triple("🎯", "Restas básicas", "Resta"),
                    Triple("✖️", "Multiplicaciones rápidas", "Multiplicacion"),
                    Triple("🍕", "Jugando con Fracciones", "Fracciones"),
                    Triple("🔢", "Completa la Serie", "Series"),
                    Triple("🧠", "Examen Adaptativo", "Exam")
                )
            ) { (icon, title, op) ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .clickable { if (op == "Exam") onExam() else onPlay(op) },
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(icon, fontSize = 28.sp)
                        Spacer(Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(title, fontWeight = FontWeight.Bold)
                            Text("¡Descubre tu nivel!", color = AppColors.Gray400, fontSize = 12.sp)
                        }
                        Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, null)
                    }
                }
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Mapa de habilidades", fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(8.dp))
                        RadarChart(skills = student.skills)
                    }
                }
            }
        }
    }
}
}

// ─── GameScreen con Cronómetro ─────────────────────────────────────────────
@Composable
fun GameScreen(student: Student, operation: String, onScore: (Int) -> Unit, onBack: () -> Unit) {
    var questionText by remember { mutableStateOf("") }
    var correctAnswer by remember { mutableIntStateOf(0) }
    var userAnswer by remember { mutableStateOf("") }
    var msg by remember { mutableStateOf("¡Resuelve el desafío!") }
    var streak by remember { mutableIntStateOf(0) }
    var currentQuestionStart by remember { mutableLongStateOf(System.currentTimeMillis()) }
    var currentTime by remember { mutableLongStateOf(0L) }
    val scope = rememberCoroutineScope()

    fun generateNewQuestion() {
        val (qText, ans) = when (operation) {
            "Suma" -> {
                val a = (1..20).random()
                val b = (1..20).random()
                Pair("$a + $b", a + b)
            }
            "Resta" -> {
                val a = (10..30).random()
                val b = (1..a).random()
                Pair("$a - $b", a - b)
            }
            "Multiplicacion" -> {
                val a = (2..9).random()
                val b = (2..9).random()
                Pair("$a × $b", a * b)
            }
            "Fracciones" -> {
                val templates = listOf(
                    Pair("¿Cuál es el numerador (arriba) de 2/3?", 2),
                    Pair("¿Cuál es el denominador (abajo) de 3/4?", 4),
                    Pair("Si divides una pizza en 4 rebanadas y te comes 1, ¿cuántas quedan?", 3),
                    Pair("Si tienes 8 dulces y regalas la mitad, ¿cuántos te quedan?", 4),
                    Pair("¿Cuál es la mitad de 12?", 6)
                )
                templates.random()
            }
            "Series" -> {
                val templates = listOf(
                    Pair("2, 4, 6, 8, ?", 10),
                    Pair("5, 10, 15, 20, ?", 25),
                    Pair("10, 9, 8, 7, ?", 6),
                    Pair("3, 6, 9, 12, ?", 15),
                    Pair("1, 3, 5, 7, ?", 9),
                    Pair("12, 10, 8, 6, ?", 4)
                )
                templates.random()
            }
            else -> Pair("2 + 2", 4)
        }
        questionText = qText
        correctAnswer = ans
    }

    LaunchedEffect(Unit) {
        generateNewQuestion()
    }

    var shakeTrigger by remember { mutableStateOf(false) }
    val shakeOffset by animateFloatAsState(
        targetValue = if (shakeTrigger) 12f else 0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioHighBouncy, stiffness = Spring.StiffnessMedium),
        finishedListener = { if (it != 0f) shakeTrigger = false },
        label = "shake"
    )

    var showConfetti by remember { mutableStateOf(false) }
    val confettiColors = listOf(Color.Red, Color.Blue, Color.Green, Color.Yellow, Color.Magenta, Color.Cyan, AppColors.Pink)
    val confettiList = remember {
        List(15) {
            Triple(
                Offset((50..950).random().toFloat(), (100..1500).random().toFloat()),
                (12..28).random().toFloat(),
                confettiColors.random()
            )
        }
    }

    LaunchedEffect(currentQuestionStart, userAnswer) {
        while (true) {
            currentTime = (System.currentTimeMillis() - currentQuestionStart) / 1000
            delay(1000)
        }
    }

    val gameBgColor = when (operation) {
        "Suma" -> Color(0xFF90CAF9)
        "Resta" -> Color(0xFFFFCC80)
        "Multiplicacion" -> Color(0xFFC5E1A5)
        "Fracciones" -> Color(0xFFE1BEE7)
        "Series" -> Color(0xFFFFCDD2)
        else -> Color(0xFFB2DFDB)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(gameBgColor)
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null) }
                Text(student.name, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text("⭐ ${student.stars}", color = AppColors.Purple, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            }

            Text(
                text = "⏱️ Tiempo: ${currentTime}s",
                fontSize = 16.sp,
                color = AppColors.Purple,
                fontWeight = FontWeight.Bold
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(vertical = 40.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = questionText, fontSize = 28.sp, fontWeight = FontWeight.Black, textAlign = TextAlign.Center, modifier = Modifier.padding(horizontal = 16.dp))
                    Text(
                        text = "= ${userAnswer.ifEmpty { "?" }}",
                        fontSize = 40.sp,
                        color = if (userAnswer.isEmpty()) AppColors.Gray400 else AppColors.Purple,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Box(
                modifier = Modifier
                    .offset(x = shakeOffset.dp)
                    .padding(8.dp)
            ) {
                Text(
                    text = msg,
                    fontWeight = FontWeight.Bold,
                    color = AppColors.Gray800,
                    fontSize = 18.sp,
                    textAlign = TextAlign.Center
                )
            }

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
                                            if (userAnswer.toIntOrNull() == correctAnswer) {
                                                val pts = if (streak >= 2) 15 else 10
                                                onScore(pts)
                                                streak++
                                                msg = "¡Excelente ${student.name}! 🎉 (${currentTime}s)"
                                                generateNewQuestion()
                                                userAnswer = ""
                                                currentQuestionStart = System.currentTimeMillis()

                                                showConfetti = true
                                                scope.launch {
                                                    delay(1500)
                                                    showConfetti = false
                                                }
                                            } else {
                                                msg = "¡Casi! Intenta de nuevo 🤔"
                                                streak = 0
                                                userAnswer = ""
                                                currentQuestionStart = System.currentTimeMillis()
                                                shakeTrigger = true
                                            }
                                        }
                                        else -> if (userAnswer.length < 2) userAnswer += key
                                    }
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(64.dp),
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

        if (showConfetti) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                confettiList.forEach { (pos, size, color) ->
                    drawCircle(color = color, radius = size, center = pos)
                }
            }
        }
    }
}

// ─── RewardsScreen ─────────────────────────────────────────────────────────
@Composable
fun RewardsScreen(student: Student, onBack: () -> Unit) {
    // MEJORA 3: Borde dinámico pulsante para premios desbloqueados
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

    // MEJORA 3: Shimmer shader brush lineal para premios bloqueados
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
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null) }
            Text("🏆 Mis Premios", fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, color = AppColors.Purple)
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
            items(REWARDS) { r ->
                val unlocked = student.stars >= r.stars

                Card(
                    modifier = Modifier
                        .aspectRatio(1f)
                        .border(
                            width = if (unlocked) pulsingBorderWidth.dp else 0.dp, // MEJORA 3: Borde pulsante en desbloqueado
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
                            Text(r.emoji, fontSize = 32.sp, modifier = Modifier.alpha(if (unlocked) 1f else 0.45f))
                            Spacer(Modifier.height(4.dp))
                            Text(r.name, fontWeight = FontWeight.Bold, fontSize = 13.sp, textAlign = TextAlign.Center)
                            Text("⭐ ${r.stars}", fontSize = 11.sp, color = AppColors.Amber, fontWeight = FontWeight.Bold)
                            if (unlocked) {
                                Text("✓ Desbloqueado", fontSize = 10.sp, color = AppColors.Green, fontWeight = FontWeight.Bold)
                            }
                        }

                        // MEJORA 3: Capa de brillo Shimmer semitransparente para premios bloqueados
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

// ─── TeacherPanel ───────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeacherPanel(uid: String, onBack: () -> Unit) {
    var selected by remember { mutableStateOf<Student?>(null) }
    val viewModel = remember { StudentViewModel() }
    var alumnos by remember { mutableStateOf<List<FirebaseStudent>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var teacherProfile by remember { mutableStateOf<Map<String, Any>?>(null) }

    val db = FirebaseFirestore.getInstance()

    LaunchedEffect(uid) {
        if (uid.isNotEmpty()) {
            try {
                val doc = db.collection("usuarios").document(uid).get().await()
                teacherProfile = doc.data
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        viewModel.obtenerTodosAlumnos { lista ->
            alumnos = lista
            isLoading = false
        }
    }

    val teacherName = teacherProfile?.get("nombre") as? String ?: "Docente"
    val teacherSchool = teacherProfile?.get("colegio") as? String ?: "Institución Educativa"
    val teacherGrade = teacherProfile?.get("grado") as? String ?: ""
    val teacherSeccion = teacherProfile?.get("seccion") as? String ?: ""

    // Filter students by teacher's grade
    val alumnosFiltrados = if (teacherGrade.isNotEmpty()) {
        alumnos.filter { it.grado == teacherGrade }
    } else {
        alumnos
    }

    val alumnosLocales = alumnosFiltrados.map { firebaseStudent ->
        val parts = firebaseStudent.nombre.split(" ")
        val firstName = parts.firstOrNull() ?: ""
        val lastName = parts.drop(1).joinToString(" ")
        Student(
            id = firebaseStudent.pin,
            name = firstName,
            lastName = lastName,
            grade = firebaseStudent.grado,
            classroom = firebaseStudent.grado,
            pin = firebaseStudent.pin.toString(),
            level = firebaseStudent.nivel_actual,
            stars = firebaseStudent.estrellas,
            accuracy = firebaseStudent.precision.toInt(),
            streak = 3,
            weekData = listOf(10, 20, 15, 30, 25, 40, 35),
            skills = mapOf(
                "Sumas" to (firebaseStudent.nivel_actual * 25).coerceAtMost(100),
                "Restas" to (firebaseStudent.nivel_actual * 20).coerceAtMost(100),
                "Números" to (firebaseStudent.nivel_actual * 30).coerceAtMost(100),
                "Lógica" to (firebaseStudent.nivel_actual * 15).coerceAtMost(100),
                "Problemas" to (firebaseStudent.nivel_actual * 10).coerceAtMost(100),
                "Multiplicación" to (firebaseStudent.nivel_actual * 18).coerceAtMost(100),
                "Fracciones" to (firebaseStudent.nivel_actual * 12).coerceAtMost(100),
                "Series" to (firebaseStudent.nivel_actual * 14).coerceAtMost(100)
            ),
            avatar = firebaseStudent.avatar,
            diagnosticoRealizado = true,
            equippedTheme = firebaseStudent.equipped_theme,
            unlockedAvatars = firebaseStudent.unlocked_avatars,
            unlockedThemes = firebaseStudent.unlocked_themes
        )
    }

    if (selected != null) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(onClick = { selected = null }) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                            }
                            Spacer(Modifier.width(8.dp))
                            Column {
                                Text(selected!!.name, fontWeight = FontWeight.Bold)
                                Text(selected!!.grade, fontSize = 12.sp, color = AppColors.Gray600)
                            }
                        }
                    }
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .background(AppColors.Bg)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
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
                            modifier = Modifier.size(80.dp).background(AppColors.PurpleLight, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(selected!!.avatar, fontSize = 40.sp)
                        }
                        Spacer(Modifier.height(8.dp))
                        Text(selected!!.name + " " + selected!!.lastName, fontWeight = FontWeight.Bold, fontSize = 22.sp, color = AppColors.Purple)
                        Spacer(Modifier.height(16.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            MetricCard("${selected!!.stars}", "Estrellas", "⭐", AppColors.Amber)
                            MetricCard("${selected!!.accuracy}%", "Precisión", "🎯", AppColors.Green)
                            MetricCard("${selected!!.level}", "Nivel", "📊", AppColors.Purple)
                        }
                    }
                }
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text("🎯 Habilidades", fontWeight = FontWeight.Bold, color = AppColors.Purple)
                        Spacer(Modifier.height(12.dp))
                        selected!!.skills.forEach { (skill, value) ->
                            SkillProgressBar(skill, value)
                            Spacer(Modifier.height(8.dp))
                        }
                    }
                }
            }
        }
    } else {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Panel de Profesores", fontWeight = FontWeight.Bold) },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                        }
                    }
                )
            }
        ) { paddingValues ->
            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = AppColors.Purple)
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .background(AppColors.Bg)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Teacher Info Header Card
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                            shape = RoundedCornerShape(24.dp),
                            colors = CardDefaults.cardColors(containerColor = AppColors.PurpleLight)
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                Box(
                                    modifier = Modifier.size(60.dp).background(AppColors.Purple, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("👩‍🏫", fontSize = 32.sp)
                                }
                                Column {
                                    Text("¡Hola, $teacherName!", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = AppColors.Purple)
                                    Text("Colegio: $teacherSchool", fontSize = 13.sp, color = AppColors.Gray800)
                                    if (teacherGrade.isNotEmpty()) {
                                        Text("A cargo de: $teacherGrade - $teacherSeccion", fontSize = 13.sp, color = AppColors.Gray700, fontWeight = FontWeight.SemiBold)
                                    }
                                }
                            }
                        }
                    }

                    item {
                        Text("Lista de Estudiantes", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    }

                    if (alumnosLocales.isEmpty()) {
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                                shape = RoundedCornerShape(24.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White)
                            ) {
                                Column(
                                    modifier = Modifier.padding(24.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Text("🦎", fontSize = 48.sp)
                                    Text(
                                        "No hay alumnos registrados aún",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp,
                                        color = AppColors.Purple,
                                        textAlign = TextAlign.Center
                                    )
                                    Text(
                                        "Crea perfiles para tus alumnos o indícales que ingresen con sus PINs en la app para verlos aquí.",
                                        fontSize = 14.sp,
                                        color = AppColors.Gray600,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                    } else {
                        items(alumnosLocales) { alumno ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { selected = alumno },
                                shape = RoundedCornerShape(24.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White)
                            ) {
                                Row(
                                    modifier = Modifier.padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier.size(40.dp).background(AppColors.PurpleLight, CircleShape),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(alumno.avatar, fontSize = 24.sp)
                                        }
                                        Spacer(Modifier.width(12.dp))
                                        Column {
                                            Text(alumno.name + " " + alumno.lastName, fontWeight = FontWeight.Bold)
                                            Text("Grado: ${alumno.grade}", fontSize = 12.sp, color = AppColors.Gray500)
                                        }
                                    }
                                    Text("⭐ ${alumno.stars}", fontWeight = FontWeight.Bold, color = AppColors.Amber)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// ─── ParentsPanel ──────────────────────────────────────────────────────────
@Composable
fun ParentsPanel(onBack: () -> Unit, onCreateStudentProfile: () -> Unit, parentEmail: String, student: Student?) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.Bg)
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Panel de Padres", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = AppColors.Purple)
            IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null) }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Sesión activa:", fontWeight = FontWeight.Bold)
                Text(parentEmail, color = AppColors.Gray600)
            }
        }

        if (student != null) {
            Text("Progreso de ${student.name}:", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("⭐ Estrellas totales: ${student.stars}")
                    Text("🎯 Precisión promedio: ${student.accuracy}%")
                    Text("🔥 Racha de días: ${student.streak}")
                }
            }
        }

        Button(
            onClick = onCreateStudentProfile,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = AppColors.Purple)
        ) {
            Text("+ Crear Perfil de Estudiante", fontWeight = FontWeight.Bold)
        }
    }
}
