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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.myapprecetas.vm.VMEditarPerfil

@Composable
fun ImagenPerfil(uri: Uri?, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(120.dp)
            .clip(CircleShape)
            .background(Color(0xFFE0E0E0))
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
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "Icono de perfil",
                tint = Color.Gray,
                modifier = Modifier.size(64.dp)
            )
        }
    }
}

@Composable
fun CorreoNoEditable(email: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = email,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f),
            color = Color.Gray
        )
        TextButton(onClick = {}) {
            Text("No editable", color = Color.LightGray)
        }
    }
}

@Composable
fun CampoNombre(
    nombreUsuario: String,
    editandoNombre: Boolean,
    editandoContrasena: Boolean,
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
                enabled = !editandoContrasena
            ) {
                Text("Editar")
            }
        }
    }
}


@Composable
fun CampoContrasena(
    nuevaContrasena: String,
    editandoContrasena: Boolean,
    editandoNombre: Boolean,
    onContrasenaChange: (String) -> Unit,
    onEditarClick: () -> Unit,
    onCancelarClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        if (editandoContrasena) {
            OutlinedTextField(
                value = nuevaContrasena,
                onValueChange = onContrasenaChange,
                label = { Text("Nueva contraseña") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp)
            )
            TextButton(onClick = onCancelarClick) {
                Text("Cancelar")
            }
        } else {
            Text(
                text = "••••••••",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f),
                color = Color.Gray
            )
            TextButton(
                onClick = onEditarClick,
                enabled = !editandoNombre
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
        modifier = Modifier.fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
    ) {
        Text("Guardar", color = Color.White)
    }
}

