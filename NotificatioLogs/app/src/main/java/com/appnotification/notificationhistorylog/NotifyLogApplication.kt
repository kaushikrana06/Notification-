package com.appnotification.notificationhistorylog

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.facebook.ads.AudienceNetworkAds
import timber.log.Timber
import timber.log.Timber.DebugTree

class NotifyLogApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(DebugTree())
        }
        if (SettingsActivity.Companion.isNightModeEnabled(applicationContext)) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }

        // Initialize the Audience Network SDK
        AudienceNetworkAds.initialize(this)
    }
}