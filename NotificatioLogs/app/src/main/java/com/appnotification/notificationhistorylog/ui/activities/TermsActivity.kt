package com.appnotification.notificationhistorylog.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import com.appnotification.notificationhistorylog.R
import android.widget.TextView
import androidx.appcompat.widget.Toolbar

class TermsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_terms)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        val backButton = toolbar.findViewById<ImageView>(R.id.back_image)
        val titleTextView = toolbar.findViewById<TextView>(R.id.title_text)
        val searchButton = toolbar.findViewById<ImageView>(R.id.search_image)
        val menuButton = toolbar.findViewById<ImageView>(R.id.menu_image)
        searchButton.visibility = View.GONE
        menuButton.visibility = View.INVISIBLE
        titleTextView.text = getString(R.string.settings_tnc)
        backButton.setOnClickListener { finish() }
    }
}