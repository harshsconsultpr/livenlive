package com.app.tf.livnlive

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.view.View
import android.widget.TextView
import android.R.raw
import android.content.pm.ActivityInfo
import android.text.method.ScrollingMovementMethod
import java.io.ByteArrayOutputStream
import java.io.IOException


class TnCActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tn_c)
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        val view = findViewById<TextView>(R.id.tncLabel)
        val txt = readTxt()
        view.setText(Html.fromHtml(txt));
        view.movementMethod = ScrollingMovementMethod()

    }

    fun closeTnc(view: View) {
        finish()
    }

    private fun readTxt(): String {
        val inputStream = resources.openRawResource(R.raw.tbcnew)
        val byteArrayOutputStream = ByteArrayOutputStream()
        var i: Int
        try {
            i = inputStream.read()
            while (i != -1) {
                byteArrayOutputStream.write(i)
                i = inputStream.read()
            }
            inputStream.close()
        } catch (e: IOException) {
            // TODO Auto-generated catch block
            e.printStackTrace()
        }

        return byteArrayOutputStream.toString()
    }
}