package com.example.facturaapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.lifecycleScope
import com.example.facturaapp.data.DatabaseProvider
import com.example.facturaapp.data.FacturaRepository
import com.example.facturaapp.ui.FacturaViewModel
import com.example.facturaapp.ui.FacturaViewModelFactory
import com.example.facturaapp.navigation.AppNavigation
import com.example.facturaapp.ui.theme.FacturaAppTheme
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.facturaapp.data.FacturaDatabase
import com.example.facturaapp.data.FacturaEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

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
//        lifecycleScope.launch {
//            val factura = FacturaEntity(
//                numeroFactura = "12345",
//                fechaEmision = "2025-01-31",
//                emisor = "Empresa A",
//                emisorNIF = "12345678A",
//                emisorDireccion = "Calle Falsa 123",
//                receptor = "Cliente B",
//                receptorNIF = "87654321B",
//                receptorDireccion = "Avenida Real 456",
//                baseImponible = 100.0,
//                iva = 21.0,
//                total = 121.0
//            )
//            val db = FacturaDatabase.getDatabase(applicationContext)
//            db.facturaDao().insertFactura(factura)
//
//            val facturas = db.facturaDao().getAllFacturas().first()
//            println("Facturas en la base de datos: $facturas")
//        }

    }
}
