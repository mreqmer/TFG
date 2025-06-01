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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.example.myapprecetas.ui.theme.common.ConstanteIcono
import com.example.myapprecetas.ui.theme.common.ConstanteTexto
import com.example.myapprecetas.ui.theme.fuenteTexto
import com.example.myapprecetas.userauth.AuthManager
import com.example.myapprecetas.vm.VMPerfil

/**
 * Barra superior del perfil del usuario que muestra el título y las acciones disponibles,
 * como editar perfil o cerrar sesión.
 */
@Composable
fun TopBarPerfil(vm: VMPerfil, navController: NavHostController) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        HeaderTexto()
        AccionesPerfil( navController)
    }
}

/**
 * Título principal de la pantalla de perfil.
 */
@Composable
fun HeaderTexto() {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = "Mi Perfil",
            fontSize = ConstanteTexto.TextoGrande,
            fontFamily = FamilyQuicksand.quicksand,
            fontWeight = FontWeight.Bold,
            color = Colores.Negro,
            modifier = Modifier.padding(start = 4.dp)
        )
    }
}

/**
 * Contiene los botones de acción del perfil
 */
@Composable
fun AccionesPerfil( navController: NavHostController) {
    Row {
        //Editar perfil
        IconButton(onClick = { navController.navigate("editar-perfil") }) {
            Icon(
                painter = painterResource(id = R.drawable.edit2),
                contentDescription = "Editar perfil",
                tint = Colores.Negro,
                modifier = Modifier.size(ConstanteIcono.IconoPequeno)
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        //Cerrar sesión
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
                tint = Colores.Negro,
                modifier = Modifier.size(ConstanteIcono.IconoPequeno)
            )
        }
    }
}

/**
 * Muestra un ítem individual de receta con imagen, título, descripción y opción de eliminar
 */
@Composable
fun ItemReceta(receta: DTORecetaSimplificada, navController: NavHostController, onConfirmar: (Int) -> Unit ,) {
    var showDialog by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp,Colores.Gris.copy(alpha = 0.9f), RoundedCornerShape(12.dp))
            .background(Colores.Blanco)
            .clickable { navController.navigate("detalles_receta/${receta.idReceta}") }
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
                // Imagen de la receta
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
                // Etiqueta de tiempo
                Box(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(4.dp)
                        .background(Colores.Blanco.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
                        .padding(horizontal = 4.dp, vertical = 2.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            painter = painterResource(R.drawable.temporizador2),
                            contentDescription = "Tiempo",
                            tint = Colores.Negro,
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
            // Información de la receta
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            ) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = receta.nombreReceta,
                        fontWeight = FontWeight.Bold,
                        fontFamily = fuenteTexto,
                        fontSize = ConstanteTexto.TextoNormal,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )

                    //Borrar receta
                    IconButton(onClick = { showDialog = true }) {
                        Icon(
                            modifier = Modifier.size(ConstanteIcono.IconoPequeno),
                            painter = painterResource(R.drawable.papelera),
                            contentDescription = "Eliminar receta",
                            tint = Colores.RojoError
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = receta.descripcion,
                    fontWeight = FontWeight.Light,
                    fontFamily = fuenteTexto,
                    fontSize = ConstanteTexto.TextoPequeno,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.weight(1f))

                Text(
                    text = "Por ${receta.nombreUsuario}",
                    fontSize = ConstanteTexto.TextoMuyPequeno,
                    color = Colores.Gris,
                    fontFamily = fuenteTexto,
                    modifier = Modifier.padding(bottom = 5.dp),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
        // Diálogo de confirmación para eliminar receta
        if (showDialog) {
            ConfirmarBorradoDialog(
                onConfirmar = {
                    onConfirmar(receta.idReceta)
                    showDialog = false
                },
                    onCancelar = {
                        showDialog = false
                    })
        }
    }
}

/**
 * Muestra la información del usuario en el perfil: nombre, email, fecha y foto.
 */
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
            //Imagen del usuario
            ImagenPerfil(imagenPerfil)

            Spacer(modifier = Modifier.width(16.dp))

            //otro datos, nombre, correo, etc
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

/**
 * Imagen de perfil del usuario
 */
@Composable
private fun ImagenPerfil(imagenPerfil: String?) {
    if (!imagenPerfil.isNullOrEmpty()) {
        Image(
            painter = rememberAsyncImagePainter(imagenPerfil),
            contentDescription = "Imagen de perfil",
            modifier = Modifier
                .size(84.dp)
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
}

/**
 * Divisor horizontal con texto en el centro
 */
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

/**
 * Botón para acceder a la creación de una nueva receta desde el perfil.
 */
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
                color = Colores.Negro
            )
        }
    }
}

/**
 * Lista de las recetas creadas por el usuario, si las hay
 */
@Composable
fun ListaRecetasPerfil(
    vm: VMPerfil,
    navController: NavHostController,
) {
    val listaRecetas by vm.listaRecetas.collectAsState()
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp, bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(
            items = listaRecetas,
            key = { receta -> receta.idReceta }
        ) { receta ->
            ItemReceta(receta = receta, navController,  onConfirmar = { idReceta -> vm.borrarReceta(idReceta) } )
        }
    }
}

/**
 * Dialog para el borrado de las recetas
 */
@Composable
fun ConfirmarBorradoDialog(
    onConfirmar: () -> Unit,
    onCancelar: () -> Unit
) {
    AlertDialog(
        containerColor = Colores.MarronClaro,
        onDismissRequest = onCancelar,
        title = {
            Text(
                text = "Borrar Receta",
                fontFamily = fuenteTexto,
                fontWeight = FontWeight.Bold,
                color = Colores.Negro
            )
        },
        text = {
            Text(
                text = "¿Está seguro que quiere borrar esta receta?",
                fontFamily = fuenteTexto,
                fontWeight = FontWeight.SemiBold,
                color = Colores.Negro
            )
        },
        confirmButton = {
            TextButton(
                onClick = onConfirmar,
                modifier = Modifier.padding(start = 62.dp)
            ) {
                Text(
                    "Confirmar",
                    fontFamily = fuenteTexto,
                    fontWeight = FontWeight.Bold,
                    color = Colores.VerdeOscuro,
                    fontSize = ConstanteTexto.TextoNormal
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onCancelar) {
                Text(
                    "Cancelar",
                    fontFamily = fuenteTexto,
                    fontWeight = FontWeight.Bold,
                    color = Colores.RojoError,
                    fontSize = ConstanteTexto.TextoNormal
                )
            }
        }
    )
}
