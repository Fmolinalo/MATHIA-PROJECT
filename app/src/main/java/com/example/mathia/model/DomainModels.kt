package com.example.mathia.model

// ─── Enums & Helper Models ─────────────────────────────────────────────────
enum class AlertType {
    STREAK,      // Orange theme (🔥)
    SUCCESS,     // Green theme (⭐)
    MOTIVATIONAL, // Purple theme (🧠)
    CHALLENGE    // Pink/Blue theme (🏆)
}

data class MathiaAlert(
    val title: String,
    val message: String,
    val type: AlertType,
    val buttonText: String = "¡Entendido!"
)

data class Reward(
    val id: Int,
    val name: String,
    val emoji: String,
    val stars: Int,
    val desc: String
)

data class ShopItem(
    val id: String,
    val name: String,
    val visual: String, // Emoji or representation
    val cost: Int,
    val isAvatar: Boolean
)

data class ExamResponse(
    val pregunta: String,
    val correcta: Boolean,
    val tiempo: Long,
    val dificultad: Int
)

data class Mission(
    val id: String,
    val description: String,
    val target: Int,
    val current: Int,
    val rewardStars: Int,
    val isWeekly: Boolean
)

// ─── Domain Student Model ──────────────────────────────────────────────────
data class Student(
    val id: Int,
    val name: String,
    val lastName: String,
    val grade: String,
    val classroom: String,
    val pin: String,
    val level: Int,
    val stars: Int,
    val xp: Int = 0,
    val totalQuestions: Int = 0,
    val correctAnswers: Int = 0,
    val examsCompleted: Int = 0,
    val accuracy: Int = 0,
    val streak: Int = 0,
    val weekData: List<Int> = listOf(0, 0, 0, 0, 0, 0, 0),
    val monthData: List<Int> = listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
    val skills: Map<String, Int> = emptyMap(),
    val incorrectasPorTema: Map<String, Int> = emptyMap(),
    val asistencia: List<String> = emptyList(),
    val recomendaciones: List<String> = emptyList(),
    val avatar: String = "👶",
    val diagnosticoRealizado: Boolean = false,
    val tiempoTotal: Long = 0,
    val tiempoPromedio: Double = 0.0,
    val equippedTheme: String = "Lila Clásico",
    val unlockedAvatars: List<String> = listOf("👶"),
    val unlockedThemes: List<String> = listOf("Lila Clásico"),
    val dailyMissionProgress: Int = 0,
    val weeklyMissionProgress: Int = 0,
    val edad: Int = 6,
    val colegio: String = "",
    val seccion: String = "",
    val docenteAsignado: String = "",
    val fechaCreacion: Long = 0L,
    val padreEmail: String = ""
)

// ─── Firebase Models ───────────────────────────────────────────────────────
data class FirebaseStudent(
    val nombre: String = "",
    val grado: String = "",
    val edad: Int = 6,
    val colegio: String = "",
    val seccion: String = "",
    val docente_asignado: String = "Sin asignar",
    val fecha_creacion: Long = 0L,
    val nivel_actual: Int = 1,
    val precision: Double = 0.0,
    val estrellas: Int = 0,
    val pin: Int = 0,
    val padre_email: String = "",
    val avatar: String = "👶",
    val equipped_theme: String = "Lila Clásico",
    val unlocked_avatars: List<String> = listOf("👶"),
    val unlocked_themes: List<String> = listOf("Lila Clásico"),
    
    // Gamification & AI tracking fields
    val xp: Int = 0,
    val streak: Int = 0,
    val total_preguntas: Int = 0,
    val correctas: Int = 0,
    val incorrectas: Int = 0,
    val tiempo_total: Long = 0,
    val tiempo_promedio: Double = 0.0,
    val diagnostico_realizado: Boolean = false,
    val weekData: List<Int> = listOf(0, 0, 0, 0, 0, 0, 0),
    val monthData: List<Int> = listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
    val skills: Map<String, Int> = emptyMap(),
    val incorrectas_por_tema: Map<String, Int> = emptyMap(),
    val asistencia: List<String> = emptyList(),
    val recomendaciones: List<String> = emptyList(),
    val daily_mission_progress: Int = 0,
    val weekly_mission_progress: Int = 0
)

data class AdultoFirebase(
    val uid: String = "",
    val email: String = "",
    val password: String = "",
    val rol: String = "", // "padre" or "docente"
    val nombre: String = "",
    val colegio: String = "",
    val grado: String = "",
    val seccion: String = "",
    val perfil_completo: Boolean = false,
    val estudiante_pin: Int? = null,
    val fechaRegistro: Long = 0
)

// ─── Platform Improvement Models ───────────────────────────────────────────
data class RespuestaHistorial(
    val id: String = "",
    val estudianteId: String = "",
    val estudianteNombre: String = "",
    val grado: String = "",
    val seccion: String = "",
    val docenteAsignado: String = "",
    val padreAsociado: String = "",
    val fecha: String = "",
    val hora: String = "",
    val timestamp: Long = 0L,
    val operacion: String = "",
    val pregunta: String = "",
    val respuestaCorrecta: String = "",
    val respuestaElegida: String = "",
    val tiempoRespuesta: Long = 0L,
    val dificultad: String = "",
    val correcta: Boolean = false,
    val intentos: Int = 1,
    val estrellasObtenidas: Int = 0,
    val experienciaGanada: Int = 0,
    val dispositivo: String = ""
)

data class ReporteSesion(
    val id: String = "",
    val estudianteId: String = "",
    val estudianteNombre: String = "",
    val grado: String = "",
    val seccion: String = "",
    val docenteId: String = "",
    val padreEmail: String = "",
    val tipo: String = "", // "Práctica" o "Examen"
    val preguntasCount: Int = 0,
    val correctas: Int = 0,
    val incorrectas: Int = 0,
    val tiempoPromedio: Double = 0.0,
    val nivel: Int = 1,
    val competenciasDominadas: List<String> = emptyList(),
    val competenciasReforzar: List<String> = emptyList(),
    val recomendaciones: List<String> = emptyList(),
    val estrellasObtenidas: Int = 0,
    val fecha: String = "",
    val timestamp: Long = 0L
)

data class Observacion(
    val id: String = "",
    val docenteId: String = "",
    val docenteNombre: String = "",
    val texto: String = "",
    val fecha: String = "",
    val timestamp: Long = 0L
)

data class NotificacionFirebase(
    val id: String = "",
    val titulo: String = "",
    val cuerpo: String = "",
    val timestamp: Long = 0L,
    val read: Boolean = false,
    val tipo: String = "" // "alumno", "docente", "padre"
)


