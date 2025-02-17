package com.example.facturaapp.data

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await

/**
 * FacturaRepository gestiona todas las operaciones con Firestore.
 */
class FacturaRepository {

    private val firestore = FirebaseFirestore.getInstance()
    private val facturaCollection = firestore.collection("facturas")

    /**
     * getAllFacturas()
     * - Recupera todos los documentos de la colección "facturas".
     * - Convierte cada documento a FacturaEntity.
     */
    fun getAllFacturas(): Flow<List<FacturaEntity>> = flow {
        try {
            val snapshot = facturaCollection.get().await()
            val facturas = snapshot.documents.mapNotNull { document ->
                val factura = document.toObject(FacturaEntity::class.java)
                // Asegurarnos de que la factura tenga su ID
                factura?.id = document.id
                factura
            }
            emit(facturas)
        } catch (e: Exception) {
            println("Error al obtener facturas: ${e.message}")
            emit(emptyList())
        }
    }

    /**
     * addFactura(factura)
     * - Genera un ID automático con .add()
     * - Asigna ese ID a factura.id y hace un .set() con merge si quieres.
     */
    suspend fun addFactura(factura: FacturaEntity) {
        try {
            // 1) Añadir sin especificar ID (Firestore asigna)
            val docRef = facturaCollection.add(factura.toMap()).await()

            // 2) docRef.id es el ID que Firestore generó
            factura.id = docRef.id

            // 3) Opcionalmente, guardamos la factura con el campo id ya asignado (merge).
            //    De esta forma, en Firestore, la propiedad "id" dentro del documento no quedará vacía.
            facturaCollection.document(docRef.id)
                .set(factura.toMap(), SetOptions.merge())
                .await()

        } catch (e: Exception) {
            println("Error al guardar factura en Firebase: ${e.message}")
        }
    }

    /**
     * updateFactura(factura)
     * - Usa factura.id para actualizar el documento correspondiente.
     * - Asegúrate de que factura.id no esté vacío.
     */
    suspend fun updateFactura(factura: FacturaEntity) {
        try {
            if (factura.id.isNotEmpty()) {
                facturaCollection.document(factura.id)
                    .set(factura.toMap(), SetOptions.merge())
                    .await()
            } else {
                println("No se puede actualizar. El ID está vacío.")
            }
        } catch (e: Exception) {
            println("Error al actualizar factura en Firebase: ${e.message}")
        }
    }

    /**
     * deleteFactura(facturaId)
     * - Elimina el documento con ese ID.
     */
    suspend fun deleteFactura(facturaId: String) {
        try {
            facturaCollection.document(facturaId).delete().await()
        } catch (e: Exception) {
            println("Error al eliminar la factura en Firebase: ${e.message}")
        }
    }
}
