package com.example.myapprecetas.views.creacionreceta

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.example.myapprecetas.R
import com.example.myapprecetas.dto.constantesobjetos.ConstantesObjetos
import com.example.myapprecetas.ui.theme.Colores
import com.example.myapprecetas.ui.theme.FamilyQuicksand
import com.example.myapprecetas.ui.theme.common.ConstanteIcono
import com.example.myapprecetas.ui.theme.common.ConstanteTexto
import com.example.myapprecetas.ui.theme.common.HeaderAtras
import com.example.myapprecetas.vm.VMCreacionReceta

@Composable
fun CrearRecetaView(vm: VMCreacionReceta, navController: NavHostController) {
    var titulo by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var tiempoPreparacion by remember { mutableStateOf("0") }
    var pasos = remember { mutableStateListOf("") }
    var dificultadSeleccionada by remember { mutableStateOf("") }
    var ingredientesBusqueda by remember { mutableStateOf("") }
    var categoriasBusqueda by remember { mutableStateOf("") }
//    var tiempoPreparacionSeleccionado by remember { mutableStateOf<String?>(null) }

    Scaffold(
        containerColor = Colores.Blanco,
        topBar = {
            HeaderAtras(texto = "Crear Receta", navController = navController)
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.Start
        ) {
            item {
                Spacer(modifier = Modifier.height(32.dp))

                SeccionTitulo("Título")
                TituloInputField(
                    value = titulo,
                    onValueChange = { titulo = it }
                )

                Spacer(modifier = Modifier.height(14.dp))

                SeccionTitulo("Descripción")
                DescripcionInputField(
                    value = descripcion,
                    onValueChange = { descripcion = it }
                )

                Spacer(modifier = Modifier.height(14.dp))

                SeccionTitulo("Ingredientes")
                InputFieldIngredientes(
                    onValueChange = { ingredientesBusqueda = it },
                    navController = navController
                )
//                InputField(
//                    value = ingredientesBusqueda,
//                    onValueChange = { ingredientesBusqueda = it },
//                    placeholder = "Buscar ingredientes..."
//                )

                Spacer(modifier = Modifier.height(16.dp))

                SeccionTitulo("Pasos")
                pasos.forEachIndexed { index, paso ->
                    PasoItem(index, paso, onValueChange = { pasos[index] = it }) {
                        pasos.removeAt(index) // Eliminamos el paso de la lista
                    }
                }

                AddPaso(
                    onClick = { pasos.add("") }
                )

                Spacer(modifier = Modifier.height(16.dp))

                SeccionTitulo("Dificultad")
                DificultadChips(
                    dificultadSeleccionada = dificultadSeleccionada,
                    onDificultadSeleccionada = { nuevaDificultad ->
                        dificultadSeleccionada = nuevaDificultad
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                SeccionTitulo("Tiempo de preparación")
                var tiempoPreparacion by remember { mutableStateOf(30) }

                TiempoPreparacionField(
                    value = tiempoPreparacion,
                    onValueChange = { nuevoTiempo -> tiempoPreparacion = nuevoTiempo }
                )
//                TiempoPreparacionDropdown(
//                    selectedTime = tiempoPreparacionSeleccionado,
//                    onTimeSelected = { tiempoPreparacionSeleccionado = it }
//                )

                Spacer(modifier = Modifier.height(16.dp))

                SeccionTitulo("Categorías")
                InputField(
                    value = categoriasBusqueda,
                    onValueChange = { categoriasBusqueda = it },
                    placeholder = "Buscar categorías..."
                )

                Spacer(modifier = Modifier.height(32.dp))

                BtnGuardarReceta(
                    onClick = { },
                )

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}



@Composable
fun SeccionTitulo(texto: String) {
    Text(
        texto,
        fontSize = 18.sp,
        fontFamily = FamilyQuicksand.quicksand,
        fontWeight = FontWeight.SemiBold,
        color = Colores.Negro,
        modifier = Modifier.padding(bottom = 8.dp)
    )
}

@Composable
fun TituloInputField(
    value: String,
    onValueChange: (String) -> Unit
) {
    val maxLength = 80
    val caracteresRestantes = maxLength - value.length
    val maxLenght = 80
    OutlinedTextField(
        value = value,
        onValueChange = {
            if (it.length <= maxLength) onValueChange(it)
        },
        placeholder = {
            Text(
                text = "Escribe el título",
                fontFamily = FamilyQuicksand.quicksand,
                color = Colores.Gris
            )
        },
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 50.dp)
            .background(Color.White, shape = MaterialTheme.shapes.medium)
            .border(1.dp, Colores.Gris, MaterialTheme.shapes.medium),
        shape = MaterialTheme.shapes.medium,
        textStyle = LocalTextStyle.current.copy(
            color = Color.Black,
            fontFamily = FamilyQuicksand.quicksand
        )
    )
    TextoCaracteresRestantes(
        caracteresRestantes,
        maxLength
    )
}

@Composable
fun DescripcionInputField(
    value: String,
    onValueChange: (String) -> Unit
) {
    val maxLength = 300
    val caracteresRestantes = maxLength - value.length

    Column(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = value,
            onValueChange = {
                if (it.length <= maxLength) onValueChange(it)
            },
            placeholder = {
                Text(
                    text = "Escribe la descripción",
                    fontFamily = FamilyQuicksand.quicksand,
                    color = Colores.Gris
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 80.dp)
                .background(Color.White, shape = MaterialTheme.shapes.medium)
                .border(1.dp, Colores.Gris, MaterialTheme.shapes.medium),
            shape = MaterialTheme.shapes.medium,
            textStyle = LocalTextStyle.current.copy(
                color = Color.Black,
                fontFamily = FamilyQuicksand.quicksand
            ),
            maxLines = 5,

            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Done
            )
        )

        TextoCaracteresRestantes(
            caracteresRestantes,
            maxLength
        )
    }
}

@Composable
fun InputField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = {
            Text(
                text = placeholder,
                fontFamily = FamilyQuicksand.quicksand,
                color = Colores.Gris
            )
        },
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, shape = MaterialTheme.shapes.medium)
            .border(1.dp, Colores.Gris, MaterialTheme.shapes.medium),
        shape = MaterialTheme.shapes.medium,
        maxLines = 1,
        textStyle = LocalTextStyle.current.copy(
            color = Color.Black,
            fontFamily = FamilyQuicksand.quicksand
        )
    )
}

