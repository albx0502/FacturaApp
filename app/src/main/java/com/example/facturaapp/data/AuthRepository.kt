package com.example.facturaapp.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class AuthRepository {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    /**
     * Registra un usuario en Firebase Authentication y lo guarda en Firestore.
     * Devuelve un Result indicando éxito o error.
     */
    suspend fun signUpWithEmail(email: String, password: String): Result<Unit> {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val uid = result.user?.uid ?: return Result.failure(Exception("No se pudo obtener el UID."))

            // Guardar usuario en Firestore
            val user = hashMapOf(
                "uid" to uid,
                "email" to email
            )
            firestore.collection("usuarios").document(uid).set(user).await()

            Result.success(Unit) // Registro exitoso
        } catch (e: Exception) {
            Result.failure(e) // Retorna el error
        }
    }

    /**
     * Inicia sesión con email y password.
     * Devuelve un Result indicando éxito o error.
     */
    suspend fun signInWithEmail(email: String, password: String): Result<Unit> {
        return try {
            auth.signInWithEmailAndPassword(email, password).await()
            Result.success(Unit) // Inicio de sesión exitoso
        } catch (e: Exception) {
            Result.failure(e) // Retorna el error
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

    /**
     * Obtiene el email del usuario actual o `null` si no hay usuario autenticado.
     */
    fun getCurrentUserEmail(): String? {
        return auth.currentUser?.email
    }
}
