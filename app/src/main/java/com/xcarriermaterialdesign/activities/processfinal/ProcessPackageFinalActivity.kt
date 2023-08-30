package com.xcarriermaterialdesign.activities.processfinal

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import android.widget.AdapterView.OnItemClickListener
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import androidx.room.Room
import com.xcarriermaterialdesign.BottomNavigationActivity
import com.xcarriermaterialdesign.activities.camera.CameraActivity
import com.xcarriermaterialdesign.R
import com.xcarriermaterialdesign.databinding.ActivityProcessPackageFinalBinding
import com.xcarriermaterialdesign.dbmodel.CarrierPackage
import com.xcarriermaterialdesign.dbmodel.LocationDao
import com.xcarriermaterialdesign.dbmodel.LocationPackage
import com.xcarriermaterialdesign.dbmodel.ReasonDao
import com.xcarriermaterialdesign.dbmodel.ReasonPackage
import com.xcarriermaterialdesign.dbmodel.StatusDao
import com.xcarriermaterialdesign.dbmodel.StorageLocationDao
import com.xcarriermaterialdesign.dbmodel.StorageLocationPackage
import com.xcarriermaterialdesign.activities.signature.SigantureviewActivity
import com.xcarriermaterialdesign.roomdatabase.*
import com.xcarriermaterialdesign.activities.scanner.SimpleScannerActivity
import com.xcarriermaterialdesign.utils.AnalyticsApplication
import com.xcarriermaterialdesign.utils.DWUtilities
import com.xcarriermaterialdesign.utils.NetWorkService
import com.xcarriermaterialdesign.utils.NetworkChangeReceiver
import com.xcarriermaterialdesign.utils.NetworkConnection
import com.xcarriermaterialdesign.utils.*
import java.text.SimpleDateFormat
import java.util.*


class ProcessPackageFinalActivity : AppCompatActivity(), NetworkChangeReceiver.NetCheckerReceiverListener {




    private lateinit var binding: ActivityProcessPackageFinalBinding

    var image1Bitmap : Bitmap? = null

    val model: ProcessPackageFinalViewModel by viewModels()


    private lateinit var processDao: ProcessDao
    private lateinit var bulkDao: BulkDao
    private lateinit var statusDao: StatusDao


    private lateinit var reasonDao: ReasonDao
    private lateinit var locationDao: LocationDao
    private lateinit var storageLocationDao: StorageLocationDao





    private lateinit var processPackage: List<ProcessPackage>

    private lateinit var carrierPackage: List<CarrierPackage>
    private lateinit var reasonPackage: List<ReasonPackage>
    private lateinit var locationPackage: List<LocationPackage>
    private lateinit var storageLocationPackage: List<StorageLocationPackage>

    private  var statusArray = mutableListOf<String>()

    private  var reasonArray = mutableListOf<String>()
    private  var locationArray = mutableListOf<String>()
    private  var storagelocationArray = mutableListOf<String>()


    var bitMap1: Bitmap? = null


    var digitalSignBase64Str : String = ""

    var trackingnumbers:String = ""
    var packagestatus:String = ""
    var scanType = 0
    val storage = 1
    val lock = 2
    val bin = 3
    val buil = 4
    val route = 5
    val track = 0


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

