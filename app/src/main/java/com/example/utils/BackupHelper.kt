package com.example.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import com.example.data.local.ExpiryType
import com.example.data.local.Product
import com.example.data.local.ProductCategory
import com.example.data.local.ProductLocation
import com.example.data.local.ProductStatus
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object BackupHelper {
    data class ImportPreview(
        val products: List<Product>,
        val skipped: Int
    )

    data class ImportResult(
        val imported: Int,
        val skipped: Int
    )
    
    fun exportToJson(context: Context, products: List<Product>) {
        try {
            val array = JSONArray()
            products.forEach { p -> array.put(p.toJson()) }

            val root = JSONObject().apply {
                put("schemaVersion", 1)
                put("exportedAt", System.currentTimeMillis())
                put("products", array)
            }
            
            val jsonString = root.toString(2)
            shareTextFile(context, jsonString, "despensa_backup_${System.currentTimeMillis()}.json", "application/json")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun parseProductsJson(content: String): ImportPreview {
        val trimmed = content.trim()
        if (trimmed.isBlank()) return ImportPreview(emptyList(), 0)

        val productsArray = if (trimmed.startsWith("[")) {
            JSONArray(trimmed)
        } else {
            JSONObject(trimmed).optJSONArray("products") ?: JSONArray()
        }

        val products = mutableListOf<Product>()
        var skipped = 0
        for (i in 0 until productsArray.length()) {
            val obj = productsArray.optJSONObject(i)
            val product = obj?.toProduct()
            if (product != null) products.add(product) else skipped++
        }

        return ImportPreview(products, skipped)
    }

    fun exportToCsv(context: Context, products: List<Product>) {
        try {
            val sb = java.lang.StringBuilder()
            sb.append("Nombre,Categoria,Ubicacion,Estado,PrecioTotal,Cantidad,Unidad,FechaVencimiento\n")
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            
            products.forEach { p ->
                val expiry = if (p.expirationDate != null) dateFormat.format(Date(p.expirationDate)) else "Sin fecha"
                val name = p.name.replace(",", " ")
                sb.append("$name,${p.category},${p.location.name},${p.status.name},${p.totalPrice},${p.quantity},${p.unit},$expiry\n")
            }
            
            shareTextFile(context, sb.toString(), "despensa_reporte_${System.currentTimeMillis()}.csv", "text/csv")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun shareTextFile(context: Context, content: String, filename: String, mimeType: String) {
        val file = File(context.cacheDir, filename)
        file.writeText(content)
        
        val uri: Uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
        
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = mimeType
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        
        context.startActivity(Intent.createChooser(intent, "Guardar o compartir archivo"))
    }

    private fun Product.toJson(): JSONObject = JSONObject().apply {
        put("name", name)
        put("category", category.name)
        put("totalPrice", totalPrice)
        put("quantity", quantity)
        put("unit", unit)
        put("location", location.name)
        put("expiryType", expiryType.name)
        if (expirationDate != null) put("expirationDate", expirationDate)
        put("addedDate", addedDate)
        put("status", status.name)
        if (resolvedDate != null) put("resolvedDate", resolvedDate)
        if (barcode != null) put("barcode", barcode)
        if (notes != null) put("notes", notes)
        if (brand != null) put("brand", brand)
        if (snoozeUntil != null) put("snoozeUntil", snoozeUntil)
        if (minimumStock != null) put("minimumStock", minimumStock)
    }

    private fun JSONObject.toProduct(): Product? {
        val name = optString("name").trim().ifBlank { return null }
        return Product(
            name = name,
            category = enumValueOrDefault(optString("category"), ProductCategory.OTHER),
            totalPrice = optDouble("totalPrice", 0.0),
            quantity = optDouble("quantity", 1.0),
            unit = optString("unit", "uds").ifBlank { "uds" },
            location = enumValueOrDefault(optString("location"), ProductLocation.PANTRY),
            expiryType = enumValueOrDefault(optString("expiryType"), ExpiryType.FIXED),
            expirationDate = optLongOrNull("expirationDate"),
            addedDate = optLong("addedDate", System.currentTimeMillis()),
            status = enumValueOrDefault(optString("status"), ProductStatus.ACTIVE),
            resolvedDate = optLongOrNull("resolvedDate"),
            barcode = optStringOrNull("barcode"),
            notes = optStringOrNull("notes"),
            brand = optStringOrNull("brand"),
            snoozeUntil = optLongOrNull("snoozeUntil"),
            minimumStock = optDoubleOrNull("minimumStock")
        )
    }

    private inline fun <reified T : Enum<T>> enumValueOrDefault(value: String?, default: T): T =
        try { enumValueOf<T>(value.orEmpty()) } catch (_: Exception) { default }

    private fun JSONObject.optStringOrNull(name: String): String? =
        if (has(name) && !isNull(name)) optString(name).ifBlank { null } else null

    private fun JSONObject.optLongOrNull(name: String): Long? =
        if (has(name) && !isNull(name)) optLong(name) else null

    private fun JSONObject.optDoubleOrNull(name: String): Double? =
        if (has(name) && !isNull(name)) optDouble(name) else null
}
