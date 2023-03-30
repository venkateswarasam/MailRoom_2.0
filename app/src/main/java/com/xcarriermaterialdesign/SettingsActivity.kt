package com.xcarriermaterialdesign

import android.R.attr.left
import android.R.attr.right
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.xcarriermaterialdesign.utils.AnalyticsApplication


class SettingsActivity : AppCompatActivity() {

    var logout:TextView?= null
    var logout_img:ImageView?= null

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AnalyticsApplication.instance!!.setLocaleFa(this)

        setContentView(R.layout.activity_settings)

        supportActionBar?.hide()
        logout = findViewById(R.id.logout)
        logout_img = findViewById(R.id.logout_img)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)

        toolbar.setNavigationOnClickListener {

            finish()
        }


        logout_img!!.setOnClickListener {


            val intent = Intent(this, MainActivity::class.java)

            startActivity(intent)
        }


      //  initActionBar()

    }


    private fun initActionBar() {

        supportActionBar?.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
        supportActionBar?.setDisplayShowCustomEnabled(true)
        supportActionBar?.setCustomView(R.layout.actionbarnew)
        val view1: View = supportActionBar!!.customView
        val toolbar: Toolbar = view1.parent as Toolbar








      //  toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24)
      toolbar.setContentInsetsAbsolute(0, 0)
        val headertext:TextView = view1.findViewById(R.id.headertext)
        headertext.text = getString(R.string.user_settings)



        val profile: ImageView = view1.findViewById(R.id.profile)
        profile.setImageResource(R.drawable.ic_outline_save_24)

      /*  profile.setOnClickListener {

            val intent = Intent(this, SettingsActivity::class.java)

            startActivity(intent)

        }*/









    }
}