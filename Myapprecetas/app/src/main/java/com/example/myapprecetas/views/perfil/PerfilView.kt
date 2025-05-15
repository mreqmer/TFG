package com.example.myapprecetas.views.perfil

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.myapprecetas.R
import com.example.myapprecetas.objetos.dto.DTORecetaSimplificada
import com.example.myapprecetas.ui.theme.Colores
import com.example.myapprecetas.ui.theme.FamilyQuicksand
import com.example.myapprecetas.ui.theme.common.BotonAtras
import com.example.myapprecetas.ui.theme.common.ConstanteIcono
import com.example.myapprecetas.ui.theme.common.ConstanteTexto
import com.example.myapprecetas.userauth.AuthManager
import com.example.myapprecetas.vm.VMPerfil

val fuenteTexto: FontFamily = FamilyQuicksand.quicksand

@Composable
fun PerfilView(vm: VMPerfil, navController: NavHostController) {

    val listaRecetas by vm.listaRecetas.collectAsState()
    val listaPrueba = if (listaRecetas.isNotEmpty()) List(10) { listaRecetas[0] } else emptyList()
    val nombreUsuario by vm.nombreUsuario.collectAsState()
    val email by vm.email.collectAsState()

    Scaffold(
        containerColor = Colores.Blanco,
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    BotonAtras(ConstanteIcono.IconoNormal, navController)

                    Text(
                        text = "Mi Perfil",
                        fontSize = ConstanteTexto.TextoGrande,
                        fontFamily = FamilyQuicksand.quicksand,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }

                Row {
                    IconButton(onClick = { /* Editar perfil */ }) {
                        Icon(
                            painter = painterResource(id = R.drawable.edit2),
                            contentDescription = "Editar perfil",
                            tint = Color.Black,
                            modifier = Modifier.size(ConstanteIcono.IconoPequeno)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    IconButton(onClick = {
                        AuthManager.logoutWithRevokeAccess(navController.context) {
                            navController.navigate("inicio") {
                                popUpTo("perfil") { inclusive = true }
                            }
                        }
                    }) {
                        Icon(
                            painter = painterResource(id = R.drawable.logout2),
                            contentDescription = "Cerrar sesión",
                            tint = Color.Black,
                            modifier = Modifier.size(ConstanteIcono.IconoPequeno)
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(10.dp))  // Reducido el espacio de arriba

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Colores.VerdeOscuro.copy(alpha = 0.05f))
                    .padding(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_launcher_background),
                        contentDescription = "Imagen de perfil",
                        modifier = Modifier
                            .size(ConstanteIcono.IconoGrande * 2)
                            .clip(CircleShape)
                            .background(Colores.MarronClaro.copy(alpha = 0.3f))
                    )

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        nombreUsuario?.let {
                            Text(
                                text = it,
                                fontSize = ConstanteTexto.TextoGrande,
                                fontFamily = FamilyQuicksand.quicksand
                            )
                        }
                        email?.let {
                            Text(
                                text = it,
                                fontSize = ConstanteTexto.TextoPequeno,
                                fontFamily = FamilyQuicksand.quicksand,
                                color = Colores.Gris
                            )
                        }
                        Text(
                            text = "Miembro desde: diciembre 2024",
                            fontSize = ConstanteTexto.TextoMuyPequeno,
                            fontFamily = FamilyQuicksand.quicksand,
                            color = Colores.Gris
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))  // Reducido el espacio antes de "Mis Recetas"

            // Divisor elegante con texto
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)  // Reducido el espacio vertical
            ) {
                HorizontalDivider(
                    modifier = Modifier
                        .weight(1f)
                        .height(1.dp),
                    color = Colores.Gris.copy(alpha = 0.2f)
                )
                Text(
                    text = "Mis Recetas",
                    modifier = Modifier.padding(horizontal = 12.dp),
                    fontSize = ConstanteTexto.TextoNormal,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = FamilyQuicksand.quicksand,
                    color = Colores.Gris
                )
                HorizontalDivider(
                    modifier = Modifier
                        .weight(1f)
                        .height(1.dp),
                    color = Colores.Gris.copy(alpha = 0.2f)
                )
            }

            // Carta "Crea una receta" ajustada
            Card(
                onClick = {
                    vm.clearIngredientes()
                    navController.navigate("creacionReceta") {
                        popUpTo("creacionReceta") { inclusive = true }
                    }
                },
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                colors = CardDefaults.cardColors(containerColor = Colores.VerdeOscuro.copy(alpha = 0.05f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(75.dp)
                    .border(
                        width = 1.dp,
                        color = Colores.Gris.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(16.dp)
                    )
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 20.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.mas2),
                        contentDescription = "Añadir receta",
                        tint = Colores.VerdeOscuro,
                        modifier = Modifier.size(ConstanteIcono.IconoNormal)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Crea una receta",
                        fontSize = ConstanteTexto.TextoNormal,
                        fontWeight = FontWeight.Medium,
                        fontFamily = FamilyQuicksand.quicksand,
                        color = Color.Black
                    )
                }

            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, bottom = 16.dp),  // Reducido el margen inferior
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(
                    items = listaRecetas,
                    key = { it.idReceta.toString() }
                ) { receta ->
                    ItemReceta(receta = receta, navController)
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }
}

@Composable
fun ItemReceta(receta: DTORecetaSimplificada, navController: NavHostController) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
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
                        model = receta.fotoReceta,
                        placeholder = painterResource(R.drawable.ic_launcher_background),
                        error = painterResource(R.drawable.ic_launcher_background)
                    ),
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
                Column {
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

                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = "Por ${receta.nombreUsuario}",
                    fontSize = ConstanteTexto.TextoMuyPequeno,
                    color = Color.Gray,
                    fontFamily = fuenteTexto,
                    modifier = Modifier.padding(bottom = 5.dp)
                )
            }
        }
    }
}

