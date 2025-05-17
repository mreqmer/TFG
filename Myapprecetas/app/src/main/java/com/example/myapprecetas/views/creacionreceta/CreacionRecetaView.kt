package com.example.myapprecetas.views.creacionreceta

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.myapprecetas.ui.theme.Colores
import com.example.myapprecetas.ui.theme.common.HeaderAtras
import com.example.myapprecetas.vm.VMCreacionReceta
import kotlinx.coroutines.launch

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
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    vm.obtieneTodo()

    // estados del ViewModel
    val titulo by vm.nombreReceta.collectAsState()
    val descripcion by vm.descripcion.collectAsState()
    val tiempoPreparacion by vm.tiempoPreparacion.collectAsState()
    val dificultadSeleccionada by vm.dificultad.collectAsState()
    val ingredientesSeleccionados by vm.ingredientesSeleccionados.collectAsState()
    val categorias by vm.categorias.collectAsState()
    val categoriasSeleccionadas by vm.categoriasSeleccionadas.collectAsState()
    val pasos by vm.pasos.collectAsState()
    val imagenUri by vm.imagenUri.collectAsState()
    val cargando by vm.cargando.collectAsState()
    val cargandoImagen by vm.cargandoImagen.collectAsState()

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
            InputField(
                value = titulo,
                onValueChange = { vm.actualizarNombre(it) },
                placeholder = "Escribe el título",
                maxLength = 80,
                maxLines = 4,
                minSize = 50.dp
            )

            Spacer(modifier = Modifier.height(14.dp))

            SeccionTitulo("Descripción")
            InputField(
                value = descripcion,
                onValueChange = { vm.actualizarDescripcion(it) },
                placeholder = "Escribe la descripción",
                maxLength = 300,
                maxLines = 5,
                minSize = 80.dp
            )

            Spacer(modifier = Modifier.height(14.dp))

            SeccionTitulo("Foto")
            FotoSelector(
                imagenUri = imagenUri,
                onUpdateFoto = { uri -> vm.actualizarFoto(uri) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            SeccionTitulo("Ingredientes")
            BotonAddIngrediente(
                onClick = { navController.navigate("addIngrediente") }
            )

            Spacer(modifier = Modifier.height(8.dp))

            ListaIngredientesSeleccionados(
                ingredientesSeleccionados = ingredientesSeleccionados,
                onEliminarIngrediente = { ingrediente ->
                    vm.deleteIngredienteSeleccionado(ingrediente.idIngrediente)
                    vm.removeIngrediente(ingrediente.idIngrediente)
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            SeccionTitulo("Pasos")
            pasos.forEachIndexed { index, paso ->
                PasoItem(
                    index = index,
                    paso = paso.descripcion,
                    onValueChange = { nuevaDescripcion ->
                        val listaActualizada = pasos.toMutableList()
                        listaActualizada[index] = paso.copy(descripcion = nuevaDescripcion)
                        vm.actualizarPasos(listaActualizada)
                    },
                    onDelete = {
                        val listaActualizada = pasos.toMutableList()
                        listaActualizada.removeAt(index)
                        vm.actualizarPasos(listaActualizada)
                    }
                )
            }

            AddPaso(
                onClick = { vm.addPaso() }
            )

            Spacer(modifier = Modifier.height(16.dp))

            SeccionTitulo("Tiempo de preparación")
            TiempoPreparacionField(
                value = tiempoPreparacion.toIntOrNull() ?: 10,
                onValueChange = { vm.actualizarTiempo(it.toString()) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            SeccionTitulo("Dificultad")
            DificultadChips(
                dificultadSeleccionada = dificultadSeleccionada,
                onDificultadSeleccionada = { vm.actualizarDificultad(it) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            SeccionTitulo("Categorías")

            CategoriasSelector(vm = vm)

            Spacer(modifier = Modifier.height(32.dp))
            //TODO borrar
            var cosa by remember { mutableStateOf("") }
            BtnGuardarReceta(
                isLoading = cargando,
                onClick = {
                    coroutineScope.launch {
                        val recetaCreada = vm.crearRecetaPrueba(1)
                        if (recetaCreada) {
                            navController.navigate("perfil")
                        } else {
                            Toast.makeText(context, "Error en la creación de la receta", Toast.LENGTH_SHORT).show()
                            navController.navigate("perfil")
                        }
                    }
                }
            )

            Text(text = cosa)

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}



