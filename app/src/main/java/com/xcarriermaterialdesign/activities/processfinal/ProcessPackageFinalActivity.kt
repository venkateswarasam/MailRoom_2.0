package com.xcarriermaterialdesign.activities.processfinal

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.AdapterView.OnItemClickListener
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.room.Room
import com.xcarriermaterialdesign.BottomNavigationActivity
import com.xcarriermaterialdesign.BuildConfig
import com.xcarriermaterialdesign.R
import com.xcarriermaterialdesign.activities.camera.CameraActivity
import com.xcarriermaterialdesign.activities.scanner.SimpleScannerActivity
import com.xcarriermaterialdesign.activities.signature.SigantureviewActivity
import com.xcarriermaterialdesign.databinding.ActivityProcessPackageFinalBinding
import com.xcarriermaterialdesign.dbmodel.LocationDao
import com.xcarriermaterialdesign.dbmodel.ReasonDao
import com.xcarriermaterialdesign.dbmodel.StatusDao
import com.xcarriermaterialdesign.dbmodel.StorageLocationDao
import com.xcarriermaterialdesign.model.CheckPackage
import com.xcarriermaterialdesign.roomdatabase.BulkDao
import com.xcarriermaterialdesign.roomdatabase.CamerDao
import com.xcarriermaterialdesign.roomdatabase.ProcessDao
import com.xcarriermaterialdesign.roomdatabase.ProcessDatabase
import com.xcarriermaterialdesign.roomdatabase.ProcessPackage
import com.xcarriermaterialdesign.roomdatabase.TrackingDao
import com.xcarriermaterialdesign.utils.AnalyticsApplication
import com.xcarriermaterialdesign.utils.ApplicationSharedPref
import com.xcarriermaterialdesign.utils.DWUtilities
import com.xcarriermaterialdesign.utils.LoadingView
import com.xcarriermaterialdesign.utils.NetWorkService
import com.xcarriermaterialdesign.utils.NetworkChangeReceiver
import com.xcarriermaterialdesign.utils.NetworkConnection
import com.xcarriermaterialdesign.utils.ServiceDialog
import org.jetbrains.anko.toast
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date


class ProcessPackageFinalActivity : AppCompatActivity(), NetworkChangeReceiver.NetCheckerReceiverListener {




    private lateinit var binding: ActivityProcessPackageFinalBinding


    val model: ProcessPackageFinalViewModel by viewModels()


    private lateinit var processDao: ProcessDao
    private lateinit var trackingDao: TrackingDao
    private lateinit var bulkDao: BulkDao
    private lateinit var statusDao: StatusDao
    private lateinit var camerDao: CamerDao


    private lateinit var reasonDao: ReasonDao
    private lateinit var locationDao: LocationDao
    private lateinit var storageLocationDao: StorageLocationDao

    var statusCode : String = ""
    var reasonCode : String = ""
    var storageCode : String = ""


    // update package
    var trueCount = 0
    var falseCount = 0




    private lateinit var processPackage: List<ProcessPackage>


    private  var statusArray = mutableListOf<String>()

    private  var reasonArray = mutableListOf<String>()
    private  var locationArray = mutableListOf<String>()
    private  var storagelocationArray = mutableListOf<String>()




    var digitalSignBase64Str : String = ""

    var image1 :String = ""
    var image2 :String = ""
    var image3 :String = ""



    var packagestatus:String = ""
    var scanType = 0
    val storage = 1
    val lock = 2
    val bin = 3
    val buil = 4
    val route = 5
    val track = 0
    val mail = 6
    val doc = 7


    var checkPackages: List<CheckPackage>?= null


