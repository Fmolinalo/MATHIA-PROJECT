package com.example.mathia

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

/**
 * Repositorio de datos semilla para MathIA.
 * Crea usuarios de prueba (padres, docentes y alumnos) en Firebase Auth + Firestore
 * usando la API REST de Firebase Identity Toolkit para no interferir con la sesión actual.
 */
class SeedDataRepository {

    private val db = Firebase.firestore
    private val auth = FirebaseAuth.getInstance()

    // API Key del proyecto Firebase (google-services.json)
    private val apiKey = "AIzaSyAVQJl42ZY_xS0KFYJmE9fZSxQHgv3edYA"
    private val signUpUrl = "https://identitytoolkit.googleapis.com/v1/accounts:signUp?key=$apiKey"
    private val password = "MathIA2026!"

    // ─── Datos de los usuarios a crear ─────────────────────────────────────

    data class UsuarioSemilla(
        val email: String,
        val nombre: String,
        val rol: String,
        val colegio: String = "Maria Auxiliadora",
        val grado: String = "3ro de Primaria",
        val seccion: String = "Sección A",
        val estudiantePin: Int? = null
    )

    private val docentesRaw = listOf(
        listOf("teacher01", "María Fernández", "María Auxiliadora", "1ro Primaria", "A"),
        listOf("teacher02", "Carlos Quispe", "María Auxiliadora", "1ro Primaria", "B"),
        listOf("teacher03", "Rosa Medina", "María Auxiliadora", "1ro Primaria", "C"),
        listOf("teacher04", "Luis Herrera", "María Auxiliadora", "1ro Primaria", "D"),
        listOf("teacher05", "Patricia Salas", "María Auxiliadora", "1ro Primaria", "E"),
        listOf("teacher06", "Jorge Rojas", "María Auxiliadora", "2do Primaria", "A"),
        listOf("teacher07", "Ana Castillo", "María Auxiliadora", "2do Primaria", "B"),
        listOf("teacher08", "Miguel Torres", "María Auxiliadora", "2do Primaria", "C"),
        listOf("teacher09", "Claudia Vargas", "María Auxiliadora", "2do Primaria", "D"),
        listOf("teacher10", "Ricardo León", "María Auxiliadora", "2do Primaria", "E"),
        listOf("teacher11", "Silvia Flores", "María Auxiliadora", "3ro Primaria", "A"),
        listOf("teacher12", "Pedro Gómez", "María Auxiliadora", "3ro Primaria", "B"),
        listOf("teacher13", "Andrea Huamán", "3 de Mayo", "3ro Primaria", "C"),
        listOf("teacher14", "Fernando Díaz", "3 de Mayo", "3ro Primaria", "D"),
        listOf("teacher15", "Jessica Paredes", "3 de Mayo", "3ro Primaria", "E"),
        listOf("teacher16", "José Cabrera", "3 de Mayo", "1ro Primaria", "A"),
        listOf("teacher17", "Karen Silva", "3 de Mayo", "1ro Primaria", "B"),
        listOf("teacher18", "Víctor Ramos", "3 de Mayo", "1ro Primaria", "C"),
        listOf("teacher19", "Paola Núñez", "3 de Mayo", "1ro Primaria", "D"),
        listOf("teacher20", "Roberto Espinoza", "3 de Mayo", "1ro Primaria", "E"),
        listOf("teacher21", "Mónica Cueva", "San Martín de Porres", "2do Primaria", "A"),
        listOf("teacher22", "Daniel Chávez", "San Martín de Porres", "2do Primaria", "B"),
        listOf("teacher23", "Sandra Molina", "San Martín de Porres", "2do Primaria", "C"),
        listOf("teacher24", "Iván Ponce", "San Martín de Porres", "2do Primaria", "D"),
        listOf("teacher25", "Teresa Bravo", "San Martín de Porres", "2do Primaria", "E"),
        listOf("teacher26", "Marco Beltrán", "José Carlos Mariátegui", "3ro Primaria", "A"),
        listOf("teacher27", "Gloria Lozano", "José Carlos Mariátegui", "3ro Primaria", "B"),
        listOf("teacher28", "Raúl Acosta", "José Carlos Mariátegui", "3ro Primaria", "C"),
        listOf("teacher29", "Lorena Vega", "José Carlos Mariátegui", "3ro Primaria", "D"),
        listOf("teacher30", "Hugo Palacios", "José Carlos Mariátegui", "3ro Primaria", "E"),
        listOf("teacher31", "Natalia Ortiz", "Santa Rosa", "1ro Primaria", "A"),
        listOf("teacher32", "Edgar Castro", "Santa Rosa", "1ro Primaria", "B"),
        listOf("teacher33", "Carolina Fuentes", "Santa Rosa", "1ro Primaria", "C"),
        listOf("teacher34", "Óscar Vera", "Santa Rosa", "1ro Primaria", "D"),
        listOf("teacher35", "Cecilia Arce", "Santa Rosa", "1ro Primaria", "E"),
        listOf("teacher36", "Kevin Montes", "Santa Rosa", "2do Primaria", "A"),
        listOf("teacher37", "Yolanda Delgado", "Santa Rosa", "2do Primaria", "B"),
        listOf("teacher38", "Cristian Valdez", "Santa Rosa", "2do Primaria", "C"),
        listOf("teacher39", "Diana Aguilar", "Santa Rosa", "2do Primaria", "D"),
        listOf("teacher40", "Julio Navarro", "Santa Rosa", "2do Primaria", "E"),
        listOf("teacher41", "Liliana Campos", "Nuestra Señora del Carmen", "3ro Primaria", "A"),
        listOf("teacher42", "Renzo Cárdenas", "Nuestra Señora del Carmen", "3ro Primaria", "B"),
        listOf("teacher43", "Verónica Peña", "Nuestra Señora del Carmen", "3ro Primaria", "C"),
        listOf("teacher44", "Alberto Soto", "Nuestra Señora del Carmen", "3ro Primaria", "D"),
        listOf("teacher45", "Susana Lozada", "Nuestra Señora del Carmen", "3ro Primaria", "E"),
        listOf("teacher46", "Héctor Mendoza", "Virgen del Carmen", "1ro Primaria", "A"),
        listOf("teacher47", "Elena Carrillo", "Virgen del Carmen", "2do Primaria", "B"),
        listOf("teacher48", "Gustavo Quinteros", "Virgen del Carmen", "3ro Primaria", "C"),
        listOf("teacher49", "Martha Pacheco", "Virgen del Carmen", "1ro Primaria", "D"),
        listOf("teacher50", "Wilfredo Gutiérrez", "Virgen del Carmen", "2do Primaria", "E")
    )

