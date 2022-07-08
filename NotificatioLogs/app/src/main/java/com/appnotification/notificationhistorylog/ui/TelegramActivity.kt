package com.appnotification.notificationhistorylog.ui

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener
import com.appnotification.notificationhistorylog.CommonCl.SharedCommon
import com.appnotification.notificationhistorylog.CommonCl.SharedCommon.getPreferencesInt
import com.appnotification.notificationhistorylog.CommonCl.SharedCommon.key1
import com.appnotification.notificationhistorylog.CommonCl.SharedCommon.putPreferencesInt
import com.appnotification.notificationhistorylog.R
import com.appnotification.notificationhistorylog.ads.InterstitialAdsManager
import com.appnotification.notificationhistorylog.misc.Util
import com.appnotification.notificationhistorylog.misc.Util.appInstalledOrNot
import com.facebook.ads.*
import timber.log.Timber

class TelegramActivity : AppCompatActivity(), OnRefreshListener {
    private var recyclerView: RecyclerView? = null
    private var swipeRefreshLayout: SwipeRefreshLayout? = null
    private var mAdView: AdView? = null
    private var adListener: AdListener? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_browse)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        val backButton = toolbar.findViewById<ImageView>(R.id.back_image)
        val titleTextView = toolbar.findViewById<TextView>(R.id.title_text)
        val searchButton = toolbar.findViewById<ImageView>(R.id.search_image)
        val menuButton = toolbar.findViewById<ImageView>(R.id.menu_image)
        searchButton.visibility = View.GONE
        menuButton.visibility = View.INVISIBLE
        titleTextView.setText(R.string.title_app_telegram)
        backButton.setOnClickListener { finish() }
        val adsManager = InterstitialAdsManager(this)
        adsManager.show()
        val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(this)
        val recyclerView : RecyclerView = findViewById(R.id.list)
        recyclerView.layoutManager = layoutManager
        val swipeRefreshLayout:SwipeRefreshLayout = findViewById(R.id.swiper)
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent)
        swipeRefreshLayout.setOnRefreshListener(this)
        val isAppInstalled = appInstalledOrNot(this, Util.PACKAGE_TELEGRAM)
        if (isAppInstalled) {
            update()
            //ADS+Firebase
            mAdView = AdView(this, getString(R.string.fb_banner_ad), AdSize.BANNER_HEIGHT_50)
            showads()
            mAdView!!.loadAd(mAdView!!.buildLoadAdConfig().withAdListener(adListener).build())
            (findViewById<View>(R.id.fb_container) as LinearLayout).addView(mAdView)
        } else {
            Toast.makeText(this, "You Don't Have This App On Your Phone", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (data != null && DetailsActivity.ACTION_REFRESH == data.getStringExtra(DetailsActivity.EXTRA_ACTION)) {
            update()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.browse_appwise, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_refresh -> {
                update()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun update() {
        val adapter = GmailAdapter(this, Util.PACKAGE_TELEGRAM)
        recyclerView!!.adapter = adapter
        if (adapter.itemCount == 0) {
            Toast.makeText(this, R.string.empty_log_file, Toast.LENGTH_LONG).show()
            finish()
        }
    }

    override fun onRefresh() {
        update()
        swipeRefreshLayout!!.isRefreshing = false
    }

    private fun showads() {
        adListener = object : AdListener {
            override fun onError(ad: Ad, adError: AdError) {
                Timber.e("AdError: " + adError.errorCode + " " + adError.errorMessage)
            }

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
}