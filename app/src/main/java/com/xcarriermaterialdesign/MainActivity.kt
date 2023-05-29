package com.xcarriermaterialdesign

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.room.Room
import com.xcarriermaterialdesign.roomdatabase.BulkDao
import com.xcarriermaterialdesign.roomdatabase.ProcessDao
import com.xcarriermaterialdesign.roomdatabase.ProcessDatabase
import com.xcarriermaterialdesign.utils.AnalyticsApplication

class MainActivity : AppCompatActivity() {

    var forgotPassword:TextView?= null
    var privacy:TextView?= null
    var login:TextView?= null
    private lateinit var processDao: ProcessDao
    private lateinit var bulkDao: BulkDao

    private val neededPermissions = arrayOf(
        Manifest.permission.CAMERA
    )



    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AnalyticsApplication.instance!!.setLocaleFa(this)

        setContentView(R.layout.activity_main)
        supportActionBar?.hide();

        checkPermission()
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
        bulkDao = db.bulkDao()

      processDao.deleteAllProcessPackages()
     //   bulkDao.deleteAllBulkPackages()

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


    private fun checkPermission(): Boolean {
        val currentAPIVersion = Build.VERSION.SDK_INT
        if (currentAPIVersion >= Build.VERSION_CODES.M) {
            val permissionsNotGranted = ArrayList<String>()
            for (permission in neededPermissions) {
                if (ContextCompat.checkSelfPermission(
                        this,
                        permission
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    permissionsNotGranted.add(permission)
                }
            }
            if (permissionsNotGranted.size > 0) {
                var shouldShowAlert = false
                for (permission in permissionsNotGranted) {
                    shouldShowAlert =
                        ActivityCompat.shouldShowRequestPermissionRationale(this, permission)
                }

                val arr = arrayOfNulls<String>(permissionsNotGranted.size)
                val permissions = permissionsNotGranted.toArray(arr)
                if (shouldShowAlert) {
                   // showPermissionAlert(permissions)
                } else {
                    requestPermissions(permissions)
                }
                return false
            }
        }
        return true
    }

    private fun requestPermissions(permissions: Array<String?>) {
        ActivityCompat.requestPermissions(this, permissions, REQUEST_CODE
        )
    }

    companion object {
        const val REQUEST_CODE = 100
    }

}