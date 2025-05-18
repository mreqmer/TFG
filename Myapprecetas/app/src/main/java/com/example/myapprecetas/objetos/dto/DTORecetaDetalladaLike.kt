package com.example.myapprecetas.objetos.dto

data class DTORecetaDetalladaLike
(
    val categorias: List<Categoria>,
    val ingredientes: List<Ingrediente>,
    val pasos: List<Paso>,
    val descripcion: String,
    val dificultad: String,
    val fechaCreacion: String,
    val idReceta: Int,
    val nombreReceta: String,
    val tiempoPreparacion: Int,
    val fotoReceta: String,
    val nombreUsuario: String?,
    val tieneLike: Boolean
)