    private val docentes = docentesRaw.map { raw ->
        val normalizedGrado = raw[3].replace(" Primaria", " de Primaria")
        val normalizedSeccion = "Sección ${raw[4]}"
        UsuarioSemilla(
            email = "${raw[0]}@mathia.com",
            nombre = raw[1],
            rol = "docente",
            colegio = raw[2],
            grado = normalizedGrado,
            seccion = normalizedSeccion
        )
    }

    private val padresRaw = listOf(
        listOf("1234", "Melisa Gutierrez", "Sofía Pacheco"),
        listOf("1235", "Carlos Mendoza", "Mateo Mendoza"),
        listOf("1236", "Ana Rodríguez", "Valeria Rodríguez"),
        listOf("1237", "Luis Fernández", "Diego Fernández"),
        listOf("1238", "Patricia Torres", "Camila Torres"),
        listOf("1239", "José Vargas", "Thiago Vargas"),
        listOf("1240", "María Rojas", "Luciana Rojas"),
        listOf("1241", "Pedro Salazar", "Sebastián Salazar"),
        listOf("1242", "Andrea Castillo", "Isabella Castillo"),
        listOf("1243", "Ricardo Gómez", "Emiliano Gómez"),
        listOf("1244", "Diana Paredes", "Antonella Paredes"),
        listOf("1245", "Miguel Soto", "Joaquín Soto"),
        listOf("1246", "Silvia Herrera", "Renata Herrera"),
        listOf("1247", "Fernando León", "Benjamín León"),
        listOf("1248", "Claudia Ruiz", "Alessandra Ruiz"),
        listOf("1249", "Roberto Medina", "Fabricio Medina"),
        listOf("1250", "Jessica Flores", "Nicole Flores"),
        listOf("1251", "Hugo Cabrera", "Cristopher Cabrera"),
        listOf("1252", "Verónica Peña", "Mía Peña"),
        listOf("1253", "Sergio Valdez", "Gabriel Valdez"),
        listOf("1254", "Rosa Aguilar", "Emily Aguilar"),
        listOf("1255", "Daniel Chávez", "Axel Chávez"),
        listOf("1256", "Karen Navarro", "Abril Navarro"),
        listOf("1257", "Javier Quispe", "Adrián Quispe"),
        listOf("1258", "Mónica Cárdenas", "Mariana Cárdenas"),
        listOf("1259", "Víctor Ramos", "Ian Ramos"),
        listOf("1260", "Paola Espinoza", "Ariana Espinoza"),
        listOf("1261", "Gustavo Campos", "Leonardo Campos"),
        listOf("1262", "Elena Silva", "Julieta Silva"),
        listOf("1263", "Óscar Núñez", "Samuel Núñez"),
        listOf("1264", "Natalia Fuentes", "Emma Fuentes"),
        listOf("1265", "Julio Vera", "Liam Vera"),
        listOf("1266", "Cecilia Ortiz", "Martina Ortiz"),
        listOf("1267", "Edgar Lozano", "Gael Lozano"),
        listOf("1268", "Lorena Acosta", "Aitana Acosta"),
        listOf("1269", "Raúl Carrillo", "Bruno Carrillo"),
        listOf("1270", "Susana Molina", "Daniela Molina"),
        listOf("1271", "Iván Ponce", "Matías Ponce"),
        listOf("1272", "Teresa Delgado", "Victoria Delgado"),
        listOf("1273", "Alberto Castro", "Lucas Castro"),
        listOf("1274", "Carolina Huamán", "Amanda Huamán"),
        listOf("1275", "Renzo Palacios", "Nicolás Palacios"),
        listOf("1276", "Liliana Bravo", "Zoe Bravo"),
        listOf("1277", "Marco Cueva", "Thiago Cueva"),
        listOf("1278", "Sandra Lozada", "Alma Lozada"),
        listOf("1279", "Héctor Pineda", "Maximiliano Pineda"),
        listOf("1280", "Gloria Arce", "Lucía Arce"),
        listOf("1281", "Kevin Montes", "Facundo Montes"),
        listOf("1282", "Yolanda Vega", "Olivia Vega"),
        listOf("1283", "Cristian Beltrán", "Agustín Beltrán")
    )

