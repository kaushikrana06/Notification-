package com.appnotification.notificationhistorylog.ui

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.telephony.TelephonyManager
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.View.OnLongClickListener
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.*
import android.widget.RatingBar.OnRatingBarChangeListener
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.ShareCompat
import androidx.core.content.ContextCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.appnotification.notificationhistorylog.*
import com.appnotification.notificationhistorylog.BuildConfig
import com.appnotification.notificationhistorylog.CommonCl.SharedCommon
import com.appnotification.notificationhistorylog.CommonCl.SharedCommon.getPreferencesInt
import com.appnotification.notificationhistorylog.CommonCl.SharedCommon.key1
import com.appnotification.notificationhistorylog.CommonCl.SharedCommon.keyfaqs
import com.appnotification.notificationhistorylog.CommonCl.SharedCommon.keylog
import com.appnotification.notificationhistorylog.CommonCl.SharedCommon.keyopenrate
import com.appnotification.notificationhistorylog.CommonCl.SharedCommon.keyopenrateall
import com.appnotification.notificationhistorylog.CommonCl.SharedCommon.keyopenratefav
import com.appnotification.notificationhistorylog.CommonCl.SharedCommon.keyopenratetutorial
import com.appnotification.notificationhistorylog.CommonCl.SharedCommon.keyopensetting
import com.appnotification.notificationhistorylog.CommonCl.SharedCommon.putPreferencesInt
import com.appnotification.notificationhistorylog.CommonCl.SharedCommon.putSharedPreferencesString
import com.appnotification.notificationhistorylog.R
import com.appnotification.notificationhistorylog.misc.Const
import com.appnotification.notificationhistorylog.misc.DatabaseHelper
import com.appnotification.notificationhistorylog.misc.ExportTask
import com.appnotification.notificationhistorylog.service.NotificationHandler
import com.facebook.ads.*
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.onesignal.OneSignal
import smartdevelop.ir.eram.showcaseviewlib.GuideView
import smartdevelop.ir.eram.showcaseviewlib.config.DismissType
import smartdevelop.ir.eram.showcaseviewlib.config.Gravity
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.set

class MainActivity : AppCompatActivity() {
    private val mlogtext = false
    private val mlogongoing = false
    var whatnew: String? = null
    var showallnotf: String? = null
    var shownotif: String? = null
    var showtuto: String? = null
    var showfav: String? = null
    var livenotice: String? = null
    var versionfirebase: String? = null
    var logno: String? = null
    var versionCode = BuildConfig.VERSION_CODE
    var versionName = BuildConfig.VERSION_NAME
    var appid = BuildConfig.APPLICATION_ID
    var firebaseRemoteConfigprice: FirebaseRemoteConfig? = null
    var txtcount: TextView? = null
    var PrivacyUrl = "https://xenonstudio.in/helpnotifcation"
    var ivNotification: ImageView? = null
    var shake: Animation? = null
    var view1: View? = null
    var view2: View? = null
    var view3: View? = null
    var view4: View? = null
    var view5: View? = null
    var rltip: RelativeLayout? = null
    var rlfav: RelativeLayout? = null
    var sharerl: RelativeLayout? = null
    var tpfav: RelativeLayout? = null
    var ncrl: RelativeLayout? = null
    var seletcapps: RelativeLayout? = null
    var txt: TextView? = null
    var txtversion: TextView? = null
    var logtextno: TextView? = null
    var apppackagename = "com.appnotification.notificationhistorylog"
    var onesignalid: String? = null
    var adnotshowing: String? = null
    var splashTread: Thread? = null
    private var mAuth: FirebaseAuth? = null
    private var mInterstitialAd: InterstitialAd? = null
    private var mAdView: AdView? = null
    private var mUserRef: DatabaseReference? = null
    private var museref: DatabaseReference? = null
    private var mdatareport: DatabaseReference? = null
    private val mcredtref: DatabaseReference? = null
    private val mlinkupdate: DatabaseReference? = null
    private val mGuideView: GuideView? = null
    private val buildergf: GuideView.Builder? = null
    private val builder2: GuideView.Builder? = null
    private var adListener: AdListener? = null
    private fun dofirstinforun() {
        val settings = getSharedPreferences("FIRSTRUNINFO111", MODE_PRIVATE)
        if (settings.getBoolean("isFirstRunDialogBoxtex111", true)) {
            val editor = settings.edit()
            editor.putBoolean("isFirstRunDialogBoxtex111", false)
            editor.commit()
            // sendnoifications();
            val builder = android.app.AlertDialog.Builder(this@MainActivity)
            builder.setIcon(R.drawable.notificationlogo)
            builder.setTitle("New Features Available!")
            builder.setMessage("-Multi-Language Support \n -View Notification App Wise \n -Notice For App In Settings \n -Help Section Improved \n -UI Changes \n -Bug Fixed \n")
            builder.setCancelable(false)
            builder.setPositiveButton("Got It") { dialogInterface, i -> dialogInterface.dismiss() }
            builder.setNegativeButton("Change Language") { dialogInterface, i ->
                val startIntent = Intent(this@MainActivity, SettingsActivity::class.java)
                startActivity(startIntent)
            }
            val dialog = builder.create()
            dialog.show()
        }
    }

