package com.example.myapprecetas.views.creacionreceta

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.myapprecetas.R
import com.example.myapprecetas.objetos.dto.Ingrediente
import com.example.myapprecetas.objetos.dto.constantesobjetos.ConstantesObjetos
import com.example.myapprecetas.ui.theme.Colores
import com.example.myapprecetas.ui.theme.FamilyQuicksand
import com.example.myapprecetas.ui.theme.common.ConstanteIcono
import com.example.myapprecetas.ui.theme.common.ConstanteTexto
import com.example.myapprecetas.vm.VMCreacionReceta

/**
 * Título de cada sección en la interfaz
 */
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

/**
 * Campo de entrada de texto con longitud y tamaño definidos
 */
@Composable
fun InputField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    maxLength: Int,
    maxLines: Int,
    minSize: Dp
) {
    val caracteresRestantes = maxLength - value.length

    Column(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = value,
            onValueChange = {
                if (it.length <= maxLength) onValueChange(it)
            },
            placeholder = {
                Text(
                    text = placeholder,
                    fontFamily = FamilyQuicksand.quicksand,
                    color = Colores.Gris
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = minSize)
                .background(Color.White, shape = MaterialTheme.shapes.medium)
                .border(1.dp, Colores.Gris, MaterialTheme.shapes.medium),
            shape = MaterialTheme.shapes.medium,
            textStyle = LocalTextStyle.current.copy(
                color = Color.Black,
                fontFamily = FamilyQuicksand.quicksand
            ),
            maxLines = maxLines,
        )
         //Carácteres restantes
        TextoCaracteresRestantes(
            caracteresRestantes,
            maxLength
        )
    }
}

/**
 * Botón que navega a la vista para añadir un nuevo ingrediente
 */
@Composable
fun BotonAddIngrediente(onClick: () -> Unit) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp),
        shape = RoundedCornerShape(10.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = Colores.VerdeOscuro.copy(alpha = 0.2f),
            contentColor = Colores.Negro
        )
    ) {

        Icon(
            modifier = Modifier
                .size(20.dp),
            painter = painterResource(R.drawable.mas),
            contentDescription = "Añadir ingrediente")
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = "Añadir ingrediente",fontFamily = FamilyQuicksand.quicksand)
    }
}

/**
 *  Muestra la lista de ingredientes seleccionados en la receta
 */
@Composable
fun ListaIngredientesSeleccionados(
    ingredientesSeleccionados: List<Ingrediente>,
    onEliminarIngrediente: (Ingrediente) -> Unit,
) {
    if (ingredientesSeleccionados.isEmpty()) {
        Text("No hay ingredientes seleccionados.", fontFamily = FamilyQuicksand.quicksand)
    } else {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .heightIn(min = 50.dp, max = 2000.dp)
                    .padding(bottom = 8.dp)
            ) {
                items(ingredientesSeleccionados) { ingrediente ->
                    //Lista de ingredientes
                    IngredienteItem(
                        ingrediente = ingrediente,
                        onEliminarIngrediente = onEliminarIngrediente
                    )
                }
            }
        }
    }
}

/**
 * Muestra un solo ingrediente dentro de una card
 */
@Composable
fun IngredienteItem(
    ingrediente: Ingrediente,
    onEliminarIngrediente: (Ingrediente) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .border(
                width = 1.dp,
                color = Colores.Gris.copy(alpha = 0.2f),
                shape = RoundedCornerShape(12.dp)
            )
            .padding(12.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Nombre, cantidad y medida
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = ingrediente.nombreIngrediente,
                        fontSize = ConstanteTexto.TextoNormal,
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = FamilyQuicksand.quicksand,
                        color = Color.Black
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Cantidad: ${ingrediente.cantidad}${ingrediente.medida}",
                        fontSize = ConstanteTexto.TextoPequeno,
                        fontFamily = FamilyQuicksand.quicksand,
                        color = Color.Gray
                    )
                }

                IconButton(
                    onClick = { onEliminarIngrediente(ingrediente) },
                    modifier = Modifier.size(ConstanteIcono.IconoPequeno)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.papelera),
                        contentDescription = "Eliminar ingrediente",
                        tint = Color.Gray
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Notas: ${ingrediente.notas}",
                fontSize = ConstanteTexto.TextoPequeno,
                fontFamily = FamilyQuicksand.quicksand,
                color = Color.Gray
            )
        }
    }
}

/**
 * Campo para escribir un paso con opción para eliminarlo
 */
@Composable
fun PasoItem(index: Int, paso: String, onValueChange: (String) -> Unit, onDelete: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(end = 8.dp)
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
                    fontFamily = FamilyQuicksand.quicksand,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(13.dp))

            Icon(
                painter = painterResource(R.drawable.papelera),
                contentDescription = "Eliminar paso",
                tint = Colores.Gris,
                modifier = Modifier
                    .size(20.dp)
                    .clickable {
                        onDelete()
                    }
            )
        }

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

/**
 * Boton para añadir un nuevo paso
 */

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

