package com.xcarriermaterialdesign.activities.scannerprocess

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.media.RingtoneManager
import android.media.ToneGenerator
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.zxing.Result
import com.google.zxing.integration.android.IntentIntegrator
import com.journeyapps.barcodescanner.CaptureManager
import com.journeyapps.barcodescanner.DecoratedBarcodeView
import com.xcarriermaterialdesign.BottomNavigationActivity
import com.xcarriermaterialdesign.R
import com.xcarriermaterialdesign.roomdatabase.ProcessDao
import com.xcarriermaterialdesign.roomdatabase.ProcessPackage
import com.xcarriermaterialdesign.utils.ServiceDialog
import me.dm7.barcodescanner.zxing.ZXingScannerView

class SimpleProcessActivty : AppCompatActivity(), ZXingScannerView.ResultHandler {



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


    private fun requestPermissions(permissions: Array<String?>) {
        ActivityCompat.requestPermissions(this, permissions, BottomNavigationActivity.REQUEST_CODE
        )
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




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_simple_scanner)
        supportActionBar?.hide()




        val contentFrame = findViewById<ViewGroup>(R.id.content_frame)

        checkPermission()
        mScannerView = ZXingScannerView(this)
        contentFrame.addView(mScannerView)



        barcodeText = findViewById(R.id.barcode_text)
        nextbutton = findViewById(R.id.nextbutton)
        tapbutton = findViewById(R.id.tapbutton)
        close_img = findViewById(R.id.close_img)
        info_layout = findViewById(R.id.info_layout)
        ok_text = findViewById(R.id.oktext)

        //  initialiseDetectorsAndSources()


        // processDao.insertProcessPackage(ProcessPackage(arrayList.toString()))


        nextbutton!!.setOnClickListener {


            if (barcodeData.equals("")){



                info_layout?.visibility = View.VISIBLE
            }

            else if (barcodeData!!.contains(" ")){

                ServiceDialog.ShowDialog(this,"Please Scan  valid barcode")
            }
            else{

                val intent = Intent()
                intent.putExtra("barcode", barcodeData)
                setResult(RESULT_OK, intent)
                finish()

            }







        }

        close_img?.setOnClickListener {

            finish()
        }


        ok_text?.setOnClickListener {

            info_layout?.visibility = View.GONE

        }
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
                { mScannerView!!.resumeCameraPreview(this@SimpleProcessActivty) },
                2000
            )









    }





}