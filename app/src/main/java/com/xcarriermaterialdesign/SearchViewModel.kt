package com.xcarriermaterialdesign

import android.app.Activity
import androidx.lifecycle.ViewModel

class SearchViewModel: ViewModel() {

    lateinit var  activity : Activity



    fun config(activity: Activity)
    {
        this.activity = activity
    }
}