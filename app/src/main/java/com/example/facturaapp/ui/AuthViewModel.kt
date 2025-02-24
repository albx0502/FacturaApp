package com.example.facturaapp.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.facturaapp.data.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * AuthViewModel maneja la lógica de autenticación con FirebaseAuth.
 */
class AuthViewModel(
    private val repository: AuthRepository
) : ViewModel() {

    private val _authState = MutableStateFlow<FirebaseUser?>(repository.getCurrentUser())
    val authState: StateFlow<FirebaseUser?> = _authState.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val authListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
        _authState.value = firebaseAuth.currentUser
    }

    init {
        FirebaseAuth.getInstance().addAuthStateListener(authListener)
    }

    fun clearError() {
        _errorMessage.value = null
    }

    // ✅ Nuevo método para establecer mensajes de error manualmente
    fun setErrorMessage(message: String) {
        _errorMessage.value = message
    }

    /**
     * Registro con email y password.
     */
    fun signUp(email: String, password: String) {
        viewModelScope.launch {
            try {
                repository.signUpWithEmail(email, password)
                _authState.value = repository.getCurrentUser()
            } catch (e: Exception) {
                _errorMessage.value = traducirErrorFirebase(e.message)
            }
        }
    }

    /**
     * Inicio de sesión con email y password.
     */
    fun signIn(email: String, password: String) {
        viewModelScope.launch(Dispatchers.IO) {  // Ejecutar en hilo de fondo
            try {
                repository.signInWithEmail(email, password)
                withContext(Dispatchers.Main) {
                    _authState.value = repository.getCurrentUser()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    _errorMessage.value = traducirErrorFirebase(e.message)
                }
            }
        }
    }


    /**
     * Cerrar sesión.
     */
    fun signOut() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.signOut()
            withContext(Dispatchers.Main) {
                _authState.value = null // Asegura que se actualiza en el hilo principal
            }
        }
    }



    /**
     * Traduce los errores comunes de Firebase a español.
     */
    private fun traducirErrorFirebase(error: String?): String {
        return when {
            error?.contains("The email address is badly formatted") == true -> "Formato de correo inválido."
            error?.contains("There is no user record corresponding to this identifier") == true -> "El usuario no existe."
            error?.contains("The password is invalid") == true -> "Contraseña incorrecta."
            error?.contains("The email address is already in use") == true -> "Este correo ya está registrado."
            error?.contains("Password should be at least 6 characters") == true -> "La contraseña debe tener al menos 6 caracteres."
            else -> "Error desconocido: $error"
        }
    }

    override fun onCleared() {
        super.onCleared()
        FirebaseAuth.getInstance().removeAuthStateListener(authListener)
    }
}
