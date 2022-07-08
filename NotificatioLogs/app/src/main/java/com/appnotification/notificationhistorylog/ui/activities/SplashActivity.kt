package com.appnotification.notificationhistorylog.ui.activities

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.appnotification.notificationhistorylog.R
import com.appnotification.notificationhistorylog.ui.NewMainActivity

class SplashActivity : AppCompatActivity() {
    private var activity: Activity? = null
    @Suppress("DEPRECATION")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            supportActionBar?.hide()
            window.decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        } catch (ignore: Exception) {
        }
        activity = this
        try {
            setContentView(R.layout.activity_splash)
            changeStatusBarColor()
            Handler(Looper.getMainLooper()).postDelayed({
                val intent = Intent(activity, NewMainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                (activity as SplashActivity).finish()
                (activity as SplashActivity).startActivity(intent)
            }, 3000)
        } catch (ignore: Exception) {
        }
    }

    /**
     * Making notification bar transparent
     */
    private fun changeStatusBarColor() {
        val window = window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = Color.TRANSPARENT
    }
}