package com.example.myapprecetas.views.detallesreceta

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.navigation.NavHostController
import com.example.myapprecetas.ui.theme.common.CargandoElementos
import com.example.myapprecetas.vm.VMDetallesReceta


@Composable
fun DetallesRecetaView(vm: VMDetallesReceta, navController: NavHostController ) {
    val receta by vm.recetaDetalles.collectAsState()
    val cargando by vm.cargando.collectAsState()
    val porciones by vm.porciones.collectAsState()

    if (cargando) {
        CargandoElementos()

    }else{
        Box {

            DetallesRecetaScreen(vm, receta, porciones, navController)
        }
    }


}

