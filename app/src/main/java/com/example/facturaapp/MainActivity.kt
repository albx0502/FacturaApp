package com.example.facturaapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.facturaapp.data.AuthRepository
import com.example.facturaapp.data.FacturaRepository
import com.example.facturaapp.navigation.AppNavigation
import com.example.facturaapp.ui.AuthViewModel
import com.example.facturaapp.ui.AuthViewModelFactory
import com.example.facturaapp.ui.FacturaViewModel
import com.example.facturaapp.ui.FacturaViewModelFactory
import com.example.facturaapp.ui.theme.FacturaAppTheme

/**
 * MainActivity: punto de entrada.
 * - Crea los Repositorios (Auth y Factura).
 * - Crea sus ViewModels usando las Factories.
 * - Llama a AppNavigation con ambos ViewModels.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Repositorios para Facturas y Auth
        val facturaRepository = FacturaRepository()
        val authRepository = AuthRepository()

        // Factories
        val facturaFactory = FacturaViewModelFactory(facturaRepository)
        val authFactory = AuthViewModelFactory(authRepository)

        setContent {
            FacturaAppTheme {
                // Obtenemos ambos ViewModels
                val facturaViewModel: FacturaViewModel = viewModel(factory = facturaFactory)
                val authViewModel: AuthViewModel = viewModel(factory = authFactory)

                // Navegación principal, pasando los 2 ViewModels
                AppNavigation(
                    facturaViewModel = facturaViewModel,
                    authViewModel = authViewModel
                )
            }
        }
    }
}
