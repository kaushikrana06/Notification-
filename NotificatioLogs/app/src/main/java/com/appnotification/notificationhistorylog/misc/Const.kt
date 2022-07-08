package com.appnotification.notificationhistorylog.misc

import com.appnotification.notificationhistorylog.BuildConfig

object Const {
    @JvmField
    val DEBUG = BuildConfig.DEBUG
    const val VERSION = BuildConfig.VERSION_CODE.toLong()

    // Feature flags
    const val ENABLE_ACTIVITY_RECOGNITION = true
    const val ENABLE_LOCATION_SERVICE = true

    // Preferences shown in the UI
    const val PREF_FAVORITES = "pref_favorites"
    const val PREF_STATUS = "pref_status"
    const val PREF_TEXT = "pref_text"
    const val PREF_ONGOING = "pref_ongoing"
    const val PREF_BROWSE = "pref_browse"
    const val PREF_ENTRIES = "pref_entries"
    const val PREF_VERSION = "pref_version"

    // Preferences not shown in the UI
    const val PREF_LAST_ACTIVITY = "pref_last_activity"
    const val PREF_LAST_LOCATION = "pref_last_location"
    const val EMAIL = "notificationappgp@gmail.com"
}