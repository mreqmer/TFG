package com.example.myapprecetas.dto

data class DTORecetaItem(
    val Categorias: List<Categoria>,
    val Descripcion: String,
    val Duracion: Int,
    val Id: Int,
    val IdCreador: Int,
    val Ingredientes: List<Ingrediente>,
    val Nombre: String,
    val Pasos: List<Paso>
)