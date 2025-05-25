package com.example.myapprecetas.vm

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapprecetas.api.Endpoints
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VMEditarPerfil @Inject constructor(
    private val endpoints: Endpoints // API inyectada
) : ViewModel() {

    // Nombre de usuario editable
    private val _nombreUsuario = MutableStateFlow("")
    val nombreUsuario: StateFlow<String> = _nombreUsuario

    // Email solo de lectura (podrías traerlo del backend si lo necesitas)
    private val _emailUsuario = MutableStateFlow("")
    val emailUsuario: StateFlow<String> = _emailUsuario

    // Estado de edición
    private val _editandoNombre = MutableStateFlow(false)
    val editandoNombre = _editandoNombre

    private val _editandoContrasena = MutableStateFlow(false)
    val editandoContrasena = _editandoContrasena

    // Contraseña
    private val _nuevaContrasena = MutableStateFlow("")
    val nuevaContrasena = _nuevaContrasena

    // Imagen seleccionada
    private val _imagenUri = MutableStateFlow<Uri?>(null)
    var imagenUri = _imagenUri


    // Acciones de UI
    fun cambiarNombre(nombre: String) {
        _nombreUsuario.value = nombre
    }

    fun cambiarContrasena(nueva: String) {
        _nuevaContrasena.value = nueva
    }

    fun toggleEditandoNombre() {
        _editandoNombre.value = !_editandoNombre.value
    }

    fun toggleEditandoContrasena() {
        _editandoContrasena.value = !_editandoContrasena.value
    }

    fun cancelarNombre() {
        _editandoNombre.value = false
    }

    fun cancelarContrasena() {
        _editandoContrasena.value = false
        _nuevaContrasena.value = ""
    }

    fun actualizarImagen(uri: Uri?) {
        _imagenUri.value = uri
    }

    fun guardarCambios() {
        viewModelScope.launch {

            _editandoNombre.value = false
            _editandoContrasena.value = false
        }
    }
}