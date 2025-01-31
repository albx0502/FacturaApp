package com.example.facturaapp.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.facturaapp.data.FacturaEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FacturaDetailScreen(
    factura: FacturaEntity,
    onBackClick: () -> Unit,
    onEditClick: (FacturaEntity) -> Unit,
    onDeleteClick: (FacturaEntity) -> Unit
) {

    var showDeleteConfirmation by remember { mutableStateOf(false) }

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
                // Botón "Eliminar"
                item {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Button(
                            onClick = { showDeleteConfirmation = true },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.error
                            )
                        ) {
                            Text("Eliminar")
                        }
                    }
                }
            }
        }
    )
    // Diálogo de confirmación de eliminación
    if (showDeleteConfirmation) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmation = false },
            title = { Text("Confirmar Eliminación") },
            text = { Text("¿Estás seguro de que deseas eliminar esta factura? Esta acción no se puede deshacer.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDeleteClick(factura)
                        showDeleteConfirmation = false
                        onBackClick() // Vuelve a la pantalla anterior después de eliminar
                    }
                ) {
                    Text("Eliminar", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmation = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
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
