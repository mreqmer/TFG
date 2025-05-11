package com.example.myapprecetas.objetos.dto

data class DTORecetaDetallada(
    val categorias: List<Categoria>,
    val ingredientes: List<Ingrediente>,
    val pasos: List<Paso>,
    val receta: Receta,
    val nombreUsuario: String?
)