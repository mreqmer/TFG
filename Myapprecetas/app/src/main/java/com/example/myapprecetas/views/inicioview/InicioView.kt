package com.example.myapprecetas.views.inicioview

import androidx.compose.runtime.Composable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.myapprecetas.ui.theme.Colores

@Composable
fun InicioView(navController: NavHostController) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Colores.MarronClaro)
    ) {
        // Fondo
        BackgroundImage()

        LogoInicio(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(bottom = 100.dp)
        )

        // Botones de registrarse e iniciar sesión
        InicioScreen(navController)
    }
}