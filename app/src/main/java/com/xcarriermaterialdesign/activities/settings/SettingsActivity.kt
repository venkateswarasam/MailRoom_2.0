package com.xcarriermaterialdesign.activities.settings

import android.Manifest
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.ImageDecoder
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.PopupMenu
import android.widget.TextView
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
import androidx.room.Room
import com.pixelcarrot.base64image.Base64Image
import com.squareup.picasso.MemoryPolicy
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso
import com.xcarriermaterialdesign.BuildConfig
import com.xcarriermaterialdesign.R
import com.xcarriermaterialdesign.activities.country.Country
import com.xcarriermaterialdesign.activities.login.MainActivity
import com.xcarriermaterialdesign.databinding.ActivitySettingsBinding
import com.xcarriermaterialdesign.model.ChangePasswordRequest
import com.xcarriermaterialdesign.model.ChangePasswordResponse
import com.xcarriermaterialdesign.model.GetProfileRequest
import com.xcarriermaterialdesign.model.GetProfileResponse
import com.xcarriermaterialdesign.model.GetProfileResponse1
import com.xcarriermaterialdesign.model.MailNotificationsInfoX
import com.xcarriermaterialdesign.model.MobileUserInfoXX
import com.xcarriermaterialdesign.model.ProfilePicRequest
import com.xcarriermaterialdesign.model.ProfilePicResponse
import com.xcarriermaterialdesign.model.UpdateProfileRequest
import com.xcarriermaterialdesign.model.UpdateResponse
import com.xcarriermaterialdesign.roomdatabase.CamerDao
import com.xcarriermaterialdesign.roomdatabase.CameraPackage
import com.xcarriermaterialdesign.roomdatabase.ProcessDatabase
import com.xcarriermaterialdesign.utils.AnalyticsApplication
import com.xcarriermaterialdesign.utils.ApplicationSharedPref
import com.xcarriermaterialdesign.utils.LoadingView
import com.xcarriermaterialdesign.utils.ServiceDialog
import com.xcarriermaterialdesign.utils.ServiceDialogNavigation
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.util.regex.Pattern


class SettingsActivity : AppCompatActivity() {



    private lateinit var binding:ActivitySettingsBinding

    val model: SettingsViewModel by viewModels()

    var profileImageUriString = ""

    var countrycode = ""


    // mail notifications
    val notifyall = false

    val notifyDelivered = false
    val notifyException = true
    var notifyMe = false
    val sms_save = false




    var save_state = false


    val araylist:ArrayList<String> = ArrayList()



    val araylist_states:ArrayList<String> = ArrayList()


    private lateinit var cameraPackage: List<CameraPackage>



    private val neededPermissions = arrayOf(
        Manifest.permission.CAMERA)


    var itemObj: JSONObject = JSONObject()
    var itemObjcode: JSONObject = JSONObject()
    var jsonArray:JSONArray = JSONArray()
    var jsonArraycode:JSONArray = JSONArray()


    private var latestTmpUri: Uri? = null

    private lateinit var takeImageResult: ActivityResultLauncher<Uri>


    private lateinit var camerDao: CamerDao


    private  val countriesArray = mutableListOf<String>()
    private var statesArray = mutableListOf<String>()
    private var ccarray = mutableListOf<String>()
    private var countrymatchArray = mutableListOf<String>()

    var states:List<Country>?= null



    @RequiresApi(Build.VERSION_CODES.O)

    val activityResultLauncher = registerForActivityResult<String, Uri>(
        ActivityResultContracts.GetContent()
    ) { result ->

        if (result != null) {

            convertUriToBase64AndUpload(result)
        }
    }


    //password pattern

    private val PASSWORD_PATTERNNEW= Pattern.compile( "^"+
            "(?=.*[0-9])"+
            "(?=.*[a-z])"+
            "(?=.*[A-Z])"+
            "(?=.*[!@#$%()'*,-./:;<=>?['\']^_'{|}&+=])"+
            "(?=\\S+$)"+
            ".{8,16}"+
            "$")



