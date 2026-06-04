package com.example.mathia

import android.content.Context
import android.widget.Toast
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

object UploadExercises {

    private val db = Firebase.firestore

    // ============================================
    // PRIMERO DE PRIMARIA - 66 EJERCICIOS
    // ============================================

    private val ejerciciosPrimero = listOf(
        // CONJUNTO A: Números y conteo hasta 20 (A-001 a A-012)
        mapOf("id" to "A-001", "enunciado" to "¿Cuántos pollitos hay? 🐥🐥🐥🐥🐥🐥", "correcta" to "6", "opcionA" to "4", "opcionB" to "5", "opcionC" to "6", "dificultad" to 1, "explicacion" to "Cuenta uno por uno cada pollito. Total: 6."),
        mapOf("id" to "A-002", "enunciado" to "Escribe el número que falta: 3, 5, 7, ___, 11", "correcta" to "9", "opcionA" to "8", "opcionB" to "9", "opcionC" to "10", "dificultad" to 1, "explicacion" to "Suma de 2 en 2: 3→5→7→9→11."),
        mapOf("id" to "A-003", "enunciado" to "¿Qué número es mayor: 12 ó 17?", "correcta" to "17", "opcionA" to "12", "opcionB" to "17", "opcionC" to "Son iguales", "dificultad" to 1, "explicacion" to "17 tiene más unidades que 12."),
        mapOf("id" to "A-004", "enunciado" to "En una fila, Lucía es la quinta. ¿Qué número de lugar ocupa?", "correcta" to "5", "opcionA" to "3", "opcionB" to "4", "opcionC" to "5", "dificultad" to 1, "explicacion" to "Quinto = 5° lugar."),
        mapOf("id" to "A-005", "enunciado" to "Completa: 14, 15, ___, 17, ___, 19", "correcta" to "16 y 18", "opcionA" to "16 y 18", "opcionB" to "15 y 17", "opcionC" to "16 y 17", "dificultad" to 1, "explicacion" to "Serie consecutiva de 1 en 1."),
        mapOf("id" to "A-006", "enunciado" to "¿Es verdad que 9 > 15?", "correcta" to "Falso", "opcionA" to "Verdadero", "opcionB" to "Falso", "opcionC" to "", "dificultad" to 1, "explicacion" to "9 es menor que 15. El signo correcto es <."),
        mapOf("id" to "A-007", "enunciado" to "En una canasta hay 8 mangos y en otra hay 13. ¿Cuál tiene más?", "correcta" to "La segunda", "opcionA" to "La primera", "opcionB" to "La segunda", "opcionC" to "Tienen igual", "dificultad" to 2, "explicacion" to "13 > 8, entonces la segunda canasta tiene más."),
        mapOf("id" to "A-008", "enunciado" to "¿Qué número está entre 11 y 13?", "correcta" to "12", "opcionA" to "10", "opcionB" to "11", "opcionC" to "12", "dificultad" to 2, "explicacion" to "Entre 11 y 13 está el 12."),
        mapOf("id" to "A-009", "enunciado" to "En la recta numérica, ¿qué número está a 3 posiciones a la derecha del 6?", "correcta" to "9", "opcionA" to "3", "opcionB" to "8", "opcionC" to "9", "dificultad" to 2, "explicacion" to "6 + 3 = 9."),
        mapOf("id" to "A-010", "enunciado" to "María tiene fichas numeradas del 1 al 20. Saca las fichas pares. ¿Cuántas fichas sacó?", "correcta" to "10", "opcionA" to "8", "opcionB" to "9", "opcionC" to "10", "dificultad" to 3, "explicacion" to "Pares: 2,4,6,8,10,12,14,16,18,20 = 10 fichas."),
        mapOf("id" to "A-011", "enunciado" to "El número misterioso es mayor que 14, menor que 18 y es impar. ¿Qué número es?", "correcta" to "15 y 17", "opcionA" to "15", "opcionB" to "16", "opcionC" to "17", "dificultad" to 3, "explicacion" to "Impares entre 14 y 18: 15 y 17. Ambos cumplen."),
        mapOf("id" to "A-012", "enunciado" to "En una tienda hay 3 cajas con 5 mangos cada una. ¿Cuántos mangos hay? ¿Es ese número par o impar?", "correcta" to "15, impar", "opcionA" to "15, impar", "opcionB" to "15, par", "opcionC" to "13, impar", "dificultad" to 4, "explicacion" to "3×5=15. El 15 termina en 5, es impar."),

        // CONJUNTO B: Adición y sustracción hasta 20 (B-001 a B-012)
        mapOf("id" to "B-001", "enunciado" to "¿Cuánto es 6 + 8?", "correcta" to "14", "opcionA" to "13", "opcionB" to "14", "opcionC" to "15", "dificultad" to 1, "explicacion" to "6 + 8 = 14."),
        mapOf("id" to "B-002", "enunciado" to "¿Cuánto es 17 - 9?", "correcta" to "8", "opcionA" to "6", "opcionB" to "7", "opcionC" to "8", "dificultad" to 1, "explicacion" to "17 - 9 = 8."),
        mapOf("id" to "B-003", "enunciado" to "Pedro tiene 7 canicas y le dan 6 más. ¿Cuántas tiene ahora?", "correcta" to "13", "opcionA" to "11", "opcionB" to "12", "opcionC" to "13", "dificultad" to 1, "explicacion" to "7 + 6 = 13 canicas."),
        mapOf("id" to "B-004", "enunciado" to "Había 15 pajaritos en un árbol y volaron 8. ¿Cuántos quedaron?", "correcta" to "7", "opcionA" to "5", "opcionB" to "6", "opcionC" to "7", "dificultad" to 1, "explicacion" to "15 - 8 = 7 pajaritos."),
        mapOf("id" to "B-005", "enunciado" to "5 + ___ = 12", "correcta" to "7", "opcionA" to "5", "opcionB" to "6", "opcionC" to "7", "dificultad" to 1, "explicacion" to "12 - 5 = 7."),
        mapOf("id" to "B-006", "enunciado" to "Si 9 + 4 = 13, ¿cuánto es 4 + 9?", "correcta" to "13", "opcionA" to "12", "opcionB" to "13", "opcionC" to "14", "dificultad" to 2, "explicacion" to "Propiedad conmutativa: el orden no cambia el resultado."),
        mapOf("id" to "B-007", "enunciado" to "¿Cuánto es el doble de 7?", "correcta" to "14", "opcionA" to "12", "opcionB" to "14", "opcionC" to "16", "dificultad" to 2, "explicacion" to "Doble de 7 = 7 + 7 = 14."),
        mapOf("id" to "B-008", "enunciado" to "Si 8 + 5 = 13, ¿cuánto es 13 - 5?", "correcta" to "8", "opcionA" to "7", "opcionB" to "8", "opcionC" to "9", "dificultad" to 2, "explicacion" to "La resta deshace la suma: 13 - 5 = 8."),
        mapOf("id" to "B-009", "enunciado" to "3 + 6 + ___ = 15", "correcta" to "6", "opcionA" to "4", "opcionB" to "5", "opcionC" to "6", "dificultad" to 2, "explicacion" to "3+6=9; 15-9=6."),
        mapOf("id" to "B-010", "enunciado" to "En una fiesta hay 9 niños y 7 niñas. Llegan 4 niños más. ¿Cuántos niños hay en total?", "correcta" to "13", "opcionA" to "13", "opcionB" to "14", "opcionC" to "15", "dificultad" to 3, "explicacion" to "9 + 4 = 13 niños (sin contar niñas)."),
        mapOf("id" to "B-011", "enunciado" to "Ana tenía 18 flores. Regaló 6 y luego encontró 4 más. ¿Cuántas tiene ahora?", "correcta" to "16", "opcionA" to "14", "opcionB" to "15", "opcionC" to "16", "dificultad" to 3, "explicacion" to "18-6=12; 12+4=16."),
        mapOf("id" to "B-012", "enunciado" to "El número misterioso: si le sumas 7 queda 15, si le restas 3 queda 5. ¿Cuál es el número?", "correcta" to "8", "opcionA" to "7", "opcionB" to "8", "opcionC" to "9", "dificultad" to 4, "explicacion" to "15-7=8 y 8-3=5. Las dos condiciones se cumplen con 8."),

        // CONJUNTO C: Decenas y unidades hasta 50 (C-001 a C-011)
        mapOf("id" to "C-001", "enunciado" to "En el número 32, ¿cuántas decenas hay?", "correcta" to "3", "opcionA" to "2", "opcionB" to "3", "opcionC" to "30", "dificultad" to 1, "explicacion" to "El 3 está en la posición de decenas = 30."),
        mapOf("id" to "C-002", "enunciado" to "¿Cómo se descompone el 27?", "correcta" to "b y c son correctas", "opcionA" to "2+7", "opcionB" to "20+7", "opcionC" to "2D+7U", "dificultad" to 1, "explicacion" to "27 = 20+7 = 2D+7U."),
        mapOf("id" to "C-003", "enunciado" to "¿Cómo se escribe en número 'cuarenta y tres'?", "correcta" to "43", "opcionA" to "34", "opcionB" to "40", "opcionC" to "43", "dificultad" to 1, "explicacion" to "Cuarenta=40, tres=3. Total: 43."),
        mapOf("id" to "C-004", "enunciado" to "¿Cuál es el sucesor de 39?", "correcta" to "40", "opcionA" to "38", "opcionB" to "39", "opcionC" to "40", "dificultad" to 1, "explicacion" to "Sucesor = número siguiente = 39+1=40."),
        mapOf("id" to "C-005", "enunciado" to "¿Cuánto es 20 + 30?", "correcta" to "50", "opcionA" to "23", "opcionB" to "50", "opcionC" to "230", "dificultad" to 2, "explicacion" to "2 decenas + 3 decenas = 5 decenas = 50."),
        mapOf("id" to "C-006", "enunciado" to "Hay 2 grupos de 10 galletas y 8 galletas sueltas. ¿Cuántas galletas hay?", "correcta" to "28", "opcionA" to "18", "opcionB" to "28", "opcionC" to "20", "dificultad" to 2, "explicacion" to "2×10 + 8 = 28 galletas."),
        mapOf("id" to "C-007", "enunciado" to "Ordena de menor a mayor: 45, 23, 38, 12", "correcta" to "12,23,38,45", "opcionA" to "12,23,38,45", "opcionB" to "45,38,23,12", "opcionC" to "12,38,23,45", "dificultad" to 2, "explicacion" to "Se ordena por decenas primero, luego unidades."),
        mapOf("id" to "C-008", "enunciado" to "Hay aproximadamente ___ decenas en 47.", "correcta" to "4", "opcionA" to "3", "opcionB" to "4", "opcionC" to "5", "dificultad" to 2, "explicacion" to "47 tiene 4 decenas completas (40) y 7 unidades sueltas."),
        mapOf("id" to "C-009", "enunciado" to "Tengo 4 billetes de 10 soles y 6 monedas de 1 sol. ¿Cuánto tengo en total?", "correcta" to "S/46", "opcionA" to "S/46", "opcionB" to "S/56", "opcionC" to "S/106", "dificultad" to 3, "explicacion" to "4×10 + 6×1 = 40+6 = S/46."),
        mapOf("id" to "C-010", "enunciado" to "Un granjero tiene 3 grupos de 10 naranjas y 7 sueltas. Vende 15. ¿Cuántas le quedan?", "correcta" to "22", "opcionA" to "17", "opcionB" to "22", "opcionC" to "25", "dificultad" to 3, "explicacion" to "3×10+7=37; 37-15=22."),
        mapOf("id" to "C-011", "enunciado" to "¿Qué números de 2 cifras tienen sus dígitos sumando 8? Escribe cuántos son.", "correcta" to "8 números", "opcionA" to "8 números", "opcionB" to "7 números", "opcionC" to "9 números", "dificultad" to 4, "explicacion" to "Son 8 números: 17,26,35,44,53,62,71,80"),

        // CONJUNTO D: Geometría (D-001 a D-011)
        mapOf("id" to "D-001", "enunciado" to "¿Cuántos lados tiene un triángulo?", "correcta" to "3", "opcionA" to "2", "opcionB" to "3", "opcionC" to "4", "dificultad" to 1, "explicacion" to "Tri = tres. El triángulo tiene 3 lados."),
        mapOf("id" to "D-002", "enunciado" to "¿Cuántos lados tiene un cuadrado?", "correcta" to "4", "opcionA" to "3", "opcionB" to "4", "opcionC" to "5", "dificultad" to 1, "explicacion" to "El cuadrado tiene 4 lados iguales."),
        mapOf("id" to "D-003", "enunciado" to "¿Qué cuerpo geométrico tiene forma de pelota?", "correcta" to "Esfera", "opcionA" to "Cubo", "opcionB" to "Cono", "opcionC" to "Esfera", "dificultad" to 1, "explicacion" to "La pelota es esférica."),
        mapOf("id" to "D-004", "enunciado" to "La pelota está ___ de la caja (dentro).", "correcta" to "dentro", "opcionA" to "encima", "opcionB" to "debajo", "opcionC" to "dentro", "dificultad" to 1, "explicacion" to "Dentro = en el interior del objeto."),
        mapOf("id" to "D-005", "enunciado" to "¿Cuántos vértices (esquinas) tiene un rectángulo?", "correcta" to "4", "opcionA" to "2", "opcionB" to "3", "opcionC" to "4", "dificultad" to 2, "explicacion" to "El rectángulo tiene 4 esquinas = 4 vértices."),
        mapOf("id" to "D-006", "enunciado" to "¿Cuántas caras tiene un cubo?", "correcta" to "6", "opcionA" to "4", "opcionB" to "5", "opcionC" to "6", "dificultad" to 2, "explicacion" to "El cubo tiene 6 caras cuadradas."),
        mapOf("id" to "D-007", "enunciado" to "Saliendo de casa: camina 3 pasos al frente, 2 a la derecha, 1 atrás. ¿En qué dirección neta se movió?", "correcta" to "Derecha y adelante", "opcionA" to "Derecha", "opcionB" to "Izquierda", "opcionC" to "Derecha y adelante", "dificultad" to 2, "explicacion" to "3 frente - 1 atrás = 2 frente; 2 derecha. Neto: frente-derecha."),
        mapOf("id" to "D-008", "enunciado" to "¿Cuál figura NO tiene lados rectos?", "correcta" to "Círculo", "opcionA" to "Cuadrado", "opcionB" to "Triángulo", "opcionC" to "Círculo", "dificultad" to 2, "explicacion" to "El círculo es curvo, no tiene lados rectos."),
        mapOf("id" to "D-009", "enunciado" to "¿Qué figura tiene 3 lados y 3 vértices?", "correcta" to "Triángulo", "opcionA" to "Cuadrado", "opcionB" to "Triángulo", "opcionC" to "Pentágono", "dificultad" to 3, "explicacion" to "3 lados y 3 vértices = triángulo."),
        mapOf("id" to "D-010", "enunciado" to "Si doblas una hoja por la mitad, ¿qué figura obtienes?", "correcta" to "Rectángulo", "opcionA" to "Triángulo", "opcionB" to "Cuadrado", "opcionC" to "Rectángulo", "dificultad" to 3, "explicacion" to "Una hoja doblada por la mitad forma un rectángulo."),
        mapOf("id" to "D-011", "enunciado" to "Una figura tiene 5 lados iguales y 5 vértices. ¿Cómo se llama? ¿Cuántos triángulos puedes trazar dentro?", "correcta" to "Pentágono; 3 triángulos", "opcionA" to "Pentágono; 3", "opcionB" to "Hexágono; 4", "opcionC" to "Pentágono; 2", "dificultad" to 4, "explicacion" to "Penta=5 lados = pentágono. Desde un vértice se trazan 5-2=3 triángulos."),

        // CONJUNTO E: Medición (E-001 a E-010)
        mapOf("id" to "E-001", "enunciado" to "¿Cuántos días tiene una semana?", "correcta" to "7", "opcionA" to "5", "opcionB" to "6", "opcionC" to "7", "dificultad" to 1, "explicacion" to "Lunes a domingo = 7 días."),
        mapOf("id" to "E-002", "enunciado" to "¿Cuántos meses tiene un año?", "correcta" to "12", "opcionA" to "10", "opcionB" to "11", "opcionC" to "12", "dificultad" to 1, "explicacion" to "Enero a diciembre = 12 meses."),
        mapOf("id" to "E-003", "enunciado" to "Tienes 3 monedas de S/1. ¿Cuánto tienes en total?", "correcta" to "S/3", "opcionA" to "S/1", "opcionB" to "S/2", "opcionC" to "S/3", "dificultad" to 1, "explicacion" to "3 × S/1 = S/3."),
        mapOf("id" to "E-004", "enunciado" to "¿Qué es más largo: un lápiz de 15 cm o una regla de 30 cm?", "correcta" to "La regla", "opcionA" to "El lápiz", "opcionB" to "La regla", "opcionC" to "Son iguales", "dificultad" to 1, "explicacion" to "30 cm > 15 cm."),
        mapOf("id" to "E-005", "enunciado" to "Tienes S/5 y compras un cuaderno que cuesta S/3. ¿Cuánto te sobra?", "correcta" to "S/2", "opcionA" to "S/1", "opcionB" to "S/2", "opcionC" to "S/3", "dificultad" to 2, "explicacion" to "S/5 - S/3 = S/2."),
        mapOf("id" to "E-006", "enunciado" to "Una cuerda mide 45 cm y otra mide 28 cm. ¿Cuánto miden juntas?", "correcta" to "73 cm", "opcionA" to "63", "opcionB" to "73", "opcionC" to "83", "dificultad" to 2, "explicacion" to "45 + 28 = 73 cm."),
        mapOf("id" to "E-007", "enunciado" to "Si hoy es martes, ¿qué día será en 3 días?", "correcta" to "Viernes", "opcionA" to "Miércoles", "opcionB" to "Jueves", "opcionC" to "Viernes", "dificultad" to 2, "explicacion" to "Martes→Miércoles→Jueves→Viernes."),
        mapOf("id" to "E-008", "enunciado" to "Mamá tiene S/10. Compra pan por S/3 y leche por S/4. ¿Cuánto le queda?", "correcta" to "S/3", "opcionA" to "S/1", "opcionB" to "S/2", "opcionC" to "S/3", "dificultad" to 3, "explicacion" to "3+4=S/7 gastado; 10-7=S/3."),
        mapOf("id" to "E-009", "enunciado" to "3 libros miden 8 cm, 12 cm y 15 cm. ¿Cuánto miden todos juntos?", "correcta" to "35 cm", "opcionA" to "30", "opcionB" to "35", "opcionC" to "38", "dificultad" to 3, "explicacion" to "8+12+15=35 cm."),
        mapOf("id" to "E-010", "enunciado" to "Si cada mes tiene 4 semanas y un año tiene 12 meses, ¿cuántas semanas tiene el año aproximadamente?", "correcta" to "48 semanas aprox.", "opcionA" to "36", "opcionB" to "48", "opcionC" to "52", "dificultad" to 4, "explicacion" to "4 × 12 = 48 semanas (aprox., el año real tiene 52)."),

        // CONJUNTO F: Estadística y patrones (F-001 a F-010)
        mapOf("id" to "F-001", "enunciado" to "Patrón: ○△○△○___", "correcta" to "△", "opcionA" to "○", "opcionB" to "△", "opcionC" to "□", "dificultad" to 1, "explicacion" to "Alterna ○ y △. Después de ○ viene △."),
        mapOf("id" to "F-002", "enunciado" to "2, 4, 6, 8, ___", "correcta" to "10", "opcionA" to "9", "opcionB" to "10", "opcionC" to "11", "dificultad" to 1, "explicacion" to "Suma 2 cada vez."),
        mapOf("id" to "F-003", "enunciado" to "¿En qué grupo van: pelota, naranja, globo, moneda?", "correcta" to "Círculos", "opcionA" to "Triángulos", "opcionB" to "Cuadrados", "opcionC" to "Círculos", "dificultad" to 1, "explicacion" to "Todos tienen forma redondeada."),
        mapOf("id" to "F-004", "enunciado" to "Pictograma: 🐕🐕🐕=3 perros, 🐈🐈=2 gatos. ¿Qué animal hay más?", "correcta" to "Perros", "opcionA" to "Perros", "opcionB" to "Gatos", "opcionC" to "Igual", "dificultad" to 1, "explicacion" to "3 > 2."),
        mapOf("id" to "F-005", "enunciado" to "Patrón: ○○△○○△___", "correcta" to "○", "opcionA" to "○", "opcionB" to "△", "opcionC" to "□", "dificultad" to 2, "explicacion" to "Patrón AAB: ○○△. Después de △ vuelve a empezar con ○."),
        mapOf("id" to "F-006", "enunciado" to "1, 3, 6, 10, ___ (suma 2, 3, 4, ...)", "correcta" to "15", "opcionA" to "13", "opcionB" to "14", "opcionC" to "15", "dificultad" to 2, "explicacion" to "Diferencias: +2, +3, +4, +5. 10+5=15."),
        mapOf("id" to "F-007", "enunciado" to "En una encuesta: 5 niños prefieren fútbol, 3 básquet, 7 voley. ¿Cuál es el deporte más popular?", "correcta" to "Voley", "opcionA" to "Fútbol", "opcionB" to "Básquet", "opcionC" to "Voley", "dificultad" to 2, "explicacion" to "7 > 5 > 3."),
        mapOf("id" to "F-008", "enunciado" to "Clasifica: cuadrado rojo, círculo azul, cuadrado azul, círculo rojo. ¿Cuántos son azules?", "correcta" to "2", "opcionA" to "1", "opcionB" to "2", "opcionC" to "3", "dificultad" to 3, "explicacion" to "Círculo azul + cuadrado azul = 2 azules."),
        mapOf("id" to "F-009", "enunciado" to "Tabla: lunes=4, martes=7, miércoles=3, jueves=6. ¿Qué día se vendió menos?", "correcta" to "Miércoles", "opcionA" to "Lunes", "opcionB" to "Martes", "opcionC" to "Miércoles", "dificultad" to 3, "explicacion" to "3 es el número más pequeño."),
        mapOf("id" to "F-010", "enunciado" to "Crea un patrón numérico que empiece en 3, tenga 6 términos y sume de 3 en 3.", "correcta" to "3,6,9,12,15,18", "opcionA" to "3,6,9,12,15,18", "opcionB" to "3,5,7,9,11,13", "opcionC" to "3,4,5,6,7,8", "dificultad" to 4, "explicacion" to "Suma 3 cada vez: 3→6→9→12→15→18.")
    )

