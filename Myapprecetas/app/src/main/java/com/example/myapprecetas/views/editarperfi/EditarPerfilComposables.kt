package com.example.myapprecetas.views.editarperfi

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImagePainter.State.Empty.painter
import coil.compose.rememberAsyncImagePainter
import com.example.myapprecetas.R
import com.example.myapprecetas.ui.theme.Colores

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
                modifier = Modifier.fillMaxSize()
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
                label = { Text("Nombre de usuario") },
                singleLine = true,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp)
            )
            TextButton(onClick = onCancelarNombre) {
                Text("Cancelar")
            }
        } else {
            Text(
                text = nombreUsuario,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f)
            )
            TextButton(
                onClick = onEditarNombre,
            ) {
                Text("Editar")
            }
        }
    }
}


@Composable
fun BotonGuardar(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(45.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Colores.VerdeOscuro,
            contentColor = Colores.Blanco
        ),
    ) {
        Text(
            text = "Guardar cambios",
            fontSize = 16.sp
        )
    }
}

