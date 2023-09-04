package com.xcarriermaterialdesign.activities.login

import android.Manifest
import android.app.AlertDialog
import android.content.*
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.provider.Settings.Secure
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.room.Room
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.location.*
import com.xcarriermaterialdesign.BottomNavigationActivity
import com.xcarriermaterialdesign.activities.forgot.ForgotPassword
import com.xcarriermaterialdesign.R
import com.xcarriermaterialdesign.WebActvity
import com.xcarriermaterialdesign.api.RetrofitClient
import com.xcarriermaterialdesign.databinding.ActivityMainBinding
import com.xcarriermaterialdesign.roomdatabase.BulkDao
import com.xcarriermaterialdesign.roomdatabase.ProcessDao
import com.xcarriermaterialdesign.roomdatabase.ProcessDatabase
import com.xcarriermaterialdesign.utils.AnalyticsApplication
import com.xcarriermaterialdesign.utils.ApplicationSharedPref
import com.xcarriermaterialdesign.utils.LoadingView
import com.xcarriermaterialdesign.utils.ServiceDialog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.json.JSONObject


class MainActivity : AppCompatActivity() {



    private lateinit var binding: ActivityMainBinding

    val model: LoginViewmodel by viewModels()


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



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AnalyticsApplication.instance!!.setLocaleFa(this)

     //   setContentView(R.layout.activity_main)
        supportActionBar?.hide();


        ApplicationSharedPref.init(this)

