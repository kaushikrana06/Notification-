package com.appnotification.notificationhistorylog

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.appnotification.notificationhistorylog.ui.BrowseActivity
import timber.log.Timber

class FavActivity : AppCompatActivity() {
    var dbHelper: DbHelperIdeas? = null
    var mAdapter: ArrayAdapter<String?>? = null
    var lstTask: ListView? = null
    var view1: View? = null
    var view2: View? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fav)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        val backButton = toolbar.findViewById<ImageView>(R.id.back_image)
        val titleTextView = toolbar.findViewById<TextView>(R.id.title_text)
        val searchButton = toolbar.findViewById<ImageView>(R.id.search_image)
        val menuButton = toolbar.findViewById<ImageView>(R.id.menu_image)
        searchButton.visibility = View.GONE
        menuButton.visibility = View.INVISIBLE
        titleTextView.text = getString(R.string.favorites)
        backButton.setOnClickListener { finish() }
        dbHelper = DbHelperIdeas(this)
        lstTask = findViewById(R.id.lstTaskidea)
        loadTaskList()
        doFirstRun()
    }

    private fun loadTaskList() {
        val taskList = dbHelper?.taskList
        if (mAdapter == null) {
            if (taskList != null) {
                mAdapter = ArrayAdapter(this, R.layout.row, R.id.task_title, taskList.toList())
            }
            lstTask!!.adapter = mAdapter
        } else {
            mAdapter!!.clear()
            mAdapter!!.addAll(taskList!!)
            mAdapter!!.notifyDataSetChanged()
        }
    }

    fun deleteTask(view: View) {
        val parent = view.parent as View
        val taskTextView = parent.findViewById<TextView>(R.id.task_title)
        Timber.e(taskTextView.text as String)
        val task = taskTextView.text.toString()
        dbHelper!!.deleteTask(task)
        loadTaskList()
    }

    private fun doFirstRun() {
        val settings = getSharedPreferences("FIRSTRUNTEXT2", MODE_PRIVATE)
        if (settings.getBoolean("isFirstRunDialogBoxtext2", true)) {
            val editor = settings.edit()
            editor.putBoolean("isFirstRunDialogBoxtext2", false)
            editor.commit()
            val builder = AlertDialog.Builder(this@FavActivity, R.style.DialogTheme)
            builder.setIcon(R.drawable.notificationlogo)
            builder.setTitle("Add Notifications To Favorite  !")
            builder.setMessage("You Can Later Read Them  ")
            builder.setCancelable(false)
            builder.setPositiveButton("Start Adding ") { dialogInterface, i ->
                val startIntent = Intent(this@FavActivity, BrowseActivity::class.java)
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
}
