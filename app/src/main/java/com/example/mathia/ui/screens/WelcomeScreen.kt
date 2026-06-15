package com.example.mathia.ui.screens

import android.os.Build
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import com.example.mathia.AppColors
import com.example.mathia.R
import com.example.mathia.SeedDataRepository
import com.example.mathia.StudentViewModel
import kotlinx.coroutines.launch

@Composable
fun WelcomeScreen(onCreateProfile: () -> Unit, onLogin: () -> Unit) {
    val context = LocalContext.current
    val imageLoader = remember(context) {
        ImageLoader.Builder(context).components {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) add(ImageDecoderDecoder.Factory())
            else add(GifDecoder.Factory())
        }.build()
    }

    val infiniteTransition = rememberInfiniteTransition(label = "ajolote_pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    val starOffsets = remember {
        List(12) {
            Offset(
                x = (50..950).random().toFloat(),
                y = (100..1800).random().toFloat()
            )
        }
    }

    val scope = rememberCoroutineScope()
    val seedRepo = remember { SeedDataRepository() }

    // Setup dialog state
    var showSetupDialog by remember { mutableStateOf(false) }
    var showResultDialog by remember { mutableStateOf(false) }
    var isSettingUp by remember { mutableStateOf(false) }
    var setupLog by remember { mutableStateOf(mutableListOf<String>()) }
    var setupResult by remember { mutableStateOf("") }

    Box(modifier = Modifier.fillMaxSize()) {
        var demoChecked by remember { mutableStateOf(StudentViewModel.isDemoMode) }

        // ── Switch Modo Demo (top right) ────────────────────────────────────
        Row(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
                .statusBarsPadding(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Demo 🧪",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = if (demoChecked) AppColors.SageGreen else AppColors.Gray600
            )
            Switch(
                checked = demoChecked,
                onCheckedChange = { checked ->
                    demoChecked = checked
                    StudentViewModel.isDemoMode = checked
                },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = AppColors.SageGreen,
                    checkedTrackColor = AppColors.SageGreen.copy(alpha = 0.5f),
                    uncheckedThumbColor = AppColors.Gray400,
                    uncheckedTrackColor = AppColors.Gray200
                )
            )
        }

        // ── Canvas estrellas ─────────────────────────────────────────────────
        Canvas(modifier = Modifier.fillMaxSize()) {
            starOffsets.forEach { offset ->
                val sz = 10f
                val path = Path().apply {
                    moveTo(offset.x, offset.y - sz)
                    lineTo(offset.x + sz * 0.3f, offset.y - sz * 0.3f)
                    lineTo(offset.x + sz, offset.y)
                    lineTo(offset.x + sz * 0.3f, offset.y + sz * 0.3f)
                    lineTo(offset.x, offset.y + sz)
                    lineTo(offset.x - sz * 0.3f, offset.y + sz * 0.3f)
                    lineTo(offset.x - sz, offset.y)
                    lineTo(offset.x - sz * 0.3f, offset.y - sz * 0.3f)
                    close()
                }
                drawPath(path, AppColors.Amber.copy(alpha = 0.7f))
            }
        }

        // ── Contenido principal ──────────────────────────────────────────────
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Logo Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("🧮", fontSize = 40.sp)
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "MathIA",
                        fontSize = 30.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = AppColors.Purple
                    )
                    Text(
                        "¡Aprende mates jugando!",
                        fontSize = 13.sp,
                        color = AppColors.Gray600
                    )
                }
            }

            // Ajolote
            Box(
                modifier = Modifier
                    .size(150.dp)
                    .background(AppColors.PinkLight, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = R.drawable.ajolote,
                    imageLoader = imageLoader,
                    contentDescription = "Mateo el Ajolote",
                    modifier = Modifier
                        .size(115.dp)
                        .scale(scale)
                )
            }

            // Bienvenida
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "¡Bienvenidos a MathIA!",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        "¡Hola! Soy Mateo, tu amigo ajolote.\n¡Vamos a aprender matemáticas juntos!",
                        fontSize = 13.sp,
                        color = AppColors.Gray600,
                        textAlign = TextAlign.Center
                    )
                }
            }

            // Botones
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // ── BOTÓN PRINCIPAL ─────────────────────────────────────
                Button(
                    onClick = onLogin,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AppColors.Purple)
                ) {
                    Text(
                        "¡Comenzar! 🚀",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Color.White
                    )
                }

                // ── BOTÓN CONFIGURAR USUARIOS (Para el evaluador) ───────
                OutlinedButton(
                    onClick = { showSetupDialog = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(50.dp),
                    border = BorderStroke(1.5.dp, AppColors.SageGreen),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = AppColors.SageGreen
                    )
                ) {
                    Text(
                        "⚙️  Configurar Usuarios Demo",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }

    // ── DIÁLOGO: Confirmación de Setup ──────────────────────────────────────
    if (showSetupDialog) {
        AlertDialog(
            onDismissRequest = { if (!isSettingUp) showSetupDialog = false },
            containerColor = Color.White,
            shape = RoundedCornerShape(24.dp),
            title = {
                Text(
                    "⚙️ Configurar Usuarios Demo",
                    fontWeight = FontWeight.Bold,
                    color = AppColors.Purple
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        "Se crearán los siguientes usuarios en Firebase Auth y Firestore:",
                        fontSize = 14.sp,
                        color = AppColors.Gray700
                    )
                    Card(
                        colors = CardDefaults.cardColors(containerColor = AppColors.Bg),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                "👨‍🏫 50 Docentes",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = AppColors.Purple
                            )
                            Text(
                                "📧 teacher01@mathia.com a teacher50@mathia.com\n🔑 Contraseña: MathIA2026!",
                                fontSize = 11.sp,
                                color = AppColors.Gray700
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                "👪 50 Padres de Familia",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = AppColors.Purple
                            )
                            Text(
                                "📧 parent01@mathia.com a parent50@mathia.com\n🔑 Contraseña: MathIA2026!",
                                fontSize = 11.sp,
                                color = AppColors.Gray700
                            )
                            HorizontalDivider(color = AppColors.Gray200)
                            Text(
                                "🧒 50 Alumnos asignados automáticamente:\n📌 PINs desde 1234 hasta 1283",
                                fontSize = 11.sp,
                                color = AppColors.Gray700,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    if (isSettingUp) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            CircularProgressIndicator(
                                color = AppColors.Purple,
                                modifier = Modifier.size(32.dp)
                            )
                            setupLog.lastOrNull()?.let { ultimo ->
                                Text(
                                    ultimo,
                                    fontSize = 12.sp,
                                    color = AppColors.Gray600,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                if (!isSettingUp) {
                    Button(
                        onClick = {
                            isSettingUp = true
                            setupLog = mutableListOf()
                            scope.launch {
                                val result = seedRepo.configurarUsuariosSemilla(
                                    onProgress = { msg ->
                                        setupLog = (setupLog + msg).toMutableList()
                                    }
                                )
                                isSettingUp = false
                                showSetupDialog = false
                                setupResult = result.getOrDefault("Error en la configuración")
                                showResultDialog = true
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = AppColors.Purple),
                        shape = RoundedCornerShape(50.dp)
                    ) {
                        Text("✅ Crear Usuarios", fontWeight = FontWeight.Bold)
                    }
                }
            },
            dismissButton = {
                if (!isSettingUp) {
                    TextButton(onClick = { showSetupDialog = false }) {
                        Text("Cancelar", color = AppColors.Gray600)
                    }
                }
            }
        )
    }

    // ── DIÁLOGO: Resultado del Setup ────────────────────────────────────────
    if (showResultDialog) {
        AlertDialog(
            onDismissRequest = { showResultDialog = false },
            containerColor = Color.White,
            shape = RoundedCornerShape(24.dp),
            title = {
                Text(
                    "🎉 ¡Configuración Completada!",
                    fontWeight = FontWeight.Bold,
                    color = AppColors.Green
                )
            },
            text = {
                Column(
                    modifier = Modifier
                        .verticalScroll(rememberScrollState())
                        .fillMaxWidth()
                ) {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFE8F5E9)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = setupResult,
                            fontSize = 13.sp,
                            modifier = Modifier.padding(14.dp),
                            color = AppColors.Gray800,
                            lineHeight = 20.sp
                        )
                    }
                    Spacer(Modifier.height(12.dp))

                    // Tabla resumen final
                    Text(
                        "📋 Resumen de Acceso:",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 14.sp,
                        color = AppColors.Purple
                    )
                    Spacer(Modifier.height(8.dp))
                    val resumenAcceso = listOf(
                        Triple("👨‍🏫", "docente@mathia.com", "Docente"),
                        Triple("👪", "parent@mathia.com", "Padre → PIN 1234"),
                        Triple("👪", "mama1@mathia.com", "Padre → PIN 1111"),
                        Triple("👪", "mama2@mathia.com", "Padre → PIN 2222"),
                        Triple("👪", "papa3@mathia.com", "Padre → PIN 3333")
                    )
                    resumenAcceso.forEach { (icon, email, desc) ->
                        Row(
                            modifier = Modifier.padding(vertical = 3.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(icon, fontSize = 14.sp)
                            Column {
                                Text(
                                    email,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AppColors.Gray800
                                )
                                Text(desc, fontSize = 11.sp, color = AppColors.Gray600)
                            }
                        }
                    }
                    Spacer(Modifier.height(6.dp))
                    Text(
                        "🔑 Contraseña para todos: MathIA2026!",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppColors.SageGreen
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { showResultDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = AppColors.Purple),
                    shape = RoundedCornerShape(50.dp)
                ) {
                    Text("¡Listo! 🚀", fontWeight = FontWeight.Bold)
                }
            }
        )
    }
}