    var digitalSignResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult())
        { result ->
            if (result.resultCode == RESULT_OK){
                if (result.data?.hasExtra("RESULT_TEXT")!!){
                    val text = result.data!!.extras?.getString("RESULT_TEXT")
                        ?: "No Result Provided"

                    val bitmapIm = AnalyticsApplication.instance?.getBitmapSign()


//                    val scaledBitmap: Bitmap = Bitmap.createScaledBitmap(bitmapIm!!, 100, 100, true)
//                    val mCanvas = Canvas(scaledBitmap)






                    if (bitmapIm!= null){

                        binding.signature.visibility = View.GONE
                        binding.signImage.visibility = View.VISIBLE


                       // binding.signImage.setImageBitmap(Bitmap.createScaledBitmap(bitmapIm, 1000, 1000, false));


                        binding.signImage.setImageBitmap(bitmapIm)



                    }


                    // println("==bitmap==${result.data!!.hasExtra("RESULT_TEXT")}")

                   digitalSignBase64Str = AnalyticsApplication.instance?.getDigitalSignBase64()!!

                    println("==proceesdign==$digitalSignBase64Str")


                }
            }
        }


    override fun onBackPressed() {


        processDao.deleteAllProcessPackages()
        trackingDao.deleteAllProcessPackages()
        camerDao.deleteAllBulkPackages()


        val intent = Intent(this, BottomNavigationActivity::class.java)

        startActivity(intent)
        finish()

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
     //   setContentView(R.layout.activity_process_package_final)

        model.config(this)
        supportActionBar?.hide()


        binding = DataBindingUtil.setContentView(this, R.layout.activity_process_package_final)


        val intent = intent

         checkPackages = intent.getSerializableExtra("checklist") as List<CheckPackage>


        // true and false count



        for (i in 0 until checkPackages!!.size) {
            val packageData: CheckPackage = checkPackages!![i]
            val checkExist = packageData.CheckExist
            if (checkExist) {
                trueCount++
            } else {
                falseCount++
            }
        }



        // Print the counts
        System.out.println("Number of 'true' values: $trueCount");
        System.out.println("Number of 'false' values: $falseCount");












        //intent.getBooleanExtra("bulkflag", false)


        println("==boolean==${intent.getBooleanExtra("bulkflag",false)}")


        DWUtilities.CreateDWProfile(this, resources.getString(R.string.activity_intent_filter_action5),"true")


        startService(Intent(applicationContext, NetWorkService::class.java))

        if (!NetworkConnection().isNetworkAvailable(this)) {

            binding.profile.setImageResource(R.drawable.syncyellow)

        }
        else{

            binding.profile.setImageResource(R.drawable.syncnew)

        }





        val db = Room.databaseBuilder(
            applicationContext,
            ProcessDatabase::class.java, "Process_database"
        ).allowMainThreadQueries().build()

        processDao = db.processDao()
        trackingDao = db.TrackingDao()
        bulkDao = db.bulkDao()
        statusDao = db.statusDao()
        camerDao = db.cameraDao()

        reasonDao = db.reasonDao()
        locationDao = db.locationDao()
        storageLocationDao = db.storageLocationDao()


        processPackage = processDao.getAllProcessPackages()

        statusDao.getAllStatusPackages().forEach{

            statusArray.add(it.StatusDescription)
        }



        reasonDao.getAllReasonPackages().forEach{

            reasonArray.add(it.ReasonDescription)
        }

        locationDao.getAllLocationPackages().forEach{

            locationArray.add(it.LocationName)
        }


        storageLocationDao.getAllStorageLocationPackages().forEach{

            storagelocationArray.add(it.StorageLocationDescription)
        }






        binding.toolbar.setNavigationOnClickListener {

            processDao.deleteAllProcessPackages()
            trackingDao.deleteAllProcessPackages()

            val intent = Intent(this, BottomNavigationActivity::class.java)

            startActivity(intent)
            finish()

        }

        binding.backbutton.setOnClickListener {

            processDao.deleteAllProcessPackages()
            trackingDao.deleteAllProcessPackages()

            val intent = Intent(this, BottomNavigationActivity::class.java)

            startActivity(intent)
            finish()
        }


        ArrayAdapter(this, android.R.layout.simple_list_item_1, statusArray).also {
                adapter ->
            binding.dropdownItem.setAdapter(adapter)
        }


        ArrayAdapter(this, android.R.layout.simple_list_item_1, reasonArray).also {
                adapter ->
            binding.reasonItem.setAdapter(adapter)
        }



        ArrayAdapter(this, android.R.layout.simple_list_item_1, locationArray).also {
                adapter ->
            binding.locItem1.setAdapter(adapter)
        }


        ArrayAdapter(this, android.R.layout.simple_list_item_1, locationArray).also {
                adapter ->
            binding.locItem.setAdapter(adapter)
        }


        ArrayAdapter(this, android.R.layout.simple_list_item_1, storagelocationArray).also {
                adapter ->
            binding.storageItem.setAdapter(adapter)
        }


        binding.dropdownItem.onItemClickListener =
            OnItemClickListener { parent, arg1, position, arg3 ->
                val item = parent.getItemAtPosition(position)

                when(item){

                    "Received"->{


                        binding.buildingLay.visibility = View.VISIBLE
                        binding.mailstopLay.visibility = View.VISIBLE
                        binding.stoargelay.visibility = View.VISIBLE
                        binding.binLay.visibility = View.VISIBLE
                        binding.dock.visibility = View.VISIBLE
                        binding.signedby.visibility = View.VISIBLE
                        binding.signhere.visibility = View.VISIBLE
                        binding.noteshere.visibility = View.VISIBLE
                        binding.captureImages.visibility = View.VISIBLE

                        binding.bottomactionlay.visibility = View.VISIBLE
                        binding.savechanges.visibility = View.VISIBLE

                        binding.signature.text = getString(R.string.signedhere)


                        binding.locatondrop.visibility = View.GONE
                        binding.locatondrop1.visibility = View.GONE
                        binding.imagesLayout.visibility = View.GONE


                        binding.drivername.visibility = View.GONE
                        binding.carriersignature.visibility = View.GONE
                        binding.route.visibility = View.VISIBLE
                        binding.locker.visibility = View.VISIBLE
                        binding.reasonLay.visibility = View.GONE
                        binding.required.visibility = View.GONE


                    }

                    "Delivered to Location"->{



                        binding.buildingLay.visibility = View.VISIBLE
                        binding.locatondrop.visibility = View.VISIBLE
                        binding.locatondrop1.visibility = View.GONE
                        binding.signhere.visibility = View.VISIBLE
                        binding.signedby.visibility = View.VISIBLE
                        binding.noteshere.visibility = View.VISIBLE
                        binding.savechanges.visibility = View.VISIBLE
                        binding.bottomactionlay.visibility = View.VISIBLE
                        binding.captureImages.visibility = View.VISIBLE
                        binding.signature.text = getString(R.string.signedhere)


                        binding.mailstopLay.visibility = View.GONE
                        binding.imagesLayout.visibility = View.GONE
                        binding.stoargelay.visibility = View.GONE
                        binding.binLay.visibility = View.GONE
                        binding.dock.visibility = View.GONE
                        binding.drivername.visibility = View.GONE
                        binding.carriersignature.visibility = View.GONE
                        binding.route.visibility = View.GONE
                        binding.locker.visibility = View.GONE
                        binding.reasonLay.visibility = View.GONE
                        binding.required.visibility = View.GONE


                    }

                    "HandOff To Carrier"->{


                        binding.buildingLay.visibility = View.VISIBLE
                        binding.dock.visibility = View.VISIBLE
                        binding.locatondrop.visibility = View.VISIBLE
                        binding.drivername.visibility = View.VISIBLE
                        binding.signhere.visibility = View.VISIBLE

                        binding.signature.text = getString(R.string.carrersign)

                        binding.savechanges.visibility = View.VISIBLE
                        binding.bottomactionlay.visibility = View.VISIBLE
                        binding.captureImages.visibility = View.VISIBLE
                        binding.imagesLayout.visibility = View.GONE

                        binding.locatondrop1.visibility = View.GONE

                        binding.mailstopLay.visibility = View.GONE
                        binding.stoargelay.visibility = View.GONE
                        binding.binLay.visibility = View.GONE
                        binding.signedby.visibility = View.GONE
                       // binding.signhere.visibility = View.GONE
                        binding.noteshere.visibility = View.GONE
                        binding.route.visibility = View.GONE
                        binding.locker.visibility = View.GONE
                        binding.reasonLay.visibility = View.GONE
                        binding.required.visibility = View.GONE



                    }

                    "Return to Requestor"->{




                        binding.reasonLay.visibility = View.VISIBLE
                        binding.required.visibility = View.VISIBLE
                        binding.buildingLay.visibility = View.VISIBLE
                        binding.dock.visibility = View.VISIBLE
                        binding.locatondrop.visibility = View.GONE
                        binding.locatondrop1.visibility = View.VISIBLE
                        binding.mailstopLay.visibility = View.VISIBLE
                        binding.stoargelay.visibility = View.VISIBLE
                        binding.binLay.visibility = View.VISIBLE
                        binding.route.visibility = View.VISIBLE
                        binding.locker.visibility = View.VISIBLE
                        binding.noteshere.visibility = View.VISIBLE
                        binding.savechanges.visibility = View.VISIBLE
                        binding.bottomactionlay.visibility = View.VISIBLE


                        binding.drivername.visibility = View.GONE
                        binding.captureImages.visibility = View.GONE
                        binding.imagesLayout.visibility = View.GONE
                        binding.carriersignature.visibility = View.GONE
                        binding.signedby.visibility = View.GONE
                        binding.signhere.visibility = View.GONE



                    }

                    "Return to Sender"->{




                        binding.reasonLay.visibility = View.VISIBLE
                        binding.required.visibility = View.VISIBLE
                        binding.buildingLay.visibility = View.VISIBLE
                        binding.dock.visibility = View.VISIBLE
                        binding.locatondrop.visibility = View.GONE
                        binding.locatondrop1.visibility = View.VISIBLE
                        binding.mailstopLay.visibility = View.VISIBLE
                        binding.stoargelay.visibility = View.VISIBLE
                        binding.binLay.visibility = View.VISIBLE
                        binding.route.visibility = View.VISIBLE
                        binding.locker.visibility = View.VISIBLE
                        binding.noteshere.visibility = View.VISIBLE
                        binding.savechanges.visibility = View.VISIBLE
                        binding.bottomactionlay.visibility = View.VISIBLE


                        binding.drivername.visibility = View.GONE
                        binding.captureImages.visibility = View.GONE
                        binding.imagesLayout.visibility = View.GONE
                        binding.carriersignature.visibility = View.GONE
                        binding.signedby.visibility = View.GONE
                        binding.signhere.visibility = View.GONE



                    }


                    "Delivered to Locker"->{



                        binding.buildingLay.visibility = View.VISIBLE
                        binding.locatondrop.visibility = View.VISIBLE
                        binding.locatondrop1.visibility = View.GONE
                        binding.signhere.visibility = View.VISIBLE
                        binding.signedby.visibility = View.VISIBLE
                        binding.noteshere.visibility = View.VISIBLE
                        binding.savechanges.visibility = View.VISIBLE
                        binding.bottomactionlay.visibility = View.VISIBLE
                        binding.captureImages.visibility = View.VISIBLE
                        binding.signature.text = getString(R.string.signedhere)


                        binding.mailstopLay.visibility = View.GONE
                        binding.imagesLayout.visibility = View.GONE
                        binding.stoargelay.visibility = View.GONE
                        binding.binLay.visibility = View.GONE
                        binding.dock.visibility = View.GONE
                        binding.drivername.visibility = View.GONE
                        binding.carriersignature.visibility = View.GONE
                        binding.route.visibility = View.GONE
                        binding.locker.visibility = View.GONE
                        binding.reasonLay.visibility = View.GONE
                        binding.required.visibility = View.GONE


                    }

                    "Delivered to Person"->{



                        binding.buildingLay.visibility = View.VISIBLE
                        binding.locatondrop.visibility = View.VISIBLE
                        binding.locatondrop1.visibility = View.GONE
                        binding.signhere.visibility = View.VISIBLE
                        binding.signedby.visibility = View.VISIBLE
                        binding.noteshere.visibility = View.VISIBLE
                        binding.savechanges.visibility = View.VISIBLE
                        binding.bottomactionlay.visibility = View.VISIBLE
                        binding.captureImages.visibility = View.VISIBLE
                        binding.signature.text = getString(R.string.signedhere)


                        binding.mailstopLay.visibility = View.GONE
                        binding.imagesLayout.visibility = View.GONE
                        binding.stoargelay.visibility = View.GONE
                        binding.binLay.visibility = View.GONE
                        binding.dock.visibility = View.GONE
                        binding.drivername.visibility = View.GONE
                        binding.carriersignature.visibility = View.GONE
                        binding.route.visibility = View.GONE
                        binding.locker.visibility = View.GONE
                        binding.reasonLay.visibility = View.GONE
                        binding.required.visibility = View.GONE


                    }

                    "Delivered Delayed"->{



                        binding.buildingLay.visibility = View.VISIBLE
                        binding.locatondrop.visibility = View.VISIBLE
                        binding.locatondrop1.visibility = View.GONE
                        binding.signhere.visibility = View.VISIBLE
                        binding.signedby.visibility = View.VISIBLE
                        binding.noteshere.visibility = View.VISIBLE
                        binding.savechanges.visibility = View.VISIBLE
                        binding.bottomactionlay.visibility = View.VISIBLE
                        binding.captureImages.visibility = View.VISIBLE
                        binding.signature.text = getString(R.string.signedhere)


                        binding.mailstopLay.visibility = View.GONE
                        binding.imagesLayout.visibility = View.GONE
                        binding.stoargelay.visibility = View.GONE
                        binding.binLay.visibility = View.GONE
                        binding.dock.visibility = View.GONE
                        binding.drivername.visibility = View.GONE
                        binding.carriersignature.visibility = View.GONE
                        binding.route.visibility = View.GONE
                        binding.locker.visibility = View.GONE
                        binding.reasonLay.visibility = View.GONE
                        binding.required.visibility = View.GONE


                    }

                    else->{

                        binding.buildingLay.visibility = View.VISIBLE
                        binding.mailstopLay.visibility = View.VISIBLE
                        binding.stoargelay.visibility = View.VISIBLE
                        binding.binLay.visibility = View.VISIBLE
                        binding.dock.visibility = View.VISIBLE
                        binding.signedby.visibility = View.VISIBLE
                        binding.signhere.visibility = View.VISIBLE
                        binding.noteshere.visibility = View.VISIBLE
                        binding.captureImages.visibility = View.VISIBLE

                        binding.bottomactionlay.visibility = View.VISIBLE
                        binding.savechanges.visibility = View.VISIBLE

                        binding.signature.text = getString(R.string.signedhere)


                        binding.locatondrop.visibility = View.GONE
                        binding.locatondrop1.visibility = View.GONE
                        binding.imagesLayout.visibility = View.GONE


                        binding.drivername.visibility = View.GONE
                        binding.carriersignature.visibility = View.GONE
                        binding.route.visibility = View.VISIBLE
                        binding.locker.visibility = View.VISIBLE
                        binding.reasonLay.visibility = View.GONE
                        binding.required.visibility = View.GONE


                    }


                }


                packagestatus = item.toString()

            }



        binding.savechanges.setOnClickListener {





             image1 = ApplicationSharedPref.read(ApplicationSharedPref.IMAGE1,"")!!
             image2 = ApplicationSharedPref.read(ApplicationSharedPref.IMAGE2,"")!!
             image3 = ApplicationSharedPref.read(ApplicationSharedPref.IMAGE3,"")!!





            val packageImages = PackageImages(
                image1,
                image2,
                image3,
                digitalSignBase64Str)


            val predicatedResult =  statusDao.getAllStatusPackages()?.filter { it.StatusDescription == binding.dropdownItem.text.toString()}


            statusCode = try {


                predicatedResult!![0].StatusCode?:""

            } catch (e:java.lang.IndexOutOfBoundsException){


                ""

            }


            val predicatedResult_reason =  reasonDao.getAllReasonPackages()?.filter { it.ReasonDescription == binding.reasonItem.text.toString()}




            reasonCode = try {


                predicatedResult_reason!![0].ReasonId?:""

            } catch (e:java.lang.IndexOutOfBoundsException){


                ""

            }


            val predicatedResult_storage =  storageLocationDao.getAllStorageLocationPackages()?.filter { it.StorageLocationDescription == binding.storageItem.text.toString()}




            storageCode = try {


                predicatedResult_storage!![0].StorageLocationId?:""

            } catch (e:java.lang.IndexOutOfBoundsException){


                ""

            }


            if (trueCount==0){



                val createUpdateRequest = CreateUpdateRequest("Android",
                    BuildConfig.VERSION_NAME,
                    binding.bin.text.toString(),
                    binding.buildingname.text.toString(),
                    intent.getBooleanExtra("bulkflag", false),
                    "",
                    ApplicationSharedPref.read(ApplicationSharedPref.COMPANY_ID,"")!!,
                    ApplicationSharedPref.read(ApplicationSharedPref.LOGINID,"")!!,
                    SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date()),
                    Build.MODEL,
                    binding.docnumber.text.toString(),
                    binding.drivertext.text.toString(),
                    ApplicationSharedPref.read(ApplicationSharedPref.LATTITUDE,"17.23")!!,
                    binding.lockertext.text.toString(),
                    ApplicationSharedPref.read(ApplicationSharedPref.LONGITUDE,"72.56")!!,
                    binding.mailstop.text.toString(),
                    binding.notestext.text.toString(),
                    Build.VERSION.RELEASE,
                    ApplicationSharedPref.read(ApplicationSharedPref.PLANT_ID,"")!!,
                    reasonCode,
                    binding.routeText.text.toString(),
                    statusCode,
                    storageCode,
                    intent.getSerializableExtra("checklist") as List<CheckPackage>,
                    packageImages = packageImages,binding.signname.text.toString())


                println("==createrequest==$createUpdateRequest")




                model.createpackage(createUpdateRequest)


            }

            else{

                val createUpdateRequest = UpdatePackageRequest("Android",
                    BuildConfig.VERSION_NAME,
                    binding.bin.text.toString(),
                    binding.buildingname.text.toString(),
                    intent.getBooleanExtra("bulkflag", false),
                    "",
                    ApplicationSharedPref.read(ApplicationSharedPref.COMPANY_ID,"")!!,
                    Build.MODEL,
                    binding.docnumber.text.toString(),
                    binding.drivertext.text.toString(),
                    ApplicationSharedPref.read(ApplicationSharedPref.LATTITUDE,"17.23")!!,

                    binding.lockertext.text.toString(),

                    ApplicationSharedPref.read(ApplicationSharedPref.LONGITUDE,"17.23")!!,
                    binding.mailstop.text.toString(),

                    binding.notestext.text.toString(),

                    Build.VERSION.RELEASE,
                    ApplicationSharedPref.read(ApplicationSharedPref.PLANT_ID,"")!!,

                    reasonCode,
                    binding.routeText.text.toString(),
                    binding.signname.text.toString(),
                    statusCode,
                    storageCode,
                    ApplicationSharedPref.read(ApplicationSharedPref.LOGINID,"")!!,

                    SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date()),
                    intent.getSerializableExtra("checklist") as List<CheckPackage>,
                    packageImages
                    )


                println("==updaterequest==$createUpdateRequest")




                model.updatepackage(createUpdateRequest)


            }











        }



        binding.buildingScan.setOnClickListener {

            val intent = Intent(this, SimpleScannerActivity::class.java)

            intent.putExtra("flag","process")


           startActivityForResult(intent,54321)
        }


        binding.mailstopScan.setOnClickListener {

            val intent = Intent(this, SimpleScannerActivity::class.java)
            intent.putExtra("flag","process")


            startActivityForResult(intent,54322)
        }

        binding.binscan.setOnClickListener {

            val intent = Intent(this, SimpleScannerActivity::class.java)
            intent.putExtra("flag","process")



            startActivityForResult(intent,54323)
        }



        binding.storagescan.setOnClickListener {

            val intent = Intent(this, SimpleScannerActivity::class.java)

            intent.putExtra("flag","process")



            startActivityForResult(intent,54324)
        }


        binding.docscan.setOnClickListener {

            val intent = Intent(this, SimpleScannerActivity::class.java)

            intent.putExtra("flag","process")



            startActivityForResult(intent,54325)
        }

        binding.routescan.setOnClickListener {

            val intent = Intent(this, SimpleScannerActivity::class.java)

            intent.putExtra("flag","process")


            //   intent.putExtra("scanning","Scanning")

            startActivityForResult(intent,54326)
        }


        binding.lockerscan.setOnClickListener {

            val intent = Intent(this, SimpleScannerActivity::class.java)

            intent.putExtra("flag","process")


            //   intent.putExtra("scanning","Scanning")

            startActivityForResult(intent,54327)
        }


        var cameraActivityWithResult =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult())
            { result ->
                if (result.resultCode == RESULT_OK){

                 //   print(result.resultCode)

                    val countcheck = AnalyticsApplication.instance!!.getEmpId()

                    println("==count==$countcheck")


                    when(countcheck){

                        "1"->{


                            binding.imagesLayout.visibility = View.VISIBLE

                                binding.cardimage1.visibility = View.VISIBLE
                                binding.cardimage4.visibility = View.VISIBLE


                                binding.image1.setImageBitmap(AnalyticsApplication.instance?.getPackageImage1())

                                binding.captureImages.visibility = View.GONE

                                binding.cardimage2.visibility = View.GONE
                                binding.cardimage3.visibility = View.GONE







                        }


                        "2"->{


                            binding.imagesLayout.visibility = View.VISIBLE

                            binding.captureImages.visibility = View.GONE

                            binding.cardimage2.visibility = View.VISIBLE

                            // edited
                            binding.cardimage1.visibility = View.VISIBLE
                            binding.image1.setImageBitmap(AnalyticsApplication.instance?.getPackageImage1())
// edited
                            binding.image2.setImageBitmap(AnalyticsApplication.instance?.getPackageImage2())

                            binding.cardimage4.visibility = View.VISIBLE

                            binding.cardimage3.visibility =  View.GONE




                        }

                        "3"->{


                            binding.imagesLayout.visibility = View.VISIBLE

                            binding.captureImages.visibility = View.GONE

                            // edited

                            binding.cardimage1.visibility = View.VISIBLE
                            binding.cardimage2.visibility = View.VISIBLE

                            binding.image1.setImageBitmap(AnalyticsApplication.instance?.getPackageImage1())


                            binding.image2.setImageBitmap(AnalyticsApplication.instance?.getPackageImage2())

// edited

                            binding.cardimage3.visibility = View.VISIBLE

                            binding.image3.setImageBitmap(AnalyticsApplication.instance?.getPackageImage3())

                            binding.cardimage4.visibility = View.GONE



                            }

                        else->{

                            binding.imagesLayout.visibility = View.GONE
                          //  binding.captureImages.visibility = View.VISIBLE
                        }



                    }








                }
            }

        binding.captureImages.setOnClickListener {



        //    PreferenceManager.getDefaultSharedPreferences(this).edit().clear().apply();

            camerDao.deleteAllBulkPackages()


            var intent: Intent = Intent(this, CameraActivity::class.java)
            cameraActivityWithResult.launch(intent)


        }



        binding.cardimage4.setOnClickListener {


            camerDao.deleteAllBulkPackages()

            // PreferenceManager.getDefaultSharedPreferences(this).edit().clear().apply();

            var intent: Intent = Intent(this, CameraActivity::class.java)
            cameraActivityWithResult.launch(intent)


        }

        binding.cardimage1.setOnClickListener {

            camerDao.deleteAllBulkPackages()


            var intent: Intent = Intent(this, CameraActivity::class.java)
            cameraActivityWithResult.launch(intent)


        }

        binding.cardimage2.setOnClickListener {

            camerDao.deleteAllBulkPackages()


            var intent: Intent = Intent(this, CameraActivity::class.java)
            cameraActivityWithResult.launch(intent)


        }
        binding.cardimage3.setOnClickListener {

            camerDao.deleteAllBulkPackages()


            var intent: Intent = Intent(this, CameraActivity::class.java)
            cameraActivityWithResult.launch(intent)


        }


        binding.signhere.setOnClickListener {


         digitalSignResult.launch(Intent(this, SigantureviewActivity::class.java))




        }


        binding.signature.setOnClickListener {


            digitalSignResult.launch(Intent(this, SigantureviewActivity::class.java))




        }


        binding.bin.setOnFocusChangeListener { _,hasFocus ->
            scanType = if(hasFocus){
                bin
            }else{
                track
            }

        }

        binding.buildingname.setOnFocusChangeListener { _,hasFocus ->
            scanType = if(hasFocus){
                buil
            }else{
                track
            }

        }

        binding.mailstop.setOnFocusChangeListener { _,hasFocus ->
            scanType = if(hasFocus){
                mail
            }else{
                track
            }

        }

        binding.docnumber.setOnFocusChangeListener { _,hasFocus ->
            scanType = if(hasFocus){
                doc
            }else{
                track
            }

        }








        binding.routeText.setOnFocusChangeListener { _,hasFocus ->
            scanType = if(hasFocus){
                route
            }else{
                track
            }

        }


        binding.lockertext.setOnFocusChangeListener { _,hasFocus ->
            scanType = if(hasFocus){
                lock
            }else{
                track
            }

        }







        // response handling

        model.checkinoutResponse.observe(this, Observer<CreateUpdateResponse> { item ->

            LoadingView.hideLoading()

            println("==statuscode${item.StatusCode}")


            if (item.StatusCode == 200){


                toast(item.Result.ReturnMsg)

              //  Toast.makeText(this, item.Result.ReturnMsg, Toast.LENGTH_SHORT).show()




                ApplicationSharedPref.write(ApplicationSharedPref.IMAGE1,"")
                ApplicationSharedPref.write(ApplicationSharedPref.IMAGE2,"")
                ApplicationSharedPref.write(ApplicationSharedPref.IMAGE3,"")

                onBackPressed()

            }

            else{



                ServiceDialog.ShowDialog(this, item.Result.ReturnMsg)
            }












        })




    }



    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        displayScanResult(intent)
    }

    private fun displayScanResult(scanIntent: Intent) {
        val decodedSource =
            scanIntent.getStringExtra(resources.getString(R.string.datawedge_intent_key_source))
        val decodedData =
            scanIntent.getStringExtra(resources.getString(R.string.datawedge_intent_key_data))
        val decodedLabelType =
            scanIntent.getStringExtra(resources.getString(R.string.datawedge_intent_key_label_type))
        val scan = "$decodedData [$decodedLabelType]\n\n"
        //  output.text = scan
        // binding.tracking.setText(decodedData)


        runOnUiThread(java.lang.Runnable {


            when(scanType){

                storage -> {
                    binding.storagetext.setText(decodedData)
                }
                lock -> {
                    binding.lockertext.setText(decodedData)

                }
                bin -> {
                    binding.bin.setText(decodedData)

                }
                buil -> {
                    binding.buildingname.setText(decodedData)

                }
                route -> {
                    binding.routeText.setText(decodedData)
                }

                mail->{

                    binding.mailstop.setText(decodedData)
                }

                doc->{

                    binding.docnumber.setText(decodedData)
                }


                else -> {


                }
            }



        })



    }

















    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when(requestCode){
            54321->{

                if (resultCode == RESULT_OK) {
                    val name = data!!.getStringExtra("barcode")
                    println("==name$name")
                    binding.buildingname!!.setText(name)
                }



            }

            54322->{

                if (resultCode == RESULT_OK) {
                    val name = data!!.getStringExtra("barcode")
                    println("==name$name")
                    binding.mailstop!!.setText(name)
                }



            }


            54323->{

                if (resultCode == RESULT_OK) {
                    val name = data!!.getStringExtra("barcode")
                    println("==name$name")
                    binding.bin!!.setText(name)
                }



            }


            54324->{

                if (resultCode == RESULT_OK) {
                    val name = data!!.getStringExtra("barcode")
                    println("==name$name")
                    binding.storagetext!!.setText(name)
                }



            }

            54325->{

                if (resultCode == RESULT_OK) {
                    val name = data!!.getStringExtra("barcode")
                    println("==name$name")
                    binding.docnumber!!.setText(name)
                }



            }

            54326->{

                if (resultCode == RESULT_OK) {
                    val name = data!!.getStringExtra("barcode")
                    println("==name$name")
                    binding.routeText!!.setText(name)
                }



            }

            54327->{

                if (resultCode == RESULT_OK) {
                    val name = data!!.getStringExtra("barcode")
                    println("==name$name")
                    binding.lockertext!!.setText(name)
                }





            }






            }
        }

    override fun onResume() {
        super.onResume()

        NetworkChangeReceiver.netConnectionCheckerReceiver = this
    }

    private fun showMessage(isConnected: Boolean) {

        if (!isConnected) {

            binding.profile.setImageResource(R.drawable.syncyellow)





        } else {


            binding.profile.setImageResource(R.drawable.syncnew)




        }


    }

    override fun onNetworkConnectionChanged(isConnected: Boolean) {

        showMessage(isConnected)

        if (isConnected){



            binding.profile.setImageResource(R.drawable.syncnew)


        }
        else{


            binding.profile.setImageResource(R.drawable.syncyellow)



        }
    }


}

