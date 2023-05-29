package com.xcarriermaterialdesign.barcodescanner

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.media.ToneGenerator
import android.os.Bundle
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.View
import android.widget.ImageView
import android.widget.TextView

import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.room.Room
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.Detector.Detections
import com.google.android.gms.vision.barcode.Barcode
import com.google.android.gms.vision.barcode.BarcodeDetector
import com.google.zxing.integration.android.IntentIntegrator
import com.google.zxing.integration.android.IntentIntegrator.REQUEST_CODE
import com.journeyapps.barcodescanner.CaptureManager
import com.journeyapps.barcodescanner.DecoratedBarcodeView
import com.xcarriermaterialdesign.R
import com.xcarriermaterialdesign.process.ProcessPackageActivity
import com.xcarriermaterialdesign.roomdatabase.ProcessDao
import com.xcarriermaterialdesign.roomdatabase.ProcessDatabase
import com.xcarriermaterialdesign.roomdatabase.ProcessPackage
import java.io.IOException


open class BarcodeScannerActivity : AppCompatActivity() {


    private var surfaceView: SurfaceView? = null
    private var barcodeDetector: BarcodeDetector? = null
    private var cameraSource: CameraSource? = null
    private val REQUEST_CAMERA_PERMISSION = 201

    //This class provides methods to play DTMF tones
    private var toneGen1: ToneGenerator? = null
    private var barcodeText: TextView? = null
    private var nextbutton: TextView? = null
    private var barcodeData: String? = null

    private var tapbutton:ImageView?= null
    private var redline:View?= null



    val arrayList = ArrayList<String>()

    //using zxing barcode scanner

    private var capture: CaptureManager? = null
    private var barcodeScannerView: DecoratedBarcodeView? = null

    private var qrScanIntegrator: IntentIntegrator? = null

    val ASK_QUESTION_REQUEST = 1000

