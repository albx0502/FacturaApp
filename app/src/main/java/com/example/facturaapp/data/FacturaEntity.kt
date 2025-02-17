package com.example.facturaapp.data

import com.google.firebase.firestore.Exclude

/**
 * Representa una Factura en Firestore.
 *
 * - 'id' => ID autogenerado (String).
 * - 'numeroFactura', 'fechaEmision', etc. => campos de la factura.
 * - 'tipoFactura' => "Emitida" o "Recibida".
 */
data class FacturaEntity(
    var id: String = "",
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
    var total: Double = 0.0,
    var tipoFactura: String = "Emitida"
) {
    // Constructor vacío requerido por Firebase
    constructor() : this(
        "",
        "", "", "",
        "", "", "", "", "",
        0.0, 0.0, 0.0, "Emitida"
    )

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
            "total" to total,
            "tipoFactura" to tipoFactura
        )
    }
}
