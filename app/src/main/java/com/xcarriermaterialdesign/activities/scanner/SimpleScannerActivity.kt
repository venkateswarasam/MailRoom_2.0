package com.xcarriermaterialdesign.activities.scanner

import android.Manifest
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.ContentResolver
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.media.RingtoneManager
import android.media.ToneGenerator
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.room.Room
import com.google.zxing.Result
import com.google.zxing.integration.android.IntentIntegrator
import com.journeyapps.barcodescanner.CaptureManager
import com.journeyapps.barcodescanner.DecoratedBarcodeView
import com.xcarriermaterialdesign.BottomNavigationActivity
import com.xcarriermaterialdesign.R
import com.xcarriermaterialdesign.process.ProcessPackageActivity
import com.xcarriermaterialdesign.roomdatabase.ProcessDao
import com.xcarriermaterialdesign.roomdatabase.ProcessDatabase
import com.xcarriermaterialdesign.roomdatabase.ProcessPackage
import me.dm7.barcodescanner.zxing.ZXingScannerView

class SimpleScannerActivity : AppCompatActivity(), ZXingScannerView.ResultHandler {


    private var mScannerView: ZXingScannerView? = null

    private val REQUEST_CAMERA_PERMISSION = 201

    //This class provides methods to play DTMF tones
    private var toneGen1: ToneGenerator? = null
    private var barcodeText: TextView? = null
    private var nextbutton: TextView? = null
    private var barcodeData: String? = ""

    private var tapbutton: ImageView?= null
    private var close_img: ImageView?= null
    private var redline: View?= null



    val arrayList = ArrayList<String>()

    //using zxing barcode scanner

    private var capture: CaptureManager? = null
    private var barcodeScannerView: DecoratedBarcodeView? = null

    private var qrScanIntegrator: IntentIntegrator? = null

    val ASK_QUESTION_REQUEST = 1000

    private lateinit var processDao: ProcessDao

    var check:String?= null

    private lateinit var processPackage: List<ProcessPackage>

    internal var info_layout: LinearLayout?= null
    internal var ok_text:TextView?= null