    private val padres = padresRaw.mapIndexed { index, raw ->
        val numStr = String.format("%02d", index + 1)
        UsuarioSemilla(
            email = "parent$numStr@mathia.com",
            nombre = raw[1],
            rol = "padre",
            estudiantePin = raw[0].toInt()
        )
    }

    data class AlumnoSemilla(
        val pin: String,
        val nombre: String,
        val grado: String = "3ro de Primaria",
        val seccion: String = "Sección A",
        val edad: Int = 8,
        val colegio: String = "Maria Auxiliadora",
        val docenteAsignado: String = "Efraín Caceres",
        val padreEmail: String,
        val avatar: String,
        val estrellas: Int = 0,
        val xp: Int = 0,
        val nivel: Int = 1,
        val precision: Double = 0.0,
        val streak: Int = 0
    )

    private val avatars = listOf("default", "superhero", "rocket", "wizard")

    private val alumnos = padresRaw.mapIndexed { index, raw ->
        val numStr = String.format("%02d", index + 1)
        val padEmail = "parent$numStr@mathia.com"
        val avatar = avatars[index % avatars.size]
        val teacher = docentes[index] // maps 1-to-1 with docentes list (index 0 to 49)
        
        AlumnoSemilla(
            pin = raw[0],
            nombre = raw[2],
            grado = teacher.grado,
            seccion = teacher.seccion,
            edad = 8,
            colegio = teacher.colegio,
            docenteAsignado = teacher.nombre,
            padreEmail = padEmail,
            avatar = avatar,
            estrellas = (100..400).random(),
            xp = (30..95).random(),
            nivel = (1..2).random(),
            precision = (60..90).random().toDouble(),
            streak = (0..5).random()
        )
    }

