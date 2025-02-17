package com.example.facturaapp.data

import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await

/**
 * AuthRepository gestiona la autenticación con FirebaseAuth:
 * - Registro, Login, Logout, usuario actual.
 */
class AuthRepository {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    /**
     * Crea un usuario con email y password.
     */
    suspend fun signUpWithEmail(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password).await()
    }

    /**
     * Inicia sesión con email y password.
     */
    suspend fun signInWithEmail(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password).await()
    }

    /**
     * Cierra sesión.
     */
    fun signOut() {
        auth.signOut()
    }

    /**
     * Retorna el usuario actual, o null si no hay nadie logueado.
     */
    fun getCurrentUser() = auth.currentUser
}
