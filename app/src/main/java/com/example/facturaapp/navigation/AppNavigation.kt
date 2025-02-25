package com.example.facturaapp.navigation

import androidx.compose.runtime.*
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.facturaapp.ui.*
import com.google.firebase.auth.FirebaseUser

@Composable
fun AppNavigation(
    facturaViewModel: FacturaViewModel,
    authViewModel: AuthViewModel
) {
    val navController = rememberNavController()
    val currentUser by authViewModel.authState.collectAsState()

    // Manejo de redirección basado en el estado de autenticación
    LaunchedEffect(currentUser) {
        val currentRoute = navController.currentDestination?.route
        if (currentUser != null && currentRoute != "list") {
            navController.navigate("list") {
                popUpTo("login") { inclusive = true } // Limpiamos historial de login
            }
        } else if (currentUser == null && currentRoute != "login") {
            navController.navigate("login") {
                popUpTo("list") { inclusive = true } // Evita que el usuario vuelva si cerró sesión
            }
        }
    }

    NavHost(navController = navController, startDestination = if (currentUser != null) "list" else "login") {

        // Pantalla de Login
        composable("login") {
            LoginScreen(
                authViewModel = authViewModel,
                onNavigateToRegister = { navController.navigate("register") },
                onLoginSuccess = { navController.navigate("list") }
            )
        }

        // Pantalla de Registro
        composable("register") {
            RegisterScreen(
                authViewModel = authViewModel,
                onRegistrationSuccess = { navController.navigate("list") }
            )
        }

        // Pantalla de Listado de Facturas
        composable("list") {
            FacturaListScreen(
                viewModel = facturaViewModel,
                authViewModel = authViewModel,
                navController = navController,
                onFacturaClick = { factura ->
                    navController.navigate("facturaDetail/${factura.id}")
                },
                onNavigateToForm = { navController.navigate("facturaForm") } // <-- Agregamos la versión sin parámetros
            )
        }

        // Pantalla de Detalle de Factura
        composable(
            "facturaDetail/{facturaId}",
            arguments = listOf(navArgument("facturaId") { type = NavType.StringType })
        ) { backStackEntry ->
            val facturaId = backStackEntry.arguments?.getString("facturaId") ?: ""
            FacturaDetailScreen(
                facturaId = facturaId,
                viewModel = facturaViewModel,
                navController = navController
            )
        }

        // Pantalla de Creación/Edición de Factura (con facturaId opcional)
        composable("facturaForm") { // 🚀 Agregar versión sin parámetros
            FacturaScreen(
                viewModel = facturaViewModel,
                facturaId = null,
                onNavigateToList = { navController.navigate("list") }
            )
        }

        composable(
            "facturaForm/{facturaId}",
            arguments = listOf(navArgument("facturaId") {
                type = NavType.StringType
                nullable = true
            })
        ) { backStackEntry ->
            val facturaId = backStackEntry.arguments?.getString("facturaId")
            FacturaScreen(
                viewModel = facturaViewModel,
                facturaId = facturaId,
                onNavigateToList = { navController.navigate("list") }
            )
        }
    }
}