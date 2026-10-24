# Inventario Móvil TI

Proyecto académico de la asignatura DSY1105, Desarrollo de Aplicaciones Móviles.

## Integrantes

- Benjamín
- Diego Sáez

## Descripción

Aplicación Android para llevar un inventario de productos tecnológicos con datos de prueba. Permite consultar las existencias y registrar entradas y salidas de productos.

## Funciones actuales

- Registrar productos con nombre, categoría, stock inicial y stock mínimo.
- Validar los campos de los formularios.
- Registrar entradas y salidas sin permitir stock negativo.
- Cambiar entre Productos e Historial desde la barra inferior.
- Consultar todos los movimientos con su fecha y hora.
- Mostrar un aviso cuando el stock está en el mínimo o por debajo.
- Guardar productos y movimientos en el dispositivo con Room.
- Copiar la información de un producto al portapapeles.
- Usar vibración al enviar un movimiento a guardar.
- Mostrar animaciones al cambiar de pantalla y en las tarjetas.

## Herramientas

Kotlin, Jetpack Compose, Material 3, ViewModel, Room y Gradle.

Las pantallas están en `ui`, las validaciones en `validacion`, la gestión del estado en `viewmodel` y el almacenamiento en `data`.

## Cómo ejecutar

1. Instalar Android Studio con el SDK de Android 37.
2. En Android Studio, elegir Clone Repository y pegar https://github.com/diegosaezm/inventario_ti.git.
3. Guardar el proyecto en una ruta sin tildes ni caracteres especiales.
4. Abrir el proyecto y esperar la sincronización de Gradle. El proyecto solicita JDK 25 para Gradle.
5. Preparar un emulador o conectar un teléfono con Android 7.0 (API 24) o superior y depuración USB activada.
6. Seleccionar la configuración app y presionar Run.
7. Agregar un producto para comenzar. La base de datos se crea vacía en la primera ejecución.

Para sincronizar por primera vez se necesita conexión a internet. La vibración requiere un dispositivo compatible.

## Alcance actual

Este avance usa almacenamiento local. Todavía no incluye inicio de sesión, permisos por rol ni consumo de una API. No se conecta a sistemas comerciales reales.
