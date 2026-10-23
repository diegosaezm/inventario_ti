
package com.example.inventariomovilti.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "movimientos")
data class MovimientoEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val productoId: Int,
    val nombreProducto: String,
    val tipo: String,
    val cantidad: Int,
    val fecha: Long
)
