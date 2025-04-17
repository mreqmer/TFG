package com.example.myapprecetas.dto

data class DTORecetaSimplificada(
    val descripcion: String,
    val dificultad: String,
    val fechaCreacion: String,
    val idCreador: Int,
    val idReceta: Int,
    val nombreReceta: String,
    val tiempoPreparacion: Int
)