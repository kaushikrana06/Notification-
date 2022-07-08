package com.appnotification.notificationhistorylog.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.appnotification.notificationhistorylog.R
import com.appnotification.notificationhistorylog.ui.items.AppItem

class AppsAdapter     // RecyclerView recyclerView;
    (private val apps: Array<AppItem?>, private val listener: OnAppClickListener) :
    RecyclerView.Adapter<AppsAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val listItem = layoutInflater.inflate(R.layout.app_item, parent, false)
        return ViewHolder(listItem)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val appItem = apps[position]
        holder.appName.text = appItem?.name
        appItem?.icon?.let { holder.appIcon.setImageResource(it) }
        holder.container.setOnClickListener { listener.onAppClick(appItem?.name, position) }
    }

    override fun getItemCount(): Int {
        return apps.size
    }

    interface OnAppClickListener {
        fun onAppClick(appName: String?, position: Int)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var appIcon: ImageView = itemView.findViewById<View>(R.id.app_icon) as ImageView
        var appName: TextView = itemView.findViewById<View>(R.id.app_name) as TextView
        var container: LinearLayout = itemView.findViewById<View>(R.id.app_item_container) as LinearLayout

    }
}