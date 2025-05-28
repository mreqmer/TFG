package com.example.myapprecetas.views.perfil

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.myapprecetas.ui.theme.Colores
import com.example.myapprecetas.ui.theme.FamilyQuicksand
import com.example.myapprecetas.vm.VMPerfil

val fuenteTexto: FontFamily = FamilyQuicksand.quicksand

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

@Composable
fun PerfilScreen(
    vm: VMPerfil,
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val nombreUsuario by vm.nombreUsuario.collectAsState()
    val email by vm.email.collectAsState()
    val listaRecetas by vm.listaRecetas.collectAsState()
    val imagen by vm.imagenPerfil.collectAsState()
    val fechaRegistro by vm.fechaString.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(10.dp))

        PerfilInfo(nombreUsuario, email, imagen, fechaRegistro)

        Spacer(modifier = Modifier.height(18.dp))

        Divisor(texto = "Mis Recetas")

        BotonCrearReceta(vm = vm, navController = navController)

        ListaRecetasPerfil(vm = vm, navController = navController)
    }
}