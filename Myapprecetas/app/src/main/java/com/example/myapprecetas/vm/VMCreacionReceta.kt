package com.example.myapprecetas.vm

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapprecetas.dto.creacion.DTOCategoriaSimplificada
import com.example.myapprecetas.dto.creacion.DTOIngredienteSimplificado
import com.example.myapprecetas.dto.creacion.DTONuevaReceta
import com.example.myapprecetas.dto.creacion.DTOPasoSimplificado
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class VMCreacionReceta @Inject constructor() : ViewModel() {

    private val _nombreReceta = MutableStateFlow("")
    private val _descripcion = MutableStateFlow("")
    private val _tiempoPreparacion = MutableStateFlow("")
    private val _fotoReceta = MutableStateFlow("")
    private val _dificultad = MutableStateFlow("")
    private val _categorias = MutableStateFlow<List<DTOCategoriaSimplificada>>(emptyList())
    private val _ingredientes = MutableStateFlow<List<DTOIngredienteSimplificado>>(emptyList())
    private val _pasos = MutableStateFlow<List<DTOPasoSimplificado>>(emptyList())
    private val _cargando = MutableStateFlow(false)

    val nombreReceta: StateFlow<String> = _nombreReceta
    val descripcion: StateFlow<String> = _descripcion
    val tiempoPreparacion: StateFlow<String> = _tiempoPreparacion
    val fotoReceta: StateFlow<String> = _fotoReceta
    val dificultad: StateFlow<String> = _dificultad
    val categorias: StateFlow<List<DTOCategoriaSimplificada>> = _categorias
    val ingredientes: StateFlow<List<DTOIngredienteSimplificado>> = _ingredientes
    val pasos: StateFlow<List<DTOPasoSimplificado>> = _pasos
    val cargando: StateFlow<Boolean> = _cargando

    fun actualizarNombre(nombre: String) {
        _nombreReceta.value = nombre
    }

    fun actualizarDescripcion(descripcion: String) {
        _descripcion.value = descripcion
    }

    fun actualizarTiempo(tiempo: String) {
        _tiempoPreparacion.value = tiempo
    }

    fun actualizarFoto(url: String) {
        _fotoReceta.value = url
    }

    fun actualizarDificultad(dificultad: String) {
        _dificultad.value = dificultad
    }

    fun actualizarCategorias(lista: List<DTOCategoriaSimplificada>) {
        _categorias.value = lista
    }

    fun actualizarIngredientes(lista: List<DTOIngredienteSimplificado>) {
        _ingredientes.value = lista
    }

    fun actualizarPasos(lista: List<DTOPasoSimplificado>) {
        _pasos.value = lista
    }

    fun crearReceta(idCreador: Int) {
        viewModelScope.launch {
            _cargando.value = true
            try {
                val nuevaReceta = DTONuevaReceta(
                    idReceta = 0,
                    idCreador = idCreador,
                    nombreReceta = nombreReceta.value,
                    descripcion = descripcion.value,
                    tiempoPreparacion = tiempoPreparacion.value.toIntOrNull() ?: 0,
                    dificultad = dificultad.value,
                    fechaCreacion = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                    fotoReceta = fotoReceta.value,
                    categorias = categorias.value,
                    ingredientes = ingredientes.value,
                    pasos = pasos.value
                )

                Log.i(":::PRUEBA", "Nueva receta creada: $nuevaReceta")

            } catch (e: Exception) {
                Log.e(":::PRUEBA", "Error al crear receta: ${e.message}")
            } finally {
                _cargando.value = false
            }
        }
    }
}
