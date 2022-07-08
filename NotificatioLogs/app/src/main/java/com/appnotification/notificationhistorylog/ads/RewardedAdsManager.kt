package com.appnotification.notificationhistorylog.ads

import android.app.Activity
import com.facebook.ads.Ad
import com.appnotification.notificationhistorylog.R
import com.facebook.ads.AdError
import com.facebook.ads.RewardedVideoAd
import com.facebook.ads.RewardedVideoAdListener

class RewardedAdsManager(activity: Activity) : RewardedVideoAdListener {
    private val rewardedVideoAd: RewardedVideoAd
    fun show() {
        if (rewardedVideoAd.isAdLoaded) rewardedVideoAd.show()
    }

    override fun onRewardedVideoCompleted() {}
    override fun onError(ad: Ad, adError: AdError) {}
    override fun onAdLoaded(ad: Ad) {}
    override fun onAdClicked(ad: Ad) {}
    override fun onLoggingImpression(ad: Ad) {}
    override fun onRewardedVideoClosed() {}

    init {
        rewardedVideoAd = RewardedVideoAd(activity, activity.getString(R.string.fb_interstitial_ad))
        rewardedVideoAd.loadAd(
            rewardedVideoAd.buildLoadAdConfig()
                .withAdListener(this)
                .build()
        )
    }
}