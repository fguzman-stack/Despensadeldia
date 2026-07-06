package com.example.data.repository

import com.example.data.local.AppSettings
import com.example.data.local.PantryDao
import com.example.data.local.Product
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PantryRepository(private val pantryDao: PantryDao) {

    val settings: Flow<AppSettings> = pantryDao.getSettings()
        .map { it ?: AppSettings() }

    val activeProducts: Flow<List<Product>> = pantryDao.getActiveProducts()

    val consumedProducts: Flow<List<Product>> = pantryDao.getProductsByStatus("CONSUMED")

    val wastedProducts: Flow<List<Product>> = pantryDao.getProductsByStatus("WASTED")

    suspend fun getSettingsDirect(): AppSettings? = pantryDao.getSettingsDirect()
    suspend fun getActiveProductsDirect(): List<Product> = pantryDao.getActiveProductsDirect()

    suspend fun saveSettings(settings: AppSettings) {
        pantryDao.insertSettings(settings)
    }

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
}