    // ============================================
    // SEGUNDO DE PRIMARIA - 59 EJERCICIOS
    // ============================================

    private val ejerciciosSegundo = listOf(
        // CONJUNTO A: Números hasta 100 (G-001 a G-010)
        mapOf("id" to "G-001", "enunciado" to "¿Cómo se escribe en número 'ochenta y cinco'?", "correcta" to "85", "opcionA" to "58", "opcionB" to "80", "opcionC" to "85", "dificultad" to 1, "explicacion" to "Ochenta=80, cinco=5. 80+5=85."),
        mapOf("id" to "G-002", "enunciado" to "En 73, el dígito 7 representa:", "correcta" to "7 decenas", "opcionA" to "7 unidades", "opcionB" to "7 decenas", "opcionC" to "70 decenas", "dificultad" to 1, "explicacion" to "7 en posición de decenas = 70."),
        mapOf("id" to "G-003", "enunciado" to "Serie de 10 en 10: 30, 40, 50, ___, 70", "correcta" to "60", "opcionA" to "55", "opcionB" to "60", "opcionC" to "65", "dificultad" to 1, "explicacion" to "Suma 10 cada vez."),
        mapOf("id" to "G-004", "enunciado" to "¿El número 46 es par o impar?", "correcta" to "Par", "opcionA" to "Par", "opcionB" to "Impar", "opcionC" to "", "dificultad" to 1, "explicacion" to "Termina en 6 (dígito par)."),
        mapOf("id" to "G-005", "enunciado" to "¿Cuál es el mayor: 64, 46, 74, 47?", "correcta" to "74", "opcionA" to "46", "opcionB" to "47", "opcionC" to "74", "dificultad" to 2, "explicacion" to "7 decenas es más que 6 o 4 decenas."),
        mapOf("id" to "G-006", "enunciado" to "Descompón 96: 96 = ___D + ___U = ___ + ___", "correcta" to "9D+6U=90+6", "opcionA" to "9D+6U=90+6", "opcionB" to "8D+16U=80+16", "opcionC" to "10D-4U=100-4", "dificultad" to 2, "explicacion" to "9 decenas y 6 unidades."),
        mapOf("id" to "G-007", "enunciado" to "¿Cuántas decenas hay en 100?", "correcta" to "10", "opcionA" to "1", "opcionB" to "5", "opcionC" to "10", "dificultad" to 2, "explicacion" to "10 × 10 = 100. Una centena = 10 decenas."),
        mapOf("id" to "G-008", "enunciado" to "¿Cuántos números pares hay entre 50 y 60 (sin incluirlos)?", "correcta" to "4", "opcionA" to "3", "opcionB" to "4", "opcionC" to "5", "dificultad" to 3, "explicacion" to "Pares: 52, 54, 56, 58. Son 4."),
        mapOf("id" to "G-009", "enunciado" to "Ordena de mayor a menor: 78, 87, 17, 71", "correcta" to "87,78,71,17", "opcionA" to "17,71,78,87", "opcionB" to "87,78,71,17", "opcionC" to "78,87,71,17", "dificultad" to 3, "explicacion" to "Por decenas: 87>78>71>17."),
        mapOf("id" to "G-010", "enunciado" to "El número tiene 8 decenas y sus cifras suman 13. ¿Qué número es?", "correcta" to "85", "opcionA" to "85", "opcionB" to "86", "opcionC" to "87", "dificultad" to 4, "explicacion" to "8D: la decena es 8. 8+5=13. El número es 85."),

        // CONJUNTO B: Doble, triple y mitad (H-001 a H-009)
        mapOf("id" to "H-001", "enunciado" to "¿Cuánto es el doble de 9?", "correcta" to "18", "opcionA" to "16", "opcionB" to "17", "opcionC" to "18", "dificultad" to 1, "explicacion" to "Doble de 9 = 9+9 = 18."),
        mapOf("id" to "H-002", "enunciado" to "¿Cuánto es el triple de 6?", "correcta" to "18", "opcionA" to "12", "opcionB" to "16", "opcionC" to "18", "dificultad" to 1, "explicacion" to "Triple de 6 = 6+6+6 = 18."),
        mapOf("id" to "H-003", "enunciado" to "¿Cuánto es la mitad de 24?", "correcta" to "12", "opcionA" to "10", "opcionB" to "11", "opcionC" to "12", "dificultad" to 1, "explicacion" to "24 ÷ 2 = 12."),
        mapOf("id" to "H-004", "enunciado" to "Luis tiene 8 caramelos. Su hermano tiene el doble. ¿Cuántos tiene el hermano?", "correcta" to "16", "opcionA" to "10", "opcionB" to "14", "opcionC" to "16", "dificultad" to 2, "explicacion" to "Doble de 8 = 16."),
        mapOf("id" to "H-005", "enunciado" to "En una tienda hay 15 naranjas. Se vende la mitad. ¿Cuántas quedan?", "correcta" to "No es posible", "opcionA" to "7", "opcionB" to "8", "opcionC" to "No es posible", "dificultad" to 2, "explicacion" to "15 es número impar, no tiene mitad exacta en naturales."),
        mapOf("id" to "H-006", "enunciado" to "Una caja tiene 7 chocolates. ¿Cuántos chocolates hay en 3 cajas iguales?", "correcta" to "21", "opcionA" to "14", "opcionB" to "21", "opcionC" to "28", "dificultad" to 2, "explicacion" to "Triple de 7 = 7×3 = 21."),
        mapOf("id" to "H-007", "enunciado" to "El triple de un número es 24. ¿Cuál es ese número?", "correcta" to "8", "opcionA" to "6", "opcionB" to "7", "opcionC" to "8", "dificultad" to 3, "explicacion" to "24 ÷ 3 = 8."),
        mapOf("id" to "H-008", "enunciado" to "Tengo 12 pegatinas. Mi amiga tiene el triple. ¿Cuántas más que yo tiene mi amiga?", "correcta" to "24", "opcionA" to "24", "opcionB" to "36", "opcionC" to "48", "dificultad" to 3, "explicacion" to "Triple: 12×3=36; 36−12=24 más."),
        mapOf("id" to "H-009", "enunciado" to "El doble de un número menos 5 es igual a 17. ¿Cuál es el número?", "correcta" to "11", "opcionA" to "9", "opcionB" to "10", "opcionC" to "11", "dificultad" to 4, "explicacion" to "2×n−5=17; 2n=22; n=11."),

        // CONJUNTO C: Adición y sustracción con reagrupación (I-001 a I-010)
        mapOf("id" to "I-001", "enunciado" to "¿Cuánto es 43 + 25?", "correcta" to "68", "opcionA" to "68", "opcionB" to "78", "opcionC" to "98", "dificultad" to 1, "explicacion" to "3+5=8; 4+2=6. Sin reagrupar: 68."),
        mapOf("id" to "I-002", "enunciado" to "¿Cuánto es 67 - 34?", "correcta" to "33", "opcionA" to "23", "opcionB" to "33", "opcionC" to "43", "dificultad" to 1, "explicacion" to "7−4=3; 6−3=3. Sin reagrupar: 33."),
        mapOf("id" to "I-003", "enunciado" to "¿Cuánto es 47 + 36?", "correcta" to "83", "opcionA" to "73", "opcionB" to "82", "opcionC" to "83", "dificultad" to 2, "explicacion" to "7+6=13 (escribo 3, llevo 1); 4+3+1=8. Total: 83."),
        mapOf("id" to "I-004", "enunciado" to "¿Cuánto es 62 - 38?", "correcta" to "24", "opcionA" to "24", "opcionB" to "25", "opcionC" to "34", "dificultad" to 2, "explicacion" to "Presto: 12−8=4; 5−3=2. Total: 24."),
        mapOf("id" to "I-005", "enunciado" to "En el mercado hay 54 limones y 37 naranjas. ¿Cuántas frutas hay en total?", "correcta" to "91", "opcionA" to "81", "opcionB" to "90", "opcionC" to "91", "dificultad" to 2, "explicacion" to "54+37=91 frutas."),
        mapOf("id" to "I-006", "enunciado" to "Un costal tenía 80 papas. Se vendieron 43. ¿Cuántas quedan?", "correcta" to "37", "opcionA" to "33", "opcionB" to "36", "opcionC" to "37", "dificultad" to 2, "explicacion" to "80−43=37 papas."),
        mapOf("id" to "I-007", "enunciado" to "Si 35+28=63, ¿cómo lo verificamos?", "correcta" to "Ambas", "opcionA" to "63−28=35", "opcionB" to "63−35=28", "opcionC" to "Ambas", "dificultad" to 3, "explicacion" to "La resta inversa verifica la suma."),
        mapOf("id" to "I-008", "enunciado" to "Juana tiene S/50. Gasta S/23 en útiles y S/14 en recreo. ¿Cuánto le queda?", "correcta" to "S/13", "opcionA" to "S/11", "opcionB" to "S/12", "opcionC" to "S/13", "dificultad" to 3, "explicacion" to "23+14=37; 50−37=S/13."),
        mapOf("id" to "I-009", "enunciado" to "Hay 45 niños y 38 niñas en primaria. La directora tiene 52 años. ¿Cuántos alumnos hay?", "correcta" to "83", "opcionA" to "83", "opcionB" to "90", "opcionC" to "130", "dificultad" to 3, "explicacion" to "45+38=83 alumnos. La edad de la directora no sirve."),
        mapOf("id" to "I-010", "enunciado" to "A + B = 75. A − B = 15. ¿Cuánto es A? ¿Cuánto es B?", "correcta" to "A=45, B=30", "opcionA" to "A=45, B=30", "opcionB" to "A=50, B=25", "opcionC" to "A=40, B=35", "dificultad" to 4, "explicacion" to "(75+15)÷2=45; (75−15)÷2=30."),

        // CONJUNTO D: Multiplicación tablas 1-5 (J-001 a J-010)
        mapOf("id" to "J-001", "enunciado" to "¿Cuánto es 2 × 7?", "correcta" to "14", "opcionA" to "12", "opcionB" to "14", "opcionC" to "16", "dificultad" to 1, "explicacion" to "2 × 7 = 14."),
        mapOf("id" to "J-002", "enunciado" to "¿Cuánto es 3 × 8?", "correcta" to "24", "opcionA" to "21", "opcionB" to "24", "opcionC" to "27", "dificultad" to 1, "explicacion" to "3 × 8 = 24."),
        mapOf("id" to "J-003", "enunciado" to "¿Cuánto es 5 × 7?", "correcta" to "35", "opcionA" to "30", "opcionB" to "35", "opcionC" to "40", "dificultad" to 1, "explicacion" to "5 × 7 = 35."),
        mapOf("id" to "J-004", "enunciado" to "4 + 4 + 4 = ___ × 4", "correcta" to "3", "opcionA" to "1", "opcionB" to "2", "opcionC" to "3", "dificultad" to 1, "explicacion" to "Tres veces 4 = 3 × 4 = 12."),
        mapOf("id" to "J-005", "enunciado" to "¿Cuánto es 4 × 6?", "correcta" to "24", "opcionA" to "20", "opcionB" to "22", "opcionC" to "24", "dificultad" to 2, "explicacion" to "4 × 6 = 24."),
        mapOf("id" to "J-006", "enunciado" to "¿Es igual 3 × 7 que 7 × 3?", "correcta" to "Sí, ambas dan 21", "opcionA" to "Sí, ambas dan 21", "opcionB" to "No, son diferentes", "opcionC" to "Depende", "dificultad" to 2, "explicacion" to "Propiedad conmutativa: el orden no cambia el resultado."),
        mapOf("id" to "J-007", "enunciado" to "Hay 6 canastas con 4 naranjas cada una. ¿Cuántas naranjas hay?", "correcta" to "24", "opcionA" to "10", "opcionB" to "20", "opcionC" to "24", "dificultad" to 2, "explicacion" to "6 × 4 = 24 naranjas."),
        mapOf("id" to "J-008", "enunciado" to "5 filas de 8 sillas. ¿Cuántas sillas hay? Si ya hay 12 personas sentadas, ¿cuántos asientos libres quedan?", "correcta" to "40/28", "opcionA" to "40/28", "opcionB" to "38/26", "opcionC" to "40/18", "dificultad" to 3, "explicacion" to "5×8=40; 40−12=28 asientos libres."),
        mapOf("id" to "J-009", "enunciado" to "¿Cuánto es 1 × 99?", "correcta" to "99", "opcionA" to "1", "opcionB" to "9", "opcionC" to "99", "dificultad" to 1, "explicacion" to "Cualquier número × 1 = ese número."),
        mapOf("id" to "J-010", "enunciado" to "Encuentro 3 números distintos entre el 1 y el 5 cuyo producto sea 24. ¿Cuáles son?", "correcta" to "2, 3 y 4", "opcionA" to "2, 3 y 4", "opcionB" to "1, 4 y 6", "opcionC" to "2, 2 y 6", "dificultad" to 4, "explicacion" to "2 × 3 × 4 = 24."),

        // CONJUNTO E: Geometría y medición (K-001 a K-010)
        mapOf("id" to "K-001", "enunciado" to "¿Cuántos centímetros hay en 1 metro?", "correcta" to "100", "opcionA" to "10", "opcionB" to "50", "opcionC" to "100", "dificultad" to 1, "explicacion" to "1 m = 100 cm."),
        mapOf("id" to "K-002", "enunciado" to "¿Cuántos minutos tiene 1 hora?", "correcta" to "60", "opcionA" to "30", "opcionB" to "50", "opcionC" to "60", "dificultad" to 1, "explicacion" to "1 hora = 60 minutos."),
        mapOf("id" to "K-003", "enunciado" to "Tienes 2 billetes de S/5 y 3 monedas de S/1. ¿Cuánto tienes?", "correcta" to "S/13", "opcionA" to "S/11", "opcionB" to "S/13", "opcionC" to "S/15", "dificultad" to 1, "explicacion" to "2×5 + 3×1 = 10+3 = S/13."),
        mapOf("id" to "K-004", "enunciado" to "¿Cuántos días tiene enero?", "correcta" to "31", "opcionA" to "28", "opcionB" to "30", "opcionC" to "31", "dificultad" to 1, "explicacion" to "Enero tiene 31 días."),
        mapOf("id" to "K-005", "enunciado" to "Una mesa mide 120 cm. ¿Cuántos metros y cm son?", "correcta" to "1 m y 20 cm", "opcionA" to "1 m y 20 cm", "opcionB" to "12 m", "opcionC" to "1.2 cm", "dificultad" to 2, "explicacion" to "100 cm = 1 m; sobran 20 cm."),
        mapOf("id" to "K-006", "enunciado" to "Cinta roja: 85 cm. Cinta azul: 47 cm. ¿Cuánto miden juntas?", "correcta" to "132 cm", "opcionA" to "112", "opcionB" to "122", "opcionC" to "132", "dificultad" to 2, "explicacion" to "85+47=132 cm."),
        mapOf("id" to "K-007", "enunciado" to "¿Cuántos gramos tiene 1 kilogramo?", "correcta" to "1000", "opcionA" to "10", "opcionB" to "100", "opcionC" to "1000", "dificultad" to 2, "explicacion" to "1 kg = 1000 g."),
        mapOf("id" to "K-008", "enunciado" to "Si son las 3:00 p.m. y han pasado 2 horas y 30 minutos, ¿qué hora es?", "correcta" to "5:30 p.m.", "opcionA" to "4:30", "opcionB" to "5:00", "opcionC" to "5:30", "dificultad" to 3, "explicacion" to "3:00 + 2h 30min = 5:30 p.m."),
        mapOf("id" to "K-009", "enunciado" to "Compras 3 jugos de S/4 cada uno y pagas con S/20. ¿Cuánto te dan de vuelto?", "correcta" to "S/8", "opcionA" to "S/7", "opcionB" to "S/8", "opcionC" to "S/9", "dificultad" to 3, "explicacion" to "3×4=S/12; 20−12=S/8."),
        mapOf("id" to "K-010", "enunciado" to "Una tira mide 2 m y 40 cm. La corto en 4 partes iguales. ¿Cuánto mide cada parte en cm?", "correcta" to "60 cm", "opcionA" to "50 cm", "opcionB" to "55 cm", "opcionC" to "60 cm", "dificultad" to 4, "explicacion" to "2m 40cm = 240 cm; 240÷4 = 60 cm."),

        // CONJUNTO F: Fracciones, estadística y patrones (L-001 a L-010)
        mapOf("id" to "L-001", "enunciado" to "Una pizza se divide en 4 partes iguales. Si comes 1, ¿qué fracción comiste?", "correcta" to "1/4", "opcionA" to "1/2", "opcionB" to "1/3", "opcionC" to "1/4", "dificultad" to 1, "explicacion" to "1 de 4 partes iguales = 1/4."),
        mapOf("id" to "L-002", "enunciado" to "¿Qué fracción es mayor: 1/2 ó 1/4?", "correcta" to "1/2", "opcionA" to "1/4", "opcionB" to "1/2", "opcionC" to "Son iguales", "dificultad" to 1, "explicacion" to "El trozo 1/2 es más grande que 1/4."),
        mapOf("id" to "L-003", "enunciado" to "Barras: rojo=6, azul=4, verde=8. ¿Qué color tiene más?", "correcta" to "Verde", "opcionA" to "Rojo", "opcionB" to "Azul", "opcionC" to "Verde", "dificultad" to 1, "explicacion" to "8 es el valor más alto."),
        mapOf("id" to "L-004", "enunciado" to "5, 10, 15, 20, ___", "correcta" to "25", "opcionA" to "22", "opcionB" to "24", "opcionC" to "25", "dificultad" to 1, "explicacion" to "Suma 5 cada vez."),
        mapOf("id" to "L-005", "enunciado" to "¿Cuánto es 1/2 de 18?", "correcta" to "9", "opcionA" to "8", "opcionB" to "9", "opcionC" to "10", "dificultad" to 2, "explicacion" to "18 ÷ 2 = 9."),
        mapOf("id" to "L-006", "enunciado" to "30, 26, 22, 18, ___", "correcta" to "14", "opcionA" to "12", "opcionB" to "14", "opcionC" to "16", "dificultad" to 2, "explicacion" to "Resta 4 cada vez. 18−4=14."),
        mapOf("id" to "L-007", "enunciado" to "Tabla: helado=7, torta=5, fruta=9. ¿Cuántos niños votaron en total?", "correcta" to "21", "opcionA" to "19", "opcionB" to "20", "opcionC" to "21", "dificultad" to 2, "explicacion" to "7+5+9=21 niños."),
        mapOf("id" to "L-008", "enunciado" to "Tabla: niños con mascota: 1A=4, 1B=6; sin mascota: 1A=16, 1B=14. ¿Total alumnos?", "correcta" to "40", "opcionA" to "30", "opcionB" to "40", "opcionC" to "38", "dificultad" to 3, "explicacion" to "4+6+16+14=40 alumnos."),
        mapOf("id" to "L-009", "enunciado" to "Ordena de menor a mayor: 1/4, 1/2, 1/3", "correcta" to "1/4 < 1/3 < 1/2", "opcionA" to "1/4, 1/3, 1/2", "opcionB" to "1/2, 1/3, 1/4", "opcionC" to "1/3, 1/4, 1/2", "dificultad" to 3, "explicacion" to "A mayor denominador, fracción más pequeña (mismo numerador=1)."),
        mapOf("id" to "L-010", "enunciado" to "¿Qué fracción de 24 es 6? Escribe la fracción y simplifica.", "correcta" to "1/4", "opcionA" to "1/4", "opcionB" to "1/3", "opcionC" to "1/2", "dificultad" to 4, "explicacion" to "6/24 = 1/4.")
    )

