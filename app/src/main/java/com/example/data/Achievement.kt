package com.example.data

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.ui.theme.Emerald
import com.example.ui.theme.Amber
import com.example.ui.theme.Coral
import com.example.ui.theme.Sky
import com.example.ui.theme.PremiumAction

enum class Achievement(
    val id: String,
    val title: String,
    val description: String,
    val icon: ImageVector,
    val color: Color,
    val tier: Int
) {
    FIRST_RESCUE(
        id = "first_rescue",
        title = "Primer Rescate",
        description = "Consumiste un producto antes de que caduque",
        icon = Icons.Filled.Shield,
        color = Emerald,
        tier = 1
    ),
    RESCUE_10(
        id = "rescue_10",
        title = "Rescatista",
        description = "10 productos consumidos antes de vencer",
        icon = Icons.Filled.Shield,
        color = Emerald,
        tier = 2
    ),
    RESCUE_50(
        id = "rescue_50",
        title = "Héroe del Rescate",
        description = "50 productos salvados del desperdicio",
        icon = Icons.Filled.Shield,
        color = Emerald,
        tier = 3
    ),
    FIRST_DONATION(
        id = "first_donation",
        title = "Corazón Generoso",
        description = "Donaste o compartiste tu primer producto",
        icon = Icons.Filled.VolunteerActivism,
        color = Sky,
        tier = 1
    ),
    DONATION_10(
        id = "donation_10",
        title = "Filántropo",
        description = "10 productos donados o compartidos",
        icon = Icons.Filled.VolunteerActivism,
        color = Sky,
        tier = 2
    ),
    STREAK_7(
        id = "streak_7",
        title = "Racha de 7 Días",
        description = "Revisaste tu despensa 7 días seguidos",
        icon = Icons.Filled.LocalFireDepartment,
        color = Amber,
        tier = 1
    ),
    STREAK_30(
        id = "streak_30",
        title = "Racha de 30 Días",
        description = "Un mes sin perder el control de tu despensa",
        icon = Icons.Filled.LocalFireDepartment,
        color = PremiumAction,
        tier = 3
    ),
    ZERO_WASTE_WEEK(
        id = "zero_waste_week",
        title = "Semana Zero Waste",
        description = "No desperdiciaste nada en una semana",
        icon = Icons.Filled.Eco,
        color = Emerald,
        tier = 2
    ),
    ZERO_WASTE_MONTH(
        id = "zero_waste_month",
        title = "Mes Zero Waste",
        description = "Un mes entero sin desperdicio",
        icon = Icons.Filled.Eco,
        color = PremiumAction,
        tier = 3
    ),
    INVENTORY_50(
        id = "inventory_50",
        title = "Despensa Abastecida",
        description = "Registraste 50 productos en tu inventario",
        icon = Icons.Filled.Kitchen,
        color = Amber,
        tier = 2
    ),
    PRODUCT_MASTER(
        id = "product_master",
        title = "Maestro de la Despensa",
        description = "100+ productos registrados",
        icon = Icons.Filled.WorkspacePremium,
        color = PremiumAction,
        tier = 3
    ),
    WASTE_AWARE(
        id = "waste_aware",
        title = "Consciente",
        description = "Marcaste tu primer producto como desperdicio",
        icon = Icons.Filled.Visibility,
        color = Coral,
        tier = 1
    );

    companion object {
        fun compute(
            streakDays: Int,
            totalConsumed: Int,
            totalWasted: Int,
            totalDonated: Int,
            rescuedCount: Int,
            totalEverAdded: Int,
            weeklyWasted: Int
        ): Set<String> {
            val earned = mutableSetOf<String>()

            if (rescuedCount >= 1) earned.add(FIRST_RESCUE.id)
            if (rescuedCount >= 10) earned.add(RESCUE_10.id)
            if (rescuedCount >= 50) earned.add(RESCUE_50.id)

            if (totalDonated >= 1) earned.add(FIRST_DONATION.id)
            if (totalDonated >= 10) earned.add(DONATION_10.id)

            if (streakDays >= 7) earned.add(STREAK_7.id)
            if (streakDays >= 30) earned.add(STREAK_30.id)

            if (totalWasted >= 1) earned.add(WASTE_AWARE.id)
            if (weeklyWasted == 0 && totalConsumed > 0) earned.add(ZERO_WASTE_WEEK.id)

            if (totalEverAdded >= 50) earned.add(INVENTORY_50.id)
            if (totalEverAdded >= 100) earned.add(PRODUCT_MASTER.id)

            return earned
        }
    }
}
