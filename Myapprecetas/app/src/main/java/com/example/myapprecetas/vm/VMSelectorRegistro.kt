package com.example.myapprecetas.vm

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapprecetas.repositories.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VMSelectorRegistro@Inject constructor(
    private val authRepository: AuthRepository,
) : ViewModel()  {

    private val _cargando = MutableStateFlow(false)
    val cargando: StateFlow<Boolean> = _cargando

    private val _loginSuccess = MutableStateFlow(false)

    /**
     * Hace el sign in con google
     */
    fun signInWithGoogle(idToken: String) {
        viewModelScope.launch {
            try {
                _cargando.value = true
                val success = authRepository.signInWithGoogle(idToken)
                _loginSuccess.value = success
                if (!success) {
                    Log.d(":::ERROR", "Error al autenticar con Google")
                }
            } catch (e: Exception) {
                Log.d(":::ERROR", "Error ")
            } finally {
                _cargando.value = false
            }
        }
    }
}
