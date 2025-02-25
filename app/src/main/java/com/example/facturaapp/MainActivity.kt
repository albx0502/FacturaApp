package com.example.facturaapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.facturaapp.data.AuthRepository
import com.example.facturaapp.data.FacturaRepository
import com.example.facturaapp.navigation.AppNavigation
import com.example.facturaapp.ui.AuthViewModel
import com.example.facturaapp.ui.FacturaViewModel
import com.example.facturaapp.ui.theme.FacturaAppTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            FacturaAppTheme {
                // ✅ Se inicializan los repositorios directamente dentro del `setContent`
                val facturaRepository = FacturaRepository()
                val authRepository = AuthRepository()

                // ✅ Se pasa el repositorio directamente en lugar de instanciar un Factory
                val facturaViewModel: FacturaViewModel = viewModel { FacturaViewModel(facturaRepository) }
                val authViewModel: AuthViewModel = viewModel { AuthViewModel(authRepository) }

                AppNavigation(
                    facturaViewModel = facturaViewModel,
                    authViewModel = authViewModel
                )
            }
        }
    }
}
