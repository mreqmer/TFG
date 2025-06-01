package com.example.myapprecetas.views.favoritas

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
    if (listaReceta.isEmpty()) {
        MensajeSinRecetas()
    } else {
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
                ItemReceta(receta, navController, vm)
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

/**
 * Componente para el mensaje cuando no hay recetas favoritas
 */
@Composable
fun MensajeSinRecetas() {
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .widthIn(max = 250.dp)
                .padding(horizontal = 16.dp)
                .align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "No hay recetas favoritas. ¡Añade alguna!",
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

/**
 * Componente para cada ítem de receta en la lista
 */
@Composable
fun ItemReceta(
    receta: DTORecetaUsuarioLike,
    navController: NavHostController,
    vm: VMFavoritas
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

                // Pie de receta con nombre de usuario y botón de favoritos
                RecetaFavoritoUsuario(
                    nombreUsuario = receta.nombreUsuario,
                    tieneLike = receta.tieneLike,
                    onToggleLike = { vm.toggleLike(receta.idReceta) }
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
