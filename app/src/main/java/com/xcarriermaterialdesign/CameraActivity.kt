package com.xcarriermaterialdesign

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.hardware.Camera
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.util.Base64
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.imageview.ShapeableImageView
import com.xcarriermaterialdesign.utils.AnalyticsApplication
import com.xcarriermaterialdesign.utils.MyApplication
import java.io.*

class CameraActivity : AppCompatActivity(), SurfaceHolder.Callback, Camera.PictureCallback {



    lateinit var surfaceView: SurfaceView
    lateinit var showPermissionMsg: TextView
    lateinit var image1IV: ShapeableImageView
    lateinit var cardimage1: CardView
    lateinit var image2IV: ShapeableImageView
    lateinit var image3IV: ShapeableImageView

    lateinit var cancel_img1: ImageView
    lateinit var cancel_img2: ImageView
    lateinit var cancel_img3: ImageView




    lateinit var takeIV: ImageView
    lateinit var saveIV: ImageView
    lateinit var cancelled: ImageView
    lateinit var cameraback: TextView
    lateinit var retakeIV: TextView
    lateinit var usePhotoTV: TextView

    private var surfaceHolder: SurfaceHolder? = null
    private var camera: Camera? = null

    private var isCapturing = false


    /*  private val neededPermissions = arrayOf(
          Manifest.permission.WRITE_EXTERNAL_STORAGE,
          Manifest.permission.CAMERA,
      )*/


    private val neededPermissions = arrayOf(
        Manifest.permission.CAMERA
    )




    var count = 0
    var bitMap1: Bitmap? = null
    var rotatedBitmap: Bitmap? = null
    var bitMap2: Bitmap? = null
    var bitMap3: Bitmap? = null
    var bytes1: ByteArray? = null
    var bytes2: ByteArray? = null
    var bytes3: ByteArray? = null

