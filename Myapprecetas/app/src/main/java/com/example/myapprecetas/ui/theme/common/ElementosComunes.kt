package com.example.myapprecetas.ui.theme.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.myapprecetas.R
import com.example.myapprecetas.objetos.dto.DTORecetaUsuarioLike
import com.example.myapprecetas.ui.theme.Colores
import com.example.myapprecetas.ui.theme.FamilyQuicksand
import com.example.myapprecetas.ui.theme.fuenteTexto

/**
 * Botón para volver atrás, de default hace popBackStack, pero se le puede indicar otras rutas por si se quiere recomponer la interfaz
 */
@Composable
fun BotonAtras(size: Dp, navController: NavHostController,  onClick: () -> Unit = { navController.popBackStack() }) {
    IconButton(onClick = onClick) {
        Icon(
            painter = painterResource(id = R.drawable.left),
            contentDescription = "Volver",
            tint = Colores.Negro,
            modifier = Modifier.size(size)
        )
    }
}

/**
 * Header con el botón para volver atrás, de default hace popBackStack, pero se le puede indicar otras rutas por si se quiere recomponer la interfaz
 */
@Composable
fun HeaderAtras(texto: String, navController: NavHostController, onClick: () -> Unit = { navController.popBackStack() }) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 40.dp, bottom = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        BotonAtras(24.dp, navController, onClick)
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

/**
 * Circulo de carga
 */
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

/**
 * Muestra el logo con un mensaje personalizado para mostrar al usuario que no hay recetas en ese listado
 */
@Composable
fun MensajeSinRecetas(
    texto: String,
    modifier: Modifier = Modifier,
    verticalArrangement: Arrangement.Vertical = Arrangement.Center,
    horizontalAlignment: Alignment.Horizontal = Alignment.CenterHorizontally
) {
    Column(
        modifier = modifier,
        verticalArrangement = verticalArrangement,
        horizontalAlignment = horizontalAlignment
    ) {
        Text(
            text = texto,
            fontSize = ConstanteTexto.TextoGrande,
            fontWeight = FontWeight.SemiBold,
            fontFamily = fuenteTexto,
            color = Colores.Gris.copy(alpha = 0.9f),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(20.dp))

        Icon(
            painter = painterResource(R.drawable.logochupchup),
            contentDescription = "icono chup chup",
            tint = Colores.Gris.copy(alpha = 0.9f),
            modifier = Modifier.size(200.dp)
        )
    }
}

//region listado de receta
/**
 * Componente para cada ítem de receta en la lista
 */
@Composable
fun ItemRecetaPrincipal(
    receta: DTORecetaUsuarioLike,
    navController: NavHostController,
    onToggleLike: (idReceta: Int) -> Unit
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
            // Imagen Receta con tiempo de preparación
            ImagenRecetaTimer(
                fotoReceta = receta.fotoReceta,
                tiempoPreparacion = receta.tiempoPreparacion
            )

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
                    style = MaterialTheme.typography.bodyMedium.copy(fontFamily = fuenteTexto),
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.weight(1f))

                RecetaFavoritoUsuario(
                    nombreUsuario = receta.nombreUsuario,
                    tieneLike = receta.tieneLike,
                    onToggleLike = { onToggleLike(receta.idReceta) }
                )
            }
        }
    }
}

/**
 * Componente para la imagen de la receta con el temporizador
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

        // Indicador de tiempo de preparación
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
 * Componente para el pie de cada receta, usuario y botón de like
 */
@Composable
fun RecetaFavoritoUsuario(
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
//endregion