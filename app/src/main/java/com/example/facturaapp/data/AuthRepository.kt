package com.example.facturaapp.data

import android.util.Log
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

                // 🔹 Verifica si el usuario existe
                val user = result.user ?: return@withContext Result.failure(Exception("No se pudo autenticar el usuario."))

                // 🔹 Refresca el token de autenticación
                user.getIdToken(true).await()

                Result.success(Unit)
            } catch (e: Exception) {
                Log.e("AuthRepository", "Error al iniciar sesión: ${e.message}")
                Result.failure(e)
            }
        }
    }



    fun signOut(onSignOut: () -> Unit) {
        auth.signOut()
        onSignOut()
    }


    fun getCurrentUser(): FirebaseUser? {
        return auth.currentUser ?: auth.currentUser?.reload()?.run { auth.currentUser }
    }


    fun isUserLoggedIn(): Boolean {
        val isLoggedIn = auth.currentUser != null
        Log.d("AuthRepository", "Usuario autenticado: $isLoggedIn")
        return isLoggedIn
    }


}
