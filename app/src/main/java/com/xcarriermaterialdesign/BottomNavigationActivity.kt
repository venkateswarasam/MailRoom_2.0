package com.xcarriermaterialdesign

import android.Manifest
import android.app.AlertDialog
import android.content.*
import android.content.IntentSender.SendIntentException
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.gms.common.api.*
import com.google.android.gms.location.*
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.xcarriermaterialdesign.activities.dashboard.DashboardViewModel
import com.xcarriermaterialdesign.databinding.ActivityBottomNavigationBinding
import com.xcarriermaterialdesign.model.ConfigRequest
import com.xcarriermaterialdesign.model.ConfigResponse
import com.xcarriermaterialdesign.utils.AnalyticsApplication
import com.xcarriermaterialdesign.utils.ApplicationSharedPref
import com.xcarriermaterialdesign.utils.LoadingView
import com.xcarriermaterialdesign.utils.ServiceDialog
import java.util.*
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import com.xcarriermaterialdesign.activities.settings.SettingsActivity
import com.xcarriermaterialdesign.dbmodel.CarrierDao
import com.xcarriermaterialdesign.dbmodel.CarrierPackage
import com.xcarriermaterialdesign.dbmodel.LocationDao
import com.xcarriermaterialdesign.dbmodel.LocationPackage
import com.xcarriermaterialdesign.dbmodel.ReasonDao
import com.xcarriermaterialdesign.dbmodel.ReasonPackage
import com.xcarriermaterialdesign.dbmodel.StatusDao
import com.xcarriermaterialdesign.dbmodel.StatusPackage
import com.xcarriermaterialdesign.dbmodel.StorageLocationDao
import com.xcarriermaterialdesign.dbmodel.StorageLocationPackage
import com.xcarriermaterialdesign.model.Reason
import com.xcarriermaterialdesign.model.Statuse
import com.xcarriermaterialdesign.model.StorageLocation
import com.xcarriermaterialdesign.roomdatabase.ProcessDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class BottomNavigationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBottomNavigationBinding

    private var mLocationRequest: LocationRequest? = null
    private var mFusedLocationClient: FusedLocationProviderClient? = null
    private var mSettingsClient: SettingsClient? = null

    private var mLocationSettingsRequest: LocationSettingsRequest? = null
    private var mLocationCallback: LocationCallback? = null
    private var mCurrentLocation: Location? = null
    internal var latitude = 0.0
    internal var longitude = 0.0
    private var mRequestingLocationUpdates: Boolean? = null


    val model: DashboardViewModel by viewModels()

    private lateinit var StatusDao: StatusDao


    private lateinit var carrierDao: CarrierDao
    private lateinit var reasonDao: ReasonDao
    private lateinit var storageLocationDao: StorageLocationDao
    private lateinit var locationDao: LocationDao





    var status:Status?= null
    private val neededPermissions = arrayOf(
        Manifest.permission.CAMERA
    )


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AnalyticsApplication.instance!!.setLocaleFa(this)

       // startService(Intent(applicationContext, NetWorkService::class.java))

        ApplicationSharedPref.init(this)

        binding = ActivityBottomNavigationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        model.config(this)


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


        enable()


        val db = Room.databaseBuilder(
            applicationContext,
            ProcessDatabase::class.java, "Process_database"
        ).allowMainThreadQueries().build()


        StatusDao = db.statusDao()

        reasonDao = db.reasonDao()

        locationDao = db.locationDao()

        storageLocationDao = db.storageLocationDao()

        carrierDao = db.carrierDao()



        val permission = Manifest.permission.ACCESS_FINE_LOCATION
        val res: Int = applicationContext.checkCallingOrSelfPermission(permission)

        if (res == PackageManager.PERMISSION_GRANTED){


            init1()
            startLocationUpdates()


            // startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
        }
        else{


          //  checkLocationPermission()


        }










        model.configResponse.observe(this, Observer<ConfigResponse> { item ->

            LoadingView.hideLoading()

            if (item.StatusCode == 200) {


                //Toast.makeText(this,"Forgot Successful", Toast.LENGTH_SHORT).show()



                lifecycleScope.launch(Dispatchers.IO) {

                    //  val test = item.Result.Success.StatusData.Statuses

                    val statusPList: List<StatusPackage> =
                        convertJsonToStatusEntity(item.Result.Statuses)

                    if (statusPList.isNotEmpty()) {
                        StatusDao.deleteStatusPackage()
                    }
                    StatusDao.insertStatusPackage(statusPList)



                    // carrier list


                    val carrierPList: List<CarrierPackage> =
                        convertJsonToCarrierEntity(item.Result.Carriers)

                    if (carrierPList.isNotEmpty()) {
                        carrierDao.deleteCarrierPackage()
                    }
                    carrierDao.insertCarrierPackage(carrierPList)


                    // reasons list

                    val reasonPList: List<ReasonPackage> =
                        convertJsonToReasonsEntity(item.Result.Reasons)

                    if (reasonPList.isNotEmpty()) {
                        reasonDao.deleteReasonPackage()
                    }
                    reasonDao.insertReasonPackage(reasonPList)



                    //storage locations

                    val storagelocationPList: List<StorageLocationPackage> =
                        convertJsonToStoragelocationEntity(item.Result.StorageLocations)

                    if (storagelocationPList.isNotEmpty()) {
                        storageLocationDao.deleteStorageLocationPackage()
                    }
                    storageLocationDao.insertStorageLocationPackage(storagelocationPList)

                    //locations

                    val locationsPList: List<LocationPackage> =
                        convertJsonToLocationEntity(item.Result.Locations)

                    if (locationsPList.isNotEmpty()) {
                        locationDao.deleteLocationPackage()
                    }
                    locationDao.insertLocationPackage(locationsPList)












                }
















                return@Observer

            }

            else{

                ServiceDialog.ShowDialog(this, getString(R.string.servererror))


                /*SweetAlertDialog(
                    this,
                    SweetAlertDialog.ERROR_TYPE
                ).setTitleText("Error").setContentText(item.Message)
                    .show()*/

                return@Observer
            }






        })




        val configRequest = ConfigRequest(ApplicationSharedPref.read(ApplicationSharedPref.COMPANY_ID,"")!!,
            ApplicationSharedPref.read(ApplicationSharedPref.MS_EMAIL,"")!!,
            ApplicationSharedPref.read(ApplicationSharedPref.MS_PASSWORD,"")!!,
            ApplicationSharedPref.read(ApplicationSharedPref.PLANT_ID,"")!!
        )

        model.configinformation(configRequest)


        /*val refreshRequest = RefreshRequest(ApplicationSharedPref.read(ApplicationSharedPref.REFRESH_TOKEN,"")!!)

        model.refresh(refreshRequest)*/

















        //  initActionBar()
    }


    private fun convertJsonToStatusEntity(josnReceiver: List<Statuse>): List<StatusPackage> {
        var statusPackageList: MutableList<StatusPackage> = ArrayList()
        for (statuseX in josnReceiver) {

            var statusP = StatusPackage(
                StatusDescription = statuseX.StatusDescription ?: "",
                StatusCode = statuseX.StatusCode ?: ""
            )
            statusPackageList.add(statusP)
        }
        return statusPackageList
    }


    private fun convertJsonToCarrierEntity(josnReceiver: List<com.xcarriermaterialdesign.model.Carrier>): List<CarrierPackage> {
        var statusPackageList: MutableList<CarrierPackage> = ArrayList()
        for (statuseX in josnReceiver) {

            var statusP = CarrierPackage(
                CarrierDescription = statuseX.CarrierDescription ?: "",
                CarrierId = statuseX.CarrierId ?: ""
            )
            statusPackageList.add(statusP)
        }
        return statusPackageList
    }





    private fun convertJsonToReasonsEntity(josnReceiver: List<Reason>): List<ReasonPackage> {
        var statusPackageList: MutableList<ReasonPackage> = ArrayList()
        for (statuseX in josnReceiver) {

            var statusP = ReasonPackage(
                ReasonId = statuseX.ReasonId ?: "",
                ReasonDescription = statuseX.ReasonDescription ?: ""
            )
            statusPackageList.add(statusP)
        }
        return statusPackageList
    }



    private fun convertJsonToStoragelocationEntity(josnReceiver: List<StorageLocation>): List<StorageLocationPackage> {
        var statusPackageList: MutableList<StorageLocationPackage> = ArrayList()
        for (statuseX in josnReceiver) {

            var statusP = StorageLocationPackage(
                StorageLocationDescription = statuseX.StorageLocationDescription ?: "",
                StorageLocationId = statuseX.StorageLocationId ?: ""
            )
            statusPackageList.add(statusP)
        }
        return statusPackageList
    }


    private fun convertJsonToLocationEntity(josnReceiver: List<com.xcarriermaterialdesign.model.Location>): List<LocationPackage> {
        var statusPackageList: MutableList<LocationPackage> = ArrayList()
        for (statuseX in josnReceiver) {

            var statusP = LocationPackage(
                LocationId = statuseX.LocationId ?: "",
                LocationName = (statuseX.LocationName ?: "")
            )
            statusPackageList.add(statusP)
        }
        return statusPackageList
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

     //   enable()


        val locationManager: LocationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        /*if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {

            // buildAlertMessageNoGps();
        }
        else{

            init1()
            startLocationUpdates()

        }*/
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


                             //   enable()

                               // buildAlertMessageNoGps();
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







    private fun enable(){

        val googleApiClient = GoogleApiClient.Builder(this)
            .addApi(LocationServices.API).build()
        googleApiClient.connect()

        val locationRequest = LocationRequest.create()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 10000
        locationRequest.fastestInterval = 10000 / 2

        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
        builder.setAlwaysShow(true)


        val result: PendingResult<LocationSettingsResult> =
            LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build())
        result.setResultCallback { result ->


             status = result.status


            when (status!!.statusCode) {

                LocationSettingsStatusCodes.SUCCESS ->{

                 //   Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show()

                    Log.i(
                        TAG,
                        "All location settings are satisfied."
                    )


                    val permission = Manifest.permission.ACCESS_FINE_LOCATION
                    val res: Int = applicationContext.checkCallingOrSelfPermission(permission)

                    if (res == PackageManager.PERMISSION_GRANTED){


                        init1()
                        startLocationUpdates()


                        // startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                    }
                    else{


                        checkLocationPermission()

                        // checkLocationPermission()


                    }





                }





                LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> {
                    Log.i(
                        TAG,
                        "Location settings are not satisfied. Show the user a dialog to upgrade location settings "
                    )
                   try {
                        // Show the dialog by calling startResolutionForResult(), and check the result
                        // in onActivityResult().



                       status?.startResolutionForResult(
                           this@BottomNavigationActivity,
                           REQUEST_CHECK_SETTINGS1
                       )



/*

                       val builder: AlertDialog.Builder = AlertDialog.Builder(this)
                       builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                           .setCancelable(false)
                           .setPositiveButton("Yes",
                               DialogInterface.OnClickListener { dialog, id ->






                               })
                           .setNegativeButton("No",
                               DialogInterface.OnClickListener { dialog, id -> dialog.cancel() })
                       val alert: AlertDialog = builder.create()
                       alert.show()

*/









                     //  buildAlertMessageNoGps()

                    } catch (e: SendIntentException) {
                        Log.i(TAG, "PendingIntent unable to execute request.")
                    }
                }
                LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE ->{

                  //  builder.setAlwaysShow(false)

                    Log.i(
                        TAG,
                        "Location settings are inadequate, and cannot be fixed here. Dialog not created."


                    )

                }

            }
        }

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)


        when(requestCode){

            REQUEST_CHECK_SETTINGS1->{
                if (resultCode == RESULT_CANCELED){


                  //  Toast.makeText(this, "cancelled dialog", Toast.LENGTH_SHORT).show()
                    //  status?.isCanceled
                }

                else if (resultCode == RESULT_OK){



                    val permission = Manifest.permission.ACCESS_FINE_LOCATION
                    val res: Int = applicationContext.checkCallingOrSelfPermission(permission)

                    if (res == PackageManager.PERMISSION_GRANTED){


                        init1()
                        startLocationUpdates()


                        // startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                    }
                    else{


                        checkLocationPermission()



                    }

                }

            }


        }



    }




    companion object {
        const val REQUEST_CODE = 1000

        private val TAG = "LocationProvider"
        private val REQUEST_PERMISSIONS_REQUEST_CODE = 101
        private const val FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS: Long = 5000
        private const val REQUEST_CHECK_SETTINGS = 100
        private const val REQUEST_CHECK_SETTINGS1 = 100
        private const val REQ_CODE_VERSION_UPDATE = 1001
        private const val CODE_AUTHENTICATION_VERIFICATION = 1001

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