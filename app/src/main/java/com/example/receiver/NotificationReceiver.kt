package com.example.receiver

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.MainActivity
import com.example.data.local.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Calendar

class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val db = AppDatabase.getDatabase(context)
                val settings = db.pantryDao().getSettingsDirect()
                
                if (settings != null && !settings.notificationEnabled) {
                    pendingResult.finish()
                    return@launch
                }

                val activeProducts = db.pantryDao().getActiveProductsDirect()
                val now = System.currentTimeMillis()
                val limitExpiringSoon = now + (3L * 24 * 60 * 60 * 1000)
                
                val expired = activeProducts.filter { it.expirationDate < now }
                val expiringSoon = activeProducts.filter { it.expirationDate in now..limitExpiringSoon }

                if (expired.isNotEmpty() || expiringSoon.isNotEmpty()) {
                    val mostUrgent = expiringSoon.firstOrNull() ?: expired.firstOrNull()
                    val currencySymbol = settings?.currencySymbol ?: "$"
                    
                    val totalValueAtRisk = (expired.sumOf { it.price * it.quantity } + 
                                           expiringSoon.sumOf { it.price * it.quantity })

                    showPersuasiveNotification(
                        context, 
                        expired.size, 
                        expiringSoon.size, 
                        mostUrgent?.name,
                        totalValueAtRisk,
                        currencySymbol
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                pendingResult.finish()
            }
        }
    }

    private fun showPersuasiveNotification(
        context: Context, 
        expiredCount: Int, 
        soonCount: Int, 
        productName: String?,
        valueAtRisk: Double,
        currencySymbol: String
    ) {
        val channelId = "pantry_expiry_channel"
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Avisos de Vencimiento de Alimentos",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }

        val openIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            openIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0)
        )

        val messages = mutableListOf<String>()
        
        if (productName != null) {
            messages.add("Tus ${productName.lowercase()} están pidiendo auxilio. 🚨")
        }
        
        if (valueAtRisk > 0) {
            messages.add("Hoy puedes salvar $currencySymbol${String.format("%.2f", valueAtRisk)} consumiendo estos productos.")
        } else {
            messages.add("No dejes que tu dinero termine en la basura. 💸")
        }

        val contentText = if (expiredCount > 0) {
            "¡Atención! Tienes $expiredCount productos vencidos. Revisa tu despensa ahora."
        } else {
            messages.random()
        }

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Despensa al Día 🍏")
            .setContentText(contentText)
            .setStyle(NotificationCompat.BigTextStyle().bigText(contentText))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(101, notification)
    }

    companion object {
        fun scheduleNotification(context: Context, hour: Int, minute: Int) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as android.app.AlarmManager
            val intent = Intent(context, NotificationReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0)
            )

            val calendar = Calendar.getInstance().apply {
                timeInMillis = System.currentTimeMillis()
                set(Calendar.HOUR_OF_DAY, hour)
                set(Calendar.MINUTE, minute)
                set(Calendar.SECOND, 0)
                if (before(Calendar.getInstance())) {
                    add(Calendar.DAY_OF_YEAR, 1)
                }
            }

            alarmManager.setInexactRepeating(
                android.app.AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                android.app.AlarmManager.INTERVAL_DAY,
                pendingIntent
            )
        }

        fun cancelNotification(context: Context) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as android.app.AlarmManager
            val intent = Intent(context, NotificationReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                0,
                intent,
                PendingIntent.FLAG_NO_CREATE or (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0)
            )
            if (pendingIntent != null) {
                alarmManager.cancel(pendingIntent)
            }
        }
    }
}
