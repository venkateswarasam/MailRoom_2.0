package com.xcarriermaterialdesign.ui.home

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
import com.xcarriermaterialdesign.barcodescanner.BarcodeScannerActivity
import com.xcarriermaterialdesign.databinding.FragmentHomeBinding
import com.xcarriermaterialdesign.process.ManualProcessPackageActivity
import com.xcarriermaterialdesign.process.ProcessPackageActivity

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private var qrScanIntegrator: IntentIntegrator? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        homeViewModel.text.observe(viewLifecycleOwner) {
           // textView.text = it
        }
        (activity as AppCompatActivity).supportActionBar?.hide()

        setupScanner()
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




            val intent = Intent(activity, BarcodeScannerActivity::class.java)
            activity?.startActivity(intent)

            // batchScanResultLauncher.launch(qrScanIntegrator?.createScanIntent())
        }



        binding.manualPackage.setOnClickListener()
        {




            val intent = Intent(activity, ManualProcessPackageActivity::class.java)
            activity?.startActivity(intent)

            // batchScanResultLauncher.launch(qrScanIntegrator?.createScanIntent())
        }









        val check = false

        if (!check){

          //initActionBar()

        }



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











    private fun setupScanner() {
        qrScanIntegrator = IntentIntegrator(activity)
        qrScanIntegrator?.setOrientationLocked(false)
        qrScanIntegrator?.setPrompt("")
        qrScanIntegrator?.captureActivity

    }







    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}