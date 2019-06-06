package com.app.tf.livnlive

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.util.Log
import android.view.View
import com.android.billingclient.api.*
import com.android.billingclient.api.BillingClient.BillingResponse
//import com.android.B
import com.android.billingclient.api.BillingClient.SkuType
import java.util.HashSet
import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.Color
import kotlinx.android.synthetic.main.activity_buy_msg.*
import org.json.JSONObject






class BuyMsgActivity : AppCompatActivity(), PurchasesUpdatedListener  {

    private var mBillingClient: BillingClient? = null
    var toUserId = ""
    var toUserName = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_buy_msg)
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        buyMsgPackDesc.text = "In order to message other matches, you will need to purchase the messaging upgrade for \$4.99 per month.  This charge will automatically renew each month on the purchase day until cancelled by the user.  Payment will be charged to the users account in Play store.\n\n" +
        "        • Product: In app messaging for Liv N' Live\n" +
        "        • Pricing: Full access at \$4.99 per month\n" +
        "        • Payment will be charged to Play store Account at confirmation of purchase.\n" +
        "        • Subscription automatically renews unless auto-renew is turned off at least 24-hours before the end of the current period.\n" +
        "        • Account will be charged for renewal within 24-hours prior to the end of the current period, for \$4.99\n" +
        "        • Subscriptions may be managed by you and auto-renewal may be turned off by going to your Account Settings after purchase.\n" +
        "        • Any unused portion of a free trial period, if offered, will be forfeited when you purchase a subscription, where applicable."
        toUserId = intent.getStringExtra("toUserId")
        toUserName = intent.getStringExtra("toUserName")
    }

    fun cancelPurchase(view: View){
        finish()
    }

    fun buyMsgPack(view: View) {
        mBillingClient = BillingClient.newBuilder(this).setListener(this).build()
        mBillingClient!!.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(@BillingResponse billingResponseCode: Int) {
                if (billingResponseCode == BillingResponse.OK) {

                    val skuList = ArrayList<String>()
                    skuList.add("livnliveiam")
                    //skuList.add("android.test.purchased")
                    val params = SkuDetailsParams.newBuilder()
                    params.setSkusList(skuList).setType(SkuType.SUBS)
                    mBillingClient!!.querySkuDetailsAsync(params.build(),
                            object : SkuDetailsResponseListener {
                                override fun onSkuDetailsResponse(@BillingResponse responseCode: Int, skuDetailsList: List<SkuDetails>) {
                                    // Process the result.
                                    if (responseCode === BillingResponse.OK && skuDetailsList != null) {
                                        for (skuDetails in skuDetailsList) {
                                            val sku = skuDetails.getSku()
                                            val price = skuDetails.getPrice()
                                            if ("livnliveiam" == sku) {
                                            //    if ("android.test.purchased" == sku) {
                                                val flowParams = BillingFlowParams.newBuilder()
                                                        .setSku(sku)
                                                        .setType(SkuType.INAPP)
                                                        .build()
                                                val responseCode = mBillingClient!!.launchBillingFlow(this@BuyMsgActivity, flowParams)
                                            }
                                        }
                                    }
                                    else {
                                        val dialog: AlertDialog
                                        val builder = AlertDialog.Builder(this@BuyMsgActivity)
                                        builder.setTitle("")
                                        builder.setMessage("Cannot make purchase")
                                        builder.setPositiveButton("Ok"){dialog, which ->

                                            dialog.dismiss()
                                        }
                                        dialog = builder.create()
                                        dialog.show()
                                        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK)
                                    }
                                }
                            })
                    // The billing client is ready. You can query purchases here.
                }
                else {
                    val builder = AlertDialog.Builder(this@BuyMsgActivity)
                    builder.setTitle("")
                    builder.setMessage("Cannot make purchase")
                    builder.setPositiveButton("Ok"){dialog, which ->
                    }
                    val dialog: AlertDialog = builder.create()
                    dialog.show()
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK)
                }
            }

            override fun onBillingServiceDisconnected() {
                // Try to restart the connection on the next request to
                // Google Play by calling the startConnection() method.
            }
        })

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)

    }

    override fun onPurchasesUpdated(@BillingResponse responseCode: Int,
                                             purchases: List<Purchase>?) {
        if (responseCode == BillingResponse.OK) {
//            for (purchase in purchases) {
////                handlePurchase(purchase)
//                val o = JSONObject(purchase.originalJson)
//                val id = o.optString("productId")
//                if (id == "messagepack4") {
            if (purchases != null) {
                for (purchase in purchases) {
                    mBillingClient!!.consumeAsync(purchase.purchaseToken, ConsumeResponseListener { responseCode, purchaseToken ->
                        if (responseCode == BillingClient.BillingResponse.OK) {

                        }
                    })
                }
            }
                   DataManager.savePaidStatus(true)
                    val msgActivity = Intent(this, MessageActivity::class.java)
                    msgActivity.putExtra("toUserId", toUserId);
                    msgActivity.putExtra("toUserName", toUserName);
                    startActivity(msgActivity)
                    finish();

//                }
//        }
        } else  {
            // Handle an error caused by a user cancelling the purchase flow.
            val builder = AlertDialog.Builder(this@BuyMsgActivity)
            builder.setTitle("")
            builder.setMessage("Cannot make purchase")
            builder.setPositiveButton("Ok"){dialog, which ->
            }
            val dialog: AlertDialog = builder.create()
            dialog.show()
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK)
        }
    }
}

