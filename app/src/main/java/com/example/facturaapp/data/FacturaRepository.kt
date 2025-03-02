package com.example.facturaapp.data

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
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
    private val listeners = mutableListOf<ListenerRegistration>()

    fun getAllFacturas(): Flow<List<FacturaEntity>> = callbackFlow {
        val user = auth.currentUser
        if (user == null) {
            Log.w("FacturaRepository", "❌ Usuario no autenticado - No se pueden obtener facturas.")
            trySend(emptyList()).isSuccess
            close()
            return@callbackFlow
        }

        Log.d("FacturaRepository", "🔍 Buscando facturas para UID: ${user.uid}")

        val facturaCollection = firestore.collection("usuarios")
            .document(user.uid)
            .collection("facturas")

        val listener = facturaCollection.addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.e("FacturaRepository", "❌ Error obteniendo facturas: ${e.message}")
                close(e)
                return@addSnapshotListener
            }

            val facturas = snapshot?.documents?.mapNotNull { doc ->
                doc.toObject(FacturaEntity::class.java)?.copy(id = doc.id)
            } ?: emptyList()

            Log.d("FacturaRepository", "📌 Facturas obtenidas: ${facturas.size}")
            if (facturas.isEmpty()) {
                Log.w("FacturaRepository", "⚠️ No se encontraron facturas en Firestore.")
            }

            trySend(facturas).isSuccess
        }

        listeners.add(listener)
        awaitClose { listener.remove() }
    }.flowOn(Dispatchers.IO)



    fun getFacturaById(facturaId: String): Flow<FacturaEntity?> = callbackFlow {
        if (facturaId.isBlank()) {
            Log.e("FacturaRepository", "ID de factura vacío")
            close(Exception("ID de factura vacío"))
            return@callbackFlow
        }

        val user = auth.currentUser ?: run {
            Log.e("FacturaRepository", "Usuario no autenticado")
            close(Exception("Usuario no autenticado"))
            return@callbackFlow
        }

        val facturaDoc = firestore.collection("usuarios")
            .document(user.uid)
            .collection("facturas")
            .document(facturaId)

        val listener = facturaDoc.addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.e("FacturaRepository", "Error obteniendo factura: ${e.message}")
                close(e)
                return@addSnapshotListener
            }

            val factura = snapshot?.toObject(FacturaEntity::class.java)?.copy(id = facturaId)
            Log.d("FacturaRepository", "Factura obtenida: ${factura?.id ?: "Ninguna"}")
            trySend(factura).isSuccess
        }

        listeners.add(listener)
        awaitClose { listener.remove() }
    }.flowOn(Dispatchers.IO)

    suspend fun addFactura(factura: FacturaEntity) = withContext(Dispatchers.IO) {
        val user = auth.currentUser
        if (user == null) {
            Log.e("FacturaRepository", "❌ Usuario no autenticado, no se puede añadir factura.")
            throw Exception("Usuario no autenticado")
        }

        Log.d("FacturaRepository", "✅ Usuario autenticado: ${user.uid}")

        val facturaCollection = firestore.collection("usuarios")
            .document(user.uid)
            .collection("facturas")

        try {
            if (factura.numeroFactura.isBlank()) {
                Log.e("FacturaRepository", "❌ Número de factura vacío, no se guarda.")
                throw Exception("El número de factura no puede estar vacío.")
            }

            val docRef = facturaCollection.document()
            val newFactura = factura.copy(id = docRef.id)

            Log.d("FacturaRepository", "📌 Guardando factura en Firestore: ${newFactura.toMap()}")

            docRef.set(newFactura.toMap()).await()

            val snapshot = docRef.get().await()
            if (snapshot.exists()) {
                Log.d("FacturaRepository", "✅ Factura guardada correctamente en Firestore: ${snapshot.data}")
            } else {
                Log.e("FacturaRepository", "❌ Error: La factura no se guardó en Firestore.")
            }

        } catch (e: Exception) {
            Log.e("FacturaRepository", "❌ Error al guardar factura: ${e.message}")
            throw Exception("Error al guardar factura: ${e.message}")
        }
    }





    suspend fun updateFactura(factura: FacturaEntity) = withContext(Dispatchers.IO) {
        val user = auth.currentUser ?: throw Exception("Usuario no autenticado")

        if (factura.id.isBlank()) throw Exception("ID de factura vacío")

        try {
            firestore.collection("usuarios")
                .document(user.uid)
                .collection("facturas")
                .document(factura.id)
                .set(factura.toMap(), SetOptions.merge())
                .await()
            Log.d("FacturaRepository", "Factura actualizada: ${factura.id}")

        } catch (e: Exception) {
            Log.e("FacturaRepository", "Error al actualizar la factura: ${e.message}")
            throw Exception("Error al actualizar la factura: ${e.message}")
        }
    }

    suspend fun deleteFactura(facturaId: String) = withContext(Dispatchers.IO) {
        val user = auth.currentUser ?: throw Exception("Usuario no autenticado")

        if (facturaId.isBlank()) throw Exception("ID de factura vacío")

        try {
            firestore.collection("usuarios")
                .document(user.uid)
                .collection("facturas")
                .document(facturaId)
                .delete()
                .await()
            Log.d("FacturaRepository", "Factura eliminada: $facturaId")

        } catch (e: Exception) {
            Log.e("FacturaRepository", "Error al eliminar la factura: ${e.message}")
            throw Exception("Error al eliminar la factura: ${e.message}")
        }
    }

    fun cancelAllFirestoreListeners() {
        listeners.forEach { it.remove() }
        listeners.clear()
        Log.d("FacturaRepository", "Se han cancelado todos los listeners de Firestore")
    }
}
