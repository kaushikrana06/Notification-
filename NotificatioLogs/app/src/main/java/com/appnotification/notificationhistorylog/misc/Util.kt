package com.appnotification.notificationhistorylog.misc

import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.content.ContentResolver
import android.content.Context
import android.media.AudioManager
import android.os.PowerManager
import android.os.Build
import android.os.LocaleList
import androidx.core.content.PermissionChecker
import android.os.BatteryManager
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.content.Intent
import android.content.pm.ResolveInfo
import android.content.pm.ActivityInfo
import android.content.pm.ApplicationInfo
import android.provider.Settings
import com.appnotification.notificationhistorylog.BuildConfig
import java.lang.Exception
import java.util.ArrayList

object Util {
    const val PACKAGE_FACEBOOK = "com.facebook.katana"
    const val PACKAGE_WHATSAPP = "com.whatsapp"
    const val PACKAGE_INSTAGRAM = "com.instagram.android"
    const val PACKAGE_GMAIL = "com.google.android.gm"
    const val PACKAGE_TELEGRAM = "org.telegram.messenger"
    const val PACKAGE_CALENDAR = "com.google.android.calendar"
    const val PACKAGE_PHONE = "com.google.android.calendar"
    @JvmStatic
    fun getAppNameFromPackage(
        context: Context,
        packageName: String?,
        returnNull: Boolean
    ): String? {
        return try {
            val pm = context.applicationContext.packageManager
            val ai: ApplicationInfo?
            ai = try {
                pm.getApplicationInfo(packageName!!, 0)
            } catch (e: PackageManager.NameNotFoundException) {
                null
            }
            if (returnNull) {
                if (ai == null) null else pm.getApplicationLabel(ai).toString()
            } else (if (ai != null) pm.getApplicationLabel(ai) else packageName) as String?
        } catch (e: Exception) {
            "Unknown"
        }
    }

    @JvmStatic
    fun getAppIconFromPackage(context: Context, packageName: String?): Drawable? {
        val pm = context.applicationContext.packageManager
        var drawable: Drawable? = null
        try {
            val ai = pm.getApplicationInfo(packageName!!, 0)
            if (ai != null) {
                drawable = pm.getApplicationIcon(ai)
            }
        } catch (e: Exception) {
            if (Const.DEBUG) e.printStackTrace()
        }
        return drawable
    }

    @JvmStatic
    fun nullToEmptyString(charsequence: CharSequence?): String {
        return charsequence?.toString() ?: ""
    }

    @JvmStatic
    fun isNotificationAccessEnabled(context: Context): Boolean {
        try {
            val contentResolver = context.contentResolver
            val listeners =
                Settings.Secure.getString(contentResolver, "enabled_notification_listeners")
            return !(listeners == null || !listeners.contains(BuildConfig.APPLICATION_ID))
        } catch (e: Exception) {
            if (Const.DEBUG) e.printStackTrace()
        }
        return false
    }

    @JvmStatic
    fun getRingerMode(context: Context): Int {
        val am = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        if (am != null) {
            try {
                return am.ringerMode
            } catch (e: Exception) {
                if (Const.DEBUG) e.printStackTrace()
            }
        }
        return -1
    }

    @JvmStatic
    fun isScreenOn(context: Context): Boolean {
        val pm = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        if (pm != null) {
            try {
                return if (Build.VERSION.SDK_INT >= 20) {
                    pm.isInteractive
                } else {
                    pm.isScreenOn
                }
            } catch (e: Exception) {
                if (Const.DEBUG) e.printStackTrace()
            }
        }
        return false
    }

    fun getLocale(context: Context): String {
        return if (Build.VERSION.SDK_INT >= 24) {
            val localeList = context.resources.configuration.locales
            localeList.toString()
        } else {
            context.resources.configuration.locale.toString()
        }
    }

    @JvmStatic
    fun hasPermission(context: Context?, permission: String?): Boolean {
        return PermissionChecker.checkSelfPermission(
            context!!,
            permission!!
        ) == PermissionChecker.PERMISSION_GRANTED
    }

