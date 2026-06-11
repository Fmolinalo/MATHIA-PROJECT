package com.example.mathia

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class StudentViewModel : ViewModel() {

    private val repository = FirebaseRepository()

    private val _alumnoActual =
        MutableStateFlow<FirebaseStudent?>(null)

    val alumnoActual: StateFlow<FirebaseStudent?> =
        _alumnoActual

    fun login(
        pin: String,
        onResult: (FirebaseStudent?) -> Unit
    ) {
        viewModelScope.launch {
            val alumno =
                repository.obtenerAlumnoPorPin(pin)

            _alumnoActual.value = alumno

            onResult(alumno)
        }
    }

    fun guardarToken(
        pin: String,
        token: String
    ) {
        Firebase.firestore
            .collection("alumnos")
            .document(pin)
            .update(
                mapOf(
                    "fcm_token" to token
                )
            )
    }

    fun actualizarEstrellas(
        pin: String,
        estrellas: Int
    ) {
        viewModelScope.launch {
            repository.actualizarEstrellas(
                pin,
                estrellas
            )

            val current =
                _alumnoActual.value

            if (current != null) {
                _alumnoActual.value =
                    current.copy(
                        estrellas =
                            current.estrellas +
                                    estrellas
                    )
            }
        }
    }

    fun actualizarNivel(
        pin: String,
        nivel: Int
    ) {
        viewModelScope.launch {
            repository.actualizarNivel(
                pin,
                nivel
            )

            val current =
                _alumnoActual.value

            if (current != null) {
                _alumnoActual.value =
                    current.copy(
                        nivel_actual = nivel
                    )
            }
        }
    }

    fun actualizarPrecision(
        pin: String,
        precision: Double
    ) {
        viewModelScope.launch {
            repository.actualizarPrecision(
                pin,
                precision
            )

            val current =
                _alumnoActual.value

            if (current != null) {
                _alumnoActual.value =
                    current.copy(
                        precision = precision
                    )
            }
        }
    }

    fun obtenerTodosAlumnos(
        onResult: (List<FirebaseStudent>) -> Unit
    ) {
        viewModelScope.launch {
            val alumnos =
                repository.obtenerTodosAlumnos()

            onResult(alumnos)
        }
    }

    fun crearAlumno(
        alumno: Map<String, Any>,
        pin: String,
        onResult: (Boolean) -> Unit
    ) {
        viewModelScope.launch {
            val success =
                repository.crearAlumno(
                    alumno,
                    pin
                )

            onResult(success)
        }
    }

    fun obtenerEstudiantesPorEmailPadre(
        email: String,
        onResult: (List<FirebaseStudent>) -> Unit
    ) {
        viewModelScope.launch {
            val alumnos =
                repository
                    .obtenerEstudiantesPorEmailPadre(
                        email
                    )

            onResult(alumnos)
        }
    }

    fun actualizarPadreEmail(
        pin: String,
        padreEmail: String,
        onResult: (Boolean) -> Unit
    ) {
        viewModelScope.launch {
            val success =
                repository.actualizarPadreEmail(
                    pin,
                    padreEmail
                )

            onResult(success)
        }
    }

    fun guardarDiagnostico(
        pin: String,
        correctas: Int,
        incorrectas: Int,
        tiempoTotal: Long,
        tiempoPromedio: Double
    ) {
        Firebase.firestore
            .collection("alumnos")
            .document(pin)
            .update(
                mapOf(
                    "diagnostico_realizado" to true,
                    "correctas" to correctas,
                    "incorrectas" to incorrectas,
                    "tiempo_total" to tiempoTotal,
                    "tiempo_promedio" to tiempoPromedio,
                    "fecha_diagnostico" to
                            FieldValue.serverTimestamp()
                )
            )
    }

    fun enviarNotificacionExamen(
        pin: String,
        correctas: Int,
        precision: Int
    ) {
        Firebase.firestore
            .collection("notifications")
            .add(
                hashMapOf(
                    "studentPin" to pin,
                    "title" to
                            "Examen completado",
                    "body" to
                            "Puntaje: $correctas respuestas correctas | Precisión: $precision%",
                    "timestamp" to
                            System.currentTimeMillis(),
                    "read" to false,
                    "tipo" to "alumno"
                )
            )
    }

    fun notificarPadre(
        pin: String,
        nombreAlumno: String,
        precision: Int
    ) {
        Firebase.firestore
            .collection("notifications")
            .add(
                hashMapOf(
                    "studentPin" to pin,
                    "title" to
                            "Nuevo resultado disponible",
                    "body" to
                            "$nombreAlumno completó un examen con $precision% de precisión",
                    "timestamp" to
                            System.currentTimeMillis(),
                    "read" to false,
                    "tipo" to "padre"
                )
            )
    }

    fun notificarDocente(
        pin: String,
        nombreAlumno: String,
        precision: Int
    ) {
        Firebase.firestore
            .collection("notifications")
            .add(
                hashMapOf(
                    "studentPin" to pin,
                    "title" to
                            "Evaluación completada",
                    "body" to
                            "$nombreAlumno terminó su examen con $precision%",
                    "timestamp" to
                            System.currentTimeMillis(),
                    "read" to false,
                    "tipo" to "docente"
                )
            )
    }

    fun crearRecordatorioExamen(
        pin: String
    ) {
        Firebase.firestore
            .collection("notifications")
            .add(
                hashMapOf(
                    "studentPin" to pin,
                    "title" to
                            "Examen pendiente",
                    "body" to
                            "Aún no has completado tu examen adaptativo.",
                    "timestamp" to
                            System.currentTimeMillis(),
                    "read" to false,
                    "tipo" to "recordatorio"
                )
            )
    }

    // Google Sign-In for adults with Firebase Auth integration
    fun loginConGoogle(idToken: String, email: String, rol: String, onResult: (Boolean, Boolean) -> Unit) {
        viewModelScope.launch {
            val (success, isComplete) = repository.loginConGoogle(idToken, email, rol)
            onResult(success, isComplete)
        }
    }

    fun actualizarPerfilDocente(
        uid: String,
        nombre: String,
        colegio: String,
        grado: String,
        seccion: String,
        onResult: (Boolean) -> Unit
    ) {
        viewModelScope.launch {
            val success = repository.actualizarPerfilDocente(uid, nombre, colegio, grado, seccion)
            onResult(success)
        }
    }

    fun actualizarCosmeticos(
        pin: String,
        avatar: String,
        theme: String,
        onResult: (Boolean) -> Unit
    ) {
        viewModelScope.launch {
            val success = repository.actualizarCosmeticos(pin, avatar, theme)
            onResult(success)
        }
    }

    fun comprarCosmetico(
        pin: String,
        nuevoCosmetico: String,
        esAvatar: Boolean,
        costo: Int,
        onResult: (Boolean) -> Unit
    ) {
        viewModelScope.launch {
            val success = repository.comprarCosmetico(pin, nuevoCosmetico, esAvatar, costo)
            onResult(success)
        }
    }
}