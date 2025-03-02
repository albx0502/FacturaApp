package com.example.facturaapp.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
fun FacturaDetailScreen(
    facturaId: String,
    viewModel: FacturaViewModel,
    authViewModel: AuthViewModel,
    navController: NavController
) {
    val isUserLoggedIn by authViewModel.isUserLoggedIn.collectAsStateWithLifecycle()

    LaunchedEffect(isUserLoggedIn) {
        if (!isUserLoggedIn) {
            navController.navigate("login") {
                popUpTo("facturaDetail") { inclusive = true }
                launchSingleTop = true
            }
        }
    }

    val factura by viewModel.getFacturaById(facturaId).collectAsState(initial = null)
    var showDeleteDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalles de la Factura") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { paddingValues ->
        if (!isUserLoggedIn) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            if (factura == null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                val facturaData = remember(factura) { getFacturaDetails(factura!!) }
                LazyColumn(
                    modifier = Modifier
                        .padding(paddingValues)
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(facturaData) { detail ->
                        DetailItem(label = detail.first, value = detail.second)
                    }
                    item {
                        ActionButtons(
                            factura = factura!!,
                            onBackClick = { navController.popBackStack() },
                            onEditClick = { factura ->
                                viewModel.editFactura(factura) // ✅ Precargar la factura antes de editar
                                navController.navigate("facturaForm/${factura.id}") // ✅ Pasamos el ID correctamente
                            },
                            onDeleteClick = { showDeleteDialog = true }
                        )
                    }

                }

                if (showDeleteDialog) {
                    ConfirmDeleteDialog(
                        onConfirm = {
                            viewModel.deleteFactura(factura!!)
                            showDeleteDialog = false
                            navController.popBackStack()
                        },
                        onDismiss = { showDeleteDialog = false }
                    )
                }
            }
        }
    }
}


/**
 * Crea una lista de pares (label, value) con la información de la factura.
 */
fun getFacturaDetails(factura: FacturaEntity): List<Pair<String, String>> {
    val decimalFormat = NumberFormat.getNumberInstance(Locale("es", "ES")).apply {
        minimumFractionDigits = 2
        maximumFractionDigits = 2
    }
    return listOf(
        "Documento ID" to factura.id,
        "Número de Factura" to factura.numeroFactura,
        "Fecha de Emisión" to factura.fechaEmision,
        "Emisor" to "${factura.emisor} (${factura.emisorNIF})",
        "Dirección del Emisor" to factura.emisorDireccion,
        "Receptor" to "${factura.receptor} (${factura.receptorNIF})",
        "Dirección del Receptor" to factura.receptorDireccion,
        "Base Imponible (€)" to decimalFormat.format(factura.baseImponible),
        "IVA (€)" to decimalFormat.format(factura.iva),
        "Total (€)" to decimalFormat.format(factura.total),
        "Tipo de Factura" to factura.tipoFactura
    )
}

/**
 * Componente para mostrar un elemento de detalle con estilo.
 */
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

/**
 * Botones de acción en la pantalla de detalles.
 */
@Composable
fun ActionButtons(
    factura: FacturaEntity,
    onBackClick: () -> Unit,
    onEditClick: (FacturaEntity) -> Unit,
    onDeleteClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(onClick = onBackClick) { Text("Cerrar") }
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = { onEditClick(factura) }) { Text("Actualizar") }
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = onDeleteClick,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.error,
                contentColor = MaterialTheme.colorScheme.onError
            )
        ) {
            Text("Eliminar")
        }
    }
}

/**
 * Diálogo de confirmación de eliminación.
 */
@Composable
fun ConfirmDeleteDialog(onConfirm: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Confirmar Eliminación") },
        text = { Text("¿Estás seguro de que deseas eliminar esta factura? Esta acción no se puede deshacer.") },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("Eliminar", color = MaterialTheme.colorScheme.error)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}
