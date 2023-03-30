package com.xcarriermaterialdesign.process

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.AdapterView.OnItemClickListener
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import com.xcarriermaterialdesign.BottomNavigationActivity
import com.xcarriermaterialdesign.R
import com.xcarriermaterialdesign.barcodescanner.BarcodeScannerActivity
import com.xcarriermaterialdesign.barcodescanner.ProcessBarcodeScannerActivity
import com.xcarriermaterialdesign.databinding.ActivityProcessPackageFinalBinding


class ProcessPackageFinalActivity : AppCompatActivity() {




    private lateinit var binding: ActivityProcessPackageFinalBinding


    val model: ProcessPackageFinalViewModel by viewModels()


    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
     //   setContentView(R.layout.activity_process_package_final)

        model.config(this)
        supportActionBar?.hide()

     //   initactionbar()


        binding = DataBindingUtil.setContentView(this, R.layout.activity_process_package_final)



        //  val dropdown_item = findViewById<AutoCompleteTextView>(R.id.dropdown_item)

        val araylist:ArrayList<String> = ArrayList()
        araylist.add("Received")
        araylist.add("Delivered")
        araylist.add("In-Transit")
        araylist.add("Delivery Attempt 1")
        araylist.add("Delivery Attempt 2")
        araylist.add("Delivery Attempt 3")
        araylist.add("Handover to Carrier")
        araylist.add("Picked Up")
        araylist.add("Exception")
        araylist.add("Returned")
        araylist.add("Cancelled")
        araylist.add("Dropped Off at Front Desk")
        araylist.add("Left at Disk")
        araylist.add("Left in Office")
        araylist.add("Dropped Off at Mailroom")


        binding.toolbar.setNavigationOnClickListener {
            val intent = Intent(this, BottomNavigationActivity::class.java)

            startActivity(intent)

        }

        binding.backbutton.setOnClickListener {

            finish()
        }

      //  dropdownlay.setOnKeyListener=null
        ArrayAdapter(this, android.R.layout.simple_list_item_1, araylist).also {
                adapter ->
            binding.dropdownItem.setAdapter(adapter)
        }


        binding.dropdownItem.onItemClickListener =
            OnItemClickListener { parent, arg1, position, arg3 ->
                val item = parent.getItemAtPosition(position)

                when(item){

                    "Received"->{

                        binding.buildingLay.visibility = View.VISIBLE
                        binding.mailstopLay.visibility = View.VISIBLE
                        binding.storage.visibility = View.VISIBLE
                        binding.binLay.visibility = View.VISIBLE
                        binding.dock.visibility = View.VISIBLE
                        binding.signedby.visibility = View.VISIBLE
                        binding.signhere.visibility = View.VISIBLE
                        binding.noteshere.visibility = View.VISIBLE

                        binding.bottomactionlay.visibility = View.VISIBLE
                        binding.savechanges.visibility = View.VISIBLE


                        binding.locatondrop.visibility = View.GONE
                        binding.locatondrop1.visibility = View.GONE

                        binding.drivername.visibility = View.GONE
                        binding.carriersignature.visibility = View.GONE
                        binding.route.visibility = View.VISIBLE
                        binding.locker.visibility = View.VISIBLE
                        binding.reasonLay.visibility = View.GONE
                        binding.required.visibility = View.GONE

                    }

                    "Delivered"->{

                        binding.buildingLay.visibility = View.VISIBLE
                        binding.locatondrop.visibility = View.VISIBLE
                        binding.locatondrop1.visibility = View.GONE
                        binding.signhere.visibility = View.VISIBLE
                        binding.signedby.visibility = View.VISIBLE
                        binding.noteshere.visibility = View.VISIBLE
                        binding.savechanges.visibility = View.VISIBLE
                        binding.bottomactionlay.visibility = View.VISIBLE


                        binding.mailstopLay.visibility = View.GONE
                        binding.storage.visibility = View.GONE
                        binding.binLay.visibility = View.GONE
                        binding.dock.visibility = View.GONE
                        binding.drivername.visibility = View.GONE
                        binding.carriersignature.visibility = View.GONE
                        binding.route.visibility = View.GONE
                        binding.locker.visibility = View.GONE
                        binding.reasonLay.visibility = View.GONE
                        binding.required.visibility = View.GONE

                    }

                    "Handover to Carrier"->{

                        binding.buildingLay.visibility = View.VISIBLE
                        binding.dock.visibility = View.VISIBLE
                        binding.locatondrop.visibility = View.VISIBLE
                        binding.drivername.visibility = View.VISIBLE
                        binding.carriersignature.visibility = View.VISIBLE
                        binding.savechanges.visibility = View.VISIBLE
                        binding.bottomactionlay.visibility = View.VISIBLE
                        binding.locatondrop1.visibility = View.GONE

                        binding.mailstopLay.visibility = View.GONE
                        binding.storage.visibility = View.GONE
                        binding.binLay.visibility = View.GONE
                        binding.signedby.visibility = View.GONE
                        binding.signhere.visibility = View.GONE
                        binding.noteshere.visibility = View.GONE
                        binding.route.visibility = View.GONE
                        binding.locker.visibility = View.GONE
                        binding.reasonLay.visibility = View.GONE
                        binding.required.visibility = View.GONE


                    }

                    "Returned"->{

                        binding.reasonLay.visibility = View.VISIBLE
                        binding.required.visibility = View.VISIBLE
                        binding.buildingLay.visibility = View.VISIBLE
                        binding.dock.visibility = View.VISIBLE
                        binding.locatondrop.visibility = View.GONE
                        binding.locatondrop1.visibility = View.VISIBLE
                        binding.mailstopLay.visibility = View.VISIBLE
                        binding.storage.visibility = View.VISIBLE
                        binding.binLay.visibility = View.VISIBLE
                        binding.route.visibility = View.VISIBLE
                        binding.locker.visibility = View.VISIBLE
                        binding.noteshere.visibility = View.VISIBLE
                        binding.savechanges.visibility = View.VISIBLE
                        binding.bottomactionlay.visibility = View.VISIBLE


                        binding.drivername.visibility = View.GONE
                        binding.carriersignature.visibility = View.GONE
                        binding.signedby.visibility = View.GONE
                        binding.signhere.visibility = View.GONE


                    }
                }


              //  Toast.makeText(this, item.toString(), Toast.LENGTH_SHORT).show()

            }



