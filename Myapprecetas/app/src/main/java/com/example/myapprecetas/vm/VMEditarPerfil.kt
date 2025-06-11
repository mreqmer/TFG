package com.example.myapprecetas.vm

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapprecetas.api.Endpoints
import com.example.myapprecetas.objetos.dto.DTOUpdateUsuario
import com.example.myapprecetas.repositories.AuthRepository
import com.example.myapprecetas.repositories.CloudinaryRepository
import com.example.myapprecetas.userauth.AuthManager.currentUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@HiltViewModel
class VMEditarPerfil @Inject constructor(
    private val endpoints: Endpoints,
    private val authRepository: AuthRepository,
    private val repo: CloudinaryRepository,
) : ViewModel() {

    // URL de la imagen de perfil por defecto
    private val DEFAULT_FOTO = "https://res.cloudinary.com/dckzmg9c1/image/upload/v1747491439/fotoperfil_cfajca.png"

    private val _nombreUsuario = MutableStateFlow("")
    val nombreUsuario: StateFlow<String> = _nombreUsuario

    private val _editandoNombre = MutableStateFlow(false)
    val editandoNombre: StateFlow<Boolean> =  _editandoNombre

    private val _imagenUri = MutableStateFlow<Uri?>(null)
    var imagenUri: StateFlow<Uri?> = _imagenUri

    private val _guardandoCambios = MutableStateFlow(false)
    val guardandoCambios: StateFlow<Boolean> = _guardandoCambios

    private val _guardadoExitoso = MutableStateFlow(false)
    val guardadoExitoso: StateFlow<Boolean> = _guardadoExitoso

    private val _errorGuardado = MutableStateFlow<String?>(null)
    val errorGuardado: StateFlow<String?> = _errorGuardado

    //Guarda la foto y el id de esta actuales por si se editan
    private var fotoPerfilActualUrl: String? = null
    private var publicIdFotoActual: String? = null

    init {
        cargaDatos()
    }


    /**
     * Carga los datos iniciales
     */
    fun cargaDatos() {
        _nombreUsuario.value = currentUser.value?.displayName ?: "User"
        _imagenUri.value = currentUser.value?.photoUrl

        fotoPerfilActualUrl = currentUser.value?.photoUrl?.toString() ?: DEFAULT_FOTO
        publicIdFotoActual = obtenerPublicIdDeUrl(fotoPerfilActualUrl)
    }

    fun cancelarNombre() {
        _editandoNombre.value = false
    }

    fun cambiarNombre(nombre: String) {
        _nombreUsuario.value = nombre
    }

    fun toggleEditandoNombre() {
        _editandoNombre.value = !_editandoNombre.value
    }

    fun actualizarImagen(uri: Uri?) {
        if (uri != null) {
            _imagenUri.value = uri
        }
    }

    /**
     * Guarda los cambios del perfil: nombre, foto y actualiza en API, Firebase y Cloudinary
     */
    fun guardarCambios() {
        viewModelScope.launch {
            _guardandoCambios.value = true
            val uid = currentUser.value?.uid

            try {
                // Actualiza el nombre en Firebase
                if (_nombreUsuario.value.isNotBlank() && _nombreUsuario.value != currentUser.value?.displayName) {
                    authRepository.updateDisplayName(_nombreUsuario.value)
                }

                var urlFotoNueva: String? = null
                var publicIdFotoNueva: String? = null

                // Si se actualiza la imagen se sube a cloudinary
                if (_imagenUri.value != null && _imagenUri.value.toString() != fotoPerfilActualUrl) {
                    val resultado = subirImagenSuspend(_imagenUri.value!!)
                    urlFotoNueva = resultado.first
                    publicIdFotoNueva = resultado.second
                    if (urlFotoNueva != null) {
                        authRepository.updateProfilePhoto(urlFotoNueva)
                    }
                }

                // Actualizar usuario en la API
                val usuarioUpdate = uid?.let {
                    DTOUpdateUsuario(
                        uid = it,
                        nombreUsuario = _nombreUsuario.value,
                        fotoPerfil = urlFotoNueva ?: fotoPerfilActualUrl ?: DEFAULT_FOTO
                    )
                }

                val response = usuarioUpdate?.let { endpoints.updateUsuario(it) }
                if (response != null) {
                    if (!response.isSuccessful) {
                        Log.e(":::Error", "Error al actualizar usuario en API: ${response.errorBody()?.string()}")

                    }
                }

                // Borrar la foto antigua si se ha actualizado
                if (urlFotoNueva != null
                    && publicIdFotoActual != null
                    && publicIdFotoActual != publicIdFotoNueva
                    && fotoPerfilActualUrl != null
                    && fotoPerfilActualUrl != DEFAULT_FOTO
                    && !esFotoGoogleDefault(fotoPerfilActualUrl!!)
                ) {
                    repo.deleteImage(
                        publicIdFotoActual!!,
                        onSuccess = { Log.d(":::ERROR", "Foto antigua borrada correctamente") },
                        onError = { err -> Log.e(":::ERROR", "Error borrando foto antigua: $err") }
                    )
                }
                _guardadoExitoso.value = true
            } catch (e: Exception) {
                Log.e("VMEditarPerfil", "Error guardando cambios: ${e.localizedMessage}")
                _errorGuardado.value = "Error al guardar cambios: ${e.localizedMessage}"
                _guardadoExitoso.value = false
            }finally {
                _guardandoCambios.value = false
            }
        }
    }

    /**
     * Suspende la corrutina mientras se sube la imagen a Cloudinary
     */
    private suspend fun subirImagenSuspend(uri: Uri): Pair<String?, String?> {
        return suspendCoroutine { cont ->
            repo.uploadImage(
                imageUri = uri,
                onLoading = { },
                onSuccess = { url, publicIdResult -> cont.resume(url to publicIdResult) },
                onError = { error -> cont.resume(null to null) }
            )
        }
    }

    /**
     * Extrae el publicId de la URL de la imagen de Cloudinary
     */
    private fun obtenerPublicIdDeUrl(url: String?): String? {
        if (url == null) return null
        var publicId: String  = ""
        try {
            val partes = url.split("/")

            val ultimoSegmento = partes.lastOrNull() ?: return null

            publicId = ultimoSegmento.substringBeforeLast('.')


        } catch (e: Exception) {
            Log.e(":::ERROR", "Error obteniendo publicID cambios: ${e.localizedMessage}")
        }
        return publicId
    }

    /**
     * Determina si la imagen actual es una predeterminada de Google
     */
    private fun esFotoGoogleDefault(url: String): Boolean {
        return url.contains("googleusercontent.com")
    }
}