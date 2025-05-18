package com.example.myapprecetas.vm

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.myapprecetas.repositories.AuthRepository
import com.example.myapprecetas.repositories.CloudinaryRepository
import com.example.myapprecetas.userauth.AuthManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class VMConstrucciondos @Inject constructor(
    private val repo: CloudinaryRepository,
    private val authRepository: AuthRepository,
) : ViewModel() {

    var isLoading by mutableStateOf(false)
        private set

    var imageUrl by mutableStateOf<String?>(null)
        private set

    var publicId by mutableStateOf<String?>(null)
        private set

    var uploadError by mutableStateOf<String?>(null)
        private set

    // Campo para el UID que quieras consultar
    var uidInput by mutableStateOf("")

    // Estado para el nombre del usuario consultado
    private val _nombreUsuario = MutableStateFlow<String?>("Usuario no encontrado")
    val nombreUsuario: StateFlow<String?> = _nombreUsuario

    fun subirImagen(uri: Uri) {
        isLoading = true
        uploadError = null
        imageUrl = null
        publicId = null

        repo.uploadImage(
            imageUri = uri,
            onLoading = {
                isLoading = true
            },
            onSuccess = { url, publicIdResult ->
                imageUrl = url
                publicId = publicIdResult
                isLoading = false
            },
            onError = { error ->
                uploadError = error
                isLoading = false
            }
        )
    }

    suspend fun borrarImagen() {
        val publicIdValue = publicId

        if (publicIdValue.isNullOrEmpty()) {
            uploadError = "No hay una imagen para borrar"
            return
        }

        isLoading = true
        uploadError = null

        repo.deleteImage(
            publicId = publicIdValue,
            onSuccess = {
                imageUrl = null
                publicId = null
                isLoading = false
            },
            onError = { error ->
                uploadError = error
                isLoading = false
            }
        )
    }

    suspend fun updateProfilePhoto(photoUrl: String) {
        isLoading = true
        uploadError = null

        try {
            authRepository.updateProfilePhoto(photoUrl)
            uploadError = null
        } catch (e: Exception) {
            uploadError = "Error al actualizar la foto: ${e.localizedMessage}"
        } finally {
            isLoading = false
        }
    }

}
