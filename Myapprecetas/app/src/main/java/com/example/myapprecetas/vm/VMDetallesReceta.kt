package com.example.myapprecetas.vm

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapprecetas.api.Endpoints
import com.example.myapprecetas.objetos.dto.DTORecetaDetalladaLike
import com.example.myapprecetas.objetos.dto.DTOToggleLike
import com.example.myapprecetas.userauth.AuthManager.currentUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VMDetallesReceta @Inject constructor(
    private val endpoints: Endpoints
) : ViewModel() {

    //region Atributos y propiedades

    private var _recetaDetalles = MutableStateFlow<DTORecetaDetalladaLike?>(null)
    var recetaDetalles: StateFlow<DTORecetaDetalladaLike?> = _recetaDetalles

    private var _cargando = MutableStateFlow(false)
    var cargando: StateFlow<Boolean> = _cargando

    private var _porciones = MutableStateFlow(2)
    val porciones: StateFlow<Int> = _porciones

    private val _likeInProgress = MutableStateFlow(false)

    private var _idReceta: Int? = null

    //endregion

    //region Init

    init {
    }

    //endregion

    //region Actualización

    /**
     * Asigna el ID de la receta y lanza la carga de detalles desde la API.
     */
    fun setRecetaId(idReceta: Int) {
        _idReceta = idReceta
        cargaRecetas()
    }

    //endregion

    //region Llamadas a la API

    /**
     * Llama a la API para obtener los detalles de la receta junto con el estado de "like".
     */
    private fun cargaRecetas() {
        val uid = currentUser.value?.uid ?: run {
            Log.i("UID NULO", "El UID del usuario es nulo, no se puede realizar la carga.")
            return
        }

        _idReceta?.let { id ->
            viewModelScope.launch {
                _cargando.value = true
                try {
                    val request = DTOToggleLike(idReceta = id, uid = uid)
                    val response = endpoints.getRecetaDetalladaLike(request)

                    if (response.isSuccessful) {
                        response.body()?.let { recetaLike ->
                            _recetaDetalles.value = recetaLike
                            Log.i("OKAY", recetaLike.toString())
                        } ?: run {
                            Log.i("RESPUESTA VACIA", "Respuesta vacía")
                        }
                    } else {
                        Log.i("ERROR SERVIDOR", "Error: ${response.code()} - ${response.message()}")
                    }
                } catch (e: Exception) {
                    Log.i("EXCEPTION", e.toString())
                } finally {
                    _cargando.value = false
                }
            }
        } ?: run {
            Log.i("ID RECETA NULO", "El ID de la receta es nulo, no se puede realizar la carga.")
        }
    }

    /**
     * Llama a la API para alternar el estado de "like" de la receta.
     */
    fun toggleLike() {
        if (_likeInProgress.value || _recetaDetalles.value == null) return

        val uid = currentUser.value?.uid ?: return
        val idReceta = _recetaDetalles.value?.idReceta ?: return

        viewModelScope.launch {
            _likeInProgress.value = true
            try {
                val likeDto = DTOToggleLike(idReceta, uid)
                val response = endpoints.toggleLike(likeDto)

                if (response.isSuccessful) {
                    val respuesta = response.body()
                    if (respuesta != null && respuesta.success) {
                        val nuevoEstado = respuesta.likeActivo

                        // Actualizar el estado del like en el objeto de receta
                        _recetaDetalles.value = _recetaDetalles.value?.copy(
                            tieneLike = nuevoEstado
                        )

                        Log.i("ToggleLike", "Like actualizado a: $nuevoEstado")
                    } else {
                        Log.e("ToggleLike", "Respuesta nula o success = false")
                    }
                } else {
                    Log.e("ToggleLike", "Error servidor: ${response.code()} ${response.message()}")
                }
            } catch (e: Exception) {
                Log.e("ToggleLike", "Excepción: ${e.message}")
            } finally {
                _likeInProgress.value = false
            }
        }
    }
    //endregion

    //region Funciones auxiliares

    /**
     * Aumenta el número de porciones y ajusta las cantidades de ingredientes
     */
    fun aumentaPorciones() {
        ajustarCantidadesIngredientes(true)
        _porciones.value = when (_porciones.value) {
            2 -> 4
            4 -> 8
            8 -> 8
            else -> 2
        }

    }

    /**
     * Disminuye el número de porciones y ajusta las cantidades de ingredientes
     */
    fun disminuyePorciones() {
        ajustarCantidadesIngredientes(false)
        _porciones.value = when (_porciones.value) {
            8 -> 4
            4 -> 2
            2 -> 2
            else -> 2
        }
    }

    /**
     * Modifica las cantidades de los ingredientes dependiendo si se aumenta o disminuye porciones.
     * Solo permite duplicar o dividir por 2 hasta los límites definidos (2 y 8 porciones).
     */
    private fun ajustarCantidadesIngredientes(aumento: Boolean) {
        _recetaDetalles.value?.let { recetaDetallada ->
            val ingredientesActualizados = recetaDetallada.ingredientes.map { ingrediente ->
                ingrediente.copy(
                    cantidad =
                    if(aumento && porciones.value < 8){
                        (ingrediente.cantidad * 2)
                    }else if(!aumento && porciones.value > 2){
                        (ingrediente.cantidad / 2)
                    }else{
                        ingrediente.cantidad
                    }
                )
            }

            _recetaDetalles.value = recetaDetallada.copy(ingredientes = ingredientesActualizados)
        }
    }
    //endregion
}