/**
 * Campo para introducir el tiempo de preparacion
 */
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

        OutlinedTextField(
            value = value.toString(),
            onValueChange = { newValue ->
                newValue.toIntOrNull()?.let {
                    onValueChange(it.coerceIn(valorMinimo, valorMaximo))
                }
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done),
            modifier = Modifier.width(70.dp)
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy((-4).dp),
            modifier = Modifier.padding(start = 8.dp)
        ) {
            IconButton(
                onClick = {
                    val newValue = (value + saltoTiempo).coerceAtMost(valorMaximo)
                    onValueChange(newValue)

                },
                modifier = Modifier.size(32.dp)
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

/**
 * Chips para seleccionar la dificultad de la receta
 */
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
        ConstantesObjetos.Recetas_Dificultad.entries.forEach { dificultad ->
            AssistChip(
                onClick = { onDificultadSeleccionada(dificultad.label) },
                label = {
                    Text(
                        text = dificultad.label,
                        color = if (dificultad.label == dificultadSeleccionada) Colores.Blanco else Colores.Negro
                    )
                },
                colors = AssistChipDefaults.assistChipColors(
                    containerColor = if (dificultad.label == dificultadSeleccionada) Colores.MarronOscuro.copy(alpha = 0.9f) else Colores.MarronOscuro.copy(alpha = 0.2f)
                ),
                modifier = Modifier
                    .padding(vertical = 6.dp, horizontal = 4.dp)
                    .wrapContentWidth(Alignment.CenterHorizontally)
                    .height(48.dp),
                shape = MaterialTheme.shapes.medium.copy(CornerSize(20.dp))
            )
        }
    }
}

/**
 * Selector de categorías con chips para seleccionar hasta 5
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CategoriasSelector(vm: VMCreacionReceta) {
    val categorias by vm.categorias.collectAsState()
    val seleccionadas by vm.categoriasSeleccionadas.collectAsState()

    Column {
        Text("Categorías (${seleccionadas.size}/5)",
            style = MaterialTheme.typography.titleSmall)

        FlowRow(

            verticalArrangement = Arrangement.Center
        ) {
            categorias.forEach { categoria ->
                val estaSeleccionada = seleccionadas.any { it.idCategoria == categoria.idCategoria }

                AssistChip(
                    onClick = { vm.toggleCategoria(categoria) },
                    label = { Text(categoria.nombreCategoria) },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = if (estaSeleccionada)
                            Colores.VerdeOscuro.copy(alpha = 0.9f)
                        else
                            Colores.VerdeOscuro.copy(alpha = 0.2f)
                    ),
                    modifier = Modifier
                        .padding(vertical = 3.dp, horizontal = 2.dp)
                        .wrapContentWidth(Alignment.CenterHorizontally)
                        .height(40.dp),
                    shape = MaterialTheme.shapes.medium.copy(CornerSize(20.dp))
                )
            }
        }
    }
}

/**
 * Botón para guardar y crear una nueva receta
 */
@Composable
fun BtnGuardarReceta(
    isLoading: Boolean,
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
            containerColor = Colores.MarronOscuro.copy(alpha = 0.8f),
            contentColor = MaterialTheme.colorScheme.onPrimary
        )
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                color = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.size(24.dp)
            )
        } else {
            Text(
                text = "Guardar receta",
                fontSize = ConstanteTexto.TextoSemigrande,
                fontWeight = FontWeight.SemiBold,
            )
        }
    }
}

/**
 * Selecciona una foto para subirla
 */
@Composable
fun FotoSelector(
    imagenUri: Uri?,
    onUpdateFoto: (Uri?) -> Unit
) {
    val galeriaLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            onUpdateFoto(uri)
        }
    )

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start
        ) {
            Box(
                modifier = Modifier
                    .width(100.dp)
                    .height(100.dp)
                    .border(1.dp, Colores.Gris, RoundedCornerShape(10.dp))
                    .background(Colores.MarronClaro.copy(alpha = 0.6f), shape = RoundedCornerShape(10.dp))
                    .clickable { galeriaLauncher.launch("image/*") },
                contentAlignment = Alignment.Center
            ) {
                if (imagenUri != null) {
                    Image(
                        painter = rememberAsyncImagePainter(imagenUri),
                        contentDescription = "Vista previa de la imagen",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(10.dp))
                    )
                } else {
                    Icon(
                        painter = painterResource(R.drawable.mas2),
                        contentDescription = "Añadir imagen",
                        tint = Colores.Negro,
                        modifier = Modifier.size(ConstanteIcono.IconoNormal)
                    )
                }
            }

            if (imagenUri != null) {
                Icon(
                    painter = painterResource(R.drawable.papelera),
                    contentDescription = "Eliminar imagen",
                    tint = Colores.Gris,
                    modifier = Modifier
                        .size(38.dp)
                        .padding(start = 16.dp)
                        .clickable { onUpdateFoto(null) }
                )
            }
        }
    }
}

/**
 * Muestra los carácteres restantes para introducir texto
 */
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
