package com.example.facturaapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.facturaapp.data.FacturaRepository
import com.example.facturaapp.ui.FacturaViewModel
import com.example.facturaapp.ui.FacturaViewModelFactory
import com.example.facturaapp.ui.theme.FacturaAppTheme
import com.example.facturaapp.navigation.AppNavigation

/**
 * MainActivity: punto de entrada.
 * - Crea el FacturaRepository (Firestore).
 * - Inyecta en el ViewModel con FacturaViewModelFactory.
 * - Llama a AppNavigation para el enrutamiento Compose.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Creamos el repositorio que maneja Firestore
        val repository = FacturaRepository()
        val factory = FacturaViewModelFactory(repository)

        setContent {
            FacturaAppTheme {
                // Obtenemos el ViewModel con la factory
                val viewModel: FacturaViewModel = viewModel(factory = factory)

                // Navegación principal
                AppNavigation(viewModel = viewModel)
            }
        }
    }
}
