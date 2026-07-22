package com.example.data.remote

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.net.URL

data class GeminiConfig(
    val apiKey: String = "",
    val model: String = "gemini-2.0-flash"
)

object GeminiService {

    suspend fun sendMessage(
        config: GeminiConfig,
        systemContext: String,
        userMessage: String,
    ): String? = withContext(Dispatchers.IO) {
        if (config.apiKey.isBlank()) return@withContext null
        try {
            val url = URL(
                "https://generativelanguage.googleapis.com/v1beta/models/${config.model}:generateContent?key=${config.apiKey}"
            )
            val body = JSONObject().apply {
                put("contents", JSONArray().apply {
                    put(JSONObject().apply {
                        put("role", "user")
                        put("parts", JSONArray().apply {
                            put(JSONObject().apply {
                                put("text", "$systemContext\n\n$userMessage")
                            })
                        })
                    })
                })
                put("generationConfig", JSONObject().apply {
                    put("maxOutputTokens", 512)
                    put("temperature", 0.7)
                })
            }
            val connection = url.openConnection() as java.net.HttpURLConnection
            connection.apply {
                requestMethod = "POST"
                setRequestProperty("Content-Type", "application/json")
                doOutput = true
                connectTimeout = 15000
                readTimeout = 15000
            }
            connection.outputStream.use { os ->
                os.write(body.toString().toByteArray())
            }
            val responseText = connection.inputStream.bufferedReader().readText()
            connection.disconnect()

            val json = JSONObject(responseText)
            val candidates = json.optJSONArray("candidates")
            val content = candidates?.optJSONObject(0)
                ?.optJSONObject("content")
            val parts = content?.optJSONArray("parts")
            parts?.optJSONObject(0)?.optString("text")
        } catch (_: Exception) { null }
    }
}
