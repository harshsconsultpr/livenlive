package com.app.tf.livnlive

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.media.Image
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import android.graphics.drawable.BitmapDrawable
import android.support.constraint.ConstraintLayout
import android.support.v7.app.AlertDialog
import com.facebook.login.LoginManager
import com.google.firebase.auth.FirebaseAuth
import android.provider.MediaStore
import android.R.attr.data
import android.R.attr.bitmap
import android.support.v4.app.NotificationCompat.getExtras
import android.R.attr.data
import android.content.Context
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.graphics.Matrix
import android.net.Uri
import android.os.Build
import android.support.v4.app.NotificationCompat.getExtras
import android.util.Log
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.image
import android.support.annotation.NonNull
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import com.facebook.*
import com.facebook.login.LoginResult
import com.google.android.gms.tasks.OnFailureListener
import com.google.firebase.storage.StorageMetadata
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FacebookAuthCredential
import com.google.firebase.auth.FacebookAuthProvider
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.sql.Connection
import java.util.*
import java.util.concurrent.CountDownLatch
import android.Manifest
import android.media.ExifInterface
import android.os.Environment
import android.support.v4.content.FileProvider
import com.theartofdev.edmodo.cropper.CropImage
import java.io.*
import java.text.SimpleDateFormat


class PersonalSettingActivity : AppCompatActivity() {

    var imageUrl = ""
    var hasPermission = false
    var image: Bitmap? = null
    var tempImage: Bitmap? = null
    private var callbackManager: CallbackManager? = null
    var outputURI: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                        12)

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            hasPermission = true
            // Permission has already been granted
        }
        setContentView(R.layout.activity_personal_setting)

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        val auth =  "com.app.tf.livnlive.fileprovider";
        val output = createFilePath()  //File(new File(getFilesDir(), PHOTOS), FILENAME);
        outputURI = FileProvider.getUriForFile(this, auth, output)

        this.supportActionBar?.setTitle("Your Profile");
        callbackManager = CallbackManager.Factory.create()
        val mCLayout = findViewById(R.id.personalSettingActivity) as ConstraintLayout
        Utility.setupUI(mCLayout,this)
        val uploadBtn = findViewById(R.id.uploadBtn) as Button
        uploadBtn.setOnClickListener {

            val list = resources.getStringArray(R.array.uploadItems)
            setting1(list,uploadBtn)

        }
        val display =   findViewById<TextView>(R.id.diplayText)
        display.setOnClickListener {
            val list = resources.getStringArray(R.array.dipalyItems)
            setting(list,display)

        }

        val interests = findViewById<TextView>(R.id.interest)

        val continueBtn = findViewById(R.id.next) as Button

        // set on-click listener
        continueBtn.setOnClickListener {
            // your code to perform when the user clicks on the button

            val diplayTxt = display.text

            if (interests.text.isNullOrEmpty()|| diplayTxt.isNullOrEmpty() ) {
                //throw error
                val builder = AlertDialog.Builder(this)
                // Set the alert dialog title
                builder.setTitle("")
                // Display a message on alert dialog
                builder.setMessage("Please fill all the details!")
                // Set a positive button and its click listener on alert dialog
                builder.setPositiveButton("Ok"){dialog, which ->
                    // Do something when user press the positive button
                }
                val dialog: AlertDialog = builder.create()
                // Display the alert dialog on app interface
                dialog.show()
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK)

            }
            else{
                if (image == null) {
                    imageUrl == ""
                    image = BitmapFactory.decodeResource(getResources(), R.drawable.defaultprofile);
                }
                else {
                    DataManager.myImage = image
                }

                DataManager.tempUserObject.userInterests = UserInterests(interests = interests.text.toString(),displayItems = diplayTxt.toString(), userImage = image!! ,userImageUrl = imageUrl)

                val info = Intent(this, SearchSettingActivity::class.java)
                // Start the profile activity.
                startActivity(info);
            }

        }
        var userInfo = DataManager.tempUserObject.userInterests
        if (userInfo == null) {
            userInfo = DataManager.userObject.userInterests
        }
        if (userInfo != null) {
            interests.text = userInfo.interests
            display.text = userInfo.displayItems
            val pic = findViewById(R.id.pic) as ImageView
            pic.setImageBitmap(userInfo.userImage)
            image = userInfo.userImage
            imageUrl = userInfo.userImageUrl

        }






    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            12 -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    hasPermission = true
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return
            }

        // Add other 'when' lines to check for other
        // permissions this app might request.
            else -> {
                // Ignore all other requests.
            }
        }
    }

