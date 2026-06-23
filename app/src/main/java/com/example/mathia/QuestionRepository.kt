package com.example.mathia

import android.util.Log
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
// Asegúrate de importar la ruta correcta donde creaste RetrofitClient
import com.example.mathia.data.network.RetrofitClient

class QuestionRepository {

    private val db = Firebase.firestore

    // 1. Iniciamos el cliente de Retrofit para hablar con Laravel
    private val apiService = RetrofitClient.apiService

    // ==========================================
    // MÉTODO 1: EL ORIGINAL DE TU EQUIPO (FIREBASE)
    // ==========================================
    suspend fun cargarPreguntas(grado: String): List<QuestionFirebase> {
        return try {
            val gradoNormalizado = normalizarGrado(grado)
            println("📚 Cargando preguntas para grado: $gradoNormalizado")

            val resultado = db.collection("grados")
                .document(gradoNormalizado)
                .collection("modulos")
                .get()
                .await()

            val preguntas = resultado.documents.mapNotNull { doc ->
                doc.toObject(QuestionFirebase::class.java)
                    ?.copy(id = doc.id)
                    ?.takeIf { it.enunciado.isNotBlank() }
            }

            if (preguntas.isEmpty()) {
                throw Exception("No hay preguntas en Firebase para $gradoNormalizado")
            }

            preguntas
        } catch (e: Exception) {
            println("❌ Error: ${e.message}")
            throw Exception("No se pudieron cargar las preguntas. Por favor, verifica tu conexión y que existan preguntas en Firebase.")
        }
    }

    // ==========================================
    // MÉTODO 2: LA FUSIÓN MÁGICA (FIREBASE + IA)
    // ==========================================
    suspend fun cargarPreguntasMixtas(grado: String, temaDeseado: String = "operaciones básicas"): List<QuestionFirebase> {
        // 1. Cargamos las preguntas estáticas de Firebase
        val preguntasFirebase = cargarPreguntas(grado).toMutableList()

        // 2. Intentamos pedirle a la IA una pregunta nueva
        try {
            Log.e("MATHIA_DEBUG", "Intentando conectar a Laravel en la IP 192.168.100.66...")

            val peticion = mapOf("tema" to temaDeseado, "nivel" to grado)
            val respuestaIA = apiService.obtenerRetoDinamico(peticion)

            if (respuestaIA.isSuccessful && respuestaIA.body()?.success == true) {
                val retoMágico = respuestaIA.body()!!.data

                // 3. Convertimos el JSON de la IA en un objeto de Firebase
                val preguntaGenerada = QuestionFirebase(
                    id = "IA-${System.currentTimeMillis()}",
                    correcta = retoMágico.correcta,
                    dificultad = retoMágico.dificultad,
                    enunciado = retoMágico.enunciado + " 🤖", // Identificador visual
                    explicacion = retoMágico.explicacion,
                    opcionA = retoMágico.opcionA,
                    opcionB = retoMágico.opcionB,
                    opcionC = retoMágico.opcionC
                )

                // 4. Inyectamos la pregunta en la lista
                preguntasFirebase.add(preguntaGenerada)
                Log.e("MATHIA_DEBUG", "✨ ¡PREGUNTA IA INYECTADA CON ÉXITO!")
            } else {
                Log.e("MATHIA_DEBUG", "❌ Laravel respondió con error: ${respuestaIA.errorBody()?.string()}")
            }
        } catch (e: Exception) {
            Log.e("MATHIA_DEBUG", "⚠️ Falla catastrófica de red: ${e.message}")
        }

        // Devolvemos la lista mezclada a la pantalla
        return preguntasFirebase
    }

    // ==========================================
    // MeTODO DE NORMALIZACIÓN
    // ==========================================
    private fun normalizarGrado(grado: String): String {
        return when {
            grado.lowercase().contains("1ro") || grado.lowercase().contains("primero") || grado == "1" -> "primero"
            grado.lowercase().contains("2do") || grado.lowercase().contains("segundo") || grado == "2" -> "segundo"
            grado.lowercase().contains("3ro") || grado.lowercase().contains("tercero") || grado == "3" -> "tercero"
            grado.lowercase().contains("4to") || grado.lowercase().contains("cuarto") || grado == "4" -> "cuarto"
            grado.lowercase().contains("5to") || grado.lowercase().contains("quinto") || grado == "5" -> "quinto"
            grado.lowercase().contains("6to") || grado.lowercase().contains("sexto") || grado == "6" -> "sexto"
            else -> grado.lowercase()
        }
    }
}

// ==========================================
// MODELO DE DATOS DE FIREBASE
// ==========================================
data class QuestionFirebase(
    val id: String = "",
    val correcta: String = "",
    val dificultad: Int = 1,
    val enunciado: String = "",
    val explicacion: String = "",
    val opcionA: String = "",
    val opcionB: String = "",
    val opcionC: String = ""
)