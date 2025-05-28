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

    private val _listaRecetas = MutableStateFlow<List<DTORecetaSimplificada>>(emptyList())
    private val _idUsuario = MutableStateFlow<Int>(0)
    private val _nombreUsuario = MutableStateFlow<String?>("Usuario")
    private var _cargando = MutableStateFlow<Boolean>(false)
    private var _cargandoUsuario = MutableStateFlow<Boolean>(false)
    private var _email = MutableStateFlow<String?>("")
    private val _imagenPerfil = MutableStateFlow<String?>(null)
    private val _fechaCreacion = MutableStateFlow<String?>("")
    private val _fechaString = MutableStateFlow<String?>("")

    val listaRecetas: StateFlow<List<DTORecetaSimplificada>> = _listaRecetas
    val idUsuario: StateFlow<Int?> = _idUsuario
    val nombreUsuario: StateFlow<String?> = _nombreUsuario
    var cargando: StateFlow<Boolean> = _cargando
    val email: StateFlow<String?> = _email
    val imagenPerfil: StateFlow<String?> = _imagenPerfil
    val fechaString: StateFlow<String?> = _fechaString


    init{
        _nombreUsuario.value = AuthManager.currentUser.value?.displayName ?: "Usuario"
        _email.value = AuthManager.currentUser.value?.email ?: ""
        _imagenPerfil.value = (AuthManager.currentUser.value?.photoUrl ?: "").toString()
        cargaUsuario()
        formatearFecha()
    }

    fun clearIngredientes() {
        repository.clearIngredientes()
    }

    fun eliminarRecetaLocal(idReceta: Int) {
        val nuevaLista = _listaRecetas.value.filterNot { it.idReceta == idReceta }
        _listaRecetas.value = nuevaLista

    }

    private fun cargaUsuario() {
        val uid = AuthManager.currentUser.value?.uid ?: return

        viewModelScope.launch {
            _cargandoUsuario.value = true
            try {
                val response = endpoints.getUsuarioUID(uid)
                if (response.isSuccessful) {
                    response.body()?.let { usuario ->
                        // Aquí actualizas los campos con el usuario obtenido
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

    private fun cargaRecetas() {
        viewModelScope.launch {
            _cargando.value = true
            try {
                val response = endpoints.getRecetasPorIdUsuario(_idUsuario.value)
                if (response.isSuccessful) {
                    response.body()?.let {
                        _listaRecetas.value = it
                        Log.i("OKAY", response.body().toString())
                        _cargando.value = false
                    } ?: run {
                        Log.i("RESPUESTA VACIA", response.body().toString())
                        _cargando.value = false
                    }
                } else {
                    Log.i("ERROR SERVIDOR", response.body().toString())
                    _cargando.value = false
                }
            } catch (e: Exception) {
                Log.i("EXCEPTION", e.toString())
                _cargando.value = false
            }
        }
    }

    fun formatearFecha() {
        val fechaCreacion = AuthManager.currentUser.value?.metadata?.creationTimestamp
        val date = fechaCreacion?.let { Date(it) }
        val dateFormat = SimpleDateFormat("MMMM yyyy", Locale("es", "ES"))
        _fechaString.value = "Miembro desde: ${dateFormat.format(date)}"
    }

    fun borrarReceta(idReceta: Int){
        val uid = AuthManager.currentUser.value?.uid ?: return

        viewModelScope.launch {
            try {
                val response = endpoints.borrarReceta(uid, idReceta)
                if (response.isSuccessful) {
                    Log.d(":::Correcto",  _listaRecetas.value.toString())
                    _listaRecetas.update { recetas -> recetas.filter { it.idReceta != idReceta } }
                    Log.d(":::Correcto",  "Borrado correctamente")
                    Log.d(":::Correcto",  _listaRecetas.value.toString())
                } else {
                    val errorMessage = response.errorBody()?.string()
                    Log.d(":::Error", "Mensaje: $errorMessage")
                }
            } catch (e: Exception) {
                Log.d(":::Error",  "Excepción: ${e.localizedMessage}")
            }
        }
    }


}
