package com.example.myapprecetas.views.listadoreceta

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.myapprecetas.R
import com.example.myapprecetas.objetos.dto.DTORecetaUsuarioLike
import com.example.myapprecetas.ui.theme.Colores
import com.example.myapprecetas.ui.theme.common.ConstanteIcono
import com.example.myapprecetas.ui.theme.common.ConstanteTexto
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
                    MensajeSinRecetasLista()
                }
            }
        } else {
            items(
                items = listaReceta,
                key = { receta -> receta.idReceta }
            ) { receta ->
                ItemReceta(receta, navController, vm)
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
 * Muestra un ítem de receta con su imagen, título, descripción y botón de like
 */
@Composable
fun ItemReceta(
    receta: DTORecetaUsuarioLike,
    navController: NavHostController,
    vm: VMListadoReceta
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .border(1.dp, Colores.Gris, RoundedCornerShape(12.dp))
            .background(Colores.Blanco)
            .clickable { navController.navigate("detalles_receta/${receta.idReceta}") }
            .padding(top = 12.dp, bottom = 2.dp)
            .padding(horizontal = 12.dp)
            .height(130.dp)
    ) {
        Row(modifier = Modifier.fillMaxSize()) {
            // Imagen con temporizador
            ImagenRecetaTimer(
                fotoReceta = receta.fotoReceta,
                tiempoPreparacion = receta.tiempoPreparacion
            )

            Spacer(modifier = Modifier.width(10.dp))

            // Contenido textual
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            ) {
                // Nombre de la receta
                Text(
                    text = receta.nombreReceta,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = fuenteTexto,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                // Descripción de la receta
                Text(
                    text = receta.descripcion,
                    fontSize = ConstanteTexto.TextoPequeno,
                    fontFamily = fuenteTexto,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.weight(1f))

                // Fila inferior con usuario y like
                RecetaLikeUsername(
                    nombreUsuario = receta.nombreUsuario,
                    tieneLike = receta.tieneLike,
                    onToggleLike = { vm.toggleLike(receta.idReceta) }
                )
            }
        }
    }
}

/**
 * Componente que muestra la imagen de la receta con el temporizador superpuesto
 */
@Composable
fun ImagenRecetaTimer(
    fotoReceta: String,
    tiempoPreparacion: Int
) {
    Box(
        modifier = Modifier
            .size(120.dp)
            .clip(RoundedCornerShape(8.dp))
    ) {
        Image(
            painter = rememberAsyncImagePainter(
                model = fotoReceta,
                placeholder = painterResource(R.drawable.ic_launcher_background),
                error = painterResource(R.drawable.ic_launcher_background)
            ),
            contentDescription = "Imagen de receta",
            contentScale = ContentScale.Crop,
            modifier = Modifier.size(120.dp)
        )

        // Tiempo de preparación en la esquina superior
        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(4.dp)
                .background(Colores.Blanco.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                .padding(horizontal = 4.dp, vertical = 2.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = painterResource(R.drawable.temporizador2),
                    contentDescription = "Tiempo",
                    modifier = Modifier.size(ConstanteIcono.IconoMuyPequeno)
                )
                Spacer(modifier = Modifier.width(2.dp))
                Text(
                    text = "${tiempoPreparacion}'",
                    fontSize = 12.sp,
                    fontFamily = fuenteTexto,
                    fontWeight = FontWeight.Bold,
                    color = Colores.Negro
                )
            }
        }
    }
}

/**
 * Componente que muestra el nombre de usuario y el botón de like
 */
@Composable
fun RecetaLikeUsername(
    nombreUsuario: String,
    tieneLike: Boolean,
    onToggleLike: () -> Unit
) {
    Box(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Por $nombreUsuario",
            fontSize = ConstanteTexto.TextoMuyPequeno,
            color = Colores.Gris,
            fontFamily = fuenteTexto,
            modifier = Modifier.align(Alignment.CenterStart),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        val iconoCorazon = if (tieneLike) R.drawable.heart else R.drawable.corazonvacio2

        Icon(
            painter = painterResource(iconoCorazon),
            contentDescription = if (tieneLike) "Quitar de favoritos" else "Añadir a favoritos",
            tint = Colores.RojoError,
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .offset(y = (-3).dp)
                .size(ConstanteIcono.IconoPequeno)
                .clickable { onToggleLike() }
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

/**
 * Muestra un mensaje cuando no hay recetas disponibles
 */
@Composable
fun MensajeSinRecetasLista() {
    Column(
        modifier = Modifier.offset(y = (-60).dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "¡Vaya, no hay recetas!",
            fontSize = ConstanteTexto.TextoGrande,
            fontWeight = FontWeight.SemiBold,
            fontFamily = fuenteTexto,
            color = Colores.Gris.copy(alpha = 0.9f),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(20.dp))

        Icon(
            painter = painterResource(R.drawable.potchupchup),
            contentDescription = "logo",
            tint =  Colores.Gris.copy(alpha = 0.9f),
            modifier = Modifier.size(150.dp)
        )
    }
}