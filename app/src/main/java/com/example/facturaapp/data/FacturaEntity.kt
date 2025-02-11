package com.example.facturaapp.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.firestore.Exclude

@Entity(tableName = "facturas")
data class FacturaEntity(
    @PrimaryKey(autoGenerate = true) var id: Int = 0,
    var numeroFactura: String = "",
    var fechaEmision: String = "",
    var emisor: String = "",
    var emisorNIF: String = "",
    var emisorDireccion: String = "",
    var receptor: String = "",
    var receptorNIF: String = "",
    var receptorDireccion: String = "",
    var baseImponible: Double = 0.0,
    var iva: Double = 0.0,
    var total: Double = 0.0
) {
    // Firebase necesita un constructor vacío sin argumentos
    constructor() : this(0, "", "", "", "", "", "", "", "", 0.0, 0.0, 0.0)

    // Convertir FacturaEntity a un mapa para Firebase
    @Exclude
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
