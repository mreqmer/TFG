package com.example.myapprecetas.views

import android.util.Log
import android.widget.Toast
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SegmentedButtonDefaults.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.AsyncImagePainter.State.Empty.painter
import com.example.myapprecetas.R
import com.example.myapprecetas.vm.VMLogin
import com.google.firebase.auth.FirebaseAuth
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.sp
import com.example.myapprecetas.ui.theme.VerdeOscuro


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
    val email by vm.Email.observeAsState("")
    val password by vm.Password.observeAsState("")
    var isPasswordVisible = vm.EsPasswordVisible.observeAsState().value ?: false

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
            EmailField(email, {vm.OnLoginChanged(it, password)})

            PasswordField(password, {vm.OnLoginChanged(email, it)}, isPasswordVisible)

            PasswordOlvidada()

            Spacer(modifier = Modifier.height(16.dp))

            TextOtherOptions()

            LoginOtherOptions()
        }

        // Botón de login (posición estable)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp)
        ) {
            Button(
                onClick = { vm.signIn(email, password) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),  // Más alto (antes 52.dp)
                shape = RoundedCornerShape(10.dp),  // Más cuadrado (antes 12.dp por defecto)
                colors = ButtonDefaults.buttonColors(
                    containerColor = VerdeOscuro,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Text(
                    text = "Iniciar sesión",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.SemiBold  // Texto más grueso
                    ),
                    fontSize = 18.sp  // Tamaño aumentado
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            user?.let {
                Text(
                    text = "Bienvenido, ${it.email}",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Normal
                    )
                )
            }

            error?.let {
                Text(
                    text = it,
                    color = Color.Red,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Medium
                    )
                )
            }
        }}}


@Composable
fun BtnLogin(vm: VMLogin, email: String, password: String, navController: NavHostController) {
    Button(
        onClick = { vm.signIn(email, password) },
        modifier = Modifier.fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(
            containerColor = VerdeOscuro, // Color de fondo
            contentColor = Color.White // Color del texto (contraste)
        )
    ) {
        Text("Login")
    }
}
@Composable
fun EmailField(email: String, onTextChanged: (String) -> Unit){

    OutlinedTextField(
        value = email,
        onValueChange = {onTextChanged(it)},
        //label = { Text("Username") },
        placeholder = {Text("Usuario")},
        modifier = Modifier.fillMaxWidth().border(2.dp, Color.Black, MaterialTheme.shapes.medium),
        shape = MaterialTheme.shapes.medium,

        leadingIcon = {
            Icon(
                painter = painterResource(R.drawable.usero),
                contentDescription = "pw",
                modifier = Modifier.width(24.dp).height(24.dp)
            )
        }
    )
}
@Composable
fun PasswordField(password: String, onTextChanged: (String) -> Unit, isPasswordVisible: Boolean){
    OutlinedTextField(
        value = password,
        onValueChange =  {onTextChanged(it)},
        //label = { Text("Password") },
        placeholder = {Text("Password")},
        visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions.Default,
        modifier = Modifier.fillMaxWidth().border(2.dp, Color.Black, MaterialTheme.shapes.medium),
        shape = MaterialTheme.shapes.medium,
        trailingIcon = {
            Icon(
                painter = painterResource(R.drawable.eye),
                contentDescription = "pwVisible",
                modifier = Modifier.width(24.dp).height(24.dp)
            )
        },
        leadingIcon = {
            Icon(
                painter = painterResource(R.drawable.lock),
                contentDescription = "pw",
                modifier = Modifier.width(24.dp).height(24.dp)
            )
        }
    )
}

@Composable
private fun PasswordOlvidada(){
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
        horizontalArrangement = Arrangement.End
    ) {
        Text(
            text = "¿Has olvidado tu contraseña?",
            color =  VerdeOscuro,
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
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
        )
        Text(
            text = "Otras opciones de verificación",
            modifier = Modifier.padding(horizontal = 8.dp),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
        HorizontalDivider(
            modifier = Modifier.width(40.dp),
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
        )
    }
}

@Composable
private fun LoginOtherOptions(
    iconPadding: Dp = 16.dp
) {
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
                        .size(34.dp)
                        .align(Alignment.CenterStart)
                        .padding(start = iconPadding),
                    tint = Color.Unspecified
                )

                Text(
                    text = "Continuar con Google",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.align(Alignment.Center),
                    color = Color.Black,
                )
            }
        }
    }
}