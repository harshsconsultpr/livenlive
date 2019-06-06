package com.app.tf.livnlive

import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.LocationManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.util.Log
import android.widget.ImageView
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.FirebaseInstanceIdService
import android.R.id.edit
import android.content.SharedPreferences
import android.content.pm.PackageInfo
import android.os.Handler
import android.support.v4.app.ActivityCompat
import com.facebook.login.LoginManager
import com.google.android.gms.flags.impl.SharedPreferencesFactory.getSharedPreferences
import com.google.firebase.auth.FirebaseAuth


import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;
import android.util.Base64
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

class LaunchActivity : AppCompatActivity() {

    private val LOCATION_REQUEST_CODE = 101
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Fabric.with(this, Crashlytics())
        setContentView(R.layout.activity_launch)
        val gifView = findViewById<GifImageView>(R.id.splash)
        gifView.setGifImageResource(R.drawable.forever)
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        /*try {
            val info = packageManager.getPackageInfo(
                    "com.livnlive.go",
                    PackageManager.GET_SIGNATURES)
            for (signature in info.signatures) {
                val md = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                Log.e("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT))
            }
        } catch (e: PackageManager.NameNotFoundException) {

        } catch (e: NoSuchAlgorithmException) {

        }*/
        Handler().postDelayed({
            val preferences = this.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)

            val newUser = preferences.getBoolean("firstTime", true)

            if (newUser) {
                val editor = preferences.edit()
                editor.putBoolean("firstTime", false)
                editor.commit()
                DataManager.saveOnlineStatus(false)
                FirebaseAuth.getInstance().signOut();
                LoginManager.getInstance().logOut();
                val login = Intent(this, MainActivity::class.java)
                // Start the login activity.
                startActivity(login);
                finish()
            }

            else {
                if (mAuth.currentUser == null) {
                    val login = Intent(this, MainActivity::class.java)
                    // Start the login activity.
                    startActivity(login);
                    finish()

                } else {
                    val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
                    if ((locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))) {
                        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                                != PackageManager.PERMISSION_GRANTED ) {
                            /*val builder = AlertDialog.Builder(this)
                            // Set the alert dialog title
                            builder.setTitle("")
                            // Display a message on alert dialog
                            builder.setMessage("Please enable location services to use the app")
                            val dialog: AlertDialog = builder.create()
                            // Display the alert dialog on app interface
                            dialog.show()*/
                            ActivityCompat.requestPermissions(this,
                                    arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                                    LOCATION_REQUEST_CODE)
                        }
                        else {
                            DataManager.fetchUserData(this) { task ->
                                Log.d("", "task" + task)
                                DataManager.saveUserDeviceToken()
                                if (task) {
                                    val search = Intent(this, FeedsActivity::class.java)
                                    search.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                                    startActivity(search)
                                } else {
                                    DataManager.isFirstSetup = true
                                    val search = Intent(this, ProfileActivity::class.java)
                                    startActivity(search)
                                }
                                finish()
                            }
                        }
                        }
                    else
                    {
                        val builder = AlertDialog.Builder(this)
                        builder.setTitle("")
                        builder.setMessage("Please enable location services before log in")
                        val dialog: AlertDialog
                        builder.setPositiveButton("Ok") { dialog, which -> dialog.dismiss(); finish()}
                        dialog = builder.create()
                        dialog.show()
                        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK)
                    }

                }
            }
        }, 5000)

    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            LOCATION_REQUEST_CODE -> {

                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions(this,
                            arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                            LOCATION_REQUEST_CODE)
                } else {
                    DataManager.fetchUserData(this) { task ->
                        Log.d("", "task" + task)
                        DataManager.saveUserDeviceToken()
                        if (task) {
                            val search = Intent(this, FeedsActivity::class.java)
                            search.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                            startActivity(search)
                        } else {
                            DataManager.isFirstSetup = true
                            val search = Intent(this, ProfileActivity::class.java)
                            startActivity(search)
                        }
                        finish()
                    }
                }
            }
        }
    }
  }


class MyApplication: Application() {

//    fun onResume() {
//        super.onResume()
//
//        val myApp = this.getApplication() as MyApplication
//        if (myApp.wasInBackground) {
//            //Do specific came-here-from-background code
//        }
//
//        myApp.stopActivityTransitionTimer()
//    }
}