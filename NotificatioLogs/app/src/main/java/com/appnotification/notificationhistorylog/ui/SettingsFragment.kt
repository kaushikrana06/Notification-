package com.appnotification.notificationhistorylog.ui

import android.app.AlertDialog
import android.content.*
import android.database.DatabaseUtils
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.appnotification.notificationhistorylog.CommonCl.SharedCommon
import com.appnotification.notificationhistorylog.CommonCl.SharedCommon.getPreferencesInt
import com.appnotification.notificationhistorylog.CommonCl.SharedCommon.keyopenrateall
import com.appnotification.notificationhistorylog.CommonCl.SharedCommon.putPreferencesInt
import com.appnotification.notificationhistorylog.R
import com.appnotification.notificationhistorylog.misc.Const
import com.appnotification.notificationhistorylog.misc.DatabaseHelper
import com.appnotification.notificationhistorylog.misc.Util.isNotificationAccessEnabled
import com.appnotification.notificationhistorylog.service.NotificationHandler
//import com.appnotification.notificationhistorylog.ui.SettingsFragment

class SettingsFragment : PreferenceFragmentCompat() {
    var tx: TextView? = null
    private var dbHelper: DatabaseHelper? = null
    private var updateReceiver: BroadcastReceiver? = null
    private var prefStatus: Preference? = null
    private val prefText: Preference? = null
    private val prefOngoing: Preference? = null
    private var prefEntries: Preference? = null
    private val SettingsFragment: Any? = null
    private val prefFavorites: Preference? = null
    override fun onCreatePreferences(bundle: Bundle, s: String) {
        addPreferencesFromResource(R.xml.preferences)
        val orall = getPreferencesInt(activity, keyopenrateall, 0)
        val pm = preferenceManager
        /*getListView().setBackgroundColor(Color.TRANSPARENT);
        getListView().setBackgroundColor(Color.rgb(4, 26, 55));*/prefStatus = pm.findPreference(
            Const.PREF_STATUS
        )
        prefStatus?.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            startActivity(Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"))
            true
        }
        pm.findPreference<Preference>(Const.PREF_BROWSE)!!.onPreferenceClickListener =
            Preference.OnPreferenceClickListener {
                val scor = SharedCommon
                var orfavc = getPreferencesInt(activity, keyopenrateall, 0)
                orfavc++
                putPreferencesInt(activity, SharedCommon.keyopenrateall, orfavc)
                startActivity(Intent(activity, BrowseActivity::class.java))
                true
            }


        // prefText = pm.findPreference(Const.PREF_TEXT);
        //  prefOngoing = pm.findPreference(Const.PREF_ONGOING);
        prefEntries = pm.findPreference(Const.PREF_ENTRIES)


        // pm.findPreference(Const.PREF_VERSION).setSummary(BuildConfig.VERSION_NAME + (Const.DEBUG ? " dev" : ""));
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            dbHelper = DatabaseHelper(activity)
        } catch (e: Exception) {
            if (Const.DEBUG) e.printStackTrace()
        }
        updateReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                update()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (isNotificationAccessEnabled(
                requireActivity()
            )
        ) {
            prefStatus?.setSummary(R.string.settings_notification_access_enabled)

            //prefText.setEnabled(true);
            // prefOngoing.setEnabled(true);
        } else {
            prefStatus?.setSummary(R.string.settings_notification_access_disabled)
            //prefText.setEnabled(false);
            //prefOngoing.setEnabled(false);
        }
        val filter = IntentFilter()
        filter.addAction(NotificationHandler.BROADCAST)
        LocalBroadcastManager.getInstance(requireActivity()).registerReceiver(updateReceiver!!, filter)
        update()
    }

    override fun onPause() {
        LocalBroadcastManager.getInstance(requireActivity()).unregisterReceiver(updateReceiver!!)
        super.onPause()
    }

    private fun update() {
        try {
            val db = dbHelper?.readableDatabase
            val numRowsPosted =
                DatabaseUtils.queryNumEntries(db, DatabaseHelper.PostedEntry.TABLE_NAME)
            prefEntries?.summary = "" + numRowsPosted
            val nnn = numRowsPosted.toString()
            val pref = requireActivity().getPreferences(Context.MODE_PRIVATE)
            val edt = pref.edit()
            edt.putString("facebook_id", nnn)
            edt.commit()
            if (numRowsPosted >= 19000) {
                val n = numRowsPosted.toInt()
                val nn = "" + n
                overnotificationmessage(n, nn)
            }
            val numRowsFavorite = DatabaseUtils.queryNumEntries(
                db, DatabaseHelper.PostedEntry.TABLE_NAME,
                DatabaseHelper.PostedEntry.COLUMN_NAME_FAVORITE + "=?", arrayOf(1.toString())
            )
            val stringResource1 =
                if (numRowsFavorite == 1L) R.string.settings_browse_summary_singular else R.string.settings_browse_summary_plural
            prefFavorites?.summary = getString(stringResource1, numRowsFavorite)
        } catch (e: Exception) {
            if (Const.DEBUG) e.printStackTrace()
        }
    }

    private fun overnotificationmessage(n: Int, nn: String) {


        // Toast.makeText(getActivity(), "", Toast.LENGTH_SHORT).show();
        val builder = AlertDialog.Builder(activity, R.style.DialogTheme)
        builder.setIcon(R.drawable.ic_splash_logo)
        builder.setTitle("Note ")
        builder.setMessage("You Have More Than 10,000 Notifications Logged, We Request You To Clear Notifications So That App Can Run Smoothly ")
        builder.setCancelable(true)
        builder.setNeutralButton("DISMISS") { dialogInterface, i -> dialogInterface.dismiss() }
        builder.setPositiveButton("CLEAR") { dialogInterface, i ->
            try {
                val dbHelper = DatabaseHelper(activity)
                val db = dbHelper.writableDatabase
                db.execSQL(DatabaseHelper.SQL_DELETE_ENTRIES_POSTED)
                db.execSQL(DatabaseHelper.SQL_CREATE_ENTRIES_POSTED)
                db.execSQL(DatabaseHelper.SQL_DELETE_ENTRIES_REMOVED)
                db.execSQL(DatabaseHelper.SQL_CREATE_ENTRIES_REMOVED)
                val local = Intent()
                local.action = NotificationHandler.BROADCAST
                LocalBroadcastManager.getInstance(requireActivity()).sendBroadcast(local)
                val startIntentr = Intent(activity, NewMainActivity::class.java)
                startActivity(startIntentr)
            } catch (e: Exception) {
                if (Const.DEBUG) e.printStackTrace()
            }
            Toast.makeText(activity, "Cleared !", Toast.LENGTH_LONG).show()
            dialogInterface.dismiss()
        }
        val dialog = builder.create()
        dialog.show()
        val positive = dialog.getButton(DialogInterface.BUTTON_POSITIVE)
        positive.setTextColor(resources.getColor(R.color.colorText))
        val negative = dialog.getButton(DialogInterface.BUTTON_NEUTRAL)
        negative.setTextColor(resources.getColor(R.color.colorText))
    }

    companion object {
        val TAG = SettingsFragment::class.java.name
    }
}