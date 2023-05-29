package com.xcarriermaterialdesign.trackreport

import android.app.Activity
import androidx.lifecycle.ViewModel

class TrackReportViewModel : ViewModel() {

    lateinit var  activity : Activity



    fun config(activity: Activity)
    {
        this.activity = activity
    }
}