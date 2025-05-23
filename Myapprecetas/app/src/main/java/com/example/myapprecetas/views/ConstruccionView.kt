package com.example.myapprecetas.views

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.myapprecetas.ui.theme.common.HeaderAtras


@Composable
fun EditarPerfilDemoView(
    navController: NavHostController,
    onGuardar: (String, Uri?, String?) -> Unit,
    onCancelar: () -> Unit
) {
    var nombreUsuario by remember { mutableStateOf("chef_maestro99") }
    val emailUsuario = "chef@recetas.com"
    var nuevaContrasena by remember { mutableStateOf("") }

    var editandoNombre by remember { mutableStateOf(false) }
    var editandoContrasena by remember { mutableStateOf(false) }

    var imagenUri by remember { mutableStateOf<Uri?>(null) }
    val launcherImagen = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri -> imagenUri = uri }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        HeaderAtras("Editar perfil", navController)

        Spacer(modifier = Modifier.height(24.dp))

        // Imagen de perfil
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(Color(0xFFE0E0E0))
                .clickable { launcherImagen.launch("image/*") },
            contentAlignment = Alignment.Center
        ) {
            if (imagenUri != null) {
                Image(
                    painter = rememberAsyncImagePainter(imagenUri),
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


        Spacer(modifier = Modifier.height(24.dp))

        // Correo electrónico (solo lectura)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = emailUsuario,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f),
                color = Color.Gray
            )
            TextButton(onClick = {}) {
                Text("No editable", color = Color.LightGray)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Nombre de usuario
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (editandoNombre) {
                OutlinedTextField(
                    value = nombreUsuario,
                    onValueChange = { nombreUsuario = it },
                    label = { Text("Nombre de usuario") },
                    singleLine = true,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                )
                TextButton(onClick = {
                    editandoNombre = false
                }) {
                    Text("Cancelar")
                }
            } else {
                Text(
                    text = nombreUsuario,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.weight(1f)
                )
                TextButton(
                    onClick = {
                        if (!editandoContrasena) editandoNombre = true
                    },
                    enabled = !editandoContrasena
                ) {
                    Text("Editar")
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Contraseña
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (editandoContrasena) {
                OutlinedTextField(
                    value = nuevaContrasena,
                    onValueChange = { nuevaContrasena = it },
                    label = { Text("Nueva contraseña") },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                )
                TextButton(onClick = {
                    editandoContrasena = false
                    nuevaContrasena = ""
                }) {
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
                    onClick = {
                        if (!editandoNombre) editandoContrasena = true
                    },
                    enabled = !editandoNombre
                ) {
                    Text("Editar")
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                onGuardar(nombreUsuario, imagenUri, nuevaContrasena.takeIf { it.isNotBlank() })
            },
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
        ) {
            Text("Guardar", color = Color.White)
        }

        Spacer(modifier = Modifier.height(12.dp))

        TextButton(onClick = onCancelar) {
            Text("Cancelar", color = Color.Gray)
        }
    }
}
