package com.example.data.ai

import com.example.data.local.ProductCategory
import com.example.data.recipe.RecipeCatalog

object PantryChatbot {

    fun ask(products: List<String>, question: String): String {
        val q = question.lowercase().trim()

        val intent = detectIntent(q)

        return when (intent) {
            Intent.RECIPE -> handleRecipe(products, q)
            Intent.STORAGE -> handleStorage(q)
            Intent.EXPIRY -> handleExpiry(q)
            Intent.SUBSTITUTE -> handleSubstitution(q)
            Intent.ORGANIZE -> handleOrganization()
            Intent.HELLO -> handleGreeting()
            Intent.BUY -> handleShopping(products, q)
            Intent.UNKNOWN -> handleUnknown(products, q)
        }
    }

    private enum class Intent {
        RECIPE, STORAGE, EXPIRY, SUBSTITUTE, ORGANIZE, HELLO, BUY, UNKNOWN
    }

    private fun detectIntent(q: String): Intent {
        return when {
            q.contains("recet") || q.contains("cocin") || q.contains("prepar")
            || q.contains("cocinar") || q.contains("plato") || q.contains("comid")
            || q.contains("almorzar") || q.contains("cenar") || q.contains("desayun")
            || q.contains("hacer con") || q.contains("comer") || q.contains("menu")
            || q.contains("guiso") || q.contains("sopa") || q.contains("ensalada")
            || q.contains("pasta") || q.contains("arroz") || q.contains("horno")
            || q.contains("freír") || q.contains("hervir") -> Intent.RECIPE

            q.contains("conserv") || q.contains("guard") || q.contains("almacen")
            || q.contains("refriger") || q.contains("nevera") || q.contains("frigor")
            || q.contains("freezer") || q.contains("congel") || q.contains("despensa")
            || q.contains("temperatura") || q.contains("cadena de frío")
            || q.contains("cuanto dura") || q.contains("cuánto dura")
            || q.contains("cuanto tiempo") -> Intent.STORAGE

            q.contains("caduc") || q.contains("venc") || q.contains("expiro")
            || q.contains("caducó") || q.contains("caducado") || q.contains("malo")
            || q.contains("dañado") || q.contains("podrido") || q.contains("fecha")
            || q.contains("expiró") || q.contains("expirado") || q.contains("vence")
            || q.contains("consumir antes") || q.contains("consumir luego")
            || q.contains("saber si") || q.contains("esta malo") || q.contains("está malo")
            || q.contains("olor") || q.contains("aspecto") -> Intent.EXPIRY

            q.contains("sustitu") || q.contains("reemplaz") || q.contains("cambi")
            || q.contains("alternativa") || q.contains("en vez de") || q.contains("en lugar de")
            || q.contains("suplente") || q.contains("opción") || q.contains("puedo usar")
            || q.contains("que uso") || q.contains("qué uso") -> Intent.SUBSTITUTE

            q.contains("organiz") || q.contains("orden") || q.contains("clasific")
            || q.contains("etiquet") || q.contains("rotul") || q.contains("como ordenar")
            || q.contains("cómo ordenar") || q.contains("distribuir")
            || q.contains("primeros en entrar") || q.contains("fifo") -> Intent.ORGANIZE

            q.contains("hola") || q.contains("buen") || q.contains("hey") || q.contains("que tal")
            || q.contains("qué tal") || q.contains("como estas") || q.contains("cómo estás")
            || q.contains("saludos") || q.contains("ayuda") || q.contains("puedes")
            || q.contains("gracias") || q == "" || q.contains("buenos días")
            || q.contains("buenas tardes") || q.contains("buenas noches") -> Intent.HELLO

            q.contains("compr") || q.contains("falta") || q.contains("necesit")
            || q.contains("lista") || q.contains("mercado") || q.contains("super")
            || q.contains("abastecer") || q.contains("reponer")
            || q.contains("que comprar") || q.contains("qué comprar") -> Intent.BUY

            else -> Intent.UNKNOWN
        }
    }

