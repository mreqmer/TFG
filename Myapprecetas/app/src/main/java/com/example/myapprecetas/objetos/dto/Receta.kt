package com.example.myapprecetas.objetos.dto

data class Receta(
    val descripcion: String,
    val dificultad: String,
    val fechaCreacion: String,
    val idReceta: Int,
    val nombreReceta: String,
    val tiempoPreparacion: Int,
    val fotoReceta: String,
    val nombreUsuario: String
)