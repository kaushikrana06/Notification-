package com.appnotification.notificationhistorylog.ui

import android.content.ActivityNotFoundException
import android.content.ContentValues
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.BaseColumns
import android.text.format.DateUtils
import android.util.Log
import android.view.Menu
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ShareCompat
import androidx.palette.graphics.Palette
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.appnotification.notificationhistorylog.BuildConfig
import com.appnotification.notificationhistorylog.CommonCl.SharedCommon
import com.appnotification.notificationhistorylog.CommonCl.SharedCommon.getPreferencesInt
import com.appnotification.notificationhistorylog.CommonCl.SharedCommon.key1
import com.appnotification.notificationhistorylog.CommonCl.SharedCommon.putPreferencesInt
import com.appnotification.notificationhistorylog.R
import com.appnotification.notificationhistorylog.ads.InterstitialAdsManager
import com.appnotification.notificationhistorylog.db.DbHelperGmail
import com.appnotification.notificationhistorylog.misc.Const
import com.appnotification.notificationhistorylog.misc.DatabaseHelper
import com.appnotification.notificationhistorylog.misc.Util.getAppIconFromPackage
import com.appnotification.notificationhistorylog.misc.Util.getAppNameFromPackage
import com.facebook.ads.*
import com.google.android.gms.ads.InterstitialAd
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import org.json.JSONException
import org.json.JSONObject
import smartdevelop.ir.eram.showcaseviewlib.GuideView
import smartdevelop.ir.eram.showcaseviewlib.config.DismissType
import smartdevelop.ir.eram.showcaseviewlib.config.Gravity
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class DetailsActivity : AppCompatActivity() {
    var whatnew: String? = null
    var showallnotf: String? = null
    var shownotif: String? = null
    var showtuto: String? = null
    var showfav: String? = null
    var livenotice: String? = null
    var versionfirebase: String? = null
    var fbadview: AdView? = null
    var texttext: String? = null

    //    ImageView ivNotification;
    var shake: Animation? = null
    var tvName: TextView? = null
    var txtinv: TextView? = null
    var contentText: String? = null
    var tvText: TextView? = null
    var tvDate: TextView? = null
    var view1: View? = null
    var txtgmail: String? = null
    var txtwhatsapp: String? = null
    var txtinstagram: String? = null

    //    DbHelperIdeas dbHelper;
    var dbHelpgmail: DbHelperGmail? = null
    var mAdapter: ArrayAdapter<String>? = null
    var mAdaptergmail: ArrayAdapter<String>? = null
    var lstTask: ListView? = null
    var lstTaskgmail: ListView? = null
    var versionCode = BuildConfig.VERSION_CODE
    var versionName = BuildConfig.VERSION_NAME
    var firebaseRemoteConfigprice: FirebaseRemoteConfig? = null
    var appid = BuildConfig.APPLICATION_ID
    private val buttonjson: Button? = null
    private var buttonNotifySettings: LinearLayout? = null
    private var mUserRef: DatabaseReference? = null
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
            startActivity(Intent(this@DetailsActivity, NewMainActivity::class.java))
            finish()
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
    private var mAdView: AdView? = null
    private var adListener: AdListener? = null
    private var interstitialAdsManager: InterstitialAdsManager? = null
    private var packageName = null
    private var appUid = 0
    private val mInterstitialAd: InterstitialAd? = null
    private val recyclerView: RecyclerView? = null
    private val swipeRefreshLayout: SwipeRefreshLayout? = null
    private var mAuth: FirebaseAuth? = null
    private var dialog: AlertDialog? = null
    private var textOpenApp: TextView? = null
    private var appJson: String? = null
    private fun showPopup(v: View) {
        /* PopupMenu popup = new PopupMenu(this, v);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.main, popup.getMenu());
        popup.show();*/
        val popup = PopupMenu(this, v)
        popup.setOnMenuItemClickListener { item ->
            if (item.itemId == R.id.menu_share) {
                shareapplink()
            }
            false
        }
        popup.inflate(R.menu.details)
        popup.show()
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)
        // Action bar button handled
        val backIcon = findViewById<ImageView>(R.id.back_image)
        val deleteIcon = findViewById<ImageView>(R.id.delete_image)
        val favIcon = findViewById<ImageView>(R.id.fav_image)
        val sendIcon = findViewById<ImageView>(R.id.send_image)
        val moreIcon = findViewById<ImageView>(R.id.menu_image)
        backIcon.setOnClickListener { finish() }
        deleteIcon.setOnClickListener { confirmDelete() }
        favIcon.setOnClickListener { v ->
            v.startAnimation(shake)
            addToFavorites()
        }
        sendIcon.setOnClickListener {
            Toast.makeText(
                this@DetailsActivity,
                "Sharing Notification Details..",
                Toast.LENGTH_SHORT
            ).show()
            ShareCompat.IntentBuilder.from(this@DetailsActivity)
                .setType("text/plain")
                .setChooserTitle("")
                .setText(
                    """
    App :${tvName?.text}
    Notification : ${tvText?.text}
    Date & Time :${tvDate?.text}
    """.trimIndent()
                )
                .startChooser()
        }
        moreIcon.setOnClickListener { view ->
            showPopup(view)
            // ((NewMainActivity)getActivity()).openactionmenu();
        }
        interstitialAdsManager = InterstitialAdsManager(this)
        interstitialAdsManager?.show()
        val buttonNotifySettings: LinearLayout = findViewById(R.id.button_notification)
        buttonNotifySettings.setOnClickListener(View.OnClickListener {
            try {
                val intent = Intent()
                intent.action = "android.settings.APP_NOTIFICATION_SETTINGS"
                //intent.putExtra("android.provider.extra.APP_PACKAGE", packageName)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            } catch (e: Exception) {
                if (Const.DEBUG) e.printStackTrace()
            }
        })
        textOpenApp = findViewById(R.id.text_openapp)
        val buttonOpenApp = findViewById<LinearLayout>(R.id.button_openapp)
        buttonOpenApp.setOnClickListener {
            if (packageName?.contains("systemui") == true) {
                //Toast.makeText(DetailsActivity.this, "", Toast.LENGTH_SHORT).show();
                textOpenApp?.text = "System App Cannot Be Opened"
                //                    openplay.setVisibility(View.GONE);
            } else if (packageName == "android") {
                //Toast.makeText(DetailsActivity.this, "", Toast.LENGTH_SHORT).show();
                textOpenApp?.text = "System App Cannot Be Opened"
                //                    openplay.setVisibility(View.GONE);
            } else if (packageName == "com.motorola.ccc.ota") {
                //Toast.makeText(DetailsActivity.this, "", Toast.LENGTH_SHORT).show();
                textOpenApp?.text = "System App Cannot Be Opened"
                //                    openplay.setVisibility(View.GONE);
            } else {
                Toast.makeText(this@DetailsActivity, "Opening..", Toast.LENGTH_SHORT).show()
                startActivity(packageManager.getLaunchIntentForPackage(packageName!!))
            }
        }
        val tvJSON = findViewById<TextView>(R.id.json)
        val buttonMoreInfo = findViewById<LinearLayout>(R.id.button_moreinfo)
        buttonMoreInfo.setOnClickListener {
            tvJSON.text = appJson
            tvJSON.visibility = View.VISIBLE
        }

//        dbHelper = new DbHelperIdeas(this);
        lstTask = findViewById(R.id.lstTaskidea)
        txtinv = findViewById(R.id.txtinv)
        dbHelpgmail = DbHelperGmail(this)
        lstTaskgmail = findViewById(R.id.lsttakgmail)
        txtinv = findViewById(R.id.txtinv)


//        firstrun();

//        btnselevct = findViewById(R.id.btnselevct);
//        btnselevct.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//
//                if (packageName.contains("gm")) {
//
//                    String task = txtgmail + "\n" + contentText;
//                    dbHelpgmail.insertNewTask(task);
//                    //Toast.makeText(DetailsActivity.this, "", Toast.LENGTH_SHORT).show();
//                    Toast.makeText(DetailsActivity.this, "Add To :" + tvName.getText() + " Section", Toast.LENGTH_SHORT).show();
//
//                } else if (packageName.equals("com.whatsapp")) {
//                    //Toast.makeText(DetailsActivity.this, "", Toast.LENGTH_SHORT).show();
//
//                    Toast.makeText(DetailsActivity.this, "Add To :" + tvName.getText() + " Section", Toast.LENGTH_SHORT).show();
//
//
//                } else {
//
//                    btnselevct.setText("Click To Add This To Favorite Section");
//                    Toast.makeText(DetailsActivity.this, "We Dont Have Section For This App, You Can Add This To Your Favorite Section", Toast.LENGTH_SHORT).show();
//                    btnselevct.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View view) {
//                            String task = String.valueOf(txtinv.getText());
//                            addToFavorites();
////                            dbHelper.insertNewTask(task);
////
////                            Toast.makeText(DetailsActivity.this, "Added To Favorite ", Toast.LENGTH_SHORT).show();
//                        }
//                    });
//                }
//            }
//        });
        shake = AnimationUtils.loadAnimation(this, R.anim.shake)

//        openplay = findViewById(R.id.buttonopen);
//        openplay.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                if (packageName.contains("systemui")) {
//                    //Toast.makeText(DetailsActivity.this, "", Toast.LENGTH_SHORT).show();
//                    textOpenApp.setText("System App Cannot Be Opened");
////                    openplay.setVisibility(View.GONE);
//
//                } else if (packageName.equals("android")) {
//                    //Toast.makeText(DetailsActivity.this, "", Toast.LENGTH_SHORT).show();
//                    textOpenApp.setText("System App Cannot Be Opened");
////                    openplay.setVisibility(View.GONE);
//                } else {
//                    Toast.makeText(DetailsActivity.this, "Opening PlayStore..", Toast.LENGTH_SHORT).show();
//                    final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
//                    try {
//                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + packageName)));
//                    } catch (android.content.ActivityNotFoundException anfe) {
//                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + packageName)));
//                    }
//                }
//            }
//        });
        firebaseRemoteConfigprice = FirebaseRemoteConfig.getInstance()
        val configSettings = FirebaseRemoteConfigSettings.Builder().build()
        firebaseRemoteConfigprice?.setConfigSettingsAsync(configSettings)
        val pricedata: MutableMap<String, Any> = HashMap()
        pricedata["showads"] = "yn"
        pricedata["showfacebookads"] = "yn"
        firebaseRemoteConfigprice?.setDefaultsAsync(pricedata)

//        checkadsstatus();

        //Facebook ADS
        //ADS+Firebase
        mAdView = AdView(this, getString(R.string.fb_banner_ad), AdSize.BANNER_HEIGHT_50)
        showads()
        mAdView?.loadAd(mAdView?.buildLoadAdConfig()?.withAdListener(adListener)?.build())
        (findViewById<View>(R.id.fb_container) as LinearLayout).addView(mAdView)
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
        view1 = findViewById(R.id.icon)
        //        view3 = findViewById(R.id.buttonjson);
    }

    override fun onDestroy() {
        if (mAdView != null) {
            mAdView?.destroy()
        }
        super.onDestroy()
    }

    //    private void checkadsstatus() {
    //
    //
    //        firebaseRemoteConfigprice.fetch(0)
    //                .addOnCompleteListener(new OnCompleteListener<Void>() {
    //                    @Override
    //                    public void onComplete(@NonNull Task<Void> task) {
    //
    //                        Log.e("TaskError", "info" + firebaseRemoteConfigprice.getInfo().getLastFetchStatus());
    //
    //
    //                        Log.e("TaskError", "firebaseremote" + firebaseRemoteConfigprice.getString("btn_text"));
    //
    //                        if (task.isSuccessful()) {
    //
    //
    //                            firebaseRemoteConfigprice.fetchAndActivate();
    //                            /*txt600.setText(firebaseRemoteConfigprice.getString("txt600"));
    //                            txt1500.setText(firebaseRemoteConfigprice.getString("txt1500"));
    //                            txt3200.setText(firebaseRemoteConfigprice.getString("txt3200"));
    //                            txt5000.setText(firebaseRemoteConfigprice.getString("txt5000"));
    //
    // pricedata.put("showfavtab", "yn");
    //        pricedata.put("showtutorial", "yn");
    //        pricedata.put("showbrowseallnotification", "yn");
    //        pricedata.put("showbrowsenotification", "yn");
    //
    //*/
    //                            whatnew = (firebaseRemoteConfigprice.getString("showads"));
    //                            showallnotf = (firebaseRemoteConfigprice.getString("showfacebookads"));
    //
    //
    //                            if (showallnotf.equals("yes")) {
    ////                                showfacebookads();
    //                                //Toast.makeText(NewMainActivity.this, "Showing", Toast.LENGTH_SHORT).show();
    //                            } else if (showallnotf.equals("no")) {
    //                                mAdView.setVisibility(View.GONE);
    //
    //                                com.facebook.ads.AdView adView2 = new com.facebook.ads.AdView(DetailsActivity.this, "913703148991807_913704952324960", com.facebook.ads.AdSize.BANNER_HEIGHT_50);
    //
    //                                adView2.setVisibility(View.GONE);
    //                                // Toast.makeText(NewMainActivity.this, "Nope", Toast.LENGTH_SHORT).show();
    //
    //                                Log.e("AdsStatus", "Not Showing");
    //
    //                            }
    //
    //                            Log.e("TaskError", "firebaseremote" + firebaseRemoteConfigprice.getString("btn_text"));
    //
    //
    //                                   /* Picasso.get().load(firebaseRemoteConfigprice.getString("image_link"))
    //                                            .into(img);*/
    //                        } else {
    //
    //
    //                            String exp = ("" + task.getException().getMessage());
    //                            if (exp.equals("null")) {
    //
    //                                whatnew = ("Server Not Responding ");
    //
    //
    //                                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(DetailsActivity.this);
    //
    //
    //                            } else {
    //                                Log.e("TaskError", "taskexcep :" + task.getException().getMessage() + task.getException() + task);
    //                                Toast.makeText(DetailsActivity.this, "Internet Connection Error", Toast.LENGTH_SHORT).show();
    //                                View parentLayout = findViewById(android.R.id.content);
    //
    //                                Snackbar snackbar = Snackbar
    //                                        .make(parentLayout, "Internet Connection Error", Snackbar.LENGTH_INDEFINITE);
    //                                View snackbarView = snackbar.getView();
    //                                snackbarView.setBackgroundColor(Color.parseColor("#FF104162"));
    //                                snackbar.setAction("Retry", new View.OnClickListener() {
    //                                    @Override
    //                                    public void onClick(View view) {
    //                                        //  checkConnection();
    //                                        snackbar.dismiss();
    //                                    }
    //                                });
    //
    //                                snackbar.show();
    //                            }
    //                        }
    //                    }
    //                });
    //    }
    //    private void showfacebookads() {
    //        AudienceNetworkAds.initialize(DetailsActivity.this);
    //
    //        String adid = "913703148991807_913704952324960";
    //        // fbadview = new AdView(DetailsActivity.this, "913703148991807_913704952324960", AdSize.BANNER_HEIGHT_50);
    //        com.facebook.ads.AdView adView2 = new com.facebook.ads.AdView(this, "913703148991807_913704952324960", com.facebook.ads.AdSize.BANNER_HEIGHT_50);
    //        // Find the Ad Container
    //        LinearLayout adContainer = findViewById(R.id.banner_container);
    //
    //        // Add the ad view to your activity layout
    //        adContainer.addView(adView2);
    //        adView2.loadAd();
    //    }
    private fun firstrun() {
        val settings = getSharedPreferences("FIRSTRUNINFOdet11", MODE_PRIVATE)
        if (settings.getBoolean("isFirstRunDialogBoxtexdet1", true)) {
            val editor = settings.edit()
            editor.putBoolean("isFirstRunDialogBoxtexdet1", false)
            editor.commit()
            // sendnoifications();
            val builder = android.app.AlertDialog.Builder(this@DetailsActivity)
            builder.setIcon(R.drawable.ic_splash_logo)
            builder.setTitle("How To Use!")
            builder.setMessage("Want To Know How To Use This Feature ?")
            builder.setCancelable(false)
            builder.setPositiveButton("No") { dialogInterface, _ -> dialogInterface.dismiss() }
            builder.setNegativeButton("Yes") { _, _ -> showcasetimebaby() }
            val dialog = builder.create()
            dialog.show()
            val positive = dialog.getButton(DialogInterface.BUTTON_POSITIVE)
            positive.setTextColor(resources.getColor(R.color.colorText))
            val neutral = dialog.getButton(DialogInterface.BUTTON_NEGATIVE)
            neutral.setTextColor(resources.getColor(R.color.colorText))
        }
    }

    //    private void showsecondshowcase() {
    //        new GuideView.Builder(DetailsActivity.this)
    //                .setTitle("Open App")
    //                .setContentText("From Here You Can Open Apps Directly In Your Phone")
    //                .setGravity(smartdevelop.ir.eram.showcaseviewlib.config.Gravity.auto)
    //                .setTargetView(view2)
    //                .setDismissType(DismissType.outside) //optional - default dismissible by TargetView
    //                .setGuideListener(new GuideListener() {
    //                    @Override
    //                    public void onDismiss(View view) {
    //                        showsthirdshowcase();
    //                    }
    //                })
    //                .build()
    //                .show();
    //    }
    //    private void showsthirdshowcase() {
    //
    //
    //        new GuideView.Builder(DetailsActivity.this)
    //                .setTitle("More Info")
    //                .setContentText("From Here You Can View Details About Notifications ")
    //                .setGravity(smartdevelop.ir.eram.showcaseviewlib.config.Gravity.auto)
    //                .setTargetView(view3)
    //                .setDismissType(DismissType.outside) //optional - default dismissible by TargetView
    //                .setGuideListener(new GuideListener() {
    //                    @Override
    //                    public void onDismiss(View view) {
    //                        showforthshowcase();
    //                    }
    //                })
    //                .build()
    //                .show();
    //
    //    }
    //    private void showforthshowcase() {
    //
    //        new GuideView.Builder(this)
    //                .setTitle("More")
    //                .setContentText("From Above Menu You Can Delete,Favorite,Send any Notification")
    //                .setGravity(Gravity.auto) //optional
    //                .setDismissType(DismissType.outside) //optional - default DismissType.targetView
    //                .setTargetView(view3)
    //                .setContentTextSize(14)//optional
    //                .setTitleTextSize(16)//optional
    //                .build()
    //                .show();
    //        //  shake();
    //    }
    private fun showcasetimebaby() {
        GuideView.Builder(this@DetailsActivity)
            .setTitle("Play Store")
            .setContentText("From Here You Will Be Able to Open Apps In Play Store")
            .setGravity(Gravity.auto)
            .setTargetView(view1)
            .setDismissType(DismissType.outside) //optional - default dismissible by TargetView
            .setGuideListener {
                //                        showsecondshowcase();
            }
            .build()
            .show()
    }

    override fun onPause() {
        if (dialog != null && dialog!!.isShowing) {
            dialog?.dismiss()
            dialog = null
        }
        super.onPause()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.details, menu)
        return true
    }

    //    @Override
    //    public boolean onPrepareOptionsMenu(Menu menu) {
    //        try {
    //            ivNotification = menu.findItem(R.id.fav).getActionView().findViewById(R.id.ivNotification);
    //            txtinv.setText("App :" + tvName.getText() + "\nNotification : " + tvText.getText() + "\nDate & Time :" + tvDate.getText());
    //            txtgmail = ("\nNotification : " + tvText.getText() + "\nDate & Time :" + tvDate.getText());
    //            String tt = "Hello";
    //            String tet = ("App :" + tvName.getText() + "\nNotification : " + tvText.getText() + "\nDate & Time :" + tvDate.getText());
    //            ivNotification.setOnClickListener(new View.OnClickListener() {
    //                @Override
    //                public void onClick(View view) {
    //                    runOnUiThread(new Runnable() {
    //                        @Override
    //                        public void run() {
    //                            shake();
    //                            addToFavorites();
    ////                            String task = String.valueOf(txtinv.getText());
    ////                            dbHelper.insertNewTask(task);
    ////                            Toast.makeText(DetailsActivity.this, "Added To Favorite ", Toast.LENGTH_SHORT).show();
    //                        }
    //                    });
    //
    //                }
    //            });
    //
    //            ivNotification.setOnLongClickListener(new View.OnLongClickListener() {
    //                @Override
    //                public boolean onLongClick(View view) {
    //                    runOnUiThread(new Runnable() {
    //                        @Override
    //                        public void run() {
    ////                            final Vibrator vibe1 = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
    ////                            Intent startIntent = new Intent(DetailsActivity.this, FavActivity.class);
    ////                            startIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    ////                            startActivity(startIntent);
    //                            addToFavorites();
    //                        }
    //                    });
    //
    //
    //                    return false;
    //                }
    //            });
    //        } catch (Exception e) {
    //            Log.e(TAG, "onPrepareOptionsMenu :  Exception: " + e.toString());
    //            e.printStackTrace();
    //        }
    //        return super.onPrepareOptionsMenu(menu);
    //    }
    //
    //    @Override
    //    public boolean onOptionsItemSelected(MenuItem item) {
    //        if (item.getItemId() == R.id.menu_delete) {
    //            confirmDelete();
    //        }
    //        if (item.getItemId() == R.id.menu_share) {
    //            shareapplink();
    //        }
    //        if (item.getItemId() == R.id.add) {
    //            Toast.makeText(DetailsActivity.this, "Sharing Notification Details..", Toast.LENGTH_SHORT).show();
    //
    //
    //            ShareCompat.IntentBuilder.from(DetailsActivity.this)
    //                    .setType("text/plain")
    //                    .setChooserTitle("")
    //                    .setText("App :" + tvName.getText() + "\nNotification : " + tvText.getText() + "\nDate & Time :" + tvDate.getText())
    //                    .startChooser();
    //        }
    //
    //        return super.onOptionsItemSelected(item);
    //    }
    //
    //    private void shake() {
    //        ivNotification.startAnimation(shake);
    //    }
    private fun addToFavorites() {
        val helper = DatabaseHelper(this)
        val db = helper.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(DatabaseHelper.PostedEntry.COLUMN_NAME_FAVORITE, 1)
        db.update(DatabaseHelper.PostedEntry.TABLE_NAME, contentValues, "_ID=$id", null)
        db.close()
        helper.close()
        Toast.makeText(this, getString(R.string.message_add_favorite), Toast.LENGTH_SHORT).show()
    }

    private fun shareapplink() {
        Toast.makeText(this@DetailsActivity, "Sharing App..", Toast.LENGTH_SHORT).show()
        ShareCompat.IntentBuilder.from(this@DetailsActivity)
            .setType("text/plain")
            .setChooserTitle("Share URL")
            .setText("Hey, Download This App - https://play.google.com/store/apps/details?id=$packageName")
            .startChooser()
    }

    private fun loadDetails(id: String) {
        var json: JSONObject? = null
        appJson = "error"
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
                    appJson = json.toString(2)
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


//        buttonjson = findViewById(R.id.buttonjson);
//        buttonjson.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                //Toast.makeText(DetailsActivity.this, "", Toast.LENGTH_SHORT).show();
//
//                tvJSON.setVisibility(View.VISIBLE);
//                buttonjson.setVisibility(View.GONE
//                );
//
//
//            }
//        });
        val icon = findViewById<ImageView>(R.id.icon)
        icon.setOnClickListener {
            if (packageName?.contains("systemui") == true) {
                //Toast.makeText(DetailsActivity.this, "", Toast.LENGTH_SHORT).show();
                textOpenApp?.text = "System App Cannot Be Opened"
                //                    openplay.setVisibility(View.GONE);
            } else if (packageName == "android") {
                //Toast.makeText(DetailsActivity.this, "", Toast.LENGTH_SHORT).show();
                textOpenApp?.text = "System App Cannot Be Opened"
                //                    openplay.setVisibility(View.GONE);
            } else {
                Toast.makeText(this@DetailsActivity, "Opening PlayStore..", Toast.LENGTH_SHORT)
                    .show()
                val appPackageName =
                    getPackageName() // getPackageName() from Context or Activity object
                try {
                    startActivity(
                        Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("market://details?id=$packageName")
                        )
                    )
                } catch (anfe: ActivityNotFoundException) {
                    startActivity(
                        Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("https://play.google.com/store/apps/details?id=$packageName")
                        )
                    )
                }
            }
        }
        val card = findViewById<LinearLayout>(R.id.card)
        //        CardView buttons = findViewById(R.id.buttons);
        if (json != null) {
            val packageName = json?.optString("packageName", "???").toString()
            val titleText = json?.optString("title")
            contentText = json?.optString("text")
            texttext = contentText
            val text = """$titleText
$contentText""".trim { it <= ' ' }
            if ("" != text) {
                card.visibility = View.VISIBLE
                //ImageView icon = findViewById(R.id.icon);
                icon.setImageDrawable(getAppIconFromPackage(this, packageName))

                //ImageView image = (ImageView) findViewById(R.id.icon);
                // ColorFilter test = image.getColorFilter();
                geticoncolor()
                //Color tagColor = Color.rgb(Color.red(color),Color.green(color), Color.blue(color));
                // imageview.setTag(tagColor);
                //tagColor = (Color) imageview.getTag();
                //Toast.makeText(this, ""+test, Toast.LENGTH_SHORT).show();
                val tvName: TextView = findViewById(R.id.name)
                tvName.text = getAppNameFromPackage(this, packageName, false)
                val tvText:TextView = findViewById(R.id.text)
                tvText.text = text
                val tvDate:TextView = findViewById(R.id.date)
                if (SHOW_RELATIVE_DATE_TIME) {
                    tvDate.text = DateUtils.getRelativeDateTimeString(
                        this,
                        json.optLong("systemTime"),
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
                    tvDate.text = format.format(json.optLong("systemTime"))
                }
                try {
                    val app = this.packageManager.getApplicationInfo(packageName, 0)
                    buttonNotifySettings?.visibility = View.VISIBLE
                    appUid = app.uid
                } catch (e: PackageManager.NameNotFoundException) {
                    if (Const.DEBUG) e.printStackTrace()
                    buttonNotifySettings?.visibility = View.GONE
                }
            } else {
                card.visibility = View.GONE
            }
        } else {
            card.visibility = View.GONE
            buttonNotifySettings?.visibility = View.GONE
        }
    }

    private fun geticoncolor() {
        try {
            if (packageName?.contains("systemui") == true) {
                //Toast.makeText(DetailsActivity.this, "", Toast.LENGTH_SHORT).show();
                textOpenApp?.text = "System App Cannot Be Opened"
                //                openplay.setVisibility(View.GONE);
            } else if (packageName == "android") {
                //Toast.makeText(DetailsActivity.this, "", Toast.LENGTH_SHORT).show();
                textOpenApp?.text = "System App Cannot Be Opened"
                //                openplay.setVisibility(View.GONE);
            } else {
                val icon = findViewById<ImageView>(R.id.icon)
                icon.setImageDrawable(getAppIconFromPackage(this, packageName))

                //Bitmap bitmap = BitmapFactory.decodeResource(getResources(), geticoncolor());
                val bitmap3 = (icon.drawable as BitmapDrawable).bitmap

                //BitmapDrawable drawable = (BitmapDrawable) icon.getDrawable();
                //Bitmap bitmap2 = drawable.getBitmap();
                Palette.from(bitmap3).generate {
                    //                        //work with the palette here
//                        int defaultValue = 0x000000;
//                        int vibrant = palette.getVibrantColor(defaultValue);
//                        int vibrantLight = palette.getLightVibrantColor(defaultValue);
//                        int vibrantDark = palette.getDarkVibrantColor(defaultValue);
//                        int muted = palette.getMutedColor(defaultValue);
//                        int mutedLight = palette.getLightMutedColor(defaultValue);
//                        int mutedDark = palette.getDarkMutedColor(defaultValue);
//
//                        Log.d("Color Codes", "vibrant" + vibrant + "vibrantDark" + vibrantDark);
//                        if (vibrantDark == 0) {
//
//                            textOpenApp.setTextColor(vibrant);
//                            //openplay.setTextColor(vibrant);
//                        } else {
//                            openapp.setTextColor(vibrantDark);
//                        }
//                        // Toast.makeText(DetailsActivity.this, ""+vibrantDark+vibrant, Toast.LENGTH_LONG).show();
//                        if (vibrant == 0 && vibrantDark == 0) {
//
//                            openapp.setTextColor(mutedDark);
//
////                            ActionBar actionBar = getSupportActionBar();
////                            actionBar.setBackgroundDrawable(new ColorDrawable(mutedDark));
//
//                            //actionBar.setTitle(Html.fromHtml("<vibrantLight>Hello World</font>"));
////                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
////                                Window window = getWindow();
////                                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
////                                window.setStatusBarColor(mutedDark);
////                            }
//                            //openplay.setTextColor(vibrant);
//                        } else {
////                            ActionBar actionBar = getSupportActionBar();
////                            actionBar.setBackgroundDrawable(new ColorDrawable(vibrant));
////
////                            //actionBar.setTitle(Html.fromHtml("<vibrantLight>Hello World</font>"));
////                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
////                                Window window = getWindow();
////                                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
////                                window.setStatusBarColor(vibrant);
////                            }
//                        }
//                        // Toast.makeText(DetailsActivity.this, ""+vibrant+vibrantDark+mutedLight+mutedDark, Toast.LENGTH_SHORT).show();
//                /*vibrantView.setBackgroundColor(vibrant);
//                vibrantLightView.setBackgroundColor(vibrantLight);
//                vibrantDarkView.setBackgroundColor(vibrantDark);
//                mutedView.setBackgroundColor(muted);
//                mutedLightView.setBackgroundColor(mutedLight);
//                mutedDarkView.setBackgroundColor(mutedDark);*/
                }
            }
        } catch (e: Exception) {
            // This will catch any exception, because they are all descended from Exception
            //Toast.makeText(this, ""+e, Toast.LENGTH_SHORT).show();
            mAuth = FirebaseAuth.getInstance()
            val currentUser = mAuth?.currentUser
            if (currentUser == null) {
                Log.e("user", "null")
            } else {
                mUserRef = mAuth?.currentUser?.uid?.let {
                    FirebaseDatabase.getInstance().reference.child("Detail-Activty").child(
                        it
                    )
                }
                val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZZZZZ", Locale.getDefault())
                val datte = format.format(Date())
                val idtimedate = datte.substring(5, 10)
                val idtime = datte.substring(11, 19)
                mUserRef?.child("packageName")?.setValue(packageName)
                mUserRef?.child("Date")?.setValue(datte)
                mUserRef?.child("Exception")?.setValue(e)

                //username = currentUser.getUid();
                //SAVEDATAREPORT
                //savereport(currentUser);
            }
        }
    }

    private fun finishWithToast() {
        Toast.makeText(this, R.string.details_error, Toast.LENGTH_SHORT).show()
        finish()
    }

    private fun confirmDelete() {
        if (dialog != null && dialog?.isShowing == true) {
            dialog?.dismiss()
        }
        val dialog: AlertDialog = AlertDialog.Builder(this)
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
            //intent.putExtra("android.provider.extra.APP_PACKAGE", packageName)
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