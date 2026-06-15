package com.example.mathia

import com.example.mathia.model.FirebaseStudent

object RecommendationEngine {

    fun generarRecomendaciones(student: FirebaseStudent): List<String> {
        val recomendaciones = mutableListOf<String>()

        // 1. Análisis de Precisión Global
        val prec = student.precision
        when {
            prec >= 85.0 -> {
                recomendaciones.add("¡Increíble precisión! Has mejorado significativamente en tus habilidades matemáticas. 🌟")
            }
            prec in 50.0..84.9 -> {
                recomendaciones.add("Vas por excelente camino. ¡Un poco más de práctica y dominarás todos los temas! 👍")
            }
            prec > 0.0 && prec < 50.0 -> {
                recomendaciones.add("¡No te rindas! La práctica hace al maestro. Repasa con calma cada ejercicio antes de responder. 💪")
            }
        }

        // 2. Análisis de Errores Frecuentes y Competencias (Skills)
        // Buscamos cuál es la habilidad con menor puntaje en el mapa de skills (si existe)
        if (student.skills.isNotEmpty()) {
            val skillMasBaja = student.skills.minByOrNull { it.value }
            if (skillMasBaja != null && skillMasBaja.value < 70) {
                val tema = skillMasBaja.key.lowercase()
                recomendaciones.add("Se recomienda practicar más $tema para reforzar esta competencia. 🎯")
            }
        }

        // También buscamos el tema con más respuestas incorrectas
        if (student.incorrectas_por_tema.isNotEmpty()) {
            val temaMasFallado = student.incorrectas_por_tema.maxByOrNull { it.value }
            if (temaMasFallado != null && temaMasFallado.value > 2) {
                val tema = temaMasFallado.key
                recomendaciones.add("Debes reforzar el tema de $tema. ¡Tú puedes lograrlo! 🧠")
            }
        }

        // Si no hay errores registrados pero la precisión es baja en general
        if (student.skills.isEmpty() && prec in 1.0..59.9) {
            recomendaciones.add("Te sugerimos tomar el Examen Adaptativo para identificar qué temas reforzar. 📝")
        }

        // 3. Análisis de Tiempos de Respuesta
        val tiempoProm = student.tiempo_promedio
        if (tiempoProm > 0.0) {
            when {
                tiempoProm > 20.0 -> {
                    recomendaciones.add("Tómate tu tiempo, pero intenta agilizar tus cálculos mentales para responder un poquito más rápido. ⏱️")
                }
                tiempoProm in 1.0..10.0 && prec >= 80.0 -> {
                    recomendaciones.add("¡Eres súper veloz y preciso resolviendo problemas! Mateo está impresionado. ⚡🚀")
                }
            }
        }

        // 4. Análisis de Racha (Streak)
        val racha = student.streak
        when {
            racha >= 5 -> {
                recomendaciones.add("¡Racha espectacular de $racha días! No pares ahora y sigue ganando multiplicadores de XP. 🔥")
            }
            racha in 2..4 -> {
                recomendaciones.add("¡Mantén encendido tu fuego de estudio! Llevas $racha días practicando seguidos. 🏃‍♂️")
            }
            racha == 0 -> {
                recomendaciones.add("¡Mateo te extraña! Resuelve un desafío hoy para iniciar una nueva racha de estudio. 🧒")
            }
        }

        // 5. Nivel y Estrellas
        val estrellas = student.estrellas
        if (estrellas >= 150) {
            recomendaciones.add("¡Tu esfuerzo brilla! Tienes una colección fantástica de $estrellas estrellas. ¡Visita la tienda! 👑🏪")
        }

        // 6. Nivel actual
        if (student.nivel_actual >= 5) {
            recomendaciones.add("¡Felicidades por alcanzar el nivel ${student.nivel_actual}! Sigue superándote con retos más difíciles. 🏆")
        } else if (student.nivel_actual == 1 && student.total_preguntas > 0) {
            recomendaciones.add("¡Sigue sumando XP para subir al Nivel 2 y desbloquear recompensas! 🐣")
        }

        // Si no hay suficientes datos aún (primer login)
        if (recomendaciones.isEmpty()) {
            recomendaciones.add("¡Bienvenido a MathIA! Inicia practicando sumas o restas para que Mateo analice tu progreso. 🎒")
            recomendaciones.add("Realiza el Examen Adaptativo para descubrir tu nivel actual de matemáticas. 🧬")
        }

        // Retornar máximo 3 recomendaciones más importantes
        return recomendaciones.shuffled().take(3)
    }
}
