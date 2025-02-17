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
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

/**
 * Pantalla de creación/edición de Facturas.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FacturaScreen(
    viewModel: FacturaViewModel,
    onNavigateToList: () -> Unit
) {
    // Estados para campos
    var numeroFactura by remember { mutableStateOf("") }
    var fechaEmision by remember { mutableStateOf("") }
    var emisorEmpresa by remember { mutableStateOf("") }
    var emisorNIF by remember { mutableStateOf("") }
    var emisorDireccion by remember { mutableStateOf("") }
    var receptorCliente by remember { mutableStateOf("") }
    var receptorNIF by remember { mutableStateOf("") }
    var receptorDireccion by remember { mutableStateOf("") }
    var baseImponible by remember { mutableStateOf("") }
    var ivaPorcentaje by remember { mutableStateOf("0") }
    var tipoFactura by remember { mutableStateOf("Emitida") }

    // SnackBar para mensajes
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Observamos la factura en edición
    val facturaToEdit by viewModel.facturaToEdit.collectAsState()

    // Cuando entra en modo edición, rellenamos campos
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

            val base = factura.baseImponible
            val ivaVal = factura.iva
            val porcentaje = if (base != 0.0) ((ivaVal / base) * 100).toInt() else 0
            ivaPorcentaje = porcentaje.toString()

            tipoFactura = factura.tipoFactura
        }
    }

    // Observamos mensajes de ViewModel
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
    val ivaCalculado by remember(baseImponible, ivaPorcentaje) {
        derivedStateOf {
            val baseDouble = baseImponible.toDoubleOrNull() ?: 0.0
            val ivaDouble = ivaPorcentaje.toDoubleOrNull() ?: 0.0
            (baseDouble * ivaDouble) / 100
        }
    }
    val total by remember(baseImponible, ivaCalculado) {
        derivedStateOf {
            (baseImponible.toDoubleOrNull() ?: 0.0) + ivaCalculado
        }
    }

    // Formateamos el total con coma
    val decimalFormat = NumberFormat.getNumberInstance(Locale("es", "ES")).apply {
        minimumFractionDigits = 2
        maximumFractionDigits = 2
    }
    val totalFormateado = decimalFormat.format(total)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Gestión de Facturas") },
                actions = {
                    IconButton(onClick = onNavigateToList) {
                        Icon(imageVector = Icons.Default.List, contentDescription = "Ver Lista")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                // Construimos FacturaEntity según si es edición o creación
                val newOrEditedFactura = facturaToEdit?.copy(
                    numeroFactura = numeroFactura,
                    fechaEmision = fechaEmision,
                    emisor = emisorEmpresa,
                    emisorNIF = emisorNIF,
                    emisorDireccion = emisorDireccion,
                    receptor = receptorCliente,
                    receptorNIF = receptorNIF,
                    receptorDireccion = receptorDireccion,
                    baseImponible = baseImponible.toDoubleOrNull() ?: 0.0,
                    iva = ivaCalculado,
                    total = total,
                    tipoFactura = tipoFactura
                ) ?: FacturaEntity(
                    id = "", // El ID se generará en Firestore
                    numeroFactura = if (numeroFactura.isBlank()) UUID.randomUUID().toString() else numeroFactura,
                    fechaEmision = fechaEmision,
                    emisor = emisorEmpresa,
                    emisorNIF = emisorNIF,
                    emisorDireccion = emisorDireccion,
                    receptor = receptorCliente,
                    receptorNIF = receptorNIF,
                    receptorDireccion = receptorDireccion,
                    baseImponible = baseImponible.toDoubleOrNull() ?: 0.0,
                    iva = ivaCalculado,
                    total = total,
                    tipoFactura = tipoFactura
                )

                // Guardamos la factura
                viewModel.saveFactura(newOrEditedFactura)

                // Salir del modo edición o limpiar campos
                if (facturaToEdit != null) {
                    viewModel.editFactura(null)
                } else {
                    // Limpiar
                    numeroFactura = ""
                    fechaEmision = ""
                    emisorEmpresa = ""
                    emisorNIF = ""
                    emisorDireccion = ""
                    receptorCliente = ""
                    receptorNIF = ""
                    receptorDireccion = ""
                    baseImponible = ""
                    ivaPorcentaje = "0"
                    tipoFactura = "Emitida"
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
                        DatePickerField(label = "Fecha de Emisión", value = fechaEmision) {
                            fechaEmision = it
                        }
                    }
                    item {
                        FieldCard(
                            label = "Base Imponible (€)",
                            value = baseImponible,
                            keyboardType = KeyboardType.Number
                        ) { baseImponible = it }
                    }
                    item {
                        IvaDropdownField(
                            selectedIva = ivaPorcentaje,
                            onIvaSelected = { newIva -> ivaPorcentaje = newIva }
                        )
                    }
                    item {
                        TipoFacturaDropdownField(
                            selectedTipo = tipoFactura,
                            onTipoSelected = { tipoFactura = it }
                        )
                    }
                    item {
                        Text(
                            text = "Total (€): $totalFormateado",
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                }

                // Snackbar
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
fun FieldCard(
    label: String,
    value: String,
    keyboardType: KeyboardType = KeyboardType.Text,
    onValueChange: (String) -> Unit
) {
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IvaDropdownField(
    selectedIva: String,
    onIvaSelected: (String) -> Unit
) {
    val ivaValues = listOf("0", "4", "10", "21")
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = selectedIva,
            onValueChange = {},
            label = { Text("IVA (%)") },
            readOnly = true,
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
                .padding(8.dp)
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            ivaValues.forEach { value ->
                DropdownMenuItem(
                    text = { Text(value) },
                    onClick = {
                        onIvaSelected(value)
                        expanded = false
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TipoFacturaDropdownField(
    selectedTipo: String,
    onTipoSelected: (String) -> Unit
) {
    val tipos = listOf("Emitida", "Recibida")
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = selectedTipo,
            onValueChange = {},
            label = { Text("Tipo de Factura") },
            readOnly = true,
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
                .padding(8.dp)
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            tipos.forEach { tipo ->
                DropdownMenuItem(
                    text = { Text(tipo) },
                    onClick = {
                        onTipoSelected(tipo)
                        expanded = false
                    }
                )
            }
        }
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