@Composable
fun InputFieldIngredientes(
    onValueChange: (String) -> Unit,
    navController: NavController
) {
    OutlinedTextField(
        value = "Añadir ingredientes...",
        onValueChange = onValueChange,
        placeholder = {
            Text(
                text = "Añadir ingredientes...",
                fontFamily = FamilyQuicksand.quicksand,
                color = Colores.Gris
            )
        },
        leadingIcon = {
            Icon(
                painter = painterResource(R.drawable.search),
                contentDescription = "Buscar",
                tint = Colores.Gris,
                modifier = Modifier
                    .size(ConstanteIcono.IconoPequeno)
            )
        },
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, shape = MaterialTheme.shapes.medium)
            .border(1.dp, Colores.Gris, MaterialTheme.shapes.medium)
            .clickable { navController.navigate("construcciondos") },
        shape = MaterialTheme.shapes.medium,

        maxLines = 1,
        textStyle = LocalTextStyle.current.copy(
            color = Color.Black,
            fontFamily = FamilyQuicksand.quicksand
        )
    )
}

@Composable
fun PasoItem(index: Int, paso: String, onValueChange: (String) -> Unit, onDelete: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        // Colocamos el número del paso y el ícono en una columna
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(end = 8.dp) // Espaciado entre la columna y el campo de texto
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .background(Colores.VerdeOscuro.copy(alpha = 0.9f), shape = CircleShape)
                    .align(Alignment.CenterHorizontally),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "${index + 1}",
                    color = Colores.Blanco,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(13.dp))

            // Ícono de la papelerita, lo colocamos debajo del número en la columna
            Icon(
                painter = painterResource(R.drawable.papelera), // Usando el ícono de papelerita de Material
                contentDescription = "Eliminar paso",
                tint = Colores.Gris,
                modifier = Modifier
                    .size(20.dp)
                    .clickable {
                        onDelete() // Llamamos a la función para eliminar el paso
                    }
            )
        }

        // Campo de texto para el paso
        OutlinedTextField(
            value = paso,
            onValueChange = onValueChange,
            placeholder = {
                Text(
                    text = "Paso ${index + 1}",
                    fontFamily = FamilyQuicksand.quicksand,
                    color = Colores.Gris
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 75.dp)
                .background(Color.White, shape = MaterialTheme.shapes.medium)
                .border(1.dp, Colores.Gris, MaterialTheme.shapes.medium),
            shape = MaterialTheme.shapes.medium,
            maxLines = 5,
            textStyle = LocalTextStyle.current.copy(
                color = Color.Black,
                fontFamily = FamilyQuicksand.quicksand
            )
        )
    }
}
@Composable
fun AddPaso(onClick: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable(onClick = onClick)
    ) {
        Icon(
            painter = painterResource(R.drawable.mas) ,
            contentDescription = "Añadir paso",
            modifier = Modifier.size(ConstanteIcono.IconoPequeno)
        )

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = "Añadir paso",
            fontSize = ConstanteTexto.TextoSemigrande,
            fontFamily = FamilyQuicksand.quicksand,
            fontWeight = FontWeight.SemiBold,

        )
    }
}

