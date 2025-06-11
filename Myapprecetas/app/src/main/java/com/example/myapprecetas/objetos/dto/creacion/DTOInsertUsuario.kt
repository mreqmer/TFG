package com.example.myapprecetas.objetos.dto.creacion

data class DTOInsertUsuario(
    val firebaseUID: String,
    val nombreUsuario: String,
    val correoElectronico: String,
    val fotoPerfil: String
)