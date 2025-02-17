package com.example.facturaapp.data

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await

/**
 * FacturaRepository gestiona las operaciones con Firestore:
 * - getAllFacturas(), addFactura(), updateFactura(), deleteFactura().
 */
class FacturaRepository {

    private val firestore = FirebaseFirestore.getInstance()
    private val facturaCollection = firestore.collection("facturas")

    /**
     * Recupera todas las facturas desde "facturas".
     */
    fun getAllFacturas(): Flow<List<FacturaEntity>> = flow {
        try {
            val snapshot = facturaCollection.get().await()
            val facturas = snapshot.documents.mapNotNull { doc ->
                val factura = doc.toObject(FacturaEntity::class.java)
                // Asignar el doc.id a la factura
                factura?.id = doc.id
                factura
            }
            emit(facturas)
        } catch (e: Exception) {
            println("Error al obtener facturas: ${e.message}")
            emit(emptyList())
        }
    }

    /**
     * Añade una factura nueva. Genera un ID automáticamente.
     */
    suspend fun addFactura(factura: FacturaEntity) {
        try {
            // 1) Añadir sin especificar ID => Firestore crea uno
            val docRef = facturaCollection.add(factura.toMap()).await()

            // 2) docRef.id => ID generado
            factura.id = docRef.id

            // 3) Opcionalmente, hacemos un set() con merge para que 'id' quede guardado en el documento
            facturaCollection.document(docRef.id)
                .set(factura.toMap(), SetOptions.merge())
                .await()
        } catch (e: Exception) {
            println("Error al guardar factura en Firebase: ${e.message}")
        }
    }

    /**
     * Actualiza una factura existente (usando factura.id).
     */
    suspend fun updateFactura(factura: FacturaEntity) {
        try {
            if (factura.id.isNotEmpty()) {
                facturaCollection.document(factura.id)
                    .set(factura.toMap(), SetOptions.merge())
                    .await()
            } else {
                println("No se puede actualizar. ID vacío.")
            }
        } catch (e: Exception) {
            println("Error al actualizar factura en Firebase: ${e.message}")
        }
    }

    /**
     * Elimina el documento con la ID dada.
     */
    suspend fun deleteFactura(facturaId: String) {
        try {
            facturaCollection.document(facturaId).delete().await()
        } catch (e: Exception) {
            println("Error al eliminar la factura en Firebase: ${e.message}")
        }
    }
}
