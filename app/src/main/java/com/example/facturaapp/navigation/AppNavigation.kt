package com.example.facturaapp.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.facturaapp.ui.*
import com.google.firebase.auth.FirebaseUser

@Composable
fun AppNavigation(
    facturaViewModel: FacturaViewModel,
    authViewModel: AuthViewModel
) {
    val navController = rememberNavController()
    val currentUser: FirebaseUser? = authViewModel.authState.collectAsState().value

    // Manejar redirección al detectar cambios en el usuario autenticado
    LaunchedEffect(currentUser) {
        if (currentUser != null) {
            navController.navigate("list") {
                popUpTo(0) // Elimina el historial de navegación previo para evitar retrocesos inesperados
            }
        }
    }

    NavHost(navController = navController, startDestination = if (currentUser != null) "list" else "login") {

        // Pantalla de Login
        composable(route = "login") {
            LoginScreen(
                authViewModel = authViewModel,
                onNavigateToRegister = {
                    navController.navigate("register")
                },
                onLoginSuccess = {
                    navController.navigate("list") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }

        // Pantalla de Registro
        composable(route = "register") {
            RegisterScreen(
                authViewModel = authViewModel,
                onRegistrationSuccess = {
                    navController.navigate("list") {
                        popUpTo("register") { inclusive = true }
                    }
                }
            )
        }

        // Pantalla de Listado de Facturas
        composable(route = "list") {
            FacturaListScreen(
                viewModel = facturaViewModel,  // 🔹 Corregido: Se pasa correctamente como "viewModel"
                authViewModel = authViewModel,
                navController = navController, // 🔹 Corregido: Se pasa el navController necesario
                onFacturaClick = { factura ->
                    navController.navigate("facturaDetail/${factura.id}")
                },
                onNavigateToForm = {
                    navController.navigate("facturaForm")
                }
            )
        }
    }
}
