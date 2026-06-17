package com.example.mathia

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.compose.setContent
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.ImageLoader
import coil.compose.rememberAsyncImagePainter
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import com.example.mathia.model.*
import com.example.mathia.ui.screens.*
import com.example.mathia.ui.theme.MathkidsTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

// ─── App Colors ───────────────────────────────────────────────────────────
object AppColors {
    // New palette colors
    val MathiaRed = Color(0xFFD9303E)
    val MathiaBurgundy = Color(0xFF732231)
    val MathiaNavy = Color(0xFF231640)
    val MathiaTeal = Color(0xFF11594C)
    val MathiaGold = Color(0xFFD9C771)

    // Mapping existing color names to avoid compilation errors and re-theme dynamically
    val Purple = MathiaNavy
    val PurpleLight = Color(0xFFF5ECE1) // WarmBeige
    val Pink = MathiaRed
    val PinkLight = Color(0xFFFFF0F5)
    val Green = MathiaTeal
    val GreenLight = Color(0xFFE6F9EC)
    val Amber = MathiaGold
    val AmberLight = Color(0xFFFFFCE6)
    val Blue = MathiaNavy
    val Red = MathiaRed
    val Bg = Color(0xFFFDFBF7)
    val White = Color(0xFFFFFFFF)
    
    // Grays mapped to warmer tones
    val Gray100 = Color(0xFFF9F6F0)
    val Gray200 = Color(0xFFEAE3D5)
    val Gray400 = Color(0xFFBCAFA0)
    val Gray500 = Color(0xFF8F7E6D)
    val Gray600 = Color(0xFF6E5F50)
    val Gray700 = Color(0xFF4E3F31)
    val Gray800 = MathiaNavy
}

