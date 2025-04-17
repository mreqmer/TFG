package com.example.myapprecetas.views.listadoreceta

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.myapprecetas.vm.VMListadoReceta

@Composable
fun ListadoRecetaView(vm: VMListadoReceta, navController: NavHostController) {
    val cargando = vm.cargando.collectAsState().value
    val insets = androidx.compose.foundation.layout.WindowInsets.systemBars
        .only(androidx.compose.foundation.layout.WindowInsetsSides.Top + androidx.compose.foundation.layout.WindowInsetsSides.Bottom)
        .asPaddingValues()

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        if (cargando) {
            CircularProgressIndicator(
                modifier = Modifier
                    .height(50.dp)
                    .width(50.dp),
                color = Color.Red
            )
        } else {
            ListadoRecetaScreen(vm, navController, insets)
        }
    }
}
