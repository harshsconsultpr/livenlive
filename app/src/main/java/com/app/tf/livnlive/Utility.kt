package com.app.tf.livnlive

import android.app.ActionBar
import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import android.graphics.*
import android.support.constraint.ConstraintLayout
import android.support.constraint.ConstraintSet
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView


object Utility {


    // Create the instance
    private var instance: Utility? = null

    fun hideSoftKeyboard(activity: Activity) {
        val inputMethodManager = activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        if (activity.currentFocus != null){
            inputMethodManager.hideSoftInputFromWindow(activity.currentFocus!!.windowToken, 0)
        }
    }

    fun setupUI(view: View, activity: Activity) {
    val activity = activity
        //Set up touch listener for non-text box views to hide keyboard.
        if (view !is EditText) {

            view.setOnTouchListener { v, event ->
                hideSoftKeyboard(activity)
                false
            }
        }


        //If a layout container, iterate over children and seed recursion.
        if (view is ViewGroup) {

            for (i in 0 until view.childCount) {

                val innerView = view.getChildAt(i)

                setupUI(innerView, activity)
            }
        }
    }





}

