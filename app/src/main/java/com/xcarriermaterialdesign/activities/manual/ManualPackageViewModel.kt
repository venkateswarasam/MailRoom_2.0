package com.xcarriermaterialdesign.activities.manual

import android.app.Activity
import androidx.lifecycle.ViewModel

class ManualPackageViewModel : ViewModel() {

    lateinit var activity: Activity


    fun config(activity: Activity) {
        this.activity = activity
    }

}
