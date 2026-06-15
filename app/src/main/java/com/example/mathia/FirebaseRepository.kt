package com.example.mathia

import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.FieldValue
import com.google.firebase.ktx.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.tasks.await
import com.example.mathia.model.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class FirebaseRepository {

    private val db = Firebase.firestore

    init {
        // Objective 7: Enable local caching in Firestore
        try {
            val settings = FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build()
            db.firestoreSettings = settings
            println("✅ Firestore local cache enabled successfully.")
        } catch (e: Exception) {
            println("⚠️ Error setting Firestore settings: ${e.message}")
        }
    }

    // ============ STUDENT OPERATIONS ============

    suspend fun obtenerAlumnoPorPin(pin: String): FirebaseStudent? {
        return try {
            val resultado = db.collection("usuarios")
                .whereEqualTo("pin", pin.toIntOrNull() ?: return null)
                .get()
                .await()

            if (resultado.documents.isEmpty()) return null
            resultado.documents.first().toObject(FirebaseStudent::class.java)
        } catch (e: Exception) {
            println("❌ Error obteniendo alumno por PIN: ${e.message}")
            null
        }
    }

    suspend fun crearAlumno(alumno: Map<String, Any>, pin: String): Boolean {
        return try {
            db.collection("usuarios")
                .document(pin)
                .set(alumno)
                .await()
            println("✅ Alumno creado con PIN: $pin")
            true
        } catch (e: Exception) {
            println("❌ Error creando alumno: ${e.message}")
            false
        }
    }

    suspend fun obtenerTodosAlumnos(): List<FirebaseStudent> {
        return try {
            val resultado = db.collection("usuarios")
                .whereNotEqualTo("pin", null) // Fetch only students, who have pin fields
                .get()
                .await()
            resultado.documents.mapNotNull { it.toObject(FirebaseStudent::class.java) }
        } catch (e: Exception) {
            println("❌ Error obteniendo todos los alumnos: ${e.message}")
            emptyList()
        }
    }

    suspend fun obtenerEstudiantesPorEmailPadre(email: String): List<FirebaseStudent> {
        return try {
            val resultado = db.collection("usuarios")
                .whereEqualTo("padre_email", email)
                .get()
                .await()
            resultado.documents.mapNotNull { it.toObject(FirebaseStudent::class.java) }
        } catch (e: Exception) {
            println("❌ Error obteniendo estudiantes por email padre: ${e.message}")
            emptyList()
        }
    }

    suspend fun registrarAsistenciaYRacha(pin: String): Pair<Int, List<String>> {
        try {
            val resultado = db.collection("usuarios")
                .whereEqualTo("pin", pin.toIntOrNull() ?: return Pair(0, emptyList()))
                .get()
                .await()

            if (resultado.documents.isNotEmpty()) {
                val doc = resultado.documents.first()
                val currentStreak = doc.getLong("streak")?.toInt() ?: 0
                @Suppress("UNCHECKED_CAST")
                val currentAsistencia = doc.get("asistencia") as? List<String> ?: emptyList()

                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val todayStr = dateFormat.format(Date())

                if (currentAsistencia.contains(todayStr)) {
                    // Already logged in today, streak remains unchanged
                    return Pair(currentStreak, currentAsistencia)
                }

                val updatedAsistencia = currentAsistencia.toMutableList().apply { add(todayStr) }
                var newStreak = currentStreak

                if (currentAsistencia.isNotEmpty()) {
                    val lastDateStr = currentAsistencia.last()
                    val lastDate = dateFormat.parse(lastDateStr)
                    val todayDate = dateFormat.parse(todayStr)
                    if (lastDate != null && todayDate != null) {
                        val diff = todayDate.time - lastDate.time
                        val diffDays = diff / (1000 * 60 * 60 * 24)
                        if (diffDays == 1L) {
                            // Converted yesterday
                            newStreak++
                        } else if (diffDays > 1L) {
                            // Missed days, reset streak to 1
                            newStreak = 1
                        }
                    }
                } else {
                    newStreak = 1
                }

                doc.reference.update(
                    mapOf(
                        "asistencia" to updatedAsistencia,
                        "streak" to newStreak
                    )
                ).await()

                return Pair(newStreak, updatedAsistencia)
            }
        } catch (e: Exception) {
            println("❌ Error registrando asistencia y racha: ${e.message}")
        }
        return Pair(0, emptyList())
    }

    suspend fun actualizarEstrellasYXP(pin: String, estrellasGained: Int, xpGained: Int) {
        try {
            val resultado = db.collection("usuarios")
                .whereEqualTo("pin", pin.toIntOrNull() ?: return)
                .get()
                .await()

            if (resultado.documents.isNotEmpty()) {
                val doc = resultado.documents.first()
                val currentEstrellas = doc.getLong("estrellas")?.toInt() ?: 0
                val currentXP = doc.getLong("xp")?.toInt() ?: 0
                val currentLevel = doc.getLong("nivel_actual")?.toInt() ?: 1

                val newEstrellas = currentEstrellas + estrellasGained
                val newXP = currentXP + xpGained
                val newLevel = (newXP / 100) + 1 // 100 XP per level

                val updateMap = mutableMapOf<String, Any>(
                    "estrellas" to newEstrellas,
                    "xp" to newXP,
                    "nivel_actual" to newLevel
                )

                doc.reference.update(updateMap).await()
                println("✅ Estrellas y XP actualizadas: estrellas=$newEstrellas, xp=$newXP, nivel=$newLevel")
            }
        } catch (e: Exception) {
            println("❌ Error actualizando estrellas y XP: ${e.message}")
        }
    }

    suspend fun actualizarHabilidadesYErrores(
        pin: String,
        tema: String,
        correcta: Boolean,
        nuevaHabilidadScore: Int
    ) {
        try {
            val resultado = db.collection("usuarios")
                .whereEqualTo("pin", pin.toIntOrNull() ?: return)
                .get()
                .await()

            if (resultado.documents.isNotEmpty()) {
                val doc = resultado.documents.first()
                @Suppress("UNCHECKED_CAST")
                val currentSkills = doc.get("skills") as? Map<String, Long> ?: emptyMap()
                @Suppress("UNCHECKED_CAST")
                val currentErrors = doc.get("incorrectas_por_tema") as? Map<String, Long> ?: emptyMap()
                
                val currentCorrectas = doc.getLong("correctas")?.toInt() ?: 0
                val currentIncorrectas = doc.getLong("incorrectas")?.toInt() ?: 0
                val currentTotal = doc.getLong("total_preguntas")?.toInt() ?: 0

                val updatedSkills = currentSkills.toMutableMap()
                updatedSkills[tema] = nuevaHabilidadScore.toLong()

                val updatedErrors = currentErrors.toMutableMap()
                if (!correcta) {
                    val count = currentErrors[tema] ?: 0L
                    updatedErrors[tema] = count + 1L
                }

                val nextTotal = currentTotal + 1
                val nextCorrectas = if (correcta) currentCorrectas + 1 else currentCorrectas
                val nextIncorrectas = if (!correcta) currentIncorrectas + 1 else currentIncorrectas
                val nextPrecision = if (nextTotal > 0) (nextCorrectas.toDouble() / nextTotal) * 100.0 else 0.0

                doc.reference.update(
                    mapOf(
                        "skills" to updatedSkills,
                        "incorrectas_por_tema" to updatedErrors,
                        "total_preguntas" to nextTotal,
                        "correctas" to nextCorrectas,
                        "incorrectas" to nextIncorrectas,
                        "precision" to nextPrecision
                    )
                ).await()
            }
        } catch (e: Exception) {
            println("❌ Error actualizando habilidades y errores: ${e.message}")
        }
    }

    suspend fun actualizarMisiones(pin: String, dailyInc: Int, weeklyInc: Int) {
        try {
            val resultado = db.collection("usuarios")
                .whereEqualTo("pin", pin.toIntOrNull() ?: return)
                .get()
                .await()

            if (resultado.documents.isNotEmpty()) {
                val doc = resultado.documents.first()
                val currentDaily = doc.getLong("daily_mission_progress")?.toInt() ?: 0
                val currentWeekly = doc.getLong("weekly_mission_progress")?.toInt() ?: 0

                doc.reference.update(
                    mapOf(
                        "daily_mission_progress" to currentDaily + dailyInc,
                        "weekly_mission_progress" to currentWeekly + weeklyInc
                    )
                ).await()
            }
        } catch (e: Exception) {
            println("❌ Error actualizando misiones: ${e.message}")
        }
    }

    suspend fun guardarRecomendaciones(pin: String, recomendaciones: List<String>) {
        try {
            val resultado = db.collection("usuarios")
                .whereEqualTo("pin", pin.toIntOrNull() ?: return)
                .get()
                .await()

            if (resultado.documents.isNotEmpty()) {
                resultado.documents.first().reference
                    .update("recomendaciones", recomendaciones)
                    .await()
            }
        } catch (e: Exception) {
            println("❌ Error guardando recomendaciones: ${e.message}")
        }
    }

    suspend fun guardarDiagnostico(
        pin: String,
        correctas: Int,
        incorrectas: Int,
        tiempoTotal: Long,
        tiempoPromedio: Double
    ) {
        try {
            val resultado = db.collection("usuarios")
                .whereEqualTo("pin", pin.toIntOrNull() ?: return)
                .get()
                .await()

            if (resultado.documents.isNotEmpty()) {
                val doc = resultado.documents.first()
                val curCorrectas = doc.getLong("correctas")?.toInt() ?: 0
                val curIncorrectas = doc.getLong("incorrectas")?.toInt() ?: 0
                val curTotal = doc.getLong("total_preguntas")?.toInt() ?: 0
                val curTime = doc.getLong("tiempo_total")?.toLong() ?: 0L

                val nextCorrectas = curCorrectas + correctas
                val nextIncorrectas = curIncorrectas + incorrectas
                val nextTotal = curTotal + correctas + incorrectas
                val nextTime = curTime + tiempoTotal
                val nextAvgTime = if (nextTotal > 0) nextTime.toDouble() / nextTotal else tiempoPromedio
                val nextPrecision = if (nextTotal > 0) (nextCorrectas.toDouble() / nextTotal) * 100.0 else 0.0

                doc.reference.update(
                    mapOf(
                        "diagnostico_realizado" to true,
                        "total_preguntas" to nextTotal,
                        "correctas" to nextCorrectas,
                        "incorrectas" to nextIncorrectas,
                        "tiempo_total" to nextTime,
                        "tiempo_promedio" to nextAvgTime,
                        "precision" to nextPrecision,
                        "fecha_diagnostico" to FieldValue.serverTimestamp()
                    )
                ).await()
            }
        } catch (e: Exception) {
            println("❌ Error en guardarDiagnostico: ${e.message}")
        }
    }

    suspend fun comprarCosmetico(
        pin: String,
        nuevoCosmetico: String,
        esAvatar: Boolean,
        costo: Int
    ): Boolean {
        return try {
            val resultado = db.collection("usuarios")
                .whereEqualTo("pin", pin.toIntOrNull() ?: return false)
                .get()
                .await()

            if (resultado.documents.isNotEmpty()) {
                val doc = resultado.documents.first()
                val estrellasActuales = doc.getLong("estrellas") ?: 0
                if (estrellasActuales < costo) return false

                val campo = if (esAvatar) "unlocked_avatars" else "unlocked_themes"
                @Suppress("UNCHECKED_CAST")
                val listaActual = doc.get(campo) as? List<String> ?: emptyList()
                val listaActualizada = listaActual.toMutableList().apply {
                    if (!contains(nuevoCosmetico)) add(nuevoCosmetico)
                }

                doc.reference.update(
                    mapOf(
                        "estrellas" to (estrellasActuales - costo),
                        campo to listaActualizada
                    )
                ).await()
                true
            } else {
                false
            }
        } catch (e: Exception) {
            println("❌ Error comprando cosmético: ${e.message}")
            false
        }
    }

    suspend fun actualizarCosmeticos(pin: String, avatar: String, theme: String): Boolean {
        return try {
            val resultado = db.collection("usuarios")
                .whereEqualTo("pin", pin.toIntOrNull() ?: return false)
                .get()
                .await()

            if (resultado.documents.isNotEmpty()) {
                resultado.documents.first().reference.update(
                    mapOf(
                        "avatar" to avatar,
                        "equipped_theme" to theme
                    )
                ).await()
                true
            } else {
                false
            }
        } catch (e: Exception) {
            println("❌ Error actualizando cosméticos: ${e.message}")
            false
        }
    }

    // ============ ADULT OPERATIONS ============

    suspend fun loginConGoogle(idToken: String, email: String, rol: String): Pair<Boolean, Boolean> {
        return try {
            val auth = FirebaseAuth.getInstance()
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val authResult = auth.signInWithCredential(credential).await()
            val firebaseUser = authResult.user ?: return Pair(false, false)
            val uid = firebaseUser.uid

            val docRef = db.collection("usuarios").document(uid)
            val doc = docRef.get().await()
            if (doc.exists()) {
                val dbRole = doc.getString("rol") ?: ""
                val expectedRole = if (rol == "padres" || rol == "padre") "padre" else "docente"
                if (dbRole != expectedRole) {
                    println("❌ Error: Conflicto de roles. Esperado: $expectedRole, Encontrado: $dbRole")
                    return Pair(false, false)
                }
                val isComplete = doc.getBoolean("perfil_completo") ?: false
                Pair(true, isComplete)
            } else {
                val rolNormalizado = if (rol == "padres" || rol == "padre") "padre" else "docente"
                val nuevoUsuario = hashMapOf(
                    "uid" to uid,
                    "email" to email,
                    "rol" to rolNormalizado,
                    "nombre" to (firebaseUser.displayName ?: email.substringBefore("@")),
                    "perfil_completo" to false
                )
                docRef.set(nuevoUsuario).await()
                Pair(true, false)
            }
        } catch (e: Exception) {
            println("❌ Error en loginConGoogle: ${e.message}")
            e.printStackTrace()
            Pair(false, false)
        }
    }

    suspend fun actualizarPerfilDocente(
        uid: String,
        nombre: String,
        colegio: String,
        grado: String,
        seccion: String
    ): Boolean {
        return try {
            db.collection("usuarios")
                .document(uid)
                .update(
                    mapOf(
                        "nombre" to nombre,
                        "colegio" to colegio,
                        "grado" to grado,
                        "seccion" to seccion,
                        "perfil_completo" to true
                    )
                )
                .await()
            true
        } catch (e: Exception) {
            println("❌ Error actualizando perfil docente: ${e.message}")
            false
        }
    }

    // ============ PLATFORM IMPROVEMENT OPERATIONS ============

    suspend fun registrarRespuestaHistorial(res: RespuestaHistorial): Boolean {
        return try {
            val docRef = db.collection("HistorialRespuestas")
                .document(res.estudianteId)
                .collection("respuestas")
                .document(res.id.ifEmpty { db.collection("HistorialRespuestas").document().id })
            
            val finalRes = res.copy(id = docRef.id)
            docRef.set(finalRes).await()
            true
        } catch (e: Exception) {
            println("❌ Error en registrarRespuestaHistorial: ${e.message}")
            false
        }
    }

    suspend fun obtenerHistorialRespuestas(pin: String): List<RespuestaHistorial> {
        return try {
            val resultado = db.collection("HistorialRespuestas")
                .document(pin)
                .collection("respuestas")
                .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .get()
                .await()
            resultado.documents.mapNotNull { it.toObject(RespuestaHistorial::class.java) }
        } catch (e: Exception) {
            println("❌ Error en obtenerHistorialRespuestas: ${e.message}")
            emptyList()
        }
    }

    suspend fun guardarReporteSesion(reporte: ReporteSesion): Boolean {
        return try {
            val docRef = db.collection("ReportesSesiones")
                .document(reporte.id.ifEmpty { db.collection("ReportesSesiones").document().id })
            val finalReporte = reporte.copy(id = docRef.id)
            docRef.set(finalReporte).await()
            true
        } catch (e: Exception) {
            println("❌ Error en guardarReporteSesion: ${e.message}")
            false
        }
    }

    suspend fun obtenerReportesSesionPorDocente(docenteId: String): List<ReporteSesion> {
        return try {
            val resultado = db.collection("ReportesSesiones")
                .whereEqualTo("docenteId", docenteId)
                .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .get()
                .await()
            resultado.documents.mapNotNull { it.toObject(ReporteSesion::class.java) }
        } catch (e: Exception) {
            println("❌ Error en obtenerReportesSesionPorDocente: ${e.message}")
            emptyList()
        }
    }

    suspend fun obtenerReportesSesionPorPadre(padreEmail: String): List<ReporteSesion> {
        return try {
            val resultado = db.collection("ReportesSesiones")
                .whereEqualTo("padreEmail", padreEmail)
                .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .get()
                .await()
            resultado.documents.mapNotNull { it.toObject(ReporteSesion::class.java) }
        } catch (e: Exception) {
            println("❌ Error en obtenerReportesSesionPorPadre: ${e.message}")
            emptyList()
        }
    }

    suspend fun guardarObservacion(pin: String, obs: Observacion): Boolean {
        return try {
            val docRef = db.collection("usuarios")
                .document(pin)
                .collection("observaciones")
                .document(obs.id.ifEmpty { db.collection("usuarios").document().id })
            val finalObs = obs.copy(id = docRef.id)
            docRef.set(finalObs).await()
            true
        } catch (e: Exception) {
            println("❌ Error en guardarObservacion: ${e.message}")
            false
        }
    }

    suspend fun obtenerObservaciones(pin: String): List<Observacion> {
        return try {
            val resultado = db.collection("usuarios")
                .document(pin)
                .collection("observaciones")
                .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .get()
                .await()
            resultado.documents.mapNotNull { it.toObject(Observacion::class.java) }
        } catch (e: Exception) {
            println("❌ Error en obtenerObservaciones: ${e.message}")
            emptyList()
        }
    }

    suspend fun guardarNotificacion(uid: String, not: NotificacionFirebase): Boolean {
        return try {
            val docRef = db.collection("usuarios")
                .document(uid)
                .collection("notificaciones")
                .document(not.id.ifEmpty { db.collection("usuarios").document().id })
            val finalNot = not.copy(id = docRef.id)
            docRef.set(finalNot).await()
            true
        } catch (e: Exception) {
            println("❌ Error en guardarNotificacion: ${e.message}")
            false
        }
    }

    suspend fun obtenerNotificaciones(uid: String): List<NotificacionFirebase> {
        return try {
            val resultado = db.collection("usuarios")
                .document(uid)
                .collection("notificaciones")
                .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .get()
                .await()
            resultado.documents.mapNotNull { it.toObject(NotificacionFirebase::class.java) }
        } catch (e: Exception) {
            println("❌ Error en obtenerNotificaciones: ${e.message}")
            emptyList()
        }
    }

    suspend fun obtenerAdultoPorUid(uid: String): AdultoFirebase? {
        return try {
            val doc = db.collection("usuarios").document(uid).get().await()
            if (doc.exists()) {
                doc.toObject(AdultoFirebase::class.java)
            } else {
                null
            }
        } catch (e: Exception) {
            println("❌ Error en obtenerAdultoPorUid: ${e.message}")
            null
        }
    }

    suspend fun actualizarPerfilPadre(uid: String, nombre: String, pinHijo: Int): Boolean {
        return try {
            db.collection("usuarios").document(uid).update(
                mapOf(
                    "nombre" to nombre,
                    "perfil_completo" to true,
                    "estudiante_pin" to pinHijo
                )
            ).await()
            true
        } catch (e: Exception) {
            println("❌ Error en actualizarPerfilPadre: ${e.message}")
            false
        }
    }
}