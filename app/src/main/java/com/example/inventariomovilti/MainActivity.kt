
package com.example.inventariomovilti
import androidx.compose.animation.animateContentSize
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
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.room.Room
import com.example.inventariomovilti.data.local.InventarioDatabase
import com.example.inventariomovilti.data.repository.InventarioRepository
import com.example.inventariomovilti.ui.components.FormularioMovimiento
import com.example.inventariomovilti.ui.components.FormularioProducto
import com.example.inventariomovilti.viewmodel.InventarioViewModel
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
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

        val database = Room.databaseBuilder(
            applicationContext,
            InventarioDatabase::class.java,
            "inventario_database"
        ).build()

        val repository = InventarioRepository(database)

        val inventarioViewModel = ViewModelProvider(
            this,
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(
                    modelClass: Class<T>
                ): T {
                    if (modelClass.isAssignableFrom(
                            InventarioViewModel::class.java
                        )
                    ) {
                        @Suppress("UNCHECKED_CAST")
                        return InventarioViewModel(repository) as T
                    }
                    throw IllegalArgumentException(
                        "ViewModel desconocido"
                    )
                }
            }
        )[InventarioViewModel::class.java]

        setContent {
            MaterialTheme {
                PantallaInventario(inventarioViewModel)
            }
        }
    }
}

@Composable
fun PantallaInventario(
    viewModel: InventarioViewModel
) {

    val productos by viewModel.productos.collectAsState()
    val movimientos by viewModel.movimientos.collectAsState()

    var mostrarFormulario by remember {
        mutableStateOf(false)
    }

    var productoSeleccionado by remember {
        mutableStateOf<Producto?>(null)
    }

    var mostrarHistorial by remember {
        mutableStateOf(false)
    }

    if (mostrarFormulario) {
        AlertDialog(
            onDismissRequest = {
                mostrarFormulario = false
            },
            title = { Text("Nuevo producto") },
            text = {
                FormularioProducto(
                    onGuardar = { nuevoProducto ->
                        viewModel.agregarProducto(nuevoProducto)
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

    productoSeleccionado?.let { producto ->

        AlertDialog(
            onDismissRequest = {
                productoSeleccionado = null
            },
            title = { Text("Registrar movimiento") },
            text = {
                FormularioMovimiento(
                    producto = producto,
                    onGuardar = { tipo, cantidad ->
                        viewModel.registrarMovimiento(
                            productoId = producto.id,
                            tipo = tipo,
                            cantidad = cantidad
                        )
                        productoSeleccionado = null
                    },
                    onCancelar = {
                        productoSeleccionado = null
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

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = { mostrarFormulario = true }
        ) {
            Text("+ Agregar producto")
        }

        OutlinedButton(
            onClick = {
                mostrarHistorial = !mostrarHistorial
            }
        ) {
            Text(
                if (mostrarHistorial)
                    "Ocultar historial"
                else
                    "Ver historial"
            )
        }

        if (mostrarHistorial) {
            Text(
                "Últimos movimientos",
                style = MaterialTheme.typography.titleMedium
            )

            if (movimientos.isEmpty()) {
                Text("Sin movimientos registrados")
            }

            movimientos.take(3).forEach { movimiento ->
                Text(
                    "${movimiento.tipo}: " +
                            "${movimiento.nombreProducto} " +
                            "(${movimiento.cantidad} unidades)"
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            "Productos registrados: ${productos.size}",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(8.dp))


        AnimatedVisibility(
            visible = mostrarHistorial,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            Column {
                Text(
                    "Últimos movimientos",
                    style = MaterialTheme.typography.titleMedium
                )

                if (movimientos.isEmpty()) {
                    Text("Sin movimientos registrados")
                }

                movimientos.take(3).forEach { movimiento ->
                    Text(
                        "${movimiento.tipo}: " +
                                "${movimiento.nombreProducto} " +
                                "(${movimiento.cantidad} unidades)"
                    )
                }
            }
        }


        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(productos, key = { it.id }) { producto ->

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .animateContentSize()
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
                                "⚠ Stock bajo",
                                color = MaterialTheme.colorScheme.error
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Button(
                            onClick = {
                                productoSeleccionado = producto
                            }
                        ) {
                            Text("Registrar movimiento")
                        }
                    }
                }
            }
        }
    }
}
