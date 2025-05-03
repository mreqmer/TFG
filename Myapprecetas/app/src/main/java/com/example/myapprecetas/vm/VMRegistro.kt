package com.example.myapprecetas.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapprecetas.repositories.AuthRepository
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VMRegistro@Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {


    // Estados privados
    private val _nombre = MutableStateFlow("")
    private val _email = MutableStateFlow("")
    private val _password = MutableStateFlow("")
    private val _repetirPassword = MutableStateFlow("")
    private val _errorMostrado = MutableStateFlow("")
    private val _cargando = MutableStateFlow(false)
    private val _registroSuccess = MutableStateFlow(false)

    // Estados públicos
    val nombre: StateFlow<String> = _nombre
    val email: StateFlow<String> = _email
    val password: StateFlow<String> = _password
    val repetirPassword: StateFlow<String> = _repetirPassword
    val errorMostrado: StateFlow<String> = _errorMostrado
    val cargando: StateFlow<Boolean> = _cargando
    val registroSuccess: StateFlow<Boolean> = _registroSuccess

    val error: StateFlow<String?> = authRepository.error

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

//    fun register(onSuccess: () -> Unit) {
//        var loginSuccess : Boolean
//        if (password.value != repetirPassword.value) {
//            _error.value = "Las contraseñas no coinciden"
//        } else {
//            viewModelScope.launch {
//                val result = authRepository.register(email.value, password.value)
//                if (result) {
//                    loginSuccess = authRepository.signIn(email.value, password.value)
//                    if (loginSuccess) {
//                        _error.value = ""
//                        onSuccess()
//                    } else {
//                        _error.value = "Error al iniciar sesión después del registro"
//                    }
//                } else {
//                    _error.value = authRepository.error.value
//                }
//            }
//        }
//    }
    fun register(onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                if (_email.value.isBlank() || _password.value.isBlank() || _nombre.value.isBlank()) {
                    _errorMostrado.value = "Por favor completa todos los campos"
                    return@launch
                }

                if (_password.value != _repetirPassword.value) {
                    _errorMostrado.value = "Las contraseñas no coinciden"
                    return@launch
                }
                if (!isEmailValid(_email.value)) {
                    _errorMostrado.value = "Correo electrónico inválido"
                    return@launch
                }

                _cargando.value = true
                _errorMostrado.value = ""

                val result = authRepository.register(_email.value, _password.value, _nombre.value)

                if (result) {
                    val loginSuccess = authRepository.signIn(_email.value, _password.value)
                    if (loginSuccess) {
                        _registroSuccess.value = true
                        onSuccess()
                    } else {
                        traducirErrorFirebase()
                    }
                } else {
                    traducirErrorFirebase()
                }
            } catch (e: Exception) {
                _errorMostrado.value = when (e) {
                    is FirebaseAuthUserCollisionException -> "El correo ya está registrado"
                    is FirebaseAuthWeakPasswordException -> "Contraseña demasiado débil"
                    is FirebaseNetworkException -> "Error de red"
                    else -> "Error al registrar: ${e.localizedMessage}"
                }
            } finally {
                _cargando.value = false
            }
        }
    }

    private fun isEmailValid(email: String): Boolean {
        val emailPattern = "^[A-Za-z0-9+_.-]+@(.+)$"
        return email.matches(Regex(emailPattern))
    }

    private fun traducirErrorFirebase() {
        val errorMessage = error.value
        _errorMostrado.value = when (errorMessage) {
            "The given password is invalid. [ Password should be at least 6 characters ]" -> "La contraseña debe tener al menos 6 caracteres."
            "The email address is badly formatted." -> "Correo electrónico inválido"
            "The password is invalid or the user does not have a password." -> "Cuenta o contraseña no válidas."
            "There is no user record corresponding to this identifier." -> "Cuenta no encontrada. ¿Quieres registrarte?"
            "The supplied auth credential is incorrect, malformed or has expired." -> "Cuenta o contraseña no válidas."
            "The user account has been disabled by an administrator." -> "Cuenta desactivada. Contacta al soporte."
            "Too many unsuccessful login attempts. Please try again later." -> "Demasiados intentos. Espera 5 minutos."
            "A network error has occurred." -> "Error de conexión."
            "An internal error has occurred." -> "Error interno. Intenta más tarde."
            "The email address is already in use by another account." -> "Ese correo ya está registrado."
            "A network error (such as timeout, interrupted connection or unreachable host) has occurred." -> "Error de conexión"
            else -> "Error al autenticar: ${errorMessage ?: "Desconocido"}"
        }
    }
}