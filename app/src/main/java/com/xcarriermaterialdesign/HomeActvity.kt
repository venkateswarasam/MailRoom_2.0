package com.xcarriermaterialdesign

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class HomeActvity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_actvity)

        getSupportActionBar()!!.hide();

    }
}