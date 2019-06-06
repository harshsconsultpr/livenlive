package com.app.tf.livnlive

import android.app.ActionBar
import android.app.Activity
import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import android.widget.ArrayAdapter
import android.widget.AdapterView
import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.support.constraint.ConstraintLayout
import android.support.v7.app.AlertDialog
import java.text.DateFormatSymbols
import java.util.*
import android.widget.TextView
import kotlin.collections.ArrayList
import android.view.Gravity
import android.view.ViewGroup
import com.app.tf.livnlive.R.id.listView
import android.widget.CheckedTextView
import android.support.annotation.NonNull
import com.app.tf.livnlive.R.id.listView
import com.app.tf.livnlive.R.id.listView
import com.facebook.internal.Utility.arrayList
import com.app.tf.livnlive.R.id.listView
import com.facebook.internal.Utility.arrayList
import com.app.tf.livnlive.R.id.listView
import com.facebook.internal.Utility.arrayList
import com.app.tf.livnlive.R.id.listView
import com.facebook.internal.Utility.arrayList
















class ProfileActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_profile)
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        val nameLbl = findViewById<TextView>(R.id.NameText)
        nameLbl.setOnClickListener{
            dismissSelector()
        }
        this.supportActionBar?.setTitle("Your Profile");



        val mCLayout = findViewById(R.id.coordinator_layout) as ConstraintLayout
        Utility.setupUI(mCLayout,this)

        val bodySpinner = findViewById<TextView>(R.id.body)
        bodySpinner.setOnClickListener {
            val bodylist = resources.getStringArray(R.array.bodyType)
             setting(bodylist,bodySpinner)
        }

        val mnthSpinner = findViewById<TextView>(R.id.month)
        mnthSpinner.setOnClickListener {
            val symbols = DateFormatSymbols()
            val monthNames = symbols.getMonths()
            setting(monthNames,mnthSpinner)
        }
        val yrSpinner = findViewById<TextView>(R.id.year)
        yrSpinner.setOnClickListener {
            val years = ArrayList<String>()
            val thisYear = Calendar.getInstance().get(Calendar.YEAR)
            for (i in 1920..thisYear) {
                years.add(Integer.toString(i))
            }
            val array = arrayOfNulls<String>(years.size)
            years.toArray(array)
            array.reverse()
            setting(array ,yrSpinner)
        }
        val ethSpinner = findViewById<TextView>(R.id.ethnicity)
        ethSpinner.setOnClickListener {
            val bodylist = resources.getStringArray(R.array.ethnicity)
            setting(bodylist,ethSpinner)
        }
        val genderSpinner = findViewById<TextView>(R.id.gender)
        genderSpinner.setOnClickListener {
            val bodylist = resources.getStringArray(R.array.gender)
            setting(bodylist,genderSpinner)
        }


        val htSpinner = findViewById<TextView>(R.id.HtText)
        htSpinner.setOnClickListener {
            val ht = ArrayList<String>()
            for (i in 4..7) {
                for (j in 1..12) {
                    if (j == 12) {
                        ht.add( Integer.toString(i + 1) + "'0\"")
                    }
                    else {
                        ht.add( Integer.toString(i) + "'" + Integer.toString(j) + "\"")
                    }
                }
            }

            val array = arrayOfNulls<String>(ht.size)
            ht.toArray(array)

            setting(array,htSpinner)
        }


        // get reference to button
        val continueBtn = findViewById(R.id.CONTINUE) as Button

        // set on-click listener
        continueBtn.setOnClickListener {
            // your code to perform when the user clicks on the button
            val nameText = nameLbl.text
            val bodyType = bodySpinner.text
            val mnth = mnthSpinner.text
            val yr = yrSpinner.text
            val eth = ethSpinner.text
            val gen = genderSpinner.text
            val ht = htSpinner.text
            if (nameText.isNullOrEmpty()|| bodyType.isNullOrEmpty() || mnth.isNullOrEmpty() || yr.isNullOrEmpty() || eth.isNullOrEmpty() || gen.isNullOrEmpty() || ht.isNullOrEmpty()) {
            //throw error

                //throw error
                val builder = AlertDialog.Builder(this)
                builder.setTitle("")
                builder.setMessage("Please fill all the details!")
                builder.setPositiveButton("Ok"){dialog, which ->
                }
                val dialog: AlertDialog = builder.create()
                dialog.show()
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK)

            }
            else{
                var heightString = ht.toString()
                heightString = heightString.replace("'",".",true)
                heightString = heightString.replace("\"","",true)
                val heightSplit = heightString.split(".")
                val feetValue = heightSplit.first()
                val inchValue = heightSplit.last()
                val height = (feetValue.toInt() * 12) + inchValue.toInt()


                DataManager.tempUserObject.userProfile = UserProfile(name = nameText.toString(),bodyType = bodyType.toString(),month = mnth.toString(),year = yr.toString(),ethnicity = eth.toString() ,height = height.toDouble(), gender = gen.toString())

                val info = Intent(this, PersonalSettingActivity::class.java)
                // Start the profile activity.
                startActivity(info);
            }

        }

        var userInfo = DataManager.tempUserObject.userProfile
        if (userInfo == null) {
            userInfo = DataManager.userObject.userProfile
        }
        if (userInfo != null) {
            val name = findViewById<TextView>(R.id.NameText)
            name.text = userInfo.name
            bodySpinner.text = userInfo.bodyType
            mnthSpinner.text = userInfo.month
            yrSpinner.text = userInfo.year
            ethSpinner.text = userInfo.ethnicity
            genderSpinner.text = userInfo.gender
            val height = userInfo.height.toString().toDouble()
            val feetValue = (height / 12).toInt()
            val inchValue = (height - (feetValue * 12)).toInt()
            val ht = "${feetValue}'${inchValue}\""
            htSpinner.text = ht
        }
    }


    fun setting(array: Array<String?>, textView: TextView){
        //Remove
        val continueBtn = findViewById(R.id.CONTINUE) as Button
        continueBtn.setVisibility(View.GONE);

        //Add
        val  mListView = findViewById<ListView>(R.id.listView)
        mListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE)
        val adapter = ArrayAdapter<String>(this, R.layout.spinnertextlayout, array)
        mListView.setAdapter(adapter)




        val  listLbl = findViewById<TextView>(R.id.listLbl)
        listLbl.text = textView.hint


        var j = 0
        for (item in array) {
            if (textView.text.contains(item.toString()) ) {
                mListView.setItemChecked(j, true)
            }
            j++
        }




        val ll = findViewById<LinearLayout>(R.id.LinearLayout02)
//        ll.setVisibility(View.VISIBLE)
        mListView.setVisibility(View.VISIBLE)






        mListView.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            // 1
            val selectedItem = array.get(position)
            // 2
            textView.setText(selectedItem)
            dismissSelector()
        }
    }
    fun dismissSpinner(view: View){
        dismissSelector()

    }

    fun dismissSelector(){
        val  mListView = findViewById<ListView>(R.id.listView)
        if (mListView.visibility == View.VISIBLE){

            mListView.setVisibility(View.INVISIBLE)
            val ll = findViewById<LinearLayout>(R.id.LinearLayout02)
            ll.setVisibility(View.INVISIBLE)
            val continueBtn = findViewById(R.id.CONTINUE) as Button
            continueBtn.setVisibility(View.VISIBLE)
        }
    }

}
