package com.appnotification.notificationhistorylog.ui

import android.view.View
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import android.widget.TextView
import android.widget.RelativeLayout
import android.widget.LinearLayout
import com.appnotification.notificationhistorylog.R

class GmailViewHolder internal constructor(view: View) : RecyclerView.ViewHolder(view) {
    var icon: ImageView
    var name: TextView
    var preview: TextView
    var text: TextView
    var date: TextView
    var option_text: TextView? = null
    var viewBackground_delete: RelativeLayout
    var viewForeground: RelativeLayout
    var item: LinearLayout

    init {
        item = view.findViewById(R.id.item)
        icon = view.findViewById(R.id.icon)
        name = view.findViewById(R.id.name)
        preview = view.findViewById(R.id.preview)
        text = view.findViewById(R.id.text)
        date = view.findViewById(R.id.date)
        viewBackground_delete = view.findViewById(R.id.view_background_2)
        viewForeground = view.findViewById(R.id.view_foreground)
        view.findViewById<View>(R.id.view_background).visibility = View.GONE
    }
}