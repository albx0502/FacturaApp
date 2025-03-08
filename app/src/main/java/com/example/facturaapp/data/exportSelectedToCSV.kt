package com.example.facturaapp.data

import android.content.Context
import android.os.Environment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.io.File
import java.io.FileWriter
import java.io.IOException

fun exportSelectedToCSV(context: Context, selectedInvoices: List<String>) {
    val db = FirebaseFirestore.getInstance()
    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return // Asegura que hay un usuario autenticado

    // Acceder a las facturas dentro del usuario autenticado
    db.collection("usuarios").document(userId).collection("facturas")
        .whereIn("id_factura", selectedInvoices)
        .get()
        .addOnSuccessListener { documents ->
            val fileName = "facturas_export.csv"
            val directory = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
            val file = File(directory, fileName)

            try {
                val writer = FileWriter(file)
                writer.append("Fecha,Número Factura,Base Imponible,IVA,IRPF,Total,CIF Cliente,Nombre Cliente\n")

                for (document in documents) {
                    val fecha = document.getString("fechaEmision") ?: "N/A"
                    val numero = document.getString("numeroFactura") ?: "N/A"
                    val baseImponible = document.getDouble("baseImponible") ?: 0.0
                    val iva = document.getDouble("iva") ?: 0.0
                    val total = document.getDouble("total") ?: 0.0
                    val emisor = document.getString("emisor") ?: "N/A"
                    val receptor = document.getString("receptor") ?: "N/A"

                    writer.append("$fecha,$numero,$baseImponible,$iva,$total,$emisor,$receptor\n")
                }

                writer.flush()
                writer.close()

                println("✅ Exportación a CSV completada: ${file.absolutePath}")
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        .addOnFailureListener { e ->
            e.printStackTrace()
        }
}
