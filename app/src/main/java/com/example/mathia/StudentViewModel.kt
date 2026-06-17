package com.example.mathia

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import com.example.mathia.model.*

class StudentViewModel : ViewModel() {

    private val repository = FirebaseRepository()

    companion object {
        var isDemoMode = false
        val mockUsuarios = mutableMapOf<String, Any>()
        val mockHistorial = mutableListOf<RespuestaHistorial>()
        val mockReportes = mutableListOf<ReporteSesion>()
        val mockObservaciones = mutableListOf<Observacion>()
        val mockNotificaciones = mutableListOf<NotificacionFirebase>()

        init {
            resetMockData()
        }

        fun resetMockData() {
            mockUsuarios.clear()
            mockHistorial.clear()
            mockReportes.clear()
            mockObservaciones.clear()
            mockNotificaciones.clear()

            // 1. Student Sofía Pacheco (pin "1234")
            mockUsuarios["1234"] = FirebaseStudent(
                nombre = "Sofía Pacheco",
                grado = "3ro de Primaria",
                seccion = "Sección A",
                edad = 6,
                colegio = "Maria Auxiliadora",
                docente_asignado = "Efraín Caceres",
                fecha_creacion = System.currentTimeMillis() - 10 * 24 * 3600000L,
                nivel_actual = 1,
                precision = 85.0,
                estrellas = 505,
                xp = 90,
                pin = 1234,
                padre_email = "parent@mathia.com",
                avatar = "default",
                equipped_theme = "Lila Clásico",
                unlocked_avatars = listOf("default"),
                unlocked_themes = listOf("Lila Clásico", "Verde Menta"),
                streak = 3,
                daily_mission_progress = 3,
                weekly_mission_progress = 12,
                skills = mapOf(
                    "Sumas" to 90,
                    "Restas" to 80,
                    "Multiplicación" to 75,
                    "Fracciones" to 60,
                    "Series" to 85
                ),
                incorrectas_por_tema = mapOf("Fracciones" to 5, "Multiplicación" to 3),
                asistencia = listOf("2026-06-12", "2026-06-13", "2026-06-14", "2026-06-15"),
                recomendaciones = listOf(
                    "¡Increíble precisión del 85%!",
                    "Te sugiero practicar un poco más de Fracciones para dominarlo por completo.",
                    "¡Llevas una súper racha de 3 días practicando!"
                )
            )

            // 2. Parent Melisa Gutierrez (uid "parent_uid")
            mockUsuarios["parent_uid"] = AdultoFirebase(
                uid = "parent_uid",
                email = "parent@mathia.com",
                password = "123456",
                rol = "padre",
                nombre = "Melisa Gutierrez",
                perfil_completo = true,
                estudiante_pin = 1234
            )

            // 3. Teacher Efraín Caceres (uid "teacher_uid")
            mockUsuarios["teacher_uid"] = AdultoFirebase(
                uid = "teacher_uid",
                email = "teacher@mathia.com",
                password = "123456",
                rol = "docente",
                nombre = "Efraín Caceres",
                colegio = "Maria Auxiliadora",
                grado = "3ro de Primaria",
                seccion = "Sección A",
                perfil_completo = true
            )

            // Pre-populate mock observations
            mockObservaciones.add(
                Observacion(
                    id = "obs1",
                    docenteId = "teacher_uid",
                    docenteNombre = "Efraín Caceres",
                    texto = "Sofía muestra gran entusiasmo en las sumas y series. Excelente participación en clase.",
                    fecha = "2026-06-14",
                    timestamp = System.currentTimeMillis() - 24 * 3600000L
                )
            )

            // Pre-populate mock session reports
            mockReportes.add(
                ReporteSesion(
                    id = "rep1",
                    estudianteId = "1234",
                    estudianteNombre = "Sofía Pacheco",
                    grado = "3ro de Primaria",
                    seccion = "Sección A",
                    docenteId = "teacher_uid",
                    padreEmail = "parent@mathia.com",
                    tipo = "Práctica",
                    preguntasCount = 10,
                    correctas = 9,
                    incorrectas = 1,
                    tiempoPromedio = 8.5,
                    nivel = 1,
                    competenciasDominadas = listOf("Sumas", "Series"),
                    competenciasReforzar = listOf("Fracciones"),
                    recomendaciones = listOf("Sigue repasando fracciones."),
                    estrellasObtenidas = 95,
                    fecha = "2026-06-15",
                    timestamp = System.currentTimeMillis()
                )
            )

            // Pre-populate mock notifications
            mockNotificaciones.add(
                NotificacionFirebase(
                    id = "not1",
                    titulo = "Sofía completó una práctica",
                    cuerpo = "Sofía Pacheco resolvió 10 preguntas con un 90% de precisión.",
                    timestamp = System.currentTimeMillis(),
                    read = false
                )
            )
        }
    }

