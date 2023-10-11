package com.xcarriermaterialdesign.activities.camera

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.hardware.Camera
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Base64
import android.view.LayoutInflater
import android.view.SurfaceHolder
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.xcarriermaterialdesign.R
import com.xcarriermaterialdesign.databinding.ActivityCameraBinding
import com.xcarriermaterialdesign.roomdatabase.CamerDao
import com.xcarriermaterialdesign.roomdatabase.CameraPackage
import com.xcarriermaterialdesign.roomdatabase.ProcessDatabase
import com.xcarriermaterialdesign.utils.AnalyticsApplication
import com.xcarriermaterialdesign.utils.ApplicationSharedPref
import com.xcarriermaterialdesign.utils.ServiceDialog
import org.jetbrains.anko.toast
import java.io.*

class CameraActivity : AppCompatActivity(), SurfaceHolder.Callback, Camera.PictureCallback {





    private var surfaceHolder: SurfaceHolder? = null
    private var camera: Camera? = null

    private var isCapturing = false





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


    private var adapter: ProcessAdapter_new? = null




    private lateinit var binding: ActivityCameraBinding

    val model: CameraViewModel by viewModels()

    private lateinit var camerDao: CamerDao

    override fun onBackPressed() {
        finish()
    }





    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        model.config(this)

        supportActionBar?.hide()

        binding = DataBindingUtil.setContentView(this, R.layout.activity_camera)


        val db = Room.databaseBuilder(
            applicationContext,
            ProcessDatabase::class.java, "Process_database"
        ).allowMainThreadQueries().build()

        camerDao = db.cameraDao()


        binding.takeIV.setOnClickListener {
            if (!isCapturing){


                captureImage()

            }


        }


        binding.cancelled.setOnClickListener {

            finish()
        }

        binding.saveIV.setOnClickListener { saveImages() }

        binding.usePhotoTV.setOnClickListener {


            if (camerDao.getAllCameraPackages().isEmpty()){

                Toast.makeText(this,"Please upload at least one Photo", Toast.LENGTH_SHORT).show()

                return@setOnClickListener
            }

            usePhotos()


        }

        binding.retakeIV.setOnClickListener {


            camerDao.deleteAllBulkPackages()

            getallcamerapackages()

            isCapturing= false


            count = 0
         // retake()
        }

        binding.cameraback.setOnClickListener {
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


        binding.cancelImg1.setOnClickListener {


            count -= 1
            bitMap1 = null
            bytes1 = null
            binding.cameraback.visibility = View.VISIBLE
            binding.cancelImg1.visibility = View.GONE
            binding.retakeIV.visibility = View.GONE
            binding.usePhotoTV.visibility = View.INVISIBLE
            binding.image1IV.visibility = View.GONE
            binding.takeIV.visibility = View.VISIBLE


        }

        binding.cancelImg2.setOnClickListener {


            count -= 1
            bitMap2 = null
            bytes2 = null
            binding.image2IV.visibility = View.GONE
            binding.cancelImg2.visibility = View.GONE
            binding.takeIV.visibility = View.VISIBLE


        }

        binding.cancelImg3.setOnClickListener {


            count -= 1
            bitMap3 = null
            bytes3 = null
            binding.image3IV.visibility = View.GONE
            binding.cancelImg3.visibility = View.GONE
            binding.takeIV.visibility = View.VISIBLE



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


                  //  Toast.makeText(this,"Permission granted", Toast.LENGTH_SHORT).show()
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
                        //showPermissionMsg.visibility = View.VISIBLE
                        return
                    }
                    else{

                      //  Toast.makeText(this,"Permission granted2", Toast.LENGTH_SHORT).show()

                       // setupSurfaceHolder()

                        finish()
                        overridePendingTransition(0, 0)
                        startActivity(intent)
                        overridePendingTransition(0, 0)




                        return

                    }
                }

                toast("permission granted")


               // Toast.makeText(this,"Permission granted1", Toast.LENGTH_SHORT).show()

                setupSurfaceHolder()
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun setupSurfaceHolder() {
        binding.surfaceView.visibility = View.VISIBLE

        surfaceHolder = binding.surfaceView.holder
        binding.surfaceView.holder.addCallback(this)
    }


