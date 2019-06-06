package com.app.tf.livnlive

/**
 * Created by Mark on 3/28/18.
 */


import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

import android.util.Log
//import jdk.nashorn.internal.runtime.ECMAException.getException
//import android.support.test.orchestrator.junit.BundleJUnitUtils.getResult
//import org.junit.experimental.results.ResultMatchers.isSuccessful
import android.graphics.BitmapFactory
import android.graphics.Bitmap

import android.content.Context
import com.google.firebase.iid.FirebaseInstanceId
import java.text.*
//import com.sun.org.apache.xpath.internal.SourceTreeManager.getXMLReader
import android.os.AsyncTask
import com.google.android.gms.tasks.Task
import org.jetbrains.anko.coroutines.experimental.asReference
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.io.InputStream
import java.time.LocalDateTime
import java.util.*
import java.util.concurrent.CountDownLatch
import java.time.format.DateTimeFormatter
import kotlin.collections.ArrayList


var db = FirebaseFirestore.getInstance()

val mAuth = FirebaseAuth.getInstance()

private fun getAge(year: String, month: String): Int {
    val dob = Calendar.getInstance()
    val today = Calendar.getInstance()

    val date = SimpleDateFormat("MMMM").parse(month)
    val cal = Calendar.getInstance()
    cal.time = date
    dob.set(year.toInt(), cal.get(Calendar.MONTH), 1)

    var age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR)

    if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)) {
        age--
    }


    return age
}

object DataManager {

    var userObject = User()
    var tempUserObject = User()
    var isFirstSetup = false
    var myImage: Bitmap? = null

    var feedDataImage: Bitmap? = null

    var userDataImage: Bitmap? = null

    fun saveUserData() {
        if (tempUserObject.userInterests != null && tempUserObject.userProfile != null && tempUserObject.userSearchPref != null) {
            userObject.userProfile = tempUserObject.userProfile!!.copy()
            userObject.userInterests = tempUserObject.userInterests!!.copy()
            userObject.userSearchPref = tempUserObject.userSearchPref!!.copy()
            tempUserObject.userProfile = null
            tempUserObject.userInterests = null
            tempUserObject.userSearchPref = null
        }
        if (userObject.userInterests != null && userObject.userProfile != null && userObject.userSearchPref != null) {

            val age = getAge(userObject.userProfile!!.year, userObject.userProfile!!.month)
            var tokenString = ""
            val token = FirebaseInstanceId.getInstance().getToken()
            if (token != null && mAuth.currentUser != null) {
                tokenString = token
            }
            var user: HashMap<String, Any> = hashMapOf("name" to userObject.userProfile!!.name,
                    "monthofbirth" to userObject.userProfile!!.month,
                    "yearofbirth" to userObject.userProfile!!.year,
                    "height" to userObject.userProfile!!.height,
                    "bodyType" to userObject.userProfile!!.bodyType,
                    "ethnicity" to userObject.userProfile!!.ethnicity,
                    "gender" to userObject.userProfile!!.gender,
                    "age" to age,
                    "interests" to userObject.userInterests!!.interests,
                    "displayitems" to userObject.userInterests!!.displayItems,
                    "searchFromAge" to userObject.userSearchPref!!.fromAge,
                    "searchToAge" to userObject.userSearchPref!!.toAge,
                    "searchFromHeight" to userObject.userSearchPref!!.fromHeight,
                    "searchToHeight" to userObject.userSearchPref!!.toHeight,
                    "searchBodyType" to userObject.userSearchPref!!.bodyType,
                    "searchEthnicity" to userObject.userSearchPref!!.ethnicity,
                    "searchGender" to userObject.userSearchPref!!.gender,
                    "available" to userObject.isOnline,
                    "isPaid" to userObject.isPaid,
                    "deviceToken" to tokenString
                    )

            if (isFirstSetup) {
                user["signUpDate"] = Date()
            }
            if (userObject.userInterests!!.userImageUrl != "") {
                user["imageUrl"] = userObject.userInterests!!.userImageUrl
            }

            db.collection("UserProfile").document(mAuth.currentUser!!.uid)
                    .update(user)
                    .addOnSuccessListener {documentReference ->
                        Log.d("", "DocumentSnapshot added with ID: ") }
                    .addOnFailureListener { e ->
                        db.collection("UserProfile").document(mAuth.currentUser!!.uid).set(user)
                                .addOnSuccessListener { docReference ->
                                    Log.d("", "DocumentSnapshot added with ID: ") }
                                .addOnFailureListener { e ->
                                    Log.w("", "Error adding document", e) }

                                }
                        }
        }


