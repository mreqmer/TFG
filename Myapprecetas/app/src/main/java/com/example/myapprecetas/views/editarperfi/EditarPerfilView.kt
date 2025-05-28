package com.example.myapprecetas.views.editarperfi

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
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

@Composable
fun EditarPerfilView(
    vm: VMEditarPerfil,
    navController: NavHostController,
) {

    val nombreUsuario by vm.nombreUsuario.collectAsState()
    val imagenUri by vm.imagenUri.collectAsState()
    val editandoNombre by vm.editandoNombre.collectAsState()


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

        BotonGuardar {

            vm.guardarCambios()
        }

        Spacer(modifier = Modifier.height(12.dp))

        TextButton({navController.navigate("perfil")}) {
            Text("Cancelar", color = Color.Gray)
        }
    }
}
