package com.example.mathia.ui.screens

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.mathia.AppColors
import com.example.mathia.R

data class TutorOption(
    val key: String,
    val name: String,
    val subtitle: String,
    val description: String,
    val quote: String,
    val imageRes: Int
)

@Composable
fun TutorSelectionDialog(
    currentTutor: String,
    onTutorSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val tutors = remember {
        listOf(
            TutorOption(
                key = "axolita",
                name = "Axolita",
                subtitle = "Tu compañera de juegos",
                description = "¡Aprende mates divirtiéndote! Te dará ánimos súper alegres y celebrará cada logro contigo.",
                quote = "\"¡Súper rápido! ¡Eres genial!\"",
                imageRes = R.drawable.ajolote_student
            ),
            TutorOption(
                key = "prof_axol",
                name = "Profesor Axol",
                subtitle = "Tu tutor de lógica",
                description = "Enfocado en desafíos y superación. Te ayudará a pensar lógicamente en cada problema.",
                quote = "\"Excelente lógica. Sigamos con el reto.\"",
                imageRes = R.drawable.ajolote_teacher_male
            ),
            TutorOption(
                key = "prof_axolina",
                name = "Profesora Axolina",
                subtitle = "Tu guía paciente",
                description = "Con ella avanzarás a tu propio ritmo. Te dará consejos cariñosos y te guiará paso a paso.",
                quote = "\"¡Vas muy bien, mi cielo! Con calma se aprende mejor.\"",
                imageRes = R.drawable.ajolote_teacher_female
            )
        )
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
                .border(3.dp, AppColors.MathiaNavy, RoundedCornerShape(28.dp)),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Elige tu Tutor Guía",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Black,
                    color = AppColors.MathiaNavy,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "Tu tutor te acompañará en todas las pantallas y te dará consejos de estudio.",
                    fontSize = 13.sp,
                    color = AppColors.Gray600,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )

                tutors.forEach { tutor ->
                    val isSelected = currentTutor == tutor.key
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) Color(0xFFFFFDF2) else Color(0xFFF9F9F9)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onTutorSelected(tutor.key)
                            }
                            .border(
                                width = if (isSelected) 3.dp else 1.dp,
                                color = if (isSelected) AppColors.MathiaGold else AppColors.Gray200,
                                shape = RoundedCornerShape(20.dp)
                            ),
                        elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 2.dp else 0.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Image(
                                painter = painterResource(id = tutor.imageRes),
                                contentDescription = tutor.name,
                                modifier = Modifier
                                    .size(75.dp)
                                    .clip(RoundedCornerShape(12.dp))
                            )
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = tutor.name,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = AppColors.MathiaNavy
                                )
                                Text(
                                    text = tutor.subtitle,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 11.sp,
                                    color = if (isSelected) AppColors.MathiaTeal else AppColors.Gray600
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = tutor.description,
                                    fontSize = 11.sp,
                                    color = AppColors.Gray700,
                                    lineHeight = 14.sp
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = tutor.quote,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AppColors.MathiaRed,
                                    lineHeight = 12.sp
                                )
                            }
                        }
                    }
                }

                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = RoundedCornerShape(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AppColors.MathiaNavy)
                ) {
                    Text(
                        "Listo",
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}
