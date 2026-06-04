package com.example.mathia

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import android.widget.Toast
import com.google.firebase.ktx.Firebase
import com.google.firebase.firestore.ktx.firestore
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import com.example.mathia.UploadExercises
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
    val avatar: String
)

data class Reward(
    val id: Int,
    val name: String,
    val emoji: String,
    val stars: Int,
    val desc: String
)

// ─── Constants ─────────────────────────────────────────────────────────────

object AppColors {
    val Purple = Color(0xFF7C3AED)
    val PurpleLight = Color(0xFFEDE9FE)
    val Pink = Color(0xFFEC4899)
    val PinkLight = Color(0xFFFCE7F3)
    val Green = Color(0xFF10B981)
    val GreenLight = Color(0xFFD1FAE5)
    val Amber = Color(0xFFF59E0B)
    val AmberLight = Color(0xFFFEF3C7)
    val Blue = Color(0xFF3B82F6)
    val Red = Color(0xFFEF4444)
    val Bg = Color(0xFFF5F3FF)
    val White = Color(0xFFFFFFFF)
    val Gray100 = Color(0xFFF3F4F6)
    val Gray200 = Color(0xFFE5E7EB)
    val Gray400 = Color(0xFF9CA3AF)
    val Gray500 = Color(0xFF6B7280)  // ← NUEVO
    val Gray600 = Color(0xFF4B5563)
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
        avatar = "🦄"
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
        avatar = "🐉"
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
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.Bg)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            "¿Quién va a ingresar?",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = AppColors.Purple
        )
        Spacer(Modifier.height(30.dp))
        Button(
            onClick = onStudent,
            modifier = Modifier.fillMaxWidth().height(60.dp),
            colors = ButtonDefaults.buttonColors(containerColor = AppColors.Purple)
        ) {
            Text("Niño / Estudiante")
        }
        Spacer(Modifier.height(12.dp))
        Button(
            onClick = onTeacher,
            modifier = Modifier.fillMaxWidth().height(60.dp),
            colors = ButtonDefaults.buttonColors(containerColor = AppColors.Blue)
        ) {
            Text("Profesor")
        }
        Spacer(Modifier.height(12.dp))
        Button(
            onClick = onParent,
            modifier = Modifier.fillMaxWidth().height(60.dp),
            colors = ButtonDefaults.buttonColors(containerColor = AppColors.Green)
        ) {
            Text("Padre / Madre")
        }
        Spacer(Modifier.height(20.dp))
        OutlinedButton(onClick = onBack) {
            Text("Volver")
        }
    }
}