        model.config(this)


        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)



        if (ApplicationSharedPref.read(ApplicationSharedPref.LOGINCHECK,"").equals("true")){


            val intent = Intent(this, BottomNavigationActivity::class.java)

            startActivity(intent)
        }


        //  checkPermission()


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





       /* forgotPassword = findViewById(R.id.forgotpassword)
        login = findViewById(R.id.login)
        privacy = findViewById(R.id.privacy)*/


        val db = Room.databaseBuilder(
            applicationContext,
            ProcessDatabase::class.java, "Process_database"
        ).allowMainThreadQueries().build()

        processDao = db.processDao()
        bulkDao = db.bulkDao()

      processDao.deleteAllProcessPackages()
     //   bulkDao.deleteAllBulkPackages()

        binding.privacy!!.setOnClickListener {


            val intent = Intent(this, WebActvity::class.java)

            startActivity(intent)
        }




        binding.forgotpassword!!.setOnClickListener {


            val intent = Intent(this, ForgotPassword::class.java)

            startActivity(intent)
        }




        model.loginResponse.observe(this, Observer<LoginResponse> { item ->

            LoadingView.hideLoading()

            println("==statuscode${item.StatusCode}")

            if (item.StatusCode == 200) {

                ApplicationSharedPref.write(ApplicationSharedPref.MS_EMAIL,binding.emailtext.text.toString())
               // ApplicationSharedPref.write(ApplicationSharedPref.MS_PASSWORD,binding.password.text.toString())

                ApplicationSharedPref.write(ApplicationSharedPref.ROLEID,item.Result.MobileUserInfo.RoleId.toString())


                ApplicationSharedPref.write(ApplicationSharedPref.COMPANY_ID,item.Result.MobileUserInfo.CompanyId)
                ApplicationSharedPref.write(ApplicationSharedPref.PLANT_ID,item.Result.MobileUserInfo.PlantId)
                ApplicationSharedPref.write(ApplicationSharedPref.EMP_ID,item.Result.MobileUserInfo.EmployeeId)
                ApplicationSharedPref.write(ApplicationSharedPref.USERNAME,item.Result.MobileUserInfo.UserName)
                ApplicationSharedPref.write(ApplicationSharedPref.USERALIAS,item.Result.MobileUserInfo.UserAlias)
                ApplicationSharedPref.write(ApplicationSharedPref.USERROLE,item.Result.MobileUserInfo.UserRole)

                ApplicationSharedPref.write(ApplicationSharedPref.DEPARTMENT,item.Result.MobileUserInfo.Department)
                ApplicationSharedPref.write(ApplicationSharedPref.DESIGNATION,item.Result.MobileUserInfo.Designation)
                ApplicationSharedPref.write(ApplicationSharedPref.CUSTOMERNAME,item.Result.MobileUserInfo.CustomerName)
                ApplicationSharedPref.write(ApplicationSharedPref.PLANTNAME,item.Result.MobileUserInfo.PlantName)




                ApplicationSharedPref.write(ApplicationSharedPref.LOGINID, item.Result.MobileUserInfo.LoginId.toString())






                ApplicationSharedPref.write(ApplicationSharedPref.LOGINCHECK, "true")



                //Toast.makeText(this,"Login Successful", Toast.LENGTH_SHORT).show()


                finish()

                val intent = Intent(this, BottomNavigationActivity::class.java)
                startActivity(intent)


                return@Observer

            }

            else{



                ServiceDialog.ShowDialog(this, item.Message)




                return@Observer
            }






        })



        model.authenticateResponse.observe(this, Observer<Authenticate_Response> { item ->

            LoadingView.hideLoading()

            println("==statuscode${item.StatusCode}")




            //    Toast.makeText(this, item.StatusCode.toString(), Toast.LENGTH_SHORT).show()
            if (item.StatusCode == 200) {



                val loginrequest = LoginRequestNew(binding.emailtext.text.toString(),binding.password.text.toString())


                model.login(loginrequest = loginrequest,email = binding.emailtext.text.toString(), password = binding.password.text.toString(),
                    item.Result.TokenKey)

                ApplicationSharedPref.write(ApplicationSharedPref.TOKEN, item.Result.TokenKey)
                ApplicationSharedPref.write(ApplicationSharedPref.REFRESH_TOKEN, item.Result.RefreshTokenKey)


                return@Observer
            }



            else{





                ServiceDialog.ShowDialog(this, item.Result.ReturnMsg)




                return@Observer
            }








        })











        binding.login.setOnClickListener {

           val android_id = Secure.getString(applicationContext.contentResolver, Secure.ANDROID_ID)

            println("==deviceid==$android_id")

            val loginrequest = LoginRequest(binding.emailtext.text.toString(),binding.password.text.toString(),
            android_id,"elemica")


            println("==loginrequest==${loginrequest}")

            model.authenticate(loginrequest, binding.emailtext.text.toString(),binding.password.text.toString())




           //login_API()
            //login_service()



        }


    }













    //loginAPI call


    private fun login_API() {

        LoadingView.displayLoadingWithText(this,"Please wait", false)

        val android_id = Secure.getString(applicationContext.contentResolver, Secure.ANDROID_ID)

        println("==deviceid==$android_id")

        val request = LoginRequest(binding.emailtext.text.toString(),
            binding.password.text.toString(),android_id,"elemica"
           )


        val service = RetrofitClient().webService
        CoroutineScope(Dispatchers.IO).launch {
            try {


                val response = service.authenticate(
                    request,"API Key suchi.123")

                withContext(Dispatchers.Main) {


                    println("==response==$response")

                    when {
                        response!!.isSuccessful -> {

                         //   LoadingView.hideLoading()

                            Toast.makeText(this@MainActivity,"IN", Toast.LENGTH_SHORT).show()

                            doSome2(response.body())





                        }
                        else -> {


                            LoadingView.hideLoading()
                           // viewDialog!!.hideDialog()
                            Toast.makeText(this@MainActivity, getString(R.string.servererror), Toast.LENGTH_SHORT).show()
                        }
                    }

                }
            } catch (ex: Exception) {

                LoadingView.hideLoading()

                println("==exceptin==$ex")


                Handler(Looper.getMainLooper()).post {

                    ServiceDialog.ShowDialog(this@MainActivity, "The server encountered an internal error or misconfigurations and was unable to complete your request.")





                }

                ex.printStackTrace()
            } finally {
                Log.e("TAG", "sendRequest: ")
            }
        }
    }


    private fun doSome2(body: Authenticate_Response?) {
     // Log.e("TAG Venkat", "doSome: ${body!!.StatusCode}")

        LoadingView.hideLoading()



        if (body?.StatusCode == 200){

            Toast.makeText(this, body.Result.TokenKey, Toast.LENGTH_SHORT).show()


        }
        else if (body?.StatusCode!!.equals("401")){

            Toast.makeText(this, body.Result.toString(), Toast.LENGTH_SHORT).show()

        }









    }







    fun login_service() {


        val client = OkHttpClient().newBuilder()
            .build()

        val mediaType: MediaType = "application/json".toMediaTypeOrNull()!!


        lateinit var jsonObject1: JSONObject
        jsonObject1 = JSONObject()

        jsonObject1.put("Email","mahesh.utlapalli@elemica.com")
        jsonObject1.put("Password","123.Process")
        jsonObject1.put("DeviceId","1234567890")
        jsonObject1.put("PackageName","elemica")

        val body = RequestBody.create(
            mediaType,
            jsonObject1.toString()
        )
        val request: Request = Request.Builder()
            .url("https://mailroom-dev.myxcarrier.com/MobileAppService/api/Authenticate")
            .method("GET", body)
            .addHeader("Content-Type", "application/json")
            .addHeader("Authorization", "APIKey suchi.123")
            .build()
        val response = client.newCall(request).execute()

        println("==response==$response")
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