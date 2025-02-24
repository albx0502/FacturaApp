package com.example.facturaapp.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class AuthRepository {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    /**
     * Registra un usuario en Firebase Authentication y lo guarda en Firestore.
     * Devuelve un Result indicando éxito o error.
     */
    suspend fun signUpWithEmail(email: String, password: String): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                val result = auth.createUserWithEmailAndPassword(email, password).await()
                val uid = result.user?.uid ?: return@withContext Result.failure(Exception("No se pudo obtener el UID."))

                val user = hashMapOf("uid" to uid, "email" to email)
                firestore.collection("usuarios").document(uid).set(user).await()

                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }


    /**
     * Inicia sesión con email y password.
     * Devuelve un Result indicando éxito o error.
     */
    suspend fun signInWithEmail(email: String, password: String): Result<Unit> {
        return withContext(Dispatchers.IO) { // 🔹 Ejecuta la autenticación en segundo plano
            try {
                auth.signInWithEmailAndPassword(email, password).await()
                withContext(Dispatchers.Main) { Result.success(Unit) } // 🔹 Actualiza en el hilo principal
            } catch (e: Exception) {
                withContext(Dispatchers.Main) { Result.failure(e) } // 🔹 Manda el error sin bloquear la UI
            }
        }
    }


    /**
     * Cierra sesión.
     */
    fun signOut() {
        auth.signOut()
    }

    /**
     * Obtiene el usuario actual o `null` si no está autenticado.
     */
    fun getCurrentUser(): FirebaseUser? {
        return auth.currentUser
    }

}
