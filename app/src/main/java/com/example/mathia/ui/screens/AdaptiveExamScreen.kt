package com.example.mathia.ui.screens

import android.os.Build
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.EmojiEvents
import com.example.mathia.AppColors
import com.example.mathia.QuestionFirebase
import com.example.mathia.QuestionRepository
import com.example.mathia.StudentViewModel
import com.example.mathia.model.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun AdaptiveExamScreen(
    student: Student,
    onFinish: (Map<String, Int>, String, Int, Int) -> Unit, // (nuevasSkills, nivelName, correctCount, totalCount)
    viewModel: StudentViewModel
) {
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
        
        // Adaptive query logic: takes 4 basic questions (diff 1), 3 intermediate (diff 2), and 3 advanced (diff 3)
        val diff1 = todas.filter { it.dificultad == 1 }.shuffled().take(4)
        val diff2 = todas.filter { it.dificultad == 2 }.shuffled().take(3)
        val diff3 = todas.filter { it.dificultad == 3 }.shuffled().take(3)
        
        questions = (diff1 + diff2 + diff3).shuffled()
        isLoading = false
    }

    val tiempoTotal = remember(respuestas) { respuestas.sumOf { it.tiempo } }
    val tiempoPromedio = remember(respuestas) { if (respuestas.isNotEmpty()) respuestas.map { it.tiempo }.average() else 0.0 }

    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize().background(AppColors.Bg), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = AppColors.Purple)
        }
        return
    }

    if (questions.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize().background(AppColors.Bg), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text("No hay preguntas disponibles", fontWeight = FontWeight.Bold)
                Button(onClick = { onFinish(emptyMap(), "Básico", 0, 0) }, colors = ButtonDefaults.buttonColors(containerColor = AppColors.Purple)) {
                    Text("Volver")
                }
            }
        }
        return
    }

    if (!done && qIdx < questions.size) {
        val q = questions[qIdx]
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(AppColors.Bg)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Psychology,
                        contentDescription = null,
                        tint = AppColors.Purple,
                        modifier = Modifier.size(20.dp)
                    )
                    Text("Examen Adaptativo", fontWeight = FontWeight.Bold, color = AppColors.Purple, fontSize = 18.sp)
                }
                Text("${qIdx + 1}/${questions.size}", fontSize = 14.sp, color = AppColors.Gray600, fontWeight = FontWeight.Bold)
            }
            LinearProgressIndicator(
                progress = { qIdx.toFloat() / questions.size },
                modifier = Modifier.fillMaxWidth().height(8.dp),
                color = AppColors.Purple,
                trackColor = AppColors.Gray200
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(vertical = 40.dp, horizontal = 16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Resuelve:", fontSize = 14.sp, color = AppColors.Gray400)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(q.enunciado, fontSize = 28.sp, fontWeight = FontWeight.Black, textAlign = TextAlign.Center)
                }
            }

            val options = listOf(q.opcionA, q.opcionB, q.opcionC).filter { it.isNotEmpty() }.shuffled()
            Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                options.forEach { opt ->
                    Button(
                        onClick = {
                            val correcta = opt == q.correcta
                            val timeSpent = (System.currentTimeMillis() - questionStartTime) / 1000
                            
                            // Log exam response to history
                            val fFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                            val hFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
                            val now = Date()
                            
                            val starsGained = if (correcta) 10 else 0
                            val xpGained = if (correcta) 10 else 0
                            
                            val log = RespuestaHistorial(
                                id = "",
                                estudianteId = student.pin,
                                estudianteNombre = "${student.name} ${student.lastName}",
                                grado = student.grade,
                                seccion = student.seccion,
                                docenteAsignado = student.docenteAsignado,
                                padreAsociado = student.padreEmail,
                                fecha = fFormat.format(now),
                                hora = hFormat.format(now),
                                timestamp = now.time,
                                operacion = "Examen Adaptativo",
                                pregunta = q.enunciado,
                                respuestaCorrecta = q.correcta,
                                respuestaElegida = opt,
                                tiempoRespuesta = timeSpent,
                                dificultad = when (q.dificultad) { 1 -> "Fácil"; 2 -> "Medio"; else -> "Difícil" },
                                correcta = correcta,
                                intentos = 1,
                                estrellasObtenidas = starsGained,
                                experienciaGanada = xpGained,
                                dispositivo = "${Build.MANUFACTURER} ${Build.MODEL}"
                            )
                            viewModel.registrarRespuestaHistorial(log)

                            respuestas = respuestas.toMutableList().apply {
                                add(ExamResponse(q.enunciado, correcta, timeSpent, q.dificultad))
                            }
                            results = results + correcta
                            questionStartTime = System.currentTimeMillis()
                            qIdx++
                            if (qIdx >= questions.size) done = true
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(60.dp),
                        shape = RoundedCornerShape(50.dp),
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
        val nivel = when { porcentaje >= 80 -> "Avanzado"; porcentaje >= 50 -> "Intermedio"; else -> "Básico" }
        
        val nuevasSkills = student.skills.toMutableMap()
        val skillPrincipal = when {
            questions.any { it.enunciado.contains("+") } -> "Sumas"
            questions.any { it.enunciado.contains("-") } -> "Restas"
            questions.any { it.enunciado.contains("×") } -> "Multiplicación"
            questions.any { it.enunciado.contains("/") || it.enunciado.contains("numerador") } -> "Fracciones"
            else -> "Series"
        }
        nuevasSkills[skillPrincipal] = ((student.skills[skillPrincipal] ?: 0) + porcentaje.toInt()) / 2

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(AppColors.Bg)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.EmojiEvents,
                            contentDescription = null,
                            tint = AppColors.Amber,
                            modifier = Modifier.size(28.dp)
                        )
                        Text("¡Examen Completado!", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = AppColors.Purple)
                    }
                    
                    Divider(color = AppColors.PurpleLight, thickness = 1.dp)

                    Text("Resultados del Desafío:", fontWeight = FontWeight.Medium, fontSize = 16.sp)
                    Text("Puntaje: $correctas / $total correctas", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = AppColors.Green)
                    Text("Precisión: ${porcentaje.toInt()}%", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = AppColors.Purple)
                    Text("Nivel Determinado: $nivel", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = AppColors.Pink)
                    
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Tiempo Total", fontSize = 11.sp, color = AppColors.Gray500)
                            Text("${tiempoTotal}s", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Tiempo Promedio", fontSize = 11.sp, color = AppColors.Gray500)
                            Text("${tiempoPromedio.toInt()}s", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            viewModel.guardarDiagnostico(
                                student.pin,
                                correctas,
                                total - correctas,
                                tiempoTotal,
                                tiempoPromedio
                            )
                            viewModel.enviarNotificacionExamen(student.pin, correctas, porcentaje.toInt())
                            viewModel.notificarPadre(student.pin, student.name, porcentaje.toInt())
                            viewModel.notificarDocente(student.pin, student.name, porcentaje.toInt())
                            
                            viewModel.actualizarEstrellas(student.pin, correctas * 10)
                            viewModel.actualizarPrecision(student.pin, porcentaje.toDouble())
                            
                            // Save exam session report
                            val fFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                            val now = Date()
                            val skillsDom = student.skills.filter { it.value >= 75 }.keys.toList()
                            val skillsRef = student.skills.filter { it.value < 75 }.keys.toList()
                            
                            val report = ReporteSesion(
                                id = "",
                                estudianteId = student.pin,
                                estudianteNombre = "${student.name} ${student.lastName}",
                                grado = student.grade,
                                seccion = student.seccion,
                                docenteId = "", // assigned in repository
                                padreEmail = student.padreEmail,
                                tipo = "Examen",
                                preguntasCount = total,
                                correctas = correctas,
                                incorrectas = total - correctas,
                                tiempoPromedio = tiempoPromedio,
                                nivel = student.level,
                                competenciasDominadas = skillsDom,
                                competenciasReforzar = skillsRef,
                                recomendaciones = student.recomendaciones,
                                estrellasObtenidas = correctas * 10,
                                fecha = fFormat.format(now),
                                timestamp = now.time
                            )
                            viewModel.guardarReporteSesion(report)

                            // OnFinish callback updates UI student object
                            onFinish(nuevasSkills, nivel, correctas, total)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AppColors.Purple)
                    ) {
                        Text("Finalizar y Guardar", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }
            }
        }
    }
}
