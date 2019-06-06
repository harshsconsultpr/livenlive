package com.app.tf.livnlive

import android.animation.ObjectAnimator
import android.content.Intent
import android.content.pm.ActivityInfo
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.view.View
import android.widget.EditText
import com.google.firebase.auth.FirebaseAuth

import com.google.android.gms.tasks.OnCompleteListener
import android.view.animation.AnimationUtils
import android.view.animation.Animation
import android.support.v7.app.AlertDialog



class ForgotPasswordActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_forgot_password)
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        val mCLayout = findViewById(R.id.fuip) as ConstraintLayout

        Utility.setupUI(mCLayout,this)
    }
    fun loginPage(view: View){
        val main = Intent(this, MainActivity::class.java)

        // Start the main activity.
        startActivity(main)
    }
    fun sendLinkAction(view:View) {
        val textField = findViewById<EditText>(R.id.email)

        val auth = FirebaseAuth.getInstance()
        val emailAddress = textField.getText().toString()



        if (emailAddress != ""){
            auth.sendPasswordResetEmail(emailAddress.toString())
                    .addOnCompleteListener(object : OnCompleteListener<Void> {
                        override fun onComplete(task: com.google.android.gms.tasks.Task<Void>) {
                            if (task.isSuccessful()) {
                                val builder = AlertDialog.Builder(this@ForgotPasswordActivity)

                                // Set the alert dialog title
                                builder.setTitle("")

                                // Display a message on alert dialog
                                builder.setMessage("An email has been sent with the reset information")

                                // Set a positive button and its click listener on alert dialog
                                builder.setPositiveButton("Ok"){dialog, which ->
                                    // Do something when user press the positive button

                                    // Change the app background color
                                    finish()
                                }


                                // Display a negative button on alert dialog
//                                builder.setNegativeButton("No"){dialog,which ->
//                                    Toast.makeText(applicationContext,"You are not agree.",Toast.LENGTH_SHORT).show()
//                                }
//
//
//                                // Display a neutral button on alert dialog
//                                builder.setNeutralButton("Cancel"){_,_ ->
//                                    Toast.makeText(applicationContext,"You cancelled the dialog.",Toast.LENGTH_SHORT).show()
//                                }

                                // Finally, make the alert dialog using builder
                                val dialog: AlertDialog = builder.create()

                                // Display the alert dialog on app interface
                                dialog.show()

                            }
                        }
                    })
        }
        else{
            val shake = AnimationUtils.loadAnimation(this, R.anim.shake)

            textField.startAnimation(shake)
        }

    }
}