    private lateinit var processDao: ProcessDao



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_barcode_scanner)
        supportActionBar?.hide()

       // setupScanner()

        val db = Room.databaseBuilder(
            applicationContext,
            ProcessDatabase::class.java, "Process_database"
        ).allowMainThreadQueries().build()


        processDao = db.processDao()


       // toneGen1 = ToneGenerator(AudioManager.STREAM_MUSIC, 100)
        surfaceView = findViewById(R.id.surface_view)
        barcodeText = findViewById(R.id.barcode_text)
        nextbutton = findViewById(R.id.nextbutton)
        tapbutton = findViewById(R.id.tapbutton)
        redline = findViewById(R.id.redline)
      //  initialiseDetectorsAndSources()


       // processDao.insertProcessPackage(ProcessPackage(arrayList.toString()))


        nextbutton!!.setOnClickListener {



           val intent = Intent(this, ProcessPackageActivity::class.java)
          //  intent.putExtra("list", arrayList)
            startActivity(intent)
        }

      //  zxing_barcodescanner(savedInstanceState)












    }


  /*  protected fun initializeContent(): DecoratedBarcodeView? {
      //  setContentView(R.layout.zxing_capture)
        return findViewById<View>(R.id.zxing_barcode_scanner) as DecoratedBarcodeView
    }*/


  /*  override fun onResume() {
        super.onResume()
        capture!!.onResume()
    }

    override fun onPause() {
        super.onPause()
        capture!!.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
    //    capture!!.onDestroy()
    }*/

    private fun setupScanner() {
        qrScanIntegrator = IntentIntegrator(this)
        qrScanIntegrator?.setOrientationLocked(false)
        qrScanIntegrator?.setPrompt("")
        qrScanIntegrator?.captureActivity

    }


    private fun zxing_barcodescanner(savedInstanceState: Bundle?){


       // setupScanner()
      //  qrScanIntegrator = IntentIntegrator(this)

     //   barcodeScannerView = initializeContent()

        capture = CaptureManager(this, barcodeScannerView)
        capture!!.initializeFromIntent(intent, savedInstanceState)
        capture!!.decode()



     //   Toast.makeText(this, intent.data.toString(), Toast.LENGTH_SHORT).show()


        // val intentResult = IntentIntegrator.parseActivityResult(resultCode, data)



         startActivityForResult(intent, REQUEST_CODE);


        //Toast.makeText(this, capture!!.decode().toString(), Toast.LENGTH_SHORT).show()


      /*  val batchScanResultLauncher  = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {

            if (it.resultCode == RESULT_OK) {
                val intentResult = IntentIntegrator.parseActivityResult(it.resultCode, it.data)


                Toast.makeText(this, intentResult.contents, Toast.LENGTH_SHORT).show()

                // binding.batchIdTV.setText(intentResult.contents.toString())
            }
        }*/


      //   batchScanResultLauncher.launch(qrScanIntegrator?.createScanIntent())


    }


 /*   protected fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState!!)
        capture!!.onSaveInstanceState(outState)
    }*/

   /* override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        capture!!.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }


   override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {

            val intentResult = IntentIntegrator.parseActivityResult(resultCode, data)


            Toast.makeText(this, intentResult.contents, Toast.LENGTH_SHORT).show()

          //  val requiredValue = data!!.getStringExtra("key")
        }


    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        return barcodeScannerView!!.onKeyDown(keyCode, event) || super.onKeyDown(keyCode, event)
    }
*/










    private fun initialiseDetectorsAndSources() {

        //Toast.makeText(getApplicationContext(), "Barcode scanner started", Toast.LENGTH_SHORT).show();
        barcodeDetector = BarcodeDetector.Builder(this)
            .setBarcodeFormats(Barcode.ALL_FORMATS)
            .build()
        cameraSource = CameraSource.Builder(this, barcodeDetector)
            .setRequestedPreviewSize(1920, 1080)
            .setAutoFocusEnabled(true) //you should add this feature

            .build()

        surfaceView!!.holder.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceCreated(holder: SurfaceHolder) {
                try {
                    if (ActivityCompat.checkSelfPermission(
                            this@BarcodeScannerActivity,
                            Manifest.permission.CAMERA
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        cameraSource?.start(surfaceView!!.holder)
                    } else {
                        ActivityCompat.requestPermissions(
                            this@BarcodeScannerActivity,
                            arrayOf(Manifest.permission.CAMERA),
                            REQUEST_CAMERA_PERMISSION
                        )
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }

            override fun surfaceChanged(
                holder: SurfaceHolder,
                format: Int,
                width: Int,
                height: Int
            ) {
            }

            override fun surfaceDestroyed(holder: SurfaceHolder) {
                cameraSource?.stop()
            }
        })

        barcodeDetector?.setProcessor(object : Detector.Processor<Barcode> {
            override fun release() {
                // Toast.makeText(getApplicationContext(), "To prevent memory leaks barcode scanner has been stopped", Toast.LENGTH_SHORT).show();
            }

            override fun receiveDetections(detections: Detections<Barcode>) {
                val barcodes = detections.detectedItems


                if (barcodes.size() != 0) {
                    barcodeText!!.post {
                        if (barcodes.valueAt(0).email != null) {
                            barcodeText!!.removeCallbacks(null)
                            barcodeData = barcodes.valueAt(0).email.address


                            if (arrayList.contains(barcodeData)){

                              //  Toast.makeText(this@BarcodeScannerActivity,"you already captured data", Toas)
                            }
                            else{

                                processDao.insertProcessPackage(ProcessPackage(barcodeData!!))
                                arrayList.add(barcodeData!!)
                            }


                            displaycount()
                            //  barcodeText!!.text = barcodeData
                            //toneGen1!!.startTone(ToneGenerator.TONE_CDMA_PIP, 150)
                        } else {

                            barcodeData = barcodes.valueAt(0).displayValue

                            if (arrayList.contains(barcodeData)){



                                //  Toast.makeText(this@BarcodeScannerActivity,"you already captured data", Toas)
                            }
                            else{

                                processDao.insertProcessPackage(ProcessPackage(barcodeData!!))

                               arrayList.add(barcodeData!!)
                            }

                            println("==arraylist==$arrayList")

                            displaycount()

                            // barcodeText!!.text = barcodeData
                            //toneGen1!!.startTone(ToneGenerator.TONE_CDMA_PIP, 150)
                        }
                    }
                }

            }
        })







        tapbutton?.setOnClickListener {



            initialiseDetectorsAndSources()

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













   override fun onPause() {
        super.onPause()
        supportActionBar!!.hide()
        cameraSource!!.release()
    }

    override fun onResume() {
        super.onResume()
        supportActionBar!!.hide()
        initialiseDetectorsAndSources()
    }













}