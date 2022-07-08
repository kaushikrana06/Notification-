package com.appnotification.notificationhistorylog

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.appnotification.notificationhistorylog.db.DbHelperGmail
import com.appnotification.notificationhistorylog.ui.BrowseActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import smartdevelop.ir.eram.showcaseviewlib.GuideView
import smartdevelop.ir.eram.showcaseviewlib.config.DismissType
import smartdevelop.ir.eram.showcaseviewlib.config.Gravity

class SelectedNotifActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    var dbHelper: DbHelperGmail? = null
    var mAdapter: ArrayAdapter<String>? = null
    var lstTask: ListView? = null
    var view1: View? = null
    var view2: View? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_selected_notif)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        val fab = findViewById<FloatingActionButton>(R.id.fab)
        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }
        dbHelper = DbHelperGmail(this)
        lstTask = findViewById(R.id.lsttakgmail)
        loadTaskList()
        doFirstRun()
        val drawer = findViewById<DrawerLayout>(R.id.drawer_layout)
        val navigationView = findViewById<NavigationView>(R.id.nav_view)
        val toggle = ActionBarDrawerToggle(
            this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawer.addDrawerListener(toggle)
        toggle.syncState()
        navigationView.setNavigationItemSelectedListener(this)
    }

    override fun onBackPressed() {
        val drawer = findViewById<DrawerLayout>(R.id.drawer_layout)
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.selected_notif, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId
        return if (id == R.id.action_settings) {
            true
        } else super.onOptionsItemSelected(item)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        val id = item.itemId
        if (id == R.id.nav_home) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {
        } else if (id == R.id.nav_slideshow) {
        } else if (id == R.id.nav_tools) {
        } else if (id == R.id.nav_share) {
        } else if (id == R.id.nav_send) {
        }
        val drawer = findViewById<DrawerLayout>(R.id.drawer_layout)
        drawer.closeDrawer(GravityCompat.START)
        return true
    }

    private fun loadTaskList() {
        val taskList = dbHelper!!.taskList
        if (mAdapter == null) {
            mAdapter = ArrayAdapter(this, R.layout.row, R.id.task_title, taskList)
            lstTask!!.adapter = mAdapter
        } else {
            mAdapter!!.clear()
            mAdapter!!.addAll(taskList)
            mAdapter!!.notifyDataSetChanged()
        }
    }

    fun deleteTask(view: View) {
        val parent = view.parent as View
        val taskTextView = parent.findViewById<TextView>(R.id.task_title)
        Log.e("String", (taskTextView.text as String))
        val task = taskTextView.text.toString()
        dbHelper!!.deleteTask(task)
        loadTaskList()
    }

    private fun doFirstRun() {
        val settings = getSharedPreferences("FIRSTRUNTEXT2", MODE_PRIVATE)
        if (settings.getBoolean("isFirstRunDialogBoxtext2", true)) {
            val editor = settings.edit()
            editor.putBoolean("isFirstRunDialogBoxtext2", false)
            editor.apply()
            val builder = AlertDialog.Builder(this@SelectedNotifActivity, R.style.DialogTheme)
            builder.setIcon(R.drawable.ic_splash_logo)
            builder.setTitle("Add Notifications To Favorite  !")
            builder.setMessage("You Can Later Read Them  ")
            builder.setCancelable(false)
            builder.setPositiveButton("Start Adding ") { dialogInterface, i ->
                val startIntent = Intent(this@SelectedNotifActivity, BrowseActivity::class.java)
                startActivity(startIntent)
            }
            builder.setNeutralButton("Got It ") { dialogInterface, i -> dialogInterface.dismiss() }
            val dialog = builder.create()
            dialog.show()
            val positive = dialog.getButton(DialogInterface.BUTTON_POSITIVE)
            positive.setTextColor(resources.getColor(R.color.colorText))
            val neutral = dialog.getButton(DialogInterface.BUTTON_NEUTRAL)
            neutral.setTextColor(resources.getColor(R.color.colorText))
        }
    }

    private fun showtipshowcase() {
        GuideView.Builder(this)
            .setTitle("Add Tasks")
            .setContentText("Click Here To Add Your Ideas")
            .setGravity(Gravity.auto) //optional
            .setDismissType(DismissType.outside) //optional - default DismissType.targetView
            .setTargetView(view1)
            .setContentTextSize(14) //optional
            .setTitleTextSize(16) //optional
            .build()
            .show()
    }
}