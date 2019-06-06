package com.app.tf.livnlive

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.activity_feeds.*
import kotlinx.android.synthetic.main.feedsitem.view.*
import android.widget.*
import com.google.firebase.auth.FirebaseAuth
import com.facebook.login.LoginManager
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.profsetting.view.*
import android.graphics.Color
import android.os.Handler
import android.content.*
import android.content.pm.ActivityInfo
import android.support.v7.app.AlertDialog
import android.support.v4.content.ContextCompat
import android.location.LocationManager
import android.location.LocationListener
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.location.Location
import android.net.Uri
import android.os.IBinder
import android.support.v4.content.LocalBroadcastManager
import android.util.Log
import com.android.billingclient.api.BillingClient
import com.android.vending.billing.IInAppBillingService
import com.app.tf.livnlive.R.id.feedslist
import com.app.tf.livnlive.R.id.imageView
import com.app.tf.livnlive.model.Category
import com.app.tf.livnlive.model.ResponseCategory
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.smarteist.autoimageslider.*
import mekotlinapps.dnyaneshwar.`in`.restdemo.model.Response
import mekotlinapps.dnyaneshwar.`in`.restdemo.model.ResponseBottom
import mekotlinapps.dnyaneshwar.`in`.restdemo.rest.APIClient
import mekotlinapps.dnyaneshwar.`in`.restdemo.rest.ApiInterface
import org.json.JSONArray
import retrofit2.Call
import retrofit2.Callback
import java.util.*

var miles="5";
var catid="0";
var mService: IInAppBillingService? = null
var spinnerCategory: Spinner?=null
var listItemsTxt1 = arrayOf("Select Category","All")
var categories = ArrayList<Category>()
var latitude="";
var ads: MutableList<FeedsData> = mutableListOf()
var longitude="";
var imguri="";
class FeedsActivity:AppCompatActivity() {

    var flag=true;

    var imgarr : JSONArray? =null;


    private val mInterval = 20000 // 5 seconds by default, can be changed later
    private var mHandlerService: Handler? = null
    var topMost = true

    val mServiceConn: ServiceConnection = object: ServiceConnection {

        override fun onServiceDisconnected(name: ComponentName) {
            mService = null
        }

        override fun onServiceConnected(name: ComponentName, service: IBinder) {

            mService = IInAppBillingService.Stub.asInterface(service);
        }
    }

