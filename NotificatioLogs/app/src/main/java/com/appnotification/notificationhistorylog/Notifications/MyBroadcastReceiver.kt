package com.appnotification.notificationhistorylog.Notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer

class MyBroadcastReceiver : BroadcastReceiver() {
    var mp: MediaPlayer? = null
    override fun onReceive(context: Context, intent: Intent) {


        //Toast.makeText(context, "Hello", Toast.LENGTH_LONG).show();
    }
}