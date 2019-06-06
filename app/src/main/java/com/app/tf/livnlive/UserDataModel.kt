package com.app.tf.livnlive

import android.graphics.Bitmap
import android.media.Image
import java.util.*
import android.app.ProgressDialog
import android.content.Context
import android.widget.RelativeLayout
import android.widget.ProgressBar





/**
 * Created by vaibhav on 3/28/18.
 */

class User {

    var userProfile: UserProfile? = null
    var userInterests: UserInterests? = null
    var userSearchPref: UserSearchPref? = null
    var searchedUsers: MutableList<FeedsData> = mutableListOf()
    var seachedUserIds: ArrayList<String> = arrayListOf()
    var blockedUsers: ArrayList<String> = arrayListOf()
    var tempBlockedUsers: ArrayList<String> = arrayListOf()
    var tempBlockedUserTime: ArrayList<Date> = arrayListOf()
    var deviceToken = ""
    var isOnline = false
    var isPaid = false
    var signUpDate = Date()
    var messages: MutableList<UserMsg> = mutableListOf()

}

data class UserProfile(var name: String, var bodyType: String, var month: String, var year: String, var ethnicity: String, var height: Double, var gender: String)

data class UserInterests(var interests: String, var displayItems: String, var userImage: Bitmap, var userImageUrl: String)

data class UserSearchPref(var fromAge: Int, var toAge: Int, var fromHeight: Double, var toHeight: Double, var ethnicity: String, var bodyType: String, var gender: String)

data class FeedsData(var image: Bitmap, var id: String, var title: String? = null, var interest: String? = null)


data class UserMsg(var conUser: String, var isFrom: Boolean, var Date: Date, var text: String)

