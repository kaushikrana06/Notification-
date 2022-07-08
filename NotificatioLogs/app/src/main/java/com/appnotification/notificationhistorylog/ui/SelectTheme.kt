package com.appnotification.notificationhistorylog.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Switch
import androidx.appcompat.app.AppCompatActivity
import com.appnotification.notificationhistorylog.R

class SelectTheme : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        val useDarkTheme = preferences.getBoolean(PREF_DARK_THEME, false)
        if (useDarkTheme) {
            setTheme(R.style.AppTheme_Dark)
        }
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_theme)
        val toggle = findViewById<Switch>(R.id.theme_switch)
        toggle.isChecked = useDarkTheme
        toggle.setOnCheckedChangeListener { view, isChecked -> toggleTheme(isChecked) }
    }

    fun toggleTheme(darkTheme: Boolean) {
        val editor = getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit()
        editor.putBoolean(PREF_DARK_THEME, darkTheme)
        editor.apply()
        startActivity(Intent(this@SelectTheme, MainActivity::class.java))
        finish()
    }

    companion object {
        private const val PREFS_NAME = "prefs"
        private const val PREF_DARK_THEME = "dark_theme"
    }
}