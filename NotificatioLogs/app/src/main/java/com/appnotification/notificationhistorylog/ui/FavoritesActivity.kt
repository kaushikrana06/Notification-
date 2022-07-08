package com.appnotification.notificationhistorylog.ui

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
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
import com.facebook.ads.*

class FavoritesActivity : AppCompatActivity() {
    private var adapter: FavoriteAdapter? = null
    private var mAdView: AdView? = null
    private var adListener: AdListener? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favorites)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        val backButton = toolbar.findViewById<ImageView>(R.id.back_image)
        val titleTextView = toolbar.findViewById<TextView>(R.id.title_text)
        val searchButton = toolbar.findViewById<ImageView>(R.id.search_image)
        val menuButton = toolbar.findViewById<ImageView>(R.id.menu_image)
        searchButton.visibility = View.GONE
        menuButton.visibility = View.INVISIBLE
        titleTextView.text = getString(R.string.favorites)
        backButton.setOnClickListener { finish() }
        val adsManager = InterstitialAdsManager(this)
        adsManager.show()
        val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(this)
        val recyclerView = findViewById<RecyclerView>(R.id.favorites_list)
        recyclerView.layoutManager = layoutManager
        adapter = FavoriteAdapter(this, true)
        recyclerView.adapter = adapter

//        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
//            @Override
//            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
//                return false;
//            }
//
//            @Override
//            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
//
//                if (direction == ItemTouchHelper.LEFT) {
//                    adapter.deleteFromFavorite(viewHolder.getAdapterPosition());
//                }
//
//            }
//
//            @Override
//            public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
//                if (viewHolder != null) {
//                    final View foregroundView = ((FavoriteViewHolder) viewHolder).container;
//                    getDefaultUIUtil().onSelected(foregroundView);
//                }
//            }
//
//            @Override
//            public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
//                final View foregroundView = ((FavoriteViewHolder) viewHolder).container;
//                getDefaultUIUtil().clearView(foregroundView);
//            }
//
//            @Override
//            public void onChildDraw(Canvas c, RecyclerView recyclerView,
//                                    RecyclerView.ViewHolder viewHolder, float dX, float dY,
//                                    int actionState, boolean isCurrentlyActive) {
//                final View foregroundView = ((FavoriteViewHolder) viewHolder).container;
//
//                getDefaultUIUtil().onDraw(c, recyclerView, foregroundView, dX, dY,
//                        actionState, isCurrentlyActive);
//
//            }
//
//            @Override
//            public void onChildDrawOver(Canvas c, RecyclerView recyclerView,
//                                        RecyclerView.ViewHolder viewHolder, float dX, float dY,
//                                        int actionState, boolean isCurrentlyActive) {
//                final View foregroundView = ((FavoriteViewHolder) viewHolder).container;
//                getDefaultUIUtil().onDrawOver(c, recyclerView, foregroundView, dX, dY,
//                        actionState, isCurrentlyActive);
//                if (getSwipeDirs(recyclerView, viewHolder) == ItemTouchHelper.RIGHT) {
////                    ((FavoriteViewHolder) viewHolder).viewBackground_delete.setBackgroundColor(getResources().getColor(R.color.green));
////                    ((FavoriteViewHolder) viewHolder).option_text.setText("ADD TO FAVORITES");
//                }
//            }
//
//
//        }).attachToRecyclerView(recyclerView);
        if (adapter?.itemCount == 0) {
            Toast.makeText(this, getString(R.string.message_no_favorite), Toast.LENGTH_LONG).show()
            finish()
            return
        }

        //ADS+Firebase
        mAdView = AdView(this, getString(R.string.fb_banner_ad), AdSize.BANNER_HEIGHT_50)
        showads()
        mAdView!!.loadAd(mAdView?.buildLoadAdConfig()?.withAdListener(adListener)?.build())
        (findViewById<View>(R.id.fb_container) as LinearLayout).addView(mAdView)
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