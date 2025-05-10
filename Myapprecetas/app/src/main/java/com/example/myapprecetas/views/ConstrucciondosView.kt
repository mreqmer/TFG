package com.example.myapprecetas.views

import android.net.Uri
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.myapprecetas.userauth.AuthManager
import com.example.myapprecetas.vm.VMConstrucciondos

@Composable
fun PaginaEnConstrucciondosConBotonAtras(
    viewModel: VMConstrucciondos,
    onBackClick: NavHostController,
) {
    val user by AuthManager.currentUser.collectAsState()
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    val context = LocalContext.current
    val activity = context as? ComponentActivity

    val openGalleryLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? -> uri?.let { imageUri = it } }

    // Estado de carga
    val isLoading = viewModel.isLoading
    val imageUrl = viewModel.imageUrl
    val uploadError = viewModel.uploadError

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("En construcción", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(16.dp))

        user?.let {
            Text("Usuario: ${it.displayName ?: "Nombre no disponible"}")
            Text("Correo: ${it.email ?: "Correo no disponible"}")
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Botón para abrir galería
        Button(onClick = {
            openGalleryLauncher.launch("image/*")
        }) {
            Text("Seleccionar imagen")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Muestra la imagen seleccionada
        imageUri?.let {
            Box(
                modifier = Modifier
                    .size(width = 140.dp, height = 100.dp)
                    .padding(8.dp)
                    .border(1.dp, Color.Gray, RoundedCornerShape(8.dp))
            ) {
                Image(
                    painter = rememberAsyncImagePainter(it),
                    contentDescription = "Imagen seleccionada",
                    modifier = Modifier.fillMaxSize()
                )

                EliminarImagenBoton { imageUri = null }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Muestra mensaje de error si ocurre
        uploadError?.let {
            Text(it, color = Color.Red)
        }

        // Cargar o subir imagen a Cloudinary
        if (imageUri != null && !isLoading) {
            Button(
                onClick = {
                    viewModel.subirImagen(imageUri!!)
                }
            ) {
                Text("Subir Imagen")
            }
        }

        // Muestra indicador de carga
        if (isLoading) {
            CircularProgressIndicator()
        }

        // Muestra URL de la imagen subida si es exitosa
        imageUrl?.let {
            Text("Imagen subida: $it")
        }

        // Aquí mostramos la imagen de Cloudinary que has subido
        VerImagenDesdeUrl()

        Spacer(modifier = Modifier.height(32.dp))

        // Botón para cerrar sesión
        Button(onClick = {
            AuthManager.logoutWithRevokeAccess(onBackClick.context) {
                onBackClick.navigate("inicio") {
                    popUpTo("construccion") { inclusive = true }
                }
            }
        }) {
            Text("Cerrar sesión")
        }
    }
}

@Composable
fun EliminarImagenBoton(
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .padding(4.dp)
            .width(20.dp)
            .height(20.dp)
            .background(Color.White, RoundedCornerShape(6.dp))
    ) {
        IconButton(
            onClick = onClick,
            modifier = Modifier
                .fillMaxSize()
                .align(Alignment.Center)
        ) {
            Icon(
                painter = painterResource(id = android.R.drawable.ic_delete),
                contentDescription = "Eliminar imagen",
                tint = Color.Gray
            )
        }
    }
}

@Composable
fun VerImagenDesdeUrl() {
    val imageUrl = "https://res.cloudinary.com/dckzmg9c1/image/upload/v1746387877/xzpt2abtqn1jijttzkb0.jpg"

    Image(
        painter = rememberAsyncImagePainter(imageUrl),
        contentDescription = "Imagen de Cloudinary",
        modifier = Modifier.fillMaxWidth()
    )
}
