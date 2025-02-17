package com.example.facturaapp.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.facturaapp.data.AuthRepository
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * AuthViewModel maneja la lógica de registro, login y logout en FirebaseAuth.
 */
class AuthViewModel(
    private val repository: AuthRepository
) : ViewModel() {

    // authState indica el usuario actual: null si no hay sesión
    private val _authState = MutableStateFlow<FirebaseUser?>(repository.getCurrentUser())
    val authState: StateFlow<FirebaseUser?> = _authState

    // Mensajes de error (p.ej. contraseña corta, email inválido, etc.)
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    fun clearError() {
        _errorMessage.value = null
    }

    /**
     * Registro con email/password
     */
    fun signUp(email: String, password: String) {
        viewModelScope.launch {
            try {
                repository.signUpWithEmail(email, password)
                // Si fue exitoso, actualizamos authState
                _authState.value = repository.getCurrentUser()
            } catch (e: Exception) {
                // Si falla, mostramos error
                _errorMessage.value = e.message
            }
        }
    }

    /**
     * Inicio de sesión con email/password
     */
    fun signIn(email: String, password: String) {
        viewModelScope.launch {
            try {
                repository.signInWithEmail(email, password)
                _authState.value = repository.getCurrentUser()
            } catch (e: Exception) {
                _errorMessage.value = e.message
            }
        }
    }

    /**
     * Cerrar sesión
     */
    fun signOut() {
        repository.signOut()
        _authState.value = null
    }
}
