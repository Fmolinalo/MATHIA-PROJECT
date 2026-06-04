package com.example.mathia

import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

class FirebaseRepository {

    private val db = Firebase.firestore

    // ============ FUNCIONES PARA ALUMNOS ============

    suspend fun actualizarEstrellas(pin: String, estrellas: Int) {
        try {
            val resultado = db.collection("usuarios")
                .whereEqualTo("pin", pin.toIntOrNull() ?: return)
                .get()
                .await()

            if (resultado.documents.isNotEmpty()) {
                val doc = resultado.documents.first()
                val estrellasActuales = doc.getLong("estrellas") ?: 0
                doc.reference
                    .update("estrellas", estrellasActuales.toInt() + estrellas)
                    .await()
                println("✅ Estrellas actualizadas: +$estrellas")
            }
        } catch (e: Exception) {
            println("❌ Error actualizando estrellas: ${e.message}")
        }
    }

    suspend fun actualizarNivel(pin: String, nivel: Int) {
        try {
            val resultado = db.collection("usuarios")
                .whereEqualTo("pin", pin.toIntOrNull() ?: return)
                .get()
                .await()

            if (resultado.documents.isNotEmpty()) {
                resultado.documents.first().reference
                    .update("nivel_actual", nivel)
                    .await()
                println("✅ Nivel actualizado: $nivel")
            }
        } catch (e: Exception) {
            println("❌ Error actualizando nivel: ${e.message}")
        }
    }

    suspend fun actualizarPrecision(pin: String, nuevaPrecision: Double) {
        try {
            val resultado = db.collection("usuarios")
                .whereEqualTo("pin", pin.toIntOrNull() ?: return)
                .get()
                .await()

            if (resultado.documents.isNotEmpty()) {
                resultado.documents.first().reference
                    .update("precision", nuevaPrecision)
                    .await()
                println("✅ Precisión actualizada: $nuevaPrecision%")
            }
        } catch (e: Exception) {
            println("❌ Error actualizando precisión: ${e.message}")
        }
    }

    suspend fun obtenerAlumnoPorPin(pin: String): FirebaseStudent? {
        return try {
            val resultado = db.collection("usuarios")
                .whereEqualTo("pin", pin.toIntOrNull() ?: return null)
                .get()
                .await()

            if (resultado.documents.isEmpty()) return null
            resultado.documents.first().toObject(FirebaseStudent::class.java)
        } catch (e: Exception) {
            println("❌ Error obteniendo alumno: ${e.message}")
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
            val resultado = db.collection("usuarios").get().await()
            resultado.documents.mapNotNull { it.toObject(FirebaseStudent::class.java) }
        } catch (e: Exception) {
            println("❌ Error obteniendo todos los alumnos: ${e.message}")
            emptyList()
        }
    }

    // NUEVA FUNCIÓN: Obtener estudiantes por email del padre
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

    // NUEVA FUNCIÓN: Actualizar el email del padre en el estudiante
    suspend fun actualizarPadreEmail(pin: String, padreEmail: String): Boolean {
        return try {
            val resultado = db.collection("usuarios")
                .whereEqualTo("pin", pin.toIntOrNull() ?: return false)
                .get()
                .await()

            if (resultado.documents.isNotEmpty()) {
                resultado.documents.first().reference
                    .update("padre_email", padreEmail)
                    .await()
                println("✅ Padre_email actualizado para PIN $pin: $padreEmail")
                true
            } else {
                false
            }
        } catch (e: Exception) {
            println("❌ Error actualizando padre_email: ${e.message}")
            false
        }
    }

    // ============ FUNCIONES PARA ADULTOS ============

    suspend fun registrarAdulto(
        email: String,
        password: String,
        rol: String,
        nombre: String,
        estudiantePin: Int? = null
    ): Boolean {
        return try {
            val adulto = mutableMapOf(
                "email" to email,
                "password" to password,
                "rol" to rol,
                "nombre" to nombre,
                "fechaRegistro" to System.currentTimeMillis()
            )
            if (estudiantePin != null && rol == "padres") {
                adulto["estudiante_pin"] = estudiantePin
            }
            db.collection("adultos")
                .document(email)
                .set(adulto)
                .await()
            println("✅ Adulto registrado: $nombre ($rol)")
            true
        } catch (e: Exception) {
            println("❌ Error registrando adulto: ${e.message}")
            false
        }
    }

    suspend fun loginAdulto(email: String, password: String): AdultoFirebase? {
        return try {
            val resultado = db.collection("adultos")
                .whereEqualTo("email", email)
                .whereEqualTo("password", password)
                .get()
                .await()

            if (resultado.documents.isEmpty()) return null
            resultado.documents.first().toObject(AdultoFirebase::class.java)
        } catch (e: Exception) {
            println("❌ Error login adulto: ${e.message}")
            null
        }
    }
}

// ============ DATA CLASSES (SOLO UNA VEZ) ============

data class AdultoFirebase(
    val email: String = "",
    val password: String = "",
    val rol: String = "",
    val nombre: String = "",
    val estudiante_pin: Int? = null,
    val fechaRegistro: Long = 0
)

data class FirebaseStudent(
    val nombre: String = "",
    val grado: String = "",
    val edad: Int = 0,
    val nivel_actual: Int = 1,
    val precision: Double = 0.0,
    val estrellas: Int = 0,
    val pin: Int = 0,
    val padre_email: String = ""
)