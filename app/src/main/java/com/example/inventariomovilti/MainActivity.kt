
package com.example.inventariomovilti

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator

import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items

import androidx.compose.material3.*
import androidx.compose.runtime.*
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


// MODELO UTILIZADO POR LA INTERFAZ
data class Producto(
    val id: Int,
    val nombre: String,
    val categoria: String,
    val stock: Int,
    val stockMinimo: Int
)


// ACTIVIDAD PRINCIPAL
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


// PANTALLA PRINCIPAL
@Composable
fun PantallaInventario(
    viewModel: InventarioViewModel
) {

    val context = LocalContext.current

    // Datos observados desde Room
    val productos by viewModel.productos.collectAsState()
    val movimientos by viewModel.movimientos.collectAsState()

    // Estados visuales
    var mostrarFormulario by remember {
        mutableStateOf(false)
    }

    var productoSeleccionado by remember {
        mutableStateOf<Producto?>(null)
    }

    var mostrarHistorial by remember {
        mutableStateOf(false)
    }


    // FORMULARIO PARA AGREGAR PRODUCTOS
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


    // FORMULARIO DE MOVIMIENTOS
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

                        // Retroalimentación nativa
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


    // CONTENIDO PRINCIPAL
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


        // AGREGAR PRODUCTOS
        Button(
            onClick = {
                mostrarFormulario = true
            }
        ) {
            Text("+ Agregar producto")
        }


        // MOSTRAR U OCULTAR HISTORIAL
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


        // HISTORIAL ANIMADO
        AnimatedVisibility(
            visible = mostrarHistorial,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {

            Column(
                verticalArrangement =
                    Arrangement.spacedBy(6.dp)
            ) {

                Text(
                    text = "Últimos movimientos",
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


        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Productos registrados: ${productos.size}",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(8.dp))


        // MENSAJE CUANDO NO HAY PRODUCTOS
        if (productos.isEmpty()) {

            Text(
                "No hay productos registrados. " +
                        "Presiona Agregar producto para comenzar."
            )
        }


        // CATALOGO DE PRODUCTOS
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement =
                Arrangement.spacedBy(12.dp)
        ) {

            items(
                items = productos,
                key = { it.id }
            ) { producto ->

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
                            style =
                                MaterialTheme.typography.titleMedium
                        )

                        Text(
                            "Categoría: ${producto.categoria}"
                        )

                        Text(
                            "Stock: ${producto.stock}"
                        )

                        Text(
                            "Mínimo: ${producto.stockMinimo}"
                        )


                        // ALERTA DE STOCK BAJO
                        if (
                            producto.stock <= producto.stockMinimo
                        ) {
                            Text(
                                text = "⚠ Stock bajo",
                                color = MaterialTheme.colorScheme.error
                            )
                        }

                        Spacer(
                            modifier = Modifier.height(8.dp)
                        )


                        // REGISTRAR MOVIMIENTO
                        Button(
                            onClick = {
                                productoSeleccionado = producto
                            }
                        ) {
                            Text("Registrar movimiento")
                        }

                        Spacer(
                            modifier = Modifier.height(8.dp)
                        )


                        // RECURSO NATIVO: PORTAPAPELES
                        OutlinedButton(
                            onClick = {
                                copiarProducto(context, producto)
                            }
                        ) {
                            Text("Copiar información")
                        }
                    }
                }
            }
        }
    }
}


// RECURSO NATIVO: PORTAPAPELES
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


// RECURSO NATIVO: VIBRACION
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