    private fun checkadsstatus() {
        firebaseRemoteConfigprice?.fetch(0)
            ?.addOnCompleteListener { task ->
                Log.e("TaskError", "info" + firebaseRemoteConfigprice?.info?.lastFetchStatus)
                Log.e(
                    "TaskError",
                    "firebaseremote" + firebaseRemoteConfigprice?.getString("btn_text")
                )
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
                    showallnotf = firebaseRemoteConfigprice?.getString("showbrowseallnotification")
                    showfav = firebaseRemoteConfigprice?.getString("showfavtab")
                    showtuto = firebaseRemoteConfigprice?.getString("showtutorial")
                    livenotice = firebaseRemoteConfigprice?.getString("livenotice")
                    shownotif = firebaseRemoteConfigprice?.getString("showbrowsenotification")
                    if (showtuto == "yes") {
                        rltip?.visibility = View.VISIBLE
                    } else {
                        rltip?.visibility = View.GONE
                    }
                    if (shownotif == "yes") {
                        seletcapps?.visibility = View.VISIBLE
                    } else if (shownotif == "shownoif") {
                        val builder =
                            android.app.AlertDialog.Builder(this@MainActivity, R.style.AlertDialog)
                        builder.setMessage(livenotice)
                        builder.setCancelable(false)
                        builder.setPositiveButton("GOT IT") { dialogInterface, i -> dialogInterface.dismiss() }
                            .setNegativeButton("Developer Contact") { dialogInterface, i ->
                                val send = Intent(Intent.ACTION_SENDTO)
                                val uriText = "mailto:" + Uri.encode("thexenonstudio@gmail.com") +
                                        "?subject=" + Uri.encode("Notification Log - Developer Contact") +
                                        "&body=" + Uri.encode("Hello, Type Your Query/Problem/Bug/Suggestions Here \n\n\n ------------ \n\n Version Code : $versionCode\n Version Name : $versionName\n Application ID : $appid")
                                val uri = Uri.parse(uriText)
                                send.data = uri
                                startActivity(Intent.createChooser(send, "Send Mail Via : "))
                            }
                        val dialog = builder.create()
                        dialog.show()
                    } else if (shownotif == "no") {
                        seletcapps?.visibility = View.GONE
                    }
                    if (whatnew == "yes") {
                        showads()
                        showintads()
                        //Toast.makeText(MainActivity.this, "Showing", Toast.LENGTH_SHORT).show();
                    } else if (whatnew == "no") {
                        mAdView?.visibility = View.GONE

                        // Toast.makeText(MainActivity.this, "Nope", Toast.LENGTH_SHORT).show();
                        Log.e("AdsStatus", "Not Showing")
                    }
                    Log.e(
                        "TaskError",
                        "firebaseremote" + firebaseRemoteConfigprice?.getString("btn_text")
                    )


                    /* Picasso.get().load(firebaseRemoteConfigprice.getString("image_link"))
                                                .into(img);*/
                } else {
                    val exp = "" + task.exception?.message
                    if (exp == "null") {
                        whatnew = "Server Not Responding "
                        val builder = android.app.AlertDialog.Builder(this@MainActivity)
                    } else {
                        Log.e(
                            "TaskError",
                            "taskexcep :" + task.exception?.message + task.exception + task
                        )
                        Toast.makeText(
                            this@MainActivity,
                            "Internet Connection Error",
                            Toast.LENGTH_SHORT
                        ).show()
                        val parentLayout = findViewById<View>(android.R.id.content)
                        val snackbar = Snackbar
                            .make(
                                parentLayout,
                                "Internet Connection Error",
                                Snackbar.LENGTH_INDEFINITE
                            )
                        val snackbarView = snackbar.view
                        snackbarView.setBackgroundColor(Color.parseColor("#FF104162"))
                        snackbar.setAction("Retry") {
                            checkConnection()
                            snackbar.dismiss()
                        }
                        snackbar.show()
                    }
                }
            }
    }
    @Suppress("DEPRECATION")
    protected val isOnline: Boolean
        protected get() {
            val cm = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
            val netInfo = cm.activeNetworkInfo
            return netInfo != null && netInfo.isConnectedOrConnecting
        }

    fun checkConnection() {
        if (isOnline) {
            Log.d("Hello", "Net")
        } else {

            //cddata.setVisibility(View.GONE);
            val parentLayout = findViewById<View>(android.R.id.content)
            val snackbar = Snackbar
                .make(parentLayout, "Internet Connection Not Found! ", Snackbar.LENGTH_SHORT)
            val snackbarView = snackbar.view
            snackbarView.setBackgroundColor(Color.parseColor("#FF104162"))
            snackbar.show()
        }
    }

    private fun showintads() {
        if (mInterstitialAd?.isAdLoaded == true) {
            mInterstitialAd?.show()
        } else {
            Log.d("TAG", "The interstitial wasn't loaded yet.")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val txtversion :TextView= findViewById(R.id.versioncode)
        val logtextno:TextView = findViewById(R.id.logtextno)
        mAuth = FirebaseAuth.getInstance()
        AudienceNetworkAds.initialize(this)
        mInterstitialAd = InterstitialAd(this, getString(R.string.fb_interstitial_ad))
        mInterstitialAd!!.loadAd()
        mAdView = AdView(this, getString(R.string.fb_banner_ad), AdSize.BANNER_HEIGHT_50)
        mAdView?.loadAd(mAdView?.buildLoadAdConfig()?.withAdListener(adListener)?.build())
        (findViewById<View>(R.id.fb_container) as LinearLayout).addView(mAdView)
        //testid-ca-app-pub-3940256099942544/1033173712
        //upid-ca-app-pub-6778147776084460/3563503620
        //my-INT-ID--ca-app-pub-8081344892743036/1424914117
        mAuth = FirebaseAuth.getInstance()
        if (mAuth?.currentUser != null) {
            mdatareport = mAuth?.currentUser?.uid?.let {
                FirebaseDatabase.getInstance().reference.child("UsageReports").child(
                    it
                )
            }
            museref = mAuth?.currentUser?.uid?.let {
                FirebaseDatabase.getInstance().reference.child("mainacreport").child(
                    it
                )
            }
            mUserRef = mAuth?.currentUser?.uid?.let {
                FirebaseDatabase.getInstance().reference.child("openreport").child(
                    it
                )
            }
        }
        //Onesignal
        OneSignal.startInit(this)
            .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
            .unsubscribeWhenNotificationsAreDisabled(true)
            .init()
        OneSignal.idsAvailable { userId, registrationId ->
            Log.d("debug", "User:$userId")
            if (registrationId != null) onesignalid = userId
            Log.d("debug", "registrationId:$registrationId")
        }
        val i = getPreferencesInt(applicationContext, key1, 0)
        val sc = SharedCommon
        if (i <= 0) {
            Log.d("", "")
        }
        val or = getPreferencesInt(applicationContext, keyopenrate, 0)
        val orset = getPreferencesInt(applicationContext, keyopensetting, 0)
        val ortip = getPreferencesInt(applicationContext, keyopenratetutorial, 0)
        val orfav = getPreferencesInt(applicationContext, keyopenratefav, 0)
        val scor = SharedCommon
        if (or <= 0) {
            Log.d("", "")
        }
        val rlfav:RelativeLayout = findViewById(R.id.favrl)
        rlfav.setOnClickListener(View.OnClickListener { export() })
        val sharerl: RelativeLayout = findViewById(R.id.sharerl)
        sharerl.setOnClickListener(View.OnClickListener { shareapplink() })
        val rltip: RelativeLayout = findViewById(R.id.tiprl)
        rltip.setOnClickListener(View.OnClickListener { //Toast.makeText(MainActivity.this, "", Toast.LENGTH_SHORT).show();
            val scor = SharedCommon
            var ortop = getPreferencesInt(applicationContext, keyopenratetutorial, 0)
            ortop++
            putPreferencesInt(applicationContext, SharedCommon.keyopenratetutorial, ortop)
            firebaseRemoteConfigprice?.fetchAndActivate()
            whatnew = firebaseRemoteConfigprice?.getString("showads")
            if (whatnew == "yes") {
                showads()
                showintads()
                //Toast.makeText(MainActivity.this, "Showing", Toast.LENGTH_SHORT).show();
            } else if (whatnew == "no") {
                mAdView?.visibility = View.GONE

                //  Toast.makeText(MainActivity.this, "Nope", Toast.LENGTH_SHORT).show();
            }
            showcasetimebaby()
        })
        rltip.setOnLongClickListener(OnLongClickListener {
            Toast.makeText(this@MainActivity, "" + adnotshowing, Toast.LENGTH_SHORT).show()
            false
        })
        val seletcapps:RelativeLayout = findViewById(R.id.seletcapps)
        seletcapps.setOnClickListener(View.OnClickListener {
            val scor = SharedCommon
            var or = getPreferencesInt(applicationContext, keyopenrate, 0)
            or++
            putPreferencesInt(applicationContext, SharedCommon.keyopenrate, or)
            showappdailog()
            // dofirstinforun();
            /*Intent startIntent = new Intent(MainActivity.this, SelectedNotifActivity.class);
                    startActivity(startIntent);*/
        })
        val tpfav:RelativeLayout = findViewById(R.id.tpfav)
        tpfav.setOnClickListener(View.OnClickListener {
            val scor = SharedCommon
            var orfavc = getPreferencesInt(applicationContext, keyopenratefav, 0)
            orfavc++
            putPreferencesInt(applicationContext, SharedCommon.keyopenratefav, orfavc)
            firebaseRemoteConfigprice?.fetchAndActivate()
            whatnew = firebaseRemoteConfigprice?.getString("showads")
            if (whatnew == "yes") {
                showads()
                showintads()
                //Toast.makeText(MainActivity.this, "Showing", Toast.LENGTH_SHORT).show();
            } else if (whatnew == "no") {
                mAdView?.visibility = View.GONE

                //  Toast.makeText(MainActivity.this, "Nope", Toast.LENGTH_SHORT).show();
            }
            val startIntent = Intent(this@MainActivity, FavActivity::class.java)
            startActivity(startIntent)
        })
        view1 = findViewById(R.id.favrl)
        view2 = findViewById(R.id.fra)
        view3 = findViewById(R.id.sharerl)
        view4 = findViewById(R.id.tiprl)
        view5 = findViewById(R.id.fra)
        doFirstRun()
        shake = AnimationUtils.loadAnimation(this, R.anim.shake)
        txtversion.setText("v$versionName")
        firebaseRemoteConfigprice = FirebaseRemoteConfig.getInstance()
        val configSettings = FirebaseRemoteConfigSettings.Builder().build()
        firebaseRemoteConfigprice?.setConfigSettingsAsync(configSettings)
        val pricedata: MutableMap<String, Any> = HashMap()
        pricedata["showads"] = "yn"
        pricedata["showfavtab"] = "yn"
        pricedata["showtutorial"] = "yn"
        pricedata["showbrowseallnotification"] = "yn"
        pricedata["showbrowsenotification"] = "yn"
        pricedata["livenotice"] = "yn"
        firebaseRemoteConfigprice?.setDefaultsAsync(pricedata)
        checkadsstatus()
    }

    private fun showads() {
        //testid---ca-app-pub-3940256099942544/6300978111
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

    private fun showappdailog() {
        val listitems = arrayOf("WhatsApp", "Gmail", "Facebook", "Instagram", "Calender", "Calls")
        val mBuilder = android.app.AlertDialog.Builder(this@MainActivity, R.style.AlertDialogedit)
        mBuilder.setTitle("Browse Notifications Of  ")
        mBuilder.setSingleChoiceItems(listitems, -1) { dialogInterface, i -> /* String name = "show";
                SharedCommon.putSharedPreferencesString(MainActivity.this,SharedCommon.keysettingdailog,name);*/
            if (i == 0) {

                //  setlocale("en");
                val startIntent = Intent(this@MainActivity, WhatsappActivity::class.java)
                startActivity(startIntent)
            } else if (i == 1) {

                // setlocale("hi");
                Toast.makeText(this@MainActivity, "Gmail Notifications", Toast.LENGTH_SHORT).show()
                /*recreate();*/
                val startIntent = Intent(this@MainActivity, GmailActivity::class.java)
                startActivity(startIntent)
            } else if (i == 2) {

                // setlocale("tr");
                // Toast.makeText(MainActivity.this, "tr", Toast.LENGTH_SHORT).show();
                val startIntent = Intent(this@MainActivity, FacebookActivity::class.java)
                startActivity(startIntent)
            } else if (i == 3) {

                // setlocale("de");
                // Toast.makeText(MainActivity.this, "de", Toast.LENGTH_SHORT).show();
                val startIntent = Intent(this@MainActivity, InstaActivity::class.java)
                startActivity(startIntent)
            } else if (i == 4) {

                //  Toast.makeText(MainActivity.this, "it", Toast.LENGTH_SHORT).show();
                val startIntent = Intent(this@MainActivity, CalenderActivity::class.java)
                startActivity(startIntent)
            } else if (i == 5) {


                //Toast.makeText(MainActivity.this, "gu", Toast.LENGTH_SHORT).show();
                val startIntent = Intent(this@MainActivity, CallsActivity::class.java)
                startActivity(startIntent)
            }
            dialogInterface.dismiss()
        }
        val mDialog = mBuilder.create()
        mDialog.show()
    }

    private fun showratingbar() {
        val li = LayoutInflater.from(this@MainActivity)
        val promptsView = li.inflate(R.layout.ratinglayout, null)
        val alertDialogBuilder = android.app.AlertDialog.Builder(
            this@MainActivity
        )

        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView)
        val rate: RatingBar
        rate = promptsView.findViewById(R.id.ratingBardai)
        rate.onRatingBarChangeListener = OnRatingBarChangeListener { ratingBar, v, b ->
            when (ratingBar.rating.toInt()) {
                1 -> {
                    Toast.makeText(this@MainActivity, "We Will Improve Our App", Toast.LENGTH_SHORT)
                        .show()
                    val startIntent = Intent(this@MainActivity, MainActivity::class.java)
                    startActivity(startIntent)
                }
                2 -> {
                    Toast.makeText(this@MainActivity, "We Will Improve Our App", Toast.LENGTH_SHORT)
                        .show()
                    val startIntent1 = Intent(this@MainActivity, MainActivity::class.java)
                    startActivity(startIntent1)
                }
                3 -> {
                    Toast.makeText(
                        this@MainActivity,
                        "Thanks For Your Feedback",
                        Toast.LENGTH_SHORT
                    ).show()
                    val startIntent2 = Intent(this@MainActivity, MainActivity::class.java)
                    startActivity(startIntent2)
                }
                4 -> {
                    Toast.makeText(
                        this@MainActivity,
                        "Please Rate Us On Play Store",
                        Toast.LENGTH_SHORT
                    ).show()
                    val appPackageName =
                        packageName // getPackageName() from Context or Activity object
                    try {
                        startActivity(
                            Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse("market://details?id=$apppackagename")
                            )
                        )
                    } catch (anfe: ActivityNotFoundException) {
                        startActivity(
                            Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse("https://play.google.com/store/apps/details?id=$apppackagename")
                            )
                        )
                    }
                }
                5 -> {
                    Toast.makeText(
                        this@MainActivity,
                        "Please Rate Us On Play Store",
                        Toast.LENGTH_SHORT
                    ).show()
                    val appPackageName1 =
                        packageName // getPackageName() from Context or Activity object
                    try {
                        startActivity(
                            Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse("market://details?id=$apppackagename")
                            )
                        )
                    } catch (anfe: ActivityNotFoundException) {
                        startActivity(
                            Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse("https://play.google.com/store/apps/details?id=$apppackagename")
                            )
                        )
                    }
                }
                else -> {}
            }
        }


        // set dialog message
        alertDialogBuilder
            .setCancelable(true)

        // create alert dialog
        val alertDialog = alertDialogBuilder.create()

        // show it
        alertDialog.show()
    }

    private fun showcasetimebaby() {
        GuideView.Builder(this@MainActivity)
            .setTitle("Export Logs")
            .setContentText("Share/Export Your Logs From Here")
            .setGravity(Gravity.auto)
            .setTargetView(view1)
            .setDismissType(DismissType.outside) //optional - default dismissible by TargetView
            .setGuideListener { showsecondshowcase() }
            .build()
            .show()
    }

    private fun showsecondshowcase() {
        GuideView.Builder(this@MainActivity)
            .setTitle("Notifications")
            .setContentText("From Here You Browse Notifications, See No. Of Notification Logged, Change Permission Status")
            .setGravity(Gravity.auto)
            .setTargetView(view2)
            .setDismissType(DismissType.outside) //optional - default dismissible by TargetView
            .setGuideListener { showsthirdshowcase() }
            .build()
            .show()
    }

    private fun showsthirdshowcase() {
        GuideView.Builder(this@MainActivity)
            .setTitle("Share App")
            .setContentText("From Here Directly Share Apps With Friends And Family Members")
            .setGravity(Gravity.auto)
            .setTargetView(view3)
            .setDismissType(DismissType.outside) //optional - default dismissible by TargetView
            .setGuideListener { showforthshowcase() }
            .build()
            .show()
    }

    private fun showforthshowcase() {
        GuideView.Builder(this)
            .setTitle("More")
            .setContentText("You Can Delete Logs, Change Log Settings From Above Menu \n\n Also While Browsing Notification You Can Open App In PLay Store, Share Particular App, Add To Favorite And Lot More..\n\nLog Text Means - Log the actual content of the notifications. Increases the size of the log file but allows you to read past messages. ")
            .setGravity(Gravity.auto) //optional
            .setDismissType(DismissType.outside) //optional - default DismissType.targetView
            .setTargetView(view4)
            .setContentTextSize(14) //optional
            .setTitleTextSize(16) //optional
            .build()
            .show()

        //  shake();
    }

    private fun showtipshowcase() {
        GuideView.Builder(this)
            .setTitle("Status")
            .setContentText("Click On Status To Give Access To Notification")
            .setGravity(Gravity.auto) //optional
            .setDismissType(DismissType.outside) //optional - default DismissType.targetView
            .setTargetView(view5)
            .setContentTextSize(14) //optional
            .setTitleTextSize(16) //optional
            .build()
            .show()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        /* CheckBox checkBox = (CheckBox) menu.findItem(R.id.menuShowlogtext).getActionView();
        checkBox.setText("Log Text");

        CheckBox checkBox2 = (CheckBox) menu.findItem(R.id.menuShowlogon).getActionView();
        checkBox.setText("Log Ongoing");*/return true
    }

    public override fun onStart() {

        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = mAuth?.currentUser
        //Toast.makeText(this, ""+currentUser, Toast.LENGTH_SHORT).show();
        if (currentUser == null) {
            sendToStart()
        } else {
            val i = getPreferencesInt(applicationContext, key1, 0)
            val or = getPreferencesInt(applicationContext, keyopenrate, 0)
            val orfav = getPreferencesInt(applicationContext, keyopenratefav, 0)
            val ortip = getPreferencesInt(applicationContext, keyopenratetutorial, 0)
            val orset = getPreferencesInt(applicationContext, keyopensetting, 0)
            val orall = getPreferencesInt(applicationContext, keyopenrateall, 0)
            val orfaqs = getPreferencesInt(applicationContext, keyfaqs, 0)
            val logno = getPreferencesInt(applicationContext, keylog, 0)
            val pref = getPreferences(MODE_PRIVATE)
            val id = pref.getString("facebook_id", "")
            //Toast.makeText(this, ""+id, Toast.LENGTH_SHORT).show();
            val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZZZZZ", Locale.getDefault())
            val datte = format.format(Date())
            val idtimedate = datte.substring(5, 10)
            val idtime = datte.substring(11, 19)
            mdatareport = mAuth?.currentUser?.uid?.let {
                FirebaseDatabase.getInstance().reference.child("UsageReports").child(
                    it
                )
            }
            museref = mAuth?.currentUser?.uid?.let {
                FirebaseDatabase.getInstance().reference.child("mainacreport").child(
                    it
                )
            }
            mUserRef = mAuth?.currentUser?.uid?.let {
                FirebaseDatabase.getInstance().reference.child("openreport").child(
                    it
                )
            }
            mUserRef?.child("Onesignal-ID")?.setValue(onesignalid)
            mUserRef?.child("Last Seen")?.setValue(datte)
            mUserRef?.child("Notifications Logged")?.setValue(id)
            mUserRef?.child("FAQs")?.setValue(orfaqs)
            museref?.child("D-i")?.setValue(i)
            mUserRef?.child("Browse-Notification-All")?.setValue(orall)
            mUserRef?.child("Browse-Notification")?.setValue(or)
            mUserRef?.child("Settings")?.setValue(orset)
            mUserRef?.child("Tutorial")?.setValue(ortip)
            mUserRef?.child("Favorite")?.setValue(orfav)
            //username = currentUser.getUid();
            //SAVEDATAREPORT
            //savereport(currentUser);
        }
        super.onStart()
        // Toast.makeText(this, ""+currentUser, Toast.LENGTH_SHORT).show();
        //updateUI(currentUser);
    }

    private fun sendToStart() {

        //username = "Not Signed In";
        //Toast.makeText(this, "Not Signed", Toast.LENGTH_SHORT).show();;
    }

    private fun signInAnonymously() {
        // showProgressDialog();
        // [START signin_anonymously]
        mAuth?.signInAnonymously()
            ?.addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    //Log.d(TAG, "signInAnonymously:success");
                    val user = mAuth!!.currentUser
                    // updateUI(user);
                } else {
                    // If sign in fails, display a message to the user.
                    //Log.w(TAG, "signInAnonymously:failure", task.getException());
                    Toast.makeText(
                        this@MainActivity, "Authentication failed.",
                        Toast.LENGTH_SHORT
                    ).show()
                    //updateUI(null);
                }

                // [START_EXCLUDE]
                // hideProgressDialog();
                // [END_EXCLUDE]
            }
        // [END signin_anonymously]
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_delete -> {
                confirm()
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
            R.id.backup -> {
                Toast.makeText(this, "Hello", Toast.LENGTH_SHORT).show()
                return true
            }
            R.id.restore -> {
                Toast.makeText(this, "Hello Res", Toast.LENGTH_SHORT).show()
                return true
            }
            R.id.faqs -> {
                val scor = SharedCommon
                var orfavc = getPreferencesInt(applicationContext, keyfaqs, 0)
                orfavc++
                putPreferencesInt(applicationContext, SharedCommon.keyfaqs, orfavc)
                val startIntent = Intent(this@MainActivity, FAQActivity::class.java)
                startActivity(startIntent)
                /*String url = "https://xenonstudio.in/notificationlog#24f3eaf5-2efe-4e30-9c58-975c032e08e0";
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url));
                    startActivity(i);*/return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun helpdialog() {
        val li = LayoutInflater.from(this@MainActivity)
        val promptsView = li.inflate(R.layout.helplayout, null)
        val alertDialogBuilder = android.app.AlertDialog.Builder(
            this@MainActivity
        )

        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView)
        val edtname: EditText
        val edtemail: EditText
        val edtphone: EditText
        val edtpincode: EditText
        val edtid: EditText
        edtname = promptsView.findViewById(R.id.edtfullname)
        edtemail = promptsView.findViewById(R.id.edtemail)
        edtphone = promptsView.findViewById(R.id.edtphonenumber)
        edtpincode = promptsView.findViewById(R.id.edtpincode)
        edtid = promptsView.findViewById(R.id.edtpaypalorpaytm)
        val tm = this.getSystemService(TELEPHONY_SERVICE) as TelephonyManager
        val countryCodeValue = tm.networkCountryIso
        val sp = promptsView
            .findViewById<Spinner>(R.id.spinnerpaypalpaytm)
        val userInput = promptsView
            .findViewById<Button>(R.id.btndiasub)
        userInput.setOnClickListener {
            val name = edtname.text.toString()
            val email = edtemail.text.toString()
            val phone = edtphone.text.toString()
            val pincode = edtpincode.text.toString()
            val id = edtid.text.toString()
            val Method = "" + sp.selectedItem
            if (name == "" || pincode == "" || id == "") {
                Toast.makeText(
                    this@MainActivity,
                    "Please Enter All The Details",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                val toast = Toast.makeText(
                    this@MainActivity,
                    "SEND MAIL VIA GMAIL/YAHOO ",
                    Toast.LENGTH_LONG
                )
                val view1 = toast.view
                view1!!.background.setColorFilter(
                    Color.parseColor("#FF104162"),
                    PorterDuff.Mode.SRC_IN
                )
                val text = view1.findViewById<TextView>(android.R.id.message)
                text.setTextColor(Color.WHITE)
                toast.show()
                val send = Intent(Intent.ACTION_SENDTO)
                val uriText = "mailto:" + Uri.encode("notificationapp.xenonstudio@gmail.com") +
                        "?subject=" + Uri.encode("$Method - Notification Log App") +
                        "&body=" + Uri.encode(
                    """Name: $name
Country: $pincode
Query Type: $Method
Query: $id 


 ------------ 

 Version Code : $versionCode
 Build : ${Build.BRAND}
${Build.MODEL}
${Build.DEVICE}"""
                )
                val uri = Uri.parse(uriText)
                send.data = uri
                startActivity(Intent.createChooser(send, "Send Mail Via : "))
                splashTread = object : Thread() {
                    override fun run() {
                        try {
                            var waited = 0
                            // Splash screen pause time
                            while (waited < 10600) {
                                sleep(100)
                                waited += 100
                            }
                            sendFCMPush()
                            finish()
                        } catch (e: InterruptedException) {
                            // do nothing
                        } finally {
                            finish()
                        }
                    }
                }
                (splashTread as Thread).start()
            }
        }
        alertDialogBuilder
            .setCancelable(false)
            .setNegativeButton(
                "Go Back"
            ) { dialog, id -> dialog.dismiss() }

        // create alert dialog
        val alertDialog = alertDialogBuilder.create()

        // show it
        alertDialog.show()
    }

    private fun sendFCMPush() {
        val requestID = System.currentTimeMillis().toInt()
        val contentIntent = PendingIntent.getActivity(
            baseContext,
            0, Intent(), PendingIntent.FLAG_ONE_SHOT
        )
        val builder = NotificationCompat.Builder(baseContext)
        val pauseIntent = Intent(this, MainActivity::class.java)
        pauseIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        pauseIntent.putExtra("pause", true)
        val pausePendingIntent = PendingIntent.getActivity(
            this,
            requestID,
            pauseIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        builder.setAutoCancel(true)
        val cancelIntent = Intent(this, MainActivity::class.java)
        builder.setAutoCancel(true)
        builder.setAutoCancel(true)
            .setDefaults(Notification.DEFAULT_LIGHTS or Notification.DEFAULT_SOUND)
            .setWhen(System.currentTimeMillis())
            .setSmallIcon(R.drawable.notificationlogo)
            .setSound(Uri.parse("uri://notification_xperia.mp3"))
            .setContentTitle("Query Received ")
            .setColor(
                ContextCompat.getColor(
                    applicationContext,
                    R.color.colorPrimary
                )
            ) /*.addAction(R.drawable.ic_action_boom, "Action!", someOtherPendingIntent)*/
            .setContentText("Contact Us If Don't Get Mail Within 7 Days")
            .setContentIntent(pausePendingIntent)


//Then add the action to your notification
        val manager = baseContext.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(1, builder.build())
    }

    private fun openhelp() {
        firebaseRemoteConfigprice?.fetchAndActivate()
        whatnew = firebaseRemoteConfigprice?.getString("showads")
        if (whatnew == "yes") {
            showads()
            showintads()
            //Toast.makeText(MainActivity.this, "Showing", Toast.LENGTH_SHORT).show();
        } else if (whatnew == "no") {
            mAdView?.visibility = View.GONE

            //  Toast.makeText(MainActivity.this, "Nope", Toast.LENGTH_SHORT).show();
        }
        val taskEditText = EditText(this)
        val dialog = android.app.AlertDialog.Builder(this@MainActivity, R.style.AlertDialogedit)
            .setTitle("Your Suggestion")
            .setMessage("Provide Details And Send Us Mail ")
            .setView(taskEditText)
            .setCancelable(false)
            .setPositiveButton("Send Mail") { dialog, which ->
                val task = taskEditText.text.toString()
                sendmailintent(task)
            }
            .setNegativeButton("Cancel", null)
            .create()
        dialog.show()
    }

    private fun sendmailintent(task: String) {
        val toast = Toast.makeText(this, "SEND MAIL VIA GMAIL/YAHOO ", Toast.LENGTH_LONG)
        val view = toast.view
        view!!.background.setColorFilter(Color.parseColor("#FF104162"), PorterDuff.Mode.SRC_IN)
        val text = view.findViewById<TextView>(android.R.id.message)
        text.setTextColor(Color.WHITE)
        toast.show()
        val send = Intent(Intent.ACTION_SENDTO)
        val uriText = "mailto:" + Uri.encode("notificationapp.xenonstudio@gmail.com") +
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
        Toast.makeText(this@MainActivity, "Sharing App..", Toast.LENGTH_SHORT).show()
        ShareCompat.IntentBuilder.from(this@MainActivity)
            .setType("text/plain")
            .setChooserTitle("Share URL")
            .setText("Hey, Download This Awesome Notification History Log App - " + "https://play.google.com/store/apps/details?id=com.appnotification.notificationhistorylog")
            .startChooser()
    }

    private fun sendnotification() {
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
            .setSmallIcon(R.drawable.notificationlogo)
            .setContentTitle("Deleted Successfully !")
            .setColor(
                ContextCompat.getColor(
                    applicationContext,
                    R.color.colorPrimary
                )
            ) /*.addAction(R.drawable.ic_action_boom, "Action!", someOtherPendingIntent)*/
            .setContentText("Logs Has Been Deleted")
            .setContentIntent(pausePendingIntent)

//Then add the action to your notification
        val manager = baseContext.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(1, buildernotif.build())
    }

    private fun showlogtext() {
        val status = 0
        putSharedPreferencesString(this@MainActivity, SharedCommon.keylogtext, status.toString())
    }

    private fun opensetting() {
        val scor = SharedCommon
        var orfavc = getPreferencesInt(applicationContext, keyopensetting, 0)
        orfavc++
        putPreferencesInt(applicationContext, SharedCommon.keyopensetting, orfavc)
        val startIntent = Intent(this@MainActivity, SettingsActivity::class.java)
        startActivity(startIntent)
    }

    private fun confirm() {
        val builder = AlertDialog.Builder(this, R.style.AlertDialogStyle)
        builder.setTitle(R.string.dialog_delete_header)
        builder.setMessage(R.string.dialog_delete_text)
        builder.setNegativeButton(R.string.dialog_delete_no) { dialogInterface, i -> }
        builder.setPositiveButton(R.string.dialog_delete_yes) { dialogInterface, i ->
            truncate()
            sendnotification()
        }
        builder.show()
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
        } catch (e: Exception) {
            if (Const.DEBUG) e.printStackTrace()
        }
    }

    private fun doFirstRun2() {
        val settings = getSharedPreferences("FIRSTRUNTEXTrateitbaby", MODE_PRIVATE)
        if (settings.getBoolean("isFirstRunDialogBoxtextrb", true)) {
            val editor = settings.edit()
            editor.putBoolean("isFirstRunDialogBoxtextrb", false)
            editor.commit()
            showratingbar()
        }
    }

    private fun doFirstRun() {
        val settings = getSharedPreferences("FIRSTRUNTEXT111", MODE_PRIVATE)
        if (settings.getBoolean("isFirstRunDialogBoxtext111", true)) {
            val editor = settings.edit()
            editor.putBoolean("isFirstRunDialogBoxtext111", false)
            editor.commit()
            sendnoifications()
            val builder = android.app.AlertDialog.Builder(this@MainActivity)
            builder.setIcon(R.drawable.notificationlogo)
            builder.setTitle("Welcome !")
            builder.setMessage("Please Make Sure That You Provide Necessary Permissions ")
            builder.setCancelable(false)
            builder.setPositiveButton("Where") { dialogInterface, i ->
                showtipshowcase()
                dialogInterface.dismiss()
            }
            builder.setNeutralButton("GOT IT") { dialogInterface, i -> dialogInterface.dismiss() }
            builder.setNeutralButton("Whats New !") { dialogInterface, i ->
                val builder = android.app.AlertDialog.Builder(this@MainActivity)
                builder.setIcon(R.drawable.notificationlogo)
                builder.setTitle("New Update  :-$versionName")
                builder.setMessage("Multi-Language Support \n View Notification App Wise \nUI Changes \nBug Fixed \n")
                builder.setCancelable(false)
                builder.setPositiveButton("Got It") { dialogInterface, i ->
                    showtipshowcase()
                    dialogInterface.dismiss()
                }
                val dialog = builder.create()
                dialog.show()
            }
            val dialog = builder.create()
            dialog.show()
        }
    }

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
            .setSmallIcon(R.drawable.notificationlogo)
            .setContentTitle("You Have Latest Version Of The App ")
            .setColor(
                ContextCompat.getColor(
                    applicationContext,
                    R.color.colorPrimary
                )
            ) /*.addAction(R.drawable.ic_action_boom, "Action!", someOtherPendingIntent)*/
            .setContentText("Now Save Battery With The New Dark Mode !")
            .setContentIntent(pausePendingIntent)

//Then add the action to your notification
        val manager = baseContext.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(1, buildernotif.build())
    }

    private fun export() {
        if (!ExportTask.exporting) {
            val exportTask = ExportTask(this, findViewById(android.R.id.content))
            exportTask.execute()
            doFirstRun2()
        }
    }
}