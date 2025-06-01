package com.example.myapprecetas.views.creacionreceta

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.myapprecetas.vm.VMCreacionReceta
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.navigation.NavHostController
import com.example.myapprecetas.ui.theme.Colores
import com.example.myapprecetas.ui.theme.common.HeaderAtras

/*
 *Vista principal para añadir ingredientes a una receta
 */
@Composable
fun AddIngredienteView(vm: VMCreacionReceta, navController: NavHostController) {
    Scaffold(
        containerColor = Colores.Blanco,
        topBar = {
            // Header con botón de retroceso
            HeaderAtras(texto = "Crear Receta", navController = navController)
        },
        content = { innerPadding ->
            // Contenido principal de la pantalla
            AddIngredienteScreen(vm, innerPadding, navController)
        }
    )
}

/*
 *Contenido principal de la pantalla de añadir ingredientes
 */
@Composable
private fun AddIngredienteScreen(
    vm: VMCreacionReceta,
    innerPadding: PaddingValues,
    navController: NavHostController
) {
    // Estados vm
    val ingredientesBusqueda by vm.listadoIngredientes.collectAsState()
    val ingredientesSeleccionados by vm.ingredientesSeleccionados.collectAsState()
    val cargandoIngredientes by vm.cargandoIngredientes.collectAsState()
    val busqueda by vm.busqueda.collectAsState()

    // Estado local para controlar edición de ingredientes
    var ingredienteEnEdicion by remember { mutableStateOf<Int?>(null) }
    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }

    // Layout principal
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .padding(horizontal = 16.dp)
    ) {
        // Campo de búsqueda de ingredientes
        BusquedaIngredientes(
            vm = vm,
            busqueda = busqueda,
            focusRequester = focusRequester,
        )

        // Muestra estado de carga o resultados
        if (cargandoIngredientes) {
            TextoCarga()
        } else if (ingredientesBusqueda.isNotEmpty()) {
            ListaResultados(ingredientesBusqueda, vm, focusManager)
        }

        // Sección de ingredientes seleccionados
        IngredientesSeleccionados(
            ingredientesSeleccionados = ingredientesSeleccionados,
            ingredienteEnEdicion = ingredienteEnEdicion,
            onEditIngredient = { ingredienteEnEdicion = it },
            onSaveIngredient = { ingredienteEnEdicion = null },
            vm = vm
        )

        // Botón final para guardar cambios
        BotonGuardar(vm, navController, ingredientesSeleccionados)
    }
}

