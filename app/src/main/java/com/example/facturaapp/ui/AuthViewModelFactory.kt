package com.example.facturaapp.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.facturaapp.data.AuthRepository

/**
 * Factory para crear una instancia de AuthViewModel con AuthRepository inyectado.
 */
class AuthViewModelFactory(private val repository: AuthRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return modelClass.getConstructor(AuthRepository::class.java)
            .newInstance(repository)
    }
}
