<div align="center">
  <br>
  <img src="app/src/main/res/mipmap-xxxhdpi/ic_launcher_round.webp" width="120" height="120" alt="Despensa al Día">
  <br>
  <h1>🌿 Despensa al Día</h1>
  <p><strong>Ve lo que importa hoy. Aprovecha lo que ya tienes.</strong></p>
  <p><em>Tu asistente inteligente contra el desperdicio de alimentos</em></p>
  <br>
  <p>
    <img src="https://img.shields.io/badge/Kotlin-2.2.10-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white" alt="Kotlin">
    <img src="https://img.shields.io/badge/Jetpack_Compose-2024.09-4285F4?style=for-the-badge&logo=jetpackcompose&logoColor=white" alt="Compose">
    <img src="https://img.shields.io/badge/Min_SDK-24-00C853?style=for-the-badge&logo=android&logoColor=white" alt="Min SDK">
    <img src="https://img.shields.io/badge/Target_SDK-36-FF6D00?style=for-the-badge&logo=android&logoColor=white" alt="Target SDK">
    <img src="https://img.shields.io/badge/Room-2.7.0-FF5722?style=for-the-badge&logo=sqlite&logoColor=white" alt="Room">
  </p>
  <p>
    <img src="https://img.shields.io/badge/status-production-2ECC71?style=flat-square" alt="Status">
    <img src="https://img.shields.io/badge/license-MIT-FFB300?style=flat-square" alt="License">
    <img src="https://img.shields.io/badge/privacy-100%25_offline-7B1FA2?style=flat-square" alt="Privacy">
    <img src="https://img.shields.io/badge/localization-3_languages-00ACC1?style=flat-square" alt="Localization">
  </p>
  <br>
</div>

---

## 📋 Tabla de Contenidos

