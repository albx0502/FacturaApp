package com.example.facturaapp.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.facturaapp.data.FacturaEntity
import com.example.facturaapp.data.FacturaRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class FacturaViewModel(private val repository: FacturaRepository) : ViewModel() {

    private val _uiMessage = MutableStateFlow<String?>(null)
    val uiMessage: StateFlow<String?> get() = _uiMessage

    private val _facturas = MutableStateFlow<List<FacturaEntity>>(emptyList())
    val facturas: StateFlow<List<FacturaEntity>> = repository.getAllFacturas()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _facturaToEdit = MutableStateFlow<FacturaEntity?>(null)
    val facturaToEdit: StateFlow<FacturaEntity?> get() = _facturaToEdit

    init {
        loadFacturas()
    }

    private fun loadFacturas() {
        viewModelScope.launch {
            repository.getAllFacturas().collect { facturasList ->
                _facturas.value = facturasList
            }
        }
    }

    fun setUiMessage(message: String) {
        _uiMessage.value = message
    }

    fun deleteFactura(factura: FacturaEntity) {
        viewModelScope.launch {
            try {
                repository.deleteFactura(factura)
                setUiMessage("Factura eliminada con éxito")
                loadFacturas()
            } catch (e: Exception) {
                setUiMessage("Error al eliminar la factura: ${e.message}")
            }
        }
    }

    fun editFactura(factura: FacturaEntity?) {
        _facturaToEdit.value = factura
    }

    fun saveFactura(factura: FacturaEntity) {
        if (factura.numeroFactura.isBlank() || factura.emisor.isBlank() || factura.receptor.isBlank()) {
            setUiMessage("Todos los campos son obligatorios")
            return
        }
        viewModelScope.launch {
            try {
                if (factura.id > 0) {
                    repository.updateFactura(factura)
                    setUiMessage("Factura actualizada con éxito")
                } else {
                    repository.addFactura(factura)
                    setUiMessage("Factura creada con éxito")
                }
                loadFacturas()
            } catch (e: Exception) {
                setUiMessage("Error al guardar la factura: ${e.message}")
            }
        }
    }

    fun clearMessage() {
        _uiMessage.value = null
    }
}