    private val _alumnoActual = MutableStateFlow<FirebaseStudent?>(null)
    val alumnoActual: StateFlow<FirebaseStudent?> = _alumnoActual

    private val _historialRespuestas = MutableStateFlow<List<RespuestaHistorial>>(emptyList())
    val historialRespuestas: StateFlow<List<RespuestaHistorial>> = _historialRespuestas

    private val _observaciones = MutableStateFlow<List<Observacion>>(emptyList())
    val observaciones: StateFlow<List<Observacion>> = _observaciones

    private val _reportesSesiones = MutableStateFlow<List<ReporteSesion>>(emptyList())
    val reportesSesiones: StateFlow<List<ReporteSesion>> = _reportesSesiones

    private val _notificaciones = MutableStateFlow<List<NotificacionFirebase>>(emptyList())
    val notificaciones: StateFlow<List<NotificacionFirebase>> = _notificaciones

    // ============ STUDENT AUTHENTICATION ============

    fun login(pin: String, onResult: (FirebaseStudent?) -> Unit) {
        if (isDemoMode) {
            val alumno = mockUsuarios[pin] as? FirebaseStudent
            _alumnoActual.value = alumno
            onResult(alumno)
            return
        }
        viewModelScope.launch {
            val alumno = repository.obtenerAlumnoPorPin(pin)
            _alumnoActual.value = alumno
            
            if (alumno != null) {
                // Trigger attendance calendar check-in and streak calculations
                val (newStreak, updatedAsistencia) = repository.registrarAsistenciaYRacha(pin)
                val updatedAlumno = alumno.copy(
                    streak = newStreak,
                    asistencia = updatedAsistencia
                )
                _alumnoActual.value = updatedAlumno
                
                // Regenerate AI recommendations on login to make sure they are fresh
                regenerarRecomendaciones(pin, updatedAlumno)
                onResult(updatedAlumno)
            } else {
                onResult(null)
            }
        }
    }

    fun guardarToken(pin: String, token: String) {
        Firebase.firestore
            .collection("usuarios")
            .document(pin)
            .update(mapOf("fcm_token" to token))
    }

    // ============ PROGRESS & GAMIFICATION UPDATES ============

