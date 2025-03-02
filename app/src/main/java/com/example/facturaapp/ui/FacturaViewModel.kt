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

    private var _facturas = MutableStateFlow<List<FacturaEntity>>(emptyList())
    val facturas: StateFlow<List<FacturaEntity>> = _facturas.asStateFlow()

    private val _facturaToEdit = MutableStateFlow<FacturaEntity?>(null)
    val facturaToEdit: StateFlow<FacturaEntity?> = _facturaToEdit.asStateFlow()

    init {
        fetchFacturas()
    }

    fun setUiMessage(message: String) {
        _uiMessage.value = message
    }

    fun clearMessage() {
        _uiMessage.value = null
    }

    fun fetchFacturas() {
        viewModelScope.launch {
            repository.getAllFacturas()
                .collect { facturas ->
                    _facturas.value = facturas
                }
        }
    }

    fun getFacturaById(facturaId: String): StateFlow<FacturaEntity?> {
        if (facturaId.isBlank()) {
            return flowOf(null).stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)
        }
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
                fetchFacturas() // 🔥 Recargar facturas tras eliminar
            } catch (e: Exception) {
                Log.e("FacturaViewModel", "Error al eliminar la factura", e)
                setUiMessage("Error al eliminar la factura: ${e.message}")
            }
        }
    }


    fun saveFactura(factura: FacturaEntity) {
        Log.d("FacturaViewModel", "🟢 Intentando guardar factura: $factura")

        if (factura.emisor.isBlank() || factura.receptor.isBlank()) {
            Log.e("FacturaViewModel", "❌ Emisor y Receptor son obligatorios")
            setUiMessage("Los campos Emisor y Receptor son obligatorios.")
            return
        }

        viewModelScope.launch {
            try {
                if (factura.id.isEmpty()) {
                    Log.d("FacturaViewModel", "📌 Nueva factura, llamando a addFactura()")
                    repository.addFactura(factura)
                    setUiMessage("Factura creada con éxito")
                } else {
                    Log.d("FacturaViewModel", "📌 Editando factura existente, llamando a updateFactura()")
                    repository.updateFactura(factura)
                    setUiMessage("Factura actualizada con éxito")
                }
                fetchFacturas() // 🔥 Forzar recarga después de añadir una factura

            } catch (e: Exception) {
                Log.e("FacturaViewModel", "❌ Error al guardar la factura", e)
                setUiMessage("Error al guardar la factura: ${e.message}")
            }
        }
    }




    fun clearFacturasOnLogout() { // 🚀 Evita que Firestore siga ejecutando consultas tras logout
        _facturas.value = emptyList()
    }
}
