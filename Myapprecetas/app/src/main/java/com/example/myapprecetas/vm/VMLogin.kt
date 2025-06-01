package com.example.myapprecetas.vm

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapprecetas.repositories.AuthRepository
import com.example.myapprecetas.userauth.AuthManager
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

    //region Atributos y propiedades
    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password

    private var _esPasswordVisible = MutableStateFlow(false)
    var esPasswordVisible: StateFlow<Boolean> = _esPasswordVisible

    private val _errorMostrado = MutableStateFlow("")
    val errorMostrado: StateFlow<String> = _errorMostrado

    private val _errorMostradoDialog = MutableStateFlow("")
    val errorMostradoDialog: StateFlow<String> = _errorMostradoDialog

    private val _cargando = MutableStateFlow(false)
    val cargando: StateFlow<Boolean> = _cargando

    private val _loginSuccess = MutableStateFlow(false)
    val loginSuccess: StateFlow<Boolean> = _loginSuccess

    val user: StateFlow<FirebaseUser?> = AuthManager.currentUser

    val error: StateFlow<String?> = authRepository.error
    //endregion


    //region constructor
    init {

    }
    //endregion
    //region Actualiza
    fun onLoginChanged(email: String, password: String) {
        _email.value = email
        _password.value = password
    }

    fun onPasswordVisibleChanged() {
        _esPasswordVisible.value = !_esPasswordVisible.value
    }
    fun resetErrorDialog(){
        _errorMostradoDialog.value = ""
    }

    fun limpiarEstadoResetPassword() {
        estadoResetPassword = null
    }
    //endregion

    //region Metodos
    fun signInWithGoogle(idToken: String) {
        viewModelScope.launch {
            try {
                _cargando.value = true
                val success = authRepository.signInWithGoogle(idToken)
                _loginSuccess.value = success
                if (!success) {
                    _errorMostrado.value = authRepository.error.value ?: "Error al autenticar con Google"
                }
            } catch (e: Exception) {
                _errorMostrado.value = "Error: ${e.localizedMessage}"
            } finally {
                _cargando.value = false
            }
        }
    }

    fun signInViewModel(email: String, password: String) {
        viewModelScope.launch {
            try {
                if (email.isBlank() || password.isBlank()) {
                    _errorMostrado.value = "Por favor completa todos los campos"
                    return@launch
                }

                _cargando.value = true
                _errorMostrado.value = ""

                val success = authRepository.signIn(email, password)

                if (!success) {
                    traducirErrorFirebase()
                }
            } catch (e: Exception) {
                _errorMostrado.value = when (e) {
                    is FirebaseAuthInvalidCredentialsException -> "Credenciales inválidas"
                    is FirebaseAuthInvalidUserException -> "Usuario no encontrado"
                    is FirebaseNetworkException -> "Error de red"
                    is FirebaseTooManyRequestsException -> "Demasiados intentos. Intenta más tarde"
                    else -> "Error al iniciar sesión: ${e.localizedMessage}"
                }
            } finally {
                _cargando.value = false
            }
        }
    }

    private fun traducirErrorFirebase() {
        val errorMessage = error.value
        _errorMostrado.value = when (errorMessage) {
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


    var estadoResetPassword by mutableStateOf<Result<Unit>?>(null)
        private set

    fun restablecerPassword(email: String) {
        viewModelScope.launch {
            val success = authRepository.sendPasswordReset(email)
            if (success) {

                _errorMostradoDialog.value = ""
                estadoResetPassword = Result.success(Unit)
            } else {

                _errorMostradoDialog.value = authRepository.error.value ?: "Error desconocido"
                estadoResetPassword = Result.failure(Exception(_errorMostradoDialog.value))
            }
        }
    }
    //endregion



}

