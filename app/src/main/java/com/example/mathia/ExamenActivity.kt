package com.example.mathia

import android.os.Bundle
import android.widget.*
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.*

class ExamenActivity : ComponentActivity() {

    private lateinit var tvPregunta: TextView
    private lateinit var tvProgreso: TextView
    private lateinit var radioGroup: RadioGroup
    private lateinit var rbOpcionA: RadioButton
    private lateinit var rbOpcionB: RadioButton
    private lateinit var rbOpcionC: RadioButton
    private lateinit var btnSiguiente: Button
    private lateinit var btnEnviar: Button
    private lateinit var progressBar: ProgressBar

    private val repository = QuestionRepository()
    private var preguntas = listOf<QuestionFirebase>()
    private var preguntaActual = 0
    private val respuestasUsuario = mutableListOf<String>()
    private var gradoSeleccionado = ""
    private var studentPin = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_examen)

        inicializarVistas()
        gradoSeleccionado = intent.getStringExtra("GRADO") ?: "primero"
        studentPin = intent.getStringExtra("PIN") ?: ""
        cargarPreguntas()
        configurarBotones()
    }

    private fun inicializarVistas() {
        tvPregunta = findViewById(R.id.tvPregunta)
        tvProgreso = findViewById(R.id.tvProgreso)
        radioGroup = findViewById(R.id.radioGroup)
        rbOpcionA = findViewById(R.id.rbOpcionA)
        rbOpcionB = findViewById(R.id.rbOpcionB)
        rbOpcionC = findViewById(R.id.rbOpcionC)
        btnSiguiente = findViewById(R.id.btnSiguiente)
        btnEnviar = findViewById(R.id.btnEnviar)
        progressBar = findViewById(R.id.progressBar)
    }

    private fun cargarPreguntas() {
        lifecycleScope.launch {
            mostrarCargando(true)
            try {
                val todasLasPreguntas = repository.cargarPreguntas(gradoSeleccionado)
                preguntas = todasLasPreguntas.shuffled().take(10)
                if (preguntas.isNotEmpty()) {
                    mostrarPregunta(0)
                    actualizarProgreso()
                }
            } catch (e: Exception) {
                Toast.makeText(this@ExamenActivity, e.message, Toast.LENGTH_LONG).show()
                finish()
            } finally {
                mostrarCargando(false)
            }
        }
    }

    private fun mostrarPregunta(index: Int) {
        val pregunta = preguntas[index]
        tvPregunta.text = pregunta.enunciado
        rbOpcionA.text = pregunta.opcionA
        rbOpcionB.text = pregunta.opcionB
        rbOpcionC.text = pregunta.opcionC
        
        // Deseleccionar todos manualmente debido a la estructura anidada en CardView
        rbOpcionA.isChecked = false
        rbOpcionB.isChecked = false
        rbOpcionC.isChecked = false
        
        // Restaurar respuesta si ya existía
        if (index < respuestasUsuario.size && respuestasUsuario[index].isNotEmpty()) {
            when (respuestasUsuario[index]) {
                pregunta.opcionA -> rbOpcionA.isChecked = true
                pregunta.opcionB -> rbOpcionB.isChecked = true
                pregunta.opcionC -> rbOpcionC.isChecked = true
            }
        }
        
        actualizarVisibilidadBotones()
        actualizarProgreso()
    }

    private fun actualizarProgreso() {
        tvProgreso.text = "Pregunta ${preguntaActual + 1} de ${preguntas.size}"
    }

    private fun configurarBotones() {
        btnSiguiente.setOnClickListener {
            if (guardarRespuestaActual()) {
                if (preguntaActual + 1 < preguntas.size) {
                    preguntaActual++
                    mostrarPregunta(preguntaActual)
                }
            }
        }

        btnEnviar.setOnClickListener {
            if (guardarRespuestaActual()) {
                calcularYMostrarResultados()
            }
        }

        // Configurar selección mutuamente exclusiva manual
        rbOpcionA.setOnClickListener { selectOption(rbOpcionA) }
        rbOpcionB.setOnClickListener { selectOption(rbOpcionB) }
        rbOpcionC.setOnClickListener { selectOption(rbOpcionC) }
    }

    private fun selectOption(selected: RadioButton) {
        rbOpcionA.isChecked = (selected == rbOpcionA)
        rbOpcionB.isChecked = (selected == rbOpcionB)
        rbOpcionC.isChecked = (selected == rbOpcionC)
    }

    private fun guardarRespuestaActual(): Boolean {
        // Encontrar manualmente cuál está checked
        val selectedRadioButton = when {
            rbOpcionA.isChecked -> rbOpcionA
            rbOpcionB.isChecked -> rbOpcionB
            rbOpcionC.isChecked -> rbOpcionC
            else -> null
        }
        
        if (selectedRadioButton == null) {
            Toast.makeText(this, "⚠️ Debes seleccionar una respuesta", Toast.LENGTH_SHORT).show()
            return false
        }
        
        val respuesta = selectedRadioButton.text.toString()
        
        if (preguntaActual >= respuestasUsuario.size) {
            respuestasUsuario.add(respuesta)
        } else {
            respuestasUsuario[preguntaActual] = respuesta
        }
        
        return true
    }

    private fun obtenerTemaDePregunta(enunciado: String): String {
        val texto = enunciado.lowercase()
        return when {
            texto.contains("×") || texto.contains("*") || texto.contains("multiplica") || 
            texto.contains("triple") || texto.contains("producto") || texto.contains("veces") -> "Multiplicación"
            
            texto.contains("/") || texto.contains("fracción") || texto.contains("mitad") || 
            texto.contains("tercio") || texto.contains("divid") || texto.contains("partes") -> "Fracciones"
            
            texto.contains("-") || texto.contains("rest") || texto.contains("menos") || 
            texto.contains("queda") || texto.contains("regal") || texto.contains("quitar") || texto.contains("perdió") -> "Restas"
            
            texto.contains("+") || texto.contains("sum") || texto.contains("doble") || 
            texto.contains("total") || texto.contains("más") || texto.contains("agrega") || texto.contains("junt") -> "Sumas"
            
            texto.contains("patrón") || texto.contains("serie") || texto.contains("siguiente") || 
            texto.contains("secuencia") || texto.contains("mayor") || texto.contains("menor") || 
            texto.contains("conteo") || texto.contains("falta") || texto.contains("orden") || texto.contains("___") -> "Series"
            
            else -> "Sumas"
        }
    }

    private fun calcularYMostrarResultados() {
        val db = FirebaseFirestore.getInstance()
        var puntuacion = 0
        val detalles = mutableListOf<Map<String, String>>()
        
        for (i in preguntas.indices) {
            val userAns = respuestasUsuario[i].trim()
            val correctAns = preguntas[i].correcta.trim()
            
            // Validación robusta: por coincidencia de texto u opción letra (A, B, C)
            val esCorrecta = (userAns == correctAns) || 
                (correctAns.equals("A", ignoreCase = true) && userAns == preguntas[i].opcionA.trim()) ||
                (correctAns.equals("B", ignoreCase = true) && userAns == preguntas[i].opcionB.trim()) ||
                (correctAns.equals("C", ignoreCase = true) && userAns == preguntas[i].opcionC.trim())
                
            if (esCorrecta) puntuacion++
            
            detalles.add(mapOf(
                "pregunta" to preguntas[i].enunciado,
                "respuestaUsuario" to respuestasUsuario[i],
                "respuestaCorrecta" to preguntas[i].correcta,
                "esCorrecta" to esCorrecta.toString(),
                "explicacion" to preguntas[i].explicacion
            ))
        }
        
        val porcentaje = (puntuacion * 100) / preguntas.size

        // Guardar resultados en Firestore
        if (studentPin.isNotEmpty()) {
            lifecycleScope.launch {
                try {
                    val docRef = db.collection("usuarios").document(studentPin)
                    val studentDoc = docRef.get().await()
                    if (studentDoc.exists()) {
                        val currentCorrectas = studentDoc.getLong("correctas")?.toInt() ?: 0
                        val currentIncorrectas = studentDoc.getLong("incorrectas")?.toInt() ?: 0
                        val currentTotal = studentDoc.getLong("total_preguntas")?.toInt() ?: 0
                        
                        @Suppress("UNCHECKED_CAST")
                        val currentSkills = studentDoc.get("skills") as? Map<String, Long> ?: emptyMap()
                        @Suppress("UNCHECKED_CAST")
                        val currentErrors = studentDoc.get("incorrectas_por_tema") as? Map<String, Long> ?: emptyMap()
                        
                        val updatedSkills = currentSkills.toMutableMap()
                        val updatedErrors = currentErrors.toMutableMap()

                        for (i in preguntas.indices) {
                            val userAns = respuestasUsuario[i].trim()
                            val correctAns = preguntas[i].correcta.trim()
                            val esCorrecta = (userAns == correctAns) || 
                                (correctAns.equals("A", ignoreCase = true) && userAns == preguntas[i].opcionA.trim()) ||
                                (correctAns.equals("B", ignoreCase = true) && userAns == preguntas[i].opcionB.trim()) ||
                                (correctAns.equals("C", ignoreCase = true) && userAns == preguntas[i].opcionC.trim())
                            
                            val tema = obtenerTemaDePregunta(preguntas[i].enunciado)
                            
                            if (esCorrecta) {
                                val currentSkillScore = updatedSkills[tema] ?: 0L
                                val newSkillScore = (currentSkillScore + 5L).coerceAtMost(100L)
                                updatedSkills[tema] = newSkillScore
                            } else {
                                val currentFails = updatedErrors[tema] ?: 0L
                                updatedErrors[tema] = currentFails + 1L
                            }
                        }

                        val nextCorrectas = currentCorrectas + puntuacion
                        val nextIncorrectas = currentIncorrectas + (preguntas.size - puntuacion)
                        val nextTotal = currentTotal + preguntas.size
                        val nextPrecision = if (nextTotal > 0) (nextCorrectas.toDouble() / nextTotal) * 100.0 else 0.0
                        
                        val nombre = studentDoc.getString("nombre") ?: "Estudiante"
                        val seccion = studentDoc.getString("seccion") ?: "Sección A"
                        val colegio = studentDoc.getString("colegio") ?: "María Auxiliadora"
                        val docente = studentDoc.getString("docente_asignado") ?: "Sin asignar"
                        val padre = studentDoc.getString("padre_email") ?: ""
                        
                        // 1. Actualizar perfil del estudiante (incluyendo skills y fallas por tema)
                        docRef.update(
                            mapOf(
                                "correctas" to nextCorrectas,
                                "incorrectas" to nextIncorrectas,
                                "total_preguntas" to nextTotal,
                                "precision" to nextPrecision,
                                "skills" to updatedSkills,
                                "incorrectas_por_tema" to updatedErrors
                            )
                        ).await()

                        // 2. Guardar historial detallado de cada pregunta
                        val fFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                        val hFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
                        val now = Date()
                        
                        for (i in preguntas.indices) {
                            val userAns = respuestasUsuario[i].trim()
                            val correctAns = preguntas[i].correcta.trim()
                            val esCorrecta = (userAns == correctAns) || 
                                (correctAns.equals("A", ignoreCase = true) && userAns == preguntas[i].opcionA.trim()) ||
                                (correctAns.equals("B", ignoreCase = true) && userAns == preguntas[i].opcionB.trim()) ||
                                (correctAns.equals("C", ignoreCase = true) && userAns == preguntas[i].opcionC.trim())
                            
                            val tema = obtenerTemaDePregunta(preguntas[i].enunciado)
                            
                            val log = hashMapOf(
                                "estudianteId" to studentPin,
                                "estudianteNombre" to nombre,
                                "grado" to gradoSeleccionado,
                                "seccion" to seccion,
                                "docenteAsignado" to docente,
                                "padreAsociado" to padre,
                                "fecha" to fFormat.format(now),
                                "hora" to hFormat.format(now),
                                "timestamp" to now.time,
                                "operacion" to "Examen IA",
                                "pregunta" to preguntas[i].enunciado,
                                "respuestaCorrecta" to preguntas[i].correcta,
                                "respuestaElegida" to respuestasUsuario[i],
                                "tiempoRespuesta" to 8L,
                                "dificultad" to when (preguntas[i].dificultad) { 1 -> "Fácil"; 2 -> "Medio"; else -> "Difícil" },
                                "correcta" to esCorrecta,
                                "intentos" to 1,
                                "estrellasObtenidas" to 0,
                                "experienciaGanada" to if (esCorrecta) 10 else 0,
                                "dispositivo" to "${android.os.Build.MANUFACTURER} ${android.os.Build.MODEL}"
                            )
                            db.collection("historial_respuestas").add(log).await()
                        }
                    }
                } catch (e: Exception) {
                    println("❌ Error guardando resultados de examen en Firestore: ${e.message}")
                }
            }
        }
        
        val intent = android.content.Intent(this, ResultadoActivity::class.java)
        intent.putExtra("PUNTUACION", puntuacion)
        intent.putExtra("TOTAL", preguntas.size)
        intent.putExtra("PORCENTAJE", porcentaje)
        intent.putExtra("GRADO", gradoSeleccionado)
        intent.putExtra("PIN", studentPin)
        intent.putExtra("DETALLES", ArrayList(detalles.map { HashMap(it) }))
        startActivity(intent)
        finish()
    }

    private fun actualizarVisibilidadBotones() {
        val esUltima = preguntaActual == preguntas.size - 1
        btnSiguiente.visibility = if (esUltima) android.view.View.GONE else android.view.View.VISIBLE
        btnEnviar.visibility = if (esUltima) android.view.View.VISIBLE else android.view.View.GONE
    }

    private fun mostrarCargando(mostrar: Boolean) {
        progressBar.visibility = if (mostrar) android.view.View.VISIBLE else android.view.View.GONE
        rbOpcionA.isEnabled = !mostrar
        rbOpcionB.isEnabled = !mostrar
        rbOpcionC.isEnabled = !mostrar
        btnSiguiente.isEnabled = !mostrar
        btnEnviar.isEnabled = !mostrar
    }
}
