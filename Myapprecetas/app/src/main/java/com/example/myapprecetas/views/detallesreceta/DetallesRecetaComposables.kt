package com.example.myapprecetas.views.detallesreceta

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.myapprecetas.R
import com.example.myapprecetas.objetos.dto.Categoria
import com.example.myapprecetas.objetos.dto.DTORecetaDetalladaLike
import com.example.myapprecetas.objetos.dto.Ingrediente
import com.example.myapprecetas.objetos.dto.Paso
import com.example.myapprecetas.ui.theme.Colores
import com.example.myapprecetas.ui.theme.FamilyQuicksand
import com.example.myapprecetas.ui.theme.common.ConstanteIcono
import com.example.myapprecetas.ui.theme.common.ConstanteTexto
import com.example.myapprecetas.ui.theme.common.BotonAtras
import com.example.myapprecetas.vm.VMDetallesReceta

val fuenteTexto = FamilyQuicksand.quicksand


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetallesRecetaScreen(
    vm: VMDetallesReceta,
    receta: DTORecetaDetalladaLike?,
    porciones: Int,
    navController: NavHostController,
) {

    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val state = scrollBehavior.state
    val initialImageHeight = 240.dp
    val minImageHeight = 0.dp
    val targetHeight = (initialImageHeight * (1 - state.collapsedFraction)).coerceAtLeast(minImageHeight)
    val imageHeight by animateDpAsState(targetValue = targetHeight, animationSpec = tween(300), label = "")

    receta?.let { data ->
//        Image(
//            painter = painterResource(id = R.drawable.ic_launcher_background),
//            contentDescription = "Imagen de la receta",
//            modifier = Modifier
//                .fillMaxWidth()
//                .height(imageHeight),
//            contentScale = ContentScale.Crop
//        )

        Image(
            painter = rememberAsyncImagePainter(
                model = receta.fotoReceta,
                placeholder = painterResource(R.drawable.ic_launcher_background),
                error = painterResource(R.drawable.ic_launcher_background)
            ),
            contentDescription = "Imagen de ${receta.nombreReceta}",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .height(imageHeight)
        )

        Scaffold(
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                LargeTopAppBar(
                    title = {},
                    scrollBehavior = scrollBehavior,
                    colors = TopAppBarDefaults.largeTopAppBarColors(
                        containerColor = Color.Transparent,
                        scrolledContainerColor = Color.Transparent
                    ),
                    navigationIcon = {
                        BotonAtras(ConstanteIcono.IconoNormal, navController) {
                            navController.navigate("lista_recetas") {
                                launchSingleTop = true
                                popUpTo("lista_recetas") { inclusive = true }
                            }
                        }
                    }
                )
            },
            containerColor = Color.Transparent
        ) { innerPadding ->
            DetallesRecetaContenido(vm, innerPadding,data,imageHeight, porciones, navController
            )
        }
    } ?: run {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No se ha podido cargar la receta.", fontFamily = fuenteTexto, fontSize = 16.sp)
        }
    }
}

@Composable
fun DetallesRecetaContenido(
    vm: VMDetallesReceta,
    innerPadding: PaddingValues,
    data: DTORecetaDetalladaLike,
    imageHeight: Dp,
    porciones: Int,
    navController: NavHostController
) {
    LazyColumn(
        modifier = Modifier
            .padding(innerPadding)
            .fillMaxSize()
            .background(Color.Transparent),
        contentPadding = PaddingValues(top = imageHeight / 2.5f)
    ) {
        item { CabeceraReceta(data) { vm.toggleLike() } }
        item { SelectorRaciones(porciones, { vm.AumentaPorciones() }, {vm.DisminuyePorciones()}) }
        item { TituloSeccion("Ingredientes") }
        items(data.ingredientes) { IngredienteItem(it) }
        item { TituloSeccion("Pasos") }
        items(data.pasos) { PasoItem(it) }
        item { CategoriaChips(data.categorias, navController) }
    }
}

