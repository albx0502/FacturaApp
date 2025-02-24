package com.example.facturaapp.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.facturaapp.data.FacturaRepository
import com.example.facturaapp.ui.FacturaViewModel


/**
 * Factory para inyectar FacturaRepository en FacturaViewModel.
 */
class FacturaViewModelFactory(private val repository: FacturaRepository) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(FacturaViewModel::class.java) -> {
                @Suppress("UNCHECKED_CAST")
                FacturaViewModel(repository) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
