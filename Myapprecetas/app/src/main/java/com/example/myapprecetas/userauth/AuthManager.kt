package com.example.myapprecetas.userauth

import android.content.Context
import com.example.myapprecetas.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Gestor de autenticación de Firebase y Google Sign-In.
 * Maneja el inicio y cierre de sesión de usuarios.
 */
object AuthManager {

    //Firebase
    private val firebaseAuth = FirebaseAuth.getInstance()

    // Usuario actual
    private val _currentUser = MutableStateFlow(firebaseAuth.currentUser)
    val currentUser: StateFlow<FirebaseUser?> = _currentUser.asStateFlow()

    // Escucha los cambios en el estado de autenticación
    private val authListener = FirebaseAuth.AuthStateListener { auth ->
        _currentUser.value = auth.currentUser
    }

    init {
        // Inicializa el listener para cambios en el estado de autenticación
        firebaseAuth.addAuthStateListener(authListener)
    }

    /**
     * Cierra sesión y revoca el acceso de Google Sign-In.
     * @param context El contexto de la aplicación.
     * @param onComplete Función que se ejecuta al completar el proceso de cierre de sesión.
     */
    fun logoutWithRevokeAccess(context: Context, onComplete: () -> Unit) {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.string_server_client_id))
            .requestEmail()
            .build()

        val googleSignInClient = GoogleSignIn.getClient(context, gso)

        // Revoca el acceso y luego cierra sesión
        googleSignInClient.revokeAccess().addOnCompleteListener {
            firebaseAuth.signOut()
            onComplete()
        }
    }

}
