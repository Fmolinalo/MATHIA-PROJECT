package com.example.mathia

import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

class QuestionRepository {

    private val db = Firebase.firestore

    suspend fun cargarPreguntas(grado: String): List<QuestionFirebase> {
        return try {
            // Normalizar el nombre del grado
            val gradoNormalizado = when (grado.lowercase()) {
                "1ro", "primero", "1" -> "primero"
                "2do", "segundo", "2" -> "segundo"
                "3ro", "tercero", "3" -> "tercero"
                else -> grado.lowercase()
            }

            println("📚 Cargando preguntas para grado: $gradoNormalizado")

            val resultado = db.collection("grados")
                .document(gradoNormalizado)
                .collection("modulos")
                .get()
                .await()

            println("📚 Preguntas encontradas: ${resultado.documents.size}")

            val preguntas = resultado.documents.mapNotNull { doc ->
                val pregunta = doc.toObject(QuestionFirebase::class.java)
                if (pregunta != null && pregunta.enunciado.isNotBlank()) {
                    println("✅ Pregunta cargada: ${pregunta.enunciado}")
                    pregunta
                } else {
                    null
                }
            }

            if (preguntas.isEmpty()) {
                println("⚠️ No hay preguntas en Firebase para $gradoNormalizado")
                // Preguntas de respaldo para pruebas
                getBackupQuestions(gradoNormalizado)
            } else {
                preguntas
            }
        } catch (e: Exception) {
            println("❌ Error cargando preguntas: ${e.message}")
            // Preguntas de respaldo en caso de error
            getBackupQuestions(grado)
        }
    }

    // Preguntas de respaldo para que la app no se quede sin preguntas
    private fun getBackupQuestions(grado: String): List<QuestionFirebase> {
        println("📚 Usando preguntas de respaldo para: $grado")

        return when (grado) {
            "primero" -> listOf(
                QuestionFirebase(
                    correcta = "4",
                    dificultad = 1,
                    enunciado = "2 + 2 = ?",
                    opcionA = "3",
                    opcionB = "4",
                    opcionC = "5"
                ),
                QuestionFirebase(
                    correcta = "5",
                    dificultad = 1,
                    enunciado = "3 + 2 = ?",
                    opcionA = "4",
                    opcionB = "5",
                    opcionC = "6"
                ),
                QuestionFirebase(
                    correcta = "3",
                    dificultad = 1,
                    enunciado = "5 - 2 = ?",
                    opcionA = "2",
                    opcionB = "3",
                    opcionC = "4"
                )
            )
            "segundo" -> listOf(
                QuestionFirebase(
                    correcta = "15",
                    dificultad = 1,
                    enunciado = "7 + 8 = ?",
                    opcionA = "14",
                    opcionB = "15",
                    opcionC = "16"
                ),
                QuestionFirebase(
                    correcta = "9",
                    dificultad = 1,
                    enunciado = "12 - 3 = ?",
                    opcionA = "8",
                    opcionB = "9",
                    opcionC = "10"
                ),
                QuestionFirebase(
                    correcta = "24",
                    dificultad = 1,
                    enunciado = "12 + 12 = ?",
                    opcionA = "22",
                    opcionB = "23",
                    opcionC = "24"
                ),
                QuestionFirebase(
                    correcta = "45",
                    dificultad = 2,
                    enunciado = "23 + 22 = ?",
                    opcionA = "44",
                    opcionB = "45",
                    opcionC = "46"
                )
            )
            "tercero" -> listOf(
                QuestionFirebase(
                    correcta = "42",
                    dificultad = 1,
                    enunciado = "24 + 18 = ?",
                    opcionA = "40",
                    opcionB = "41",
                    opcionC = "42"
                ),
                QuestionFirebase(
                    correcta = "17",
                    dificultad = 1,
                    enunciado = "31 - 14 = ?",
                    opcionA = "15",
                    opcionB = "16",
                    opcionC = "17"
                ),
                QuestionFirebase(
                    correcta = "56",
                    dificultad = 1,
                    enunciado = "7 × 8 = ?",
                    opcionA = "48",
                    opcionB = "54",
                    opcionC = "56"
                ),
                QuestionFirebase(
                    correcta = "78",
                    dificultad = 2,
                    enunciado = "45 + 33 = ?",
                    opcionA = "76",
                    opcionB = "77",
                    opcionC = "78"
                )
            )
            else -> emptyList()
        }
    }
}

data class QuestionFirebase(
    val correcta: String = "",
    val dificultad: Int = 1,
    val enunciado: String = "",
    val explicacion: String = "",
    val opcionA: String = "",
    val opcionB: String = "",
    val opcionC: String = ""
)