// ─── MainActivity ──────────────────────────────────────────────────────────
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        FirebaseMessaging.getInstance()
            .token
            .addOnSuccessListener { token ->
                println("FCM Token: $token")
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
    val context = LocalContext.current
    val sharedPref = remember { context.getSharedPreferences("mathia_prefs", Context.MODE_PRIVATE) }
    
    var screen by remember { mutableStateOf("splash") } // "splash", "welcome", etc.

    // Ensure demo mode is OFF by default — real Firestore data
    LaunchedEffect(Unit) {
        StudentViewModel.isDemoMode = false
    }
    var loginRole by remember { mutableStateOf("") }
    var student by remember { mutableStateOf<Student?>(null) }
    var gameOp by remember { mutableStateOf("Suma") }
    var parentEmail by remember { mutableStateOf("") }
    var loggedInUid by remember { mutableStateOf("") }
    var activeAlert by remember { mutableStateOf<MathiaAlert?>(null) }
    val viewModel = remember { StudentViewModel() }

    val scope = rememberCoroutineScope()

    // Auto-login check
    LaunchedEffect(Unit) {
        val savedPin = sharedPref.getString("student_pin", null)
        val currentUser = FirebaseAuth.getInstance().currentUser

        if (savedPin != null) {
            // Auto-login student
            viewModel.login(savedPin) { alumno ->
                if (alumno != null) {
                    val parts = alumno.nombre.split(" ")
                    val firstName = parts.firstOrNull() ?: alumno.nombre
                    val lastName = parts.drop(1).joinToString(" ")
                    student = Student(
                        id = alumno.pin,
                        name = firstName,
                        lastName = lastName,
                        grade = alumno.grado,
                        classroom = alumno.grado,
                        pin = alumno.pin.toString(),
                        level = alumno.nivel_actual,
                        stars = alumno.estrellas,
                        xp = alumno.xp,
                        totalQuestions = alumno.total_preguntas,
                        correctAnswers = alumno.correctas,
                        examsCompleted = alumno.total_preguntas / 10 + 1,
                        accuracy = alumno.precision.toInt(),
                        streak = alumno.streak,
                        weekData = alumno.weekData,
                        monthData = alumno.monthData,
                        skills = alumno.skills.ifEmpty {
                            mapOf(
                                "Sumas" to alumno.precision.toInt(),
                                "Restas" to alumno.precision.toInt(),
                                "Multiplicación" to alumno.precision.toInt(),
                                "Fracciones" to alumno.precision.toInt(),
                                "Series" to alumno.precision.toInt()
                            )
                        },
                        incorrectasPorTema = alumno.incorrectas_por_tema,
                        asistencia = alumno.asistencia,
                        recomendaciones = alumno.recomendaciones,
                        avatar = alumno.avatar,
                        equippedTheme = alumno.equipped_theme,
                        unlockedAvatars = alumno.unlocked_avatars,
                        unlockedThemes = alumno.unlocked_themes,
                        diagnosticoRealizado = alumno.diagnostico_realizado,
                        tiempoTotal = alumno.tiempo_total,
                        tiempoPromedio = alumno.tiempo_promedio,
                        dailyMissionProgress = alumno.daily_mission_progress,
                        weeklyMissionProgress = alumno.weekly_mission_progress,
                        edad = alumno.edad,
                        colegio = alumno.colegio,
                        seccion = alumno.seccion,
                        docenteAsignado = alumno.docente_asignado,
                        fechaCreacion = alumno.fecha_creacion,
                        padreEmail = alumno.padre_email
                    )
                    screen = "menu"
                } else {
                    // PIN no longer valid or student deleted
                    sharedPref.edit().remove("student_pin").apply()
                    screen = "welcome"
                }
            }
        } else if (currentUser != null) {
            // Auto-login teacher or parent
            val uid = currentUser.uid
            val db = FirebaseFirestore.getInstance()
            try {
                val doc = db.collection("usuarios").document(uid).get().await()
                if (doc.exists()) {
                    val role = doc.getString("rol") ?: ""
                    val isComplete = doc.getBoolean("perfil_completo") ?: false
                    parentEmail = currentUser.email ?: ""
                    loggedInUid = uid

                    if (role == "docente") {
                        screen = if (isComplete) "teacher_panel" else "complete_teacher_profile"
                    } else if (role == "padre") {
                        screen = if (isComplete) "parents_panel" else "complete_parent_profile"
                    } else {
                        FirebaseAuth.getInstance().signOut()
                        screen = "welcome"
                    }
                } else {
                    FirebaseAuth.getInstance().signOut()
                    screen = "welcome"
                }
            } catch (e: Exception) {
                FirebaseAuth.getInstance().signOut()
                screen = "welcome"
            }
        } else {
            delay(1200) // Minimum display time for Mateo splash
            screen = "welcome"
        }
    }

    fun logout() {
        FirebaseAuth.getInstance().signOut()
        sharedPref.edit().remove("student_pin").apply()
        loginRole = ""
        student = null
        parentEmail = ""
        loggedInUid = ""
        screen = "welcome"
    }

    val currentBgColor = remember(student?.equippedTheme, screen) {
        if (screen == "splash") {
            AppColors.Bg
        } else {
            when (student?.equippedTheme) {
                "Verde Menta" -> Color(0xFFE8F5E9)
                "Azul Espacial" -> Color(0xFFE3F2FD)
                "Amarillo Sol" -> Color(0xFFFFFDE7)
                "Rosa Algodón" -> Color(0xFFFCE4EC)
                else -> AppColors.Bg
            }
        }
    }

    val examLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) {
        student?.let { s ->
            viewModel.login(s.pin) { alumno ->
                if (alumno != null) {
                    val parts = alumno.nombre.split(" ")
                    val firstName = parts.firstOrNull() ?: alumno.nombre
                    val lastName = parts.drop(1).joinToString(" ")
                    student = s.copy(
                        stars = alumno.estrellas,
                        xp = alumno.xp,
                        level = alumno.nivel_actual,
                        totalQuestions = alumno.total_preguntas,
                        correctAnswers = alumno.correctas,
                        examsCompleted = alumno.total_preguntas / 10 + 1,
                        accuracy = alumno.precision.toInt(),
                        skills = alumno.skills,
                        streak = alumno.streak
                    )
                }
            }
        }
        screen = "menu"
    }

    Surface(modifier = Modifier.fillMaxSize(), color = currentBgColor) {
        Box(modifier = Modifier.fillMaxSize()) {
            AnimatedContent(
                targetState = screen,
                transitionSpec = {
                    when {
                        targetState == "splash" || targetState == "welcome" ->
                            fadeIn(tween(400)) togetherWith fadeOut(tween(300))
                        targetState == "menu" || initialState == "menu" ->
                            fadeIn(tween(350)) + slideInHorizontally(tween(350)) { it / 6 } togetherWith
                            fadeOut(tween(250)) + slideOutHorizontally(tween(250)) { -it / 6 }
                        targetState in listOf("game", "exam", "rewards", "leaderboard", "student_profile") ->
                            slideInHorizontally(tween(380, easing = FastOutSlowInEasing)) { it } + fadeIn(tween(300)) togetherWith
                            slideOutHorizontally(tween(280)) { -it / 3 } + fadeOut(tween(250))
                        initialState in listOf("game", "exam", "rewards", "leaderboard", "student_profile") ->
                            slideInHorizontally(tween(380, easing = FastOutSlowInEasing)) { -it } + fadeIn(tween(300)) togetherWith
                            slideOutHorizontally(tween(280)) { it / 3 } + fadeOut(tween(250))
                        else ->
                            fadeIn(tween(350)) togetherWith fadeOut(tween(280))
                    }
                },
                label = "main_nav"
            ) { currentScreen ->
            when (currentScreen) {
                "splash" -> LoadingScreen()
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
                        // Save PIN for session persistence
                        sharedPref.edit().putString("student_pin", loggedInStudent.pin).apply()
                        
                        activeAlert = MathiaAlert(
                            title = "¡Bienvenido!",
                            message = "¡Hola, ${loggedInStudent.name}! Mateo te da la bienvenida. ¿Listo para entrenar tu mente matemática hoy?",
                            type = AlertType.MOTIVATIONAL,
                            buttonText = "¡Empezar!"
                        )
                        screen = if (loggedInStudent.diagnosticoRealizado) "menu" else "diagnostico"
                    },
                    viewModel = viewModel
                )
                "create" -> {
                    val context = LocalContext.current
                    CreateProfileScreen(onCreated = { name, lastName, grade, seccion, edad, colegio, avatar, pin ->
                        val alumnoFirebase = hashMapOf(
                            "nombre" to "$name $lastName",
                            "grado" to grade,
                            "seccion" to seccion,
                            "edad" to edad,
                            "colegio" to colegio,
                            "docente_asignado" to "Sin asignar",
                            "fecha_creacion" to System.currentTimeMillis(),
                            "nivel_actual" to 1,
                            "precision" to 0.0,
                            "estrellas" to 0,
                            "xp" to 0,
                            "pin" to pin.toInt(),
                            "padre_email" to parentEmail,
                            "avatar" to avatar,
                            "equipped_theme" to "Lila Clásico",
                            "unlocked_avatars" to listOf("default", avatar),
                            "unlocked_themes" to listOf("Lila Clásico"),
                            "streak" to 0,
                            "total_preguntas" to 0,
                            "correctas" to 0,
                            "incorrectas" to 0,
                            "tiempo_total" to 0L,
                            "tiempo_promedio" to 0.0,
                            "diagnostico_realizado" to false,
                            "skills" to mapOf(
                                "Sumas" to 0,
                                "Restas" to 0,
                                "Multiplicación" to 0,
                                "Fracciones" to 0,
                                "Series" to 0
                            ),
                            "incorrectas_por_tema" to emptyMap<String, Int>(),
                            "recomendaciones" to listOf("¡Realiza el Examen Adaptativo para descubrir tu nivel actual de matemáticas!")
                        )
                        viewModel.crearAlumno(alumnoFirebase, pin) { success ->
                            if (success) {
                                Toast.makeText(context, "¡Perfil creado con éxito! Ingresa con tu PIN", Toast.LENGTH_SHORT).show()
                                screen = "welcome"
                            }
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
                        onLeaderboard = { screen = "leaderboard" },
                        onLogout = { logout() },
                        onUpdateStudent = { s -> student = s },
                        onShowAlert = { alert -> activeAlert = alert },
                        onProfile = { screen = "student_profile" },
                        viewModel = viewModel
                    )
                }
                "game" -> student?.let { s ->
                    GameScreen(
                        student = s,
                        operation = gameOp,
                        onScore = { stars, xp, topic, speedSec ->
                            // Update Firestore & retrieve new stats
                            viewModel.registrarActividadJuego(s.pin, stars, xp, topic, correcta = true, respuestaTiempo = speedSec)
                            
                            val nextStars = s.stars + stars
                            val nextXP = s.xp + xp
                            val nextLevel = (nextXP / 100) + 1
                            val leveledUp = nextLevel > s.level

                            student = s.copy(stars = nextStars, xp = nextXP, level = nextLevel)

                            activeAlert = if (leveledUp) {
                                MathiaAlert(
                                    title = "¡Súper Nivel UP!",
                                    message = "¡Guau! Alcanzaste el Nivel $nextLevel. Mateo te ha regalado un Cofre Sorpresa. ¡Ábrelo en la tienda!",
                                    type = AlertType.CHALLENGE,
                                    buttonText = "¡Genial!"
                                )
                            } else {
                                MathiaAlert(
                                    title = "¡Excelente!",
                                    message = "¡Ganaste $stars estrellas y $xp XP! Mateo está brincando de alegría. ¡Sigue así!",
                                    type = AlertType.SUCCESS,
                                    buttonText = "¡De acuerdo!"
                                )
                            }
                        },
                        onBack = { screen = "menu" },
                        viewModel = viewModel
                    )
                }
                "student_profile" -> student?.let {
                    StudentProfileScreen(
                        student = it,
                        onBack = { screen = "menu" }
                    )
                }
                "rewards" -> student?.let { RewardsScreen(it, onBack = { screen = "menu" }) }
                "leaderboard" -> student?.let { LeaderboardScreen(it, onBack = { screen = "menu" }, viewModel = viewModel) }
                "exam" -> student?.let {
                    val context = LocalContext.current
                    val intent = remember {
                        android.content.Intent(context, ExamenActivity::class.java).apply {
                            putExtra("GRADO", it.grade)
                            putExtra("PIN", it.pin)
                        }
                    }
                    LaunchedEffect(Unit) {
                        examLauncher.launch(intent)
                    }
                    Box(
                        modifier = Modifier.fillMaxSize().background(AppColors.Bg),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = AppColors.Purple)
                    }
                }
                "teacher_panel" -> TeacherPanel(
                    uid = loggedInUid,
                    onBack = { logout() },
                    onCreateStudentProfile = { screen = "create" },
                    viewModel = viewModel
                )
                "parents_panel" -> ParentsPanel(onBack = { logout() }, onCreateStudentProfile = { screen = "create" }, parentEmail = parentEmail, viewModel = viewModel)
                "diagnostico" -> student?.let { DiagnosticoScreen(it, viewModel, onFinish = { screen = "menu" }) }
            }

            } // end AnimatedContent

            // Alert Dialog Overlay
            activeAlert?.let { alert ->
                MathiaAlertDialog(
                    alert = alert,
                    onDismiss = { activeAlert = null }
                )
            }
        }
    }
}

// ─── Loading / Splash Screen ───────────────────────────────────────────────
@Composable
fun LoadingScreen() {
    val context = LocalContext.current
    val imageLoader = remember(context) {
        ImageLoader.Builder(context).components {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) add(ImageDecoderDecoder.Factory())
            else add(GifDecoder.Factory())
        }.build()
    }

    val infiniteTransition = rememberInfiniteTransition(label = "mateo_jump")
    val translationY by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -30f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "jump"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.Bg),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(150.dp)
                .graphicsLayer { this.translationY = translationY }
                .clip(CircleShape)
                .background(AppColors.PurpleLight),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = rememberAsyncImagePainter(
                    model = R.drawable.ajolote,
                    imageLoader = imageLoader
                ),
                contentDescription = "Cargando MathIA...",
                modifier = Modifier.size(110.dp)
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "Cargando MathIA...",
            fontSize = 20.sp,
            fontWeight = FontWeight.ExtraBold,
            color = AppColors.Purple
        )
        Spacer(modifier = Modifier.height(16.dp))
        CircularProgressIndicator(color = AppColors.Purple)
    }
}

