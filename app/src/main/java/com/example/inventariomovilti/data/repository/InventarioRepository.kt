
package com.example.inventariomovilti.data.repository

import androidx.room.withTransaction
import com.example.inventariomovilti.Producto
import com.example.inventariomovilti.data.local.InventarioDatabase
import com.example.inventariomovilti.data.local.ProductoEntity
import com.example.inventariomovilti.data.local.MovimientoEntity
import com.example.inventariomovilti.model.Movimiento
import kotlinx.coroutines.flow.map

class InventarioRepository(
    private val database: InventarioDatabase
) {
    private val dao = database.inventarioDao()

    val productos = dao.obtenerProductos().map { lista ->
        lista.map { entidad ->
            Producto(
                id = entidad.id,
                nombre = entidad.nombre,
                categoria = entidad.categoria,
                stock = entidad.stock,
                stockMinimo = entidad.stockMinimo
            )
        }
    }

    val movimientos = dao.obtenerMovimientos().map { lista ->
        lista.map { entidad ->
            Movimiento(
                id = entidad.id,
                productoId = entidad.productoId,
                nombreProducto = entidad.nombreProducto,
                tipo = entidad.tipo,
                cantidad = entidad.cantidad,
                fecha = entidad.fecha
            )
        }
    }

    suspend fun agregarProducto(producto: Producto) {
        dao.insertarProducto(
            ProductoEntity(
                nombre = producto.nombre,
                categoria = producto.categoria,
                stock = producto.stock,
                stockMinimo = producto.stockMinimo
            )
        )
    }

    suspend fun registrarMovimiento(
        productoId: Int,
        tipo: String,
        cantidad: Int
    ): Boolean {
        if (cantidad <= 0) return false
        if (tipo != "Entrada" && tipo != "Salida") return false

        return database.withTransaction {
            val producto = dao.buscarProducto(productoId)
                ?: return@withTransaction false

            val nuevoStock = when (tipo) {
                "Entrada" -> {
                    if (producto.stock > Int.MAX_VALUE - cantidad) {
                        return@withTransaction false
                    }
                    producto.stock + cantidad
                }
                else -> {
                    if (cantidad > producto.stock) {
                        return@withTransaction false
                    }
                    producto.stock - cantidad
                }
            }

            dao.actualizarProducto(
                producto.copy(stock = nuevoStock)
            )

            dao.insertarMovimiento(
                MovimientoEntity(
                    productoId = producto.id,
                    nombreProducto = producto.nombre,
                    tipo = tipo,
                    cantidad = cantidad,
                    fecha = System.currentTimeMillis()
                )
            )

            true
        }
    }
}
