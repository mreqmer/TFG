package com.example.myapprecetas.vm

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapprecetas.api.Endpoints
import com.example.myapprecetas.dto.DTORecetaSimplificada
import com.example.myapprecetas.userauth.AuthManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VMPerfil @Inject constructor(
    private val endpoints: Endpoints
) : ViewModel() {

    private val _listaRecetas = MutableStateFlow<List<DTORecetaSimplificada>>(emptyList())
    private val _nombreUsuario = MutableStateFlow<String?>("Usuario")
    private var _cargando = MutableStateFlow<Boolean>(false)
    private var _email = MutableStateFlow<String?>("")

    val listaRecetas: StateFlow<List<DTORecetaSimplificada>> = _listaRecetas
    val nombreUsuario: StateFlow<String?> = _nombreUsuario
    var cargando: StateFlow<Boolean> = _cargando
    val email: StateFlow<String?> = _email

    init{
        _nombreUsuario.value = AuthManager.currentUser.value?.displayName ?: "Usuario"
        _email.value = AuthManager.currentUser.value?.email ?: ""
        cargaRecetas()
    }

    private fun cargaRecetas() {
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

}
