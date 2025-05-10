package com.example.myapprecetas.dto.constantesobjetos

class ConstantesObjetos {

    //Enum coincidente con las dificultades de las recetas. Estas son las opciones disponibles con
    //la constraint de la BD
    enum class Recetas_Dificultad(val label: String) {
        MUY_BAJA("Muy baja"),
        BAJA("Baja"),
        MEDIA("Media"),
        MEDIA_ALTA("Media-Alta"),
        ALTA("Alta")
    }

}