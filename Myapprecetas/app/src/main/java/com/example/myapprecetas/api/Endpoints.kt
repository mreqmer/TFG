package com.example.myapprecetas.api

import com.example.myapprecetas.objetos.dto.Categoria
import com.example.myapprecetas.objetos.dto.DTORecetaDetallada
import com.example.myapprecetas.objetos.dto.DTORecetaDetalladaLike
import com.example.myapprecetas.objetos.dto.DTORecetaSimplificada
import com.example.myapprecetas.objetos.dto.DTORecetaUsuarioLike
import com.example.myapprecetas.objetos.dto.DTOToggleLike
import com.example.myapprecetas.objetos.dto.DTOUsuario
import com.example.myapprecetas.objetos.dto.Ingrediente
import com.example.myapprecetas.objetos.dto.RecetaUsuarioLikeResponse
import com.example.myapprecetas.objetos.dto.Usuario
import com.example.myapprecetas.objetos.dto.creacion.DTOInsertUsuario
import com.example.myapprecetas.objetos.dto.creacion.DTONuevaReceta
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import com.example.myapprecetas.objetos.dto.creacion.RecetaResponse
import okhttp3.ResponseBody
import retrofit2.http.DELETE

interface Endpoints {


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

    @POST("Usuarios/Nuevo")
    suspend fun postNuevoUsuario(@Body usuarioInsert: DTOInsertUsuario): Response<Usuario>

    @GET("recetas/RecetasLikes/{uid}")
    suspend fun getRecetasConLike(@Path("uid") firebaseUid: String): Response<List<DTORecetaUsuarioLike>>

    @GET("recetas/RecetasLikes/Busqueda/{uid}/{busqueda}")
    suspend fun obtenerRecetasLikesPorNombre(@Path("uid") uid: String, @Path("busqueda") busqueda: String): Response<List<DTORecetaUsuarioLike>>

    @GET("recetas/likes/{uid}")
    suspend fun getRecetasFavoritasPorUid(@Path("uid") firebaseUid: String): Response<List<DTORecetaUsuarioLike>>

    @POST("Likes/toggle")
    suspend fun toggleLike(@Body like: DTOToggleLike): Response<RecetaUsuarioLikeResponse>

    @POST("Recetas/Favorita")
    suspend fun getRecetaDetalladaLike(@Body recetaDetalladaLike: DTOToggleLike): Response<DTORecetaDetalladaLike>

    @GET("recetas/RecetasLikes/Busqueda/{uid}")
    suspend fun getRecetasLikesFiltrado(
        @Path("uid") uid: String,
        @Query("busqueda") busqueda: String? = null,
        @Query("categoria") categoria: String? = null,
        @Query("tiempo") tiempo: Int? = null,
        @Query("dificultad") dificultad: String? = null,
        @Query("ingredientes") ingredientes: List<String>? = null
    ): Response<List<DTORecetaUsuarioLike>>

    @DELETE("Recetas/Borrar")
    suspend fun borrarReceta(
        @Query("uid") uid: String,
        @Query("idReceta") idReceta: Int
    ): Response<Unit>

}