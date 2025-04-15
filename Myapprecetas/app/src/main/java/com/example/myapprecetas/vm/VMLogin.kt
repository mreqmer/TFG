package com.example.myapprecetas.vm

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapprecetas.repositories.AuthRepository
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VMLogin @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    // Estados
    private val email = MutableStateFlow("")
    val Email: StateFlow<String> = email

    private val password = MutableStateFlow("")
    val Password: StateFlow<String> = password

    private var esPasswordVisible = MutableStateFlow(false)
    var EsPasswordVisible: StateFlow<Boolean> = esPasswordVisible

    private val errorMostrado = MutableStateFlow("")
    val ErrorMostrado: StateFlow<String> = errorMostrado

    private val cargando = MutableStateFlow(false)
    val Cargando: StateFlow<Boolean> = cargando

    // 🔥 Usuario actual observado desde AuthManager
    val user: StateFlow<FirebaseUser?> = AuthManager.currentUser

    // 🔥 Acceso al estado del login
    val isLoggedIn: Boolean
        get() = AuthManager.isLoggedIn()

    val error: StateFlow<String?> = authRepository.error

    private val LoginSuccess = MutableStateFlow(false)
    val loginSuccess: StateFlow<Boolean> = LoginSuccess

    fun onLoginChanged(email: String, password: String) {
        this.email.value = email
        this.password.value = password
    }

    fun togglePasswordVisibility() {
        esPasswordVisible.value = !esPasswordVisible.value
    }

    fun OnLoginChanged(email: String, password: String) {
        this.email.value = email
        this.password.value = password
    }

    fun OnPasswordVisibleChanged() {
        esPasswordVisible.value = !esPasswordVisible.value
    }

    fun signInWithGoogle(idToken: String) {
        viewModelScope.launch {
            try {
                cargando.value = true
                val success = authRepository.signInWithGoogle(idToken)
                LoginSuccess.value = success
                if (!success) {
                    errorMostrado.value = authRepository.error.value ?: "Error al autenticar con Google"
                }
            } catch (e: Exception) {
                errorMostrado.value = "Error: ${e.localizedMessage}"
            } finally {
                cargando.value = false
            }
        }
    }

    fun signInViewModel(email: String, password: String) {
        viewModelScope.launch {
            try {
                if (email.isBlank() || password.isBlank()) {
                    errorMostrado.value = "Por favor completa todos los campos"
                    return@launch
                }

                cargando.value = true
                errorMostrado.value = ""

                val success = authRepository.signIn(email, password)

                if (!success) {
                    traducirErrorFirebase()
                }
            } catch (e: Exception) {
                errorMostrado.value = when (e) {
                    is FirebaseAuthInvalidCredentialsException -> "Credenciales inválidas"
                    is FirebaseAuthInvalidUserException -> "Usuario no encontrado"
                    is FirebaseNetworkException -> "Error de red"
                    is FirebaseTooManyRequestsException -> "Demasiados intentos. Intenta más tarde"
                    else -> "Error al iniciar sesión: ${e.localizedMessage}"
                }
            } finally {
                cargando.value = false
            }
        }
    }


    private fun traducirErrorFirebase() {
        val errorMessage = error.value
        errorMostrado.value = when (errorMessage) {
            "The email address is badly formatted." -> "Correo electrónico inválido"
            "The password is invalid or the user does not have a password." -> "Cuenta o contraseña no válidas."
            "There is no user record corresponding to this identifier." -> "Cuenta no encontrada. ¿Quieres registrarte?"
            "The supplied auth credential is incorrect, malformed or has expired." -> "Cuenta o contraseña no válidas."
            "The user account has been disabled by an administrator." -> "Cuenta desactivada. Contacta al soporte."
            "Too many unsuccessful login attempts. Please try again later." -> "Demasiados intentos. Espera 5 minutos."
            "A network error has occurred." -> "Error de conexión."
            "An internal error has occurred." -> "Error interno. Intenta más tarde."
            else -> "Error al autenticar: ${errorMessage ?: "Desconocido"}"
        }
    }
}
