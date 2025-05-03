package com.example.myapprecetas.vm.wapper

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.userProfileChangeRequest
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@Singleton
class FirebaseAuth @Inject constructor(
    private val auth: FirebaseAuth // Inyectado por Hilt
) {
    /**
     * Inicia sesión con email y contraseña.
     * @param email Correo electrónico
     * @param password Contraseña
     * @param onResult Callback que devuelve (éxito, mensajeError)
     */
    fun signIn(
        email: String,
        password: String,
        onResult: (Boolean, String?) -> Unit
    ) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onResult(true, null)
                } else {
                    onResult(false, task.exception?.message ?: "Error desconocido")
                }
            }
    }
    suspend fun signInWithGoogle(idToken: String): FirebaseUser? {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        return try {
            auth.signInWithCredential(credential).await().user
        } catch (e: Exception) {
            null
        }
    }

    fun registerWithEmail(
        email: String,
        password: String,
        displayName: String,
        onResult: (Boolean, String?) -> Unit
    ) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    val profileUpdates = userProfileChangeRequest {
                        this.displayName = displayName
                    }
                    user?.updateProfile(profileUpdates)?.addOnCompleteListener { updateTask ->
                        if (updateTask.isSuccessful) {
                            onResult(true, null)
                        } else {
                            onResult(false, updateTask.exception?.message ?: "Error al actualizar el perfil")
                        }
                    }
                } else {
                    onResult(false, task.exception?.message ?: "Error al registrar")
                }
            }
    }



//    fun register(
//        email: String,
//        password: String,
//        onResult: (Boolean, String?) -> Unit
//    ) {
//        auth.createUserWithEmailAndPassword(email, password)
//            .addOnCompleteListener { task ->
//                if (task.isSuccessful) {
//                    onResult(true, null)
//                } else {
//                    onResult(false, task.exception?.message ?: "Error desconocido")
//                }
//            }
//    }

    /**
     * Obtiene el usuario actualmente autenticado.
     */
    fun getCurrentUser(): FirebaseUser? = auth.currentUser

    /**
     * Cierra la sesión actual.
     */
    fun signOut() = auth.signOut()
}