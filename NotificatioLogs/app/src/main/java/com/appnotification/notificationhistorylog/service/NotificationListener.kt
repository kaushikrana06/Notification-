package com.appnotification.notificationhistorylog.service

import android.Manifest
import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import com.appnotification.notificationhistorylog.misc.Const
import com.appnotification.notificationhistorylog.misc.Util.hasPermission
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.location.*

class NotificationListener : NotificationListenerService() {
    private var activityRecognitionClient: ActivityRecognitionClient? = null
    private var activityRecognitionPendingIntent: PendingIntent? = null
    private var fusedLocationClient: FusedLocationProviderClient? = null
    private var fusedLocationPendingIntent: PendingIntent? = null
    override fun onCreate() {
        super.onCreate()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onListenerConnected() {
        super.onListenerConnected()
        instance = this
        startActivityRecognition()
        startFusedLocationIntentService()
    }

    override fun onListenerDisconnected() {
        instance = null
        stopActivityRecognition()
        stopFusedLocationIntentService()
        super.onListenerDisconnected()
    }

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        try {
            val notificationHandler = NotificationHandler(this)
            notificationHandler.handlePosted(sbn)
        } catch (e: Exception) {
            if (Const.DEBUG) e.printStackTrace()
        }
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification) {
        try {
            val notificationHandler = NotificationHandler(this)
            notificationHandler.handleRemoved(sbn, -1)
        } catch (e: Exception) {
            if (Const.DEBUG) e.printStackTrace()
        }
    }

    override fun onNotificationRemoved(
        sbn: StatusBarNotification,
        rankingMap: RankingMap,
        reason: Int
    ) {
        try {
            val notificationHandler = NotificationHandler(this)
            notificationHandler.handleRemoved(sbn, reason)
        } catch (e: Exception) {
            if (Const.DEBUG) e.printStackTrace()
        }
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    private fun startActivityRecognition() {
        if (!Const.ENABLE_ACTIVITY_RECOGNITION) {
            return
        }
        try {
            if (GoogleApiAvailability.getInstance()
                    .isGooglePlayServicesAvailable(this) == ConnectionResult.SUCCESS
            ) {
                activityRecognitionPendingIntent = PendingIntent.getService(
                    this,
                    0,
                    Intent(this, ActivityRecognitionIntentService::class.java),
                    PendingIntent.FLAG_UPDATE_CURRENT
                )

               var activityRecognitionClient = ActivityRecognition.getClient(this)
                activityRecognitionClient.requestActivityUpdates(
                    120000L,
                    activityRecognitionPendingIntent
                )
            }
        } catch (e: Exception) {
            if (Const.DEBUG) e.printStackTrace()
        }
    }

    private fun stopActivityRecognition() {
        if (!Const.ENABLE_ACTIVITY_RECOGNITION) {
            return
        }
        try {
            if (activityRecognitionClient != null && activityRecognitionPendingIntent != null) {
                activityRecognitionClient?.removeActivityUpdates(activityRecognitionPendingIntent)
                activityRecognitionClient = null
                activityRecognitionPendingIntent = null
            }
        } catch (e: Exception) {
            if (Const.DEBUG) e.printStackTrace()
        }
    }

    @SuppressLint("MissingPermission", "UnspecifiedImmutableFlag")
    private fun startFusedLocationIntentService() {
        if (!Const.ENABLE_LOCATION_SERVICE) {
            return
        }
        if (hasPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) && hasPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        ) {
            val locationRequest = LocationRequest.create()
            locationRequest.priority = LocationRequest.PRIORITY_LOW_POWER
            fusedLocationPendingIntent = PendingIntent.getService(
                this,
                0,
                Intent(this, FusedLocationIntentService::class.java),
                PendingIntent.FLAG_UPDATE_CURRENT
            )
           val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
            fusedLocationClient.requestLocationUpdates(locationRequest, fusedLocationPendingIntent)
        }
    }

    private fun stopFusedLocationIntentService() {
        if (!Const.ENABLE_LOCATION_SERVICE) {
            return
        }
        if (fusedLocationClient != null && fusedLocationPendingIntent != null) {
            fusedLocationClient?.removeLocationUpdates(fusedLocationPendingIntent)
        }
    }

    companion object {
        private var instance: NotificationListener? = null
        val allActiveNotifications: Array<StatusBarNotification>?
            get() {
                if (instance != null) {
                    try {
                        return instance?.activeNotifications
                    } catch (e: Exception) {
                        if (Const.DEBUG) e.printStackTrace()
                    }
                }
                return null
            }

        @get:TargetApi(Build.VERSION_CODES.O)
        val allSnoozedNotifications: Array<StatusBarNotification>?
            get() {
                if (instance != null) {
                    try {
                        return instance?.snoozedNotifications
                    } catch (e: Exception) {
                        if (Const.DEBUG) e.printStackTrace()
                    }
                }
                return null
            }

        @JvmStatic
        @get:TargetApi(Build.VERSION_CODES.LOLLIPOP)
        val interruptionFilter: Int?
            get() {
                if (instance != null) {
                    try {
                        return instance?.currentInterruptionFilter
                    } catch (e: Exception) {
                        if (Const.DEBUG) e.printStackTrace()
                    }
                }
                return -1
            }

        @JvmStatic
        @get:TargetApi(Build.VERSION_CODES.LOLLIPOP)
        val listenerHints: Int?
            get() {
                if (instance != null) {
                    try {
                        return instance?.currentListenerHints
                    } catch (e: Exception) {
                        if (Const.DEBUG) e.printStackTrace()
                    }
                }
                return -1
            }

        @JvmStatic
        @get:TargetApi(Build.VERSION_CODES.LOLLIPOP)
        val ranking: RankingMap?
            get() {
                if (instance != null) {
                    try {
                        return instance?.currentRanking
                    } catch (e: Exception) {
                        if (Const.DEBUG) e.printStackTrace()
                    }
                }
                return null
            }
    }
}