package com.xcarriermaterialdesign

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.ActionBar
import androidx.appcompat.widget.Toolbar
import com.xcarriermaterialdesign.utils.AnalyticsApplication

class ForgotPassword : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AnalyticsApplication.instance!!.setLocaleFa(this)

        setContentView(R.layout.activity_forgot_password)

       // supportActionBar!!.hide()

        initactionbar()



        val toolbar = findViewById<Toolbar>(R.id.toolbar)

        toolbar.setNavigationOnClickListener {

            finish()
        }

        val resetpassword = findViewById<TextView>(R.id.resetpassword)

        resetpassword.setOnClickListener {

            val intent = Intent(this, MainActivity::class.java)

            startActivity(intent)
        }


        val privacy = findViewById<TextView>(R.id.privacy)


        privacy!!.setOnClickListener {


            val intent = Intent(this, WebActvity::class.java)

            startActivity(intent)
        }







    }


    private fun initactionbar(){

        supportActionBar?.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
        supportActionBar?.setDisplayShowCustomEnabled(true)
        supportActionBar?.setCustomView(R.layout.actionbarnew)
        val view1: View =  supportActionBar!!.customView
        val toolbar: Toolbar = view1.parent as Toolbar
        toolbar.setContentInsetsAbsolute(0, 0)
        //  toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24)
        val headertext:TextView = view1.findViewById(R.id.headertext)
        headertext.text = getString(R.string.backtosign)
        val profile: ImageView = view1.findViewById(R.id.profile)
        //val back: ImageView = view1.findViewById(R.id.back)

        // back.visibility = View.GONE
        //  profile.setImageResource(R.drawable.profilewhite)
        profile.visibility = View.GONE



    }

}