package com.appnotification.notificationhistorylog

import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log

class NotificationListenerWhatsapp : NotificationListenerService() {
    override fun onNotificationPosted(sbn: StatusBarNotification) {
        val packageName = sbn.packageName
        val notitficationTitle = sbn.notification.extras.getString("android.title")
        val notificationtext = sbn.notification.extras.getString("android.text")
        Log.v("notify_pkg_name", packageName)
        Log.v("notify_title", notitficationTitle!!)
        Log.v("notify_text", notitficationTitle)
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification) {
        super.onNotificationRemoved(sbn)
    }
}