    fun fetchUserData(context: Context, callback: (Boolean) -> Unit) {
        val docRef = db.collection("UserProfile").document(mAuth.currentUser!!.uid)
        docRef.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val document = task.result
                if (document != null) {
                    var data = task.result.data
                    if (data != null) {

                        var userName = data.get("name").toString()
                        var bodyType = data.get("bodyType").toString()
                        var month = data.get("monthofbirth").toString()
                        var year = data.get("yearofbirth").toString()
                        var ethnicity = data.get("ethnicity").toString()
                        var heightString = data.get("height").toString()

                        var gender = data.get("gender").toString()
                        if (userName != "null" && heightString != "null") {
                            userObject.userProfile = UserProfile(userName, bodyType, month, year, ethnicity, heightString.toDouble(), gender)
                        }

                        var interests = data.get("interests").toString()
                        var displayItems = data.get("displayitems").toString()
                        var userImageUrl = data.get("imageUrl").toString()
                        if (userImageUrl == "null") {
                            userImageUrl = ""
                        }
                        var image = BitmapFactory.decodeResource(context.getResources(), R.drawable.defaultprofile);
                        val countDownLatch = CountDownLatch(1)
                        getBitmapFromURL(context, userImageUrl, countDownLatch)
                        countDownLatch.await()
                        if (userDataImage != null) {
                            image = userDataImage
                            myImage = userDataImage
                        }
                        userDataImage = null
                        if (interests != "null" && displayItems != "null") {
                            userObject.userInterests = UserInterests(interests, displayItems, image, userImageUrl)
                        }

                        //UserSearchPref(var fromAge: Int, var toAge: Int, var fromHeight: Int, var toHeight: Int, var ethnicity: String, var bodyType: String, var gender: String)
                        var searchfromAgeString = data.get("searchFromAge").toString()
                        var searchtoAgeString = data.get("searchToAge").toString()
                        var searchfromHeightString = data.get("searchFromHeight").toString()
                        var searchtoHeightString = data.get("searchToHeight").toString()
                        var searchethnicity = data.get("searchEthnicity").toString()
                        var searchbodyType = data.get("searchBodyType").toString()
                        var searchgender = data.get("searchGender").toString()

                        if (searchfromAgeString != "null" && searchtoAgeString != "null" && searchfromHeightString != "null" && searchtoHeightString != "null") {
                            userObject.userSearchPref = UserSearchPref(searchfromAgeString.toInt(), searchtoAgeString.toInt(), searchfromHeightString.toDouble(), searchtoHeightString.toDouble(), searchethnicity, searchbodyType, searchgender)

                        }

                        val isOnline = data.get("available") as? Boolean
                        if (isOnline != null) {
                            userObject.isOnline = isOnline
                        }
                        val isPaid = data.get("isPaid") as? Boolean
                        if (isPaid != null) {
                            userObject.isPaid = isPaid
                        }

                        var searchedUsers = data.get("SearchedUsers") as? ArrayList<String>
                        var blockedUsers = data.get("blockedUsers") as? ArrayList<String>
                        var tempBlockedUsers = data.get("tempBlockedUsers") as? ArrayList<String>
                        var tempBlockedUserTime = data.get("tempBlockedUserTime") as? ArrayList<Date>

                        userObject.blockedUsers = arrayListOf<String>()
                        if (blockedUsers != null) {
                            userObject.blockedUsers = blockedUsers
                        }

                        userObject.tempBlockedUsers = arrayListOf<String>()
                        if (tempBlockedUsers != null) {
                            userObject.tempBlockedUsers = tempBlockedUsers
                        }

                        userObject.tempBlockedUserTime = arrayListOf<Date>()
                        if (tempBlockedUserTime != null) {
                            userObject.tempBlockedUserTime = tempBlockedUserTime
                        }


                        userObject.searchedUsers = mutableListOf<FeedsData>()
                        if (searchedUsers != null && searchedUsers.count() != 0) {
                            userObject.seachedUserIds = searchedUsers
                            for (searchUser in searchedUsers) {

                                val docRef = db.collection("UserProfile").document(searchUser)
                                docRef.get().addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        val document = task.result
                                        if (document != null) {
                                            var data = task.result.data
                                            if (data != null) {
                                                val displayItems = data.get("displayitems").toString()
                                                var userImageUrl = data.get("imageUrl").toString()

                                                var image = BitmapFactory.decodeResource(context.getResources(), R.drawable.defaultprofile)//getBitmapFromURL(context, userImageUrl)
                                                val countDownLatch1 = CountDownLatch(1)
                                                getBitmapFromURL1(context, userImageUrl, countDownLatch1)
                                                countDownLatch1.await()
                                                if (feedDataImage != null) {
                                                    image = feedDataImage
                                                }
                                                feedDataImage = null
                                                var searchUserObj = FeedsData(image, searchUser)
                                                when (displayItems) {
                                                    "Photo + Name" -> {
                                                        var userName = data.get("name").toString()
                                                        searchUserObj.title = userName
                                                    }
                                                    "Photo + Name + Interests" -> {
                                                        var userName = data.get("name").toString()
                                                        var interests = data.get("interests").toString()
                                                        searchUserObj.title = userName
                                                        searchUserObj.interest = interests
                                                    }
                                                    else -> { // Note the block
                                                    }
                                                }

                                                userObject.searchedUsers.add(searchUserObj)
                                            }
                                        }
                                    }
                                    if (searchedUsers.last() == searchUser) {
                                        if (userObject.userInterests != null && userObject.userProfile != null && userObject.userSearchPref != null) {
                                            callback(true)
                                        } else {
                                            callback(false)
                                        }
                                    }
                                }

                            }
                        } else {
                            if (userObject.userInterests != null && userObject.userProfile != null && userObject.userSearchPref != null) {
                                callback(true)
                            } else {
                                callback(false)
                            }
                        }
                        Log.d("", "DocumentSnapshot data: " + task.result.data)
                    } else {
                        if (userObject.userInterests != null && userObject.userProfile != null && userObject.userSearchPref != null) {
                            callback(true)
                        } else {
                            callback(false)
                        }
                    }
                } else {
                    if (userObject.userInterests != null && userObject.userProfile != null && userObject.userSearchPref != null) {
                        callback(true)
                    }
                    else {
                        callback(false)
                    }
                    Log.d("", "No such document")
                }
            } else {
                if (userObject.userInterests != null && userObject.userProfile != null && userObject.userSearchPref != null) {
                    callback(true)
                }
                else {
                    callback(false)
                }
                Log.d("", "get failed with ", task.exception)
            }
        }
    }

    fun fetchSearchedUsers(context: Context, callback: (Boolean) -> Unit) {
        val currentSearchList = userObject.seachedUserIds
        userObject.searchedUsers = mutableListOf<FeedsData>()
        val docRef = db.collection("UserProfile").document(mAuth.currentUser!!.uid)
        docRef.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val document = task.result
                if (document != null) {
                    var data = task.result.data
                    if (data != null) {
                        userObject.searchedUsers = mutableListOf<FeedsData>()
                        var searchedUsers = data.get("SearchedUsers") as? ArrayList<String>

                        var count = 0
                        if (searchedUsers != null && searchedUsers.count() != 0) {
                            userObject.seachedUserIds = searchedUsers
                            for (searchUser in searchedUsers) {

                                val docRef = db.collection("UserProfile").document(searchUser)
                                docRef.get().addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        val document = task.result
                                        if (document != null) {
                                            var data = task.result.data
                                            if (data != null) {
                                                val displayItems = data.get("displayitems").toString()
                                                var userImageUrl = data.get("imageUrl").toString()
                                                var image = BitmapFactory.decodeResource(context.getResources(), R.drawable.defaultprofile)//getBitmapFromURL(context, userImageUrl)
                                                val countDownLatch1 = CountDownLatch(1)
                                                getBitmapFromURL1(context, userImageUrl, countDownLatch1)
                                                countDownLatch1.await()
                                                if (feedDataImage != null) {
                                                    image = feedDataImage
                                                }
                                                feedDataImage = null
                                                var searchUserObj = FeedsData(image, searchUser)
                                                when (displayItems) {
                                                    "Photo + Name" -> {
                                                        var userName = data.get("name").toString()
                                                        searchUserObj.title = userName
                                                    }
                                                    "Photo + Name + Interests" -> {
                                                        var userName = data.get("name").toString()
                                                        var interests = data.get("interests").toString()
                                                        searchUserObj.title = userName
                                                        searchUserObj.interest = interests
                                                    }
                                                    else -> { // Note the block
                                                    }
                                                }
                                                userObject.searchedUsers.add(searchUserObj)
                                            }

                                        }
                                    }
                                    if (count >= searchedUsers.count() - 1) {
                                        callback(true)
                                    }
                                    count++;
                                }
                            }
                            userObject.seachedUserIds = searchedUsers

                        } else {
                            callback(true)
                        }
                    }
                    else {
                        callback(false)
                    }
                }
                else {
                    callback(false)
                }
            }
        }

    }

    fun saveImageUrl(url: String) {
        if (mAuth.currentUser != null && url != "") {
            val user: HashMap<String, Any> = hashMapOf("imageUrl" to url)
            db.collection("UserProfile").document(mAuth.currentUser!!.uid)
                    .update(user)
                    .addOnSuccessListener {documentReference ->
                        Log.d("", "DocumentSnapshot added with ID: ") }
                    .addOnFailureListener { e ->
                        db.collection("UserProfile").document(mAuth.currentUser!!.uid).set(user)
                                .addOnSuccessListener { docReference ->
                                    Log.d("", "DocumentSnapshot added with ID: ") }
                                .addOnFailureListener { e ->
                                    Log.w("", "Error adding document", e) }

                    }
        }

    }

    fun saveUserDeviceToken() {
        val token = FirebaseInstanceId.getInstance().getToken()
        if (token != null && mAuth.currentUser != null) {
            val user: HashMap<String, Any> = hashMapOf("deviceToken" to token,
                    "isiOS" to false)
            db.collection("UserProfile").document(mAuth.currentUser!!.uid)
                    .update(user)
                    .addOnSuccessListener {documentReference ->
                        Log.d("", "DocumentSnapshot added with ID: ") }
                    .addOnFailureListener { e ->
                        db.collection("UserProfile").document(mAuth.currentUser!!.uid).set(user)
                                .addOnSuccessListener { docReference ->
                                    Log.d("", "DocumentSnapshot added with ID: ") }
                                .addOnFailureListener { e ->
                                    Log.w("", "Error adding document", e) }

                    }
        }

    }

    fun saveOnlineStatus(status: Boolean) {
        if (mAuth.currentUser != null) {
            val user: HashMap<String, Any> = hashMapOf("available" to status)
            db.collection("UserProfile").document(mAuth.currentUser!!.uid)
                    .update(user)
                    .addOnSuccessListener {documentReference ->
                        Log.d("", "DocumentSnapshot added with ID: ") }
                    .addOnFailureListener { e ->
                        db.collection("UserProfile").document(mAuth.currentUser!!.uid).set(user)
                                .addOnSuccessListener { docReference ->
                                    Log.d("", "DocumentSnapshot added with ID: ") }
                                .addOnFailureListener { e ->
                                    Log.w("", "Error adding document", e) }

                    }
        }

    }

