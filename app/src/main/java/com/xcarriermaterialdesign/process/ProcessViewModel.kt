package com.xcarriermaterialdesign.process

import android.app.Activity
import androidx.lifecycle.ViewModel

class ProcessViewModel: ViewModel() {

    lateinit var  activity : Activity



    fun config(activity: Activity)
    {
        this.activity = activity
    }

}