    private fun captureImage() {
        if (camera != null) {

            isCapturing = true


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
                    binding.image1IV.visibility = View.VISIBLE
                    binding.retakeIV.visibility = View.VISIBLE
                    binding.cancelImg1.visibility = View.VISIBLE
                    binding.usePhotoTV.visibility = View.VISIBLE
                    binding.usePhotoTV.text = getString(R.string.send)
                    binding.cameraback.visibility = View.GONE
                    bitMap1 = bmp
                    bytes1 = bytes

                    binding.image1IV.setImageBitmap(rotatedBitmap)

                    AnalyticsApplication.instance?.setPackageImage1(bitMap1)


                    AnalyticsApplication.instance?.setImage1Base64Str(base64Encoded)

                    ApplicationSharedPref.write(ApplicationSharedPref.IMAGE1,base64Encoded)

                    camerDao.insertCameraPackage(CameraPackage(base64Encoded))

                    getallcamerapackages()

                     println("==image1==${base64Encoded}")
                    isCapturing = false

                }
                2 -> {
                    bitMap2 = bmp
                    bytes2 = bytes
                    binding.image2IV.visibility = View.VISIBLE
                    binding.cancelImg2.visibility = View.VISIBLE
                    binding.image2IV.setImageBitmap(rotatedBitmap)

                    AnalyticsApplication.instance?.setPackageImage2(bitMap2)


                    ApplicationSharedPref.write(ApplicationSharedPref.IMAGE2,base64Encoded)

                    AnalyticsApplication.instance?.setImage2Base64Str(base64Encoded)

                    camerDao.insertCameraPackage(CameraPackage(base64Encoded!!))


                    //  MyApplication.applicationContext().setImage2Base64Str(base64Encoded)
                    isCapturing = false

                    getallcamerapackages()

                }
                3 -> {
                    bitMap3 = bmp
                    bytes3 = bytes
                    binding.image3IV.visibility = View.VISIBLE
                    binding.cancelImg3.visibility = View.VISIBLE
                    binding.saveIV.visibility = View.GONE
                   // binding.takeIV.visibility = View.GONE
                    binding.image3IV.setImageBitmap(rotatedBitmap)

                    AnalyticsApplication.instance!!.setImage3Base64Str(base64Encoded)

                    ApplicationSharedPref.write(ApplicationSharedPref.IMAGE3,base64Encoded)


                    // MyApplication.applicationContext().setImage3Base64Str(base64Encoded)

                    AnalyticsApplication.instance?.setPackageImage3(bitMap3)

                    camerDao.insertCameraPackage(CameraPackage(base64Encoded!!))


                    isCapturing = false

                    getallcamerapackages()

                }
            }
        }
        else{

            ServiceDialog.ShowDialog(this,"Only 3 pictures are allowed")
          //  Toast.makeText(this@CameraActivity, "Only 3 pictures are allowed", Toast.LENGTH_LONG).show()
            return
        }

    }

    fun retake(){
        if (count==1){

            count -= 1
            bitMap1 = null
            bytes1 = null
            binding.cameraback.visibility = View.VISIBLE
            binding.retakeIV.visibility = View.GONE
            binding.usePhotoTV.visibility = View.INVISIBLE
            binding.image1IV.visibility = View.GONE
            binding.cancelImg1.visibility = View.GONE

            AnalyticsApplication.instance?.setPackageImage1(null)


        }else if (count==2){
            count -= 1
            bitMap2 = null
            bytes2 = null
            binding.image2IV.visibility = View.GONE
            binding.cancelImg2.visibility = View.GONE

            AnalyticsApplication.instance?.setPackageImage2(null)


        }else if (count==3){
            count -= 1
            bitMap3 = null
            bytes3 = null
            binding.saveIV.visibility = View.GONE
            binding.takeIV.visibility = View.VISIBLE
            binding.image3IV.visibility = View.GONE
            binding.cancelImg3.visibility = View.GONE

            AnalyticsApplication.instance?.setPackageImage3(null)

        }
    }

    fun saveImages(){

        usePhotos()
    }

    fun usePhotos(){

        AnalyticsApplication.instance?.setEmpId(count.toString())

        val intent = Intent()
        setResult(Activity.RESULT_OK,intent)
        finish()


    }

    fun getallcamerapackages(){



        adapter =
            ProcessAdapter_new(this, camerDao.getAllCameraPackages())

        val manager = LinearLayoutManager(this)
        binding.camerarecylerview.setHasFixedSize(true)

        binding.camerarecylerview.layoutManager = manager
        binding.camerarecylerview.adapter = adapter


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


    inner class ProcessAdapter_new(val context : Context, private val mList: List<CameraPackage>) : RecyclerView.Adapter<ProcessAdapter_new.ViewHolder>() {



        // create new views
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProcessAdapter_new.ViewHolder {
            // inflates the card_view_design view
            // that is used to hold list item
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.camera_item_list, parent, false)

            return ViewHolder(view)
        }


        @SuppressLint("SuspiciousIndentation")
        override fun onBindViewHolder(holder: ViewHolder, @SuppressLint("RecyclerView") position: Int) {




            val itemsViewModel = mList[position]


            val decodedString: ByteArray = Base64.decode(itemsViewModel.ImageString, Base64.DEFAULT)
            val decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)


               holder.image3IV.setImageBitmap(decodedByte)

            holder.cancel_img3.setOnClickListener {


                camerDao.deleteProcessPackages(itemsViewModel.ImageString)


                count -= 1
                isCapturing = false



                getallcamerapackages()



               /* finish()
                overridePendingTransition(0, 0)
                startActivity(intent)
                overridePendingTransition(0, 0)
*/
            }





        }




        // binds the list items to a viewh


        // return the number of the items in the list
        override fun getItemCount(): Int {
            return mList.size
        }

        // Holds the views for adding it to image and text
        inner class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {


            val image3IV: ImageView = itemView.findViewById(R.id.image3IV)
            val cancel_img3: ImageView = itemView.findViewById(R.id.cancel_img3)




        }






    }



}