package com.example.facturaapp.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.facturaapp.data.FacturaEntity
import java.text.NumberFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FacturaListScreen(
    viewModel: FacturaViewModel,
    authViewModel: AuthViewModel,
    navController: NavController,
    onFacturaClick: (FacturaEntity) -> Unit,
    onNavigateToForm: () -> Unit,
) {
    val facturas by viewModel.facturas.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Listado de Facturas") },
                actions = {
                    IconButton(onClick = onNavigateToForm) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = "Agregar Factura")
                    }
                    IconButton(
                        onClick = {
                            authViewModel.signOut()
                            navController.navigate("login") {
                                popUpTo("list") { inclusive = true }
                                launchSingleTop = true
                            }
                        }
                    ) {
                        Icon(imageVector = Icons.Default.ExitToApp, contentDescription = "Cerrar Sesión")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            if (facturas.isEmpty()) {
                EmptyStateMessage(paddingValues)
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    items(facturas, key = { it.id }) { factura ->
                        FacturaCard(factura, onFacturaClick)
                    }
                }
            }
        }
    }
}

@Composable
fun EmptyStateMessage(paddingValues: PaddingValues) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "No hay facturas disponibles.", style = MaterialTheme.typography.bodyLarge)
    }
}

@Composable
fun FacturaCard(
    factura: FacturaEntity,
    onFacturaClick: (FacturaEntity) -> Unit
) {
    val decimalFormat = remember { NumberFormat.getNumberInstance(Locale("es", "ES")) } // 🚀 Optimización
    val totalFormatted = remember { decimalFormat.format(factura.total) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onFacturaClick(factura) }
            .padding(4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Factura N.º: ${factura.numeroFactura}",
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = "Emisor: ${factura.emisor}",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Receptor: ${factura.receptor}",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Total: $totalFormatted €",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.align(Alignment.End)
            )
        }
    }
}
