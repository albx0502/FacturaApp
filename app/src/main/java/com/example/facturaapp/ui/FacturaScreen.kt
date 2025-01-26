package com.example.facturaapp.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.facturaapp.data.FacturaEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FacturaScreen(viewModel: FacturaViewModel) {
    val facturas = viewModel.facturas.collectAsState().value
    var numero by remember { mutableStateOf("") }
    var emisor by remember { mutableStateOf("") }
    var receptor by remember { mutableStateOf("") }
    var total by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Gestión de Facturas") })
        },
        content = { paddingValues ->
            Column(modifier = Modifier.padding(paddingValues)) {
                // Formulario para añadir una factura
                OutlinedTextField(
                    value = numero,
                    onValueChange = { numero = it },
                    label = { Text("Número de Factura") },
                    modifier = Modifier.fillMaxWidth().padding(8.dp)
                )
                OutlinedTextField(
                    value = emisor,
                    onValueChange = { emisor = it },
                    label = { Text("Emisor") },
                    modifier = Modifier.fillMaxWidth().padding(8.dp)
                )
                OutlinedTextField(
                    value = receptor,
                    onValueChange = { receptor = it },
                    label = { Text("Receptor") },
                    modifier = Modifier.fillMaxWidth().padding(8.dp)
                )
                OutlinedTextField(
                    value = total,
                    onValueChange = { total = it },
                    label = { Text("Total (€)") },
                    modifier = Modifier.fillMaxWidth().padding(8.dp)
                )
                Button(
                    onClick = {
                        viewModel.addFacturas(
                            FacturaEntity(
                                numero = numero,
                                emisor = emisor,
                                receptor = receptor,
                                total = total.toDoubleOrNull() ?: 0.0,
                                fechaEmision = "Hoy",
                                emisorNIF = "N/A",
                                emisorDireccion = "N/A",
                                receptorNIF = "N/A",
                                receptorDireccion = "N/A",
                                baseImponible = 0.0,
                                iva = 0.0
                            )
                        )
                        numero = ""
                        emisor = ""
                        receptor = ""
                        total = ""
                    },
                    modifier = Modifier.padding(8.dp)
                ) {
                    Text("Agregar Factura")
                }

                // Lista de facturas
                LazyColumn {
                    items(facturas) { factura ->
                        FacturaItem(
                            factura = factura,
                            onDelete = { viewModel.deleteFacturas(it) }
                        )
                    }
                }
            }
        }
    )
}
@Composable
fun FacturaItem(factura: FacturaEntity, onDelete: (FacturaEntity) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Factura N.º: ${factura.numero}")
            Text("Emisor: ${factura.emisor}")
            Text("Receptor: ${factura.receptor}")
            Text("Total: ${factura.total} €")
            Button(
                onClick = { onDelete(factura) },
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Text("Eliminar")
            }
        }
    }
}


