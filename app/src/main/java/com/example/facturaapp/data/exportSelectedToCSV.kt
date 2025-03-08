package com.example.facturaapp.data

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.ActivityResultLauncher
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.io.OutputStreamWriter

// Guarda la referencia del Intent para la exportación
private var exportCsvLauncher: ActivityResultLauncher<Intent>? = null
private var csvContent: String? = null // Para almacenar temporalmente los datos CSV antes de guardarlos

fun exportSelectedToCSV(
    context: Activity,
    selectedInvoices: List<String>,
    onSuccess: (String) -> Unit,
    onError: () -> Unit
) {
    val db = FirebaseFirestore.getInstance()
    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return onError()

    db.collection("usuarios").document(userId).collection("facturas")
        .whereIn("id_factura", selectedInvoices)
        .get()
        .addOnSuccessListener { documents ->
            val stringBuilder = StringBuilder()
            stringBuilder.append("Fecha,Número Factura,Base Imponible,IVA,Total,Emisor,Receptor\n")

            for (document in documents) {
                val fecha = document.getString("fechaEmision") ?: "N/A"
                val numero = document.getString("numeroFactura") ?: "N/A"
                val baseImponible = document.getDouble("baseImponible") ?: 0.0
                val iva = document.getDouble("iva") ?: 0.0
                val total = document.getDouble("total") ?: 0.0
                val emisor = document.getString("emisor") ?: "N/A"
                val receptor = document.getString("receptor") ?: "N/A"

                stringBuilder.append("$fecha,$numero,$baseImponible,$iva,$total,$emisor,$receptor\n")
            }

            onSuccess(stringBuilder.toString()) // Llama a la función de éxito con el CSV generado
        }
        .addOnFailureListener { e ->
            e.printStackTrace()
            onError() // Llama a la función de error si falla
        }
}




fun saveCsvToUri(context: Context, uri: Uri, csvContent: String) {
    try {
        context.contentResolver.openOutputStream(uri)?.use { outputStream ->
            OutputStreamWriter(outputStream).use { writer ->
                writer.write(csvContent)
                writer.flush()
            }
        }
        println("CSV guardado en: $uri")
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

