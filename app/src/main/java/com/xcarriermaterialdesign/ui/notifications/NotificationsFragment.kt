package com.xcarriermaterialdesign.ui.notifications

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.zxing.integration.android.IntentIntegrator
import com.xcarriermaterialdesign.R
import com.xcarriermaterialdesign.SettingsActivity
import com.xcarriermaterialdesign.databinding.FragmentNotificationsBinding

class NotificationsFragment : Fragment() {

    private var _binding: FragmentNotificationsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private var qrScanIntegrator: IntentIntegrator? = null


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



        val check = false

        if (!check){

            //initactionbar()

        }




        val batchScanResultLauncher  = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {

            if (it.resultCode == AppCompatActivity.RESULT_OK) {
                val intentResult = IntentIntegrator.parseActivityResult(it.resultCode, it.data)


                Toast.makeText(activity, intentResult.contents, Toast.LENGTH_SHORT).show()

                // binding.batchIdTV.setText(intentResult.contents.toString())
            }
        }

        binding.barcode.setOnClickListener()
        {
            batchScanResultLauncher.launch(qrScanIntegrator?.createScanIntent())
        }

















        // initactionbar()
        return root
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
}