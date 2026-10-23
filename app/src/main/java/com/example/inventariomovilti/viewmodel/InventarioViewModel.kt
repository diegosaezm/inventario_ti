
package com.example.inventariomovilti.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.inventariomovilti.Producto
import com.example.inventariomovilti.data.repository.InventarioRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class InventarioViewModel(
    private val repository: InventarioRepository
) : ViewModel() {

    val productos = repository.productos.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val movimientos = repository.movimientos.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun agregarProducto(producto: Producto) {
        if (producto.nombre.isBlank() ||
            producto.categoria.isBlank() ||
            producto.stock < 0 ||
            producto.stockMinimo < 0
        ) return

        viewModelScope.launch {
            repository.agregarProducto(producto)
        }
    }

    fun registrarMovimiento(
        productoId: Int,
        tipo: String,
        cantidad: Int
    ) {
        viewModelScope.launch {
            repository.registrarMovimiento(
                productoId = productoId,
                tipo = tipo,
                cantidad = cantidad
            )
        }
    }
}