                   /*digitalSignBase64Str =
                        AnalyticsApplication.instance?.getDigitalSignBase64().toString()

                    println("==proceesdign==$digitalSignBase64Str")

                    Log.i("msg==",digitalSignBase64Str)*/

                }
            }
        }


    override fun onBackPressed() {


        processDao.deleteAllProcessPackages()

        val intent = Intent(this, BottomNavigationActivity::class.java)

        startActivity(intent)

    }

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
     //   setContentView(R.layout.activity_process_package_final)

        model.config(this)
        supportActionBar?.hide()

        //   initactionbar()

        binding = DataBindingUtil.setContentView(this, R.layout.activity_process_package_final)



        DWUtilities.CreateDWProfile(this, resources.getString(R.string.activity_intent_filter_action2),"true")


        startService(Intent(applicationContext, NetWorkService::class.java))

        if (!NetworkConnection().isNetworkAvailable(this)) {

            binding.profile!!.setImageResource(R.drawable.syncyellow)

        }
        else{

            binding.profile!!.setImageResource(R.drawable.syncnew)

        }

        //  val dropdown_item = findViewById<AutoCompleteTextView>(R.id.dropdown_item)

    /*    val araylist:ArrayList<String> = ArrayList()
        araylist.add("Received")
        araylist.add("Delivered")
        araylist.add("In-Transit")
        araylist.add("Delivery Attempt 1")
        araylist.add("Delivery Attempt 2")
        araylist.add("Delivery Attempt 3")
        araylist.add("Handover to Carrier")
        araylist.add("Picked Up")
        araylist.add("Exception")
        araylist.add("Returned")
        araylist.add("Cancelled")
        araylist.add("Dropped Off at Front Desk")
        araylist.add("Left at Disk")
        araylist.add("Left in Office")
        araylist.add("Dropped Off at Mailroom")*/



        val db = Room.databaseBuilder(
            applicationContext,
            ProcessDatabase::class.java, "Process_database"
        ).allowMainThreadQueries().build()

        processDao = db.processDao()
        bulkDao = db.bulkDao()
        statusDao = db.statusDao()

        reasonDao = db.reasonDao()
        locationDao = db.locationDao()
        storageLocationDao = db.storageLocationDao()


        processPackage = processDao.getAllProcessPackages()

        statusDao.getAllStatusPackages()?.forEach{

            statusArray.add(it.StatusDescription)
        }



        reasonDao.getAllReasonPackages()?.forEach{

            reasonArray.add(it.ReasonDescription)
        }

        locationDao.getAllLocationPackages()?.forEach{

            locationArray.add(it.LocationName)
        }


        storageLocationDao.getAllStorageLocationPackages()?.forEach{

            storagelocationArray.add(it.StorageLocationDescription)
        }




        // binding.image1.setImageBitmap(AnalyticsApplication.instance().getPackageImage1())


        binding.toolbar.setNavigationOnClickListener {

            processDao.deleteAllProcessPackages()

            val intent = Intent(this, BottomNavigationActivity::class.java)

            startActivity(intent)

        }

        binding.backbutton.setOnClickListener {

            finish()
        }

      //  dropdownlay.setOnKeyListener=null
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


                }


                packagestatus = item.toString()

            }



        binding.savechanges.setOnClickListener {

         //   processDao.deleteAllProcessPackages()

            for (element in processPackage){

              //  trackingnumbers = element.trackingNumber

                val enDate = SimpleDateFormat("EEE MMM d yyyy • HH:mm aa", Locale("en"))

                val date: String = enDate.format(Calendar.getInstance().time)


                bulkDao.insertBulkPackage(BulkPackage(element.trackingNumber,packagestatus, date,binding.routeText.text.toString(),
                    binding.bin.text.toString(),element.carriername))

            }

            processDao.deleteAllProcessPackages()

            Toast.makeText(this, "Data saved successfully", Toast.LENGTH_SHORT).show()




            val intent = Intent(this, BottomNavigationActivity::class.java)

            startActivity(intent)
        }



        binding.buildingScan.setOnClickListener {

            val intent = Intent(this, SimpleScannerActivity::class.java)

            intent.putExtra("flag","process")

           // startActivity(intent)

         //   intent.putExtra("scanning","Scanning")

           startActivityForResult(intent,54321)
        }


        binding.mailstopScan.setOnClickListener {

            val intent = Intent(this, SimpleScannerActivity::class.java)
            intent.putExtra("flag","process")

            //   intent.putExtra("scanning","Scanning")

            startActivityForResult(intent,54322)
        }

        binding.binscan.setOnClickListener {

            val intent = Intent(this, SimpleScannerActivity::class.java)
            intent.putExtra("flag","process")


            //   intent.putExtra("scanning","Scanning")

            startActivityForResult(intent,54323)
        }



        binding.storagescan.setOnClickListener {

            val intent = Intent(this, SimpleScannerActivity::class.java)

            intent.putExtra("flag","process")


            //   intent.putExtra("scanning","Scanning")

            startActivityForResult(intent,54324)
        }


        binding.docscan.setOnClickListener {

            val intent = Intent(this, SimpleScannerActivity::class.java)

            intent.putExtra("flag","process")


            //   intent.putExtra("scanning","Scanning")

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






                 //  Toast.makeText(this, countcheck, Toast.LENGTH_SHORT).show()

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



            var intent: Intent = Intent(this, CameraActivity::class.java)
            cameraActivityWithResult.launch(intent)


        }



        binding.cardimage4.setOnClickListener {

           // PreferenceManager.getDefaultSharedPreferences(this).edit().clear().apply();

            var intent: Intent = Intent(this, CameraActivity::class.java)
            cameraActivityWithResult.launch(intent)


        }

        binding.cardimage1.setOnClickListener {

            var intent: Intent = Intent(this, CameraActivity::class.java)
            cameraActivityWithResult.launch(intent)


        }

        binding.cardimage2.setOnClickListener {

            var intent: Intent = Intent(this, CameraActivity::class.java)
            cameraActivityWithResult.launch(intent)


        }
        binding.cardimage3.setOnClickListener {

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
                else -> {

                }
            }



        })



    }
















    private fun initactionbar(){

        supportActionBar?.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
        supportActionBar?.setDisplayShowCustomEnabled(true)
        supportActionBar?.setCustomView(R.layout.actionbarnew)
        val view1: View =  supportActionBar!!.customView
        val toolbar: Toolbar = view1.parent as Toolbar
        toolbar.setContentInsetsAbsolute(0, 0)
        toolbar.setNavigationIcon(R.drawable.ic_baseline_close_24)
        val headertext: TextView = view1.findViewById(R.id.headertext)
        headertext.text = getString(R.string.process)
        val profile: ImageView = view1.findViewById(R.id.profile)
         // val back: ImageView = view1.findViewById(R.id.back)


        profile.setImageResource(R.drawable.syncnew)

        profile.setOnClickListener {



        }
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

            binding.profile!!.setImageResource(R.drawable.syncyellow)





        } else {


            binding.profile!!.setImageResource(R.drawable.syncnew)



            //  save!!.setImageDrawable(resources.getDrawable(R.drawable.savegreen))

        }


    }

    override fun onNetworkConnectionChanged(isConnected: Boolean) {

        showMessage(isConnected)

        if (isConnected){



            binding.profile!!.setImageResource(R.drawable.syncnew)


        }
        else{


            binding.profile!!.setImageResource(R.drawable.syncyellow)



        }
    }


}

