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
class VMFavoritas @Inject constructor(
    private val endpoints: Endpoints
) : ViewModel() {

    // region Estados observables
    private var _cargando = MutableStateFlow(false)
    var cargando: StateFlow<Boolean> = _cargando

    private val _listaRecetas = MutableStateFlow<List<DTORecetaUsuarioLike>>(emptyList())
    val listaRecetas: StateFlow<List<DTORecetaUsuarioLike>> = _listaRecetas

    private val _likesEstado = MutableStateFlow<Map<Int, Boolean>>(emptyMap())

    private val _likeInProgress = MutableStateFlow(false)
    private val _isRefreshing = MutableStateFlow(false)

    val isRefreshing: StateFlow<Boolean> = _isRefreshing
    // endregion

    // region Init
    init {
        cargaRecetas()
    }
    // endregion

    // region Actualiza
    /**
     * Refresca la lista de recetas favoritas en el pulltorefesh
     */
    fun onRefresh() {
        viewModelScope.launch {
            _isRefreshing.value = true
            cargaRecetas()
            _isRefreshing.value = false
        }
    }
    // endregion

    // region Llamadas API

    /**
     * Llama al endpoint para obtener las recetas favoritas del usuario actual
     * y actualiza la lista y los estados de like.
     */
    private fun cargaRecetas() {
        val uid = currentUser.value?.uid ?: ""
        _cargando.value = true

        viewModelScope.launch {
            try {
                val response = endpoints.getRecetasFavoritasPorUid(uid)
                if (response.isSuccessful) {
                    response.body()?.let { lista ->
                        _listaRecetas.value = lista

                        // Asocia el estado de like de cada receta a su id
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
     * Cambia el estado de like para una receta determinada.
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

                        // Actualizar mapa de likes en memoria
                        _likesEstado.value = _likesEstado.value.toMutableMap().apply {
                            put(idReceta, nuevoEstado)
                        }

                        // Actualizar la receta correspondiente en la lista
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

    // endregion
}