        binding.savechanges.setOnClickListener {

            Toast.makeText(this, "Data saved successfully", Toast.LENGTH_SHORT).show()


            val intent = Intent(this, BottomNavigationActivity::class.java)

            startActivity(intent)
        }



        binding.buildingScan.setOnClickListener {

            val intent = Intent(this, ProcessBarcodeScannerActivity::class.java)

         //   intent.putExtra("scanning","Scanning")

            startActivityForResult(intent,54321)
        }


        binding.mailstopScan.setOnClickListener {

            val intent = Intent(this, ProcessBarcodeScannerActivity::class.java)

            //   intent.putExtra("scanning","Scanning")

            startActivityForResult(intent,54322)
        }

        binding.binscan.setOnClickListener {

            val intent = Intent(this, ProcessBarcodeScannerActivity::class.java)

            //   intent.putExtra("scanning","Scanning")

            startActivityForResult(intent,54323)
        }



        binding.storagescan.setOnClickListener {

            val intent = Intent(this, ProcessBarcodeScannerActivity::class.java)

            //   intent.putExtra("scanning","Scanning")

            startActivityForResult(intent,54324)
        }


        binding.docscan.setOnClickListener {

            val intent = Intent(this, ProcessBarcodeScannerActivity::class.java)

            //   intent.putExtra("scanning","Scanning")

            startActivityForResult(intent,54325)
        }

        binding.routescan.setOnClickListener {

            val intent = Intent(this, ProcessBarcodeScannerActivity::class.java)

            //   intent.putExtra("scanning","Scanning")

            startActivityForResult(intent,54326)
        }


        binding.lockerscan.setOnClickListener {

            val intent = Intent(this, ProcessBarcodeScannerActivity::class.java)

            //   intent.putExtra("scanning","Scanning")

            startActivityForResult(intent,54327)
        }
    }


    private fun initactionbar(){

        supportActionBar?.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
        supportActionBar?.setDisplayShowCustomEnabled(true)
        supportActionBar?.setCustomView(R.layout.actionbarnew)
        val view1: View =  supportActionBar!!.customView
        val toolbar: Toolbar = view1.parent as Toolbar
        toolbar.setContentInsetsAbsolute(0, 0)
        toolbar.setNavigationIcon(R.drawable.ic_baseline_close_24)
        val headertext: TextView = view1.findViewById(R.id.headertext)
        headertext.text = getString(R.string.process)
        val profile: ImageView = view1.findViewById(R.id.profile)
         // val back: ImageView = view1.findViewById(R.id.back)


        profile.setImageResource(R.drawable.syncnew)

        profile.setOnClickListener {



        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when(requestCode){
            54321->{

                if (resultCode == RESULT_OK) {
                    val name = data!!.getStringExtra("barcode")
                    println("==name$name")
                    binding.buildingname!!.setText(name)
                }



            }

            54322->{

                if (resultCode == RESULT_OK) {
                    val name = data!!.getStringExtra("barcode")
                    println("==name$name")
                    binding.mailstop!!.setText(name)
                }



            }


            54323->{

                if (resultCode == RESULT_OK) {
                    val name = data!!.getStringExtra("barcode")
                    println("==name$name")
                    binding.bin!!.setText(name)
                }



            }


            54324->{

                if (resultCode == RESULT_OK) {
                    val name = data!!.getStringExtra("barcode")
                    println("==name$name")
                    binding.storagetext!!.setText(name)
                }



            }

            54325->{

                if (resultCode == RESULT_OK) {
                    val name = data!!.getStringExtra("barcode")
                    println("==name$name")
                    binding.docnumber!!.setText(name)
                }



            }

            54326->{

                if (resultCode == RESULT_OK) {
                    val name = data!!.getStringExtra("barcode")
                    println("==name$name")
                    binding.routeText!!.setText(name)
                }



            }

            54327->{

                if (resultCode == RESULT_OK) {
                    val name = data!!.getStringExtra("barcode")
                    println("==name$name")
                    binding.lockertext!!.setText(name)
                }



            }
        }


    }

}