    fun registrarActividadJuego(pin: String, starsEarned: Int, xpEarned: Int, tema: String, correcta: Boolean, respuestaTiempo: Long) {
        if (isDemoMode) {
            val current = mockUsuarios[pin] as? FirebaseStudent
            if (current != null) {
                val nextXP = current.xp + xpEarned
                val nextLevel = (nextXP / 100) + 1
                val nextStars = current.estrellas + starsEarned
                
                // Update skill
                val skillsMap = current.skills.toMutableMap()
                val currentSkill = skillsMap[tema] ?: 0
                val inc = if (correcta) 5 else 0
                skillsMap[tema] = (currentSkill + inc).coerceAtMost(100)
                
                // Update incorrectas
                val incorrectasMap = current.incorrectas_por_tema.toMutableMap()
                if (!correcta) {
                    incorrectasMap[tema] = (incorrectasMap[tema] ?: 0) + 1
                }
                
                // Increment questions counter
                val totalPreguntas = current.total_preguntas + 1
                val correctasCount = current.correctas + (if (correcta) 1 else 0)
                val incorrectasCount = current.incorrectas + (if (correcta) 0 else 1)
                val precision = (correctasCount * 100.0 / totalPreguntas)
                
                val updated = current.copy(
                    xp = nextXP,
                    nivel_actual = nextLevel,
                    estrellas = nextStars,
                    skills = skillsMap,
                    incorrectas_por_tema = incorrectasMap,
                    total_preguntas = totalPreguntas,
                    correctas = correctasCount,
                    incorrectas = incorrectasCount,
                    precision = precision
                )
                mockUsuarios[pin] = updated
                _alumnoActual.value = updated
                
                // Simulation of generating recommendations
                val recs = RecommendationEngine.generarRecomendaciones(updated)
                val updatedWithRecs = updated.copy(recomendaciones = recs)
                mockUsuarios[pin] = updatedWithRecs
                _alumnoActual.value = updatedWithRecs
            }
            return
        }
        viewModelScope.launch {
            val current = _alumnoActual.value ?: repository.obtenerAlumnoPorPin(pin)
            if (current != null) {
                // 1. Update stars and XP (handles level-up internally)
                repository.actualizarEstrellasYXP(pin, starsEarned, xpGained = xpEarned)
                
                // 2. Calculate updated skill score based on current level and topic
                val currentSkillScore = current.skills[tema] ?: 0
                val inc = if (correcta) 5 else 0
                val newSkillScore = (currentSkillScore + inc).coerceAtMost(100)
                
                // 3. Update skills progress and error counts
                repository.actualizarHabilidadesYErrores(pin, tema, correcta, newSkillScore)
                
                // 4. Update daily and weekly missions progress
                repository.actualizarMisiones(pin, dailyInc = 1, weeklyInc = 1)
                
                // Fetch the updated student profile to get fresh values
                val updated = repository.obtenerAlumnoPorPin(pin)
                if (updated != null) {
                    _alumnoActual.value = updated
                    // Regenerate AI recommendations with new stats
                    regenerarRecomendaciones(pin, updated)
                }
            }
        }
    }

    fun regenerarRecomendaciones(pin: String, student: FirebaseStudent) {
        viewModelScope.launch {
            val recs = RecommendationEngine.generarRecomendaciones(student)
            repository.guardarRecomendaciones(pin, recs)
            
            // Sync current state
            if (_alumnoActual.value?.pin == student.pin) {
                _alumnoActual.value = _alumnoActual.value?.copy(recomendaciones = recs)
            }
        }
    }

    fun actualizarEstrellas(pin: String, estrellas: Int) {
        viewModelScope.launch {
            repository.actualizarEstrellasYXP(pin, estrellas, xpGained = estrellas) // grant stars and XP equally
            val updated = repository.obtenerAlumnoPorPin(pin)
            if (updated != null) {
                _alumnoActual.value = updated
                regenerarRecomendaciones(pin, updated)
            }
        }
    }

    fun actualizarNivel(pin: String, nivel: Int) {
        viewModelScope.launch {
            Firebase.firestore.collection("usuarios").document(pin).update("nivel_actual", nivel).await()
            val updated = repository.obtenerAlumnoPorPin(pin)
            if (updated != null) {
                _alumnoActual.value = updated
                regenerarRecomendaciones(pin, updated)
            }
        }
    }

    fun actualizarPrecision(pin: String, precision: Double) {
        viewModelScope.launch {
            Firebase.firestore.collection("usuarios").document(pin).update("precision", precision).await()
            val updated = repository.obtenerAlumnoPorPin(pin)
            if (updated != null) {
                _alumnoActual.value = updated
                regenerarRecomendaciones(pin, updated)
            }
        }
    }

    fun guardarDiagnostico(
        pin: String,
        correctas: Int,
        incorrectas: Int,
        tiempoTotal: Long,
        tiempoPromedio: Double
    ) {
        if (isDemoMode) {
            val current = mockUsuarios[pin] as? FirebaseStudent
            if (current != null) {
                val updated = current.copy(
                    diagnostico_realizado = true,
                    correctas = correctas,
                    incorrectas = incorrectas,
                    tiempo_total = tiempoTotal,
                    tiempo_promedio = tiempoPromedio
                )
                mockUsuarios[pin] = updated
                _alumnoActual.value = updated
                regenerarRecomendaciones(pin, updated)
            }
            return
        }
        viewModelScope.launch {
            repository.guardarDiagnostico(pin, correctas, incorrectas, tiempoTotal, tiempoPromedio)
            val updated = repository.obtenerAlumnoPorPin(pin)
            if (updated != null) {
                _alumnoActual.value = updated
                regenerarRecomendaciones(pin, updated)
            }
        }
    }

