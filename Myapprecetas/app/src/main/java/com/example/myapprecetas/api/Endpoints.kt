package com.example.myapprecetas.api

import com.example.myapprecetas.dto.DTORecetaDetallada
import com.example.myapprecetas.dto.DTORecetaSimplificada
import com.example.myapprecetas.dto.creacion.DTONuevaReceta
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface Endpoints {

//    @GET("personas")
//    suspend fun getPersonas(): Response<List<DTOPersona>>

    @GET("recetas")
    suspend fun getRecetas(): Response<List<DTORecetaSimplificada>>

    @GET("recetas/{id}")
    suspend fun getRecetaPorId(@Path("id") id: Int): Response<DTORecetaDetallada>

    @GET("recetas/creador/{idUsuario}")
    suspend fun getRecetasPorIdUsuario(@Path("idUsuario") idUsuario: Int): Response<List<DTORecetaSimplificada>>

    @POST("recetas/add")
    suspend fun addReceta(@Body nuevaReceta: DTONuevaReceta): Response<DTONuevaReceta>
}