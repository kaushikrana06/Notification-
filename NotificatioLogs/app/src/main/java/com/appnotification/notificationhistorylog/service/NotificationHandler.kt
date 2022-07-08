package com.appnotification.notificationhistorylog.service

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.service.notification.StatusBarNotification
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.preference.PreferenceManager
import com.appnotification.notificationhistorylog.misc.Const
import com.appnotification.notificationhistorylog.misc.DatabaseHelper

class NotificationHandler internal constructor(private val context: Context) {
    private val sp: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
    fun handlePosted(sbn: StatusBarNotification) {
        if (sbn.isOngoing && !sp.getBoolean(Const.PREF_ONGOING, false)) {
            if (Const.DEBUG) println("posted ongoing!")
            return
        }
        val text = sp.getBoolean(Const.PREF_TEXT, true)
        val no = NotificationObject(context, sbn, text, -1)
        log(
            DatabaseHelper.PostedEntry.TABLE_NAME,
            DatabaseHelper.PostedEntry.COLUMN_NAME_CONTENT,
            no.toString()
        )
    }

    fun handleRemoved(sbn: StatusBarNotification, reason: Int) {
        if (sbn.isOngoing && !sp.getBoolean(Const.PREF_ONGOING, false)) {
            if (Const.DEBUG) println("removed ongoing!")
            return
        }
        val no = NotificationObject(context, sbn, false, reason)
        log(
            DatabaseHelper.RemovedEntry.TABLE_NAME,
            DatabaseHelper.RemovedEntry.COLUMN_NAME_CONTENT,
            no.toString()
        )
    }

    private fun log(tableName: String, columnName: String, content: String?) {
        try {
            if (content != null) {
                synchronized(LOCK) {
                    val dbHelper = DatabaseHelper(context)
                    val db = dbHelper.writableDatabase
                    val values = ContentValues()
                    values.put(columnName, content)
                    db.insert(tableName, "null", values)
                    db.close()
                    dbHelper.close()
                }
                val local = Intent()
                local.action = BROADCAST
                LocalBroadcastManager.getInstance(context).sendBroadcast(local)
            }
        } catch (e: Exception) {
            if (Const.DEBUG) e.printStackTrace()
        }
    }

    companion object {
        const val BROADCAST = "org.hcilab.projects.nlogx.update"
        const val LOCK = "lock"
    }

}