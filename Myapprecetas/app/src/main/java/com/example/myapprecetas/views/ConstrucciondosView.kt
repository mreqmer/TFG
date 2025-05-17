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
import kotlinx.coroutines.launch

@Composable
fun PaginaEnConstrucciondosConBotonAtras(
    viewModel: VMConstrucciondos,
    onBackClick: NavHostController,
) {
    val coroutineScope = rememberCoroutineScope()

    val user by AuthManager.currentUser.collectAsState()
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    val context = LocalContext.current
    val activity = context as? ComponentActivity

    val openGalleryLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? -> uri?.let { imageUri = it } }

    val isLoading = viewModel.isLoading
    val imageUrl = "https://res.cloudinary.com/dckzmg9c1/image/upload/v1747348523/vwpaxerpmc2tcrdtuwqz.jpg"
    val publicId = viewModel.publicId
    val uploadError = viewModel.uploadError

    var deleteMessage by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("En construcciówern", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        user?.let {
            Text("Usuario: ${it.displayName ?: "Nombre no disponible"}")
            Text("Correo: ${it.email ?: "Correo no disponible"}")
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(onClick = {
            openGalleryLauncher.launch("image/*")
        }) {
            Text("Seleccionar imagen")
        }

        Button(onClick = {
            coroutineScope.launch {
                viewModel.updateProfilePhoto(imageUrl!!)
            }
        }) {
            Text("Actualizar Foto de Perfil")
        }

        Spacer(modifier = Modifier.height(16.dp))

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
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (imageUri != null && !isLoading) {
            Button(onClick = { viewModel.subirImagen(imageUri!!) }) {
                Text("Subir Imagen")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (imageUrl != null && !isLoading) {

        }

        Spacer(modifier = Modifier.height(16.dp))

        if (viewModel.publicId != null && !viewModel.isLoading) {
            Button(onClick = {
                coroutineScope.launch {
                    viewModel.borrarImagen()
                    deleteMessage = "Se ha borrado"
                }
            }) {
                Text("Borrar Imagen")
            }
        }

        if (deleteMessage?.isNotEmpty() == true) {
            deleteMessage?.let { Text(it) }
        }

        if (isLoading) {
            CircularProgressIndicator()
        }

        uploadError?.let {
            Text(it, color = Color.Red)
        }

        deleteMessage?.let {
            Text(it, color = Color.Green)
        }

//        imageUrl?.let {
//            VerImagenDesdeUrl(imageUrl = it)
//        }

        Spacer(modifier = Modifier.height(32.dp))

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