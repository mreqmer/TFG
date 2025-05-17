package com.example.myapprecetas.api

import com.example.myapprecetas.objetos.dto.Categoria
import com.example.myapprecetas.objetos.dto.DTORecetaDetallada
import com.example.myapprecetas.objetos.dto.DTORecetaSimplificada
import com.example.myapprecetas.objetos.dto.DTOUsuario
import com.example.myapprecetas.objetos.dto.Ingrediente
import com.example.myapprecetas.objetos.dto.creacion.DTONuevaReceta
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import com.example.myapprecetas.objetos.dto.creacion.RecetaResponse
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

    @GET("ingredientes")
    suspend fun getIngredientes(): Response<List<Ingrediente>>

    @GET("ingredientes/Buscar")
    suspend fun buscarIngredientes(
        @Query("busquedaNombre") busquedaNombre: String
    ): Response<List<Ingrediente>>

    @GET("Categorias/listado")
    suspend fun getCategorias(): Response<List<Categoria>>

    @GET("Usuarios/{FirebaseUID}")
    suspend fun getUsuarioUID(@Path("FirebaseUID") firebaseUID: String): Response<DTOUsuario>

    @POST("recetas/add")
    suspend fun subirNuevaReceta(@Body receta: DTONuevaReceta): Response<RecetaResponse>

}