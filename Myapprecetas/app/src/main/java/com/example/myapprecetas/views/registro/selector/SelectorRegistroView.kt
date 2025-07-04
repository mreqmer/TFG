package com.example.myapprecetas.views.registro.selector

import android.app.Activity
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.myapprecetas.R
import com.example.myapprecetas.ui.theme.Colores
import com.example.myapprecetas.ui.theme.FamilyQuicksand
import com.example.myapprecetas.ui.theme.common.ConstanteIcono
import com.example.myapprecetas.ui.theme.common.ConstanteTexto
import com.example.myapprecetas.ui.theme.common.HeaderAtras
import com.example.myapprecetas.ui.theme.fuenteTexto
import com.example.myapprecetas.vm.VMSelectorRegistro
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException

/**
 * Compenente principal de la vista
 */
@Composable
fun SelectorRegistroView(vm: VMSelectorRegistro, navController: NavHostController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // TopBar (posición fija arriba)
        HeaderAtras("Registro", navController)

        // Contenido principal centrado dinámicamente
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(top = 50.dp),
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                HeaderMetodoRegistro()
                Spacer(modifier = Modifier.height(32.dp))
                MetodosAutenticacion(navController, vm)
            }
        }
    }
}

/**
 *Header de los métodos de registro
 */
@Composable
private fun HeaderMetodoRegistro() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 40.dp)
    ) {
        Text(
            text = "Método de registro",
            fontSize = ConstanteTexto.TextoGrande,
            fontWeight = FontWeight.Bold,
            color = Colores.Negro,
            fontFamily = fuenteTexto,
            textAlign = TextAlign.Center
        )

        Text(
            text = "Selecciona cómo quieres registrarte",
            fontSize = ConstanteTexto.TextoNormal,
            color = Colores.Negro.copy(alpha = 0.8f),
            modifier = Modifier.padding(top = 8.dp),
            fontFamily = fuenteTexto,
            textAlign = TextAlign.Center
        )

        // Divisor idéntico al de login
        HorizontalDivider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, bottom = 16.dp),
            thickness = 1.dp,
            color = Colores.Gris
        )
    }
}

/**
 * Los diferentes métodos de autenticacion
 */
@Composable
private fun MetodosAutenticacion(navController: NavHostController, vm: VMSelectorRegistro) {
    val context = LocalContext.current

    // Launcher para iniciar la actividad de Google Sign-In
    val googleSignInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data
            try {
                val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                val account = task.getResult(ApiException::class.java)
                account.idToken?.let { idToken ->
                    vm.signInWithGoogle(idToken)
                }
            } catch (e: ApiException) {
                Log.e("GoogleSignIn", "Error: ${e.localizedMessage}")
            }
        }
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        //botón de inicio con gmail
        AuthButton(
            icon = R.drawable.mail,
            text = "Continuar con Email",
            containerColor = Colores.VerdeClaro,
            onClick = { navController.navigate("registro") },
        )
        //Botón de inicio con google
        AuthButton(
            icon = R.drawable.google,
            text = "Continuar con Google",
            containerColor = Colores.Blanco,
            onClick = {
                // Usamos la variable context para crear el cliente de Google Sign-In
                val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(context.getString(R.string.string_server_client_id))
                    .requestEmail()
                    .build()

                val googleSignInClient = GoogleSignIn.getClient(context, gso)
                googleSignInLauncher.launch(googleSignInClient.signInIntent)
            },
        )
    }
}

/**
 * Diferentes botones de los métodos de autenticación
 *
 */
@Composable
private fun AuthButton(
    icon: Int,
    text: String,
    containerColor: Color,
    onClick: () -> Unit,
) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(2.dp, Colores.Gris),
        colors = ButtonDefaults.outlinedButtonColors(containerColor = containerColor)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                painter = painterResource(id = icon),
                contentDescription = null,
                modifier = Modifier.size(ConstanteIcono.IconoNormal),
                tint = Color.Unspecified
            )

            Text(
                text = text,
                fontSize = ConstanteTexto.TextoSemigrande,
                fontWeight = FontWeight.Medium,
                color = Colores.Negro.copy(alpha = 0.9f),
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp),
                fontFamily = fuenteTexto,
                textAlign = TextAlign.Center
            )
        }
    }
}

