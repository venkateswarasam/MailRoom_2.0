package com.xcarriermaterialdesign.ui.notifications

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.zxing.integration.android.IntentIntegrator
import com.xcarriermaterialdesign.R
import com.xcarriermaterialdesign.databinding.FragmentNotificationsBinding
import com.xcarriermaterialdesign.activities.scannerprocess.SimpleProcessActivty
import com.xcarriermaterialdesign.model.BuildingRequest
import com.xcarriermaterialdesign.model.BuildingResponse
import com.xcarriermaterialdesign.model.CheckRequest
import com.xcarriermaterialdesign.model.CheckinoutResponse
import com.xcarriermaterialdesign.model.GetProfileResponse
import com.xcarriermaterialdesign.ui.home.HomeViewModel
import com.xcarriermaterialdesign.utils.NetWorkService
import com.xcarriermaterialdesign.utils.NetworkChangeReceiver
import com.xcarriermaterialdesign.utils.NetworkConnection
import com.xcarriermaterialdesign.utils.*
import java.text.SimpleDateFormat
import java.util.*

class NotificationsFragment : Fragment(), NetworkChangeReceiver.NetCheckerReceiverListener {

    private var _binding: FragmentNotificationsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!


    lateinit var sharedPreference : SharedPreferences


    var checkintime:String=""

    var hour:String = ""
    var minutes:String = ""
    var seconds:String = ""


  //  val model: NotificationsViewModel by viewModels()



    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val notificationsViewModel =
            ViewModelProvider(this)[NotificationsViewModel::class.java]


        _binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        val root: View = binding.root


        notificationsViewModel.config(activity as AppCompatActivity)


        //setupScanner()
        (activity as AppCompatActivity).supportActionBar?.hide()

        val check = false

        if (!check){


            (activity as AppCompatActivity).startService(Intent( (activity as AppCompatActivity), NetWorkService::class.java))


        }

        if (arguments!= null){

            //  strtext = arguments!!.getString("decode")!!

            val bundle = arguments
            val message = bundle!!.getString("decode")

            requireActivity().runOnUiThread(java.lang.Runnable {


                binding!!.buildingText.setText(message)



            })

        }




        sharedPreference = (activity as AppCompatActivity).getSharedPreferences("CheckInPref", Context.MODE_PRIVATE)


        if (sharedPreference?.getString("time","")?.isNotBlank() == true)
        {

            binding.checkInDetailsLL.visibility = View.VISIBLE

            binding.checkintime.text = sharedPreference!!.getString("time","")
            binding.buildingText.setText(sharedPreference!!.getString("buildId",""))

            checkintime = sharedPreference.getString("timestamp","")!!

        }


        if (!NetworkConnection().isNetworkAvailable((activity as AppCompatActivity))) {

            _binding!!.profile!!.setImageResource(R.drawable.syncyellow)

        }
        else{

            _binding!!.profile!!.setImageResource(R.drawable.syncnew)

        }






        // response handling


