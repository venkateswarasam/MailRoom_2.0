package com.xcarriermaterialdesign.process

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Bitmap
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.github.gcacace.signaturepad.views.SignaturePad
import com.kyanogen.signatureview.SignatureView
import com.xcarriermaterialdesign.R
import com.xcarriermaterialdesign.utils.AnalyticsApplication
import java.io.ByteArrayOutputStream


class SigantureviewActivity : AppCompatActivity() {

    private lateinit var signatureView: SignatureView
    private lateinit var signatureBitmap: Bitmap
    internal lateinit var path: String

    var isdrawn = false

    var back: ImageView?= null
    var saveimage: ImageView?= null

    internal var toolbar:Toolbar?= null

    internal var signaturelayout:LinearLayout?= null


    private var mSignaturePad: SignaturePad? = null


    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)

        val orientation = newConfig.orientation
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {

            isdrawn = false


         //   mSignaturePad?.clear()

        }
        else if (orientation == Configuration.ORIENTATION_LANDSCAPE) {

            isdrawn = false

         //   mSignaturePad?.clear()

        }

    }



    @SuppressLint("ClickableViewAccessibility", "MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sigantureview)

        supportActionBar?.hide()

      /*  val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        val height = displayMetrics.heightPixels
        val width = displayMetrics.widthPixels

        (this).windowManager
            .defaultDisplay
            .getMetrics(displayMetrics)*/

        toolbar = findViewById(R.id.toolbar)


        saveimage = findViewById<ImageView>(R.id.saveimage)
     //   signaturelayout = findViewById<LinearLayout>(R.id.signaturelayout)

        mSignaturePad = findViewById(R.id.signature_pad)



        mSignaturePad?.setOnSignedListener(object : SignaturePad.OnSignedListener {
            override fun onStartSigning() {

                isdrawn = true
              //  Toast.makeText(this@SigantureviewActivity, "OnStartSigning", Toast.LENGTH_SHORT).show()
            }

            override fun onSigned() {

                isdrawn = true
             //   mSaveButton.setEnabled(true)
               // mClearButton.setEnabled(true)
            }

            override fun onClear() {

                isdrawn = false
              //  mSaveButton.setEnabled(false)
                //mClearButton.setEnabled(false)
            }
        })





        signatureView = findViewById<SignatureView>(R.id.signature_view)




        val clearImg = findViewById<ImageView>(R.id.clearIV)
        val clear_lv = findViewById<LinearLayout>(R.id.clear_lv)




        clear_lv.setOnClickListener()
        {

            mSignaturePad?.clear()

           signatureView.clearCanvas()
            isdrawn = false


        }


        signatureView.setOnTouchListener(View.OnTouchListener { v, event ->

            if (MotionEvent.ACTION_UP == event.action) {


                isdrawn = true


            }
            false // return is important...
        })


        saveimage?.setOnClickListener {


            if (isdrawn) {

                signatureBitmap = mSignaturePad?.signatureBitmap!!


                  // signatureBitmap = signatureView.signatureBitmap

              //  bitmap = signatureBitmap!!

                println("==bitmapsign==$signatureBitmap")

                AnalyticsApplication.instance?.setBitmapSign(signatureBitmap)

                println("==bitmapsign==${AnalyticsApplication.instance?.getBitmapSign()}")

              mSignaturePad?.clear()
              //  signatureView.clearCanvas()
                path = saveImage(signatureBitmap)

                val intent = Intent()
                intent.putExtra("RESULT_TEXT", path)
                setResult(Activity.RESULT_OK, intent)
                finish()
            }

            else{

                Toast.makeText(this,"Please draw Signature", Toast.LENGTH_SHORT).show()
            }
        }

        toolbar?.setNavigationOnClickListener {


            AnalyticsApplication.instance?.setBitmapSign(null)

            finish()

        }
    }



    fun saveImage(myBitmap: Bitmap): String {
        val bytes = ByteArrayOutputStream()
        myBitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes)

        val bytesArray = bytes.toByteArray()

       // MyApplication.applicationContext().setDigitalSignBase64(Base64.encodeToString(bytesArray, Base64.DEFAULT))

//        val wallpaperDirectory = File(
//            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString() + IMAGE_DIRECTORY /*iDyme folder*/
//        )
//        // have the object build the directory structure, if needed.
//        if (!wallpaperDirectory.exists()) {
//            wallpaperDirectory.mkdirs()
//            Log.d("hhhhh", wallpaperDirectory.toString())
//        }
//
//        try {
//            val f = File(
//                wallpaperDirectory, Calendar.getInstance()
//                    .timeInMillis.toString() + ".jpg"
//            )
//            f.createNewFile()
//            val fo = FileOutputStream(f)
//            fo.write(bytes.toByteArray())
//            MediaScannerConnection.scanFile(
//                this,
//                arrayOf(f.path),
//                arrayOf("image/jpeg"), null
//            )
//            fo.close()
//            Log.d("TAG", "File Saved::--->" + f.absolutePath)
////            Toast.makeText(applicationContext, "Signature Saved !!!", Toast.LENGTH_SHORT)
////                .show()
//            signatureView.clearCanvas()

//            return f.absolutePath
//        } catch (e1: Exception) {
//            e1.printStackTrace()
//        }

        return ""

    }

}