//    fun getCurrentTimeUsingDate() {
//        val date = Date()
//        val strDateFormat = "hh:mm:ss a"
//        val dateFormat = SimpleDateFormat(strDateFormat)
//        val formattedDate = dateFormat.format(date)
//        println("Current time of the day using Date - 12 hour format: $formattedDate")
//    }

    fun sendMessage(toUser: String, messageText: String, callback: (Boolean) -> Unit) {
        if (mAuth.currentUser != null) {
            val chars = "abcdefghijklmnopqrstuvwxyz".toCharArray()
            val sb = StringBuilder(20)
            val random = Random()
            for (i in 0..19) {
                val c = chars[random.nextInt(chars.size)]
                sb.append(c)
            }
//            val current = LocalDateTime.now()
//
//            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")
//            val formatted = current.format(formatter)

            val date = Date()
            val user: HashMap<String, Any> = hashMapOf("fromUser" to mAuth.currentUser!!.uid,
                    "toUser" to toUser,
                    "messageText" to messageText,
                    "fromUserName" to userObject.userProfile!!.name,
                    "messageDate" to date)

            val msg = UserMsg(toUser, true, date, messageText)
            userObject.messages.add(msg)

            db.collection("UserMessage").document(sb.toString())
                    .set(user)
                    .addOnSuccessListener {documentReference ->
                        Log.d("", "DocumentSnapshot added with ID: ") }
                    .addOnFailureListener { e ->
                        db.collection("UserProfile").document(mAuth.currentUser!!.uid).update(user)
                                .addOnSuccessListener { docReference ->
                                    Log.d("", "DocumentSnapshot added with ID: ") }
                                .addOnFailureListener { e ->
                                    Log.w("", "Error adding document", e) }

                    }
            callback(true)
        }

    }

    fun savePaidStatus(status: Boolean) {
        if (mAuth.currentUser != null) {
            userObject.isPaid = true
            val user: HashMap<String, Any> = hashMapOf("isPaid" to status)
            db.collection("UserProfile").document(mAuth.currentUser!!.uid)
                    .update(user)
                    .addOnSuccessListener {documentReference ->
                        Log.d("", "DocumentSnapshot added with ID: ") }
                    .addOnFailureListener { e ->
                        db.collection("UserProfile").document(mAuth.currentUser!!.uid).set(user)
                                .addOnSuccessListener { docReference ->
                                    Log.d("", "DocumentSnapshot added with ID: ") }
                                .addOnFailureListener { e ->
                                    Log.w("", "Error adding document", e) }

                    }
        }

    }

    fun saveLocation(lat: Double, long: Double) {
        if (mAuth != null && mAuth.currentUser != null) {
            val user: HashMap<String, Any> = hashMapOf("Lon" to long,
                    "Lat" to lat)
            db.collection("UserProfile").document(mAuth.currentUser!!.uid)
                    .update(user)
                    .addOnSuccessListener {documentReference ->

                        db.collection("UserLocation").document(mAuth.currentUser!!.uid)
                                .update(user)
                                .addOnSuccessListener {documentReference ->

                                    Log.d("", "DocumentSnapshot added with ID: ") }
                                .addOnFailureListener { e ->
                                    db.collection("UserLocation").document(mAuth.currentUser!!.uid).set(user)
                                            .addOnSuccessListener { docReference ->
                                                Log.d("", "DocumentSnapshot added with ID: ") }
                                            .addOnFailureListener { e ->
                                                Log.w("", "Error adding document", e) }

                                }
                        Log.d("", "DocumentSnapshot added with ID: ") }
                    .addOnFailureListener { e ->
                        db.collection("UserProfile").document(mAuth.currentUser!!.uid).set(user)
                                .addOnSuccessListener { docReference ->
                                    Log.d("", "DocumentSnapshot added with ID: ") }

                        db.collection("UserLocation").document(mAuth.currentUser!!.uid)
                                .update(user)
                                .addOnSuccessListener {documentReference ->

                                    Log.d("", "DocumentSnapshot added with ID: ") }
                                .addOnFailureListener { e ->
                                    db.collection("UserLocation").document(mAuth.currentUser!!.uid).set(user)
                                            .addOnSuccessListener { docReference ->
                                                Log.d("", "DocumentSnapshot added with ID: ") }
                                            .addOnFailureListener { e ->
                                                Log.w("", "Error adding document", e) }

                                }
                                .addOnFailureListener { e ->

                                    Log.w("", "Error adding document", e) }

                    }
        }

    }

    fun updateLikeList(withUser: String) {
        if (mAuth.currentUser != null) {
            val user: HashMap<String, Any> = hashMapOf("likedUser" to withUser,
                    "userName" to userObject.userProfile!!.name)
            db.collection("UserLikeList").document(mAuth.currentUser!!.uid)
                    .set(user)
                    .addOnSuccessListener {documentReference ->
                        Log.d("", "DocumentSnapshot added with ID: ") }
                    .addOnFailureListener { e ->
                        db.collection("UserProfile").document(mAuth.currentUser!!.uid).update(user)
                                .addOnSuccessListener { docReference ->
                                    Log.d("", "DocumentSnapshot added with ID: ") }
                                .addOnFailureListener { e ->
                                    Log.w("", "Error adding document", e) }

                    }
        }

    }

    fun updateBlockList(withUser: String) {
        if (mAuth.currentUser != null) {
            val blockedUSers = userObject.blockedUsers
            userObject.blockedUsers.add(withUser)
            userObject.seachedUserIds.remove(withUser)
            val user: HashMap<String, Any> = hashMapOf("blockedUsers" to userObject.blockedUsers,
                    "SearchedUsers" to userObject.seachedUserIds)
            db.collection("UserProfile").document(mAuth.currentUser!!.uid)
                    .update(user)
                    .addOnSuccessListener {documentReference ->
                        Log.d("", "DocumentSnapshot added with ID: ") }
                    .addOnFailureListener { e ->
                        db.collection("UserProfile").document(mAuth.currentUser!!.uid).set(user)
                                .addOnSuccessListener { docReference ->
                                    Log.d("", "DocumentSnapshot added with ID: ") }
                                .addOnFailureListener { e ->
                                    Log.w("", "Error adding document", e) }

                    }
        }

    }

    fun updateTempBlockList(withUser: String) {
        if (mAuth.currentUser != null) {
            val blockedUSers = userObject.tempBlockedUsers
            userObject.tempBlockedUsers.add(withUser)
            userObject.tempBlockedUserTime.add(Date())
            userObject.seachedUserIds.remove(withUser)
            val user: HashMap<String, Any> = hashMapOf("tempBlockedUsers" to userObject.tempBlockedUsers,
                    "tempBlockedUserTime" to userObject.tempBlockedUserTime,
                    "SearchedUsers" to userObject.seachedUserIds)
            db.collection("UserProfile").document(mAuth.currentUser!!.uid)
                    .update(user)
                    .addOnSuccessListener {documentReference ->
                        Log.d("", "DocumentSnapshot added with ID: ") }
                    .addOnFailureListener { e ->
                        db.collection("UserProfile").document(mAuth.currentUser!!.uid).set(user)
                                .addOnSuccessListener { docReference ->
                                    Log.d("", "DocumentSnapshot added with ID: ") }
                                .addOnFailureListener { e ->
                                    Log.w("", "Error adding document", e) }

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
                userDataImage = BitmapFactory.decodeStream(input)
                countDownLatch.countDown()
            } catch (e: Exception) {
                print(e)
                countDownLatch.countDown()
            }
            uiThread {
            }
        }
    }

    fun getBitmapFromURL1(context: Context, src: String, countDownLatch: CountDownLatch) {
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
                feedDataImage = BitmapFactory.decodeStream(input)
                countDownLatch.countDown()
            } catch (e: Exception) {
                print(e)
                countDownLatch.countDown()
            }
            uiThread {
            }
        }
    }
}

