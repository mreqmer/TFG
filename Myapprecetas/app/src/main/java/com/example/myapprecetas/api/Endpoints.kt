package com.example.myapprecetas.api

import com.example.myapprecetas.dto.DTORecetaDetallada
import com.example.myapprecetas.dto.DTORecetaSimplificada
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface Endpoints {

//    @GET("personas")
//    suspend fun getPersonas(): Response<List<DTOPersona>>

    @GET("recetas")
    suspend fun getRecetas(): Response<List<DTORecetaSimplificada>>

    @GET("recetas/{id}")
    suspend fun getRecetaPorId(@Path("id") id: Int): Response<DTORecetaDetallada>

}