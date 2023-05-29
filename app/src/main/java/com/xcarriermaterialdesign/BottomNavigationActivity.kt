package com.xcarriermaterialdesign

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.xcarriermaterialdesign.databinding.ActivityBottomNavigationBinding
import com.xcarriermaterialdesign.utils.AnalyticsApplication
import com.xcarriermaterialdesign.utils.NetWorkService
import com.xcarriermaterialdesign.utils.NetworkChangeReceiver
import com.xcarriermaterialdesign.utils.NetworkConnection
import java.text.SimpleDateFormat
import java.util.*

class BottomNavigationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBottomNavigationBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AnalyticsApplication.instance!!.setLocaleFa(this)

       // startService(Intent(applicationContext, NetWorkService::class.java))


        binding = ActivityBottomNavigationBinding.inflate(layoutInflater)
        setContentView(binding.root)


        AnalyticsApplication.instance?.setPlantId("")








        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_bottom_navigation)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications
            )
        )

        //initActionBar()

        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)



















        //  initActionBar()
    }


    private fun initActionBar() {

       supportActionBar?.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
        supportActionBar?.setDisplayShowCustomEnabled(true)
        supportActionBar?.setCustomView(R.layout.action_bar)
        val view1: View = supportActionBar!!.customView
        val toolbar: Toolbar = view1.parent as Toolbar
        toolbar.setContentInsetsAbsolute(0, 0)

        val headertext:TextView = view1.findViewById(R.id.headertext)
        headertext.text = getString(R.string.goodafter)
        val profile: ImageView = view1.findViewById(R.id.profile)
        val back: ImageView = view1.findViewById(R.id.back)

        back.visibility = View.GONE
        profile.setImageResource(R.drawable.ic_outline_account_circle_24)



        profile.setOnClickListener {

            val intent = Intent(this, SettingsActivity::class.java)

            startActivity(intent)

        }









    }



}