// ─── LoginScreen ──────────────────────────────────────────────────────────
@Composable
fun LoginScreen(
    onBack: () -> Unit,
    onLoginSuccess: (Student) -> Unit,
    viewModel: StudentViewModel
) {
    var pin by remember { mutableStateOf("") }
    var error by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    val context = LocalContext.current

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
                .size(120.dp)
                .background(AppColors.PinkLight, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text("🧮", fontSize = 60.sp)
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            "Iniciar Sesión",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = AppColors.Purple
        )

        Spacer(modifier = Modifier.height(32.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = pin,
                    onValueChange = { if (it.length <= 4) pin = it },
                    label = { Text("PIN de 4 dígitos") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                if (error.isNotEmpty()) {
                    Text(error, color = AppColors.Red, fontSize = 14.sp)
                }

                if (isLoading) {
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                }

                Button(
                    onClick = {
                        if (pin.length == 4) {
                            isLoading = true
                            error = ""
                            viewModel.login(pin) { alumno ->
                                isLoading = false
                                if (alumno != null) {
                                    val firebaseStudent = Student(
                                        id = alumno.pin,
                                        name = alumno.nombre.split(" ").firstOrNull() ?: alumno.nombre,
                                        lastName = alumno.nombre.split(" ").getOrNull(1) ?: "",
                                        grade = alumno.grado,
                                        classroom = "",
                                        pin = alumno.pin.toString(),
                                        level = alumno.nivel_actual,
                                        stars = alumno.estrellas,
                                        totalQuestions = 0,
                                        correctAnswers = 0,
                                        examsCompleted = 0,
                                        accuracy = alumno.precision.toInt(),
                                        streak = 0,
                                        weekData = listOf(0, 0, 0, 0, 0, 0, 0),
                                        skills = mapOf(
                                            "Sumas" to alumno.precision.toInt(),
                                            "Restas" to alumno.precision.toInt(),
                                            "Números" to alumno.precision.toInt(),
                                            "Lógica" to alumno.precision.toInt(),
                                            "Problemas" to alumno.precision.toInt()
                                        ),
                                        avatar = "🦄"
                                    )
                                    onLoginSuccess(firebaseStudent)
                                } else {
                                    error = "PIN incorrecto"
                                }
                            }
                        } else {
                            error = "El PIN debe tener 4 dígitos"
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = AppColors.Purple),
                    enabled = !isLoading
                ) {
                    Text("Entrar")
                }

                OutlinedButton(
                    onClick = onBack,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Volver")
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        TextButton(
            onClick = {
                val db = Firebase.firestore
                db.collection("usuarios").get()
                    .addOnSuccessListener { result ->
                        Toast.makeText(context, "✅ Conexión OK! ${result.documents.size} usuarios", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(context, "❌ Error: ${e.message}", Toast.LENGTH_LONG).show()
                    }
            }
        ) {
            Text("🔌 Probar conexión a Firebase")
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
            drawLine(
                color = AppColors.Gray200,
                start = center,
                end = Offset(center.x + radius * cos(angle).toFloat(), center.y + radius * sin(angle).toFloat()),
                strokeWidth = 1.dp.toPx()
            )
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
            drawRoundRect(
                color = color.copy(alpha = 0.8f),
                topLeft = Offset(i * (barW + gap), size.height - bh),
                size = Size(barW, bh),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(3.dp.toPx())
            )
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
        Spacer(modifier = Modifier.height(4.dp))

        // CORREGIDO: Barra de progreso
        LinearProgressIndicator(
            progress = pct,
            modifier = Modifier.fillMaxWidth().height(8.dp),
            color = AppColors.Purple,
            trackColor = AppColors.Gray200
        )
    }
}

// ─── StreakCalendar ────────────────────────────────────────────────────────
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

// ─── InfoBox ───────────────────────────────────────────────────────────────
@Composable
fun InfoBox(text: String, bg: Color, fg: Color) {
    Box(modifier = Modifier.fillMaxWidth().background(bg, RoundedCornerShape(10.dp)).padding(horizontal = 12.dp, vertical = 8.dp)) {
        Text(text, fontSize = 12.sp, color = fg)
    }
}

// ─── MainActivity ──────────────────────────────────────────────────────────
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

// ─── MainApp ───────────────────────────────────────────────────────────────
@Composable
fun MainApp() {
    var screen by remember { mutableStateOf("welcome") }
    var loginRole by remember { mutableStateOf("") }
    var student by remember { mutableStateOf<Student?>(null) }
    var gameOp by remember { mutableStateOf("Suma") }
    var parentEmail by remember { mutableStateOf("") }
    val viewModel = remember { StudentViewModel() }

    fun logout() {
        loginRole = ""
        student = null
        parentEmail = ""
        screen = "welcome"
    }

    Surface(modifier = Modifier.fillMaxSize(), color = AppColors.Bg) {
        when (screen) {
            "welcome" -> {
                WelcomeScreen(
                    onCreateProfile = { screen = "create" },
                    onLogin = { screen = "select_login" }
                )
            }
            "select_login" -> {
                SelectLoginTypeScreen(
                    onStudent = { loginRole = "student"; screen = "login" },
                    onTeacher = { loginRole = "docente"; screen = "adult_auth" },
                    onParent = { loginRole = "padres"; screen = "adult_auth" },
                    onBack = { screen = "welcome" }
                )
            }
            "adult_auth" -> {
                AdultAuthScreen(
                    rol = loginRole,
                    onBack = { screen = "select_login" },
                    onLoginSuccess = { role, email ->
                        parentEmail = email
                        screen = if (role == "docente") "teacher_panel" else "parents_panel"
                    }
                )
            }
            "login" -> {
                LoginScreen(
                    onBack = { screen = "select_login" },
                    onLoginSuccess = {
                        student = it
                        screen = "menu"
                    },
                    viewModel = viewModel
                )
            }
            "create" -> {
                val context = LocalContext.current
                CreateProfileScreen(
                    onCreated = { name, lastName, grade, classroom, avatar, pin ->
                        val newStudent = Student(
                            id = (MOCK_STUDENTS.size + 1).takeIf { MOCK_STUDENTS.isNotEmpty() } ?: 1,
                            name = name,
                            lastName = lastName,
                            grade = grade,
                            classroom = classroom,
                            pin = pin,
                            level = 1,
                            stars = 0,
                            totalQuestions = 0,
                            correctAnswers = 0,
                            examsCompleted = 0,
                            accuracy = 0,
                            streak = 0,
                            weekData = listOf(0, 0, 0, 0, 0, 0, 0),
                            skills = mapOf(
                                "Sumas" to 0,
                                "Restas" to 0,
                                "Números" to 0,
                                "Lógica" to 0,
                                "Problemas" to 0
                            ),
                            avatar = avatar
                        )

                        val alumnoFirebase = hashMapOf(
                            "nombre" to "$name $lastName",
                            "grado" to grade,
                            "edad" to 6,
                            "nivel_actual" to 1,
                            "precision" to 0.0,
                            "estrellas" to 0,
                            "pin" to pin.toInt()
                        )

                        viewModel.crearAlumno(alumnoFirebase, pin) { success: Boolean ->
                            if (success) {
                                MOCK_STUDENTS.add(newStudent)
                                screen = "welcome"
                            } else {
                                Toast.makeText(
                                    context,
                                    "Error al crear el perfil",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    },
                    onBack = { screen = "welcome" }
                )
            }
            "menu" -> {
                student?.let { currentStudent ->
                    MainMenuScreen(
                        student = currentStudent,
                        onPlay = { gameOp = it; screen = "game" },
                        onRewards = { screen = "rewards" },
                        onExam = { screen = "exam" },
                        onLogout = { logout() }
                    )
                }
            }
            "game" -> {
                student?.let { currentStudent ->
                    GameScreen(
                        currentStudent,
                        gameOp,
                        onScore = { puntos ->
                            viewModel.actualizarEstrellas(currentStudent.pin, puntos)
                            student = currentStudent.copy(stars = currentStudent.stars + puntos)
                        },
                        onBack = { screen = "menu" }
                    )
                }
            }
            "rewards" -> {
                student?.let { currentStudent ->
                    RewardsScreen(currentStudent, onBack = { screen = "menu" })
                }
            }
            "exam" -> {
                student?.let { currentStudent ->
                    AdaptiveExamScreen(
                        student = currentStudent,
                        onFinish = { nuevasSkills, nivel, correctas, total ->
                            val estrellasGanadas = correctas * 10
                            val nuevaPrecision = if (currentStudent.totalQuestions + total > 0) {
                                ((currentStudent.accuracy * currentStudent.totalQuestions + (correctas * 100 / total)) /
                                        (currentStudent.totalQuestions + 1)).toInt().coerceAtMost(100)
                            } else {
                                (correctas * 100 / total).toInt()
                            }

                            student = currentStudent.copy(
                                stars = currentStudent.stars + estrellasGanadas,
                                accuracy = nuevaPrecision,
                                totalQuestions = currentStudent.totalQuestions + total,
                                correctAnswers = currentStudent.correctAnswers + correctas,
                                examsCompleted = currentStudent.examsCompleted + 1,
                                skills = nuevasSkills,
                                level = when (nivel) {
                                    "Avanzado" -> 3
                                    "Intermedio" -> 2
                                    else -> 1
                                }
                            )
                            screen = "menu"
                        },
                        viewModel = viewModel
                    )
                }
            }
            "teacher_panel" -> {
                TeacherPanel(
                    onBack = { logout() }
                )
            }
            "parents_panel" -> {
                ParentsPanel(
                    onBack = { logout() },
                    onCreateStudentProfile = { screen = "create" },
                    parentEmail = parentEmail
                )
            }
        }
    }
}

// ─── WelcomeScreen ─────────────────────────────────────────────────────────
@Composable
fun WelcomeScreen(
    onCreateProfile: () -> Unit,
    onLogin: (String?) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val imageLoader = remember(context) {
        ImageLoader.Builder(context)
            .components {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    add(ImageDecoderDecoder.Factory())
                } else {
                    add(GifDecoder.Factory())
                }
            }
            .build()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Tarjeta de presentación
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(text = "🧮", fontSize = 40.sp, textAlign = TextAlign.Center)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "MathIA",
                    fontSize = 30.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = AppColors.Purple,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "¡Aprende mates jugando!",
                    fontSize = 13.sp,
                    color = AppColors.Gray600,
                    textAlign = TextAlign.Center
                )
            }
        }

        // Mascota
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
                modifier = Modifier.size(120.dp),
                contentScale = ContentScale.Fit
            )
        }

        // Mensaje de bienvenida
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "¡Bienvenidos a MathIA!",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.ExtraBold
                )
                Text(
                    "¡Hola! Soy Mateo, tu amigo ajolote.\n¡Vamos a aprender matemáticas juntos!",
                    fontSize = 14.sp,
                    color = AppColors.Gray600,
                    textAlign = TextAlign.Center
                )
            }
        }

        // Botones principales
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = onCreateProfile,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AppColors.Purple),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("+ Crear Nuevo Perfil", fontWeight = FontWeight.Bold)
            }

            OutlinedButton(
                onClick = { onLogin(null) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.5.dp, AppColors.Gray200)
            ) {
                Text("→ Iniciar Sesión", color = AppColors.Gray800)
            }

            // Botones de acceso rápido
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                TextButton(onClick = { onLogin("docente") }) {
                    Text("Panel Docente", color = AppColors.Purple)
                }
                Spacer(modifier = Modifier.width(24.dp))
                TextButton(onClick = { onLogin("padres") }) {
                    Text("Panel Padres", color = AppColors.Purple)
                }
            }
        }

        // 🔹 BOTÓN PARA SUBIR LOS 201 EJERCICIOS (SOLO DESARROLLO) 🔹
        Button(
            onClick = {
                scope.launch {
                    try {
                        Toast.makeText(context, "🚀 Subiendo ejercicios...", Toast.LENGTH_SHORT).show()
                        UploadExercises.uploadAllExercises()
                        Toast.makeText(context, "✅ ¡201 ejercicios subidos a Firebase!", Toast.LENGTH_LONG).show()
                    } catch (e: Exception) {
                        Toast.makeText(context, "❌ Error: ${e.message}", Toast.LENGTH_LONG).show()
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981))
        ) {
            Text("📤 SUBIR 201 EJERCICIOS A FIREBASE")
        }

        // 🔹 BOTÓN PARA PROBAR CONEXIÓN A FIREBASE 🔹
        Button(
            onClick = {
                val db = Firebase.firestore
                db.collection("usuarios").get()
                    .addOnSuccessListener { result ->
                        Toast.makeText(
                            context,
                            "✅ Firebase OK! ${result.documents.size} usuarios",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(
                            context,
                            "❌ Error: ${e.message}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = AppColors.Blue)
        ) {
            Text("🔌 Probar conexión a Firebase")
        }
    }
}
// ─── CreateProfileScreen ───────────────────────────────────────────────────
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CreateProfileScreen(
    onCreated: (String, String, String, String, String, String) -> Unit,
    onBack: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var grade by remember { mutableStateOf("primero") }
    var classroom by remember { mutableStateOf("1ro A") }
    var pin by remember { mutableStateOf("") }
    var avatarIdx by remember { mutableStateOf(0) }
    val avatars = listOf("🦸", "👨‍🚀", "🧙‍♀️", "🦄", "🐉", "🦊", "🐼", "🦁", "🐯", "🐨")

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp).verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Text("Crear Perfil", fontSize = 26.sp, fontWeight = FontWeight.ExtraBold, color = AppColors.Purple)

        Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color.White)) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Column {
                    Text("¿Cómo te llamas?", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    TextField(value = name, onValueChange = { name = it }, placeholder = { Text("Tu nombre...") }, modifier = Modifier.fillMaxWidth())
                }
                Column {
                    Text("Apellido", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    TextField(value = lastName, onValueChange = { lastName = it }, modifier = Modifier.fillMaxWidth())
                }
                Column {
                    Text("Grado", fontWeight = FontWeight.Bold)
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("primero", "segundo", "tercero").forEach { g ->
                            Button(
                                onClick = { grade = g },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = if (grade == g) AppColors.Pink else AppColors.Gray100)
                            ) {
                                Text(g, color = if (grade == g) Color.White else AppColors.Gray600)
                            }
                        }
                    }
                }
                Column {
                    Text("Salón", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    TextField(value = classroom, onValueChange = { classroom = it }, placeholder = { Text("Ej: 1ro A") }, modifier = Modifier.fillMaxWidth())
                }
                Column {
                    Text("Elige tu avatar", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    FlowRow(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        avatars.forEachIndexed { i, em ->
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (avatarIdx == i) AppColors.AmberLight else AppColors.Gray100)
                                    .border(2.dp, if (avatarIdx == i) AppColors.Amber else Color.Transparent, RoundedCornerShape(12.dp))
                                    .clickable { avatarIdx = i },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(em, fontSize = 24.sp)
                            }
                        }
                    }
                }
                Column {
                    Text("Crea un PIN de 4 dígitos", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    TextField(
                        value = pin,
                        onValueChange = { if (it.length <= 4) pin = it },
                        placeholder = { Text("1234") },
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                Button(
                    onClick = {
                        if (name.isNotBlank() && lastName.isNotBlank() && pin.length == 4) {
                            onCreated(name, lastName, grade, classroom, avatars[avatarIdx], pin)
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = (name.isNotBlank() && lastName.isNotBlank() && pin.length == 4)
                ) {
                    Text("Continuar →")
                }
                OutlinedButton(onClick = onBack, modifier = Modifier.fillMaxWidth()) {
                    Text("Volver")
                }
            }
        }
    }
}

// ─── AdaptiveExamScreen ────────────────────────────────────────────────────
@Composable
fun AdaptiveExamScreen(
    student: Student,
    onFinish: (Map<String, Int>, String, Int, Int) -> Unit,
    viewModel: StudentViewModel
) {
    val repository = remember { QuestionRepository() }
    var questions by remember { mutableStateOf<List<QuestionFirebase>>(emptyList()) }
    var qIdx by remember { mutableStateOf(0) }
    var results by remember { mutableStateOf(listOf<Boolean>()) }
    var done by remember { mutableStateOf(false) }
    var dificultadActual by remember { mutableStateOf(1) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        questions = repository.cargarPreguntas(student.grade.lowercase())
        isLoading = false
    }

    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    if (questions.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("No hay preguntas disponibles para este grado")
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = { onFinish(emptyMap(), "Básico", 0, 0) }) {
                    Text("Volver")
                }
            }
        }
        return
    }

    if (!done && qIdx < questions.size) {
        val preguntasFiltradas = questions.filter { it.dificultad == dificultadActual }
        val q = if (preguntasFiltradas.isNotEmpty()) preguntasFiltradas.random() else questions.random()

        Column(
            modifier = Modifier.fillMaxSize().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Examen Adaptativo", fontWeight = FontWeight.Bold, color = AppColors.Purple)
                Text("${qIdx + 1} / ${questions.size}", fontSize = 13.sp, color = AppColors.Gray400)
            }

            // CORREGIDO: LinearProgressIndicator sin lambda
            LinearProgressIndicator(
                progress = qIdx.toFloat() / questions.size,
                modifier = Modifier.fillMaxWidth(),
                color = AppColors.Purple
            )

            Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                Column(modifier = Modifier.padding(vertical = 40.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Resuelve:", fontSize = 13.sp, color = AppColors.Gray400)
                    Text(q.enunciado, fontSize = 28.sp, fontWeight = FontWeight.Black, textAlign = TextAlign.Center)
                }
            }

            val options = listOf(q.opcionA, q.opcionB, q.opcionC).filter { it.isNotEmpty() }.shuffled()
            Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                options.forEach { opt ->
                    Button(
                        onClick = {
                            val correcta = opt == q.correcta
                            results = results + correcta
                            if (correcta) {
                                dificultadActual = (dificultadActual + 1).coerceAtMost(3)
                            } else {
                                dificultadActual = (dificultadActual - 1).coerceAtLeast(1)
                            }
                            qIdx++
                            if (qIdx >= questions.size) {
                                done = true
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(60.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = AppColors.Gray800),
                        border = BorderStroke(2.dp, AppColors.Gray200)
                    ) {
                        Text(opt, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    } else if (done) {
        val correctas = results.count { it }
        val total = questions.size
        val porcentaje = (correctas.toFloat() / total) * 100
        val nivel = when {
            porcentaje >= 80 -> "Avanzado"
            porcentaje >= 50 -> "Intermedio"
            else -> "Básico"
        }

        val estrellasGanadas = correctas * 10
        val nuevasSkills = student.skills.toMutableMap()
        val skillPrincipal = when {
            questions.any { it.enunciado.contains("+") || it.enunciado.contains("suma") } -> "Sumas"
            questions.any { it.enunciado.contains("-") || it.enunciado.contains("resta") } -> "Restas"
            else -> "Problemas"
        }
        val nuevaPrecision = ((student.skills[skillPrincipal] ?: 0) + porcentaje.toInt()) / 2
        nuevasSkills[skillPrincipal] = nuevaPrecision.coerceAtMost(100)

        Column(
            modifier = Modifier.fillMaxSize().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("¡Examen Completado!", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = AppColors.Purple)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Puntaje: $correctas/$total", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Text("Porcentaje: ${porcentaje.toInt()}%", fontSize = 16.sp)
                    Text("⭐ Estrellas ganadas: $estrellasGanadas", fontSize = 16.sp, color = AppColors.Amber)
                    Text("Nivel alcanzado: $nivel", fontSize = 16.sp, color = AppColors.Green)
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        onClick = {
                            viewModel.actualizarEstrellas(student.pin, estrellasGanadas)
                            viewModel.actualizarPrecision(student.pin, porcentaje.toDouble())
                            viewModel.actualizarNivel(student.pin, when(nivel) {
                                "Avanzado" -> 3
                                "Intermedio" -> 2
                                else -> 1
                            })
                            onFinish(nuevasSkills, nivel, correctas, total)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = AppColors.Purple)
                    ) {
                        Text("Finalizar")
                    }
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
    onLogout: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier.size(48.dp).background(AppColors.AmberLight, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(student.avatar, fontSize = 28.sp)
                    }
                    Column {
                        Text("¡Hola, ${student.name}!", fontWeight = FontWeight.Bold, fontSize = 17.sp)
                        Text("Nivel ${student.level} • ${student.stars} estrellas", fontSize = 12.sp, color = AppColors.Gray400)
                    }
                }
                Row {
                    Button(onClick = onRewards, colors = ButtonDefaults.buttonColors(containerColor = AppColors.Amber)) {
                        Text("Premios")
                    }
                    Spacer(Modifier.width(8.dp))
                    Button(onClick = onLogout, colors = ButtonDefaults.buttonColors(containerColor = AppColors.Red)) {
                        Text("Salir")
                    }
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
                    Text("🤖 Análisis IA", fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(12.dp))
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        InfoBox("Excelente en sumas", AppColors.GreenLight, AppColors.Green)
                        InfoBox("Practicar restas", AppColors.AmberLight, AppColors.Amber)
                    }
                }
            }
        }
        item {
            Text("Tus Desafíos", fontWeight = FontWeight.Bold, fontSize = 18.sp)
        }
        items(listOf(
            Triple("📈", "Mejorando sumas", "Suma"),
            Triple("🎯", "Restas básicas", "Resta"),
            Triple("🧠", "Examen Adaptativo", "Exam")
        )) { (icon, title, op) ->
            Card(modifier = Modifier.fillMaxWidth().clickable {
                if (op == "Exam") onExam() else onPlay(op)
            }, colors = CardDefaults.cardColors(containerColor = Color.White)) {
                Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text(icon, fontSize = 28.sp)
                    Spacer(Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(title, fontWeight = FontWeight.Bold)
                        Text("¡Descubre tu nivel!", color = AppColors.Gray400, fontSize = 12.sp)
                    }
                    Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null)
                }
            }
        }
        item {
            Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Mapa de habilidades", fontWeight = FontWeight.Bold)
                    RadarChart(skills = student.skills)
                }
            }
        }
    }
}

