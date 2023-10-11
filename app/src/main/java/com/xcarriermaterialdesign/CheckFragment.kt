package com.xcarriermaterialdesign

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.xcarriermaterialdesign.activities.scannerprocess.SimpleProcessActivty
import com.xcarriermaterialdesign.databinding.FragmentCheckBinding
import com.xcarriermaterialdesign.model.BuildingRequest
import com.xcarriermaterialdesign.model.BuildingResponse
import com.xcarriermaterialdesign.model.CheckRequest
import com.xcarriermaterialdesign.model.CheckinoutResponse
import com.xcarriermaterialdesign.utils.ApplicationSharedPref
import com.xcarriermaterialdesign.utils.LoadingView
import com.xcarriermaterialdesign.utils.NetWorkService
import com.xcarriermaterialdesign.utils.NetworkChangeReceiver
import com.xcarriermaterialdesign.utils.NetworkConnection
import com.xcarriermaterialdesign.utils.ServiceDialog
import org.jetbrains.anko.toast
import java.text.SimpleDateFormat
import java.util.Date


open class CheckFragment : Fragment(),NetworkChangeReceiver.NetCheckerReceiverListener {


    lateinit var sharedPreference : SharedPreferences


    var checkintime:String=""



    private var binding:FragmentCheckBinding? = null

    val notificationsViewModel: CheckViewModel by viewModels()

    var strtext:String = ""






    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        notificationsViewModel.config(activity as AppCompatActivity)


        binding = FragmentCheckBinding.inflate(inflater, container, false)
        val root: View = binding!!.root


        (activity as AppCompatActivity).supportActionBar?.hide()





        val check = false

        if (!check){


            (activity as AppCompatActivity).startService(Intent( (activity as AppCompatActivity), NetWorkService::class.java))


        }


        if (arguments!= null){

          //  strtext = arguments!!.getString("decode")!!

            val bundle = arguments
            val message = bundle!!.getString("decode")

            processnfc(message!!)
        }


      /*  if (!ApplicationSharedPref.readboolean(ApplicationSharedPref.BUILDINGCHECK,false)!!){


            binding!!.buildingText.setText(ApplicationSharedPref.read(ApplicationSharedPref.BUILDING,""))

        }*/








        sharedPreference = (activity as AppCompatActivity).getSharedPreferences("CheckInPref", Context.MODE_PRIVATE)


        if (sharedPreference.getString("time","")?.isNotEmpty() == true)
        {

            binding!!.checkInDetailsLL.visibility = View.VISIBLE

            binding!!.checkintime.text = sharedPreference.getString("time","")
            binding!!.buildingText.setText(sharedPreference.getString("buildId",""))

            checkintime = sharedPreference.getString("timestamp","")!!


          /*  binding!!.buildingText.isFocusable = false
            binding!!.buildingText.isClickable = false*/




        }


        if (!NetworkConnection().isNetworkAvailable((activity as AppCompatActivity))) {

            binding!!.profile.setImageResource(R.drawable.syncyellow)

        }
        else{

            binding!!.profile.setImageResource(R.drawable.syncnew)

        }






        // response handling






        binding!!.barcode.setOnClickListener()
        {

            if (sharedPreference?.getBoolean("checkIn",false)!!)
            {


                ServiceDialog.ShowDialog(activity as AppCompatActivity,"You are already Checked-In")


               // (activity as AppCompatActivity).toast("You are already Checked-In")


            //    Toast.makeText(activity,"You are already Checked-In", Toast.LENGTH_SHORT).show()


                return@setOnClickListener
            }

            val intent = Intent(activity as AppCompatActivity, SimpleProcessActivty::class.java)
            startActivityForResult(intent,12345)

        }



        binding!!.checkin.setOnClickListener {


            if (sharedPreference?.getBoolean("checkIn",false)!!)
            {

                ServiceDialog.ShowDialog(activity as AppCompatActivity,"You are already Checked-In")


              //  Toast.makeText(activity,"You are already Checked-In", Toast.LENGTH_SHORT).show()


            }

            else{


                val buildingRequest = BuildingRequest(
                    binding!!.buildingText.text.toString(),
                    ApplicationSharedPref.read(ApplicationSharedPref.COMPANY_ID, "")!!,
                    ApplicationSharedPref.read(ApplicationSharedPref.PLANT_ID, "")!!,
                )


                notificationsViewModel.check_building( binding!!.buildingText.text.toString(),buildingRequest)

            }





        }



