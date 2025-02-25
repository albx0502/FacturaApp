package com.example.facturaapp.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class FacturaRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    /**
     * Obtiene todas las facturas del usuario autenticado en **tiempo real**.
     */
    fun getAllFacturas(): Flow<List<FacturaEntity>> = callbackFlow {
        val user = auth.currentUser ?: run {
            close(Exception("Usuario no autenticado"))
            return@callbackFlow
        }

        val facturaCollection = firestore.collection("usuarios")
            .document(user.uid).collection("facturas")

        val listener = facturaCollection.addSnapshotListener { snapshot, e ->
            if (e != null) {
                close(e)
                return@addSnapshotListener
            }

            val facturas = snapshot?.documents?.mapNotNull { doc ->
                doc.toObject(FacturaEntity::class.java)?.copy(id = doc.id)
            } ?: emptyList()

            trySend(facturas).isSuccess
        }

        awaitClose { listener.remove() }
    }.flowOn(Dispatchers.IO) // 🔹 Ahora se ejecuta en segundo plano

    fun getFacturaById(facturaId: String): Flow<FacturaEntity?> = callbackFlow {
        val user = auth.currentUser ?: run {
            close(Exception("Usuario no autenticado"))
            return@callbackFlow
        }

        val facturaDoc = firestore.collection("usuarios")
            .document(user.uid)
            .collection("facturas")
            .document(facturaId)

        val listener = facturaDoc.addSnapshotListener { snapshot, e ->
            if (e != null) {
                close(e)
                return@addSnapshotListener
            }

            val factura = snapshot?.toObject(FacturaEntity::class.java)?.copy(id = facturaId)
            trySend(factura).isSuccess
        }

        awaitClose { listener.remove() } // Se detiene cuando ya no se usa
    }.flowOn(Dispatchers.IO)


    /**
     * Guarda una factura asociada al usuario autenticado.
     */
    suspend fun addFactura(factura: FacturaEntity) = withContext(Dispatchers.IO) {
        val user = auth.currentUser ?: throw Exception("Usuario no autenticado")
        val facturaCollection = firestore.collection("usuarios").document(user.uid).collection("facturas")

        try {
            val docRef = facturaCollection.add(factura.toMap()).await()
            facturaCollection.document(docRef.id).set(factura.toMap(), SetOptions.merge()).await()

        } catch (e: Exception) {
            throw Exception("Error al guardar factura: ${e.message}")
        }
    }

    /**
     * Actualiza una factura del usuario autenticado.
     */
    suspend fun updateFactura(factura: FacturaEntity) = withContext(Dispatchers.IO) {
        val user = auth.currentUser ?: throw Exception("Usuario no autenticado")
        if (factura.id.isNotEmpty()) {
            firestore.collection("usuarios")
                .document(user.uid)
                .collection("facturas")
                .document(factura.id)
                .set(factura.toMap(), SetOptions.merge())
                .await()
        }
    }

    /**
     * Elimina una factura del usuario autenticado.
     */
    suspend fun deleteFactura(facturaId: String) = withContext(Dispatchers.IO) {
        val user = auth.currentUser ?: throw Exception("Usuario no autenticado")
        firestore.collection("usuarios")
            .document(user.uid)
            .collection("facturas")
            .document(facturaId)
            .delete()
            .await()
    }
}