    var jsonObject1:JSONObject = JSONObject()





    @SuppressLint("NewApi")
    @RequiresApi(Build.VERSION_CODES.O)

    fun convertUriToBase64AndUpload(result : Uri)
    {
        profileImageUriString = result.toString()

        val source = ImageDecoder.createSource(this.contentResolver, result)
        val bitmap = ImageDecoder.decodeBitmap(source)


        val stream1 = ByteArrayOutputStream()
        val compressed = getResizedBitmap(bitmap,600)
        val imageInByte1 = stream1.toByteArray()
        val length1 = imageInByte1.size.toLong()

        println("==image==$compressed")

       // binding.profileimage.setImageBitmap(bitmap)

        Base64Image.encode(compressed) { base64 ->
            base64?.let {


                val profilePicRequest = ProfilePicRequest(ApplicationSharedPref.read(ApplicationSharedPref.COMPANY_ID,"")!!,
                    ApplicationSharedPref.read(ApplicationSharedPref.LOGINID,"")?.toInt(), ApplicationSharedPref.read(ApplicationSharedPref.PLANT_ID,"")!!,
                    base64)


                model.uploadProilePic(profilePicRequest)



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


    var textWatcher: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            // this function is called before text is edited
        }

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            // this function is called when text is edited

            val newpass = binding.newpassword.text.toString()
            val confirm = binding.confirmpassword.text.toString()


            if (s.isEmpty()&&newpass.isEmpty()&&confirm.isEmpty()){

                save_state = false


                binding.savebutton.text = getString(R.string.savechanges)

                return

            }

            else{



                save_state = true

                binding.savebutton.text = getString(R.string.changepassword)

                return

            }


        }

        override fun afterTextChanged(s: Editable) {

            val newpass = binding.newpassword.text.toString()
            val confirm = binding.confirmpassword.text.toString()


            if (s.isEmpty()&&newpass.isEmpty()&&confirm.isEmpty()){

                save_state = false

                binding.savebutton.text = getString(R.string.savechanges)

                return
            }


            else{

                save_state = true

                binding.savebutton.text = getString(R.string.changepassword)

                return

            }

            // this function is called after text is edited
        }
    }