    // ============ REPOSITORY WRAPPERS ============

    fun obtenerTodosAlumnos(onResult: (List<FirebaseStudent>) -> Unit) {
        if (isDemoMode) {
            val alumnos = mockUsuarios.values.filterIsInstance<FirebaseStudent>()
            onResult(alumnos)
            return
        }
        viewModelScope.launch {
            val alumnos = repository.obtenerTodosAlumnos()
            onResult(alumnos)
        }
    }

    fun crearAlumno(alumno: Map<String, Any>, pin: String, onResult: (Boolean) -> Unit) {
        if (isDemoMode) {
            val pinInt = pin.toIntOrNull() ?: 1234
            val nombre = alumno["nombre"] as? String ?: ""
            val grado = alumno["grado"] as? String ?: ""
            val seccion = alumno["seccion"] as? String ?: ""
            val edad = alumno["edad"] as? Int ?: 6
            val colegio = alumno["colegio"] as? String ?: ""
            val avatar = alumno["avatar"] as? String ?: "default"
            val email = alumno["padre_email"] as? String ?: ""

            val nuevo = FirebaseStudent(
                nombre = nombre,
                grado = grado,
                seccion = seccion,
                edad = edad,
                colegio = colegio,
                avatar = avatar,
                pin = pinInt,
                padre_email = email,
                nivel_actual = 1,
                fecha_creacion = System.currentTimeMillis()
            )
            mockUsuarios[pin] = nuevo
            onResult(true)
            return
        }
        viewModelScope.launch {
            val success = repository.crearAlumno(alumno, pin)
            onResult(success)
        }
    }

    fun obtenerEstudiantesPorEmailPadre(email: String, onResult: (List<FirebaseStudent>) -> Unit) {
        if (isDemoMode) {
            val list = mockUsuarios.values.filterIsInstance<FirebaseStudent>().filter { it.padre_email == email }
            onResult(list)
            return
        }
        viewModelScope.launch {
            val alumnos = repository.obtenerEstudiantesPorEmailPadre(email)
            onResult(alumnos)
        }
    }

    fun actualizarPadreEmail(pin: String, padreEmail: String, onResult: (Boolean) -> Unit) {
        if (isDemoMode) {
            val current = mockUsuarios[pin] as? FirebaseStudent
            if (current != null) {
                mockUsuarios[pin] = current.copy(padre_email = padreEmail)
            }
            onResult(true)
            return
        }
        viewModelScope.launch {
            Firebase.firestore.collection("usuarios").document(pin).update("padre_email", padreEmail).await()
            onResult(true)
        }
    }

    fun actualizarCosmeticos(pin: String, avatar: String, theme: String, onResult: (Boolean) -> Unit) {
        if (isDemoMode) {
            val current = mockUsuarios[pin] as? FirebaseStudent
            if (current != null) {
                val updated = current.copy(avatar = avatar, equipped_theme = theme)
                mockUsuarios[pin] = updated
                if (_alumnoActual.value?.pin?.toString() == pin) {
                    _alumnoActual.value = updated
                }
            }
            onResult(true)
            return
        }
        viewModelScope.launch {
            val success = repository.actualizarCosmeticos(pin, avatar, theme)
            if (success) {
                val current = _alumnoActual.value
                if (current != null && current.pin.toString() == pin) {
                    _alumnoActual.value = current.copy(avatar = avatar, equipped_theme = theme)
                }
            }
            onResult(success)
        }
    }

