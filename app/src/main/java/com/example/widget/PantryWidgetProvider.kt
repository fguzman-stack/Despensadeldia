package com.example.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.RemoteViews
import com.example.MainActivity
import com.example.R
import com.example.data.local.AppDatabase
import com.example.data.local.Product
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PantryWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val db = AppDatabase.getDatabase(context)
                // Obtenemos los 3 productos más urgentes
                val activeProducts = db.pantryDao().getActiveProductsDirect().filter { it.expirationDate != null }.take(3)
                
                for (appWidgetId in appWidgetIds) {
                    val views = RemoteViews(context.packageName, R.layout.pantry_widget)
                    
                    // Acción para abrir la app al tocar el widget
                    val intent = Intent(context, MainActivity::class.java)
                    val pendingIntent = PendingIntent.getActivity(
                        context, 0, intent, 
                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                    )
                    views.setOnClickPendingIntent(R.id.widget_title, pendingIntent)

                    if (activeProducts.isEmpty()) {
                        views.setTextViewText(R.id.widget_item1, context.getString(R.string.widget_empty))
                        views.setViewVisibility(R.id.widget_item2, View.GONE)
                        views.setViewVisibility(R.id.widget_item3, View.GONE)
                    } else {
                        val now = System.currentTimeMillis()
                        val itemIds = listOf(R.id.widget_item1, R.id.widget_item2, R.id.widget_item3)
                        
                        activeProducts.forEachIndexed { index, product ->
                            val diff = product.expirationDate!! - now
                            val days = (diff / (24 * 60 * 60 * 1000)).toInt()
                            
                            val statusText = when {
                                days < 0 -> context.getString(R.string.widget_expired)
                                days == 0 -> context.getString(R.string.widget_due_today)
                                days == 1 -> context.getString(R.string.widget_due_tomorrow)
                                else -> "$days ${context.getString(R.string.days)}"
                            }
                            
                            views.setTextViewText(itemIds[index], "• ${product.name} ($statusText)")
                            views.setViewVisibility(itemIds[index], View.VISIBLE)
                        }
                        
                        // Ocultar slots no usados
                        for (i in activeProducts.size until 3) {
                            views.setViewVisibility(itemIds[i], View.GONE)
                        }
                    }
                    appWidgetManager.updateAppWidget(appWidgetId, views)
                }
            } finally {
                pendingResult.finish()
            }
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        if (intent.action == AppWidgetManager.ACTION_APPWIDGET_UPDATE) {
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val component = ComponentName(context, PantryWidgetProvider::class.java)
            val ids = appWidgetManager.getAppWidgetIds(component)
            onUpdate(context, appWidgetManager, ids)
        }
    }

    companion object {
        fun triggerUpdate(context: Context) {
            val intent = Intent(context, PantryWidgetProvider::class.java).apply {
                action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
            }
            context.sendBroadcast(intent)
        }
    }
}