- [¿Qué es Despensa al Día?](#-qué-es-despensa-al-día)
- [✨ Funcionalidades — Flujo Completo](#-funcionalidades--flujo-completo)
  - [🏠 Pantalla Principal: "Usa Primero"](#1-usa-primero)
  - [📦 Inventario Inteligente](#2-inventario-inteligente)
  - [📊 Estadísticas y Logros](#3-estadísticas-y-logros)
  - [🛒 Lista de Compras](#4-lista-de-compras)
  - [📅 Planificador Semanal](#5-planificador-semanal)
  - [🍳 Recetas Inteligentes](#6-recetas-inteligentes)
  - [📱 Escáner de Código de Barras](#7-escáner-de-código-de-barras)
  - [🔮 Predicción de Caducidad](#8-predicción-de-caducidad)
  - [🏆 Gamificación y Logros](#9-gamificación)
  - [⚙️ Ajustes y Personalización](#10-ajustes)
  - [🔔 Notificaciones y Widget](#11-notificaciones-y-widget)
  - [📤 Exportación de Datos](#12-exportación)
  - [⏰ Posponer y Repaso Rápido](#13-posponer-y-repaso-rápido) <!-- NUEVO -->
- [🎨 Identidad Visual — Temas y Colores](#-identidad-visual--temas-y-colores) <!-- ACTUALIZADO -->
- [🏗️ Arquitectura](#️-arquitectura)
- [🗂️ Estructura del Proyecto](#️-estructura-del-proyecto)
- [🔧 Tecnologías](#-tecnologías)
- [🚀 Cómo Ejecutar](#-cómo-ejecutar)
- [❓ FAQ / ¿Qué pasa si...?](#-faq--qué-pasa-si)
- [📄 Licencia](#-licencia)

---
- [¿Qué es Despensa al Día?](#-qué-es-despensa-al-día) Agrupa productos por urgencia (vencidos, hoy, mañana, esta semana) y permite resolverlos con un solo toque. Incluye tarjeta hero con resumen del día, valor en riesgo y acciones rápidas.
- **Inventario Inteligente** — Registro rápido con plantillas predefinidas. Campos completos: nombre, categoría, ubicación (despensa, refrigerador, congelador, mascotas, etc.), tipo de vencimiento (fecha fija, consumo preferente, estimada), precio, cantidad, marca y notas.
- **Resolución de Productos** — Marca cada producto como **Consumido**, **Desechado** o **Donado** con un solo gesto (swipe o diálogo). Cada acción tiene su propio color (verde, coral, lavanda) y retroalimentación visual. Deshacer disponible.
- **Estadísticas Mensuales** — Visualiza tasa de consumo, valor aprovechado vs. perdido, categorías con más desperdicio, racha de ahorro y salud del inventario con gráfico donut.
- **Lista de Compras** — Genera y gestiona una lista de compras agrupada por categoría. Compártela en texto plano con quien quieras.
- **Notificaciones Locales** — Alertas automáticas diarias configuradas a la hora que elijas. Te dice exactamente qué productos están por vencer y cuánto dinero está en riesgo.
- **Widget en Pantalla de Inicio** — Muestra los 3 productos más urgentes sin abrir la app.
- **Exportación de Datos** — Exporta todo tu inventario a **JSON** o **CSV** y compártelo con cualquier aplicación.
- **Tema Claro / Oscuro** — Selecciona entre tema Sistema, Claro u Oscuro desde Ajustes. El tema oscuro usa una paleta verde bosque profundo con acentos sutiles; el claro usa tonos marfil cálidos.
- **Idiomas y formatos regionales** — Se adapta al idioma, formato de fecha, moneda y unidades del dispositivo. Compatible con español, inglés y portugués de Brasil.
- **100% Privado** — Tus productos y registros se guardan localmente en tu dispositivo. Sin cuentas, sin servidores.

## Identidad Visual — Jardín de Frescura

El diseño de Despensa al Día sigue el tema **Jardín de Frescura**: una identidad visual premium, cálida y serena, inspirada en el ciclo natural de los alimentos.

### Principios visuales

- **No es una app ecológica genérica:** evitamos verde brillante y estética infantil. Usamos tonos vegetales apagados, elegantes y globales.
- **El color comunica urgencia sin alarmar:** verde para disponible, ámbar para atención, coral para prioridad, lavanda para donación, gris cálido para descarte.
- **Microinteracciones funcionales:** animaciones sutiles en tarjetas, hero card con contador animado y fondos con degradado ambiental.
- **Jerarquía clara:** el nombre del producto y su urgencia son lo principal; ubicación, cantidad y precio son secundarios.
- **Iconografía consistente:** Material Symbols redondeados para acciones, ubicaciones y categorías, sin mezclar estilos.

### Paleta

**Modo oscuro:**
| Color | Código | Uso |
|---|---|---|
| Fondo base | `#101814` | Verde bosque profundo |
| Superficie | `#18231D` | Paneles y secciones |
| Tarjeta | `#203027` | Componentes elevados |
| Verde | `#62C99A` / `#8ED6A2` | Acciones, disponible |
| Ámbar | `#F6C76D` | Atención próxima |
| Coral | `#F1846B` | Urgencia |
| Lavanda | `#B8A4E8` | Donación |

**Modo claro:**
| Color | Código | Uso |
|---|---|---|
| Fondo | `#F7F5EE` | Marfil cálido |
| Verde | `#2E7D5B` | Acción principal |
| Verde suave | `#DDF1DF` | Contenedores |
| Ámbar suave | `#FFF0CB` | Atención suave |
| Coral suave | `#FCE0D8` | Urgencia suave |

## Fragmentos Clave del Proyecto

### 1. Paleta de color — `Color.kt`

Cada color tiene un significado funcional: el verde indica disponible, el ámbar atención, el coral urgencia y la lavanda donación. Los nombres de variable se mantuvieron del diseño original para no cambiar importaciones en las pantallas.

```kotlin
// Verde fresco — disponible, saludable, consumible
val Emerald = Color(0xFF62C99A)
val EmeraldLight = Color(0xFF8ED6A2)
val EmeraldDark = Color(0xFF2E7D5B)

// Ámbar — atención sin alarma
val Amber = Color(0xFFF6C76D)

// Coral — urgencia
val Coral = Color(0xFFF1846B)

// Lavanda — donación, acción secundaria positiva
val Sky = Color(0xFFB8A4E8)

// Fondos oscuros
val DarkBackground = Color(0xFF101814)
val DarkSurface = Color(0xFF18231D)
val DarkSurfaceVariant = Color(0xFF203027)

// Texto
val TextOnDark = Color(0xFFF2F5EF)
val TextPrimary = Color(0xFF1A241E)
```

### 2. Degradado ambiental de fondo — `AmbientGradientBackground`

Crea una atmósfera sutil sin imágenes pesadas. En modo oscuro usa un halo verde menta difuso; en claro usa tonos marfil cálidos. Se renderiza con `Canvas` y `radialGradient`, respetando el tema activo.

```kotlin
@Composable
fun AmbientGradientBackground() {
    val isDark = MaterialTheme.colorScheme.background == Color(0xFF101814)
    val colors = if (isDark) {
        listOf(Color(0xFF101814), Color(0xFF0F1F16), Color(0xFF0E1A12))
    } else {
        listOf(Color(0xFFF7F5EE), Color(0xFFF4F3EA), Color(0xFFF0EFE4))
    }
    Canvas(modifier = Modifier.fillMaxSize()) {
        drawRect(
            brush = Brush.radialGradient(
                colors = colors,
                center = Offset(size.width * 0.85f, size.height * 0.1f),
                radius = size.height * 0.8f
            )
        )
    }
}
```

### 3. Hero Card — `UrgencyHeroCard`

La tarjeta principal de "Usa Primero". Usa un degradado lineal, un contador animado con `spring` y muestra el valor económico en riesgo. todo sin modificar la lógica de negocio.

```kotlin
@Composable
fun UrgencyHeroCard(
    totalUrgent: Int,
    totalValueAtRisk: Double,
    currencySymbol: String,
    expiredCount: Int,
    todayCount: Int
) {
    val isDark = MaterialTheme.colorScheme.background == Color(0xFF101814)
    val gradientStart = if (isDark) Color(0xFF1A3A2A) else Color(0xFFE8F5EE)
    val gradientEnd = if (isDark) Color(0xFF1E3028) else Color(0xFFF5F0E0)

    val animatedCount by animateIntAsState(
        targetValue = totalUrgent,
        animationSpec = spring(dampingRatio = 0.6f, stiffness = 80f)
    )

    Card(shape = RoundedCornerShape(24.dp), elevation = CardDefaults.cardElevation(0.dp)) {
        Box(modifier = Modifier
            .fillMaxWidth()
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(gradientStart, gradientEnd),
                    start = Offset.Zero, end = Offset(1000f, 200f)
                ),
                shape = RoundedCornerShape(24.dp)
            )
        ) {
            Row(modifier = Modifier.padding(20.dp).fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Productos para usar", style = MaterialTheme.typography.bodyMedium)
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text("$animatedCount", style = MaterialTheme.typography.displayLarge,
                             fontWeight = FontWeight.ExtraBold)
                        Text("hoy", modifier = Modifier.padding(bottom = 6.dp))
                    }
                    if (totalValueAtRisk > 0)
                        Text("$${totalValueAtRisk} en riesgo", color = Amber)
                }
                Column(horizontalAlignment = Alignment.End) {
                    UrgencyStat("$expiredCount", "vencidos", Coral)
                    UrgencyStat("$todayCount", "vence hoy", Coral)
                }
            }
        }
    }
}
```

### 4. Tarjeta de producto con acento lateral — `ProductCard`

Cada producto se muestra como una tarjeta compacta con una barra lateral de 4dp coloreada por urgencia. Usa un icono por categoría (no ubicación), mantiene las acciones existentes y se anima al aparecer con `fade-in`.

```kotlin
@Composable
fun ProductCard(/* ... */) {
    val animatedAlpha by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(400, delayMillis = 50)
    )

    Card(
        modifier = Modifier.fillMaxWidth()
            .graphicsLayer(alpha = animatedAlpha),
        shape = RoundedCornerShape(20.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min)) {
            // Barra lateral de urgencia
            Box(modifier = Modifier.width(4.dp).fillMaxHeight()
                .clip(RoundedCornerShape(topStart = 20.dp, bottomStart = 20.dp))
                .background(urgencyColor))
            Column(modifier = Modifier.padding(14.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Icono circular por categoría
                    Box(modifier = Modifier.size(40.dp).clip(CircleShape)
                        .background(urgencyColor.copy(alpha = 0.1f))) {
                        Icon(categoryIcon(product.category), tint = urgencyColor)
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(product.name, fontWeight = FontWeight.Bold)
                        Text("${product.quantity} ${product.unit} · ${product.location.nombre}")
                    }
                    Badge(urgencyLabel, urgencyColor)  // "Hoy", "Mañana", etc.
                }
                // Botones: Consumir y Posponer
                Row {
                    FilledTonalButton(onClick = onResolve,
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = Emerald.copy(alpha = 0.12f),
                            contentColor = Emerald)) {
                        Icon(Icons.Filled.Check)
                        Text(stringResource(R.string.action_resolve))
                    }
                    OutlinedButton(onClick = onSnooze) {
                        Icon(Icons.Filled.Snooze)
                        Text(stringResource(R.string.action_snooze))
                    }
                }
            }
        }
    }
}
```

### 5. Capa de datos — `PantryDao` y `PantryRepository`

Room con consultas reactivas mediante Flow. El DAO expone productos filtrados por estado y el repositorio orquesta las operaciones de resolución.

```kotlin
// PantryDao.kt — consultas principales
@Query("SELECT * FROM products WHERE status = :status ORDER BY expirationDate ASC")
fun getProductsByStatus(status: ProductStatus): Flow<List<Product>>

@Query("SELECT * FROM products WHERE status = 'ACTIVE' ORDER BY expirationDate ASC")
fun getActiveProducts(): Flow<List<Product>>

@Query("UPDATE products SET status = :status, resolvedDate = :resolvedDate WHERE id = :productId")
suspend fun resolveProduct(productId: Long, status: ProductStatus, resolvedDate: Long)

// PantryRepository.kt — lógica de resolución
fun markAsConsumed(product: Product) {
    scope.launch {
        pantryDao.resolveProduct(product.id, ProductStatus.CONSUMED, System.currentTimeMillis())
    }
}
```

### 6. Clasificación por urgencia — `UrgencyUtils`

El corazón de "Usa Primero" es el sistema de `UrgencyBucket` con `java.time.LocalDate` para evitar errores de zona horaria. Los productos pospuestos (`snoozeUntil > now`) se filtran automáticamente sin modificar su `expirationDate`.

```kotlin
enum class UrgencyBucket { EXPIRED, TODAY, TOMORROW, THIS_WEEK, NONE }

data class UrgentProduct(val product: Product, val bucket: UrgencyBucket)

fun Product.urgencyBucket(nowMillis: Long, zoneId: ZoneId): UrgencyBucket {
    val today = Instant.ofEpochMilli(nowMillis).atZone(zoneId).toLocalDate()
    val expiryDate = expirationDate?.let { 
        Instant.ofEpochMilli(it).atZone(zoneId).toLocalDate() 
    } ?: return NONE
    return when {
        expiryDate.isBefore(today) -> EXPIRED
        expiryDate == today        -> TODAY
        expiryDate == today.plusDays(1) -> TOMORROW
        expiryDate <= today.plusDays(7) -> THIS_WEEK
        else -> NONE
    }
}

fun Product.isCurrentlySnoozed(nowMillis: Long): Boolean = 
    snoozeUntil?.let { it > nowMillis } == true

fun List<Product>.getUrgentProducts(): List<UrgentProduct> =
    this.asSequence()
        .filterNot { it.isCurrentlySnoozed() }
        .map { UrgentProduct(it, it.urgencyBucket()) }
        .filter { it.bucket != NONE }
        .sortedWith(compareBy<UrgentProduct> { it.bucket.ordinal }
            .thenBy { it.product.expirationDate ?: Long.MAX_VALUE })
        .toList()
```

### 7. ViewModel compartido — `PantryViewModel`

Un solo ViewModel para toda la app usando StateFlow. Las pantallas se suscriben a los estados que necesitan sin duplicar lógica.

```kotlin
class PantryViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = PantryRepository(/* ... */)

    val activeProductsState: StateFlow<List<Product>> =
        repository.activeProducts.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val consumedProductsState: StateFlow<List<Product>> =
        repository.consumedProducts.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun markAsConsumed(product: Product) = repository.markAsConsumed(product)
    fun markAsWasted(product: Product) = repository.markAsWasted(product)
    fun markAsDonated(product: Product) = repository.markAsDonated(product)
    fun undoProductResolution(product: Product) = repository.undoResolution(product)
}
```

## Capturas de pantalla

## 🌱 ¿Qué es Despensa al Día?

**Despensa al Día** no es solo un inventario. Es tu **asistente diario contra el desperdicio de alimentos**. Te dice exactamente qué usar primero, cuánto dinero estás arriesgando, y te ayuda a transformar tu despensa en ahorro real.

| ⚡ Característica | ✅ |
|:---|---:|
| 100% offline — tus datos nunca salen del dispositivo | ✅ |
| Sin cuentas, sin registro, sin servidores | ✅ |
| Código abierto (MIT) | ✅ |
| Traducción completa a 3 idiomas | ✅ |
| Tema claro, oscuro y premium | ✅ |
| Widget en pantalla de inicio | ✅ |

---

## ✨ Funcionalidades — Flujo Completo

### 1. 🏠 Usa Primero

**¿Qué hace?** Muestra los productos agrupados por urgencia: **Vencidos → Hoy → Mañana → Esta semana**.

```
                    ┌─────────────────────────────────────┐
                    │      📊 ¡N productos para usar!      │
                    │     $XX.XX en riesgo    Venc: X/Hoy: X│
                    └─────────────────────────────────────┘
                    ┌─────────────────────────────────────┐
                    │  [▶ Repaso Rápido]  [📖 Recetas]    │
                    └─────────────────────────────────────┘

  🔴 VENCIDOS (3)
  ┌─────────────────────────────────────┐
  │ ██ Leche · 1L · 🧊 Refrigerador     │ 🔴 Vencido │
  │ [✓ Resolver] [⏰ Posponer]           │            │
  └─────────────────────────────────────┘

  🟠 VENCE HOY (2)
  ┌─────────────────────────────────────┐
  │ ██ Yogur · 4uds · 🧊 Refrigerador   │ 🔴 Hoy     │
  │ [✓ Resolver] [⏰ Posponer]           │            │
  └─────────────────────────────────────┘

  🟡 VENCE MAÑANA (1) ...
  🟢 ESTA SEMANA (5) ...
```

**Flujo completo:**

1. Abres la app → pantalla "Usa Primero"
2. La app calcula urgencia automáticamente según `expirationDate`
3. Ves la **Hero Card** con total urgente + valor en riesgo
4. Puedes hacer **Repaso Rápido** (revisar uno por uno)
5. O tocar **Resolver** en cada producto
6. Se abre un diálogo: **Consumido** ✅ / **Desechado** ❌ / **Donado** 🎁 / **Conservar** 📦
7. Al resolver, aparece un **Snackbar** con opción "Deshacer"
8. También puedes **Posponer** (1, 3 o 7 días) para que desaparezca temporalmente

**¿Qué pasa si...?**

| Si... | Entonces... |
|:---|---|
| No hay productos urgentes | Aparece pantalla "¡Todo en orden!" con acceso a recetas |
| Pospones un producto | Se oculta hasta la fecha elegida, pero la fecha real no cambia |
| Borras la app sin exportar | Los datos se pierden (100% local) |
| Marcas algo como consumido por error | Usa "Deshacer" en el snackbar (vuelve a ACTIVE) |
| La fecha ya pasó hace días | Aparece en rojo como "Vencido" |
| Tienes 30 productos urgentes | Se muestran todos ordenados por fecha, con scroll |

---

### 2. 📦 Inventario Inteligente

**¿Qué hace?** Gestiona todos tus productos con búsqueda, filtros y entrada rápida.

```
  ┌─────────────────────────────────────┐
  │  🧾 Mi Inventario                  │
  │  12 productos registrados  📅 🛒    │
  └─────────────────────────────────────┘
  ┌─────────────────────────────────────┐
  │  🔍 Buscar productos...             │
  └─────────────────────────────────────┘
  [Todos] [🧊 Nevera] [🍞 Despensa] [❄️ Congelador] ...
  [Todas] [🥬 Frutas] [🥛 Lácteos] [🍗 Carnes] ...

  ┌─ Desliza derecha: ✅ Consumido ─────┐
  │  🥛 Leche · 1L · 🧊 Refrigerador   │
  │  Vence: 25 Jul          💰 $2.50   │
  └─ Desliza izquierda: ❌ Desechado ───┘
```

**Flujo completo:**

1. Desde la pestaña "Inventario" ves todos los productos activos
2. Usa **búsqueda** por nombre o notas
3. Filtra por **ubicación** (Nevera, Despensa, Congelador...)
4. Filtra por **categoría** (Frutas, Lácteos, Carnes...)
5. Tap en **+** (FAB) para añadir producto
6. Se abre el diálogo con modo **rápido** y **completo**

**Diálogo Añadir/Editar — Modo Rápido:**

```
  ┌─────────────────────────────────────┐
  │  ➕ Añadir Producto                 │
  │                                     │
  │  Productos frecuentes: [🥛Leche×5]  │
  │  Plantillas: [🥛Leche][🥚Huevos]... │
  │                                     │
  │  📝 ¿Qué es?  _________________     │
  │  [📱 Escanear código de barras]     │  ← NUEVO
  │                                     │
  │  Ubicación: 🧊 Nevera (seleccionado)│
  │  Tipo vencimiento: 📅 Fijo          │
  │  📅 Fecha: 25/07/2026               │
  │                                     │
  │  [▼ Más detalles]                   │
  │                                     │
  │       [Cancelar]    [Guardar]       │
  └─────────────────────────────────────┘
```

**Diálogo Añadir/Editar — Modo Completo:**

```
  ┌─────────────────────────────────────┐
  │  [▲ Menos detalles]                 │
  │                                     │
  │  Categoría: [🥬 Frutas y Verduras ▼]│
  │  💰 Precio ($): [____]   Unidad: v]│
  │  📦 Cantidad: [____]                │
  │  🏷️ Marca: [________________]       │
  │  ⚠️ Stock mínimo: [____]            │
  │  📝 Notas: [________________]        │
  │                                     │
  │       [Cancelar]    [Guardar]       │
  └─────────────────────────────────────┘
```

**¿Qué pasa si...?**

| Si... | Entonces... |
|:---|---|
| Usas una plantilla rápida | Se autocompleta nombre, categoría y ubicación |
| El producto ya existe en la lista de compras | No se duplica al añadir desde inventario |
| Marcas "Sin vencimiento" | La fecha se oculta y el producto nunca aparece como urgente |
| Usas modo ESTIMADO | La app predice la fecha según categoría+ubicación |
| Pones precio 0 | No se muestra el valor económico en estadísticas |
| Es la primera vez que añades | Los campos usan valores por defecto sensatos |
| Deslizas un producto a la derecha | Se marca como consumido (con snackbar para deshacer) |
| Deslizas un producto a la izquierda | Se marca como desechado (con snackbar para deshacer) |
| El producto baja del stock mínimo | Aparece una etiqueta 🛒 "Stock bajo" |

---

### 3. 📊 Estadísticas y Logros

**¿Qué hace?** Visualiza tu consumo mensual, tasa de aprovechamiento, salud del inventario y logros ganados.

```
  ┌─────────────────────────────────────┐
  │  📈 Esta semana                     │
  │  ¡Te ocupaste de 8 productos!       │
  │  💰 Valor aprovechado: $15.50       │
  │  Consumido: 6 · Donado: 1           │
  │  Más desperdiciado: Lácteos         │
  └─────────────────────────────────────┘

  ┌─────────────────────────────────────┐
  │  🏆 Logros                          │
  │  🛡️ Primer Rescate  🔥 Racha 7d     │
  │  🎁 Corazón Generoso  🌱 Semana ZW  │
  │  👁️ Consciente                      │
  └─────────────────────────────────────┘

  ┌─────────────────────────────────────┐
  │  📊 Tu actividad en julio           │
  │                                     │
  │  [✅ Consumido]  [❌ Desechado]     │
  │       12              3             │
  │  [🎁 Donado]    [🛡️ Rescatado]     │
  │       1               8             │
  │                                     │
  │       📈 Tasa de consumo            │
  │       ┌─────────────┐              │
  │       │  🟢 80%     │              │
  │       │ aprovechado │              │
  │       └─────────────┘              │
  │                                     │
  │  💰 Valor económico este mes        │
  │  ● Consumido: $24.00                │
  │  ● Desperdiciado: $6.00             │
  │  ● Donado: $3.50                    │
  │                                     │
  │  🗑️ Categorías con más desperdicio  │
  │  Frutas y Verduras · 2 productos    │
  │                                     │
  │  📋 Salud del inventario            │
  │  🟢 Frescos: 15  ████████░░░       │
  │  🟡 Por vencer: 3  ██░░░░░░░░░     │
  │  🔴 Vencidos: 1   ░░░░░░░░░░░      │
  └─────────────────────────────────────┘
```

**¿Qué pasa si...?**

| Si... | Entonces... |
|:---|---|
| Nunca has resuelto ningún producto | Aparece un card informativo con ayuda |
| No pusiste precios | Las secciones de valor económico se ocultan |
| Tienes racha de 30+ días | Obtienes el logro "Racha de 30 Días" |
| Desperdicias algo | Obtienes el logro "Consciente" (tu primer desperdicio) |
| Consumes antes del vencimiento | Se cuenta como "Rescatado" |
| No desperdicias nada en una semana | Obtienes "Semana Zero Waste" |

---

### 4. 🛒 Lista de Compras

**¿Qué hace?** Crea y gestiona tu lista de compras agrupada por categoría, con verificación antes de comprar.

```
  ┌─────────────────────────────────────┐
  │  🛒 Lista de Compras    [Compartir] │
  │                                     │
  │  📋 PENDIENTES                      │
  │  ☐ 🥛 Leche · 1L                    │
  │  ☐ 🥚 Huevos · 12uds               │
  │  ☐ 🍞 Pan · 1uds                    │
  │                                     │
  │  ✅ COMPRADOS                       │
  │  ☑ 🍎 Manzanas · 1kg                │
  │                                     │
  │  [➕ Añadir]  [🧹 Limpiar comprados] │
  │  [🔍 Revisar lo que ya tienes]       │
  └─────────────────────────────────────┘
```

**¿Qué pasa si...?**

| Si... | Entonces... |
|:---|---|
| Tocas "Revisar lo que ya tienes" | La app cruza tu lista contra tu inventario ignorando **mayúsculas, tildes y espacios repetidos** (ej. "café" coincide con "CAFE") |
| Compartes la lista | Se envía como texto plano vía WhatsApp, email, etc. |
| Limpias comprados | Se borran todos los items marcados como comprados |
| Añades un item que ya existe | No se duplica (se ignora) |

---

### 5. 📅 Planificador Semanal

**¿Qué hace?** Planifica qué vas a comer cada día de la semana usando recetas de tu despensa.

```
  ┌─────────────────────────────────────┐
  │  📅 Plan Semanal                    │
  │                                     │
  │  Lun │ Sin plan      🥣🍝🌙        │
  │  Mar │ 🥣 Batido                    │
  │      │ 🍝 Pasta Alfredo    [×]      │
  │      │ 🌙 Revuelto                 │
  │  Mié │ 🥣 Sin plan                  │
  │  ...                                │
  │                                     │
  │  🛒 Ingredientes faltantes          │
  │  + Jamón                            │
  │  + Zanahoria                        │
  └─────────────────────────────────────┘
```

**¿Qué pasa si...?**

| Si... | Entonces... |
|:---|---|
| No seleccionas ninguna receta para un día | Aparece como "Sin plan" |
| Tocas un icono de comida | Se abre el selector de recetas |
| Todos los ingredientes están en tu despensa | La lista de faltantes estará vacía |
| Cambias de semana | Los planes se guardan por semana |
| Quitas una receta | Se elimina del día y se recalcula la lista de compras |

---

### 6. 🍳 Recetas Inteligentes

**¿Qué hace?** Te muestra recetas que puedes preparar con lo que ya tienes, priorizando lo que está por vencer.

```
  ┌─────────────────────────────────────┐
  │  📖 Recetas con tu despensa        │
  │                                     │
  │  🟢 Tienes todos los ingredientes    │
  │  ┌─────────────────────────────────┐│
  │  │ 🥬 Tortilla de Patatas          ││
  │  │ ⏱️ 30 min · 🟢 Fácil            ││
  │  │ 🟢 Huevo 🟢 Patata 🟢 Cebolla   ││
  │  │ [Ver pasos ▼]                   ││
  │  └─────────────────────────────────┘│
  │                                     │
  │  🟡 Te faltan ingredientes          │
  │  ┌─────────────────────────────────┐│
  │  │ 🍗 Arroz con Pollo              ││
  │  │ ⏱️ 40 min · 🟢 Fácil            ││
  │  │ 🟢 Arroz 🔴 Pollo 🔴 Zanahoria  ││
  │  └─────────────────────────────────┘│
  └─────────────────────────────────────┘
```

**¿Qué pasa si...?**

| Si... | Entonces... |
|:---|---|
| No tienes ningún ingrediente de ninguna receta | Aparece mensaje "No hay recetas disponibles" |
| Tienes todos los ingredientes | La receta aparece como "✓ Completa" |
| Te falta algún ingrediente | Se muestra en rojo cuáles faltan |
| Hay muchas recetas coincidentes | Se ordenan por las que usan **más ingredientes urgentes** primero |
| Tienes productos por vencer | Las recetas que los usan aparecen al principio de la lista |
| Tocas "Ver pasos" | Se despliegan ingredientes detallados y paso a paso |

---

### 7. 📱 Escáner de Código de Barras

**¿Qué hace?** Escanea códigos de barras con la cámara para añadir productos al instante.

```
  ┌─────────────────────────────────────┐
  │                                     │
  │      ┌───────────────────┐          │
  │      │  ┌─┐         ┌─┐  │          │
  │      │  │ │         │ │  │          │
  │      │  └─┘         └─┘  │          │
  │      └───────────────────┘          │
  │                                     │
  │   Apunta el código de barras        │
  │   al centro                         │
  │                                     │
  │                    [✕]              │
  └─────────────────────────────────────┘
```

**Flujo completo:**

1. Tocas "Escanear código de barras" en el diálogo de añadir producto
2. Se solicita permiso de cámara (si no lo has concedido)
3. ML Kit analiza el flujo de cámara en tiempo real
4. Al detectar un código, se cierra el escáner y se guarda el código
5. El código aparece en el botón de escaneo

**¿Qué pasa si...?**

| Si... | Entonces... |
|:---|---|
| No concedes permiso de cámara | Aparece pantalla de solicitud con botón para conceder |
| La cámara no encuentra código | Sigue escaneando hasta que detecte uno |
| Escaneas un código ya registrado | El DAO tiene búsqueda por código de barras (preparado para futuro lookup) |
| No hay suficiente luz | Aparece imagen oscura; necesitas mejor iluminación |
| Tu dispositivo no tiene cámara | Se requiere `android.hardware.camera` (checks en Manifest) |

---

### 8. 🔮 Predicción de Caducidad

**¿Qué hace?** Cuando seleccionas "Estimado" como tipo de vencimiento, la app predice automáticamente la fecha según la categoría y ubicación del producto.

**Tabla de predicción por defecto:**

| Categoría | Nevera | Despensa | Congelador |
|:---|:---:|:---:|:---:|
| 🥬 Frutas y Verduras | 5 días | 3 días | 90 días |
| 🥛 Lácteos y Huevos | 14 días | — | 60 días |
| 🍗 Carnes y Pescados | 3 días | — | 180 días |
| 🍞 Panadería | 7 días | 5 días | 60 días |
| 🥫 Despensa / Granos | 30 días | 365 días | — |
| ❄️ Congelados | 7 días | — | 180 días |
| 🥤 Bebidas | 30 días | 180 días | — |

**¿Qué pasa si...?**

| Si... | Entonces... |
|:---|---|
| Seleccionas "Estimado" | Aparece un card informativo con los días estimados y se auto-fija la fecha |
| Cambias la categoría o ubicación | Se recalcula la fecha automáticamente |
| No hay datos para la combinación | Usa valor por defecto de 7 días |
| Quieres poner otra fecha | Puedes cambiarla manualmente en el selector de fecha |

---

### 9. 🏆 Gamificación

**¿Qué hace?** Gana logros automáticamente según tu comportamiento. Todo se calcula en el dispositivo.

| Logro | Cómo obtenerlo | 🏅 |
|:---|---|:---:|
| 🛡️ **Primer Rescate** | Consume un producto antes de que caduque | 🥇 |
| 🛡️ **Rescatista** | Rescata 10 productos | 🥈 |
| 🛡️ **Héroe del Rescate** | Rescata 50 productos | 🥉 |
| 🎁 **Corazón Generoso** | Dona o comparte tu primer producto | 🥇 |
| 🎁 **Filántropo** | Dona 10 productos | 🥈 |
| 🔥 **Racha de 7 Días** | Abre la app 7 días seguidos | 🥇 |
| 🔥 **Racha de 30 Días** | Un mes sin perder el control | 🥉 |
| 🌱 **Semana Zero Waste** | No desperdicies nada en una semana | 🥈 |
| 🌟 **Mes Zero Waste** | Un mes entero sin desperdicio | 🥉 |
| 🧾 **Despensa Abastecida** | Registra 50 productos | 🥈 |
| 👑 **Maestro de la Despensa** | Registra 100+ productos | 🥉 |
| 👁️ **Consciente** | Marca tu primer desperdicio | 🥇 |

**¿Qué pasa si...?**

| Si... | Entonces... |
|:---|---|
| Abres la app cada día | Se incrementa tu racha automáticamente |
| Dejas de abrir la app un día | La racha se reinicia a 1 |
| Consigues un nuevo logro | Aparece en la sección de Logros en Estadísticas |
| Tienes más de 6 logros | Se muestran los primeros 6 y un "+X más" |
| Marcas un producto como desperdicio | Obtienes el logro "Consciente" inmediatamente |

---

### 10. ⚙️ Ajustes

**¿Qué hace?** Configura región, notificaciones, tema, exportación y privacidad.

```
  ┌─────────────────────────────────────┐
  │  ⚙️ Ajustes                          │
  │                                     │
  │  ┌─ 🌐 Ubicación y Moneda ─────────┐│
  │  │ País: [_______________]          ││
  │  │ Moneda: [USD]  Símbolo: [$]    ││
  │  └─────────────────────────────────┘│
  │                                     │
  │  ┌─ 🔔 Notificaciones Diarias ─────┐│
  │  │ Recordatorios de caducidad [🔘]  ││
  │  │ Hora: 09:00           [Modificar]││
  │  │ [🔔 Probar Notificación]         ││
  │  └─────────────────────────────────┘│
  │                                     │
  │  ┌─ 🎨 Apariencia ─────────────────┐│
  │  │ [⚙️Sistema] [☀️Claro] [🌙Oscuro]││
  │  │ [🌟Premium]                      ││
  │  └─────────────────────────────────┘│
  │                                     │
  │  ┌─ 💾 Datos y Respaldo ──────────┐│
  │  │ Exportar todo tu inventario     ││
  │  │ [📄 JSON] [📊 CSV]              ││
  │  └─────────────────────────────────┘│
  │                                     │
  │  ┌─ ℹ️ Acerca de ─────────────────┐│
  │  │ Privacidad: 100% local          ││
  │  │ Opciones de anuncios            ││
  │  └─────────────────────────────────┘│
  └─────────────────────────────────────┘
```

**¿Qué pasa si...?**

| Si... | Entonces... |
|:---|---|
| Cambias de país o moneda | Se actualiza el símbolo en toda la app |
| Activas notificaciones en Android 13+ | Se solicita permiso de notificaciones |
| Desactivas notificaciones | Se cancela la alarma programada |
| Seleccionas tema Premium | La app cambia a una paleta dorado/aubergine oscura |
| Exportas a JSON | Se genera un archivo JSON completo y se abre el share sheet |
| Exportas a CSV | Se genera un CSV con todos los productos y se abre el share sheet |

---

### 11. 🔔 Notificaciones y Widget

**¿Qué hace?** Te recuerda diariamente los productos por vencer y muestra los 3 más urgentes en tu pantalla de inicio.

```
  ┌─────────────────────────────────────┐
  │  🔔 Despensa al Día                 │
  │  3 productos vencidos               │
  │  🥛 Leche - ¡corre!                 │
  │  🥚 Huevos - Vencen hoy             │
  │  💰 $5.50 en riesgo                 │
  └─────────────────────────────────────┘

  ┌─── Widget (3 urgentes) ─────────────┐
  │  📋 Despensa al Día                 │
  │  🥛 Leche · Vencido                 │
  │  🥚 Huevos · Vence hoy ⚠️           │
  │  🍞 Pan · Mañana                    │
  └─────────────────────────────────────┘
```

**¿Qué pasa si...?**

| Si... | Entonces... |
|:---|---|
| No hay productos urgentes | No se muestra notificación ese día |
| Tocas la notificación | Abre la app directamente |
| Tocas el widget | Abre la app |
| Desinstalas la app | Las notificaciones y widget se eliminan automáticamente |
| Cambias la hora en ajustes | Se reprograma la alarma con la nueva hora |

---

### 12. 📤 Exportación

**¿Qué hace?** Exporta todo tu inventario a JSON o CSV para respaldo o análisis.

**JSON — Estructura completa:**
```json
[
  {
    "name": "Leche",
    "category": "DAIRY_EGGS",
    "totalPrice": 2.50,
    "quantity": 1.0,
    "unit": "L",
    "location": "FRIDGE",
    "expiryType": "FIXED",
    "expirationDate": 1782345600000,
    "status": "ACTIVE",
    "barcode": null,
    "brand": "Marca Ejemplo"
  }
]
```

**CSV — Columnas:**
```
name,category,totalPrice,quantity,unit,location,expiryType,expirationDate,status,barcode,brand,notes
Leche,DAIRY_EGGS,2.50,1,L,FRIDGE,FIXED,1782345600000,ACTIVE,,Marca Ejemplo,
```

**¿Qué pasa si...?**

| Si... | Entonces... |
|:---|---|
| No tienes productos | Se exporta un array JSON vacío o CSV con solo cabeceras |
| Abres el JSON en Excel | Necesitarás importarlo como datos JSON |
| Compartes el archivo | Se usa el Intent del sistema (Android Share Sheet) |
| Quieres restaurar | Puedes hacerlo manualmente importando el JSON |

---

### 13. ⏰ Posponer y Repaso Rápido

**¿Qué hace?** Posponer oculta productos urgentes temporalmente sin modificar su fecha de vencimiento real. El Repaso Rápido permite resolver productos uno por uno con acciones rápidas y opción de posponer.

```
  ┌─────────────────────────────────────┐
  │  ⏰ Posponer aviso de Leche          │
  │  La fecha de vencimiento no cambiará │
  │  ┌─────────────────────────────────┐│
  │  │ ⏰ 1 día                        ││
  │  │ ⏰ 3 días                       ││
  │  │ ⏰ 7 días                       ││
  │  └─────────────────────────────────┘│
  │              [Cancelar]             │
  └─────────────────────────────────────┘

  ┌─── Repaso Rápido ───────────────────┐
  │  🔄 3 de 8                          │
  │  ████████░░░░░░░░░░░░               │
  │                                     │
  │         🥛 Leche                    │
  │      Vencido · 1L · 🧊 Nevera       │
  │                                     │
  │  [✅ Consumido]  [🎁 Donado]        │
  │  [⏰ Posponer]    [⏭ Saltar]        │
  └─────────────────────────────────────┘
```

**Flujo completo:**

1. Tocas "Posponer" en un producto urgente → se abre diálogo con 1, 3 o 7 días
2. El producto desaparece de "Usa Primero" hasta que `snoozeUntil` se cumpla
3. Para cancelar, puedes editar el producto y limpiar el campo snooze
4. Tocas "Repaso Rápido" → se abre un bottom sheet con productos uno a uno
5. Puedes: Consumir ✅, Donar 🎁, Desechar ❌, o Posponer ⏰
6. Si saltas, el producto queda sin resolver (vuelve a aparecer en la lista)
7. Al terminar, ves resumen: cuántos resolviste, valor ahorrado

**¿Qué pasa si...?**

| Si... | Entonces... |
|:---|---|
| Pospones 1 día | El producto se oculta hasta mañana a la misma hora |
| Pospones 7 días | Desaparece una semana, aunque su fecha real sea hoy |
| Llega la fecha de snooze | El producto vuelve a aparecer según su urgencia real |
| Cancelas el Repaso Rápido | Los productos pendientes no se modifican |
| Resuelves todos los productos | Aparece pantalla de finalización con resumen y estadísticas |

---

## 🎨 Identidad Visual — Temas y Colores

Despensa al Día tiene **3 temas completos**: Claro, Oscuro y Premium (Astral), todos con Material 3.

### Tema Claro — "Jardín"

| Rol | Código | Descripción |
|:---|---:|:---|
| Fondo | `#F7F5EE` | Marfil cálido |
| Superficie | `#F5F0E0` | Paneles |
| Verde acción | `#2E7D5B` (EmeraldDark) | Botones, disponible |
| Verde suave | `#DDF1DF` | Contenedores de acciones |
| Ámbar atención | `#F6C76D` (Amber) | Próximo a vencer |
| Coral urgencia | `#F1846B` (Coral) | Vencido, hoy |
| Lavanda donación | `#B8A4E8` (Sky) | Donación, acción positiva |
| Texto primario | `#1A241E` | Títulos, cuerpo |
| Texto secundario | `#6B7280` (TextSecondary) | Subtítulos |

### Tema Oscuro — "Bosque"

| Rol | Código | Descripción |
|:---|---:|:---|
| Fondo base | `#101814` | Verde bosque profundo |
| Superficie | `#18231D` | Paneles y secciones |
| Tarjeta | `#203027` (DarkSurfaceVariant) | Componentes elevados |
| Verde claro | `#62C99A` (Emerald) | Acciones, estado fresco |
| Verde brillante | `#8ED6A2` (EmeraldLight) | Iconos, badges |
| Ámbar | `#F6C76D` (Amber) | Atención próxima |
| Coral | `#F1846B` (Coral) | Urgencia, vencido |
| Lavanda | `#B8A4E8` (Sky) | Donación |
| Texto claro | `#F2F5EF` (TextOnDark) | Texto principal |
| Texto secundario | `#9CA3AF` (TextOnDarkSecondary) | Subtítulos |

### Tema Premium (Astral) — "Noche Estrellada"

| Rol | Código | Descripción |
|:---|---:|:---|
| Fondo base | `#080A18` | Azul medianoche |
| Superficie | `#0C0F24` | Paneles |
| Tarjeta | `#141A33` | Componentes elevados |
| Acento | `#D4AF37` | Dorado metálico |
| Atención | `#E27D60` | Terracota |
| Texto | `#E8E6F0` | Blanco suave |

### Urgencia por colores

| Bucket | Tema Claro | Tema Oscuro |
|:---|---:|:---:|
| `EXPIRED` | Coral `#F1846B` | Coral `#F1846B` |
| `TODAY` | Coral `#F1846B` | Coral `#F1846B` |
| `TOMORROW` | Ámbar `#F6C76D` | Ámbar `#F6C76D` |
| `THIS_WEEK` | Ámbar `#F6C76D` | Ámbar `#F6C76D` |
| `NONE` | Verde `#2E7D5B` | Verde `#62C99A` |

### UrgencyBucket enum

```kotlin
enum class UrgencyBucket {
    EXPIRED,     // 🔴 Fecha ya pasó
    TODAY,       // 🔴 Vence hoy
    TOMORROW,    // 🟡 Vence mañana
    THIS_WEEK,   // 🟡 Vence en los próximos 7 días
    NONE         // 🟢 Sin urgencia
}
```

### Principios visuales

- **No es una app ecológica genérica:** evitamos verde brillante y estética infantil. Usamos tonos vegetales apagados, elegantes y globales.
- **El color comunica urgencia sin alarmar:** verde para disponible, ámbar para atención, coral para prioridad, lavanda para donación.
- **Microinteracciones funcionales:** animaciones sutiles en tarjetas, hero card con contador animado y fondos con degradado ambiental.
- **Jerarquía clara:** el nombre del producto y su urgencia son lo principal; ubicación, cantidad y precio son secundarios.
- **Iconografía consistente:** Material Symbols redondeados para acciones, ubicaciones y categorías.

---

## 🏗️ Arquitectura

```
                        ┌──────────────────────┐
                        │     MainActivity      │
                        │  (Single Activity)    │
                        └──────────┬───────────┘
                                   │
                   ┌───────────────┴───────────────┐
                   │      PantryAppContainer       │
                   │  NavigationBar: 4/5 pestañas  │
                   └───┬───┬───┬───┬───┬──────────┘
                       │   │   │   │   │
         ┌─────────────┘   │   │   │   └──────────────┐
         ▼                 ▼   ▼   ▼                  ▼
  ┌────────────┐   ┌────────────┐   ┌────────────┐   ┌────────────┐
  │UseFirstScr │   │DashboardScr│   │ StatsScreen│   │SettingsScr │
  │  (Usa 1°)  │   │(Inventario)│   │(Estadísticas│   │ (Ajustes)  │
  └──────┬─────┘   └──────┬─────┘   └──────┬─────┘   └──────┬─────┘
         │                │                │                │
         ▼                ▼                ▼                ▼
  ┌─────────────────────────────────────────────────────────────┐
  │                    PantryViewModel                          │
  │  • activeProductsState  • consumedProductsState              │
  │  • wastedProductsState  • donatedProductsState               │
  │  • settingsState        • shoppingItemsState                 │
  │  • frequentProductsState  • earnedAchievements               │
  │  • recipeSuggestions                                        │
  └──────────────────────────┬──────────────────────────────────┘
                             │
                             ▼
  ┌─────────────────────────────────────────────────────────────┐
  │                    PantryRepository                         │
  └──────────────────────────┬──────────────────────────────────┘
                             │
                             ▼
  ┌─────────────────────────────────────────────────────────────┐
  │                      PantryDao (Room)                       │
  │  • getActiveProducts() • getProductsByStatus()               │
  │  • getProductsExpiringBefore() • getExpiredProducts()        │
  │  • getResolvedProductsInRange() • getProductByBarcode()     │
  │  • CRUD: insert/update/delete                                │
  └──────────────────────────┬──────────────────────────────────┘
                             │
              ┌──────────────┴──────────────┐
              ▼                             ▼
   ┌──────────────────┐         ┌──────────────────┐
   │  products (Room) │         │  app_settings    │
   │  shopping_items  │         │  (singleton)     │
   └──────────────────┘         └──────────────────┘
```

**Flujo de datos:**

```
  Usuario toca "Consumir"
       │
       ▼
  UseFirstScreen / DashboardScreen
       │
       ▼
  PantryViewModel.markAsConsumed(product)
       │
       ▼
  PantryRepository.updateProduct(product.copy(status=CONSUMED))
       │
       ▼
  PantryDao.updateProduct(product)
       │
       ▼
  Room database (products table)
       │
       ▼
  StateFlow re-emite (activeProducts, consumedProducts)
       │
       ▼
  UI se actualiza automáticamente
  Snackbar "Leche consumido" + "Deshacer"
```

---

## 🗂️ Estructura del Proyecto

```
app/src/main/java/com/example/
├── MainActivity.kt                # Punto de entrada (Single Activity)
├── data/
│   ├── Achievement.kt             # 🆕 Sistema de logros (12 badges)
│   ├── local/
│   │   ├── AppDatabase.kt         # Room (singleton, migraciones v1→v2→v3→v4→v5)
│   │   ├── AppSettings.kt         # Configuración (moneda, tema, racha, notif.)
│   │   ├── Converters.kt          # TypeConverters para enums
│   │   ├── PantryDao.kt           # DAO con queries reactivas (Flow)
│   │   ├── Product.kt             # Entidad producto (24 campos)
│   │   ├── ProductEnums.kt        # Enums: Status, ExpiryType, Location, Category
│   │   ├── ProductFrequent.kt     # Productos frecuentes (view)
│   │   └── ShoppingItem.kt        # Entidad lista de compras
│   ├── recipe/
│   │   ├── Recipe.kt              # 🆕 Data class Receta
│   │   └── RecipeCatalog.kt       # 🆕 11 recetas predefinidas + matching engine
│   └── repository/
│       └── PantryRepository.kt    # Capa de acceso a datos
├── receiver/
│   └── NotificationReceiver.kt    # AlarmManager para notificaciones
├── ui/
│   ├── screens/
│   │   ├── BarcodeScannerScreen.kt  # 🆕 Escáner con CameraX + ML Kit
│   │   ├── DashboardScreen.kt       # Inventario + diálogo add/edit + escáner
│   │   ├── MealPlannerSheet.kt      # 🆕 Planificador semanal de comidas
│   │   ├── OnboardingScreen.kt      # Onboarding 3 páginas
│   │   ├── RecipeSuggestionsSheet.kt # 🆕 Recetas según tu inventario
│   │   ├── SettingsScreen.kt        # Ajustes, exportación, tema
│   │   ├── SetupScreen.kt           # Selección inicial de país/moneda
│   │   ├── ShoppingListSheet.kt     # Bottom sheet lista de compras
│   │   ├── StatsScreen.kt           # Estadísticas + 🆕 logros
│   │   └── UseFirstScreen.kt        # "Usa Primero" + 🆕 recetas
│   ├── theme/
│   │   ├── Color.kt                 # Paleta Jardín de Frescura (67 colores)
│   │   ├── Theme.kt                 # Tema claro/oscuro/premium Material3
│   │   └── Type.kt                  # Tipografía (14 estilos)
│   ├── ads/
│   │   ├── AdManager.kt             # AdMob Native Ads
│   │   ├── ConsentManager.kt        # Google UMP
│   │   └── NativeAdCard.kt          # Composable de anuncio nativo
│   └── viewmodel/
│       └── PantryViewModel.kt       # ViewModel compartido + 🆕 logros/recetas
├── utils/
│   ├── BackupHelper.kt              # Exportación JSON/CSV
│   ├── ExpiryPredictor.kt           # 🆕 Predicción de caducidad por categoría
│   ├── MealPlannerHelper.kt         # 🆕 Almacenamiento y matching del plan
│   └── UrgencyUtils.kt              # 🆕 UrgencyBucket, getUrgentProducts, isCurrentlySnoozed
└── widget/
    └── PantryWidgetProvider.kt      # Widget hogar (3 urgentes)

res/
├── values/strings.xml               # Inglés (239 strings)
├── values-es/strings.xml            # Español (225 strings)
├── values-pt-rBR/strings.xml        # Portugués (223 strings)
├── layout/pantry_widget.xml         # Layout del widget
├── layout/native_ad_layout.xml      # Template de anuncio nativo
```

---

## 🔧 Tecnologías

| Categoría | Tecnología | Versión |
|:---|---|:---:|
| **Lenguaje** | Kotlin | 2.2.10 |
| **UI** | Jetpack Compose + Material Design 3 | BOM 2024.09 |
| **Arquitectura** | MVVM + ViewModel + StateFlow | — |
| **Base de datos** | Room (SQLite) + KSP | 2.7.0 |
| **Cámara** | CameraX | 1.5.0 |
| **Visión** | ML Kit Barcode Scanning | 17.3.0 |
| **Notificaciones** | AlarmManager + NotificationChannel | — |
| **Widget** | AppWidgetProvider + RemoteViews | — |
| **Exportación** | FileProvider + ContentResolver | — |
| **Anuncios** | Google AdMob Native Ads | 23.6.0 |
| **Consentimiento** | Google UMP | 2.2.0 |
| **Testing** | JUnit 4, Robolectric, Roborazzi | — |

---

## 🚀 Cómo Ejecutar

### Requisitos

| Herramienta | Versión |
|:---|---|
| [Android Studio](https://developer.android.com/studio) | Última estable |
| JDK | 17+ |
| Gradle | 8.x (incluido) |
| Dispositivo / Emulador | Android 7.0+ (API 24) |

### Pasos

```bash
# 1. Clona el repositorio
git clone https://github.com/anomalyco/despensa-al-dia.git
cd despensa-al-dia

# 2. Abre con Android Studio
# File → Open → selecciona la carpeta del proyecto

# 3. O compila desde terminal
./gradlew assembleDebug

# 4. Instala en dispositivo conectado
./gradlew installDebug
```

---

## ❓ FAQ / ¿Qué pasa si...?

### Instalación y configuración

| Pregunta | Respuesta |
|:---|---|
| ¿Necesito conexión a internet? | No, la app funciona 100% offline. Solo necesitas internet para anuncios. |
| ¿Necesito crear una cuenta? | No. Todos tus datos son locales. |
| ¿Se pierden mis datos si borro la app? | Sí. Usa la exportación JSON antes de desinstalar. |
| ¿Puedo migrar datos a otro teléfono? | Exporta a JSON en el origen, transfiere el archivo, e impórtalo en el destino (función próxima). |
| ¿Qué hago si la app no abre? | Asegúrate de tener Android 7.0+ (API 24+). |

### Productos

| Pregunta | Respuesta |
|:---|---|
| ¿Puedo añadir productos sin fecha? | Sí, selecciona "Sin vencimiento" |
| ¿Qué significa "Estimado"? | La app calcula la fecha según la categoría y ubicación. Ej: pollo en nevera ≈ 3 días. |
| ¿Puedo editar un producto después? | Sí, toca la tarjeta del producto en Inventario. |
| ¿Puedo añadir productos desde el escáner? | Escanea el código, se guarda en el producto. |
| ¿Cómo cambio la cantidad? | Edita el producto y cambia la cantidad. |

### Urgencia y notificaciones

| Pregunta | Respuesta |
|:---|---|
| ¿A qué hora llegan las notificaciones? | A la hora que configures en Ajustes (default: 9:00). |
| ¿Pasa algo si snoozeo un producto? | Se oculta temporalmente. La fecha real no cambia. |
| ¿Qué pasa con los "Best Before"? | Se muestran con un hint: "Consumo preferente — revisar antes de usar". |
| ¿Puedo desactivar las notificaciones? | Sí, desde Ajustes → Notificaciones. |

### Estadísticas y gamificación

| Pregunta | Respuesta |
|:---|---|
| ¿Cómo consigo la racha? | Abre la app al menos una vez al día. |
| ¿Se reinicia la racha? | Sí, si no abres la app en 24h se reinicia a 1. |
| ¿Cuántos logros hay? | 12 logros en 3 niveles (🥇🥈🥉). |
| ¿Puedo compartir mis logros? | Por ahora son solo locales. ¡Próximamente! |
| ¿Las estadísticas son mensuales? | Sí, se reinician cada mes natural. |

### Recetas y planificación

| Pregunta | Respuesta |
|:---|---|
| ¿Cómo se qué recetas puedo hacer? | La app cruza tus productos contra 11 recetas predefinidas. |
| ¿Puedo añadir mis propias recetas? | No todavía, pero el sistema está preparado para ello. |
| ¿El plan semanal se guarda? | Sí, en almacenamiento local del dispositivo. |
| ¿Puedo compartir el plan semanal? | No directamente. Puedes compartir la lista de la compra. |

### Privacidad y datos

| Pregunta | Respuesta |
|:---|---|
| ¿La app envía mis datos a algún servidor? | No. Todo es 100% local. |
| ¿Qué hacen los anuncios? | Google AdMob muestra anuncios nativos. Puedes configurar tu privacidad en Ajustes. |
| ¿Puedo borrar todos mis datos? | Desinstala la app o borra los datos desde Ajustes del sistema. |
| ¿Cómo exporto mis datos? | Ajustes → Datos y Respaldo → JSON o CSV. |

---

## 📄 Licencia

```
MIT License

Copyright (c) 2026 Despensa al Día

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```

---

<div align="center">
  <br>
  <p>Hecho con 💚 por <strong>Despensa al Día</strong></p>
  <p><em>Reduce el desperdicio. Ahorra dinero. Come mejor.</em></p>
  <br>
  <p>
    <a href="#-tabla-de-contenidos">⬆ Volver arriba</a>
  </p>
  <br>
</div>
