package com.example.mathia

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class StudentViewModel : ViewModel() {

    private val repository = FirebaseRepository()

    private val _alumnoActual = MutableStateFlow<FirebaseStudent?>(null)
    val alumnoActual: StateFlow<FirebaseStudent?> = _alumnoActual

    fun login(pin: String, onResult: (FirebaseStudent?) -> Unit) {
        viewModelScope.launch {
            val alumno = repository.obtenerAlumnoPorPin(pin)
            _alumnoActual.value = alumno
            onResult(alumno)
        }
    }

    fun actualizarEstrellas(pin: String, estrellas: Int) {
        viewModelScope.launch {
            repository.actualizarEstrellas(pin, estrellas)
            val current = _alumnoActual.value
            if (current != null) {
                _alumnoActual.value = current.copy(
                    estrellas = current.estrellas + estrellas
                )
            }
        }
    }

    fun actualizarNivel(pin: String, nivel: Int) {
        viewModelScope.launch {
            repository.actualizarNivel(pin, nivel)
            val current = _alumnoActual.value
            if (current != null) {
                _alumnoActual.value = current.copy(nivel_actual = nivel)
            }
        }
    }

    fun actualizarPrecision(pin: String, precision: Double) {
        viewModelScope.launch {
            repository.actualizarPrecision(pin, precision)
            val current = _alumnoActual.value
            if (current != null) {
                _alumnoActual.value = current.copy(precision = precision)
            }
        }
    }

    fun obtenerTodosAlumnos(onResult: (List<FirebaseStudent>) -> Unit) {
        viewModelScope.launch {
            val alumnos = repository.obtenerTodosAlumnos()
            onResult(alumnos)
        }
    }

    fun crearAlumno(alumno: Map<String, Any>, pin: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val success = repository.crearAlumno(alumno, pin)
            onResult(success)
        }
    }

    fun obtenerEstudiantesPorEmailPadre(email: String, onResult: (List<FirebaseStudent>) -> Unit) {
        viewModelScope.launch {
            val alumnos = repository.obtenerEstudiantesPorEmailPadre(email)
            onResult(alumnos)
        }
    }

    fun actualizarPadreEmail(pin: String, padreEmail: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val success = repository.actualizarPadreEmail(pin, padreEmail)
            onResult(success)
        }
    }
}