package com.example.mathia.network

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("ia/generar-pregunta")
    suspend fun obtenerRetoDinamico(@Body peticion: Map<String, String>): Response<IAResponse>
}

data class IAResponse(
    val success: Boolean,
    val data: RetoMagico
)

data class RetoMagico(
    val enunciado: String,
    val correcta: String,
    val dificultad: Int,
    val explicacion: String,
    val opcionA: String,
    val opcionB: String,
    val opcionC: String
)
