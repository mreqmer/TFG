package com.example.myapprecetas.vm

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapprecetas.api.Endpoints
import com.example.myapprecetas.objetos.dto.DTORecetaUsuarioLike
import com.example.myapprecetas.objetos.dto.DTOToggleLike
import com.example.myapprecetas.userauth.AuthManager.currentUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VMRecetaCategoria @Inject constructor(
    private val endpoints: Endpoints
) : ViewModel() {

    //region Atributos y propiedades
    private var _categoria = MutableStateFlow<String?>(null)

    private var _cargando = MutableStateFlow<Boolean>(false)
    var cargando: StateFlow<Boolean> = _cargando

    private val _listaRecetas = MutableStateFlow<List<DTORecetaUsuarioLike>>(emptyList())
    val listaRecetas: StateFlow<List<DTORecetaUsuarioLike>> = _listaRecetas

    private val _likesEstado = MutableStateFlow<Map<Int, Boolean>>(emptyMap())

    private val _likeInProgress = MutableStateFlow<Boolean>(false)

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing
    //endregion

    //region constructor
    init {
    }

    //endregion

    //region Actualiza
    /**
     * Establece la categoría seleccionada
     *
     * @param categoria Categoría seleccionada por el usuario.
     */
    fun setCategoria(categoria: String) {
        if (_categoria.value != categoria) {
            _categoria.value = categoria
            cargaRecetasFiltro()
        }
    }



    /**
     * Llama a la función de carga y marca el estado de refresco.
     */
    fun onRefresh() {
        viewModelScope.launch {
            _isRefreshing.value = true
            cargaRecetasFiltro()
            _isRefreshing.value = false
        }
    }
    //endregion

    //region Llamadas API
    /**
     * Carga la lista de recetas filtradas por categoría
     * También actualiza el estado local de likes.
     */
    fun cargaRecetasFiltro() {
        val uid = currentUser.value?.uid ?: ""
        _cargando.value = true
        viewModelScope.launch {
            _cargando.value = true
            try {
                val response = endpoints.getRecetasLikesFiltrado(uid = uid, categoria =  _categoria.value)
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

    /**
     * Alterna el estado del like de una receta específica.
     *
     * @param idReceta ID de la receta a la que se quiere dar o quitar like.
     */
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
    //endregion

    //region Funciones
    /**
     * Retorna el texto de bienvenida con la categoría actual.
     *
     * @return Texto con la categoría seleccionada o cadena vacía si no hay ninguna.
     */
    fun textoBienvenida(): String{
        return if(_categoria.value.isNullOrEmpty()) "" else _categoria.value!!
    }
    //endregion

}