    override fun onBackPressed() {
        finish()
    }





    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)

        supportActionBar?.hide()

        surfaceView = findViewById<SurfaceView>(R.id.surfaceView)
       // showPermissionMsg = findViewById<TextView>(R.id.showPermissionMsg)
        image1IV = findViewById<ShapeableImageView>(R.id.image1IV)
        image2IV = findViewById<ShapeableImageView>(R.id.image2IV)
        image3IV = findViewById<ShapeableImageView>(R.id.image3IV)

        cancel_img1 = findViewById<ImageView>(R.id.cancel_img1)
        cancel_img2 = findViewById<ImageView>(R.id.cancel_img2)
        cancel_img3 = findViewById<ImageView>(R.id.cancel_img3)


        takeIV = findViewById<ImageView>(R.id.takeIV)
        saveIV = findViewById<ImageView>(R.id.saveIV)
        cancelled = findViewById<ImageView>(R.id.cancelled)
        cameraback = findViewById<TextView>(R.id.cameraback)
        retakeIV = findViewById<TextView>(R.id.retakeIV)
        usePhotoTV = findViewById<TextView>(R.id.usePhotoTV)

        takeIV.setOnClickListener {
            if (!isCapturing)
                captureImage()
        }


        cancelled.setOnClickListener {

            finish()
        }

        saveIV.setOnClickListener { saveImages() }

        usePhotoTV.setOnClickListener {


            usePhotos()


        }

        retakeIV.setOnClickListener { retake() }

        cameraback.setOnClickListener {
            val intent = Intent()
            setResult(Activity.RESULT_CANCELED,intent)
            finish()
        }
        // Check code to get permissions if needed.v
        val result = checkPermission()
        if (result) {
            setupSurfaceHolder()
// if permissions granted, update ui accordingly.
        }


        cancel_img1.setOnClickListener {


            count -= 1
            bitMap1 = null
            bytes1 = null
            cameraback.visibility = View.VISIBLE
            cancel_img1.visibility = View.GONE
            retakeIV.visibility = View.GONE
            usePhotoTV.visibility = View.INVISIBLE
            image1IV.visibility = View.GONE
            takeIV.visibility = View.VISIBLE


        }

        cancel_img2.setOnClickListener {


            count -= 1
            bitMap2 = null
            bytes2 = null
            image2IV.visibility = View.GONE
            cancel_img2.visibility = View.GONE
            takeIV.visibility = View.VISIBLE


        }

        cancel_img3.setOnClickListener {


            count -= 1
            bitMap3 = null
            bytes3 = null
            image3IV.visibility = View.GONE
            cancel_img3.visibility = View.GONE
            takeIV.visibility = View.VISIBLE



        }

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
                    showPermissionAlert(permissions)
                } else {
                    requestPermissions(permissions)
                }
                return false
            }
        }
        return true
    }

    private fun showPermissionAlert(permissions: Array<String?>) {
        val alertBuilder = AlertDialog.Builder(this)
        alertBuilder.setCancelable(true)
        alertBuilder.setTitle(R.string.permission_required)
        alertBuilder.setMessage(R.string.permission_message)
        alertBuilder.setPositiveButton(R.string.yes) { _, _ -> requestPermissions(permissions) }
        val alert = alertBuilder.create()
        alert.show()
    }

    private fun requestPermissions(permissions: Array<String?>) {
        ActivityCompat.requestPermissions(this@CameraActivity, permissions, REQUEST_CODE)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            REQUEST_CODE -> {
                for (result in grantResults) {
                    if (result == PackageManager.PERMISSION_DENIED) {
                        Toast.makeText(
                            this@CameraActivity,
                            R.string.permission_warning,
                            Toast.LENGTH_LONG
                        ).show()
                        showPermissionMsg.visibility = View.VISIBLE
                        return
                    }
                }
                setupSurfaceHolder()
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun setupSurfaceHolder() {
        surfaceView.visibility = View.VISIBLE

        surfaceHolder = surfaceView.holder
        surfaceView.holder.addCallback(this)
    }


    private fun captureImage() {
        if (camera != null) {

            isCapturing = true
           // LoadingView.displayLoadingWithText(this,"",false)


            camera!!.takePicture(null, null, this)
        }
    }

    override fun surfaceCreated(surfaceHolder: SurfaceHolder) {
        startCamera()
    }

    private fun startCamera() {
        camera = Camera.open()
        camera!!.setDisplayOrientation(90)
        try {
            camera!!.setPreviewDisplay(surfaceHolder)
            camera!!.startPreview()
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }

    override fun surfaceChanged(surfaceHolder: SurfaceHolder, i: Int, i1: Int, i2: Int) {
        resetCamera()
    }

    private fun resetCamera() {
        if (surfaceHolder!!.surface == null) {
            // Return if preview surface does not exist
            return
        }

        // Stop if preview surface is already running.
        camera!!.stopPreview()
        try {
            // Set preview display
            camera!!.setPreviewDisplay(surfaceHolder)
        } catch (e: IOException) {
            e.printStackTrace()
        }

        // Start the camera preview...
        camera!!.startPreview()
    }

    override fun surfaceDestroyed(surfaceHolder: SurfaceHolder) {
        releaseCamera()
    }

    private fun releaseCamera() {
        camera!!.stopPreview()
        camera!!.release()
        camera = null
    }

    override fun onPictureTaken(bytes: ByteArray, camera: Camera) {
        loadImage(bytes)
        resetCamera()
    }

    fun getResizedBitmap(image: Bitmap, maxSize: Int): Bitmap? {
        var width = image.width
        var height = image.height
        val bitmapRatio = width.toFloat() / height.toFloat()
        if (bitmapRatio > 1) {
            width = maxSize
            height = (width / bitmapRatio).toInt()
        } else {
            height = maxSize
            width = (height * bitmapRatio).toInt()
        }
        return Bitmap.createScaledBitmap(image, width, height, true)
    }

    @SuppressLint("ResourceAsColor")
    private fun loadImage(bytes: ByteArray){
        if(count < 3){
            count += 1

            val rawBitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)

            val stream1 = ByteArrayOutputStream()
            val bmp = getResizedBitmap(rawBitmap,600)
             rotatedBitmap = bmp?.rotate(90f)
            rotatedBitmap?.compress(Bitmap.CompressFormat.PNG, 100, stream1)
            val imageInByte1 = stream1.toByteArray()

            val base64Encoded = Base64.encodeToString(imageInByte1, Base64.DEFAULT)

            when (count) {

                1 -> {
                    image1IV.visibility = View.VISIBLE
                    retakeIV.visibility = View.VISIBLE
                    cancel_img1.visibility = View.VISIBLE
                    usePhotoTV.visibility = View.VISIBLE
                    usePhotoTV.text = getString(R.string.send)
                    cameraback.visibility = View.GONE
                    bitMap1 = bmp
                    bytes1 = bytes

                    image1IV.setImageBitmap(rotatedBitmap)

                    AnalyticsApplication.instance?.setPackageImage1(bitMap1)


                   // AnalyticsApplication.instance?.setImage1Base64Str(base64Encoded)
                    //  println("==image1==${MyApplication.applicationContext().getImage1Base64Str()}")
                    isCapturing = false

                }
                2 -> {
                    bitMap2 = bmp
                    bytes2 = bytes
                    image2IV.visibility = View.VISIBLE
                    cancel_img2.visibility = View.VISIBLE
                    image2IV.setImageBitmap(rotatedBitmap)

                    AnalyticsApplication.instance?.setPackageImage2(bitMap2)


                  //  AnalyticsApplication.instance?.setImage2Base64Str(base64Encoded)


                    //  MyApplication.applicationContext().setImage2Base64Str(base64Encoded)
                    isCapturing = false

                }
                3 -> {
                    bitMap3 = bmp
                    bytes3 = bytes
                    image3IV.visibility = View.VISIBLE
                    cancel_img3.visibility = View.VISIBLE
                    saveIV.visibility = View.GONE
                    takeIV.visibility = View.GONE
                    image3IV.setImageBitmap(rotatedBitmap)
                    // MyApplication.applicationContext().setImage3Base64Str(base64Encoded)

                    AnalyticsApplication.instance?.setPackageImage3(bitMap3)


                    isCapturing = false

                }
            }
        }
        else{
            Toast.makeText(this@CameraActivity, "Only 3 pictures are allowed", Toast.LENGTH_LONG).show()
            return
        }

      //  LoadingView.hideLoading()
    }

    fun retake(){
        if (count==1){
            count -= 1
            bitMap1 = null
            bytes1 = null
            cameraback.visibility = View.VISIBLE
            retakeIV.visibility = View.GONE
            usePhotoTV.visibility = View.INVISIBLE
            image1IV.visibility = View.GONE
            cancel_img1.visibility = View.GONE

            AnalyticsApplication.instance?.setPackageImage1(null)

          //  MyApplication.applicationContext().setPackageImage1(null)
            //MyApplication.applicationContext().setImage1Base64Str(null)
        }else if (count==2){
            count -= 1
            bitMap2 = null
            bytes2 = null
            image2IV.visibility = View.GONE
            cancel_img2.visibility = View.GONE

            AnalyticsApplication.instance?.setPackageImage2(null)

           // MyApplication.applicationContext().setImage2Base64Str(null)

        }else if (count==3){
            count -= 1
            bitMap3 = null
            bytes3 = null
            saveIV.visibility = View.GONE
            takeIV.visibility = View.VISIBLE
            image3IV.visibility = View.GONE
            cancel_img3.visibility = View.GONE

            AnalyticsApplication.instance?.setPackageImage3(null)

            // MyApplication.applicationContext().setImage3Base64Str(null)
        }
    }

    fun saveImages(){
//        for (i in 1..3){
//            if (count==1){
//                saveImage(bytes1)
//            }else if (count==2){
//                saveImage(bytes2)
//            }else if (count==3){
//                saveImage(bytes3)
//            }
//        }
        usePhotos()
    }

    fun usePhotos(){

        AnalyticsApplication.instance?.setEmpId(count.toString())

        val intent = Intent()
        setResult(Activity.RESULT_OK,intent)
        finish()

    }

    private fun saveImage(bytes: ByteArray?) {
        val outStream: FileOutputStream
        try {
            val fileName = "TUTORIALWING_" + System.currentTimeMillis() + ".jpg"
            val file = File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                fileName
            )
            outStream = FileOutputStream(file)
            outStream.write(bytes)
            outStream.close()
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    companion object {
        const val REQUEST_CODE = 100
    }

    private fun initActionBar() {

        supportActionBar?.hide()
    }

    fun Bitmap.rotate(degrees: Float): Bitmap {
        val matrix = Matrix().apply { postRotate(degrees) }
        return Bitmap.createBitmap(this, 0, 0, width, height, matrix, true)
    }


}