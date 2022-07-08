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
import org.json.JSONObject
import timber.log.Timber
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

internal class GmailAdapter(private val context: Activity, private val packageName: String) :
    RecyclerView.Adapter<BrowseViewHolder>() {
    private val format = DateFormat.getDateInstance(DateFormat.DEFAULT, Locale.getDefault())
    private val iconCache: HashMap<String, Drawable?>
    private val handler = Handler()
    private var data: ArrayList<DataItem>
    private var lastDate = ""
    private var shouldLoadMore = true
    fun deleteItem(position: Int) {
        val id = data[position].id
        Timber.i("delete favorite")
        val helper = DatabaseHelper(context)
        val db = helper.writableDatabase
        db.delete(DatabaseHelper.PostedEntry.TABLE_NAME, "_ID=$id", null)
        data.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, data.size)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BrowseViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_browse, parent, false)
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
        if (iconCache[item.packageName] != null && iconCache.containsKey(item.packageName)) {
            vh.icon.setImageDrawable(iconCache[item.packageName])
        } else {
            val appIcon = getAppIconFromPackage(
                context, item.packageName
            )
            if (appIcon != null) vh.icon.setImageDrawable(appIcon) else vh.icon.setImageResource(R.mipmap.ic_launcher)
        }
        vh.item.tag = "" + item.id
        vh.name.text = item.appName
        vh.time.text = item.time
        if (item.preview.length == 0) {
            vh.preview.visibility = View.GONE
            vh.text.visibility = View.VISIBLE
            vh.text.text = item.text
        } else {
            vh.text.visibility = View.GONE
            vh.preview.visibility = View.VISIBLE
            vh.preview.text = item.preview
        }
        Timber.i("onBindViewHolder: " + item.shouldShowDate() + "  " + item.date)
        if (item.shouldShowDate()) {
            vh.date.visibility = View.VISIBLE
            vh.date.text = item.date
        } else {
            vh.date.visibility = View.GONE
        }
        if (position == itemCount - 1) {
            loadMore(packageName, item.id)
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    fun filterList(filteredList: ArrayList<DataItem>) {
        data = filteredList
        notifyDataSetChanged()
    }

    private fun loadMore(packageName: String, afterId: Long) {
        if (!shouldLoadMore) {
            if (Const.DEBUG) println("not loading more items")
            return
        }
        if (Const.DEBUG) println("loading more items")
        val before = itemCount
        val databaseHelper = DatabaseHelper(context)
        val db = databaseHelper.readableDatabase
        try {
            val cursor = db.query(
                DatabaseHelper.PostedEntry.TABLE_NAME,
                arrayOf(
                    BaseColumns._ID,
                    DatabaseHelper.PostedEntry.COLUMN_NAME_CONTENT
                ),
                BaseColumns._ID + " < ?",
                arrayOf("" + afterId),
                null,
                null,
                BaseColumns._ID + " DESC",
                PAGE_SIZE
            )
            if (cursor != null && cursor.moveToFirst()) {
                for (i in 0 until cursor.count) {
                    try {
                        Timber.i("Column1: %s", cursor.getLong(0))
                        Timber.i("Column2: %s", cursor.getString(1))
                        val dataItem: DataItem = DataItem(
                            context, cursor.getLong(0), cursor.getString(1)
                        )
                        Timber.e(dataItem.packageName + " == " + packageName)
                        if (dataItem.packageName == packageName) {
                            val thisDate = dataItem.date
                            dataItem.setShowDate(lastDate != thisDate)
                            lastDate = thisDate
                            data.add(dataItem)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    cursor.moveToNext()
                }
                cursor.close()
            }
        } catch (e: Exception) {
            if (Const.DEBUG) e.printStackTrace()
        } finally {
            db.close()
            databaseHelper.close()
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
        for (item in data) {
            if (item.appName?.lowercase(Locale.getDefault())?.contains(text.lowercase(Locale.getDefault())) == true) {
                filteredList.add(item)
            }
        }
        return filteredList
    }

    inner class DataItem internal constructor(
        context: Context?,
        val id: Long,
        str: String
    ) {
        val packageName: String
        val appName: String?
        val text: String
        val preview: String
        val date: String
        val time: String
        private var showDate: Boolean
        fun shouldShowDate(): Boolean {
            return showDate
        }

        fun setShowDate(showDate: Boolean) {
            this.showDate = showDate
        }

        init {
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
            val df = SimpleDateFormat("HH:mm", Locale.US)
            time = df.format(json.optLong("systemTime"))
            showDate = false
        }
    }

    companion object {
        private const val LIMIT = Int.MAX_VALUE
        private const val PAGE_SIZE = "9999"
    }

    init {
        data = ArrayList()
        iconCache = HashMap()
        loadMore(packageName, Int.MAX_VALUE.toLong())
    }
}