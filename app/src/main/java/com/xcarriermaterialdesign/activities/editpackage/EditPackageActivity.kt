package com.xcarriermaterialdesign.activities.editpackage

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.room.Room
import com.xcarriermaterialdesign.R
import com.xcarriermaterialdesign.databinding.ActivityEditPackageBinding
import com.xcarriermaterialdesign.dbmodel.CarrierDao
import com.xcarriermaterialdesign.dbmodel.CarrierPackage
import com.xcarriermaterialdesign.process.ProcessPackageActivity
import com.xcarriermaterialdesign.roomdatabase.ProcessDao
import com.xcarriermaterialdesign.roomdatabase.ProcessDatabase
import com.xcarriermaterialdesign.roomdatabase.ProcessPackage
import com.xcarriermaterialdesign.roomdatabase.TrackingDao
import com.xcarriermaterialdesign.roomdatabase.TrackingNumbersRequestItem

class EditPackageActivity : AppCompatActivity() {


    private lateinit var processDao: ProcessDao
    private lateinit var trackingDao: TrackingDao

    private lateinit var processPackage: List<ProcessPackage>
    private lateinit var carrierPackage: List<CarrierPackage>


    private lateinit var carrierDao: CarrierDao

    val araylist:ArrayList<String> = ArrayList()




    private lateinit var binding: ActivityEditPackageBinding

    val model: EditPackageViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_edit_package)


        supportActionBar?.hide()

        model.config(this)


        binding = DataBindingUtil.setContentView(this, R.layout.activity_edit_package)





        binding.toolbar.setNavigationOnClickListener {

            val intent = Intent(this, ProcessPackageActivity::class.java)
            startActivity(intent)

            finish()
        }


        var trackingId = intent.getStringExtra("trackingId")
        var id = intent.getStringExtra("id")
        var carriername = intent.getStringExtra("carriername")

    //  Toast.makeText(this,carriername, Toast.LENGTH_SHORT).show()

        binding.editText.setText(trackingId)





        val db = Room.databaseBuilder(
            applicationContext,
            ProcessDatabase::class.java, "Process_database"
        ).allowMainThreadQueries().build()

        processDao = db.processDao()

        trackingDao = db.TrackingDao()
        carrierDao = db.carrierDao()

        processPackage = processDao.getAllProcessPackages()
        carrierPackage =  carrierDao.getAllCarrierPackages()



      //  val dropdownItem = findViewById<AutoCompleteTextView>(R.id.dropdown_item)


        processPackage = processDao.isData(trackingId)


        for (i in processPackage.indices){

            binding.dropdownItem.setText(processPackage[i].carriername)
        }



        for (i in 0 until carrierPackage.size){


            araylist.add(carrierPackage.get(i).CarrierDescription)
        }


        ArrayAdapter(this, android.R.layout.simple_list_item_1, araylist).also {
                adapter ->
            binding.dropdownItem.setAdapter(adapter)

        }

        binding.profile.setOnClickListener {


            if (binding.editText.text.toString() == ""){

                Toast.makeText(this, "Please enter tracking number", Toast.LENGTH_SHORT).show()
            }

            else if (binding.dropdownItem.text.toString() == ""){


                Toast.makeText(this, "Please select Carrier", Toast.LENGTH_SHORT).show()


            }

            else{



                processDao.updateProcessPackages(id,binding.editText.text.toString())
                trackingDao.updateProcessPackages(id,binding.editText.text.toString())

                processDao.updateProcessPackageCarrier(id, binding.dropdownItem.text.toString())



                val intent = Intent(this, ProcessPackageActivity::class.java)

                startActivity(intent)
            }






        }





    }
}