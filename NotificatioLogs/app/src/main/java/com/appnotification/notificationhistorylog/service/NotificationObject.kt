package com.appnotification.notificationhistorylog.service

import android.app.Notification
import android.content.Context
import android.os.Build
import android.preference.PreferenceManager
import android.service.notification.NotificationListenerService.Ranking
import android.service.notification.StatusBarNotification
import androidx.core.app.NotificationCompat
import com.appnotification.notificationhistorylog.BuildConfig
import com.appnotification.notificationhistorylog.misc.Const
import com.appnotification.notificationhistorylog.misc.Util.getAppNameFromPackage
import com.appnotification.notificationhistorylog.misc.Util.getBatteryLevel
import com.appnotification.notificationhistorylog.misc.Util.getBatteryStatus
import com.appnotification.notificationhistorylog.misc.Util.getConnectivityType
import com.appnotification.notificationhistorylog.misc.Util.getRingerMode
import com.appnotification.notificationhistorylog.misc.Util.isNetworkAvailable
import com.appnotification.notificationhistorylog.misc.Util.isScreenOn
import com.appnotification.notificationhistorylog.misc.Util.nullToEmptyString
import org.json.JSONObject
import java.util.*

internal class NotificationObject(
    private val context: Context,
    sbn: StatusBarNotification,
    private val LOG_TEXT: Boolean,
    reason: Int
) {
    private val n: Notification = sbn.notification

    // General
    private val packageName: String = sbn.packageName
    private val postTime: Long = sbn.postTime
    private val systemTime: Long = System.currentTimeMillis()
    private val isClearable: Boolean = sbn.isClearable
    private val isOngoing: Boolean = sbn.isOngoing
    private var lastActivity: String? = null
    private var lastLocation: String? = null

    // 18
    private val nid: Int = sbn.id
    private val tag: String = sbn.tag

    // 26
    private val removeReason: Int
    private var `when`: Long = 0
    private var number = 0
    private var flags = 0
    private var defaults = 0
    private var ledARGB = 0
    private var ledOff = 0
    private var ledOn = 0

    // Device
    private var ringerMode = 0
    private var isScreenOn = false
    private var batteryLevel = 0
    private var batteryStatus: String? = null
    private var isConnected = false
    private var connectionType: String? = null

    // Compat
    private var group: String? = null
    private var isGroupSummary = false
    private var category: String? = null
    private var actionCount = 0
    private var isLocalOnly = false
    private var people: List<*>? = null
    private var style: String? = null

    // 16
    private var priority = 0

    // 20
    private var key: String? = null
    private var sortKey: String? = null

    // 21
    private var visibility = 0
    private var color = 0
    private var interruptionFilter = 0
    private var listenerHints = 0
    private var matchesInterruptionFilter = false

    // Text
    private var appName: String? = null
    private var tickerText: String? = null
    private var title: String? = null
    private var titleBig: String? = null
    private var text: String? = null
    private var textBig: String? = null
    private var textInfo: String? = null
    private var textSub: String? = null
    private var textSummary: String? = null
    private var textLines: String? = null
    private fun extract() {
        // General
        `when` = n.`when`
        flags = n.flags
        defaults = n.defaults
        ledARGB = n.ledARGB
        ledOff = n.ledOffMS
        ledOn = n.ledOnMS
        number = if (Build.VERSION.SDK_INT < 24) { // as of 24, this number is not shown anymore
            n.number
        } else {
            -1
        }

        // Device
        ringerMode = getRingerMode(context)
        isScreenOn = isScreenOn(context)
        batteryLevel = getBatteryLevel(context)
        batteryStatus = getBatteryStatus(
            context
        )
        isConnected = isNetworkAvailable(
            context
        )
        connectionType = getConnectivityType(
            context
        )

        // 16
        priority = n.priority

        // 21
        visibility = n.visibility
        color = n.color
        listenerHints = NotificationListener.listenerHints!!
        interruptionFilter = NotificationListener.interruptionFilter!!
        val ranking = Ranking()
        val rankingMap = NotificationListener.ranking
        if (rankingMap != null && rankingMap.getRanking(key, ranking)) {
            matchesInterruptionFilter = ranking.matchesInterruptionFilter()
        }

        // Compat
        group = NotificationCompat.getGroup(n)
        isGroupSummary = NotificationCompat.isGroupSummary(n)
        category = NotificationCompat.getCategory(n)
        actionCount = NotificationCompat.getActionCount(n)
        isLocalOnly = NotificationCompat.getLocalOnly(n)
        val extras = NotificationCompat.getExtras(n)
        if (extras != null) {
            val tmp = extras.getStringArray(NotificationCompat.EXTRA_PEOPLE)
            people = if (tmp != null) listOf(*tmp) else null
            style = extras.getString(NotificationCompat.EXTRA_TEMPLATE)
        }

        // Text
        if (LOG_TEXT) {
            appName = getAppNameFromPackage(
                context, packageName, false
            )
            tickerText = nullToEmptyString(n.tickerText)
            if (extras != null) {
                title = nullToEmptyString(extras.getCharSequence(NotificationCompat.EXTRA_TITLE))
                titleBig = nullToEmptyString(extras.getCharSequence(NotificationCompat.EXTRA_TITLE))
                text = nullToEmptyString(extras.getCharSequence(NotificationCompat.EXTRA_TEXT))
                textBig =
                    nullToEmptyString(extras.getCharSequence(NotificationCompat.EXTRA_BIG_TEXT))
                textInfo =
                    nullToEmptyString(extras.getCharSequence(NotificationCompat.EXTRA_INFO_TEXT))
                textSub =
                    nullToEmptyString(extras.getCharSequence(NotificationCompat.EXTRA_SUB_TEXT))
                textSummary =
                    nullToEmptyString(extras.getCharSequence(NotificationCompat.EXTRA_SUMMARY_TEXT))
                val lines = extras.getCharSequenceArray(NotificationCompat.EXTRA_TEXT_LINES)
                if (lines != null) {
                    textLines = ""
                    for (line in lines) {
                        textLines += """
                            $line
                            
                            """.trimIndent()
                    }
                    textLines = textLines?.trim { it <= ' ' }
                }
            }
        }
    }

    override fun toString(): String {
        return try {
            val json = JSONObject()

            // General
            json.put("packageName", packageName)
            json.put("postTime", postTime)
            json.put("systemTime", systemTime)
            json.put("offset", TimeZone.getDefault().getOffset(systemTime))
            json.put("version", BuildConfig.VERSION_CODE)
            json.put("sdk", Build.VERSION.SDK_INT)
            json.put("isOngoing", isOngoing)
            json.put("isClearable", isClearable)
            json.put("when", `when`)
            json.put("number", number)
            json.put("flags", flags)
            json.put("defaults", defaults)
            json.put("ledARGB", ledARGB)
            json.put("ledOn", ledOn)
            json.put("ledOff", ledOff)

            // Device
            json.put("ringerMode", ringerMode)
            json.put("isScreenOn", isScreenOn)
            json.put("batteryLevel", batteryLevel)
            json.put("batteryStatus", batteryStatus)
            json.put("isConnected", isConnected)
            json.put("connectionType", connectionType)

            // Compat
            json.put("group", group)
            json.put("isGroupSummary", isGroupSummary)
            json.put("category", category)
            json.put("actionCount", actionCount)
            json.put("isLocalOnly", isLocalOnly)
            json.put("people", if (people == null) 0 else people!!.size)
            json.put("style", style)
            //json.put("displayName",    displayName);

            // Text
            if (LOG_TEXT) {
                json.put("tickerText", tickerText)
                json.put("title", title)
                json.put("titleBig", titleBig)
                json.put("text", text)
                json.put("textBig", textBig)
                json.put("textInfo", textInfo)
                json.put("textSub", textSub)
                json.put("textSummary", textSummary)
                json.put("textLines", textLines)
            }
            json.put("appName", appName)

            // 16
            json.put("priority", priority)

            // 18
            json.put("nid", nid)
            json.put("tag", tag)

            // 20
            json.put("key", key)
            json.put("sortKey", sortKey)

            // 21
            json.put("visibility", visibility)
            json.put("color", color)
            json.put("interruptionFilter", interruptionFilter)
            json.put("listenerHints", listenerHints)
            json.put("matchesInterruptionFilter", matchesInterruptionFilter)

            // 26
            if (true && removeReason != -1) {
                json.put("removeReason", removeReason)
            }

            // Activity
            if (Const.ENABLE_ACTIVITY_RECOGNITION && lastActivity != null) {
                json.put("lastActivity", lastActivity?.let { JSONObject(it) })
            }

            // Location
            if (Const.ENABLE_LOCATION_SERVICE && lastLocation != null) {
                json.put("lastLocation", lastLocation?.let { JSONObject(it) })
            }
            json.toString()
        } catch (e: Exception) {
            if (Const.DEBUG) e.printStackTrace()
            null
        }.toString()
    }

    init {
        key = sbn.key
        sortKey = n.sortKey
        removeReason = reason
        extract()
        if (Const.ENABLE_ACTIVITY_RECOGNITION || Const.ENABLE_LOCATION_SERVICE) {
            val sp = PreferenceManager.getDefaultSharedPreferences(
                context
            )
            lastActivity = sp.getString(Const.PREF_LAST_ACTIVITY, null)
            lastLocation = sp.getString(Const.PREF_LAST_LOCATION, null)
        }
    }
}