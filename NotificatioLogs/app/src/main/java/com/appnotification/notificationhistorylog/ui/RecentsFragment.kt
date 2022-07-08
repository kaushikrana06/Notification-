package com.appnotification.notificationhistorylog.ui

//import com.google.android.gms.ads.AdRequest
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.fragment.app.Fragment
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
import com.facebook.ads.Ad
import com.facebook.ads.AdError
import com.facebook.ads.AdSize
import com.facebook.ads.AdView
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.formats.UnifiedNativeAd
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*

/**
 * A simple [Fragment] subclass.
 */
class RecentsFragment : Fragment(), OnRefreshListener {
    private val mNativeAds: MutableList<UnifiedNativeAd> = ArrayList()
    var imgmore: ImageView? = null
    //var imgemnu: ImageView? = null
  private lateinit var imgemnu: ImageView
    //    private AdView mAdView;
    var whatnew: String? = null
    var adappid: String? = null
    var nativeadid: String? = null
    var hisormine: String? = null
    var eroorcode: String? = null
    var livenotice: String? = null
    var versionCode = BuildConfig.VERSION_CODE
    var versionName = BuildConfig.VERSION_NAME
    var appid = BuildConfig.APPLICATION_ID
    var firebaseRemoteConfigprice: FirebaseRemoteConfig? = null
    private var mUserRef: DatabaseReference? = null
    private var museref: DatabaseReference? = null
    private var mdatareport: DatabaseReference? = null
    private val mcredtref: DatabaseReference? = null
    private val mlinkupdate: DatabaseReference? = null
    private var adLoader: AdLoader? = null
    private var mAuth: FirebaseAuth? = null
    private var recyclerView: RecyclerView? = null
    private var swipeRefreshLayout: SwipeRefreshLayout? = null
    private val selectedNavigation: TextView? = null
    private var searchEdit: EditText? = null
    private var mAdView: AdView? = null
    private var adListener: com.facebook.ads.AdListener? = null
    private var isSearchVisible = false
    private fun showads() {
        adListener = object : com.facebook.ads.AdListener {
            override fun onError(ad: Ad, adError: AdError) {
                Timber.e("FacebookAdError: " + adError.errorCode + " : " + adError.errorMessage)
            }

            override fun onAdLoaded(ad: Ad) {}
            override fun onAdClicked(ad: Ad) {
                val sc = SharedCommon
                var i = getPreferencesInt(context, key1, 0)
                i++
                putPreferencesInt(context, SharedCommon.key1, i)
            }

            override fun onLoggingImpression(ad: Ad) {}
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_recents, container, false)
        isSearchVisible = false
        //        getActivity().setTitle("Notifications");
        val bundle = this.arguments
        //        String fragmentName = bundle.getString("selected_navigation");

        // Initialize the Mobile Ads SDK.
        if (savedInstanceState == null) {
            //loadNativeAds();
            Timber.i("nativeads LOG")
        }
        //selectedNavigation = view.findViewById(R.id.selected_navigation);

        //selectedNavigation.setText(fragmentName);
        val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(activity)
        val recyclerView:RecyclerView = view.findViewById(R.id.list)
        recyclerView.layoutManager = layoutManager
        imgemnu=view.findViewById(R.id.imageView2)
        imgemnu.setOnClickListener{
            (activity as NewMainActivity)?.openDrawer()
        }
        val imgmore:ImageView = view.findViewById(R.id.imgmore)
        imgmore.setOnClickListener{
            (activity as NewMainActivity).showPopup(view)
            // ((NewMainActivity)getActivity()).openactionmenu();
        }
        val searchButton = view.findViewById<ImageView>(R.id.imgsearch)
        searchButton.setOnClickListener {
            val editView = requireActivity().currentFocus
            if (editView != null) {
                val imm =
                    requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(editView.windowToken, 0)
            }
            Timber.i("onClick: SearchButton: isSearchVisible:  %s", isSearchVisible)
            requireActivity().runOnUiThread {
                if (isSearchVisible) {
                    isSearchVisible = false
                    searchEdit?.setText("")
                    searchEdit?.visibility = View.GONE
                } else {
                    isSearchVisible = true
                    searchEdit?.visibility = View.VISIBLE
                }
            }
        }
        val swipeRefreshLayout:SwipeRefreshLayout = view.findViewById(R.id.swiper)
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent)
        swipeRefreshLayout.setOnRefreshListener(this)
        update()
        val searchEdit:EditText = view.findViewById(R.id.edit_search)
        searchEdit.isCursorVisible = false
        searchEdit.visibility = View.GONE
        searchEdit.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                searchEdit.isCursorVisible = false
            }

            override fun afterTextChanged(editable: Editable) {
                val mAdapter = BrowseAdapterHome(activity!!, mNativeAds)
                mAdapter.filterList(mAdapter.filter(editable.toString()) as ArrayList<BrowseAdapterHome.DataItem>)
                recyclerView.adapter = mAdapter
                searchEdit.isCursorVisible = true
                firebasedatabseupdate(editable.toString())
            }
        })
        //ADS+Firebase
        //mAdView = findViewById(R.id.adView);
        //AdRequest adRequest = new AdRequest.Builder().build();
        // mAdView.loadAd(adRequest);
        mAdView = AdView(context, getString(R.string.fb_banner_ad), AdSize.BANNER_HEIGHT_50)
        showads()
        mAdView?.loadAd(mAdView?.buildLoadAdConfig()?.withAdListener(adListener)?.build())
        (view.findViewById<View>(R.id.fb_container) as LinearLayout).addView(mAdView)
        firebaseRemoteConfigprice = FirebaseRemoteConfig.getInstance()
        val configSettings = FirebaseRemoteConfigSettings.Builder().build()
        firebaseRemoteConfigprice?.setConfigSettingsAsync(configSettings)
        val pricedata: MutableMap<String, Any> = HashMap()
        pricedata["shownativeads"] = "yn"
        pricedata["appid"] = "yn"
        pricedata["nativeadid"] = "yn"
        pricedata["hisormine"] = "yn"
        firebaseRemoteConfigprice?.setDefaultsAsync(pricedata)
        checkadstatus()
        return view
    }

    private fun checkadstatus() {
        run {
            firebaseRemoteConfigprice!!.fetch(0)
                .addOnCompleteListener { task ->
                    Timber.i("info%s", firebaseRemoteConfigprice!!.info.lastFetchStatus)
                    Timber.i("firebaseremote%s", firebaseRemoteConfigprice!!.getString("btn_text"))
                    if (task.isSuccessful) {
                        firebaseRemoteConfigprice?.fetchAndActivate()
                        whatnew = firebaseRemoteConfigprice?.getString("shownativeads")
                        adappid = firebaseRemoteConfigprice?.getString("appid")
                        nativeadid = firebaseRemoteConfigprice?.getString("nativeadid")
                        hisormine = firebaseRemoteConfigprice?.getString("hisormine")
                        Timber.i("adappid : %s", adappid)
                        Timber.i("nativeadid : %s", nativeadid)
                        if (whatnew == "yes") {
                            loadNativeAds()

                            //Toast.makeText(MainActivity.this, "Showing", Toast.LENGTH_SHORT).show();
                        } else if (whatnew == "no") {
                            //mNativeAds.remove(true)
                            /* Toast.makeText(MainActivity.this, "Nope", Toast.LENGTH_SHORT).show(); */
                            Timber.i("AdsStatus Not Showing")
                        }
                        /* if (hisormine.equals("mine")){
                                        loadNativeAds();
    
                                        //Toast.makeText(MainActivity.this, "Showing", Toast.LENGTH_SHORT).show();
                                    } else if (hisormine.equals("his")){
                                        adappid = "";
                                        nativeadid = nativeadid;
    
                                        // Toast.makeText(MainActivity.this, "Nope", Toast.LENGTH_SHORT).show();
                                        Log.e("AdsStatus","his");
    
                                    }
                                    else if (hisormine.equals("test")){
                                        adappid = "ca-app-pub-8081344892743036~8262343723";
                                        nativeadid = "ca-app-pub-3940256099942544/2247696110";
    
                                        // Toast.makeText(MainActivity.this, "Nope", Toast.LENGTH_SHORT).show();
                                        Log.e("AdsStatus","his");
    
                                    }*/Timber.i(
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
                                activity,
                                getString(R.string.check_your_internet_connection),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
        }
    }

    override fun onDetach() {
        if (mAdView != null) {
            mAdView?.destroy()
        }
        super.onDetach()
    }
@Suppress("DEPRECATION")
    private fun loadNativeAds() {
        val builder = AdLoader.Builder(activity, getString(R.string.ad_native_unit_id))
        adLoader =
            builder.forUnifiedNativeAd { unifiedNativeAd -> // A native ad loaded successfully, check if the ad loader has finished loading
                // and if so, insert the ads into the list.
                mNativeAds.add(unifiedNativeAd)
                if (!adLoader?.isLoading!!) {
                    update()
                }
            }.withAdListener(
                object : AdListener() {
                    @Deprecated("Deprecated in Java")
                    override fun onAdFailedToLoad(errorCode: Int) {
                        // A native ad failed to load, check if the ad loader has finished loading
                        // and if so, insert the ads into the list.
                        Timber.i(
                            "The previous native ad failed to load. Attempting to"
                                    + " load another." + errorCode
                        )
                        eroorcode = errorCode.toString()
                        if (!adLoader?.isLoading!!){
                            update()
                        }
                    }
                }).build()

        // Load the Native ads.
       // adLoader.loadAds(AdRequest.Builder().build(), NUMBER_OF_ADS)

//        final NativeAd nativeAd = new NativeAd(getContext(), getString(R.string.fb_native_ad));
//        nativeAd.loadAd();
//
//        if (nativeAd.isAdLoaded()) {
//            inflateAd(getContext(), nativeAd, nativeAdLayout);
//        }
    }

    //    public static void inflateAd(Context context, NativeAd nativeAd, NativeAdLayout nativeAdLayout) {
    //        nativeAd.unregisterView();
    //        try {
    //            int i = 0;
    //            View view = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.native_ad_layout_1, nativeAdLayout, false);
    //            nativeAdLayout.addView(view);
    //            LinearLayout linearLayout = (LinearLayout) view.findViewById(R.id.ad_choices_container);
    //            AdOptionsView adOptionsView = new AdOptionsView(context, nativeAd, nativeAdLayout);
    //            linearLayout.removeAllViews();
    //            linearLayout.addView(adOptionsView, 0);
    //            TextView textView = (TextView) view.findViewById(R.id.native_ad_title);
    //            MediaView mediaView2 = (MediaView) view.findViewById(R.id.native_ad_media);
    //            TextView textView2 = (TextView) view.findViewById(R.id.native_ad_social_context);
    //            TextView textView3 = (TextView) view.findViewById(R.id.native_ad_body);
    //            TextView textView4 = (TextView) view.findViewById(R.id.native_ad_sponsored_label);
    //            Button button = (Button) view.findViewById(R.id.native_ad_call_to_action);
    //            textView.setText(nativeAd.getAdvertiserName());
    //            textView3.setText(nativeAd.getAdBodyText());
    //            textView2.setText(nativeAd.getAdSocialContext());
    //            if (!nativeAd.hasCallToAction()) {
    //                i = 4;
    //            }
    //            button.setVisibility(i);
    //            button.setText(nativeAd.getAdCallToAction());
    //            textView4.setText(nativeAd.getSponsoredTranslation());
    //            List arrayList = new ArrayList();
    //            arrayList.add(textView);
    //            arrayList.add(button);
    //            nativeAd.registerViewForInteraction(view, mediaView2, arrayList);
    //        } catch (Exception e) {
    //            e.printStackTrace();
    //        }
    //    }
    private fun firebasedatabseupdate(s: String) {
        mAuth = FirebaseAuth.getInstance()
        val currentUser = mAuth?.currentUser
        if (currentUser == null) {
            sendToStart()
        } else {
            val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZZZZZ", Locale.getDefault())
            val datte = format.format(Date())
            val idtimedate = datte.substring(5, 10)
            val idtime = datte.substring(11, 19)
            mdatareport = mAuth?.currentUser?.uid?.let {
                FirebaseDatabase.getInstance().reference.child("UsageReports").child(
                    it
                )
            }
            museref = mAuth?.currentUser?.let {
                it?.uid?.let { it1 ->
                    FirebaseDatabase.getInstance().reference.child("mainacreport").child(
                        it1
                    )
                }
            }
            mUserRef = mAuth?.currentUser?.uid?.let {
                FirebaseDatabase.getInstance().reference.child("Search-Query").child(
                    it
                )
            }
            val adapter = BrowseAdapterHome(requireActivity(), mNativeAds)
            val coint = adapter.itemCount.toString()
            mUserRef?.child("Last-Query")?.setValue(s)
            mUserRef?.child("Log-Count-While-Searching")?.setValue(coint)
            mUserRef?.child("Last-Query-Time")?.setValue(datte)
            mUserRef?.child("ADERCO")?.setValue(eroorcode)

            //username = currentUser.getUid();
            //SAVEDATAREPORT
            //savereport(currentUser);
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (data != null && DetailsActivity.ACTION_REFRESH == data.getStringExtra(DetailsActivity.EXTRA_ACTION)) {
            update()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    private fun sendToStart() {

        //username = "Not Signed In";
        //Toast.makeText(this, "Not Signed", Toast.LENGTH_SHORT).show();;
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_refresh -> {
                update()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    /*    @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setHasOptionsMenu(true);
        }*/
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

    private fun update() {
        val adapter = BrowseAdapterHome(requireActivity(), mNativeAds)
        recyclerView?.adapter = adapter
        if (adapter.itemCount == 0) {
            Toast.makeText(activity, getString(R.string.message_no_notify_yet), Toast.LENGTH_SHORT)
                .show()

            /*Toast.makeText(getContext(), R.string.empty_log_file, Toast.LENGTH_LONG).show();
            Intent startIntent = new Intent(getActivity(), IssueActivity.class);
            startActivity(startIntent);*/
        }
    }

    override fun onRefresh() {
        update()
        swipeRefreshLayout?.isRefreshing = false
    }

    fun refreshAdapter() {
        update()
        swipeRefreshLayout?.isRefreshing = false
    }

    companion object {
        const val NUMBER_OF_ADS = 10
    }
}