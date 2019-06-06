package com.app.tf.livnlive

import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.EditText
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth

import android.view.animation.Animation
import android.widget.ProgressBar
import android.widget.Toast
import mekotlinapps.dnyaneshwar.`in`.restdemo.rest.APIClient
import mekotlinapps.dnyaneshwar.`in`.restdemo.rest.ApiInterface
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.google.firebase.internal.FirebaseAppHelper.getUid
import com.google.firebase.auth.FirebaseUser




class PromoCode : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_promo_code)
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        val mCLayout = findViewById(R.id.fuip) as ConstraintLayout

        Utility.setupUI(mCLayout,this)
    }
    fun loginPage(view: View){

        finish()
    }
    fun sendLinkAction(view: View) {

        val textField = findViewById<EditText>(R.id.email)

        val auth = FirebaseAuth.getInstance()
        val emailAddress = textField.getText().toString()
        val progressBar = findViewById<ProgressBar>(R.id.progressBar)


        if (emailAddress != ""){

            val currentFirebaseUser = FirebaseAuth.getInstance().currentUser
            //Toast.makeText(this, "" + currentFirebaseUser!!.uid, Toast.LENGTH_SHORT).show()
            progressBar.visibility = View.VISIBLE

            var apiServices = APIClient.client.create(ApiInterface::class.java)

            val call = apiServices.checkPromo(currentFirebaseUser!!.uid,emailAddress)

            call.enqueue(object : Callback<mekotlinapps.dnyaneshwar.`in`.restdemo.model.Response> {
                override fun onResponse(call: Call<mekotlinapps.dnyaneshwar.`in`.restdemo.model.Response>, response: Response<mekotlinapps.dnyaneshwar.`in`.restdemo.model.Response>) {


                    progressBar.visibility = View.GONE
                    if(response.body()!!.result=="bad"||response.body()!!.result=="invalid")
                    {
                        Toast.makeText(applicationContext, response.body()!!.msg,Toast.LENGTH_SHORT).show()

                    }
                    else
                    {
                        Toast.makeText(applicationContext, response.body()!!.msg,Toast.LENGTH_SHORT).show()

                        finish()
                    }
                }

                override fun onFailure(call: Call<mekotlinapps.dnyaneshwar.`in`.restdemo.model.Response>?, t: Throwable?) {
                    progressBar.visibility = View.GONE
                    Toast.makeText(applicationContext,"Some issue please try again later.",Toast.LENGTH_SHORT).show()


                }
            })
         }
        else{
            val shake = AnimationUtils.loadAnimation(this, R.anim.shake)

            textField.startAnimation(shake)
            Toast.makeText(applicationContext,"Please enter valid promo code value.",Toast.LENGTH_SHORT).show()
//
        }

    }
}
