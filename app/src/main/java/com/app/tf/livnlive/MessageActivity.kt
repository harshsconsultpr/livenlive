package com.app.tf.livnlive

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.LocalBroadcastManager
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.View.TEXT_ALIGNMENT_VIEW_END
import android.view.View.TEXT_ALIGNMENT_VIEW_START
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import com.android.billingclient.api.BillingClient
import kotlinx.android.synthetic.main.activity_message.*
import kotlinx.android.synthetic.main.msgview.view.*
import java.util.*

class MessageActivity : AppCompatActivity() {

    var toUserId = ""
    var topMost = true
    var messages: List<UserMsg> = listOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_message)
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        toUserId = intent.getStringExtra("toUserId")
        val toUserNameString = intent.getStringExtra("toUserName")
        toNameText.text = toUserNameString
        messages = DataManager.userObject.messages.filter { it.conUser == toUserId }

        var layout = LinearLayoutManager(this@MessageActivity)
        messageList.layoutManager = layout
        messageList.adapter = MsgActivityItem(this@MessageActivity, messages)
        if (messages.count() > 0) {
            messageList.scrollToPosition(messages.count() - 1)
        }
    }

    fun closeMsg(view: View) {
        finish()
    }

    fun sendMessageAction(view: View) {

        val textField = findViewById<EditText>(R.id.sendMsg)
        if (textField.text.toString() != "" && toUserId != "") {
            DataManager.sendMessage(toUserId, textField.text.toString()) {
                messages = DataManager.userObject.messages.filter { it.conUser == toUserId }
                var layout = LinearLayoutManager(this@MessageActivity)
                messageList.layoutManager = layout
                messageList.adapter = MsgActivityItem(this@MessageActivity, messages)
                if (messages.count() > 0) {
                    messageList.scrollToPosition(messages.count() - 1)
                }
            }
        }
        textField.text.clear()
        textField.setText("", TextView.BufferType.EDITABLE)
    }

    val msgReceiverMsg = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            var type = intent!!.getStringExtra("type")
            if (type != null) {
                if (type == "M" && topMost) {
                    messages = DataManager.userObject.messages.filter { it.conUser == toUserId }
                    var layout = LinearLayoutManager(this@MessageActivity)
                    messageList.layoutManager = layout
                    messageList.adapter = MsgActivityItem(this@MessageActivity, messages)
                    if (messages.count() > 0) {
                        messageList.scrollToPosition(messages.count() - 1)
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        topMost = true
        LocalBroadcastManager.getInstance(this).registerReceiver(msgReceiverMsg, IntentFilter("FCMMSG"));
    }

    override fun onPause() {
        super.onPause()
        topMost = false

    }
}


class MsgActivityItem (val context: Context, val dataSource: List<UserMsg>) : RecyclerView.Adapter<MsgViewHolder>() {


    // Gets the number of animals in the list
    override fun getItemCount(): Int {
        return dataSource.count()
//        if (DataManager.userObject.isOnline) {
//            return  dataSource.count() + 1
//        }
//        else {
//            return 1
//        }

    }

    override fun getItemViewType(position: Int): Int {

        return position
    }
    // Inflates the item views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MsgViewHolder {
        return  MsgViewHolder(LayoutInflater.from(context).inflate(R.layout.msgview, parent, false))

    }

    // Binds each animal in the ArrayList to a view
    override fun onBindViewHolder(holder: MsgViewHolder, position: Int) {


        val message = dataSource[position]
        holder?.msg?.text = message.text
        if (message.isFrom) {
            holder?.msg?.textAlignment = TEXT_ALIGNMENT_VIEW_END
        }
        else {
            holder?.msg?.textAlignment = TEXT_ALIGNMENT_VIEW_START
        }
    }
}



class MsgViewHolder (view: View) : RecyclerView.ViewHolder(view) {
    // Holds the TextView that will add each animal to
    val msg = view.msgText
}