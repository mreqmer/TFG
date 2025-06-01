package com.example.myapprecetas.views.detallesreceta

import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.myapprecetas.objetos.dto.DTORecetaDetalladaLike
import com.example.myapprecetas.ui.theme.common.BotonAtras
import com.example.myapprecetas.ui.theme.common.CargandoElementos
import com.example.myapprecetas.ui.theme.common.ConstanteIcono
import com.example.myapprecetas.ui.theme.fuenteTexto
import com.example.myapprecetas.vm.VMDetallesReceta


/**
 * Vista principal que gestiona la carga y muestra de los detalles de una receta.
 */
@Composable
fun DetallesRecetaView(vm: VMDetallesReceta, navController: NavHostController ) {
    val receta by vm.recetaDetalles.collectAsState()
    val cargando by vm.cargando.collectAsState()
    val porciones by vm.porciones.collectAsState()

    //Controla el botón atrás del sistema
    BackHandler(enabled = true) {
        navController.navigate("lista_recetas") {
            launchSingleTop = true
            popUpTo("lista_recetas") { inclusive = true }
        }
    }
    if (cargando) {
        CargandoElementos()

    }else{
        Box {

            DetallesRecetaScreen(vm, receta, porciones, navController)
        }
    }
}

/**
 * Pantalla con Scaffold que contiene imagen superior colapsable y contenido de la receta.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetallesRecetaScreen(
    vm: VMDetallesReceta,
    receta: DTORecetaDetalladaLike?,
    porciones: Int,
    navController: NavHostController,
) {

    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val state = scrollBehavior.state
    val initialImageHeight = 240.dp
    val minImageHeight = 0.dp
    val targetHeight = (initialImageHeight * (1 - state.collapsedFraction)).coerceAtLeast(minImageHeight)
    val imageHeight by animateDpAsState(targetValue = targetHeight, animationSpec = tween(300), label = "")

    receta?.let { data ->
        // Imagen superior de la receta con animación
        ImagenReceta(receta, imageHeight)

        Scaffold(
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                LargeTopAppBar(
                    title = {},
                    scrollBehavior = scrollBehavior,
                    colors = TopAppBarDefaults.largeTopAppBarColors(
                        containerColor = Color.Transparent,
                        scrolledContainerColor = Color.Transparent
                    ),
                    navigationIcon = {
                        // Botón para volver a la lista de recetas
                        BotonAtras(ConstanteIcono.IconoNormal, navController) {
                            navController.popBackStack()
                            navController.navigate("lista_recetas") {
                                launchSingleTop = true
                                popUpTo("lista_recetas") { inclusive = true }
                            }
                        }
                    }
                )
            },
            containerColor = Color.Transparent
        ) { innerPadding ->
            DetallesRecetaContenido(vm, innerPadding,data,imageHeight, porciones, navController
            )
        }
    } ?: run {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No se ha podido cargar la receta.", fontFamily = fuenteTexto, fontSize = 16.sp)
        }
    }
}

