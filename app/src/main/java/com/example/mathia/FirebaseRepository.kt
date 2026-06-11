package com.example.mathia

import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
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

    // Google Sign-In for parent or teacher with Firebase Auth integration
    suspend fun loginConGoogle(idToken: String, email: String, rol: String): Pair<Boolean, Boolean> { // (success, isProfileComplete)
        return try {
            val auth = FirebaseAuth.getInstance()
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val authResult = auth.signInWithCredential(credential).await()
            val firebaseUser = authResult.user ?: return Pair(false, false)
            val uid = firebaseUser.uid

            val docRef = db.collection("usuarios").document(uid)
            val doc = docRef.get().await()
            if (doc.exists()) {
                val isComplete = doc.getBoolean("perfil_completo") ?: false
                println("✅ Login Google exitoso para uid: $uid. Perfil completo: $isComplete")
                Pair(true, isComplete)
            } else {
                val rolNormalizado = if (rol == "padres") "padre" else "docente"
                val nuevoUsuario = hashMapOf(
                    "uid" to uid,
                    "email" to email,
                    "rol" to rolNormalizado,
                    "nombre" to (firebaseUser.displayName ?: email.substringBefore("@")),
                    "perfil_completo" to false
                )
                docRef.set(nuevoUsuario).await()
                println("✅ Nuevo usuario Google creado para uid: $uid")
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
            println("✅ Perfil de docente actualizado para uid: $uid")
            true
        } catch (e: Exception) {
            println("❌ Error actualizando perfil de docente: ${e.message}")
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
                resultado.documents.first().reference
                    .update(
                        mapOf(
                            "avatar" to avatar,
                            "equipped_theme" to theme
                        )
                    )
                    .await()
                true
            } else {
                false
            }
        } catch (e: Exception) {
            println("❌ Error actualizando cosméticos: ${e.message}")
            false
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

                doc.reference
                    .update(
                        mapOf(
                            "estrellas" to (estrellasActuales - costo),
                            campo to listaActualizada
                        )
                    )
                    .await()
                true
            } else {
                false
            }
        } catch (e: Exception) {
            println("❌ Error comprando cosmético: ${e.message}")
            false
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
    val padre_email: String = "",
    val avatar: String = "👶",
    val equipped_theme: String = "Lila",
    val unlocked_avatars: List<String> = listOf("👶"),
    val unlocked_themes: List<String> = listOf("Lila")
)