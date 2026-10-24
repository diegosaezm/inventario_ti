
package com.example.inventariomovilti.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

import com.example.inventariomovilti.validacion.Validaciones
import com.example.inventariomovilti.Producto
import com.example.inventariomovilti.model.CategoriaProducto

@OptIn(ExperimentalMaterial3Api::class)
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

    var menuExpandido by remember { mutableStateOf(false) }

    val errorNombre = Validaciones.validarNombre(nombre)
    val errorCategoria = Validaciones.validarCategoria(categoria)
    val errorStock = Validaciones.validarStock(stock)
    val errorMinimo = Validaciones.validarStock(stockMinimo)

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
            isError = mostrarErrores && errorNombre.isNotEmpty(),
            supportingText = {
                if (mostrarErrores && errorNombre.isNotEmpty()) {
                    Text(errorNombre)
                }
            },
            modifier = Modifier.fillMaxWidth()
        )

        ExposedDropdownMenuBox(
            expanded = menuExpandido,
            onExpandedChange = {
                menuExpandido = it
            }
        ) {
            OutlinedTextField(
                value = categoria,
                onValueChange = {},
                readOnly = true,
                label = { Text("Categoría") },
                isError = mostrarErrores && errorCategoria.isNotEmpty(),
                supportingText = {
                    if (mostrarErrores && errorCategoria.isNotEmpty()) {
                        Text(errorCategoria)
                    }
                },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(
                        expanded = menuExpandido
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(
                        ExposedDropdownMenuAnchorType.PrimaryNotEditable
                    )
            )

            ExposedDropdownMenu(
                expanded = menuExpandido,
                onDismissRequest = {
                    menuExpandido = false
                }
            ) {
                CategoriaProducto.entries.forEach { opcion ->
                    DropdownMenuItem(
                        text = { Text(opcion.nombre) },
                        onClick = {
                            categoria = opcion.nombre
                            menuExpandido = false
                        }
                    )
                }
            }
        }

        OutlinedTextField(
            value = stock,
            onValueChange = { stock = it },
            label = { Text("Stock inicial") },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number
            ),
            isError = mostrarErrores && errorStock.isNotEmpty(),
            supportingText = {
                if (mostrarErrores && errorStock.isNotEmpty()) {
                    Text(errorStock)
                }
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
            isError = mostrarErrores && errorMinimo.isNotEmpty(),
            supportingText = {
                if (mostrarErrores && errorMinimo.isNotEmpty()) {
                    Text(errorMinimo)
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
                    mostrarErrores = true

                    if (
                        errorNombre.isEmpty() &&
                        errorCategoria.isEmpty() &&
                        errorStock.isEmpty() &&
                        errorMinimo.isEmpty()
                    ) {
                        onGuardar(
                            Producto(
                                id = 0,
                                nombre = nombre.trim(),
                                categoria = categoria,
                                stock = stock.toInt(),
                                stockMinimo = stockMinimo.toInt()
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
