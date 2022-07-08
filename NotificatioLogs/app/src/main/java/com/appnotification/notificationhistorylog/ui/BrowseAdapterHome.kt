package com.appnotification.notificationhistorylog.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Handler
import android.os.SystemClock
import android.provider.BaseColumns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.recyclerview.widget.RecyclerView
import com.appnotification.notificationhistorylog.R
import com.appnotification.notificationhistorylog.misc.Const
import com.appnotification.notificationhistorylog.misc.DatabaseHelper
import com.appnotification.notificationhistorylog.misc.Util.getAppIconFromPackage
import com.appnotification.notificationhistorylog.misc.Util.getAppNameFromPackage
import com.appnotification.notificationhistorylog.ui.DetailsActivity
import com.google.android.gms.ads.formats.UnifiedNativeAd
import com.google.android.gms.ads.formats.UnifiedNativeAdView
import org.json.JSONException
import org.json.JSONObject
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
@Suppress("DEPRECATE")
internal class BrowseAdapterHome(private val context: Activity, mNativeAds: List<UnifiedNativeAd>) :
    RecyclerView.Adapter<BrowseViewHolderHome>() {
    private val format = DateFormat.getDateInstance(DateFormat.DEFAULT, Locale.getDefault())
    private val handler = Handler()
    private val mRecyclerViewItems: List<UnifiedNativeAd>
    private val iconCache: HashMap<String?, Drawable?> = HashMap()
    var ads = 0
    private var data: ArrayList<DataItem>
    private var lastDate: String? = ""
    private var shouldLoadMore = true
    private var lastClickTime: Long = 0
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BrowseViewHolderHome {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_browse_home, parent, false)
        val vh = BrowseViewHolderHome(view)
        vh.item.setOnClickListener { v: View ->
            // preventing double, using threshold of 1000 ms
            if (SystemClock.elapsedRealtime() - lastClickTime < 1000) {
                return@setOnClickListener
            }
            lastClickTime = SystemClock.elapsedRealtime()
            val id = v.tag as String
            val intent = Intent(context, DetailsActivity::class.java)
            intent.putExtra(DetailsActivity.EXTRA_ID, id)
            val p1 = Pair.create<View, String>(vh.icon, "icon")
            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                context, p1
            )
            context.startActivityForResult(intent, 1, options.toBundle())
        }
        return vh
    }

    override fun onBindViewHolder(vh: BrowseViewHolderHome, position: Int) {
        val item = data[position]
        if (mRecyclerViewItems.size == ads) ads = 0
        if (mRecyclerViewItems.size > ads) {
            if (position % 18 == 0) {
                vh.adView.visibility = View.VISIBLE
                val nativeAd = mRecyclerViewItems[ads]
                ads++
                populateNativeAdView(nativeAd, vh.adView)
            } else {
                vh.adView.visibility = View.GONE
            }
        } else {
            vh.adView.visibility = View.GONE
        }
        if (iconCache.size > 0 && iconCache[item.packageName] != null && iconCache.containsKey(item.packageName)) {
            val drawable = iconCache[item.packageName]
            if (drawable != null) {
                vh.icon.setImageDrawable(drawable)
            } else {
                vh.icon.setImageDrawable(
                    getAppIconFromPackage(
                        context, item.packageName
                    )
                )
            }
        } else {
            vh.icon.setImageResource(R.drawable.ic_splash_logo)
        }
        vh.item.tag = "" + item.id
        vh.name.text = item.appName
        vh.time.text = item.time
        if (item.preview!!.isEmpty()) {
            vh.preview.visibility = View.GONE
            vh.text.visibility = View.VISIBLE
            vh.text.text = item.text
        } else {
            vh.text.visibility = View.GONE
            vh.preview.visibility = View.VISIBLE
            vh.preview.text = item.preview
        }
        if (item.shouldShowDate()) {
            vh.date.visibility = View.VISIBLE
            vh.date.text = item.date
        } else {
            vh.date.visibility = View.GONE
        }
        if (position == itemCount - 1) {
            loadMore(item.id)
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun filterList(filteredList: ArrayList<DataItem>) {
        data = filteredList
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun loadMore(afterId: Long) {
        if (!shouldLoadMore) {
            if (Const.DEBUG) println("not loading more items")
            return
        }
        if (Const.DEBUG) println("loading more items")
        val before = itemCount
        try {
            val databaseHelper = DatabaseHelper(context)
            val db = databaseHelper.readableDatabase
            val cursor = db.query(
                DatabaseHelper.PostedEntry.TABLE_NAME, arrayOf(
                    BaseColumns._ID,
                    DatabaseHelper.PostedEntry.COLUMN_NAME_CONTENT
                ),
                BaseColumns._ID + " < ?", arrayOf("" + afterId),
                null,
                null,
                BaseColumns._ID + " DESC",
                PAGE_SIZE
            )
            if (cursor != null && cursor.moveToFirst()) {
                for (i in 0 until cursor.count) {
                    val dataItem: DataItem = DataItem(
                        context, cursor.getLong(0), cursor.getString(1)
                    )
                    val thisDate = dataItem.date
                    if (lastDate == thisDate) {
                        dataItem.setShowDate(false)
                    }
                    lastDate = thisDate
                    data.add(dataItem)
                    cursor.moveToNext()
                }
                cursor.close()
            }
            db.close()
            databaseHelper.close()
        } catch (e: Exception) {
            if (Const.DEBUG) e.printStackTrace()
        }
        val after = itemCount
        if (before == after) {
            if (Const.DEBUG) println("no new items loaded: $itemCount")
            shouldLoadMore = false
        }
        if (itemCount > LIMIT) {
            if (Const.DEBUG) println("reached the limit, not loading more items: $itemCount")
            shouldLoadMore = false
        }
        handler.post { notifyDataSetChanged() }
    }

    fun filter(text: String): ArrayList<*> {
        val filteredList = ArrayList<DataItem>()
        run {
            for (item in data) {
                if (item.appName != null && item.appName != "" && (item.appName?.lowercase(Locale.getDefault())
                        ?.contains(text.lowercase(Locale.getDefault())) == true ||
                            item.text?.lowercase(Locale.getDefault())?.contains(text.lowercase(
                                Locale.getDefault()
                            )) == true ||
                            item.date?.lowercase(Locale.getDefault())?.contains(text.lowercase(
                                Locale.getDefault()
                            )) == true)
                ) {
                    filteredList.add(item)
                } else if (item.text != null && item.text?.lowercase(Locale.getDefault())
                        ?.contains(text.lowercase(Locale.getDefault())) == true
                ) {
                    filteredList.add(item)
                } else if (item.packageName != null && item.packageName?.lowercase(Locale.getDefault())
                        ?.contains(text.lowercase(Locale.getDefault())) == true
                ) {
                    filteredList.add(item)
                }
            }
        }
        return filteredList
    }
@Suppress("DEPRECATION")
    private fun populateNativeAdView(
        nativeAd: UnifiedNativeAd,
        adView: UnifiedNativeAdView
    ) {
        // Some assets are guaranteed to be in every UnifiedNativeAd.
        (adView.headlineView as TextView).text = nativeAd.headline
        (adView.bodyView as TextView).text = nativeAd.body
        (adView.callToActionView as Button).text = nativeAd.callToAction

        // These assets aren't guaranteed to be in every UnifiedNativeAd, so it's important to
        // check before trying to display them.
        val icon = nativeAd.icon
        if (icon == null) {
            adView.iconView.visibility = View.INVISIBLE
        } else {
            (adView.iconView as ImageView).setImageDrawable(icon.drawable)
            adView.iconView.visibility = View.VISIBLE
        }
        if (nativeAd.price == null) {
            adView.priceView.visibility = View.INVISIBLE
        } else {
            adView.priceView.visibility = View.VISIBLE
            (adView.priceView as TextView).text = nativeAd.price
        }
        if (nativeAd.store == null) {
            adView.storeView.visibility = View.INVISIBLE
        } else {
            adView.storeView.visibility = View.VISIBLE
            (adView.storeView as TextView).text = nativeAd.store
        }
        if (nativeAd.starRating == null) {
            adView.starRatingView.visibility = View.INVISIBLE
        } else {
            (adView.starRatingView as RatingBar).rating = nativeAd.starRating.toFloat()
            adView.starRatingView.visibility = View.VISIBLE
        }
        if (nativeAd.advertiser == null) {
            adView.advertiserView.visibility = View.INVISIBLE
        } else {
            (adView.advertiserView as TextView).text = nativeAd.advertiser
            adView.advertiserView.visibility = View.VISIBLE
        }

        // Assign native ad object to the native view.
        adView.setNativeAd(nativeAd)
    }

    inner class DataItem internal constructor(
        context: Context?,
        val id: Long,
        str: String?
    ) {
        var time: String? = null
        var packageName: String? = null
        var appName: String? = null
        var text: String? = null
        var preview: String? = null
        var date: String? = null
        private var showDate = false
        fun shouldShowDate(): Boolean {
            return showDate
        }

        fun setShowDate(showDate: Boolean) {
            this.showDate = showDate
        }

        init {
            try {
                val json = str?.let { JSONObject(it) }
                packageName = json?.getString("packageName")
                appName = getAppNameFromPackage(
                    context!!, packageName, false
                )
                text = str
                val title = json?.optString("title")
                val text = json?.optString("text")
                preview = """$title
$text""".trim { it <= ' ' }
                if (!iconCache.containsKey(packageName)) {
                    iconCache[packageName] = getAppIconFromPackage(
                        context, packageName
                    )
                }
                date = format.format(json?.optLong("systemTime"))
                val df: DateFormat = SimpleDateFormat("HH:mm", Locale.US)
                time = df.format(json?.optLong("systemTime"))
                showDate = true
            } catch (e: JSONException) {
                if (Const.DEBUG) e.printStackTrace()
            }
        }
    }

    companion object {
        private const val LIMIT = Int.MAX_VALUE
        private const val PAGE_SIZE = "2599"
    }

    init {
        data = ArrayList()
        loadMore(Int.MAX_VALUE.toLong())
        mRecyclerViewItems = mNativeAds
    }
}