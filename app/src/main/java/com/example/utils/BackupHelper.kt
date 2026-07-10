package com.example.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import com.example.data.local.Product
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object BackupHelper {
    
    fun exportToJson(context: Context, products: List<Product>) {
        try {
            val array = JSONArray()
            products.forEach { p ->
                val obj = JSONObject()
                obj.put("name", p.name)
                obj.put("category", p.category)
                obj.put("totalPrice", p.totalPrice)
                obj.put("quantity", p.quantity)
                obj.put("unit", p.unit)
                obj.put("location", p.location.name)
                obj.put("expiryType", p.expiryType.name)
                if (p.expirationDate != null) obj.put("expirationDate", p.expirationDate)
                obj.put("addedDate", p.addedDate)
                obj.put("status", p.status.name)
                if (p.resolvedDate != null) obj.put("resolvedDate", p.resolvedDate)
                if (p.barcode != null) obj.put("barcode", p.barcode)
                if (p.notes != null) obj.put("notes", p.notes)
                if (p.brand != null) obj.put("brand", p.brand)
                if (p.snoozeUntil != null) obj.put("snoozeUntil", p.snoozeUntil)
                array.put(obj)
            }
            
            val jsonString = array.toString(2)
            shareTextFile(context, jsonString, "despensa_backup_${System.currentTimeMillis()}.json", "application/json")
        } catch (e: Exception) {
            e.printStackTrace()
        }
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
}
