package com.app.tf.livnlive

import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.support.design.widget.TabLayout
import android.support.v7.app.AppCompatActivity

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup

import kotlinx.android.synthetic.main.activity_prof_tab.*
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.ListView
import com.app.tf.livnlive.R.id.listView
import android.widget.TextView
import kotlinx.android.synthetic.main.preview_textfield.*
import com.app.tf.livnlive.R.id.listView




class ProfTab : AppCompatActivity() {

    /**
     * The [android.support.v4.view.PagerAdapter] that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * [android.support.v4.app.FragmentStatePagerAdapter].
     */
    private var mSectionsPagerAdapter: SectionsPagerAdapter? = null

    override fun onResume() {
        super.onResume()
//        val img = findViewById<ImageView>(R.id.imageView3)

//        img.setImageBitmap(R.drawable.logo)
        mSectionsPagerAdapter = SectionsPagerAdapter(supportFragmentManager)

        // Set up the ViewPager with the sections adapter.
        container.adapter = mSectionsPagerAdapter
        DataManager.tempUserObject.userProfile = null
        DataManager.tempUserObject.userSearchPref = null
        DataManager.tempUserObject.userInterests = null

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_prof_tab)

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        nameTextView.text = DataManager.userObject.userProfile!!.name
//        val img = findViewById<ImageView>(R.id.imageView3)
//
//        img.setImageBitmap(DataManager.userObject.userInterests!!.userImage)
        //setSupportActionBar(toolbar)
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = SectionsPagerAdapter(supportFragmentManager)

        // Set up the ViewPager with the sections adapter.
        container.adapter = mSectionsPagerAdapter

        container.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabs))
        tabs.addOnTabSelectedListener(TabLayout.ViewPagerOnTabSelectedListener(container))
        tabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab) {

            }

            override fun onTabSelected(tab: TabLayout.Tab) {
                val tabId = tab.position
                if (tabId == 2) {
                    finish()
                }

//                this.getTabContent(tabId)
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {

            }
        })


    }
    fun editProfClicked(view: View){
        val profTab = Intent(this, ProfileActivity::class.java)
        // Start the login activity.
        startActivity(profTab);
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_prof_tab, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId

        if (id == R.id.action_settings) {
            return true
        }

        return super.onOptionsItemSelected(item)
    }


    /**
     * A [FragmentPagerAdapter] that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    inner class SectionsPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

        override fun getItem(position: Int): Fragment {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1)
        }

        override fun getCount(): Int {
            // Show 3 total pages.
            return 3
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */

    class PlaceholderFragment : Fragment() {
        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                                  savedInstanceState: Bundle?): View? {
            if (arguments?.getInt(ARG_SECTION_NUMBER) == 3) {
                return null
            }
            else if (arguments?.getInt(ARG_SECTION_NUMBER) == 1) {
                val rootView = inflater.inflate(R.layout.fragment_prof_tab, container, false)
                val list = resources.getStringArray(R.array.previewHead)
                val objects =   ArrayList<CustomObject>()
                for(index in list.indices){
                    var item = list[index]
                    when (index) {
                        0 -> {
                            var userData = DataManager.userObject.userProfile!!
                            var myData =  userData.month + " " + userData.year
                            val height = userData.height.toString().toDouble()
                            val feetValue = (height / 12).toInt()
                            val inchValue = (height - (feetValue * 12)).toInt()
                            val ht = "${feetValue}'${inchValue}\""
                            myData = myData + "\n" +  ht + " " + userData.bodyType
                            myData = myData + "\n" +  userData.ethnicity
                            myData = myData + "\n" +  userData.gender + "\n"

                            val ob1 =  CustomObject(prop1 = item, prop2 = myData)
                            objects.add(ob1)
                        }
                        1 -> {
                            val displayItems = DataManager.userObject.userInterests!!.displayItems
                            val ob2 =  CustomObject(prop1 = item, prop2 = displayItems)
                            objects.add(ob2)
                        }
                        2 -> {
                            val displayItems = DataManager.userObject.userInterests!!.interests
                            val ob3 =  CustomObject(prop1 = item, prop2 = displayItems)
                            objects.add(ob3)
                        }
                        else -> { // Note the block
                            print("x is neither 1 nor 2")
                        }
                    }
                    list[index] = item
                }
                val lv = rootView.findViewById(R.id.previewList) as ListView

                val customAdapter = CustomAdapter(this.context!!, objects)
                lv.adapter = customAdapter
                return rootView
            }
            else if (arguments?.getInt(ARG_SECTION_NUMBER) == 2) {
                val rootView = inflater.inflate(R.layout.fragment_prof_tab, container, false)
                val list = resources.getStringArray(R.array.previewHead1)
                var userData = DataManager.userObject.userSearchPref!!
                val objects1 =   ArrayList<CustomObject>()
                for(index in list.indices){
                    var item = list[index]
                    var displayText = ""
                    when (index) {
                        0 -> {
                            displayText = userData.fromAge.toString() + " - " + userData.toAge.toString()
                            val ob2 =  CustomObject(prop1 = item, prop2 = displayText)
                            objects1.add(ob2)
                        }
                        1 -> {
                            val height = userData.fromHeight.toString().toDouble()
                            val feetValue = (height / 12).toInt()
                            val inchValue = (height - (feetValue * 12)).toInt()
                            val ht = "${feetValue}'${inchValue}\""
                            val height1 = userData.toHeight.toString().toDouble()
                            val feetValue1 = (height1 / 12).toInt()
                            val inchValue1 = (height1 - (feetValue1 * 12)).toInt()
                            val ht1 = "${feetValue1}'${inchValue1}\""
                            displayText = ht + " - " + ht1
                            val ob2 =  CustomObject(prop1 = item, prop2 = displayText)
                            objects1.add(ob2)
                        }
                        2 -> {
                            displayText = userData.gender.replace(",", ", ",true)
                            val ob3 =  CustomObject(prop1 = item, prop2 = displayText)
                            objects1.add(ob3)
                        }
                        3 -> {
                            displayText = userData.bodyType.replace(",", ", ",true)
                            val ob4 =  CustomObject(prop1 = item, prop2 = displayText)
                            objects1.add(ob4)
                        }
                        4 -> {
                            displayText = userData.ethnicity.replace(",", ", ",true)
                            val ob5 =  CustomObject(prop1 = item, prop2 = displayText)
                            objects1.add(ob5)
                        }

                        else -> { // Note the block
                            print("x is neither 1 nor 2")
                        }
                    }
                    item = "\n" + item + displayText + "\n"
                    list[index] = item
                }
                val lv = rootView.findViewById(R.id.previewList) as ListView

                val customAdapter = CustomAdapter(this.context!!, objects1)
                lv.adapter = customAdapter




                return rootView
            }
            else {
                return null
            }
        }

        override fun onActivityCreated(savedInstanceState: Bundle?) {
            super.onActivityCreated(savedInstanceState)


        }

        companion object {
            /**
             * The fragment argument representing the section number for this
             * fragment.
             */
            private val ARG_SECTION_NUMBER = "section_number"

            /**
             * Returns a new instance of this fragment for the given section
             * number.
             */
            fun newInstance(sectionNumber: Int): PlaceholderFragment {
                val fragment = PlaceholderFragment()
                val args = Bundle()
                args.putInt(ARG_SECTION_NUMBER, sectionNumber)
                fragment.arguments = args
                return fragment
            }
        }

    }




}

