package com.example.myapprecetas.objetos.dto

data class Usuario(
    val correoElectronico: String,
    val fechaRegistro: String,
    val firebaseUID: String,
    val idUsuario: Int,
    val nombreUsuario: String
)