    // ─── Función principal: crear todos los usuarios ────────────────────────

    suspend fun configurarUsuariosSemilla(
        onProgress: (String) -> Unit
    ): Result<String> = withContext(Dispatchers.IO) {
        val resumen = StringBuilder()
        var creados = 0
        var yaExistian = 0
        var errores = 0

        onProgress("Iniciando configuración...")

        // 1. Crear docentes
        for (docente in docentes) {
            onProgress("Creando docente: ${docente.nombre}...")
            val resultado = crearUsuarioAuth(docente.email, docente.nombre)
            when {
                resultado == null -> {
                    onProgress("️ ${docente.email} ya existía en Auth")
                    yaExistian++
                    val uid = obtenerUidExistente(docente.email)
                    if (uid != null) {
                        guardarAdultoFirestore(uid, docente)
                        onProgress("Perfil Firestore de ${docente.nombre} actualizado")
                    }
                }
                resultado.isNotEmpty() -> {
                    guardarAdultoFirestore(resultado, docente)
                    onProgress("Docente ${docente.nombre} creado")
                    creados++
                    resumen.appendLine("‍${docente.nombre} → ${docente.email}")
                }
                else -> { errores++; onProgress("Error creando ${docente.email}") }
            }
        }

        // 2. Crear padres
        for (padre in padres) {
            onProgress("Creando padre: ${padre.nombre}...")
            val resultado = crearUsuarioAuth(padre.email, padre.nombre)
            when {
                resultado == null -> {
                    yaExistian++
                    onProgress("️ ${padre.email} ya existía, actualizando perfil...")
                    val uid = obtenerUidExistente(padre.email)
                    if (uid != null) guardarAdultoFirestore(uid, padre)
                }
                resultado.isNotEmpty() -> {
                    guardarAdultoFirestore(resultado, padre)
                    onProgress("Padre ${padre.nombre} creado")
                    creados++
                    resumen.appendLine("${padre.nombre} → ${padre.email}")
                }
                else -> { errores++; onProgress("Error creando ${padre.email}") }
            }
        }

        // 3. Crear alumnos en Firestore (sin Auth, usan PIN)
        onProgress("Creando perfiles de alumnos...")
        for (alumno in alumnos) {
            try {
                guardarAlumnoFirestore(alumno)
                onProgress("Alumno ${alumno.nombre} (PIN: ${alumno.pin}) creado")
                creados++
                resumen.appendLine("${alumno.nombre} → PIN ${alumno.pin} (padre: ${alumno.padreEmail})")
            } catch (e: Exception) {
                errores++
                onProgress("Error creando alumno ${alumno.nombre}: ${e.message}")
            }
        }

        onProgress("¡Configuración completada!")

        val resumenFinal = """
            Usuarios procesados: ${creados + yaExistian}
            ️ Ya existían: $yaExistian
            Con errores: $errores
            
            $resumen
        """.trimIndent()

        Result.success(resumenFinal)
    }

    // ─── Crear usuario en Firebase Auth via REST API ────────────────────────
    // Retorna el UID si fue creado, null si ya existía, "" si hubo error

    private suspend fun crearUsuarioAuth(email: String, nombre: String): String? =
        withContext(Dispatchers.IO) {
            try {
                val url = URL(signUpUrl)
                val conn = url.openConnection() as HttpURLConnection
                conn.requestMethod = "POST"
                conn.setRequestProperty("Content-Type", "application/json")
                conn.doOutput = true
                conn.connectTimeout = 10_000
                conn.readTimeout = 10_000

                val body = JSONObject().apply {
                    put("email", email)
                    put("password", password)
                    put("displayName", nombre)
                    put("returnSecureToken", true)
                }.toString()

                OutputStreamWriter(conn.outputStream).use { it.write(body) }

                val responseCode = conn.responseCode
                if (responseCode == 200) {
                    val response = conn.inputStream.bufferedReader().readText()
                    val json = JSONObject(response)
                    json.getString("localId") // UID
                } else {
                    val error = conn.errorStream?.bufferedReader()?.readText() ?: ""
                    val errJson = JSONObject(error)
                    val errMsg = errJson.optJSONObject("error")?.optString("message") ?: ""
                    if (errMsg.contains("EMAIL_EXISTS")) null else ""
                }
            } catch (e: Exception) {
                println("Error en REST Auth: ${e.message}")
                ""
            }
        }

