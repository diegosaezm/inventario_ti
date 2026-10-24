
package com.example.inventariomovilti

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator

import androidx.activity.compose.BackHandler
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateContentSize

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.res.painterResource
import com.example.inventariomovilti.model.Movimiento
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.room.Room

import com.example.inventariomovilti.data.local.InventarioDatabase
import com.example.inventariomovilti.data.repository.InventarioRepository
import com.example.inventariomovilti.ui.components.FormularioMovimiento
import com.example.inventariomovilti.ui.components.FormularioProducto
import com.example.inventariomovilti.viewmodel.InventarioViewModel


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

                    if (
                        modelClass.isAssignableFrom(
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

    val context = LocalContext.current

    val productos by viewModel.productos.collectAsState()
    val movimientos by viewModel.movimientos.collectAsState()

    var mostrarFormulario by remember {
        mutableStateOf(false)
    }

    var productoSeleccionado by remember {
        mutableStateOf<Producto?>(null)
    }

    var pantalla by rememberSaveable { mutableStateOf("Productos") }

    BackHandler(enabled = pantalla == "Historial" && !mostrarFormulario && productoSeleccionado == null) {
        pantalla = "Productos"
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

            title = {
                Text("Registrar movimiento")
            },

            text = {
                FormularioMovimiento(
                    producto = producto,

                    onGuardar = { tipo, cantidad ->

                        viewModel.registrarMovimiento(
                            productoId = producto.id,
                            tipo = tipo,
                            cantidad = cantidad
                        )

                        vibrar(context)

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


    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = pantalla == "Productos",
                    onClick = { pantalla = "Productos" },
                    icon = {
                        Icon(painterResource(R.drawable.ic_productos), contentDescription = null)
                    },
                    label = { Text("Productos") }
                )
                NavigationBarItem(
                    selected = pantalla == "Historial",
                    onClick = { pantalla = "Historial" },
                    icon = {
                        Icon(painterResource(R.drawable.ic_historial), contentDescription = null)
                    },
                    label = { Text("Historial") }
                )
            }
        }
    ) { espacio ->
        AnimatedContent(
            targetState = pantalla,
            modifier = Modifier
                .fillMaxSize()
                .padding(espacio)
                .padding(16.dp),
            label = "Cambio de pantalla"
        ) { seleccion ->
            if (seleccion == "Productos") {
                PantallaProductos(
                    productos = productos,
                    onAgregar = { mostrarFormulario = true },
                    onMovimiento = { productoSeleccionado = it },
                    onCopiar = { copiarProducto(context, it) }
                )
            } else {
                PantallaHistorial(movimientos)
            }
        }
    }
}

@Composable
fun PantallaProductos(
    productos: List<Producto>,
    onAgregar: () -> Unit,
    onMovimiento: (Producto) -> Unit,
    onCopiar: (Producto) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Text("Inventario Móvil TI", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(12.dp))
        Button(onClick = onAgregar) {
            Text("Agregar producto")
        }
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            "Productos registrados: ${productos.size}",
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(modifier = Modifier.height(8.dp))
        if (productos.isEmpty()) {
            Text("No hay productos registrados. Presiona Agregar producto para comenzar.")
        }
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(productos, key = { it.id }) { producto ->
                Card(modifier = Modifier.fillMaxWidth().animateContentSize()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(producto.nombre, style = MaterialTheme.typography.titleMedium)
                        Text("Categoría: ${producto.categoria}")
                        Text("Stock: ${producto.stock}")
                        Text("Mínimo: ${producto.stockMinimo}")
                        if (producto.stock <= producto.stockMinimo) {
                            Text("Stock bajo", color = MaterialTheme.colorScheme.error)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(onClick = { onMovimiento(producto) }) {
                            Text("Registrar movimiento")
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedButton(onClick = { onCopiar(producto) }) {
                            Text("Copiar información")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PantallaHistorial(movimientos: List<Movimiento>) {
    val formatoFecha = remember { SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()) }

    Column(modifier = Modifier.fillMaxSize()) {
        Text("Historial", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(12.dp))
        if (movimientos.isEmpty()) {
            Text("Sin movimientos registrados")
        }
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(movimientos, key = { it.id }) { movimiento ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(movimiento.nombreProducto, style = MaterialTheme.typography.titleMedium)
                        Text("${movimiento.tipo}: ${movimiento.cantidad} unidades")
                        Text(formatoFecha.format(Date(movimiento.fecha)))
                    }
                }
            }
        }
    }
}

fun copiarProducto(
    context: Context,
    producto: Producto
) {

    val clipboard = context.getSystemService(
        Context.CLIPBOARD_SERVICE
    ) as ClipboardManager

    val informacion = """
        Producto: ${producto.nombre}
        Categoría: ${producto.categoria}
        Stock: ${producto.stock}
        Stock mínimo: ${producto.stockMinimo}
    """.trimIndent()

    val clip = ClipData.newPlainText(
        "Información del producto",
        informacion
    )

    clipboard.setPrimaryClip(clip)
}


fun vibrar(context: Context) {

    val vibrator = context.getSystemService(
        Context.VIBRATOR_SERVICE
    ) as? Vibrator

    if (vibrator?.hasVibrator() == true) {

        if (
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
        ) {

            vibrator.vibrate(
                VibrationEffect.createOneShot(
                    120,
                    VibrationEffect.DEFAULT_AMPLITUDE
                )
            )

        } else {

            @Suppress("DEPRECATION")
            vibrator.vibrate(120)
        }
    }
}
