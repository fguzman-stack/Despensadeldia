<div align="center">
  <h1>Despensa al Día</h1>
  <p><strong>Controla tu despensa, evita el desperdicio y ahorra dinero.</strong></p>
  <br>
</div>

Despensa al Día es una aplicación móvil 100% offline que te ayuda a gestionar el inventario de tu despensa y nevera. Registra tus alimentos con su fecha de vencimiento, recibe notificaciones antes de que venzan y visualiza cuánto dinero ahorras al no desperdiciar comida.

## ✨ Funcionalidades

- **Inventario inteligente** — Registra alimentos con nombre, categoría, cantidad, precio y fecha de vencimiento.
- **Estados de frescura** — Cada producto muestra su estado: Fresco, Vence pronto, Vence hoy o Ya venció.
- **Notificaciones locales** — Recibe alertas automáticas para consumir los alimentos próximos a vencer.
- **Estadísticas de ahorro** — Lleva la cuenta de cuánto dinero has ahorrado vs. desperdiciado.
- **Widget en pantalla de inicio** — Consulta los productos por vencer sin abrir la app.
- **Filtros y búsqueda** — Busca por nombre o filtra por categorías (Frutas, Lácteos, Carnes, Bebidas, Despensa, etc.).
- **100% offline** — Todos tus datos se almacenan localmente en el dispositivo. Sin conexión a internet necesaria.

## 📱 Capturas de pantalla

*(Agrega aquí las capturas de tu aplicación)*

## 🚀 Cómo ejecutar

### Requisitos

- [Android Studio](https://developer.android.com/studio) (última versión estable)

### Pasos

1. Abre Android Studio.
2. Selecciona **Open** y elige la carpeta del proyecto.
3. Permite que Android Studio resuelva las dependencias e incompatibilidades.
4. Crea un archivo `.env` en la raíz del proyecto con tu clave de Gemini API (consulta `.env.example`).
5. En `app/build.gradle.kts`, elimina o comenta la línea `signingConfig = signingConfigs.getByName("debugConfig")`.
6. Ejecuta la aplicación en un emulador o dispositivo físico.

## 🧱 Estructura del proyecto

```
Despensa-al-Dia/
├── app/                    # Código fuente de la aplicación
│   └── src/main/
│       ├── java/           # Lógica de la app (Kotlin)
│       └── res/            # Recursos (layouts, strings, imágenes)
├── assets/                 # Assets adicionales
├── gradle/                 # Configuración de Gradle
├── build.gradle.kts        # Configuración del proyecto
├── settings.gradle.kts     # Configuración de módulos
└── README.md
```

## 🛠️ Tecnologías

- **Lenguaje:** Kotlin
- **UI:** Jetpack Compose
- **Base de datos:** Room (SQLite local)
- **Arquitectura:** MVVM
- **API:** Gemini (funcionalidad opcional con IA)

## 📄 Licencia

Este proyecto está bajo la licencia MIT. Consulta el archivo `LICENSE` para más detalles.
