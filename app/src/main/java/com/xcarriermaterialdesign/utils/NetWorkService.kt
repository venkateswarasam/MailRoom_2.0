package com.xcarriermaterialdesign.utils

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.IBinder

class NetWorkService: Service() {

    private val mReceiver: BroadcastReceiver? = null


    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        onTaskRemoved(intent)
        return START_STICKY
    }
    override fun onBind(intent: Intent): IBinder? {
        // TODO: Return the communication channel to the service.
        throw UnsupportedOperationException("Not yet implemented")
    }

    override fun onCreate() {
        onRegisterNetworkState()
        super.onCreate()
    }



    // code change here



    override fun onDestroy() {
        super.onDestroy()
        val broadcastIntent = Intent()
        broadcastIntent.action = "restartservice"
        broadcastIntent.setClass(this, NetworkChangeReceiver::class.java)
        this.sendBroadcast(broadcastIntent)
    }


    /* override fun onDestroy() {
         super.onDestroy()



         try {
             if (NetworkChangeReceiver != null) unregisterReceiver(NetworkChangeReceiver())
         } catch (e: Exception) {
         }

     //    unregisterReceiver(NetworkChangeReceiver())
     }*/




    private fun onRegisterNetworkState(){
        registerReceiver(
            NetworkChangeReceiver(),
            IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        )

    }



    /* override fun onDestroy() {
         super.onDestroy()

         val restartServiceIntent = Intent(applicationContext, this.javaClass)
         restartServiceIntent.setPackage(packageName)
         startService(restartServiceIntent)

     }*/

    override fun onTaskRemoved(rootIntent: Intent) {
        val restartServiceIntent = Intent(applicationContext, this.javaClass)
        restartServiceIntent.setPackage(packageName)
        startService(restartServiceIntent)
        super.onTaskRemoved(rootIntent)
    }
}