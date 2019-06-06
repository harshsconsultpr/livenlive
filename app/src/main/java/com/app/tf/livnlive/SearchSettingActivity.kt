package com.app.tf.livnlive

import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.v7.app.AlertDialog
import android.view.View
import android.widget.*
import kotlinx.android.synthetic.main.activity_search_setting.*
import java.util.*
import com.app.tf.livnlive.R.id.listView
import android.util.SparseBooleanArray
import com.app.tf.livnlive.R.id.transition_position
import android.widget.CheckedTextView
import android.widget.AdapterView
import android.widget.AdapterView.OnItemClickListener
import com.app.tf.livnlive.R.id.listView
import android.widget.TextView

class SearchSettingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_setting)

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);


        val minAge = 18
        val maxAge = 100
        this.supportActionBar?.setTitle("Who are you looking for?");

        val ageStartSpin = findViewById<TextView>(R.id.startAge)
        ageStartSpin.setOnClickListener {
            val age = ArrayList<String>()
            for (i in minAge..maxAge) {
                age.add(Integer.toString(i))
            }
            val array = arrayOfNulls<String>(age.size)
            age.toArray(array)
            setting(array ,ageStartSpin)
        }

        val ageEndSpin = findViewById<TextView>(R.id.endAge)
        ageEndSpin.setOnClickListener {
            val age = ArrayList<String>()
            //minAge = Integer.parseInt(ageStartSpin.getText().toString());

            for (i in minAge..maxAge) {
                age.add(Integer.toString(i))
            }
            val array = arrayOfNulls<String>(age.size)
            age.toArray(array)
            setting(array ,ageEndSpin)
        }
        //HEIGHT

        val htStartSpin = findViewById<TextView>(R.id.htStart)
        htStartSpin.setOnClickListener {
            val ht = ArrayList<String>()
            for (i in 4..7) {
                for (j in 0..12) {
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

            setting(array,htStartSpin)
        }

        val htEndSpin = findViewById<TextView>(R.id.htEnd)
        htEndSpin.setOnClickListener {
            val ht = ArrayList<String>()
            for (i in 4..7) {
                for (j in 0..12) {
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

            setting(array,htEndSpin)
        }




        val bodySpinner = findViewById<TextView>(R.id.body)
        bodySpinner.setOnClickListener {
            val bodylist = resources.getStringArray(R.array.bodyType)
            setting(bodylist,bodySpinner)
        }
        val ethnicity = findViewById<TextView>(R.id.ethnicitytext)
        ethnicity.setOnClickListener {
            val list = resources.getStringArray(R.array.ethnicity)
            setting(list,ethnicity)
        }
        val gender = findViewById<TextView>(R.id.gender)
        gender.setOnClickListener {
            val list = resources.getStringArray(R.array.gender)
            setting(list,gender)
        }

        val saveBtn = findViewById(R.id.saveBtn) as Button
        saveBtn.setOnClickListener {



            if (bodySpinner.text.isNullOrEmpty()|| ethnicity.text.isNullOrEmpty() || gender.text.isNullOrEmpty() || ageStartSpin.text.isNullOrEmpty() || ageEndSpin.text.isNullOrEmpty() || htStartSpin.text.isNullOrEmpty() || htEndSpin.text.isNullOrEmpty()) {
                //throw error
                val builder = AlertDialog.Builder(this)
                // Set the alert dialog title
                builder.setTitle("")
                // Display a message on alert dialog
                builder.setMessage("Please fill all the details!")
                // Set a positive button and its click listener on alert dialog
                builder.setPositiveButton("Ok"){dialog, which ->
                    // Do something when user press the positive button
                }
                val dialog: AlertDialog = builder.create()
                // Display the alert dialog on app interface
                dialog.show()
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK)
            }
            else{

                // your code to perform when the user clicks on the button
                val bodyType = bodySpinner.text
                val eth = ethnicity.text
                val gen = gender.text

                val ageStart = Integer.parseInt(ageStartSpin.getText().toString());
                val ageEnd =  Integer.parseInt(ageEndSpin.getText().toString());
                val htStart = htStartSpin.text
                val htEnd = htEndSpin.text

                val htTo = getHt(htEnd)
                val htFrom = getHt(htStart)
                val ageFrom = ageStart
                val ageTo = ageEnd
                var success = true
                if (htTo < htFrom) {
                    success = false
                    val builder = AlertDialog.Builder(this)
                    // Set the alert dialog title
                    builder.setTitle("")
                    // Display a message on alert dialog
                    builder.setMessage("From height cannot be less that to height")
                    // Set a positive button and its click listener on alert dialog
                    builder.setPositiveButton("Ok"){dialog, which ->
                        // Do something when user press the positive button
                    }
                    val dialog: AlertDialog = builder.create()
                    // Display the alert dialog on app interface
                    dialog.show()
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK)
                }
                else if (ageTo < ageFrom){
                    success = false
                    val builder = AlertDialog.Builder(this)
                    // Set the alert dialog title
                    builder.setTitle("")
                    // Display a message on alert dialog
                    builder.setMessage("From age cannot be less that to age")
                    // Set a positive button and its click listener on alert dialog
                    builder.setPositiveButton("Ok"){dialog, which ->
                        // Do something when user press the positive button
                    }
                    val dialog: AlertDialog = builder.create()
                    // Display the alert dialog on app interface
                    dialog.show()
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK)
                }
                if (success) {
                    DataManager.tempUserObject.userSearchPref =
                            UserSearchPref(fromAge = ageStart,
                                    toAge = ageEnd, fromHeight = getHt(htStart),
                                    toHeight = getHt(htEnd),
                                    ethnicity = eth.toString(),
                                    bodyType = bodyType.toString(),
                                    gender = gen.toString())

                    DataManager.saveUserData()
                    if (DataManager.isFirstSetup) {
                        DataManager.isFirstSetup = false
                        val info = Intent(this, FeedsActivity::class.java)
                        // Start the Feeds activity.
                        info.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                        startActivity(info);
                    } else {
                        val info = Intent(this, ProfTab::class.java)
                        // Start the Feeds activity.
                        info.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                        startActivity(info);
                    }
                }
            }


        }
        var userInfo = DataManager.tempUserObject.userSearchPref
        if (userInfo == null) {
            userInfo = DataManager.userObject.userSearchPref
        }
        if (userInfo != null) {
            ageStartSpin.text = userInfo.fromAge.toString()
            ageEndSpin.text = userInfo.toAge.toString()
            val height = userInfo.fromHeight.toString().toDouble()
            val feetValue = (height / 12).toInt()
            val inchValue = (height - (feetValue * 12)).toInt()
            val ht = "${feetValue}'${inchValue}\""
            htStartSpin.text = ht
            val height1 = userInfo.toHeight.toString().toDouble()
            val feetValue1 = (height1 / 12).toInt()
            val inchValue1 = (height1 - (feetValue1 * 12)).toInt()
            val ht1 = "${feetValue1}'${inchValue1}\""
            htEndSpin.text = ht1
            ethnicitytext.text = userInfo.ethnicity
            bodySpinner.text = userInfo.bodyType
            gender.text = userInfo.gender
        }

    }
    fun getHt(htString: CharSequence) : Double{
        var heightString = htString.toString()
        heightString = heightString.replace("'",".",true)
        heightString = heightString.replace("\"","",true)
        val heightSplit = heightString.split(".")
        val feetValue = heightSplit.first()
        val inchValue = heightSplit.last()
        val height = (feetValue.toDouble() * 12) + inchValue.toDouble()

        return height;

    }


    fun setting(array: Array<String?>, textView: TextView){
    //Remove
        val saveBtn = findViewById(R.id.saveBtn) as Button
        saveBtn.setVisibility(View.GONE);


    //Add
        val  listLabel = findViewById<TextView>(R.id.listLabel)
        listLabel.text = textView.hint

        val  mListView = findViewById<ListView>(R.id.searchlistView)
        val adapter = ArrayAdapter(this,  R.layout.spinnertextlayout, array)
        mListView.setAdapter(adapter)






        //Done+Heading row
        val ll = findViewById<LinearLayout>(R.id.LinearLayout04)


        if ((textView ==  findViewById<TextView>(R.id.startAge) )   ||
                (textView ==  findViewById<TextView>(R.id.endAge)) ||
                (textView ==  findViewById<TextView>(R.id.htStart)) ||
                (textView ==  findViewById<TextView>(R.id.htEnd))){
            mListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE)

        }else{
            mListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        }



        var j = 0
        for (item in array) {
            if (textView.text.contains(item.toString()) ) {
                mListView.setItemChecked(j, true)
            }
            j++
        }


        mListView.setVisibility(View.VISIBLE)
        ll.setVisibility(View.VISIBLE)


        mListView.onItemClickListener = OnItemClickListener { parent, view, position, id ->
            //val v = mListView.getChildAt(position)
            val v = mListView.getChildAt(position - mListView.getFirstVisiblePosition());

            if (v == null) {
                return@OnItemClickListener
            }
            val ctv = v?.findViewById(R.id.spinnerTextView) as CheckedTextView


            if ((textView ==  findViewById<TextView>(R.id.startAge) )   ||
                    (textView ==  findViewById<TextView>(R.id.endAge)) ||
                    (textView ==  findViewById<TextView>(R.id.htStart)) ||
                    (textView ==  findViewById<TextView>(R.id.htEnd)))
            {
                textView.setText(ctv.text)

            }else{
                mListView.getCheckedItemPositions()
                val checked = mListView.getCheckedItemPositions()
                val selectedItems = ArrayList<String>()
                for (i in 0 until checked.size()) {
                    // Item position in adapter
                    val position = checked.keyAt(i)
                    if (checked.valueAt(i))
                        array.get(position)?.let { selectedItems.add(it) }
                }
                val outputStrArr = arrayOfNulls<String>(selectedItems.size)
                for (i in 0 until selectedItems.size) {
                    outputStrArr[i] = selectedItems[i]
                }
                val builder = StringBuilder()
                for (s in outputStrArr) {
                    builder.append(s)
                    if ( s != outputStrArr[outputStrArr.size - 1]){
                        builder.append(",")
                    }
                }
                val str = builder.toString()
                textView.setText(str)


            }
           /* val  mListView = findViewById<ListView>(R.id.searchlistView)
            if (mListView.visibility == View.VISIBLE){

                mListView.setVisibility(View.INVISIBLE)
                val ll = findViewById<LinearLayout>(R.id.LinearLayout04)
                ll.setVisibility(View.INVISIBLE)
                val saveBtn = findViewById(R.id.saveBtn) as Button
                saveBtn.setVisibility(View.VISIBLE)
            }*/

        }

    }
    //Done button click
    fun dismissSpinner(view: View){
        val  mListView = findViewById<ListView>(R.id.searchlistView)
        if (mListView.visibility == View.VISIBLE){

            mListView.setVisibility(View.INVISIBLE)
            val ll = findViewById<LinearLayout>(R.id.LinearLayout04)
            ll.setVisibility(View.INVISIBLE)
            val saveBtn = findViewById(R.id.saveBtn) as Button
            saveBtn.setVisibility(View.VISIBLE)
        }


    }
}
