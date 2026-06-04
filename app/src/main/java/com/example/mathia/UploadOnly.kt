package com.example.mathia

import kotlinx.coroutines.runBlocking

// Este archivo se ejecuta UNA SOLA VEZ para subir los ejercicios
// Después de ejecutarlo, puedes borrarlo o comentarlo

fun main() = runBlocking {
    println("🚀 Subiendo 201 ejercicios a Firebase...")
    UploadExercises.uploadAllExercises()
    println("✅ ¡Completado!")
}