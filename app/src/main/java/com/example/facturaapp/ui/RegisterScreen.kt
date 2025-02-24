package com.example.facturaapp.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

/**
 * Pantalla de registro de usuario con Email/Password.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    authViewModel: AuthViewModel,
    onRegistrationSuccess: () -> Unit
) {
    val authState by authViewModel.authState.collectAsState()
    val errorMessage by authViewModel.errorMessage.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Si se registró con éxito (authState != null), navegamos
    LaunchedEffect(authState) {
        if (authState != null) {
            onRegistrationSuccess()
        }
    }

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isSubmitting by remember { mutableStateOf(false) } // Para deshabilitar el botón mientras se envía

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Registro") })
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth()
                )

                TextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Contraseña") },
                    modifier = Modifier.fillMaxWidth(),
                    visualTransformation = PasswordVisualTransformation()
                )

                if (errorMessage != null) {
                    // Muestra el error en un Snackbar en lugar de un texto simple
                    LaunchedEffect(errorMessage) {
                        scope.launch {
                            snackbarHostState.showSnackbar(errorMessage ?: "")
                            authViewModel.clearError() // Limpia el error después de mostrarlo
                        }
                    }
                }

                Button(
                    onClick = {
                        if (email.isBlank() || password.isBlank()) {
                            scope.launch { snackbarHostState.showSnackbar("Todos los campos son obligatorios") }
                        } else {
                            isSubmitting = true
                            authViewModel.signUp(email, password)
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isSubmitting // Deshabilita el botón mientras se está enviando
                ) {
                    Text("Crear Cuenta")
                }
            }
        }
    )
}
