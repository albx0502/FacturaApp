package com.example.facturaapp.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.facturaapp.data.FacturaEntity
import com.example.facturaapp.data.FacturaRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class FacturaViewModel(private val repository: FacturaRepository) : ViewModel() {

    private val _uiMessage = MutableStateFlow<String?>(null)
    val uiMessage: StateFlow<String?> = _uiMessage.asStateFlow()

    val facturas: StateFlow<List<FacturaEntity>> = repository.getAllFacturas()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _facturaToEdit = MutableStateFlow<FacturaEntity?>(null)
    val facturaToEdit: StateFlow<FacturaEntity?> = _facturaToEdit.asStateFlow()

    private fun setUiMessage(message: String) {
        _uiMessage.value = message
    }

    fun clearMessage() {
        _uiMessage.value = null
    }

    fun getFacturaById(facturaId: String): StateFlow<FacturaEntity?> {
        return repository.getFacturaById(facturaId)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)
    }

    fun editFactura(factura: FacturaEntity?) {
        _facturaToEdit.value = factura
    }

    fun deleteFactura(factura: FacturaEntity) {
        viewModelScope.launch {
            try {
                repository.deleteFactura(factura.id)
                setUiMessage("Factura eliminada con éxito")
            } catch (e: Exception) {
                Log.e("FacturaViewModel", "Error al eliminar la factura", e)
                setUiMessage("Error al eliminar la factura: ${e.message}")
            }
        }
    }

    fun saveFactura(factura: FacturaEntity) {
        if (factura.emisor.isBlank() || factura.receptor.isBlank()) {
            setUiMessage("Los campos Emisor y Receptor son obligatorios.")
            return
        }

        viewModelScope.launch {
            try {
                if (factura.id.isEmpty()) {
                    repository.addFactura(factura)
                    setUiMessage("Factura creada con éxito")
                } else {
                    repository.updateFactura(factura)
                    setUiMessage("Factura actualizada con éxito")
                }
            } catch (e: Exception) {
                Log.e("FacturaViewModel", "Error al guardar la factura", e)
                setUiMessage("Error al guardar la factura: ${e.message}")
            }
        }
    }
}