@Composable
fun CabeceraReceta(data: DTORecetaDetalladaLike, onToggleLike: () -> Unit) {
    Column(Modifier.padding(horizontal = 16.dp)) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min) // para que el box tenga altura mínima según contenido
        ) {
            Text(
                data.nombreReceta,
                fontFamily = fuenteTexto,
                fontSize = ConstanteTexto.TextoGrande,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(end = 30.dp) // alineado a la izquierda verticalmente centrado
            )
            Icon(
                painter = painterResource(
                    if (data.tieneLike) R.drawable.heart else R.drawable.corazonvacio2
                ),
                tint = Colores.RojoError,
                contentDescription = if (data.tieneLike) "Favorito" else "No favorito",
                modifier = Modifier
                    .size(ConstanteIcono.IconoNormal)
                    .align(Alignment.TopEnd)
                    .offset(y = 6.dp)
                    .clickable { onToggleLike() }
            )
        }
        Spacer(Modifier.height(4.dp))
        Text(
            "Por ${data.nombreUsuario}",
            fontFamily = fuenteTexto,
            fontSize = ConstanteTexto.TextoPequeno,
            fontWeight = FontWeight.SemiBold,
            color = Colores.Gris
        )
        Spacer(Modifier.height(12.dp))
        Text(
            data.descripcion,
            fontFamily = fuenteTexto,
            fontSize = ConstanteTexto.TextoNormal,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(Modifier.height(20.dp))

        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconoTexto(R.drawable.temporizador, "${data.tiempoPreparacion} min")
            Spacer(Modifier.width(32.dp))
            IconoTexto(R.drawable.dificultad, data.dificultad)
        }

        Spacer(Modifier.height(24.dp))
    }
}

@Composable
fun IconoTexto(iconRes: Int, text: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(
            painter = painterResource(iconRes),
            contentDescription = null,
            modifier = Modifier.size(ConstanteIcono.IconoNormal)
        )
        Text(
            text,
            fontFamily = fuenteTexto,
            fontSize = ConstanteTexto.TextoPequeno,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun SelectorRaciones(
    porciones: Int,
    IncrementaPorciones: () -> Unit,
    DisminuyePorciones: () -> Unit
) {
    Column(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .clip(RoundedCornerShape(50))
                .background(Colores.VerdeOscuro.copy(alpha = 0.5f))
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            //TODO Cambiar por iconos el + y el -
            Text(
                "-",
                fontFamily = fuenteTexto,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier =
                Modifier
                    .padding(horizontal = 10.dp)
                    .clickable {
                        DisminuyePorciones()
                    }
            )
            Text(
                text = porciones.toString(),
                fontFamily = fuenteTexto,
                fontSize = ConstanteTexto.TextoNormal,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                " Porciones ",
                fontFamily = fuenteTexto,
                fontSize = ConstanteTexto.TextoNormal,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                "+",

                fontFamily = fuenteTexto,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier =
                    Modifier
                        .padding(horizontal = 10.dp)
                        .clickable {
                            IncrementaPorciones()
                        }

            )
        }
        Spacer(Modifier.height(16.dp))
    }
}

@Composable
fun IngredienteItem(ingrediente: Ingrediente) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp, vertical = 4.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Colores.MarronClaro.copy(alpha = 0.5f))
            .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                ingrediente.nombreIngrediente,
                fontFamily = fuenteTexto,
                fontSize = ConstanteTexto.TextoNormal
            )
            Text(
                "${ingrediente.cantidad} ${ingrediente.medida}",
                fontFamily = fuenteTexto,
                fontSize = ConstanteTexto.TextoPequeno,
                fontWeight = FontWeight.SemiBold
            )
        }

        if (ingrediente.notas.isNotBlank()) {
            Text(
                ingrediente.notas,
                fontFamily = fuenteTexto,
                fontSize = ConstanteTexto.TextoMuyPequeno,
                color = Colores.Gris,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

@Composable
fun PasoItem(paso: Paso) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp, vertical = 4.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Colores.VerdeClaro.copy(alpha = 0.6f))
            .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        Text(
            "Paso ${paso.orden}",
            fontFamily = fuenteTexto,
            fontSize = ConstanteTexto.TextoNormal,
            fontWeight = FontWeight.Bold,
            color = Colores.Negro
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            paso.descripcion,
            fontFamily = fuenteTexto,
            fontSize = ConstanteTexto.TextoNormal
        )
    }
}

@Composable
fun TituloSeccion(titulo: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(24.dp))
        Text(
            titulo,
            fontFamily = fuenteTexto,
            fontSize = ConstanteTexto.TextoSemigrande,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(12.dp))
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CategoriaChips(categorias: List<Categoria>, navController: NavHostController) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TituloSeccion("Categorías")
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)) {
            categorias.forEach {
                AssistChip(
                    onClick = {navController.navigate("receta_categoria/${it.nombreCategoria}")},
                    label = {
                        Text(
                            it.nombreCategoria,
                            fontFamily = fuenteTexto,
                            fontSize = 13.sp
                        )
                    },
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
            }
        }
        Spacer(Modifier.height(24.dp))
    }
}
