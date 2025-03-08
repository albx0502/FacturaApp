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
import com.example.facturaapp.data.exportSelectedToCSV
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.emptyFlow
import java.text.NumberFormat
import java.util.*
import androidx.compose.ui.platform.LocalContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FacturaListScreen(
    viewModel: FacturaViewModel,
    authViewModel: AuthViewModel,
    navController: NavController,
    onFacturaClick: (FacturaEntity) -> Unit,
    onNavigateToForm: () -> Unit,
) {
    val context = LocalContext.current
    val isUserLoggedIn by authViewModel.isUserLoggedIn.collectAsStateWithLifecycle()

    val selectedFacturas = remember { mutableStateListOf<String>() }


    LaunchedEffect(isUserLoggedIn) {
        delay(500)
        if (!isUserLoggedIn && FirebaseAuth.getInstance().currentUser == null) {
            navController.navigate("login") {
                popUpTo("list") { inclusive = true }
                launchSingleTop = true
            }
        }
    }

    val facturas by viewModel.facturas.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Listado de Facturas") },
                actions = {
                    if (isUserLoggedIn) {
                        IconButton(onClick = onNavigateToForm) {
                            Icon(imageVector = Icons.Default.Add, contentDescription = "Agregar Factura")
                        }
                        IconButton(
                            onClick = {
                                authViewModel.signOut()
                                viewModel.clearFacturasOnLogout()
                                navController.navigate("login") {
                                    popUpTo("list") { inclusive = true }
                                    launchSingleTop = true
                                }
                            }
                        ) {
                            Icon(imageVector = Icons.Default.ExitToApp, contentDescription = "Cerrar Sesión")
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            if(selectedFacturas.isNotEmpty()){
                FloatingActionButton(
                    onClick = {
                        exportSelectedToCSV(context, selectedFacturas)
                    }
                ) {
                    Icon(Icons.Default.ExitToApp, contentDescription = "Exportar a CSV")
                }
            }
        }
    ) { paddingValues ->
        if (isUserLoggedIn) {
            Column(modifier = Modifier.padding(paddingValues)) {
                if (facturas.isEmpty()) {
                    EmptyStateMessage()
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(16.dp)
                    ) {
                        items(facturas, key = { it.id }) { factura ->
                            FacturaCard(
                                factura = factura,
                                onFacturaClick = onFacturaClick,
                                selectedFacturas=selectedFacturas
                            )
                        }
                    }
                }
            }
        } else {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
    }
}

@Composable
fun EmptyStateMessage() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "No hay facturas disponibles.", style = MaterialTheme.typography.bodyLarge)
    }
}

@Composable
fun FacturaCard(
    factura: FacturaEntity,
    onFacturaClick: (FacturaEntity) -> Unit,
    selectedFacturas: MutableList<String>
) {
    val decimalFormat = NumberFormat.getNumberInstance(Locale("es", "ES"))
    val totalFormatted = decimalFormat.format(factura.total)
    val isSelected = remember { mutableStateOf(selectedFacturas.contains(factura.id)) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onFacturaClick(factura) }
            .padding(4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = isSelected.value,
                onCheckedChange = { checked ->
                    isSelected.value = checked
                    if (checked) {
                        selectedFacturas.add(factura.id)
                    } else {
                        selectedFacturas.remove(factura.id)
                    }
                }
            )
            Column(modifier = Modifier.weight(1f)) {
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
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}
