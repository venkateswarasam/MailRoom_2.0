package com.xcarriermaterialdesign.barcodescanner

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.media.ToneGenerator
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.app.ActivityCompat
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.barcode.Barcode
import com.google.android.gms.vision.barcode.BarcodeDetector
import com.google.zxing.integration.android.IntentIntegrator
import com.journeyapps.barcodescanner.CaptureManager
import com.journeyapps.barcodescanner.DecoratedBarcodeView
import com.xcarriermaterialdesign.R
import java.io.IOException

class ProcessBarcodeScannerActivity : AppCompatActivity() {


    private var surfaceView: SurfaceView? = null
    private var barcodeDetector: BarcodeDetector? = null
    private var cameraSource: CameraSource? = null
    private val REQUEST_CAMERA_PERMISSION = 201

    //This class provides methods to play DTMF tones
    private var toneGen1: ToneGenerator? = null
    private var barcodeText: TextView? = null
    private var nextbutton: TextView? = null
    private var barcodeData: String? = null

    private var tapbutton: ImageView?= null
    private var redline: View?= null



    val arrayList = ArrayList<String>()

    //using zxing barcode scanner

    private var capture: CaptureManager? = null
    private var barcodeScannerView: DecoratedBarcodeView? = null

    private var qrScanIntegrator: IntentIntegrator? = null

    val ASK_QUESTION_REQUEST = 1000

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_process_barcode_scanner)

        supportActionBar?.hide()

        // setupScanner()


        // toneGen1 = ToneGenerator(AudioManager.STREAM_MUSIC, 100)
        surfaceView = findViewById(R.id.surface_view)
        barcodeText = findViewById(R.id.barcode_text)
        nextbutton = findViewById(R.id.nextbutton)
        tapbutton = findViewById(R.id.tapbutton)
        redline = findViewById(R.id.redline)
        //  initialiseDetectorsAndSources()


        nextbutton!!.setOnClickListener {


            val intent = Intent()
            intent.putExtra("barcode", barcodeData)
            setResult(RESULT_OK, intent)
            finish()

        }

        //  zxing_barcodescanner(savedInstanceState)



    }




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
                            this@ProcessBarcodeScannerActivity,
                            Manifest.permission.CAMERA
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        cameraSource?.start(surfaceView!!.holder)
                    } else {
                        ActivityCompat.requestPermissions(
                            this@ProcessBarcodeScannerActivity,
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

            override fun receiveDetections(detections: Detector.Detections<Barcode>) {
                val barcodes = detections.detectedItems


                if (barcodes.size() != 0) {
                    barcodeText!!.post {
                        if (barcodes.valueAt(0).email != null) {
                            barcodeText!!.removeCallbacks(null)
                            barcodeData = barcodes.valueAt(0).email.address

                            barcodeText!!.text = "1 code captured"



                         /*   if (arrayList.contains(barcodeData)){

                                //  Toast.makeText(this@BarcodeScannerActivity,"you already captured data", Toas)
                            }
                            else{
                                arrayList.add(barcodeData!!)
                            }*/








                            //displaycount()
                            //  barcodeText!!.text = barcodeData
                            //toneGen1!!.startTone(ToneGenerator.TONE_CDMA_PIP, 150)
                        } else {

                            barcodeData = barcodes.valueAt(0).displayValue

                            barcodeText!!.text = "1 code captured"


                            /*  if (arrayList.contains(barcodeData)){

                                  //  Toast.makeText(this@BarcodeScannerActivity,"you already captured data", Toas)
                              }
                              else{
                                  arrayList.add(barcodeData!!)
                              }

                              println("==arraylist==$arrayList")

                              displaycount()*/

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