@Composable
fun TiempoPreparacionField(
    value: Int,
    onValueChange: (Int) -> Unit,
) {
    val valorMinimo = 5
    val valorMaximo = 400
    val saltoTiempo = 5

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {

        // Campo de texto más pequeño
        OutlinedTextField(
            value = value.toString(),
            onValueChange = { newValue ->
                newValue.toIntOrNull()?.let {
                    onValueChange(it.coerceIn(valorMinimo, valorMaximo))
                }
            },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
            modifier = Modifier.width(70.dp) // Solo ajustamos el ancho del campo
        )


        // Columna con las flechas a la derecha
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy((-4).dp), // Más pegadas entre sí
            modifier = Modifier.padding(start = 8.dp)
        ) {
            IconButton(
                onClick = {
                    val newValue = (value + saltoTiempo).coerceAtMost(valorMaximo)
                    onValueChange(newValue)
                },
                modifier = Modifier.size(32.dp) // Tamaño del botón ajustado
            ) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowUp,
                    contentDescription = "Incrementar",
                    modifier = Modifier.size(24.dp)
                )
            }

            IconButton(
                onClick = {
                    val newValue = (value - saltoTiempo).coerceAtLeast(valorMinimo)
                    onValueChange(newValue)
                },
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = "Decrementar",
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DificultadChips(
    dificultadSeleccionada: String,
    onDificultadSeleccionada: (String) -> Unit
) {
    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
    ) {
        ConstantesObjetos.Recetas_Dificultad.values().forEach { dificultad ->
            AssistChip(
                onClick = { onDificultadSeleccionada(dificultad.label) },
                label = {
                    Text(
                        text = dificultad.label,
                        color = if (dificultad.label == dificultadSeleccionada) Colores.Blanco else Colores.Negro
                    )
                },
                colors = AssistChipDefaults.assistChipColors(
                    containerColor = if (dificultad.label == dificultadSeleccionada) Colores.VerdeOscuro.copy(alpha = 0.9f) else Colores.VerdeOscuro.copy(alpha = 0.2f)
                ),
                modifier = Modifier
                    .padding(vertical = 6.dp, horizontal = 4.dp)
                    .wrapContentWidth(Alignment.CenterHorizontally) // Asegura que los chips se centren
                    .height(48.dp),
                shape = MaterialTheme.shapes.medium.copy(CornerSize(20.dp)) // Bordes más redondeados
            )
        }
    }
}

@Composable
fun BtnGuardarReceta(
    onClick: () -> Unit,
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .imePadding()
            .fillMaxWidth()
            .height(50.dp),
        shape = RoundedCornerShape(10.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Colores.MarronOscuro.copy(alpha = 0.8f), // Mantén el mismo color pero con 0.9 de opacidad
            contentColor = MaterialTheme.colorScheme.onPrimary
        )
    ) {
            Text(
                text = "Guardar receta", // El texto del botón
                fontSize = ConstanteTexto.TextoSemigrande,
                fontWeight = FontWeight.SemiBold,
            )
    }
}
@Composable
fun TextoCaracteresRestantes(
    caracteresRestantes: Int,
    maxLength: Int
) {
    val warningThreshold = (maxLength * 0.15).toInt()

    Text(
        text = "$caracteresRestantes",
        color = when {
            caracteresRestantes <= warningThreshold -> Color.Red
            else -> Color.Gray
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 4.dp),
        textAlign = TextAlign.End
    )
}

