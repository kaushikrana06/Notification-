package com.appnotification.notificationhistorylog.misc

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.database.Cursor
import android.os.AsyncTask
import android.os.Build
import android.provider.BaseColumns
import android.view.View
import androidx.core.content.FileProvider
import com.appnotification.notificationhistorylog.BuildConfig
import com.appnotification.notificationhistorylog.R
import com.google.android.material.snackbar.Snackbar
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

 class ExportTask(private val context: Context, private val view: View) :
    AsyncTask<Void?, Void?, Void?>() {
    private var snackbar: Snackbar? = null
    @Deprecated("Deprecated in Java")
    override fun onPreExecute() {
        exporting = true
        snackbar = Snackbar.make(view, R.string.snackbar_export, Snackbar.LENGTH_INDEFINITE)
        snackbar?.show()
    }

    @Deprecated("Deprecated in Java")
    @SuppressLint("Range")
    protected override fun doInBackground(vararg p0: Void?): Void? {
        // Create share folder
        val exportPath = File(context.cacheDir, "share")
        if (!exportPath.exists()) {
            val mkdirsResult = exportPath.mkdirs()
            if (Const.DEBUG) {
                println("Share directory created: $mkdirsResult")
            }
        }

        // Clean up old exports
        val oldFiles = exportPath.listFiles()
        for (oldFile in oldFiles!!) {
            if (oldFile.isFile && oldFile.name.startsWith("notification_export")) {
                val deleteResult = oldFile.delete()
                if (Const.DEBUG) {
                    println("Existing cache file deleted: $deleteResult")
                }
            }
        }

        // Generate a file name
        val currentTime = System.currentTimeMillis()
        val exportDate =
            SimpleDateFormat("yyyyMMdd-HHmmss", Locale.getDefault()).format(Date(currentTime))
        val exportFileName = String.format(EXPORT_FILE_NAME, exportDate)

        // Create a new file
        val newFile = File(exportPath, exportFileName)
        try {
            // Start writing
            val outputStream = FileOutputStream(newFile)
            outputStream.write("{\n\n".toByteArray())

            // Device info
            outputStream.write("\"device\": ".toByteArray())
            val json = JSONObject()
            try {
                val time = System.currentTimeMillis()
                json.put("version", BuildConfig.VERSION_CODE)
                json.put(
                    "locale", Util.getLocale(
                        context
                    )
                )
                json.put("model", Build.MODEL)
                json.put("device", Build.DEVICE)
                json.put("product", Build.PRODUCT)
                json.put("manufacturer", Build.MANUFACTURER)
                json.put("sdk", Build.VERSION.SDK_INT)
                json.put("timezone", TimeZone.getDefault().id)
                json.put("offset", TimeZone.getDefault().getOffset(time))
                json.put("exportTime", time)
                val pm = context.packageManager
                val packages = pm.getInstalledApplications(PackageManager.GET_META_DATA)
                val array = JSONArray()
                for (packageInfo in packages) {
                    val obj = JSONObject()
                    val isSystemApp = packageInfo.flags and ApplicationInfo.FLAG_SYSTEM != 0
                    /*obj.put("appName",     Util.getAppNameFromPackage(context, packageInfo.packageName, false));
                    obj.put("packageName", packageInfo.packageName);
                    obj.put("enabled",     packageInfo.enabled);
                    obj.put("system",      isSystemApp);*/array.put(obj)
                }
                json.put("apps", array)
            } catch (e: JSONException) {
                if (Const.DEBUG) e.printStackTrace()
            }
            outputStream.write(json.toString().toByteArray())
            outputStream.write(",\n\n".toByteArray())

            // Posted and removed notifications
            val dbHelper = DatabaseHelper(context)
            val db = dbHelper.readableDatabase
            var c: Cursor?
            val projectionPosted = arrayOf(
                BaseColumns._ID,
                DatabaseHelper.PostedEntry.COLUMN_NAME_CONTENT
            )
            val sortOrderPosted: String = BaseColumns._ID + " ASC"
            val projectionRemoved = arrayOf(
                BaseColumns._ID,
                DatabaseHelper.RemovedEntry.COLUMN_NAME_CONTENT
            )
            val sortOrderRemoved: String = BaseColumns._ID + " ASC"
            outputStream.write("\"posted\": [\n".toByteArray())
            c = db.query(
                DatabaseHelper.PostedEntry.TABLE_NAME,
                projectionPosted,
                null,
                null,
                null,
                null,
                sortOrderPosted
            )
            if (c != null) {
                c.moveToFirst()
                for (i in 0 until c.count) {
                    val content =
                        c.getString(c.getColumnIndex(DatabaseHelper.PostedEntry.COLUMN_NAME_CONTENT))
                    outputStream.write("\t".toByteArray())
                    outputStream.write(content.toByteArray())
                    if (i == c.count - 1) {
                        outputStream.write("\n".toByteArray())
                    } else {
                        outputStream.write(",\n".toByteArray())
                    }
                    c.moveToNext()
                }
                c.close()
            }
            outputStream.write("],\n\n".toByteArray())
            outputStream.write("\"removed\": [\n".toByteArray())
            c = db.query(
                DatabaseHelper.RemovedEntry.TABLE_NAME,
                projectionRemoved,
                null,
                null,
                null,
                null,
                sortOrderRemoved
            )
            if (c != null) {
                c.moveToFirst()
                for (i in 0 until c.count) {
                    val content =
                        c.getString(c.getColumnIndex(DatabaseHelper.RemovedEntry.COLUMN_NAME_CONTENT))
                    outputStream.write("\t".toByteArray())
                    outputStream.write(content.toByteArray())
                    if (i == c.count - 1) {
                        outputStream.write("\n".toByteArray())
                    } else {
                        outputStream.write(",\n".toByteArray())
                    }
                    c.moveToNext()
                }
                c.close()
            }
            outputStream.write("]\n\n}".toByteArray())
            outputStream.close()
        } catch (e: Exception) {
            if (Const.DEBUG) e.printStackTrace()
        }

        // Get the content provider URI
        val contentUri =
            FileProvider.getUriForFile(context, "org.hcilab.projects.nlogx.fileprovider", newFile)

        // Open the share dialog
        val sharingIntent = Intent(Intent.ACTION_SEND)
        sharingIntent.type = "text/plain"
        sharingIntent.putExtra(Intent.EXTRA_STREAM, contentUri)
        context.startActivity(
            Intent.createChooser(
                sharingIntent,
                context.resources.getString(R.string.menu_export)
            )
        )
        return null
    }

    @Deprecated("Deprecated in Java")
    override fun onPostExecute(aVoid: Void?) {
        if (snackbar != null && snackbar!!.isShownOrQueued) {
            snackbar?.dismiss()
        }
        exporting = false
    }

    companion object {
        private const val EXPORT_FILE_NAME = "notification_export_%s.txt"
        @JvmField
        var exporting = false
    }
}