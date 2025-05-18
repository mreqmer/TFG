package com.example.myapprecetas.vm

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapprecetas.api.Endpoints
import com.example.myapprecetas.objetos.dto.DTORecetaSimplificada
import com.example.myapprecetas.objetos.dto.DTORecetaUsuarioLike
import com.example.myapprecetas.objetos.dto.DTOToggleLike
import com.example.myapprecetas.objetos.dto.creacion.DTOInsertUsuario
import com.example.myapprecetas.userauth.AuthManager
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

    private val _listaRecetas = MutableStateFlow<List<DTORecetaUsuarioLike>>(emptyList())
    val listaRecetas: StateFlow<List<DTORecetaUsuarioLike>> = _listaRecetas

    private val _nombreUsuario = MutableStateFlow<String?>("Usuario")
    val nombreUsuario: StateFlow<String?> = _nombreUsuario

    private var _cargando = MutableStateFlow<Boolean>(false)
    var cargando: StateFlow<Boolean> = _cargando

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing

    private val _likesEstado = MutableStateFlow<Map<Int, Boolean>>(emptyMap())
    val likesEstado: StateFlow<Map<Int, Boolean>> = _likesEstado

    // Estado para bloquear botón like mientras se hace petición
    private val _likeInProgress = MutableStateFlow<Boolean>(false)
    val likeInProgress: StateFlow<Boolean> = _likeInProgress

    init {
        enviarUsuarioApi()
        cargaRecetas()
    }

    fun onRefresh() {
        viewModelScope.launch {
            _isRefreshing.value = true
            cargaRecetas()
            _isRefreshing.value = false
        }
    }

    private fun cargaRecetas() {
        val uid = currentUser.value?.uid ?: ""

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
}