package com.example.data.recipe

object RecipeCatalog {

    val recipes: List<Recipe> = listOf(
        Recipe(
            id = "tortilla",
            name = "Tortilla de Patatas",
            ingredients = listOf("Huevos", "Patatas", "Cebolla"),
            steps = listOf(
                "Pela y corta las patatas en rodajas finas",
                "Fríe las patatas en aceite abundante hasta que estén tiernas",
                "Bate los huevos en un bol grande",
                "Mezcla las patatas (y cebolla si gusta) con los huevos",
                "Cuaja la tortilla en una sartén por ambos lados"
            ),
            prepTimeMinutes = 30,
            difficulty = "Fácil",
            region = "ES",
            category = com.example.data.local.ProductCategory.PRODUCE
        ),
        Recipe(
            id = "arroz_pollo",
            name = "Arroz con Pollo",
            ingredients = listOf("Arroz", "Pollo", "Zanahoria", "Ajo"),
            steps = listOf(
                "Dora el pollo en una olla con aceite",
                "Añade el ajo picado y la zanahoria en cubos",
                "Agrega el arroz y sofríe 2 minutos",
                "Cubre con caldo y cocina 20 minutos a fuego medio",
                "Deja reposar 5 minutos antes de servir"
            ),
            prepTimeMinutes = 40,
            difficulty = "Fácil",
            region = "ES",
            category = com.example.data.local.ProductCategory.MEAT_SEAFOOD
        ),
        Recipe(
            id = "ensalada",
            name = "Ensalada Mixta",
            ingredients = listOf("Lechuga", "Tomate", "Cebolla", "Atún"),
            steps = listOf(
                "Lava y corta la lechuga",
                "Corta el tomate en cubos",
                "Corta la cebolla en rodajas finas",
                "Mezcla todo con el atún",
                "Aliña con aceite, vinagre y sal"
            ),
            prepTimeMinutes = 10,
            difficulty = "Fácil",
            region = "ES",
            category = com.example.data.local.ProductCategory.PRODUCE
        ),
        Recipe(
            id = "pasta",
            name = "Pasta Alfredo",
            ingredients = listOf("Fideos", "Leche", "Queso", "Ajo"),
            steps = listOf(
                "Cocina la pasta según las instrucciones",
                "En una sartén, derrite mantequilla con ajo picado",
                "Añade leche y queso rallado, remueve hasta espesar",
                "Mezcla la salsa con la pasta escurrida",
                "Sirve caliente con queso extra"
            ),
            prepTimeMinutes = 20,
            difficulty = "Fácil",
            region = "IT",
            category = com.example.data.local.ProductCategory.PANTRY
        ),
        Recipe(
            id = "pollo_horno",
            name = "Pollo al Horno con Verduras",
            ingredients = listOf("Pollo", "Patatas", "Zanahoria", "Ajo"),
            steps = listOf(
                "Precalienta el horno a 200°C",
                "Corta las patatas y zanahorias en trozos",
                "Coloca el pollo y verduras en una bandeja",
                "Aliña con aceite, ajo, sal y especias",
                "Hornea 45 minutos hasta que esté dorado"
            ),
            prepTimeMinutes = 60,
            difficulty = "Medio",
            region = "ES",
            category = com.example.data.local.ProductCategory.MEAT_SEAFOOD
        ),
        Recipe(
            id = "flan",
            name = "Flan Casero",
            ingredients = listOf("Leche", "Huevos", "Azúcar"),
            steps = listOf(
                "Prepara el caramelo: derrite azúcar en una sartén",
                "Vierte el caramelo en el molde",
                "Mezcla leche, huevos y azúcar",
                "Vierte la mezcla en el molde",
                "Cocina al baño María 45 minutos"
            ),
            prepTimeMinutes = 60,
            difficulty = "Medio",
            region = "ES",
            category = com.example.data.local.ProductCategory.DAIRY_EGGS
        ),
        Recipe(
            id = "sandwich",
            name = "Sándwich Mixto",
            ingredients = listOf("Pan", "Queso", "Jamón"),
            steps = listOf(
                "Toma dos rebanadas de pan",
                "Coloca queso y jamón entre ellas",
                "Tuesta en una sartén o sandwichera",
                "Sirve caliente"
            ),
            prepTimeMinutes = 5,
            difficulty = "Fácil",
            region = "US",
            category = com.example.data.local.ProductCategory.BAKERY
        ),
        Recipe(
            id = "batido",
            name = "Batido de Frutas",
            ingredients = listOf("Leche", "Yogur", "Frutas"),
            steps = listOf(
                "Corta las frutas en trozos",
                "Coloca todo en la licuadora",
                "Licúa hasta obtener una mezcla homogénea",
                "Sirve frío"
            ),
            prepTimeMinutes = 5,
            difficulty = "Fácil",
            region = "ES",
            category = com.example.data.local.ProductCategory.DAIRY_EGGS
        ),
        Recipe(
            id = "revuelto",
            name = "Revuelto de Verduras",
            ingredients = listOf("Huevos", "Cebolla", "Tomate"),
            steps = listOf(
                "Pica la cebolla y el tomate en cubos",
                "Sofríe la cebolla en una sartén",
                "Añade el tomate y cocina 3 minutos",
                "Bate los huevos y viértelos en la sartén",
                "Remueve hasta que cuaje"
            ),
            prepTimeMinutes = 10,
            difficulty = "Fácil",
            region = "ES",
            category = com.example.data.local.ProductCategory.PRODUCE
        ),
        Recipe(
            id = "sopa",
            name = "Sopa de Fideos",
            ingredients = listOf("Fideos", "Tomate", "Ajo", "Cebolla"),
            steps = listOf(
                "Sofríe ajo y cebolla picados",
                "Añade tomate triturado",
                "Agrega agua y lleva a ebullición",
                "Añade los fideos y cocina 10 minutos",
                "Sazona al gusto y sirve caliente"
            ),
            prepTimeMinutes = 25,
            difficulty = "Fácil",
            region = "ES",
            category = com.example.data.local.ProductCategory.PANTRY
        ),
        Recipe(
            id = "pollo_plancha",
            name = "Pollo a la Plancha con Ensalada",
            ingredients = listOf("Pollo", "Lechuga", "Tomate", "Limón"),
            steps = listOf(
                "Sazona el pollo con sal y limón",
                "Cocina en una plancha caliente 6 minutos por lado",
                "Lava y corta la lechuga y el tomate",
                "Sirve el pollo con la ensalada",
                "Aliña con aceite y limón"
            ),
            prepTimeMinutes = 20,
            difficulty = "Fácil",
            region = "ES",
            category = com.example.data.local.ProductCategory.MEAT_SEAFOOD
        )
    )

