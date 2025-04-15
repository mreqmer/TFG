package com.example.myapprecetas.repositories

import com.example.myapprecetas.vm.wapper.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@Singleton
class AuthRepository @Inject constructor(
    private val authWrapper: FirebaseAuth
) {
    private val _user = MutableStateFlow<FirebaseUser?>(null)
    val user: StateFlow<FirebaseUser?> = _user

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    suspend fun signIn(email: String, password: String): Boolean {
        return suspendCoroutine { continuation ->
            authWrapper.signIn(email, password) { success, errorMessage ->
                if (success) {
                    _user.value = authWrapper.getCurrentUser()
                    _error.value = null
                    continuation.resume(true)
                } else {
                    _error.value = errorMessage
                    continuation.resume(false)
                }
            }
        }
    }

    suspend fun signInWithGoogle(idToken: String): Boolean {
        return try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val authResult = authWrapper.signInWithGoogle(idToken)
            if (authResult != null) {
                _user.value = authResult
                _error.value = null
                true
            } else {
                _error.value = "Error al autenticar con Google"
                false
            }
        } catch (e: Exception) {
            _error.value = "Error: ${e.localizedMessage}"
            false
        }
    }

    fun getCurrentUser() = authWrapper.getCurrentUser()

}