        binding!!.checkout.setOnClickListener {



            val bui= sharedPreference.getString("buildId","")


            if (bui!! == ""){


                if (binding!!.buildingText.text.toString().isEmpty()){

                    ServiceDialog.ShowDialog(activity as AppCompatActivity,"Please do Check-In before Check-Out")

                }


            }

            else{


                if (bui!= binding!!.buildingText.text.toString()){



                    ServiceDialog.ShowDialog(activity as AppCompatActivity,"Please enter valid Building Id")

                }
                else{


                    val checkRequest = CheckRequest(
                        binding!!.buildingText.text.toString(),
                        checkintime,
                        SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date()),
                        ApplicationSharedPref.read(ApplicationSharedPref.COMPANY_ID, "")!!,
                        "0",
                        ApplicationSharedPref.read(ApplicationSharedPref.LATTITUDE, "17.45")!!,
                        ApplicationSharedPref.read(ApplicationSharedPref.LOGINID, "")!!,
                        ApplicationSharedPref.read(ApplicationSharedPref.LONGITUDE, "72.56")!!,
                        "CHECKOUT",
                        ApplicationSharedPref.read(ApplicationSharedPref.PLANT_ID, "")!!,
                    )


                    notificationsViewModel.savebuilding(checkRequest)

                }






            }







        }














        responses()

        return  root






        // return inflater.inflate(R.layout.fragment_check, container, false)
    }



    fun processnfc(decodevalue:String){




        requireActivity().runOnUiThread(java.lang.Runnable {


            binding!!.buildingText.setText(decodevalue)



        })

    }













    private fun responses()
    {

        println("==response==${"invalid checks happend"}")


        notificationsViewModel.buildingResponse.observe(activity as AppCompatActivity, Observer<BuildingResponse> { item ->

            LoadingView.hideLoading()

            if (item.StatusCode == 200){


                // Toast.makeText(activity as AppCompatActivity, item.Result.ReturnMsg, Toast.LENGTH_SHORT).show()

                checkintime =  SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date())


                val checkRequest = CheckRequest(
                    binding!!.buildingText.text.toString(),
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


                binding!!.buildingText.setText("")

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

    }

    fun checkoutmessage(difftime:String){




        binding!!.checkInDetailsLL.visibility = View.INVISIBLE



        binding!!.buildingText.text!!.clear()

      /*  binding!!.buildingText.isFocusable = true
        binding!!.buildingText.isClickable = true*/

        // binding.infoLayout.visibility= View.VISIBLE

        val message = "Successfully Checked-Out at "+SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date())+" and the time difference is "+difftime


        ServiceDialog.ShowDialog(activity as AppCompatActivity,message)




        updateCheckinDetailsToPref(write = false)

    }



    fun checkbuilding(returnmsg:String){

        println("==return==$returnmsg")


        ServiceDialog.ShowDialog(activity as AppCompatActivity,returnmsg)

      //  Toast.makeText(activity,returnmsg, Toast.LENGTH_SHORT).show()

        binding!!.checkInDetailsLL.visibility = View.VISIBLE

        binding!!.checkintime.text = checkintime


      /*  binding!!.buildingText.isFocusable = false
        binding!!.buildingText.isClickable = false*/




        updateCheckinDetailsToPref(write = true)

    }






    private fun updateCheckinDetailsToPref(write : Boolean)
    {

        var editor = sharedPreference.edit()

        if (write) {
            editor.putString("time",  binding!!.checkintime.text as String)
            editor.putString("timestamp", checkintime)
            editor.putString("buildId",  binding!!.buildingText.text.toString())
            editor.putBoolean("checkIn",true)
            editor.commit()
        }
        else

        {
            editor.clear()
            editor.apply()
        }
    }













    private fun showMessage(isConnected: Boolean) {
        if (!isConnected) {


            binding!!.profile.setImageResource(R.drawable.syncnew)

        } else {


            binding!!.profile.setImageResource(R.drawable.syncyellow)





        }


    }

    override fun onResume() {
        super.onResume()
        NetworkChangeReceiver.netConnectionCheckerReceiver = this
    }
    override fun onNetworkConnectionChanged(isConnected: Boolean) {
        showMessage(isConnected)

        if (isConnected){

            binding!!.profile.setImageResource(R.drawable.syncnew)





        }
        else{

            binding!!.profile.setImageResource(R.drawable.syncyellow)




        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when(requestCode){
            12345->{

                if (resultCode == AppCompatActivity.RESULT_OK) {
                    val name = data!!.getStringExtra("barcode")
                    println("==name$name")

                    if (name!!.contains(" ")){

                        ServiceDialog.ShowDialog(activity, "Please Enter or Scan Building Name")

                    }

                    else{


                        binding!!.buildingText!!.setText(name)

                    }






                }



            }








        }
    }



}