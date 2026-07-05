package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "app_settings")
data class AppSettings(
    @PrimaryKey val id: Int = 1, // Singleton row
    val onboardingCompleted: Boolean = false,
    val countryName: String = "",
    val currencyCode: String = "",
    val currencySymbol: String = "",
    val theme: String = "SYSTEM", // "SYSTEM", "LIGHT", "DARK"
    val notificationHour: Int = 9,
    val notificationMinute: Int = 0,
    val notificationEnabled: Boolean = true,
    val streakDays: Int = 0,
    val lastCheckTimestamp: Long = 0L // To compute streak daily updates
)
