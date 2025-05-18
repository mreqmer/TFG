package com.example.myapprecetas.objetos.dto

data class DTORecetaUsuarioLike(
    val descripcion: String,
    val dificultad: String,
    val fechaCreacion: String,
    val fotoReceta: String,
    val idCreador: Int,
    val idReceta: Int,
    val nombreReceta: String,
    val nombreUsuario: String,
    val tiempoPreparacion: Int,
    val tieneLike: Boolean
)