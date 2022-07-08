package com.appnotification.notificationhistorylog.service

import android.app.IntentService
import android.content.Intent
import android.preference.PreferenceManager
import com.appnotification.notificationhistorylog.misc.Const
import com.google.android.gms.location.ActivityRecognitionResult
import com.google.android.gms.location.DetectedActivity
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class ActivityRecognitionIntentService : IntentService("ActivityRecognitionIntentService") {
    @Deprecated("Deprecated in Java")
    override fun onHandleIntent(intent: Intent?) {
        if (intent == null) {
            return
        }
        val result = ActivityRecognitionResult.extractResult(intent)
        val detectedActivities = result.probableActivities as ArrayList<DetectedActivity>
        val activities = ArrayList<String?>()
        val confidences = ArrayList<Int?>()
        for (activity in detectedActivities) {
            activities.add(getActivityString(activity.type))
            confidences.add(activity.confidence)
        }
        val json = JSONObject()
        var str: String? = null
        try {
            val now = System.currentTimeMillis()
            json.put("time", now)
            json.put("offset", TimeZone.getDefault().getOffset(now))
            json.put("activities", JSONArray(activities))
            json.put("confidences", JSONArray(confidences))
            str = json.toString()
        } catch (e: JSONException) {
            if (Const.DEBUG) e.printStackTrace()
        }
        val sp = PreferenceManager.getDefaultSharedPreferences(this)
        sp.edit().putString(Const.PREF_LAST_ACTIVITY, str).apply()
    }

    private fun getActivityString(detectedActivityType: Int): String {
        return when (detectedActivityType) {
            DetectedActivity.IN_VEHICLE -> "IN_VEHICLE"
            DetectedActivity.ON_BICYCLE -> "ON_BICYCLE"
            DetectedActivity.ON_FOOT -> "ON_FOOT"
            DetectedActivity.RUNNING -> "RUNNING"
            DetectedActivity.STILL -> "STILL"
            DetectedActivity.TILTING -> "TILTING"
            DetectedActivity.UNKNOWN -> "UNKNOWN"
            DetectedActivity.WALKING -> "WALKING"
            else -> detectedActivityType.toString() + ""
        }
    }
}