// ─── GameScreen ────────────────────────────────────────────────────────────
@Composable
fun GameScreen(student: Student, operation: String, onScore: (Int) -> Unit, onBack: () -> Unit) {
    var n1 by remember { mutableStateOf((1..15).random()) }
    var n2 by remember { mutableStateOf((1..n1).random()) }
    var userAnswer by remember { mutableStateOf("") }
    var msg by remember { mutableStateOf("¡Resuelve el desafío!") }
    var streak by remember { mutableStateOf(0) }

    val correct = if (operation == "Suma") n1 + n2 else n1 - n2

    Column(
        modifier = Modifier.fillMaxSize().background(Color(0xFFF0F4C3)).padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
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

// ─── RewardsScreen ─────────────────────────────────────────────────────────
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

// ─── TeacherPanel ──────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeacherPanel(
    onBack: () -> Unit
) {
    var selected by remember { mutableStateOf<Student?>(null) }
    val viewModel = remember { StudentViewModel() }
    var alumnos by remember { mutableStateOf<List<FirebaseStudent>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        viewModel.obtenerTodosAlumnos { lista ->
            alumnos = lista
            isLoading = false
        }
    }

    if (selected != null) {
        // ========== VISTA DETALLE DEL ESTUDIANTE SELECCIONADO ==========
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(onClick = { selected = null }) {
                                Icon(
                                    Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Back"
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    selected!!.name,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp,
                                    color = AppColors.Purple
                                )
                                Text(
                                    "${selected!!.grade}",
                                    fontSize = 12.sp,
                                    color = AppColors.Gray600
                                )
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.White
                    )
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
                // Tarjeta de estadísticas principales
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .background(AppColors.PurpleLight, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(selected!!.avatar, fontSize = 40.sp)
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            selected!!.name,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = AppColors.Purple
                        )
                        Text(
                            "${selected!!.grade}",
                            fontSize = 14.sp,
                            color = AppColors.Gray600
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            MetricCard(
                                value = "${selected!!.stars}",
                                label = "Estrellas",
                                icon = "⭐",
                                color = AppColors.Amber
                            )
                            MetricCard(
                                value = "${selected!!.accuracy}%",
                                label = "Precisión",
                                icon = "🎯",
                                color = AppColors.Green
                            )
                            MetricCard(
                                value = "${selected!!.level}",
                                label = "Nivel",
                                icon = "📊",
                                color = AppColors.Purple
                            )
                        }
                    }
                }

                // Estadísticas detalladas
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            "📈 Estadísticas de Progreso",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = AppColors.Purple
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            StatItem(
                                value = "${selected!!.totalQuestions}",
                                label = "Preguntas",
                                color = AppColors.Blue
                            )
                            StatItem(
                                value = "${selected!!.correctAnswers}",
                                label = "Correctas",
                                color = AppColors.Green
                            )
                            StatItem(
                                value = "${selected!!.examsCompleted}",
                                label = "Exámenes",
                                color = AppColors.Amber
                            )
                            StatItem(
                                value = "${selected!!.streak}",
                                label = "Racha",
                                color = AppColors.Red
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text("Progreso general", fontSize = 12.sp, color = AppColors.Gray600)
                        LinearProgressIndicator(
                            progress = selected!!.accuracy / 100f,
                            modifier = Modifier.fillMaxWidth().height(10.dp),
                            color = AppColors.Purple,
                            trackColor = AppColors.Gray200
                        )
                        Text(
                            "${selected!!.accuracy}% completado",
                            fontSize = 11.sp,
                            color = AppColors.Gray400,
                            modifier = Modifier.align(Alignment.End)
                        )
                    }
                }

                // Actividad semanal
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            "📅 Actividad Semanal",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = AppColors.Purple
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        MiniBarChart(data = selected!!.weekData, color = AppColors.Purple)
                    }
                }

                // Mapa de habilidades
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            "🎯 Mapa de Habilidades",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = AppColors.Purple
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        RadarChart(skills = selected!!.skills, size = 180.dp)
                        Spacer(modifier = Modifier.height(16.dp))

                        selected!!.skills.forEach { (skill, value) ->
                            SkillProgressBar(skill = skill, value = value)
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }

                // Recomendaciones IA
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = AppColors.PurpleLight),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            "🤖 Recomendaciones IA",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = AppColors.Purple
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        val peorSkill = selected!!.skills.minByOrNull { it.value }
                        if (peorSkill != null && peorSkill.value < 70) {
                            RecommendationCard(
                                icon = "⚠️",
                                title = "Área de mejora",
                                message = "${selected!!.name} necesita practicar ${peorSkill.key}",
                                detail = "${100 - peorSkill.value}% de mejora necesaria",
                                color = AppColors.Amber
                            )
                        }

                        when {
                            selected!!.accuracy < 60 -> {
                                RecommendationCard(
                                    icon = "📖",
                                    title = "Recomendación",
                                    message = "Repasar conceptos básicos",
                                    detail = "Ejercicios de nivel 1",
                                    color = AppColors.Blue
                                )
                            }
                            selected!!.accuracy < 80 -> {
                                RecommendationCard(
                                    icon = "📈",
                                    title = "Recomendación",
                                    message = "Practicar ejercicios intermedios",
                                    detail = "Subir al siguiente nivel pronto",
                                    color = AppColors.Amber
                                )
                            }
                            else -> {
                                RecommendationCard(
                                    icon = "🎉",
                                    title = "¡Excelente progreso!",
                                    message = "${selected!!.name} está listo para avanzar",
                                    detail = "Felicitar por su dedicación",
                                    color = AppColors.Green
                                )
                            }
                        }

                        if (selected!!.streak > 0) {
                            Spacer(modifier = Modifier.height(8.dp))
                            RecommendationCard(
                                icon = "🔥",
                                title = "Racha actual",
                                message = "${selected!!.streak} días seguidos practicando",
                                detail = "¡Sigue así!",
                                color = AppColors.Red
                            )
                        }
                    }
                }
            }
        }
    } else {
        // ========== VISTA LISTA DE ESTUDIANTES ==========
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(onClick = onBack) {
                                Icon(
                                    Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Back",
                                    tint = AppColors.Purple
                                )
                            }
                            Text(
                                "Panel Docente",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = AppColors.Purple
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.White
                    )
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(AppColors.Bg)
            ) {
                // Resumen General
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = AppColors.Purple),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            "📊 Resumen General",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            SummaryCard(
                                value = if (!isLoading) "${alumnos.size}" else "...",
                                label = "Estudiantes",
                                icon = "👨‍🎓"
                            )
                            SummaryCard(
                                value = if (!isLoading && alumnos.isNotEmpty())
                                    "${alumnos.sumOf { it.estrellas } / alumnos.size}" else "...",
                                label = "Promedio ⭐",
                                icon = "⭐"
                            )
                            SummaryCard(
                                value = if (!isLoading && alumnos.isNotEmpty())
                                    "${alumnos.sumOf { it.precision.toInt() } / alumnos.size}%" else "...",
                                label = "Promedio %",
                                icon = "🎯"
                            )
                        }
                    }
                }

                // Título de la lista
                Text(
                    "👥 Mis Estudiantes",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = AppColors.Purple,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )

                // Lista de estudiantes
                if (isLoading) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = AppColors.Purple)
                    }
                } else if (alumnos.isEmpty()) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column(
                            modifier = Modifier.padding(40.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("📭", fontSize = 48.sp)
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                "No hay estudiantes registrados",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium,
                                color = AppColors.Gray600
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                "Agrega estudiantes desde la app",
                                fontSize = 12.sp,
                                color = AppColors.Gray400
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        items(alumnos) { fbStudent ->
                            StudentCard(
                                student = fbStudent,
                                onClick = {
                                    selected = Student(
                                        id = fbStudent.pin,
                                        name = fbStudent.nombre.split(" ").firstOrNull() ?: fbStudent.nombre,
                                        lastName = fbStudent.nombre.split(" ").getOrNull(1) ?: "",
                                        grade = fbStudent.grado,
                                        classroom = "",
                                        pin = fbStudent.pin.toString(),
                                        level = fbStudent.nivel_actual,
                                        stars = fbStudent.estrellas,
                                        totalQuestions = 0,
                                        correctAnswers = 0,
                                        examsCompleted = 0,
                                        accuracy = fbStudent.precision.toInt(),
                                        streak = 0,
                                        weekData = listOf(0,0,0,0,0,0,0),
                                        skills = mapOf(
                                            "Sumas" to fbStudent.precision.toInt(),
                                            "Restas" to fbStudent.precision.toInt(),
                                            "Números" to fbStudent.precision.toInt(),
                                            "Lógica" to fbStudent.precision.toInt(),
                                            "Problemas" to fbStudent.precision.toInt()
                                        ),
                                        avatar = when (fbStudent.grado) {
                                            "primero" -> "🐣"
                                            "segundo" -> "🦊"
                                            else -> "🦁"
                                        }
                                    )
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MetricCard(value: String, label: String, icon: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(icon, fontSize = 24.sp)
        Text(
            value,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Text(
            label,
            fontSize = 12.sp,
            color = AppColors.Gray600
        )
    }
}

@Composable
fun StatItem(value: String, label: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            value,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Text(
            label,
            fontSize = 11.sp,
            color = AppColors.Gray500
        )
    }
}

@Composable
fun SkillProgressBar(skill: String, value: Int) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(skill, fontSize = 13.sp, fontWeight = FontWeight.Medium)
            Text("$value%", fontSize = 13.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = value / 100f,
            modifier = Modifier.fillMaxWidth().height(8.dp),
            color = when {
                value >= 80 -> AppColors.Green
                value >= 50 -> AppColors.Amber
                else -> AppColors.Red
            },
            trackColor = AppColors.Gray200
        )
    }
}

@Composable
fun RecommendationCard(icon: String, title: String, message: String, detail: String, color: Color) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(icon, fontSize = 28.sp)
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(title, fontSize = 12.sp, color = color, fontWeight = FontWeight.Bold)
                Text(message, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                Text(detail, fontSize = 11.sp, color = AppColors.Gray500)
            }
        }
    }
}

