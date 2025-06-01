package com.example.myapprecetas.views.listadoreceta

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.navigation.NavHostController
import com.example.myapprecetas.ui.theme.Colores
import com.example.myapprecetas.ui.theme.common.CargandoElementos
import com.example.myapprecetas.vm.VMListadoReceta
import eu.bambooapps.material3.pullrefresh.PullRefreshIndicator
import eu.bambooapps.material3.pullrefresh.PullRefreshIndicatorDefaults
import eu.bambooapps.material3.pullrefresh.pullRefresh
import eu.bambooapps.material3.pullrefresh.rememberPullRefreshState
import kotlinx.coroutines.launch

/**
 * Función principal que representa la vista del listado de recetas
 */
@Composable
fun ListadoRecetaView(vm: VMListadoReceta, navController: NavHostController) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(drawerState.currentValue) {
        if (drawerState.isClosed) {
            keyboardController?.hide()
        }
    }

    // Drawer de navegación lateral para los filtros
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            // Contenido del drawer (perfil, navegación, etc.)
            DrawerContent(
                vm = vm,
                insets = WindowInsets.systemBars
                    .only(WindowInsetsSides.Top + WindowInsetsSides.Bottom)
                    .asPaddingValues()
            )
        }
    ){
        // Contenedor principal de la pantalla
        Box(Modifier.fillMaxSize()) {
            // Llama a la función que construye la UI principal de la pantalla de recetas
            ListadoRecetaScreen(
                vm = vm,
                navController = navController,
                insets = WindowInsets.systemBars
                    .only(WindowInsetsSides.Top + WindowInsetsSides.Bottom)
                    .asPaddingValues(),
                onAbrirDrawer = { scope.launch { drawerState.open() } } // Abre el drawer
            )
        }
    }
}

/**
 * /Función que contiene la lógica principal y UI del listado de recetas
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListadoRecetaScreen(
    vm: VMListadoReceta,
    navController: NavHostController,
    insets: PaddingValues,
    onAbrirDrawer: () -> Unit
) {
    val listaReceta by vm.listaRecetas.collectAsState()
    val nombreUsuario by vm.nombreUsuario.collectAsState()
    val cargando by vm.cargando.collectAsState()
    val isRefreshing by vm.isRefreshing.collectAsState()

    val textBienvenida = "¡Saludos, ${nombreUsuario?.substringBefore(" ")}!"

    //Refresh, vuelve a cargar recetas
    val pullRefreshState = rememberPullRefreshState(
        refreshing = isRefreshing,
        onRefresh = {
            vm.buscarRecetas()
        }
    )

    // Contenedor principal, varia dependiendo de si está o no cargando
    Box(
        modifier = Modifier
            .fillMaxSize()
            .pullRefresh(pullRefreshState)
    ) {
        if (cargando) {
            CargandoElementos()
        } else {
            ListadoRecetas(
                listaReceta = listaReceta,
                navController = navController,
                insets = insets,
                textBienvenida = textBienvenida,
                vm = vm,
                onAbrirDrawer = onAbrirDrawer
            )
        }

        // Indicador de pull-to-refresh
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

