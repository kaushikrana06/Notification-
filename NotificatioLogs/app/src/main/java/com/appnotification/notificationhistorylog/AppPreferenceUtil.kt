package com.appnotification.notificationhistorylog

import android.content.Context
import android.preference.PreferenceManager

object AppPreferenceUtil {
    //    public static void saveString(ContextString key, String value) {
    //        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(CacheManager.getAppContext());
    //        SharedPreferences.Editor editor = settings.edit();
    //        editor.putString(key, value);
    //        editor.apply();
    //    }
    //
    //    public static String getString(String key, String defaultValue) {
    //        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(CacheManager.getAppContext());
    //        return settings.getString(key, defaultValue);
    //    }
    fun saveInt(context: Context?, key: String?, value: Int) {
        val settings = PreferenceManager.getDefaultSharedPreferences(context)
        val editor = settings.edit()
        editor.putInt(key, value)
        editor.apply()
    }

    fun getInt(context: Context?, key: String?, defaultValue: Int): Int {
        val settings = PreferenceManager.getDefaultSharedPreferences(context)
        return settings.getInt(key, defaultValue)
    }

    //    public static void saveBoolean(String key, boolean value) {
    //        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(CacheManager.getAppContext());
    //        SharedPreferences.Editor editor = settings.edit();
    //        editor.putBoolean(key, value);
    //        editor.apply();
    //    }
    //
    //    public static boolean getBoolean(String key, boolean defaultValue) {
    //        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(CacheManager.getAppContext());
    //        return settings.getBoolean(key, defaultValue);
    //    }
    //
    //    public static void saveLong(String key, long value) {
    //        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(CacheManager.getAppContext());
    //        SharedPreferences.Editor editor = settings.edit();
    //        editor.putLong(key, value);
    //        editor.apply();
    //    }
    //
    //    public static long getLong(String key, long defaultValue) {
    //        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(CacheManager.getAppContext());
    //        return settings.getLong(key, defaultValue);
    //    }
    fun removeKey(context: Context?, key: String?) {
        val settings = PreferenceManager.getDefaultSharedPreferences(context)
        val editor = settings.edit()
        editor.remove(key)
        editor.apply()
    }
}