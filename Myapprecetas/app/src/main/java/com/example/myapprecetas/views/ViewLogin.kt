package com.example.myapprecetas.views

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.myapprecetas.vm.VMLogin
import com.google.firebase.auth.FirebaseAuth



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

//    LaunchedEffect(user) {
//        if (user != null) {
//            navController.navigate("listado_personas") {
//                popUpTo("login") { inclusive = true }
//            }
//        }
//    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Login", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        EmailField(email, {vm.OnLoginChanged(it, password)})

        Spacer(modifier = Modifier.height(8.dp))

        PasswordField(password, {vm.OnLoginChanged(email, it)},isPasswordVisible)

        Spacer(modifier = Modifier.height(16.dp))

        BtnLogin(vm ,email, password, navController)

        Spacer(modifier = Modifier.height(8.dp))

        user?.let {
            Text("Bienvenido, ${it.email}")
        }

        // Mostrar error
        error?.let {
            Text(it, color = Color.Red)
        }

    }
}


@Composable
fun BtnLogin(vm: VMLogin ,email: String, password: String, navController: NavHostController){
    Button(
        onClick = { vm.signIn(email, password) },
        modifier = Modifier.fillMaxWidth()
    ) {
        Text("Login")
    }

}
@Composable
fun EmailField(email: String, onTextChanged: (String) -> Unit){

    OutlinedTextField(
        value = email,
        onValueChange = {onTextChanged(it)},
        label = { Text("Username") },
        modifier = Modifier.fillMaxWidth()
    )
}
@Composable
fun PasswordField(password: String, onTextChanged: (String) -> Unit, isPasswordVisible: Boolean){
    OutlinedTextField(
        value = password,
        onValueChange =  {onTextChanged(it)},
        label = { Text("Password") },
        visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions.Default,
        modifier = Modifier.fillMaxWidth()
    )

}