package com.example.myapprecetas.views.inicioview

import androidx.compose.runtime.Composable
import com.example.myapprecetas.views.InicioScreen
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
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

        // Logo de la app
        LogoInicio()

        // Botones de registrarse e iniciar seson
        InicioScreen(navController)
    }
}
