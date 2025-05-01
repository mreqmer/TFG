package com.example.myapprecetas.ui.theme.common

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.myapprecetas.R
import com.example.myapprecetas.ui.theme.Colores
import com.example.myapprecetas.ui.theme.FamilyQuicksand

@Composable
fun BotonAtras(size: Dp, navController: NavHostController) {
    IconButton(onClick = { navController.popBackStack() }) {
        Icon(
            painter = painterResource(id = R.drawable.left),
            contentDescription = "Volver",
            tint = Colores.Negro,
            modifier = Modifier.size(size)
        )
    }
}

@Composable
fun HeaderAtras(texto: String, navController: NavHostController) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 40.dp, bottom = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        BotonAtras(24.dp, navController)
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = texto,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.headlineMedium,
            color = Color.Black,
            fontFamily = FamilyQuicksand.quicksand
        )
    }
}

@Composable
fun CargandoElementos() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 50.dp),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            modifier = Modifier
                .size(80.dp)
                .padding(16.dp),
            color = Colores.RojoError,
            strokeWidth = 6.dp
        )
    }
}