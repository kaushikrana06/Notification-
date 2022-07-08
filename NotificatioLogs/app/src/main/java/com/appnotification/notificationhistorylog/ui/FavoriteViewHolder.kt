package com.appnotification.notificationhistorylog.ui

import android.view.View
import android.widget.Button
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import androidx.constraintlayout.widget.ConstraintLayout
import android.widget.TextView
import com.appnotification.notificationhistorylog.R

class FavoriteViewHolder internal constructor(view: View) : RecyclerView.ViewHolder(view) {
    var container: ConstraintLayout
    var appIcon: ImageView
    var appName: TextView
    var title: TextView
    var preview: TextView
    var date: TextView
    var remove: Button

    init {
        container = view.findViewById(R.id.container)
        appIcon = view.findViewById(R.id.app_icon)
        appName = view.findViewById(R.id.app_name)
        title = view.findViewById(R.id.message_title)
        preview = view.findViewById(R.id.message_preview)
        date = view.findViewById(R.id.msg_date)
        remove = view.findViewById(R.id.button_remove)
    }
}