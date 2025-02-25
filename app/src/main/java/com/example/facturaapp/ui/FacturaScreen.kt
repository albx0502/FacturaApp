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
import androidx.compose.runtime.saveable.rememberSaveable
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
    facturaId: String?,
    onNavigateToList: () -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Estados guardados incluso si la pantalla se recomponen o rota el dispositivo.
    var numeroFactura by rememberSaveable { mutableStateOf("") }
    var fechaEmision by rememberSaveable { mutableStateOf("") }
    var emisorEmpresa by rememberSaveable { mutableStateOf("") }
    var emisorNIF by rememberSaveable { mutableStateOf("") }
    var emisorDireccion by rememberSaveable { mutableStateOf("") }
    var receptorCliente by rememberSaveable { mutableStateOf("") }
    var receptorNIF by rememberSaveable { mutableStateOf("") }
    var receptorDireccion by rememberSaveable { mutableStateOf("") }
    var baseImponible by rememberSaveable { mutableStateOf("") }
    var ivaPorcentaje by rememberSaveable { mutableStateOf("0") }
    var tipoFactura by rememberSaveable { mutableStateOf("Emitida") }

    val facturaToEdit by viewModel.getFacturaById(facturaId ?: "").collectAsState(initial = null)
    val uiMessage by viewModel.uiMessage.collectAsState()

    // Actualizar campos si se está editando una factura
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
            ivaPorcentaje = ((factura.iva / factura.baseImponible) * 100).toInt().toString()
            tipoFactura = factura.tipoFactura
        }
    }

    // Manejo de mensajes en Snackbar
    LaunchedEffect(uiMessage) {
        uiMessage?.let {
            scope.launch {
                snackbarHostState.showSnackbar(it)
                viewModel.clearMessage()
            }
        }
    }

    // Cálculo de IVA y total
    val baseDouble = baseImponible.toDoubleOrNull() ?: 0.0
    val ivaDouble = ivaPorcentaje.toDoubleOrNull() ?: 0.0
    val ivaCalculado = (baseDouble * ivaDouble) / 100
    val total = baseDouble + ivaCalculado
    val totalFormateado = NumberFormat.getNumberInstance(Locale("es", "ES")).apply {
        minimumFractionDigits = 2
        maximumFractionDigits = 2
    }.format(total)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (facturaId != null) "Editar Factura" else "Crear Factura") },
                actions = {
                    IconButton(onClick = onNavigateToList) {
                        Icon(imageVector = Icons.Default.List, contentDescription = "Ver Lista")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                handleSaveFactura(
                    numeroFactura, fechaEmision, emisorEmpresa, emisorNIF, emisorDireccion,
                    receptorCliente, receptorNIF, receptorDireccion, baseDouble, ivaCalculado,
                    total, tipoFactura, facturaToEdit, viewModel
                )
            }) {
                Icon(imageVector = Icons.Default.Check, contentDescription = "Guardar Factura")
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item { SectionHeader("Información del Emisor") }
            item { FieldCard("Emisor - Empresa", emisorEmpresa) { emisorEmpresa = it } }
            item { FieldCard("Emisor - NIF", emisorNIF) { emisorNIF = it } }
            item { FieldCard("Emisor - Dirección", emisorDireccion) { emisorDireccion = it } }

            item { SectionHeader("Información del Receptor") }
            item { FieldCard("Receptor - Cliente", receptorCliente) { receptorCliente = it } }
            item { FieldCard("Receptor - NIF", receptorNIF) { receptorNIF = it } }
            item { FieldCard("Receptor - Dirección", receptorDireccion) { receptorDireccion = it } }

            item { SectionHeader("Detalles de la Factura") }
            item { DatePickerField("Fecha de Emisión", fechaEmision) { fechaEmision = it } }
            item { FieldCard("Base Imponible (€)", baseImponible, KeyboardType.Number) { baseImponible = it } }
            item { IvaDropdownField(ivaPorcentaje) { ivaPorcentaje = it } }
            item { TipoFacturaDropdownField(tipoFactura) { tipoFactura = it } }
            item {
                Text(
                    text = "Total (€): $totalFormateado",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
        }
    }
}

/**
 * Guarda o edita una factura de manera optimizada.
 */
fun handleSaveFactura(
    numeroFactura: String, fechaEmision: String, emisor: String, emisorNIF: String, emisorDireccion: String,
    receptor: String, receptorNIF: String, receptorDireccion: String, baseImponible: Double,
    iva: Double, total: Double, tipoFactura: String, facturaToEdit: FacturaEntity?, viewModel: FacturaViewModel
) {
    val factura = facturaToEdit?.copy(
        numeroFactura = numeroFactura,
        fechaEmision = fechaEmision,
        emisor = emisor,
        emisorNIF = emisorNIF,
        emisorDireccion = emisorDireccion,
        receptor = receptor,
        receptorNIF = receptorNIF,
        receptorDireccion = receptorDireccion,
        baseImponible = baseImponible,
        iva = iva,
        total = total,
        tipoFactura = tipoFactura
    ) ?: FacturaEntity("", numeroFactura, fechaEmision, emisor, emisorNIF, emisorDireccion, receptor, receptorNIF, receptorDireccion, baseImponible, iva, total, tipoFactura)

    viewModel.saveFactura(factura)
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
