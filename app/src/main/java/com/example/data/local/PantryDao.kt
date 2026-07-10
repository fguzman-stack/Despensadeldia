package com.example.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface PantryDao {
    // App Settings Queries
    @Query("SELECT * FROM app_settings WHERE id = 1 LIMIT 1")
    fun getSettings(): Flow<AppSettings?>

    @Query("SELECT * FROM app_settings WHERE id = 1 LIMIT 1")
    suspend fun getSettingsDirect(): AppSettings?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSettings(settings: AppSettings)

    // ─── Active Products ───────────────────────────────────────────

    @Query("SELECT * FROM products WHERE status = 'ACTIVE' ORDER BY expirationDate ASC")
    fun getActiveProducts(): Flow<List<Product>>

    @Query("SELECT * FROM products WHERE status = 'ACTIVE' ORDER BY expirationDate ASC")
    suspend fun getActiveProductsDirect(): List<Product>

    // ─── Products by Status ────────────────────────────────────────

    @Query("SELECT * FROM products WHERE status = :status ORDER BY resolvedDate DESC")
    fun getProductsByStatus(status: ProductStatus): Flow<List<Product>>

    // ─── Products by Location ──────────────────────────────────────

    @Query("SELECT * FROM products WHERE status = 'ACTIVE' AND location = :location ORDER BY expirationDate ASC")
    fun getActiveProductsByLocation(location: ProductLocation): Flow<List<Product>>

    // ─── Expiry Queries ────────────────────────────────────────────

    @Query("""
        SELECT * FROM products 
        WHERE status = 'ACTIVE' 
        AND expirationDate IS NOT NULL 
        AND expirationDate <= :timestamp 
        AND (snoozeUntil IS NULL OR snoozeUntil <= :now)
        ORDER BY expirationDate ASC
    """)
    suspend fun getProductsExpiringBefore(timestamp: Long, now: Long = System.currentTimeMillis()): List<Product>

    @Query("""
        SELECT * FROM products 
        WHERE status = 'ACTIVE' 
        AND expirationDate IS NOT NULL 
        AND expirationDate < :now
        ORDER BY expirationDate ASC
    """)
    suspend fun getExpiredProducts(now: Long = System.currentTimeMillis()): List<Product>

    // ─── Resolved Products for Stats ───────────────────────────────

    @Query("""
        SELECT * FROM products 
        WHERE status != 'ACTIVE' 
        AND resolvedDate IS NOT NULL 
        AND resolvedDate >= :startOfMonth 
        AND resolvedDate < :endOfMonth
        ORDER BY resolvedDate DESC
    """)
    suspend fun getResolvedProductsInRange(startOfMonth: Long, endOfMonth: Long): List<Product>

    // ─── Barcode Lookup ────────────────────────────────────────────

    @Query("SELECT * FROM products WHERE barcode = :barcode ORDER BY addedDate DESC LIMIT 1")
    suspend fun getProductByBarcode(barcode: String): Product?

    // ─── All & By ID ───────────────────────────────────────────────

    @Query("SELECT * FROM products ORDER BY expirationDate ASC")
    fun getAllProducts(): Flow<List<Product>>

    @Query("SELECT * FROM products ORDER BY addedDate DESC")
    suspend fun getAllProductsDirect(): List<Product>

    @Query("SELECT * FROM products WHERE id = :id LIMIT 1")
    suspend fun getProductById(id: Int): Product?

    // ─── CRUD ──────────────────────────────────────────────────────

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(product: Product)

    @Update
    suspend fun updateProduct(product: Product)

    @Delete
    suspend fun deleteProduct(product: Product)

    @Query("DELETE FROM products WHERE id = :id")
    suspend fun deleteProductById(id: Int)

    @Query("DELETE FROM products")
    suspend fun deleteAllProducts()
    
    // ─── Shopping List ─────────────────────────────────────────────

    @Query("SELECT * FROM shopping_items ORDER BY isChecked ASC, category ASC, name ASC")
    fun getAllShoppingItems(): Flow<List<ShoppingItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertShoppingItem(item: ShoppingItem)

    @Query("SELECT * FROM shopping_items WHERE name = :name COLLATE NOCASE LIMIT 1")
    suspend fun getShoppingItemByNameNormalized(name: String): ShoppingItem?

    @Update
    suspend fun updateShoppingItem(item: ShoppingItem)

    @Delete
    suspend fun deleteShoppingItem(item: ShoppingItem)

    @Query("DELETE FROM shopping_items WHERE isChecked = 1")
    suspend fun deleteCheckedShoppingItems()
}
