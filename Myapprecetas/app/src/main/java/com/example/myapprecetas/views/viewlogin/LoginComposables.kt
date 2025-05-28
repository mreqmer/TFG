package com.example.myapprecetas.views.viewlogin

import android.app.Activity
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.myapprecetas.R
import com.example.myapprecetas.ui.theme.Colores
import com.example.myapprecetas.ui.theme.FamilyQuicksand
import com.example.myapprecetas.ui.theme.common.ConstanteTexto
import com.example.myapprecetas.ui.theme.common.ConstanteIcono
import com.example.myapprecetas.vm.VMLogin
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseUser

// Variable para controlar la fuente desde un solo lugar
private val currentFont = FamilyQuicksand.quicksand // Cambia aquí la fuente si necesitas probar otra

@Composable
fun MensajeError(mensajeError: String?, cargando: Boolean) {
    val mensaje = if (cargando) "" else mensajeError
    Text(
        modifier = Modifier.padding(start = 2.dp),
        color = Colores.RojoError,
        text = mensaje ?: "",
        fontFamily = currentFont
    )
}

@Composable
fun EmailField(email: String, onTextChanged: (String) -> Unit) {
    OutlinedTextField(
        value = email,
        onValueChange = { onTextChanged(it) },
        placeholder = {
            Text(
                text = "Correo electrónico",
                fontFamily = currentFont
            )
        },
        modifier = Modifier
            .fillMaxWidth()
            .border(2.dp, Colores.Gris, MaterialTheme.shapes.medium),
        shape = MaterialTheme.shapes.medium,
        maxLines = 1,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Email,
            imeAction = ImeAction.Next
        ),
        textStyle = TextStyle(color = Color.Black, fontFamily = currentFont),
        leadingIcon = {
            Icon(
                painter = painterResource(R.drawable.usero),
                contentDescription = "usuario",
                modifier = Modifier.size(24.dp),
                tint = Colores.Gris
            )
        }
    )
}

@Composable
fun PasswordField(
    vm: VMLogin,
    password: String,
    onTextChanged: (String) -> Unit,
    isPasswordVisible: Boolean
) {
    val iconoPassword = if (!isPasswordVisible) R.drawable.eye else R.drawable.eyecrossedo

    OutlinedTextField(
        value = password,
        onValueChange = { onTextChanged(it) },
        placeholder = {
            Text(
                text = "Password",
                fontFamily = currentFont
            )
        },
        visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Password,
            imeAction = ImeAction.Done
        ),
        modifier = Modifier
            .fillMaxWidth()
            .border(2.dp, Colores.Gris, MaterialTheme.shapes.medium),
        shape = MaterialTheme.shapes.medium,
        textStyle = TextStyle(color = Color.Black, fontFamily = currentFont),
        maxLines = 1,
        leadingIcon = {
            Icon(
                painter = painterResource(R.drawable.lock),
                contentDescription = "password",
                modifier = Modifier.size(24.dp),
                tint = Colores.Gris
            )
        },
        trailingIcon = {
            Icon(
                painter = painterResource(id = iconoPassword),
                contentDescription = "mostrar password",
                modifier = Modifier
                    .size(24.dp)
                    .clickable { vm.onPasswordVisibleChanged() },
                tint = Colores.Gris
            )
        }
    )
}

@Composable
fun PasswordOlvidada(vm: VMLogin) {
    var mostrarDialog by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End
    ) {
        Text(
            text = "¿Has olvidado tu contraseña?",
            fontSize = ConstanteTexto.TextoPequeno,
            color = Colores.VerdeOscuro,
            fontWeight = FontWeight.SemiBold,
            textDecoration = TextDecoration.Underline,
            modifier = Modifier.clickable { mostrarDialog = true},
            fontFamily = currentFont
        )
    }

    if (mostrarDialog) {
        DialogoRestablecerPassword(
            onDismiss = {
                mostrarDialog = false
                vm.resetErrorDialog()
            }
        )
    }
}

