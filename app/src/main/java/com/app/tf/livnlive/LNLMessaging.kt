package com.app.tf.livnlive

import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.FirebaseInstanceIdService
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Handler
import android.os.Looper
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AlertDialog
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import android.support.v4.content.LocalBroadcastManager
import android.content.ComponentName
import android.app.ActivityManager
import java.util.*


class LNLInstanceIDService : FirebaseInstanceIdService() {

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    // [START refresh_token]
    override fun onTokenRefresh() {
        // Get updated InstanceID token.
        val refreshedToken = FirebaseInstanceId.getInstance().token
        Log.d(TAG, "Refreshed token: " + refreshedToken!!)

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        sendRegistrationToServer(refreshedToken)
    }
    // [END refresh_token]

    /**
     * Persist token to third-party servers.
     *
     * Modify this method to associate the user's FCM InstanceID token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */
    private fun sendRegistrationToServer(token: String?) {
        DataManager.saveUserDeviceToken()
        // TODO: Implement this method to send token to your app server.
    }

    companion object {

        private val TAG = "MyFirebaseIIDService"
    }
}

class LNLMessagingService : FirebaseMessagingService() {

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    // [START receive_message]
    override fun onMessageReceived(remoteMessage: RemoteMessage?) {
        // [START_EXCLUDE]
        // There are two types of messages data messages and notification messages. Data messages are handled
        // here in onMessageReceived whether the app is in the foreground or background. Data messages are the type
        // traditionally used with GCM. Notification messages are only received here in onMessageReceived when the app
        // is in the foreground. When the app is in the background an automatically generated notification is displayed.
        // When the user taps on the notification they are returned to the app. Messages containing both notification
        // and data payloads are treated as notification messages. The Firebase console always sends notification
        // messages. For more see: https://firebase.google.com/docs/cloud-messaging/concept-options
        // [END_EXCLUDE]

        Log.d(TAG, "From: " + remoteMessage!!.from!!)

        // Check if message contains a data payload.
        if (remoteMessage.data.size > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.data)
            val type = remoteMessage.data["type"]
            if (type == "L") {
                var name = "Someone"
                var user = remoteMessage.data["user"]
                if (user != null) {
                    name = user.toString()
                }
                val intent = Intent("FCMLK")
                // add data
                intent.putExtra("type", type)
                intent.putExtra("name", name)
                LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
                sendNotification("You have a new like", name + " liked you")
            }
            else if (type == "M") {
                var name = "Someone"
                var user = remoteMessage.data["name"]
                if (user != null) {
                    name = user.toString()
                }
                var message =  ""
                var msg = remoteMessage.data["message"]
                if (msg != null) {
                    message = msg.toString()
                }
                val intent = Intent("FCMMSG")
                var userId = remoteMessage.data["user"]
                if (userId == null) {
                    userId = ""
                }
                var newMsg = UserMsg(userId, false, Date(), message)
                DataManager.userObject.messages.add(newMsg)
                // add data
                intent.putExtra("type", type)
                intent.putExtra("user", userId)
                intent.putExtra("name", name)
                intent.putExtra("message", message)
                LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
                sendNotification("You have a new message from " + name, "")
            }
            else {
                val intent = Intent("FCMSM")
                LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
                sendNotification("You have a new match", "Click here to see your match")
            }
        }
        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }
    // [END receive_message]

    /**
     * Create and show a simple notification containing the received FCM message.
     *
     * @param messageBody FCM message body received.
     */
    private fun sendNotification(messageTitle: String, messageBody: String) {

        var intent = Intent(this, FeedsActivity::class.java)
        if (DataManager.userObject.userProfile == null || DataManager.userObject.userInterests == null) {
            intent = Intent(this, LaunchActivity::class.java)
        }
        if (mAuth.currentUser == null) {
            intent = Intent(this, MainActivity::class.java)
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT)

        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.logonew)
                .setContentTitle(messageTitle)
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setColor(Color.parseColor("#9A0100"))
                .setContentIntent(pendingIntent)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build())
    }

    companion object {

        private val TAG = "MyFirebaseMsgService"
    }
}