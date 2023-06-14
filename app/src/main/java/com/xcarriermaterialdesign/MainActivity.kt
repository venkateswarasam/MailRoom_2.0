package com.xcarriermaterialdesign

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.*
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.room.Room
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.location.*
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



    private var mLocationRequest: LocationRequest? = null
    private var mFusedLocationClient: FusedLocationProviderClient? = null
    private var mSettingsClient: SettingsClient? = null

    private var mLocationSettingsRequest: LocationSettingsRequest? = null
    private var mLocationCallback: LocationCallback? = null
    private var mCurrentLocation: Location? = null
    internal var latitude = 0.0
    internal var longitude = 0.0
    private var mRequestingLocationUpdates: Boolean? = null



    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AnalyticsApplication.instance!!.setLocaleFa(this)

        setContentView(R.layout.activity_main)
        supportActionBar?.hide();

        checkPermission()
       // val current = resources.configuration.locale

       // println("==language==$current")



     /*   val permission = Manifest.permission.ACCESS_FINE_LOCATION
        val res: Int = applicationContext.checkCallingOrSelfPermission(permission)

        if (res == PackageManager.PERMISSION_GRANTED){


            init1()
            startLocationUpdates()


            // startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
        }
        else{


            checkLocationPermission()


        }*/





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


    override fun onResume() {
        super.onResume()



        val locationManager: LocationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {

            // buildAlertMessageNoGps();
        }
        else{

            init1()
            startLocationUpdates()

        }
    }


    private fun checkLocationPermission(): Boolean {
        return if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?

            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                androidx.appcompat.app.AlertDialog.Builder(this)
                    .setTitle(R.string.app_name)
                    .setMessage(R.string.msgstatus)
                    .setPositiveButton(R.string.ok) { dialogInterface, i ->
                        //Prompt the user once explanation has been shown

                        ActivityCompat.requestPermissions(
                            this, arrayOf<String?>(
                                Manifest.permission.ACCESS_FINE_LOCATION
                            ), REQUEST_PERMISSIONS_REQUEST_CODE
                        )
                    }
                    .create()
                    .show()
            } else {
                // No explanation needed, we can request the permission.

                //  val intent = Intent(this, PermissionActivity::class.java)
                //  startActivity(intent)
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf<String?>(Manifest.permission.ACCESS_FINE_LOCATION),
                    REQUEST_PERMISSIONS_REQUEST_CODE
                )
            }
            false
        } else {









            true
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {



            REQUEST_PERMISSIONS_REQUEST_CODE -> {// If request is cancelled, the result arrays are empty.
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.

                    // loadSplashScreen()

                    init1()
                    startLocationUpdates()

                    //   startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))


                    /*   val permission = Manifest.permission.ACCESS_FINE_LOCATION
                       val res: Int = applicationContext.checkCallingOrSelfPermission(permission)

                       if (res == PackageManager.PERMISSION_GRANTED){

                           startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                       }
                       else{


                           checkLocationPermission()


                       }*/







                } else {






                    /*   val permission = Manifest.permission.ACCESS_FINE_LOCATION
                       val res: Int = applicationContext.checkCallingOrSelfPermission(permission)

                       if (res == PackageManager.PERMISSION_GRANTED){

                           startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                       }
                       else{


                           checkLocationPermission()


                       }*/





                    //  loadSplashScreen()

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.



                }
                return
            }




        }
    }



    private fun init1() {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        mSettingsClient = LocationServices.getSettingsClient(this)
        mLocationCallback = object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult) {
                super.onLocationResult(p0)
                // location is received

                mCurrentLocation = p0.lastLocation


                updateLocationUI()
            }
        }
        mRequestingLocationUpdates = false
        mLocationRequest = LocationRequest()
        mLocationRequest!!.fastestInterval =
            FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS
        mLocationRequest!!.priority =
            com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
        val builder = LocationSettingsRequest.Builder()
        builder.addLocationRequest(mLocationRequest!!)
        mLocationSettingsRequest = builder.build()


    }

    private fun startLocationUpdates() {
        mSettingsClient
            ?.checkLocationSettings(mLocationSettingsRequest!!)
            ?.addOnSuccessListener(this) {
                Log.i(ContentValues.TAG, "All location settings are satisfied.")

                if (ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return@addOnSuccessListener
                }
                mFusedLocationClient!!.requestLocationUpdates(
                    mLocationRequest!!,
                    mLocationCallback!!, Looper.myLooper()!!
                )
                updateLocationUI()
            }
            ?.addOnFailureListener(this) { e ->
                val statusCode = (e as ApiException).statusCode
                when (statusCode) {
                    LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> {
                        /*   Log.i(
                               ContentValues.TAG,
                               "Location settings are not satisfied. Attempting to upgrade " +
                                       "location settings "
                           )*/
                        try {


                            val locationManager: LocationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

                            if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {



                                buildAlertMessageNoGps();
                            }








                            // Show the dialog by calling startResolutionForResult(), and check the
                            // result in onActivityResult().
                            /* val rae = e as ResolvableApiException
                             rae.startResolutionForResult(
                                 this,
                                 REQUEST_CHECK_SETTINGS
                             )*/

                        } catch (sie: IntentSender.SendIntentException) {
                            Log.i(ContentValues.TAG, "PendingIntent unable to execute request.")
                        }
                    }
                    LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {
                        val errorMessage = "Location settings are inadequate, and cannot be " +
                                "fixed here. Fix in Settings."
                        Log.e(ContentValues.TAG, errorMessage)
                        Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
                    }
                }
                updateLocationUI()
            }
    }


    private fun updateLocationUI() {
        if (mCurrentLocation != null) {
            latitude = mCurrentLocation!!.latitude
            longitude = mCurrentLocation!!.longitude

            println("==latitude===$latitude")
            println("==longi===$longitude")
            // ToastCustomClass(this, latitude.toString())


            //loadSplashScreen()


        }
        /* val geocoder = Geocoder(this)
         try {
             val addresses: List<Address?>? = geocoder.getFromLocation(latitude, longitude, 1)
             //  if (geocoder.equals("")) {
             val stringBuilder = StringBuilder()
             if (addresses!!.size > 0) {
                 val returnAddress = addresses[0]
                 val localityString: String? = returnAddress!!.locality
                 val name: String? = returnAddress.featureName
                 val subLocality: String? = returnAddress.subLocality
                 val country: String? = returnAddress.countryName
                 val region_code: String? = returnAddress.countryCode
                 val zipcode: String? = returnAddress.postalCode
                 val state: String? = returnAddress.adminArea
                 val addline: String? = returnAddress.getAddressLine(0)

                // val position = LatLng(latitude, longitude)






             }
             //}
         } catch (e: IOException) {
             e.printStackTrace()
         }*/
    }


    private fun buildAlertMessageNoGps() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
            .setCancelable(false)
            .setPositiveButton("Yes",
                DialogInterface.OnClickListener { dialog, id ->

                    val permission = Manifest.permission.ACCESS_FINE_LOCATION
                    val res: Int = applicationContext.checkCallingOrSelfPermission(permission)

                    if (res == PackageManager.PERMISSION_GRANTED){

                        startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                    }
                    else{


                        checkLocationPermission()


                    }


                    // startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))


                    /*  if (!checkLocationPermission()){

                          startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))

                      }*/



                })
            .setNegativeButton("No",
                DialogInterface.OnClickListener { dialog, id -> dialog.cancel() })
        val alert: AlertDialog = builder.create()
        alert.show()
    }












    companion object {
        const val REQUEST_CODE = 100

        private val TAG = "LocationProvider"
        private val REQUEST_PERMISSIONS_REQUEST_CODE = 101
        private const val FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS: Long = 5000
        private const val REQUEST_CHECK_SETTINGS = 100
        private const val REQ_CODE_VERSION_UPDATE = 1001
        private const val CODE_AUTHENTICATION_VERIFICATION = 1001

    }

}