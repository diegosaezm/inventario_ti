
package com.example.inventariomovilti.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.inventariomovilti.validacion.Validaciones
import com.example.inventariomovilti.Producto

@Composable
fun FormularioMovimiento(
    producto: Producto,
    onGuardar: (String, Int) -> Unit,
    onCancelar: () -> Unit
) {
    var tipo by remember { mutableStateOf("Entrada") }
    var cantidad by remember { mutableStateOf("") }
    var mostrarError by remember { mutableStateOf(false) }

    val errorCantidad = Validaciones.validarCantidad(tipo, cantidad, producto.stock)

    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = producto.nombre,
            style = MaterialTheme.typography.titleMedium
        )

        Text("Stock disponible: ${producto.stock}")

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = tipo == "Entrada",
                onClick = { tipo = "Entrada" },
                label = { Text("Entrada") }
            )

            FilterChip(
                selected = tipo == "Salida",
                onClick = { tipo = "Salida" },
                label = { Text("Salida") }
            )
        }

        OutlinedTextField(
            value = cantidad,
            onValueChange = {
                cantidad = it
                mostrarError = false
            },
            label = { Text("Cantidad") },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number
            ),
            isError = mostrarError && errorCantidad.isNotEmpty(),
            supportingText = {
                if (mostrarError && errorCantidad.isNotEmpty()) {
                    Text(errorCantidad)
                }
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
                    mostrarError = true

                    if (errorCantidad.isEmpty()) {
                        onGuardar(tipo, cantidad.toInt())
                    }
                }
            ) {
                Text("Guardar movimiento")
            }
        }
    }
}
