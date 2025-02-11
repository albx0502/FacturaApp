package com.example.facturaapp.data

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await

class FacturaRepository {

    private val firestore = FirebaseFirestore.getInstance()
    private val facturaCollection = firestore.collection("facturas")

    fun getAllFacturas(): Flow<List<FacturaEntity>> = flow {
        try {
            val snapshot = facturaCollection.get().await()
            val facturas = snapshot.documents.mapNotNull { it.toObject(FacturaEntity::class.java) }
            emit(facturas)
        } catch (e: Exception) {
            println("Error al obtener facturas: ${e.message}")
            emit(emptyList())
        }
    }

    suspend fun addFactura(factura: FacturaEntity) {
        try {
            facturaCollection.document(factura.id.toString()).set(factura.toMap()).await()
        } catch (e: Exception) {
            println("Error al guardar factura en Firebase: ${e.message}")
        }
    }

    suspend fun updateFactura(factura: FacturaEntity) {
        try {
            facturaCollection.document(factura.id.toString())
                .set(factura.toMap(), SetOptions.merge()).await()
        } catch (e: Exception) {
            println("Error al actualizar factura en Firebase: ${e.message}")
        }
    }

    suspend fun deleteFactura(facturaId: String) {
        try {
            facturaCollection.document(facturaId).delete().await()
        } catch (e: Exception) {
            println("Error al eliminar la factura en Firebase: ${e.message}")
        }
    }
}
