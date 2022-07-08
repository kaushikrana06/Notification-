package com.appnotification.notificationhistorylog.ui

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener
import com.appnotification.notificationhistorylog.BuildConfig
import com.appnotification.notificationhistorylog.CommonCl.SharedCommon
import com.appnotification.notificationhistorylog.CommonCl.SharedCommon.getPreferencesInt
import com.appnotification.notificationhistorylog.CommonCl.SharedCommon.key1
import com.appnotification.notificationhistorylog.CommonCl.SharedCommon.putPreferencesInt
import com.appnotification.notificationhistorylog.R
import com.appnotification.notificationhistorylog.ads.InterstitialAdsManager
import com.appnotification.notificationhistorylog.misc.Const
import com.appnotification.notificationhistorylog.misc.DatabaseHelper
import com.appnotification.notificationhistorylog.misc.ExportTask
import com.appnotification.notificationhistorylog.misc.Util.isNotificationAccessEnabled
import com.appnotification.notificationhistorylog.service.NotificationHandler
import com.facebook.ads.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.quinny898.library.persistentsearch.SearchBox
import timber.log.Timber

class BrowseActivity : AppCompatActivity(), OnRefreshListener, View.OnClickListener {
    var whatnew: String? = null
    var showallnotf: String? = null
    var shownotif: String? = null
    var showtuto: String? = null
    var showfav: String? = null
    var livenotice: String? = null
    var versionCode = BuildConfig.VERSION_CODE
    var versionName = BuildConfig.VERSION_NAME
    var isSearch: Boolean? = null
    var mAdapter: BrowseAdapter? = null
    var appid = BuildConfig.APPLICATION_ID
    var firebaseRemoteConfigprice: FirebaseRemoteConfig? = null
    private val search: SearchBox? = null
    private var recyclerView: RecyclerView? = null
    private var swipeRefreshLayout: SwipeRefreshLayout? = null
    private var mAdView: AdView? = null
    private val mAuth: FirebaseAuth? = null
    private var searchEdit: EditText? = null
    private var adListener: AdListener? = null
    private var backButton: ImageView? = null
    private var searchButton: ImageView? = null
    private var menuButton: ImageView? = null
    private var titleTextView: TextView? = null
    private fun search() {
        if (searchEdit?.visibility == View.VISIBLE) {
            searchEdit?.visibility = View.GONE
            searchEdit?.setText("")
            update()
            /* InputMethodManager imm = (InputMethodManager)getSystemService(
                    Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(searchEdit.getWindowToken(), 0);*/
        } else {
            searchEdit?.visibility = View.VISIBLE
            /* searchEdit.requestFocus();
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(searchEdit, InputMethodManager.SHOW_IMPLICIT);*/
        }
    }

