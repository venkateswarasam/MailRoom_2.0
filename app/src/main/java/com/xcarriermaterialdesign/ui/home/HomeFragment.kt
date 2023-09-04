package com.xcarriermaterialdesign.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.zxing.integration.android.IntentIntegrator
import com.squareup.picasso.MemoryPolicy
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso
import com.xcarriermaterialdesign.R
import com.xcarriermaterialdesign.TabNavigations
import com.xcarriermaterialdesign.activities.dashboard.DashboardViewModel
import com.xcarriermaterialdesign.activities.settings.SettingsActivity
import com.xcarriermaterialdesign.databinding.FragmentHomeBinding
import com.xcarriermaterialdesign.activities.manual.ManualProcessPackageActivity
import com.xcarriermaterialdesign.activities.scanner.SimpleScannerActivity
import com.xcarriermaterialdesign.model.GetProfileRequest
import com.xcarriermaterialdesign.model.GetProfileResponse
import com.xcarriermaterialdesign.utils.ApplicationSharedPref
import com.xcarriermaterialdesign.utils.LoadingView
import com.xcarriermaterialdesign.utils.NetWorkService
import com.xcarriermaterialdesign.utils.NetworkChangeReceiver
import com.xcarriermaterialdesign.utils.NetworkConnection
import com.xcarriermaterialdesign.utils.ServiceDialog


class HomeFragment : Fragment(), NetworkChangeReceiver.NetCheckerReceiverListener {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private var qrScanIntegrator: IntentIntegrator? = null


    internal var count = 10

    val model: HomeViewModel by viewModels()



    private fun showMessage(isConnected: Boolean) {
        if (!isConnected) {

            binding.onlinepackages.visibility =  View.VISIBLE
            binding.offlineconnection.visibility =  View.GONE
            binding.offlinepackages.visibility =  View.GONE


        } else {



            binding.onlinepackages.visibility =  View.GONE
            binding.offlineconnection.visibility =  View.VISIBLE
            binding.offlinepackages.visibility =  View.VISIBLE

            //  save!!.setImageDrawable(resources.getDrawable(R.drawable.savegreen))

        }


    }



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        model.config(activity as AppCompatActivity)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

      /*  homeViewModel.text.observe(viewLifecycleOwner) {
           // textView.text = it
        }*/
        (activity as AppCompatActivity).supportActionBar?.hide()

        //setupScanner()

        val check = false

        if (!check){


            (activity as AppCompatActivity).startService(Intent( (activity as AppCompatActivity), NetWorkService::class.java))


        }



        if (!NetworkConnection().isNetworkAvailable((activity as AppCompatActivity))) {


            binding.onlinepackages.visibility =  View.GONE
          binding.offlineconnection.visibility =  View.VISIBLE
            binding.offlinepackages.visibility =  View.VISIBLE

           // showBottomSheetDialog()
        }
        else{




          /*  val handler = Handler()
            handler.postDelayed(
                {

                    binding.onlinepackages.visibility =  View.GONE

                },
                1000
            )*/

          /*  count = 10

            val timer = Timer()
            //Set the schedule function and rate
            //Set the schedule function and rate
            timer.scheduleAtFixedRate(object : TimerTask() {
                override fun run() {

                    (activity as AppCompatActivity)?.runOnUiThread { timer_function() }
                }
            }, 0, 1000)*/



            binding.onlinepackages.visibility =  View.VISIBLE
            binding.offlineconnection.visibility =  View.GONE
            binding.offlinepackages.visibility =  View.GONE
        }














        _binding!!.profile.setOnClickListener {

            val intent = Intent(activity, SettingsActivity::class.java)
            activity?.startActivity(intent)

        }


        val batchScanResultLauncher  = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {

            if (it.resultCode == AppCompatActivity.RESULT_OK) {
                val intentResult = IntentIntegrator.parseActivityResult(it.resultCode, it.data)


                Toast.makeText(activity, intentResult.contents, Toast.LENGTH_SHORT).show()

               // binding.batchIdTV.setText(intentResult.contents.toString())
            }
        }

        binding.scanner.setOnClickListener()
        {




            val intent = Intent(activity, SimpleScannerActivity::class.java)
            activity?.startActivity(intent)

            // batchScanResultLauncher.launch(qrScanIntegrator?.createScanIntent())
        }



        binding.manualPackage.setOnClickListener()
        {




            val intent = Intent(activity, ManualProcessPackageActivity::class.java)
            activity?.startActivity(intent)

            // batchScanResultLauncher.launch(qrScanIntegrator?.createScanIntent())
        }



        // getprofile


