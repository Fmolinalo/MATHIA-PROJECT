package com.example.mathia.data.network

import com.example.mathia.network.ApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    // IMPORTANTE: La IP cambiará dependiendo de cómo pruebes la app (ver paso 3)
    private const val BASE_URL = "http://192.168.100.66:8000/"

    val apiService: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}