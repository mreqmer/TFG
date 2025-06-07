package com.example.myapprecetas.api

import com.example.myapprecetas.objetos.dto.Categoria
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
import retrofit2.http.DELETE

interface Endpoints {

    //region RECETAS - OPERACIONES GENERALES

    /**
     * Obtener todas las recetas simplificadas creadas por un usuario específico.
     */
    @GET("Recetas/Creador/{idUsuario}")
    suspend fun getRecetasPorIdUsuario(
        @Path("idUsuario") idUsuario: Int
    ): Response<List<DTORecetaSimplificada>>


    /**
     * Subir una nueva receta.
     */
    @POST("Recetas/Add")
    suspend fun subirNuevaReceta(
        @Body receta: DTONuevaReceta
    ): Response<RecetaResponse>

    /**
     * Borrar una receta según UID del creador e ID de la receta.
     */
    @DELETE("Recetas/Borrar")
    suspend fun borrarReceta(
        @Query("uid") uid: String,
        @Query("idReceta") idReceta: Int
    ): Response<Unit>

    //endregion

    //region RECETAS - LIKES Y FAVORITOS

    /**
     * Alternar el "like" de una receta para un usuario.
     */
    @POST("Likes/toggle")
    suspend fun toggleLike(
        @Body like: DTOToggleLike
    ): Response<RecetaUsuarioLikeResponse>

    /**
     * Obtener recetas con like para un usuario.
     */
    @GET("Recetas/RecetaLikes/Listado/{uid}")
    suspend fun getRecetasConLike(
        @Path("uid") firebaseUid: String
    ): Response<List<DTORecetaUsuarioLike>>

    /**
     * Obtener recetas favoritas del usuario.
     */
    @GET("Recetas/RecetaLikes/ListadoFav/{uid}")
    suspend fun getRecetasFavoritasPorUid(
        @Path("uid") firebaseUid: String
    ): Response<List<DTORecetaUsuarioLike>>

    /**
     * Obtener detalles de una receta con like
     */
    @POST("Recetas/RecetaLikes")
    suspend fun getRecetaDetalladaLike(
        @Body recetaDetalladaLike: DTOToggleLike
    ): Response<DTORecetaDetalladaLike>

    /**
     * Obtener recetas con like filtradas por parámetros (nombre, categoría, etc.).
     */
    @GET("Recetas/RecetaLikes/Listado/Busqueda/{uid}")
    suspend fun getRecetasLikesFiltrado(
        @Path("uid") uid: String,
        @Query("busqueda") busqueda: String? = null,
        @Query("categoria") categoria: String? = null,
        @Query("tiempo") tiempo: Int? = null,
        @Query("dificultad") dificultad: String? = null,
        @Query("ingredientes") ingredientes: List<String>? = null
    ): Response<List<DTORecetaUsuarioLike>>

    //endregion

    //region INGREDIENTES Y CATEGORÍAS

    /**
     * Buscar ingredientes por nombre.
     */
    @GET("ingredientes/Buscar")
    suspend fun buscarIngredientes(
        @Query("busquedaNombre") busquedaNombre: String
    ): Response<List<Ingrediente>>

    /**
     * Obtener el listado completo de categorías.
     */
    @GET("Categorias/listado")
    suspend fun getCategorias(): Response<List<Categoria>>

    //endregion

    //region USUARIOS

    /**
     * Obtener datos del usuario por su UID de Firebase.
     */
    @GET("Usuarios/{FirebaseUID}")
    suspend fun getUsuarioUID(
        @Path("FirebaseUID") firebaseUID: String
    ): Response<DTOUsuario>

    /**
     * Insertar un nuevo usuario en la base de datos.
     */
    @POST("Usuarios/Nuevo")
    suspend fun postNuevoUsuario(
        @Body usuarioInsert: DTOInsertUsuario
    ): Response<Usuario>

    //endregion
}