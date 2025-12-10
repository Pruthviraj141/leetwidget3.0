package com.atharva.leetcode_streak

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.SharedPreferences
import android.graphics.BitmapFactory
import android.util.Log
import android.widget.RemoteViews
import es.antonborri.home_widget.HomeWidgetProvider
import com.atharva.leetcode_streak.R
import android.net.Uri
import es.antonborri.home_widget.HomeWidgetLaunchIntent
import java.io.File

class LeetCodeWidgetProvider : HomeWidgetProvider() {
    companion object {
        private const val TAG = "LeetCodeWidgetProvider"
    }

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray,
        widgetData: SharedPreferences
    ) {
        Log.d(TAG, "Widget update started for ${appWidgetIds.size} widgets")
        
        for (appWidgetId in appWidgetIds) {
            Log.d(TAG, "Updating widget $appWidgetId")
            val views = RemoteViews(context.packageName, R.layout.widget_layout)

            val imagePath = widgetData.getString("filename", null)
            Log.d(TAG, "Image path from preferences: $imagePath")
            
            if (imagePath != null) {
                try {
                    val imageFile = File(imagePath)
                    if (imageFile.exists() && imageFile.canRead()) {
                        val bitmap = BitmapFactory.decodeFile(imagePath)
                        if (bitmap != null) {
                            Log.d(TAG, "Successfully loaded image for widget $appWidgetId")
                            views.setImageViewBitmap(R.id.widget_image, bitmap)
                        } else {
                            Log.e(TAG, "Failed to decode bitmap from: $imagePath")
                            setDefaultView(views, "Failed to decode image")
                        }
                    } else {
                        Log.e(TAG, "Image file does not exist or is not readable: $imagePath")
                        setDefaultView(views, "Image not found")
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error loading image: ${e.message}")
                    setDefaultView(views, "Error loading image")
                }
            } else {
                Log.d(TAG, "No image path found in preferences for widget $appWidgetId")
                setDefaultView(views, "No data available")
            }

            // Set up click handlers
            try {
                val clickIntent = HomeWidgetLaunchIntent.getActivity(
                    context,
                    MainActivity::class.java,
                    Uri.parse("homewidget://refresh")
                )
                views.setOnClickPendingIntent(R.id.widget_root, clickIntent)
                views.setOnClickPendingIntent(R.id.widget_image, clickIntent)
                Log.d(TAG, "Click handlers set for widget $appWidgetId")
            } catch (e: Exception) {
                Log.e(TAG, "Error setting click handlers: ${e.message}")
            }

            // Update the widget
            appWidgetManager.updateAppWidget(appWidgetId, views)
            Log.d(TAG, "Widget $appWidgetId updated successfully")
        }
    }

    private fun setDefaultView(views: RemoteViews, message: String) {
        // Try to set a placeholder image if available, otherwise show a colored background
        try {
            views.setImageViewResource(R.id.widget_image, R.drawable.widget_background)
            Log.d(TAG, "Set default placeholder image")
        } catch (e: Exception) {
            Log.e(TAG, "Could not set placeholder image: ${e.message}")
            // As a last resort, we'll leave the image view empty
        }
    }

    override fun onDeleted(context: Context, appWidgetIds: IntArray) {
        Log.d(TAG, "Widget(s) deleted: ${appWidgetIds.contentToString()}")
        super.onDeleted(context, appWidgetIds)
    }

    override fun onEnabled(context: Context) {
        Log.d(TAG, "Widget provider enabled")
        super.onEnabled(context)
    }

    override fun onDisabled(context: Context) {
        Log.d(TAG, "Widget provider disabled")
        super.onDisabled(context)
    }
}