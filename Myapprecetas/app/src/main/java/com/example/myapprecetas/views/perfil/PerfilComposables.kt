package com.example.myapprecetas.views.perfil

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
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

@Composable
fun TopBarPerfil(vm: VMPerfil, navController: NavHostController) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        HeaderTexto(navController)
        AccionesPerfil(vm, navController)
    }
}

@Composable
fun HeaderTexto(navController: NavHostController) {
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
}

@Composable
fun AccionesPerfil(vm: VMPerfil, navController: NavHostController) {
    Row {
        IconButton(onClick = { /* Editar perfil, puedes pasar función o dejar vacío */ }) {
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
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis

                    )
                }

                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = "Por ${receta.nombreUsuario}",
                    fontSize = ConstanteTexto.TextoMuyPequeno,
                    color = Color.Gray,
                    fontFamily = fuenteTexto,
                    modifier = Modifier.padding(bottom = 5.dp),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun PerfilInfo(
    nombreUsuario: String?,
    email: String?,
    imagenPerfil: String?,
    fechaRegistro: String?
) {
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
            if (!imagenPerfil.isNullOrEmpty()) {
                Image(
                    painter = rememberAsyncImagePainter(imagenPerfil),
                    contentDescription = "Imagen de perfil",
                    modifier = Modifier
                        .size(ConstanteIcono.IconoGrande * 2)
                        .clip(CircleShape)
                        .background(Colores.MarronClaro.copy(alpha = 0.3f))
                )
            } else {
                // Imagen por defecto si no hay URL
                Image(
                    painter = painterResource(id = R.drawable.fotoperfil),
                    contentDescription = "Imagen de perfil por defecto",
                    modifier = Modifier
                        .size(ConstanteIcono.IconoGrande * 2)
                        .clip(CircleShape)
                        .background(Colores.MarronClaro.copy(alpha = 0.3f))
                )
            }

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
                if (fechaRegistro != null) {
                    Text(
                        text = fechaRegistro,
                        fontSize = ConstanteTexto.TextoMuyPequeno,
                        fontFamily = FamilyQuicksand.quicksand,
                        color = Colores.Gris
                    )
                }
            }
        }
    }
}

@Composable
fun Divisor(texto: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp)
    ) {
        HorizontalDivider(
            modifier = Modifier
                .weight(1f)
                .height(1.dp),
            color = Colores.Gris.copy(alpha = 0.2f)
        )
        Text(
            text = texto,
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
}

@Composable
fun BotonCrearReceta(vm: VMPerfil, navController: NavHostController) {
    val idUsuario by vm.idUsuario.collectAsState()
    Card(
        onClick = {
            vm.clearIngredientes()
            idUsuario?.let {
                navController.navigate("creacionReceta/$it") {
                    popUpTo("creacionReceta") { inclusive = true }
                }
            }
        },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        colors = CardDefaults.cardColors(containerColor = Colores.VerdeOscuro.copy(alpha = 0.05f)),
        modifier = Modifier
            .fillMaxWidth()
            .height(68.dp)
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
}

@Composable
fun ListaRecetasPerfil(
    listaRecetas: List<DTORecetaSimplificada>,
    navController: NavHostController
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp, bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(
            items = listaRecetas,
            key = { it.idReceta.toString() }
        ) { receta ->
            ItemReceta(receta = receta, navController)
        }
    }
}