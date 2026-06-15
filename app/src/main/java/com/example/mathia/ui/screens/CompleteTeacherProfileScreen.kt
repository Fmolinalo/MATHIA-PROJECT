package com.example.mathia.ui.screens

import android.os.Build
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
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
import com.example.mathia.AppColors
import com.example.mathia.R
import com.example.mathia.StudentViewModel

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
                        .padding(16.dp)
                ) {
                    Text(
                        text = "¡Hola! Completa tu perfil para que empecemos a trabajar en tu aula.",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.DarkGray
                    )
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Completa tu Perfil 👩‍🏫",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppColors.Purple
                    )

                    if (isLoading) {
                        CircularProgressIndicator(color = AppColors.Purple)
                    } else {
                        // Nombre
                        Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text("Nombre Completo", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = AppColors.Gray600)
                            OutlinedTextField(
                                value = nombre,
                                onValueChange = { nombre = it },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            )
                        }

                        // Colegio
                        Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text("Nombre del Colegio/Escuela", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = AppColors.Gray600)
                            OutlinedTextField(
                                value = colegio,
                                onValueChange = { colegio = it },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            )
                        }

                        // Grado
                        Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text("Grado", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = AppColors.Gray600)
                            Box(modifier = Modifier.fillMaxWidth()) {
                                OutlinedTextField(
                                    value = gradoSelected,
                                    onValueChange = {},
                                    readOnly = true,
                                    modifier = Modifier.fillMaxWidth().clickable { isGradoExpanded = !isGradoExpanded },
                                    enabled = false,
                                    shape = RoundedCornerShape(12.dp),
                                    trailingIcon = { Icon(Icons.Default.ArrowDropDown, null) }
                                )
                                Box(modifier = Modifier.matchParentSize().clickable { isGradoExpanded = !isGradoExpanded })
                                DropdownMenu(expanded = isGradoExpanded, onDismissRequest = { isGradoExpanded = false }) {
                                    grados.forEach { g ->
                                        DropdownMenuItem(text = { Text(g) }, onClick = { gradoSelected = g; isGradoExpanded = false })
                                    }
                                }
                            }
                        }

                        // Sección
                        Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text("Sección", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = AppColors.Gray600)
                            Box(modifier = Modifier.fillMaxWidth()) {
                                OutlinedTextField(
                                    value = seccionSelected,
                                    onValueChange = {},
                                    readOnly = true,
                                    modifier = Modifier.fillMaxWidth().clickable { isSeccionExpanded = !isSeccionExpanded },
                                    enabled = false,
                                    shape = RoundedCornerShape(12.dp),
                                    trailingIcon = { Icon(Icons.Default.ArrowDropDown, null) }
                                )
                                Box(modifier = Modifier.matchParentSize().clickable { isSeccionExpanded = !isSeccionExpanded })
                                DropdownMenu(expanded = isSeccionExpanded, onDismissRequest = { isSeccionExpanded = false }) {
                                    secciones.forEach { s ->
                                        DropdownMenuItem(text = { Text(s) }, onClick = { seccionSelected = s; isSeccionExpanded = false })
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        val isValid = nombre.isNotBlank() && colegio.isNotBlank() && !gradoSelected.contains("Selecciona") && !seccionSelected.contains("Selecciona")

                        Button(
                            onClick = {
                                isLoading = true
                                viewModel.actualizarPerfilDocente(uid, nombre, colegio, gradoSelected, seccionSelected) { success ->
                                    isLoading = false
                                    if (success) {
                                        Toast.makeText(context, "¡Perfil guardado con éxito!", Toast.LENGTH_SHORT).show()
                                        onCompleteSuccess()
                                    } else {
                                        Toast.makeText(context, "Error al guardar el perfil. Intenta de nuevo.", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            },
                            enabled = isValid,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            shape = RoundedCornerShape(50.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = AppColors.Purple)
                        ) {
                            Text("Guardar y Continuar", fontWeight = FontWeight.Bold)
                        }
                    }
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
                Text("Cerrar Sesión", color = AppColors.Purple, fontWeight = FontWeight.Bold)
            }
        }
    }
}
