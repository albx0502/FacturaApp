package com.example.facturaapp.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.facturaapp.data.FacturaEntity
import com.example.facturaapp.data.FacturaRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FacturaViewModel(private val repository: FacturaRepository) : ViewModel() {

    private val _uiMessage = MutableStateFlow<String?>(null)
    val uiMessage: StateFlow<String?> = _uiMessage.asStateFlow()

    val facturas: StateFlow<List<FacturaEntity>> = repository.getAllFacturas()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _facturaToEdit = MutableStateFlow<FacturaEntity?>(null)
    val facturaToEdit: StateFlow<FacturaEntity?> = _facturaToEdit.asStateFlow()

    fun setUiMessage(message: String) {
        _uiMessage.value = message
    }

    fun clearMessage() {
        _uiMessage.value = null
    }

    fun getFacturaById(facturaId: String): Flow<FacturaEntity?> {
        return repository.getFacturaById(facturaId)
    }

    fun editFactura(factura: FacturaEntity?) {
        _facturaToEdit.value = factura
    }

    fun deleteFactura(factura: FacturaEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                repository.deleteFactura(factura.id)
                withContext(Dispatchers.Main) { setUiMessage("Factura eliminada con éxito") }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) { setUiMessage("Error al eliminar la factura: ${e.message}") }
            }
        }
    }

    fun saveFactura(factura: FacturaEntity) {
        if (factura.emisor.isBlank() || factura.receptor.isBlank()) {
            setUiMessage("Los campos Emisor y Receptor son obligatorios.")
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            try {
                if (factura.id.isEmpty()) {
                    repository.addFactura(factura)
                    withContext(Dispatchers.Main) { setUiMessage("Factura creada con éxito") }
                } else {
                    repository.updateFactura(factura)
                    withContext(Dispatchers.Main) { setUiMessage("Factura actualizada con éxito") }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) { setUiMessage("Error al guardar la factura: ${e.message}") }
            }
        }
    }
}