@Composable
fun SummaryCard(value: String, label: String, icon: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(icon, fontSize = 28.sp)
        Text(
            value,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Text(
            label,
            fontSize = 12.sp,
            color = Color.White.copy(alpha = 0.8f)
        )
    }
}

@Composable
fun StudentCard(student: FirebaseStudent, onClick: () -> Unit) {
    val progreso = (student.estrellas / 210f * 100).toInt()
    val avatar = when (student.grado) {
        "primero" -> "🐣"
        "segundo" -> "🦊"
        else -> "🦁"
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .background(AppColors.PurpleLight, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(avatar, fontSize = 32.sp)
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    student.nombre,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = AppColors.Purple
                )
                Text(
                    "${student.grado} • Nivel ${student.nivel_actual}",
                    fontSize = 12.sp,
                    color = AppColors.Gray600
                )
                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("⭐ ${student.estrellas}", fontSize = 12.sp, color = AppColors.Amber)
                    Text("🎯 ${student.precision.toInt()}%", fontSize = 12.sp, color = AppColors.Green)
                }

                Spacer(modifier = Modifier.height(8.dp))

                LinearProgressIndicator(
                    progress = progreso / 100f,
                    modifier = Modifier.fillMaxWidth().height(6.dp),
                    color = AppColors.Purple,
                    trackColor = AppColors.Gray200
                )
            }

            Icon(
                Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = AppColors.Gray400
            )
        }
    }
}



