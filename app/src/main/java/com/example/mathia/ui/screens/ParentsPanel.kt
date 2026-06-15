package com.example.mathia.ui.screens

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
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
import com.example.mathia.AppColors
import com.example.mathia.StudentViewModel
import com.example.mathia.model.FirebaseStudent
import com.example.mathia.model.Student
import com.example.mathia.model.ReporteSesion
import com.example.mathia.model.Observacion
import com.example.mathia.model.NotificacionFirebase
import com.example.mathia.ui.components.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParentsPanel(
    onBack: () -> Unit,
    onCreateStudentProfile: () -> Unit,
    parentEmail: String,
    viewModel: StudentViewModel
) {
    var tabState by remember { mutableIntStateOf(0) } // 0 = Resumen, 1 = Reportes, 2 = Observaciones, 3 = Perfil
    var children by remember { mutableStateOf<List<FirebaseStudent>>(emptyList()) }
    var selectedChild by remember { mutableStateOf<Student?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var isDropdownExpanded by remember { mutableStateOf(false) }

    // Live state from VM
    val sessionReports by viewModel.reportesSesiones.collectAsState()
    val observations by viewModel.observaciones.collectAsState()
    val notifications by viewModel.notificaciones.collectAsState()

    val parentUid = remember { FirebaseAuth.getInstance().currentUser?.uid ?: "" }
    var parentName by remember { mutableStateOf("") }
    
    val db = FirebaseFirestore.getInstance()

    // Load initial data
    LaunchedEffect(parentEmail) {
        if (parentEmail.isNotEmpty()) {
            if (parentUid.isNotEmpty()) {
                try {
                    val doc = db.collection("usuarios").document(parentUid).get().await()
                    parentName = doc.getString("nombre") ?: ""
                    viewModel.obtenerNotificaciones(parentUid)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            viewModel.obtenerEstudiantesPorEmailPadre(parentEmail) { list ->
                children = list
                isLoading = false
                
                // Select first child automatically if exists
                if (list.isNotEmpty() && selectedChild == null) {
                    val first = list.first()
                    val parts = first.nombre.split(" ")
                    selectedChild = Student(
                        id = first.pin,
                        name = parts.firstOrNull() ?: first.nombre,
                        lastName = parts.drop(1).joinToString(" "),
                        grade = first.grado,
                        classroom = first.seccion.ifEmpty { "Sección A" },
                        pin = first.pin.toString(),
                        level = first.nivel_actual,
                        stars = first.estrellas,
                        xp = first.xp,
                        totalQuestions = first.total_preguntas,
                        correctAnswers = first.correctas,
                        examsCompleted = first.total_preguntas / 10 + 1,
                        accuracy = first.precision.toInt(),
                        streak = first.streak,
                        weekData = first.weekData.ifEmpty { listOf(10, 20, 15, 30, 25, 40, 35) },
                        monthData = first.monthData.ifEmpty { listOf(40, 50, 45, 60, 55, 70, 65, 80, 75, 90, 85, 95) },
                        skills = first.skills.ifEmpty {
                            mapOf(
                                "Sumas" to (first.nivel_actual * 20).coerceAtMost(100),
                                "Restas" to (first.nivel_actual * 18).coerceAtMost(100),
                                "Multiplicación" to (first.nivel_actual * 15).coerceAtMost(100),
                                "Fracciones" to (first.nivel_actual * 12).coerceAtMost(100),
                                "Series" to (first.nivel_actual * 14).coerceAtMost(100)
                            )
                        },
                        incorrectasPorTema = first.incorrectas_por_tema,
                        asistencia = first.asistencia,
                        recomendaciones = first.recomendaciones,
                        avatar = first.avatar,
                        diagnosticoRealizado = first.diagnostico_realizado,
                        tiempoTotal = first.tiempo_total,
                        tiempoPromedio = first.tiempo_promedio,
                        equippedTheme = first.equipped_theme,
                        unlockedAvatars = first.unlocked_avatars,
                        unlockedThemes = first.unlocked_themes,
                        edad = first.edad,
                        colegio = first.colegio,
                        seccion = first.seccion,
                        docenteAsignado = first.docente_asignado
                    )
                }
            }
            viewModel.obtenerReportesSesionPorPadre(parentEmail)
        } else {
            isLoading = false
        }
    }

    // Load child observations when child changes
    LaunchedEffect(selectedChild) {
        selectedChild?.let { child ->
            viewModel.obtenerObservaciones(child.pin)
        }
    }

    Scaffold(
        bottomBar = {
            NavigationBar(containerColor = Color.White) {
                NavigationBarItem(
                    selected = tabState == 0,
                    onClick = { tabState = 0 },
                    icon = { Icon(Icons.Default.Home, null) },
                    label = { Text("Resumen", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(selectedIconColor = AppColors.Purple, unselectedIconColor = AppColors.Gray400)
                )
                NavigationBarItem(
                    selected = tabState == 1,
                    onClick = { tabState = 1 },
                    icon = { Icon(Icons.Default.Info, null) },
                    label = { Text("Reportes", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(selectedIconColor = AppColors.Purple, unselectedIconColor = AppColors.Gray400)
                )
                NavigationBarItem(
                    selected = tabState == 2,
                    onClick = { tabState = 2 },
                    icon = { Icon(Icons.Default.List, null) },
                    label = { Text("Observaciones", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(selectedIconColor = AppColors.Purple, unselectedIconColor = AppColors.Gray400)
                )
                NavigationBarItem(
                    selected = tabState == 3,
                    onClick = { tabState = 3 },
                    icon = { Icon(Icons.Default.Person, null) },
                    label = { Text("Perfil", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(selectedIconColor = AppColors.Purple, unselectedIconColor = AppColors.Gray400)
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(AppColors.Bg)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            
            // Header row (Parent Panel + Log out)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = when (tabState) {
                        0 -> "Panel de Padres 👨‍👩‍👧"
                        1 -> "Reportes de Sesiones 📝"
                        2 -> "Observaciones del Maestro 🏫"
                        else -> "Mi Cuenta de Tutor 👨‍👩‍👧"
                    },
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = AppColors.Purple
                )
                IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = AppColors.Purple) }
            }

            if (isLoading) {
                Box(modifier = Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = AppColors.Purple)
                }
            } else {
                // Child Selector dropdown (visible on all tabs except Perfil)
                if (tabState != 3 && children.isNotEmpty()) {
                    Box(modifier = Modifier.fillMaxWidth()) {
                        OutlinedButton(
                            onClick = { isDropdownExpanded = !isDropdownExpanded },
                            modifier = Modifier.fillMaxWidth().height(50.dp),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.5.dp, AppColors.Purple),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = AppColors.Purple)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = selectedChild?.let { "${it.avatar} ${it.name} ${it.lastName}" } ?: "Seleccionar Hijo",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                                Icon(Icons.Default.ArrowDropDown, null)
                            }
                        }
                        
                        DropdownMenu(
                            expanded = isDropdownExpanded,
                            onDismissRequest = { isDropdownExpanded = false },
                            modifier = Modifier.fillMaxWidth(0.9f)
                        ) {
                            children.forEach { child ->
                                val parts = child.nombre.split(" ")
                                val firstName = parts.firstOrNull() ?: child.nombre
                                val lastName = parts.drop(1).joinToString(" ")
                                DropdownMenuItem(
                                    text = { Text("${child.avatar} ${child.nombre} (${child.grado} - ${child.seccion})") },
                                    onClick = {
                                        selectedChild = Student(
                                            id = child.pin,
                                            name = firstName,
                                            lastName = lastName,
                                            grade = child.grado,
                                            classroom = child.seccion.ifEmpty { "Sección A" },
                                            pin = child.pin.toString(),
                                            level = child.nivel_actual,
                                            stars = child.estrellas,
                                            xp = child.xp,
                                            totalQuestions = child.total_preguntas,
                                            correctAnswers = child.correctas,
                                            examsCompleted = child.total_preguntas / 10 + 1,
                                            accuracy = child.precision.toInt(),
                                            streak = child.streak,
                                            weekData = child.weekData.ifEmpty { listOf(10, 20, 15, 30, 25, 40, 35) },
                                            monthData = child.monthData.ifEmpty { listOf(40, 50, 45, 60, 55, 70, 65, 80, 75, 90, 85, 95) },
                                            skills = child.skills.ifEmpty {
                                                mapOf(
                                                    "Sumas" to (child.nivel_actual * 20).coerceAtMost(100),
                                                    "Restas" to (child.nivel_actual * 18).coerceAtMost(100),
                                                    "Multiplicación" to (child.nivel_actual * 15).coerceAtMost(100),
                                                    "Fracciones" to (child.nivel_actual * 12).coerceAtMost(100),
                                                    "Series" to (child.nivel_actual * 14).coerceAtMost(100)
                                                )
                                            },
                                            incorrectasPorTema = child.incorrectas_por_tema,
                                            asistencia = child.asistencia,
                                            recomendaciones = child.recomendaciones,
                                            avatar = child.avatar,
                                            diagnosticoRealizado = child.diagnostico_realizado,
                                            tiempoTotal = child.tiempo_total,
                                            tiempoPromedio = child.tiempo_promedio,
                                            equippedTheme = child.equipped_theme,
                                            unlockedAvatars = child.unlocked_avatars,
                                            unlockedThemes = child.unlocked_themes,
                                            edad = child.edad,
                                            colegio = child.colegio,
                                            seccion = child.seccion,
                                            docenteAsignado = child.docente_asignado
                                        )
                                        isDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                // Main tab contents
                Box(modifier = Modifier.weight(1f)) {
                    if (children.isEmpty() && tabState != 3) {
                        // Empty children state
                        Card(
                            modifier = Modifier.fillMaxWidth().padding(top = 20.dp),
                            shape = RoundedCornerShape(24.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White)
                        ) {
                            Column(
                                modifier = Modifier.padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Text("👶", fontSize = 48.sp)
                                Text("No tienes hijos registrados", fontWeight = FontWeight.Bold, color = AppColors.Purple)
                                Text("Por favor, pulsa el botón inferior para registrar a tu hijo en MathIA.", fontSize = 14.sp, color = AppColors.Gray600, textAlign = TextAlign.Center)
                            }
                        }
                    } else {
                        when (tabState) {
                            0 -> {
                                // TAB 0: RESUMEN ACADEMICO DEL HIJO SELECCIONADO
                                selectedChild?.let { s ->
                                    val semColor = when {
                                        s.accuracy >= 80 -> AppColors.Green
                                        s.accuracy >= 50 -> AppColors.Amber
                                        else -> AppColors.Red
                                    }
                                    val semText = when {
                                        s.accuracy >= 80 -> "Desempeño Alto 🟢"
                                        s.accuracy >= 50 -> "Desempeño Medio 🟡"
                                        else -> "Requiere Reforzamiento 🔴"
                                    }

                                    Column(
                                        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
                                        verticalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        // Semaphore
                                        Card(
                                            modifier = Modifier.fillMaxWidth(),
                                            shape = RoundedCornerShape(16.dp),
                                            colors = CardDefaults.cardColors(containerColor = semColor.copy(alpha = 0.1f)),
                                            border = BorderStroke(2.dp, semColor)
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(12.dp),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Text("Estatus Académico:", fontWeight = FontWeight.Bold, color = AppColors.Gray800)
                                                Text(semText, fontWeight = FontWeight.Black, color = semColor)
                                            }
                                        }

                                        // Stats metrics card
                                        Card(
                                            modifier = Modifier.fillMaxWidth(),
                                            shape = RoundedCornerShape(20.dp),
                                            colors = CardDefaults.cardColors(containerColor = Color.White)
                                        ) {
                                            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                                Text("📊 Métricas de ${s.name}", fontWeight = FontWeight.Bold, color = AppColors.Purple)
                                                Divider(color = AppColors.PurpleLight)
                                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                                    Text("Estrellas Recolectadas:", color = AppColors.Gray600)
                                                    Text("⭐ ${s.stars}", fontWeight = FontWeight.Bold, color = AppColors.Amber)
                                                }
                                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                                    Text("Precisión General:", color = AppColors.Gray600)
                                                    Text("${s.accuracy}%", fontWeight = FontWeight.Bold, color = AppColors.Green)
                                                }
                                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                                    Text("Racha de Práctica:", color = AppColors.Gray600)
                                                    Text("🔥 ${s.streak} días", fontWeight = FontWeight.Bold, color = AppColors.Pink)
                                                }
                                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                                    Text("Nivel Alcanzado:", color = AppColors.Gray600)
                                                    Text("Nivel ${s.level}", fontWeight = FontWeight.Bold, color = AppColors.Purple)
                                                }
                                            }
                                        }

                                        // Recommendations card
                                        Card(
                                            modifier = Modifier.fillMaxWidth(),
                                            shape = RoundedCornerShape(20.dp),
                                            colors = CardDefaults.cardColors(containerColor = Color(0xFFFAF5FF)),
                                            border = BorderStroke(1.dp, AppColors.Purple.copy(alpha = 0.2f))
                                        ) {
                                            Column(modifier = Modifier.padding(16.dp)) {
                                                Text("💡 Consejos Pedagógicos para Casa", fontWeight = FontWeight.Bold, color = AppColors.Purple)
                                                Spacer(modifier = Modifier.height(8.dp))
                                                if (s.recomendaciones.isEmpty()) {
                                                    Text("Mateo está analizando las estadísticas de tu hijo. ¡Continúen practicando!", fontSize = 12.sp, color = AppColors.Gray600)
                                                } else {
                                                    s.recomendaciones.forEach { rec ->
                                                        Text("• $rec", fontSize = 13.sp, color = AppColors.Gray800, modifier = Modifier.padding(vertical = 2.dp))
                                                    }
                                                }
                                            }
                                        }

                                        // Skills Breakdown progress bar
                                        Card(
                                            modifier = Modifier.fillMaxWidth(),
                                            shape = RoundedCornerShape(20.dp),
                                            colors = CardDefaults.cardColors(containerColor = Color.White)
                                        ) {
                                            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                                Text("🎯 Desglose por Competencia", fontWeight = FontWeight.Bold, color = AppColors.Purple)
                                                s.skills.forEach { (skill, value) ->
                                                    SkillProgressBar(skill, value)
                                                }
                                            }
                                        }

                                        // Weekly evolution
                                        Card(
                                            modifier = Modifier.fillMaxWidth(),
                                            shape = RoundedCornerShape(20.dp),
                                            colors = CardDefaults.cardColors(containerColor = Color.White)
                                        ) {
                                            Column(
                                                modifier = Modifier.padding(16.dp),
                                                horizontalAlignment = Alignment.CenterHorizontally,
                                                verticalArrangement = Arrangement.spacedBy(8.dp)
                                            ) {
                                                Text("📈 Evolución de Ejercicios Diarios", fontWeight = FontWeight.Bold, modifier = Modifier.align(Alignment.Start))
                                                MiniBarChart(data = s.weekData, color = AppColors.Purple)
                                                Text("Últimos 7 días activos", fontSize = 11.sp, color = AppColors.Gray500)
                                            }
                                        }
                                    }
                                }
                            }
                            1 -> {
                                // TAB 1: REPORTES DE SESIONES DEL HIJO SELECCIONADO
                                selectedChild?.let { s ->
                                    val childReports = sessionReports.filter { it.estudianteId == s.pin }
                                    
                                    LazyColumn(
                                        modifier = Modifier.fillMaxSize(),
                                        verticalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        if (childReports.isEmpty()) {
                                            item {
                                                Box(modifier = Modifier.fillMaxWidth().height(150.dp), contentAlignment = Alignment.Center) {
                                                    Text("Aún no se han generado reportes para ${s.name}.", color = AppColors.Gray500)
                                                }
                                            }
                                        } else {
                                            items(childReports) { report ->
                                                val prec = if(report.preguntasCount > 0) (report.correctas * 100) / report.preguntasCount else 0
                                                
                                                Card(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    shape = RoundedCornerShape(16.dp),
                                                    colors = CardDefaults.cardColors(containerColor = Color.White),
                                                    border = BorderStroke(1.dp, AppColors.PurpleLight)
                                                ) {
                                                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                                        Row(
                                                            modifier = Modifier.fillMaxWidth(),
                                                            horizontalArrangement = Arrangement.SpaceBetween,
                                                            verticalAlignment = Alignment.CenterVertically
                                                        ) {
                                                            Text("Sesión de ${report.tipo} 📝", fontWeight = FontWeight.Bold, color = AppColors.Purple, fontSize = 15.sp)
                                                            Text(report.fecha, fontSize = 11.sp, color = AppColors.Gray500)
                                                        }
                                                        Divider(color = AppColors.Gray100)
                                                        
                                                        Row(
                                                            modifier = Modifier.fillMaxWidth(),
                                                            horizontalArrangement = Arrangement.SpaceBetween
                                                        ) {
                                                            Column {
                                                                Text("Preguntas", fontSize = 10.sp, color = AppColors.Gray500)
                                                                Text("${report.preguntasCount}", fontWeight = FontWeight.Bold)
                                                            }
                                                            Column {
                                                                Text("Correctas", fontSize = 10.sp, color = AppColors.Gray500)
                                                                Text("${report.correctas}", fontWeight = FontWeight.Bold, color = AppColors.Green)
                                                            }
                                                            Column {
                                                                Text("Precisión", fontSize = 10.sp, color = AppColors.Gray500)
                                                                Text("$prec%", fontWeight = FontWeight.Black, color = AppColors.Green)
                                                            }
                                                            Column {
                                                                Text("T. Promedio", fontSize = 10.sp, color = AppColors.Gray500)
                                                                Text("${report.tiempoPromedio.toInt()}s", fontWeight = FontWeight.Bold)
                                                            }
                                                        }

                                                        if (report.competenciasDominadas.isNotEmpty()) {
                                                            Text(
                                                                "Habilidades consolidadas: ${report.competenciasDominadas.joinToString(", ")}",
                                                                fontSize = 11.sp,
                                                                color = AppColors.Gray600,
                                                                fontWeight = FontWeight.SemiBold
                                                            )
                                                        }
                                                        if (report.competenciasReforzar.isNotEmpty()) {
                                                            Text(
                                                                "Competencias por reforzar: ${report.competenciasReforzar.joinToString(", ")}",
                                                                fontSize = 11.sp,
                                                                color = AppColors.Red,
                                                                fontWeight = FontWeight.SemiBold
                                                            )
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            2 -> {
                                // TAB 2: OBSERVACIONES DEL DOCENTE PARA EL HIJO SELECCIONADO
                                selectedChild?.let { s ->
                                    LazyColumn(
                                        modifier = Modifier.fillMaxSize(),
                                        verticalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        item {
                                            Text(
                                                "Retroalimentación del docente para ${s.name}:",
                                                fontWeight = FontWeight.SemiBold,
                                                fontSize = 14.sp,
                                                color = AppColors.Gray800
                                            )
                                        }
                                        
                                        if (observations.isEmpty()) {
                                            item {
                                                Box(modifier = Modifier.fillMaxWidth().height(150.dp), contentAlignment = Alignment.Center) {
                                                    Text("El docente no ha registrado observaciones aún.", color = AppColors.Gray500)
                                                }
                                            }
                                        } else {
                                            items(observations) { obs ->
                                                Card(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    shape = RoundedCornerShape(16.dp),
                                                    colors = CardDefaults.cardColors(containerColor = Color.White),
                                                    border = BorderStroke(1.dp, AppColors.PurpleLight)
                                                ) {
                                                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                                        Text(obs.texto, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = AppColors.Purple)
                                                        Row(
                                                            modifier = Modifier.fillMaxWidth(),
                                                            horizontalArrangement = Arrangement.SpaceBetween,
                                                            verticalAlignment = Alignment.CenterVertically
                                                        ) {
                                                            Text("Por: ${obs.docenteNombre}", fontSize = 11.sp, color = AppColors.Gray500)
                                                            Text(obs.fecha, fontSize = 11.sp, color = AppColors.Gray500)
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            3 -> {
                                // TAB 3: PERFIL DEL PADRE / TUTOR
                                LazyColumn(
                                    modifier = Modifier.fillMaxSize(),
                                    verticalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    item {
                                        Card(
                                            modifier = Modifier.fillMaxWidth(),
                                            shape = RoundedCornerShape(24.dp),
                                            colors = CardDefaults.cardColors(containerColor = Color.White)
                                        ) {
                                            Column(
                                                modifier = Modifier.padding(20.dp),
                                                horizontalAlignment = Alignment.CenterHorizontally
                                            ) {
                                                Box(modifier = Modifier.size(80.dp).background(AppColors.PurpleLight, CircleShape), contentAlignment = Alignment.Center) {
                                                    Text("👨‍👩‍👧", fontSize = 44.sp)
                                                }
                                                Spacer(Modifier.height(12.dp))
                                                Text(parentName.ifEmpty { "Padre de Familia" }, fontWeight = FontWeight.ExtraBold, fontSize = 22.sp, color = AppColors.Purple)
                                                Text(parentEmail, fontSize = 14.sp, color = AppColors.Gray500)
                                            }
                                        }
                                    }

                                    // Children list card
                                    item {
                                        Card(
                                            modifier = Modifier.fillMaxWidth(),
                                            shape = RoundedCornerShape(20.dp),
                                            colors = CardDefaults.cardColors(containerColor = Color.White)
                                        ) {
                                            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                                Text("🧒 Hijos Registrados", fontWeight = FontWeight.Bold, color = AppColors.Purple)
                                                Divider(color = AppColors.PurpleLight)
                                                
                                                children.forEach { child ->
                                                    Row(
                                                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                                        horizontalArrangement = Arrangement.SpaceBetween,
                                                        verticalAlignment = Alignment.CenterVertically
                                                    ) {
                                                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                                            Text(child.avatar, fontSize = 20.sp)
                                                            Text(child.nombre, fontWeight = FontWeight.Bold)
                                                        }
                                                        Text("PIN: ${child.pin}", color = AppColors.Gray500, fontSize = 13.sp)
                                                    }
                                                }
                                            }
                                        }
                                    }

                                    // Notifications log card
                                    item {
                                        Card(
                                            modifier = Modifier.fillMaxWidth(),
                                            shape = RoundedCornerShape(20.dp),
                                            colors = CardDefaults.cardColors(containerColor = Color.White)
                                        ) {
                                            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                                Text("🔔 Historial de Notificaciones Escolares", fontWeight = FontWeight.Bold, color = AppColors.Purple)
                                                Divider(color = AppColors.PurpleLight)
                                                
                                                if (notifications.isEmpty()) {
                                                    Text("No hay notificaciones académicas registradas.", fontSize = 12.sp, color = AppColors.Gray500)
                                                } else {
                                                    notifications.forEach { not ->
                                                        Row(
                                                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                                                            verticalAlignment = Alignment.CenterVertically
                                                        ) {
                                                            Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(if(not.read) AppColors.Gray400 else AppColors.Pink))
                                                            Column(modifier = Modifier.weight(1f)) {
                                                                Text(not.titulo, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                                                Text(not.cuerpo, fontSize = 12.sp, color = AppColors.Gray600)
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }

                                    // Logout Button
                                    item {
                                        OutlinedButton(
                                            onClick = onBack,
                                            modifier = Modifier.fillMaxWidth().height(50.dp),
                                            shape = RoundedCornerShape(50.dp),
                                            border = BorderStroke(2.dp, AppColors.Purple)
                                        ) {
                                            Text("Cerrar Sesión", color = AppColors.Purple, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Registrar nuevo hijo button (always present if not in Perfil tab)
            if (tabState != 3) {
                Button(
                    onClick = onCreateStudentProfile,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AppColors.Purple)
                ) {
                    Text("+ Registrar Nuevo Hijo/a", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }
            }
        }
    }
}
