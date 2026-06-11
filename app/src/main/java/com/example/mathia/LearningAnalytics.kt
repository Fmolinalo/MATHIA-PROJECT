package com.example.mathia

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

fun calcularRiesgo(
    precision: Int
): String {

    return when {

        precision < 40 ->
            "ALTO"

        precision < 70 ->
            "MEDIO"

        else ->
            "BAJO"
    }
}

fun predecirNivel(
    precision: Int,
    tiempoPromedio: Double
): String {

    return when {

        precision >= 85 &&
                tiempoPromedio <= 15 ->

            "Probable Avanzado"

        precision >= 60 ->

            "Probable Intermedio"

        else ->

            "Necesita Refuerzo"
    }
}

fun generarAlertas(
    precision: Int,
    tiempoPromedio: Double
): List<String> {

    val alertas = mutableListOf<String>()

    if (precision < 50)
        alertas.add(
            "Precisión menor al 50%"
        )

    if (tiempoPromedio > 20)
        alertas.add(
            "Tiempo de respuesta elevado"
        )

    if (alertas.isEmpty())
        alertas.add(
            "Sin alertas"
        )

    return alertas
}

@Composable
fun AlertCard(
    alerts: List<String>
) {

    Card {

        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            Text(
                "Alertas",
                fontWeight = FontWeight.Bold
            )

            Spacer(
                Modifier.height(8.dp)
            )

            alerts.forEach {

                Text("• $it")
            }
        }
    }
}

@Composable
fun PredictionCard(
    prediction: String
) {

    Card {

        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            Text(
                "Predicción",
                fontWeight = FontWeight.Bold
            )

            Spacer(
                Modifier.height(8.dp)
            )

            Text(prediction)
        }
    }
}

@Composable
fun WeeklyProgressChart(
    weekData: List<Int>
) {

    Card {

        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            Text(
                "Evolución Semanal",
                fontWeight = FontWeight.Bold
            )

            Spacer(
                Modifier.height(8.dp)
            )

            weekData.forEachIndexed { index, value ->

                Text("Día ${index + 1}")

                LinearProgressIndicator(
                    progress = { value / 100f },
                    modifier = Modifier.fillMaxWidth()
                )

                Text("$value%")
            }
        }
    }
}