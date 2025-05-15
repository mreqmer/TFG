package com.example.myapprecetas.objetos.dto.creacion


class RecetaResponse {
    data class RecetaResponse(
        val receta: RecetaInsertada
    )

    data class RecetaInsertada(
        val idReceta: Int,
        val nombreReceta: String,
        val descripcion: String,
        val tiempoPreparacion: Int,
        val dificultad: Int,
        val fechaCreacion: String,
        val fotoReceta: String?,
        val idCreador: Int,
        val pasos: List<DTOPasoSimplificado>,
        val ingredientes: List<DTOIngredienteSimplificado>,
        val categorias: List<DTOCategoriaSimplificada>
    )
}