package com.example.facturaapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.facturaapp.data.DatabaseProvider
import com.example.facturaapp.data.FacturaRepository
import com.example.facturaapp.ui.FacturaViewModel
import com.example.facturaapp.ui.FacturaViewModelFactory
import com.example.facturaapp.navigation.AppNavigation
import com.example.facturaapp.ui.theme.FacturaAppTheme
import androidx.lifecycle.viewmodel.compose.viewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicializar la base de datos y el repositorio
        val database = DatabaseProvider.getDatabase(applicationContext)
        val repository = FacturaRepository(database.facturaDao())

        // Configurar ViewModelFactory
        val factory = FacturaViewModelFactory(repository)

        setContent {
            FacturaAppTheme {
                val viewModel: FacturaViewModel = viewModel(factory = factory)
                AppNavigation(viewModel = viewModel)
            }
        }
    }
}
