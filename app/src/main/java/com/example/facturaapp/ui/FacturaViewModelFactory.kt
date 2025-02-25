package com.example.facturaapp.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.facturaapp.data.FacturaRepository

class FacturaViewModelFactory(private val repository: FacturaRepository) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        require(modelClass == FacturaViewModel::class.java) {
            "FacturaViewModelFactory solo puede crear instancias de FacturaViewModel. Intentaste crear: ${modelClass.name}"
        }
        return FacturaViewModel(repository) as T
    }
}
