package com.example.facturaapp.data

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await

class FacturaRepository(private val facturaDao: FacturaDao) {

    private val firestore = FirebaseFirestore.getInstance()
    private val facturaCollection = firestore.collection("facturas")

    fun getAllFacturas() = facturaDao.getAllFacturas()

    suspend fun addFactura(factura: FacturaEntity) {
        // Insertar en Room
        facturaDao.insertFactura(factura)

        // Insertar en Firebase
        try {
            facturaCollection.document(factura.id.toString()).set(factura.toMap()).await()
        } catch (e: Exception) {
            println("Error al guardar factura en Firebase: ${e.message}")
        }
    }

    suspend fun updateFactura(factura: FacturaEntity) {
        // Actualizar en Room
        facturaDao.updateFactura(factura)

        // Actualizar en Firebase
        try {
            facturaCollection.document(factura.id.toString())
                .set(factura.toMap(), SetOptions.merge()).await()
        } catch (e: Exception) {
            println("Error al actualizar factura en Firebase: ${e.message}")
        }
    }

    suspend fun deleteFactura(factura: FacturaEntity) {
        // Eliminar de Room
        facturaDao.deleteFactura(factura)

        // Eliminar de Firebase
        try {
            facturaCollection.document(factura.id.toString()).delete().await()
        } catch (e: Exception) {
            println("Error al eliminar la factura en Firebase: ${e.message}")
        }
    }
}
