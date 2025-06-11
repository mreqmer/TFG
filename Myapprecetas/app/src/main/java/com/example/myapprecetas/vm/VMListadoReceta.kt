package com.example.myapprecetas.vm

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapprecetas.api.Endpoints
import com.example.myapprecetas.objetos.dto.DTORecetaUsuarioLike
import com.example.myapprecetas.objetos.dto.DTOToggleLike
import com.example.myapprecetas.objetos.dto.Ingrediente
import com.example.myapprecetas.objetos.dto.constantesobjetos.ConstantesObjetos
import com.example.myapprecetas.objetos.dto.creacion.DTOInsertUsuario
import com.example.myapprecetas.userauth.AuthManager.currentUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VMListadoReceta @Inject constructor(
    private val endpoints: Endpoints
) : ViewModel() {

    // region Propiedades y atributos privado y públicos

    // Listas estáticas públicas para filtros
    val tiempos = listOf("< 15 min", "< 30 min", "< 45 min", "< 1h", "< 2h")
    val dificultades = ConstantesObjetos.Recetas_Dificultad.entries.map { it.label }

    private val _categorias = MutableStateFlow<List<String?>>(emptyList())
    val categorias: StateFlow<List<String?>> = _categorias

    private val _filtroSeleccionadoCategoria = MutableStateFlow<String?>(null)
    val filtroSeleccionadoCategoria: StateFlow<String?> = _filtroSeleccionadoCategoria

    private val _filtroSeleccionadoTiempo = MutableStateFlow<String?>(null)
    val filtroSeleccionadoTiempo: StateFlow<String?> = _filtroSeleccionadoTiempo

    private val _filtroSeleccionadoDificultad = MutableStateFlow<String?>(null)
    val filtroSeleccionadoDificultad: StateFlow<String?> = _filtroSeleccionadoDificultad

    private val _listaRecetas = MutableStateFlow<List<DTORecetaUsuarioLike>>(emptyList())
    val listaRecetas: StateFlow<List<DTORecetaUsuarioLike>> = _listaRecetas

    private val _nombreUsuario = MutableStateFlow<String?>("Usuario")
    val nombreUsuario: StateFlow<String?> = _nombreUsuario

    private var _cargando = MutableStateFlow(false)
    val cargando: StateFlow<Boolean> = _cargando

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing

    private val _likesEstado = MutableStateFlow<Map<Int, Boolean>>(emptyMap())

    private val _likeInProgress = MutableStateFlow(false)

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _listadoIngredientes = MutableStateFlow<List<Ingrediente>>(emptyList())
    val listadoIngredientes: StateFlow<List<Ingrediente>> = _listadoIngredientes

    private val _cargandoIngredientes = MutableStateFlow(false)

    private val _ingredientesSeleccionados = MutableStateFlow<List<Ingrediente>>(emptyList())
    val ingredientesSeleccionados: StateFlow<List<Ingrediente>> = _ingredientesSeleccionados

    // endregion


    // region Constructor

    init {
        enviarUsuarioApi()
        cargaRecetas()
        cargaCategorias()
    }

    // endregion


    // region Actualiza

    /** Actualiza el filtro de categoría */
    fun setFiltroCategoria(categoria: String?) {
        _filtroSeleccionadoCategoria.value = categoria
    }

    /** Actualiza el filtro de tiempo */
    fun setFiltroTiempo(tiempo: String?) {
        _filtroSeleccionadoTiempo.value = tiempo
    }

    /** Actualiza el filtro de dificultad */
    fun setFiltroDificultad(dificultad: String?) {
        _filtroSeleccionadoDificultad.value = dificultad
    }

    /** Actualiza la query de búsqueda */
    fun onActualizaQuery(newQuery: String) {
        _searchQuery.value = newQuery
    }

    // endregion


    // region Métodos para gestión de ingredientes y filtros seleccionados

    /** agrega un ingrediente del filtro */
    fun agregarIngredienteSeleccionado(ingrediente: Ingrediente) {
        _ingredientesSeleccionados.value += ingrediente
        _listadoIngredientes.value = emptyList()
    }

    /** quita el ingrediente del filtro */
    fun quitarIngredienteSeleccionado(ingrediente: Ingrediente) {
        _ingredientesSeleccionados.value -= ingrediente
    }

    /** Restablece todos los filtros y recarga las recetas */
    fun reestableceFiltro() {
        _searchQuery.value = ""
        _filtroSeleccionadoTiempo.value = null
        _filtroSeleccionadoCategoria.value = null
        _filtroSeleccionadoDificultad.value = null
        _ingredientesSeleccionados.value = emptyList()
        cargaRecetas()
    }

    // endregion


    //region LLamadas API

    /** Carga la lista de recetas con likes desde la API */
    private fun cargaRecetas() {
        val uid = currentUser.value?.uid ?: ""
        _cargando.value = true
        viewModelScope.launch {
            try {
                val response = endpoints.getRecetasConLike(uid)
                if (response.isSuccessful) {
                    response.body()?.let { lista ->
                        _listaRecetas.value = lista
                        _likesEstado.value = lista.associate { it.idReceta to it.tieneLike }
                    }
                }
            } catch (e: Exception) {
                Log.i("EXCEPTION", e.toString())
            } finally {
                _cargando.value = false
            }
        }
    }

    /** Alterna el estado de like para una receta */
    fun toggleLike(idReceta: Int) {
        if (_likeInProgress.value) return
        val uid = currentUser.value?.uid ?: return

        viewModelScope.launch {
            _likeInProgress.value = true
            try {
                val likeDto = DTOToggleLike(idReceta, uid)
                val response = endpoints.toggleLike(likeDto)
                if (response.isSuccessful) {
                    val respuesta = response.body()
                    if (respuesta != null && respuesta.success) {
                        val nuevoEstado = respuesta.likeActivo
                        _likesEstado.value = _likesEstado.value.toMutableMap().apply {
                            put(idReceta, nuevoEstado)
                        }
                        _listaRecetas.value = _listaRecetas.value.map { receta ->
                            if (receta.idReceta == idReceta) receta.copy(tieneLike = nuevoEstado)
                            else receta
                        }
                    }
                } else {
                    Log.e("ToggleLike", "Error servidor: ${response.code()} ${response.message()}")
                }
            } catch (e: Exception) {
                Log.e("ToggleLike", "Excepción: ${e.message}")
            } finally {
                _likeInProgress.value = false
            }
        }
    }

    /** Carga la lista de categorías desde la API */
    private fun cargaCategorias() {
        viewModelScope.launch {
            try {
                val response = endpoints.getCategorias()
                if (response.isSuccessful) {
                    response.body()?.let { lista ->
                        val nombres = lista.map { it.nombreCategoria }
                        val listaActual = _categorias.value.toMutableList()
                        listaActual.addAll(nombres)
                        _categorias.value = listaActual
                    }
                } else {
                    Log.e(
                        "BuscarRecetas",
                        "Error servidor: ${response.code()} ${response.message()}"
                    )
                }
            } catch (e: Exception) {
                Log.e("BuscarRecetas", "Excepción: ${e.message}")
            } finally {
                _cargando.value = false
            }
        }
    }

    /** Realiza la búsqueda de recetas según filtros y query */
    fun buscarRecetas() {
        val uid = currentUser.value?.uid ?: return
        val query = _searchQuery.value.trim()
        val categoria = _filtroSeleccionadoCategoria.value
        val dificultad = _filtroSeleccionadoDificultad.value
        val tiempoFiltrado = parseTiempoSeleccionado()
        val ingredientesNombres = _ingredientesSeleccionados.value
            .map { it.nombreIngrediente }
            .filter { it.isNotBlank() }

        _cargando.value = true
        viewModelScope.launch {
            try {
                val response = endpoints.getRecetasLikesFiltrado(
                    uid = uid,
                    busqueda = query,
                    categoria = categoria,
                    tiempo = tiempoFiltrado,
                    dificultad = dificultad,
                    ingredientes = ingredientesNombres.ifEmpty { null }
                )
                if (response.isSuccessful) {
                    response.body()?.let { lista ->
                        _listaRecetas.value = lista
                        _likesEstado.value = lista.associate { it.idReceta to it.tieneLike }
                    }
                } else if (response.code() == 404) {
                    _listaRecetas.value = emptyList()
                    _likesEstado.value = emptyMap()
                } else {
                    Log.e(
                        "BuscarRecetas",
                        "Error servidor: ${response.code()} ${response.message()}"
                    )
                }
            } catch (e: Exception) {
                Log.e("BuscarRecetas", "Excepción: ${e.message}")
            } finally {
                _cargando.value = false
            }
        }
    }

    /** Envía el usuario actual a la API para registro o actualización */
    private fun enviarUsuarioApi() {
        val usuarioDTO = DTOInsertUsuario(
            firebaseUID = currentUser.value?.uid ?: "",
            correoElectronico = currentUser.value?.email ?: "",
            nombreUsuario = currentUser.value?.displayName ?: "",
            fotoPerfil = currentUser.value?.photoUrl?.toString()
            ?: "https://res.cloudinary.com/dckzmg9c1/image/upload/v1747491439/fotoperfil_cfajca.png"
        )
        viewModelScope.launch {
            _cargando.value = true
            try {
                Log.d(":::API", "Enviando usuario a API: $usuarioDTO")
                val response = endpoints.postNuevoUsuario(usuarioDTO)
                if (response.isSuccessful) {
                    Log.i(":::OKAY", "Usuario enviado correctamente a la API")
                } else {
                    Log.i(
                        ":::ERROR SERVIDOR",
                        "Error al enviar usuario: ${response.code()} - ${response.message()}"
                    )
                }
            } catch (e: Exception) {
                Log.i(":::EXCEPTION", "Error al enviar usuario: ${e.message}")
            } finally {
                _cargando.value = false
                Log.d(":::API", "Proceso API finalizado")
                _nombreUsuario.value = currentUser.value?.displayName ?: "Usuario"
            }
        }
    }

    /** Busca ingredientes por nombre usando el endpoint */
    fun buscarIngredientes(busquedaNombre: String) {
        viewModelScope.launch {
            _cargandoIngredientes.value = true
            try {
                val response = endpoints.buscarIngredientes(busquedaNombre)
                if (response.isSuccessful) {
                    response.body()?.let {
                        _listadoIngredientes.value = it
                        Log.i("OKAY", "Ingredientes encontrados: ${it.size}")
                    } ?: Log.i("RESPUESTA VACIA", "La respuesta de búsqueda está vacía.")
                } else {
                    Log.i(
                        "ERROR SERVIDOR",
                        "Error en la respuesta del servidor: ${response.message()}"
                    )
                }
            } catch (e: Exception) {
                Log.i("EXCEPTION", "Error al buscar ingredientes: $e")
            } finally {
                _cargandoIngredientes.value = false
            }
        }
    }

    /** Convierte el filtro de tiempo de texto a entero (minutos) */
    private fun parseTiempoSeleccionado(): Int? = when (_filtroSeleccionadoTiempo.value) {
        "< 15 min" -> 15
        "< 30 min" -> 30
        "< 45 min" -> 45
        "< 1h" -> 60
        "< 2h" -> 120
        else -> null
    }

    // endregion

}