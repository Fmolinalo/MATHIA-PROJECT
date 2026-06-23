package com.example.mathia.data.model

data class RetoResponse(
    val success: Boolean,
    val data: MathiaQuestionIA
)

data class MathiaQuestionIA(
    val enunciado: String,
    val opcionA: String,
    val opcionB: String,
    val opcionC: String,
    val correcta: String,
    val explicacion: String,
    val dificultad: Int
)