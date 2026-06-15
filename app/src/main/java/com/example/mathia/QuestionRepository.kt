package com.example.mathia

import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

class QuestionRepository {

    private val db = Firebase.firestore

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