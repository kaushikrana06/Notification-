package com.appnotification.notificationhistorylog.Notifications

import android.annotation.SuppressLint
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.media.RingtoneManager
import android.util.Log
import androidx.core.app.NotificationCompat
import com.appnotification.notificationhistorylog.CommonCl.SharedCommon
import com.appnotification.notificationhistorylog.CommonCl.SharedCommon.putSharedPreferencesString
import com.appnotification.notificationhistorylog.R
import com.appnotification.notificationhistorylog.SettingsActivity
import com.appnotification.notificationhistorylog.ui.BrowseActivity
import com.appnotification.notificationhistorylog.ui.MainActivity
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import org.json.JSONException
import org.json.JSONObject

class MyFirebaseMessagingService : FirebaseMessagingService() {
    @SuppressLint("LogNotTimber")
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        val token = FirebaseInstanceId.getInstance().token
        putSharedPreferencesString(this@MyFirebaseMessagingService, SharedCommon.keytokenid, token)

        //To Get FCM TOKEN
        //eTBuGS-wrO4:APA91bGb1zBE1u9MIS1QrHo7MFaLfUROlAoDjZOpt7sdkLUpWe7K-yFcbYWxg1cMQPsl4vFAtuIr_N4MLkVlNQ93x0aRpAwP6Xw30M6PBWhC-R-GWLIJUgkOsDd4jAcwcdMUBeUTiS3g
        Log.d("Firebase", "token " + FirebaseInstanceId.getInstance().token)

        // Check if message contains a data payload.
        if (remoteMessage.data.isNotEmpty()) {
            Log.d(TAG, "Message data payload: " + remoteMessage.data)
            try {
                val data = (remoteMessage.data as Map<*, *>?)?.let { JSONObject(it) }
                val jsonMessage = data?.getString("extra_information")
                Log.d(
                    TAG, """
     onMessageReceived: 
     Extra Information: $jsonMessage
     """.trimIndent()
                )
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }

        // Check if message contains a notification payload.
        if (remoteMessage.notification != null) {
            val title = remoteMessage.notification!!.title //get title
            val message = remoteMessage.notification!!.body //get message
            val click_action = remoteMessage.notification!!.clickAction //get click_action
            Log.d(TAG, "Message Notification Title: $title")
            Log.d(TAG, "Message Notification Body: $message")
            Log.e(TAG, "Message Notification click_action: $click_action")
            sendNotification(title, message, click_action)
        }
    }

    override fun onDeletedMessages() {}
    private fun sendNotification(title: String?, messageBody: String?, click_action: String?) {
        val intent: Intent
        Log.e(TAG, "@@@ Click action ::> $click_action")
        if (click_action == "SETTINGS") {
            intent = Intent(this, SettingsActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        } else if (click_action == "MAINACTIVITY") {
            intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        } else if (click_action == "BROWSE") {
            intent = Intent(this, BrowseActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        } else {
            intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        }
        val pendingIntent = PendingIntent.getActivity(
            this, 0 /* Request code */, intent,
            PendingIntent.FLAG_ONE_SHOT
        )
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(messageBody)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build())
    }

    companion object {
        private const val TAG = "FirebaseMessagingServic"
    }
}