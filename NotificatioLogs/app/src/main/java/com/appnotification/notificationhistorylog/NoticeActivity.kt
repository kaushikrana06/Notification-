package com.appnotification.notificationhistorylog

import android.app.AlertDialog
import android.content.*
import android.graphics.PorterDuff
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.*
import android.webkit.WebView
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.appnotification.notificationhistorylog.CommonCl.SharedCommon.getPreferencesInt
import com.appnotification.notificationhistorylog.CommonCl.SharedCommon.key1
import com.appnotification.notificationhistorylog.CommonCl.SharedCommon.keynotification
import com.appnotification.notificationhistorylog.CommonCl.SharedCommon.putPreferencesInt
import com.appnotification.notificationhistorylog.SettingsActivity
import com.appnotification.notificationhistorylog.ui.MainActivity
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import timber.log.Timber

class NoticeActivity : AppCompatActivity() {
    var whatnew: String? = null
    var versionfirebase: String? = null
    var btnlimit: Button? = null
    var splashTread: Thread? = null
    var apppackagename = "com.appnotification.notificationhistorylog"
    var progressBar: ProgressBar? = null
    var linernotification: LinearLayout? = null
    var checkupdates: String? = ""
    var versionCode = BuildConfig.VERSION_CODE
    var versionName = BuildConfig.VERSION_NAME