        model.getprofileResponse.observe(activity as AppCompatActivity, Observer<GetProfileResponse> { item ->

            LoadingView.hideLoading()

            if (item.StatusCode == 200){


              //  binding.headertext.text = "Hello"+" "+item.Result.MobileUserInfo.FirstName



                ApplicationSharedPref.write(ApplicationSharedPref.PROFILEIMAGE,item.Result.MobileUserInfo.ProfileImage)



              /*  Picasso.get().load(ApplicationSharedPref.read(ApplicationSharedPref.PROFILEIMAGE,"")).placeholder(R.drawable.ic_outline_account_circle_24).memoryPolicy(
                    MemoryPolicy.NO_CACHE)
                    .networkPolicy(NetworkPolicy.NO_CACHE)
                    .into((binding.profile))*/


            }


            else{


                ServiceDialog.ShowDialog(activity as AppCompatActivity, item.Result.ReturnMsg)
            }









        });
















        val getProfileRequest = GetProfileRequest(ApplicationSharedPref.read(ApplicationSharedPref.COMPANY_ID,"")!!,
            ApplicationSharedPref.read(ApplicationSharedPref.MS_EMAIL,"")!!, ApplicationSharedPref.read(ApplicationSharedPref.LOGINID,"")?.toInt()!!,
            ApplicationSharedPref.read(ApplicationSharedPref.PLANT_ID,"")!!
        )


        model.getprofiledata(getProfileRequest)










        return root
    }


    private fun initActionBar() {

        (activity as AppCompatActivity).supportActionBar?.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
        (activity as AppCompatActivity).supportActionBar?.setDisplayShowCustomEnabled(true)
        (activity as AppCompatActivity).supportActionBar?.setCustomView(R.layout.action_bar)
        val view1: View = (activity as AppCompatActivity).supportActionBar!!.customView
        val toolbar: Toolbar = view1.parent as Toolbar
        toolbar.setContentInsetsAbsolute(0, 0)

        val headertext:TextView = view1.findViewById(R.id.headertext)
        headertext.text = getString(R.string.goodafter)
        val profile: ImageView = view1.findViewById(R.id.profile)
        val back: ImageView = view1.findViewById(R.id.back)

        back.visibility = View.GONE
        profile.setImageResource(R.drawable.ic_outline_account_circle_24)



        profile.setOnClickListener {

            val intent = Intent(activity, SettingsActivity::class.java)

            startActivity(intent)

        }









    }



    private fun timer_function() {


        if (count > 0) {
            count -= 1

        }
        if (count == 0) {

            binding.onlinepackages!!.visibility = View.GONE


        }
    }









    private fun setupScanner() {
        qrScanIntegrator = IntentIntegrator(activity)
        qrScanIntegrator?.setOrientationLocked(false)
        qrScanIntegrator?.setPrompt("")
        qrScanIntegrator?.captureActivity

    }




    override fun onResume() {
        super.onResume()
        NetworkChangeReceiver.netConnectionCheckerReceiver =  this
    }







    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onNetworkConnectionChanged(isConnected: Boolean) {

       // showMessage(isConnected)

        if (isConnected){



          /*  val handler = Handler()
            handler.postDelayed(
                {

                    binding.onlinepackages.visibility =  View.GONE

                },
                1000
            )*/


           /* count = 10

            val timer = Timer()
            //Set the schedule function and rate
            //Set the schedule function and rate
            timer.scheduleAtFixedRate(object : TimerTask() {
                override fun run() {

                    activity?.runOnUiThread { timer_function() }
                }
            }, 0, 1000)
*/
         //   binding.profile.setImageResource(R.drawable.syncnew)


            binding.onlinepackages.visibility =  View.VISIBLE
         binding.offlineconnection.visibility =  View.GONE
            binding.offlinepackages.visibility =  View.GONE


        }
        else{

            binding.onlinepackages.visibility =  View.GONE
           binding.offlineconnection.visibility =  View.VISIBLE
            binding.offlinepackages.visibility =  View.VISIBLE

            //showBottomSheetDialog()
          //  binding.profile.setImageResource(R.drawable.round_sync_24)



        }
    }

    private fun showBottomSheetDialog() {
        val bottomSheetDialog = BottomSheetDialog(activity as AppCompatActivity)
        bottomSheetDialog.setContentView(R.layout.bottom_sheet_dialog)
        val copy = bottomSheetDialog.findViewById<RelativeLayout>(R.id.offline_layout)
        val cloase_bottom = bottomSheetDialog.findViewById<ImageView>(R.id.cloase_bottom)

        cloase_bottom?.setOnClickListener {

            bottomSheetDialog.dismiss()
        }


        bottomSheetDialog.show()
    }
}