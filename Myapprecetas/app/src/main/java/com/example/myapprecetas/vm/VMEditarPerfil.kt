package com.example.myapprecetas.vm

import android.net.Uri
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapprecetas.api.Endpoints
import com.example.myapprecetas.repositories.AuthRepository
import com.example.myapprecetas.repositories.CloudinaryRepository
import com.example.myapprecetas.userauth.AuthManager.currentUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

//TODO, actualizar en la api, que el boton espere hata que cargue para navegar, que recomponga en el boton, que borre la imagen anterior cuando subas
@HiltViewModel
class VMEditarPerfil @Inject constructor(
    private val endpoints: Endpoints,
    private val authRepository: AuthRepository,
    private val repo: CloudinaryRepository,
) : ViewModel() {

    // Nombre de usuario editable
    private val _nombreUsuario = MutableStateFlow("")
    val nombreUsuario: StateFlow<String> = _nombreUsuario


    // Estado de edición
    private val _editandoNombre = MutableStateFlow(false)
    val editandoNombre: StateFlow<Boolean> =  _editandoNombre


    // Imagen seleccionada
    private val _imagenUri = MutableStateFlow<Uri?>(null)
    var imagenUri: StateFlow<Uri?> = _imagenUri



    init{
        cargaDatos()
    }

    fun cargaDatos(){
        _nombreUsuario.value = currentUser.value?.displayName ?: "User"
        _imagenUri.value = currentUser.value?.photoUrl

    }

    fun cancelarNombre() {
        _editandoNombre.value = false
    }

    // Acciones de UI
    fun cambiarNombre(nombre: String) {
        _nombreUsuario.value = nombre
    }

    fun toggleEditandoNombre() {
        _editandoNombre.value = !_editandoNombre.value
    }

     fun editarDisplayName(){
        viewModelScope.launch {
            if(!_nombreUsuario.value.isNullOrEmpty()) {
                authRepository.updateDisplayName(_nombreUsuario.value)
            }
        }
    }


    fun actualizarImagen(uri: Uri?) {
        _imagenUri.value = uri
    }

    fun guardarCambios() {
        viewModelScope.launch {
            if(!_nombreUsuario.value.isNullOrEmpty()) {
                authRepository.updateDisplayName(_nombreUsuario.value)
            }
            _imagenUri.value?.let { uri ->
                subirImagen(uri)
            }
        }
    }

    // Cloudinary
    private val _cargandoImagen = MutableStateFlow(false)
    val cargandoImagen: StateFlow<Boolean> = _cargandoImagen

    private val _errorImagen = MutableStateFlow<String?>(null)
    val errorImagen: StateFlow<String?> = _errorImagen

    private val _fotoReceta = MutableStateFlow("") // URL final
    val fotoReceta: StateFlow<String> = _fotoReceta

    private val _publicId = MutableStateFlow<String?>(null)
    val publicId: StateFlow<String?> = _publicId

    suspend fun updateProfilePhoto(photoUrl: String) {
        try {
            authRepository.updateProfilePhoto(photoUrl)
        } catch (e: Exception) {
            Log.d(":::Error","Error al actualizar la foto: ${e.localizedMessage}")
        }
    }

    fun subirImagen(uri: Uri) {
        _cargandoImagen.value = true
        _errorImagen.value = null.toString()
        _fotoReceta.value = ""
        _publicId.value = null

        repo.uploadImage(
            imageUri = uri,
            onLoading = {
                _cargandoImagen.value = true
            },
            onSuccess = { url, publicIdResult ->
                _fotoReceta.value = url
                _publicId.value = publicIdResult
                _cargandoImagen.value = false
                viewModelScope.launch {
                    updateProfilePhoto(url)
                }
            },
            onError = { error ->
                _errorImagen.value = error
                _cargandoImagen.value = false
            }
        )
    }

}