@Composable
fun DialogoRestablecerPassword(
    onDismiss: () -> Unit,
    vmLogin: VMLogin = hiltViewModel()
) {
    val context = LocalContext.current
    var correo by remember { mutableStateOf("") }
    val estadoReset = vmLogin.estadoResetPassword
    val errorDialog = vmLogin.errorMostradoDialog.collectAsState()

    LaunchedEffect(estadoReset) {
        estadoReset?.let { result ->
            if (result.isSuccess) {
                vmLogin.resetErrorDialog()
                Toast.makeText(
                    context,
                    "Correo enviado a $correo",
                    Toast.LENGTH_LONG
                ).show()
                onDismiss()
            }

            vmLogin.limpiarEstadoResetPassword()
        }
    }
    AlertDialog(
        containerColor = Colores.MarronClaro,
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Recuperar contraseña",
                fontFamily = currentFont,
                fontWeight = FontWeight.Bold,
                color = Colores.Negro
            )
        },
        text = {
            Column {
                Text(
                    text = "Revisa tu correo electrónico para reestablecer tu contraseña",
                    fontFamily = currentFont,
                    fontWeight = FontWeight.SemiBold,
                    color = Colores.Negro
                )
                Spacer(modifier = Modifier.height(12.dp))
                Column {
                    OutlinedTextField(
                        value = correo,
                        onValueChange = { correo = it },
                        label = {
                            Text(
                                text = "Correo electrónico",
                                fontFamily = currentFont,
                                color = Colores.Negro
                            )
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Text(
                        text = errorDialog.value,
                        color = Colores.RojoError,
                        fontSize = ConstanteTexto.TextoPequeno,
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = currentFont,
                        modifier = Modifier
                            .padding(start = 16.dp, top = 4.dp)
                            .heightIn(min = 20.dp)
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                vmLogin.restablecerPassword(correo)
            },
                modifier = Modifier.padding(start = 82.dp)
            ) {
                Text(
                    "Aceptar",
                    fontFamily = currentFont,
                    fontWeight = FontWeight.Bold,
                    color = Colores.VerdeOscuro,
                    fontSize = ConstanteTexto.TextoNormal
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    "Cancelar",
                    fontFamily = currentFont,
                    fontWeight = FontWeight.Bold,
                    color = Colores.RojoError,
                    fontSize = ConstanteTexto.TextoNormal
                )
            }
        }

    )
}



@Composable
fun TextOtherOptions() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        HorizontalDivider(
            modifier = Modifier.width(40.dp),
            thickness = 1.dp,
            color = Colores.Gris
        )
        Text(
            text = "Otras opciones de verificación",
            fontSize = ConstanteTexto.TextoNormal,
            modifier = Modifier.padding(horizontal = 8.dp),
            color = Colores.Gris,
            fontFamily = currentFont
        )
        HorizontalDivider(
            modifier = Modifier.width(40.dp),
            thickness = 1.dp,
            color = Colores.Gris
        )
    }
}

@Composable
fun LoginOtherOptions(vm: VMLogin, navController: NavHostController) {
    val context = LocalContext.current

    val googleSignInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data
            try {
                val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                val account = task.getResult(ApiException::class.java)
                account.idToken?.let { token ->
                    vm.signInWithGoogle(token)
                }
            } catch (e: Exception) {
                Log.e("GoogleSignIn", "Error: ${e.message}")
            }
        }
    }
    val loginSuccess by vm.loginSuccess.collectAsState()

    LaunchedEffect(loginSuccess) {
        if (loginSuccess) {
            navController.navigate("lista_recetas") {
                popUpTo("login") { inclusive = true }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp)
    ) {
        OutlinedButton(
            onClick = {
                val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(context.getString(R.string.string_server_client_id))
                    .requestEmail()
                    .build()
                val googleSignInClient = GoogleSignIn.getClient(context, gso)
                googleSignInLauncher.launch(googleSignInClient.signInIntent)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = MaterialTheme.shapes.small,
            border = BorderStroke(2.dp, MaterialTheme.colorScheme.outline)
        ) {
            Box(modifier = Modifier.fillMaxWidth()) {
                Icon(
                    painter = painterResource(id = R.drawable.google),
                    contentDescription = "Google",
                    modifier = Modifier
                        .size(ConstanteIcono.IconoExtraGrande)
                        .align(Alignment.CenterStart)
                        .padding(start = 20.dp),
                    tint = Color.Unspecified
                )
                Text(
                    text = "Continuar con Google",
                    fontSize = ConstanteTexto.TextoSemigrande,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.align(Alignment.Center),
                    color = Color.Black.copy(alpha = 0.7f),
                    fontFamily = currentFont
                )
            }
        }
    }
}

@Composable
fun BtnLogin(
    vm: VMLogin,
    email: String,
    password: String,
    user: FirebaseUser?,
    cargando: Boolean,
    navController: NavHostController
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(user) {
        if (user != null) {
            navController.navigate("lista_recetas") {
                popUpTo("login") { inclusive = true }
            }
        }
    }

    Button(
        onClick = {
            vm.signInViewModel(email, password)
            keyboardController?.hide()
        },
        modifier = Modifier
            .imePadding()
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(10.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Colores.VerdeOscuro,
            contentColor = MaterialTheme.colorScheme.onPrimary
        )
    ) {
        if (cargando) {
            CircularProgressIndicator()
        }else {
            Text(
                text = "Iniciar sesión",
                fontSize = ConstanteTexto.TextoSemigrande,
                fontFamily = currentFont,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