    fun comprarCosmetico(pin: String, nuevoCosmetico: String, esAvatar: Boolean, costo: Int, onResult: (Boolean) -> Unit) {
        if (isDemoMode) {
            val current = mockUsuarios[pin] as? FirebaseStudent
            if (current != null) {
                val nextStars = (current.estrellas - costo).coerceAtLeast(0)
                val updated = if (esAvatar) {
                    current.copy(
                        estrellas = nextStars,
                        unlocked_avatars = current.unlocked_avatars + nuevoCosmetico
                    )
                } else {
                    current.copy(
                        estrellas = nextStars,
                        unlocked_themes = current.unlocked_themes + nuevoCosmetico
                    )
                }
                mockUsuarios[pin] = updated
                if (_alumnoActual.value?.pin?.toString() == pin) {
                    _alumnoActual.value = updated
                }
            }
            onResult(true)
            return
        }
        viewModelScope.launch {
            val success = repository.comprarCosmetico(pin, nuevoCosmetico, esAvatar, costo)
            if (success) {
                val updated = repository.obtenerAlumnoPorPin(pin)
                if (updated != null) {
                    _alumnoActual.value = updated
                }
            }
            onResult(success)
        }
    }

    // ============ GOOGLE AUTHENTICATION & PROFILE ============

    fun loginConGoogle(idToken: String, email: String, rol: String, onResult: (Boolean, Boolean) -> Unit) {
        if (isDemoMode) {
            val found = mockUsuarios.values.filterIsInstance<AdultoFirebase>().firstOrNull { it.email == email }
            if (found != null) {
                onResult(true, found.perfil_completo)
            } else {
                val uid = "google_${email.hashCode()}"
                val nuevoAdulto = AdultoFirebase(
                    uid = uid,
                    email = email,
                    rol = if (rol == "docente") "docente" else "padre",
                    nombre = email.substringBefore("@"),
                    perfil_completo = false
                )
                mockUsuarios[uid] = nuevoAdulto
                onResult(true, false)
            }
            return
        }
        viewModelScope.launch {
            val (success, isComplete) = repository.loginConGoogle(idToken, email, rol)
            onResult(success, isComplete)
        }
    }

    fun actualizarPerfilDocente(
        uid: String,
        nombre: String,
        colegio: String,
        grado: String,
        seccion: String,
        onResult: (Boolean) -> Unit
    ) {
        if (isDemoMode) {
            val current = mockUsuarios[uid] as? AdultoFirebase
            if (current != null) {
                mockUsuarios[uid] = current.copy(
                    nombre = nombre,
                    colegio = colegio,
                    grado = grado,
                    seccion = seccion,
                    perfil_completo = true
                )
            } else {
                mockUsuarios[uid] = AdultoFirebase(
                    uid = uid,
                    nombre = nombre,
                    colegio = colegio,
                    grado = grado,
                    seccion = seccion,
                    rol = "docente",
                    perfil_completo = true
                )
            }
            onResult(true)
            return
        }
        viewModelScope.launch {
            val success = repository.actualizarPerfilDocente(uid, nombre, colegio, grado, seccion)
            onResult(success)
        }
    }

    // ============ NOTIFICATIONS ============

    fun enviarNotificacionExamen(pin: String, correctas: Int, precision: Int) {
        if (isDemoMode) {
            mockNotificaciones.add(
                NotificacionFirebase(
                    id = "not_${System.currentTimeMillis()}",
                    titulo = "Examen completado",
                    cuerpo = "¡Puntaje: $correctas respuestas correctas | Precisión: $precision%!",
                    timestamp = System.currentTimeMillis(),
                    read = false
                )
            )
            return
        }
        Firebase.firestore
            .collection("notifications")
            .add(
                hashMapOf(
                    "studentPin" to pin,
                    "title" to "Examen completado",
                    "body" to "¡Puntaje: $correctas respuestas correctas | Precisión: $precision%!",
                    "timestamp" to System.currentTimeMillis(),
                    "read" to false,
                    "tipo" to "alumno"
                )
            )
    }

