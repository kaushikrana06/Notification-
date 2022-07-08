package com.appnotification.notificationhistorylog.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.Menu
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
import com.appnotification.notificationhistorylog.BuildConfig
import com.appnotification.notificationhistorylog.CommonCl.SharedCommon
import com.appnotification.notificationhistorylog.CommonCl.SharedCommon.getPreferencesInt
import com.appnotification.notificationhistorylog.CommonCl.SharedCommon.key1
import com.appnotification.notificationhistorylog.CommonCl.SharedCommon.putPreferencesInt
import com.appnotification.notificationhistorylog.R
import com.appnotification.notificationhistorylog.ads.InterstitialAdsManager
import com.facebook.ads.*
import java.util.stream.Collectors

class ViewGrouped : AppCompatActivity(), OnRefreshListener {
    private var newData: ArrayList<GroupedDataItem>? = null
    private var holderObjs: ArrayList<HelperObject>? = null
    private var recyclerView: RecyclerView? = null
    private var data: ArrayList<BrowseAppAdapter.DataItem>? = null
    private var swipeRefreshLayout: SwipeRefreshLayout? = null
    private var mAdView: AdView? = null
    private var adListener: AdListener? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_grouped)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        val backButton = toolbar.findViewById<ImageView>(R.id.back_image)
        val titleTextView = toolbar.findViewById<TextView>(R.id.title_text)
        val searchButton = toolbar.findViewById<ImageView>(R.id.search_image)
        val menuButton = toolbar.findViewById<ImageView>(R.id.menu_image)
        searchButton.visibility = View.GONE
        menuButton.visibility = View.INVISIBLE
        titleTextView.text = getString(R.string.nav_stats)
        backButton.setOnClickListener { finish() }
        val adsManager = InterstitialAdsManager(this)
        adsManager.show()
        val swipeRefreshLayout:SwipeRefreshLayout = findViewById(R.id.swiper)
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent)
        swipeRefreshLayout.setOnRefreshListener(this)
        val recyclerView :RecyclerView= findViewById(R.id.list)
        val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager
        try {
            update()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        //ADS+Firebase
        mAdView = AdView(this, getString(R.string.fb_banner_ad), AdSize.BANNER_HEIGHT_50)
        showads()
        mAdView?.loadAd(mAdView?.buildLoadAdConfig()?.withAdListener(adListener)?.build())
        (findViewById<View>(R.id.fb_container) as LinearLayout).addView(mAdView)
    }

    override fun onDestroy() {
        if (mAdView != null) {
            mAdView?.destroy()
        }
        super.onDestroy()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menustats, menu)
        return true
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

    //    @Override
    //    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
    //        switch (item.getItemId()) {
    //
    //            case R.id.count:
    //                graphinfo();
    //                    /*getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
    //                            new SettingsFragment()).commit();*/
    //
    //
    //                return true;
    //
    //
    //        }
    //        return super.onOptionsItemSelected(item);
    //    }
    //
    //    private void graphinfo() {
    //        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(ViewGrouped.this);
    //
    //        builder.setIcon(R.drawable.notificationlogo);
    //
    //        builder.setTitle("Coming Soon!");
    //
    //        builder.setMessage("Get Insights Of Notifications, View Data Of Notifications Into Graph And Manage Easily");
    //        builder.setCancelable(false);
    //
    //        builder.setPositiveButton("Got It", new DialogInterface.OnClickListener() {
    //            @Override
    //            public void onClick(DialogInterface dialogInterface, int i) {
    //                dialogInterface.dismiss();
    //                Intent startIntent = new Intent(ViewGrouped.this, NewMainActivity.class);
    //                startActivity(startIntent);
    //
    //            }
    //        });
    //        builder.setNegativeButton("Notify Me", new DialogInterface.OnClickListener() {
    //            @Override
    //            public void onClick(DialogInterface dialogInterface, int i) {
    //                Toast.makeText(ViewGrouped.this, "We Will Notify You ", Toast.LENGTH_SHORT).show();
    //                dialogInterface.dismiss();
    //            }
    //        });
    //
    //
    //        android.app.AlertDialog dialog = builder.create();
    //        dialog.show();
    //    }
    @Throws(Exception::class)
    private fun update() {
        //start filling recycler view
        holderObjs = ArrayList()
        newData = ArrayList()
        data = ArrayList()
        val adapter = BrowseAppAdapter(this)
        data = adapter.data
        for (itm in data!!) {
            try {
                val dataItem = GroupedDataItem(this, itm.str)
                if (dataItem != null) newData!!.add(dataItem)
            } catch (e: Exception) {
                if (BuildConfig.DEBUG) e.printStackTrace()
            }
        }
        @SuppressLint("NewApi", "LocalSuppress") val groupedByPackageName =
            newData?.stream()?.collect(
                Collectors.groupingBy { w: GroupedDataItem -> w.packageName })
        if (groupedByPackageName != null) {
            for (str in groupedByPackageName.keys) {
                val obj = HelperObject()
                obj.packageName = str
                obj.title = groupedByPackageName[str]!![0].appName
                obj.notificationCount = groupedByPackageName[str]!!.size
                holderObjs?.add(obj)
            }
        }
        holderObjs?.sortWith(Comparator { obj1, obj2 ->
            // ## Ascending order
    //                return obj1.firstName.compareToIgnoreCase(obj2.firstName); // To compare string values
    //                return Integer.valueOf(obj1.getNotificationCount()).compareTo(Integer.valueOf(obj2.getNotificationCount())); // To compare integer values

            // ## Descending order
            // return obj2.firstName.compareToIgnoreCase(obj1.firstName); // To compare string values
            obj2.notificationCount.compareTo(obj1.notificationCount) // To compare integer values
        })
        val groupedAdapter = ViewGroupedAdapter(this, holderObjs!!)
        recyclerView?.adapter = groupedAdapter
        //        Log.e("Map size","Size: "+groupedByPackageName.size());
//        recyclerView.setAdapter(adapter);
        if (adapter.itemCount == 0) {
            Toast.makeText(this, R.string.empty_log_file, Toast.LENGTH_LONG).show()
            finish()
        }
    }

    override fun onRefresh() {
        try {
            update()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        swipeRefreshLayout?.isRefreshing = false
    }
}