    val msgReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            var type = intent!!.getStringExtra("type")
            if (type != null) {
                if (type == "L") {
                    val name = intent!!.getStringExtra("name")
                    val dialog: AlertDialog
                    val builder = AlertDialog.Builder(this@FeedsActivity)
                        builder.setTitle("")
                        builder.setMessage(name + " liked you")
                        builder.setPositiveButton("Ok"){dialog, which ->
                            dialog.dismiss()
                        }
                        dialog = builder.create()
                        dialog.show()
                        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK)

                }
            }
        }
    }

    val matchReceived = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (DataManager.userObject.userProfile == null || DataManager.userObject.userInterests == null) {
                DataManager.fetchUserData(this@FeedsActivity) { task ->
                    Log.d("", "task" + task)
                    if (task) {
//                        DataManager.fetchSearchedUsers(this@FeedsActivity) {
                            feedslist.layoutManager = LinearLayoutManager(this@FeedsActivity)
                        val preferences = context!!.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
                        val type = preferences.getInt("type", 0)
                        var array= arrayOf<String>("");
                        if(type==1)
                            feedslist.adapter = FeedsActivityItem(this@FeedsActivity, (DataManager.userObject.searchedUsers.distinctBy { it.id }+ads).shuffled(), imgarr)
                        else
                            feedslist.adapter = FeedsActivityItem(this@FeedsActivity, ads.shuffled(), imgarr)
//
//
//  }
                    } else {
                        DataManager.isFirstSetup = true
                        val search = Intent(this@FeedsActivity, ProfileActivity::class.java)
                        startActivity(search)
                        finish()
                    }
                }
            }
            else {
                DataManager.fetchSearchedUsers(this@FeedsActivity) {
                    feedslist.layoutManager = LinearLayoutManager(this@FeedsActivity)
                   // feedslist.adapter = FeedsActivityItem(this@FeedsActivity, (DataManager.userObject.searchedUsers.distinctBy { it.id }+ads).shuffled(), imgarr)
                    val preferences = context!!.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
                    val type = preferences.getInt("type", 0)
                    var array= arrayOf<String>("");
                    if(type==1)
                        feedslist.adapter = FeedsActivityItem(this@FeedsActivity, (DataManager.userObject.searchedUsers.distinctBy { it.id }+ads).shuffled(), imgarr)
                    else
                        feedslist.adapter = FeedsActivityItem(this@FeedsActivity, ads.shuffled(), imgarr)
//
                }
            }
        }
    }

    val msgReceiverMsg = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            var type = intent!!.getStringExtra("type")
            if (type != null) {
                if (type == "M" && topMost) {
                    val name = intent!!.getStringExtra("name")
                    val message = intent!!.getStringExtra("message")
                    val builder = AlertDialog.Builder(this@FeedsActivity)
                    builder.setTitle("")
                    builder.setMessage("New message from " + name)
                    builder.setPositiveButton("Ok"){dialog, which ->
                    }
                    val dialog: AlertDialog = builder.create()
                    dialog.show()
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        topMost = true
        setting()
        LocalBroadcastManager.getInstance(this).registerReceiver(msgReceiver, IntentFilter("FCMLK"));
        LocalBroadcastManager.getInstance(this).registerReceiver(msgReceiverMsg, IntentFilter("FCMMSG"));
        LocalBroadcastManager.getInstance(this).registerReceiver(matchReceived, IntentFilter("FCMSM"));
        val currentFirebaseUser = FirebaseAuth.getInstance().currentUser
        var apiServices = APIClient.client.create(ApiInterface::class.java)

        val call = apiServices.checkSubscription(currentFirebaseUser!!.uid)
        val preferences = this.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)

        call.enqueue(object : Callback<Response> {
            override fun onResponse(call: Call<Response>, response: retrofit2.Response<Response>) {
               if(response.body()!!.result=="bad"||response.body()!!.result=="invalid")
                {
                    val editor = preferences.edit()
                    editor.putBoolean("validation", false)
                    editor.commit()
                }
                else
                {
                    val editor = preferences.edit()
                    editor.putBoolean("validation", true)
                    editor.commit()
                }
            }

            override fun onFailure(call: Call<Response>?, t: Throwable?) {

                val editor = preferences.edit()
                editor.putBoolean("validation", false)
                editor.commit()
            }
        })



        flag=true;
    }

    override fun onPause() {
        super.onPause()
        topMost = false

    }

    var isMenuVisible = false
    private var locationManager : LocationManager? = null
    private val locationListener: LocationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {

            latitude=location.latitude.toString();
            longitude=location.longitude.toString();
            DataManager.saveLocation(location.latitude, location.longitude)
            if(flag)
            {
             //   latitude="26.6459";
             //   longitude="-80.4303";
                var apiServices = APIClient.client.create(ApiInterface::class.java)
                val call1 = apiServices.bottom(latitude,longitude);

                call1.enqueue(object : Callback<ResponseBottom> {
                    override fun onResponse(call: Call<ResponseBottom>, response: retrofit2.Response<ResponseBottom>) {
                        if(response.body()!!.result=="bad"||response.body()!!.result=="invalid")
                        {
                            //JSONArray arr=new JSONArray();
                             }
                        else
                        {
                            setSliderViews(response.body()!!.bannerImageUri!!, response.body()!!.banners!!)

                        }
                    }

                    override fun onFailure(call: Call<ResponseBottom>?, t: Throwable?) {

System.out.println("failure");
                    }
                })

                flag=false;
            }
//            thetext.setText("" + location.longitude + ":" + location.latitude);
        }
        override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
        override fun onProviderEnabled(provider: String) {}
        override fun onProviderDisabled(provider: String) {}
    }


    private fun setSliderViews(base : String, images : ArrayList<Object>) {
        val sliderLayout: SliderLayout
        sliderLayout = findViewById(R.id.imageSlider)
        sliderLayout!!.clearSliderViews()
        var close : ImageView
        close = findViewById(R.id.close)
        close.visibility=View.VISIBLE
        close.setOnClickListener(View.OnClickListener { sliderLayout.visibility = View.INVISIBLE
        close.visibility = View.INVISIBLE
        })
        sliderLayout.setIndicatorAnimation(IndicatorAnimations.SWAP) //set indicator animation by using SliderLayout.IndicatorAnimations. :WORM or THIN_WORM or COLOR or DROP or FILL or NONE or SCALE or SCALE_DOWN or SLIDE and SWAP!!
        sliderLayout.setSliderTransformAnimation(SliderAnimations.FADETRANSFORMATION)
        sliderLayout.scrollTimeInSec = 10 //set scroll delay in seconds :
        sliderLayout.visibility=View.VISIBLE
        val gson = Gson() // for pretty print feature

        var jsonStr : String = "";
        jsonStr= gson.toJson(images)

        var images1 : JSONArray=JSONArray(jsonStr)
        //images1=JSONArray(jsonStr);
        //setSliderViews()
       // sliderLayout!!.removeAllViews()
        for (i in 0 until images1.length()) {

            val sliderView = DefaultSliderView(this)

            when (i) {
                i ->sliderView.imageUrl = base+images1.getJSONObject(i).get("img")
             /*   0 -> sliderView.setImageDrawable(R.drawable.ic_launcher_background)
                1 -> sliderView.imageUrl = "https://images.pexels.com/photos/218983/pexels-photo-218983.jpeg?auto=compress&cs=tinysrgb&dpr=2&h=750&w=1260"
                2 -> sliderView.imageUrl = "https://images.pexels.com/photos/747964/pexels-photo-747964.jpeg?auto=compress&cs=tinysrgb&h=750&w=1260"
                3 -> sliderView.imageUrl = "https://images.pexels.com/photos/929778/pexels-photo-929778.jpeg?auto=compress&cs=tinysrgb&dpr=2&h=750&w=1260"
           */ }

            sliderView.setImageScaleType(ImageView.ScaleType.CENTER_CROP)
            sliderView.description = " "

            sliderView.setOnSliderClickListener {
                val openURL = Intent(android.content.Intent.ACTION_VIEW)
                openURL.data = Uri.parse(images1.getJSONObject(i).get("url") as String?)
                startActivity(openURL)
                //Toast.makeText(this@FeedsActivity, "This is slider " + (i + 1), Toast.LENGTH_SHORT).show()
            }

            //at last add this view in your layout :
            sliderLayout!!.addSliderView(sliderView)
           // images1.remove(i-1);
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feeds)
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        topMost = true
        val view  = findViewById<View>(R.id.imageFullView)
        val imageView = findViewById<ImageView>(R.id.searchImageView)
        val button = findViewById<Button>(R.id.closeButton)
        val listItemsTxt = arrayOf("Select Radius","1 Mile", "3 Mile", "5 Mile")
        var spinnerAdapter: ArrayAdapter<String> = ArrayAdapter<String>(applicationContext, R.layout.view_drop_down_menu, listItemsTxt)
        var spinnerRadius=findViewById<Spinner>(R.id.spinnerRadius)
        spinnerRadius?.adapter = spinnerAdapter
        spinnerRadius.setSelection(3);
        spinnerCategory=findViewById<Spinner>(R.id.spinnerCategory)
        var spinnerAdapter1: ArrayAdapter<String> = ArrayAdapter<String>(applicationContext!!, R.layout.view_drop_down_menu, listItemsTxt1)

        spinnerCategory?.adapter = spinnerAdapter1
        spinnerRadius?.setOnItemSelectedListener (object : AdapterView.OnItemSelectedListener {

            override fun onNothingSelected(p0: AdapterView<*>?) {

                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                if(listItemsTxt[position].contains("Select"))
                    miles="0";
                if(listItemsTxt[position].contains("1"))
                    miles="1";
                if(listItemsTxt[position].contains("3"))
                    miles="3";
                if(listItemsTxt[position].contains("5"))
                    miles="5";
            }


        })

        spinnerCategory?.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {

            override fun onNothingSelected(p0: AdapterView<*>?) {

                // TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                if(listItemsTxt1[position].contains("Select"))
                    catid="0";
                else if(listItemsTxt1[position].contains("All"))
                    catid="0";
                else
                {
                    catid= categories.get(position-1).geolcoationBannerCategoryID!!
                }
                latitude="22.66215";
                longitude="75.9035";
                var apiServices = APIClient.client.create(ApiInterface::class.java)
                val call1 = apiServices.loc(latitude,longitude, catid, miles);

                call1.enqueue(object : Callback<ResponseBottom> {
                    override fun onResponse(call: Call<ResponseBottom>, response: retrofit2.Response<ResponseBottom>) {
                        if(response.body()!!.result=="bad"||response.body()!!.result=="invalid")
                        {
                            //JSONArray arr=new JSONArray();
                        }
                        else
                        {
                            //setSliderViews(response.body()!!.bannerImageUri!!, response.body()!!.banners!!)

                            val gson = Gson() // for pretty print feature

                            var jsonStr : String = "";
                            jsonStr= gson.toJson(response.body()!!.banners!!)

                            imguri=response.body()!!.bannerImageUri!!
                            imgarr =JSONArray(jsonStr)

                            ads.clear()
                            for (i in 0..(imgarr!!.length() - 1)) {
                                val item = imgarr!!.getJSONObject(i)
                                var image = BitmapFactory.decodeResource(applicationContext.getResources(), R.drawable.defaultprofile)//getBitmapFromURL(context, userImageUrl)

                                var adObj = FeedsData(image,"advertisement##"+item.getString("img"),item.getString("url"),item.getString("adsType")+"##"+item.getString("distance"))

                                ads.add(adObj)
                                // Your code here
                            }


                            //var newlist=list+ listOf(imgarr)


                }
            }

            override fun onFailure(call: Call<ResponseBottom>?, t: Throwable?) {

                System.out.println("failure");
            }
        })


    }


})
        var switchItem=findViewById<Switch>(R.id.goLive)
        switchItem?.isChecked = DataManager.userObject.isOnline
        var profImageView=findViewById<ImageView>(R.id.profImageView)
        profImageView?.setImageBitmap(DataManager.userObject.userInterests!!.userImage)
        if (DataManager.myImage != null) {
            profImageView?.setImageBitmap(DataManager.myImage)
        }
        var descText=findViewById<TextView>(R.id.descText)
        var yourMatches=findViewById<TextView>(R.id.yourmatches)
       if (DataManager.userObject.isOnline) {
           descText?.text = "You are Liv'n Live!"
            //holder?.descText?.setTextColor(-0x1)
           yourMatches?.text = "Your Matches:"
        }
        else {
           descText?.text = "Flip the switch to go live!"
            //holder?.descText?.setTextColor(-0x1)
           yourMatches?.text = ""
        }
        switchItem?.setOnCheckedChangeListener({ view : View, isChecked: Boolean ->
            if (isChecked) {
                descText?.text = "You are Liv'n Live!"
                //holder?.descText?.setTextColor(-0x1)
                yourMatches?.text = "Your Matches:"
            }
            else {
                descText?.text = "Flip the switch to go live!"
                //holder?.descText?.setTextColor(-0x1)
                yourMatches?.text = ""
            }

        })
        view.visibility = View.INVISIBLE
        imageView.visibility = View.INVISIBLE
        button.visibility = View.INVISIBLE
        latitude="22.66215";
        longitude="75.9035";
        var apiServices = APIClient.client.create(ApiInterface::class.java)
        val call1 = apiServices.loc(latitude,longitude, catid, miles);

        call1.enqueue(object : Callback<ResponseBottom> {
            override fun onResponse(call: Call<ResponseBottom>, response: retrofit2.Response<ResponseBottom>) {
                if(response.body()!!.result=="bad"||response.body()!!.result=="invalid")
                {
                    //JSONArray arr=new JSONArray();
                }
                else
                {
                    //setSliderViews(response.body()!!.bannerImageUri!!, response.body()!!.banners!!)

                    val gson = Gson() // for pretty print feature

                    var jsonStr : String = "";
                    jsonStr= gson.toJson(response.body()!!.banners!!)

                    imguri=response.body()!!.bannerImageUri!!
                    imgarr =JSONArray(jsonStr)
ads.clear()
                    for (i in 0..(imgarr!!.length() - 1)) {
                        val item = imgarr!!.getJSONObject(i)
                        var image = BitmapFactory.decodeResource(getResources(), R.drawable.defaultprofile)//getBitmapFromURL(context, userImageUrl)

                        var adObj = FeedsData(image,"advertisement##"+item.getString("img"),item.getString("url"),item.getString("adsType")+"##"+item.getString("distance"))

                        ads.add(adObj)
                        // Your code here
                    }


                                //var newlist=list+ listOf(imgarr)

                            /*}
                            }
                        }*/
                    }
            }

            override fun onFailure(call: Call<ResponseBottom>?, t: Throwable?) {

                System.out.println("failure");
            }
        })


        if (DataManager.userObject.userProfile == null || DataManager.userObject.userInterests == null) {
            DataManager.fetchUserData(this@FeedsActivity) { task ->
                Log.d("", "task" + task)
                if (task) {
//                        DataManager.fetchSearchedUsers(this@FeedsActivity) {
                    feedslist.layoutManager = LinearLayoutManager(this@FeedsActivity)
                   // feedslist.adapter = FeedsActivityItem(this@FeedsActivity, (DataManager.userObject.searchedUsers.distinctBy { it.id }+ads).shuffled(), imgarr)
                    val preferences = this.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
                    val type = preferences.getInt("type", 0)
                    var array= arrayOf<String>("");
                    if(type==1)
                        feedslist.adapter = FeedsActivityItem(this@FeedsActivity, (DataManager.userObject.searchedUsers.distinctBy { it.id }+ads).shuffled(), imgarr)
                    else
                        feedslist.adapter = FeedsActivityItem(this@FeedsActivity, ads.shuffled(), imgarr)
//
//                        }
                } else {
                    DataManager.isFirstSetup = true
                    val search = Intent(this@FeedsActivity, ProfileActivity::class.java)
                    startActivity(search)
                    finish()
                }
            }
        }
        else {
            feedslist.layoutManager = LinearLayoutManager(this@FeedsActivity)
           // feedslist.adapter = FeedsActivityItem(this@FeedsActivity, (DataManager.userObject.searchedUsers.distinctBy { it.id }+ads).shuffled(), imgarr)
            val preferences = this.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
            val type = preferences.getInt("type", 0)
            var array= arrayOf<String>("");
            if(type==1)
                feedslist.adapter = FeedsActivityItem(this@FeedsActivity, (DataManager.userObject.searchedUsers.distinctBy { it.id }+ads).shuffled(), imgarr)
            else
                feedslist.adapter = FeedsActivityItem(this@FeedsActivity, ads.shuffled(), imgarr)
//
        }

        val serviceIntent = Intent("com.android.vending.billing.InAppBillingService.BIND")
        serviceIntent.`package` = "com.android.vending"
        bindService(serviceIntent, mServiceConn, Context.BIND_AUTO_CREATE)

        mHandlerService = Handler()
        startRepeatingTask()
        setting()

        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED && locationManager!!.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
