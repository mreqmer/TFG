package com.example.myapprecetas.views.creacionreceta

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.myapprecetas.vm.VMCreacionReceta
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImagePainter.State.Empty.painter
import com.example.myapprecetas.R
import com.example.myapprecetas.ui.theme.Colores
import com.example.myapprecetas.ui.theme.common.ConstanteIcono
import com.example.myapprecetas.ui.theme.common.HeaderAtras

@Composable
fun AddRecetaView(vm: VMCreacionReceta, navController: NavHostController) {
    // Layout de la pantalla, con el padding para el contenido
    Scaffold(
        containerColor = Colores.Blanco,
        topBar = {
            HeaderAtras(texto = "Crear Receta", navController = navController)

        },
        content = { innerPadding ->
            // Llamar al AddRecetaScreen con el ViewModel y padding
            AddRecetaScreen(vm = vm, innerPadding = innerPadding)
        }
    )
}

@Composable
fun AddRecetaScreen(vm: VMCreacionReceta, innerPadding: PaddingValues) {
    val ingredientesBusqueda by vm.listadoIngredientes.collectAsState()
    val ingredientesSeleccionados by vm.ingredientesSeleccionados.collectAsState()
    val cargandoIngredientes by vm.cargandoIngredientes.collectAsState()

    var busqueda by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .padding(horizontal = 16.dp)
    ) {
        // Buscador
        OutlinedTextField(
            value = busqueda,
            onValueChange = {
                busqueda = it
                if (busqueda.isNotBlank()) {
                    vm.buscarIngredientes(busqueda)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            label = { Text("Buscar ingredientes...") },
            singleLine = true,
            trailingIcon = {
                if (busqueda.isNotEmpty()) {
                    IconButton(onClick = {
                        busqueda = ""
                        vm.actualizarIngredientes(listOf())
                    }) {
                        Icon(painter = painterResource(R.drawable.cerrar), contentDescription = "Limpiar búsqueda",modifier = Modifier.size(ConstanteIcono.IconoNormal))
                    }
                }
            }
        )

        // Resultados de la búsqueda
        if (cargandoIngredientes) {
            Text("Buscando ingredientes...", modifier = Modifier.padding(16.dp))
        } else if (ingredientesBusqueda.isNotEmpty()) {
            LazyColumn {
                items(ingredientesBusqueda) { ingrediente ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = ingrediente.nombreIngrediente,
                            modifier = Modifier.weight(1f)
                        )

                        IconButton(
                            onClick = {
                                val nuevoIngrediente = ingrediente.copy(cantidad = 1, notas = "")

                                // Actualizar la lista de ingredientes seleccionados
                                vm.addIngredienteSeleccionado(nuevoIngrediente)

                                // Limpiar el campo de búsqueda y la lista de ingredientes de búsqueda
                                busqueda = ""
                                vm.actualizarIngredientes(listOf())  // Limpiar la lista de búsqueda
                            }
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.mas),
                                contentDescription = "Añadir Ingrediente",
                                modifier = Modifier.size(ConstanteIcono.IconoNormal)
                            )
                        }
                    }
                }
            }
        } else if (busqueda.isNotEmpty()) {
            Text(
                text = "No se encontraron ingredientes",
                modifier = Modifier.padding(16.dp),
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Ingredientes seleccionados
        Text(
            text = "Ingredientes seleccionados:",
            fontWeight = FontWeight.SemiBold,
            fontSize = 18.sp,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        if (ingredientesSeleccionados.isEmpty()) {
            Text(
                text = "No hay ingredientes seleccionados.",
                modifier = Modifier.padding(vertical = 8.dp)
            )
        } else {
            LazyColumn {
                items(ingredientesSeleccionados) { ingrediente ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = ingrediente.nombreIngrediente,
                            modifier = Modifier.weight(1f)
                        )

                        OutlinedTextField(
                            value = ingrediente.cantidad.toString(),
                            onValueChange = { cantidadStr ->
                                val cantidad = cantidadStr.toIntOrNull() ?: 1

                                // Llama a la función del VM para actualizar la cantidad
                                vm.actualizarCantidadIngrediente(ingrediente.idIngrediente, cantidad)

                                // Limpiar búsqueda y resultados de búsqueda
                                busqueda = ""
                                vm.actualizarIngredientes(listOf())
                            },
                            modifier = Modifier.width(80.dp),
                            label = { Text("Cantidad") },
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        IconButton(
                            {vm.deleteIngredienteSeleccionado(ingrediente)}
                        ) {
                            Icon(painter = painterResource(R.drawable.papelera), contentDescription = "Eliminar Ingrediente", modifier = Modifier.size(
                                ConstanteIcono.IconoNormal))
                        }
                    }
                }
            }
        }
    }
}

