package com.app.tf.livnlive

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.login.LoginResult
import com.facebook.FacebookException
import com.facebook.FacebookSdk
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task


import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_main.*
import com.app.tf.livnlive.R.string.LoginButton
import android.support.annotation.NonNull
import com.google.firebase.internal.FirebaseAppHelper.getToken
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.AuthCredential
import com.facebook.AccessToken
import android.app.Activity
import android.content.Context
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.Color
import android.location.LocationManager
import android.support.constraint.ConstraintLayout
import android.support.constraint.ConstraintSet
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.support.v4.app.ActivityCompat
import android.widget.*
import com.facebook.login.LoginManager
import org.jetbrains.anko.textColor
import com.facebook.GraphRequest
import dmax.dialog.SpotsDialog

class MainActivity : AppCompatActivity() {
    private var mEmailField: EditText? = null
    private var mPasswordField: EditText? = null
    private var mPlaces: ImageView? = null
    private var mDating: ImageView? = null
    var mCallbackManager: CallbackManager? = null //= CallbackManager()
    var locationPermissionGranted = false
    var Semail = ""
    var type=0;
    var activityView: SpotsDialog? = null
    var accepted:Boolean = false


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)
        if (mCallbackManager != null) {
            mCallbackManager!!.onActivityResult(requestCode, resultCode, data)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)



        setContentView(R.layout.activity_main)

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        adjustScreenSize()

        mEmailField = findViewById<EditText>(R.id.email)

        mPasswordField = findViewById<EditText>(R.id.password)

        mPlaces = findViewById<ImageView>(R.id.places)

        mDating = findViewById<ImageView>(R.id.dating)

        mCallbackManager = CallbackManager.Factory.create()

        activityView = SpotsDialog(this, R.style.Custom);

        val tncswitch = findViewById<Switch>(R.id.TncSwitchMain)
        tncswitch.setOnClickListener{
            if (accepted == true ){
                accepted = false
            }else{
                accepted = true

            }
        }

        val loginButton = findViewById<com.facebook.login.widget.LoginButton>(R.id.fblogin_button)
        loginButton.setReadPermissions("email", "public_profile")


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
        loginButton.registerCallback(mCallbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(loginResult: LoginResult) {
                val token = loginResult.accessToken
                val request = GraphRequest.newMeRequest(token) { `object`, response ->
                    try {
                        if (`object`.has("email")) {
                            Semail = `object`.getString("email")
                        }


                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    handleFacebookAccessToken(loginResult.getAccessToken())
                }
                val parameters = Bundle()
                parameters.putString("fields", "id,email")
                request.parameters = parameters
                request.executeAsync()
            }

            override fun onCancel() {
            }

            override fun onError(error: FacebookException) {
            }
        })

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
            if ((locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                                android.Manifest.permission.ACCESS_FINE_LOCATION)) {
                } else {
                    ActivityCompat.requestPermissions(this,
                            arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                            1)
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
        else {
            val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

            if ((locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))) {
                locationPermissionGranted = true
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

        val mCLayout = findViewById(R.id.mainActivity) as ConstraintLayout

        Utility.setupUI(mCLayout,this)
    }

    fun openTncMain(view: View) {
        val tnc = Intent(this, TnCActivity::class.java)
        startActivity(tnc);
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            1 -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    locationPermissionGranted = true
                } else {
                }
                return
            }
            else -> { }
        }
    }

    private fun handleFacebookAccessToken(token: AccessToken) {
        if (locationPermissionGranted) {
            val credential = FacebookAuthProvider.getCredential(token.token)
//            FacebookAuthProvider.
            activityView!!.show();
            mAuth.signInWithCredential(credential)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            val user = mAuth.currentUser
                            if (user != null) {
                                DataManager.fetchUserData(this) { task ->
                                    Log.d("", "task" + task)
                                    activityView!!.hide();
                                    if (task) {
                                        val search = Intent(this, FeedsActivity::class.java)
                                        startActivity(search)
                                    } else {
                                        if(type==1) {
                                            DataManager.isFirstSetup = true
                                            val search = Intent(this, ProfileActivity::class.java)
                                            startActivity(search)
                                        }
                                        else
                                        {
                                            val search = Intent(this, FeedsActivity::class.java)
                                            startActivity(search)
                                        }
                                    }
                                    finish();
                                }
                            }
                        } else {
                            activityView!!.hide();
                            LoginManager.getInstance().logOut();
                            val exp = task.exception!!.message
                            if (exp == "An account already exists with the same email address but different sign-in credentials. Sign in using a provider associated with this email address.") {

                                val builder = AlertDialog.Builder(this)
                                builder.setTitle("")
                                builder.setMessage("Account already exists with this email. Please provide the password to link the accounts.")
                                val editText = EditText(this)
                                val lp =  LinearLayout.LayoutParams(
                                        LinearLayout.LayoutParams.MATCH_PARENT,
                                        LinearLayout.LayoutParams.MATCH_PARENT);
                                editText.layoutParams = lp
                                builder.setView(editText)
                                builder.setPositiveButton("Ok") { dialog, which ->
                                    val password = editText.getText().toString();
                                    if (password == "" || Semail == "") {
                                        val builder = AlertDialog.Builder(this)
                                        builder.setTitle("")
                                        builder.setMessage("Invalid Password")
                                        builder.setPositiveButton("Ok") { dialog, which -> }
                                        val dialog: AlertDialog = builder.create()
                                        dialog.show()
                                        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK)
                                    }
                                    else {
                                        activityView!!.show();
                                        mAuth.signInWithEmailAndPassword(Semail, password)
                                                .addOnCompleteListener(this) { task ->
                                                    if (task.isSuccessful) {
                                                        mAuth.currentUser!!.linkWithCredential(credential)
                                                                .addOnCompleteListener(this) { task ->
                                                                    if (task.isSuccessful) {
                                                                        DataManager.fetchUserData(this) { task ->
                                                                            Log.d("", "task" + task)
                                                                            activityView!!.hide();
                                                                            if (task) {
                                                                                val search = Intent(this, FeedsActivity::class.java)
                                                                                startActivity(search)
                                                                            } else {
                                                                                if(type==1) {
                                                                                    DataManager.isFirstSetup = true
                                                                                    val search = Intent(this, ProfileActivity::class.java)
                                                                                    startActivity(search)
                                                                                }
                                                                                else
                                                                                {
                                                                                    val search = Intent(this, FeedsActivity::class.java)
                                                                                    startActivity(search)
                                                                                }
                                                                            }
                                                                            finish();
                                                                        }
                                                                    } else {
                                                                        activityView!!.hide();
                                                                        val builder = AlertDialog.Builder(this)
                                                                        builder.setTitle("")
                                                                        builder.setMessage("The email address or password is incorrect. Please try again!")
                                                                        val dialog1 = builder.setPositiveButton("Ok") { dialog, which -> }
                                                                        val dialog: AlertDialog = builder.create()
                                                                        dialog.show()
                                                                        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK)
                                                                    }

                                                                    // ...
                                                                }

                                                    } else {
                                                        activityView!!.hide();
                                                        val builder = AlertDialog.Builder(this)
                                                        builder.setTitle("")
                                                        builder.setMessage("The email address or password is incorrect. Please try again!")
                                                        val dialog1 = builder.setPositiveButton("Ok") { dialog, which -> }
                                                        val dialog: AlertDialog = builder.create()
                                                        dialog.show()
                                                        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK)
                                                    }
                                                }
                                    }
                                }
                                val dialog: AlertDialog = builder.create()
                                dialog.show()
                                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK)

                            }
                            else {
                                activityView!!.hide();
//                            task.exception
                                val builder = AlertDialog.Builder(this)
                                builder.setTitle("")
                                builder.setMessage("Unable to verify the credentials. Please try again later")
                                builder.setPositiveButton("Ok") { dialog, which -> }
                                val dialog: AlertDialog = builder.create()
                                dialog.show()
                                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK)
                            }
                        }
                    }
        }
        else {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("")
            builder.setMessage("Please enable location services before log in")
            builder.setPositiveButton("Ok") { dialog, which -> }
            val dialog: AlertDialog = builder.create()
            dialog.show()
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK)
        }
    }


    override fun onStart() {
        super.onStart()


    }


    fun loginUser(view: View) {
        if (!validateForm()) {
            return
        }
        val email = mEmailField!!.text.toString()
        val password = mPasswordField!!.text.toString()
        if (locationPermissionGranted) {
            activityView!!.show();
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            DataManager.fetchUserData(this) { task ->
                                Log.d("", "task" + task)
                                activityView!!.hide();
                                if (task) {
                                    val search = Intent(this, FeedsActivity::class.java)
                                    startActivity(search)
                                } else {
                                    if(type==1) {
                                        DataManager.isFirstSetup = true
                                        val search = Intent(this, ProfileActivity::class.java)
                                        startActivity(search)
                                    }
                                    else
                                    {
                                        val search = Intent(this, FeedsActivity::class.java)
                                        startActivity(search)
                                    }
                                }
                                val preferences = this.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)

                                    val editor = preferences.edit()
                                    editor.putInt("type", type)
                                    editor.commit()
                                finish();
                            }

                        } else {
                            activityView!!.hide();
                            val builder = AlertDialog.Builder(this)
                            builder.setTitle("")
                            builder.setMessage("The email address or password is incorrect. Please try again!")
                            val dialog1 = builder.setPositiveButton("Ok") { dialog, which -> }
                            val dialog: AlertDialog = builder.create()
                            dialog.show()
                            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK)
                        }
                    }
        }
        else {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("")
            builder.setMessage("Please enable location services before log in")
            builder.setPositiveButton("Ok") { dialog, which -> }
            val dialog: AlertDialog = builder.create()
            dialog.show()
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK)
        }
    }

    fun showSignupScn(view: View) {

        val signup = Intent(this, SignupActivity::class.java)

        // Start the new activity.
        startActivity(signup)
        finish(); // Call once you redirect to another activity

    }

    private fun signOut() {
        mAuth!!.signOut()
    }

    fun forgotUIPWD(view: View) {
        val fuip = Intent(this, ForgotPasswordActivity::class.java)
        startActivity(fuip)
        finish();
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


    fun hideSoftKeyboard(activity: Activity) {
        val inputMethodManager = activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(activity.currentFocus!!.windowToken, 0)
    }



    fun adjustScreenSize(){
        val screenSize = resources.configuration.screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK
        if ((screenSize == Configuration.SCREENLAYOUT_SIZE_SMALL ) || (screenSize == Configuration.SCREENLAYOUT_SIZE_NORMAL )){

            var img = findViewById<ImageView>(R.id.imageView)
            val lt = img.layoutParams
            val layoutParams =  ConstraintLayout.LayoutParams(lt.width*3/4, lt.height*3/4);
            img.setLayoutParams(layoutParams);
            val main = findViewById(R.id.mainActivity) as ConstraintLayout
            val set = ConstraintSet()
            set.clone(main)
            set.connect(img.getId(), ConstraintSet.RIGHT, ConstraintSet.PARENT_ID, ConstraintSet.RIGHT, 0);
            set.connect(img.getId(), ConstraintSet.LEFT, ConstraintSet.PARENT_ID, ConstraintSet.LEFT, 0);
            set.connect(img.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, 20);
            set.setHorizontalBias(img.getId(), 0.5F);
            set.applyTo(main)
        }


    }




}