    // ============================================
    // TERCERO DE PRIMARIA - 76 EJERCICIOS
    // ============================================

    private val ejerciciosTercero = listOf(
        // CONJUNTO A: Números hasta 9999 (M-001 a M-010)
        mapOf("id" to "M-001", "enunciado" to "En 4783, el dígito 7 representa:", "correcta" to "700", "opcionA" to "7", "opcionB" to "70", "opcionC" to "700", "dificultad" to 1, "explicacion" to "El 7 está en centenas = 700."),
        mapOf("id" to "M-002", "enunciado" to "Descompón 5348", "correcta" to "5000+300+40+8", "opcionA" to "5+3+4+8", "opcionB" to "5000+348", "opcionC" to "5000+300+40+8", "dificultad" to 1, "explicacion" to "5M+3C+4D+8U."),
        mapOf("id" to "M-003", "enunciado" to "¿Cómo se escribe 'cuatro mil doscientos treinta y uno'?", "correcta" to "4231", "opcionA" to "4123", "opcionB" to "4213", "opcionC" to "4231", "dificultad" to 1, "explicacion" to "Cuatro mil=4000, doscientos=200, treinta=30, uno=1."),
        mapOf("id" to "M-004", "enunciado" to "¿Cuál es mayor: 3879 ó 3987?", "correcta" to "3987", "opcionA" to "3879", "opcionB" to "3987", "opcionC" to "Son iguales", "dificultad" to 1, "explicacion" to "Mismas unidades de millar y centenas. Decenas: 8<9."),
        mapOf("id" to "M-005", "enunciado" to "Redondea 4762 a la centena más cercana.", "correcta" to "4800", "opcionA" to "4700", "opcionB" to "4760", "opcionC" to "4800", "dificultad" to 2, "explicacion" to "62 está más cerca de 100 que de 0. Se redondea hacia arriba."),
        mapOf("id" to "M-006", "enunciado" to "Serie de 1000 en 1000: 2000, 3000, ___, 5000, ___", "correcta" to "4000 y 6000", "opcionA" to "3500 y 5500", "opcionB" to "4000 y 6000", "opcionC" to "3000 y 4000", "dificultad" to 2, "explicacion" to "Suma 1000 cada término."),
        mapOf("id" to "M-007", "enunciado" to "¿Qué número romano es XIV?", "correcta" to "14", "opcionA" to "11", "opcionB" to "12", "opcionC" to "14", "dificultad" to 2, "explicacion" to "X=10, IV=4 (5−1). XIV=14."),
        mapOf("id" to "M-008", "enunciado" to "Ordena de mayor a menor: 1234, 4321, 2143, 3412", "correcta" to "4321,3412,2143,1234", "opcionA" to "1234,2143,3412,4321", "opcionB" to "4321,3412,2143,1234", "opcionC" to "4321,2143,3412,1234", "dificultad" to 3, "explicacion" to "Por unidades de millar: 4>3>2>1."),
        mapOf("id" to "M-009", "enunciado" to "Soy un número de 4 cifras. Mis millares son 6. Mis centenas son el doble de mis millares. Mi número es mayor que 6400. ¿Qué número soy?", "correcta" to "Cualquier número 6200-6299", "opcionA" to "6200", "opcionB" to "6300", "opcionC" to "Cualquier número 6200-6299", "dificultad" to 3, "explicacion" to "6M, 2C (doble de 6 no aplica bien)."),
        mapOf("id" to "M-010", "enunciado" to "¿Cuántos números de 4 cifras se pueden formar con los dígitos 1, 2, 3, 4 sin repetir?", "correcta" to "24", "opcionA" to "16", "opcionB" to "20", "opcionC" to "24", "dificultad" to 4, "explicacion" to "4×3×2×1 = 24 números (permutaciones)."),

        // CONJUNTO B: Las 4 operaciones (N-001 a N-010)
        mapOf("id" to "N-001", "enunciado" to "¿Cuánto es 456 + 278?", "correcta" to "734", "opcionA" to "624", "opcionB" to "724", "opcionC" to "734", "dificultad" to 1, "explicacion" to "6+8=14(llevo 1); 5+7+1=13(llevo 1); 4+2+1=7. Total:734."),
        mapOf("id" to "N-002", "enunciado" to "¿Cuánto es 800 - 364?", "correcta" to "436", "opcionA" to "346", "opcionB" to "436", "opcionC" to "436", "dificultad" to 1, "explicacion" to "800−364=436."),
        mapOf("id" to "N-003", "enunciado" to "¿Cuánto es 34 × 6?", "correcta" to "204", "opcionA" to "194", "opcionB" to "200", "opcionC" to "204", "dificultad" to 1, "explicacion" to "34×6: 4×6=24(llevo 2); 3×6+2=20. Total: 204."),
        mapOf("id" to "N-004", "enunciado" to "¿Cuánto es 84 ÷ 7?", "correcta" to "12", "opcionA" to "10", "opcionB" to "11", "opcionC" to "12", "dificultad" to 1, "explicacion" to "84 ÷ 7 = 12 (tabla del 7)."),
        mapOf("id" to "N-005", "enunciado" to "¿Cuánto es 23 × 14?", "correcta" to "322", "opcionA" to "312", "opcionB" to "318", "opcionC" to "322", "dificultad" to 2, "explicacion" to "23×4=92; 23×10=230; 92+230=322."),
        mapOf("id" to "N-006", "enunciado" to "¿Cuánto es 37 ÷ 5? (cociente y residuo)", "correcta" to "7 resto 2", "opcionA" to "7 resto 2", "opcionB" to "7 resto 3", "opcionC" to "8 resto 0", "dificultad" to 2, "explicacion" to "5×7=35; 37−35=2. Cociente=7, Residuo=2."),
        mapOf("id" to "N-007", "enunciado" to "¿Cuánto es 5 × 8 − 12 + 6?", "correcta" to "34", "opcionA" to "30", "opcionB" to "34", "opcionC" to "38", "dificultad" to 2, "explicacion" to "5×8=40; 40−12=28; 28+6=34."),
        mapOf("id" to "N-008", "enunciado" to "Una fábrica produce 125 cajas por hora. En 8 horas, ¿cuántas cajas produce? Si cada caja tiene 24 unidades, ¿cuántas unidades en total?", "correcta" to "1000 cajas / 24000 unidades", "opcionA" to "1000/24000", "opcionB" to "1000/240", "opcionC" to "1000/2400", "dificultad" to 3, "explicacion" to "125×8=1000 cajas; 1000×24=24000 unidades."),
        mapOf("id" to "N-009", "enunciado" to "760 alumnos se forman en filas de 8. ¿Cuántas filas completas hay? ¿Cuántos alumnos sobran?", "correcta" to "95 filas, 0 sobran", "opcionA" to "95 filas, 0 sobran", "opcionB" to "90 filas, 5 sobran", "opcionC" to "94 filas, 4 sobran", "dificultad" to 3, "explicacion" to "760÷8=95 exacto, sin residuo."),
        mapOf("id" to "N-010", "enunciado" to "El producto de dos números consecutivos es 182. ¿Cuáles son esos números?", "correcta" to "13 y 14", "opcionA" to "12 y 13", "opcionB" to "13 y 14", "opcionC" to "14 y 15", "dificultad" to 4, "explicacion" to "13×14=182."),

        // CONJUNTO C: Fracciones equivalentes (O-001 a O-012)
        mapOf("id" to "O-001", "enunciado" to "En 5/8, el numerador es:", "correcta" to "5", "opcionA" to "5", "opcionB" to "8", "opcionC" to "58", "dificultad" to 1, "explicacion" to "El número de arriba (partes tomadas) es el numerador."),
        mapOf("id" to "O-002", "enunciado" to "¿Es 7/5 una fracción propia o impropia?", "correcta" to "Impropia", "opcionA" to "Propia", "opcionB" to "Impropia", "opcionC" to "Mixta", "dificultad" to 1, "explicacion" to "Numerador (7) > denominador (5) → impropia."),
        mapOf("id" to "O-003", "enunciado" to "¿Cuál fracción es equivalente a 1/2?", "correcta" to "3/6", "opcionA" to "1/4", "opcionB" to "2/3", "opcionC" to "3/6", "dificultad" to 1, "explicacion" to "1/2 = 3/6 (multiplicar por 3 arriba y abajo)."),
        mapOf("id" to "O-004", "enunciado" to "¿Cuánto es 2/7 + 3/7?", "correcta" to "5/7", "opcionA" to "5/7", "opcionB" to "5/14", "opcionC" to "6/7", "dificultad" to 1, "explicacion" to "Igual denominador: sumar numeradores. 2+3=5, denominador 7."),
        mapOf("id" to "O-005", "enunciado" to "¿Cuál es mayor: 4/9 ó 6/9?", "correcta" to "6/9", "opcionA" to "4/9", "opcionB" to "6/9", "opcionC" to "Iguales", "dificultad" to 2, "explicacion" to "Igual denominador: mayor numerador = fracción mayor."),
        mapOf("id" to "O-006", "enunciado" to "¿Cuánto es 1/3 de 27?", "correcta" to "9", "opcionA" to "7", "opcionB" to "8", "opcionC" to "9", "dificultad" to 2, "explicacion" to "27 ÷ 3 = 9."),
        mapOf("id" to "O-007", "enunciado" to "¿Cuánto es 2/5 de 30?", "correcta" to "12", "opcionA" to "6", "opcionB" to "10", "opcionC" to "12", "dificultad" to 2, "explicacion" to "30÷5=6 (un quinto); 6×2=12 (dos quintos)."),
        mapOf("id" to "O-008", "enunciado" to "¿Cuánto es 5/8 − 2/8?", "correcta" to "3/8", "opcionA" to "3/8", "opcionB" to "3/16", "opcionC" to "7/8", "dificultad" to 2, "explicacion" to "5−2=3. Denominador sigue siendo 8."),
        mapOf("id" to "O-009", "enunciado" to "Simplifica 6/9", "correcta" to "2/3", "opcionA" to "1/3", "opcionB" to "2/3", "opcionC" to "3/4", "dificultad" to 3, "explicacion" to "MCD(6,9)=3; 6/3=2; 9/3=3. Resultado: 2/3."),
        mapOf("id" to "O-010", "enunciado" to "¿Cuál es mayor: 3/4 ó 2/3?", "correcta" to "3/4", "opcionA" to "3/4", "opcionB" to "2/3", "opcionC" to "Iguales", "dificultad" to 3, "explicacion" to "3/4=9/12; 2/3=8/12. 9>8 → 3/4 > 2/3."),
        mapOf("id" to "O-011", "enunciado" to "Un camino mide 500 m. Jorge recorre 2/5 el primer día y 1/4 el segundo. ¿Cuántos metros recorrió en total?", "correcta" to "325 m", "opcionA" to "200m+125m=325m", "opcionB" to "300+200=500", "opcionC" to "250+150=400", "dificultad" to 3, "explicacion" to "2/5×500=200; 1/4×500=125; 200+125=325m."),
        mapOf("id" to "O-012", "enunciado" to "¿Qué fracción sumada a 3/8 da como resultado 1 entero?", "correcta" to "5/8", "opcionA" to "3/8", "opcionB" to "5/8", "opcionC" to "1/8", "dificultad" to 4, "explicacion" to "1 = 8/8; 8/8 − 3/8 = 5/8."),

        // CONJUNTO D: Geometría (P-001 a P-012)
        mapOf("id" to "P-001", "enunciado" to "¿Cómo se llama el ángulo que mide exactamente 90°?", "correcta" to "Recto", "opcionA" to "Agudo", "opcionB" to "Recto", "opcionC" to "Obtuso", "dificultad" to 1, "explicacion" to "90° = ángulo recto (forma una L perfecta)."),
        mapOf("id" to "P-002", "enunciado" to "¿Cuántos lados tiene un hexágono?", "correcta" to "6", "opcionA" to "5", "opcionB" to "6", "opcionC" to "7", "dificultad" to 1, "explicacion" to "Hexa = 6 (latín)."),
        mapOf("id" to "P-003", "enunciado" to "Un triángulo con los 3 lados iguales se llama:", "correcta" to "Equilátero", "opcionA" to "Escaleno", "opcionB" to "Isósceles", "opcionC" to "Equilátero", "dificultad" to 1, "explicacion" to "Equi = igual + látero = lado."),
        mapOf("id" to "P-004", "enunciado" to "Un cuadrado tiene lados de 9 cm. ¿Cuál es su perímetro?", "correcta" to "36 cm", "opcionA" to "18 cm", "opcionB" to "27 cm", "opcionC" to "36 cm", "dificultad" to 1, "explicacion" to "P = 4 × 9 = 36 cm."),
        mapOf("id" to "P-005", "enunciado" to "Un rectángulo: largo=11 cm, ancho=6 cm. ¿Cuál es el perímetro?", "correcta" to "34 cm", "opcionA" to "17 cm", "opcionB" to "34 cm", "opcionC" to "66 cm", "dificultad" to 2, "explicacion" to "P = 2×(11+6) = 2×17 = 34 cm."),
        mapOf("id" to "P-006", "enunciado" to "Un cuadrado de 7 cm de lado. ¿Cuál es su área?", "correcta" to "49 cm²", "opcionA" to "14 cm²", "opcionB" to "28 cm²", "opcionC" to "49 cm²", "dificultad" to 2, "explicacion" to "A = 7 × 7 = 49 cm²."),
        mapOf("id" to "P-007", "enunciado" to "Un rectángulo: base=12 cm, altura=5 cm. ¿Cuál es su área?", "correcta" to "60 cm²", "opcionA" to "17 cm²", "opcionB" to "34 cm²", "opcionC" to "60 cm²", "dificultad" to 2, "explicacion" to "A = 12 × 5 = 60 cm²."),
        mapOf("id" to "P-008", "enunciado" to "Si el diámetro de un círculo es 14 cm, ¿cuánto mide el radio?", "correcta" to "7 cm", "opcionA" to "4 cm", "opcionB" to "7 cm", "opcionC" to "14 cm", "dificultad" to 2, "explicacion" to "Radio = diámetro ÷ 2 = 14÷2 = 7 cm."),
        mapOf("id" to "P-009", "enunciado" to "Un terreno rectangular mide 25 m × 18 m. ¿Cuánto cuesta cercarlo si cada metro de cerca cuesta S/15?", "correcta" to "S/1290", "opcionA" to "S/645", "opcionB" to "S/900", "opcionC" to "S/1290", "dificultad" to 3, "explicacion" to "P=2×(25+18)=86m; 86×15=S/1290."),
        mapOf("id" to "P-010", "enunciado" to "Un triángulo isósceles tiene 2 lados de 8 cm y el tercer lado de 5 cm. ¿Cuál es su perímetro?", "correcta" to "21 cm", "opcionA" to "13 cm", "opcionB" to "16 cm", "opcionC" to "21 cm", "dificultad" to 3, "explicacion" to "8+8+5=21 cm."),
        mapOf("id" to "P-011", "enunciado" to "Un cuadrado de 10 cm tiene un rectángulo de 6×4 cm dentro. ¿Cuál es el área que queda fuera del rectángulo?", "correcta" to "76 cm²", "opcionA" to "24 cm²", "opcionB" to "76 cm²", "opcionC" to "100 cm²", "dificultad" to 3, "explicacion" to "Cuadrado: 100 cm²; rectángulo: 24 cm²; 100−24=76 cm²."),
        mapOf("id" to "P-012", "enunciado" to "El perímetro de un rectángulo es 56 cm y su largo es el doble de su ancho. ¿Cuáles son sus dimensiones?", "correcta" to "Largo=18.67 cm, Ancho=9.33 cm", "opcionA" to "Largo=18.67, Ancho=9.33", "opcionB" to "Largo=20, Ancho=8", "opcionC" to "Largo=16, Ancho=12", "dificultad" to 4, "explicacion" to "2l+2a=56; l=2a; 2(2a)+2a=56; 6a=56; a=9.33, l=18.67"),

        // CONJUNTO E: Medición avanzada (Q-001 a Q-010)
        mapOf("id" to "Q-001", "enunciado" to "¿Cuántos gramos hay en 3 kg?", "correcta" to "3000 g", "opcionA" to "300 g", "opcionB" to "1500 g", "opcionC" to "3000 g", "dificultad" to 1, "explicacion" to "3 × 1000 = 3000 g."),
        mapOf("id" to "Q-002", "enunciado" to "¿Cuántos mililitros hay en 2 litros?", "correcta" to "2000 mL", "opcionA" to "200 mL", "opcionB" to "1000 mL", "opcionC" to "2000 mL", "dificultad" to 1, "explicacion" to "2 × 1000 = 2000 mL."),
        mapOf("id" to "Q-003", "enunciado" to "¿Cuántos segundos tiene 1 minuto?", "correcta" to "60", "opcionA" to "30", "opcionB" to "60", "opcionC" to "100", "dificultad" to 1, "explicacion" to "1 minuto = 60 segundos."),
        mapOf("id" to "Q-004", "enunciado" to "¿Cuántas horas tiene un día?", "correcta" to "24", "opcionA" to "12", "opcionB" to "20", "opcionC" to "24", "dificultad" to 1, "explicacion" to "1 día = 24 horas."),
        mapOf("id" to "Q-005", "enunciado" to "¿Cuántos gramos son 2 kg y 500 g?", "correcta" to "2500 g", "opcionA" to "2005 g", "opcionB" to "2500 g", "opcionC" to "25000 g", "dificultad" to 2, "explicacion" to "2×1000+500 = 2500 g."),
        mapOf("id" to "Q-006", "enunciado" to "¿Cuántas horas hay en 3 días y 6 horas?", "correcta" to "78 h", "opcionA" to "72 h", "opcionB" to "78 h", "opcionC" to "80 h", "dificultad" to 2, "explicacion" to "3×24+6 = 72+6 = 78 horas."),
        mapOf("id" to "Q-007", "enunciado" to "Una botella tiene 750 mL. ¿Cuántas botellas necesito para llenar un bidón de 6 L?", "correcta" to "8", "opcionA" to "6", "opcionB" to "7", "opcionC" to "8", "dificultad" to 2, "explicacion" to "6L=6000 mL; 6000÷750=8 botellas."),
        mapOf("id" to "Q-008", "enunciado" to "Un camión carga 3 bultos de 45 kg, 2 bultos de 30 kg y 1 bulto de 80 kg. ¿Cuánto carga en total?", "correcta" to "275 kg", "opcionA" to "235 kg", "opcionB" to "250 kg", "opcionC" to "275 kg", "dificultad" to 3, "explicacion" to "3×45=135, 2×30=60, 80; 135+60+80=275 kg."),
        mapOf("id" to "Q-009", "enunciado" to "Una tienda vende leche en bolsas de 250 mL. Si vendió 48 bolsas, ¿cuántos litros vendió?", "correcta" to "12 L", "opcionA" to "10 L", "opcionB" to "12 L", "opcionC" to "14 L", "dificultad" to 3, "explicacion" to "48×250=12000 mL = 12 L."),
        mapOf("id" to "Q-010", "enunciado" to "Un bebé nació hace 2 años, 3 meses y 15 días. ¿Cuántos días tiene aproximadamente? (año=365 días, mes=30 días)", "correcta" to "835 días aprox.", "opcionA" to "730 días", "opcionB" to "800 días", "opcionC" to "835 días", "dificultad" to 4, "explicacion" to "365×2=730; 30×3=90; +15=835 días."),

        // CONJUNTO F: Estadística y probabilidad (R-001 a R-012)
        mapOf("id" to "R-001", "enunciado" to "Datos: 4, 7, 4, 9, 4, 6, 7. ¿Cuál es la moda?", "correcta" to "4", "opcionA" to "4", "opcionB" to "6", "opcionC" to "7", "dificultad" to 1, "explicacion" to "La moda es el dato que más se repite: 4 aparece 3 veces."),
        mapOf("id" to "R-002", "enunciado" to "Lanzas una moneda. ¿Es posible sacar cara?", "correcta" to "Posible", "opcionA" to "Imposible", "opcionB" to "Posible", "opcionC" to "Seguro", "dificultad" to 1, "explicacion" to "Hay dos opciones: cara o sello. Es posible (no seguro ni imposible)."),
        mapOf("id" to "R-003", "enunciado" to "Barras: manzana=10, pera=6, naranja=8, uva=12. ¿Qué fruta hay menos?", "correcta" to "Pera", "opcionA" to "Manzana", "opcionB" to "Pera", "opcionC" to "Naranja", "dificultad" to 1, "explicacion" to "6 es el valor más bajo."),
        mapOf("id" to "R-004", "enunciado" to "Pictograma: cada ⭐ vale 5. Hay 4 estrellas para 3A. ¿Cuántos alumnos hay?", "correcta" to "20", "opcionA" to "4", "opcionB" to "15", "opcionC" to "20", "dificultad" to 1, "explicacion" to "4 × 5 = 20 alumnos."),
        mapOf("id" to "R-005", "enunciado" to "Notas: 14, 16, 12, 18. ¿Cuál es el promedio (media)?", "correcta" to "15", "opcionA" to "14", "opcionB" to "15", "opcionC" to "16", "dificultad" to 2, "explicacion" to "(14+16+12+18)÷4 = 60÷4 = 15."),
        mapOf("id" to "R-006", "enunciado" to "Tabla: niños 3A=15, 3B=18; niñas 3A=13, 3B=16. ¿Cuántos alumnos hay en total?", "correcta" to "62", "opcionA" to "48", "opcionB" to "60", "opcionC" to "62", "dificultad" to 2, "explicacion" to "15+18+13+16=62 alumnos."),
        mapOf("id" to "R-007", "enunciado" to "Lanzas un dado (del 1 al 6). ¿Cuántas probabilidades hay de sacar un número par?", "correcta" to "3/6", "opcionA" to "1/6", "opcionB" to "2/6", "opcionC" to "3/6", "dificultad" to 2, "explicacion" to "Pares: 2, 4, 6. Son 3 de 6 posibilidades."),
        mapOf("id" to "R-008", "enunciado" to "Edades: 8, 9, 8, 10, 9, 8, 11, 9. Calcula moda y media.", "correcta" to "Moda=8, Media=9", "opcionA" to "Moda=8, Media=8", "opcionB" to "Moda=8, Media=9", "opcionC" to "Moda=9, Media=9", "dificultad" to 3, "explicacion" to "Moda: 8 (aparece 3 veces). Media: (8+9+8+10+9+8+11+9)÷8=72÷8=9."),
        mapOf("id" to "R-009", "enunciado" to "En 5 días se vendieron: 23, 31, 28, 35, 43 kilos de pan. ¿Cuál fue el promedio diario?", "correcta" to "32 kg", "opcionA" to "30 kg", "opcionB" to "32 kg", "opcionC" to "34 kg", "dificultad" to 3, "explicacion" to "(23+31+28+35+43)÷5=160÷5=32 kg."),
        mapOf("id" to "R-010", "enunciado" to "Una bolsa tiene 5 bolas rojas. Si sacas una al azar, ¿es seguro que sea roja?", "correcta" to "Seguro", "opcionA" to "Imposible", "opcionB" to "Posible", "opcionC" to "Seguro", "dificultad" to 3, "explicacion" to "Solo hay bolas rojas → siempre saldrá roja → evento seguro."),
        mapOf("id" to "R-011", "enunciado" to "Con los datos: lunes=45, martes=60, miércoles=30, jueves=75, viernes=50. ¿Qué día se vendió más? ¿Cuál es el promedio?", "correcta" to "Jueves; Promedio=52", "opcionA" to "Jueves; 52", "opcionB" to "Viernes; 50", "opcionC" to "Martes; 60", "dificultad" to 3, "explicacion" to "Max: jueves (75). Media: (45+60+30+75+50)÷5=260÷5=52."),
        mapOf("id" to "R-012", "enunciado" to "En una ruleta hay 8 secciones: 3 azules, 3 rojas y 2 verdes. ¿Cuál es la probabilidad de caer en verde?", "correcta" to "2/8 = 1/4", "opcionA" to "1/8", "opcionB" to "2/8", "opcionC" to "3/8", "dificultad" to 4, "explicacion" to "2 verdes de 8 posibilidades = 2/8 = 1/4."),

        // CONJUNTO G: Ecuaciones básicas (S-001 a S-010)
        mapOf("id" to "S-001", "enunciado" to "n + 7 = 15. ¿Cuánto es n?", "correcta" to "8", "opcionA" to "6", "opcionB" to "7", "opcionC" to "8", "dificultad" to 1, "explicacion" to "15 − 7 = 8."),
        mapOf("id" to "S-002", "enunciado" to "20 − n = 12. ¿Cuánto es n?", "correcta" to "8", "opcionA" to "6", "opcionB" to "7", "opcionC" to "8", "dificultad" to 1, "explicacion" to "20 − 12 = 8."),
        mapOf("id" to "S-003", "enunciado" to "4 × n = 28. ¿Cuánto es n?", "correcta" to "7", "opcionA" to "5", "opcionB" to "6", "opcionC" to "7", "dificultad" to 1, "explicacion" to "28 ÷ 4 = 7."),
        mapOf("id" to "S-004", "enunciado" to "n ÷ 5 = 9. ¿Cuánto es n?", "correcta" to "45", "opcionA" to "40", "opcionB" to "45", "opcionC" to "50", "dificultad" to 1, "explicacion" to "n = 9 × 5 = 45."),
        mapOf("id" to "S-005", "enunciado" to "3, 6, 12, 24, ___", "correcta" to "48", "opcionA" to "36", "opcionB" to "48", "opcionC" to "36", "dificultad" to 2, "explicacion" to "Multiplica por 2 cada término."),
        mapOf("id" to "S-006", "enunciado" to "100, 90, 81, 73, 66, ___", "correcta" to "60", "opcionA" to "58", "opcionB" to "60", "opcionC" to "62", "dificultad" to 2, "explicacion" to "Diferencias: −10, −9, −8, −7, −6. 66−6=60."),
        mapOf("id" to "S-007", "enunciado" to "2 × n + 3 = 17. ¿Cuánto es n?", "correcta" to "7", "opcionA" to "5", "opcionB" to "6", "opcionC" to "7", "dificultad" to 3, "explicacion" to "2n=17−3=14; n=14÷2=7."),
        mapOf("id" to "S-008", "enunciado" to "En una balanza: lado izquierdo tiene 3 cajas de n kg y 2 kg extra. Lado derecho: 20 kg. ¿Cuánto pesa cada caja?", "correcta" to "6 kg", "opcionA" to "5", "opcionB" to "6", "opcionC" to "7", "dificultad" to 3, "explicacion" to "3n+2=20; 3n=18; n=6 kg."),
        mapOf("id" to "S-009", "enunciado" to "Escribe la familia de operaciones de 6, 7 y 42.", "correcta" to "Las 4 operaciones", "opcionA" to "6×7=42", "opcionB" to "7×6=42", "opcionC" to "Las 4 operaciones", "dificultad" to 2, "explicacion" to "6×7=42; 7×6=42; 42÷6=7; 42÷7=6."),
        mapOf("id" to "S-010", "enunciado" to "El doble de n más el triple de n es igual a 50. ¿Cuánto es n?", "correcta" to "10", "opcionA" to "8", "opcionB" to "9", "opcionC" to "10", "dificultad" to 4, "explicacion" to "2n+3n=50; 5n=50; n=10.")
    )

