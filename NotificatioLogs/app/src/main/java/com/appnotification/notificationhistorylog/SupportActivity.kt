package com.appnotification.notificationhistorylog

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity

class SupportActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_support)
        supportActionBar!!.title = "Help"
        setSpinners()
    }

    fun setSpinners() {
        val arrayHelpSpinner = arrayOf(
            "Technical Support", "Call support"
        )
        val arrayUrgencySpinner = arrayOf(
            "Low", "High"
        )
        val s = findViewById<Spinner>(R.id.help_spinner)
        val s1 = findViewById<Spinner>(R.id.urgency_spinner)
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item, arrayHelpSpinner
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        s.adapter = adapter
        val adapter1 = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item, arrayUrgencySpinner
        )
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        s1.adapter = adapter1
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_help, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return super.onOptionsItemSelected(item)
    }
}