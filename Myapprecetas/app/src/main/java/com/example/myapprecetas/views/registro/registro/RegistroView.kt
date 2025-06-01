package com.example.myapprecetas.views.registro.registro

import android.widget.Toast
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.myapprecetas.R
import com.example.myapprecetas.ui.theme.Colores
import com.example.myapprecetas.ui.theme.FamilyQuicksand
import com.example.myapprecetas.ui.theme.common.ConstanteTexto
import com.example.myapprecetas.ui.theme.common.HeaderAtras
import com.example.myapprecetas.vm.VMRegistro

/**
 * Vista principal del registro
 */
@Composable
fun RegistroView(vm: VMRegistro, navController: NavHostController) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .windowInsetsPadding(WindowInsets.navigationBars)
    ) {
        RegistroScreen(vm, navController)
    }
}

/**
 * Componentes de la pantalla de registro
 */
@Composable
fun RegistroScreen(vm: VMRegistro, navController: NavHostController) {
    val nombre by vm.nombre.collectAsState()
    val email by vm.email.collectAsState()
    val password by vm.password.collectAsState()
    val repetirPassword by vm.repetirPassword.collectAsState()
    val error by vm.errorMostrado.collectAsState()
    val cargando by vm.cargando.collectAsState(false)
    val scrollState = rememberScrollState()
    val keyboardController = LocalSoftwareKeyboardController.current

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Cabecera fija
            HeaderAtras("Registro", navController)

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 50.dp, bottom = 120.dp)
                    .verticalScroll(scrollState),
                verticalArrangement = Arrangement.spacedBy(15.dp)
            ) {
                // Mensaje de error
                MensajeError(error)

                // Campo de username
                CampoTextoRegistro(
                    value = nombre,
                    onTextChanged = { vm.onNombreChange(it) },
                    placeholder = "Nombre de usuario",
                    icono = R.drawable.usero,
                    tipo = KeyboardType.Text
                )

                //Campo de email
                CampoTextoRegistro(
                    value = email,
                    onTextChanged = { vm.onEmailChange(it) },
                    placeholder = "Correo electrónico",
                    icono = R.drawable.mailgordo,
                    tipo = KeyboardType.Email
                )

                //Campo de contraseña
                CampoTextoRegistro(
                    value = password,
                    onTextChanged = { vm.onPasswordChange(it) },
                    placeholder = "Contraseña",
                    icono = R.drawable.lock,
                    tipo = KeyboardType.Password,
                    visualTransformation = PasswordVisualTransformation()
                )
                //Campo repetir contraseña
                CampoTextoRegistro(
                    value = repetirPassword,
                    onTextChanged = { vm.onRepetirPasswordChange(it) },
                    placeholder = "Repetir contraseña",
                    icono = R.drawable.lock,
                    tipo = KeyboardType.Password,
                    imeAction = ImeAction.Done,
                    visualTransformation = PasswordVisualTransformation()
                )


                Spacer(modifier = Modifier.height(15.dp))
                TextoCuenta(navController)
            }
        }
        //Botón apra registrarse
        BotonRegistro(
            modifier = Modifier
                .imePadding()
                .align(Alignment.BottomCenter),
            onClick = {
                vm.register(
                    onSuccess = {
                        navController.navigate("lista_recetas")
                    }
                )
                keyboardController?.hide()
            },
            cargando = cargando
        )
    }
}


