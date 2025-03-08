package com.example.facturaapp.data

import android.content.Context
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.net.Uri
import java.io.FileOutputStream
import java.io.IOException

fun generatePdf(context: Context, uri: Uri, factura: FacturaEntity?) {
    if (factura == null) return

    val pdfDocument = PdfDocument()
    val paint = Paint()
    val pageInfo = PdfDocument.PageInfo.Builder(600, 800, 1).create()
    val page = pdfDocument.startPage(pageInfo)
    val canvas = page.canvas

    paint.textSize = 16f
    canvas.drawText("Factura N.º: ${factura.numeroFactura}", 50f, 50f, paint)
    canvas.drawText("Fecha: ${factura.fechaEmision}", 50f, 80f, paint)
    canvas.drawText("Emisor: ${factura.emisor} (${factura.emisorNIF})", 50f, 110f, paint)
    canvas.drawText("Receptor: ${factura.receptor} (${factura.receptorNIF})", 50f, 140f, paint)
    canvas.drawText("Dirección del Emisor: ${factura.emisorDireccion}", 50f, 170f, paint)
    canvas.drawText("Dirección del Receptor: ${factura.receptorDireccion}", 50f, 200f, paint)
    canvas.drawText("Base Imponible: ${factura.baseImponible}€", 50f, 230f, paint)
    canvas.drawText("IVA: ${factura.iva}€", 50f, 260f, paint)
    canvas.drawText("Total: ${factura.total}€", 50f, 290f, paint)
    canvas.drawText("Tipo de Factura: ${factura.tipoFactura}", 50f, 320f, paint)

    pdfDocument.finishPage(page)

    try {
        context.contentResolver.openFileDescriptor(uri, "w")?.use { descriptor ->
            FileOutputStream(descriptor.fileDescriptor).use { outputStream ->
                pdfDocument.writeTo(outputStream)
            }
        }
    } catch (e: IOException) {
        e.printStackTrace()
    } finally {
        pdfDocument.close()
    }
}
