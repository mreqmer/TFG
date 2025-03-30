package com.example.myapprecetas.vm.wapper

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import javax.inject.Inject
import javax.inject.Singleton

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
    /**
     * Obtiene el usuario actualmente autenticado.
     */
    fun getCurrentUser(): FirebaseUser? = auth.currentUser

    /**
     * Cierra la sesión actual.
     */
    fun signOut() = auth.signOut()
}