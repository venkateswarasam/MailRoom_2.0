package com.xcarriermaterialdesign.activities.editpackage

import android.app.Activity
import androidx.lifecycle.ViewModel

class EditPackageViewModel  : ViewModel() {

    lateinit var activity: Activity


    fun config(activity: Activity) {
        this.activity = activity
    }


}