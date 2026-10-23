
package com.example.inventariomovilti.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface InventarioDao {

    @Query("SELECT * FROM productos ORDER BY nombre ASC")
    fun obtenerProductos(): Flow<List<ProductoEntity>>

    @Insert
    suspend fun insertarProducto(producto: ProductoEntity)

    @Update
    suspend fun actualizarProducto(producto: ProductoEntity)

    @Query("SELECT * FROM productos WHERE id = :id LIMIT 1")
    suspend fun buscarProducto(id: Int): ProductoEntity?

    @Query("SELECT * FROM movimientos ORDER BY fecha DESC, id DESC")
    fun obtenerMovimientos(): Flow<List<MovimientoEntity>>

    @Insert
    suspend fun insertarMovimiento(movimiento: MovimientoEntity)

    @Query("SELECT COUNT(*) FROM productos")
    suspend fun contarProductos(): Int
}
