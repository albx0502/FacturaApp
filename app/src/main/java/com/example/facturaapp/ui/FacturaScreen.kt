package com.example.facturaapp.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.facturaapp.data.FacturaEntity
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FacturaScreen(
    viewModel: FacturaViewModel,
    onNavigateToList: () -> Unit
) {
    // Estados para cada campo del formulario
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

    // Actualizar los campos si hay una factura en edición
    LaunchedEffect(viewModel.facturaToEdit.collectAsState().value) {
        viewModel.facturaToEdit.value?.let { factura ->
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

    // Cálculo automático del IVA y total
    val iva = if (baseImponible.isNotEmpty() && ivaPorcentaje.isNotEmpty()) {
        (baseImponible.toDoubleOrNull() ?: 0.0) * (ivaPorcentaje.toDoubleOrNull() ?: 0.0) / 100
    } else 0.0
    val total = if (baseImponible.isNotEmpty()) {
        (baseImponible.toDoubleOrNull() ?: 0.0) + iva
    } else 0.0

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
                // Validación: evitar agregar facturas con campos vacíos
                if (fechaEmision.isNotBlank() &&
                    emisorEmpresa.isNotBlank() &&
                    receptorCliente.isNotBlank()
                ) {
                    viewModel.saveFactura(
                        FacturaEntity(
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
                    )
                    // Limpiar campos después de guardar
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
                    // Mostrar un mensaje de error si faltan campos obligatorios
                    println("Por favor, completa todos los campos obligatorios.")
                }
            }) {
                Icon(imageVector = Icons.Default.Check, contentDescription = "Guardar Factura")
            }
        },
        content = { paddingValues ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(16.dp)
            ) {
                // Sección: Información del Emisor
                item {
                    SectionHeader(title = "Información del Emisor")
                }
                item {
                    FieldCard(label = "Emisor - Empresa", value = emisorEmpresa) { emisorEmpresa = it }
                }
                item {
                    FieldCard(label = "Emisor - NIF", value = emisorNIF) { emisorNIF = it }
                }
                item {
                    FieldCard(label = "Emisor - Dirección", value = emisorDireccion) { emisorDireccion = it }
                }

                // Sección: Información del Receptor
                item {
                    SectionHeader(title = "Información del Receptor")
                }
                item {
                    FieldCard(label = "Receptor - Cliente", value = receptorCliente) { receptorCliente = it }
                }
                item {
                    FieldCard(label = "Receptor - NIF", value = receptorNIF) { receptorNIF = it }
                }
                item {
                    FieldCard(label = "Receptor - Dirección", value = receptorDireccion) { receptorDireccion = it }
                }

                // Sección: Detalles de la Factura
                item {
                    SectionHeader(title = "Detalles de la Factura")
                }
                item {
                    DatePickerField(label = "Fecha de Emisión", value = fechaEmision) { fechaEmision = it }
                }
                item {
                    FieldCard(label = "Base Imponible (€)", value = baseImponible, keyboardType = KeyboardType.Number) {
                        baseImponible = it
                    }
                }
                item {
                    FieldCard(label = "IVA (%)", value = ivaPorcentaje, keyboardType = KeyboardType.Number) {
                        ivaPorcentaje = it
                    }
                }
                item {
                    Text(
                        text = "Total (€): $total",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
            }
        }
    )
}

@Composable
fun DatePickerField(label: String, value: String, onValueChange: (String) -> Unit) {
    val calendar = Calendar.getInstance()
    val formatter = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        trailingIcon = {
            IconButton(onClick = {
                onValueChange(formatter.format(calendar.time))
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
