package com.example.myapprecetas.repositories

import androidx.core.net.toUri
import com.example.myapprecetas.userauth.AuthManager
import com.example.myapprecetas.vm.wapper.FirebaseAuth
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
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

    /**
     * registro con email y contraseña
     * @param email correo del usuario que se registra
     * @param password contraseña del usuario que se registra
     * @param displayName nombre de usuario del usuario que se registra
     */
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

    /**
     * @param email correo para la recuperación de la contraseña
     * @return booleano de confirmación sobre si la operación fue existosa
     */
    suspend fun sendPasswordReset(email: String): Boolean {
        return try {
            authWrapper.sendPasswordResetEmail(email)
            _error.value = null
            true
        } catch (e: Exception) {
            // Aquí capturamos las excepciones Firebase comunes y ponemos un mensaje claro
            val mensaje = when (e) {

                is FirebaseAuthInvalidUserException -> "Este correo no está registrado"
                is FirebaseAuthInvalidCredentialsException -> "Correo electrónico no válido"
                is FirebaseNetworkException -> "Error de red. Verifica tu conexión"
                is FirebaseTooManyRequestsException -> "Demasiados intentos. Intenta más tarde"
                is IllegalArgumentException -> "El campo no puede estar vacío"
                else -> "Error desconocido"
            }
            setError(mensaje)
            false
        }
    }

    /**
     * @param displayName nuevo nombre de usuario del usuario actual
     * @return booleano de confirmación
     */
    suspend fun updateDisplayName(displayName: String): Boolean {
        val user = authWrapper.getCurrentUser()

        return if (user != null) {
            suspendCoroutine { continuation ->
                val profileUpdates = UserProfileChangeRequest.Builder()
                    .setDisplayName(displayName)
                    .build()

                user.updateProfile(profileUpdates)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            continuation.resume(true)
                        } else {
                            setError(task.exception?.localizedMessage ?: "Error al actualizar el nombre de usuario")
                            continuation.resume(false)
                        }
                    }
            }
        } else {
            setError("Usuario no autenticado")
            false
        }
    }
}