    fun notificarPadre(pin: String, nombreAlumno: String, precision: Int) {
        if (isDemoMode) {
            mockNotificaciones.add(
                NotificacionFirebase(
                    id = "not_${System.currentTimeMillis()}",
                    titulo = "Nuevo progreso disponible",
                    cuerpo = "$nombreAlumno completó un examen con un $precision% de precisión.",
                    timestamp = System.currentTimeMillis(),
                    read = false
                )
            )
            return
        }
        Firebase.firestore
            .collection("notifications")
            .add(
                hashMapOf(
                    "studentPin" to pin,
                    "title" to "Nuevo progreso disponible",
                    "body" to "$nombreAlumno completó un examen con un $precision% de precisión.",
                    "timestamp" to System.currentTimeMillis(),
                    "read" to false,
                    "tipo" to "padre"
                )
            )
    }

    fun notificarDocente(pin: String, nombreAlumno: String, precision: Int) {
        if (isDemoMode) {
            mockNotificaciones.add(
                NotificacionFirebase(
                    id = "not_${System.currentTimeMillis()}",
                    titulo = "Evaluación completada en clase",
                    cuerpo = "$nombreAlumno terminó su examen con $precision%.",
                    timestamp = System.currentTimeMillis(),
                    read = false
                )
            )
            return
        }
        Firebase.firestore
            .collection("notifications")
            .add(
                hashMapOf(
                    "studentPin" to pin,
                    "title" to "Evaluación completada en clase",
                    "body" to "$nombreAlumno terminó su examen con $precision%.",
                    "timestamp" to System.currentTimeMillis(),
                    "read" to false,
                    "tipo" to "docente"
                )
            )
    }

    fun crearRecordatorioExamen(pin: String) {
        if (isDemoMode) {
            mockNotificaciones.add(
                NotificacionFirebase(
                    id = "not_${System.currentTimeMillis()}",
                    titulo = "Examen pendiente",
                    cuerpo = "Aún no has completado tu examen adaptativo. ¡Inténtalo hoy!",
                    timestamp = System.currentTimeMillis(),
                    read = false
                )
            )
            return
        }
        Firebase.firestore
            .collection("notifications")
            .add(
                hashMapOf(
                    "studentPin" to pin,
                    "title" to "Examen pendiente",
                    "body" to "Aún no has completado tu examen adaptativo. ¡Inténtalo hoy!",
                    "timestamp" to System.currentTimeMillis(),
                    "read" to false,
                    "tipo" to "recordatorio"
                )
            )
    }

    // ============ PLATFORM IMPROVEMENT VIEWMODEL METHODS ============

    fun registrarRespuestaHistorial(res: RespuestaHistorial, onResult: (Boolean) -> Unit = {}) {
        if (isDemoMode) {
            val nuevoLog = res.copy(id = "log_${System.currentTimeMillis()}")
            mockHistorial.add(nuevoLog)
            if (res.estudianteId == _alumnoActual.value?.pin?.toString()) {
                obtenerHistorialRespuestas(res.estudianteId)
            }
            onResult(true)
            return
        }
        viewModelScope.launch {
            val success = repository.registrarRespuestaHistorial(res)
            if (success) {
                if (res.estudianteId == _alumnoActual.value?.pin?.toString()) {
                    obtenerHistorialRespuestas(res.estudianteId)
                }
            }
            onResult(success)
        }
    }

    fun obtenerHistorialRespuestas(pin: String) {
        if (isDemoMode) {
            val lista = mockHistorial.filter { it.estudianteId == pin }
            _historialRespuestas.value = lista
            return
        }
        viewModelScope.launch {
            val lista = repository.obtenerHistorialRespuestas(pin)
            _historialRespuestas.value = lista
        }
    }

    fun guardarReporteSesion(reporte: ReporteSesion, onResult: (Boolean) -> Unit = {}) {
        if (isDemoMode) {
            val nuevoReporte = reporte.copy(id = "rep_${System.currentTimeMillis()}")
            mockReportes.add(nuevoReporte)
            if (reporte.docenteId.isNotEmpty()) {
                obtenerReportesSesionPorDocente(reporte.docenteId)
            }
            if (reporte.padreEmail.isNotEmpty()) {
                obtenerReportesSesionPorPadre(reporte.padreEmail)
            }
            onResult(true)
            return
        }
        viewModelScope.launch {
            val success = repository.guardarReporteSesion(reporte)
            if (success) {
                if (reporte.docenteId.isNotEmpty()) {
                    obtenerReportesSesionPorDocente(reporte.docenteId)
                }
                if (reporte.padreEmail.isNotEmpty()) {
                    obtenerReportesSesionPorPadre(reporte.padreEmail)
                }
            }
            onResult(success)
        }
    }

