
package com.example.inventariomovilti.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.unit.dp
import com.example.inventariomovilti.Producto

@Composable
fun FormularioProducto(
    onGuardar: (Producto) -> Unit,
    onCancelar: () -> Unit
) {
    var nombre by remember { mutableStateOf("") }
    var categoria by remember { mutableStateOf("") }
    var stock by remember { mutableStateOf("") }
    var stockMinimo by remember { mutableStateOf("") }
    var mostrarErrores by remember { mutableStateOf(false) }

    val stockNumero = stock.toIntOrNull()
    val minimoNumero = stockMinimo.toIntOrNull()

    val nombreValido = nombre.isNotBlank()
    val categoriaValida = categoria.isNotBlank()
    val stockValido = stockNumero != null && stockNumero >= 0
    val minimoValido = minimoNumero != null && minimoNumero >= 0

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        OutlinedTextField(
            value = nombre,
            onValueChange = { nombre = it },
            label = { Text("Nombre") },
            isError = mostrarErrores && !nombreValido,
            supportingText = {
                if (mostrarErrores && !nombreValido)
                    Text("Ingresa un nombre")
            },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = categoria,
            onValueChange = { categoria = it },
            label = { Text("Categoría") },
            isError = mostrarErrores && !categoriaValida,
            supportingText = {
                if (mostrarErrores && !categoriaValida)
                    Text("Ingresa una categoría")
            },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = stock,
            onValueChange = { stock = it },
            label = { Text("Stock inicial") },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number
            ),
            isError = mostrarErrores && !stockValido,
            supportingText = {
                if (mostrarErrores && !stockValido)
                    Text("Ingresa un número entero válido (0 o mayor)")
            },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = stockMinimo,
            onValueChange = { stockMinimo = it },
            label = { Text("Stock mínimo") },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number
            ),
            isError = mostrarErrores && !minimoValido,
            supportingText = {
                if (mostrarErrores && !minimoValido)
                    Text("Ingresa un número entero válido (0 o mayor)")
            },
            modifier = Modifier.fillMaxWidth()
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedButton(onClick = onCancelar) {
                Text("Cancelar")
            }

            Button(
                onClick = {
                    mostrarErrores = true

                    if (nombreValido && categoriaValida &&
                        stockValido && minimoValido
                    ) {
                        onGuardar(
                            Producto(
                                id = 0,
                                nombre = nombre.trim(),
                                categoria = categoria.trim(),
                                stock = requireNotNull(stockNumero),
                                stockMinimo = requireNotNull(minimoNumero)
                            )
                        )
                    }
                }
            ) {
                Text("Guardar")
            }
        }
    }
}