        notificationsViewModel.buildingResponse.observe(activity as AppCompatActivity, Observer<BuildingResponse> { item ->

            LoadingView.hideLoading()

            if (item.StatusCode == 200){


               // Toast.makeText(activity as AppCompatActivity, item.Result.ReturnMsg, Toast.LENGTH_SHORT).show()

                checkintime =  SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date())


                val checkRequest = CheckRequest(binding.buildingText.text.toString(),
                    checkintime,"",
                    ApplicationSharedPref.read(ApplicationSharedPref.COMPANY_ID,"")!!,
                    "0",
                    ApplicationSharedPref.read(ApplicationSharedPref.LATTITUDE,"17.45")!!,
                    ApplicationSharedPref.read(ApplicationSharedPref.LOGINID,"")!!,
                    ApplicationSharedPref.read(ApplicationSharedPref.LONGITUDE,"72.56")!!,
                    "CHECKIN",
                    ApplicationSharedPref.read(ApplicationSharedPref.PLANT_ID,"")!!,
                )

                println("==checkinrequest==$checkRequest")


                notificationsViewModel.savebuilding(checkRequest)
            }


            else{


                _binding!!.buildingText.setText("")

                ServiceDialog.ShowDialog(activity as AppCompatActivity, item.Result.ReturnMsg)
            }









        });




        notificationsViewModel.checkinoutResponse.observe(activity as AppCompatActivity, Observer<CheckinoutResponse> { item ->

            LoadingView.hideLoading()



            if (item.StatusCode == 200){






                if (item.Result.Message == "Checked-in Successfully"){



                    //  Toast.makeText(activity as AppCompatActivity, item.Result.ReturnMsg, Toast.LENGTH_SHORT).show()

                    checkbuilding(item.Result.Message)


                    return@Observer
                }


                checkoutmessage(item.Result.DiffTime)





            }


            else{


                ServiceDialog.ShowDialog(activity as AppCompatActivity, item.Result.ReturnMsg)
            }









        });





        _binding!!.barcode.setOnClickListener()
        {

            if (sharedPreference?.getBoolean("checkIn",false)!!)
            {


                Toast.makeText(activity,"You are already Checked-In", Toast.LENGTH_SHORT).show()


                return@setOnClickListener
            }

            val intent = Intent(activity as AppCompatActivity, SimpleProcessActivty::class.java)
            startActivityForResult(intent,12345)

        }



        _binding!!.checkin.setOnClickListener {


            if (sharedPreference?.getBoolean("checkIn",false)!!)
            {


                Toast.makeText(activity,"You are already Checked-In", Toast.LENGTH_SHORT).show()


                return@setOnClickListener
            }



            val buildingRequest = BuildingRequest(binding.buildingText.text.toString(),
                ApplicationSharedPref.read(ApplicationSharedPref.COMPANY_ID,"")!!,
                ApplicationSharedPref.read(ApplicationSharedPref.PLANT_ID,"")!!,
            )


            notificationsViewModel.check_building(binding.buildingText.text.toString(),buildingRequest)



        }



        _binding!!.checkout.setOnClickListener {




            if (binding.buildingText.text.toString().isEmpty()){

                Toast.makeText(activity,"Please do Check-In before Check-Out", Toast.LENGTH_SHORT).show()

                return@setOnClickListener
            }


                val checkRequest = CheckRequest(binding.buildingText.text.toString(),
                    checkintime,
                    SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date()),
                    ApplicationSharedPref.read(ApplicationSharedPref.COMPANY_ID,"")!!,
                    "0",
                    ApplicationSharedPref.read(ApplicationSharedPref.LATTITUDE,"17.45")!!,
                    ApplicationSharedPref.read(ApplicationSharedPref.LOGINID,"")!!,
                    ApplicationSharedPref.read(ApplicationSharedPref.LONGITUDE,"72.56")!!,
                    "CHECKOUT",
                    ApplicationSharedPref.read(ApplicationSharedPref.PLANT_ID,"")!!,
                )


                notificationsViewModel.savebuilding(checkRequest)

        }










        return root
    }

    fun processnfc(decodevalue:String){






    }



    fun checkoutmessage(difftime:String){




        _binding!!.checkInDetailsLL.visibility = View.INVISIBLE

        _binding!!.buildingText.text!!.clear()

       // binding.infoLayout.visibility= View.VISIBLE

        val message = "Successfully Checked-Out at "+SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date())+" and the time difference is "+difftime


        ServiceDialog.ShowDialog(activity as AppCompatActivity,message)




        updateCheckinDetailsToPref(write = false)

    }



    fun checkbuilding(returnmsg:String){

        println("==return==$returnmsg")

        Toast.makeText(activity,returnmsg, Toast.LENGTH_SHORT).show()

        _binding!!.checkInDetailsLL.visibility = View.VISIBLE

        _binding!!.checkintime.text = checkintime




        updateCheckinDetailsToPref(write = true)

    }


    private fun timedifference(){


        val simpleDateFormat = SimpleDateFormat("HH:mm:ss")
        val date1: Date = simpleDateFormat.parse(checkintime)
        val date2: Date = simpleDateFormat.parse(SimpleDateFormat("HH:mm:ss").format(Date()))

        val differenceInMilliSeconds = Math.abs(date2.time - date1.time)
        var differenceInHours = (differenceInMilliSeconds / (60 * 60 * 1000)
                % 24)



        val differenceInMinutes = differenceInMilliSeconds / (60 * 1000) % 60

        val differenceInSeconds = differenceInMilliSeconds / 1000 % 60

        if (differenceInHours< 10){

            hour = "0$differenceInHours"

        }
        else{

            hour = differenceInHours.toString()
        }

        if (differenceInMinutes< 10){

            minutes = "0$differenceInMinutes"

        }
        else{

            minutes = differenceInMinutes.toString()
        }

        if (differenceInSeconds< 10){

            seconds = "0$differenceInSeconds"

        }
        else{

            seconds = differenceInSeconds.toString()
        }

        println(
            "Difference is " + differenceInHours + " hours "
                    + differenceInMinutes + " minutes "
                    + differenceInSeconds + " Seconds. ");
    }


    private fun time_difference(){

        val simpleDateFormat = SimpleDateFormat("HH:mm:ss")
        val date1: Date = simpleDateFormat.parse(checkintime)
        val date2: Date = simpleDateFormat.parse("")

        val differenceInMilliSeconds = Math.abs(date2.time - date1.time)
        val differenceInHours = (differenceInMilliSeconds / (60 * 60 * 1000)
                % 24)

        val differenceInMinutes = differenceInMilliSeconds / (60 * 1000) % 60

        val differenceInSeconds = differenceInMilliSeconds / 1000 % 60

        System.out.println(
            "Difference is " + differenceInHours + " hours "
                    + differenceInMinutes + " minutes "
                    + differenceInSeconds + " Seconds. ");
    }




    private fun updateCheckinDetailsToPref(write : Boolean)
    {

        var editor = sharedPreference.edit()

        if (write) {
            editor.putString("time", binding.checkintime.text as String)
            editor.putString("timestamp", checkintime)
            editor.putString("buildId", binding.buildingText.text.toString())
            editor.putBoolean("checkIn",true)
            editor.commit()
        }
        else

        {
            editor.clear()
            editor.apply()
        }
    }











    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun showMessage(isConnected: Boolean) {
        if (!isConnected) {


            binding.profile.setImageResource(R.drawable.syncnew)

        } else {


            binding.profile.setImageResource(R.drawable.syncyellow)





        }


    }

    override fun onResume() {
        super.onResume()
        NetworkChangeReceiver.netConnectionCheckerReceiver = this
    }
    override fun onNetworkConnectionChanged(isConnected: Boolean) {
        showMessage(isConnected)

        if (isConnected){

            binding.profile.setImageResource(R.drawable.syncnew)





        }
        else{

            binding.profile.setImageResource(R.drawable.syncyellow)




        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when(requestCode){
            12345->{

                if (resultCode == AppCompatActivity.RESULT_OK) {
                    val name = data!!.getStringExtra("barcode")
                    println("==name$name")
                     binding.buildingText!!.setText(name)
                }



            }








        }
    }

}