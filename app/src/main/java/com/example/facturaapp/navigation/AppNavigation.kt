package com.example.facturaapp.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.facturaapp.ui.*
import com.google.firebase.auth.FirebaseUser

/**
 * AppNavigation define las pantallas y rutas.
 * - Ahora incluye "login" y "register" como rutas principales de acceso.
 * - Solo tras loguearse con éxito se navega a "list".
 */
@Composable
fun AppNavigation(
    facturaViewModel: FacturaViewModel,
    authViewModel: AuthViewModel
) {
    val navController = rememberNavController()

    // Obtenemos el usuario actual, para ver si está logueado
    val currentUser: FirebaseUser? = authViewModel.authState.collectAsState().value

    NavHost(navController = navController, startDestination = "login") {

        // Pantalla de Login
        composable(route = "login") {
            LoginScreen(
                authViewModel = authViewModel,
                onNavigateToRegister = {
                    navController.navigate("register")
                },
                onLoginSuccess = {
                    // Si el login es exitoso, vamos a "list"
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
                    // Tras registrarse con éxito, vamos a "list"
                    navController.navigate("list") {
                        popUpTo("register") { inclusive = true }
                    }
                }
            )
        }

        // Pantalla de listado de facturas
        composable(route = "list") {
            // Opcionalmente, puedes verificar si currentUser es null:
            if (currentUser == null) {
                // Si no hay usuario logueado, regresamos a "login"
                navController.navigate("login") {
                    popUpTo("list") { inclusive = true }
                }
            } else {
                FacturaListScreen(
                    viewModel = facturaViewModel,
                    onFacturaClick = { factura ->
                        navController.navigate("details/${factura.id}")
                    },
                    onNavigateToForm = {
                        navController.navigate("form")
                    }
                )
            }
        }

        // Pantalla de creación/edición de facturas
        composable(route = "form") {
            // También puedes exigir un usuario logueado:
            if (currentUser == null) {
                navController.navigate("login") {
                    popUpTo("form") { inclusive = true }
                }
            } else {
                FacturaScreen(
                    viewModel = facturaViewModel,
                    onNavigateToList = {
                        navController.navigate("list")
                    }
                )
            }
        }

        // Pantalla de detalles de una factura
        composable(
            route = "details/{facturaId}",
            arguments = listOf(navArgument("facturaId") { type = NavType.StringType })
        ) { backStackEntry ->
            if (currentUser == null) {
                navController.navigate("login") {
                    popUpTo("details/{facturaId}") { inclusive = true }
                }
            } else {
                val facturaId = backStackEntry.arguments?.getString("facturaId") ?: ""
                val facturasState = facturaViewModel.facturas.collectAsState()
                val factura = facturasState.value.find { it.id == facturaId }

                factura?.let {
                    FacturaDetailScreen(
                        factura = it,
                        onBackClick = { navController.popBackStack() },
                        onEditClick = { facturaToEdit ->
                            facturaViewModel.editFactura(facturaToEdit)
                            navController.navigate("form")
                        },
                        onDeleteClick = { facturaToDelete ->
                            facturaViewModel.deleteFactura(facturaToDelete)
                            navController.popBackStack() // Vuelve a la lista
                        }
                    )
                }
            }
        }
    }
}
