package com.example.myapprecetas.views.recetacategoria

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.myapprecetas.objetos.dto.DTORecetaUsuarioLike
import com.example.myapprecetas.ui.theme.Colores
import com.example.myapprecetas.ui.theme.common.HeaderAtras
import com.example.myapprecetas.ui.theme.common.ItemRecetaPrincipal
import com.example.myapprecetas.ui.theme.common.MensajeSinRecetas
import com.example.myapprecetas.vm.VMRecetaCategoria

/**
 * Muestra una lista de recetas filtradas por categoría.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ListadoRecetas(
    listaReceta: List<DTORecetaUsuarioLike>,
    navController: NavHostController,
    insets: PaddingValues,
    vm: VMRecetaCategoria
) {
    val textoHeader  = "Filtrando por: \n ${vm.textoBienvenida()}"

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = insets.calculateBottomPadding() + 12.dp)
    ) {
        // Texto de bienvenida
        stickyHeader {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Colores.Blanco)
            ) {
                HeaderAtras(textoHeader, navController)
            }
        }

        // Lista de recetas, si no hay muestra mensaje

        if(listaReceta.isEmpty()){
            item {
                Box(
                    modifier = Modifier
                        .fillParentMaxHeight()
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    MensajeSinRecetas(
                        texto = "¡Vaya, no hay recetas con esa categoría!",
                        modifier = Modifier
                            .fillMaxWidth()
                            .offset(y = (-60).dp)
                    )
                }
            }
        }else{
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
}