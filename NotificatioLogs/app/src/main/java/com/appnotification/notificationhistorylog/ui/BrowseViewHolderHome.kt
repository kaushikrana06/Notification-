package com.appnotification.notificationhistorylog.ui

import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.appnotification.notificationhistorylog.R
import com.google.android.gms.ads.formats.UnifiedNativeAdView

internal class BrowseViewHolderHome(view: View) : RecyclerView.ViewHolder(view) {
    val adView: UnifiedNativeAdView = view.findViewById(R.id.ad_view)
    var icon: ImageView = view.findViewById(R.id.icon)
    var name: TextView = view.findViewById(R.id.name)
    var preview: TextView = view.findViewById(R.id.preview)
    var text: TextView = view.findViewById(R.id.text)
    var date: TextView = view.findViewById(R.id.date)
    var time: TextView = view.findViewById(R.id.time)
    var item: LinearLayout = view.findViewById(R.id.item)

    init {

        // The MediaView will display a video asset if one is present in the ad, and the
        // first image asset otherwise.
        adView.mediaView = adView.findViewById(R.id.ad_media)

        // Register the view used for each individual asset.
        adView.headlineView = adView.findViewById(R.id.ad_headline)
        adView.bodyView = adView.findViewById(R.id.ad_body)
        adView.callToActionView = adView.findViewById(R.id.ad_call_to_action)
        adView.iconView = adView.findViewById(R.id.ad_icon)
        adView.priceView = adView.findViewById(R.id.ad_price)
        adView.starRatingView = adView.findViewById(R.id.ad_stars)
        adView.storeView = adView.findViewById(R.id.ad_store)
        adView.advertiserView = adView.findViewById(R.id.ad_advertiser)
    }
}