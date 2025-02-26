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

    suspend fun signInWithEmail(email: String, password: String): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                val result = auth.signInWithEmailAndPassword(email, password).await()

                // 🔹 FORZAMOS la generación del token de autenticación
                result.user?.getIdToken(true)?.await()

                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }


    fun signOut(onSignOut: () -> Unit) {
        auth.signOut()
        onSignOut()
    }


    fun getCurrentUser(): FirebaseUser? {
        return auth.currentUser
    }

}
