package com.appnotification.notificationhistorylog.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.graphics.drawable.Drawable
import android.os.Handler
import android.os.SystemClock
import android.provider.BaseColumns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.recyclerview.widget.RecyclerView
import com.appnotification.notificationhistorylog.DbHelperIdeas
import com.appnotification.notificationhistorylog.R
import com.appnotification.notificationhistorylog.misc.Const
import com.appnotification.notificationhistorylog.misc.DatabaseHelper
import com.appnotification.notificationhistorylog.misc.Util.getAppIconFromPackage
import com.appnotification.notificationhistorylog.misc.Util.getAppNameFromPackage
import com.appnotification.notificationhistorylog.ui.DetailsActivity
import org.json.JSONException
import org.json.JSONObject
import timber.log.Timber
import java.text.DateFormat
import java.util.*

class FavoriteAdapter internal constructor(private val context: Activity, flag: Boolean) :
    RecyclerView.Adapter<FavoriteViewHolder>() {
    private val format = DateFormat.getDateInstance(DateFormat.DEFAULT, Locale.getDefault())
    private val data = ArrayList<DataItem>()
    private val iconCache = HashMap<String?, Drawable?>()
    private val handler = Handler()
    private var flag = false
    private var lastDate: String? = ""
    private var shouldLoadMore = true
    private var lastClickTime: Long = 0
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.favorite_item, parent, false)
        val vh = FavoriteViewHolder(view)
        vh.container.setOnClickListener { v: View ->
            // preventing double, using threshold of 1000 ms
            if (SystemClock.elapsedRealtime() - lastClickTime < 1000) {
                return@setOnClickListener
            }
            lastClickTime = SystemClock.elapsedRealtime()
            val id = v.tag as String
            val intent = Intent(context, DetailsActivity::class.java)
            intent.putExtra(DetailsActivity.EXTRA_ID, id)
            val p1 = Pair.create<View, String>(vh.appIcon, "icon")
            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                context, p1
            )
            context.startActivityForResult(intent, 1, options.toBundle())
        }
        return vh
    }

    override fun onBindViewHolder(vh: FavoriteViewHolder, position: Int) {
        val item = data[position]
        vh.remove.setOnClickListener { v: View ->
            val id = v.tag as String
            deleteFromFavorite(position)
        }
        if (iconCache.containsKey(item.packageName) && iconCache[item.packageName] != null) {
            vh.appIcon.setImageDrawable(iconCache[item.packageName])
        } else {
            vh.appIcon.setImageResource(R.mipmap.ic_launcher)
        }
        vh.container.tag = "" + item.id
        vh.remove.tag = "" + item.id
        vh.appName.text = item.appName
        if (item.preview?.length == 0) {
            vh.preview.visibility = View.GONE
            vh.title.visibility = View.VISIBLE
            vh.title.text = item.text
        } else {
            vh.title.visibility = View.GONE
            vh.preview.visibility = View.VISIBLE
            vh.preview.text = item.preview
        }
        vh.date.text = item.date

//        vh.date.setVisibility(View.GONE);
        if (position == itemCount - 1) {
            loadMore(item.id)
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun loadMore(afterId: Long) {
        if (!shouldLoadMore) {
            if (Const.DEBUG) println("not loading more items")
            return
        }
        val dbHelper = DbHelperIdeas(context)
        for (i in dbHelper.taskList.indices) {
            Timber.i(dbHelper.taskList[i])
        }
        Timber.e("------------------------")
        Timber.e("------------------------")
        if (Const.DEBUG) println("loading more items")
        val before = itemCount
        try {
            val databaseHelper = DatabaseHelper(context)
            val db = databaseHelper.readableDatabase
            val cursor: Cursor? = db.query(
                DatabaseHelper.PostedEntry.TABLE_NAME,
                arrayOf(
                    BaseColumns._ID,
                    DatabaseHelper.PostedEntry.COLUMN_NAME_CONTENT
                ),
                DatabaseHelper.PostedEntry.COLUMN_NAME_FAVORITE + "= ? AND " + BaseColumns._ID + " < ?",
                arrayOf("" + 1, "" + afterId),
                null,
                null,
                BaseColumns._ID + " DESC",
                PAGE_SIZE
            )
            Timber.i("CursorCount: %s", cursor?.count.toString())
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

    fun deleteFromFavorite(position: Int) {
        val id = data[position].id
        val helper = DatabaseHelper(context)
        val db = helper.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(DatabaseHelper.PostedEntry.COLUMN_NAME_FAVORITE, 0)
        db.update(DatabaseHelper.PostedEntry.TABLE_NAME, contentValues, "_ID=$id", null)
        data.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, data.size)
        db.close()
        Toast.makeText(context, context.getString(R.string.message_removed), Toast.LENGTH_SHORT)
            .show()
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
                Timber.i(json.toString())
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
        this.flag = flag
        loadMore(Int.MAX_VALUE.toLong())
    }
}