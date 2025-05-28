package com.example.myapprecetas.views.listadoreceta

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.myapprecetas.R
import com.example.myapprecetas.ui.theme.Colores
import com.example.myapprecetas.ui.theme.common.ConstanteIcono
import com.example.myapprecetas.ui.theme.common.ConstanteTexto
import com.example.myapprecetas.vm.VMListadoReceta

@Composable
fun DrawerContent(
    vm: VMListadoReceta,
    insets: PaddingValues
) {

    val categorias by vm.categorias.collectAsState()

    val filtroCategoria by vm.filtroSeleccionadoCategoria.collectAsState()
    val filtroTiempo by vm.filtroSeleccionadoTiempo.collectAsState()
    val filtroDificultad by vm.filtroSeleccionadoDificultad.collectAsState()

    ModalDrawerSheet(
        drawerContainerColor = Color.White,
        modifier = Modifier
            .fillMaxHeight()
            .width(270.dp)
            .padding(bottom = insets.calculateBottomPadding() + 12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    "Filtrar recetas",
                    fontFamily = fuenteTexto,
                    fontWeight = FontWeight.Bold,
                    fontSize = ConstanteTexto.TextoSemigrande,
                    color = Color.Black,
                )
                Spacer(Modifier.height(16.dp))

                ReestableceFiltro({ vm.reestableceFiltro() })

                FiltroIngrediente(vm)

                FiltroFlowRow(
                    titulo = "Tiempo",
                    items = vm.tiempos,
                    seleccion = filtroTiempo,
                    onSeleccionChange = { vm.setFiltroTiempo(it) },
                )

                FiltroFlowRow(
                    titulo = "Dificultad",
                    items = vm.dificultades,
                    seleccion = filtroDificultad,
                    onSeleccionChange = { vm.setFiltroDificultad(it) },
                )

                FiltroFlowRow(
                    titulo = "Categoría",
                    items = categorias,
                    seleccion = filtroCategoria,
                    onSeleccionChange = { vm.setFiltroCategoria(it) },
                )
            }

            Spacer(Modifier.height(12.dp))

            Button(
                onClick = { vm.buscarRecetas() },
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(y = 50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Colores.MarronOscuro.copy(alpha = 0.9f),
                    contentColor = Colores.Blanco
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "Aplicar filtros",
                    fontWeight = FontWeight.Bold,
                    fontFamily = fuenteTexto,
                    fontSize = ConstanteTexto.TextoNormal
                )
            }
        }
    }
}


@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FiltroFlowRow(
    titulo: String,
    items: List<String?>,
    seleccion: String?,
    onSeleccionChange: (String?) -> Unit,

) {
    Text(
        titulo,
        fontFamily = fuenteTexto,
        fontWeight = FontWeight.Bold,
        fontSize = ConstanteTexto.TextoSemigrande,
        color = Colores.Negro
    )
    Spacer(Modifier.height(8.dp))



    Spacer(Modifier.height(8.dp))

    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        items.forEach { item ->
            FilterChip(
                selected = item == seleccion,
                onClick = {
                    if (item != null) {
                        if (item == seleccion) {
                            onSeleccionChange(null)
                        } else {
                            onSeleccionChange(item)
                        }
                    }
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = Colores.VerdeClaro,
                ),
                label = {
                    if (item != null) {
                        Text(
                            item,
                            fontFamily = fuenteTexto,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = ConstanteTexto.TextoNormal,
                            color = Colores.Negro
                        )
                    }
                }
            )
            Spacer(Modifier.width(8.dp))
        }
    }
    Spacer(Modifier.height(16.dp))
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FiltroIngrediente(vm: VMListadoReceta) {
    val ingredientesSeleccionados by vm.ingredientesSeleccionados.collectAsState()
    val listadoIngredientes by vm.listadoIngredientes.collectAsState()
    var textoBusqueda by remember { mutableStateOf("") }

    Text(
        "Ingredientes",
        fontFamily = fuenteTexto,
        fontWeight = FontWeight.Bold,
        fontSize = ConstanteTexto.TextoSemigrande,
        color = Colores.Negro
    )
    Spacer(Modifier.height(8.dp))

    OutlinedTextField(
        value = textoBusqueda,
        onValueChange = {
            textoBusqueda = it
            vm.buscarIngredientes(it)
        },
        label = { Text("Buscar ingrediente") },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true
    )

    Spacer(Modifier.height(8.dp))

    listadoIngredientes.take(5).forEach { ingrediente ->
        Button(
            onClick = {
                vm.agregarIngredienteSeleccionado(ingrediente)
                textoBusqueda = ""
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 2.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Colores.MarronOscuro.copy(alpha = 0.1f)
            )
        ) {
            Text(
                text = ingrediente.nombreIngrediente,
                fontFamily = fuenteTexto,
                fontWeight = FontWeight.SemiBold,
                fontSize = ConstanteTexto.TextoNormal,
                color = Colores.Negro
            )
        }
    }

    Spacer(Modifier.height(12.dp))

    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        ingredientesSeleccionados.forEach { ingrediente ->
            AssistChip(
                modifier = Modifier.padding(end = 8.dp),
                onClick = { vm.quitarIngredienteSeleccionado(ingrediente) },
                label = {
                    Text(
                        ingrediente.nombreIngrediente,
                        fontFamily = fuenteTexto,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = ConstanteTexto.TextoSemigrande,
                        color = Colores.Negro
                    )
                },
                leadingIcon = {
                    Icon(
                        painter = painterResource(R.drawable.cerrar),
                        contentDescription = "Eliminar ingrediente",
                        Modifier.size(ConstanteIcono.IconoPequeno)
                    )
                },
                colors = AssistChipDefaults.assistChipColors(
                    containerColor = Colores.VerdeClaro.copy(alpha = 0.4f)
                )
            )
        }
    }
}

@Composable
fun ReestableceFiltro(onClick: () -> Unit) {
    AssistChip(
        onClick = onClick,
        label = {
            Text(
                "Restablecer filtros",
                fontFamily = fuenteTexto,
                fontWeight = FontWeight.Medium,
                fontSize = ConstanteTexto.TextoNormal
            )
        },
        leadingIcon = {
            Icon(
                painter = painterResource(R.drawable.cerrar), // Usa un icono de reinicio si tienes
                contentDescription = "Restablecer filtros",
                modifier = Modifier.size(ConstanteIcono.IconoPequeno)
            )
        },
        colors = AssistChipDefaults.assistChipColors(
            containerColor = Colores.Gris.copy(alpha = 0.2f),
            labelColor = Colores.Negro
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp)
    )
}
