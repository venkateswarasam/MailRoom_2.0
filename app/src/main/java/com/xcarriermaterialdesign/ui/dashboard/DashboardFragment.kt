package com.xcarriermaterialdesign.ui.dashboard

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.xcarriermaterialdesign.R
import com.xcarriermaterialdesign.SettingsActivity
import com.xcarriermaterialdesign.databinding.FragmentDashboardBinding

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    var buttonClicked = 0


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val dashboardViewModel =
            ViewModelProvider(this).get(DashboardViewModel::class.java)

        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root

        (activity as AppCompatActivity).supportActionBar?.hide()

    /*    val textView: TextView = binding.textDashboard
        dashboardViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }*/




        val check = false

        if (!check){

            //initactionbar()

        }

        binding.fab.setOnClickListener {


            when(buttonClicked){

                0->{


                    binding.alertlayout.visibility = View.VISIBLE
                    binding.fab.setImageResource(R.drawable.ic_baseline_close_24)
                    binding.searchLayout!!.setBackgroundColor(Color.parseColor("#979797"));


                    buttonClicked = 1
                }

                1->{

                    buttonClicked = 0

                    binding.alertlayout.visibility = View.GONE
                    binding.fab.setImageResource(R.drawable.addsymbol)

                    binding.searchLayout!!.setBackgroundColor(Color.parseColor("#FFFFFF"));


                }
            }


        }








       // initactionbar()


      //  (activity as AppCompatActivity).supportActionBar?.title = "Example 1"

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initactionbar(){

        (activity as AppCompatActivity).supportActionBar?.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
        (activity as AppCompatActivity).supportActionBar?.setDisplayShowCustomEnabled(true)
        (activity as AppCompatActivity).supportActionBar?.setCustomView(R.layout.actionbarnew)
        val view1: View =  (activity as AppCompatActivity).supportActionBar!!.customView
        val toolbar: Toolbar = view1.parent as Toolbar
        toolbar.setContentInsetsAbsolute(0, 0)
      //  toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24)
        val headertext:TextView = view1.findViewById(R.id.headertext)
        headertext.text = getString(R.string.searchpack)
        val profile: ImageView = view1.findViewById(R.id.profile)
     //   val back: ImageView = view1.findViewById(R.id.back)

       // back.visibility = View.GONE
        //  profile.setImageResource(R.drawable.profilewhite)
        profile.visibility = View.GONE


        profile.setOnClickListener {

            val intent = Intent(activity, SettingsActivity::class.java)

            startActivity(intent)

        }
    }



}