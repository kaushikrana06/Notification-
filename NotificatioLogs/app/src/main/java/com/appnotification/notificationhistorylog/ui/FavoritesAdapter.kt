package com.appnotification.notificationhistorylog.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Handler
import android.provider.BaseColumns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.recyclerview.widget.RecyclerView
import com.appnotification.notificationhistorylog.R
import com.appnotification.notificationhistorylog.misc.Const
import com.appnotification.notificationhistorylog.misc.DatabaseHelper
import com.appnotification.notificationhistorylog.misc.Util.getAppIconFromPackage
import com.appnotification.notificationhistorylog.misc.Util.getAppNameFromPackage
import com.appnotification.notificationhistorylog.ui.DetailsActivity
import org.json.JSONException
import org.json.JSONObject
import java.text.DateFormat
import java.util.*

class FavoritesAdapter internal constructor(private val context: Activity) :
    RecyclerView.Adapter<BrowseViewHolder>() {
    private val format = DateFormat.getDateInstance(DateFormat.DEFAULT, Locale.getDefault())
    private val data = ArrayList<DataItem>()
    private val iconCache = HashMap<String?, Drawable?>()
    private val handler = Handler()
    private var lastDate: String? = ""
    private var shouldLoadMore = true
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BrowseViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_new_browse, parent, false)
        val vh = BrowseViewHolder(view)
        vh.item.setOnClickListener { v: View ->
            val id = v.tag as String
            if (id != null) {
                val intent = Intent(context, DetailsActivity::class.java)
                intent.putExtra(DetailsActivity.EXTRA_ID, id)
                if (Build.VERSION.SDK_INT >= 21) {
                    val p1 = Pair.create<View, String>(vh.icon, "icon")
                    val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                        context, p1
                    )
                    context.startActivityForResult(intent, 1, options.toBundle())
                } else {
                    context.startActivityForResult(intent, 1)
                }
            }
        }
        return vh
    }

    override fun onBindViewHolder(vh: BrowseViewHolder, position: Int) {
        val item = data[position]
        if (iconCache.containsKey(item.packageName) && iconCache[item.packageName] != null) {
            vh.icon.setImageDrawable(iconCache[item.packageName])
        } else {
            vh.icon.setImageResource(R.mipmap.ic_launcher)
        }
        vh.item.tag = "" + item.id
        vh.name.text = item.appName
        if (item.preview?.length == 0) {
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
                DatabaseHelper.PostedEntry.COLUMN_NAME_FAVORITE + "= ?", arrayOf(1.toString()),
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

    private inner class DataItem internal constructor(
        context: Context?,
        val id: Long,
        str: String?
    ) {
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
                val json = JSONObject(str)
                packageName = json.getString("packageName")
                appName = getAppNameFromPackage(
                    context!!, packageName, false
                )
                text = str
                val title = json.optString("title")
                val text = json.optString("text")
                preview = """$title
$text""".trim { it <= ' ' }
                if (!iconCache.containsKey(packageName)) {
                    iconCache[packageName] = getAppIconFromPackage(
                        context, packageName
                    )
                }
                date = format.format(json.optLong("systemTime"))
                showDate = true
            } catch (e: JSONException) {
                if (Const.DEBUG) e.printStackTrace()
            }
        }
    }

    companion object {
        private const val LIMIT = Int.MAX_VALUE
        private const val PAGE_SIZE = "20"
    }

    init {
        loadMore(Int.MAX_VALUE.toLong())
    }
}