    private fun checkadstatus() {
        run {
            firebaseRemoteConfigprice?.fetch(0)
                ?.addOnCompleteListener { task ->
                    Timber.i("info%s", firebaseRemoteConfigprice?.info?.lastFetchStatus)
                    Timber.i("firebaseremote%s", firebaseRemoteConfigprice?.getString("btn_text"))
                    if (task.isSuccessful) {
                        firebaseRemoteConfigprice?.fetchAndActivate()
                        whatnew = firebaseRemoteConfigprice?.getString("showads")
                        if (whatnew == "yes") {
                            showads()
                            //Toast.makeText(MainActivity.this, "Showing", Toast.LENGTH_SHORT).show();
                        } else if (whatnew == "no") {
                            mAdView?.visibility = View.GONE
                            // Toast.makeText(MainActivity.this, "Nope", Toast.LENGTH_SHORT).show();
                            Timber.i("AdsStatus Not Showing")
                        }
                        Timber.i(
                            "firebaseremote%s",
                            firebaseRemoteConfigprice?.getString("btn_text")
                        )
                    } else {
                        val exp = "" + task.exception?.message
                        if (exp == "null") {
                            whatnew = "Server Not Responding "
                        } else {
                            Timber.i("taskexcep :" + task.exception?.message + task.exception + task)
                            Toast.makeText(
                                this@BrowseActivity,
                                getString(R.string.internet_error),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_browse)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        val backButton: ImageView? = toolbar.findViewById(R.id.back_image)
        val titleTextView: TextView? = toolbar.findViewById(R.id.title_text)
        val searchButton: ImageView? = toolbar.findViewById(R.id.search_image)
        val menuButton:ImageView? = toolbar.findViewById(R.id.menu_image)
        titleTextView?.text = getString(R.string.nav_browse)
        backButton?.setOnClickListener(this)
        searchButton?.setOnClickListener(this)
        menuButton?.setOnClickListener(this)


        //Ad
        val adsManager = InterstitialAdsManager(this)
        adsManager.show()
        val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(this)
        val recyclerView: RecyclerView? = findViewById(R.id.list)
        recyclerView?.layoutManager = layoutManager
        val swipeRefreshLayout: SwipeRefreshLayout? = findViewById(R.id.swiper)
        swipeRefreshLayout?.setColorSchemeResources(R.color.colorAccent)
        swipeRefreshLayout?.setOnRefreshListener(this)
        try {
            doFirstRunsecond()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        update()

        //Search

        /* search = (SearchBox) findViewById(R.id.searchbox);
        search.enableVoiceRecognition(this);
        for(int x = 0; x < 10; x++){
            SearchResult option = new SearchResult("Result " + Integer.toString(x), getResources().getDrawable(R.drawable.ic_access_time_black_24dp));
            search.addSearchable(option);
        }
        search.setMenuListener(new SearchBox.MenuListener(){

            @Override
            public void onMenuClick() {
                //Hamburger has been clicked
                Toast.makeText(BrowseActivity.this, "Menu click", Toast.LENGTH_LONG).show();
            }

        });
        search.setSearchListener(new SearchBox.SearchListener(){

            @Override
            public void onSearchOpened() {
                //Use this to tint the screen
            }

            @Override
            public void onSearchClosed() {
                //Use this to un-tint the screen
            }

            @Override
            public void onSearchTermChanged(String term) {
                //React to the search term changing
                //Called after it has updated results
            }

            @Override
            public void onSearch(String searchTerm) {
                Toast.makeText(BrowseActivity.this, searchTerm +" Searched", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onResultClick(SearchResult result) {
                //React to a result being clicked
            }

            @Override
            public void onSearchCleared() {
                //Called when the clear button is clicked
            }

        });
        search.setOverflowMenu(R.menu.overflow_menu);
        search.setOverflowMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.test_menu_item:
                        Toast.makeText(BrowseActivity.this, "Clicked!", Toast.LENGTH_SHORT).show();
                        return true;
                }
                return false;
            }
        });*/
        val searchEdit: EditText? = findViewById(R.id.edit_search)
        searchEdit?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun afterTextChanged(editable: Editable) {
                val mAdapter = BrowseAdapter(this@BrowseActivity)
                mAdapter.filterList(mAdapter.filter(editable.toString()) as ArrayList<BrowseAdapter.DataItem>)
                recyclerView?.setAdapter(mAdapter)
            }
        })
        //ADS+Firebase
        mAdView = AdView(this, getString(R.string.fb_banner_ad), AdSize.BANNER_HEIGHT_50)
        mAdView?.loadAd(mAdView?.buildLoadAdConfig()?.withAdListener(adListener)?.build())
        (findViewById<View>(R.id.fb_container) as LinearLayout).addView(mAdView)
        firebaseRemoteConfigprice = FirebaseRemoteConfig.getInstance()
        val configSettings = FirebaseRemoteConfigSettings.Builder().build()
        firebaseRemoteConfigprice?.setConfigSettingsAsync(configSettings)
        val pricedata: MutableMap<String, Any> = HashMap()
        pricedata["showads"] = "yn"
        firebaseRemoteConfigprice?.setDefaultsAsync(pricedata)
        checkadstatus()
    }

    private fun showads() {
        adListener = object : AdListener {
            override fun onError(ad: Ad, adError: AdError) {}
            override fun onAdLoaded(ad: Ad) {}
            override fun onAdClicked(ad: Ad) {
                val sc = SharedCommon
                var i = getPreferencesInt(applicationContext, key1, 0)
                i++
                putPreferencesInt(applicationContext, SharedCommon.key1, i)
            }

            override fun onLoggingImpression(ad: Ad) {}
        }
    }

    @SuppressLint("MissingSuperCall")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (data != null && DetailsActivity.ACTION_REFRESH == data.getStringExtra(DetailsActivity.EXTRA_ACTION)) {
            update()
        }
        /*  if (requestCode == 1234 && resultCode == RESULT_OK) {
            ArrayList<String> matches = data
                    .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            search.populateEditText(matches.get(0));
        }*/
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.browse, menu)
        /* MenuItem searchViewItem = menu.findItem(R.id.menu_search);
        final SearchView searchViewAndroidActionBar = (SearchView) MenuItemCompat.getActionView(searchViewItem);
        searchViewAndroidActionBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchViewAndroidActionBar.clearFocus();

              //  appseachquery(query.toString());
                //Toast.makeText(FAQActivity.this, ""+query, Toast.LENGTH_SHORT).show();



                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                //Toast.makeText(FAQActivity.this, ""+newText, Toast.LENGTH_SHORT).show();
                return false;
            }
        });*/return super.onCreateOptionsMenu(menu)
    }

    //    private void appseachquery(final String query) {
    //
    //
    //        final View parentLayout = findViewById(android.R.id.content);
    //
    //        Snackbar snackbar1 = Snackbar
    //                .make(parentLayout, "Unable To Find Your Query", Snackbar.LENGTH_LONG)
    //                .setAction("Send Mail", new View.OnClickListener() {
    //                    @Override
    //                    public void onClick(View view) {
    //                        Toast toast = Toast.makeText(BrowseActivity.this, "Mail Us Your Query", Toast.LENGTH_LONG);
    //                        View view1 = toast.getView();
    //
    //                        view1.getBackground().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);
    //
    //                        TextView text = view1.findViewById(android.R.id.message);
    //                        text.setTextColor(Color.BLACK);
    //
    //                        toast.show();
    //
    //                        Intent send = new Intent(Intent.ACTION_SENDTO);
    //                        String uriText = "mailto:" + Uri.encode("thexenonstudio@gmail.com") +
    //                                "?subject=" + Uri.encode("Around Me - FAQs") +
    //                                "&body=" + Uri.encode("" + query + " \n\n\n ------------ \n\n Version Code : " + versionCode + "\n Version Name : " + versionName + "\n Application ID : " + appid);
    //                        Uri uri = Uri.parse(uriText);
    //
    //                        send.setData(uri);
    //                        startActivity(Intent.createChooser(send, "Send Mail Via : "));
    //
    //                    }
    //                });
    //        //Query firebaseSearchQuery = mUserDatabase.orderByChild("Items").startAt(query).endAt(query + "\uf8ff");
    //
    //        //Toast.makeText(this, ""+query, Toast.LENGTH_SHORT).show();
    //
    //        /*if(query.equals("Hello")){
    //
    //            Toast.makeText(this, "Hello", Toast.LENGTH_SHORT).show();
    //        }
    //*/
    //
    //
    //        if (query.isEmpty()) {
    //
    //            //final View parentLayout = findViewById(android.R.id.content);
    //
    //
    //            Toast.makeText(this, "Please Search Any Query ", Toast.LENGTH_SHORT).show();
    //
    //            //Toast.makeText(this, "Hello", Toast.LENGTH_SHORT).show();
    //        }
    //
    //    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_refresh -> {
                update()
                return true
            }
            R.id.menu_help_browse -> {
                sendingbaby()
                return true
            }
            R.id.menu_search -> {
                search()
                return true
            }
            R.id.menu_delete -> {
                if (isNotificationAccessEnabled(
                        applicationContext
                    )
                ) {
                    confirm()
                } else {
                    openDialog()
                }
                return true
            }
            R.id.menu_export -> {
                if (isNotificationAccessEnabled(
                        applicationContext
                    )
                ) {
                    export()
                } else {
                    openDialog()
                }
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun openDialog() {
        val builder = AlertDialog.Builder(this, R.style.DialogTheme)
            .setTitle(R.string.alert)
            .setMessage(R.string.message_allow_permission)
            .setCancelable(true)
            .setPositiveButton(R.string.text_ok, null)
        val dialog = builder.create()
        dialog.show()
        val positive = dialog.getButton(DialogInterface.BUTTON_POSITIVE)
        positive.setTextColor(resources.getColor(R.color.colorText))
    }

    private fun sendingbaby() {
//        Toast toast = Toast.makeText(this, "Mail Us For More Detail", Toast.LENGTH_LONG);
//        View view = toast.getView();
//        view.getBackground().setColorFilter((Color.parseColor("#FF104162")), PorterDuff.Mode.SRC_IN);
//        TextView text = view.findViewById(android.R.id.message);
//        text.setTextColor(Color.WHITE);
//        toast.show();
        val send = Intent(Intent.ACTION_SENDTO)
        val uriText = "mailto:" + Uri.encode(Const.EMAIL) +
                "?subject=" + Uri.encode("Notification Log App") +
                "&body=" + Uri.encode("Hello, Type Your Query/Problem/Bug/Suggestions Here \n\n\n ------------ \n\n Version Code : $versionCode\n Version Name : $versionName")
        val uri = Uri.parse(uriText)
        send.data = uri
        startActivity(Intent.createChooser(send, "Send Mail Via : "))
    }

    private fun update() {
        /*  BrowseAdapter adapter = new BrowseAdapter(this,"all");
        recyclerView.setAdapter(adapter);

        if(adapter.getItemCount() == 0) {
            Toast.makeText(this, R.string.empty_log_file, Toast.LENGTH_LONG).show();
            finish();
        }*/
        val adapter = BrowseAdapter(this)
        recyclerView?.adapter = adapter
        if (adapter.itemCount == 0) {
            Toast.makeText(this, R.string.empty_log_file, Toast.LENGTH_LONG).show()
            finish()
        }
    }

    private fun confirm() {
        val builder = AlertDialog.Builder(this, R.style.DialogTheme)
        builder.setTitle(R.string.dialog_delete_header)
        builder.setMessage(R.string.dialog_delete_text)
        builder.setNegativeButton(R.string.dialog_delete_no) { dialogInterface, i -> dialogInterface.dismiss() }
        builder.setPositiveButton(R.string.dialog_delete_yes) { dialogInterface, i ->
            truncate()
            sendnotification()
        }
        val dialog = builder.create()
        dialog.show()
        val positive = dialog.getButton(DialogInterface.BUTTON_POSITIVE)
        positive.setTextColor(resources.getColor(R.color.colorText))
        val negative = dialog.getButton(DialogInterface.BUTTON_NEGATIVE)
        negative.setTextColor(resources.getColor(R.color.colorText))
    }

    private fun truncate() {
        try {
            val dbHelper = DatabaseHelper(this)
            val db = dbHelper.writableDatabase
            db.execSQL(DatabaseHelper.SQL_DELETE_ENTRIES_POSTED)
            db.execSQL(DatabaseHelper.SQL_CREATE_ENTRIES_POSTED)
            db.execSQL(DatabaseHelper.SQL_DELETE_ENTRIES_REMOVED)
            db.execSQL(DatabaseHelper.SQL_CREATE_ENTRIES_REMOVED)
            val local = Intent()
            local.action = NotificationHandler.BROADCAST
            LocalBroadcastManager.getInstance(this).sendBroadcast(local)
            startActivity(Intent(this@BrowseActivity, BrowseActivity::class.java))
            finish()
        } catch (e: Exception) {
            if (Const.DEBUG) e.printStackTrace()
        }
    }

    private fun sendnotification() {
        val requestID = System.currentTimeMillis().toInt()
        val contentIntent = PendingIntent.getActivity(
            baseContext,
            0, Intent(), PendingIntent.FLAG_ONE_SHOT
        )
        val buildernotif = NotificationCompat.Builder(baseContext)
        val pauseIntent = Intent(this, NewMainActivity::class.java)
        pauseIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        pauseIntent.putExtra("pause", true)
        val pausePendingIntent = PendingIntent.getActivity(
            this,
            requestID,
            pauseIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        buildernotif.setAutoCancel(true)
        val cancelIntent = Intent(this, NewMainActivity::class.java)
        buildernotif.setAutoCancel(true)
        buildernotif.setAutoCancel(true)
            .setDefaults(Notification.DEFAULT_LIGHTS or Notification.DEFAULT_SOUND)
            .setWhen(System.currentTimeMillis())
            .setSmallIcon(R.drawable.notificationlogo)
            .setContentTitle(getString(R.string.deleted_success))
            .setColor(
                ContextCompat.getColor(
                    applicationContext,
                    R.color.colorPrimary
                )
            ) /*.addAction(R.drawable.ic_action_boom, "Action!", someOtherPendingIntent)*/
            .setContentText(getString(R.string.message_deleted_success))
            .setContentIntent(pausePendingIntent)

//Then add the action to your notification
        val manager = baseContext.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(1, buildernotif.build())
    }

    private fun export() {
        if (!ExportTask.exporting) {
            val exportTask = ExportTask(this, findViewById(android.R.id.content))
            exportTask.execute()
        }
    }

    override fun onRefresh() {
        update()
        swipeRefreshLayout!!.isRefreshing = false
    }

    private fun doFirstRunsecond() {
        val settings = getSharedPreferences("FIRSTRUNTEXT", MODE_PRIVATE)
        if (settings.getBoolean("isFirstRunDialogBoxtext", true)) {
            val editor = settings.edit()
            editor.putBoolean("isFirstRunDialogBoxtext", false)
            editor.apply()
            val builder = AlertDialog.Builder(this@BrowseActivity, R.style.DialogTheme)
            builder.setIcon(R.drawable.ic_splash_logo)
            builder.setTitle(R.string.alert_title_tip)
            builder.setMessage(R.string.alert_message_tip)
            builder.setCancelable(false)
            builder.setPositiveButton(R.string.button_got_it) { dialogInterface, i -> dialogInterface.dismiss() }
            val dialog = builder.create()
            dialog.show()
            val positive = dialog.getButton(DialogInterface.BUTTON_POSITIVE)
            positive.setTextColor(resources.getColor(R.color.colorText))
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.back_image -> finish()
            R.id.search_image -> search()
            R.id.menu_image -> {
                val popup = PopupMenu(this, v)
                popup.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { item ->
                    when (item.itemId) {
                        R.id.menu_refresh -> {
                            update()
                            return@OnMenuItemClickListener true
                        }
                        R.id.menu_help_browse -> {
                            sendingbaby()
                            return@OnMenuItemClickListener true
                        }
                        R.id.menu_search -> {
                            search()
                            return@OnMenuItemClickListener true
                        }
                        R.id.menu_delete -> {
                            if (isNotificationAccessEnabled(
                                    applicationContext
                                )
                            ) {
                                confirm()
                            } else {
                                openDialog()
                            }
                            return@OnMenuItemClickListener true
                        }
                        R.id.menu_export -> {
                            if (isNotificationAccessEnabled(
                                    applicationContext
                                )
                            ) {
                                export()
                            } else {
                                openDialog()
                            }
                            return@OnMenuItemClickListener true
                        }
                    }
                    false
                })
                popup.inflate(R.menu.browse)
                popup.show()
            }
        }
    }
}