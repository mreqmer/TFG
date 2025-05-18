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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.myapprecetas.R
import com.example.myapprecetas.objetos.dto.DTORecetaSimplificada
import com.example.myapprecetas.ui.theme.Colores
import com.example.myapprecetas.ui.theme.FamilyQuicksand
import com.example.myapprecetas.ui.theme.common.CargandoElementos
import com.example.myapprecetas.ui.theme.common.ConstanteIcono
import com.example.myapprecetas.ui.theme.common.ConstanteTexto
import com.example.myapprecetas.vm.VMListadoReceta
import eu.bambooapps.material3.pullrefresh.PullRefreshIndicator
import eu.bambooapps.material3.pullrefresh.PullRefreshIndicatorDefaults
import eu.bambooapps.material3.pullrefresh.pullRefresh
import eu.bambooapps.material3.pullrefresh.rememberPullRefreshState

val fuenteTexto: FontFamily = FamilyQuicksand.quicksand


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListadoRecetaScreen(
    vm: VMListadoReceta,
    navController: NavHostController,
    insets: PaddingValues
) {
    val listaReceta by vm.listaRecetas.collectAsState()
    val nombreUsuario by vm.nombreUsuario.collectAsState()
    val cargando by vm.cargando.collectAsState()
    val isRefreshing by vm.isRefreshing.collectAsState()

    val textBienvenida = "¡Bienvenido, ${nombreUsuario?.substringBefore(" ")}!"

    val pullRefreshState = rememberPullRefreshState(
        refreshing = isRefreshing,
        onRefresh = {
            vm.onRefresh()
        }
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pullRefresh(pullRefreshState)
    ) {
        if (cargando) {
            CargandoElementos()
        } else {
            ListadoRecetas(
                listaReceta = listaReceta,
                navController = navController,
                insets = insets,
                textBienvenida = textBienvenida,
                vm = vm
            )
        }

        PullRefreshIndicator(
            refreshing = isRefreshing,
            state = pullRefreshState,
            modifier = Modifier.align(Alignment.TopCenter),
            colors = PullRefreshIndicatorDefaults.colors(
                contentColor = Colores.RojoError
            )
        )
    }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ListadoRecetas(
    listaReceta: List<DTORecetaSimplificada>,
    navController: NavHostController,
    insets: PaddingValues,
    textBienvenida: String,
    vm: VMListadoReceta
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

        // Buscador (sticky)
        stickyHeader {
            Box(
                modifier = Modifier
                    .background(Color.White)
                    .padding(vertical = 8.dp)
            ) {
                SearchBar(vm)
            }
        }

        // Lista de recetas
        items(
            items = listaReceta,
            key = { receta -> receta.idReceta }
        ) { receta ->
            ItemReceta(receta = receta, navController)
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}


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

@Composable
fun ItemReceta(receta: DTORecetaSimplificada, navController: NavHostController) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .border(1.dp, Color.Gray, RoundedCornerShape(12.dp))
            .background(Color.White)
            .clickable {  navController.navigate("detalles_receta/${receta.idReceta}")}
            .padding(top = 12.dp, bottom = 2.dp)
            .padding(start = 12.dp, end = 12.dp)
            .height(130.dp)
    ) {
        Row(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(RoundedCornerShape(8.dp))
            ) {

                Image(
                    painter = rememberAsyncImagePainter(
                        model = receta.fotoReceta, // URL de la foto
                        placeholder = painterResource(R.drawable.ic_launcher_background),
                        error = painterResource(R.drawable.ic_launcher_background)
                    ),
                    contentDescription = "Imagen de ${receta.nombreReceta}",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(120.dp)
                )

                Box(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(4.dp)
                        .background(Color.White.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
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
                            fontSize = ConstanteTexto.TextoMuyPequeno,
                            fontFamily = fuenteTexto,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                ) {
                    Text(
                        text = receta.nombreReceta,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontFamily = fuenteTexto
                        ),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = receta.descripcion,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontFamily = fuenteTexto
                        ),
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 4.dp) // Ajustar el espacio inferior
                ) {
                    Text(
                        text = "Por ${receta.nombreUsuario}",
                        fontSize = ConstanteTexto.TextoMuyPequeno,
                        color = Color.Gray,
                        fontFamily = fuenteTexto,
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .padding(end = 48.dp),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Icon(
                        painter = painterResource(R.drawable.heart),
                        contentDescription = "favoritos",
                        tint = Colores.RojoError,
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .offset(y = (-3).dp)
                            .size(ConstanteIcono.IconoPequeno)
                    )
                }
            }
        }
    }
}



@Composable
fun SearchBar(vm: VMListadoReceta) {
    val search = ""

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = search,
            onValueChange = { },
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
            textStyle = TextStyle(color = Color.Black),
            trailingIcon = {
                Icon(
                    painter = painterResource(R.drawable.search),
                    contentDescription = "Buscar",
                    modifier = Modifier.size(ConstanteIcono.IconoPequeno),
                    tint = Colores.Gris
                )
            }
        )

        Spacer(modifier = Modifier.width(10.dp))

        Icon(
            painter = painterResource(R.drawable.filtro2),
            contentDescription = "Filtrar",
            modifier = Modifier
                .size(ConstanteIcono.IconoGrande)
                .clip(RoundedCornerShape(12.dp))
                .background(Colores.Gris.copy(alpha = 0.1f))
                .padding(10.dp),
            tint = Colores.Gris
        )
    }
}

