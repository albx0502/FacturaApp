package com.example.facturaapp.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.facturaapp.data.FacturaEntity
import com.example.facturaapp.data.FacturaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class FacturaViewModel(private val repository: FacturaRepository) : ViewModel() {

    // Lista de facturas observando el flujo directamente
    val facturas: StateFlow<List<FacturaEntity>> = repository.getAllFacturas()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Factura seleccionada para editar
    private val _facturaToEdit = MutableStateFlow<FacturaEntity?>(null)
    val facturaToEdit: StateFlow<FacturaEntity?> get() = _facturaToEdit

    fun addFactura(factura: FacturaEntity) {
        viewModelScope.launch {
            repository.addFactura(factura)
        }
    }

    fun deleteFactura(factura: FacturaEntity) {
        viewModelScope.launch {
            repository.deleteFactura(factura)
        }
    }

    fun editFactura(factura: FacturaEntity) {
        _facturaToEdit.value = factura
    }

    fun saveFactura(factura: FacturaEntity) {
        viewModelScope.launch {
            if (factura.id != null) {
                repository.updateFactura(factura)
            } else {
                repository.addFactura(factura)
            }
            _facturaToEdit.value = null
        }
    }
}
