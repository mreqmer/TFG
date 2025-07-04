package com.example.myapprecetas.views.recetacategoria

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.example.myapprecetas.ui.theme.Colores
import com.example.myapprecetas.ui.theme.common.CargandoElementos
import com.example.myapprecetas.vm.VMRecetaCategoria
import eu.bambooapps.material3.pullrefresh.PullRefreshIndicator
import eu.bambooapps.material3.pullrefresh.PullRefreshIndicatorDefaults
import eu.bambooapps.material3.pullrefresh.pullRefresh
import eu.bambooapps.material3.pullrefresh.rememberPullRefreshState

/**
 * Vista principal de la pantalla de recetas por categoría.
 */
@Composable
fun RecetaCategoriaView(vm: VMRecetaCategoria, navController: NavHostController) {

    val insets = androidx.compose.foundation.layout.WindowInsets.systemBars
        .only(androidx.compose.foundation.layout.WindowInsetsSides.Top + androidx.compose.foundation.layout.WindowInsetsSides.Bottom)
        .asPaddingValues()

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        RecetaCategoriaScreen(vm, navController, insets)

    }
}

/**
 * Muestra una lista de recetas o un loader dependiendo del estado del ViewModel.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecetaCategoriaScreen(
    vm: VMRecetaCategoria,
    navController: NavHostController,
    insets: PaddingValues
) {

    val isRefreshing by vm.isRefreshing.collectAsState()
    val listaReceta by vm.listaRecetas.collectAsState()
    val cargando by vm.cargando.collectAsState()

    val pullRefreshState = rememberPullRefreshState(
        refreshing = isRefreshing,
        onRefresh = {
            vm.onRefresh()
        }
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pullRefresh(pullRefreshState)
    ) {
        if (cargando) {
            //circulo de carga
            CargandoElementos()

        }else{
            //Listado de recetas
            ListadoRecetas(
                listaReceta = listaReceta,
                navController = navController,
                insets = insets,
                vm = vm
            )
        }

        //Recarga las recetas
        PullRefreshIndicator(
            refreshing = isRefreshing,
            state = pullRefreshState,
            modifier = Modifier.align(Alignment.TopCenter),
            colors = PullRefreshIndicatorDefaults.colors(
                contentColor = Colores.RojoError
            )
        )
    }
}