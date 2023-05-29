package com.xcarriermaterialdesign.process

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Base64
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.widget.Toolbar
import com.kyanogen.signatureview.SignatureView
import com.xcarriermaterialdesign.R
import com.xcarriermaterialdesign.utils.AnalyticsApplication
import java.io.ByteArrayOutputStream

class SigantureviewActivity : AppCompatActivity() {

    private lateinit var signatureView: SignatureView
    private lateinit var bitmap: Bitmap
    internal lateinit var path: String

    var isdrawn = false

    var back: ImageView?= null
    var saveimage: ImageView?= null

    internal var toolbar:Toolbar?= null
    @SuppressLint("ClickableViewAccessibility", "MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sigantureview)

        supportActionBar?.hide()

        toolbar = findViewById(R.id.toolbar)


        saveimage = findViewById<ImageView>(R.id.saveimage)


        signatureView = findViewById(R.id.signature_view) as SignatureView




        val clearImg = findViewById<ImageView>(R.id.clearIV)
        val clear_lv = findViewById<LinearLayout>(R.id.clear_lv)


        clear_lv.setOnClickListener()
        {
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

                bitmap = signatureView.signatureBitmap

                println("==bitmapsign==$bitmap")

                AnalyticsApplication.instance?.setBitmapSign(bitmap)
                signatureView.clearCanvas()
                path = saveImage(bitmap)

                val intent = Intent()
                intent.putExtra("RESULT_TEXT", path)
                setResult(Activity.RESULT_OK, intent)
                finish()
            } else {

                AnalyticsApplication.instance?.setBitmapSign(null)
                // MyApplication.applicationContext().setBitmapSign(null)
                finish()

                /*SweetAlertDialog(this@SigantureviewActivity, SweetAlertDialog.ERROR_TYPE).setTitleText("Error")
                            .setContentText("Please do signature").show()*/
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