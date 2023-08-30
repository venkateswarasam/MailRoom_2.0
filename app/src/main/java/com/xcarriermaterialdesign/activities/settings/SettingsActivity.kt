package com.xcarriermaterialdesign.activities.settings

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.pixelcarrot.base64image.Base64Image
import com.xcarriermaterialdesign.BuildConfig
import com.xcarriermaterialdesign.activities.camera.CameraActivity
import com.xcarriermaterialdesign.R
import com.xcarriermaterialdesign.activities.login.MainActivity
import com.xcarriermaterialdesign.databinding.ActivitySettingsBinding
import com.xcarriermaterialdesign.model.ProfilePicRequest
import com.xcarriermaterialdesign.model.ProfilePicResponse
import com.xcarriermaterialdesign.utils.AnalyticsApplication
import com.xcarriermaterialdesign.utils.ApplicationSharedPref
import com.xcarriermaterialdesign.utils.LoadingView
import com.xcarriermaterialdesign.utils.ServiceDialog
import java.io.ByteArrayOutputStream
import java.io.File


class SettingsActivity : AppCompatActivity() {



    private lateinit var binding:ActivitySettingsBinding

    val model: SettingsViewModel by viewModels()

    var profileImageUriString = ""


    private val neededPermissions = arrayOf(
        Manifest.permission.CAMERA)




    private var latestTmpUri: Uri? = null

    private lateinit var takeImageResult: ActivityResultLauncher<Uri>

    @RequiresApi(Build.VERSION_CODES.O)

    val activityResultLauncher = registerForActivityResult<String, Uri>(
        ActivityResultContracts.GetContent()
    ) { result ->

        if (result != null) {

            convertUriToBase64AndUpload(result)
        }
    }

    @SuppressLint("NewApi")
    @RequiresApi(Build.VERSION_CODES.O)

    fun convertUriToBase64AndUpload(result : Uri)
    {
        profileImageUriString = result.toString()

        val source = ImageDecoder.createSource(this.contentResolver, result)
        val bitmap = ImageDecoder.decodeBitmap(source)


        val stream1 = ByteArrayOutputStream()
        val compressed = getResizedBitmap(bitmap,1500)
        val imageInByte1 = stream1.toByteArray()
        val length1 = imageInByte1.size.toLong()

        binding.profileimage.setImageBitmap(bitmap)

        Base64Image.encode(compressed) { base64 ->
            base64?.let {


                val profilePicRequest = ProfilePicRequest(ApplicationSharedPref.read(ApplicationSharedPref.COMPANY_ID,"")!!,
                    ApplicationSharedPref.read(ApplicationSharedPref.LOGINID,"")?.toInt(), ApplicationSharedPref.read(ApplicationSharedPref.PLANT_ID,"")!!,base64)


                model.uploadProilePic(profilePicRequest)

              //  model.uploadProilePic(base64)

                println("==image==$base64")
            }
        }
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



    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AnalyticsApplication.instance!!.setLocaleFa(this)

      // setContentView(R.layout.activity_settings)

        model.config(this)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_settings)


        ApplicationSharedPref.init(this)

        supportActionBar?.hide()


        binding.profileimage.setOnClickListener {

            val intent = Intent(this, CameraActivity::class.java)
            startActivity(intent)
        }


        binding.toolbar.setNavigationOnClickListener {

            finish()
        }


        binding.logoutImg.setOnClickListener {

            ApplicationSharedPref.write(ApplicationSharedPref.LOGINCHECK,"false")


            val intent = Intent(this, MainActivity::class.java)

            startActivity(intent)
        }

        takeImageResult = registerForActivityResult(ActivityResultContracts.TakePicture()) { isSuccess ->

            @RequiresApi(Build.VERSION_CODES.O)
            if (isSuccess) {
                latestTmpUri?.let { uri ->

                    convertUriToBase64AndUpload(uri)

                    binding.profileimage.setImageURI(uri)

                }
            }
        }




        binding.profileimage.setOnClickListener()
        {
            showPopup(binding.profileimage)

        }


        model.profilePicResponse.observe(this, Observer<ProfilePicResponse> { item ->

            LoadingView.hideLoading()

            if (item.StatusCode == 200){

                ServiceDialog.ShowDialog(this, item.Result.ReturnMsg)
            }


            else{


                ServiceDialog.ShowDialog(this, item.Result.ReturnMsg)
            }









        });



    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun showPopup(view: View) {
        val popup = PopupMenu(this, view, Gravity.END)
        popup.inflate(R.menu.profilepicoptionsmenu)

        popup.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { item: MenuItem? ->

            when (item!!.itemId) {
                R.id.header2 -> {

                    var check = checkPermission()
                    if (check) {
                        launchCamera()
                    }

                }
                R.id.header3 -> {


                    activityResultLauncher.launch("image/*");

                }
                R.id.header4 -> {

                }
            }

            true
        })

        popup.show()
    }


    fun launchCamera()
    {
        lifecycleScope.launchWhenStarted {
            getTmpFileUri().let { uri ->
                latestTmpUri = uri
                takeImageResult.launch(uri)
            }
        }
    }


    private fun getTmpFileUri(): Uri {
        val tmpFile = File.createTempFile("tmp_image_file", ".png", cacheDir).apply {
            createNewFile()
            deleteOnExit()
        }

        return FileProvider.getUriForFile(
            this,
            "${BuildConfig.APPLICATION_ID}.provider",
            tmpFile
        )
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
        ActivityCompat.requestPermissions(this, permissions, REQUEST_CODE)
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
                            this,
                            R.string.permission_warning,
                            Toast.LENGTH_LONG
                        ).show()
//                        showPermissionMsg.visibility = View.VISIBLE
                        return
                    }
                }

                launchCamera()
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    companion object {
        const val REQUEST_CODE = 100
    }



}