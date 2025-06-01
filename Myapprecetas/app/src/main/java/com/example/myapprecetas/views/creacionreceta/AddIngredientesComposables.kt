package com.example.myapprecetas.views.creacionreceta

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.myapprecetas.R
import com.example.myapprecetas.objetos.dto.Ingrediente
import com.example.myapprecetas.ui.theme.Colores
import com.example.myapprecetas.ui.theme.FamilyQuicksand
import com.example.myapprecetas.ui.theme.common.ConstanteIcono
import com.example.myapprecetas.ui.theme.common.ConstanteTexto
import com.example.myapprecetas.vm.VMCreacionReceta

/**
 * Campo de búsqueda de ingredientes
  */
@Composable
fun BusquedaIngredientes(
    vm: VMCreacionReceta,
    busqueda: String,
    focusRequester: FocusRequester,
) {
    OutlinedTextField(
        value = busqueda,
        onValueChange = { vm.actualizarBusqueda(it) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp)
            .focusRequester(focusRequester),
        label = { Text("Buscar ingredientes...") },
        singleLine = true,
        trailingIcon = {
            if (busqueda.isNotEmpty()) {
                IconButton(onClick = { vm.limpiarBusqueda() }) {
                    Icon(
                        painter = painterResource(R.drawable.cerrar),
                        contentDescription = "Limpiar búsqueda",
                        modifier = Modifier.size(ConstanteIcono.IconoNormal))
                }
            }
        }
    )
}

/**
 * Lista de resultados de búsqueda
  */
@Composable
fun ListaResultados(
    ingredientes: List<Ingrediente>,
    vm: VMCreacionReceta,
    focusManager: FocusManager
) {
    LazyColumn {
        //resultados de la búsqueda
        items(ingredientes) { ingrediente ->
            ItemListaResultados(ingrediente, vm, focusManager)
        }
    }
}

/**
 * Cartas individuales de los ingredientes de la búsqueda
 */
@Composable
private fun ItemListaResultados(
    ingrediente: Ingrediente,
    vm: VMCreacionReceta,
    focusManager: FocusManager
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = ingrediente.nombreIngrediente,
            modifier = Modifier.weight(1f),
            fontFamily = FamilyQuicksand.quicksand
        )

        IconButton(
            onClick = {
                val nuevoIngrediente = ingrediente.copy(cantidad = 1, notas = "")
                vm.addIngredienteSeleccionado(nuevoIngrediente)
                vm.addIngrediente(nuevoIngrediente)
                vm.limpiarBusqueda()
                focusManager.clearFocus()
            }
        ) {
            Icon(
                painter = painterResource(R.drawable.mas),
                contentDescription = "Añadir Ingrediente",
                modifier = Modifier.size(ConstanteIcono.IconoNormal))
        }
    }
}

/**
 * Sección de ingredientes seleccionados, si no hay muestra mensaje
 */
@Composable
fun IngredientesSeleccionados(
    ingredientesSeleccionados: List<Ingrediente>,
    ingredienteEnEdicion: Int?,
    onEditIngredient: (Int) -> Unit,
    onSaveIngredient: () -> Unit,
    vm: VMCreacionReceta
) {
    Column {
        Text(
            text = "Ingredientes seleccionados:",
            fontWeight = FontWeight.SemiBold,
            fontSize = 18.sp,
            modifier = Modifier.padding(vertical = 8.dp),
            fontFamily = FamilyQuicksand.quicksand,
        )
        //Si encuentra louestra, si no mensaje
        if (ingredientesSeleccionados.isEmpty()) {
            TextosSinIngredientes()
        } else {
            ListaIngredientesSeleccionados(
                ingredientesSeleccionados,
                ingredienteEnEdicion,
                onEditIngredient,
                onSaveIngredient,
                vm
            )
        }
    }
}

/*
* Lista con los ingredientes seleccionados
 */
