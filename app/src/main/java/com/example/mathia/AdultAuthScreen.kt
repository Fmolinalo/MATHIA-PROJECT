package com.example.mathia

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

@Composable
fun AdultAuthScreen(
    rol: String,
    onBack: () -> Unit,
    onLoginSuccess: (String, String) -> Unit   // (rol, email)
) {
    var isLoginMode by remember { mutableStateOf(true) }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var nombre by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var estudiantePin by remember { mutableStateOf("") }
    var error by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()
    val repository = remember { FirebaseRepository() }
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = AppColors.White)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    if (isLoginMode) "Iniciar Sesión" else "Registrarse",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = AppColors.Purple
                )

                Text(
                    if (rol == "docente") "Panel Docente" else "Panel de Padres",
                    fontSize = 14.sp,
                    color = AppColors.Gray600
                )

                if (!isLoginMode) {
                    OutlinedTextField(
                        value = nombre,
                        onValueChange = { nombre = it },
                        label = { Text("Nombre completo") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Correo electrónico") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Contraseña") },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                if (!isLoginMode) {
                    OutlinedTextField(
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it },
                        label = { Text("Confirmar contraseña") },
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    if (rol == "padres") {
                        OutlinedTextField(
                            value = estudiantePin,
                            onValueChange = { if (it.length <= 4) estudiantePin = it },
                            label = { Text("PIN de tu hijo/a (4 dígitos)") },
                            placeholder = { Text("Ej: 1234") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                    }
                }

                if (error.isNotEmpty()) {
                    Text(error, color = AppColors.Red, fontSize = 14.sp)
                }

                if (isLoading) {
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                }

                Button(
                    onClick = {
                        scope.launch {
                            isLoading = true
                            error = ""

                            if (isLoginMode) {
                                // MODO LOGIN
                                val adulto = repository.loginAdulto(email, password)
                                if (adulto != null && adulto.rol == rol) {
                                    Toast.makeText(context, "Bienvenido ${adulto.nombre}", Toast.LENGTH_SHORT).show()
                                    onLoginSuccess(rol, email)  // Pasa rol y email
                                } else {
                                    error = "Credenciales incorrectas"
                                }
                            } else {
                                // MODO REGISTRO
                                when {
                                    nombre.isBlank() -> error = "Ingresa tu nombre"
                                    email.isBlank() -> error = "Ingresa tu correo"
                                    !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() ->
                                        error = "Correo electrónico no válido"
                                    password.length < 6 -> error = "La contraseña debe tener al menos 6 caracteres"
                                    password != confirmPassword -> error = "Las contraseñas no coinciden"
                                    rol == "padres" && estudiantePin.length != 4 -> error = "Ingresa el PIN de 4 dígitos de tu hijo/a"
                                    else -> {
                                        val estudiante = if (rol == "padres" && estudiantePin.isNotEmpty()) {
                                            repository.obtenerAlumnoPorPin(estudiantePin)
                                        } else {
                                            null
                                        }

                                        if (rol == "padres" && estudiante == null) {
                                            error = "No se encontró un estudiante con el PIN $estudiantePin"
                                        } else {
                                            val estudiantePinInt = if (rol == "padres") estudiantePin.toIntOrNull() else null
                                            val success = repository.registrarAdulto(
                                                email = email,
                                                password = password,
                                                rol = rol,
                                                nombre = nombre,
                                                estudiantePin = estudiantePinInt
                                            )

                                            if (success) {
                                                if (rol == "padres" && estudiantePin.isNotEmpty()) {
                                                    repository.actualizarPadreEmail(estudiantePin, email)
                                                }

                                                Toast.makeText(
                                                    context,
                                                    "Registro exitoso. Ahora inicia sesión.",
                                                    Toast.LENGTH_LONG
                                                ).show()
                                                isLoginMode = true
                                                nombre = ""
                                                confirmPassword = ""
                                                email = ""
                                                password = ""
                                                estudiantePin = ""
                                            } else {
                                                error = "Error al registrar. El correo ya existe?"
                                            }
                                        }
                                    }
                                }
                            }
                            isLoading = false
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading,
                    colors = ButtonDefaults.buttonColors(containerColor = AppColors.Purple)
                ) {
                    Text(if (isLoginMode) "Entrar" else "Registrarse")
                }

                TextButton(
                    onClick = {
                        isLoginMode = !isLoginMode
                        error = ""
                        nombre = ""
                        confirmPassword = ""
                        estudiantePin = ""
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        if (isLoginMode) "¿No tienes cuenta? Regístrate" else "¿Ya tienes cuenta? Inicia sesión",
                        color = AppColors.Purple
                    )
                }

                OutlinedButton(
                    onClick = onBack,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Volver")
                }
            }
        }
    }
}