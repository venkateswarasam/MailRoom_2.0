package com.xcarriermaterialdesign.activities.processfinal

import android.app.Activity
import androidx.lifecycle.ViewModel

class ProcessPackageFinalViewModel: ViewModel() {


    lateinit var  activity : Activity



    fun config(activity: Activity)
    {
        this.activity = activity
    }

}