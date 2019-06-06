package com.app.tf.livnlive

import android.app.AlertDialog
import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.graphics.Paint
import android.net.Uri
import android.graphics.Paint.UNDERLINE_TEXT_FLAG
import android.widget.Button
import android.widget.ImageView


class Preference : AppCompatActivity() {


    var type=0;

    private var mPlaces: ImageView? = null
    private var mDating: ImageView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_preference)
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        mPlaces = findViewById<ImageView>(R.id.places)

        mDating = findViewById<ImageView>(R.id.dating)

        val preferences = this.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
        val type1 = preferences.getInt("type", 0)
        if(type1==0)
        {
            mPlaces!!.setImageResource(R.drawable.radselected)
            mDating!!.setImageResource(R.drawable.radunselected)
        }
        else
        {
            mDating!!.setImageResource(R.drawable.radselected)
            mPlaces!!.setImageResource(R.drawable.radunselected)
        }

        mPlaces!!.setOnClickListener{


            type=0
            mPlaces!!.setImageResource(R.drawable.radselected)
            mDating!!.setImageResource(R.drawable.radunselected)


        }
        mDating!!.setOnClickListener{


            type=1
            mDating!!.setImageResource(R.drawable.radselected)
            mPlaces!!.setImageResource(R.drawable.radunselected)

        }

//        button.setPaintFlags(button.getPaintFlags() or Paint.UNDERLINE_TEXT_FLAG)
    }
    fun doneAction(view: View){


        val builder = AlertDialog.Builder(this)
        // Set the alert dialog title
        builder.setTitle("")
        // Display a message on alert dialog
        builder.setMessage("Do you want to change the app mode?")
        // Set a positive button and its click listener on alert dialog
        builder.setPositiveButton("Yes") { dialog, which ->
            // Do something when user press the positive button
            val preferences = this.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)

            val editor = preferences.edit()
            editor.putInt("type", type)
            editor.commit()
            finish()

        }
        builder.setNegativeButton("No") { dialog, which ->

        }
        val dialog: AlertDialog = builder.create()
        dialog.show()
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK)
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK)

    }

}
