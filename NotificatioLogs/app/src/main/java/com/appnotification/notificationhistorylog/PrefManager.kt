package com.appnotification.notificationhistorylog

import android.content.Context
import android.content.SharedPreferences

/**
 * Created by Lincoln on 05/05/16.
 */
class PrefManager(var _context: Context) {
    @JvmName("setFirstTimeLaunch1")
    fun setFirstTimeLaunch(b: Boolean): Boolean {
return true
    }

    var pref: SharedPreferences
    var editor: SharedPreferences.Editor

    // shared pref mode
    var PRIVATE_MODE = 0
    var isFirstTimeLaunch: Boolean
        get() = pref.getBoolean(IS_FIRST_TIME_LAUNCH, true)
        set(isFirstTime) {
            editor.putBoolean(IS_FIRST_TIME_LAUNCH, isFirstTime)
            editor.commit()
        }

    companion object {
        // Shared preferences file name
        //  private static final String PREF_NAME = "androidhive-welcome";
        private const val PREF_NAME = "notificationlog-welcome12"
        private const val IS_FIRST_TIME_LAUNCH = "IsFirstTimeLaunch"
    }

    init {
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE)
        editor = pref.edit()
    }
}