package com.example.myapprecetas.views.favoritas

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
import com.example.myapprecetas.vm.VMFavoritas
import eu.bambooapps.material3.pullrefresh.PullRefreshIndicator
import eu.bambooapps.material3.pullrefresh.PullRefreshIndicatorDefaults
import eu.bambooapps.material3.pullrefresh.pullRefresh
import eu.bambooapps.material3.pullrefresh.rememberPullRefreshState

/**
 * Función principal que representa la vista del las recetas favoritas
 */
@Composable
fun FavoritasView(vm: VMFavoritas, navController: NavHostController) {

    val insets = androidx.compose.foundation.layout.WindowInsets.systemBars
        .only(androidx.compose.foundation.layout.WindowInsetsSides.Top + androidx.compose.foundation.layout.WindowInsetsSides.Bottom)
        .asPaddingValues()

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        // Composable principal de la pantalla
        FavoritasScreen(vm, navController, insets)

    }
}

/**
 * Pantalla principal que muestra la lista de recetas favoritas,
 * junto con indicador de carga y pulltorefresh
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritasScreen(
    vm: VMFavoritas,
    navController: NavHostController,
    insets: PaddingValues
) {
    val textBienvenida = "Recetas Favoritas"
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
            // Muestra pantalla de carga
            CargandoElementos()
        } else if(listaReceta.isEmpty()){
            // Muestra mensaje si no hay recetas favoritas
            MensajeSinRecetas()
        }else{
            // Muestra la lista de recetas favoritas
            ListadoRecetas(
                listaReceta = listaReceta,
                navController = navController,
                insets = insets,
                textBienvenida = textBienvenida,
                vm = vm
            )
        }

        // Muestra el indicador de refresco en la parte superior
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

