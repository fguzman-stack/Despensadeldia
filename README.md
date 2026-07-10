<div align="center">
  <h1>Despensa al Día</h1>
  <p><strong>Organiza tus alimentos, reduce desperdicios y aprovecha mejor tus compras.</strong></p>
  <br>
</div>

Despensa al Día es una aplicación móvil **offline y privada** que ayuda a personas de cualquier lugar a organizar alimentos y productos del hogar. Registra productos en espacios como despensa, refrigerador, congelador y otros; recibe recordatorios antes de fechas importantes y conoce tus hábitos de consumo y desperdicio. Tus productos y registros se guardan localmente en tu dispositivo.

## Funcionalidades

- **Usa Primero** — Pantalla principal que agrupa tus productos por urgencia: vencidos, vence hoy, vence mañana, vence esta semana. Acciones rápidas para resolver o posponer.
- **Inventario Inteligente** — Registro rápido con plantillas predefinidas. Campos completos: nombre, categoría, ubicación (despensa, refrigerador, congelador, mascotas, etc.), tipo de vencimiento (fecha fija, consumo preferente, estimada), precio, cantidad, marca y notas.
- **Resolución de Productos** — Marca cada producto como **Consumido**, **Desechado** o **Donado** con un solo gesto (swipe o diálogo). Deshacer disponible.
- **Estadísticas Mensuales** — Visualiza tasa de consumo, valor aprovechado vs. perdido, categorías con más desperdicio, racha de ahorro y salud del inventario con gráfico donut.
- **Lista de Compras** — Genera y gestiona una lista de compras agrupada por categoría. Compártela en texto plano con quien quieras.
- **Notificaciones Locales** — Alertas automáticas diarias configuradas a la hora que elijas. Te dice exactamente qué productos están por vencer y cuánto dinero está en riesgo.
- **Widget en Pantalla de Inicio** — Muestra los 3 productos más urgentes sin abrir la app.
- **Exportación de Datos** — Exporta todo tu inventario a **JSON** o **CSV** y compártelo con cualquier aplicación.
- **Tema Claro / Oscuro** — Selecciona entre tema Sistema, Claro u Oscuro desde Ajustes.
- **Idiomas y formatos regionales** — Se adapta al idioma, formato de fecha, moneda y unidades del dispositivo. Compatible con español, inglés y portugués de Brasil.
- **100% Privado** — Tus productos y registros se guardan localmente en tu dispositivo. Sin cuentas, sin servidores.

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
│   │   └── UseFirstScreen.kt    # "Usa Primero" con resolución
│   ├── theme/
│   │   ├── Color.kt             # Paleta personalizada
│   │   ├── Theme.kt             # Tema claro/oscuro Material3
│   │   └── Type.kt              # Tipografía bold
│   └── viewmodel/
│       └── PantryViewModel.kt   # ViewModel compartido
├── utils/
│   └── BackupHelper.kt          # Exportación a JSON/CSV
└── widget/
    └── PantryWidgetProvider.kt  # Widget de pantalla de inicio
```

## Tecnologías

- **Lenguaje:** Kotlin
- **UI:** Jetpack Compose (Material Design 3)
- **Base de datos:** Room (SQLite local con migraciones)
- **Arquitectura:** MVVM con StateFlow y Coroutines
- **Localización:** Android resource qualifiers (values, values-es, values-pt-rBR)
- **Componentes:** Notificaciones (AlarmManager), Widget (AppWidgetProvider), Exportación (FileProvider)

## Licencia

Este proyecto está bajo la licencia MIT. Consulta el archivo `LICENSE` para más detalles.