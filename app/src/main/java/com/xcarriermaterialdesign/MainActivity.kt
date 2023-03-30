package com.xcarriermaterialdesign

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.room.Room
import com.xcarriermaterialdesign.roomdatabase.ProcessDao
import com.xcarriermaterialdesign.roomdatabase.ProcessDatabase
import com.xcarriermaterialdesign.utils.AnalyticsApplication

class MainActivity : AppCompatActivity() {

    var forgotPassword:TextView?= null
    var privacy:TextView?= null
    var login:TextView?= null
    private lateinit var processDao: ProcessDao


    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AnalyticsApplication.instance!!.setLocaleFa(this)

        setContentView(R.layout.activity_main)
        supportActionBar?.hide();

       // val current = resources.configuration.locale

       // println("==language==$current")



        forgotPassword = findViewById(R.id.forgotpassword)
        login = findViewById(R.id.login)
        privacy = findViewById(R.id.privacy)


        val db = Room.databaseBuilder(
            applicationContext,
            ProcessDatabase::class.java, "Process_database"
        ).allowMainThreadQueries().build()

        processDao = db.processDao()

        processDao.deleteAllProcessPackages()

        privacy!!.setOnClickListener {


            val intent = Intent(this, WebActvity::class.java)

            startActivity(intent)
        }



        forgotPassword!!.setOnClickListener {


            val intent = Intent(this, ForgotPassword::class.java)

            startActivity(intent)
        }


        login!!.setOnClickListener {


            val intent = Intent(this, BottomNavigationActivity::class.java)

            startActivity(intent)
        }


    }
}