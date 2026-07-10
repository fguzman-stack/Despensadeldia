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