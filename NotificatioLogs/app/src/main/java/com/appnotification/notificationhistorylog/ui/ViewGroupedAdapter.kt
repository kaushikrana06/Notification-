package com.appnotification.notificationhistorylog.ui

import android.app.Activity
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.appnotification.notificationhistorylog.R
import com.appnotification.notificationhistorylog.misc.Util.getAppIconFromPackage
import java.text.DateFormat
import java.util.*

class ViewGroupedAdapter(private val context: Activity, private val data: ArrayList<HelperObject>) :
    RecyclerView.Adapter<BrowseViewHolder>() {
    private val iconCache: HashMap<String?, Drawable?> = HashMap()
    private val format = DateFormat.getDateInstance(DateFormat.DEFAULT, Locale.getDefault())
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BrowseViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_stats, parent, false)
        return BrowseViewHolder(view)
    }

    override fun onBindViewHolder(vh: BrowseViewHolder, position: Int) {
        val item = data[position]
        if (iconCache[item.packageName] != null && iconCache.containsKey(item.packageName)) {
            vh.icon.setImageDrawable(iconCache[item.packageName])
        } else {
            vh.icon.setImageResource(R.mipmap.ic_launcher)
        }
        vh.name.text = item.title
        vh.text.text = item.packageName
        vh.preview.text =
            context.getString(R.string.total_notification) + " " + item.notificationCount
    }

    override fun getItemCount(): Int {
        return data.size
    }

    init {
        for (itm in data) {
            if (!iconCache.containsKey(itm.packageName)) {
                iconCache[itm.packageName] = getAppIconFromPackage(
                    context, itm.packageName
                )
            }
        }
    }
}