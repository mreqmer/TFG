package com.example.myapprecetas.repositories

import androidx.core.net.toUri
import com.example.myapprecetas.userauth.AuthManager
import com.example.myapprecetas.vm.wapper.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
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
    // Usuario actual (lo gestiona AuthManager)
    val user: StateFlow<FirebaseUser?> = AuthManager.currentUser

    // Estado del error de login
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    /**
     * Mensaje de error.
     * @param message El mensaje a mostrar o guardar como error.
     */
    private fun setError(message: String?) {
        _error.value = message
    }

    /**
     * Inicia sesión con correo y contraseña usando FirebaseAuth.
     * @param email Correo del usuario.
     * @param password Contraseña del usuario.
     * @return true si la autenticación fue exitosa, false si falló.
     */
    suspend fun signIn(email: String, password: String): Boolean {
        return suspendCoroutine { continuation ->
            authWrapper.signIn(email, password) { success, errorMessage ->
                if (success) {
                    _error.value = null
                    continuation.resume(true)
                } else {
                    setError(errorMessage)
                    continuation.resume(false)
                }
            }
        }
    }

    /**
     * Inicia sesión con cuenta de Google usando un ID Token.
     * @param idToken Token proporcionado por Google Sign-In.
     * @return true si se autenticó correctamente, false si falló.
     */
    suspend fun signInWithGoogle(idToken: String): Boolean {
        return try {
            val authResult = authWrapper.signInWithGoogle(idToken)
            if (authResult != null) {
                _error.value = null
                true
            } else {
                setError("Error al autenticar con Google")
                false
            }
        } catch (e: Exception) {
            setError("Error: ${e.localizedMessage}")
            false
        }
    }

    suspend fun register(email: String, password: String, displayName: String): Boolean {
        return suspendCoroutine { continuation ->
            authWrapper.registerWithEmail(email, password, displayName) { success, errorMessage ->
                if (success) {
                    _error.value = null
                    continuation.resume(true)
                } else {
                    setError(errorMessage)
                    continuation.resume(false)
                }
            }
        }
    }

    /**
     * Actualiza la foto de perfil del usuario actual.
     * @param photoUrl La URL de la nueva foto de perfil.
     * @return true si la actualización fue exitosa, false en caso contrario.
     */
    suspend fun updateProfilePhoto(photoUrl: String): Boolean {
        val user = authWrapper.getCurrentUser()

        return if (user != null) {
            suspendCoroutine { continuation ->
                val profileUpdates = UserProfileChangeRequest.Builder()
                    .setPhotoUri(photoUrl.toUri())
                    .build()

                user.updateProfile(profileUpdates)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            continuation.resume(true)
                        } else {
                            setError(task.exception?.localizedMessage ?: "Error al actualizar la foto de perfil")
                            continuation.resume(false)
                        }
                    }
            }
        } else {
            setError("Usuario no autenticado")
            false
        }
    }

    suspend fun deleteCurrentUser(): Boolean {
        val user = authWrapper.getCurrentUser()

        return if (user != null) {
            suspendCoroutine { continuation ->
                user.delete()
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            continuation.resume(true)
                        } else {
                            setError(task.exception?.localizedMessage ?: "Error al eliminar el usuario")
                            continuation.resume(false)
                        }
                    }
            }
        } else {
            setError("No hay usuario autenticado para eliminar")
            false
        }
    }

//    fun getCurrentUser(): FirebaseUser? = authWrapper.getCurrentUser()
}
