package com.xcarriermaterialdesign.process

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.room.Room
import com.xcarriermaterialdesign.R
import com.xcarriermaterialdesign.roomdatabase.ProcessDao
import com.xcarriermaterialdesign.roomdatabase.ProcessDatabase
import com.xcarriermaterialdesign.roomdatabase.ProcessPackage

class EditPackageActivity : AppCompatActivity() {


    private lateinit var processDao: ProcessDao

    private lateinit var processPackage: List<ProcessPackage>

    internal var profile:ImageView?= null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_package)

        supportActionBar?.hide()


        val edit_text = findViewById<EditText>(R.id.edit_text)

        profile = findViewById(R.id.profile)

        val toolbar:Toolbar = findViewById(R.id.toolbar)

        toolbar.setNavigationOnClickListener {

            val intent = Intent(this, ProcessPackageActivity::class.java)
            startActivity(intent)

            finish()
        }


        var trackingId = intent.getStringExtra("trackingId")
        var id = intent.getStringExtra("id")
        var carriername = intent.getStringExtra("carriername")

    //  Toast.makeText(this,carriername, Toast.LENGTH_SHORT).show()

        edit_text.setText(trackingId)





        val db = Room.databaseBuilder(
            applicationContext,
            ProcessDatabase::class.java, "Process_database"
        ).allowMainThreadQueries().build()

        processDao = db.processDao()

        processPackage = processDao.getAllProcessPackages()





        val dropdownItem = findViewById<AutoCompleteTextView>(R.id.dropdown_item)





        val araylist:ArrayList<String> = ArrayList()
        araylist.add("FedEx")
        araylist.add("DHL")
        araylist.add("USPS")
        araylist.add("UPS")










            when(carriername){



                "FedEx"->{

                    dropdownItem.setText(araylist.get(0))
                }

                "DHL"->{

                    dropdownItem.setText(araylist.get(1))
                }

                "USPS"->{

                    dropdownItem.setText(araylist.get(2))
                }
                "UPS"->{

                    dropdownItem.setText(araylist.get(3))
                }

            }




        ArrayAdapter(this, android.R.layout.simple_list_item_1, araylist).also {
                adapter ->
            dropdownItem.setAdapter(adapter)

        }










        profile?.setOnClickListener {


            if (edit_text.text.toString() == ""){

                Toast.makeText(this, "Please enter tracking number", Toast.LENGTH_SHORT).show()
            }

            else if (dropdownItem.text.toString() == ""){


                Toast.makeText(this, "Please select Carrier", Toast.LENGTH_SHORT).show()


            }

            else{



                processDao.updateProcessPackages(id,edit_text.text.toString())
                processDao.updateProcessPackageCarrier(id, dropdownItem.text.toString())



                val intent = Intent(this, ProcessPackageActivity::class.java)

                startActivity(intent)
            }






        }





    }
}