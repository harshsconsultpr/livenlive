package com.app.tf.livnlive

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.Paint
import android.net.Uri
import android.graphics.Paint.UNDERLINE_TEXT_FLAG
import android.widget.Button


class Aboutus : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_aboutus)
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        val button = findViewById<Button>(R.id.button5)
        button.setPaintFlags(button.getPaintFlags() or Paint.UNDERLINE_TEXT_FLAG)
    }
    fun doneAction(view: View){
        finish()
    }
    fun openURL(view: View){
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.golivnlive.com"))
        startActivity(browserIntent)
    }
}