//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
//        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
//            val extras = data.extras
//            val imageBitmap = extras!!.get("data") as Bitmap
//            mImageView.setImageBitmap(imageBitmap)
//        }
//    }

    override fun onActivityResult(requestCode: Int, responseCode: Int,
                                  data: Intent?) {
        super.onActivityResult(requestCode, responseCode, data)



        if (responseCode == 0) {
            return
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {

            val result = CropImage.getActivityResult(data)
            if (responseCode == RESULT_OK) {
                val resultUri = result.uri
                val pic = findViewById(R.id.pic) as ImageView
                pic.setImageURI(resultUri)
                image = (pic.getDrawable() as BitmapDrawable).bitmap
                //        if (mAuth != null) {
//            mAuth.currentUser.
            val image1 = pic.image
            val bitmap = (image1!! as BitmapDrawable).getBitmap()
            val storage = FirebaseStorage.getInstance()
            val storageRef = storage.getReference()
            val name = "images/" + mAuth.currentUser!!.uid + ".jpg"
            val imagesRef = storageRef.child(name)
            val stream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, stream)
            val image2 = stream.toByteArray()
            imagesRef.putBytes(image2).addOnSuccessListener {documentReference ->

                imageUrl = documentReference.downloadUrl.toString()
                Log.d("", "DocumentSnapshot added with ID: ")
                if (DataManager.userObject.userInterests != null) {
                    DataManager.userObject.userInterests!!.userImage = image!!
                    DataManager.userObject.userInterests!!.userImageUrl = imageUrl
                    DataManager.myImage = image!!
                }

                DataManager.saveImageUrl(imageUrl)
            }
                    .addOnFailureListener { e ->
                    }

//        }




            } else if (responseCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
//      Exception error = result.getError();
            }
            return
        }

        val pic = findViewById(R.id.pic) as ImageView

        when (requestCode) {
            0 -> if (responseCode == Activity.RESULT_OK) {
                if (data == null) {
                    return
                }
                val selectedImage = data!!.data

                pic.setImageURI(selectedImage)

                image = (pic.getDrawable() as BitmapDrawable).bitmap
            }
            1 -> if (responseCode == Activity.RESULT_OK) {
                if (data == null) {
                    return
                }
                val selectedImage = data!!.data
                CropImage.activity(selectedImage).setAspectRatio(1, 1)
                        .start(this);
            }
            2 -> {
                val photo = MediaStore.Images.Media.getBitmap(
                        contentResolver, outputURI!!)
//                val photo = data!!.getExtras().get("data") as Bitmap
//                val tempUri = getImageUri(applicationContext, photo)
                CropImage.activity(outputURI).setAspectRatio(1, 1)
                        .start(this);
            }
            64206 -> {
                callbackManager!!.onActivityResult(requestCode, responseCode, data)
                return
            }
        }
    }

    var mCurrentPhotoPath: String = ""

    fun createFilePath(): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName = "JPEG_" + timeStamp + "_"
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val image = File.createTempFile(imageFileName, ".jpg", storageDir)
        return image
    }

