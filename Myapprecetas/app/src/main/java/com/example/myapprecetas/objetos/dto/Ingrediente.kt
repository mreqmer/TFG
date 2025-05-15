package com.example.myapprecetas.objetos.dto

data class Ingrediente(
    var cantidad: Int,
    val categoria: String,
    val idIngrediente: Int,
    val medida: String,
    val nombreIngrediente: String,
    val notas: String
)