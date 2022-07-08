package com.appnotification.notificationhistorylog

import android.content.ContentValues
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.BaseColumns
import android.text.format.DateUtils
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.appnotification.notificationhistorylog.misc.Const
import com.appnotification.notificationhistorylog.misc.DatabaseHelper
import com.appnotification.notificationhistorylog.misc.Util.getAppIconFromPackage
import com.appnotification.notificationhistorylog.misc.Util.getAppNameFromPackage
import org.json.JSONException
import org.json.JSONObject
import java.text.DateFormat
import java.util.*

class NewDetailsActivity : AppCompatActivity() {
    var json: JSONObject? = null
    var titleText: String? = null
    var contentText: String? = null
    var date: String? = null
    var menu1: Menu? = null
    private var id: String? = null
    private val doDelete = DialogInterface.OnClickListener { dialog: DialogInterface?, which: Int ->
        var affectedRows = 0
        try {
            val databaseHelper = DatabaseHelper(this)
            val db = databaseHelper.writableDatabase
            affectedRows = db.delete(
                DatabaseHelper.PostedEntry.TABLE_NAME,
                BaseColumns._ID + " = ?", arrayOf(id)
            )
            db.close()
            databaseHelper.close()
        } catch (e: Exception) {
            if (Const.DEBUG) e.printStackTrace()
        }
        if (affectedRows > 0) {
            val data = Intent()
            data.putExtra(EXTRA_ACTION, ACTION_REFRESH)
            setResult(RESULT_OK, data)
            finish()
        }
    }
    private var packageName= null
    private var appUid = 0
    private var dialog: AlertDialog? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_details)
        val intent = intent
        if (intent != null) {
            id = intent.getStringExtra(EXTRA_ID)
            if (id != null) {
                loadDetails(id!!)
            } else {
                finishWithToast()
            }
        } else {
            finishWithToast()
        }
    }

    override fun onPause() {
        if (dialog != null && dialog!!.isShowing) {
            dialog!!.dismiss()
            dialog = null
        }
        super.onPause()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.details, menu)
        if (intent.getStringExtra("EXTRA_INFO") == "hide") {
            menu.findItem(R.id.menu_favorite_remove).isVisible = false
            menu.findItem(R.id.menu_favorite).isVisible = false
        } else {
            menu.findItem(R.id.menu_favorite_remove).isVisible = false
            menu.findItem(R.id.menu_favorite).isVisible = true
        }
        menu1 = menu
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_delete -> {
                confirmDelete()
            }
            R.id.menu_favorite -> {
                item.isVisible = false
                menu1!!.findItem(R.id.menu_favorite_remove).isVisible = true
                addToFavorites()
                Log.d("MENU OPT", "favorite")
            }
            R.id.menu_favorite_remove -> {
                item.isVisible = false
                menu1!!.findItem(R.id.menu_favorite).isVisible = true
                removeFavorites()
                Log.d("MENU OPT", "favorite remove")
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun removeFavorites() {
        Log.v("METHOD", "favorite remove")
        val helper = DatabaseHelper(this)
        val db = helper.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(DatabaseHelper.PostedEntry.COLUMN_NAME_FAVORITE, 0)
        db.update(DatabaseHelper.PostedEntry.TABLE_NAME, contentValues, "_ID=$id", null)
        Toast.makeText(this@NewDetailsActivity, "Removed from favorites", Toast.LENGTH_SHORT).show()
    }

    private fun addToFavorites() {
        val helper = DatabaseHelper(this)
        val db = helper.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(DatabaseHelper.PostedEntry.COLUMN_NAME_FAVORITE, 1)
        db.update(DatabaseHelper.PostedEntry.TABLE_NAME, contentValues, "_ID=$id", null)
        db.close()
        helper.close()
        Toast.makeText(this@NewDetailsActivity, "Added to favorites", Toast.LENGTH_SHORT).show()
    }

    private fun loadDetails(id: String) {
        var str: String? = "error"
        try {
            val databaseHelper = DatabaseHelper(this)
            val db = databaseHelper.readableDatabase
            val cursor = db.query(
                DatabaseHelper.PostedEntry.TABLE_NAME, arrayOf(
                    DatabaseHelper.PostedEntry.COLUMN_NAME_CONTENT
                ),
                BaseColumns._ID + " = ?", arrayOf(
                    id
                ),
                null,
                null,
                null,
                "1"
            )
            if (cursor != null && cursor.count == 1 && cursor.moveToFirst()) {
                try {
                    json = JSONObject(cursor.getString(0))
                    str = json!!.toString(2)
                } catch (e: JSONException) {
                    if (Const.DEBUG) e.printStackTrace()
                }
                cursor.close()
            }
            db.close()
            databaseHelper.close()
        } catch (e: Exception) {
            if (Const.DEBUG) e.printStackTrace()
        }
        val tvJSON = findViewById<TextView>(R.id.json)
        tvJSON.text = str
        val card = findViewById<CardView>(R.id.card)
        val buttons = findViewById<CardView>(R.id.buttons)
        if (json != null) {
            packageName = json!!.optString("packageName", "???") as Nothing?
            titleText = json!!.optString("title")
            contentText = json!!.optString("text")
            val text = """$titleText
$contentText""".trim { it <= ' ' }
            if ("" != text) {
                card.visibility = View.VISIBLE
                val icon = findViewById<ImageView>(R.id.icon)
                icon.setImageDrawable(getAppIconFromPackage(this, packageName))
                val tvName = findViewById<TextView>(R.id.name)
                tvName.text = getAppNameFromPackage(this, packageName, false)
                val tvText = findViewById<TextView>(R.id.text)
                tvText.text = text
                val tvDate = findViewById<TextView>(R.id.date)
                if (SHOW_RELATIVE_DATE_TIME) {
                    tvDate.text = DateUtils.getRelativeDateTimeString(
                        this,
                        json!!.optLong("systemTime"),
                        DateUtils.MINUTE_IN_MILLIS,
                        DateUtils.WEEK_IN_MILLIS,
                        0
                    )
                } else {
                    val format = DateFormat.getDateTimeInstance(
                        DateFormat.DEFAULT,
                        DateFormat.SHORT,
                        Locale.getDefault()
                    )
                    tvDate.text = format.format(json!!.optLong("systemTime"))
                }
                try {
                    val app = packageName?.let { this.packageManager.getApplicationInfo(it, 0) }
                    buttons.visibility = View.VISIBLE
                    appUid = app!!.uid
                } catch (e: PackageManager.NameNotFoundException) {
                    if (Const.DEBUG) e.printStackTrace()
                    buttons.visibility = View.GONE
                }
            } else {
                card.visibility = View.GONE
            }
        } else {
            card.visibility = View.GONE
            buttons.visibility = View.GONE
        }
    }

    private fun finishWithToast() {
        Toast.makeText(this, R.string.details_error, Toast.LENGTH_SHORT).show()
        finish()
    }

    private fun confirmDelete() {
        if (dialog != null && dialog!!.isShowing) {
            dialog!!.dismiss()
        }
        val dialog = AlertDialog.Builder(this, R.style.DialogTheme)
            .setTitle(R.string.delete_dialog_title)
            .setMessage(R.string.delete_dialog_text)
            .setPositiveButton(R.string.delete_dialog_yes, doDelete)
            .setNegativeButton(R.string.delete_dialog_no, null)
            .show()
        val positive = dialog.getButton(DialogInterface.BUTTON_POSITIVE)
        positive.setTextColor(resources.getColor(R.color.colorText))
        val neutral = dialog.getButton(DialogInterface.BUTTON_NEGATIVE)
        neutral.setTextColor(resources.getColor(R.color.colorText))
    }

    fun openNotificationSettings(v: View?) {
        try {
            val intent = Intent()
            intent.action = "android.settings.APP_NOTIFICATION_SETTINGS"
          //  intent.putExtra("android.provider.extra.APP_PACKAGE", packageName)
            startActivity(intent)
        } catch (e: Exception) {
            if (Const.DEBUG) e.printStackTrace()
        }
    }

    companion object {
        const val EXTRA_ID = "id"
        const val EXTRA_ACTION = "action"
        const val ACTION_REFRESH = "refresh"
        private const val SHOW_RELATIVE_DATE_TIME = true
    }
}