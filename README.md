<div align="center">
  <h1>Despensa al Día</h1>
  <p><strong>Ve lo que importa hoy. Aprovecha lo que ya tienes.</strong></p>
  <p><em>Organiza tu hogar, usa primero lo urgente y convierte menos desperdicio en más ahorro.</em></p>
  <br>
</div>

Despensa al Día es una aplicación móvil **offline y privada** que te ayuda a cuidar mejor lo que compraste. No es solo un inventario: es una guía diaria que te muestra qué usar primero, registra tus hábitos de consumo y desperdicio, y te ayuda a aprovechar mejor tus productos del hogar. Tus datos se guardan localmente en tu dispositivo. Sin cuentas, sin servidores.

## Funcionalidades

- **Usa Primero** — Pantalla principal que responde una sola pregunta: ¿qué debo usar primero?. Agrupa productos por urgencia (vencidos, hoy, mañana, esta semana) y permite resolverlos con un solo toque. Incluye tarjeta hero con resumen del día, valor en riesgo y acciones rápidas.
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

### 6. Clasificación por urgencia — lógica diaria

El corazón de "Usa Primero" es esta clasificación temporal que agrupa productos según su fecha de vencimiento. No depende de ningún servicio externo y se ejecuta íntegramente en el composable.

```kotlin
val now = System.currentTimeMillis()
val oneDayMs = 24L * 60 * 60 * 1000
val todayEnd = now + oneDayMs
val tomorrowEnd = now + 2 * oneDayMs
val weekEnd = now + 7 * oneDayMs

val expired = urgentProducts.filter { it.expirationDate!! < now }
val expiringToday = urgentProducts.filter { it.expirationDate!! in now until todayEnd }
val expiringTomorrow = urgentProducts.filter { it.expirationDate!! in todayEnd until tomorrowEnd }
val expiringThisWeek = urgentProducts.filter { it.expirationDate!! in tomorrowEnd until weekEnd }
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

*(Agrega aquí las capturas de tu aplicación)*

## Cómo ejecutar

### Requisitos

- [Android Studio](https://developer.android.com/studio) (última versión estable)
- Target SDK 36 (Android 16)

### Pasos

1. Abre Android Studio.
2. Selecciona **Open** y elige la carpeta del proyecto.
3. Permite que Android Studio resuelva las dependencias.
4. Ejecuta la aplicación en un emulador o dispositivo físico.

## Estructura del proyecto

```
app/src/main/java/com/example/
├── MainActivity.kt              # Punto de entrada (Single Activity)
├── data/
│   ├── local/
│   │   ├── AppDatabase.kt       # Room (singleton, migraciones v1→v2→v3)
│   │   ├── AppSettings.kt       # Entidad de configuración
│   │   ├── Converters.kt        # TypeConverters (enums)
│   │   ├── PantryDao.kt         # DAO con queries Flow-based
│   │   ├── Product.kt           # Entidad Producto
│   │   ├── ProductEnums.kt      # Enums: Status, ExpiryType, Location
│   │   └── ShoppingItem.kt      # Entidad Lista de Compras
│   ├── recipe/
│   │   └── Recipe.kt            # Data class Receta (no implementado)
│   └── repository/
│       └── PantryRepository.kt  # Capa de acceso a datos
├── receiver/
│   └── NotificationReceiver.kt  # AlarmManager para notificaciones
├── ui/
│   ├── screens/
│   │   ├── DashboardScreen.kt   # Inventario + diálogo add/edit
│   │   ├── OnboardingScreen.kt  # Onboarding de 3 páginas
│   │   ├── SettingsScreen.kt    # Ajustes, exportación, tema
│   │   ├── SetupScreen.kt       # Selección inicial de país/moneda
│   │   ├── ShoppingListSheet.kt # Bottom sheet de lista de compras
│   │   ├── StatsScreen.kt       # Estadísticas con gráfico donut
│   │   └── UseFirstScreen.kt    # "Usa Primero" — pantalla principal con hero card, tarjetas compactas y microanimaciones
│   ├── theme/
│   │   ├── Color.kt             # Paleta Jardín de Frescura
│   │   ├── Theme.kt             # Tema claro/oscuro Material3
│   │   └── Type.kt              # Tipografía con jerarquía optimizada
│   └── viewmodel/
│       └── PantryViewModel.kt   # ViewModel compartido
├── utils/
│   └── BackupHelper.kt          # Exportación a JSON/CSV
└── widget/
    └── PantryWidgetProvider.kt  # Widget de pantalla de inicio
```

## Tecnologías

- **Lenguaje:** Kotlin
- **UI:** Jetpack Compose (Material Design 3) con tema Jardín de Frescura
- **Animaciones:** Compose Animation API (spring, tween, fade-in, contadores animados)
- **Base de datos:** Room (SQLite local con migraciones)
- **Arquitectura:** MVVM con StateFlow y Coroutines
- **Localización:** Android resource qualifiers (values, values-es, values-pt-rBR)
- **Componentes:** Notificaciones (AlarmManager), Widget (AppWidgetProvider), Exportación (FileProvider)
- **Tipografía:** FontFamily.Default con jerarquía optimizada (ExtraBold a Medium) y espaciado refinado

## Licencia

Este proyecto está bajo la licencia MIT. Consulta el archivo `LICENSE` para más detalles.