    fun obtenerReportesSesionPorDocente(docenteId: String) {
        if (isDemoMode) {
            val teacher = mockUsuarios[docenteId] as? AdultoFirebase
            val reportsList = if (teacher != null) {
                mockReportes.filter { it.grado == teacher.grado && it.seccion == teacher.seccion }
            } else {
                mockReportes
            }
            _reportesSesiones.value = reportsList
            return
        }
        viewModelScope.launch {
            val lista = repository.obtenerReportesSesionPorDocente(docenteId)
            _reportesSesiones.value = lista
        }
    }

    fun obtenerReportesSesionPorPadre(padreEmail: String) {
        if (isDemoMode) {
            val lista = mockReportes.filter { it.padreEmail == padreEmail }
            _reportesSesiones.value = lista
            return
        }
        viewModelScope.launch {
            val lista = repository.obtenerReportesSesionPorPadre(padreEmail)
            _reportesSesiones.value = lista
        }
    }

    fun guardarObservacion(pin: String, obs: Observacion, onResult: (Boolean) -> Unit = {}) {
        if (isDemoMode) {
            val nuevaObs = obs.copy(id = "obs_${System.currentTimeMillis()}")
            mockObservaciones.add(nuevaObs)
            obtenerObservaciones(pin)
            onResult(true)
            return
        }
        viewModelScope.launch {
            val success = repository.guardarObservacion(pin, obs)
            if (success) {
                obtenerObservaciones(pin)
            }
            onResult(success)
        }
    }

    fun obtenerObservaciones(pin: String) {
        if (isDemoMode) {
            val lista = mockObservaciones
            _observaciones.value = lista
            return
        }
        viewModelScope.launch {
            val lista = repository.obtenerObservaciones(pin)
            _observaciones.value = lista
        }
    }

    fun guardarNotificacion(uid: String, not: NotificacionFirebase, onResult: (Boolean) -> Unit = {}) {
        if (isDemoMode) {
            val nuevaNot = not.copy(id = "not_${System.currentTimeMillis()}")
            mockNotificaciones.add(nuevaNot)
            obtenerNotificaciones(uid)
            onResult(true)
            return
        }
        viewModelScope.launch {
            val success = repository.guardarNotificacion(uid, not)
            if (success) {
                obtenerNotificaciones(uid)
            }
            onResult(success)
        }
    }

    fun obtenerNotificaciones(uid: String) {
        if (isDemoMode) {
            _notificaciones.value = mockNotificaciones
            return
        }
        viewModelScope.launch {
            val lista = repository.obtenerNotificaciones(uid)
            _notificaciones.value = lista
        }
    }

    fun obtenerAdultoPorUid(uid: String, onResult: (AdultoFirebase?) -> Unit) {
        if (isDemoMode) {
            val adulto = mockUsuarios[uid] as? AdultoFirebase
            onResult(adulto)
            return
        }
        viewModelScope.launch {
            val adulto = repository.obtenerAdultoPorUid(uid)
            onResult(adulto)
        }
    }

    fun actualizarPerfilPadre(uid: String, nombre: String, pinHijo: Int, onResult: (Boolean) -> Unit) {
        if (isDemoMode) {
            val current = mockUsuarios[uid] as? AdultoFirebase
            if (current != null) {
                mockUsuarios[uid] = current.copy(
                    nombre = nombre,
                    estudiante_pin = pinHijo,
                    perfil_completo = true
                )
            } else {
                mockUsuarios[uid] = AdultoFirebase(
                    uid = uid,
                    nombre = nombre,
                    estudiante_pin = pinHijo,
                    rol = "padre",
                    perfil_completo = true
                )
            }
            onResult(true)
            return
        }
        viewModelScope.launch {
            val success = repository.actualizarPerfilPadre(uid, nombre, pinHijo)
            onResult(success)
        }
    }
}