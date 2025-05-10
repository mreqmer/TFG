package com.example.myapprecetas.vm

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.myapprecetas.repositories.CloudinaryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class VMConstrucciondos @Inject constructor(
    private val repo: CloudinaryRepository
) : ViewModel() {

    var isLoading by mutableStateOf(false)
        private set

    var imageUrl by mutableStateOf<String?>(null)
        private set

    var uploadError by mutableStateOf<String?>(null)
        private set

    fun subirImagen(uri: Uri) {
        isLoading = true
        uploadError = null
        imageUrl = null

        repo.uploadImage(
            imageUri = uri,
            onLoading = {
                isLoading = true
            },
            onSuccess = { url ->
                imageUrl = url
                isLoading = false
            },
            onError = { error ->
                uploadError = error
                isLoading = false
            }
        )
    }
}