    //String apppackagename = "com.thetechroot.vision";
    var appid = BuildConfig.APPLICATION_ID
    var firebaseRemoteConfigprice: FirebaseRemoteConfig? = null
    private var mBlogList: RecyclerView? = null
    private var mDatabase: DatabaseReference? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notice)
        val linernotification: LinearLayout = findViewById(R.id.linernotification)
        linernotification.setVisibility(View.GONE)
        val progressBar: ProgressBar = findViewById(R.id.progressBar)
        progressBar.setVisibility(View.VISIBLE)
        progressBar.setIndeterminate(true)
        progressBar.getIndeterminateDrawable().setColorFilter(-0xfc9a57, PorterDuff.Mode.MULTIPLY)


        /* OneSignal.startInit(this)
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .unsubscribeWhenNotificationsAreDisabled(true)
                .init();*/mDatabase =
            FirebaseDatabase.getInstance().reference.child("Notifications")
        mDatabase!!.keepSynced(true)
        val mBlogList:RecyclerView = findViewById(R.id.myrecview)
        mBlogList.setHasFixedSize(true)
        mBlogList.setLayoutManager(LinearLayoutManager(this))
        checkConnection()
        doFirstRun()
        firebaseRemoteConfigprice = FirebaseRemoteConfig.getInstance()
        val configSettings = FirebaseRemoteConfigSettings.Builder().build()
        firebaseRemoteConfigprice!!.setConfigSettingsAsync(configSettings)
        val pricedata: MutableMap<String, Any> = HashMap()
        pricedata["whatsnew"] = "whatsnew"
        pricedata["version"] = "111"
        firebaseRemoteConfigprice!!.setDefaultsAsync(pricedata)
    }

    private fun updateremote() {
        firebaseRemoteConfigprice!!.fetch(0)
            .addOnCompleteListener { task ->
                Log.e("TaskError", "info" + firebaseRemoteConfigprice!!.info.lastFetchStatus)
                Log.e(
                    "TaskError",
                    "firebaseremote" + firebaseRemoteConfigprice!!.getString("btn_text")
                )
                if (task.isSuccessful) {
                    firebaseRemoteConfigprice!!.fetchAndActivate()
                    /*txt600.setText(firebaseRemoteConfigprice.getString("txt600"));
                                txt1500.setText(firebaseRemoteConfigprice.getString("txt1500"));
                                txt3200.setText(firebaseRemoteConfigprice.getString("txt3200"));
                                txt5000.setText(firebaseRemoteConfigprice.getString("txt5000"));
    
    
    */whatnew = firebaseRemoteConfigprice!!.getString("whatsnew")
                    versionfirebase = firebaseRemoteConfigprice!!.getString("version")
                    val builder = AlertDialog.Builder(this@NoticeActivity, R.style.DialogTheme)
                    builder.setTitle("What's New ")
                    builder.setMessage("$whatnew\n\nLatest Version$versionfirebase")
                    builder.setIcon(R.drawable.ic_splash_logo)
                    builder.setPositiveButton("GOT IT") { dialogInterface, i -> dialogInterface.dismiss() }
                    val dialog = builder.create()
                    dialog.show()
                    val positive = dialog.getButton(DialogInterface.BUTTON_POSITIVE)
                    positive.setTextColor(resources.getColor(R.color.colorText))
                    Log.e(
                        "TaskError",
                        "firebaseremote" + firebaseRemoteConfigprice!!.getString("btn_text")
                    )


                    /* Picasso.get().load(firebaseRemoteConfigprice.getString("image_link"))
                                                .into(img);*/
                } else {
                    val exp = "" + task.exception!!.message
                    if (exp == "null") {
                        whatnew = "Server Not Responding "
                        val builder = AlertDialog.Builder(this@NoticeActivity, R.style.DialogTheme)
                        builder.setTitle("What's New ")
                        builder.setMessage(whatnew)
                        builder.setIcon(R.drawable.ic_splash_logo)
                        builder.setPositiveButton("GOT IT") { dialogInterface, i -> dialogInterface.dismiss() }
                        val dialog = builder.create()
                        dialog.show()
                        val positive = dialog.getButton(DialogInterface.BUTTON_POSITIVE)
                        positive.setTextColor(resources.getColor(R.color.colorText))

                        // Toast.makeText(PriceListActivity.this, "Taking Longer Time", Toast.LENGTH_LONG).show();
                        /* txt600.setText("600 Credit (70% Off)");
                                    txt1500.setText("1500 Credit(50% Off)");
                                    txt3200.setText("3200 Credit(50% Off)");
                                    txt5000.setText("5000 Credits(70% Off)");*/


/*

                                 txt600.setText(credit600+"/600 Credits");
                                 txt1500.setText(credit1500+"/1500 Credits");
                                 txt3200.setText(credit3200+"/3200 Credits");
                                 txt5000.setText(credit5000+"/5000 Credits");
*/
                    } else {
                        Log.e(
                            "TaskError",
                            "taskexcep :" + task.exception!!.message + task.exception + task
                        )
                        Toast.makeText(
                            this@NoticeActivity,
                            "" + task.exception!!.message,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
    }

    override fun onStart() {
        super.onStart()
        startListening()
        progressBar!!.indeterminateDrawable.setColorFilter(-0xfc9a57, PorterDuff.Mode.MULTIPLY)
    }

    fun startListening() {
        val query = FirebaseDatabase.getInstance()
            .reference
            .child("Notifications")
            .limitToLast(50)
        progressBar!!.indeterminateDrawable.setColorFilter(0x0F82D2, PorterDuff.Mode.MULTIPLY)
        val options = FirebaseRecyclerOptions.Builder<Notif>()
            .setQuery(query, Notif::class.java)
            .build()
        val adapter: FirebaseRecyclerAdapter<*, *> =
            object : FirebaseRecyclerAdapter<Notif, UserViewHolder>(options) {
                override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
                    // Create a new instance of the ViewHolder, in this case we are using a custom
                    // layout called R.layout.message for each item
                    val view = LayoutInflater.from(parent.context)
                        .inflate(R.layout.noti_row, parent, false)
                    return UserViewHolder(view)
                }

                override fun onBindViewHolder(holder: UserViewHolder, position: Int, model: Notif) {
                    // Bind the Chat object to the ChatHolder
                    checkupdates = model.image
                    progressBar!!.indeterminateDrawable.setColorFilter(
                        0x07578f,
                        PorterDuff.Mode.MULTIPLY
                    )
                    progressBar!!.visibility = View.GONE
                    linernotification!!.visibility = View.GONE
                    holder.setTitle(model.title)
                    Timber.i("@@@ Response setTitle%s", model.title)
                    Timber.i("@@@ Response model%s", model)
                    Common.HOLDER = model.title!!
                    holder.setDesc(model.desc)
                    Timber.i("@@@ Response D%s", model.desc)
                    holder.setImage(model.image, applicationContext)
                    Timber.i("@@@ Response setImage%s ", model.image)
                    holder.mView.setOnClickListener { /* if (model.getImage().contains("Update")){
                            final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
                            try {
                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + apppackagename)));
                            } catch (android.content.ActivityNotFoundException anfe) {
                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + apppackagename)));
                            }

                        }*/
                        if (model.image!!.contains("Mail")) {
                            val send = Intent(Intent.ACTION_SENDTO)
                            val uriText = "mailto:" + Uri.encode("thexenonstudio@gmail.com") +
                                    "?subject=" + Uri.encode("Around Me App - Contact") +
                                    "&body=" + Uri.encode("Hello, Type Your Query/Problem/Bug/Suggestions Here")
                            val uri = Uri.parse(uriText)
                            send.data = uri
                            startActivity(Intent.createChooser(send, "Send Mail Via : "))
                        }

                        /* if(model.getTitle().contains("Notification")){
            
                                        .setGravity(Gravity.CENTER | Gravity.BOTTOM);
                                    }*/if (model.title!!.contains("Offer")) {
                            val webView = WebView(this@NoticeActivity)
                            webView.loadUrl(model.image!!)
                        }
                        if (model.image!!.contains("Help")) {
                            val startIntent = Intent(this@NoticeActivity, MainActivity::class.java)
                            startActivity(startIntent)
                        }
                        if (model.image!!.contains("December Value Back")) {
                        }
                        if (model.image!!.contains("Product Search")) {
                            val startIntent = Intent(this@NoticeActivity, MainActivity::class.java)
                            startActivity(startIntent)
                        }
                        if (model.image!!.contains(versionName)) {
                            val br = model.image!!.contains(versionName)
                            Timber.i("@@@ Response br$br$versionName")
                            checkupdates = model.image
                            if (br == true) {
                                Timber.i("@@@ Response br inside$br$versionName")

                                // Toast.makeText(NoticeActivity.this, ""+versionName+updatecheck, Toast.LENGTH_SHORT).show();
                                Toast.makeText(
                                    this@NoticeActivity,
                                    "App Is Updated",
                                    Toast.LENGTH_LONG
                                ).show()
                            } else {
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
                        } else if (model.image!!.contains("Update")) {
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
                        if (model.image!!.contains("PRO")) {
                            val startIntent = Intent(this@NoticeActivity, MainActivity::class.java)
                            startActivity(startIntent)
                        }
                        if (model.image!!.contains("Setting")) {
                            Toast.makeText(
                                this@NoticeActivity,
                                "Change Language",
                                Toast.LENGTH_SHORT
                            ).show()
                            val startIntent =
                                Intent(this@NoticeActivity, SettingsActivity::class.java)
                            startActivity(startIntent)
                        }
                        if (model.image!!.contains("Public Feedback")) {
                            val startIntent = Intent(this@NoticeActivity, MainActivity::class.java)
                            startActivity(startIntent)
                        }
                        if (model.image!!.contains("Offer")) {
                            Toast.makeText(
                                this@NoticeActivity,
                                "Enter Promo Code",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        if (model.title!!.contains("Feedback")) {
                            val webView = WebView(this@NoticeActivity)
                            webView.loadUrl(model.image!!)
                        }
                        if (model.image!!.contains("ReferPage")) {
                            val startIntent = Intent(this@NoticeActivity, MainActivity::class.java)
                            startActivity(startIntent)
                        }
                        if (model.title!!.contains("Scratch")) {
                            val srr = model.image
                        }
                        if (model.title!!.contains("Promo")) {
                            val Webviewurl = model.image
                            /* WebView webView = new WebView(NoticeActivity.this);
                                        webView.loadUrl(Webviewurl);*/
                            val i = Intent(Intent.ACTION_VIEW)
                            i.data = Uri.parse(Webviewurl)
                            startActivity(i)
                        }
                        if (model.title!!.contains("http")) {
                            val Webviewurl2 = model.image
                            /* WebView webView = new WebView(NoticeActivity.this);
                                        webView.loadUrl(Webviewurl);*/
                            val i = Intent(Intent.ACTION_VIEW)
                            i.data = Uri.parse(Webviewurl2)
                            startActivity(i)
                        }
                        if (model.image!!.contains("Ads")) {
                            val taskEditText = EditText(this@NoticeActivity)
                            val dialog =
                                AlertDialog.Builder(this@NoticeActivity, R.style.DialogTheme)
                                    .setTitle("Provide Us Your Special Code : ")
                                    .setMessage("This Will Remove Ads From App For Sometime")
                                    .setView(taskEditText)
                                    .setCancelable(false)
                                    .setPositiveButton("SUBMIT") { dialog, which ->
                                        val task = taskEditText.text.toString()
                                    }
                                    .setNegativeButton("Cancel", null)
                                    .create()
                            dialog.show()
                            val positive = dialog.getButton(DialogInterface.BUTTON_POSITIVE)
                            positive.setTextColor(resources.getColor(R.color.colorText))
                            val negative = dialog.getButton(DialogInterface.BUTTON_NEGATIVE)
                            negative.setTextColor(resources.getColor(R.color.colorText))
                        }
                        var j = getPreferencesInt(applicationContext, keynotification, 0)
                        val scnotification =
                            com.appnotification.notificationhistorylog.CommonCl.SharedCommon
                        if (j < 0) {
                            // Toast.makeText(NoticeActivity.this, "Already Added", Toast.LENGTH_SHORT).show();
                        }
                        if (model.image!!.contains("Add 10")) {
                            if (j < 0) {
                                Toast.makeText(
                                    this@NoticeActivity,
                                    "Already Added",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                val i = getPreferencesInt(applicationContext, key1, 50)
                                val preferences =
                                    PreferenceManager.getDefaultSharedPreferences(this@NoticeActivity)
                                val edit = preferences.edit()
                                edit.putInt(key1, i + 10)
                                edit.apply()
                                j--
                                putPreferencesInt(applicationContext, keynotification, j)
                                Toast.makeText(
                                    this@NoticeActivity,
                                    "Yaay !  Added",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } else {
                            Toast.makeText(this@NoticeActivity, "" + model.image, Toast.LENGTH_LONG)
                                .show()
                        }
                    }
                    // ...
                    holder.mView.setOnLongClickListener {
                        val builder = AlertDialog.Builder(this@NoticeActivity, R.style.DialogTheme)
                        builder.setTitle("Copy")
                        builder.setCancelable(true)
                        builder.setPositiveButton("COPY TEXT") { dialogInterface, i ->
                            Toast.makeText(this@NoticeActivity, "Text Copied ", Toast.LENGTH_SHORT)
                                .show()
                            val clipboard = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
                            val clip = ClipData.newPlainText("", model.title)
                            clipboard.setPrimaryClip(clip)
                            dialogInterface.dismiss()
                        }
                            .setNeutralButton("COPY LINK") { dialogInterface, i ->
                                Toast.makeText(
                                    this@NoticeActivity,
                                    "Link Copied ",
                                    Toast.LENGTH_SHORT
                                ).show()
                                val clipboard =
                                    getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = ClipData.newPlainText("", model.image)
                                clipboard.setPrimaryClip(clip)
                                dialogInterface.dismiss()
                            }
                        val dialog = builder.create()
                        dialog.show()
                        val positive = dialog.getButton(DialogInterface.BUTTON_POSITIVE)
                        positive.setTextColor(resources.getColor(R.color.colorText))
                        val negative = dialog.getButton(DialogInterface.BUTTON_NEUTRAL)
                        negative.setTextColor(resources.getColor(R.color.colorText))
                        false
                    }
                }
            }
        mBlogList!!.adapter = adapter
        adapter.startListening()
    }

    protected val isOnline: Boolean
        protected get() {
            val cm = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
            val netInfo = cm.activeNetworkInfo
            return netInfo != null && netInfo.isConnectedOrConnecting
        }

    fun checkConnection() {
        if (isOnline) {
            linernotification!!.visibility = View.GONE
            //progressBar.setVisibility(View.GONE);
            // btnretry.setVisibility(View.GONE);

            /*splashTread = new Thread() {
                @Override
                public void run() {
                    try {
                        int waited = 0;
                        // Splash screen pause time
                        while (waited < 18000) {
                            sleep(100);
                            waited += 100;
                        }
                        Intent intent = new Intent(NoticeActivity.this,
                                MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(intent);
                        NoticeActivity.this.finish();
                    } catch (InterruptedException e) {
                        // do nothing
                    } finally {
                        NoticeActivity.this.finish();
                    }

                }
            };
            splashTread.start();*/


            /*View parentLayout = findViewById(android.R.id.content);

            Snackbar snackbar = Snackbar
                    .make(parentLayout, "Internet Is Back ! " , Snackbar.LENGTH_LONG);

            snackbar.show();*/
            /*  if(progressBar != null && progressBar.isShowing()){
                progressBar.dismiss();


                View parentLayout = findViewById(android.R.id.content);

                Snackbar snackbar = Snackbar
                        .make(parentLayout, "Internet Is Back ! " , Snackbar.LENGTH_SHORT);

                snackbar.show();
            }*/
        } else {
            linernotification!!.visibility = View.VISIBLE
            progressBar!!.visibility = View.GONE
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.notification_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.action_update) {
            updateremote()


            //CreateAlertDialogWithRadioButtonGroup();
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun doFirstRun() {
        val settings = getSharedPreferences("FIRSRRUNMAINNOTIF1", MODE_PRIVATE)
        if (settings.getBoolean("ISFIRSTMANINNOTIF1", true)) {
            /*Toast toast = Toast.makeText(this, "Please Select Any One Plan", Toast.LENGTH_LONG);
            View view = toast.getView();

            view.getBackground().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);

            TextView text = view.findViewById(android.R.id.message);
            text.setTextColor(Color.BLACK);

            toast.show();*/
            val editor = settings.edit()
            editor.putBoolean("ISFIRSTMANINNOTIF1", false)
            editor.apply()
            val builder = AlertDialog.Builder(this@NoticeActivity, R.style.DialogTheme)
            builder.setTitle(R.string.notification_updates)
            builder.setIcon(R.drawable.ic_splash_logo)
            builder.setMessage(R.string.message_notification_update)
            builder.setCancelable(false)
            builder.setPositiveButton(R.string.start) { dialogInterface, i -> dialogInterface.dismiss() }
            val dialog = builder.create()
            dialog.show()
            val positive = dialog.getButton(DialogInterface.BUTTON_POSITIVE)
            positive.setTextColor(resources.getColor(R.color.colorText))
        }
    }

    class UserViewHolder(var mView: View) : RecyclerView.ViewHolder(
        mView
    ) {
        fun setTitle(title: String?) {
            val NameView = mView.findViewById<TextView>(R.id.noti_title)
            NameView.text = title
            Timber.i("@@@ Response title%s", title)
        }

        fun setDesc(desc: String?) {
            val userDesc = mView.findViewById<TextView>(R.id.noti_dec)
            userDesc.text = desc
        }

        fun setImage(image: String?, ctx: Context?) {
            val Image = mView.findViewById<ImageView>(R.id.noti_image)
            //Picasso.get().load(image).into(Image);
        }
    } /*private void showPrice() {

        FlipAnimator animator = new FlipAnimator(priceBeforeView, priceAfterText, priceContainer.getWidth()/2, priceContainer.getHeight()/2);
        animator.setDuration(800);
        animator.setRotationDirection(FlipAnimator.DIRECTION_Y);
        priceContainer.startAnimation(animator);
    }*/
}