package com.example.mathia

import android.os.Build
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.ImageLoader
import coil.compose.rememberAsyncImagePainter
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompleteParentProfileScreen(
    uid: String,
    email: String,
    onCompleteSuccess: () -> Unit,
    onLogout: () -> Unit,
    viewModel: StudentViewModel
) {
    val context = LocalContext.current
    var parentName by remember { mutableStateOf(email.substringBefore("@").replaceFirstChar { it.uppercase() }) }
    
    // Child registration states
    var childName by remember { mutableStateOf("") }
    var childSchool by remember { mutableStateOf("") }
    var childGradeSelected by remember { mutableStateOf("Selecciona el Grado del Niño") }
    var childPin by remember { mutableStateOf("") }
    
    var isLoading by remember { mutableStateOf(false) }
    var isGradeExpanded by remember { mutableStateOf(false) }

    val grades = listOf("1ro de Primaria", "2do de Primaria", "3ro de Primaria", "4to de Primaria", "5to de Primaria", "6to de Primaria")

    val imageLoader = remember(context) {
        ImageLoader.Builder(context).components {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) add(ImageDecoderDecoder.Factory())
            else add(GifDecoder.Factory())
        }.build()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.Bg)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Mateo welcome speech bubble & GIF
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Image(
                    painter = rememberAsyncImagePainter(
                        model = R.drawable.ajolote,
                        imageLoader = imageLoader
                    ),
                    contentDescription = "Mateo el Ajolote",
                    modifier = Modifier
                        .size(90.dp)
                        .clip(RoundedCornerShape(16.dp))
                )

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .background(Color.White, RoundedCornerShape(16.dp))
                        .border(2.dp, AppColors.PurpleLight, RoundedCornerShape(16.dp))
                        .padding(12.dp)
                ) {
                    Text(
                        text = "¡Hola! Ayúdame a registrar el perfil de tu hijo/a para vincularlo a tu cuenta de padres.",
                        fontSize = 13.sp,
                        color = AppColors.Purple,
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Start
                    )
                }
            }

            // Registration Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Registro de Padres 👨‍👩‍👧",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppColors.Purple,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )

                    Divider(color = AppColors.PurpleLight, thickness = 1.dp)

                    // Nombre Completo del Padre
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("Nombre Completo (Padre)", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = AppColors.Gray600)
                        OutlinedTextField(
                            value = parentName,
                            onValueChange = { parentName = it },
                            placeholder = { Text("Ej. Juan Pérez") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = AppColors.Purple,
                                unfocusedBorderColor = AppColors.Gray400
                            ),
                            singleLine = true
                        )
                    }

                    Text(
                        text = "Datos de tu Hijo/a 👶",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppColors.Purple
                    )

                    // Nombre del Niño
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("Nombre del Niño", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = AppColors.Gray600)
                        OutlinedTextField(
                            value = childName,
                            onValueChange = { childName = it },
                            placeholder = { Text("Ej. Anita Pérez") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = AppColors.Purple,
                                unfocusedBorderColor = AppColors.Gray400
                            ),
                            singleLine = true
                        )
                    }

                    // Colegio del Niño
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("Nombre del Colegio", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = AppColors.Gray600)
                        OutlinedTextField(
                            value = childSchool,
                            onValueChange = { childSchool = it },
                            placeholder = { Text("Ej. Colegio San Agustín") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = AppColors.Purple,
                                unfocusedBorderColor = AppColors.Gray400
                            ),
                            singleLine = true
                        )
                    }

                    // Grado Dropdown
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("Grado del Niño", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = AppColors.Gray600)
                        Box(modifier = Modifier.fillMaxWidth()) {
                            OutlinedTextField(
                                value = childGradeSelected,
                                onValueChange = {},
                                readOnly = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { isGradeExpanded = !isGradeExpanded },
                                enabled = false,
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    disabledBorderColor = if (childGradeSelected.contains("Selecciona")) AppColors.Gray400 else AppColors.Purple,
                                    disabledTextColor = if (childGradeSelected.contains("Selecciona")) AppColors.Gray500 else Color.Black
                                ),
                                trailingIcon = {
                                    Icon(
                                        imageVector = if (isGradeExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.ArrowDropDown,
                                        contentDescription = null,
                                        tint = AppColors.Purple
                                    )
                                }
                            )
                            Box(
                                modifier = Modifier
                                    .matchParentSize()
                                    .clickable { isGradeExpanded = !isGradeExpanded }
                            )

                            DropdownMenu(
                                expanded = isGradeExpanded,
                                onDismissRequest = { isGradeExpanded = false },
                                modifier = Modifier
                                    .fillMaxWidth(0.8f)
                                    .background(Color.White)
                            ) {
                                grades.forEach { g ->
                                    DropdownMenuItem(
                                        text = { Text(g, color = Color.Black) },
                                        onClick = {
                                            childGradeSelected = g
                                            isGradeExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    // PIN del Niño
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("Asigna un PIN de 4 dígitos para tu hijo", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = AppColors.Gray600)
                        OutlinedTextField(
                            value = childPin,
                            onValueChange = { if (it.length <= 4) childPin = it },
                            placeholder = { Text("Ej. 1234") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = AppColors.Purple,
                                unfocusedBorderColor = AppColors.Gray400
                            ),
                            singleLine = true,
                            visualTransformation = PasswordVisualTransformation()
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    val isFormValid = parentName.isNotBlank() && childName.isNotBlank() && 
                            childSchool.isNotBlank() && childPin.length == 4 && 
                            !childGradeSelected.contains("Selecciona")

                    if (isLoading) {
                        CircularProgressIndicator(
                            color = AppColors.Purple,
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )
                    } else {
                        Button(
                            onClick = {
                                isLoading = true
                                // 1. Save adult profile in "adultos"
                                val db = com.google.firebase.firestore.FirebaseFirestore.getInstance()
                                val parentData = hashMapOf(
                                    "email" to email,
                                    "nombre" to parentName,
                                    "rol" to "padres",
                                    "perfil_completo" to true,
                                    "estudiante_pin" to childPin.toInt()
                                )
                                db.collection("adultos").document(email).set(parentData)
                                    .addOnSuccessListener {
                                        // 2. Save child profile in "usuarios"
                                        val childData = hashMapOf(
                                            "nombre" to childName,
                                            "colegio" to childSchool,
                                            "grado" to childGradeSelected,
                                            "pin" to childPin.toInt(),
                                            "estrellas" to 0,
                                            "precision" to 0.0,
                                            "nivel_actual" to 1,
                                            "padre_email" to email,
                                            "avatar" to "👶",
                                            "equipped_theme" to "Lila Clásico",
                                            "unlocked_avatars" to listOf("👶"),
                                            "unlocked_themes" to listOf("Lila Clásico")
                                        )
                                        db.collection("usuarios").document(childPin).set(childData)
                                            .addOnSuccessListener {
                                                isLoading = false
                                                Toast.makeText(context, "¡Perfiles creados con éxito!", Toast.LENGTH_SHORT).show()
                                                onCompleteSuccess()
                                            }
                                            .addOnFailureListener {
                                                isLoading = false
                                                Toast.makeText(context, "Error al crear perfil del niño", Toast.LENGTH_SHORT).show()
                                            }
                                    }
                                    .addOnFailureListener {
                                        isLoading = false
                                        Toast.makeText(context, "Error al crear perfil del padre", Toast.LENGTH_SHORT).show()
                                    }
                            },
                            enabled = isFormValid,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            shape = RoundedCornerShape(50.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = AppColors.Purple,
                                disabledContainerColor = AppColors.Gray400
                            )
                        ) {
                            Text(
                                "Guardar y Entrar",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        }
                    }

                    OutlinedButton(
                        onClick = onLogout,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(50.dp),
                        border = BorderStroke(2.dp, AppColors.Purple)
                    ) {
                        Text("Cancelar / Salir", color = AppColors.Purple, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
