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
import com.example.facturaapp.ui.FacturaViewModelFactory
import com.example.facturaapp.ui.theme.FacturaAppTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            FacturaAppTheme {
                // ✅ Instanciar repositorios
                val facturaRepository = FacturaRepository()
                val authRepository = AuthRepository()

                // ✅ Instanciar AuthViewModel (NO necesita Factory)
                val authViewModel: AuthViewModel = viewModel { AuthViewModel(authRepository) }

                // ✅ Instanciar FacturaViewModel usando su Factory
                val facturaViewModel: FacturaViewModel = viewModel(factory = FacturaViewModelFactory(facturaRepository))


                AppNavigation(
                    facturaViewModel = facturaViewModel,
                    authViewModel = authViewModel
                )
            }
        }
    }
}
