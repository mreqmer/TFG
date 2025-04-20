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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.myapprecetas.R
import com.example.myapprecetas.dto.DTORecetaSimplificada
import com.example.myapprecetas.ui.theme.Colores
import com.example.myapprecetas.ui.theme.FamilyQuicksand
import com.example.myapprecetas.ui.theme.common.CargandoElementos
import com.example.myapprecetas.ui.theme.common.ConstanteIcono
import com.example.myapprecetas.ui.theme.common.ConstanteTexto
import com.example.myapprecetas.vm.VMListadoReceta
import kotlin.random.Random

val fuenteTexto: FontFamily = FamilyQuicksand.quicksand


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ListadoRecetaScreen(vm: VMListadoReceta, navController: NavHostController, insets: PaddingValues) {
    val listaReceta by vm.listaRecetas.collectAsState()
    val listaPrueba = if (listaReceta.isNotEmpty()) List(10) { listaReceta[0] } else emptyList()
    val nombreUsuario by vm.nombreUsuario.collectAsState()
    val textBienvenida = "¡Bienvenido, ${nombreUsuario?.substringBefore(" ")}!"
    val cargando = vm.cargando.collectAsState().value
    if(cargando){
        CargandoElementos()
    }else{
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
                items = listaPrueba,
                key = { "${Random.nextInt(1, 1001)}" } //TODO esto hay que cambiarlo
            ) { receta ->
                ItemReceta(receta = receta, navController)
                Spacer(modifier = Modifier.height(12.dp))
            }
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
            .clickable { navController.navigate("detalles_receta") }
            .padding(top = 12.dp, bottom = 2.dp)
            .padding(start = 12.dp, end = 12.dp)
            .height(130.dp)
    ) {
        Icon(
            painter = painterResource(R.drawable.heart),
            contentDescription = "favoritos",
            tint = Colores.RojoError,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(4.dp)
                .size(ConstanteIcono.IconoPequeno)
        )

        Row(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(RoundedCornerShape(8.dp))
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_launcher_background),
                    contentDescription = "Imagen de la receta",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                Box(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(4.dp)
                        .background(Color.White.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
                        .padding(horizontal = 4.dp, vertical = 2.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            painter = painterResource(R.drawable.temporizador2),
                            contentDescription = "Tiempo",
                            tint = Color.DarkGray,
                            modifier = Modifier.size(ConstanteIcono.IconoMuyPequeno)
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(
                            text = "30'",
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
                Column {
                    Text(
                        text = receta.nombreReceta,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontFamily = fuenteTexto
                        )
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = receta.descripcion,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontFamily = fuenteTexto
                        ),
                        maxLines = 3
                    )
                }

                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = "Por usuCreador",
                    fontSize = ConstanteTexto.TextoMuyPequeno,
                    color = Color.Gray,
                    fontFamily = fuenteTexto,
                    modifier = Modifier.padding(bottom = 5.dp)
                )
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