//    String mCurrentPhotoPath;
//
//    private File createImageFile() throws IOException {
//        // Create an image file name
//        String imageFileName = "JPEG_" + timeStamp + "_";
//        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
//        File image = File.createTempFile(
//                imageFileName,  /* prefix */
//        ".jpg",         /* suffix */
//        storageDir      /* directory */
//        );
//
//        // Save a file: path for use with ACTION_VIEW intents
//        mCurrentPhotoPath = image.getAbsolutePath();
//        return image;
//    }

    fun getImageUri(inContext: Context, inImage: Bitmap): Uri {
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(inContext.contentResolver, inImage, "Title", null)
        return Uri.parse(path)
    }


    @Throws(IOException::class)
    fun handleSamplingAndRotationBitmap(context: Context, selectedImage: Uri): Bitmap {
        val MAX_HEIGHT = 1024
        val MAX_WIDTH = 1024

        // First decode with inJustDecodeBounds=true to check dimensions
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        var imageStream = context.contentResolver.openInputStream(selectedImage)
        BitmapFactory.decodeStream(imageStream, null, options)
        imageStream!!.close()

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, MAX_WIDTH, MAX_HEIGHT)

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false
        imageStream = context.contentResolver.openInputStream(selectedImage)
        var img = BitmapFactory.decodeStream(imageStream, null, options)

        img = rotateImageIfRequired(context, img, selectedImage)
        return img
    }

    private fun calculateInSampleSize(options: BitmapFactory.Options,
                                      reqWidth: Int, reqHeight: Int): Int {
        // Raw height and width of image
        val height = options.outHeight
        val width = options.outWidth
        var inSampleSize = 1

        if (height > reqHeight || width > reqWidth) {

            // Calculate ratios of height and width to requested height and width
            val heightRatio = Math.round(height.toFloat() / reqHeight.toFloat())
            val widthRatio = Math.round(width.toFloat() / reqWidth.toFloat())

            // Choose the smallest ratio as inSampleSize value, this will guarantee a final image
            // with both dimensions larger than or equal to the requested height and width.
            inSampleSize = if (heightRatio < widthRatio) heightRatio else widthRatio

            // This offers some additional logic in case the image has a strange
            // aspect ratio. For example, a panorama may have a much larger
            // width than height. In these cases the total pixels might still
            // end up being too large to fit comfortably in memory, so we should
            // be more aggressive with sample down the image (=larger inSampleSize).

            val totalPixels = (width * height).toFloat()

            // Anything more than 2x the requested pixels we'll sample down further
            val totalReqPixelsCap = (reqWidth * reqHeight * 2).toFloat()

            while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
                inSampleSize++
            }
        }

        return inSampleSize
    }

    @Throws(IOException::class)
    private fun rotateImageIfRequired(context: Context, img: Bitmap, selectedImage: Uri): Bitmap {

        val input = context.contentResolver.openInputStream(selectedImage)
        val ei: ExifInterface
        if (Build.VERSION.SDK_INT > 23)
            ei = ExifInterface(input)
        else
            ei = ExifInterface(selectedImage.path)

        val orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)

        when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> return rotateImage(img, 90)
            ExifInterface.ORIENTATION_ROTATE_180 -> return rotateImage(img, 180)
            ExifInterface.ORIENTATION_ROTATE_270 -> return rotateImage(img, 270)
            else -> return img
        }
    }

    private fun rotateImage(img: Bitmap, degree: Int): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(degree.toFloat())
        val rotatedImg = Bitmap.createBitmap(img, 0, 0, img.width, img.height, matrix, true)
        img.recycle()
        return rotatedImg
    }

    fun setting(array: Array<String?>, textView: TextView){
        //Remove
        val continueBtn =  findViewById<Button>(R.id.next)
        continueBtn.setVisibility(View.GONE);
        val  uploadBtn = findViewById<Button>(R.id.uploadBtn)
        uploadBtn.setVisibility(View.INVISIBLE)

        //Add
        val  mListView = findViewById<ListView>(R.id.list)
        mListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE)
        val adapter = ArrayAdapter(this,  R.layout.spinnertextlayout, array)
        mListView.setAdapter(adapter)

        var j = 0
        for (item in array) {
            if (textView.text.contains(item.toString()) ) {
                mListView.setItemChecked(j, true)
            }
            j++
        }



        val ll = findViewById<LinearLayout>(R.id.LinearLayout03)
        //ll.setVisibility(View.VISIBLE)
        mListView.setVisibility(View.VISIBLE)


        mListView.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            // 1
            val selectedItem = array.get(position)
            // 2
            textView.setText(selectedItem)

            val  mListView = findViewById<ListView>(R.id.list)
            mListView.setVisibility(View.INVISIBLE)
            val ll = findViewById<LinearLayout>(R.id.LinearLayout03)
            ll.setVisibility(View.INVISIBLE)
            val continueBtn = findViewById(R.id.next) as Button
            continueBtn.setVisibility(View.VISIBLE)
            val  uploadBtn = findViewById<Button>(R.id.uploadBtn)
            uploadBtn.setVisibility(View.VISIBLE)

        }
    }


    fun setting1(array: Array<String?>, textView: TextView){
        val continueBtn =  findViewById<Button>(R.id.next)
        continueBtn.setVisibility(View.GONE);
        val  mListView = findViewById<ListView>(R.id.list)
        mListView.setVisibility(View.VISIBLE)

        val  uploadBtn = findViewById<Button>(R.id.uploadBtn)
        uploadBtn.setVisibility(View.INVISIBLE)



        val adapter = ArrayAdapter(this,  R.layout.nonchecked_spinner, array)
        mListView.setAdapter(adapter)
        val ll = findViewById<LinearLayout>(R.id.LinearLayout03)
        ll.setVisibility(View.INVISIBLE)

        val context = this
        mListView.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            // 1
            mListView.setVisibility(View.INVISIBLE)
            uploadBtn.setVisibility(View.VISIBLE)
            continueBtn.setVisibility(View.VISIBLE);

            val selectedItem = array.get(position)
            when (position) {
                0 -> {
                    val credential = AccessToken.getCurrentAccessToken()
                    if (credential != null) {
                        val bundle = Bundle()
                        bundle.putString("fields", "id, name, first_name, last_name, email, picture.type(large)")
                        GraphRequest(AccessToken.getCurrentAccessToken(), "me", bundle, HttpMethod.GET, GraphRequest.Callback {

                            if (it != null) {
                                val data = it.jsonObject
                                if (data.has("picture")) {
                                    val profPicUrl = data.getJSONObject("picture").getJSONObject("data").getString("url")
                                    val countDownLatch1 = CountDownLatch(1)
                                    getBitmapFromURL(context, profPicUrl, countDownLatch1)
                                    countDownLatch1.await()
//                                    val pic = findViewById(R.id.pic) as ImageView
//                                    pic.setImageBitmap(image)

                                    val tempUri = getImageUri(applicationContext, tempImage!!)
                                    CropImage.activity(tempUri).setAspectRatio(1, 1)
                                            .start(this);
//                                    if (mAuth != null && profPicUrl != null) {
//                                        val image1 = pic.image
//                                        val bitmap = (image1!! as BitmapDrawable).getBitmap()
//                                        val storage = FirebaseStorage.getInstance()
//                                        val storageRef = storage.getReference()
//                                        val name = "images/" + mAuth.currentUser!!.uid + ".jpg"
//                                        val imagesRef = storageRef.child(name)
//                                        val stream = ByteArrayOutputStream()
//                                        bitmap.compress(Bitmap.CompressFormat.PNG, 90, stream)
//                                        val image2 = stream.toByteArray()
//                                        imagesRef.putBytes(image2).addOnSuccessListener {documentReference ->
//
//                                            imageUrl = documentReference.downloadUrl.toString()
//                                            if (DataManager.userObject.userInterests != null) {
//                                                DataManager.userObject.userInterests!!.userImage = image!!
//                                                DataManager.userObject.userInterests!!.userImageUrl = imageUrl
//                                            }
//                                            DataManager.saveImageUrl(imageUrl)
//                                            Log.d("", "DocumentSnapshot added with ID: ") }
//                                                .addOnFailureListener { e ->
//                                                }
//
//                                    }
                                }
                            }

                        }).executeAsync()
                    }
                    else {


                        LoginManager.getInstance().registerCallback(callbackManager!!, object : FacebookCallback<LoginResult> {
                            override fun onSuccess(result: LoginResult) {
                                println("=========================onsuccess")
                                val accessToken = AccessToken.getCurrentAccessToken()
                                val credential = FacebookAuthProvider.getCredential(accessToken.getToken())
                                if (mAuth != null) {
                                    
                                    mAuth.currentUser!!.linkWithCredential(credential).addOnCompleteListener(this@PersonalSettingActivity) { task: Task<AuthResult> ->

                                        if (task.isSuccessful) {
                                            val request = GraphRequest.newMeRequest(accessToken) { `object`, response ->
                                                println("===================JSON++" + `object`)
                                                var Surl = ""

                                                try {

                                                    if (`object`.has("picture")) {
                                                        Surl = `object`.getJSONObject("picture").getJSONObject("data").getString("url")
                                                    }
                                                    val countDownLatch1 = CountDownLatch(1)
                                                    getBitmapFromURL(context, Surl, countDownLatch1)
                                                    countDownLatch1.await()
                                                    val tempUri = getImageUri(applicationContext, tempImage!!)
                                                    CropImage.activity(tempUri).setAspectRatio(1, 1)
                                                            .start(this@PersonalSettingActivity);
//                                                    val pic = findViewById(R.id.pic) as ImageView
//                                                    pic.setImageBitmap(image)
//                                                    if (mAuth != null && Surl != null) {
//                                                        val image1 = pic.image
//                                                        val bitmap = (image1!! as BitmapDrawable).getBitmap()
//                                                        val storage = FirebaseStorage.getInstance()
//                                                        val storageRef = storage.getReference()
//                                                        val name = "images/" + mAuth.currentUser!!.uid + ".jpg"
//                                                        val imagesRef = storageRef.child(name)
//                                                        val stream = ByteArrayOutputStream()
//                                                        bitmap.compress(Bitmap.CompressFormat.PNG, 90, stream)
//                                                        val image2 = stream.toByteArray()
//                                                        imagesRef.putBytes(image2).addOnSuccessListener {documentReference ->
//
//                                                            imageUrl = documentReference.downloadUrl.toString()
//                                                            if (DataManager.userObject.userInterests != null) {
//                                                                DataManager.userObject.userInterests!!.userImage = image!!
//                                                                DataManager.userObject.userInterests!!.userImageUrl = imageUrl
//                                                            }
//                                                            DataManager.saveImageUrl(imageUrl)
//                                                            Log.d("", "DocumentSnapshot added with ID: ") }
//                                                                .addOnFailureListener { e ->
//                                                                }
//
//                                                    }


                                                } catch (e: Exception) {
                                                    e.printStackTrace()
                                                }
                                            }
                                            val parameters = Bundle()
                                            parameters.putString("fields", "id, name, first_name, last_name, email, picture.type(large)")
                                            request.parameters = parameters
                                            request.executeAsync()
                                        }
                                        else {
                                            LoginManager.getInstance().logOut();
                                            val builder = AlertDialog.Builder(this@PersonalSettingActivity)
                                            builder.setTitle("")
                                            builder.setMessage("Unable to connect the FB account to your account. Please try again!")
                                            val dialog1 = builder.setPositiveButton("Ok") { dialog, which -> }
                                            val dialog: AlertDialog = builder.create()
                                            dialog.show()
                                            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK)
                                        }
                                    }
                                }


                            }

                            override fun onCancel() {
                                //TODO Auto-generated method stub
                                println("=========================onCancel")
                                LoginManager.getInstance().logOut();
                            }

                            override fun onError(error: FacebookException) {
                                //TODO Auto-generated method stub
                                println("=========================onError" + error.toString())
                                LoginManager.getInstance().logOut();
                            }
                        })

                        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("email", "public_profile"))

//                        LoginManager.getInstance().logInWithReadPermissions(this, "")

                    }
                }
                1 -> {
                    val intent = Intent()
                    intent.type = "image/*"
                    intent.action = Intent.ACTION_GET_CONTENT
                    startActivityForResult(Intent.createChooser(intent, "Choose Picture"), 1)
                }
                2 -> {
                    val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
//                     Uri outputUri=FileProvider.getUriForFile(this, AUTHORITY, output)
                    if (takePictureIntent.resolveActivity(packageManager) != null) {
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputURI);
                        startActivityForResult(takePictureIntent, 2)
                    }

                }
                else -> { // Note the block
                    print("x is neither 1 nor 2")
                }
            }



        }
    }



    fun getBitmapFromURL(context: Context, src: String, countDownLatch: CountDownLatch) {
        if (src == "") {
            countDownLatch.countDown()
        }
        var input: InputStream? = null
        doAsync {
            try {
                val src = java.net.URL(src)
                val connection = src
                        .openConnection()
                connection.setDoInput(true)
                connection.connect()
                val input = connection.getInputStream()
                tempImage = BitmapFactory.decodeStream(input)
                countDownLatch.countDown()
            } catch (e: Exception) {
                print(e)
                countDownLatch.countDown()
            }
            uiThread {
            }
        }
    }

    fun dismissSpinner(view: View){
        val  mListView = findViewById<ListView>(R.id.list)
        mListView.setVisibility(View.INVISIBLE)
        val ll = findViewById<LinearLayout>(R.id.LinearLayout03)
        ll.setVisibility(View.INVISIBLE)
        val continueBtn = findViewById(R.id.next) as Button
        continueBtn.setVisibility(View.VISIBLE)
        val  uploadBtn = findViewById<Button>(R.id.uploadBtn)
        uploadBtn.setVisibility(View.VISIBLE)
    }

    fun applySpinner(view: View){
        dismissSpinner(view)
    }
}




