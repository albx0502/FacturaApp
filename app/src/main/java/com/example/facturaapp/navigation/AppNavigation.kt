package com.example.facturaapp.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.facturaapp.ui.FacturaDetailScreen
import com.example.facturaapp.ui.FacturaListScreen
import com.example.facturaapp.ui.FacturaScreen
import com.example.facturaapp.ui.FacturaViewModel

/**
 * AppNavigation define las pantallas y rutas.
 */
@Composable
fun AppNavigation(viewModel: FacturaViewModel) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "form") {

        // Pantalla de creación/edición de facturas
        composable(route = "form") {
            FacturaScreen(
                viewModel = viewModel,
                onNavigateToList = {
                    navController.navigate("list")
                }
            )
        }

        // Pantalla de listado de facturas
        composable(route = "list") {
            FacturaListScreen(
                viewModel = viewModel,
                onFacturaClick = { factura ->
                    navController.navigate("details/${factura.id}")
                },
                onNavigateToForm = {
                    navController.navigate("form")
                }
            )
        }

        // Pantalla de detalles de una factura
        // Nota: Usamos NavType.StringType porque id ahora es un String.
        composable(
            route = "details/{facturaId}",
            arguments = listOf(navArgument("facturaId") { type = NavType.StringType })
        ) { backStackEntry ->
            val facturaId = backStackEntry.arguments?.getString("facturaId") ?: ""
            val facturasState = viewModel.facturas.collectAsState()
            val factura = facturasState.value.find { it.id == facturaId }

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
                        navController.popBackStack() // Vuelve a la lista
                    }
                )
            }
        }
    }
}
