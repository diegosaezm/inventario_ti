package com.example.inventariomovilti.validacion

import com.example.inventariomovilti.model.CategoriaProducto

object Validaciones {
    fun validarNombre(nombre: String): String {
        if (nombre.isBlank()) return "Ingresa un nombre"
        return ""
    }

    fun validarCategoria(categoria: String): String {
        if (CategoriaProducto.entries.none { it.nombre == categoria }) {
            return "Selecciona una categoría"
        }
        return ""
    }

    fun validarStock(stock: String): String {
        val numero = stock.toIntOrNull()
        if (numero == null || numero < 0) {
            return "Ingresa un entero válido (0 o mayor)"
        }
        return ""
    }

    fun validarCantidad(tipo: String, cantidad: String, stock: Int): String {
        val numero = cantidad.toIntOrNull()
        if (numero == null || numero <= 0) {
            return "Ingresa una cantidad válida mayor que cero"
        }
        if (tipo != "Entrada" && tipo != "Salida") {
            return "Selecciona entrada o salida"
        }
        if (tipo == "Salida" && numero > stock) {
            return "No puedes retirar más unidades que las disponibles"
        }
        if (tipo == "Entrada" && stock > Int.MAX_VALUE - numero) {
            return "La cantidad supera el stock permitido"
        }
        return ""
    }
}
