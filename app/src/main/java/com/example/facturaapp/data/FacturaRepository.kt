package com.example.facturaapp.data

class FacturaRepository(private val facturaDao: FacturaDao) {
    suspend fun insertFactura(factura: FacturaEntity){
        facturaDao.insertFactura(factura)
    }

    suspend fun getAllFacturas(): List<FacturaEntity>{
        return facturaDao.getAllFacturas()
    }

    suspend fun updateFactura(factura: FacturaEntity){
        facturaDao.updateFactura(factura)
    }
    suspend fun deleteFactura(factura: FacturaEntity){
        facturaDao.deleteFactura(factura)
    }
}