package com.example.mathia.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "t_estudiante")
data class StudentEntity(
    @PrimaryKey(autoGenerate = true) val id_estudiante: Long = 0,
    val nombre: String,
    val edad: Int,
    val grado: Int,
    val avatar_id: Int,
    val monedas_totales: Int = 0
)