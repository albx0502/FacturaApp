package com.example.facturaapp.navigation

import androidx.compose.runtime.*
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.facturaapp.ui.*
import com.google.firebase.auth.FirebaseUser
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.google.firebase.auth.FirebaseAuth

@Composable
fun AppNavigation(
    facturaViewModel: FacturaViewModel,
    authViewModel: AuthViewModel
) {
    val navController = rememberNavController()
    val currentUser by authViewModel.authState.collectAsState()

    LaunchedEffect(currentUser) {
        println("Evaluando estado del usuario: ${currentUser?.uid ?: "null"}")

        if (currentUser == null) {
            navController.navigate("login") {
                popUpTo("list") { inclusive = true }
            }
        } else {
            println("Usuario autenticado, cargando facturas...")
            facturaViewModel.fetchFacturas()
            navController.navigate("list") {
                popUpTo("login") { inclusive = true }
            }
        }
    }

    NavHost(navController = navController, startDestination = if (currentUser != null) "list" else "login") {

        composable("login") {
            LoginScreen(
                authViewModel = authViewModel,
                onNavigateToRegister = { navController.navigate("register") },
                onLoginSuccess = { navController.navigate("list") }
            )
        }

        composable("register") {
            RegisterScreen(
                authViewModel = authViewModel,
                onRegistrationSuccess = { navController.navigate("list") }
            )
        }

        composable("list") {
            FacturaListScreen(
                viewModel = facturaViewModel,
                authViewModel = authViewModel,
                navController = navController,
                onFacturaClick = { factura ->
                    navController.navigate("facturaDetail/${factura.id}")
                },
                onNavigateToForm = { navController.navigate("facturaForm") }
            )
        }

        composable(
            "facturaDetail/{facturaId}",
            arguments = listOf(navArgument("facturaId") { type = NavType.StringType })
        ) { backStackEntry ->
            val facturaId = backStackEntry.arguments?.getString("facturaId") ?: ""
            FacturaDetailScreen(
                facturaId = facturaId,
                viewModel = facturaViewModel,
                authViewModel = authViewModel,
                navController = navController
            )
        }

        composable("facturaForm") {
            FacturaScreen(
                viewModel = facturaViewModel,
                authViewModel = authViewModel,
                navController = navController,
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
                authViewModel = authViewModel,
                navController = navController,
                facturaId = facturaId,
                onNavigateToList = { navController.navigate("list") }
            )
        }
    }
}
