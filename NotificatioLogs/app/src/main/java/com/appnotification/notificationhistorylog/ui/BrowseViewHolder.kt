package com.appnotification.notificationhistorylog.ui

import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.appnotification.notificationhistorylog.R

class BrowseViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    @JvmField
    var icon: ImageView = view.findViewById(R.id.icon)

    @JvmField
    var name: TextView = view.findViewById(R.id.name)

    @JvmField
    var preview: TextView = view.findViewById(R.id.preview)

    @JvmField
    var text: TextView = view.findViewById(R.id.text)

    @JvmField
    var date: TextView = view.findViewById(R.id.date)

    @JvmField
    var time: TextView = view.findViewById(R.id.time)

    @JvmField
    var item: LinearLayout = view.findViewById(R.id.item)

    init {
        /*     adView = (UnifiedNativeAdView) view.findViewById(R.id.ad_view);

        // The MediaView will display a video asset if one is present in the ad, and the
        // first image asset otherwise.
        adView.setMediaView((MediaView) adView.findViewById(R.id.ad_media));

        // Register the view used for each individual asset.
        adView.setHeadlineView(adView.findViewById(R.id.ad_headline));
        adView.setBodyView(adView.findViewById(R.id.ad_body));
        adView.setCallToActionView(adView.findViewById(R.id.ad_call_to_action));
        adView.setIconView(adView.findViewById(R.id.ad_icon));
        adView.setPriceView(adView.findViewById(R.id.ad_price));
        adView.setStarRatingView(adView.findViewById(R.id.ad_stars));
        adView.setStoreView(adView.findViewById(R.id.ad_store));
        adView.setAdvertiserView(adView.findViewById(R.id.ad_advertiser));*/
    }
}