    private fun handleRecipe(products: List<String>, q: String): String {
        if (products.isEmpty()) {
            return "No tienes productos en tu despensa aún. Agrega algunos y vuelve a preguntar, ¡te sugeriré recetas! 😊"
        }

        val ingredientSet = products.map { it.lowercase().trim() }.filter { it.length > 1 }.toSet()
        val matches = RecipeCatalog.findRecipesByIngredients(ingredientSet, minMatch = 1)

        if (matches.isEmpty()) {
            return "Con los ingredientes que tienes (${
                products.take(5).joinToString(", ")
            }${if (products.size > 5) "..." else ""}) no encontré recetas en mi catálogo. Prueba buscar recetas online desde el botón de recetas en la pantalla principal."
        }

        val best = matches.first()
        val missing = best.missingIngredients.take(3)

        val response = buildString {
            appendLine("¡Puedes hacer **${best.recipe.name}**!")
            appendLine()
            appendLine("Ingredientes que tienes: ${best.matchedIngredients.joinToString(", ")}")
            if (missing.isNotEmpty()) {
                appendLine("Te faltan: ${missing.joinToString(", ")}${if (best.missingIngredients.size > 3) "..." else ""}")
            }
            appendLine()
            appendLine("Preparación:")
            best.recipe.steps.forEachIndexed { i, step ->
                appendLine("${i + 1}. $step")
            }
            appendLine()
            appendLine("⏱ ${best.recipe.prepTimeMinutes} min | Dificultad: ${best.recipe.difficulty}")
        }
        return response
    }

    private fun handleStorage(q: String): String {
        val tips = mapOf(
            "fruta" to "Las frutas como manzanas, peras y plátanos maduran a temperatura ambiente. Una vez maduras, refrigéralas. Las fresas, arándanos y frambuesas siempre en nevera y consúmelas pronto.",
            "verdura" to "Guarda las verduras de hoja verde en bolsas perforadas en el cajón de verduras de la nevera. Patatas, cebollas y ajos en lugar fresco, oscuro y seco, nunca en nevera.",
            "lacteo" to "Los lácteos se conservan mejor en la parte central de la nevera (no en la puerta, donde la temperatura varía). Revisa la fecha de consumo preferente.",
            "carne" to "La carne fresca se conserva 2-3 días en nevera. Para más tiempo, congélala. El pescado fresco debe consumirse en 24 horas o congelarlo.",
            "huevo" to "Los huevos duran 3-4 semanas en nevera. No los guardes en la puerta, mejor en un estante interior donde la temperatura es más estable.",
            "congel" to "Los alimentos congelados se mantienen seguros indefinidamente, pero la calidad baja tras 3-6 meses. Etiqueta con fecha y contenido.",
            "bebida" to "Las bebidas como refrescos y cervezas van en lugar fresco. Una vez abiertas, refrigera y consume en 2-3 días. El vino tinto no necesita nevera.",
            "despensa" to "Los granos, pastas y arroces se conservan en envases herméticos en despensa fresca y seca. Las especias pierden sabor tras 6-12 meses.",
            "default" to "Como regla general: productos frescos en nevera, congelados en freezer, y no perecederos en despensa fresca y oscura. Siempre revisa las fechas de vencimiento."
        )

        val matched = tips.entries.firstOrNull { (key, _) -> q.contains(key) }
        return matched?.value ?: tips["default"]!!
    }

    private fun handleExpiry(q: String): String {
        return """
            |**¿Cómo saber si un alimento está malo?**
            |
            |**Lácteos:** leche cortada, yogur con moho, queso con olor amoniacal.
            |**Carnes:** color grisáceo/verdoso, olor agrio o pútrido, textura babosa.
            |**Pescado:** olor fuerte a amoniaco, ojos opacos, carne que se deshace.
            |**Huevos:** prueba del agua — si flota, está malo.
            |**Verduras:** hojas marchitas, manchas negras, textura blanda o babosa.
            |**Frutas:** moho, zonas blandas, olor fermentado.
            |**Enlatados:** si la lata está abombada, oxidada o al abrir sale mal olor, deséchalo.
            |
            |⚠️ Regla de oro: **"Ante la duda, tíralo."** No vale la pena arriesgar tu salud.
        """.trimMargin()
    }

    private fun handleSubstitution(q: String): String {
        val substitutions = mapOf(
            "mantequilla" to "Puedes reemplazar la mantequilla con aceite de coco (1:1), margarina, o puré de manzana para repostería.",
            "huevo" to "Sustitutos: 1 cdta de vinagre blanco + 1 cdta de bicarbonato, o 1/4 taza de puré de manzana, o 1/2 plátano maduro triturado.",
            "leche" to "Alternativas: leche de almendras, soja, avena, o arroz (misma cantidad). Para repostería, también yogur natural diluido.",
            "harina" to "Puedes usar harina de almendras, avena molida, o maicena. La textura puede variar, ajusta la hidratación.",
            "azúcar" to "Endulzantes naturales: miel (3/4 de la cantidad), stevia, azúcar de coco, o dátiles triturados.",
            "sal" to "Alternativas: salsa de soja, algas marinas deshidratadas, o ajo en polvo para dar sabor sin sal.",
            "aceite" to "Puedes sustituir con aceite de coco, aguacate triturado, o mantequilla derretida (1:1).",
            "queso" to "Para platos salados: levadura nutricional (da sabor a queso), tofu fermentado, o queso vegano.",
            "tomate" to "Puedes usar pimientos rojos asados o calabaza para textura similar en salsas.",
            "yogur" to "Sustituto: leche de coco espesa, tofu sedoso licuado, o crema agria vegana.",
            "default" to "Dime qué ingrediente quieres sustituir y te daré opciones. Puedo ayudarte con: mantequilla, huevos, leche, harina, azúcar, sal, aceite, queso, yogur y más."
        )

        val matched = substitutions.entries.firstOrNull { (key, _) -> q.contains(key) }
        return matched?.value ?: substitutions["default"]!!
    }

    private fun handleOrganization(): String {
        return """
            |**Consejos para organizar tu despensa:**
            |
            |1. **FIFO (First In, First Out):** coloca los productos nuevos detrás de los viejos para consumir primero lo que caduca antes.
            |2. **Agrupa por categoría:** lácteos juntos, carnes juntas, etc. Así encuentras todo rápido.
            |3. **Etiqueta y fecha:** usa etiquetas con nombre y fecha de apertura/caducidad.
            |4. **Contenedores transparentes:** facilitan ver lo que tienes y evitan compras duplicadas.
            |5. **Zonas:** nevera (estante superior para lácteos, medio para sobras, inferior para carnes), freezer (carnes, verduras, preparados), despensa (secos, granos).
            |6. **Revisa semanalmente:** antes de comprar, revisa qué tienes para evitar acumular.
            |7. **La app te ayuda:** usa las etiquetas de ubicación y el filtro para mantener el control.
        """.trimMargin()
    }

    private fun handleGreeting(): String {
        return """
            |¡Hola! Soy tu asistente de despensa inteligente 🤖
            |
            |Puedo ayudarte con:
            |🍳 **Recetas** con lo que tienes
            |❄️ **Consejos de conservación**
            |📅 **Información sobre caducidad**
            |🔄 **Sustituciones de ingredientes**
            |📦 **Organización de despensa**
            |🛒 **Ideas de compra**
            |
            |¿Qué necesitas saber hoy?
        """.trimMargin()
    }

    private fun handleShopping(products: List<String>, q: String): String {
        if (products.isEmpty()) {
            return "Tu despensa está vacía. ¡Empieza agregando productos básicos como arroz, pasta, huevos y leche!"
        }

        val basics = listOf("arroz", "pasta", "fideos", "aceite", "sal", "azúcar", "café", "té",
            "leche", "huevos", "pan", "cebolla", "ajo", "tomate", "limón")

        val missing = basics.filter { b ->
            products.none { p -> p.lowercase().contains(b) }
        }

        return buildString {
            appendLine("Tienes ${products.size} productos en tu despensa.")
            if (missing.isNotEmpty()) {
                appendLine()
                appendLine("Posibles básicos que te faltan: ${missing.take(6).joinToString(", ")}${if (missing.size > 6) "..." else ""}")
                appendLine()
                appendLine("💡 Revisa tu lista de compras usando el botón del carrito 🛒")
            } else {
                appendLine("¡Tienes todos los básicos cubiertos! Revisa recetas para aprovechar lo que tienes.")
            }
            appendLine()
            appendLine("¿Quieres que te sugiera recetas con lo que tienes?")
        }
    }

    private fun handleUnknown(products: List<String>, q: String): String {
        if (products.isEmpty()) {
            return "No entendí tu pregunta, pero puedes pedirme: recetas, consejos de conservación, información de caducidad, sustituciones, organización, o ideas de compra. ¡Pregunta lo que quieras! 😊"
        }

        return buildString {
            appendLine("No estoy seguro de entender tu pregunta, pero aquí va un dato útil:")
            if (products.isNotEmpty()) {
                val tip = when {
                    products.any { it.lowercase().contains("huevo") } ->
                        "¿Sabías que los huevos duran 3-4 semanas en nevera? Ponlos en un estante interior, no en la puerta."
                    products.any { it.lowercase().contains("leche") } ->
                        "La leche se conserva mejor en la parte trasera de la nevera, donde la temperatura es más fría y estable."
                    products.any { it.lowercase().contains("pan") } ->
                        "El pan se mantiene fresco 2-3 días a temperatura ambiente. Para más tiempo, congélalo en rebanadas."
                    products.any { it.lowercase().contains("tomate") } ->
                        "Los tomates nunca van en nevera — pierden sabor y textura. Guárdalos a temperatura ambiente."
                    else -> "Revisa siempre las fechas de caducidad. Esta app te ayuda a gestionarlo. ¿Quieres preguntar algo más específico?"
                }
                appendLine(tip)
            }
            appendLine()
            appendLine("Puedes preguntarme sobre recetas, conservación, caducidad, sustituciones, organización o compras.")
        }
    }
}
