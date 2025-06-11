package com.example.myapprecetas.views.editarperfi

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.myapprecetas.ui.theme.Colores
import com.example.myapprecetas.ui.theme.common.HeaderAtras
import com.example.myapprecetas.vm.VMEditarPerfil

/***
 * Vista para editar el usuario
 */
@Composable
fun EditarPerfilView(
    vm: VMEditarPerfil,
    navController: NavHostController,
) {
    Box {
        EditarPerfilScreen(vm, navController)
    }
}

/**
 * Componentes principales de la vista de editar perfil
 */
@Composable
fun EditarPerfilScreen(
    vm: VMEditarPerfil,
    navController: NavHostController,
) {

    val nombreUsuario by vm.nombreUsuario.collectAsState()
    val imagenUri by vm.imagenUri.collectAsState()
    val editandoNombre by vm.editandoNombre.collectAsState()
    val cargando by vm.guardandoCambios.collectAsState(false)
    val guardadoExitoso by vm.guardadoExitoso.collectAsState()
    val errorGuardado by vm.errorGuardado.collectAsState()



    val launcherImagen = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri -> vm.actualizarImagen(uri) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Colores.Blanco)
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        HeaderAtras("Editar perfil", navController) {
            navController.navigate("perfil") {
                launchSingleTop = true
                popUpTo("perfil") { inclusive = true }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        ImagenPerfil(imagenUri) { launcherImagen.launch("image/*") }

        Spacer(modifier = Modifier.height(16.dp))

        CampoNombre(
            nombreUsuario = nombreUsuario,
            editandoNombre = editandoNombre,
            onNombreChange = { vm.cambiarNombre(it) },
            onCancelarNombre = { vm.cancelarNombre() },
            onEditarNombre = { vm.toggleEditandoNombre() }
        )


        Spacer(modifier = Modifier.height(50.dp))

        BotonGuardar(
            navController = navController,
            cargando = cargando,
            guardadoExitoso = guardadoExitoso,
            errorGuardado = errorGuardado,
            onGuardarClick = { vm.guardarCambios() }
        )

        Spacer(modifier = Modifier.height(12.dp))

        TextButton(
            {navController.navigate("perfil")},
            enabled = !cargando,
            ) {
            Text("Cancelar", color = Color.Gray)
        }
    }
}


