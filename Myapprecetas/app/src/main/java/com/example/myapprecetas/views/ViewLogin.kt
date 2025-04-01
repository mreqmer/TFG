package com.example.myapprecetas.views

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.myapprecetas.R
import com.example.myapprecetas.vm.VMLogin
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import coil.compose.AsyncImagePainter.State.Empty.painter

import com.example.myapprecetas.ui.theme.Colores
import com.google.android.play.integrity.internal.s
import com.google.firebase.auth.FirebaseUser


/*TODO GORDO
 los botones no van
 Que javi me verifique el padding del mensaje de error
 */


@Composable
fun ViewLogin(vm: VMLogin, navController: NavHostController){

    Box(){
        LoginScreen(vm, navController)
    }
}



@Composable
fun LoginScreen(vm: VMLogin, navController: NavHostController) {
    val context = LocalContext.current
    val user by vm.user.collectAsState()
    val error by vm.error.collectAsState()
    val email by vm.Email.collectAsState("")
    val password by vm.Password.collectAsState("")
    val isPasswordVisible = vm.EsPasswordVisible.collectAsState().value
    val errorMostrado by vm.ErrorMostrado.collectAsState(null)
    val cargando by vm.Cargando.collectAsState(false)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Título con icono
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 40.dp, bottom = 90.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = R.drawable.left),
                contentDescription = "Login icon",
                modifier = Modifier.size(24.dp),
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "Inicia sesión",
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.headlineMedium,
                color = Color.Black
            )
        }

        // Contenido principal (movido más abajo)
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(top = 40.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {

            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {

                MensajeError(errorMostrado, cargando)
                EmailField(email, { vm.OnLoginChanged(it, password) })
                Spacer(modifier = Modifier.height(20.dp))
                PasswordField(vm, password, {vm.OnLoginChanged(email, it)}, isPasswordVisible)
                PasswordOlvidada()
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextOtherOptions()

            LoginOtherOptions()
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp)
        ) {

            BtnLogin(vm, email, password, user, cargando, navController);

            Spacer(modifier = Modifier.height(8.dp))
        }}}


@Composable
fun cargaLogin(vm: ViewModel, cargando : Boolean){
    if (cargando) {
        CircularProgressIndicator(modifier = Modifier.height(20.dp).width(20.dp), color = Colores.RojoError)
    }else{

    }
}


@Composable
fun MensajeError(mensajeError : String?, cargando : Boolean) {
    var mensaje = if(cargando) "" else mensajeError
    Text(
        modifier = Modifier.padding(start = 2.dp,),
        color =  Colores.RojoError,
        text = mensaje ?: "",
        )

}

@Composable
fun BtnLogin(vm: VMLogin, email: String, password: String, user: FirebaseUser?, cargando : Boolean, navController: NavHostController) {
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(user) {
        if (user != null) {
            navController.navigate("construccion") {
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
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.SemiBold  // Texto más grueso
            ),
            fontSize = 18.sp  // Tamaño aumentado
        )

    }
}

@Composable
fun EmailField(email: String, onTextChanged: (String) -> Unit){

    OutlinedTextField(
        value = email,
        onValueChange = {onTextChanged(it)},
        //label = { Text("Username") },
        placeholder = {Text("Usuario")},
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
fun PasswordField(vm:VMLogin, password: String, onTextChanged: (String) -> Unit, isPasswordVisible: Boolean){
    var iconoPassword = if(!isPasswordVisible) R.drawable.eye else R.drawable.eyecrossedo

    OutlinedTextField(
        value = password,
        onValueChange =  {onTextChanged(it)},
        //label = { Text("Password") },
        placeholder = {Text("Password")},
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
                                .clickable { vm.OnPasswordVisibleChanged() },
                tint = Colores.Gris
            )
        },
    )
}

@Composable
private fun PasswordOlvidada(){
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.End
    ) {
        Text(
            text = "¿Has olvidado tu contraseña?",
            fontSize = 14.sp,
            color =  Colores.VerdeOscuro,
            textDecoration = TextDecoration.Underline,
            modifier = Modifier.clickable { /* TODO */ },
        )
    }
}

@Composable
private fun TextOtherOptions() {
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
            fontSize = 16.sp,
            modifier = Modifier.padding(horizontal = 8.dp),
            color = Colores.Gris
        )
        HorizontalDivider(
            modifier = Modifier.width(40.dp),
            thickness = 1.dp,
            color = Colores.Gris
        )
    }
}

@Composable
private fun LoginOtherOptions() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp)
    ) {
        OutlinedButton(
            onClick = { /* Acción */ },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = MaterialTheme.shapes.small,
            border = BorderStroke(2.dp, MaterialTheme.colorScheme.outline)
        ) {
            Box(
                modifier = Modifier.fillMaxWidth()
            ) {
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
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.align(Alignment.Center),
                    color = Color.Black.copy(alpha = 0.7f)
                )
            }
        }
    }
}
