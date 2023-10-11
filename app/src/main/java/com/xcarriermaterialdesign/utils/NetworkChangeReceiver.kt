package com.xcarriermaterialdesign.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Build

class NetworkChangeReceiver : BroadcastReceiver() {
    /* override fun onReceive(context: Context, intent: Intent?) {
         try {
             val class2 = MainMenuActivity()

             if (isOnline(context)) {


                 class2.dialog(true)

                 // dialog(true)
                // Log.e("keshav", "Online Connect Intenet ")
             } else {

                 class2.dialog(false)

                 //  dialog(false)
                // Log.e("keshav", "Conectivity Failure !!! ")
             }
         } catch (e: NullPointerException) {
             e.printStackTrace()
         }
     }

     private fun isOnline(context: Context): Boolean {
         return try {
             val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
             val netInfo = cm.activeNetworkInfo
             //should check null because in airplane mode it will be null
             netInfo != null && netInfo.isConnected
         } catch (e: NullPointerException) {
             e.printStackTrace()
             false
         }
     }*/



    interface NetCheckerReceiverListener {
        fun onNetworkConnectionChanged(isConnected: Boolean)
    }

    companion object {
        var netConnectionCheckerReceiver: NetCheckerReceiverListener? = null
    }



    override fun onReceive(context: Context, intent: Intent) {

        //   Toast.makeText(context, "Received State Successfully", Toast.LENGTH_SHORT).show()
        println("Received State Successfully")
        if (netConnectionCheckerReceiver != null) {
            netConnectionCheckerReceiver!!.onNetworkConnectionChanged(
                isConnectedOrConnecting(
                    context
                )
            )

            // code change here

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(Intent(context, NetWorkService::class.java))
            }
        }
    }



    private fun isConnectedOrConnecting(context: Context): Boolean {
        val connMgr = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connMgr.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnectedOrConnecting
    }





}