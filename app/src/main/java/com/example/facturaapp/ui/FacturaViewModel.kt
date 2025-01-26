package com.example.facturaapp.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.facturaapp.data.FacturaEntity
import com.example.facturaapp.data.FacturaRepository
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class FacturaViewModel(private val repository: FacturaRepository): ViewModel() {

    private val _facturas = MutableStateFlow<List<FacturaEntity>>(emptyList())
    val facturas: StateFlow<List<FacturaEntity>> = _facturas

    init {
        loadFacturas()
    }

    fun loadFacturas(){
        viewModelScope.launch{
            _facturas.value = repository.getAllFacturas()
        }
    }

    fun addFacturas(factura: FacturaEntity){
        viewModelScope.launch{
            repository.insertFactura(factura)
            loadFacturas()  //Recarga la lista despues de insertar
        }
    }

    fun deleteFacturas(factura: FacturaEntity){
        viewModelScope.launch{
            repository.deleteFactura(factura)
            loadFacturas()  //Recarga la lista despues de insertar
        }
    }
}