@Composable
private fun ListaIngredientesSeleccionados(
    ingredientes: List<Ingrediente>,
    ingredienteEnEdicion: Int?,
    onEditIngredient: (Int) -> Unit,
    onSaveIngredient: () -> Unit,
    vm: VMCreacionReceta
) {
    LazyColumn {
        items(ingredientes, key = { it.idIngrediente }) { ingrediente ->
            ItemIngredienteSeleccionado(
                ingrediente = ingrediente,
                enEdicion = ingredienteEnEdicion == ingrediente.idIngrediente,
                onEdit = { onEditIngredient(ingrediente.idIngrediente) },
                onSave = onSaveIngredient,
                onDelete = {
                    vm.deleteIngredienteSeleccionado(ingrediente.idIngrediente)
                    vm.removeIngrediente(ingrediente.idIngrediente)
                },
                vm = vm
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

/**
 * Tarjeta con el ingrediente seleccionado
  */
@Composable
private fun ItemIngredienteSeleccionado(
    ingrediente: Ingrediente,
    enEdicion: Boolean,
    onEdit: () -> Unit,
    onSave: () -> Unit,
    onDelete: () -> Unit,
    vm: VMCreacionReceta
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        colors = CardDefaults.cardColors(
            containerColor = Colores.VerdeOscuro.copy(alpha = 0.05f)
        ),
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .border(
                width = 1.dp,
                color = Colores.Gris.copy(alpha = 0.1f),
                shape = RoundedCornerShape(16.dp)
            )
    ) {
        Column(modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)) {
            IngredienteHeader(
                ingrediente = ingrediente,
                enEdicion = enEdicion,
                onEdit = onEdit,
                onSave = onSave,
                onDelete = onDelete,
                vm = vm
            )

            //notas adicionales para los ingredientes
            if (enEdicion) {
                CampoNotas(ingrediente, vm)
            } else if (ingrediente.notas.isNotEmpty()) {
                IngredeinteNotas(ingrediente.notas)
            }
        }
    }
}

/**
 * Contenedor del ingrediente, con los botones de editar y eliminar
 */
@Composable
private fun IngredienteHeader(
    ingrediente: Ingrediente,
    enEdicion: Boolean,
    onEdit: () -> Unit,
    onSave: () -> Unit,
    onDelete: () -> Unit,
    vm: VMCreacionReceta
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(start = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = ingrediente.nombreIngrediente,
            modifier = Modifier.weight(1f),
            fontFamily = FamilyQuicksand.quicksand,
            fontWeight = FontWeight.SemiBold
        )

        if (enEdicion) {
            EditaCantidad(ingrediente, vm)
            IconoGuardarEdit(onSave = onSave)
        } else {
            CantidadIngrediente(ingrediente)
            IconosIngredientes(onEdit, onDelete)
        }
    }
}

/**
 * Edita la cantidad de un ingrediente
 */
@Composable
private fun EditaCantidad(ingrediente: Ingrediente, vm: VMCreacionReceta) {
    OutlinedTextField(
        value = ingrediente.cantidad.toString(),
        onValueChange = { nuevaCantidad ->
            val cantidadInt = nuevaCantidad.toIntOrNull() ?: ingrediente.cantidad
            vm.actualizarCantidadIngrediente(ingrediente.idIngrediente, cantidadInt)
        },
        modifier = Modifier.width(80.dp),
        singleLine = true
    )
}

/**
 * Muestra cantidad y medida de cada ingrediente
 */
@Composable
private fun CantidadIngrediente(ingrediente: Ingrediente) {
    Text(
        text = "${ingrediente.cantidad} ${ingrediente.medida}",
        fontFamily = FamilyQuicksand.quicksand,
        modifier = Modifier.padding(end = 8.dp)
    )
}

/**
 * ButtonIcon de los botones editar/eliminar
 */
@Composable
private fun IconosIngredientes(onEdit: () -> Unit, onDelete: () -> Unit) {
    Row {
        IconButton(onClick = onEdit) {
            Icon(
                painter = painterResource(R.drawable.edit),
                contentDescription = "Editar",
                modifier = Modifier.size(ConstanteIcono.IconoNormal))
        }
        IconButton(onClick = onDelete) {
            Icon(
                painter = painterResource(R.drawable.papelera),
                contentDescription = "Eliminar",
                modifier = Modifier.size(ConstanteIcono.IconoNormal))
        }
    }
}

/**
 * Campo editable notas del ingrediente
 */
@Composable
private fun CampoNotas(ingrediente: Ingrediente, vm: VMCreacionReceta) {
    OutlinedTextField(
        value = ingrediente.notas,
        onValueChange = { vm.actualizarNotaIngrediente(ingrediente.idIngrediente, it) },
        label = { Text("Notas") },
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 8.dp, end = 8.dp),
        singleLine = true
    )
}

/**
 * Notas del ingrediente, si tiene
 */
@Composable
private fun IngredeinteNotas(notas: String) {
    Text(
        text = "Notas: $notas",
        fontFamily = FamilyQuicksand.quicksand,
        fontSize = ConstanteTexto.TextoPequeno,
        modifier = Modifier.padding(start = 8.dp, top = 4.dp)
    )
}

/**
 * ButtonIcon para guardar los campos de un ingrediente
 */
@Composable
private fun IconoGuardarEdit(onSave: () -> Unit) {
    IconButton(onClick = onSave) {
        Icon(
            painter = painterResource(R.drawable.check),
            contentDescription = "Guardar",
            modifier = Modifier.size(ConstanteIcono.IconoNormal))
    }
}

/**
 * Botón para guardar los ingrediente añadidos y volver para seguir editando receta
 */
@Composable
fun BotonGuardar(vm: VMCreacionReceta, navController: NavHostController, ingredientes: List<Ingrediente>) {
    Button(
        onClick = {
            vm.actualizarIngredientesOriginales(ingredientes)
            navController.popBackStack()
        },
        colors = ButtonDefaults.buttonColors(
            containerColor = Colores.VerdeOscuro,
            contentColor = Colores.Blanco
        ),
        shape = RoundedCornerShape(4.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp)
    ) {
        Text(
            text = "Guardar y volver",
            fontFamily = FamilyQuicksand.quicksand,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
        )
    }
}

/**
 * Texto para cuando se está buscando ingredientes
 */
@Composable
fun TextoCarga() {
    Text(
        text = "Buscando ingredientes...",
        fontFamily = FamilyQuicksand.quicksand,
        modifier = Modifier.padding(16.dp)
    )
}

/**
 * Texto para cuando no hay ingredientes
 */
@Composable
private fun TextosSinIngredientes() {
    Text(
        text = "No hay ingredientes seleccionados.",
        fontFamily = FamilyQuicksand.quicksand,
        modifier = Modifier.padding(vertical = 8.dp)
    )
}