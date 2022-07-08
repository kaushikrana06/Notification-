package com.appnotification.notificationhistorylog.CommonCl

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager

object SharedCommon {
    const val key1 = "key1"
    const val keyopenrate = "keyopenrate"
    const val keyopenrateall = "keyopenrateall"
    const val keyopenratetutorial = "keyopenratetutorial"
    const val keyopenratefav = "keyopenratefav"
    const val keyopensetting = "keyopensetting"
    const val keylog = "keylog"
    const val keyoveruse = "keyoveruse"
    const val keyfaqs = "keyfaqs"
    const val keylogtext = "keylogtext"
    const val keyname = "keyname"
    const val keydis = "keydis"
    const val keynotification = "keynotification"
    const val keytokenid = "keytokenid"
    @JvmStatic
    fun putPreferencesInt(context: Context?, key: String?, value: Int) {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        val edit = preferences.edit()
        edit.putInt(key, value)
        edit.commit()
    }

    @JvmStatic
    fun getPreferencesInt(context: Context?, key: String?, _default: Int): Int {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        return preferences.getInt(key, _default)
    }

    @JvmStatic
    fun putSharedPreferencesString(context: Context?, key: String?, `val`: String?) {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        val edit = preferences.edit()
        edit.putString(key, `val`)
        edit.commit()
    }

    fun getSharedPreferencesString(context: Context?, key: String?, _default: String?): String? {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        return preferences.getString(key, _default)
    }
}