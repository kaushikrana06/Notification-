package com.appnotification.notificationhistorylog

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.method.TextKeyListener.clear
import android.util.Log
import android.util.Log.WARN
import android.util.SparseBooleanArray
import android.view.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.appnotification.notificationhistorylog.Interface.IItemClickListner
import com.appnotification.notificationhistorylog.Model.Item
import com.appnotification.notificationhistorylog.ui.MainActivity
import com.appnotification.notificationhistorylog.ui.NewMainActivity
import com.appnotification.notificationhistorylog.viewholder.ItemViewHolder
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.github.aakira.expandablelayout.ExpandableLayoutListenerAdapter
import com.github.aakira.expandablelayout.Utils
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.*
import okhttp3.internal.platform.Platform.WARN

private val Any?.indices: Any
    get() {return indices}

class FAQActivity : AppCompatActivity() {
    //    InterstitialAd mInterstitialAd;
    var recyclerView: RecyclerView? = null
    var items: Any? = ArrayList<Any?>()
    var adapter: FirebaseRecyclerAdapter<Item, ItemViewHolder>? = null
    var expandstate = SparseBooleanArray()
    var txtitem: CharSequence = ""
    var linernotification: LinearLayout? = null
    var progressBar: ProgressBar? = null
    var apppackagename = "com.thetechroot.vision"
    var versionCode = BuildConfig.VERSION_CODE
    var versionName = BuildConfig.VERSION_NAME
    var appid = BuildConfig.APPLICATION_ID
    private var mUserDatabase: DatabaseReference? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_faq)


        //ShowAds();
        val linernotification:LinearLayout = findViewById(R.id.linerfaq)
        linernotification.setVisibility(View.GONE)
        val progressBar: ProgressBar = findViewById(R.id.progressBarfaq)
        progressBar.setVisibility(View.VISIBLE)
        progressBar.setIndeterminate(true)
        progressBar.getIndeterminateDrawable().setColorFilter(0x07578f, PorterDuff.Mode.MULTIPLY)
        val recyclerView: RecyclerView = findViewById(R.id.lst_item)
        recyclerView.setHasFixedSize(true)
        recyclerView.setLayoutManager(LinearLayoutManager(this))
        retrivedata()
        setdata()
        checkConnection()
        mUserDatabase = FirebaseDatabase.getInstance().getReference("Items")
        checkclicklink()
    }

    private fun setdata() {
        val query: Query = FirebaseDatabase.getInstance().reference.child("Items")
        val options = FirebaseRecyclerOptions.Builder<Item>()
            .setQuery(query, Item::class.java)
            .build()
        adapter = object : FirebaseRecyclerAdapter<Item, ItemViewHolder>(options) {
            override fun getItemViewType(position: Int): Int {
                Log.e("Faqs", "items$items")
                return if (items[position] as Boolean) 1 else {
                    0
                }
            }
            @SuppressLint("RecyclerView")
            override fun onBindViewHolder(holder: ItemViewHolder,position: Int, model: Item) {
                linernotification!!.visibility = View.GONE
                progressBar!!.indeterminateDrawable.setColorFilter(
                    0x07578f,
                    PorterDuff.Mode.MULTIPLY
                )
                progressBar!!.visibility = View.GONE
                when (holder.itemViewType) {
                    0 -> {
                        holder.setIsRecyclable(false)
                        holder.txt_item.text = model.text
                        txtitem = holder.txt_item.text

                        /* TextView tv = (TextView) findViewById(R.id.txt_item_text);
                        Typeface face = Typeface.createFromAsset(getAssets(),"fonts/opansans.ttf");
                        tv.setTypeface(face);*/

                        ////
                        ////
                        holder.setiItemClickListner(object : IItemClickListner {
                            override fun onClick(view: View?, position: Int) {
                                Toast.makeText(
                                    this@FAQActivity,
                                    "" + items[position],
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        })
                    }
                    1 -> {
                        holder.setIsRecyclable(true)
                        holder.txt_item.text = model.text
                        holder.txt_child.text = model.subText
                        txtitem = holder.txt_item.text
                        holder.expandableLinearLayout.setInRecyclerView(true)
                        holder.expandableLinearLayout.isExpanded = expandstate[position]
                        holder.expandableLinearLayout.setListener(object :
                            ExpandableLayoutListenerAdapter() {
                            override fun onPreOpen() {
                                changeRoatate(holder.button, 0f, 180f).start()
                                expandstate.put(position, true)
                            }

                            override fun onPreClose() {
                                changeRoatate(holder.button, 180f, 0f).start()
                                expandstate.put(position, false)
                            }
                        })
                        holder.txt_item.setOnClickListener { holder.expandableLinearLayout.toggle() }
                        holder.button.rotation = if (expandstate[position]) 180f else 0f
                        holder.button.setOnClickListener { holder.expandableLinearLayout.toggle() }
                        holder.txt_child.setOnClickListener {
                            holder.expandableLinearLayout.toggle()
                            if (holder.txt_child.text == "Mail To : thexenonstudio@gmail.com") {
                                val toast = Toast.makeText(
                                    this@FAQActivity,
                                    "Mail Us Your Query",
                                    Toast.LENGTH_LONG
                                )
                                val view1 = toast.view
                                view1!!.background.setColorFilter(
                                    Color.WHITE,
                                    PorterDuff.Mode.SRC_IN
                                )
                                val text = view1.findViewById<TextView>(android.R.id.message)
                                text.setTextColor(Color.BLACK)
                                toast.show()
                                val send = Intent(Intent.ACTION_SENDTO)
                                val uriText =
                                    "mailto:" + Uri.encode("thexenonstudio@gmail.com") +
                                            "?subject=" + Uri.encode("Around Me - Contact") +
                                            "&body=" + Uri.encode("Hello, Type Your Query/Problem/Bug/Suggestions Here \n\n\n ------------ \n\n Version Code : $versionCode\n Version Name : $versionName\n Application ID : $appid")
                                val uri = Uri.parse(uriText)
                                send.data = uri
                                startActivity(Intent.createChooser(send, "Send Mail Via : "))
                            }

                            //Toast.makeText(FAQActivity.this, ""+viewHolder.txt_child.getText(), Toast.LENGTH_SHORT).show();
                        }
                        holder.setiItemClickListner(object : IItemClickListner {
                            override fun onClick(view: View?, position: Int) {
                                // Toast.makeText(FAQActivity.this, ""+model.getText(), Toast.LENGTH_SHORT).show();
                            }
                        })
                    }
                    else -> {}
                }
            }

            override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ItemViewHolder {
                return if (viewType == 0) {
                    val itemview = LayoutInflater.from(viewGroup.context)
                        .inflate(R.layout.layout_with_child, viewGroup, false)
                    ItemViewHolder(itemview, viewType == 1)
                } else {
                    val itemview = LayoutInflater.from(viewGroup.context)
                        .inflate(R.layout.layout_with_newchild, viewGroup, false)
                    ItemViewHolder(itemview, viewType == 1)
                }
            }
        }
        expandstate.clear()
        for (i in items.indices) expandstate.append(i, false)
        recyclerView!!.adapter = adapter
    }

    private fun changeRoatate(button: RelativeLayout, from: Float, to: Float): ObjectAnimator {
        val animator = ObjectAnimator.ofFloat(button, "rotation", from, to)
        animator.duration = 300
        animator.interpolator =
            Utils.createInterpolator(Utils.LINEAR_INTERPOLATOR)
        return animator
    }

    private fun retrivedata() {
        with(items) { clear() }
        val db = FirebaseDatabase.getInstance()
            .reference
            .child("Items")
        db.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (itemSnapShot in dataSnapshot.children) {
                    val item = itemSnapShot.getValue(
                        Item::class.java
                    )
                    items.add(item)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(
                    this@FAQActivity,
                    "Something went Wrong $databaseError",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    private fun clear() {
        TODO("Not yet implemented")
    }

    //    private void ShowAds() {
    //
    //        mInterstitialAd = new InterstitialAd(this);
    //
    //        // set the ad unit ID
    //        mInterstitialAd.setAdUnitId("ca-app-pub-5136021335954278/2863631090");
    //
    //        AdRequest adRequest = new AdRequest.Builder().build();
    //
    //        // Load ads into Interstitial Ads
    //        mInterstitialAd.loadAd(adRequest);
    //
    //        mInterstitialAd.setAdListener(new AdListener() {
    //            public void onAdLoaded() {
    //                showInterstitial();
    //            }
    //        });
    //
    //    }
    override fun onBackPressed() {
        super.onBackPressed()
        val startIntent = Intent(this@FAQActivity, NewMainActivity::class.java)
        startActivity(startIntent)
    }

    //    private void showInterstitial() {
    //        if (mInterstitialAd.isLoaded()) {
    //            mInterstitialAd.show();
    //        }
    //    }
    override fun onStart() {
        checkConnection()
        progressBar!!.indeterminateDrawable.setColorFilter(-0xfc9a57, PorterDuff.Mode.MULTIPLY)
        if (adapter != null) adapter!!.startListening()
        super.onStart()
    }

    override fun onStop() {
        if (adapter != null) adapter!!.stopListening()
        super.onStop()
    }

    protected val isOnline: Boolean
        protected get() {
            val cm = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
            val netInfo = cm.activeNetworkInfo
            return netInfo != null && netInfo.isConnectedOrConnecting
        }

    fun checkConnection() {
        if (isOnline) {
            Log.e("", "")
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
                        Intent intent = new Intent(DemoActivity.this,
                                MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(intent);
                        DemoActivity.this.finish();
                    } catch (InterruptedException e) {
                        // do nothing
                    } finally {
                        DemoActivity.this.finish();
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

    override fun onResume() {
        checkConnection()
        super.onResume()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_faq, menu)

        /* MenuItem searchViewItem = menu.findItem(R.id.menu_search);
        final SearchView searchViewAndroidActionBar = (SearchView) MenuItemCompat.getActionView(searchViewItem);
        searchViewAndroidActionBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchViewAndroidActionBar.clearFocus();

                firebaseUserSearch(query.toString());
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_faqs_mail -> {
                mail()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun mail() {
        val taskEditText = EditText(this)
        val dialog = AlertDialog.Builder(this@FAQActivity, R.style.DialogTheme)
            .setTitle("Your Query")
            .setMessage("Provide Details And Send Us Mail ")
            .setView(taskEditText)
            .setCancelable(false)
            .setPositiveButton("Send Mail") { dialog, which ->
                val task = taskEditText.text.toString()
                try {
                    sendmailintent(task)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            .setNegativeButton("Cancel", null)
            .create()
        dialog.show()
        val positive = dialog.getButton(DialogInterface.BUTTON_POSITIVE)
        positive.setTextColor(resources.getColor(R.color.colorText))
    }

    @Throws(Exception::class)
    private fun sendmailintent(task: String) {
        val toast = Toast.makeText(this, "SEND MAIL VIA GMAIL/YAHOO ", Toast.LENGTH_LONG)
        val view = toast.view
        view!!.background.setColorFilter(Color.parseColor("#FF104162"), PorterDuff.Mode.SRC_IN)
        val text = view.findViewById<TextView>(android.R.id.message)
        text.setTextColor(Color.WHITE)
        toast.show()
        val send = Intent(Intent.ACTION_SENDTO)
        val uriText = "mailto:" + Uri.encode("notificationappgp@gmail.com") +
                "?subject=" + Uri.encode("Notification Log App - FAQs") +
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

    private fun firebaseUserSearch(query: String) {
        val parentLayout = findViewById<View>(android.R.id.content)
        val snackbar1 = Snackbar
            .make(parentLayout, "Unable To Find Your Query", Snackbar.LENGTH_LONG)
            .setAction("Send Mail") {
                val toast =
                    Toast.makeText(this@FAQActivity, "Mail Us Your Query", Toast.LENGTH_LONG)
                val view1 = toast.view
                view1!!.background.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN)
                val text = view1.findViewById<TextView>(android.R.id.message)
                text.setTextColor(Color.BLACK)
                toast.show()
                val send = Intent(Intent.ACTION_SENDTO)
                val uriText = "mailto:" + Uri.encode("notificationapp.xenonstudio@gmail.com") +
                        "?subject=" + Uri.encode("Around Me - FAQs") +
                        "&body=" + Uri.encode("$query \n\n\n ------------ \n\n Version Code : $versionCode\n Version Name : $versionName\n Application ID : $appid")
                val uri = Uri.parse(uriText)
                send.data = uri
                startActivity(Intent.createChooser(send, "Send Mail Via : "))
            }
        val firebaseSearchQuery =
            mUserDatabase!!.orderByChild("Items").startAt(query).endAt(query + "\uf8ff")

        //Toast.makeText(this, ""+query, Toast.LENGTH_SHORT).show();

        /*if(query.equals("Hello")){

            Toast.makeText(this, "Hello", Toast.LENGTH_SHORT).show();
        }
*/if (query.isEmpty()) {

            //final View parentLayout = findViewById(android.R.id.content);
            Toast.makeText(this, "Please Search Any Query ", Toast.LENGTH_SHORT).show()

            //Toast.makeText(this, "Hello", Toast.LENGTH_SHORT).show();
        } else {
            snackbar1.show()
        }
        ////
        if (query.contains("Credit") || query.contains("credit")) {

            //final View parentLayout = findViewById(android.R.id.content);
            snackbar1.dismiss()
            Toast.makeText(this, "Credit", Toast.LENGTH_SHORT).show()
            val snackbar = Snackbar
                .make(parentLayout, "Credit System Working ", Snackbar.LENGTH_LONG)
                .setAction("Know More") {
                    val toast = Toast.makeText(
                        this@FAQActivity,
                        "Read How Credit System Work",
                        Toast.LENGTH_LONG
                    )
                    val startIntent = Intent(this@FAQActivity, MainActivity::class.java)
                    startActivity(startIntent)
                }
            snackbar.show()
            //Toast.makeText(this, "Hello", Toast.LENGTH_SHORT).show();
        } else {
            snackbar1.show()
        }
        if (query.contains("Offer") || query.contains("offer")) {

            //final View parentLayout = findViewById(android.R.id.content);
            snackbar1.dismiss()
            val snackbar = Snackbar
                .make(parentLayout, "Tap To See Offers/Promo Codes", Snackbar.LENGTH_LONG)
                .setAction("Know") {
                    val toast = Toast.makeText(
                        this@FAQActivity,
                        "Read How Credit System Work",
                        Toast.LENGTH_LONG
                    )
                    val builder = AlertDialog.Builder(this@FAQActivity, R.style.DialogTheme)
                    builder.setTitle("Offers ")
                    builder.setMessage("You Can Avail Offer And Promo Code \n Go To Notification Section And You Will See Offers There If Any Available")
                    builder.setCancelable(false)
                    builder.setPositiveButton("Got It") { dialogInterface, i -> dialogInterface.dismiss() }
                    val dialog = builder.create()
                    dialog.show()
                    val positive = dialog.getButton(DialogInterface.BUTTON_POSITIVE)
                    positive.setTextColor(resources.getColor(R.color.colorText))
                }
            snackbar.show()
            //Toast.makeText(this, "Hello", Toast.LENGTH_SHORT).show();
        } else {


            //final View parentLayout = findViewById(android.R.id.content);
            snackbar1.show()
            /*Toast.makeText(this, "Unable To Find What You Are Searching", Toast.LENGTH_SHORT).show();
             */
        }
        if (query.contains("Credit") || query.contains("credit")) {

            //final View parentLayout = findViewById(android.R.id.content);
            snackbar1.dismiss()
            Toast.makeText(this, "Credit", Toast.LENGTH_SHORT).show()
            val snackbar = Snackbar
                .make(parentLayout, "Credit System Working ", Snackbar.LENGTH_LONG)
                .setAction("Know More") {
                    val toast = Toast.makeText(
                        this@FAQActivity,
                        "Read How Credit System Work",
                        Toast.LENGTH_LONG
                    )
                    val startIntent = Intent(this@FAQActivity, MainActivity::class.java)
                    startActivity(startIntent)
                }
            snackbar.show()
            //Toast.makeText(this, "Hello", Toast.LENGTH_SHORT).show();
        }


        /*FirebaseRecyclerAdapter<Users, UsersViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Users, UsersViewHolder>(

                Users.class,R.layout.layout_with_child, UsersViewHolder.class, firebaseSearchQuery);*/
    }

    /*@Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            //use the query to search

            if(query.contains("Hello")){

                Toast.makeText(this, "Hello !", Toast.LENGTH_SHORT).show();
            }

        }
    }*/
    override fun onPause() {
        checkConnection()
        super.onPause()
    }

    private fun checkclicklink() {
        val uri = intent.data
        val strUsername = ""
        val strPassword = ""
        if (uri != null) {
            Toast.makeText(this, "FAQs", Toast.LENGTH_SHORT).show()
        } else {
            // Your app will pop up even if http://www.myurl.com/sso is clicked, so better to handle null uri
        }
    }
}

private fun Any?.add(item: Item?) {
item.add(item)
}

private operator fun Int.next(): Int {
    var c=0
    c++
    return c
}

private operator fun Int.hasNext(): Boolean {

    return true
}

private operator fun Any.iterator(): Int {

    var c=0
    c++
    return c
}

private operator fun Any?.get(position: Int): Any {
     var c=0
     c++
    return c

}





