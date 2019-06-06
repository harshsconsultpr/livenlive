package com.app.tf.livnlive

import android.content.pm.ActivityInfo
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.text.method.ScrollingMovementMethod
import android.view.View
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_how_to_use.*

class HowToUse : AppCompatActivity() {
/*

    val howToUseText = """
This is the only “Real Time” meeting app. ‘LIV’N LIVE”. We are encouraging people to live their lives and not hide behind their phone or computer. Go meet people in person! Say “HI”, know that your match is real.

First: Down load the App from your Apple or Android App Store

Second: Sign up with your email or Facebook

Third: Fill out the information about yourself

Forth: Fill out the information about the person you are looking to meet

Fifth: Turn on the App and let it keep running in the background (do not swipe)

Sixth: Go anywhere and we will find your matches

The app will notify your phone with your matches profiles that are in the area (1 linear mile) as well as notify your match’s phone with your profile. The app will show you their picture, name, and description (if they chose to allow these 2 items to be shared) plus it will show you who your common friends are on FACEBOOK. You have an option to click the “Thumbs down” next to your match to delete this match from your phone as well as theirs.

You can also click the Green “Thumbs Up” button to let the match know that it is ok for them to come and meet you.

You can click the Message button to send a message (${'$'}4.99 Upgrade to do so).

If this is a match that you NEVER want to connect with, then you can click the “X” button and that match will never appear on your phone and you will never appear on their phone.

You can click on your match’s photo to enlarge the picture of that match.

If you are going out and you do not want to be seen by any potential matches or you do not want to be disturbed by any matches, simply click the button on the main page to “OFF”. This will ensure that you are invisible to any matches, but your matches will be invisible to you as well.
"""
*/

    val howToUseText = """
The “LIV’N LIVE” connecting app is also a “REAL TIME” app that allows you to see what is going on that day at nearby businesses.  We work with bars, restaurants, stores and other businesses to show users what they have to offer.  When you go out and want to know what is going on around you, just open up the Liv’n Live app and see what there is to do right around you.  You can choose to select a 1, 3 or 5 mile radius from where you are currently at.  This will help you save time going to each business to find out what they have to offer or look at each businesses web site to see what they have to offer.
"""

    val howToUseText2 = """
This is the only “Real Time” connecting app.  ‘LIV’N LIVE”.  We are encouraging people to live their lives and not hide behind their phone or computer.  Go meet people in person!  Say “HI”, know that your match is real.

First:		Download the App from your Apple or Android App Store
Second:		Sign up with your email or Facebook
Third:		Fill out the information about yourself
Forth:		Fill out the information about the person you are looking to meet
Fifth:		Turn on the App and let it keep running in the background (do not swipe)
Sixth:		Go anywhere and we will find your matches

The app will notify your phone with your match’s profiles that are in the area (with in 1, 3, or 5 miles) as well as notify your match’s phone with your profile.  The app will show you their picture, name, and description (if they chose to allow these 2 items to be shared) as well as your information with them, plus it will show you that you have common friends are on FACEBOOK.

You have an option to click the “Thumbs down” next to your match to delete this match from your phone as well as theirs for 24 hours.

You can also click the Green “Thumbs Up” button to send your match a notification that says, “likes you”.

You can click the Message button to send your match a message (only a ${'$'}4.99 Upgrade).

If this is a match that you NEVER want to connect with, then you can click the “X” button and that match will never appear on your phone and you will never appear on their phone.

You can click on your match’s photo to enlarge the picture of that match.

If you are going out and you do not want to be seen by any potential matches or you do not want to be disturbed by any matches, simply click the button on the main page to “OFF”.  This will ensure that you are invisible to any matches, but your matches will be invisible to you as well.

"""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_how_to_use)

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        htuLabel.text = howToUseText
        htuLabel.movementMethod = ScrollingMovementMethod()

        htuLabelSecond.text = howToUseText2
        htuLabelSecond.movementMethod = ScrollingMovementMethod()
        htuLabelplace.text = Html.fromHtml("<u>Looking for a place to go</u>")
        htuLabelDate.text = Html.fromHtml("<u>Looking for a Date</u>")


    }

    fun closeHTU(view: View) {
        finish()
    }
}
