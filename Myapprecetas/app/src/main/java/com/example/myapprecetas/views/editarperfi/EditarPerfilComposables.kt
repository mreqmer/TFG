package com.example.myapprecetas.views.editarperfi

import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.myapprecetas.R
import com.example.myapprecetas.ui.theme.Colores
import com.example.myapprecetas.ui.theme.common.ConstanteTexto
import com.example.myapprecetas.ui.theme.fuenteTexto

/**
 * Imagen de perfil del usuario
 */
@Composable
fun ImagenPerfil(uri: Uri?, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(120.dp)
            .clip(CircleShape)
            .background(Colores.Gris.copy(alpha = 0.1f))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        if (uri != null) {
            Image(
                painter = rememberAsyncImagePainter(uri),
                contentDescription = "Imagen de perfil",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
            )

        } else {
            val url = stringResource(id = R.string.img_perfil_default)
            Image(
                painter = rememberAsyncImagePainter(
                    model = url,
                    error = painterResource(R.drawable.ic_launcher_background)),
                contentDescription = "Imagen de perfil",
                modifier = Modifier.fillMaxSize(),

            )
        }
    }
}


/**
 * Nombre del usuario editable
 */
@Composable
fun CampoNombre(
    nombreUsuario: String,
    editandoNombre: Boolean,
    onNombreChange: (String) -> Unit,
    onCancelarNombre: () -> Unit,
    onEditarNombre: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        if (editandoNombre) {
            OutlinedTextField(
                value = nombreUsuario,
                onValueChange = onNombreChange,
                label = {
                    Text(
                        text = "Nombre de usuario",
                        fontFamily = fuenteTexto,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = ConstanteTexto.TextoNormal,
                        )
                        },
                singleLine = true,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp)
            )
            TextButton(onClick = onCancelarNombre) {
                Text(
                    text = "Cancelar",
                    fontFamily = fuenteTexto,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = ConstanteTexto.TextoNormal,)
            }
        } else {
            Text(
                text = nombreUsuario,
                fontFamily = fuenteTexto,
                fontWeight = FontWeight.SemiBold,
                fontSize = ConstanteTexto.TextoNormal,
                modifier = Modifier.weight(1f)
            )
            TextButton(
                onClick = onEditarNombre,
            ) {
                Text(
                    text = "Editar",
                    fontFamily = fuenteTexto,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = ConstanteTexto.TextoNormal,
                    )
            }
        }
    }
}

/**
 * Botón para guardar los datos del usuario
 */
@Composable
fun BotonGuardar(
    navController: NavHostController,
    cargando: Boolean,
    guardadoExitoso: Boolean?,
    errorGuardado: String?,
    onGuardarClick: () -> Unit,
) {
    val context = LocalContext.current

    LaunchedEffect(guardadoExitoso) {
        if (guardadoExitoso == true) {
            navController.navigate("perfil") {
                launchSingleTop = true
                popUpTo("perfil") { inclusive = true }
            }
        }
    }

    LaunchedEffect(errorGuardado) {
        errorGuardado?.let { error ->
            Toast.makeText(context, error, Toast.LENGTH_LONG).show()
            navController.navigate("perfil") {
                launchSingleTop = true
                popUpTo("perfil") { inclusive = true }
            }
        }
    }

    Button(
        onClick = onGuardarClick,
        enabled = !cargando,
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(45.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Colores.VerdeOscuro,
            contentColor = Colores.Blanco,
            disabledContainerColor = Colores.VerdeOscuro,
            disabledContentColor = Colores.Blanco
        ),
    ) {
        if (cargando) {
            CircularProgressIndicator(

                strokeWidth = 2.dp,
                modifier = Modifier.size(20.dp)
            )

        } else {
            Text(
                text = "Guardar cambios",
                fontFamily = fuenteTexto,
                fontWeight = FontWeight.SemiBold,
                fontSize = ConstanteTexto.TextoNormal,
            )
        }
    }
}

