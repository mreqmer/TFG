package com.example.myapprecetas.vm

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.myapprecetas.repositories.AuthRepository
import com.example.myapprecetas.repositories.CloudinaryRepository
import com.example.myapprecetas.userauth.AuthManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
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
            authRepository.updateProfilePhoto(photoUrl)  // con la instancia inyectada
            uploadError = null
        } catch (e: Exception) {
            uploadError = "Error al actualizar la foto: ${e.localizedMessage}"
        } finally {
            isLoading = false
        }
    }
}

