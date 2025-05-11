package com.example.myapprecetas.vm

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapprecetas.api.Endpoints
import com.example.myapprecetas.objetos.dto.Ingrediente
import com.example.myapprecetas.objetos.dto.creacion.DTOCategoriaSimplificada
import com.example.myapprecetas.objetos.dto.creacion.DTOIngredienteSimplificado
import com.example.myapprecetas.objetos.dto.creacion.DTONuevaReceta
import com.example.myapprecetas.objetos.dto.creacion.DTOPasoSimplificado
import com.example.myapprecetas.repositories.CloudinaryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class VMCreacionReceta @Inject constructor(
    private val repo: CloudinaryRepository,
    private val endpoints: Endpoints
) : ViewModel() {

    private val _nombreReceta = MutableStateFlow("")
    private val _descripcion = MutableStateFlow("")
    private val _tiempoPreparacion = MutableStateFlow("")
    private val _fotoReceta = MutableStateFlow("")
    private val _dificultad = MutableStateFlow("")
    private val _categorias = MutableStateFlow<List<DTOCategoriaSimplificada>>(emptyList())
    private val _ingredientes = MutableStateFlow<List<DTOIngredienteSimplificado>>(emptyList())
    private val _ingredientesSeleccionados = MutableStateFlow<List<Ingrediente>>(emptyList())
    private val _pasos = MutableStateFlow<List<DTOPasoSimplificado>>(emptyList())
    private val _cargando = MutableStateFlow(false)
    private val _cargandoImagen = MutableStateFlow(false)
    private val _cargandoIngredientes = MutableStateFlow(false)
    private val _uploadError = MutableStateFlow<String?>(null)
    private var _urlImagen: Uri? = null
    private val _listadoIngredientes = MutableStateFlow<List<Ingrediente>>(emptyList())

    val nombreReceta: StateFlow<String> = _nombreReceta
    val descripcion: StateFlow<String> = _descripcion
    val tiempoPreparacion: StateFlow<String> = _tiempoPreparacion
    val fotoReceta: StateFlow<String> = _fotoReceta
    val dificultad: StateFlow<String> = _dificultad
    val categorias: StateFlow<List<DTOCategoriaSimplificada>> = _categorias
    val ingredientes: StateFlow<List<DTOIngredienteSimplificado>> = _ingredientes
    val ingredientesSeleccionados: StateFlow<List<Ingrediente>> = _ingredientesSeleccionados
    val pasos: StateFlow<List<DTOPasoSimplificado>> = _pasos
    val cargando: StateFlow<Boolean> = _cargando
    val cargandoImagen: StateFlow<Boolean> = _cargandoImagen
    val cargandoIngredientes: StateFlow<Boolean> = _cargandoIngredientes
    val uploadError: StateFlow<String?> = _uploadError
    val urlImagen: Uri? get() = _urlImagen
    val listadoIngredientes: StateFlow<List<Ingrediente>> = _listadoIngredientes

    init{
    }

    //region actualizar

    /**
     * Actualiza el nombre de la receta
     */
    fun actualizarNombre(nombre: String) {
        _nombreReceta.value = nombre
    }

    fun actualizarDescripcion(descripcion: String) {
        _descripcion.value = descripcion
    }

    fun actualizarTiempo(tiempo: String) {
        _tiempoPreparacion.value = tiempo
    }

    fun actualizarDificultad(dificultad: String) {
        _dificultad.value = dificultad
    }

    fun actualizarCategorias(lista: List<DTOCategoriaSimplificada>) {
        _categorias.value = lista
    }

    fun actualizarIngredientesSeleccionados(lista: List<Ingrediente>) {
        _ingredientesSeleccionados.value = lista
    }

    fun addIngredienteSeleccionado(nuevoIngrediente: Ingrediente) {
        val ingredienteExistente = _ingredientesSeleccionados.value.find { it.idIngrediente == nuevoIngrediente.idIngrediente }

        if (ingredienteExistente == null) {
            _ingredientesSeleccionados.value += nuevoIngrediente
        }
    }

    fun deleteIngredienteSeleccionado(borrarIngrediente: Ingrediente) {
            _ingredientesSeleccionados.value -= borrarIngrediente
    }

    fun actualizarIngredientes(lista: List<Ingrediente>) {
        _listadoIngredientes.value = lista
    }

    fun actualizarPasos(lista: List<DTOPasoSimplificado>) {
        _pasos.value = lista
    }

    fun actualizarFoto(uriString: String) {
        _urlImagen = Uri.parse(uriString)
    }
    fun actualizarCantidadIngrediente(idIngrediente: Int, nuevaCantidad: Int) {
        val listaActualizada = _ingredientesSeleccionados.value.map { ingrediente ->
            if (ingrediente.idIngrediente == idIngrediente) {
                ingrediente.copy(cantidad = nuevaCantidad)
            } else {
                ingrediente
            }
        }
        _ingredientesSeleccionados.value = listaActualizada
    }
    //endregion

    //region Metodos
    /**
     * Actualiza el URI de la imagen seleccionada
     */

    /**
     * Sube la imagen a Cloudinary
     */
    fun subirImagen() {
        _urlImagen?.let { uri ->
            _cargandoImagen.value = true
            _uploadError.value = null
            _fotoReceta.value = ""

            repo.uploadImage(
                imageUri = uri,
                onLoading = { _cargandoImagen.value = true },
                onSuccess = { url ->
                    _fotoReceta.value = url
                    _cargandoImagen.value = false
                },
                onError = { error ->
                    _uploadError.value = error
                    _cargandoImagen.value = false
                }
            )
        }
    }

    /**
     * Elimina la imagen seleccionada y resetea los estados relacionados
     */
    fun eliminarImagen() {
        _urlImagen = null
        _fotoReceta.value = ""
        _uploadError.value = null
    }



    /**
     * Crea una nueva receta con los datos actuales
     */
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

                Log.i(":::CREACION_RECETA", "Nueva receta creada: $nuevaReceta")

            } catch (e: Exception) {
                Log.e(":::CREACION_RECETA", "Error al crear receta: ${e.message}")
            } finally {
                _cargando.value = false
            }
        }
    }

    fun buscarIngredientes(busquedaNombre: String) {
        viewModelScope.launch {
            _cargandoIngredientes.value = true
            try {
                // Llamamos al endpoint de búsqueda de ingredientes
                val response = endpoints.buscarIngredientes(busquedaNombre)

                if (response.isSuccessful) {
                    response.body()?.let {
                        _listadoIngredientes.value = it // Actualizamos la lista de ingredientes
                        Log.i("OKAY", "Ingredientes encontrados: ${it.size}")
                    } ?: run {
                        Log.i("RESPUESTA VACIA", "La respuesta de búsqueda está vacía.")
                    }
                } else {
                    Log.i("ERROR SERVIDOR", "Error en la respuesta del servidor: ${response.message()}")
                }
            } catch (e: Exception) {
                Log.i("EXCEPTION", "Error al buscar ingredientes: $e")
            } finally {
                _cargandoIngredientes.value = false
            }
        }
    }


    //endregion
}
