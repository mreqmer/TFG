package com.example.myapprecetas.views

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.myapprecetas.userauth.AuthManager

@Composable
fun PaginaEnConstrucciondosConBotonAtras(
    onBackClick: NavHostController
) {
    val user by AuthManager.currentUser.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("En construcción2", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(16.dp))

        user?.let {
            Text("Usuario: ${it.displayName ?: "Nombre no disponible"}")
            Text("Correo: ${it.email ?: "Correo no disponible"}")
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                AuthManager.logoutWithRevokeAccess(onBackClick.context) {
                    onBackClick.navigate("inicio") {
                        popUpTo("construccion") { inclusive = true }
                    }
                }
            }
        ) {
            Text("Cerrar sesión")
        }
    }
}
