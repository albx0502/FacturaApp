package com.example.facturaapp.data

import kotlinx.coroutines.flow.Flow

class FacturaRepository(private val facturaDao: FacturaDao) {

    // Obtener todas las facturas como Flow
    fun getAllFacturas(): Flow<List<FacturaEntity>> {
        return facturaDao.getAllFacturas()
    }

    suspend fun addFactura(factura: FacturaEntity) {
        facturaDao.insertFactura(factura)
    }

    suspend fun updateFactura(factura: FacturaEntity) {
        facturaDao.updateFactura(factura)
    }

    suspend fun deleteFactura(factura: FacturaEntity) {
        facturaDao.deleteFactura(factura)
    }

    suspend fun getFacturaById(id: Int): FacturaEntity? {
        return facturaDao.getFacturaById(id)
    }
}
