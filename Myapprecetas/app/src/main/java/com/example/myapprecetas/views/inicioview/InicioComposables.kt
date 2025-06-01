package com.example.myapprecetas.views.inicioview

import androidx.compose.runtime.Composable
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.myapprecetas.ui.theme.Colores
import com.example.myapprecetas.ui.theme.FamilyQuicksand
import com.example.myapprecetas.R
import com.example.myapprecetas.ui.theme.common.ConstanteTexto

/**
 * Imagen de fondo
 */
@Composable
fun BackgroundImage() {
    Image(
        painter = painterResource(id = R.drawable.otraprueba),
        contentDescription = null,
        modifier = Modifier.fillMaxSize(),
        contentScale = ContentScale.Crop
    )
}


/**
 * Logo de la app
 */
@Composable
fun LogoInicio(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier, // Usa el modifier recibido
        contentAlignment = Alignment.Center
    ) {
        Icon(
            modifier = Modifier.size(300.dp),
            painter = painterResource(R.drawable.logochupchup),
            contentDescription = "Logo ChupChup",
            tint = Colores.Negro.copy(alpha = 0.7f)
        )
    }
}

/**
 * Pantalla de inicio
 */
@Composable
fun InicioScreen(navController: NavHostController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .systemBarsPadding(),
        verticalArrangement = Arrangement.Bottom
    ) {
       Botones(navController)
    }
}

/**
 * Botones de iniciar sesión y regsitrarse
 */
@Composable
fun Botones(navController: NavHostController) {
    Button(
        onClick = { navController.navigate("login") },
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .padding(bottom = 16.dp),
        shape = RoundedCornerShape(10.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Colores.VerdeOscuro,
            contentColor = Colores.Blanco
        )
    ) {
        Text(
            text = "Inicia Sesión",
            fontSize = ConstanteTexto.TextoNormal,
            fontFamily = FamilyQuicksand.quicksand,
            fontWeight = FontWeight.SemiBold
        )
    }

    Button(
        onClick = { navController.navigate("selector_registro") },
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .padding(bottom = 16.dp),
        shape = RoundedCornerShape(10.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Colores.MarronOscuro,
            contentColor = Colores.Blanco
        )
    ) {
        Text(
            text = "Registrarse",
            fontSize = ConstanteTexto.TextoNormal,
            fontFamily = FamilyQuicksand.quicksand,
            fontWeight = FontWeight.SemiBold
        )
    }
}
