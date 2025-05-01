package com.example.myapprecetas.vm

import androidx.lifecycle.ViewModel
import com.example.myapprecetas.repositories.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class VMRegistro@Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {


    private val _nombre = MutableStateFlow("")
    private val _email = MutableStateFlow("")
    private val _password = MutableStateFlow("")
    private val _repetirPassword = MutableStateFlow("")
    private val _error = MutableStateFlow<String?>(null)

    // Estados públicos
    val nombre: StateFlow<String> = _nombre
    val email: StateFlow<String> = _email
    val password: StateFlow<String> = _password
    val repetirPassword: StateFlow<String> = _repetirPassword
    val error: StateFlow<String?> = _error


    // Funciones para actualizar
    fun onNombreChange(newNombre: String) {
        _nombre.value = newNombre
    }

    fun onEmailChange(newEmail: String) {
        _email.value = newEmail
    }

    fun onPasswordChange(newPassword: String) {
        _password.value = newPassword
    }

    fun onRepetirPasswordChange(newRepetirPassword: String) {
        _repetirPassword.value = newRepetirPassword
    }

}