    // ============================================
    // FUNCIÓN PRINCIPAL PARA SUBIR TODOS LOS EJERCICIOS
    // ============================================

    suspend fun uploadAllExercises() {
        println("🚀 Iniciando carga de ejercicios a Firebase...")
        println("==========================================")

        // Subir ejercicios de PRIMERO
        println("📚 Subiendo ejercicios de PRIMERO de primaria (${ejerciciosPrimero.size} ejercicios)...")
        for (ej in ejerciciosPrimero) {
            val id = ej["id"] as String
            val grado = "primero"
            try {
                db.collection("grados")
                    .document(grado)
                    .collection("modulos")
                    .document(id)
                    .set(ej)
                    .await()
                println("✅ Subido: $grado/$id - ${ej["enunciado"]}")
            } catch (e: Exception) {
                println("❌ Error subiendo $grado/$id: ${e.message}")
            }
        }
        println("✅ PRIMERO completado: ${ejerciciosPrimero.size} ejercicios")

        // Subir ejercicios de SEGUNDO
        println("\n📚 Subiendo ejercicios de SEGUNDO de primaria (${ejerciciosSegundo.size} ejercicios)...")
        for (ej in ejerciciosSegundo) {
            val id = ej["id"] as String
            val grado = "segundo"
            try {
                db.collection("grados")
                    .document(grado)
                    .collection("modulos")
                    .document(id)
                    .set(ej)
                    .await()
                println("✅ Subido: $grado/$id - ${ej["enunciado"]}")
            } catch (e: Exception) {
                println("❌ Error subiendo $grado/$id: ${e.message}")
            }
        }
        println("✅ SEGUNDO completado: ${ejerciciosSegundo.size} ejercicios")

        // Subir ejercicios de TERCERO
        println("\n📚 Subiendo ejercicios de TERCERO de primaria (${ejerciciosTercero.size} ejercicios)...")
        for (ej in ejerciciosTercero) {
            val id = ej["id"] as String
            val grado = "tercero"
            try {
                db.collection("grados")
                    .document(grado)
                    .collection("modulos")
                    .document(id)
                    .set(ej)
                    .await()
                println("✅ Subido: $grado/$id - ${ej["enunciado"]}")
            } catch (e: Exception) {
                println("❌ Error subiendo $grado/$id: ${e.message}")
            }
        }
        println("✅ TERCERO completado: ${ejerciciosTercero.size} ejercicios")

        println("\n==========================================")
        println("🎉 ¡CARGA COMPLETADA!")
        println("📊 Total ejercicios subidos: ${ejerciciosPrimero.size + ejerciciosSegundo.size + ejerciciosTercero.size}")
        println("   - Primero: ${ejerciciosPrimero.size}")
        println("   - Segundo: ${ejerciciosSegundo.size}")
        println("   - Tercero: ${ejerciciosTercero.size}")
        println("==========================================")
    }
}