    fun getAllInstalledApps(context: Context): Array<String> {
        val pm = context.packageManager
        val packages = pm.getInstalledApplications(PackageManager.GET_META_DATA)
        val list = ArrayList<String>()
        for (packageInfo in packages) {
            list.add(packageInfo.packageName)
        }
        return list.toTypedArray()
    }

    @JvmStatic
    fun getBatteryLevel(context: Context): Int {
        if (Build.VERSION.SDK_INT >= 21) {
            val bm = context.getSystemService(Context.BATTERY_SERVICE) as BatteryManager
            if (bm != null) {
                try {
                    return bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
                } catch (e: Exception) {
                    if (Const.DEBUG) e.printStackTrace()
                }
            }
        }
        return -1
    }

    @JvmStatic
    fun getBatteryStatus(context: Context): String {
        if (Build.VERSION.SDK_INT < 26) {
            return "not supported"
        }
        try {
            val bm = context.getSystemService(Context.BATTERY_SERVICE) as BatteryManager
            if (bm != null) {
                val status = bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_STATUS)
                return when (status) {
                    BatteryManager.BATTERY_STATUS_CHARGING -> "charging"
                    BatteryManager.BATTERY_STATUS_DISCHARGING -> "discharging"
                    BatteryManager.BATTERY_STATUS_FULL -> "full"
                    BatteryManager.BATTERY_STATUS_NOT_CHARGING -> "not charging"
                    BatteryManager.BATTERY_STATUS_UNKNOWN -> "unknown"
                    else -> "" + status
                }
            }
        } catch (e: Exception) {
            if (Const.DEBUG) e.printStackTrace()
        }
        return "undefined"
    }

    @JvmStatic
    fun isNetworkAvailable(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (cm != null) {
            try {
                val activeNetworkInfo = cm.activeNetworkInfo
                return activeNetworkInfo != null && activeNetworkInfo.isConnected
            } catch (e: Exception) {
                if (Const.DEBUG) e.printStackTrace()
            }
        }
        return false
    }

    @JvmStatic
    fun getConnectivityType(context: Context): String {
        try {
            val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            if (cm != null) {
                val networkInfo = cm.activeNetworkInfo
                return if (networkInfo != null) {
                    val type = networkInfo.type
                    when (type) {
                        ConnectivityManager.TYPE_BLUETOOTH -> "bluetooth"
                        ConnectivityManager.TYPE_DUMMY -> "dummy"
                        ConnectivityManager.TYPE_ETHERNET -> "ethernet"
                        ConnectivityManager.TYPE_MOBILE -> "mobile"
                        ConnectivityManager.TYPE_MOBILE_DUN -> "mobile dun"
                        ConnectivityManager.TYPE_VPN -> "vpn"
                        ConnectivityManager.TYPE_WIFI -> "wifi"
                        ConnectivityManager.TYPE_WIMAX -> "wimax"
                        else -> "" + type
                    }
                } else {
                    "none"
                }
            }
        } catch (e: Exception) {
            if (Const.DEBUG) e.printStackTrace()
        }
        return "undefined"
    }

    @JvmStatic
    fun appInstalledOrNot(context: Context, uri: String?): Boolean {
        val pm = context.packageManager
        try {
            pm.getPackageInfo(uri!!, PackageManager.GET_ACTIVITIES)
            return true
        } catch (e: PackageManager.NameNotFoundException) {
        }
        return false
    }

    @JvmStatic
    fun getCallPackage(context: Context): String {
        val packageNames: MutableList<String> = ArrayList()
        // Declare action which target application listen to initiate phone call
        val intent = Intent()
        intent.action = Intent.ACTION_DIAL
        // Query for all those applications
        val resolveInfos = context.packageManager.queryIntentActivities(intent, 0)
        // Read package name of all those applications
        for (resolveInfo in resolveInfos) {
            val activityInfo = resolveInfo.activityInfo
            packageNames.add(activityInfo.applicationInfo.packageName)
        }
        return packageNames[0]
    }
}