    var textWatcher1: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            // this function is called before text is edited
        }

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            // this function is called when text is edited

            val oldpass = binding.oldpassword.text.toString()
            val confirm = binding.confirmpassword.text.toString()


            if (s.isEmpty()&&oldpass.isEmpty()&&confirm.isEmpty()){

                save_state = false


                binding.savebutton.text = getString(R.string.savechanges)

                return

            }

            else{



                save_state = true

                binding.savebutton.text = getString(R.string.changepassword)

                return

            }


        }

        override fun afterTextChanged(s: Editable) {

            val oldpass = binding.oldpassword.text.toString()
            val confirm = binding.confirmpassword.text.toString()


            if (s.isEmpty()&&oldpass.isEmpty()&&confirm.isEmpty()){

                save_state = false

                binding.savebutton.text = getString(R.string.savechanges)

                return
            }


            else{

                save_state = true

                binding.savebutton.text = getString(R.string.changepassword)

                return

            }

            // this function is called after text is edited
        }
    }

    var textWatcher2: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            // this function is called before text is edited
        }

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            // this function is called when text is edited
            val oldpass = binding.oldpassword.text.toString()
            val newpass = binding.newpassword.text.toString()

            if (s.isEmpty()&&oldpass.isEmpty()&&newpass.isEmpty()){

                save_state = false


                binding.savebutton.text = getString(R.string.savechanges)

                return

            }

            else{



                save_state = true

                binding.savebutton.text = getString(R.string.changepassword)

                return

            }


        }

        override fun afterTextChanged(s: Editable) {


            val oldpass = binding.oldpassword.text.toString()
            val newpass = binding.newpassword.text.toString()

            if (s.isEmpty()&&oldpass.isEmpty()&&newpass.isEmpty()){

                save_state = false

                binding.savebutton.text = getString(R.string.savechanges)

                return
            }


            else{

                save_state = true

                binding.savebutton.text = getString(R.string.changepassword)

                return

            }

            // this function is called after text is edited
        }
    }



    // countries


    fun checkstates(){


        ArrayAdapter(this, android.R.layout.simple_list_item_1, araylist_states).also {
                adapter ->
            binding.dropdownItemState.setAdapter(adapter)
        }

    }
    fun countrylist(){

        val jsonDataString = readJSONDataFromFile()





        val jsonObject = JSONObject(jsonDataString)




         jsonArray = jsonObject.getJSONArray("countries")



        for ( i in 0 until jsonArray.length()) {


            itemObj = jsonArray.getJSONObject(i)

            val name = itemObj.getString("country")

            countriesArray.add(name)

//            val statess = itemObj.getString("states").get(i)

//            println("==states==$statess")



            ArrayAdapter(this, android.R.layout.simple_list_item_1, countriesArray).also { adapter ->
                binding.dropdownitem.setAdapter(adapter)
            }



        }

    }

    fun countrycodecheck(){

        val jsonDataString = readJSONDataFromFile()

        val jsonObject = JSONObject(jsonDataString)

        jsonArraycode = jsonObject.getJSONArray("countries")


        for (i in 0 until jsonArraycode.length()){

            itemObjcode = jsonArraycode.getJSONObject(i)


            val name = itemObjcode.getString("country")


            if (binding.dropdownitem.text.toString() == name){




                countrycode = itemObjcode.getString("alpha2Code")


              //  Toast.makeText(this, countrycode, Toast.LENGTH_SHORT).show()

            }


        }


    }

    fun countryassign(code:String){


        val jsonDataString = readJSONDataFromFile()

        val jsonObject = JSONObject(jsonDataString)

        jsonArraycode = jsonObject.getJSONArray("countries")


        for (i in 0 until jsonArraycode.length()){

            itemObjcode = jsonArraycode.getJSONObject(i)


            val name = itemObjcode.getString("alpha2Code")


            if (code == name){




                val checkcountry = itemObjcode.getString("country")


                binding.dropdownitem.setText(checkcountry)


             //   Toast.makeText(this, checkcountry, Toast.LENGTH_SHORT).show()

            }


        }

    }


    fun countrymatch(){


        val country = binding.dropdownitem.text.toString()

        val jsonDataString1 = readJSONDataFromFileStates()

        println("==jsonlist==$jsonDataString1")

        val jsonArray1 = JSONArray(jsonDataString1)

        for (i in 0 until jsonArray1.length()){


            jsonObject1 = jsonArray1.getJSONObject(i)

            val country_name = jsonObject1.getString("country_name")

            countrymatchArray.add(country_name)

            if (country_name.contains(country)){

                val states = jsonObject1.getString("name")
                /* val countrcode = jsonObject1.getString("country_code")

                 println("==ccode==$countrcode")

                 ccarray.add(countrcode)



                 countrycode = ccarray.get(0)

                 println("==cmode==$countrycode")*/


                statesArray.add(states)



                ArrayAdapter(this, android.R.layout.simple_list_item_1, statesArray).also {
                        adapter ->
                    binding.dropdownItemState.setAdapter(adapter)
                }


            }




        }


    }


    @Throws(IOException::class)
    private fun readJSONDataFromFile(): String? {
        var inputStream: InputStream? = null
        val builder = StringBuilder()
        try {
            var jsonString: String? = null
            inputStream = resources.openRawResource(R.raw.countrylist)
            val bufferedReader = BufferedReader(
                InputStreamReader(inputStream, "UTF-8")
            )
            while (bufferedReader.readLine().also { jsonString = it } != null) {
                builder.append(jsonString)
            }
        } finally {
            inputStream?.close()
        }
        return String(builder)
    }

    @Throws(IOException::class)
    private fun readJSONDataFromFileStates(): String? {
        var inputStream: InputStream? = null
        val builder = StringBuilder()
        try {
            var jsonString: String? = null
            inputStream = resources.openRawResource(R.raw.states)
            val bufferedReader = BufferedReader(
                InputStreamReader(inputStream, "UTF-8")
            )
            while (bufferedReader.readLine().also { jsonString = it } != null) {
                builder.append(jsonString)
            }
        } finally {
            inputStream?.close()
        }
        return String(builder)
    }




    @Throws(IOException::class)
    private fun readJSONDataFromFileCountrycodes(): String? {
        var inputStream: InputStream? = null
        val builder = StringBuilder()
        try {
            var jsonString: String? = null
            inputStream = resources.openRawResource(R.raw.countriescodelist)
            val bufferedReader = BufferedReader(
                InputStreamReader(inputStream, "UTF-8")
            )
            while (bufferedReader.readLine().also { jsonString = it } != null) {
                builder.append(jsonString)
            }
        } finally {
            inputStream?.close()
        }
        return String(builder)
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

        val db = Room.databaseBuilder(
            applicationContext,
            ProcessDatabase::class.java, "Process_database"
        ).allowMainThreadQueries().build()

        camerDao = db.cameraDao()



        binding.oldpassword.addTextChangedListener(textWatcher)

        binding.newpassword.addTextChangedListener(textWatcher1)
        binding.confirmpassword.addTextChangedListener(textWatcher2)




        binding.toolbar.setNavigationOnClickListener {

            finish()
        }


        binding.logoutImg.setOnClickListener {



            logoutdialog()


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

                getprofiledata()

                //getprofiledata1()


            }


            else{


                ServiceDialog.ShowDialog(this, item.Result.ReturnMsg)
            }

        });



        // change password response

        model.changePasswordResponse.observe(this, Observer<ChangePasswordResponse> { item ->

            LoadingView.hideLoading()

            println("===statuscode==${item.StatusCode}")

            if (item.StatusCode == 200){




                ServiceDialogNavigation.ShowDialog(this, item.Result.ReturnMsg+" "+"Please login again")



            }


            else{


                ServiceDialog.ShowDialog(this, item.Result.ReturnMsg)
            }

        });



        // new code


        binding.savechanges.setOnClickListener {

            if (!save_state){

                println("==allstatues==${binding.notifyothers.isChecked}")


              //  countrycode = binding.dropdownitem.text.toString()



                val mailNotificationsInfo = MailNotificationsInfoX(binding.allstatues.isChecked,binding.delivered.isChecked,
                        binding.exceptions.isChecked,binding.notifyme.isChecked,
                    binding.notifyothers.isChecked,binding.recevied.isChecked,binding.othersmail.text.toString())

                    val mobileUserInfoXX = MobileUserInfoXX(binding.address1.text.toString(),
                        binding.address2.text.toString(),"","",binding.city.text.toString(),
                        ApplicationSharedPref.read(ApplicationSharedPref.COMPANY_ID,"")!!,
                        countrycode,
                        ApplicationSharedPref.read(ApplicationSharedPref.CUSTOMERNAME,"")!!,
                        ApplicationSharedPref.read(ApplicationSharedPref.DEPARTMENT,"")!!,
                        ApplicationSharedPref.read(ApplicationSharedPref.DESIGNATION,"")!!,
                        binding.emailaddress.text.toString(),
                        ApplicationSharedPref.read(ApplicationSharedPref.EMP_ID,"")!!,
                        "",binding.firstname.text.toString(),
                        binding.lastname.text.toString(),
                        ApplicationSharedPref.read(ApplicationSharedPref.LOGINID,"")?.toInt()!!,
                        "","",
                        binding.mobilenumber.text.toString(),
                        ApplicationSharedPref.read(ApplicationSharedPref.PLANT_ID,"")!!,
                        ApplicationSharedPref.read(ApplicationSharedPref.PLANTNAME,"")!!,"",
                        ApplicationSharedPref.read(ApplicationSharedPref.ROLEID,"")!!.toInt(),
                        binding.dropdownItemState.text.toString(),
                        ApplicationSharedPref.read(ApplicationSharedPref.USERALIAS,"")!!,
                        ApplicationSharedPref.read(ApplicationSharedPref.USERNAME,"")!!,
                        ApplicationSharedPref.read(ApplicationSharedPref.USERROLE,"")!!,
                        binding.postalcode.text.toString())


                    val updateProfileRequest = UpdateProfileRequest(mailNotificationsInfo,mobileUserInfoXX)


                    model.updateprofile(updateProfileRequest,binding.firstname.text.toString(),
                        binding.lastname.text.toString(),binding.address1.text.toString(),
                        binding.address2.text.toString(),binding.city.text.toString(),binding.dropdownItemState.text.toString(),
                        binding.postalcode.text.toString(),countrycode,
                        binding.emailaddress.text.toString(),binding.mobilenumber.text.toString())



            }


            else{


                val changePasswordRequest = ChangePasswordRequest(
                    ApplicationSharedPref.read(ApplicationSharedPref.COMPANY_ID,"")!!,
                    ApplicationSharedPref.read(ApplicationSharedPref.MS_EMAIL,"")!!,
                    binding.newpassword.text.toString(),
                    binding.oldpassword.text.toString(),
                    ApplicationSharedPref.read(ApplicationSharedPref.PLANT_ID,"")!!,
                    ApplicationSharedPref.read(ApplicationSharedPref.USERNAME,"")!!
                )


                model.updatepassword(changePasswordRequest,binding.oldpassword.text.toString(),
                    binding.newpassword.text.toString(),binding.confirmpassword.text.toString())
            }





        }



        // getprofile

        model.getprofileResponse.observe(this, Observer<GetProfileResponse> { item ->

            LoadingView.hideLoading()

            if (item.StatusCode == 200){

              //  ServiceDialog.ShowDialog(this, "Profile Data Updated Successfully")


                binding.firstname.setText(item.Result.MobileUserInfo.FirstName)
                binding.lastname.setText(item.Result.MobileUserInfo.LastName)
                binding.city.setText(item.Result.MobileUserInfo.City)


                countryassign(item.Result.MobileUserInfo.Country)


             /*   val name = itemObjcode.getString("alpha2Code")

                if (item.Result.MobileUserInfo.Country.contains(name)){


                  val countryname =  itemObjcode.getString("country")


                    binding.dropdownitem.setText(countryname)

                }*/


               /* if (item.Result.MobileUserInfo.Country == "US"){

                    binding.dropdownitem.setText("United States")

                    return@Observer

                }*/

            //    binding.dropdownitem.setText(item.Result.MobileUserInfo.Country)
                binding.dropdownItemState.setText(item.Result.MobileUserInfo.State)


                binding.postalcode.setText(item.Result.MobileUserInfo.ZipCode)
                binding.address1.setText(item.Result.MobileUserInfo.AddressLine1)
                binding.address2.setText(item.Result.MobileUserInfo.AddressLine2)
                binding.mobilenumber.setText(item.Result.MobileUserInfo.Phone)
                binding.userMobile.text = item.Result.MobileUserInfo.Phone


                binding.useremail.text = item.Result.MobileUserInfo.Email
                binding.emailaddress.setText(item.Result.MobileUserInfo.Email)
                binding.othersmail.setText(item.Result.MailNotificationsInfo.Others_Email)

                binding.username.text = item.Result.MobileUserInfo.FirstName+" "+item.Result.MobileUserInfo.LastName


                binding.notifyme.isChecked = item.Result.MailNotificationsInfo.NotifyMe
                binding.notifyothers.isChecked = item.Result.MailNotificationsInfo.NotifyOthers
                binding.exceptions.isChecked =  item.Result.MailNotificationsInfo.NotifyException
                binding.recevied.isChecked =  item.Result.MailNotificationsInfo.NotifyReceived
                binding.delivered.isChecked =  item.Result.MailNotificationsInfo.NotifyDelivered
                binding.allstatues.isChecked =  item.Result.MailNotificationsInfo.NotifyAllStatuses



                ApplicationSharedPref.write(ApplicationSharedPref.PROFILEIMAGE,item.Result.MobileUserInfo.ProfileImage)

                Picasso.get().load(item.Result.MobileUserInfo.ProfileImage).placeholder(R.drawable.account_circle_blue).memoryPolicy(
                    MemoryPolicy.NO_CACHE)
                    .networkPolicy(NetworkPolicy.NO_CACHE)
                    .into((binding.profileimage))




                countrylist()

                countrymatch()


                countrycodecheck()
                clearfields()




            }


            else{


                ServiceDialog.ShowDialog(this, item.Result.ReturnMsg)
            }









        });



        model.getprofileResponse1.observe(this, Observer<GetProfileResponse1> { item ->

           LoadingView.hideLoading()

            if (item.StatusCode == 200){



                binding.firstname.setText(item.Result.MobileUserInfo.FirstName)
                binding.lastname.setText(item.Result.MobileUserInfo.LastName)
                binding.city.setText(item.Result.MobileUserInfo.City)


                countryassign(item.Result.MobileUserInfo.Country)


                /*    if (item.Result.MobileUserInfo.Country == "US"){

                        binding.dropdownitem.setText("United States")

                        return@Observer

                    }*/


               /* val name = itemObjcode.getString("alpha2Code")

                if (item.Result.MobileUserInfo.Country.contains(name)){


                    val countryname =  itemObjcode.getString("country")


                    binding.dropdownitem.setText(countryname)

                }*/

           //    binding.dropdownitem.setText(item.Result.MobileUserInfo.Country)
                binding.dropdownItemState.setText(item.Result.MobileUserInfo.State)


                binding.postalcode.setText(item.Result.MobileUserInfo.ZipCode)
                binding.address1.setText(item.Result.MobileUserInfo.AddressLine1)
                binding.address2.setText(item.Result.MobileUserInfo.AddressLine2)
                binding.mobilenumber.setText(item.Result.MobileUserInfo.Phone)
                binding.userMobile.text = item.Result.MobileUserInfo.Phone


                binding.useremail.text = item.Result.MobileUserInfo.Email
                binding.emailaddress.setText(item.Result.MobileUserInfo.Email)
                binding.othersmail.setText(item.Result.MailNotificationsInfo.Others_Email)


                binding.username.text = item.Result.MobileUserInfo.FirstName+" "+item.Result.MobileUserInfo.LastName


                binding.notifyme.isChecked = item.Result.MailNotificationsInfo.NotifyMe
                binding.notifyothers.isChecked = item.Result.MailNotificationsInfo.NotifyOthers
                binding.exceptions.isChecked =  item.Result.MailNotificationsInfo.NotifyException
                binding.recevied.isChecked =  item.Result.MailNotificationsInfo.NotifyReceived
                binding.delivered.isChecked =  item.Result.MailNotificationsInfo.NotifyDelivered
                binding.allstatues.isChecked =  item.Result.MailNotificationsInfo.NotifyAllStatuses



                ApplicationSharedPref.write(ApplicationSharedPref.PROFILEIMAGE,item.Result.MobileUserInfo.ProfileImage)

                Picasso.get().load(item.Result.MobileUserInfo.ProfileImage).placeholder(R.drawable.account_circle_blue).memoryPolicy(
                    MemoryPolicy.NO_CACHE)
                    .networkPolicy(NetworkPolicy.NO_CACHE)
                    .into((binding.profileimage))




                countrylist()

                countrymatch()

                countrycodecheck()
                clearfields()



            }


            else{


                ServiceDialog.ShowDialog(this, item.Result.ReturnMsg)
            }









        });




        model.updateResponse.observe(this, Observer<UpdateResponse> { item ->

            LoadingView.hideLoading()

            if (item.StatusCode == 200){

               ServiceDialog.ShowDialog(this, item.Result.ReturnMsg)



                getprofiledata()


            }


            else{


                ServiceDialog.ShowDialog(this, item.Result.ReturnMsg)
            }

        });


        binding.dropdownitem.onItemClickListener =
            AdapterView.OnItemClickListener { parent, arg1, position, arg3 ->

                val item = parent.getItemAtPosition(position)

                statesArray.clear()
                binding.dropdownItemState.text.clear()






                countrymatch()

                countrycodecheck()







            }


        getprofiledata1()




        binding.notifyothers.setOnCheckedChangeListener { buttonView, isChecked ->




            if (isChecked){

                binding.othersmail.visibility = View.VISIBLE

            }

            else{

                binding.othersmail.visibility = View.GONE

            }


        }




        binding.sms.setOnCheckedChangeListener { buttonView, isChecked ->




            if (isChecked){




               ApplicationSharedPref.writeboolean(ApplicationSharedPref.SMSCHECK, true)

            }

            else{

                ApplicationSharedPref.writeboolean(ApplicationSharedPref.SMSCHECK, false)

            }


        }

        binding.sms.isChecked =
            ApplicationSharedPref.readboolean(ApplicationSharedPref.SMSCHECK,false)!!



        // country code check




    }



    fun clearfields(){

        binding.firstname.clearFocus()
        binding.lastname.clearFocus()
        binding.address1.clearFocus()
        binding.address2.clearFocus()
        binding.city.clearFocus()
        binding.postalcode.clearFocus()
        binding.mobilenumber.clearFocus()
        binding.emailaddress.clearFocus()
    }



    fun getprofiledata(){

        val getProfileRequest = GetProfileRequest(ApplicationSharedPref.read(ApplicationSharedPref.COMPANY_ID,"")!!,
            ApplicationSharedPref.read(ApplicationSharedPref.MS_EMAIL,"")!!, ApplicationSharedPref.read(ApplicationSharedPref.LOGINID,"")?.toInt()!!,
            ApplicationSharedPref.read(ApplicationSharedPref.PLANT_ID,"")!!
        )


        model.getprofiledata(getProfileRequest)

    }


    fun getprofiledata1(){

        val getProfileRequest = GetProfileRequest(ApplicationSharedPref.read(ApplicationSharedPref.COMPANY_ID,"")!!,
            ApplicationSharedPref.read(ApplicationSharedPref.MS_EMAIL,"")!!, ApplicationSharedPref.read(ApplicationSharedPref.LOGINID,"")?.toInt()!!,
            ApplicationSharedPref.read(ApplicationSharedPref.PLANT_ID,"")!!
        )


        model.getprofiledata1(getProfileRequest)

    }


    private fun logoutdialog(){

        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialog.setCancelable(false)
        dialog.setContentView(R.layout.info_layout_logout)

        val ok = dialog.findViewById<TextView>(R.id.oktext)
        val cancel = dialog.findViewById<TextView>(R.id.cancel)


        val lp = WindowManager.LayoutParams()
        val window = dialog.window
        lp.copyFrom(window!!.attributes)
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        window.attributes = lp
        dialog.show()

        cancel.setOnClickListener {

            dialog.dismiss()
        }


        ok.setOnClickListener {




            dialog.dismiss()


            ApplicationSharedPref.write(ApplicationSharedPref.LOGINCHECK,"false")


            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
            finish()


        }
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