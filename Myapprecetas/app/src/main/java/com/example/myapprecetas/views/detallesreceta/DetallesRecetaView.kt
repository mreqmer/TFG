package com.example.myapprecetas.views.detallesreceta

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.example.myapprecetas.ui.theme.common.CargandoElementos
import com.example.myapprecetas.vm.VMDetallesReceta

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetallesRecetaView(vm: VMDetallesReceta, navController: NavHostController) {
    val receta by vm.recetaDetalles.collectAsState()
    val cargando by vm.cargando.collectAsState()

    if (cargando) {
        CargandoElementos()

    }else{
        Box {
            DetallesRecetaScreen(receta, navController)
        }
    }


}

