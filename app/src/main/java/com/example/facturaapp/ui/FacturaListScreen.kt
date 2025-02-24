package com.example.facturaapp.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.facturaapp.data.FacturaEntity
import java.text.NumberFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FacturaListScreen(
    viewModel: FacturaViewModel,
    authViewModel: AuthViewModel,
    navController: androidx.navigation.NavController, // Agrega NavController como parámetro
    onFacturaClick: (FacturaEntity) -> Unit,
    onNavigateToForm: () -> Unit,
) {
    val facturas by viewModel.facturas.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Listado de Facturas") },
                actions = {
                    IconButton(
                        onClick = onNavigateToForm,
                        modifier = Modifier.padding(end = 8.dp) // Espacio a la derecha
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = "Agregar Factura")
                    }
                    IconButton(
                        onClick = {
                            authViewModel.signOut()

                            // Asegura que Firebase ha cerrado sesión antes de navegar
                            if (authViewModel.authState.value == null) {
                                navController.navigate("login") {
                                    popUpTo("list") { inclusive = true }
                                    launchSingleTop = true
                                }
                            }
                        },
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Icon(imageVector = Icons.Default.ExitToApp, contentDescription = "Cerrar Sesión")
                    }


                }
            )
        }
    ) { paddingValues ->
        if (facturas.isEmpty()) {
            EmptyStateMessage(paddingValues)
        } else {
            FacturaListContent(facturas, onFacturaClick, paddingValues)
        }
    }
}


/**
 * Muestra un mensaje cuando no hay facturas disponibles.
 */
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

/**
 * Muestra la lista de facturas usando LazyColumn.
 */
@Composable
fun FacturaListContent(
    facturas: List<FacturaEntity>,
    onFacturaClick: (FacturaEntity) -> Unit,
    paddingValues: PaddingValues
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(16.dp)
    ) {
        items(facturas) { factura ->
            FacturaCard(factura, onFacturaClick)
        }
    }
}

/**
 * Tarjeta que representa una factura en la lista.
 */
@Composable
fun FacturaCard(
    factura: FacturaEntity,
    onFacturaClick: (FacturaEntity) -> Unit
) {
    val decimalFormat = NumberFormat.getNumberInstance(Locale("es", "ES")).apply {
        minimumFractionDigits = 2
        maximumFractionDigits = 2
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onFacturaClick(factura) }
            .padding(4.dp), // Espaciado uniforme
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
                text = "Total: ${decimalFormat.format(factura.total)} €",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.align(Alignment.End)
            )
        }
    }
}
