package com.xcarriermaterialdesign.activities.signature

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Base64
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.room.Room
import com.github.gcacace.signaturepad.views.SignaturePad
import com.kyanogen.signatureview.SignatureView
import com.xcarriermaterialdesign.R
import com.xcarriermaterialdesign.roomdatabase.CamerDao
import com.xcarriermaterialdesign.roomdatabase.CameraPackage
import com.xcarriermaterialdesign.roomdatabase.ProcessDatabase
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

    private lateinit var camerDao: CamerDao


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



        toolbar = findViewById(R.id.toolbar)

        val db = Room.databaseBuilder(
            applicationContext,
            ProcessDatabase::class.java, "Process_database"
        ).allowMainThreadQueries().build()

        camerDao = db.cameraDao()



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

          // signatureView.clearCanvas()
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


        val byteArrayOutputStream = ByteArrayOutputStream()
        myBitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()


        val encoded = Base64.encodeToString(byteArray, Base64.DEFAULT)


     /*   val bytes = ByteArrayOutputStream()
        myBitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes)

        val bytesArray = bytes.toByteArray()*/

        AnalyticsApplication.instance!!.setDigitalSignBase64(encoded)

      //  camerDao.insertCameraPackage(CameraPackage(encoded))




        return ""

    }

}