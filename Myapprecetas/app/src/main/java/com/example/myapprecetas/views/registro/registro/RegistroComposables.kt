package com.example.myapprecetas.views.registro.registro

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.myapprecetas.ui.theme.Colores
import com.example.myapprecetas.ui.theme.common.ConstanteTexto
import com.google.android.play.integrity.internal.i

@Composable
fun CampoTextoRegistro(
    value: String,
    onTextChanged: (String) -> Unit,
    placeholder: String,
    icono: Int,
    tipo: KeyboardType,
    imeAction: ImeAction = ImeAction.Next,
    visualTransformation: VisualTransformation = VisualTransformation.None
) {
    OutlinedTextField(
        value = value,
        onValueChange = onTextChanged,
        placeholder = {
            Text(
                text = placeholder,
                fontFamily = fuenteTexto
            )
        },
        maxLines = 1,
        keyboardOptions = KeyboardOptions(
            keyboardType = tipo,
            imeAction = imeAction
        ),
        visualTransformation = visualTransformation,
        modifier = Modifier
            .fillMaxWidth()
            .border(2.dp, Colores.Gris, MaterialTheme.shapes.medium),
        shape = MaterialTheme.shapes.medium,
        textStyle = TextStyle(color = Color.Black),
        leadingIcon = {
            Icon(
                painter = painterResource(id = icono),
                contentDescription = "icono",
                modifier = Modifier.size(24.dp),
                tint = Colores.Gris
            )
        }
    )
}


@Composable
fun MensajeError(error: String?) {
    Box(
        modifier = Modifier.fillMaxWidth()
    ) {
        error?.let {
            Text(
                text = it,
                color = Colores.RojoError,
                fontFamily = fuenteTexto,
                fontSize = ConstanteTexto.TextoNormal,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun TextoCuenta(navController: NavHostController){
    Box(modifier = Modifier.fillMaxWidth()) {
        TextButton(
            onClick = { navController.navigate("login") },
            modifier = Modifier.align(Alignment.Center) // Aquí es donde se centra el texto
        ) {
            Text(
                text = "¿Ya tienes cuenta? Inicia sesión",
                color = Colores.VerdeOscuro,
                fontFamily = fuenteTexto,
                fontWeight = FontWeight.Bold,
                fontSize = ConstanteTexto.TextoNormal, // Tamaño más grande
                textDecoration = TextDecoration.Underline // Subrayado
            )
        }
    }


    }
@Composable
fun BotonRegistro(modifier: Modifier = Modifier, onClick: () -> Unit = {}, cargando: Boolean) {

    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .imePadding(),
        shape = RoundedCornerShape(10.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Colores.VerdeOscuro,
            contentColor = MaterialTheme.colorScheme.onPrimary
        )
    ) {

        if (cargando) {
            CircularProgressIndicator()
        }else{
            Text(
                text = "Crear cuenta",
                fontSize = ConstanteTexto.TextoSemigrande,
                fontFamily = fuenteTexto,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

