package com.example.myapprecetas.views.listadoreceta

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.myapprecetas.R
import com.example.myapprecetas.objetos.dto.DTORecetaUsuarioLike
import com.example.myapprecetas.ui.theme.Colores
import com.example.myapprecetas.ui.theme.common.ConstanteIcono
import com.example.myapprecetas.ui.theme.common.ConstanteTexto
import com.example.myapprecetas.ui.theme.common.ItemRecetaPrincipal
import com.example.myapprecetas.ui.theme.common.MensajeSinRecetas
import com.example.myapprecetas.ui.theme.fuenteTexto
import com.example.myapprecetas.vm.VMListadoReceta

/**
 * Lista principal de recetas con buscador y mensaje si no hay resultados
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ListadoRecetas(
    listaReceta: List<DTORecetaUsuarioLike>,
    navController: NavHostController,
    insets: PaddingValues,
    textBienvenida: String,
    vm: VMListadoReceta,
    onAbrirDrawer: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = insets.calculateBottomPadding() + 12.dp)
    ) {
        // Texto de bienvenida
        item {
            HeaderBienvenida(textBienvenida)
        }

        // Buscador en la parte superior
        stickyHeader {
            Box(
                modifier = Modifier
                    .background(Colores.Blanco)
                    .padding(vertical = 8.dp)
            ) {
                FilaBuscador(vm = vm, onAbrirDrawer = onAbrirDrawer)
            }
        }

        // Lista de recetas si no hay elementos muestra una imagen
        if (listaReceta.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillParentMaxHeight()
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    MensajeSinRecetas(
                        texto = "¡Vaya, no hay recetas!",
                        modifier = Modifier
                            .fillMaxWidth()
                            .offset(y = (-60).dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    )
                }
            }
        } else {
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

/**
 * Mensaje de bienvenida
 */
@Composable
fun HeaderBienvenida(text: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 18.dp, bottom = 10.dp)
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

/**
 * Barra de búsqueda de recetas con botón para abrir el drawer de filtros
 */
@Composable
fun FilaBuscador(vm: VMListadoReceta, onAbrirDrawer: () -> Unit) {
    val searchQuery by vm.searchQuery.collectAsState()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Textfield del buscador
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { vm.onActualizaQuery(it) },
            placeholder = {
                Text(
                    "Buscar receta...",
                    fontFamily = fuenteTexto,
                    color = Color.Gray
                )
            },
            modifier = Modifier
                .weight(1f)
                .height(50.dp)
                .border(
                    width = 1.dp,
                    color = Colores.Gris.copy(alpha = 0.3f),
                    shape = MaterialTheme.shapes.medium
                ),
            shape = MaterialTheme.shapes.medium,
            maxLines = 1,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Search
            ),
            textStyle = TextStyle(
                color = Colores.Negro,
                fontFamily = fuenteTexto,
                fontSize = ConstanteTexto.TextoSemigrande
            ),
            keyboardActions = KeyboardActions(
                onSearch = {
                    vm.buscarRecetas()
                }
            ),
            trailingIcon = {
                Icon(
                    painter = painterResource(R.drawable.search),
                    contentDescription = "Buscar",
                    modifier = Modifier
                        .size(ConstanteIcono.IconoPequeno)
                        .clickable { vm.buscarRecetas() },
                    tint = Colores.Gris
                )
            }
        )

        Spacer(modifier = Modifier.width(10.dp))

        // Icono con funciones de filtrado, abre el drawer
        Icon(
            painter = painterResource(R.drawable.filtro2),
            contentDescription = "Filtrar",
            modifier = Modifier
                .size(ConstanteIcono.IconoGrande)
                .clip(RoundedCornerShape(12.dp))
                .background(Colores.Gris.copy(alpha = 0.1f))
                .padding(10.dp)
                .clickable { onAbrirDrawer() },
            tint = Colores.Gris
        )
    }
}
