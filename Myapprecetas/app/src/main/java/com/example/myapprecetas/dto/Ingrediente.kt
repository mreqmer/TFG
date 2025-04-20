package com.example.myapprecetas.dto

data class Ingrediente(
    val cantidad: Int,
    val categoria: String,
    val idIngrediente: Int,
    val medida: String,
    val nombreIngrediente: String,
    val notas: String
)