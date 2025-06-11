package com.example.myapprecetas.views.favoritas

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.myapprecetas.objetos.dto.DTORecetaUsuarioLike
import com.example.myapprecetas.ui.theme.common.ConstanteTexto
import com.example.myapprecetas.ui.theme.common.ItemRecetaPrincipal
import com.example.myapprecetas.ui.theme.fuenteTexto
import com.example.myapprecetas.vm.VMFavoritas

/**
 * Muestra la pantalla principal con la lista de recetas favoritas del usuario.
 */
@Composable
fun ListadoRecetas(
    listaReceta: List<DTORecetaUsuarioLike>,
    navController: NavHostController,
    insets: PaddingValues,
    textBienvenida: String,
    vm: VMFavoritas
) {
    // Muestra un mensaje si no hay recetas favoritas

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = insets.calculateBottomPadding() + 12.dp)
        ) {
            // Texto de bienvenida
            item {
                HeaderBienvenida(textBienvenida)
            }

            // Lista de recetas favoritas
            items(
                items = listaReceta,
                key = { receta -> receta.idReceta }
            ) { receta ->
                ItemRecetaPrincipal(
                    receta = receta,
                    navController = navController,
                    onToggleLike = { idReceta -> vm.toggleLike(idReceta) }
                )
                Spacer(modifier = Modifier.height(12.dp))
            }
    }
}

/**
 * Header de bienvenida
 */
@Composable
fun HeaderBienvenida(text: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 18.dp, bottom = 40.dp)
    ) {
        Text(
            text = text,
            fontSize = ConstanteTexto.TextoExtraGrande,
            fontFamily = fuenteTexto,
            fontWeight = FontWeight.ExtraBold,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

