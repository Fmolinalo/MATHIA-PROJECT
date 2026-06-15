package com.example.mathia

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.ComponentActivity

class ResultadoActivity : ComponentActivity() {
    
    private lateinit var tvResultado: TextView
    private lateinit var tvAnalisisIA: TextView
    private lateinit var tvRecomendaciones: TextView
    private lateinit var btnReintentar: Button
    private lateinit var btnSalir: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_resultado)

        inicializarVistas()
        mostrarResultados()
        generarAnalisisIA()
        configurarBotones()
    }

    private fun inicializarVistas() {
        tvResultado = findViewById(R.id.tvResultado)
        tvAnalisisIA = findViewById(R.id.tvAnalisisIA)
        tvRecomendaciones = findViewById(R.id.tvRecomendaciones)
        btnReintentar = findViewById(R.id.btnReintentar)
        btnSalir = findViewById(R.id.btnSalir)
    }

    private fun mostrarResultados() {
        val puntuacion = intent.getIntExtra("PUNTUACION", 0)
        val total = intent.getIntExtra("TOTAL", 0)
        val porcentaje = intent.getIntExtra("PORCENTAJE", 0)
        
        val mensaje = when {
            porcentaje >= 90 -> "¡EXCELENTE!"
            porcentaje >= 70 -> "¡MUY BIEN!"
            porcentaje >= 50 -> "BUEN INTENTO"
            else -> "CONTINÚA PRACTICANDO"
        }
        
        tvResultado.text = """
            $mensaje
            
            Calificación: $puntuacion / $total
            Porcentaje: $porcentaje%
            
            ${if (porcentaje >= 60) "✅ APROBADO" else "❌ NO APROBADO"}
        """.trimIndent()
    }

    @Suppress("UNCHECKED_CAST")
    private fun generarAnalisisIA() {
        val puntuacion = intent.getIntExtra("PUNTUACION", 0)
        val total = intent.getIntExtra("TOTAL", 0)
        val grado = intent.getStringExtra("GRADO") ?: "primero"
        val porcentaje = (puntuacion * 100) / total
        val detalles = intent.getSerializableExtra("DETALLES") as? ArrayList<HashMap<String, String>>
        
        // Análisis detallado por IA
        var analisis = "🤖 ANÁLISIS INTELIGENTE\n\n"
        
        if (detalles != null) {
            val correctas = detalles.filter { it["esCorrecta"] == "true" }
            val incorrectas = detalles.filter { it["esCorrecta"] == "false" }
            
            analisis += "📊 Estadísticas:\n"
            analisis += "• Respuestas correctas: ${correctas.size}\n"
            analisis += "• Respuestas incorrectas: ${incorrectas.size}\n\n"
            
            if (incorrectas.isNotEmpty()) {
                analisis += "❌ Temas con dificultades:\n"
                incorrectas.forEachIndexed { index, incorrecta ->
                    analisis += "${index + 1}. ${incorrecta["pregunta"]}\n"
                    if (incorrecta["explicacion"]?.isNotBlank() == true) {
                        analisis += "   💡 ${incorrecta["explicacion"]}\n"
                    }
                }
                analisis += "\n"
            }
        }
        
        analisis += "🎯 Nivel de dominio:\n"
        when {
            porcentaje >= 90 -> analisis += "• Dominio completo del grado $grado\n• Preparado para nivel superior\n"
            porcentaje >= 70 -> analisis += "• Buen dominio del grado $grado\n• Reforzar conceptos específicos\n"
            porcentaje >= 50 -> analisis += "• Dominio básico del grado $grado\n• Requiere práctica adicional\n"
            else -> analisis += "• Dominio insuficiente del grado $grado\n• Necesita repaso fundamental\n"
        }
        
        tvAnalisisIA.text = analisis
        
        // Recomendaciones personalizadas
        var recomendaciones = "📚 RECOMENDACIONES IA\n\n"
        
        when {
            porcentaje >= 90 -> {
                recomendaciones += "✅ ¡Felicidades! Has demostrado un excelente dominio.\n\n"
                recomendaciones += "Próximos pasos:\n"
                recomendaciones += "• Avanza al siguiente grado académico\n"
                recomendaciones += "• Practica problemas más complejos\n"
                recomendaciones += "• Ayuda a tus compañeros que lo necesiten\n"
            }
            porcentaje >= 70 -> {
                recomendaciones += "📈 ¡Buen trabajo! Estás en el camino correcto.\n\n"
                recomendaciones += "Para mejorar:\n"
                recomendaciones += "• Repasa los temas donde fallaste\n"
                recomendaciones += "• Practica 15 minutos diarios\n"
                recomendaciones += "• Toma este examen nuevamente en 3 días\n"
            }
            porcentaje >= 50 -> {
                recomendaciones += "💪 ¡Sigue esforzándote! La práctica hace al maestro.\n\n"
                recomendaciones += "Plan de mejora:\n"
                recomendaciones += "• Estudia los conceptos básicos del grado $grado\n"
                recomendaciones += "• Realiza ejercicios similares a los del examen\n"
                recomendaciones += "• Solicita ayuda a tu profesor o compañeros\n"
                recomendaciones += "• Repite este examen después de estudiar\n"
            }
            else -> {
                recomendaciones += "⚠️ Necesitas reforzar los fundamentos.\n\n"
                recomendaciones += "Plan de acción urgente:\n"
                recomendaciones += "• Comienza desde conceptos más básicos\n"
                recomendaciones += "• Dedica al menos 30 minutos diarios al estudio\n"
                recomendaciones += "• Utiliza material didáctico adicional\n"
                recomendaciones += "• Considera tutoría personalizada\n"
                recomendaciones += "• Repasa cada tema antes de reintentar el examen\n"
            }
        }
        
        recomendaciones += "\n💡 La inteligencia artificial recomienda: La constancia es más importante que la intensidad. ¡Continúa practicando!"
        
        tvRecomendaciones.text = recomendaciones
    }

    private fun configurarBotones() {
        btnReintentar.setOnClickListener {
            val retryIntent = android.content.Intent(this, ExamenActivity::class.java)
            retryIntent.putExtra("GRADO", intent.getStringExtra("GRADO") ?: "primero")
            retryIntent.putExtra("PIN", intent.getStringExtra("PIN") ?: "")
            startActivity(retryIntent)
            finish()
        }
        
        btnSalir.setOnClickListener {
            finish() // returns to MainActivity
        }
    }
}
