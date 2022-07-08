package com.appnotification.notificationhistorylog

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.PorterDuff
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SwitchCompat
import androidx.appcompat.widget.Toolbar
import com.appnotification.notificationhistorylog.CommonCl.SharedCommon
import com.appnotification.notificationhistorylog.CommonCl.SharedCommon.getPreferencesInt
import com.appnotification.notificationhistorylog.CommonCl.SharedCommon.key1
import com.appnotification.notificationhistorylog.CommonCl.SharedCommon.putPreferencesInt
import com.appnotification.notificationhistorylog.ui.NewMainActivity
import com.appnotification.notificationhistorylog.ui.activities.PrivacyActivity
import com.appnotification.notificationhistorylog.ui.activities.TermsActivity
import com.facebook.ads.*
import timber.log.Timber
import java.util.*

class SettingsActivity : AppCompatActivity() {
    //    private String whatnew;
    //    private String username;
    //    private TextView txtnotification;
    //    private ProgressDialog mprogreeinternet;
    //    private Thread splashTread;
    //    private Spinner splan;
    private val PrivacyUrl = "https://xenonstudio.in/privacynotificationapp"
    private val TermsUrl =
        "http://imagerecognitionapp.blogspot.com/2018/10/terms-and-conditions.html"
    private val apppackagename = "com.thetechroot.vision"
    private val versionCode = BuildConfig.VERSION_CODE
    private val versionName = BuildConfig.VERSION_NAME

    //    private final String appid = BuildConfig.APPLICATION_ID;
    //    private FirebaseRemoteConfig firebaseRemoteConfigprice;
    //    private String datedata;
    //    private InterstitialAd mInterstitialAd;
    //    private AdListener adListener;
    private var mAdView: AdView? = null
    private var adListener: AdListener? = null
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

    //    @Override
    //    protected void onResume() {
    //        // updatedata();
    //        FirebaseUser currentUser = mAuth.getCurrentUser();
    //        //Toast.makeText(this, ""+currentUser, Toast.LENGTH_SHORT).show();
    //        if (currentUser == null) {
    //            sendToStart();
    //        } else {
    //            username = currentUser.getUid();
    //            //SAVEDATAREPORT
    //            savereport(currentUser);
    //            int i = SharedCommon.getPreferencesInt(getApplicationContext(), key1, 0);
    //            museref.child("D-Ads Clicked").setValue(i);
    //        }
    //        super.onResume();
    //    }
    //    private void showdatadilog() {
    //        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(SettingsActivity.this, R.style.AlertDialog);
    //        builder.setIcon(R.drawable.notificationlogo);
    //        builder.setTitle("Note: ");
    //        builder.setMessage("(Please Check The Box To Enable It)\n" +
    //                "If the setting is activated it will report the following to us:\n" +
    //                "\n" +
    //                "When a connection attempt is successful\n" +
    //                "When a connection attempt fails \n" +
    //                "If app crashes or find bugs\n" +
    //                "Which version of the app that is running\n" +
    //                "Error Reporting \n" +
    //                "Which language the app is been used \n" +
    //                "How long the app has been running And Similar Reports\n\n" +
    //                "*Your Notification Logs or Notification Detail or any sensitive information is not captured or saved");
    //
    //        builder.setCancelable(false)
    //                .setPositiveButton("Allow", new DialogInterface.OnClickListener() {
    //                    @Override
    //                    public void onClick(DialogInterface dialog, int which) {
    //                        final Switch checkBox = findViewById(R.id.datacb);
    //                        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(SettingsActivity.this);
    //                        final SharedPreferences.Editor editor = preferences.edit();
    //                        checkBox.setChecked(preferences.contains("checked") && preferences.getBoolean("checked", false) == true);
    //
    //                        editor.putBoolean("checked", true);
    //                        editor.apply();
    //                        signInAnonymously();
    //                    }
    //                })
    //                .setPositiveButton("Know More", new DialogInterface.OnClickListener() {
    //                    @Override
    //                    public void onClick(DialogInterface dialog, int which) {
    //                        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(SettingsActivity.this, R.style.AlertDialog);
    //
    //                        builder.setIcon(R.drawable.notificationlogo);
    //                        builder.setTitle("More Info ");
    //                        builder.setMessage("We log details that help app to improve, how our users are using the app and way to avoid crash in anonymous way \n" +
    //                                "We Will Never Collect data which are sensitive such as your notification, your notification details, your message or any sensitive information" +
    //                                "\n\n Your Sensitive Data is completely safe and not been collected/saved \n\n You Can Enable/Disable Whenever You Want \n\n Your Data Will Be Deleted If You Disable The Setting");
    //
    //                        builder.setCancelable(false)
    //                                .setNegativeButton("Cancel", null);
    //
    //                        AlertDialog dialog1 = builder.create();
    //                        dialog1.show();
    //                    }
    //                })
    //                .setNegativeButton("Cancel", null);
    //
    //        AlertDialog dialog = builder.create();
    //        dialog.show();
    //                        /*WebView webView = new WebView(SettingsActivity.this);
    //
    //                        webView.loadUrl(TermsUrl);*/
    //    }
    //    private void checkadsstatus() {
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
    //
    //*/
    //                            whatnew = (firebaseRemoteConfigprice.getString("showads"));
    //                            showdataus = (firebaseRemoteConfigprice.getString("showdatausage"));
    //                            noticedata = (firebaseRemoteConfigprice.getString("noticefordata"));
    //
    ////                            if (showdataus.equals("yes")) {
    ////                                cddata.setVisibility(View.VISIBLE);
    ////                            } else if (showdataus.equals("notice")) {
    ////                                Toast.makeText(SettingsActivity.this, "" + noticedata, Toast.LENGTH_SHORT).show();
    ////                            } else if (showdataus.equals("no")) {
    ////                                cddata.setVisibility(View.GONE);
    ////                            }
    //
    //
    //                            if (whatnew.equals("yes")) {
    //                                showads();
    //                                showintads();
    //                                //Toast.makeText(SettingsActivity.this, "Showing", Toast.LENGTH_SHORT).show();
    //                            } else if (whatnew.equals("no")) {
    //                                mAdView.setVisibility(View.GONE);
    //                                // Toast.makeText(SettingsActivity.this, "Nope", Toast.LENGTH_SHORT).show();
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
    //                                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(SettingsActivity.this);
    //
    //
    //                            } else {
    //                                Timber.i("taskexcep :" + task.getException().getMessage() + task.getException() + task);
    //                                Toast.makeText(SettingsActivity.this, "Internet Connection Error", Toast.LENGTH_SHORT).show();
    //
    //                                //Toast.makeText(SettingsActivity.this, "" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
    //                            }
    //                        }
    //                    }
    //                });
    //    }
    override fun onBackPressed() {
        super.onBackPressed()
        val startIntent = Intent(this@SettingsActivity, NewMainActivity::class.java)
        startActivity(startIntent)
    }

