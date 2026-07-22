package com.example.data.remote

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL

object FrankfurterService {

    suspend fun convert(amount: Double, from: String, to: String): Double? = withContext(Dispatchers.IO) {
        try {
            val url = "https://api.frankfurter.app/latest?from=$from&to=$to"
            val json = JSONObject(URL(url).readText())
            val rate = json.optJSONObject("rates")?.optDouble(to)
            if (rate != null && rate > 0) amount * rate else null
        } catch (_: Exception) { null }
    }

    suspend fun getAllRates(base: String): Map<String, Double>? = withContext(Dispatchers.IO) {
        try {
            val json = JSONObject(URL("https://api.frankfurter.app/latest?from=$base").readText())
            val rates = json.optJSONObject("rates") ?: return@withContext null
            rates.keys().asSequence().associateWith { rates.optDouble(it, 0.0) }
        } catch (_: Exception) { null }
    }
}
