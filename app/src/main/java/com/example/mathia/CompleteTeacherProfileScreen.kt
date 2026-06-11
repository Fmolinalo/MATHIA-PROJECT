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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.ImageLoader
import coil.compose.rememberAsyncImagePainter
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompleteTeacherProfileScreen(
    uid: String,
    email: String,
    onCompleteSuccess: () -> Unit,
    onLogout: () -> Unit,
    viewModel: StudentViewModel
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    
    // States
    var nombre by remember { mutableStateOf(email.substringBefore("@").replaceFirstChar { it.uppercase() }) }
    var colegio by remember { mutableStateOf("") }
    var gradoSelected by remember { mutableStateOf("Selecciona tu Grado") }
    var seccionSelected by remember { mutableStateOf("Selecciona tu Sección") }
    var isLoading by remember { mutableStateOf(false) }

    // Dropdown visibility states
    var isGradoExpanded by remember { mutableStateOf(false) }
    var isSeccionExpanded by remember { mutableStateOf(false) }

    val grados = listOf("1ro de Primaria", "2do de Primaria", "3ro de Primaria", "4to de Primaria", "5to de Primaria", "6to de Primaria")
    val secciones = listOf("Sección A", "Sección B", "Sección C", "Sección D", "Sección E")

    // Image loader for Ajolote GIF
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

            // Mateo speech bubble & GIF
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Mateo Ajolote GIF
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

                // Speech Bubble
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .background(Color.White, RoundedCornerShape(16.dp))
                        .border(2.dp, AppColors.PurpleLight, RoundedCornerShape(16.dp))
                        .padding(12.dp)
                ) {
                    Text(
                        text = "¡Hola, colega! Ayúdame a registrar tu aula para configurar MathIA para tus alumnos.",
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
                        text = "Registro de Docente 👩‍🏫",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppColors.Purple,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )

                    Divider(color = AppColors.PurpleLight, thickness = 1.dp)

                    // Nombre Completo
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("Nombre Completo", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = AppColors.Gray600)
                        OutlinedTextField(
                            value = nombre,
                            onValueChange = { nombre = it },
                            placeholder = { Text("Ej. Prof. María Pérez") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = AppColors.Purple,
                                unfocusedBorderColor = AppColors.Gray400
                            ),
                            singleLine = true
                        )
                    }

                    // Institución / Colegio
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("Institución Educativa / Colegio", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = AppColors.Gray600)
                        OutlinedTextField(
                            value = colegio,
                            onValueChange = { colegio = it },
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
                        Text("Grado", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = AppColors.Gray600)
                        Box(modifier = Modifier.fillMaxWidth()) {
                            OutlinedTextField(
                                value = gradoSelected,
                                onValueChange = {},
                                readOnly = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { isGradoExpanded = !isGradoExpanded },
                                enabled = false, // disable key entry but allow clicking wrapper
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    disabledBorderColor = if (gradoSelected.contains("Selecciona")) AppColors.Gray400 else AppColors.Purple,
                                    disabledTextColor = if (gradoSelected.contains("Selecciona")) AppColors.Gray500 else Color.Black
                                ),
                                trailingIcon = {
                                    Icon(
                                        imageVector = if (isGradoExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.ArrowDropDown,
                                        contentDescription = null,
                                        tint = AppColors.Purple
                                    )
                                }
                            )
                            // Transparent clickable overlay for trigger
                            Box(
                                modifier = Modifier
                                    .matchParentSize()
                                    .clickable { isGradoExpanded = !isGradoExpanded }
                            )

                            DropdownMenu(
                                expanded = isGradoExpanded,
                                onDismissRequest = { isGradoExpanded = false },
                                modifier = Modifier
                                    .fillMaxWidth(0.8f)
                                    .background(Color.White)
                            ) {
                                grados.forEach { g ->
                                    DropdownMenuItem(
                                        text = { Text(g, color = Color.Black) },
                                        onClick = {
                                            gradoSelected = g
                                            isGradoExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    // Sección Dropdown
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("Sección", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = AppColors.Gray600)
                        Box(modifier = Modifier.fillMaxWidth()) {
                            OutlinedTextField(
                                value = seccionSelected,
                                onValueChange = {},
                                readOnly = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { isSeccionExpanded = !isSeccionExpanded },
                                enabled = false,
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    disabledBorderColor = if (seccionSelected.contains("Selecciona")) AppColors.Gray400 else AppColors.Purple,
                                    disabledTextColor = if (seccionSelected.contains("Selecciona")) AppColors.Gray500 else Color.Black
                                ),
                                trailingIcon = {
                                    Icon(
                                        imageVector = if (isSeccionExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.ArrowDropDown,
                                        contentDescription = null,
                                        tint = AppColors.Purple
                                    )
                                }
                            )
                            Box(
                                modifier = Modifier
                                    .matchParentSize()
                                    .clickable { isSeccionExpanded = !isSeccionExpanded }
                            )

                            DropdownMenu(
                                expanded = isSeccionExpanded,
                                onDismissRequest = { isSeccionExpanded = false },
                                modifier = Modifier
                                    .fillMaxWidth(0.8f)
                                    .background(Color.White)
                            ) {
                                secciones.forEach { s ->
                                    DropdownMenuItem(
                                        text = { Text(s, color = Color.Black) },
                                        onClick = {
                                            seccionSelected = s
                                            isSeccionExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Guardar Button (3D styled)
                    val isFormValid = nombre.isNotBlank() && colegio.isNotBlank() && 
                            !gradoSelected.contains("Selecciona") && !seccionSelected.contains("Selecciona")

                    if (isLoading) {
                        CircularProgressIndicator(
                            color = AppColors.Purple,
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )
                    } else {
                        Button(
                            onClick = {
                                isLoading = true
                                viewModel.actualizarPerfilDocente(
                                    uid = uid,
                                    nombre = nombre,
                                    colegio = colegio,
                                    grado = gradoSelected,
                                    seccion = seccionSelected
                                ) { success ->
                                    isLoading = false
                                    if (success) {
                                        Toast.makeText(context, "¡Perfil guardado con éxito!", Toast.LENGTH_SHORT).show()
                                        onCompleteSuccess()
                                    } else {
                                        Toast.makeText(context, "Error al guardar el perfil", Toast.LENGTH_SHORT).show()
                                    }
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

                    // Logout / Volver Button
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
