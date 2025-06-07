package com.example.myapprecetas.objetos.borrarclases

import com.example.myapprecetas.objetos.dto.Categoria
import com.example.myapprecetas.objetos.dto.Ingrediente
import com.example.myapprecetas.objetos.dto.Paso
import com.example.myapprecetas.objetos.dto.Receta

data class DTORecetaDetallada(
    val categorias: List<Categoria>,
    val ingredientes: List<Ingrediente>,
    val pasos: List<Paso>,
    val receta: Receta,
    val nombreUsuario: String?
)