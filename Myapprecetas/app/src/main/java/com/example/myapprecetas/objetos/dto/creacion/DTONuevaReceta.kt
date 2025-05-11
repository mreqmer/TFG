package com.example.myapprecetas.objetos.dto.creacion

data class DTONuevaReceta(

    val descripcion: String,
    val dificultad: String,
    val fechaCreacion: String,
    val fotoReceta: String,
    val idCreador: Int = 0,
    val idReceta: Int,
    val nombreReceta: String,
    val tiempoPreparacion: Int,
    val categorias: List<DTOCategoriaSimplificada>,
    val ingredientes: List<DTOIngredienteSimplificado>,
    val pasos: List<DTOPasoSimplificado>
)