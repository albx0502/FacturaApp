package com.example.facturaapp.ui

import android.app.DatePickerDialog
import android.widget.DatePicker
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.facturaapp.data.FacturaEntity
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FacturaScreen(
    viewModel: FacturaViewModel,
    onNavigateToList: () -> Unit
) {
    // Estados para los campos del formulario
    var numeroFactura by remember { mutableStateOf("") }
    var fechaEmision by remember { mutableStateOf("") }
    var emisorEmpresa by remember { mutableStateOf("") }
    var emisorNIF by remember { mutableStateOf("") }
    var emisorDireccion by remember { mutableStateOf("") }
    var receptorCliente by remember { mutableStateOf("") }
    var receptorNIF by remember { mutableStateOf("") }
    var receptorDireccion by remember { mutableStateOf("") }
    var baseImponible by remember { mutableStateOf("") }
    var ivaPorcentaje by remember { mutableStateOf("") }

    // SnackBar para mensajes flotantes
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Observando los cambios en factura para editar
    val facturaToEdit by viewModel.facturaToEdit.collectAsState()
    LaunchedEffect(facturaToEdit) {
        facturaToEdit?.let { factura ->
            numeroFactura = factura.numeroFactura
            fechaEmision = factura.fechaEmision
            emisorEmpresa = factura.emisor
            emisorNIF = factura.emisorNIF
            emisorDireccion = factura.emisorDireccion
            receptorCliente = factura.receptor
            receptorNIF = factura.receptorNIF
            receptorDireccion = factura.receptorDireccion
            baseImponible = factura.baseImponible.toString()
            ivaPorcentaje = ((factura.iva / factura.baseImponible) * 100).toString()
        }
    }

    // Mostrando mensajes en el SnackBar
    val uiMessage by viewModel.uiMessage.collectAsState()
    LaunchedEffect(uiMessage) {
        uiMessage?.let {
            scope.launch {
                snackbarHostState.showSnackbar(it)
                viewModel.clearMessage()
            }
        }
    }

    // Cálculo dinámico de IVA y total
    val iva by remember(baseImponible, ivaPorcentaje) {
        derivedStateOf {
            (baseImponible.toDoubleOrNull() ?: 0.0) * (ivaPorcentaje.toDoubleOrNull() ?: 0.0) / 100
        }
    }
    val total by remember(baseImponible, iva) {
        derivedStateOf {
            (baseImponible.toDoubleOrNull() ?: 0.0) + iva
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Gestión de Facturas") },
                actions = {
                    IconButton(onClick = onNavigateToList) {
                        Icon(imageVector = Icons.Default.List, contentDescription = "Ver Lista de Facturas")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                if (fechaEmision.isNotBlank() &&
                    emisorEmpresa.isNotBlank() &&
                    receptorCliente.isNotBlank()
                ) {
                    val updatedFactura = facturaToEdit?.copy(
                        numeroFactura = numeroFactura,
                        fechaEmision = fechaEmision,
                        emisor = emisorEmpresa,
                        emisorNIF = emisorNIF,
                        emisorDireccion = emisorDireccion,
                        receptor = receptorCliente,
                        receptorNIF = receptorNIF,
                        receptorDireccion = receptorDireccion,
                        baseImponible = baseImponible.toDoubleOrNull() ?: 0.0,
                        iva = iva,
                        total = total
                    ) ?: FacturaEntity(
                        numeroFactura = numeroFactura.ifEmpty { UUID.randomUUID().toString() },
                        fechaEmision = fechaEmision,
                        emisor = emisorEmpresa,
                        emisorNIF = emisorNIF,
                        emisorDireccion = emisorDireccion,
                        receptor = receptorCliente,
                        receptorNIF = receptorNIF,
                        receptorDireccion = receptorDireccion,
                        baseImponible = baseImponible.toDoubleOrNull() ?: 0.0,
                        iva = iva,
                        total = total
                    )

                    viewModel.saveFactura(updatedFactura)

                    if (facturaToEdit == null) {
                        numeroFactura = ""
                        fechaEmision = ""
                        emisorEmpresa = ""
                        emisorNIF = ""
                        emisorDireccion = ""
                        receptorCliente = ""
                        receptorNIF = ""
                        receptorDireccion = ""
                        baseImponible = ""
                        ivaPorcentaje = ""
                    } else {
                        viewModel.editFactura(null)
                    }
                } else {
                    viewModel.setUiMessage("Por favor, completa todos los campos obligatorios.")
                }
            }) {
                Icon(imageVector = Icons.Default.Check, contentDescription = "Guardar Factura")
            }
        },
        content = { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item { SectionHeader(title = "Información del Emisor") }
                    item { FieldCard(label = "Emisor - Empresa", value = emisorEmpresa) { emisorEmpresa = it } }
                    item { FieldCard(label = "Emisor - NIF", value = emisorNIF) { emisorNIF = it } }
                    item { FieldCard(label = "Emisor - Dirección", value = emisorDireccion) { emisorDireccion = it } }

                    item { SectionHeader(title = "Información del Receptor") }
                    item { FieldCard(label = "Receptor - Cliente", value = receptorCliente) { receptorCliente = it } }
                    item { FieldCard(label = "Receptor - NIF", value = receptorNIF) { receptorNIF = it } }
                    item { FieldCard(label = "Receptor - Dirección", value = receptorDireccion) { receptorDireccion = it } }

                    item { SectionHeader(title = "Detalles de la Factura") }
                    item {
                        DatePickerField(label = "Fecha de Emisión", value = fechaEmision) { fechaEmision = it }
                    }
                    item {
                        FieldCard(
                            label = "Base Imponible (€)",
                            value = baseImponible,
                            keyboardType = KeyboardType.Number
                        ) { baseImponible = it }
                    }
                    item {
                        FieldCard(
                            label = "IVA (%)",
                            value = ivaPorcentaje,
                            keyboardType = KeyboardType.Number
                        ) { ivaPorcentaje = it }
                    }
                    item {
                        Text(
                            text = "Total (€): $total",
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                }

                SnackbarHost(
                    hostState = snackbarHostState,
                    modifier = Modifier.align(Alignment.BottomCenter)
                )
            }
        }
    )
}

@Composable
fun DatePickerField(label: String, value: String, onValueChange: (String) -> Unit) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()
    val formatter = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())

    OutlinedTextField(
        value = value,
        onValueChange = { onValueChange(it) },
        label = { Text(label) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        trailingIcon = {
            IconButton(onClick = {
                DatePickerDialog(
                    context,
                    { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
                        calendar.set(year, month, dayOfMonth)
                        onValueChange(formatter.format(calendar.time))
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
                ).show()
            }) {
                Icon(imageVector = Icons.Default.Check, contentDescription = "Seleccionar Fecha")
            }
        }
    )
}

@Composable
fun FieldCard(label: String, value: String, keyboardType: KeyboardType = KeyboardType.Text, onValueChange: (String) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(label) },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = keyboardType),
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        )
    }
}

@Composable
fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        modifier = Modifier.padding(vertical = 8.dp)
    )
}
