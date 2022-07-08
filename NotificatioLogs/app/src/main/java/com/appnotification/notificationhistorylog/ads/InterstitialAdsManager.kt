package com.appnotification.notificationhistorylog.ads

import android.app.Activity
import com.facebook.ads.InterstitialAdListener
import com.appnotification.notificationhistorylog.ads.InterstitialAdsManager.OnAdResponseFinish
import com.appnotification.notificationhistorylog.AppPreferenceUtil
import timber.log.Timber
import com.facebook.ads.Ad
import com.appnotification.notificationhistorylog.R
import com.facebook.ads.AdError
import com.facebook.ads.InterstitialAd

class InterstitialAdsManager(private val activity: Activity) : InterstitialAdListener {
    private val interstitialAd: InterstitialAd
    private var onAdResponseFinish: OnAdResponseFinish? = null
    fun setOnAdFinishListener(onAdResponseFinish: OnAdResponseFinish?) {
        this.onAdResponseFinish = onAdResponseFinish
    }

    fun show() {
        val totalBlockCount = AppPreferenceUtil.getInt(activity, "total_block_count", 0)
        Timber.i("TotalAdBlockCount : %s", totalBlockCount)
        if (totalBlockCount == 0 && interstitialAd.isAdLoaded) {
            interstitialAd.show()
        } else {
            var currentBlockCount = AppPreferenceUtil.getInt(activity, "current_block_count", 0)
            Timber.i("currentBlockCount : %s", currentBlockCount)
            if (currentBlockCount > totalBlockCount) {
                AppPreferenceUtil.removeKey(activity, "total_block_count")
                AppPreferenceUtil.removeKey(activity, "current_block_count")
            } else {
                currentBlockCount++
                AppPreferenceUtil.saveInt(activity, "current_block_count", currentBlockCount)
            }
        }
    }

    override fun onInterstitialDisplayed(ad: Ad) {
        Timber.i("onInterstitialDisplayed.................")
    }

    override fun onInterstitialDismissed(ad: Ad) {
        Timber.i("onInterstitialDismissed.................")
        if (onAdResponseFinish != null) onAdResponseFinish!!.onAdFinish()
    }

    override fun onError(ad: Ad, adError: AdError) {
        Timber.i("onError.................")
        if (onAdResponseFinish != null) onAdResponseFinish!!.onAdFinish()
    }

    override fun onAdLoaded(ad: Ad) {
        Timber.i("onAdLoaded.................")
        show()
    }

    override fun onAdClicked(ad: Ad) {
        Timber.i("onAdClicked.................")
        val DEFAULT_BLOCK_COUNT = 3
        AppPreferenceUtil.saveInt(activity, "total_block_count", DEFAULT_BLOCK_COUNT)
    }

    override fun onLoggingImpression(ad: Ad) {
        Timber.i("onLoggingImpression.................")
    }

    interface OnAdResponseFinish {
        fun onAdFinish()
    }

    init {
        interstitialAd = InterstitialAd(activity, activity.getString(R.string.fb_interstitial_ad))
        interstitialAd.loadAd(
            interstitialAd.buildLoadAdConfig()
                .withAdListener(this)
                .build()
        )
    }
}