    // ─── Obtener UID de usuario existente usando signInWithEmailAndPassword ─

    private suspend fun obtenerUidExistente(email: String): String? =
        withContext(Dispatchers.IO) {
            try {
                val signInUrl = "https://identitytoolkit.googleapis.com/v1/accounts:signInWithPassword?key=$apiKey"
                val url = URL(signInUrl)
                val conn = url.openConnection() as HttpURLConnection
                conn.requestMethod = "POST"
                conn.setRequestProperty("Content-Type", "application/json")
                conn.doOutput = true

                val body = JSONObject().apply {
                    put("email", email)
                    put("password", password)
                    put("returnSecureToken", true)
                }.toString()

                OutputStreamWriter(conn.outputStream).use { it.write(body) }

                if (conn.responseCode == 200) {
                    val response = conn.inputStream.bufferedReader().readText()
                    JSONObject(response).getString("localId")
                } else null
            } catch (e: Exception) {
                null
            }
        }

    // ─── Guardar adulto en Firestore ────────────────────────────────────────

    private suspend fun guardarAdultoFirestore(uid: String, usuario: UsuarioSemilla) {
        val doc = hashMapOf(
            "uid" to uid,
            "email" to usuario.email,
            "rol" to usuario.rol,
            "nombre" to usuario.nombre,
            "colegio" to usuario.colegio,
            "grado" to usuario.grado,
            "seccion" to usuario.seccion,
            "perfil_completo" to true,
            "fechaRegistro" to System.currentTimeMillis()
        )
        if (usuario.rol == "docente") {
            doc["cargo"] = "Docente"
            doc["estado"] = "activo"
        }
        if (usuario.estudiantePin != null) {
            doc["estudiante_pin"] = usuario.estudiantePin
        }
        db.collection("usuarios").document(uid).set(doc).await()
    }

    // ─── Guardar alumno en Firestore (usa PIN como ID) ──────────────────────

    private suspend fun guardarAlumnoFirestore(alumno: AlumnoSemilla) {
        val doc = hashMapOf<String, Any>(
            "nombre" to alumno.nombre,
            "grado" to alumno.grado,
            "seccion" to alumno.seccion,
            "edad" to alumno.edad,
            "colegio" to alumno.colegio,
            "docente_asignado" to alumno.docenteAsignado,
            "fecha_creacion" to System.currentTimeMillis(),
            "nivel_actual" to alumno.nivel,
            "precision" to alumno.precision,
            "estrellas" to alumno.estrellas,
            "xp" to alumno.xp,
            "pin" to alumno.pin.toInt(),
            "padre_email" to alumno.padreEmail,
            "avatar" to alumno.avatar,
            "equipped_theme" to "Lila Clásico",
            "unlocked_avatars" to listOf("default", alumno.avatar),
            "unlocked_themes" to listOf("Lila Clásico"),
            "streak" to alumno.streak,
            "rol" to "estudiante",
            "total_preguntas" to 0,
            "correctas" to 0,
            "incorrectas" to 0,
            "tiempo_total" to 0L,
            "tiempo_promedio" to 0.0,
            "diagnostico_realizado" to false,
            "daily_mission_progress" to 0,
            "weekly_mission_progress" to 0,
            "skills" to mapOf(
                "Sumas" to 0,
                "Restas" to 0,
                "Multiplicación" to 0,
                "Fracciones" to 0,
                "Series" to 0
            ),
            "incorrectas_por_tema" to emptyMap<String, Int>(),
            "recomendaciones" to listOf(
                "¡Realiza el Examen Adaptativo para descubrir tu nivel actual!",
                "¡Practica Sumas todos los días para mejorar tu velocidad!"
            )
        )
        db.collection("usuarios").document(alumno.pin).set(doc).await()
    }
}
