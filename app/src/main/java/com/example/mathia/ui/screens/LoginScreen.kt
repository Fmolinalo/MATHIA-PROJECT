package com.example.mathia.ui.screens

import androidx.compose.foundation.border
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import com.example.mathia.AppColors
import com.example.mathia.StudentViewModel
import com.example.mathia.model.Student
import com.example.mathia.ui.theme.Purple2Dinamico

@Composable
fun LoginScreen(
    onBack: () -> Unit,
    onLoginSuccess: (Student) -> Unit,
    viewModel: StudentViewModel
) {
    var pin by remember { mutableStateOf("") }
    var error by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
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
            Icon(
                imageVector = Icons.Default.Calculate,
                contentDescription = null,
                tint = AppColors.Pink,
                modifier = Modifier.size(50.dp)
            )
        }
        Spacer(Modifier.height(16.dp))
        Text(
            text = "Ingresa tu PIN",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Purple2Dinamico
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
                                                            val parts = alumno.nombre.split(" ")
                                                            val firstName = parts.firstOrNull() ?: alumno.nombre
                                                            val lastName = parts.drop(1).joinToString(" ")
                                                            onLoginSuccess(
                                                                Student(
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
                                                                    examsCompleted = alumno.total_preguntas, // mapped or calculate
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
                                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
                                ) {
                                    Text(key, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(16.dp))
        OutlinedButton(
            onClick = onBack,
            modifier = Modifier.width(180.dp),
            shape = RoundedCornerShape(50.dp),
            border = androidx.compose.foundation.BorderStroke(2.dp, Purple2Dinamico),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = Purple2Dinamico)
        ) {
            Text("Volver", fontWeight = FontWeight.Bold)
        }
    }
}
