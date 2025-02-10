package com.example.facturaapp.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "facturas")
data class FacturaEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
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
) {
    // Convertir FacturaEntity a un mapa para Firebase
    fun toMap(): Map<String, Any> {
        return mapOf(
            "id" to id,
            "numeroFactura" to numeroFactura,
            "fechaEmision" to fechaEmision,
            "emisor" to emisor,
            "emisorNIF" to emisorNIF,
            "emisorDireccion" to emisorDireccion,
            "receptor" to receptor,
            "receptorNIF" to receptorNIF,
            "receptorDireccion" to receptorDireccion,
            "baseImponible" to baseImponible,
            "iva" to iva,
            "total" to total
        )
    }
}
