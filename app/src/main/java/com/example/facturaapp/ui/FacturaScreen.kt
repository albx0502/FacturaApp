package com.example.facturaapp.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.facturaapp.data.FacturaEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FacturaScreen(viewModel: FacturaViewModel){
    val facturas = viewModel.facturas.collectAsState().value

    Scaffold (
        topBar = {
            TopAppBar(title = { Text("Lista de Facturas") })
            },
        content = { paddingValues ->
            LazyColumn(contentPadding = paddingValues) {
                items(facturas) { factura ->
                    FacturaItem(factura = factura)
                }
            }
        }
    )
}

@Composable
fun FacturaItem(factura: FacturaEntity){
    Card (
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ){
        Column (modifier = Modifier.padding(16.dp)){
            Text("Factura N.º: ${factura.numero}")
            Text("Emisor: ${factura.emisor}")
            Text("Receptor: ${factura.receptor}")
            Text("Total: ${factura.total} €")
        }
    }
}

