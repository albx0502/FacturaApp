package com.example.facturaapp.ui

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.facturaapp.data.AuthRepository
import com.example.facturaapp.data.FacturaRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class AuthViewModel(
    private val repository: AuthRepository,
) : ViewModel() {

    private val _authState = MutableStateFlow<FirebaseUser?>(FirebaseAuth.getInstance().currentUser)
    val authState: StateFlow<FirebaseUser?> = _authState.asStateFlow()

    val isUserLoggedIn: StateFlow<Boolean> = _authState.map { it != null }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = repository.getCurrentUser() != null
    )

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val authListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
        val user = firebaseAuth.currentUser
        _authState.value = user
        println("Estado actualizado: Usuario = ${user?.uid ?: "null"}")
    }

    init {
        FirebaseAuth.getInstance().addAuthStateListener(authListener)
    }

    fun clearError() {
        _errorMessage.value = null
    }

    fun setErrorMessage(message: String) {
        _errorMessage.value = message
    }

    fun signUp(email: String, password: String) {
        viewModelScope.launch {
            try {
                repository.signUpWithEmail(email, password)

            } catch (e: Exception) {
                _errorMessage.value = traducirErrorFirebase(e.message)
            }
        }
    }

    fun signIn(email: String, password: String) {
        viewModelScope.launch {
            repository.signInWithEmail(email, password).onSuccess {
                FirebaseAuth.getInstance().currentUser?.reload()?.await()
                val user = FirebaseAuth.getInstance().currentUser
                _authState.value = user
                println("Inicio de sesión exitoso, usuario: ${user?.uid}")
            }.onFailure { e ->
                _errorMessage.value = traducirErrorFirebase(e.message)
            }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            repository.signOut {
                _authState.value = null
                println("Usuario cerró sesión")
            }
        }
    }

    private fun traducirErrorFirebase(error: String?): String {
        return when {
            error.isNullOrBlank() -> "Ha ocurrido un error inesperado."
            error.contains("The email address is badly formatted", ignoreCase = true) -> "Formato de correo inválido."
            error.contains("There is no user record corresponding to this identifier", ignoreCase = true) -> "El usuario no existe."
            error.contains("The password is invalid", ignoreCase = true) -> "Contraseña incorrecta."
            error.contains("The email address is already in use", ignoreCase = true) -> "Este correo ya está registrado."
            error.contains("Password should be at least 6 characters", ignoreCase = true) -> "La contraseña debe tener al menos 6 caracteres."
            else -> "Error: $error"
        }
    }

    override fun onCleared() {
        super.onCleared()
        FirebaseAuth.getInstance().removeAuthStateListener(authListener)
    }
}
