package com.example.myapprecetas.views.viewlogin

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.myapprecetas.ui.theme.common.HeaderAtras
import com.example.myapprecetas.vm.VMLogin

/*TODO GORDO
 El recuperar contraseña no va, pero no me quiero matar de momento
 */


@Composable
fun ViewLogin(vm: VMLogin, navController: NavHostController) {
    Box {
        LoginScreen(vm, navController)
    }
}

@Composable
fun LoginScreen(vm: VMLogin, navController: NavHostController) {
    val user by vm.user.collectAsState()
    val email by vm.email.collectAsState("")
    val password by vm.password.collectAsState("")
    val isPasswordVisible = vm.esPasswordVisible.collectAsState().value
    val errorMostrado by vm.errorMostrado.collectAsState(null)
    val cargando by vm.cargando.collectAsState(false)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Título con icono para volver atrás
//        IrAtrasInicioSesion(navController)
        HeaderAtras("Inicia Sesión", navController)

        // Contenido principal
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(top = 40.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Elementos de login con email
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                MensajeError(errorMostrado, cargando)
                EmailField(email) { vm.onLoginChanged(it, password) }
                Spacer(modifier = Modifier.height(20.dp))
                PasswordField(vm, password, { vm.onLoginChanged(email, it) }, isPasswordVisible)
                PasswordOlvidada(vm)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Otras opciones de login (google, etc)
            TextOtherOptions()

            LoginOtherOptions(vm, navController)
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp)
        ) {
            // Botón para hacer login
            BtnLogin(vm, email, password, user, cargando, navController)


        }
    }
}
