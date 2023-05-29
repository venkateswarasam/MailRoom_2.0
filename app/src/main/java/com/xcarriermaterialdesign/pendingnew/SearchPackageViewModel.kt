package com.xcarriermaterialdesign.pendingnew

import android.app.Activity
import androidx.lifecycle.ViewModel

class SearchPackageViewModel: ViewModel()  {


    lateinit var  activity : Activity



    fun config(activity: Activity)
    {
        this.activity = activity
    }
}