
package com.example.inventariomovilti.model

data class Movimiento(
    val id: Int,
    val productoId: Int,
    val nombreProducto: String,
    val tipo: String,
    val cantidad: Int,
    val fecha: Long = System.currentTimeMillis()
)
