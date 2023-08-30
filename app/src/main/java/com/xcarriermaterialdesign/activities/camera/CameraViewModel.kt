package com.xcarriermaterialdesign.activities.camera

import android.app.Activity
import androidx.lifecycle.ViewModel

class CameraViewModel  : ViewModel() {

    lateinit var activity: Activity


    fun config(activity: Activity) {
        this.activity = activity
    }


}