//            var locationrequest = Location()
//            locationrequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
//            locationrequest.setInterval(1000)

//            locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            locationManager!!.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 0.0.toFloat(), locationListener)
            locationManager!!.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 0.0.toFloat(), locationListener);


        }

        val call2 = apiServices.getCategory()

        call2.enqueue(object : Callback<ResponseCategory> {
            override fun onResponse(call: Call<ResponseCategory>, response: retrofit2.Response<ResponseCategory>) {
                if(response.body()!!.result=="bad"||response.body()!!.result=="invalid")
                {


                }
                else
                {
                    categories= response.body()!!.category!!;
                    listItemsTxt1=Array(categories.size+2) { "" }
                    listItemsTxt1[0]="Select Category";
                    listItemsTxt1[1]="All";
                    for (i in categories.indices) {
                        listItemsTxt1[i+2]= categories.get(i).category!!;

                    }
                    var spinnerAdapter1: ArrayAdapter<String> = ArrayAdapter<String>(applicationContext!!, R.layout.view_drop_down_menu, listItemsTxt1)

                    spinnerCategory?.adapter = spinnerAdapter1
                    spinnerCategory!!.setSelection(1);
                }
            }

            override fun onFailure(call: Call<ResponseCategory>?, t: Throwable?) {


            }
        })

    }

    var mStatusChecker: Runnable = object : Runnable {
        override fun run() {
//            if (ContextCompat.checkSelfPermission(this@FeedsActivity, android.Manifest.permission.ACCESS_FINE_LOCATION)
//                    == PackageManager.PERMISSION_GRANTED && locationManager!!.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
//                locationManager!!.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 0.0.toFloat(), locationListener)
//
//            }
            try {
                DataManager.fetchSearchedUsers (this@FeedsActivity) {
                    var recyclerViewState = feedslist.getLayoutManager().onSaveInstanceState();
                    feedslist.layoutManager = LinearLayoutManager(this@FeedsActivity)
                    feedslist.adapter = FeedsActivityItem(this@FeedsActivity, (DataManager.userObject.searchedUsers.distinctBy { it.id }+ads).shuffled(),imgarr)
                    feedslist.getLayoutManager().onRestoreInstanceState(recyclerViewState);
                } //this function can change value of mInterval.
            } finally {
                // 100% guarantee that this always happens, even if
                // your update method throws an exception
                mHandlerService!!.postDelayed(this, mInterval.toLong())
            }
        }
    }

    fun startRepeatingTask() {
        mStatusChecker.run()
    }

    fun stopRepeatingTask() {
        mHandlerService!!.removeCallbacks(mStatusChecker)
    }

    override fun onDestroy() {
        super.onDestroy()
        topMost = false
//        stopRepeatingTask()
        if (mService != null) {
            unbindService(mServiceConn)
        }
    }

    fun menuButtonClicked(view: View) {
        if (isMenuVisible) {
            val mListView = findViewById<ListView>(R.id.menuListItems)
            mListView.setVisibility(View.INVISIBLE)
        }
        else {
            val mListView = findViewById<ListView>(R.id.menuListItems)
            mListView.setVisibility(View.VISIBLE)
        }
        isMenuVisible = !isMenuVisible
    }

    fun resetView() {
//        if (DataManager.userObject.isOnline) {
        var recyclerViewState = feedslist.getLayoutManager().onSaveInstanceState();
        feedslist.layoutManager = LinearLayoutManager(this@FeedsActivity)
       // feedslist.adapter = FeedsActivityItem(this@FeedsActivity, (DataManager.userObject.searchedUsers.distinctBy { it.id }+ads).shuffled(), imgarr)
        val preferences = this.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
        val type = preferences.getInt("type", 0)
        var array= arrayOf<String>("");
        if(type==1)
            feedslist.adapter = FeedsActivityItem(this@FeedsActivity, (DataManager.userObject.searchedUsers.distinctBy { it.id }+ads).shuffled(), imgarr)
        else
            feedslist.adapter = FeedsActivityItem(this@FeedsActivity, ads.shuffled(), imgarr)
//
        feedslist.getLayoutManager().onRestoreInstanceState(recyclerViewState);
//        }
//        else {
//            feedslist.layoutManager = LinearLayoutManager(this)
//            feedslist.adapter = FeedsActivityItem(this, mutableListOf())
//        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)

    }

    fun closeView(view: View) {
        val view  = findViewById<View>(R.id.imageFullView)
        val imageView = findViewById<ImageView>(R.id.searchImageView)
        val button = findViewById<Button>(R.id.closeButton)
        view.visibility = View.INVISIBLE
        imageView.visibility = View.INVISIBLE
        button.visibility = View.INVISIBLE
    }

    fun setting(){
        //val array = resources.getStringArray(R.array.menu)


        val preferences = this.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
        val type = preferences.getInt("type", 0)
        var array= arrayOf<String>("");
        if(type==1)
         array = arrayOf<String>("Change App Mode","My Profile","Promotion Code","About Us","Terms & Conditions","Privacy Policy","How to use","Logout")
        else
            array = arrayOf<String>("Change App Mode","About Us","Terms & Conditions","Privacy Policy","How to use","Logout")

        val  mListView = findViewById<ListView>(R.id.menuListItems)
        mListView.setVisibility(View.INVISIBLE)

        val adapter = ArrayAdapter<String>(this, R.layout.custom_textview, array)
        mListView.setAdapter(adapter)

        val context = this
        mListView.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            // 1
            if (isMenuVisible) {
                val mListView = findViewById<ListView>(R.id.menuListItems)
                mListView.setVisibility(View.INVISIBLE)
            }
            else {
                val mListView = findViewById<ListView>(R.id.menuListItems)
                mListView.setVisibility(View.VISIBLE)
            }
            isMenuVisible = !isMenuVisible
            val selectedItem = array.get(position)
            // 2
            if(type==1) {
                when (position) {
                    0 -> {
                        val profTab = Intent(this, Preference::class.java)
                        startActivity(profTab);
                    }
                    1 -> {
                        val profTab = Intent(this, ProfTab::class.java)
                        startActivity(profTab);


                    }
                    2 -> {
                        val promocode = Intent(this, PromoCode::class.java)
                        startActivity(promocode);

                    }
                    3 -> {
                        val abtus = Intent(this, Aboutus::class.java)
                        startActivity(abtus);

                    }
                    4 -> {
                        val tnc = Intent(this, TnCActivity::class.java)
                        startActivity(tnc);

                    }
                    5 -> {
                        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.golivnlive.com/privacy"))
                        startActivity(browserIntent)

                    }
                    6 -> {
                        val htu = Intent(this, HowToUse::class.java)
                        startActivity(htu);

                    }
                    7 -> {
                        val builder = AlertDialog.Builder(this)
                        // Set the alert dialog title
                        builder.setTitle("")
                        // Display a message on alert dialog
                        builder.setMessage("Do you want to logout?")
                        // Set a positive button and its click listener on alert dialog
                        builder.setPositiveButton("Ok") { dialog, which ->
                            // Do something when user press the positive button
                            DataManager.saveOnlineStatus(false)
                            FirebaseAuth.getInstance().signOut();
                            LoginManager.getInstance().logOut();
                            stopRepeatingTask()
                            val loginAct = Intent(this, MainActivity::class.java)
                            // Start the login activity.
                            startActivity(loginAct);
                            this.locationManager!!.removeUpdates(locationListener)
                            this.locationManager = null
                            DataManager.userObject = User()
                            finish()

                        }
                        builder.setNegativeButton("Cancel") { dialog, which ->

                        }
                        val dialog: AlertDialog = builder.create()
                        dialog.show()
                        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK)
                        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK)

                    }
                    else -> { // Note the block
                        print("x is neither 1 nor 2")
                    }
                }
            }
                else
                {
                    when (position) {
                        0 -> {
                            val profTab = Intent(this, Preference::class.java)
                            startActivity(profTab);
                        }
                     1 ->{
                            val abtus = Intent(this, Aboutus::class.java)
                            startActivity(abtus);

                        }
                        2 ->{
                            val tnc = Intent(this, TnCActivity::class.java)
                            startActivity(tnc);

                        }
                        3 ->{
                            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.golivnlive.com/privacy"))
                            startActivity(browserIntent)

                        }
                        4 ->{
                            val htu = Intent(this, HowToUse::class.java)
                            startActivity(htu);

                        }
                        5 -> {
                            val builder = AlertDialog.Builder(this)
                            // Set the alert dialog title
                            builder.setTitle("")
                            // Display a message on alert dialog
                            builder.setMessage("Do you want to logout?")
                            // Set a positive button and its click listener on alert dialog
                            builder.setPositiveButton("Ok") { dialog, which ->
                                // Do something when user press the positive button
                                DataManager.saveOnlineStatus(false)
                                FirebaseAuth.getInstance().signOut();
                                LoginManager.getInstance().logOut();
                                stopRepeatingTask()
                                val loginAct = Intent(this, MainActivity::class.java)
                                // Start the login activity.
                                startActivity(loginAct);
                                this.locationManager!!.removeUpdates(locationListener)
                                this.locationManager = null
                                DataManager.userObject = User()
                                finish()

                            }
                            builder.setNegativeButton("Cancel") { dialog, which ->

                            }
                            val dialog: AlertDialog = builder.create()
                            dialog.show()
                            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK)
                            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK)

                        }
                        else -> { // Note the block
                            print("x is neither 1 nor 2")
                        }
                }
            }


        }
    }

}







