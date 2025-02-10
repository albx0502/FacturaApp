package com.example.facturaapp.data

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.flow.Flow

class FacturaRepository(private val facturaDao: FacturaDao) {

    private val firestore = FirebaseFirestore.getInstance()
    private val facturaCollection = firestore.collection("facturas")

    // Obtener todas las facturas desde Room
    fun getAllFacturas(): Flow<List<FacturaEntity>> {
        return facturaDao.getAllFacturas()
    }

    // Insertar factura en Room y Firestore
    suspend fun addFactura(factura: FacturaEntity) {
        facturaDao.insertFactura(factura) // Guardar en Room

        // Guardar en Firestore
        val facturaMap = factura.toMap()
        facturaCollection.document(factura.id.toString()).set(facturaMap).await()
    }

    // Actualizar factura en Room y Firestore
    suspend fun updateFactura(factura: FacturaEntity) {
        facturaDao.updateFactura(factura) // Actualizar en Room

        // Actualizar en Firestore
        val facturaMap = factura.toMap()
        facturaCollection.document(factura.id.toString()).update(facturaMap).await()
    }

    // Eliminar factura en Room y Firestore
    suspend fun deleteFactura(factura: FacturaEntity) {
        facturaDao.deleteFactura(factura) // Eliminar en Room
        facturaCollection.document(factura.id.toString()).delete().await()
    }

    // Obtener una factura por ID desde Room
    suspend fun getFacturaById(id: Int): FacturaEntity? {
        return facturaDao.getFacturaById(id)
    }

    // Sincronizar Firestore con Room
    suspend fun syncFromFirestore() {
        val snapshot = facturaCollection.get().await()
        val facturas = snapshot.documents.mapNotNull { doc ->
            doc.toObject(FacturaEntity::class.java)
        }

        facturas.forEach { factura ->
            facturaDao.insertFactura(factura) // Guardar en Room
        }
    }
}
