package com.appnotification.notificationhistorylog.ui

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.database.DatabaseUtils
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.Typeface
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.view.*
import android.widget.*
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.NotificationCompat
import androidx.core.app.ShareCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.core.view.MenuItemCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.appnotification.notificationhistorylog.*
import com.appnotification.notificationhistorylog.BuildConfig
import com.appnotification.notificationhistorylog.CommonCl.SharedCommon
import com.appnotification.notificationhistorylog.CommonCl.SharedCommon.getPreferencesInt
import com.appnotification.notificationhistorylog.CommonCl.SharedCommon.key1
import com.appnotification.notificationhistorylog.CommonCl.SharedCommon.keyfaqs
import com.appnotification.notificationhistorylog.CommonCl.SharedCommon.keyopensetting
import com.appnotification.notificationhistorylog.CommonCl.SharedCommon.keyoveruse
import com.appnotification.notificationhistorylog.CommonCl.SharedCommon.putPreferencesInt
import com.appnotification.notificationhistorylog.R
import com.appnotification.notificationhistorylog.misc.Const
import com.appnotification.notificationhistorylog.misc.DatabaseHelper
import com.appnotification.notificationhistorylog.misc.ExportTask
import com.appnotification.notificationhistorylog.misc.Util.isNotificationAccessEnabled
import com.appnotification.notificationhistorylog.service.NotificationHandler
import com.appnotification.notificationhistorylog.ui.activities.AppsActivity
import com.appnotification.notificationhistorylog.ui.activities.HelpActivity
import com.facebook.ads.*
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.onesignal.OneSignal
import timber.log.Timber
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import kotlin.collections.set

class NewMainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener,
    PopupMenu.OnMenuItemClickListener {
    private val versionCode = BuildConfig.VERSION_CODE
    private val versionName = BuildConfig.VERSION_NAME
    private val appid = BuildConfig.APPLICATION_ID
    private val dialog: AlertDialog? = null
    private val isPermissionAsked = false
    private lateinit var btndr:ImageButton

    //    Button buttoncheck;
    //    ImageView imagelog;
    private var txtcheck: TextView? = null
    private var gallery //offersnav , textGrid;
            : TextView? = null
    private var acimage: ImageView? = null
    private var actitle: TextView? = null
    private var acsubtext: TextView? = null
    private var firebaseRemoteConfigprice: FirebaseRemoteConfig? = null

    //    private String PrivacyUrl = "https://xenonstudio.in/helpnotifcation";
    //    private ImageView ivNotification;
    //    private Animation shake;
    //    private View view1, view2, view3, view4, view5;
    //    private RelativeLayout rltip, rlfav, sharerl, tpfav, ncrl, seletcapps;
    //    private TextView txt, txtversion, logtextno;
    //    private String apppackagename = "com.appnotification.notificationhistorylog";
    private var txtlogcount: TextView? = null

    //    private String onesignalid;
    //    String adnotshowing;
    private val splashTread: Thread? = null

    //    private static final int NUMBER_OF_ADS = 10;
    //    private AdLoader adLoader;
    //    private List<UnifiedNativeAd> mNativeAds = new ArrayList<>();
    //    private final boolean mlogtext = false;
    //    private final boolean mlogongoing = false;
    private var whatnew: String? = null

    //    private String showallnotf;
    private var shownotif: String? = null
    private var showtuto: String? = null

    //    private String showfav;
    private var livenotice: String? = null

    //    private TextView txtcount;
    //    private FirebaseAuth mAuth;
    private var mAdView: AdView? = null

    //    private DatabaseReference mUserRef, museref, mdatareport, mcredtref, mlinkupdate;
    private var dbHelper: DatabaseHelper? = null

    //    private EditText searchEdit;
    //    private GuideView mGuideView;
    //    private GuideView.Builder buildergf, builder2;
    private lateinit var drawer: DrawerLayout
    private var buttonAllow: Button? = null
    private var frameLayout: FrameLayout? = null
    private var adListener: AdListener? = null

    fun openDrawer() {
        drawer=findViewById(R.id.drawer_layout)
        drawer.openDrawer(Gravity.LEFT)
    }

    private fun initializeCountDrawer() {
        //Gravity property aligns the text
        gallery?.gravity = Gravity.CENTER_VERTICAL
        gallery?.setTypeface(null, Typeface.BOLD)
        gallery?.setTextColor(resources.getColor(R.color.colorPink))
        gallery?.text = getString(R.string.new_string)
        gallery?.textSize = 12f
    }

    private fun checklogcount() {
        val adapter = BrowseAdapter(this@NewMainActivity)
        val count = adapter.itemCount.toString()
        //Toast.makeText(this, ""+count, Toast.LENGTH_SHORT).show();
        val logs = count.toInt()
        if (logs == 0) {
            /*  buttoncheck.setVisibility(View.VISIBLE);
            imagelog.setVisibility(View.VISIBLE);
            txtcheck.setVisibility(View.VISIBLE);*/
            acimage?.visibility = View.VISIBLE
            buttonAllow?.visibility = View.VISIBLE
            buttonAllow?.setOnClickListener {
                startActivity(Intent(this@NewMainActivity, NewMainActivity::class.java))
                finish()
            }
            buttonAllow?.setText(R.string.retry_button)
            txtcheck?.visibility = View.VISIBLE
            Toast.makeText(this, getString(R.string.message_no_notify), Toast.LENGTH_SHORT).show()
        }
    }

    private fun checkadsstatus() {
        firebaseRemoteConfigprice?.fetch(0)
            ?.addOnCompleteListener { task ->
                Timber.i("info%s", firebaseRemoteConfigprice?.info?.lastFetchStatus)
                Timber.i("firebaseremote%s", firebaseRemoteConfigprice?.getString("btn_text"))
                if (task.isSuccessful) {
                    firebaseRemoteConfigprice?.fetchAndActivate()
                    /*txt600.setText(firebaseRemoteConfigprice.getString("txt600"));
                                txt1500.setText(firebaseRemoteConfigprice.getString("txt1500"));
                                txt3200.setText(firebaseRemoteConfigprice.getString("txt3200"));
                                txt5000.setText(firebaseRemoteConfigprice.getString("txt5000"));
    
     pricedata.put("showfavtab", "yn");
            pricedata.put("showtutorial", "yn");
            pricedata.put("showbrowseallnotification", "yn");
            pricedata.put("showbrowsenotification", "yn");
    */whatnew = firebaseRemoteConfigprice?.getString("showads")
                    //                            showallnotf = (firebaseRemoteConfigprice.getString("showbrowseallnotification"));
//                            showfav = (firebaseRemoteConfigprice.getString("showfavtab"));
                    showtuto = firebaseRemoteConfigprice?.getString("showtutorial")
                    livenotice = firebaseRemoteConfigprice?.getString("livenotice")
                    shownotif = firebaseRemoteConfigprice?.getString("showbrowsenotification")
                    when (shownotif) {
                        "yes" -> {}
                        "shownoif" -> {
                            val builder =
                                AlertDialog.Builder(this@NewMainActivity, R.style.DialogTheme)
                            builder.setMessage(livenotice)
                            builder.setCancelable(false)
                            builder.setPositiveButton(getString(R.string.button_got_it)) { dialogInterface, _ -> dialogInterface.dismiss() }
                                .setNegativeButton(R.string.developer_contact) { _, _ ->
                                    val send = Intent(Intent.ACTION_SENDTO)
                                    val uriText = "mailto:" + Uri.encode(Const.EMAIL) +
                                            "?subject=" + Uri.encode("Notification Log - Developer Contact") +
                                            "&body=" + Uri.encode("Hello, Type Your Query/Problem/Bug/Suggestions Here \n\n\n ------------ \n\n Version Code : $versionCode\n Version Name : $versionName\n Application ID : $appid")
                                    val uri = Uri.parse(uriText)
                                    send.data = uri
                                    startActivity(Intent.createChooser(send, "Send Mail Via : "))
                                }
                            val dialog = builder.create()
                            dialog.show()
                            val positive = dialog.getButton(DialogInterface.BUTTON_POSITIVE)
                            positive.setTextColor(resources.getColor(R.color.colorText))
                            val negative = dialog.getButton(DialogInterface.BUTTON_NEGATIVE)
                            negative.setTextColor(resources.getColor(R.color.colorText))
                        }
                        "no" -> {}
                    }
                    if (whatnew == "yes") {
                        showads()
                        //Toast.makeText(NewMainActivity.this, "Showing", Toast.LENGTH_SHORT).show();
                    } else if (whatnew == "no") {
                        mAdView?.visibility = View.GONE

                        // Toast.makeText(NewMainActivity.this, "Nope", Toast.LENGTH_SHORT).show();
                        Timber.i("Not Showing")
                    }
                    Timber.i("firebaseremote%s", firebaseRemoteConfigprice?.getString("btn_text"))


                    /* Picasso.get().load(firebaseRemoteConfigprice.getString("image_link"))
                                                .into(img);*/
                } else {
                    val exp = "" + task.exception?.message
                    if (exp == "null") {
                        whatnew = "Server Not Responding "
                        val builder = AlertDialog.Builder(this@NewMainActivity, R.style.DialogTheme)
                    } else {
                        Timber.i("taskexcep :" + task.exception?.message + task.exception + task)
                        val parentLayout = findViewById<View>(android.R.id.content)
                        val snackbar = Snackbar.make(
                            parentLayout,
                            R.string.internet_error,
                            Snackbar.LENGTH_INDEFINITE
                        )
                        val snackbarView = snackbar.view
                        snackbar.setTextColor(
                            ContextCompat.getColor(
                                applicationContext, R.color.colorText
                            )
                        )
                        snackbar.setActionTextColor(
                            ContextCompat.getColor(
                                applicationContext, R.color.colorText
                            )
                        )
                        snackbarView.setBackgroundColor(
                            ContextCompat.getColor(
                                applicationContext, R.color.colorNavBack
                            )
                        )
                        snackbar.setAction(R.string.retry) { //  checkConnection();
                            snackbar.dismiss()
                        }
                        snackbar.show()
                    }
                }
            }
    }

    override fun onResume() {
        super.onResume()
        if (!isNotificationAccessEnabled(
                applicationContext
            )
        ) {
//            if (!isPermissionAsked)
//                openDialog();
            acimage?.visibility = View.VISIBLE
            actitle?.visibility = View.VISIBLE
            acsubtext?.visibility = View.VISIBLE
            //            String logstring = "Notification Log ";
//            txtlogcount.setText(logstring);
        } else {
            firstrun()
            buttonAllow?.visibility = View.GONE
            acimage?.visibility = View.GONE
            actitle?.visibility = View.GONE
            acsubtext?.visibility = View.GONE
            frameLayout?.visibility = View.VISIBLE
            checklogcount()
            /* Bundle bundle = new Bundle();
            bundle.putString("selected_navigation", "Recents");
            RecentsFragment recentsFragment = new RecentsFragment();
            recentsFragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    recentsFragment).commit();*/updatelognumber()


            //navigationView.setCheckedItem(R.id.nav_recents);
        }
    }

    private fun firstrun() {
        val settings = getSharedPreferences("FIRSTRUNINFO111", MODE_PRIVATE)
        if (settings.getBoolean("isFirstRunDialogBoxtex111", true)) {
            val editor = settings.edit()
            editor.putBoolean("isFirstRunDialogBoxtex111", false)
            editor.apply()
            sendnoifications()
            //            AlertDialog.Builder builder = new AlertDialog.Builder(NewMainActivity.this, R.style.DialogTheme);
//            builder.setIcon(R.drawable.ic_splash_logo);
//            builder.setTitle(R.string.title_alert_beta);
//            builder.setMessage(R.string.message_alert_beta);
//
//            builder.setCancelable(false);
//
//            builder.setPositiveButton(R.string.button_got_it, new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialogInterface, int i) {
//
//                    dialogInterface.dismiss();
//                    Intent startIntent = new Intent(NewMainActivity.this, NewMainActivity.class);
//                    startActivity(startIntent);
//                }
//            });
//
//
//            builder.setNegativeButton(R.string.button_report_issue, new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialogInterface, int i) {
//                    helpdialog();
//                }
//            });
//
//
//            AlertDialog dialog = builder.create();
//            dialog.show();
//            Button positive = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
//            positive.setTextColor(getResources().getColor(R.color.colorText));
//            Button negative = dialog.getButton(DialogInterface.BUTTON_NEGATIVE);
//            negative.setTextColor(getResources().getColor(R.color.colorText));
        }
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    private fun sendnoifications() {
        val requestID = System.currentTimeMillis().toInt()
        val contentIntent = PendingIntent.getActivity(
            baseContext,
            0, Intent(), PendingIntent.FLAG_ONE_SHOT
        )
        val buildernotif = NotificationCompat.Builder(baseContext)
        val pauseIntent = Intent(this, MainActivity::class.java)
        pauseIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        pauseIntent.putExtra("pause", true)
        val pausePendingIntent = PendingIntent.getActivity(
            this,
            requestID,
            pauseIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        buildernotif.setAutoCancel(true)
        val cancelIntent = Intent(this, MainActivity::class.java)
        buildernotif.setAutoCancel(true)
        buildernotif.setAutoCancel(true)
            .setDefaults(Notification.DEFAULT_LIGHTS or Notification.DEFAULT_SOUND)
            .setWhen(System.currentTimeMillis())
            .setSmallIcon(R.drawable.ic_splash_logo)
            .setContentTitle(getString(R.string.title_update))
            .setColor(
                ContextCompat.getColor(
                    applicationContext,
                    R.color.colorPrimary
                )
            ) /*.addAction(R.drawable.ic_action_boom, "Action!", someOtherPendingIntent)*/
            .setContentText(getString(R.string.message_update))
            .setContentIntent(pausePendingIntent)

//Then add the action to your notification
        val manager = baseContext.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(1, buildernotif.build())
    }

    private fun updatelognumber() {
        try {
//            String logstring = "Notification Logged : ";
            val adapter = BrowseAdapter(this)
            val count = adapter.itemCount.toString()
            //Toast.makeText(this, ""+count, Toast.LENGTH_SHORT).show();
            txtlogcount?.text = count
            if (txtlogcount?.text == "0") {
                buttonAllow?.visibility = View.VISIBLE
                buttonAllow?.setText(R.string.message_no_notify_yet)
            }
            if (txtlogcount?.text == "20") {
                txtlogcount?.setText(R.string.notify_log)
            }
            val cc = count.toInt()
            if (cc >= 15) {
                val scor = SharedCommon
                var ortop = getPreferencesInt(applicationContext, keyoveruse, 0)
                ortop++
                putPreferencesInt(applicationContext, SharedCommon.keyoveruse, ortop)
                val or = getPreferencesInt(applicationContext, keyoveruse, 0)
                if (or == 0) {
                    txtlogcount?.setText(R.string.notify_log)
                    val n = 15
                    val nn = "" + n
                    overnotificationmessage(n, nn)
                }
                if (or == 5) {
                    txtlogcount?.setText(R.string.notify_log)
                    val n = 15
                    val nn = "" + n
                    overnotificationmessage(n, nn)
                }
            }
            dbHelper = DatabaseHelper(this@NewMainActivity)
            val db = dbHelper?.readableDatabase
            val numRowsPosted =
                DatabaseUtils.queryNumEntries(db, DatabaseHelper.PostedEntry.TABLE_NAME)
            //    private String versionfirebase;
            //    private String logno;
            var lognumber = numRowsPosted.toString()
            txtlogcount?.text = lognumber
            //txtlogcount.setText(logstring+count);
            lognumber = numRowsPosted.toString()
            txtlogcount?.text = lognumber
            // Toast.makeText(this, ""+numRowsPosted+lognumber, Toast.LENGTH_SHORT).show();
            val nnn = numRowsPosted.toString()
            val pref = getPreferences(MODE_PRIVATE)
            val edt = pref.edit()
            edt.putString("facebook_id", nnn)
            edt.apply()
        } catch (e: Exception) {
            if (Const.DEBUG) e.printStackTrace()
        }
    }

    private fun overnotificationmessage(n: Int, nn: String) {
        firebaseRemoteConfigprice?.fetchAndActivate()
        whatnew = firebaseRemoteConfigprice?.getString("showads")
        if (whatnew == "yes") {
            showads()
            //Toast.makeText(MainActivity.this, "Showing", Toast.LENGTH_SHORT).show();
        } else if (whatnew == "no") {
            mAdView?.visibility = View.GONE
            //  Toast.makeText(MainActivity.this, "Nope", Toast.LENGTH_SHORT).show();
        }
        // Toast.makeText(getActivity(), "", Toast.LENGTH_SHORT).show();
        val builder = AlertDialog.Builder(this@NewMainActivity, R.style.DialogTheme)
        builder.setIcon(R.drawable.ic_splash_logo)
        builder.setTitle(R.string.note)
        builder.setMessage(R.string.message_notification_exceed)
        builder.setCancelable(true)
        builder.setNeutralButton(R.string.dismiss_caps) { dialogInterface, i -> dialogInterface.dismiss() }
        builder.setPositiveButton(R.string.clear_caps) { dialogInterface, i ->
            try {
                val dbHelper = DatabaseHelper(this@NewMainActivity)
                val db = dbHelper.writableDatabase
                db.execSQL(DatabaseHelper.SQL_DELETE_ENTRIES_POSTED)
                db.execSQL(DatabaseHelper.SQL_CREATE_ENTRIES_POSTED)
                db.execSQL(DatabaseHelper.SQL_DELETE_ENTRIES_REMOVED)
                db.execSQL(DatabaseHelper.SQL_CREATE_ENTRIES_REMOVED)
                val local = Intent()
                local.action = NotificationHandler.BROADCAST
                LocalBroadcastManager.getInstance(this@NewMainActivity).sendBroadcast(local)
                recreate()
            } catch (e: Exception) {
                if (Const.DEBUG) e.printStackTrace()
            }
            Toast.makeText(
                this@NewMainActivity,
                getString(R.string.message_cleared),
                Toast.LENGTH_LONG
            ).show()
            dialogInterface.dismiss()
        }
        val dialog = builder.create()
        dialog.show()
        val positive = dialog.getButton(DialogInterface.BUTTON_POSITIVE)
        positive.setTextColor(resources.getColor(R.color.colorText))
        val neutral = dialog.getButton(DialogInterface.BUTTON_NEUTRAL)
        neutral.setTextColor(resources.getColor(R.color.colorText))
    }

    private fun openDialog() {
        if (isNotificationAccessEnabled(
                applicationContext
            )
        ) {
            return
        }
        title = ""
        val builder = AlertDialog.Builder(this, R.style.DialogTheme)
            .setTitle(R.string.alert)
            .setMessage(R.string.message_allow_permission)
            .setCancelable(true)
            .setPositiveButton(R.string.text_ok, null)
        dialog?.dismiss()
        val dialog = builder.create()
        dialog.show()
        val button = dialog.getButton(DialogInterface.BUTTON_POSITIVE)
        button.setTextColor(resources.getColor(R.color.colorText))
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
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
            R.id.menu_refresh -> {
                val fragmentManager = supportFragmentManager
                val recentsFragment =
                    fragmentManager.findFragmentByTag("recent_tag") as RecentsFragment?
                recentsFragment!!.refreshAdapter()
                return true
            }
            R.id.report -> {
                helpdialog()
                return true
            }
            R.id.menu_sug -> {
                openhelp()
                return true
            }
            R.id.menu_info -> {
                val startIntentrr = Intent(this@NewMainActivity, InfoActivity::class.java)
                startActivity(startIntentrr)
                return true
            }
            R.id.backup -> return true
            R.id.restore -> return true
            R.id.faqs -> {
                val scor = SharedCommon
                var orfavc = getPreferencesInt(applicationContext, keyfaqs, 0)
                orfavc++
                putPreferencesInt(applicationContext, SharedCommon.keyfaqs, orfavc)
                val startIntent = Intent(this@NewMainActivity, FAQActivity::class.java)
                startActivity(startIntent)
                /*String url = "https://xenonstudio.in/notificationlog#24f3eaf5-2efe-4e30-9c58-975c032e08e0";
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url));
                    startActivity(i);*/return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun opensetting() {
        val scor = SharedCommon
        var orfavc = getPreferencesInt(applicationContext, keyopensetting, 0)
        orfavc++
        putPreferencesInt(applicationContext, SharedCommon.keyopensetting, orfavc)
        val startIntent = Intent(this@NewMainActivity, SettingsActivity::class.java)
        startActivity(startIntent)
    }

    private fun openhelp() {
        /* firebaseRemoteConfigprice.activateFetched();

        whatnew=(firebaseRemoteConfigprice.getString("showads"));


        if (whatnew.equals("yes")){
            showads();
            showintads();
            //Toast.makeText(NewMainActivity.this, "Showing", Toast.LENGTH_SHORT).show();
        }
        else if (whatnew.equals("no")){
            mAdView.setVisibility(View.GONE);

            //  Toast.makeText(NewMainActivity.this, "Nope", Toast.LENGTH_SHORT).show();
        }*/
        val taskEditText = EditText(this)
        val dialog = AlertDialog.Builder(this@NewMainActivity, R.style.DialogTheme)
            .setTitle(R.string.alert_suggestion)
            .setMessage(R.string.alert_suggestion_message)
            .setView(taskEditText)
            .setCancelable(false)
            .setPositiveButton(R.string.send_mail) { dialog, which ->
                val task = taskEditText.text.toString()
                sendmailintent(task)
            }
            .setNegativeButton(R.string.dialog_delete_no, null)
            .create()
        dialog.show()
        val positive = dialog.getButton(DialogInterface.BUTTON_POSITIVE)
        positive.setTextColor(resources.getColor(R.color.colorText))
        val negative = dialog.getButton(DialogInterface.BUTTON_NEGATIVE)
        negative.setTextColor(resources.getColor(R.color.colorText))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_main)
        title = ""
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)
        val drawer:DrawerLayout = findViewById(R.id.drawer_layout)
        val navigationView = findViewById<NavigationView>(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(this)
        val toggle = ActionBarDrawerToggle(
            this, drawer, toolbar,
            R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawer.addDrawerListener(toggle)
        toggle.syncState()
        val headerview = navigationView.getHeaderView(0)
        val txtlogcount:TextView = headerview.findViewById(R.id.txtlogcount)
        // updatelognumber();
        initCountDrawer()
        txtlogcount.setOnClickListener(View.OnClickListener { updatelognumber() })

//        ActionBar actionBar = getSupportActionBar();
//        actionBar.setDisplayShowCustomEnabled(true);
//        actionBar.setDisplayShowTitleEnabled(false);
//        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#1A1F2B")));
//
//        LayoutInflater inflater = LayoutInflater.from(this);
//        View v = inflater.inflate(R.layout.titleview, null);
//
//
//        TextView actionbar_title = v.findViewById(R.id.actionbar_title);
//        //actionbar_title.setTypeface(you can set your font here also.);
//
//        actionBar.setCustomView(v);
        supportActionBar?.hide()
        //These lines should be added in the OnCreate() of your main activity
        /* refermenu=(TextView) MenuItemCompat.getActionView(navigationView.getMenu().
                findItem(R.id.nav_refer));
        statsmenu =(TextView) MenuItemCompat.getActionView(navigationView.getMenu().
                findItem(R.id.nav_stats));
//This method will initialize the count value
        initializeCountDrawer();*/
        /*  Menu menu = navigationView.getMenu();
        menu.findItem(R.id.nav_stats).setTitle("New");*/
        //menu.findItem(R.id.nav_rigt_version).setTitle(versionName);
        // freeorpro =(TextView)headerview.findViewById(R.id.freeorpro);
//        mAuth = FirebaseAuth.getInstance();
        mAdView = AdView(this, getString(R.string.fb_banner_ad), AdSize.BANNER_HEIGHT_50)
        mAdView?.loadAd(mAdView?.buildLoadAdConfig()?.withAdListener(adListener)?.build())
        (headerview.findViewById<View>(R.id.fb_container) as LinearLayout).addView(mAdView)

//        mAuth = FirebaseAuth.getInstance();
//        if (mAuth.getCurrentUser() != null) {
//
//
//            mdatareport = FirebaseDatabase.getInstance().getReference().child("UsageReports").child(mAuth.getCurrentUser().getUid());
//            museref = FirebaseDatabase.getInstance().getReference().child("mainacreport").child(mAuth.getCurrentUser().getUid());
//
//            mUserRef = FirebaseDatabase.getInstance().getReference().child("openreport").child(mAuth.getCurrentUser().getUid());
//
//        }
        OneSignal.startInit(this)
            .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
            .unsubscribeWhenNotificationsAreDisabled(true)
            .init()
        OneSignal.idsAvailable { userId, registrationId ->
            //                Log.d("debug", "User:" + userId);
//                if (registrationId != null)
//                    onesignalid = userId;
//                Log.d("debug", "registrationId:" + registrationId);
        }
        val frameLayout:FrameLayout = findViewById(R.id.fragment_container)
        val acimage:ImageView = findViewById(R.id.acimage)
        acimage.setVisibility(View.GONE)
        actitle = findViewById(R.id.actitle)
        acsubtext = findViewById(R.id.acsubtext)
        txtcheck = findViewById(R.id.logtxt)
        /*buttoncheck = findViewById(R.id.button_check);
        imagelog = (ImageView)findViewById(R.id.logimage);
        txtcheck = (TextView)findViewById(R.id.logtxt);

        buttoncheck.setVisibility(View.GONE);
        imagelog.setVisibility(View.GONE);
        txtcheck.setVisibility(View.GONE);
*/
//        txtcheck.setVisibility(View.GONE);
        val buttonAllow:Button = findViewById(R.id.button_allow)
        buttonAllow.setOnClickListener(View.OnClickListener { view: View? -> startActivity(Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS")) })
        if (savedInstanceState == null && isNotificationAccessEnabled(
                applicationContext
            )
        ) {
            val bundle = Bundle()
            bundle.putString("selected_navigation", "Recents")
            val recentsFragment = RecentsFragment()
            recentsFragment.arguments = bundle
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, recentsFragment, "recent_tag").commit()
            //  navigationView.setCheckedItem(R.id.nav_recents);
        } else {
//            if (!Util.isNotificationAccessEnabled(getApplicationContext())) {
//                openDialog();
//            }
            frameLayout.setVisibility(View.GONE)
            buttonAllow.setVisibility(View.VISIBLE)
        }


        //ADS + FIREBASE
        firebaseRemoteConfigprice = FirebaseRemoteConfig.getInstance()
        val configSettings = FirebaseRemoteConfigSettings.Builder().build()
        firebaseRemoteConfigprice!!.setConfigSettingsAsync(configSettings)
        val pricedata: MutableMap<String, Any> = HashMap()
        pricedata["showads"] = "yn"
        pricedata["showfavtab"] = "yn"
        pricedata["showtutorial"] = "yn"
        pricedata["showbrowseallnotification"] = "yn"
        pricedata["showbrowsenotification"] = "yn"
        pricedata["livenotice"] = "yn"
        firebaseRemoteConfigprice!!.setDefaultsAsync(pricedata)
        checkadsstatus()
        gallery =
            MenuItemCompat.getActionView(navigationView.menu.findItem(R.id.nav_stats)) as TextView
        gallery?.visibility = View.INVISIBLE
        initializeCountDrawer()
        /*mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712");
        //testid-ca-app-pub-3940256099942544/1033173712
        //upid-ca-app-pub-6778147776084460/3563503620
        //my-INT-ID--ca-app-pub-8081344892743036/1424914117
        mInterstitialAd.loadAd(new AdRequest.Builder().build());
        mAuth = FirebaseAuth.getInstance();


        if (mAuth.getCurrentUser() != null) {


            mdatareport = FirebaseDatabase.getInstance().getReference().child("UsageReports").child(mAuth.getCurrentUser().getUid());
            museref = FirebaseDatabase.getInstance().getReference().child("mainacreport").child(mAuth.getCurrentUser().getUid());

            mUserRef = FirebaseDatabase.getInstance().getReference().child("openreport").child(mAuth.getCurrentUser().getUid());

        }*/
    }

    private fun showads() {
        //upid--ca-app-pub-6778147776084460/7588656349
        //myid-ca-app-pub-8081344892743036/1017320664
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

    private fun helpdialog() {
        val helpIntent = Intent(this, HelpActivity::class.java)
        startActivity(helpIntent)

//        LayoutInflater li = LayoutInflater.from(NewMainActivity.this);
//        View promptsView = li.inflate(R.layout.helplayout, null);
//
//        final android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(
//                NewMainActivity.this);
//
//        // set prompts.xml to alertdialog builder
//        alertDialogBuilder.setView(promptsView);
//
//
//        final EditText edtname, edtemail, edtphone, edtpincode, edtid;
//        edtname = promptsView.findViewById(R.id.edtfullname);
//        edtemail = promptsView.findViewById(R.id.edtemail);
//        edtphone = promptsView.findViewById(R.id.edtphonenumber);
//        edtpincode = promptsView.findViewById(R.id.edtpincode);
//        edtid = promptsView.findViewById(R.id.edtpaypalorpaytm);
//
//
//        TelephonyManager tm = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
//        final String countryCodeValue = tm.getNetworkCountryIso();
//
//        final Spinner sp = promptsView
//                .findViewById(R.id.spinnerpaypalpaytm);
//
//
//        final Button userInput = promptsView
//                .findViewById(R.id.btndiasub);
//
//        userInput.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                String name = edtname.getText().toString();
//                String email = edtemail.getText().toString();
//                String phone = edtphone.getText().toString();
//                String pincode = edtpincode.getText().toString();
//                String id = edtid.getText().toString();
//
//
//                String Method = ("" + sp.getSelectedItem());
//                if (name.equals("") || pincode.equals("") || id.equals("")) {
//
//                    Toast.makeText(NewMainActivity.this, "Please Enter All The Details", Toast.LENGTH_SHORT).show();
//                } else {
//
//                    Toast toast = Toast.makeText(NewMainActivity.this, "SEND MAIL VIA GMAIL/YAHOO ", Toast.LENGTH_LONG);
//                    View view1 = toast.getView();
//
//                    view1.getBackground().setColorFilter((Color.parseColor("#FF104162")), PorterDuff.Mode.SRC_IN);
//
//
//                    TextView text = view1.findViewById(android.R.id.message);
//                    text.setTextColor(Color.WHITE);
//
//                    toast.show();
//
//                    Intent send = new Intent(Intent.ACTION_SENDTO);
//                    String uriText = "mailto:" + Uri.encode("notificationapp.xenonstudio@gmail.com") +
//                            "?subject=" + Uri.encode(Method + " - Notification Log App") +
//                            "&body=" + Uri.encode("" + "Name: " + name + "\n" + "Country: " + pincode + "\n" + "Query Type: " + Method + "\n" + "Query: " + id + " \n\n\n ------------ \n\n Version Code : " + versionCode + "\n Build : " + Build.BRAND + "\n" + Build.MODEL + "\n" + Build.DEVICE);
//                    Uri uri = Uri.parse(uriText);
//
//                    send.setData(uri);
//                    startActivity(Intent.createChooser(send, "Send Mail Via : "));
//
//                    splashTread = new Thread() {
//                        @Override
//                        public void run() {
//                            try {
//                                int waited = 0;
//                                // Splash screen pause time
//                                while (waited < 10600) {
//                                    sleep(100);
//                                    waited += 100;
//                                }
//                                sendFCMPush();
//                                NewMainActivity.this.finish();
//                            } catch (InterruptedException e) {
//                                // do nothing
//                            } finally {
//                                NewMainActivity.this.finish();
//                            }
//
//                        }
//                    };
//                    splashTread.start();
//
//                }
//            }
//        });
//        alertDialogBuilder
//                .setCancelable(false)
//                .setNegativeButton("Go Back",
//                        new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int id) {
//
//                                dialog.dismiss();
//                            }
//                        });
//
//        // create alert dialog
//        android.app.AlertDialog alertDialog = alertDialogBuilder.create();
//
//        // show it
//        alertDialog.show();
    }

    //    private void sendFCMPush() {
    //
    //
    //        int requestID = (int) System.currentTimeMillis();
    //
    //        PendingIntent contentIntent = PendingIntent.getActivity(getBaseContext(),
    //                0, new Intent(), PendingIntent.FLAG_ONE_SHOT);
    //        NotificationCompat.Builder builder = new NotificationCompat.Builder(getBaseContext());
    //
    //        Intent pauseIntent = new Intent(this, NewMainActivity.class);
    //        pauseIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
    //        pauseIntent.putExtra("pause", true);
    //        PendingIntent pausePendingIntent = PendingIntent.getActivity(this, requestID, pauseIntent, PendingIntent.FLAG_UPDATE_CURRENT);
    //        builder.setAutoCancel(true);
    //
    //        Intent cancelIntent = new Intent(this, NewMainActivity.class);
    //        builder.setAutoCancel(true);
    //
    //        builder.setAutoCancel(true)
    //                .setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_SOUND)
    //                .setWhen(System.currentTimeMillis())
    //                .setSmallIcon(R.drawable.notificationlogo)
    //                .setSound(Uri.parse("uri://notification_xperia.mp3"))
    //                .setContentTitle("Query Received ")
    //                .setColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary))
    //                /*.addAction(R.drawable.ic_action_boom, "Action!", someOtherPendingIntent)*/
    //                .setContentText("Contact Us If Don't Get Mail Within 7 Days")
    //                .setContentIntent(pausePendingIntent);
    //
    //
    ////Then add the action to your notification
    //
    //
    //        NotificationManager manager = (NotificationManager) getBaseContext().getSystemService(Context.NOTIFICATION_SERVICE);
    //        manager.notify(1, builder.build());
    //
    //
    //    }
    private fun sendmailintent(task: String) {
        val toast = Toast.makeText(this, R.string.message_send_mail, Toast.LENGTH_LONG)
        val view = toast.view
        view?.background?.setColorFilter(
            ContextCompat.getColor(
                applicationContext, R.color.colorNavBack
            ), PorterDuff.Mode.SRC_IN
        )
        val text = view?.findViewById<TextView>(android.R.id.message)
        text?.setTextColor(Color.WHITE)
        toast.show()
        val send = Intent(Intent.ACTION_SENDTO)
        val uriText = "mailto:" + Uri.encode(Const.EMAIL) +
                "?subject=" + Uri.encode("Notification Log App") +
                "&body=" + Uri.encode(
            """$task 


 ------------ 

 Version Code : $versionCode
 Build : ${Build.BRAND}
${Build.MODEL}
${Build.DEVICE}"""
        )
        val uri = Uri.parse(uriText)
        send.data = uri
        startActivity(Intent.createChooser(send, "Send Mail Via : "))
    }

    private fun shareapplink() {
        Toast.makeText(this@NewMainActivity, getString(R.string.sharing_app), Toast.LENGTH_SHORT)
            .show()
        ShareCompat.IntentBuilder.from(this@NewMainActivity)
            .setType("text/plain")
            .setChooserTitle(R.string.share_url)
            .setText(getString(R.string.share_message))
            .startChooser()
    }

    private fun confirm() {
        val builder = AlertDialog.Builder(this, R.style.DialogTheme)
        builder.setTitle(R.string.dialog_delete_header)
        builder.setMessage(R.string.dialog_delete_text)
        builder.setNegativeButton(R.string.dialog_delete_no) { dialogInterface, i -> }
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
            .setSmallIcon(R.drawable.ic_splash_logo)
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

    private fun initCountDrawer() {
        try {
            val adapter = BrowseAdapter(this)
            val count = adapter.itemCount.toString()
            //Toast.makeText(this, ""+count, Toast.LENGTH_SHORT).show();
            txtlogcount?.text = count
        } catch (e: Exception) {
            txtlogcount?.text = "0"
        }
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
            startActivity(Intent(this@NewMainActivity, NewMainActivity::class.java))
            finish()
        } catch (e: Exception) {
            if (Const.DEBUG) e.printStackTrace()
        }
    }

    private fun export() {
        if (!ExportTask.exporting) {
            val exportTask = ExportTask(this, findViewById(android.R.id.content))
            exportTask.execute()
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_all_notifications -> {
                val startIntent = Intent(this@NewMainActivity, BrowseActivity::class.java)
                startActivity(startIntent)
            }
            R.id.nav_notifications_by_apps -> {
                val appsIntent = Intent(this@NewMainActivity, AppsActivity::class.java)
                startActivity(appsIntent)
            }
            R.id.nav_refer -> {
                firebaseRemoteConfigprice?.fetchAndActivate()
                whatnew = firebaseRemoteConfigprice?.getString("showads")
                if (whatnew == "yes") {
                    showads()
                    //Toast.makeText(MainActivity.this, "Showing", Toast.LENGTH_SHORT).show();
                } else if (whatnew == "no") {
                    mAdView!!.visibility = View.GONE
                    //  Toast.makeText(MainActivity.this, "Nope", Toast.LENGTH_SHORT).show();
                }
                //startActivity(new Intent(NewMainActivity.this, ViewGrouped.class));
                referandearndailog()
            }
            R.id.nav_stats -> {
                val statIntent = Intent(this@NewMainActivity, ViewGrouped::class.java)
                startActivity(statIntent)
                return true
            }
            R.id.nav_help -> {
                helpdialog()
                return true
            }
            R.id.nav_how_to_use -> {
                val tutorialIntent = Intent(this@NewMainActivity, TutorialActivity::class.java)
                startActivity(tutorialIntent)
                return true
            }
            R.id.nav_favourites -> if (isNotificationAccessEnabled(
                    applicationContext
                )
            ) {
                val favouriteIntent = Intent(this@NewMainActivity, FavoritesActivity::class.java)
                startActivity(favouriteIntent)
            } else {
                openDialog()
            }
            R.id.nav_export_logs -> if (isNotificationAccessEnabled(
                    applicationContext
                )
            ) {
                shareapplink()
            } else {
                openDialog()
            }
            R.id.nav_settings -> {
                val settingIntent = Intent(this@NewMainActivity, SettingsActivity::class.java)
                startActivity(settingIntent)
            }
            else -> {}
        }
        drawer?.closeDrawer(GravityCompat.START)
        return true
    }

    private fun referandearndailog() {
        val startIntent = Intent(this@NewMainActivity, TestActivity::class.java)
        startActivity(startIntent)

        /* android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(NewMainActivity.this);

        builder.setIcon(R.drawable.notificationlogo);

        builder.setTitle("Coming Soon!");

        builder.setMessage("Get Amazing Google Play Coupons, Discount Code, Remove Ads and Lot More By Just Referring App To Your Friends");
        builder.setCancelable(false);

        builder.setPositiveButton("Got It", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                Intent startIntent = new Intent(NewMainActivity.this, NewMainActivity.class);
                startActivity(startIntent);

            }
        });
        builder.setNegativeButton("Notify Me", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(NewMainActivity.this, "We Will Notify You When Refer & Earn Is Available ", Toast.LENGTH_SHORT).show();
                dialogInterface.dismiss();
            }
        });



        android.app.AlertDialog dialog = builder.create();
        dialog.show();*/
    }

    //    private void browsenotificationappwise() {
    //
    //        final String[] listitems = {"WhatsApp", "Gmail", "Facebook", "Instagram", "Calender", "Calls"};
    //        AlertDialog.Builder mBuilder = new AlertDialog.Builder(NewMainActivity.this, R.style.AlertDialogedit);
    //        mBuilder.setTitle("Browse Notifications Of  ");
    //        mBuilder.setSingleChoiceItems(listitems, -1, new DialogInterface.OnClickListener() {
    //            @Override
    //            public void onClick(DialogInterface dialogInterface, int i) {
    //
    //
    //               /* String name = "show";
    //                SharedCommon.putSharedPreferencesString(NewMainActivity.this,SharedCommon.keysettingdailog,name);*/
    //
    //
    //                if (i == 0) {
    //
    //                    //  setlocale("en");
    //                    Intent startIntent = new Intent(NewMainActivity.this, WhatsappActivity.class);
    //                    startActivity(startIntent);
    //                } else if (i == 1) {
    //
    //                    // setlocale("hi");
    //                    Toast.makeText(NewMainActivity.this, "Gmail Notifications", Toast.LENGTH_SHORT).show();
    //                    /*recreate();*/
    //                    Intent startIntent = new Intent(NewMainActivity.this, GmailActivity.class);
    //                    startActivity(startIntent);
    //                } else if (i == 2) {
    //
    //                    // setlocale("tr");
    //                    // Toast.makeText(NewMainActivity.this, "tr", Toast.LENGTH_SHORT).show();
    //
    //                    Intent startIntent = new Intent(NewMainActivity.this, FacebookActivity.class);
    //                    startActivity(startIntent);
    //                } else if (i == 3) {
    //
    //                    // setlocale("de");
    //                    // Toast.makeText(NewMainActivity.this, "de", Toast.LENGTH_SHORT).show();
    //
    //                    Intent startIntent = new Intent(NewMainActivity.this, InstaActivity.class);
    //                    startActivity(startIntent);
    //                } else if (i == 4) {
    //
    //                    //  Toast.makeText(NewMainActivity.this, "it", Toast.LENGTH_SHORT).show();
    //
    //                    Intent startIntent = new Intent(NewMainActivity.this, CalenderActivity.class);
    //                    startActivity(startIntent);
    //                } else if (i == 5) {
    //
    //
    //                    //Toast.makeText(NewMainActivity.this, "gu", Toast.LENGTH_SHORT).show();
    //
    //                    Intent startIntent = new Intent(NewMainActivity.this, CallsActivity.class);
    //                    startActivity(startIntent);
    //                }
    //
    //                dialogInterface.dismiss();
    //            }
    //        });
    //
    //        android.app.AlertDialog mDialog = mBuilder.create();
    //
    //        mDialog.show();
    //
    //    }
    override fun onBackPressed() {
        if (drawer?.isDrawerOpen(GravityCompat.START) == true) {
            drawer?.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    fun openactionmenu() {
        //NewMainActivity.this.openOptionsMenu();
    }

    fun showPopup(v: View?) {
        /* PopupMenu popup = new PopupMenu(this, v);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.main, popup.getMenu());
        popup.show();*/
        val popup = PopupMenu(this@NewMainActivity, v)
        popup.setOnMenuItemClickListener(this@NewMainActivity)
        popup.inflate(R.menu.main)
        popup.show()
    }

    override fun onMenuItemClick(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_delete -> {
                if (isNotificationAccessEnabled(
                        applicationContext
                    )
                ) {
                    confirm()
                } else {
                    openDialog()
                }
                true
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
                true
            }
            R.id.menu_refresh -> {
                val fragmentManager = supportFragmentManager
                val recentsFragment =
                    fragmentManager.findFragmentByTag("recent_tag") as RecentsFragment?
                recentsFragment?.refreshAdapter()
                true
            }
            R.id.report -> {
                helpdialog()
                true
            }
            R.id.menu_sug -> {
                openhelp()
                true
            }
            R.id.menu_info -> {
                val startIntentrr = Intent(this@NewMainActivity, InfoActivity::class.java)
                startActivity(startIntentrr)
                true
            }
            R.id.backup -> {
                //                Toast.makeText(NewMainActivity.this, "Backup click!", Toast.LENGTH_SHORT).show();
                if (!(checkSelfPermission("android.permission.READ_EXTERNAL_STORAGE") == PackageManager.PERMISSION_GRANTED && checkSelfPermission(
                        "android.permission.WRITE_EXTERNAL_STORAGE"
                    ) == PackageManager.PERMISSION_GRANTED)
                ) {
                    requestPermissions(
                        arrayOf(
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                        ), 100
                    )
                } else {
                    backUp()
                }
                true
            }
            R.id.restore -> {
                //              Toast.makeText(NewMainActivity.this, "Restore click!", Toast.LENGTH_SHORT).show();
                if (!(checkSelfPermission("android.permission.READ_EXTERNAL_STORAGE") == PackageManager.PERMISSION_GRANTED && checkSelfPermission(
                        "android.permission.WRITE_EXTERNAL_STORAGE"
                    ) == PackageManager.PERMISSION_GRANTED)
                ) {
                    requestPermissions(
                        arrayOf(
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                        ), 100
                    )
                } else {
                    restore()
                }
                true
            }
            R.id.faqs -> {
                val scor = SharedCommon
                var orfavc = getPreferencesInt(applicationContext, keyfaqs, 0)
                orfavc++
                putPreferencesInt(applicationContext, SharedCommon.keyfaqs, orfavc)
                val startIntent = Intent(this@NewMainActivity, FAQActivity::class.java)
                startActivity(startIntent)
                /*String url = "https://xenonstudio.in/notificationlog#24f3eaf5-2efe-4e30-9c58-975c032e08e0";
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url));
                    startActivity(i);*/true
            }
            else -> false
        }

        // return false;
    }

    fun backUp() {
        Timber.i("---------------------------------1")
        try {
            val sd = Environment.getExternalStorageDirectory()
            val data = Environment.getDataDirectory()
            Timber.i("SDCardPath : %s", sd.absolutePath)
            Timber.i("DataPath : %s", data.absolutePath)
            if (sd.canWrite()) {
                val currentDBPath =
                    "//data//" + applicationContext.packageName + "//databases//notifications.db"
                val backupDBPath = "/NotificationBackup/notifications.db"
                val currentDB = File(data, currentDBPath)
                var backupDB = File(sd, backupDBPath)
                val destination = File(sd, "/NotificationBackup")
                if (!destination.exists()) {
                    destination.mkdir()
                }
                try {
                    backupDB = File(sd, backupDBPath)
                } catch (e: Exception) {
                    Toast.makeText(this, "" + e.message, Toast.LENGTH_SHORT).show()
                }
                if (currentDB.exists()) {
                    val src = FileInputStream(currentDB).channel
                    val dst = FileOutputStream(backupDB).channel
                    dst.transferFrom(src, 0, src.size())
                    src.close()
                    dst.close()
                }
            } else {
                Timber.e("Not able to write!")
            }
        } catch (e: Exception) {
            Timber.e("BackupException : %s", e.message)
        }
    }

    fun restore() {
        try {
            val sd = Environment.getExternalStorageDirectory()
            val data = Environment.getDataDirectory()
            if (sd.canWrite()) {
                val currentDBPath =
                    "//data//" + applicationContext.packageName + "//databases//notifications.db"
                val backupDBPath = "/NotificationBackup/notifications.db"
                val currentDB = File(data, currentDBPath)
                val backupDB = File(sd, backupDBPath)
                val path = "//data//" + applicationContext.packageName + "//databases"
                val file = File(data, path)
                if (!file.exists()) {
                    file.mkdir()
                }
                if (currentDB.exists()) {
                    val src = FileInputStream(backupDB).channel
                    val dst = FileOutputStream(currentDB).channel
                    dst.transferFrom(src, 0, src.size())
                    src.close()
                    dst.close()
                    Toast.makeText(
                        applicationContext,
                        getString(R.string.message_database_restore),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        } catch (e: Exception) {
            Timber.e("RestoreException : %s", e.message)
        }
    }
}