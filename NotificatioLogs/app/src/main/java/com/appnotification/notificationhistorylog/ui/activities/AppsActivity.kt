package com.appnotification.notificationhistorylog.ui.activities

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.appnotification.notificationhistorylog.CommonCl.SharedCommon
import com.appnotification.notificationhistorylog.CommonCl.SharedCommon.getPreferencesInt
import com.appnotification.notificationhistorylog.CommonCl.SharedCommon.key1
import com.appnotification.notificationhistorylog.CommonCl.SharedCommon.putPreferencesInt
import com.appnotification.notificationhistorylog.R
import com.appnotification.notificationhistorylog.ads.InterstitialAdsManager
import com.appnotification.notificationhistorylog.ui.*
import com.appnotification.notificationhistorylog.ui.adapters.AppsAdapter
import com.appnotification.notificationhistorylog.ui.adapters.AppsAdapter.OnAppClickListener
import com.appnotification.notificationhistorylog.ui.items.AppItem
import com.facebook.ads.*
import timber.log.Timber

abstract class AppsActivity : AppCompatActivity(), OnAppClickListener {
    private var mAdView: AdView? = null
    private var adListener: AdListener? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_apps)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        val backButton = toolbar.findViewById<ImageView>(R.id.back_image)
        val titleTextView = toolbar.findViewById<TextView>(R.id.title_text)
        val searchButton = toolbar.findViewById<ImageView>(R.id.search_image)
        val menuButton = toolbar.findViewById<ImageView>(R.id.menu_image)
        searchButton.visibility = View.GONE
        menuButton.visibility = View.INVISIBLE
        titleTextView.text = getString(R.string.nav_apps)
        backButton.setOnClickListener { finish() }
        val adsManager = InterstitialAdsManager(this)
        adsManager.show()
        val appsRecycler = findViewById<RecyclerView>(R.id.app_recycler)
        val apps = resources.getStringArray(R.array.apps)
        val items = arrayOfNulls<AppItem>(apps.size)
        for (i in apps.indices) {
            Timber.i("Apps: %s", apps[i])
            val app = AppItem()
            app.name = apps[i]
            when (i) {
                0 -> {

                    // Whatsapp
                    app.icon = R.drawable.ic_whatsapp
                }
                1 -> {

                    // Gmail
                    app.icon = R.drawable.ic_gmail
                }
                2 -> {

                    // Facebook
                    app.icon = R.drawable.ic_facebook
                }
                3 -> {

                    // Instagram
                    app.icon = R.drawable.ic_instagram
                }
                4 -> {

                    // Calendar
                    app.icon = R.drawable.ic_calendar
                }
                5 -> {

                    // Telegram
                    app.icon = R.drawable.ic_telegram
                }
                6 -> {
                    if (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES) {
                        app.icon = R.drawable.ic_phone_white
                    } else {
                        app.icon = R.drawable.ic_phone
                    }
                }
            }
            items[i] = app
        }
        val layoutManager = LinearLayoutManager(this)
        val appsAdapter = AppsAdapter(items, this)
        appsRecycler.layoutManager = layoutManager
        appsRecycler.adapter = appsAdapter


        //ADS+Firebase
        mAdView = AdView(this, getString(R.string.fb_banner_ad), AdSize.BANNER_HEIGHT_50)
        showads()
        mAdView?.loadAd(mAdView?.buildLoadAdConfig()?.withAdListener(adListener)?.build())
        (findViewById<View>(R.id.fb_container) as LinearLayout).addView(mAdView)
    }

    override fun onAppClick(appName: String?, position: Int) {
        var startIntent: Intent? = null
        when (position) {
            0 -> startIntent = Intent(this, WhatsappActivity::class.java)
            1 -> startIntent = Intent(this, GmailActivity::class.java)
            2 -> startIntent = Intent(this, FacebookActivity::class.java)
            3 -> startIntent = Intent(this, InstaActivity::class.java)
            4 -> startIntent = Intent(this, CalenderActivity::class.java)
            5 -> startIntent = Intent(this, TelegramActivity::class.java)
            6 -> startIntent = Intent(this, CallsActivity::class.java)
        }
        if (startIntent != null) {
            startIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(startIntent)
        }
    }

    private fun showads() {
        adListener = object : AdListener {
            override fun onError(ad: Ad, adError: AdError) {}
            override fun onAdLoaded(ad: Ad) {}
            override fun onAdClicked(ad: Ad) {
                val sc = SharedCommon
                var i = getPreferencesInt(applicationContext, key1, 0)
                i++
                putPreferencesInt(applicationContext, SharedCommon.key1, i)
            }

            override fun onLoggingImpression(ad: Ad) {}
        }
    }

    override fun onDestroy() {
        if (mAdView != null) {
            mAdView?.destroy()
        }
        super.onDestroy()
    }
}