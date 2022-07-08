package com.appnotification.notificationhistorylog.service

import android.app.IntentService
import android.content.Intent
import android.preference.PreferenceManager
import com.appnotification.notificationhistorylog.misc.Const
import com.google.android.gms.location.LocationResult
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class FusedLocationIntentService : IntentService("FusedLocationIntentService") {
    @Deprecated("Deprecated in Java")
    override fun onHandleIntent(intent: Intent?) {
        if (LocationResult.hasResult(intent)) {
            val locationResult = LocationResult.extractResult(intent)
            val location = locationResult.lastLocation
            val json = JSONObject()
            var str: String? = null
            try {
                val now = System.currentTimeMillis()
                json.put("time", now)
                json.put("offset", TimeZone.getDefault().getOffset(now))
                json.put("age", now - location.time)
                json.put("longitude", location.longitude.toString() + "")
                json.put("latitude", location.latitude.toString() + "")
                json.put("accuracy", location.accuracy.toDouble())
                str = json.toString()
            } catch (e: JSONException) {
                if (Const.DEBUG) e.printStackTrace()
            }
            val sp = PreferenceManager.getDefaultSharedPreferences(this)
            sp.edit().putString(Const.PREF_LAST_LOCATION, str).apply()
        }
    }
}