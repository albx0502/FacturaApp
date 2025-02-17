package com.example.facturaapp.data

import com.google.firebase.firestore.Exclude

/**
 * Entity que representa la Factura en Firestore.
 * - id: String -> ID del documento en Firestore.
 * - numeroFactura, fechaEmision, etc. -> Campos propios de la factura.
 */
data class FacturaEntity(
    // Ahora usamos String para ID.
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
    // Constructor sin argumentos para Firebase (opcional pero recomendado)
    constructor() : this(
        "",
        "", "", "",
        "", "", "", "", "",
        0.0, 0.0, 0.0, "Emitida"
    )

    /**
     * toMap() convierte la factura en un Map<String, Any>
     * para subirlo fácilmente a Firestore.
     */
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
