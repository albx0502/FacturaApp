package com.example.facturaapp.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.facturaapp.data.FacturaEntity
import com.example.facturaapp.data.FacturaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * ViewModel principal para gestionar facturas desde Firestore.
 */
class FacturaViewModel(private val repository: FacturaRepository) : ViewModel() {

    private val _uiMessage = MutableStateFlow<String?>(null)
    val uiMessage: StateFlow<String?> get() = _uiMessage

    // Observa el flujo de facturas recuperadas de Firestore
    val facturas: StateFlow<List<FacturaEntity>> = repository.getAllFacturas()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Factura en edición (si la hay)
    private val _facturaToEdit = MutableStateFlow<FacturaEntity?>(null)
    val facturaToEdit: StateFlow<FacturaEntity?> get() = _facturaToEdit

    fun setUiMessage(message: String) {
        _uiMessage.value = message
    }

    fun clearMessage() {
        _uiMessage.value = null
    }

    /**
     * Selecciona la factura a editar.
     * Si pasas null, sales del modo edición.
     */
    fun editFactura(factura: FacturaEntity?) {
        _facturaToEdit.value = factura
    }

    /**
     * Elimina la factura de Firestore.
     */
    fun deleteFactura(factura: FacturaEntity) {
        viewModelScope.launch {
            try {
                repository.deleteFactura(factura.id)
                setUiMessage("Factura eliminada con éxito")
            } catch (e: Exception) {
                setUiMessage("Error al eliminar la factura: ${e.message}")
            }
        }
    }

    /**
     * Guarda o actualiza la factura.
     * - Si factura.id está vacío => addFactura
     * - Si factura.id no está vacío => updateFactura
     */
    fun saveFactura(factura: FacturaEntity) {
        // Validaciones básicas
        if (factura.emisor.isBlank() || factura.receptor.isBlank()) {
            setUiMessage("Los campos Emisor y Receptor son obligatorios.")
            return
        }
        // Otras validaciones (NIF, IVA, etc.)

        viewModelScope.launch {
            try {
                if (factura.id.isEmpty()) {
                    // Nueva factura => add
                    repository.addFactura(factura)
                    setUiMessage("Factura creada con éxito")
                } else {
                    // Factura existente => update
                    repository.updateFactura(factura)
                    setUiMessage("Factura actualizada con éxito")
                }
            } catch (e: Exception) {
                setUiMessage("Error al guardar la factura: ${e.message}")
            }
        }
    }
}
