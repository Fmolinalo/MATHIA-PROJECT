package com.example.mathia.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import com.example.mathia.R
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mathia.AppColors

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
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = AppColors.Purple)
            }
        }

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
                Image(
                    painter = painterResource(id = R.drawable.ajolote_student),
                    contentDescription = "Estudiante",
                    modifier = Modifier.size(65.dp)
                )
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
                Image(
                    painter = painterResource(id = R.drawable.ajolote_role_teacher),
                    contentDescription = "Docente",
                    modifier = Modifier.size(65.dp)
                )
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
                Image(
                    painter = painterResource(id = R.drawable.ajolote_role_parent),
                    contentDescription = "Padre",
                    modifier = Modifier.size(65.dp)
                )
                Column {
                    Text("Padre / Madre", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = AppColors.Gray800)
                    Text("Monitorea los avances de tus hijos", fontSize = 12.sp, color = AppColors.Gray600)
                }
            }
        }
    }
}
