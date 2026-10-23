
package com.example.inventariomovilti.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [
        ProductoEntity::class,
        MovimientoEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class InventarioDatabase : RoomDatabase() {

    abstract fun inventarioDao(): InventarioDao
}
