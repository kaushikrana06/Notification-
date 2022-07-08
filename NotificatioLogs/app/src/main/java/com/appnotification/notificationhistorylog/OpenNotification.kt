package com.appnotification.notificationhistorylog

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import android.widget.Toast
import com.appnotification.notificationhistorylog.OpenNotification

/**
 * Implementation of App Widget functionality.
 */
class OpenNotification : AppWidgetProvider() {
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        // There may be multiple widgets active, so update all of them
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onEnabled(context: Context) {
        // Enter relevant functionality for when the first widget is created

        // Toast.makeText(context, "Text Recognition Widget Added", Toast.LENGTH_SHORT).show();
    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        if (intent.action == CLICK_ACTION) {
            Toast.makeText(context, "Opening...", Toast.LENGTH_SHORT).show()
            val mailClient = Intent(Intent.ACTION_VIEW)
            mailClient.putExtra("TITLE", "Hindi")
            mailClient.setClassName(
                "com.appnotification.notificationhistorylog",
                "com.appnotification.notificationhistorylog.ui.BrowseActivity"
            )
            /*
             *    THE ANSWER!!!   mailClient.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
             */mailClient.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(mailClient)
        }
    }

    companion object {
        var WIDGET_BUTTON = "MY_PACKAGE_NAME.WIDGET_BUTTON"
        var CLICK_ACTION = "CLICKED"
        fun updateAppWidget(
            context: Context, appWidgetManager: AppWidgetManager,
            appWidgetId: Int
        ) {
            val intent = Intent(context, OpenNotification::class.java)
            intent.action = CLICK_ACTION
            val pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0)
            val intentbtn = Intent(WIDGET_BUTTON)
            val pendingIntentbtn =
                PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
            val widgetText: CharSequence = context.getString(R.string.opennotifwid)
            // Construct the RemoteViews object
            val views = RemoteViews(context.packageName, R.layout.open_notification)
            views.setTextViewText(R.id.appwidgettext, widgetText)
            views.setOnClickPendingIntent(R.id.layoutwidtext, pendingIntent)
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }
}