    private val neededPermissions = arrayOf(
        Manifest.permission.CAMERA
    )

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_simple_scanner)
        supportActionBar?.hide()

        check = intent.getStringExtra("flag")


        val contentFrame = findViewById<ViewGroup>(R.id.content_frame)

        checkPermission()

        mScannerView = ZXingScannerView(this)
        contentFrame.addView(mScannerView)


        val db = Room.databaseBuilder(
            applicationContext,
            ProcessDatabase::class.java, "Process_database"
        ).allowMainThreadQueries().build()


        processDao = db.processDao()

        processPackage = processDao.getAllProcessPackages()

        barcodeText = findViewById(R.id.barcode_text)
        nextbutton = findViewById(R.id.nextbutton)
        tapbutton = findViewById(R.id.tapbutton)
        close_img = findViewById(R.id.close_img)
        info_layout = findViewById(R.id.info_layout)
        ok_text = findViewById(R.id.oktext)




        nextbutton!!.setOnClickListener {

            if (check.equals("process")){


                if (barcodeData.equals("")){



                    info_layout?.visibility = View.VISIBLE
                }

                else{

                    val intent = Intent()
                    intent.putExtra("barcode", barcodeData)
                    setResult(RESULT_OK, intent)
                    finish()
                }


            }

            else{

                if (barcodeData.equals("")){



                    info_layout?.visibility = View.VISIBLE
                }

                else{

                    val intent = Intent(this, ProcessPackageActivity::class.java)
                    //  intent.putExtra("list", arrayList)
                    startActivity(intent)
                }




            }




        }

        close_img?.setOnClickListener {

            finish()
        }


        ok_text?.setOnClickListener {

            info_layout?.visibility = View.GONE

        }



    }



    private fun checkPermission(): Boolean {
        val currentAPIVersion = Build.VERSION.SDK_INT
        if (currentAPIVersion >= Build.VERSION_CODES.M) {
            val permissionsNotGranted = java.util.ArrayList<String>()
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
        ActivityCompat.requestPermissions(this, permissions, BottomNavigationActivity.REQUEST_CODE
        )
    }








    override fun onResume() {
        super.onResume()
        mScannerView!!.setResultHandler(this)
        mScannerView!!.startCamera()
    }
    override fun onPause() {
        super.onPause()
        mScannerView!!.stopCamera()
    }

    override fun handleResult(rawResult: Result?) {

       /* Toast.makeText(
            this, "Contents = " + rawResult!!.text +
                    ", Format = " + rawResult!!.barcodeFormat.name, Toast.LENGTH_SHORT
        ).show()*/


        barcodeData = rawResult?.text


        if (check.equals("process")){

            try {
                val notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
                val r = RingtoneManager.getRingtone(applicationContext, notification)
                r.play()
            } catch (e: Exception) {
            }

            barcodeData = rawResult?.text
            barcodeText!!.text = "1 code captured"

            val handler = Handler()
            handler.postDelayed(
                { mScannerView!!.resumeCameraPreview(this@SimpleScannerActivity) },
                2000
            )
        }
        else {




            if (arrayList.contains(rawResult?.text)) {


                playNotificationSound()

                val dialog = Dialog(this@SimpleScannerActivity)
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

                dialog.setCancelable(false)
                dialog.setContentView(R.layout.info_layout_new)

                val ok = dialog.findViewById<TextView>(R.id.oktext)
                val cancel = dialog.findViewById<TextView>(R.id.cancel)

                val info_msg = dialog.findViewById<TextView>(R.id.info_msg)

                info_msg.text = "This barcode already scanned.Do you want to continue?"


                val lp = WindowManager.LayoutParams()
                val window = dialog.window
                lp.copyFrom(window!!.attributes)
                lp.width = WindowManager.LayoutParams.MATCH_PARENT
                lp.height = WindowManager.LayoutParams.WRAP_CONTENT
                val isFinishing = false
                window.attributes = lp
                dialog.show()

                ok.setOnClickListener {

                    processDao.insertProcessPackage(ProcessPackage(rawResult?.text!!,""))
                    arrayList.add(rawResult?.text!!)
                    dialog.dismiss()
                    displaycount()
                }

                cancel.setOnClickListener {

                    dialog.dismiss()
                }


                //  Toast.makeText(this@BarcodeScannerActivity,"you already captured data", Toas)
            } else {

                try {
                    val notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
                    val r = RingtoneManager.getRingtone(applicationContext, notification)
                    r.play()
                } catch (e: Exception) {
                }

                processDao.insertProcessPackage(ProcessPackage(rawResult?.text!!,""))
                arrayList.add(rawResult?.text!!)

                displaycount()

            }





            // Note:
            // * Wait 2 seconds to resume the preview.
            // * On older devices continuously stopping and resuming camera preview can result in freezing the app.
            // * I don't know why this is the case but I don't have the time to figure out.
            // Note:
            // * Wait 2 seconds to resume the preview.
            // * On older devices continuously stopping and resuming camera preview can result in freezing the app.
            // * I don't know why this is the case but I don't have the time to figure out.
            val handler = Handler()
            handler.postDelayed(
                { mScannerView!!.resumeCameraPreview(this@SimpleScannerActivity) },
                2000
            )
        }







    }

    private fun displaycount(){

        val count = arrayList.size

        //   val count = processDao.getAllProcessPackages().size

        if (count == 1){

            barcodeText!!.text = "$count code captured"

        }
        else{

            barcodeText!!.text = "$count codes captured"

        }
    }

    fun playNotificationSound() {
        try {
            val alarmSound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + applicationContext.packageName + "/" + R.raw.scanrepeated) //Here is FILE_NAME is the name of file that you want to play

            val r = RingtoneManager.getRingtone(applicationContext, alarmSound)
            r.play()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }



    }
}