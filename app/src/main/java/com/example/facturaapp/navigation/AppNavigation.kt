package com.example.facturaapp.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.facturaapp.ui.*

@Composable
fun AppNavigation(viewModel: FacturaViewModel) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "form") {
        // Pantalla del formulario de facturas
        composable(route = "form") {
            FacturaScreen(
                viewModel = viewModel,
                onNavigateToList = {
                    navController.navigate("list")
                }
            )
        }

        // Pantalla del listado de facturas
        composable(route = "list") {
            FacturaListScreen(
                viewModel = viewModel, // **Agregar viewModel para que tenga acceso a las facturas**
                onFacturaClick = { factura ->
                    navController.navigate("details/${factura.id}")
                },
                onNavigateToForm = {
                    navController.navigate("form")
                }
            )
        }

        // Pantalla de detalles de una factura
        composable(
            route = "details/{facturaId}",
            arguments = listOf(navArgument("facturaId") { type = NavType.IntType })
        ) { backStackEntry ->
            val facturaId = backStackEntry.arguments?.getInt("facturaId") ?: -1
            val factura = viewModel.facturas.collectAsState().value.find { it.id == facturaId }
            factura?.let {
                FacturaDetailScreen(
                    factura = it,
                    onBackClick = { navController.popBackStack() },
                    onEditClick = { facturaToEdit ->
                        viewModel.editFactura(facturaToEdit)
                        navController.navigate("form")
                    },
                    onDeleteClick = { facturaToDelete ->
                        viewModel.deleteFactura(facturaToDelete)
                        navController.popBackStack() // Vuelve a la lista después de borrar
                    }
                )
            }
        }

    }
}

