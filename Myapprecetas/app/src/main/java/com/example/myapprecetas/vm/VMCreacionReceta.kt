package com.example.myapprecetas.vm

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapprecetas.api.Endpoints
import com.example.myapprecetas.objetos.dto.Categoria
import com.example.myapprecetas.objetos.dto.Ingrediente
import com.example.myapprecetas.objetos.dto.creacion.DTOCategoriaSimplificada
import com.example.myapprecetas.objetos.dto.creacion.DTOIngredienteSimplificado
import com.example.myapprecetas.objetos.dto.creacion.DTONuevaReceta
import com.example.myapprecetas.objetos.dto.creacion.DTOPasoSimplificado
import com.example.myapprecetas.repositories.CloudinaryRepository
import com.example.myapprecetas.repositories.IngredienteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class VMCreacionReceta @Inject constructor(
    private val repo: CloudinaryRepository,
    private val endpoints: Endpoints,
    private val ingredienteRepository: IngredienteRepository,
) : ViewModel() {

    //region Propiedades/Atributos
    // Datos principales de la receta
    private val _nombreReceta = MutableStateFlow("")
    val nombreReceta: StateFlow<String> = _nombreReceta

    private val _descripcion = MutableStateFlow("")
    val descripcion: StateFlow<String> = _descripcion

    private val _fotoReceta = MutableStateFlow("")
    val fotoReceta: StateFlow<String> = _fotoReceta

    private val _publicId = MutableStateFlow<String?>("")

    private val _tiempoPreparacion = MutableStateFlow("")
    val tiempoPreparacion: StateFlow<String> = _tiempoPreparacion

    private val _dificultad = MutableStateFlow("")
    val dificultad: StateFlow<String> = _dificultad

    // Categorías
    private val _categorias = MutableStateFlow<List<Categoria>>(emptyList())
    val categorias: StateFlow<List<Categoria>> = _categorias

    private val _categoriasSeleccionadas = MutableStateFlow<List<Categoria>>(emptyList())
    val categoriasSeleccionadas: StateFlow<List<Categoria>> = _categoriasSeleccionadas

    // Ingredientes y pasos
    private val _ingredientes = MutableStateFlow<List<DTOIngredienteSimplificado>>(emptyList())
    val ingredientes: StateFlow<List<DTOIngredienteSimplificado>> = _ingredientes

    private val _ingredientesSeleccionados = MutableStateFlow<List<Ingrediente>>(emptyList())
    val ingredientesSeleccionados: StateFlow<List<Ingrediente>> = _ingredientesSeleccionados

    private val _listadoIngredientes = MutableStateFlow<List<Ingrediente>>(emptyList())
    val listadoIngredientes: StateFlow<List<Ingrediente>> = _listadoIngredientes

    private val _pasos = MutableStateFlow<List<DTOPasoSimplificado>>(emptyList())
    val pasos: StateFlow<List<DTOPasoSimplificado>> = _pasos

    // Estados de carga
    private val _cargando = MutableStateFlow(false)
    val cargando: StateFlow<Boolean> = _cargando

    private val _cargandoImagen = MutableStateFlow(false)

    private val _cargandoIngredientes = MutableStateFlow(false)
    val cargandoIngredientes: StateFlow<Boolean> = _cargandoIngredientes

    // Manejo de imágenes y errores relacionados
    private val _imagenUri = MutableStateFlow<Uri?>(null)
    val imagenUri: StateFlow<Uri?> = _imagenUri

    private val _errorImagen = MutableStateFlow("")

    private val _uploadError = MutableStateFlow<String?>("")

    // Otros
    private val _busqueda = MutableStateFlow("")
    val busqueda: StateFlow<String> = _busqueda

    private val _idUsuario = MutableStateFlow(-1)
    //endregion

    //region init
    init{
        obtenerCategorias()
    }
    //endregion

    //region actualizar

    /**
     * Establece el ID del usuario si no es nulo.
     */
    fun setIdUsuario(id: Int?) {
        if (id != null) {
            _idUsuario.value = id
        }
    }

    /**
     * Actualiza el nombre de la receta.
     */
    fun actualizarNombre(nombre: String) {
        _nombreReceta.value = nombre
    }

    /**
     * Actualiza la descripción de la receta.
     */
    fun actualizarDescripcion(descripcion: String) {
        _descripcion.value = descripcion
    }

    /**
     * Actualiza el tiempo de preparación como texto.
     */
    fun actualizarTiempo(tiempo: String) {
        _tiempoPreparacion.value = tiempo
    }

    /**
     * Actualiza la dificultad de la receta.
     */
    fun actualizarDificultad(dificultad: String) {
        _dificultad.value = dificultad
    }

    /**
     * Agrega o quita una categoría seleccionada.
     * No permite más de 5 categorías seleccionadas.
     */
    fun toggleCategoria(categoria: Categoria) {
        _categoriasSeleccionadas.value = _categoriasSeleccionadas.value.toMutableList().apply {
            val existe = any { it.idCategoria == categoria.idCategoria }
            if (existe) {
                removeAll { it.idCategoria == categoria.idCategoria }
            } else {
                if (size < 5) add(categoria)
            }
        }
    }

    /**
     * Actualiza la URI de la imagen seleccionada.
     */
    fun actualizarFoto(uri: Uri?) {
        _imagenUri.value = uri
    }

    /**
     * Agrega un ingrediente al repositorio.
     */
    fun addIngrediente(ingrediente: Ingrediente) {
        ingredienteRepository.addIngrediente(ingrediente)
    }

    /**
     * Elimina un ingrediente del repositorio por su ID.
     */
    fun removeIngrediente(idIngrediente: Int) {
        ingredienteRepository.removeIngrediente(idIngrediente)
    }

    /**
     * Agrega un ingrediente a la lista de seleccionados si no existe ya.
     */
    fun addIngredienteSeleccionado(nuevoIngrediente: Ingrediente) {
        val ingredienteExistente = _ingredientesSeleccionados.value.find { it.idIngrediente == nuevoIngrediente.idIngrediente }

        if (ingredienteExistente == null) {
            _ingredientesSeleccionados.value += nuevoIngrediente
        }
    }

    /**
     * Elimina un ingrediente de la lista de seleccionados por su ID.
     */
    fun deleteIngredienteSeleccionado(idIngrediente: Int) {
        _ingredientesSeleccionados.value = _ingredientesSeleccionados.value.filter { it.idIngrediente != idIngrediente }
    }

    /**
     * Actualiza la lista completa de ingredientes seleccionados con los ingredientes originales.
     */
    fun actualizarIngredientesOriginales(ingrediente: List<Ingrediente>) {
        _ingredientesSeleccionados.value = ingredienteRepository.updateIngredientes(ingrediente)
    }

    /**
     * Actualiza la nota asociada a un ingrediente seleccionado.
     */
    fun actualizarNotaIngrediente(idIngrediente: Int, nuevaNota: String) {
        _ingredientesSeleccionados.value = _ingredientesSeleccionados.value.map { ingrediente ->
            if (ingrediente.idIngrediente == idIngrediente) {
                ingrediente.copy(notas = nuevaNota) // Actualiza la nota del ingrediente
            } else {
                ingrediente
            }
        }
    }

    /**
     * Actualiza la lista completa de pasos de la receta.
     */
    fun actualizarPasos(lista: List<DTOPasoSimplificado>) {
        _pasos.value = lista
    }

    /**
     * Agrega un nuevo paso vacío al final de la lista de pasos.
     */
    fun addPaso() {
        val nuevoOrden = _pasos.value.size + 1
        val nuevoPaso = DTOPasoSimplificado(
            orden = nuevoOrden,
            descripcion = ""
        )
        _pasos.value += nuevoPaso
    }

    /**
     * Actualiza la cantidad de un ingrediente seleccionado.
     */
    fun actualizarCantidadIngrediente(idIngrediente: Int, cantidad: Int) {
        _ingredientesSeleccionados.value = _ingredientesSeleccionados.value.map { ingrediente ->
            if (ingrediente.idIngrediente == idIngrediente) {
                ingrediente.copy(cantidad = cantidad)
            } else {
                ingrediente
            }
        }
    }

    /**
     * Actualiza el texto de búsqueda de ingredientes.
     * Si el texto está vacío, limpia la búsqueda.
     */
    fun actualizarBusqueda(query: String) {
        _busqueda.value = query
        if (query.isBlank()) {
            limpiarBusqueda()
        } else {
            buscarIngredientes(query)
        }
    }

    /**
     * Limpia el texto de búsqueda y la lista de ingredientes encontrados.
     */
    fun limpiarBusqueda() {
        _busqueda.value = ""
        _listadoIngredientes.value = emptyList()
    }

    //endregion

    //region Metodos

    /**
     * Convierte los ingredientes seleccionados a una lista lista de DTOs para enviarlos al backend
      */

    private fun convertirIngredientesADTO(): List<DTOIngredienteSimplificado> {
        return _ingredientesSeleccionados.value.map { ingrediente ->
            DTOIngredienteSimplificado(
                cantidad = ingrediente.cantidad,
                idIngrediente = ingrediente.idIngrediente,
                notas = ingrediente.notas
            )
        }
    }

    /**
     *  Convierte las categorías seleccionadas a DTOs con solo el ID
      */

    private fun convertiCategoriasADTO(): List<DTOCategoriaSimplificada> {
        return categoriasSeleccionadas.value.map { categoria ->
            DTOCategoriaSimplificada(
                idCategoria = categoria.idCategoria,
            )
        }
    }

    /**
     *  Crea una receta nueva con todos los datos
     *      Si hay imagen, primero la sube. Luego monta el objeto y lo envía al servidor.
     *      Si la subida falla, borra la imagen.
      */
    suspend fun crearReceta(): Boolean {
        _cargando.value = true

        return try {
            var imageUploaded = false

            // Subir imagen si hay una seleccionada
            _imagenUri.value?.let { uri ->
                subirImagen(uri)

                // Esperar a que termine la subida
                imageUploaded = _cargandoImagen
                    .drop(1)
                    .first { !it }

                // Si hubo error, cancelar
                if (_errorImagen.value.isEmpty()) {
                    Log.e("CREAR_RECETA", "Error subiendo imagen: ${_errorImagen.value}")
                    return false
                }
            }

            // Crear objeto receta con los datos
            val nuevaReceta = DTONuevaReceta(
                idReceta = 0,
                idCreador = _idUsuario.value,
                nombreReceta = _nombreReceta.value,
                descripcion = _descripcion.value,
                tiempoPreparacion = _tiempoPreparacion.value.toIntOrNull() ?: 0,
                dificultad = _dificultad.value,
                fechaCreacion = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                fotoReceta = _fotoReceta.value,
                categorias = convertiCategoriasADTO(),
                ingredientes = convertirIngredientesADTO(),
                pasos = _pasos.value
            )

            // Subir receta al servidor
            val recetaSubida = subirReceta(nuevaReceta)

            // Si falla y subimos imagen, borrarla
            if (!recetaSubida && imageUploaded) {
                borrarImagen()
                _fotoReceta.value = ""
            }

            recetaSubida
        } catch (e: Exception) {
            Log.e("CREAR_RECETA", "Error: ${e.message}")
            _uploadError.value = "Error al crear receta: ${e.message}"
            false
        } finally {
            _cargando.value = false
        }
    }

    /**
     * Obtiene los ingredientes desde el repositorio y los guarda en la lista local
      */

    fun obtieneTodo() {
        viewModelScope.launch {
            try {
                val ingredientes = ingredienteRepository.getIngredientes()
                _ingredientesSeleccionados.value = ingredientes
            } catch (e: Exception) {
                Log.e(":::LOG", "Error al obtener ingredientes: ${e.message}")
            }
        }
    }

//endregion

    //region llamadas API

    /**
     * Envia la receta al servidor y devuelve true si fue exitosa
      */

    private suspend fun subirReceta(nuevaReceta: DTONuevaReceta): Boolean {
        _cargando.value = true
        return try {
            val response = endpoints.subirNuevaReceta(nuevaReceta)

            if (response.isSuccessful) {
                Log.i("OKAY", "Receta subida correctamente: ${response.body()}")
                true
            } else {
                Log.i("ERROR SERVIDOR", "Error al subir receta: ${response.message()}")
                false
            }
        } catch (e: Exception) {
            Log.i("EXCEPTION", "Error al subir receta: $e")
            false
        } finally {
            _cargando.value = false
        }
    }

    /**
     *     Pide al servidor la lista de categorías y la guarda
      */
    private fun obtenerCategorias() {
        viewModelScope.launch {
            try {
                val response = endpoints.getCategorias()

                if (response.isSuccessful) {
                    _categorias.value = response.body() ?: emptyList()
                } else {
                    Log.e(":::API_CATEGORIAS", "No hay categorias")
                }
            } catch (e: Exception) {
                Log.e(":::API_CATEGORIAS", "Error: ${e.stackTraceToString()}")
            }
        }
    }

    /**
     *     Busca ingredientes en el servidor según el nombre ingresado
     */
    private fun buscarIngredientes(busquedaNombre: String) {
        viewModelScope.launch {
            _cargandoIngredientes.value = true
            try {
                val response = endpoints.buscarIngredientes(busquedaNombre)

                if (response.isSuccessful) {
                    response.body()?.let {
                        _listadoIngredientes.value = it
                        Log.i("OKAY", "Ingredientes encontrados: ${it.size}")
                    } ?: run {
                        Log.i("RESPUESTA VACIA", "No se encontraron ingredientes.")
                    }
                } else {
                    Log.i("ERROR SERVIDOR", "Error al buscar ingredientes: ${response.message()}")
                }
            } catch (e: Exception) {
                Log.i("EXCEPTION", "Error al buscar ingredientes: $e")
            } finally {
                _cargandoIngredientes.value = false
            }
        }
    }

    /**
     *     Sube una imagen al servidor y guarda su URL y ID si tiene éxito
     *
     */
    private fun subirImagen(uri: Uri) {
        _cargandoImagen.value = true
        _errorImagen.value = null.toString()
        _fotoReceta.value = ""
        _publicId.value = null

        repo.uploadImage(
            imageUri = uri,
            onLoading = {
                _cargandoImagen.value = true
            },
            onSuccess = { url, publicIdResult ->
                _fotoReceta.value = url
                _publicId.value = publicIdResult
                _cargandoImagen.value = false
            },
            onError = { error ->
                _errorImagen.value = error
                _cargandoImagen.value = false
            }
        )
    }

    /**
     *      Borra la imagen del servidor si tiene una guardada
     */
    private suspend fun borrarImagen() {
        val publicIdValue = _publicId.value

        if (publicIdValue.isNullOrEmpty()) {
            _errorImagen.value = "No hay una imagen para borrar"
            return
        }

        _cargandoImagen.value = true
        _errorImagen.value = null.toString()

        repo.deleteImage(
            publicId = publicIdValue,
            onSuccess = {
                _fotoReceta.value = ""
                _publicId.value = null
                _cargandoImagen.value = false
            },
            onError = { error ->
                _errorImagen.value = error
                _cargandoImagen.value = false
            }
        )
    }

//endregion
}
