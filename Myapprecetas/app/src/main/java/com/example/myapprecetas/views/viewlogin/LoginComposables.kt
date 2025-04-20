package com.example.myapprecetas.views.viewlogin

import android.app.Activity
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import androidx.navigation.NavHostController
import com.example.myapprecetas.R
import com.example.myapprecetas.ui.theme.Colores
import com.example.myapprecetas.ui.theme.FamilyQuicksand

import com.example.myapprecetas.ui.theme.common.ConstanteTexto
import com.example.myapprecetas.ui.theme.common.BotonAtras
import com.example.myapprecetas.vm.VMLogin
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseUser

//TODO mirar quitar la fuente de global
//TODO mirar lo del teclado y el boton que se va muy arriba
object myfuente {
    var font = FamilyQuicksand.quicksand
}

@Composable
fun MensajeError(mensajeError : String?, cargando : Boolean) {
    var mensaje = if(cargando) "" else mensajeError
    Text(
        modifier = Modifier.padding(start = 2.dp,),
        color =  Colores.RojoError,
        text = mensaje ?: "",
        fontFamily = myfuente.font
    )
}

@Composable
fun IrAtrasInicioSesion(navController: NavHostController) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 40.dp, bottom = 90.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        BotonAtras(24.dp, navController)
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = "Inicia sesión",
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.headlineMedium,
            color = Color.Black,
            fontFamily = myfuente.font
        )
    }
}

@Composable
fun EmailField(email: String, onTextChanged: (String) -> Unit){

    OutlinedTextField(
        value = email,
        onValueChange = {onTextChanged(it)},
        //label = { Text("Username") },
        placeholder = { Text("Usuario") },
        modifier = Modifier.fillMaxWidth().border(2.dp, Colores.Gris, MaterialTheme.shapes.medium),
        shape = MaterialTheme.shapes.medium,
        maxLines = 1,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Email,
            imeAction = ImeAction.Next
        ),
        textStyle = TextStyle(color = Color.Black),
        leadingIcon = {
            Icon(
                painter = painterResource(R.drawable.usero),
                contentDescription = "usuario",
                modifier = Modifier.width(24.dp).height(24.dp),
                tint = Colores.Gris
            )
        }
    )
}

@Composable
fun PasswordField(vm: VMLogin, password: String, onTextChanged: (String) -> Unit, isPasswordVisible: Boolean){
    var iconoPassword = if(!isPasswordVisible) R.drawable.eye else R.drawable.eyecrossedo

    OutlinedTextField(
        value = password,
        onValueChange =  {onTextChanged(it)},
        //label = { Text("Password") },
        placeholder = { Text("Password") },
        visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Password,
            imeAction = ImeAction.Done
        ),
        modifier = Modifier.fillMaxWidth().border(2.dp, Colores.Gris, MaterialTheme.shapes.medium),
        shape = MaterialTheme.shapes.medium,
        textStyle = TextStyle(color = Color.Black),
        maxLines = 1,
        leadingIcon = {
            Icon(
                painter = painterResource(R.drawable.lock),
                contentDescription = "password",
                modifier = Modifier.width(24.dp).height(24.dp),
                tint = Colores.Gris
            )
        },
        trailingIcon = {
            Icon(
                painter = painterResource(id = iconoPassword),
                contentDescription = "mostrar password",
                modifier = Modifier
                    .width(24.dp)
                    .height(24.dp)
                    .clickable { vm.onPasswordVisibleChanged() },
                tint = Colores.Gris
            )
        },
    )
}

@Composable
fun PasswordOlvidada(){
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.End
    ) {
        Text(
            text = "¿Has olvidado tu contraseña?",
            fontSize = ConstanteTexto.TextoPequeno,
            color =  Colores.VerdeOscuro,
            textDecoration = TextDecoration.Underline,
            modifier = Modifier.clickable { /* TODO */ },
        )
    }
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
            fontFamily = myfuente.font
        )
        HorizontalDivider(
            modifier = Modifier.width(40.dp),
            thickness = 1.dp,
            color = Colores.Gris
        )
    }
}

@Composable
fun LoginOtherOptions(vm : VMLogin, navController: NavHostController) {
    val context = LocalContext.current

    val googleSignInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult() // Cambiado a StartActivityForResult
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data
            try {
                val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                val account = task.getResult(ApiException::class.java)
                account.idToken?.let { token ->
                    vm.signInWithGoogle(token) // Pasa el token directamente
                }
            } catch (e: Exception) {
                Log.e("GoogleSignIn", "Error: ${e.message}")
                // Maneja el error (puedes mostrar un Snackbar)
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
                    .requestIdToken("181983126111-1jlv71ep88fv50pbkisj9el1ak734kje.apps.googleusercontent.com")
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
                        .size(45.dp)
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
                    fontFamily = myfuente.font
                )
            }
        }
    }
}

@Composable
fun BtnLogin(vm: VMLogin, email: String, password: String, user: FirebaseUser?, cargando : Boolean, navController: NavHostController) {
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(user) {
        if (user != null) {
            navController.navigate("lista_recetas") {
                popUpTo("login") { inclusive = true }
            }
        }
    }
    Button(
        onClick = { vm.signInViewModel(email, password); keyboardController?.hide() },
        modifier = Modifier
            .imePadding()
            .fillMaxWidth()
            .height(56.dp)
        ,
        shape = RoundedCornerShape(10.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Colores.VerdeOscuro,
            contentColor = MaterialTheme.colorScheme.onPrimary
        )
    ) {
        if (cargando) CircularProgressIndicator()
        else Text(
            text = "Iniciar sesión",
            fontSize = ConstanteTexto.TextoSemigrande,
            fontFamily = myfuente.font,
            fontWeight = FontWeight.SemiBold
        )

    }
}