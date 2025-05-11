package com.example.myapprecetas.views.creacionreceta

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import androidx.navigation.NavHostController
import com.example.myapprecetas.objetos.ClsIngrediente
import com.example.myapprecetas.ui.theme.Colores
import com.example.myapprecetas.ui.theme.common.HeaderAtras
import com.example.myapprecetas.vm.VMCreacionReceta

@Composable
fun CrearRecetaView(vm: VMCreacionReceta, navController: NavHostController) {
    Scaffold(
        containerColor = Colores.Blanco,
        topBar = {
            HeaderAtras(texto = "Crear Receta", navController = navController)
        }
    ) { innerPadding ->
        CreacionRecetaScreen(vm = vm, navController = navController, innerPadding = innerPadding)
    }
}

@Composable
fun CreacionRecetaScreen(
    vm: VMCreacionReceta,
    navController: NavHostController,
    innerPadding: PaddingValues
) {
    // Declaración de las variables al inicio
    var titulo by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var tiempoPreparacion by remember { mutableIntStateOf(10) }
    var pasos = remember { mutableStateListOf("") }
    var dificultadSeleccionada by remember { mutableStateOf("") }
    var ingredientesBusqueda by remember { mutableStateOf("") }
    var categoriasBusqueda by remember { mutableStateOf("") }
    var ingredientesSeleccionados = remember { mutableStateListOf<ClsIngrediente>() }

    LazyColumn(
        modifier = Modifier
            .padding(innerPadding)
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.Start
    ) {
        item {
            // Espaciado y Título
            Spacer(modifier = Modifier.height(32.dp))

            SeccionTitulo("Título")
            InputField(
                value = titulo,
                onValueChange = { titulo = it },
                placeholder = "Escribe el título",
                maxLength = 80,
                maxLines = 4,
                minSize = 50.dp
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Descripción
            SeccionTitulo("Descripción")
            InputField(
                value = descripcion,
                onValueChange = { descripcion = it },
                placeholder = "Escribe la descripción",
                maxLength = 300,
                maxLines = 5,
                minSize = 80.dp
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Foto
            SeccionTitulo("Foto")
            FotoSelector(vm)

            Spacer(modifier = Modifier.height(16.dp))

            // Ingredientes
            SeccionTitulo("Ingredientes")
            BotonAddIngrediente(
                onClick = { navController.navigate("addIngrediente") }
            )

            Spacer(modifier = Modifier.height(8.dp))

            ListaIngredientesSeleccionados(ingredientesSeleccionados)

            Spacer(modifier = Modifier.height(16.dp))

            // Pasos
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

            // Dificultad
            SeccionTitulo("Dificultad")
            DificultadChips(
                dificultadSeleccionada = dificultadSeleccionada,
                onDificultadSeleccionada = { nuevaDificultad ->
                    dificultadSeleccionada = nuevaDificultad
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Tiempo de preparación
            SeccionTitulo("Tiempo de preparación")


            TiempoPreparacionField(
                value = tiempoPreparacion,
                onValueChange = { nuevoTiempo -> tiempoPreparacion = nuevoTiempo }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Categorías
            SeccionTitulo("Categorías")
            InputFielddddd(
                value = categoriasBusqueda,
                onValueChange = { categoriasBusqueda = it },
                placeholder = "Buscar categorías..."
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Botón para guardar receta
            BtnGuardarReceta(
                onClick = { /* Implementar acción al guardar la receta */ },
            )

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}







