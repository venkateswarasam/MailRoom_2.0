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
import androidx.lifecycle.ViewModelProvider
import com.google.zxing.integration.android.IntentIntegrator
import com.xcarriermaterialdesign.R
import com.xcarriermaterialdesign.databinding.FragmentNotificationsBinding
import com.xcarriermaterialdesign.scanner.SimpleProcessActivty
import com.xcarriermaterialdesign.utils.*
import java.text.SimpleDateFormat
import java.util.*

class NotificationsFragment : Fragment(), NetworkChangeReceiver.NetCheckerReceiverListener {

    private var _binding: FragmentNotificationsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private var qrScanIntegrator: IntentIntegrator? = null

    lateinit var sharedPreference : SharedPreferences


    var checkintime:String=""

    var hour:String = ""
    var minutes:String = ""
    var seconds:String = ""



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

        setupScanner()
        (activity as AppCompatActivity).supportActionBar?.hide()




        /* val textView: TextView = binding.textNotifications
         notificationsViewModel.text.observe(viewLifecycleOwner) {
             textView.text = it
         }*/

        (activity as AppCompatActivity).startService(Intent( (activity as AppCompatActivity), NetWorkService::class.java))


        sharedPreference = (activity as AppCompatActivity).getSharedPreferences("CheckInPref", Context.MODE_PRIVATE)


        if (sharedPreference?.getString("time","")?.isNotBlank() == true)
        {

            binding.checkInDetailsLL.visibility = View.VISIBLE

            binding.checkintime.text = sharedPreference!!.getString("time","")
            binding.buildingText.setText(sharedPreference!!.getString("buildId",""))

            checkintime = sharedPreference!!.getString("timestamp","")!!

        }


        if (!NetworkConnection().isNetworkAvailable((activity as AppCompatActivity))) {

            binding.profile!!.setImageResource(R.drawable.syncyellow)

        }
        else{

            binding.profile!!.setImageResource(R.drawable.syncnew)

        }


        val check = false

        if (!check){

            //initactionbar()

        }




/*
        val batchScanResultLauncher  = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {

            if (it.resultCode == AppCompatActivity.RESULT_OK) {
                val intentResult = IntentIntegrator.parseActivityResult(it.resultCode, it.data)


                Toast.makeText(activity, intentResult.contents, Toast.LENGTH_SHORT).show()

                // binding.batchIdTV.setText(intentResult.contents.toString())
            }
        }
*/

        binding.barcode.setOnClickListener()
        {

            if (sharedPreference?.getBoolean("checkIn",false)!!)
            {


                Toast.makeText(activity,"You are already Checked-In", Toast.LENGTH_SHORT).show()


                return@setOnClickListener
            }

            val intent = Intent(activity as AppCompatActivity, SimpleProcessActivty::class.java)
            startActivityForResult(intent,12345)

        }



        binding.checkin.setOnClickListener {

            if (sharedPreference?.getBoolean("checkIn",false)!!)
            {


                Toast.makeText(activity,"You are already Checked-In", Toast.LENGTH_SHORT).show()


                return@setOnClickListener
            }





            if (binding.buildingText.text.toString().isEmpty()){

                Toast.makeText(activity,"Please Enter or Scan Building Name", Toast.LENGTH_SHORT).show()
            }
            else{



                Toast.makeText(activity,"Checked in successfully", Toast.LENGTH_SHORT).show()

                binding.checkInDetailsLL.visibility = View.VISIBLE

                binding.checkintime.text = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date())






                 checkintime = SimpleDateFormat("HH:mm:ss").format(Date())

                updateCheckinDetailsToPref(write = true)



            }





        }

        binding.oktext.setOnClickListener {

            binding.infoLayout.visibility= View.GONE

        }

        binding.checkout.setOnClickListener {




            if (binding.buildingText.text.toString().isEmpty()){

                Toast.makeText(activity,"Please do Check-In before Check-Out", Toast.LENGTH_SHORT).show()
            }
            else{


                binding.checkInDetailsLL.visibility = View.INVISIBLE

                binding.buildingText.setText("")

                binding.infoLayout.visibility= View.VISIBLE



                val simpleDateFormat = SimpleDateFormat("HH:mm:ss")
                val date1: Date = simpleDateFormat.parse(checkintime)
                val date2: Date = simpleDateFormat.parse(SimpleDateFormat("HH:mm:ss").format(Date()))

                val differenceInMilliSeconds = Math.abs(date2.time - date1.time)
                var differenceInHours = (differenceInMilliSeconds / (60 * 60 * 1000)
                        % 24)



                val differenceInMinutes = differenceInMilliSeconds / (60 * 1000) % 60

                val differenceInSeconds = differenceInMilliSeconds / 1000 % 60

                //Toast.makeText(activity as AppCompatActivity, this, differenceInSeconds.toString(), Toast.LENGTH_SHORT)



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





                binding.checkoutMsg.text = "Successfully Checked-Out at "+SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date())+" and the time difference is "+hour+":"+minutes+":"+seconds


                updateCheckinDetailsToPref(write = false)

            }




        }














        // initactionbar()
        return root
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





    private fun setupScanner() {
        qrScanIntegrator = IntentIntegrator(activity)
        qrScanIntegrator?.setOrientationLocked(false)
        qrScanIntegrator?.setPrompt("")
        qrScanIntegrator?.captureActivity

    }



    private fun initactionbar(){

        (activity as AppCompatActivity).supportActionBar?.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
        (activity as AppCompatActivity).supportActionBar?.setDisplayShowCustomEnabled(true)
        (activity as AppCompatActivity).supportActionBar?.setCustomView(R.layout.actionbarnew)
        val view1: View =  (activity as AppCompatActivity).supportActionBar!!.customView
        val toolbar: Toolbar = view1.parent as Toolbar
        toolbar.setContentInsetsAbsolute(0, 0)
     //   toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24)
        val headertext:TextView = view1.findViewById(R.id.headertext)
        headertext.text = getString(R.string.checkin)
        val profile: ImageView = view1.findViewById(R.id.profile)
       // val back: ImageView = view1.findViewById(R.id.back)

        profile.setImageResource(R.drawable.ic_outline_save_24)


        profile.setOnClickListener {


            Toast.makeText(activity,"checkIn successfully", Toast.LENGTH_SHORT).show()

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