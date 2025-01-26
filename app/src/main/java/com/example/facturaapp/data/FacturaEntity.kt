package com.example.facturaapp.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "facturas")
data class FacturaEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "numero_factura") val numeroFactura: String,
    @ColumnInfo(name = "fecha_emision") val fechaEmision: String,
    @ColumnInfo(name = "emisor_empresa") val emisor: String,
    @ColumnInfo(name = "emisor_nif") val emisorNIF: String,
    @ColumnInfo(name = "emisor_direccion") val emisorDireccion: String,
    @ColumnInfo(name = "receptor_cliente") val receptor: String,
    @ColumnInfo(name = "receptor_nif") val receptorNIF: String,
    @ColumnInfo(name = "receptor_direccion") val receptorDireccion: String,
    @ColumnInfo(name = "base_imponible") val baseImponible: Double,
    @ColumnInfo(name = "iva") val iva: Double,
    @ColumnInfo(name = "total") val total: Double
)
