package com.example.myapprecetas.api

import com.example.myapprecetas.dto.DTORecetaSimplificada
import retrofit2.Response
import retrofit2.http.GET

interface Endpoints {

//    @GET("personas")
//    suspend fun getPersonas(): Response<List<DTOPersona>>

    @GET("recetas")
    suspend fun getRecetas(): Response<List<DTORecetaSimplificada>>

}