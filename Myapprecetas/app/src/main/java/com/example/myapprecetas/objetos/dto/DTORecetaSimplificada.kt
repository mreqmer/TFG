package com.example.myapprecetas.objetos.dto

data class DTORecetaSimplificada(
    val descripcion: String,
    val dificultad: String,
    val fechaCreacion: String,
    val fotoReceta: String,
    val idCreador: Int,
    val idReceta: Int,
    val nombreReceta: String,
    val tiempoPreparacion: Int,
    val nombreUsuario: String
)