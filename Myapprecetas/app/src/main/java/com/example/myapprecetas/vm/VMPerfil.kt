package com.example.myapprecetas.vm

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapprecetas.api.Endpoints
import com.example.myapprecetas.objetos.dto.DTORecetaSimplificada
import com.example.myapprecetas.repositories.IngredienteRepository
import com.example.myapprecetas.userauth.AuthManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject
import java.text.SimpleDateFormat
import java.util.Date

@HiltViewModel
class VMPerfil @Inject constructor(
    private val endpoints: Endpoints,
    private val repository: IngredienteRepository
) : ViewModel() {


    //region Atributos y propiedades

    private val _listaRecetas = MutableStateFlow<List<DTORecetaSimplificada>>(emptyList())
    val listaRecetas: StateFlow<List<DTORecetaSimplificada>> = _listaRecetas

    private val _idUsuario = MutableStateFlow(0)
    val idUsuario: StateFlow<Int?> = _idUsuario

    private val _nombreUsuario = MutableStateFlow<String?>("Usuario")
    val nombreUsuario: StateFlow<String?> = _nombreUsuario

    private val _cargando = MutableStateFlow(false)
    val cargando: StateFlow<Boolean> = _cargando

    private val _cargandoUsuario = MutableStateFlow(false)
    private val _email = MutableStateFlow<String?>("")
    val email: StateFlow<String?> = _email

    private val _imagenPerfil = MutableStateFlow<String?>(null)
    val imagenPerfil: StateFlow<String?> = _imagenPerfil

    private val _fechaString = MutableStateFlow<String?>("")
    val fechaString: StateFlow<String?> = _fechaString

//endregion

    //region Init

    init {
        _nombreUsuario.value = AuthManager.currentUser.value?.displayName ?: "Usuario"
        _email.value = AuthManager.currentUser.value?.email ?: ""
        _imagenPerfil.value = (AuthManager.currentUser.value?.photoUrl ?: "").toString()
        cargaUsuario()
        formatearFecha()
    }

    //endregion

    //region Funciones de actualización

    /**
     * Borra los ingredientes almacenados en el repositorio.
     */
    fun clearIngredientes() {
        repository.clearIngredientes()
    }

    //endregion

    //region Funciones
    /**
     * Formatea la fecha de creación del usuario y la guarda como texto.
     */
    private fun formatearFecha() {
        val fechaCreacion = AuthManager.currentUser.value?.metadata?.creationTimestamp
        val date = fechaCreacion?.let { Date(it) }
        val dateFormat = SimpleDateFormat("MMMM yyyy", Locale("es", "ES"))
        _fechaString.value = "Miembro desde: ${date?.let { dateFormat.format(it) }}"
    }

    //endregion

    //region Llamadas API

    /**
     * Carga los datos del usuario
     */
    private fun cargaUsuario() {
        val uid = AuthManager.currentUser.value?.uid ?: return

        viewModelScope.launch {
            _cargandoUsuario.value = true
            try {
                val response = endpoints.getUsuarioUID(uid)
                if (response.isSuccessful) {
                    response.body()?.let { usuario ->
                        _idUsuario.value = usuario.idUsuario
                        cargaRecetas()
                        Log.i(":::OKAY", "Usuario cargado: $usuario")
                    } ?: run {
                        Log.i("RESPUESTA VACIA", "No se encontró usuario para UID $uid")
                    }
                } else {
                    Log.i("ERROR SERVIDOR", "Error al cargar usuario: ${response.code()} - ${response.message()}")
                }
            } catch (e: Exception) {
                Log.i("EXCEPTION", "Error al cargar usuario: ${e.message}")
            } finally {
                _cargandoUsuario.value = false
            }
        }
    }

    /**
     * Carga las recetas asociadas al ID de usuario.
     */
    private fun cargaRecetas() {
        viewModelScope.launch {
            _cargando.value = true
            try {
                val response = endpoints.getRecetasPorIdUsuario(_idUsuario.value)
                if (response.isSuccessful) {
                    response.body()?.let {
                        _listaRecetas.value = it
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
    }

    /**
     * Borra una receta del backend y la elimina de la lista local si el borrado fue exitoso.
     * @param idReceta ID de la receta a eliminar
     */
    fun borrarReceta(idReceta: Int) {
        val uid = AuthManager.currentUser.value?.uid ?: return

        viewModelScope.launch {
            try {
                val response = endpoints.borrarReceta(uid, idReceta)
                if (response.isSuccessful) {
                    Log.d(":::Correcto", _listaRecetas.value.toString())
                    _listaRecetas.update { recetas -> recetas.filter { it.idReceta != idReceta } }
                    Log.d(":::Correcto", "Borrado correctamente")
                    Log.d(":::Correcto", _listaRecetas.value.toString())
                } else {
                    val errorMessage = response.errorBody()?.string()
                    Log.d(":::Error", "Mensaje: $errorMessage")
                }
            } catch (e: Exception) {
                Log.d(":::Error", "Excepción: ${e.localizedMessage}")
            }
        }
    }

    //endregion
}
