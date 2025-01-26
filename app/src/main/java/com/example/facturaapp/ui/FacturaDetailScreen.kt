package com.example.facturaapp.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.facturaapp.data.FacturaEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FacturaDetailScreen(
    factura: FacturaEntity,
    onBackClick: () -> Unit,
    onEditClick: (FacturaEntity) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalles de la Factura") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Volver")
                    }
                }
            )
        },
        content = { paddingValues ->
            LazyColumn(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Mostrar detalles de la factura
                items(getFacturaDetails(factura)) { detail ->
                    DetailItem(label = detail.first, value = detail.second)
                }

                // Espaciador
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Botón "Cerrar"
                item {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Button(onClick = onBackClick) {
                            Text("Cerrar")
                        }
                    }
                }

                // Botón "Actualizar"
                item {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Button(onClick = { onEditClick(factura) }) {
                            Text("Actualizar")
                        }
                    }
                }
            }
        }
    )
}

@Composable
fun DetailItem(label: String, value: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(text = label, style = MaterialTheme.typography.titleMedium)
            Text(text = value, style = MaterialTheme.typography.bodyLarge)
        }
    }
}

fun getFacturaDetails(factura: FacturaEntity): List<Pair<String, String>> {
    return listOf(
        "ID" to factura.id.toString(),
        "Número de Factura" to factura.numeroFactura,
        "Fecha de Emisión" to factura.fechaEmision,
        "Emisor - Empresa" to factura.emisor,
        "Emisor - NIF" to factura.emisorNIF,
        "Emisor - Dirección" to factura.emisorDireccion,
        "Receptor - Cliente" to factura.receptor,
        "Receptor - NIF" to factura.receptorNIF,
        "Receptor - Dirección" to factura.receptorDireccion,
        "Base Imponible (€)" to factura.baseImponible.toString(),
        "IVA (€)" to factura.iva.toString(),
        "Total (€)" to factura.total.toString()
    )
}