class FeedsActivityItem (val context: Context, val dataSource: List<FeedsData>, var imgarr: JSONArray?) : RecyclerView.Adapter<ViewHolder>() {



    // Gets the number of animals in the list
    override fun getItemCount(): Int {

        if (DataManager.userObject.isOnline) {

                return  dataSource.count()

        }
        else {
            return 0
        }
    }

    override fun getItemViewType(position: Int): Int {

        return position
    }
    // Inflates the item views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
       /* if (viewType == 0) {
            return  ViewHolder(LayoutInflater.from(context).inflate(R.layout.profsetting, parent, false))
        }else{*/
            return ViewHolder(LayoutInflater.from(context).inflate(R.layout.feedsitem, parent, false))
       // }
    }

    // Binds each animal in the ArrayList to a view
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        /*if (position == 0) {
            val listItemsTxt = arrayOf("Select Radius","1 Mile", "3 Mile", "5 Mile")
            var spinnerAdapter: ArrayAdapter<String> = ArrayAdapter<String>(context!!, R.layout.view_drop_down_menu, listItemsTxt)
            holder.spinnerRadius?.adapter = spinnerAdapter
            var spinnerAdapter1: ArrayAdapter<String> = ArrayAdapter<String>(context!!, R.layout.view_drop_down_menu, listItemsTxt1)
            holder.spinnerCategory?.adapter = spinnerAdapter1

            holder.spinnerRadius?.setOnItemSelectedListener (object : AdapterView.OnItemSelectedListener {

                override fun onNothingSelected(p0: AdapterView<*>?) {

                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }

                override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                    if(listItemsTxt[position].contains("Select"))
                        miles="0";
                    if(listItemsTxt[position].contains("1"))
                        miles="1";
                    if(listItemsTxt[position].contains("3"))
                        miles="3";
                    if(listItemsTxt[position].contains("5"))
                        miles="5";
                        }


            })

            holder.spinnerCategory?.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {

                override fun onNothingSelected(p0: AdapterView<*>?) {

                   // TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }

                override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                    if(listItemsTxt1[position].contains("Select"))
                        catid="0";
                   else
                    {
                        catid= categories.get(position-1).geolcoationBannerCategoryID!!
                    }
                    latitude="22.66215";
                    longitude="75.9035";
                    var apiServices = APIClient.client.create(ApiInterface::class.java)
                    val call1 = apiServices.loc(latitude,longitude, catid, miles);

                    call1.enqueue(object : Callback<ResponseBottom> {
                        override fun onResponse(call: Call<ResponseBottom>, response: retrofit2.Response<ResponseBottom>) {
                            if(response.body()!!.result=="bad"||response.body()!!.result=="invalid")
                            {
                                //JSONArray arr=new JSONArray();
                            }
                            else
                            {
                                //setSliderViews(response.body()!!.bannerImageUri!!, response.body()!!.banners!!)

                                val gson = Gson() // for pretty print feature

                                var jsonStr : String = "";
                                jsonStr= gson.toJson(response.body()!!.banners!!)

                                imguri=response.body()!!.bannerImageUri!!
                                imgarr =JSONArray(jsonStr)

                                ads.clear()
                                for (i in 0..(imgarr!!.length() - 1)) {
                                    val item = imgarr!!.getJSONObject(i)
                                    var image = BitmapFactory.decodeResource(context.getResources(), R.drawable.defaultprofile)//getBitmapFromURL(context, userImageUrl)

                                    var adObj = FeedsData(image,"advertisement##"+item.getString("img"),item.getString("url"),item.getString("adsType")+"##"+item.getString("distance"))

                                    ads.add(adObj)
                                    // Your code here
                                }


                                //var newlist=list+ listOf(imgarr)

                                *//*}
                                }
                            }*//*
                            }
                        }

                        override fun onFailure(call: Call<ResponseBottom>?, t: Throwable?) {

                            System.out.println("failure");
                        }
                    })


                    //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }


            })

            holder?.switchItem?.isChecked = DataManager.userObject.isOnline
            holder?.profImageView?.setImageBitmap(DataManager.userObject.userInterests!!.userImage)
            if (DataManager.myImage != null) {
                holder?.profImageView?.setImageBitmap(DataManager.myImage)
            }
            if (DataManager.userObject.isOnline) {
                holder?.descText?.text = "You are Liv'n Live!"
                //holder?.descText?.setTextColor(-0x1)
                holder?.yourMatches?.text = "Your Matches:"
            }
            else {
                holder?.descText?.text = "Flip the switch to go live!"
                //holder?.descText?.setTextColor(-0x1)
                holder?.yourMatches?.text = ""
            }
            holder?.switchItem?.setOnClickListener{
                DataManager.userObject.isOnline = !DataManager.userObject.isOnline
                if (DataManager.userObject.isOnline) {
                    holder?.descText?.text = "You are Liv'n Live!"
                  //  holder?.descText?.setTextColor(-0x1)
                    holder?.yourMatches?.text = "Your Matches:"
                }
                else {
                    holder?.descText?.text = "Flip the switch to go live!"
                    //holder?.descText?.setTextColor(-0x1)
                    holder?.yourMatches?.text = ""
                }
                DataManager.saveOnlineStatus(DataManager.userObject.isOnline)
                val activity = context as FeedsActivity
                activity.resetView()

            }

        }
        else {*/
            if(!dataSource[position].id.startsWith("advertisement##")) {
                val data = dataSource[position]
                holder.main.visibility=View.GONE;
                holder?.name?.text = data.title
                holder?.introLbl?.text = data.interest
                holder?.userImageView?.setImageBitmap(data.image)

                holder?.likeItem?.setOnClickListener {
                    DataManager.updateLikeList(data.id)
                    val dialog: AlertDialog
                    val builder = AlertDialog.Builder(context)
                    builder.setTitle("")
                    builder.setMessage("A like has been sent to the user")
                    builder.setPositiveButton("Ok") { dialog, which ->
                        dialog.dismiss()
                    }
                    dialog = builder.create()
                    dialog.show()
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK)
                }
                holder?.msgItem?.setOnClickListener {
                    val preferences = context.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)

                    val validation = preferences.getBoolean("validation", false)

                    if (!validation) {
                        if (DataManager.userObject.isPaid) {
                            val activeSubs = mService!!.getPurchases(3, "com.livnlive.go", "subs", null)
                            if (activeSubs != null) {
                                val response = activeSubs.getInt("RESPONSE_CODE")
                                if (response == BillingClient.BillingResponse.OK) {
                                    val ownedSkus = activeSubs.getStringArrayList("INAPP_PURCHASE_ITEM_LIST")
                                    val purchaseDataList = activeSubs.getStringArrayList("INAPP_PURCHASE_DATA_LIST")
                                    val signatureList = activeSubs.getStringArrayList("INAPP_DATA_SIGNATURE_LIST")
                                    val continuationToken = activeSubs.getString("INAPP_CONTINUATION_TOKEN")
                                    for (i in 0 until purchaseDataList.size) {
                                        val purchaseData = purchaseDataList.get(i)
                                        val signature = signatureList.get(i)
                                        val sku = ownedSkus.get(i)
                                    }
                                    if (purchaseDataList.size == 0) {
                                        val profTab = Intent(context, BuyMsgActivity::class.java)
                                        profTab.putExtra("toUserId", data.id);
                                        profTab.putExtra("toUserName", data.title)
                                        context.startActivity(profTab);
                                    } else {
                                        val profTab = Intent(context, MessageActivity::class.java)
                                        profTab.putExtra("toUserId", data.id);
                                        profTab.putExtra("toUserName", data.title)
                                        context.startActivity(profTab);
                                    }
                                } else {
                                    val profTab = Intent(context, BuyMsgActivity::class.java)
                                    profTab.putExtra("toUserId", data.id);
                                    profTab.putExtra("toUserName", data.title)
                                    context.startActivity(profTab);
                                }
                            } else {
                                val profTab = Intent(context, BuyMsgActivity::class.java)
                                profTab.putExtra("toUserId", data.id);
                                profTab.putExtra("toUserName", data.title)
                                context.startActivity(profTab);
                            }
                        } else {
                            val profTab = Intent(context, BuyMsgActivity::class.java)
                            profTab.putExtra("toUserId", data.id);
                            profTab.putExtra("toUserName", data.title)
                            context.startActivity(profTab);
                        }
                    } else {
                        val profTab = Intent(context, MessageActivity::class.java)
                        profTab.putExtra("toUserId", data.id);
                        profTab.putExtra("toUserName", data.title)
                        context.startActivity(profTab);
                    }

                }
                holder?.blockUser?.setOnClickListener {
                    val builder = AlertDialog.Builder(context)
                    builder.setTitle("")
                    builder.setMessage("Do you want to block the user? User will be permanently blocked.")
                    builder.setPositiveButton("Ok") { dialog, which ->
                        DataManager.updateBlockList(data.id)
//                    DataManager.updateTempBlockList(data.id)
                        DataManager.userObject.searchedUsers.remove(data)
                        val activity = context as FeedsActivity
                        activity.resetView()
                    }
                    builder.setNegativeButton("No") { dialog, which -> }
                    val dialog: AlertDialog = builder.create()
                    dialog.show()
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK)
                    dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK)
                }
                holder?.disLikeItem?.setOnClickListener {
                    val builder = AlertDialog.Builder(context)
                    builder.setTitle("")
                    builder.setMessage("Do you want to block the user? User will be blocked for 24 hrs.")
                    builder.setPositiveButton("Ok") { dialog, which ->
                        //                    DataManager.updateBlockList(data.id)
                        DataManager.updateTempBlockList(data.id)
                        DataManager.userObject.searchedUsers.remove(data)
                        val activity = context as FeedsActivity
                        activity.resetView()
                    }
                    builder.setNegativeButton("No") { dialog, which -> }
                    val dialog: AlertDialog = builder.create()
                    dialog.show()
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK)
                    dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK)

                }
                holder?.zoomImage?.setOnClickListener {
                    val activity = context as FeedsActivity
                    val view = activity.findViewById<View>(R.id.imageFullView)
                    val imageView = activity.findViewById<ImageView>(R.id.searchImageView)
                    val button = activity.findViewById<Button>(R.id.closeButton)
                    view.visibility = View.VISIBLE
                    imageView.visibility = View.VISIBLE
                    button.visibility = View.VISIBLE
                    imageView.setImageBitmap(data.image)
                }
            }
            else
            {
                val data = dataSource[position]
                holder?.name.visibility = View.GONE;
                holder?.introLbl.visibility = View.GONE;
                holder?.userImageView.visibility=View.GONE;

                holder?.likeItem?.visibility=View.GONE;
                holder?.msgItem.visibility=View.GONE;
                holder?.blockUser.visibility=View.GONE;
                holder?.disLikeItem.visibility=View.GONE;
                holder?.zoomImage.visibility=View.GONE;
               holder.close.setOnClickListener {


                   holder.main.visibility=View.GONE;
                   holder.img.visibility=View.GONE;
                   holder.feedItem.visibility=View.GONE
                               }
                if(data.interest!!.startsWith("geo"))
                {
                    val parts = data.interest!!.split("##")
                    holder.adlayout.visibility=View.VISIBLE

                    holder.adlabel.visibility=View.VISIBLE
                    holder?.adlabel?.text = parts[1]
                }
                else{
                    holder.adlayout.visibility=View.GONE

                    holder.adlabel.visibility=View.GONE
                }
                holder.main.visibility=View.VISIBLE;
                holder.img.visibility=View.VISIBLE;
                var name=data.id.removePrefix("advertisement##")
                var url=imguri+name
                Glide.with(context).load(url).into(holder.img)

                holder.img.setOnClickListener(View.OnClickListener {
                    val openURL = Intent(android.content.Intent.ACTION_VIEW)

                        openURL.data = Uri.parse(data.title as String?)
                        context.startActivity(openURL)

                })


            }
        }
   // }
}


 class ViewHolder (view: View) : RecyclerView.ViewHolder(view) {
    // Holds the TextView that will add each animal to
     val name = view.namelbl
     val introLbl = view.introLbl
     val descText = view.descText
     val profImageView = view.profImageView
     val userImageView = view.image_view
     val switchItem = view.goLive
     val likeItem = view.like
     val msgItem = view.message
     val disLikeItem = view.dislike
     val blockUser = view.blockUser
     val zoomImage = view.zoomImage
     val yourMatches = view.yourmatches
     val main = view.main
     val img = view.img
     val spinnerRadius=view.spinnerRadius
     val spinnerCategory = view.spinnerCategory
     var adlayout = view.adlayout
     var adlabel=view.adlabel
     var close=view.close
     var feedItem = view.feedItem
}
