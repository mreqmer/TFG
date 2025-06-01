package com.example.myapprecetas.views.recetacategoria

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
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
import com.example.myapprecetas.ui.theme.common.HeaderAtras
import com.example.myapprecetas.ui.theme.fuenteTexto
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
                    MensajeSinRecetasLista()
                }
            }
        }else{
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
 * muestra la tarjeta con los datos de una receta individual.
 */
@Composable
fun ItemReceta(receta: DTORecetaUsuarioLike, navController: NavHostController, vm: VMRecetaCategoria) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .border(1.dp, Colores.Gris.copy(alpha = 0.9f), RoundedCornerShape(12.dp))
            .background(Colores.Blanco)
            .clickable { navController.navigate("detalles_receta/${receta.idReceta}") }
            .padding(top = 12.dp, bottom = 2.dp)
            .padding(start = 12.dp, end = 12.dp)
            .height(130.dp)
    ) {
        Row(modifier = Modifier.fillMaxSize()) {
            //Imagen de la recetas con un temporizador
            ImagenTiempo(receta)

            Spacer(modifier = Modifier.width(10.dp))

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            ) {
                Text(
                    text = receta.nombreReceta,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = fuenteTexto,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = receta.descripcion,
                    fontSize = ConstanteTexto.TextoPequeno,
                    fontFamily = fuenteTexto,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.weight(1f))

                Box(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Por ${receta.nombreUsuario}",
                        fontSize = ConstanteTexto.TextoMuyPequeno,
                        color = Colores.Gris.copy(alpha = 0.9f),
                        fontFamily = fuenteTexto,
                        modifier = Modifier.align(Alignment.CenterStart),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    val iconoCorazon = if (receta.tieneLike) R.drawable.heart else R.drawable.corazonvacio2
//Icono favoritos
                    Icon(
                        painter = painterResource(iconoCorazon),
                        contentDescription = if (receta.tieneLike) "Quitar de favoritos" else "Añadir a favoritos",
                        tint = Colores.RojoError,
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .offset(y = (-3).dp)
                            .size(ConstanteIcono.IconoPequeno)
                            .clickable {
                                vm.toggleLike(receta.idReceta)
                            }
                    )
                }
            }
        }
    }
}


/**
 * Imagen de la receta con el tiempo de cocinarla
 *
 */
@Composable
private fun ImagenTiempo(receta: DTORecetaUsuarioLike) {
    Box(
        modifier = Modifier
            .size(120.dp)
            .clip(RoundedCornerShape(8.dp))
    ) {
        Image(
            painter = rememberAsyncImagePainter(
                model = receta.fotoReceta,
                placeholder = painterResource(R.drawable.ic_launcher_background),
                error = painterResource(R.drawable.ic_launcher_background)
            ),
            contentDescription = "Imagen de ${receta.nombreReceta}",
            contentScale = ContentScale.Crop,
            modifier = Modifier.size(120.dp)
        )

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
                    text = "${receta.tiempoPreparacion}'",
                    fontSize = 12.sp,
                    fontFamily = fuenteTexto,
                    fontWeight = FontWeight.Bold,
                    color = Colores.Negro
                )
            }
        }
    }
}

//Mensaje cuando no se encuentran recetas
@Composable
fun MensajeSinRecetasLista() {
    Column(
        modifier = Modifier.offset(y = (-60).dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "¡Vaya, no hay recetas con esa categoría!",
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
            tint = Colores.Gris.copy(alpha = 0.9f),
            modifier = Modifier.size(150.dp)
        )
    }
}