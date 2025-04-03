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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import com.example.myapprecetas.ui.theme.Colores
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseUser

/*TODO GORDO
 El recuperar contraseña no va, pero no me quiero matar de momento
 */


@Composable
fun ViewLogin(vm: VMLogin, navController: NavHostController){
    Box(){
        LoginScreen(vm, navController)
    }
}

@Composable
fun LoginScreen(vm: VMLogin, navController: NavHostController) {
    val user by vm.user.collectAsState()
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
        // Título con icono para volver atrás
        IrAtrasInicioSesion(navController)

        // Contenido principal
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(top = 40.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {

            //Elementos de login con email
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {

                MensajeError(errorMostrado, cargando)
                EmailField(email, { vm.OnLoginChanged(it, password) })
                Spacer(modifier = Modifier.height(20.dp))
                PasswordField(vm, password, {vm.OnLoginChanged(email, it)}, isPasswordVisible)
                PasswordOlvidada()
            }

            Spacer(modifier = Modifier.height(16.dp))

            //Otras opciones de login (google, etc)
            TextOtherOptions()

            LoginOtherOptions(vm, navController)
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp)
        ) {
            //Botón para hacer login
            BtnLogin(vm, email, password, user, cargando, navController);

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}