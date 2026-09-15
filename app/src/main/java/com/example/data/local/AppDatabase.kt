package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [Product::class, AppSettings::class, ShoppingItem::class],
    version = 6,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun pantryDao(): PantryDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        /**
         * Migration v1 → v2:
         * - Rename 'price' → 'totalPrice'
         * - Add: location, expiryType, barcode, notes, brand, snoozeUntil
         * - Make expirationDate nullable
         */
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE products RENAME TO products_old")

                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS products (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        name TEXT NOT NULL,
                        category TEXT NOT NULL,
                        totalPrice REAL NOT NULL DEFAULT 0.0,
                        quantity REAL NOT NULL DEFAULT 1.0,
                        unit TEXT NOT NULL DEFAULT 'uds',
                        location TEXT NOT NULL DEFAULT 'PANTRY',
                        expiryType TEXT NOT NULL DEFAULT 'FIXED',
                        expirationDate INTEGER,
                        addedDate INTEGER NOT NULL DEFAULT 0,
                        status TEXT NOT NULL DEFAULT 'ACTIVE',
                        resolvedDate INTEGER,
                        barcode TEXT,
                        notes TEXT,
                        brand TEXT,
                        snoozeUntil INTEGER
                    )
                """)

                db.execSQL("""
                    INSERT INTO products (
                        id, name, category, totalPrice, quantity, unit,
                        location, expiryType, expirationDate, addedDate,
                        status, resolvedDate
                    )
                    SELECT
                        id, name, category, price, quantity, unit,
                        'PANTRY', 'FIXED', expirationDate, addedDate,
                        status, resolvedDate
                    FROM products_old
                """)

                db.execSQL("DROP TABLE products_old")
            }
        }

        /**
         * Migration v2 → v3:
         * - Create shopping_items table
         */
        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS shopping_items (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        name TEXT NOT NULL,
                        quantity REAL NOT NULL,
                        unit TEXT NOT NULL,
                        category TEXT NOT NULL,
                        location TEXT,
                        preferredBrand TEXT,
                        estimatedPrice REAL,
                        isChecked INTEGER NOT NULL,
                        addedDate INTEGER NOT NULL,
                        sourceProductId INTEGER
                    )
                """)
            }
        }

        /**
         * Migration v3 → v4:
         * - Convert localized category strings to ProductCategory enum names
         * - Convert shopping_items.category from String to ProductCategory
         */
        val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // Update products: map old localized strings to enum names
                db.execSQL("""
                    UPDATE products SET category = 'PRODUCE'
                    WHERE category IN ('Frutas y Verduras', 'Frutas', 'Verduras', 'Frutas e Vegetais', 'Produce')
                """)
                db.execSQL("""
                    UPDATE products SET category = 'DAIRY_EGGS'
                    WHERE category IN ('Lácteos y Huevos', 'Lácteos', 'Huevos', 'Laticínios e Ovos', 'Dairy and Eggs')
                """)
                db.execSQL("""
                    UPDATE products SET category = 'MEAT_SEAFOOD'
                    WHERE category IN ('Carnes y Pescados', 'Carnes', 'Pescados', 'Carnes e Peixes', 'Meat and Seafood')
                """)
                db.execSQL("""
                    UPDATE products SET category = 'BEVERAGES'
                    WHERE category IN ('Bebidas', 'Bebidas', 'Beverages')
                """)
                db.execSQL("""
                    UPDATE products SET category = 'PANTRY'
                    WHERE category IN ('Despensa / Granos', 'Despensa', 'Granos', 'Despensa e Grãos', 'Pantry')
                """)
                db.execSQL("""
                    UPDATE products SET category = 'BAKERY'
                    WHERE category IN ('Panadería', 'Panaderia', 'Padaria', 'Bakery')
                """)
                db.execSQL("""
                    UPDATE products SET category = 'FROZEN'
                    WHERE category IN ('Congelados', 'Congelados', 'Frozen Food')
                """)
                db.execSQL("""
                    UPDATE products SET category = 'MEDICINE'
                    WHERE category IN ('Medicamentos', 'Medicamentos', 'Medicina', 'Medicine')
                """)
                db.execSQL("""
                    UPDATE products SET category = 'CLEANING'
                    WHERE category IN ('Limpieza', 'Limpeza', 'Cleaning')
                """)
                db.execSQL("""
                    UPDATE products SET category = 'PET_SUPPLIES'
                    WHERE category IN ('Mascotas', 'Mascotas', 'Pet Supplies', 'Pets')
                """)
                // Any remaining unmapped values go to OTHER
                db.execSQL("""
                    UPDATE products SET category = 'OTHER'
                    WHERE category NOT IN ('PRODUCE', 'DAIRY_EGGS', 'MEAT_SEAFOOD', 'BEVERAGES',
                        'PANTRY', 'BAKERY', 'FROZEN', 'MEDICINE', 'CLEANING', 'PET_SUPPLIES', 'OTHER')
                """)

                // Same for shopping_items
                db.execSQL("""
                    UPDATE shopping_items SET category = 'PRODUCE'
                    WHERE category IN ('Frutas y Verduras', 'Frutas', 'Verduras', 'Frutas e Vegetais', 'Produce')
                """)
                db.execSQL("""
                    UPDATE shopping_items SET category = 'DAIRY_EGGS'
                    WHERE category IN ('Lácteos y Huevos', 'Lácteos', 'Huevos', 'Laticínios e Ovos', 'Dairy and Eggs')
                """)
                db.execSQL("""
                    UPDATE shopping_items SET category = 'MEAT_SEAFOOD'
                    WHERE category IN ('Carnes y Pescados', 'Carnes', 'Pescados', 'Carnes e Peixes', 'Meat and Seafood')
                """)
                db.execSQL("""
                    UPDATE shopping_items SET category = 'BEVERAGES'
                    WHERE category IN ('Bebidas', 'Bebidas', 'Beverages')
                """)
                db.execSQL("""
                    UPDATE shopping_items SET category = 'PANTRY'
                    WHERE category IN ('Despensa / Granos', 'Despensa', 'Granos', 'Despensa e Grãos', 'Pantry')
                """)
                db.execSQL("""
                    UPDATE shopping_items SET category = 'BAKERY'
                    WHERE category IN ('Panadería', 'Panadería', 'Padaria', 'Bakery')
                """)
                db.execSQL("""
                    UPDATE shopping_items SET category = 'FROZEN'
                    WHERE category IN ('Congelados', 'Congelados', 'Frozen Food')
                """)
                db.execSQL("""
                    UPDATE shopping_items SET category = 'MEDICINE'
                    WHERE category IN ('Medicamentos', 'Medicamentos', 'Medicina', 'Medicine')
                """)
                db.execSQL("""
                    UPDATE shopping_items SET category = 'CLEANING'
                    WHERE category IN ('Limpieza', 'Limpeza', 'Cleaning')
                """)
                db.execSQL("""
                    UPDATE shopping_items SET category = 'PET_SUPPLIES'
                    WHERE category IN ('Mascotas', 'Mascotas', 'Pet Supplies', 'Pets')
                """)
                db.execSQL("""
                    UPDATE shopping_items SET category = 'OTHER'
                    WHERE category NOT IN ('PRODUCE', 'DAIRY_EGGS', 'MEAT_SEAFOOD', 'BEVERAGES',
                        'PANTRY', 'BAKERY', 'FROZEN', 'MEDICINE', 'CLEANING', 'PET_SUPPLIES', 'OTHER')
                """)
            }
        }

        /**
         * Migration v4 → v5:
         * - Add minimumStock column to products
         */
        val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE products ADD COLUMN minimumStock REAL")
            }
        }

        /**
         * Migration v5 -> v6:
         * - Add app_settings table used by onboarding, notifications and theme settings.
         */
        val MIGRATION_5_6 = object : Migration(5, 6) {
            override fun migrate(db: SupportSQLiteDatabase) {
                if (!tableExists(db, "app_settings")) {
                    createAppSettingsTable(db, "app_settings")
                } else if (!hasColumn(db, "app_settings", "geminiApiKey")) {
                    createAppSettingsTable(db, "app_settings_new")
                    db.execSQL("""
                        INSERT INTO app_settings_new (
                            id, onboardingCompleted, countryName, currencyCode, currencySymbol,
                            theme, notificationHour, notificationMinute, notificationEnabled,
                            streakDays, lastCheckTimestamp, geminiApiKey
                        )
                        SELECT
                            id, onboardingCompleted, countryName, currencyCode, currencySymbol,
                            theme, notificationHour, notificationMinute, notificationEnabled,
                            streakDays, lastCheckTimestamp, ''
                        FROM app_settings
                    """)
                    db.execSQL("DROP TABLE app_settings")
                    db.execSQL("ALTER TABLE app_settings_new RENAME TO app_settings")
                }
            }
        }

        private fun createAppSettingsTable(db: SupportSQLiteDatabase, tableName: String) {
            db.execSQL("""
                CREATE TABLE IF NOT EXISTS $tableName (
                    id INTEGER NOT NULL PRIMARY KEY,
                    onboardingCompleted INTEGER NOT NULL,
                    countryName TEXT NOT NULL,
                    currencyCode TEXT NOT NULL,
                    currencySymbol TEXT NOT NULL,
                    theme TEXT NOT NULL,
                    notificationHour INTEGER NOT NULL,
                    notificationMinute INTEGER NOT NULL,
                    notificationEnabled INTEGER NOT NULL,
                    streakDays INTEGER NOT NULL,
                    lastCheckTimestamp INTEGER NOT NULL,
                    geminiApiKey TEXT NOT NULL
                )
            """)
        }

        private fun tableExists(db: SupportSQLiteDatabase, tableName: String): Boolean {
            db.query(
                "SELECT name FROM sqlite_master WHERE type = 'table' AND name = ?",
                arrayOf(tableName)
            ).use { cursor ->
                return cursor.moveToFirst()
            }
        }

        private fun hasColumn(db: SupportSQLiteDatabase, tableName: String, columnName: String): Boolean {
            db.query("PRAGMA table_info($tableName)").use { cursor ->
                val nameIndex = cursor.getColumnIndex("name")
                while (cursor.moveToNext()) {
                    if (nameIndex >= 0 && cursor.getString(nameIndex) == columnName) return true
                }
            }
            return false
        }

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "despensa_database"
                )
                .addMigrations(
                    MIGRATION_1_2,
                    MIGRATION_2_3,
                    MIGRATION_3_4,
                    MIGRATION_4_5,
                    MIGRATION_5_6
                )
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
