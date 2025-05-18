package com.example.myapprecetas.vm

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapprecetas.api.Endpoints
import com.example.myapprecetas.objetos.dto.DTORecetaSimplificada
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

    private val _listaRecetas = MutableStateFlow<List<DTORecetaSimplificada>>(emptyList())
    val listaRecetas: StateFlow<List<DTORecetaSimplificada>> = _listaRecetas

    private val _nombreUsuario = MutableStateFlow<String?>("Usuario")
    val nombreUsuario: StateFlow<String?> = _nombreUsuario

    private var _cargando = MutableStateFlow<Boolean>(false)
    var cargando: StateFlow<Boolean> = _cargando

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing

    init{


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

    fun cargaRecetas() {
        viewModelScope.launch {
            _cargando.value = true
            try {
                val response = endpoints.getRecetas()
                if (response.isSuccessful) {
                    response.body()?.let {
                        _listaRecetas.value = it

                        Log.i("OKAY", response.body().toString())
                        _cargando.value = false
                    } ?: run {
                        Log.i("RESPUESTA VACIA", response.body().toString())
                        _cargando.value = false
                    }
                } else {
                    Log.i("ERROR SERVIDOR", response.body().toString())
                    _cargando.value = false
                }
            } catch (e: Exception) {
                Log.i("EXCEPTION", e.toString())
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

}