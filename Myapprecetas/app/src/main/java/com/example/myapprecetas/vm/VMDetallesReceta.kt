package com.example.myapprecetas.vm

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapprecetas.api.Endpoints
import com.example.myapprecetas.dto.DTORecetaDetallada
import com.example.myapprecetas.dto.Ingrediente
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.roundToInt

@HiltViewModel
class VMDetallesReceta @Inject constructor(
    private val endpoints: Endpoints
) : ViewModel() {

    private var _recetaDetalles = MutableStateFlow<DTORecetaDetallada?>(null)
    var recetaDetalles: StateFlow<DTORecetaDetallada?> = _recetaDetalles

    private var _cargando = MutableStateFlow<Boolean>(false)
    var cargando: StateFlow<Boolean> = _cargando

    private var _porciones = MutableStateFlow(2)
    val porciones: StateFlow<Int> = _porciones

    private var _idReceta: Int? = null

    init {
        cargaRecetas()
    }

    fun setRecetaId(idReceta: Int) {
        _idReceta = idReceta
        cargaRecetas()
    }

    private fun cargaRecetas() {
        // Verificamos si _idReceta es null antes de hacer la solicitud
        _idReceta?.let { id ->
            viewModelScope.launch {
                _cargando.value = true
                try {
                    val response = endpoints.getRecetaPorId(id)
                    if (response.isSuccessful) {
                        response.body()?.let {
                            _recetaDetalles.value = it
                            Log.i("OKAY", response.body().toString())
                        } ?: run {
                            Log.i("RESPUESTA VACIA", response.body().toString())
                        }
                    } else {
                        Log.i("ERROR SERVIDOR", response.body().toString())
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

    fun AumentaPorciones() {
        ajustarCantidadesIngredientes(true)
        _porciones.value = when (_porciones.value) {
            2 -> 4
            4 -> 8
            8 -> 8
            else -> 2
        }

    }

    fun DisminuyePorciones() {
        ajustarCantidadesIngredientes(false)
        _porciones.value = when (_porciones.value) {
            8 -> 4
            4 -> 2
            2 -> 2
            else -> 2
        }
    }

    fun ajustarCantidadesIngredientes(aumento: Boolean) {

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
}
