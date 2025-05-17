package com.example.myapprecetas.vm

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapprecetas.api.Endpoints
import com.example.myapprecetas.objetos.dto.DTORecetaSimplificada
import com.example.myapprecetas.userauth.AuthManager
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

    //TODO Esto es temporal
    private val _recetasDestacadas = MutableStateFlow<List<DTORecetaSimplificada>>(emptyList())
    val recetasDestacadas: StateFlow<List<DTORecetaSimplificada>> = _recetasDestacadas

    private var _cargando = MutableStateFlow<Boolean>(false)
    var cargando: StateFlow<Boolean> = _cargando

    init{
        _nombreUsuario.value = AuthManager.currentUser.value?.displayName ?: "Usuario"
        cargaRecetas()
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

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing


    fun refreshData() {
        viewModelScope.launch {
            _isRefreshing.value = true
            // Ejemplo: Recargar datos desde tu fuente (API, base de datos, etc.)
            try {
                cargaRecetas() // Tu función existente para cargar datos
            } finally {
                _isRefreshing.value = false
            }
        }
    }

}