package com.example.myapprecetas.repositories

import com.example.myapprecetas.objetos.dto.Ingrediente
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class IngredienteRepository @Inject constructor() {

    // StateFlow para almacenar los ingredientes seleccionados
    private val _ingredientesSeleccionados = MutableStateFlow<List<Ingrediente>>(emptyList())
    val ingredientesSeleccionados: StateFlow<List<Ingrediente>> = _ingredientesSeleccionados

    // Añadir ingrediente a la lista
    fun addIngrediente(ingrediente: Ingrediente) {
        if (_ingredientesSeleccionados.value.none { it.idIngrediente == ingrediente.idIngrediente }) {
            _ingredientesSeleccionados.value = _ingredientesSeleccionados.value + ingrediente
        }
    }

    // Eliminar ingrediente de la lista
    fun removeIngrediente(idIngrediente: Int) {
        _ingredientesSeleccionados.value = _ingredientesSeleccionados.value.filter { it.idIngrediente != idIngrediente }
    }

    // Actualizar la lista completa de ingredientes
    fun updateIngredientes(ingredientes: List<Ingrediente>): List<Ingrediente> {
        val ingredientesActualizados = _ingredientesSeleccionados.value.toMutableList()

        // Reemplazar cada ingrediente que coincida en la lista
        ingredientes.forEach { ingrediente ->
            val index = ingredientesActualizados.indexOfFirst { it.idIngrediente == ingrediente.idIngrediente }
            if (index != -1) {
                // Actualiza el ingrediente con los nuevos valores
                ingredientesActualizados[index] = ingrediente
            }
        }

        // Actualiza la lista en el ViewModel
        _ingredientesSeleccionados.value = ingredientesActualizados

        return _ingredientesSeleccionados.value
    }

    fun getIngredientes(): List<Ingrediente> {
        return _ingredientesSeleccionados.value
    }

    fun clearIngredientes() {
        _ingredientesSeleccionados.value = emptyList()
    }
}