    override fun onDestroy() {
        if (mAdView != null) {
            mAdView!!.destroy()
        }
        super.onDestroy()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        if (isNightModeEnabled(this)) {
//            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
//        } else {
//            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
//        }
        setContentView(R.layout.activity_settings)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        val backButton = toolbar.findViewById<ImageView>(R.id.back_image)
        val titleTextView = toolbar.findViewById<TextView>(R.id.title_text)
        val searchButton = toolbar.findViewById<ImageView>(R.id.search_image)
        val menuButton = toolbar.findViewById<ImageView>(R.id.menu_image)
        searchButton.visibility = View.GONE
        menuButton.visibility = View.INVISIBLE
        titleTextView.text = getString(R.string.settings)
        backButton.setOnClickListener { finish() }
        showads()
        //ADS+Firebase
        mAdView = AdView(this, getString(R.string.fb_banner_ad), AdSize.BANNER_HEIGHT_50)
        mAdView!!.loadAd(mAdView!!.buildLoadAdConfig().withAdListener(adListener).build())
        (findViewById<View>(R.id.fb_container) as LinearLayout).addView(mAdView)
        val buttonNotification = findViewById<LinearLayout>(R.id.button_notification)
        val buttonPermission = findViewById<LinearLayout>(R.id.button_permissions)
        val buttonLanguage = findViewById<LinearLayout>(R.id.button_language)
        val buttonTerms = findViewById<LinearLayout>(R.id.button_terms)
        val buttonPrivacy = findViewById<LinearLayout>(R.id.button_privacy)
        val buttonUpdate = findViewById<LinearLayout>(R.id.button_update)
        val buttonOngoingLog = findViewById<LinearLayout>(R.id.button_ongoinglog)
        val switchDarkMode = findViewById<SwitchCompat>(R.id.switchdark)
        //        SwitchCompat switchBetaTester = findViewById(R.id.switchBeta);

////        mSharedPref = getPreferences(Context.MODE_PRIVATE);
//        mAuth = FirebaseAuth.getInstance();
//        //datacb = (CheckBox)findViewById(R.id.datacb);
////        final Switch checkBox = findViewById(R.id.datacb);
////        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
////        final SharedPreferences.Editor editor = preferences.edit();
////        checkBox.setChecked(preferences.contains("checked") && preferences.getBoolean("checked", false) == true);
//
//        Date c = Calendar.getInstance().getTime();
//        System.out.println("Current time => " + c);
//
//        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy", Locale.US);
//        String formattedDate = df.format(c);
//
//        datedata = formattedDate;
//
//        mAuth = FirebaseAuth.getInstance();
//
//
//        if (mAuth.getCurrentUser() != null) {
//
//
//            mdatareport = FirebaseDatabase.getInstance().getReference().child("UsageReports").child(mAuth.getCurrentUser().getUid());
//            museref = FirebaseDatabase.getInstance().getReference().child("mainacreport").child(mAuth.getCurrentUser().getUid());
//
//            mUserRef = FirebaseDatabase.getInstance().getReference().child("openreport").child(mAuth.getCurrentUser().getUid());
//
//        }


//        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
//                if (checkBox.isChecked()) {
//                    editor.putBoolean("checked", true);
//                    editor.apply();
//                    signInAnonymously();
//                } else {
//                    editor.putBoolean("checked", false);
//                    editor.apply();
//                    logout();
//                }
//            }
//        });
        /* boolean isChecked = getBooleanFromPreferences("isChecked");
        Log.i("start",""+isChecked);
        datacb.setChecked(isChecked);
        datacb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton view, boolean isChecked) {
                Log.i("boolean",""+isChecked);
                SettingsActivity.this.putBooleanInPreferences(isChecked,"isChecked");
            }
        });*/


//        AudienceNetworkAds.initialize(this);
//        mInterstitialAd = new com.facebook.ads.InterstitialAd(this, getString(R.string.fb_interstitial_ad));
//        mInterstitialAd.loadAd();
//        //    private FirebaseAuth mAuth;
//        //    private DatabaseReference mDatabase;
//        //    private ViewPager mViewPager;
//        //    private DatabaseReference mUserRef, museref, mdatareport, mcredtref, mlinkupdate;
//        //    private ProgressBar progressBar;
//        //    private SharedPreferences mSharedPref;
//        AdView mAdView = new AdView(this, getString(R.string.fb_banner_ad), com.facebook.ads.AdSize.BANNER_HEIGHT_50);
//        mAdView.loadAd(mAdView.buildLoadAdConfig().withAdListener(adListener).build());
//        ((LinearLayout) findViewById(R.id.fb_container)).addView(mAdView);

//        firebaseRemoteConfigprice = FirebaseRemoteConfig.getInstance();
//        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder().build();
//        firebaseRemoteConfigprice.setConfigSettingsAsync(configSettings);
//
//        Map<String, Object> pricedata = new HashMap<>();
//        pricedata.put("showads", "yn");
//        pricedata.put("showdatausage", "yn");
//        pricedata.put("noticefordata", "yn");
//
//
//        firebaseRemoteConfigprice.setDefaultsAsync(pricedata);

//        checkadsstatus();
//        switchDarkMode.setClickable(true);
        //            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        //            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        switchDarkMode.isChecked = isNightModeEnabled(this)


        //    private String showdataus;
        //    private String noticedata;
        //    private String versionfirebase;
        //    private CheckBox cbpromo;
        val txtversion = findViewById<TextView>(R.id.version)
        txtversion.text = "v$versionName"
        buttonOngoingLog.setOnClickListener {
            Toast.makeText(
                applicationContext,
                "Ongoing log click",
                Toast.LENGTH_SHORT
            ).show()
        }

        //Permission Settings
        buttonPermission.setOnClickListener {
            val intent = Intent()
            intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
            val uri = Uri.fromParts("package", packageName, null)
            intent.data = uri
            startActivity(intent)
        }
        //        cddata = findViewById(R.id.cd_data);
//        cddata.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                showdatadilog();
//            }
//        });
//        cddata.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View view) {
//
//                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(SettingsActivity.this, R.style.AlertDialog);
//
//
//                builder.setMessage(username);
//
//                builder.setCancelable(false)
//                        .setNegativeButton("Cancel", null);
//                AlertDialog dialog = builder.create();
//                dialog.show();
//                return false;
//            }
//        });
        buttonUpdate.setOnClickListener { //                // Creates instance of the manager.
//                AppUpdateManager appUpdateManager = AppUpdateManagerFactory.create(getApplicationContext());
//
//                // Returns an intent object that you use to check for an update.
//                Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();
//
//                // Checks that the platform will allow the specified type of update.
//                appUpdateInfoTask.addOnSuccessListener(appUpdateInfo -> {
//                    if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
//                            // For a flexible update, use AppUpdateType.FLEXIBLE
//                            && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
//                        // Request the update.
//                    } else {
//                        Toast.makeText(getApplicationContext(), "No update available!", Toast.LENGTH_LONG).show();
//                    }
//                });
//                firebaseRemoteConfigprice.fetchAndActivate();
//                whatnew = (firebaseRemoteConfigprice.getString("showads"));
//                if (whatnew.equals("yes")) {
//                    showads();
//                    showintads();
//                    //Toast.makeText(MainActivity.this, "Showing", Toast.LENGTH_SHORT).show();
//                } else if (whatnew.equals("no")) {
//                    mAdView.setVisibility(View.GONE);
//
//                    //  Toast.makeText(MainActivity.this, "Nope", Toast.LENGTH_SHORT).show();
//                }
            val startIntent = Intent(this@SettingsActivity, NoticeActivity::class.java)
            startIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(startIntent)
        }
        switchDarkMode.setOnCheckedChangeListener { compoundButton: CompoundButton?, b: Boolean ->
            when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
                Configuration.UI_MODE_NIGHT_YES -> {
                    setIsNightModeEnabled(this, false)
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                }
                Configuration.UI_MODE_NIGHT_NO -> {
                    setIsNightModeEnabled(this, true)
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                }
            }
        }

//        switchBetaTester.setOnCheckedChangeListener((compoundButton, isChecked) -> {
//            Toast.makeText(getApplicationContext(), "Beta tester: " + isChecked, Toast.LENGTH_SHORT).show();
//        });
        buttonLanguage.setOnClickListener { //                firebaseRemoteConfigprice.fetchAndActivate();
//
//                whatnew = (firebaseRemoteConfigprice.getString("showads"));
//
//
//                if (whatnew.equals("yes")) {
//                    showads();
//                    showintads();
//                    //Toast.makeText(MainActivity.this, "Showing", Toast.LENGTH_SHORT).show();
//                } else if (whatnew.equals("no")) {
//                    mAdView.setVisibility(View.GONE);
//
//                    //  Toast.makeText(MainActivity.this, "Nope", Toast.LENGTH_SHORT).show();
//                }
            showlandailog()
        }

//        cdper.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View view) {
//
//
//                AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this);
//
//                builder.setMessage("We Need Permission To Access Your Phones Notification");
//
//
//                builder.setPositiveButton("GOT IT", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//
//                        dialogInterface.dismiss();
//                    }
//                });
//
//                AlertDialog dialog = builder.create();
//                dialog.show();
//
//                return false;
//            }
//        });
        buttonNotification.setOnClickListener {
            val intent = Intent()
            intent.action = "android.settings.APP_NOTIFICATION_SETTINGS"
            //for Android 5-7
            intent.putExtra("app_package", packageName)
            intent.putExtra("app_uid", applicationInfo.uid)
            // for Android O
            intent.putExtra("android.provider.extra.APP_PACKAGE", packageName)
            startActivity(intent)
        }
        buttonNotification.setOnLongClickListener {
            startActivity(Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"))
            false
        }


//        cdabout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                Intent startIntent = new Intent(SettingsActivity.this, AboutusActivity.class);
//                startActivity(startIntent);
//                /*android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(SettingsActivity.this, R.style.AlertDialog);
//
//                builder.setMessage("At Xenon Studio, we believe everyone deserves to have a safe, secure and useful app.\n" +
//                        "Innovation and simplicity makes us happy: our goal is to make AI reach every user, with the help of AI every user can feel his/her life easy.\n\n" +
//                        "We Have partnered with Uptodown App Studio To make Amazing Notification App, where user can easily manage there phones notification\n\n" +
//                        "App Developed By Xenon Studio,\n" +
//                        "\n" +
//                        "App Managed By UptoDown Apps Studio And Mob Appz\n\n" +
//                        "For More - www.xenonstudio.in/notificationlog");
//
//                builder.setPositiveButton("GOT IT", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//
//                        dialogInterface.dismiss();
//                    }
//                }).setNegativeButton("Developer Contact", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//
//                        Intent send = new Intent(Intent.ACTION_SENDTO);
//                        String uriText = "mailto:" + Uri.encode("thexenonstudio@gmail.com") +
//                                "?subject=" + Uri.encode("Notification Log - Developer Contact") +
//                                "&body=" + Uri.encode("Hello, Type Your Query/Problem/Bug/Suggestions Here" + " \n\n\n ------------ \n\n Version Code : " + versionCode + "\n Version Name : " + versionName + "\n Application ID : " + appid);
//                        Uri uri = Uri.parse(uriText);
//
//                        send.setData(uri);
//                        startActivity(Intent.createChooser(send, "Send Mail Via : "));
//                    }
//                });
//
//                AlertDialog dialog = builder.create();
//                dialog.show();*/
//            }
//        });
        buttonPrivacy.setOnClickListener {
            val privacyIntent = Intent(applicationContext, PrivacyActivity::class.java)
            privacyIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(privacyIntent)

//                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(SettingsActivity.this, R.style.AlertDialog);
//                builder.setIcon(R.drawable.ic_splash_logo);
//                builder.setTitle("Privacy Policy ");
//                builder.setMessage("Uptodown Apps built the Notification History Log app as a Free app. This SERVICE is provided by Uptodown Apps at no cost and is intended for use as is.\n" +
//                        "This page is used to inform app users regarding our policies with the collection, use, and\n" +
//                        "disclosure of any Information if anyone decided to use our Service.\n" +
//                        "If you choose to use our Service, then you agree to the collection and use of information in relation\n" +
//                        "to this policy. The Information that we collect is used for providing and improving the\n" +
//                        "Service. We will not use or share your information with anyone except as described\n" +
//                        "in this Privacy Policy.\n" +
//                        "Permission Details\n" +
//                        "Notification History Log uses the following permissions:\n" +
//                        "Internet: To show Ads using Admob and log errors using Fabric\n" +
//                        "Notification Access: To listen to all your incoming notifications and to store them in your local database.\n" +
//                        "Storage: To export notifications into Excel or Text files.\n" +
//                        "Note: All your notifications data will be stored in your local phone memory and we don’t have any access to your information. If you uninstall your app, all stored data will be deleted permanently.\n" +
//                        "Information Collection and Use\n" +
//                        "For a better experience, while using our app, we may require you to provide us with certain information. The information that we request is will be retained by us and used as described in this privacy policy.\n" +
//                        "The app does use third-party services that may collect information used to identify you.\n" +
//                        "Link to the privacy policy of third party service providers used by the app\n" +
//                        "Google Play Services\n" +
//                        "Fabric\n" +
//                        "AdMob\n" +
//                        "Log Data\n" +
//                        "We want to inform you that whenever you use our Service, in the case of an error in the app we collect data and information (through Fabric) on your phone called Log Data. This Log Data may include information such as your device name, operating system version, the version of the app when utilizing our Service, the time and date of your use of the Service, and other statistics.\n" +
//                        "Security\n" +
//                        "We value your trust in providing us your Information, thus we are striving to use commercially acceptable means of protecting it. But remember that no method of transmission over the internet, or method of electronic storage is 100% secure and reliable, and we cannot guarantee its absolute security.\n" +
//                        "Changes to This Privacy Policy\n" +
//                        "We may update our Privacy Policy from time to time. Thus, you are advised to review\n" +
//                        "this page periodically for any changes. We will notify you of any changes by posting\n" +
//                        "the new Privacy Policy on this page. These changes are effective immediately after they are posted on\n" +
//                        "this page.\n" +
//                        "Contact Us\n" +
//                        "If you have any questions or suggestions about our Privacy Policy, do not hesitate to contact us.\n \n\nFor More : barry@uptodownapp.com");
//
//                AlertDialog dialog = builder.create();
//                dialog.show();
//                        /*WebView webView = new WebView(SettingsActivity.this);
//
//                        webView.loadUrl(TermsUrl);*/
        }
        buttonTerms.setOnClickListener {
            val termsIntent = Intent(applicationContext, TermsActivity::class.java)
            termsIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(termsIntent)

//                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(SettingsActivity.this, R.style.AlertDialog);
//                builder.setIcon(R.drawable.ic_splash_logo);
//                builder.setTitle("Terms and Conditions ");
//                builder.setMessage("Notification History Log\n" +
//                        "Terms and conditions\n" +
//                        "These terms and conditions (\"Terms\", \"Agreement\") are an agreement between Mobile Application Developer (\"Mobile Application Developer\", \"us\", \"we\" or \"our\") and you (\"User\", \"you\" or \"your\"). This Agreement sets forth the general terms and conditions of your use of the Notification History Log mobile application and any of its products or services (collectively, \"Mobile Application\" or \"Services\").\n" +
//                        "\n" +
//                        "Backups\n" +
//                        "We are not responsible for Content residing in the Mobile Application. In no event shall we be held liable for any loss of any Content. It is your sole responsibility to maintain appropriate backup of your Content. Notwithstanding the foregoing, on some occasions and in certain circumstances, with absolutely no obligation, we may be able to restore some or all of your data that has been deleted as of a certain date and time when we may have backed up data for our own purposes. We make no guarantee that the data you need will be available.\n" +
//                        "\n" +
//                        "Links to other mobile applications\n" +
//                        "Although this Mobile Application may link to other mobile applications, we are not, directly or indirectly, implying any approval, association, sponsorship, endorsement, or affiliation with any linked mobile application, unless specifically stated herein. We are not responsible for examining or evaluating, and we do not warrant the offerings of, any businesses or individuals or the content of their mobile applications. We do not assume any responsibility or liability for the actions, products, services, and content of any other third-parties. You should carefully review the legal statements and other conditions of use of any mobile application which you access through a link from this Mobile Application. Your linking to any other off-site mobile applications is at your own risk.\n" +
//                        "\n" +
//                        "Prohibited uses\n" +
//                        "In addition to other terms as set forth in the Agreement, you are prohibited from using the Mobile Application or its Content: (a) for any unlawful purpose; (b) to solicit others to perform or participate in any unlawful acts; (c) to violate any international, federal, provincial or state regulations, rules, laws, or local ordinances; (d) to infringe upon or violate our intellectual property rights or the intellectual property rights of others; (e) to harass, abuse, insult, harm, defame, slander, disparage, intimidate, or discriminate based on gender, sexual orientation, religion, ethnicity, race, age, national origin, or disability; (f) to submit false or misleading information; (g) to upload or transmit viruses or any other type of malicious code that will or may be used in any way that will affect the functionality or operation of the Service or of any related mobile application, other mobile applications, or the Internet; (h) to collect or track the personal information of others; (i) to spam, phish, pharm, pretext, spider, crawl, or scrape; (j) for any obscene or immoral purpose; or (k) to interfere with or circumvent the security features of the Service or any related mobile application, other mobile applications, or the Internet. We reserve the right to terminate your use of the Service or any related mobile application for violating any of the prohibited uses.\n" +
//                        "\n" +
//                        "Intellectual property rights\n" +
//                        "This Agreement does not transfer to you any intellectual property owned by Mobile Application Developer or third-parties, and all rights, titles, and interests in and to such property will remain (as between the parties) solely with Mobile Application Developer. All trademarks, service marks, graphics and logos used in connection with our Mobile Application or Services, are trademarks or registered trademarks of Mobile Application Developer or Mobile Application Developer licensors. Other trademarks, service marks, graphics and logos used in connection with our Mobile Application or Services may be the trademarks of other third-parties. Your use of our Mobile Application and Services grants you no right or license to reproduce or otherwise use any Mobile Application Developer or third-party trademarks.\n" +
//                        "\n" +
//                        "Limitation of liability\n" +
//                        "To the fullest extent permitted by applicable law, in no event will Mobile Application Developer, its affiliates, officers, directors, employees, agents, suppliers or licensors be liable to any person for (a): any indirect, incidental, special, punitive, cover or consequential damages (including, without limitation, damages for lost profits, revenue, sales, goodwill, use or content, impact on business, business interruption, loss of anticipated savings, loss of business opportunity) however caused, under any theory of liability, including, without limitation, contract, tort, warranty, breach of statutory duty, negligence or otherwise, even if Mobile Application Developer has been advised as to the possibility of such damages or could have foreseen such damages. To the maximum extent permitted by applicable law, the aggregate liability of Mobile Application Developer and its affiliates, officers, employees, agents, suppliers and licensors, relating to the services will be limited to an amount greater of one dollar or any amounts actually paid in cash by you to Mobile Application Developer for the prior one month period prior to the first event or occurrence giving rise to such liability. The limitations and exclusions also apply if this remedy does not fully compensate you for any losses or fails of its essential purpose.\n" +
//                        "\n" +
//                        "Indemnification\n" +
//                        "You agree to indemnify and hold Mobile Application Developer and its affiliates, directors, officers, employees, and agents harmless from and against any liabilities, losses, damages or costs, including reasonable attorneys' fees, incurred in connection with or arising from any third-party allegations, claims, actions, disputes, or demands asserted against any of them as a result of or relating to your Content, your use of the Mobile Application or Services or any willful misconduct on your part.\n" +
//                        "\n" +
//                        "Severability\n" +
//                        "All rights and restrictions contained in this Agreement may be exercised and shall be applicable and binding only to the extent that they do not violate any applicable laws and are intended to be limited to the extent necessary so that they will not render this Agreement illegal, invalid or unenforceable. If any provision or portion of any provision of this Agreement shall be held to be illegal, invalid or unenforceable by a court of competent jurisdiction, it is the intention of the parties that the remaining provisions or portions thereof shall constitute their agreement with respect to the subject matter hereof, and all such remaining provisions or portions thereof shall remain in full force and effect.\n" +
//                        "\n" +
//                        "Dispute resolution\n" +
//                        "The formation, interpretation, and performance of this Agreement and any disputes arising out of it shall be governed by the substantive and procedural laws of Karnataka, India without regard to its rules on conflicts or choice of law and, to the extent applicable, the laws of India. The exclusive jurisdiction and venue for actions related to the subject matter hereof shall be the state and federal courts located in Karnataka, India, and you hereby submit to the personal jurisdiction of such courts. You hereby waive any right to a jury trial in any proceeding arising out of or related to this Agreement. The United Nations Convention on Contracts for the International Sale of Goods does not apply to this Agreement.\n" +
//                        "\n" +
//                        "Changes and amendments\n" +
//                        "We reserve the right to modify this Agreement or its policies relating to the Mobile Application or Services at any time, effective upon posting of an updated version of this Agreement in the Mobile Application. When we do, we will post a notification in our Mobile Application. Continued use of the Mobile Application after any such changes shall constitute your consent to such changes. Policy was created with WebsitePolicies.\n" +
//                        "\n" +
//                        "Acceptance of these terms\n" +
//                        "You acknowledge that you have read this Agreement and agree to all its terms and conditions. By using the Mobile Application or its Services you agree to be bound by this Agreement. If you do not agree to abide by the terms of this Agreement, you are not authorized to use or access the Mobile Application and its Services.\n" +
//                        "\n" +
//                        "Contacting us\n" +
//                        "If you have any questions about this Agreement, please contact us.\n" +
//                        "\n" +
//                        "This document was last updated on June 27, 2019 \n\nFor More : barry@uptodownapp.com");
//
//                AlertDialog dialog = builder.create();
//                dialog.show();
//                        /*WebView webView = new WebView(SettingsActivity.this);
//
//                        webView.loadUrl(TermsUrl);*/
        }
    }

    //    private void showads() {
    //
    //        adListener = new AdListener() {
    //            @Override
    //            public void onError(Ad ad, AdError adError) {
    //
    //            }
    //
    //            @Override
    //            public void onAdLoaded(Ad ad) {
    //
    //            }
    //
    //            @Override
    //            public void onAdClicked(Ad ad) {
    //                SharedCommon sc = new SharedCommon();
    //
    //                int i = SharedCommon.getPreferencesInt(getApplicationContext(), key1, 0);
    //
    //
    //                i++;
    //                SharedCommon.putPreferencesInt(getApplicationContext(), SharedCommon.key1, i);
    //            }
    //
    //            @Override
    //            public void onLoggingImpression(Ad ad) {
    //
    //            }
    //        };
    //
    //    }
    private fun showlandailog() {
        val listitems = arrayOf(
            "English",
            "हिंदी",
            "Deutschland",
            "Português",
            "My Language is Not Listed Here"
        )
        val mBuilder = AlertDialog.Builder(this@SettingsActivity, R.style.DialogTheme)
        mBuilder.setTitle(R.string.language)
        mBuilder.setSingleChoiceItems(listitems, -1) { dialogInterface: DialogInterface, i: Int ->
            val startIntent = Intent(this@SettingsActivity, NewMainActivity::class.java)
            startIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
            if (i == 0) {
                //English
                setLocale("en")
                startActivity(startIntent)
            } else if (i == 1) {
                //Hindi
                setLocale("hi")
                startActivity(startIntent)
            } else if (i == 2) {
                //Germany
                setLocale("de")
                startActivity(startIntent)
            } else if (i == 3) {
                //Germany
                setLocale("pt")
                startActivity(startIntent)
            } else if (i == 4) {
                //Germany
                //setLocale("pt");
                landailog()
            }
            dialogInterface.dismiss()
        }
        val mDialog = mBuilder.create()
        mDialog.show()
    }

    private fun landailog() {
        val taskEditText = EditText(this@SettingsActivity)
        val dialog = AlertDialog.Builder(this@SettingsActivity, R.style.DialogTheme)
            .setTitle("Provide Us Your Language Preference")
            .setMessage(" So We Can Add It In Our Future Updates ")
            .setView(taskEditText)
            .setCancelable(false)
            .setPositiveButton("Send Mail") { dialog, which ->
                val task = taskEditText.text.toString()
                try {
                    sendmailintent(task)
                } catch (e: Exception) {
                    Timber.e("Not able to find mail intent!")
                }
            }
            .setNegativeButton("Cancel", null)
            .create()
        dialog.show()
        val positive = dialog.getButton(DialogInterface.BUTTON_POSITIVE)
        positive.setTextColor(resources.getColor(R.color.colorText))
        val negative = dialog.getButton(DialogInterface.BUTTON_NEGATIVE)
        negative.setTextColor(resources.getColor(R.color.colorText))
    }

    /*    final EditText taskEditText = new EditText(SettingsActivity.this);
    android.app.AlertDialog dialog = new android.app.AlertDialog.Builder(SettingsActivity.this)
            .setTitle("Provide Us Your Language Preference ")
            .setMessage(" So We Can Add It In Our Future Updates ")
            .setView(taskEditText)
            .setCancelable(false)
            .setPositiveButton("Send Mail", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String task = String.valueOf(taskEditText.getText());
                    sendmailintent(task);
                }
            })
            .setNegativeButton("Cancel",null)
            .create();
                    dialog.show();
}

                dialogInterface.dismiss();*/
    private fun setLocale(lang: String) {
        val locale = Locale(lang)
        Locale.setDefault(locale)
        val configuration = Configuration()
        configuration.locale = locale
        baseContext.resources.updateConfiguration(
            configuration,
            baseContext.resources.displayMetrics
        )
    }

    //    private void graphdailog() {
    //        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(SettingsActivity.this);
    //
    //        builder.setIcon(R.drawable.notificationlogo);
    //
    //        builder.setTitle("Coming Soon!");
    //
    //        builder.setMessage("Get Insights Of Notifications, View Data Of Notifications Into Graph And Manage Easily");
    //        builder.setCancelable(false);
    //
    //        builder.setPositiveButton("Got It", new DialogInterface.OnClickListener() {
    //            @Override
    //            public void onClick(DialogInterface dialogInterface, int i) {
    //                dialogInterface.dismiss();
    //                Intent startIntent = new Intent(SettingsActivity.this, NewMainActivity.class);
    //                startActivity(startIntent);
    //
    //            }
    //        });
    //        builder.setNegativeButton("Notify Me", new DialogInterface.OnClickListener() {
    //            @Override
    //            public void onClick(DialogInterface dialogInterface, int i) {
    //                Toast.makeText(SettingsActivity.this, "We Will Notify You When Refer & Earn Is Available ", Toast.LENGTH_SHORT).show();
    //                dialogInterface.dismiss();
    //            }
    //        });
    //
    //
    //        android.app.AlertDialog dialog = builder.create();
    //        dialog.show();
    //    }
    @Throws(Exception::class)
    private fun sendmailintent(task: String) {
        val toast = Toast.makeText(this, "SEND MAIL VIA GMAIL/YAHOO ", Toast.LENGTH_LONG)
        val view = toast.view
        view!!.background.setColorFilter(Color.parseColor("#FF104162"), PorterDuff.Mode.SRC_IN)
        val text = view.findViewById<TextView>(android.R.id.message)
        text.setTextColor(Color.WHITE)
        toast.show()
        val locale = this@SettingsActivity.resources.configuration.locale.country
        val send = Intent(Intent.ACTION_SENDTO)
        val uriText = "mailto:" + Uri.encode("notificationappgp@gmail.com") +
                "?subject=" + Uri.encode("Language - Notification Log App ") +
                "&body=" + Uri.encode(
            """$task 


 ------------ 

 Version Code : $versionCode
 Build : ${Build.BRAND}
${Build.MODEL}
${Build.DEVICE}
$locale"""
        )
        val uri = Uri.parse(uriText)
        send.data = uri
        startActivity(Intent.createChooser(send, "Send Mail Via : "))
    }

    //    private void setAppTheme(@StyleRes int style) {
    //        setTheme(style);
    //    }
    private fun setIsNightModeEnabled(context: Context, state: Boolean) {
        val mSharedPref =
            context.getSharedPreferences(context.getString(R.string.app_name), MODE_PRIVATE)
        val mEditor = mSharedPref.edit()
        mEditor.putBoolean(NIGHT_MODE, state)
        mEditor.apply()
    } //    @Override

    //    public void onStart() {
    //        super.onStart();
    //        // Check if user is signed in (non-null) and update UI accordingly.
    //        checkConnection();
    //        updatedata();
    //        // Check if user is signed in (non-null) and update UI accordingly.
    //     /*   FirebaseUser currentUser = mAuth.getCurrentUser();
    //        //Toast.makeText(this, ""+currentUser, Toast.LENGTH_SHORT).show();
    //
    //        if(currentUser == null){
    //
    //            sendToStart();
    //
    //        } else {
    //
    //            username = currentUser.getUid();
    //            //SAVEDATAREPORT
    //            savereport(currentUser);
    //        }*/
    //        //updateUI(currentUser);
    //    }
    //    private void savereport(FirebaseUser currentUser) {
    //        mdatareport.child("User").setValue(currentUser);
    //        mdatareport.child("M-Note For User(For Dev)").setValue("-");
    //        mdatareport.child("M-Note Date (For Dev)").setValue("-");
    //
    //    }
    //
    //    private void sendToStart() {
    //
    //        username = "Not Signed In";
    //        //Toast.makeText(this, "Not Signed", Toast.LENGTH_SHORT).show();;
    //
    //    }
    //    private boolean isOnline() {
    //        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
    //        NetworkInfo netInfo = cm.getActiveNetworkInfo();
    //        return netInfo != null && netInfo.isConnectedOrConnecting();
    //    }
    //    public void checkConnection() {
    //        if (isOnline()) {
    //
    //            Log.d("Hello", "Net");
    //
    //
    //        } else {
    //
    ////            cddata.setVisibility(View.GONE);
    //            View parentLayout = findViewById(android.R.id.content);
    //
    //            Snackbar snackbar = Snackbar
    //                    .make(parentLayout, "Internet Connection Not Found! ", Snackbar.LENGTH_SHORT);
    //            View snackbarView = snackbar.getView();
    //            snackbarView.setBackgroundColor(Color.parseColor("#FF104162"));
    //
    //            snackbar.show();
    //        }
    //    }
    //    private void signInAnonymously() {
    //        // showProgressDialog();
    //        // [START signin_anonymously]
    //        mAuth.signInAnonymously()
    //                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
    //                    @Override
    //                    public void onComplete(@NonNull Task<AuthResult> task) {
    //                        if (task.isSuccessful()) {
    //                            // Sign in success, update UI with the signed-in user's information
    //                            //Log.d(TAG, "signInAnonymously:success");
    //                            FirebaseUser user = mAuth.getCurrentUser();
    //                            View parentLayout = findViewById(android.R.id.content);
    //
    //                            Snackbar snackbar = Snackbar
    //                                    .make(parentLayout, "Thanks For Improving Our App !", Snackbar.LENGTH_LONG);
    //                            View snackbarView = snackbar.getView();
    //                            snackbarView.setBackgroundColor(Color.parseColor("#FF104162"));
    //                            snackbar.show();
    //                            mAuth = FirebaseAuth.getInstance();
    //
    //
    //                            if (mAuth.getCurrentUser() != null) {
    //
    //
    //                                mdatareport = FirebaseDatabase.getInstance().getReference().child("UsageReports").child(mAuth.getCurrentUser().getUid());
    //
    //                            }
    //
    //
    //                            updateUI(user);
    //                        } else {
    //                            // If sign in fails, display a message to the user.
    //                            //Log.w(TAG, "signInAnonymously:failure", task.getException());
    //                            Toast.makeText(SettingsActivity.this, "Something Went Wrong",
    //                                    Toast.LENGTH_SHORT).show();
    //                            updateUI(null);
    //                        }
    //
    //                        // [START_EXCLUDE]
    //                        // hideProgressDialog();
    //                        // [END_EXCLUDE]
    //                    }
    //                });
    //        // [END signin_anonymously]
    //    }
    //
    //    private void updateUI(FirebaseUser user) {
    //        boolean isSignedIn = (user != null);
    //
    //        // Status text
    //        if (isSignedIn) {
    //            username = user.getUid();
    //
    //        } else {
    //            username = null;
    //        }
    //    }
    //
    //    public void putBooleanInPreferences(boolean isChecked, String key) {
    //        SharedPreferences sharedPreferences = this.getPreferences(Activity.MODE_PRIVATE);
    //        SharedPreferences.Editor editor = sharedPreferences.edit();
    //        editor.putBoolean(key, isChecked);
    //        editor.commit();
    //        signInAnonymously();
    //    }
    //
    //    public boolean getBooleanFromPreferences(String key) {
    //        SharedPreferences sharedPreferences = this.getPreferences(Activity.MODE_PRIVATE);
    //        Boolean isChecked = sharedPreferences.getBoolean(key, false);
    //
    //        return isChecked;
    //
    //    }
    //
    //    public void updatedata() {
    //
    //        FirebaseUser currentUser = mAuth.getCurrentUser();
    //        //Toast.makeText(this, ""+currentUser, Toast.LENGTH_SHORT).show();
    //
    //        if (currentUser == null) {
    //
    //            sendToStart();
    //
    //        } else {
    //
    //
    //            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZZZZZ", Locale.getDefault());
    //
    //            String datte = format.format(new Date());
    //            String idtimedate = datte.substring(5, 10);
    //            String idtime = datte.substring(11, 19);
    //            Date dNow = new Date();
    //            SimpleDateFormat ft = new SimpleDateFormat("yyMMddhhmmssMs");
    //            String datetime = ft.format(dNow);
    //            String iddate = datetime.substring(0, 3);
    //            //String uidid = uid.substring(0,5);
    //
    //            // mdatareport.child("D-Unique ID").setValue(iddate+idtimedate+idtime);
    //
    //
    //            String lan = Locale.getDefault().getDisplayLanguage();
    //            String country = SettingsActivity.this.getResources().getConfiguration().locale.getCountry();
    //            String buildID = ("" + Build.ID);
    //            TelephonyManager tm = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
    //            String countryCodeValue = tm.getNetworkCountryIso();
    //
    //            TelephonyManager manager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
    //            String carrierName = manager.getNetworkOperatorName();
    //            String devicetime = ("" + Build.USER);
    //            String buildd = ("" + Build.HOST);
    //            String devicedet = ("" + Build.BRAND + " " + Build.MODEL + " " + Build.DEVICE + " " + Build.TYPE + "" + Build.HOST);
    //            String tokenID = SharedCommon.getSharedPreferencesString(SettingsActivity.this, keytokenid, "");
    //
    //
    //            mdatareport.child("D-Date Time").setValue(datte);
    //            mdatareport.child("D-Language").setValue(lan);
    //
    //            mdatareport.child("D-Status").setValue("Logged In");
    //            mdatareport.child("D-FirebaseToken").setValue(tokenID);
    //
    //
    //            //  mdatareport.child("U-FirebaseToken").setValue(tokenID);
    //
    //            mdatareport.child("U-BuildID").setValue(buildID);
    //
    //            mdatareport.child("U-Carrier").setValue(carrierName);
    //            mdatareport.child("U-Country").setValue(countryCodeValue);
    //            mdatareport.child("U-Version").setValue(versionName);
    //            mdatareport.child("U-BuildUser").setValue(devicetime);
    //            mdatareport.child("U-Device").setValue(devicedet);
    //        }
    //    }
    //
    //    private void logout() {
    //        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZZZZZ", Locale.getDefault());
    //
    //        String datte = format.format(new Date());
    //        String idtimedate = datte.substring(5, 10);
    //        String idtime = datte.substring(11, 19);
    //
    //        mdatareport.child("D-Logged Out Time").setValue(datte);
    //
    //
    //        mdatareport.child("D-Status").setValue("Logged Out");
    //        FirebaseUser currentUser = mAuth.getCurrentUser();
    //
    //        //Toast.makeText(this, "Your Data Usage Is Will Not Been Logged", Toast.LENGTH_SHORT).show();
    //        View parentLayout = findViewById(android.R.id.content);
    //
    //        Snackbar snackbar = Snackbar
    //                .make(parentLayout, "Your Data Usages Will Not Be Logged", Snackbar.LENGTH_INDEFINITE);
    //        View snackbarView = snackbar.getView();
    //        snackbarView.setBackgroundColor(Color.parseColor("#FF104162"));
    //        snackbar.setAction("Know More", new View.OnClickListener() {
    //            @Override
    //            public void onClick(View view) {
    //                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(SettingsActivity.this, R.style.AlertDialog);
    //
    //                builder.setIcon(R.drawable.notificationlogo);
    //                builder.setTitle("More Info ");
    //                builder.setMessage("Your All Data Has Been Deleted For User ID " + currentUser.getUid());
    //                builder.setCancelable(false)
    //                        .setPositiveButton("Got It", new DialogInterface.OnClickListener() {
    //                            @Override
    //                            public void onClick(DialogInterface dialog, int which) {
    //                                snackbar.dismiss();
    //                            }
    //                        });
    //
    //                AlertDialog dialog1 = builder.create();
    //                dialog1.show();
    //            }
    //        });
    //
    //        snackbar.show();
    //
    //        splashTread = new Thread() {
    //            @Override
    //            public void run() {
    //                try {
    //                    int waited = 0;
    //                    // Splash screen pause time
    //                    while (waited < 3600) {
    //                        sleep(100);
    //                        waited += 100;
    //                    }
    //                    snackbar.dismiss();
    //
    //                } catch (InterruptedException e) {
    //                    // do nothing
    //                } finally {
    //                    snackbar.dismiss();
    //                    // SettingsActivity.this.finish();
    //                }
    //
    //            }
    //        };
    //        splashTread.start();
    //        //Toast.makeText(this, ""+currentUser, Toast.LENGTH_SHORT).show();
    //
    //
    //        // mdatareport = FirebaseDatabase.getInstance().getReference().child("usagereport").child(mAuth.getCurrentUser().getUid());
    //
    //
    //        if (currentUser == null) {
    //
    //            sendToStart();
    //
    //        } else {
    //            mdatareport.removeValue();
    //            museref.removeValue();
    //            mUserRef.removeValue();
    //            currentUser.delete();
    //            username = "Not Tracking Data Usage";
    //        }
    //
    //        //mAuth.signOut();
    //        //updateUI(null);
    //    }
    //    private void showintads() {
    //        if (mInterstitialAd.isAdLoaded()) {
    //            mInterstitialAd.show();
    //        } else {
    //            Timber.i("The interstitial wasn't loaded yet.");
    //        }
    //    }
    companion object {
        private const val NIGHT_MODE = "night_mode"
        fun isNightModeEnabled(context: Context): Boolean {
            val mSharedPref =
                context.getSharedPreferences(context.getString(R.string.app_name), MODE_PRIVATE)
            return mSharedPref.getBoolean(NIGHT_MODE, false)
        }
    }
}