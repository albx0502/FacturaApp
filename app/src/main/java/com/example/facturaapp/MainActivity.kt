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

class MainActivity : ComponentActivity() {

    // Inicializamos los repositorios con `lazy` para evitar que se creen si no se usan
    private val facturaRepository by lazy { FacturaRepository() }
    private val authRepository by lazy { AuthRepository() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            FacturaAppTheme {
                // ✅ Se usa directamente `viewModel(factory = ...)` SIN `remember {}` y SIN `try-catch`
                val facturaViewModel: FacturaViewModel = viewModel(factory = FacturaViewModelFactory(facturaRepository))
                val authViewModel: AuthViewModel = viewModel(factory = AuthViewModelFactory(authRepository))

                AppNavigation(
                    facturaViewModel = facturaViewModel,
                    authViewModel = authViewModel
                )
            }
        }
    }
}
