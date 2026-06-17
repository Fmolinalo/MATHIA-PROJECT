package com.example.mathia.ui.screens

import android.os.Build
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import com.example.mathia.R
import com.example.mathia.AppColors
import com.example.mathia.StudentViewModel
import com.example.mathia.model.Student
import com.example.mathia.model.RespuestaHistorial
import com.example.mathia.model.ReporteSesion
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun GameScreen(
    student: Student,
    operation: String, // "Suma", "Resta", "Multiplicacion", "Fracciones", "Series"
    onScore: (Int, Int, String, Long) -> Unit, // (stars, xp, topic, responseTimeSeconds)
    onBack: () -> Unit,
    viewModel: StudentViewModel
) {
    val context = LocalContext.current
    val sharedPref = remember { context.getSharedPreferences("mathia_prefs", Context.MODE_PRIVATE) }
    val currentTutor = remember { sharedPref.getString("student_tutor", "axolita") ?: "axolita" }

    val tutorImageRes = when (currentTutor) {
        "prof_axol" -> R.drawable.ajolote_teacher_male
        "prof_axolina" -> R.drawable.ajolote_teacher_female
        else -> R.drawable.ajolote_student
    }

    val tutorName = when (currentTutor) {
        "prof_axol" -> "El Profesor Axol"
        "prof_axolina" -> "La Profesora Axolina"
        else -> "Axolita"
    }

    val tutorGreeting = when (currentTutor) {
        "prof_axol" -> "¡Excelente trabajo! Has completado el análisis con éxito."
        "prof_axolina" -> "¡Qué gran esfuerzo, mi cielo! Estoy muy orgullosa de ti."
        else -> "¡Increíble! ¡Eres súper veloz resolviendo matemáticas!"
    }

    var questionText by remember { mutableStateOf("") }
    var correctAnswer by remember { mutableIntStateOf(0) }
    var userAnswer by remember { mutableStateOf("") }
    var msg by remember { mutableStateOf("¡Resuelve el desafío!") }
    var currentStreak by remember { mutableIntStateOf(0) }
    var currentQuestionStart by remember { mutableLongStateOf(System.currentTimeMillis()) }
    var currentTime by remember { mutableLongStateOf(0L) }
    
    // Session state tracking
    var questionsAnsweredInSession by remember { mutableIntStateOf(0) }
    var sessionCorrectAnswers by remember { mutableIntStateOf(0) }
    var sessionIncorrectAnswers by remember { mutableIntStateOf(0) }
    var sessionTimeSpent by remember { mutableLongStateOf(0L) }
    var attemptsCount by remember { mutableIntStateOf(1) }
    
    var totalStarsGained by remember { mutableIntStateOf(0) }
    var totalXPGained by remember { mutableIntStateOf(0) }
    var showSummary by remember { mutableStateOf(false) }
    var isSavingReport by remember { mutableStateOf(false) }

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
        attemptsCount = 1
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
        List(20) {
            Triple(
                Offset((50..950).random().toFloat(), (100..1500).random().toFloat()),
                (12..28).random().toFloat(),
                confettiColors.random()
            )
        }
    }

    LaunchedEffect(currentQuestionStart, userAnswer, showSummary) {
        if (!showSummary) {
            while (true) {
                currentTime = (System.currentTimeMillis() - currentQuestionStart) / 1000
                delay(1000)
            }
        }
    }

    val gameBgColor = when (operation) {
        "Suma" -> Color(0xFFE3F2FD) // Lila pastel/Azul suave
        "Resta" -> Color(0xFFFFF3E0) // Naranja suave
        "Multiplicacion" -> Color(0xFFF1F8E9) // Verde suave
        "Fracciones" -> Color(0xFFF3E5F5) // Lila morado suave
        "Series" -> Color(0xFFFFEBEE) // Rosa suave
        else -> Color(0xFFE0F2F1)
    }

    val headerColor = when (operation) {
        "Suma" -> Color(0xFF2196F3)
        "Resta" -> Color(0xFFFF9800)
        "Multiplicacion" -> Color(0xFF8BC34A)
        "Fracciones" -> Color(0xFF9C27B0)
        "Series" -> Color(0xFFE91E63)
        else -> AppColors.Purple
    }

    if (showSummary) {
        // SUMMARY RECAP FOR GAMEPLAY
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(AppColors.Bg)
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.EmojiEvents,
                            contentDescription = null,
                            tint = AppColors.MathiaGold,
                            modifier = Modifier.size(28.dp)
                        )
                        Text(
                            text = "¡Sesión Completada!",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = AppColors.Purple
                        )
                    }
                    
                    Box(
                        modifier = Modifier
                            .size(110.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color.White)
                            .border(2.dp, AppColors.MathiaGold, RoundedCornerShape(16.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = tutorImageRes),
                            contentDescription = tutorName,
                            modifier = Modifier.size(95.dp)
                        )
                    }
                    
                    Text(
                        text = "$tutorName dice: \"$tutorGreeting\"",
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold,
                        color = AppColors.MathiaNavy,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                    
                    Divider(color = AppColors.PurpleLight, thickness = 1.dp)
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Estrellas", fontSize = 12.sp, color = AppColors.Gray500)
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = null,
                                    tint = AppColors.Amber,
                                    modifier = Modifier.size(20.dp)
                                )
                                Text("$totalStarsGained", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = AppColors.Amber)
                            }
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("XP", fontSize = 12.sp, color = AppColors.Gray500)
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ElectricBolt,
                                    contentDescription = null,
                                    tint = AppColors.Purple,
                                    modifier = Modifier.size(20.dp)
                                )
                                Text("$totalXPGained XP", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = AppColors.Purple)
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Precisión", fontSize = 12.sp, color = AppColors.Gray500)
                            val prec = if (questionsAnsweredInSession > 0) (sessionCorrectAnswers * 100) / questionsAnsweredInSession else 0
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Flag,
                                    contentDescription = null,
                                    tint = AppColors.Green,
                                    modifier = Modifier.size(20.dp)
                                )
                                Text("$prec%", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = AppColors.Green)
                            }
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("T. Promedio", fontSize = 12.sp, color = AppColors.Gray500)
                            val avgTime = if (questionsAnsweredInSession > 0) sessionTimeSpent / questionsAnsweredInSession else 0L
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Timer,
                                    contentDescription = null,
                                    tint = AppColors.Blue,
                                    modifier = Modifier.size(20.dp)
                                )
                                Text("${avgTime}s", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = AppColors.Blue)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    if (isSavingReport) {
                        CircularProgressIndicator(color = AppColors.Purple)
                    } else {
                        Button(
                            onClick = {
                                isSavingReport = true
                                scope.launch {
                                    val now = Date()
                                    val fFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                                    val hFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
                                    
                                    val skillsDom = student.skills.filter { it.value >= 75 }.keys.toList()
                                    val skillsRef = student.skills.filter { it.value < 75 }.keys.toList()
                                    
                                    val reporte = ReporteSesion(
                                        id = "",
                                        estudianteId = student.pin,
                                        estudianteNombre = "${student.name} ${student.lastName}",
                                        grado = student.grade,
                                        seccion = student.seccion,
                                        docenteId = "", // assigned dynamically in repository
                                        padreEmail = student.padreEmail, // we map parent email
                                        tipo = "Práctica",
                                        preguntasCount = questionsAnsweredInSession,
                                        correctas = sessionCorrectAnswers,
                                        incorrectas = sessionIncorrectAnswers,
                                        tiempoPromedio = if (questionsAnsweredInSession > 0) sessionTimeSpent.toDouble() / questionsAnsweredInSession else 0.0,
                                        nivel = student.level,
                                        competenciasDominadas = skillsDom,
                                        competenciasReforzar = skillsRef,
                                        recomendaciones = student.recomendaciones,
                                        estrellasObtenidas = totalStarsGained,
                                        fecha = fFormat.format(now),
                                        timestamp = now.time
                                    )
                                    
                                    viewModel.guardarReporteSesion(reporte) {
                                        // Also trigger alerts
                                        viewModel.notificarPadre(student.pin, student.name, if (questionsAnsweredInSession > 0) (sessionCorrectAnswers * 100) / questionsAnsweredInSession else 0)
                                        viewModel.notificarDocente(student.pin, student.name, if (questionsAnsweredInSession > 0) (sessionCorrectAnswers * 100) / questionsAnsweredInSession else 0)
                                        
                                        isSavingReport = false
                                        onBack()
                                    }
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            shape = RoundedCornerShape(50.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = AppColors.Purple)
                        ) {
                            Text("Guardar y Volver", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }
                    }
                }
            }
        }
    } else {
        // ACTIVE PLAY SCREEN
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
                    IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = headerColor) }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(student.name, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = AppColors.Gray800)
                        Text("Pregunta: ${questionsAnsweredInSession + 1}/10", fontSize = 12.sp, color = AppColors.Gray500, fontWeight = FontWeight.Bold)
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = AppColors.Purple,
                            modifier = Modifier.size(18.dp)
                        )
                        Text("${student.stars + totalStarsGained}", color = AppColors.Purple, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }

                // Progress Bar
                LinearProgressIndicator(
                    progress = { questionsAnsweredInSession / 10f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = headerColor,
                    trackColor = AppColors.Gray200
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Timer,
                        contentDescription = null,
                        tint = headerColor,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "Tiempo: ${currentTime}s",
                        fontSize = 16.sp,
                        color = headerColor,
                        fontWeight = FontWeight.Bold
                    )
                }

                if (currentStreak > 0) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = AppColors.Amber),
                        shape = RoundedCornerShape(50.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Whatshot,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "Racha: $currentStreak",
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontSize = 12.sp
                            )
                        }
                    }
                }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(vertical = 30.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = questionText, fontSize = 26.sp, fontWeight = FontWeight.Black, textAlign = TextAlign.Center, modifier = Modifier.padding(horizontal = 16.dp))
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
                        fontSize = 16.sp,
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
                                                val ansVal = userAnswer.toIntOrNull()
                                                val timeSpent = (System.currentTimeMillis() - currentQuestionStart) / 1000
                                                sessionTimeSpent += timeSpent
                                                
                                                val isCorrect = ansVal == correctAnswer
                                                
                                                // Create history record
                                                val fFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                                                val hFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
                                                val now = Date()
                                                
                                                val diff = when (operation) {
                                                    "Suma", "Resta" -> "Fácil"
                                                    "Multiplicacion" -> "Medio"
                                                    else -> "Difícil"
                                                }
                                                
                                                val starsGained = if (isCorrect) (if (currentStreak >= 1) 15 else 10) else 0
                                                val xpGained = if (isCorrect) (if (currentStreak >= 1) 15 else 10) else 0

                                                val log = RespuestaHistorial(
                                                    id = "",
                                                    estudianteId = student.pin,
                                                    estudianteNombre = "${student.name} ${student.lastName}",
                                                    grado = student.grade,
                                                    seccion = student.seccion,
                                                    docenteAsignado = student.docenteAsignado,
                                                    padreAsociado = student.padreEmail, // maps to email
                                                    fecha = fFormat.format(now),
                                                    hora = hFormat.format(now),
                                                    timestamp = now.time,
                                                    operacion = operation,
                                                    pregunta = questionText,
                                                    respuestaCorrecta = correctAnswer.toString(),
                                                    respuestaElegida = userAnswer,
                                                    tiempoRespuesta = timeSpent,
                                                    dificultad = diff,
                                                    correcta = isCorrect,
                                                    intentos = attemptsCount,
                                                    estrellasObtenidas = starsGained,
                                                    experienciaGanada = xpGained,
                                                    dispositivo = "${Build.MANUFACTURER} ${Build.MODEL}"
                                                )
                                                
                                                viewModel.registrarRespuestaHistorial(log)

                                                if (isCorrect) {
                                                    totalStarsGained += starsGained
                                                    totalXPGained += xpGained
                                                    
                                                    // Add score to VM for stats updates
                                                    onScore(starsGained, xpGained, operation, timeSpent)
                                                    
                                                    sessionCorrectAnswers++
                                                    questionsAnsweredInSession++
                                                    currentStreak++
                                                    
                                                    msg = "¡Excelente! +$starsGained estrellas | +$xpGained XP"
                                                    
                                                    if (questionsAnsweredInSession >= 10) {
                                                        showSummary = true
                                                    } else {
                                                        generateNewQuestion()
                                                        userAnswer = ""
                                                        currentQuestionStart = System.currentTimeMillis()
                                                    }

                                                    showConfetti = true
                                                    scope.launch {
                                                        delay(1200)
                                                        showConfetti = false
                                                    }
                                                } else {
                                                    sessionIncorrectAnswers++
                                                    msg = "¡Casi! Intenta de nuevo"
                                                    currentStreak = 0
                                                    userAnswer = ""
                                                    shakeTrigger = true
                                                    attemptsCount++
                                                }
                                            }
                                            else -> if (userAnswer.length < 3) userAnswer += key
                                        }
                                    },
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(60.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = when (key) {
                                            "OK" -> AppColors.Green
                                            "C" -> AppColors.Red
                                            else -> Color.White
                                        },
                                        contentColor = if (key == "OK" || key == "C") Color.White else AppColors.Gray800
                                    ),
                                    shape = RoundedCornerShape(14.dp),
                                    border = if (key != "OK" && key != "C") BorderStroke(1.5.dp, AppColors.Gray200) else null
                                ) {
                                    Text(key, fontSize = 20.sp, fontWeight = FontWeight.Bold)
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
}
