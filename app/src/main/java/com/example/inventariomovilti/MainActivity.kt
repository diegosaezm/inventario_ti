
package com.example.inventariomovilti

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.inventariomovilti.ui.components.FormularioProducto

data class Producto(
    val id: Int,
    val nombre: String,
    val categoria: String,
    val stock: Int,
    val stockMinimo: Int
)

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                PantallaInventario()
            }
        }
    }
}

@Composable
fun PantallaInventario() {

    var productos by remember {
        mutableStateOf(
            listOf(
                Producto(1, "Mouse Logitech", "Periféricos", 10, 5),
                Producto(2, "Teclado Redragon", "Periféricos", 3, 5),
                Producto(3, "Router TP-Link", "Redes", 8, 4),
                Producto(4, "Notebook Lenovo", "Computación", 2, 3)
            )
        )
    }

    var mostrarFormulario by remember {
        mutableStateOf(false)
    }

    if (mostrarFormulario) {
        AlertDialog(
            onDismissRequest = {
                mostrarFormulario = false
            },
            title = {
                Text("Nuevo producto")
            },
            text = {
                FormularioProducto(
                    onGuardar = { nuevoProducto ->

                        val siguienteId =
                            (productos.maxOfOrNull { it.id } ?: 0) + 1

                        productos = productos +
                                nuevoProducto.copy(id = siguienteId)

                        mostrarFormulario = false
                    },
                    onCancelar = {
                        mostrarFormulario = false
                    }
                )
            },
            confirmButton = {}
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .safeDrawingPadding()
            .padding(16.dp)
    ) {

        Text(
            text = "Inventario Móvil TI",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                mostrarFormulario = true
            }
        ) {
            Text("+ Agregar producto")
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            items(productos, key = { it.id }) { producto ->

                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {

                        Text(
                            text = producto.nombre,
                            style = MaterialTheme.typography.titleMedium
                        )

                        Text("Categoría: ${producto.categoria}")
                        Text("Stock: ${producto.stock}")
                        Text("Mínimo: ${producto.stockMinimo}")

                        if (producto.stock <= producto.stockMinimo) {
                            Text(
                                text = "⚠ Stock bajo",
                                color = MaterialTheme.colorScheme.error
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Button(
                            enabled = producto.stock > 0,
                            onClick = {
                                productos = productos.map {
                                    if (it.id == producto.id) {
                                        it.copy(stock = it.stock - 1)
                                    } else {
                                        it
                                    }
                                }
                            }
                        ) {
                            Text("Registrar salida (-1)")
                        }
                    }
                }
            }
        }
    }
}
