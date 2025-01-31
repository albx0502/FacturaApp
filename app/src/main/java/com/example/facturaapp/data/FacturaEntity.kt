package com.example.facturaapp.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "facturas")
data class FacturaEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0, // Asegura que la ID se genere automáticamente
    val numeroFactura: String,
    val fechaEmision: String,
    val emisor: String,
    val emisorNIF: String,
    val emisorDireccion: String,
    val receptor: String,
    val receptorNIF: String,
    val receptorDireccion: String,
    val baseImponible: Double,
    val iva: Double,
    val total: Double
)