    fun findRecipesByIngredients(
        availableIngredients: Set<String>,
        urgentIngredients: Set<String> = emptySet(),
        minMatch: Int = 1
    ): List<RecipeMatch> {
        val normalizedAvailable = availableIngredients.map { it.lowercase().trim() }.toSet()
        val normalizedUrgent = urgentIngredients.map { it.lowercase().trim() }.toSet()
        return recipes.mapNotNull { recipe ->
            val normalizedRecipe = recipe.ingredients.map { it.lowercase().trim() }
            val matched = normalizedRecipe.filter { it in normalizedAvailable }
            val missing = normalizedRecipe.filter { it !in normalizedAvailable }
            val matchCount = matched.size
            val totalCount = normalizedRecipe.size
            val matchRatio = if (totalCount > 0) matchCount.toFloat() / totalCount else 0f
            val urgentMatchCount = matched.count { it in normalizedUrgent }
            if (matchCount >= minMatch) {
                RecipeMatch(
                    recipe = recipe,
                    matchedIngredients = matched,
                    missingIngredients = missing,
                    matchCount = matchCount,
                    totalCount = totalCount,
                    matchRatio = matchRatio,
                    urgentMatchCount = urgentMatchCount
                )
            } else null
        }.sortedWith(
            compareByDescending<RecipeMatch> { it.urgentMatchCount }
                .thenByDescending { it.matchRatio }
                .thenByDescending { it.matchCount }
        )
    }

    data class RecipeMatch(
        val recipe: Recipe,
        val matchedIngredients: List<String>,
        val missingIngredients: List<String>,
        val matchCount: Int,
        val totalCount: Int,
        val matchRatio: Float,
        val urgentMatchCount: Int = 0
    )
}
