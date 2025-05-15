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
import kotlinx.coroutines.flow.map
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

    private val _nombreReceta = MutableStateFlow("")
    private val _descripcion = MutableStateFlow("")
    private val _tiempoPreparacion = MutableStateFlow("")
    private val _fotoReceta = MutableStateFlow("")
    private val _dificultad = MutableStateFlow("")
    private val _categorias2 = MutableStateFlow<List<DTOCategoriaSimplificada>>(emptyList())
    private val _categorias = MutableStateFlow<List<Categoria>>(emptyList())
    private val _categoriasSeleccionadas = MutableStateFlow<List<Categoria>>(emptyList())
    private val _ingredientes = MutableStateFlow<List<DTOIngredienteSimplificado>>(emptyList())
    private val _ingredientesSeleccionados = MutableStateFlow<List<Ingrediente>>(emptyList())
    private val _pasos = MutableStateFlow<List<DTOPasoSimplificado>>(emptyList())
    private val _cargando = MutableStateFlow(false)
    private val _cargandoImagen = MutableStateFlow(false)
    private val _cargandoIngredientes = MutableStateFlow(false)
    private val _uploadError = MutableStateFlow<String?>(null)
    private var _urlImagen: Uri? = null
    private val _listadoIngredientes = MutableStateFlow<List<Ingrediente>>(emptyList())
    private val _imagenUri = MutableStateFlow<Uri?>(null)
    private val _busqueda = MutableStateFlow("")
    private val _errorImagen = MutableStateFlow("")
    private val _publicId = MutableStateFlow<String?>(null)

    val publicId: StateFlow<String?> = _publicId
    val nombreReceta: StateFlow<String> = _nombreReceta
    val descripcion: StateFlow<String> = _descripcion
    val tiempoPreparacion: StateFlow<String> = _tiempoPreparacion
    val fotoReceta: StateFlow<String> = _fotoReceta
    val dificultad: StateFlow<String> = _dificultad
    val categoriasSeleccionadas: StateFlow<List<Categoria>> = _categoriasSeleccionadas
    val categorias: StateFlow<List<Categoria>> = _categorias
    val categorias2: StateFlow<List<DTOCategoriaSimplificada>> = _categorias2
    val ingredientes: StateFlow<List<DTOIngredienteSimplificado>> = _ingredientes
    val ingredientesSeleccionados: StateFlow<List<Ingrediente>> = _ingredientesSeleccionados
    val pasos: StateFlow<List<DTOPasoSimplificado>> = _pasos
    val cargando: StateFlow<Boolean> = _cargando
    val cargandoImagen: StateFlow<Boolean> = _cargandoImagen
    val cargandoIngredientes: StateFlow<Boolean> = _cargandoIngredientes
    val uploadError: StateFlow<String?> = _uploadError
    val urlImagen: Uri? get() = _urlImagen
    val listadoIngredientes: StateFlow<List<Ingrediente>> = _listadoIngredientes
    val imagenUri: StateFlow<Uri?> = _imagenUri
    val busqueda: StateFlow<String> = _busqueda
    val errorImagen: StateFlow<String> = _errorImagen

    init{
        obtenerCategorias()
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

    private fun actualizarCategorias() {
        _categorias.value = _categoriasSeleccionadas.value.map {
            Categoria(it.idCategoria, it.nombreCategoria)
        }
    }

    fun actualizarFoto(uri: Uri?) {
        _imagenUri.value = uri
    }

    fun obtieneTodo(){
        viewModelScope.launch {
            try {
                val ingredientes = ingredienteRepository.getIngredientes()
                _ingredientesSeleccionados.value = ingredientes
            } catch (e: Exception) {
                Log.e(":::LOG", "Error al obtener ingredientes: ${e.message}")
            }
        }
    }

    fun addIngrediente(ingrediente: Ingrediente) {
        ingredienteRepository.addIngrediente(ingrediente)
    }

    fun removeIngrediente(idIngrediente: Int) {
        ingredienteRepository.removeIngrediente(idIngrediente)
    }

    fun addIngredienteSeleccionado(nuevoIngrediente: Ingrediente) {
        val ingredienteExistente = _ingredientesSeleccionados.value.find { it.idIngrediente == nuevoIngrediente.idIngrediente }

        if (ingredienteExistente == null) {
            _ingredientesSeleccionados.value = _ingredientesSeleccionados.value + nuevoIngrediente
        }
    }


    fun deleteIngredienteSeleccionado(idIngrediente: Int) {
        _ingredientesSeleccionados.value = _ingredientesSeleccionados.value.filter { it.idIngrediente != idIngrediente }
    }

    fun actualizarIngredientesOriginales(ingrediente:List<Ingrediente>) {
        _ingredientesSeleccionados.value = ingredienteRepository.updateIngredientes(ingrediente)
    }

    fun actualizarIngredientes(ingrediente: Ingrediente) {
        _ingredientesSeleccionados.value = _ingredientesSeleccionados.value.map { currentIngrediente ->
            if (currentIngrediente.idIngrediente == ingrediente.idIngrediente) {
                // Solo actualizamos el ingrediente que ha cambiado
                currentIngrediente.copy(cantidad = ingrediente.cantidad)
            } else {
                currentIngrediente // De lo contrario, dejamos el ingrediente sin cambios
            }
        }
    }

    fun actualizarNotaIngrediente(idIngrediente: Int, nuevaNota: String) {
        _ingredientesSeleccionados.value = _ingredientesSeleccionados.value.map { ingrediente ->
            if (ingrediente.idIngrediente == idIngrediente) {
                ingrediente.copy(notas = nuevaNota) // Actualiza la nota del ingrediente
            } else {
                ingrediente
            }
        }
    }

    fun actualizarPasos(lista: List<DTOPasoSimplificado>) {
        _pasos.value = lista
    }

    fun addPaso() {
        val nuevoOrden = _pasos.value.size + 1
        val nuevoPaso = DTOPasoSimplificado(
            orden = nuevoOrden,
            descripcion = ""
        )
        _pasos.value = _pasos.value + nuevoPaso
    }

    fun actualizarFoto(uriString: String) {
        _urlImagen = Uri.parse(uriString)
    }
    fun actualizarCantidadIngrediente(idIngrediente: Int, cantidad: Int) {
        _ingredientesSeleccionados.value = _ingredientesSeleccionados.value.map { ingrediente ->
            if (ingrediente.idIngrediente == idIngrediente) {
                ingrediente.copy(cantidad = cantidad)
            } else {
                ingrediente
            }
        }
    }

    fun actualizarBusqueda(query: String) {
        _busqueda.value = query
        if (query.isBlank()) {
            limpiarBusqueda()
        } else {
            buscarIngredientes(query)
        }
    }

    fun limpiarBusqueda() {
        _busqueda.value = ""
        _listadoIngredientes.value = emptyList()
    }
    //endregion

    //region Metodos


    suspend fun crearRecetaPrueba(idCreador: Int): Boolean {
        _cargando.value = true

        return try {
            // 1. Subir imagen primero si existe
            var imageUploaded = false
            _imagenUri.value?.let { uri ->
                subirImagen(uri)

                // Esperar a que termine la subida usando Flow
                imageUploaded = _cargandoImagen
                    .drop(1) // Ignorar el estado inicial
                    .first { !it } // Esperar hasta que cargandoImagen sea false

                if (_errorImagen.value.isNullOrEmpty()) {
                    Log.e("CREAR_RECETA", "Error subiendo imagen: ${_errorImagen.value}")
                    return false
                }
            }

            // 2. Crear objeto receta
            val nuevaReceta = DTONuevaReceta(
                idReceta = 0,
                idCreador = idCreador,
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

            // 3. Subir la receta
            val recetaSubida = subirReceta(nuevaReceta)

            // 4. Manejar fallo de subida de receta
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


    suspend fun subirReceta(nuevaReceta: DTONuevaReceta): Boolean {
        _cargando.value = true
        return try {
            val response = endpoints.subirNuevaReceta(nuevaReceta)

            if (response.isSuccessful) {
                Log.i("OKAY", "Receta subida correctamente: ${response.body()}")
                //                    _fotoReceta.value = ""
//                    _publicId.value = null
//                    _nombreReceta.value = ""
//                    _descripcion.value = ""
//                    _tiempoPreparacion.value = ""
//                    _dificultad.value = ""
//                    _categoriasSeleccionadas.value = emptyList()
//                    _ingredientesSeleccionados.value = emptyList()
//                    _pasos.value = emptyList()
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

    fun subirImagen(uri: Uri) {
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

    suspend fun borrarImagen() {
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



    fun convertirIngredientesADTO(): List<DTOIngredienteSimplificado> {
        return _ingredientesSeleccionados.value.map { ingrediente ->
            DTOIngredienteSimplificado(
                cantidad = ingrediente.cantidad,
                idIngrediente = ingrediente.idIngrediente,
                notas = ingrediente.notas
            )
        }
    }

    fun convertiCategoriasADTO(): List<DTOCategoriaSimplificada> {
        return categoriasSeleccionadas.value.map { categoria ->
            DTOCategoriaSimplificada(
                idCategoria = categoria.idCategoria,
            )
        }
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
                    categorias = convertiCategoriasADTO(),
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

    fun obtenerCategorias() {
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
