package com.example.myapprecetas.views.perfil

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.myapprecetas.ui.theme.Colores
import com.example.myapprecetas.vm.VMPerfil


/**
 * Composable principal de la vista de perfil.
**/
@Composable
fun PerfilView(vm: VMPerfil, navController: NavHostController) {
    Scaffold(
        containerColor = Colores.Blanco,
        topBar = {
            TopBarPerfil(vm = vm, navController = navController)
        }
    ) { innerPadding ->
        PerfilScreen(
            vm = vm,
            navController = navController,
            modifier = Modifier.padding(innerPadding)
        )
    }
}

/**
 * Composable que representa el contenido de la pantalla de perfil.
**/
@Composable
fun PerfilScreen(
    vm: VMPerfil,
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val nombreUsuario by vm.nombreUsuario.collectAsState()
    val email by vm.email.collectAsState()
    val imagen by vm.imagenPerfil.collectAsState()
    val fechaRegistro by vm.fechaString.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(10.dp))
        // Muestra la información del perfil del usuario
        PerfilInfo(nombreUsuario, email, imagen, fechaRegistro)

        Spacer(modifier = Modifier.height(18.dp))
        // Título divisor para separar secciones
        Divisor(texto = "Mis Recetas")
        // Botón para crear una nueva receta
        BotonCrearReceta(vm = vm, navController = navController)
        // Lista de recetas creadas por el usuario
        ListaRecetasPerfil(vm = vm, navController = navController)
    }
}