// ─── ParentsPanel ──────────────────────────────────────────────────────────
@Composable
fun ParentsPanel(
    onBack: () -> Unit,
    onCreateStudentProfile: () -> Unit,
    parentEmail: String
) {
    var selectedStudent by remember { mutableStateOf<Student?>(null) }
    val viewModel = remember { StudentViewModel() }
    var estudiantes by remember { mutableStateOf<List<FirebaseStudent>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        viewModel.obtenerTodosAlumnos { lista ->
            println("📊 TOTAL ALUMNOS: ${lista.size}")
            lista.forEach { alumno ->
                println("📊 Alumno: ${alumno.nombre}, Grado: ${alumno.grado}, Estrellas: ${alumno.estrellas}")
            }
            estudiantes = lista  // ← CAMBIADO: era 'alumnos'
            isLoading = false
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(20.dp).verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Text("← Volver", fontSize = 16.sp, color = AppColors.Purple)
            }
            Button(
                onClick = onCreateStudentProfile,
                colors = ButtonDefaults.buttonColors(containerColor = AppColors.Green)
            ) {
                Text("+ Crear Perfil Estudiante")
            }
        }

        Text("Panel de Padres", fontSize = 20.sp, fontWeight = FontWeight.ExtraBold)

        if (isLoading) {
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (estudiantes.isEmpty()) {
            Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = AppColors.AmberLight)) {
                Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("⚠️ No tienes estudiantes vinculados", fontWeight = FontWeight.Bold)
                    Text("Registra un estudiante o contacta al docente")
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = onCreateStudentProfile) {
                        Text("+ Crear Perfil Estudiante")
                    }
                }
            }
        } else {
            if (estudiantes.size > 1) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    estudiantes.forEach { fbStudent ->
                        Button(
                            onClick = {
                                selectedStudent = Student(
                                    id = fbStudent.pin,
                                    name = fbStudent.nombre.split(" ").firstOrNull() ?: fbStudent.nombre,
                                    lastName = fbStudent.nombre.split(" ").getOrNull(1) ?: "",
                                    grade = fbStudent.grado,
                                    classroom = "",
                                    pin = fbStudent.pin.toString(),
                                    level = fbStudent.nivel_actual,
                                    stars = fbStudent.estrellas,
                                    totalQuestions = 0,
                                    correctAnswers = 0,
                                    examsCompleted = 0,
                                    accuracy = fbStudent.precision.toInt(),
                                    streak = 0,
                                    weekData = listOf(0,0,0,0,0,0,0),
                                    skills = mapOf(
                                        "Sumas" to fbStudent.precision.toInt(),
                                        "Restas" to fbStudent.precision.toInt(),
                                        "Números" to fbStudent.precision.toInt(),
                                        "Lógica" to fbStudent.precision.toInt(),
                                        "Problemas" to fbStudent.precision.toInt()
                                    ),
                                    avatar = "🧑‍🎓"
                                )
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (selectedStudent?.pin?.toInt() == fbStudent.pin) AppColors.Purple else AppColors.Gray100
                            )
                        ) {
                            Text(fbStudent.nombre, color = if (selectedStudent?.pin?.toInt() == fbStudent.pin) Color.White else AppColors.Gray600)
                        }
                    }
                }
            }

            selectedStudent?.let { currentStudent ->
                Box(modifier = Modifier.fillMaxWidth().background(Brush.linearGradient(listOf(AppColors.Purple, AppColors.Pink)), RoundedCornerShape(16.dp)).padding(20.dp)) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            Box(modifier = Modifier.size(50.dp).background(Color.White.copy(alpha = 0.2f), CircleShape), contentAlignment = Alignment.Center) {
                                Text(currentStudent.avatar, fontSize = 28.sp)
                            }
                            Column {
                                Text(currentStudent.name, fontWeight = FontWeight.Bold, color = Color.White, fontSize = 20.sp)
                                Text("${currentStudent.grade} • Nivel ${currentStudent.level}", color = Color.White.copy(alpha = 0.85f), fontSize = 14.sp)
                            }
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("${currentStudent.stars}", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                Text("estrellas", fontSize = 12.sp, color = Color.White.copy(alpha = 0.8f))
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("${currentStudent.accuracy}%", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                Text("precisión", fontSize = 12.sp, color = Color.White.copy(alpha = 0.8f))
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("${currentStudent.examsCompleted}", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                Text("exámenes", fontSize = 12.sp, color = Color.White.copy(alpha = 0.8f))
                            }
                        }
                    }
                }

                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("📊 Reporte de Progreso", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Spacer(modifier = Modifier.height(12.dp))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("${currentStudent.totalQuestions}", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = AppColors.Purple)
                                Text("Preguntas", fontSize = 11.sp)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("${currentStudent.correctAnswers}", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = AppColors.Green)
                                Text("Correctas", fontSize = 11.sp)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("${currentStudent.streak}", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = AppColors.Amber)
                                Text("Racha días", fontSize = 11.sp)
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text("🎯 Habilidades por área", fontWeight = FontWeight.Bold)
                        currentStudent.skills.forEach { (skill, value) ->
                            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                                Text(skill, modifier = Modifier.width(80.dp), fontSize = 12.sp)
                                LinearProgressIndicator(
                                    progress = value / 100f,
                                    modifier = Modifier.weight(1f).height(8.dp),
                                    color = when {
                                        value >= 80 -> AppColors.Green
                                        value >= 50 -> AppColors.Amber
                                        else -> AppColors.Red
                                    }
                                )
                                Text(" $value%", modifier = Modifier.width(40.dp), fontSize = 12.sp)
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        val skillToImprove = currentStudent.skills.minByOrNull { it.value }
                        if (skillToImprove != null && skillToImprove.value < 70) {
                            InfoBox(
                                "⚠️ ${currentStudent.name} necesita practicar ${skillToImprove.key}. ${100 - skillToImprove.value}% de mejora necesaria",
                                AppColors.AmberLight,
                                AppColors.Amber
                            )
                        }
                        if (currentStudent.streak > 0) {
                            InfoBox(
                                "🔥 ¡${currentStudent.name} lleva ${currentStudent.streak} días de racha!",
                                AppColors.GreenLight,
                                AppColors.Green
                            )
                        }
                    }
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Card(modifier = Modifier.weight(1f)) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("Actividad Semanal", fontSize = 13.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                            MiniBarChart(data = currentStudent.weekData, color = AppColors.Purple)
                        }
                    }
                    Card(modifier = Modifier.weight(1f)) {
                        Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Habilidades", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            RadarChart(skills = currentStudent.skills, size = 100.dp)
                        }
                    }
                }
            }
        }
    }
}