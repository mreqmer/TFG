package com.example.myapprecetas.repositories

import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@Singleton
class AuthRepository @Inject constructor(
    private val authWrapper: com.example.myapprecetas.vm.wapper.FirebaseAuth
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

    fun getCurrentUser() = authWrapper.getCurrentUser()
}