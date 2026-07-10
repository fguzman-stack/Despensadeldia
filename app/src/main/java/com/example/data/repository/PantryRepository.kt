package com.example.data.repository

import com.example.data.local.AppSettings
import com.example.data.local.PantryDao
import com.example.data.local.Product
import com.example.data.local.ProductLocation
import com.example.data.local.ProductStatus
import com.example.data.local.ShoppingItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PantryRepository(private val pantryDao: PantryDao) {

    // ─── Settings ──────────────────────────────────────────────────

    val settings: Flow<AppSettings> = pantryDao.getSettings()
        .map { it ?: AppSettings() }

    suspend fun getSettingsDirect(): AppSettings? = pantryDao.getSettingsDirect()

    suspend fun saveSettings(settings: AppSettings) {
        pantryDao.insertSettings(settings)
    }

    // ─── Active Products ───────────────────────────────────────────

    val activeProducts: Flow<List<Product>> = pantryDao.getActiveProducts()

    suspend fun getActiveProductsDirect(): List<Product> = pantryDao.getActiveProductsDirect()

    // ─── Resolved Products ─────────────────────────────────────────

    val consumedProducts: Flow<List<Product>> = pantryDao.getProductsByStatus(ProductStatus.CONSUMED)

    val wastedProducts: Flow<List<Product>> = pantryDao.getProductsByStatus(ProductStatus.WASTED)

    val donatedProducts: Flow<List<Product>> = pantryDao.getProductsByStatus(ProductStatus.DONATED)

    // ─── Location-based ────────────────────────────────────────────

    fun getActiveProductsByLocation(location: ProductLocation): Flow<List<Product>> =
        pantryDao.getActiveProductsByLocation(location)

    // ─── Expiry Queries ────────────────────────────────────────────

    suspend fun getProductsExpiringBefore(timestamp: Long): List<Product> =
        pantryDao.getProductsExpiringBefore(timestamp)

    suspend fun getExpiredProducts(): List<Product> =
        pantryDao.getExpiredProducts()

    // ─── Stats Queries ─────────────────────────────────────────────

    suspend fun getResolvedProductsInRange(startOfMonth: Long, endOfMonth: Long): List<Product> =
        pantryDao.getResolvedProductsInRange(startOfMonth, endOfMonth)

    // ─── Barcode ───────────────────────────────────────────────────

    suspend fun getProductByBarcode(barcode: String): Product? =
        pantryDao.getProductByBarcode(barcode)

    // ─── CRUD ──────────────────────────────────────────────────────

    suspend fun insertProduct(product: Product) {
        pantryDao.insertProduct(product)
    }

    suspend fun updateProduct(product: Product) {
        pantryDao.updateProduct(product)
    }

    suspend fun deleteProductById(id: Int) {
        pantryDao.deleteProductById(id)
    }

    suspend fun getProductById(id: Int): Product? {
        return pantryDao.getProductById(id)
    }

    suspend fun getAllProductsDirect(): List<Product> =
        pantryDao.getAllProductsDirect()

    suspend fun deleteAllProducts() {
        pantryDao.deleteAllProducts()
    }

    // ─── Shopping List ─────────────────────────────────────────────

    val allShoppingItems: Flow<List<ShoppingItem>> = pantryDao.getAllShoppingItems()

    suspend fun insertShoppingItem(item: ShoppingItem) {
        pantryDao.insertShoppingItem(item)
    }

    suspend fun updateShoppingItem(item: ShoppingItem) {
        pantryDao.updateShoppingItem(item)
    }

    suspend fun deleteShoppingItem(item: ShoppingItem) {
        pantryDao.deleteShoppingItem(item)
    }

    suspend fun deleteCheckedShoppingItems() {
        pantryDao.deleteCheckedShoppingItems()
    }
}
