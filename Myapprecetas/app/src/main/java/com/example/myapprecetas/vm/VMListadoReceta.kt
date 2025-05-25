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
import com.google.android.play.integrity.internal.c
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VMListadoReceta @Inject constructor(
    private val endpoints: Endpoints
) : ViewModel() {

//    val categorias = listOf("Postres", "Carnes", "Veganas", "Rápidas", "Internacional")
    val tiempos = listOf("< 15 min","< 30 min", "< 45 min", "< 1h", "< 2h")
    val dificultades = ConstantesObjetos.Recetas_Dificultad.values().map { it.label }

    private val _categorias = MutableStateFlow<List<String?>>(emptyList())
    val categorias: StateFlow<List<String?>> = _categorias

    // Estados de filtros seleccionados
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

    private var _cargando = MutableStateFlow<Boolean>(false)
    var cargando: StateFlow<Boolean> = _cargando

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing

    private val _likesEstado = MutableStateFlow<Map<Int, Boolean>>(emptyMap())

    // Estado para bloquear botón like mientras se hace petición
    private val _likeInProgress = MutableStateFlow<Boolean>(false)

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _ingredientesDisponibles = MutableStateFlow<List<Ingrediente>>(emptyList())
    val ingredientesDisponibles: StateFlow<List<Ingrediente>> = _ingredientesDisponibles




    init {
        enviarUsuarioApi()
        cargaRecetas()
        cargaCategorias()
    }



    fun setFiltroCategoria(categoria: String?) {
        _filtroSeleccionadoCategoria.value = categoria
    }

    fun setFiltroTiempo(tiempo: String?) {
        _filtroSeleccionadoTiempo.value = tiempo
    }

    fun setFiltroDificultad(dificultad: String?) {
        _filtroSeleccionadoDificultad.value = dificultad
    }



    fun onRefresh() {
        _searchQuery.value = "";
        viewModelScope.launch {
            _isRefreshing.value = true
            cargaRecetas()
            _isRefreshing.value = false
        }
    }

    fun onActualizaQuery(newQuery: String) {
        _searchQuery.value = newQuery
    }

    fun cargaRecetas() {
        val uid = currentUser.value?.uid ?: ""
        _cargando.value = true
        viewModelScope.launch {
            _cargando.value = true
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

                        // Actualizar el estado local del like
                        _likesEstado.value = _likesEstado.value.toMutableMap().apply {
                            put(idReceta, nuevoEstado)
                        }
                        _listaRecetas.value = _listaRecetas.value.map { receta ->
                            if (receta.idReceta == idReceta) {
                                receta.copy(tieneLike = nuevoEstado)
                            } else receta
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

    fun cargaCategorias(){
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
                    Log.e("BuscarRecetas", "Error servidor: ${response.code()} ${response.message()}")
                }
            } catch (e: Exception) {
                Log.e("BuscarRecetas", "Excepción: ${e.message}")
            } finally {
                _cargando.value = false
            }
        }
    }

    fun buscarRecetas() {
        val uid = currentUser.value?.uid ?: return
        val query = _searchQuery.value?.trim().orEmpty()

        val categoria = _filtroSeleccionadoCategoria.value
        val dificultad = _filtroSeleccionadoDificultad.value
        val tiempoFiltrado = parseTiempoSeleccionado()
        val ingredientesNombres = _ingredientesSeleccionados.value
            ?.mapNotNull { it.nombreIngrediente }
            ?.filter { it.isNotBlank() }
            ?: emptyList()

        _cargando.value = true

        viewModelScope.launch {
            try {
                val response = endpoints.getRecetasLikesFiltrado(
                    uid = uid,
                    busqueda = query,
                    categoria = categoria,
                    tiempo = tiempoFiltrado,
                    dificultad = dificultad,
                    ingredientes = if (ingredientesNombres.isNotEmpty()) ingredientesNombres else null
                )
                if (response.isSuccessful) {
                    response.body()?.let { lista ->
                        _listaRecetas.value = lista
                        _likesEstado.value = lista.associate { it.idReceta to it.tieneLike }
                    }
                } else if (response.code() == 404) {
                    _listaRecetas.value = emptyList()
                    _likesEstado.value = emptyMap()
                }else {
                    Log.e("BuscarRecetas", "Error servidor: ${response.code()} ${response.message()}")

                }
            } catch (e: Exception) {
                Log.e("BuscarRecetas", "Excepción: ${e.message}")
            } finally {
                _cargando.value = false
            }
        }
    }

    private fun enviarUsuarioApi() {
        val usuarioDTO = DTOInsertUsuario(
            firebaseUID = currentUser.value?.uid ?: "",
            correoElectronico = currentUser.value?.email ?: "",
            nombreUsuario = currentUser.value?.displayName ?: ""
        )
        viewModelScope.launch {
            _cargando.value = true
            try {
                Log.d(":::API", "Enviando usuario a API: $usuarioDTO")
                val response = endpoints.postNuevoUsuario(usuarioDTO)
                if (response.isSuccessful) {
                    Log.i(":::OKAY", "Usuario enviado correctamente a la API")
                } else {
                    Log.i(":::ERROR SERVIDOR", "Error al enviar usuario: ${response.code()} - ${response.message()}")
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

    private val _listadoIngredientes = MutableStateFlow<List<Ingrediente>>(emptyList())
    val listadoIngredientes: StateFlow<List<Ingrediente>> = _listadoIngredientes

    private val _cargandoIngredientes = MutableStateFlow(false)
    val cargandoIngredientes: StateFlow<Boolean> = _cargandoIngredientes

    private val _ingredientesSeleccionados = MutableStateFlow<List<Ingrediente>>(emptyList())
    val ingredientesSeleccionados: StateFlow<List<Ingrediente>> = _ingredientesSeleccionados

    fun agregarIngredienteSeleccionado(ingrediente: Ingrediente) {
        _ingredientesSeleccionados.value += ingrediente
        _listadoIngredientes.value = emptyList()
    }

    fun quitarIngredienteSeleccionado(ingrediente: Ingrediente) {
        _ingredientesSeleccionados.value = _ingredientesSeleccionados.value - ingrediente
    }



    fun buscarIngredientes(busquedaNombre: String) {
        viewModelScope.launch {
            _cargandoIngredientes.value = true
            try {
                // Llamamos al endpoint de búsqueda de ingredientes
                val response = endpoints.buscarIngredientes(busquedaNombre)

                if (response.isSuccessful) {
                    response.body()?.let {
                        _listadoIngredientes.value = it // Actualizamos la lista de ingredientes
                        Log.i("OKAY", "Ingredientes encontrados: ${it.size}")
                    } ?: run {
                        Log.i("RESPUESTA VACIA", "La respuesta de búsqueda está vacía.")
                    }
                } else {
                    Log.i("ERROR SERVIDOR", "Error en la respuesta del servidor: ${response.message()}")
                }
            } catch (e: Exception) {
                Log.i("EXCEPTION", "Error al buscar ingredientes: $e")
            } finally {
                _cargandoIngredientes.value = false
            }
        }
    }
    fun parseTiempoSeleccionado(): Int? {
        return when (_filtroSeleccionadoTiempo.value) {
            "< 15 min" -> 15
            "< 30 min" -> 30
            "< 45 min" -> 45
            "< 1h" -> 60
            "< 2h" -> 120
            else -> null
        }
    }
}