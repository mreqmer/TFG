package com.example.myapprecetas.vm

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapprecetas.repositories.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VMLogin @Inject constructor(
    private val authRepository: AuthRepository // Inyectamos el repositorio
) : ViewModel() {

    private val email: MutableLiveData<String> = MutableLiveData("")
    private val password: MutableLiveData<String> = MutableLiveData("")
    private val esPasswordVisible: MutableLiveData<Boolean> = MutableLiveData(false)

    val Email: MutableLiveData<String> = email
    val Password: MutableLiveData<String> = password
    val EsPasswordVisible: MutableLiveData<Boolean> = esPasswordVisible

    val user: StateFlow<FirebaseUser?> = authRepository.user
    val error: StateFlow<String?> = authRepository.error

    fun OnLoginChanged(email: String, password: String) {
        this.email.value = email
        this.password.value = password
    }

    fun signIn(email: String, password: String) {

        authRepository.signIn(email, password)
    }
}