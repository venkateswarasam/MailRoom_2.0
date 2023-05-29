package com.xcarriermaterialdesign.pending

import android.app.Activity
import androidx.lifecycle.ViewModel

class PendingViewModel: ViewModel() {

    lateinit var  activity : Activity



    fun config(activity: Activity)
    {
        this.activity = activity
    }
}