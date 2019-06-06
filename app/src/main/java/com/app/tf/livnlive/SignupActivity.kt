package com.app.tf.livnlive

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.EditText
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import android.app.Activity
import android.R.attr.y
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.graphics.Color
import android.support.constraint.ConstraintLayout
import android.support.constraint.ConstraintSet
import android.support.v7.app.AlertDialog
import android.widget.ImageView
import android.widget.Switch
import dmax.dialog.SpotsDialog


class SignupActivity : AppCompatActivity() {
    private var mEmailField: EditText? = null
    private var mPasswordField: EditText? = null
    private var mConfirmPasswordField: EditText? = null

    private var mAuth: FirebaseAuth? = null
    var activityView: SpotsDialog? = null
    var accepted:Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        adjustScreenSize()
        mAuth = FirebaseAuth.getInstance()
        mEmailField = findViewById<EditText>(R.id.email)
        mPasswordField = findViewById<EditText>(R.id.password)
        mConfirmPasswordField = findViewById<EditText>(R.id.ConfirmPwd)
        activityView = SpotsDialog(this, R.style.Custom);

        val mCLayout = findViewById(R.id.signup) as ConstraintLayout
        val tncswitch = findViewById<Switch>(R.id.TncSwitch)
        tncswitch.setOnClickListener{
            if (accepted == true ){
                accepted = false
            }else{
                accepted = true

            }
        }
        Utility.setupUI(mCLayout,this)

    }
    fun createNewUser(view: View) {
        if (!validateForm()) {
            return
        }
        val email = mEmailField!!.text.toString()
        val password = mPasswordField!!.text.toString()
        activityView!!.show();
        mAuth!!.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    activityView!!.hide();
                    if (task.isSuccessful) {
                        // Sign in: success
                        // update UI for current User
                        val user = mAuth!!.currentUser
                        FirebaseAuth.getInstance().signOut();
                        //updateUI(user)

//                        val prof = Intent(this, ProfileActivity::class.java)
//                        // Start the profile activity.
//                        startActivity(prof);


                        val builder = AlertDialog.Builder(this)
                        // Set the alert dialog title
                        builder.setTitle("")
                        // Display a message on alert dialog
                        builder.setMessage("Your account is created. Please login to continue")
                        // Set a positive button and its click listener on alert dialog
                        builder.setPositiveButton("Ok"){dialog, which ->

                            // Do something when user press the positive button
                            val login = Intent(this, MainActivity::class.java)

                            // Start the new activity.
                            startActivity(login)
                            finish()
                        }
                        val dialog: AlertDialog = builder.create()
                        // Display the alert dialog on app interface
                        dialog.show()
                        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK)


                    } else {
                        // Sign in: fail
                        //Log.e(TAG, "signIn: Fail!", task.exception)
                        //updateUI(null)

                        val builder = AlertDialog.Builder(this)
                        // Set the alert dialog title
                        builder.setTitle("")
                        // Display a message on alert dialog
                        //builder.setMessage("The inputs are incorrect. Please try again!")
                        builder.setMessage(task.exception!!.message)
                        // Set a positive button and its click listener on alert dialog
                        builder.setPositiveButton("Ok"){dialog, which ->
                            // Do something when user press the positive button
                        }
                        val dialog: AlertDialog = builder.create()
                        // Display the alert dialog on app interface
                        dialog.show()
                        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK)
                    }
                }
    }

    fun openTnc(view: View) {
        val tnc = Intent(this, TnCActivity::class.java)
        startActivity(tnc);
    }

    private fun validateForm(): Boolean {
        var valid = true

        val email = mEmailField?.getText().toString()
        if (TextUtils.isEmpty(email)) {
            mEmailField?.setError("Required.")
            valid = false
        } else {
            mEmailField?.setError(null)
        }

        val password = mPasswordField?.getText().toString()
        if (TextUtils.isEmpty(password)) {
            mPasswordField?.setError("Required.")
            valid = false
        } else {
            mPasswordField?.setError(null)
        }
        val confirm = mConfirmPasswordField?.getText().toString()
        if (TextUtils.isEmpty(password)) {
            mConfirmPasswordField?.setError("Required.")
            valid = false
        } else {
            mConfirmPasswordField?.setError(null)
        }
        if (password != confirm){
            valid = false
            mConfirmPasswordField?.setError("Did not match.")
        }
        if (accepted == false){
            valid = false


            val builder = AlertDialog.Builder(this)
            // Set the alert dialog title
            builder.setTitle("")
            // Display a message on alert dialog
            builder.setMessage("Please accept terms and condition.")
            // Set a positive button and its click listener on alert dialog
            builder.setPositiveButton("Ok"){dialog, which ->
            }

            val dialog: AlertDialog = builder.create()
            // Display the alert dialog on app interface
            dialog.show()
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK)

        }

        return valid
    }

    fun goToLoginPage(view: View) {
        val login = Intent(this, MainActivity::class.java)

        // Start the new activity.
        startActivity(login)
        finish()

    }

    fun adjustScreenSize(){
        val screenSize = resources.configuration.screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK
        if ((screenSize == Configuration.SCREENLAYOUT_SIZE_SMALL ) || (screenSize == Configuration.SCREENLAYOUT_SIZE_NORMAL )){

            var img = findViewById<ImageView>(R.id.signupimageView)
            val lt = img.layoutParams
            val layoutParams =  ConstraintLayout.LayoutParams(lt.width*3/4, lt.height*3/4);
            img.setLayoutParams(layoutParams);
            val signup = findViewById(R.id.signup) as ConstraintLayout
            val set = ConstraintSet()
            set.clone(signup)
            set.connect(img.getId(), ConstraintSet.RIGHT, ConstraintSet.PARENT_ID, ConstraintSet.RIGHT, 0);
            set.connect(img.getId(), ConstraintSet.LEFT, ConstraintSet.PARENT_ID, ConstraintSet.LEFT, 0);
            set.connect(img.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, 20);
            set.setHorizontalBias(img.getId(), 0.5F);
            set.applyTo(signup)
        }


    }


}
