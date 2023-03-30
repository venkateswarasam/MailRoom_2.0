package com.xcarriermaterialdesign.process

import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.room.Room
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.xcarriermaterialdesign.R
import com.xcarriermaterialdesign.roomdatabase.ProcessDao
import com.xcarriermaterialdesign.roomdatabase.ProcessDatabase
import com.xcarriermaterialdesign.roomdatabase.ProcessPackage
import com.xcarriermaterialdesign.utils.CourseModal
import java.lang.reflect.Type
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class ManualProcessPackageActivity : AppCompatActivity() {

    val arrayList = ArrayList<String>()


    private var courseModalArrayList: ArrayList<CourseModal>? = null


    private lateinit var processDao: ProcessDao

    var filledtextfiled:EditText?= null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manual_process_package)

      //  initactionbar()
       supportActionBar?.hide()


        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        toolbar.setNavigationOnClickListener {

            finish()
        }

        val db = Room.databaseBuilder(
            applicationContext,
            ProcessDatabase::class.java, "Process_database"
        ).allowMainThreadQueries().build()


        processDao = db.processDao()

        courseModalArrayList = ArrayList()


         filledtextfiled = findViewById<EditText>(R.id.edit_text)

        filledtextfiled!!.setOnKeyListener(View.OnKeyListener { v, keyCode, event ->




                if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP) {
                    //Perform Code


                    processDao.insertProcessPackage(ProcessPackage(filledtextfiled?.text.toString()))






                 //   processDao.insertProcessPackage(ProcessPackage(filledtextfiled.text.toString()))




                  //  arrayList.add(filledtextfiled.text.toString())


                    courseModalArrayList!!.add(
                        CourseModal(
                            filledtextfiled!!.text.toString(),
                        )
                    )

                    savedata()




                  /*  val intent = Intent(this, ProcessPackageActivity::class.java)
                 //  intent.putExtra("list", courseModalArrayList)
                    startActivity(intent)*/

                    return@OnKeyListener true





            }
            false
        })


    }



    private fun getdata(){

        val sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE)

        val gson = Gson()
        val json = sharedPreferences.getString("trackingnumbers", null)

        val type: Type = object : TypeToken<ArrayList<CourseModal?>?>() {}.type

        courseModalArrayList = gson.fromJson(json, type);

        if (courseModalArrayList == null) {
            // if the array list is empty
            // creating a new array list.
            courseModalArrayList = ArrayList()
        }
    }

    private fun savedata(){


        val sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        val gson = Gson()
        val json = gson.toJson(courseModalArrayList)

        editor.putString("trackingnumbers", json);

        editor.apply();
      //  Toast.makeText(this, "Saved Array List to Shared preferences. ", Toast.LENGTH_SHORT).show();

        val intent = Intent(this, ProcessPackageActivity::class.java)
        //  intent.putExtra("list", courseModalArrayList)
        startActivity(intent)

    }
    private fun initactionbar(){

        supportActionBar?.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
        supportActionBar?.setDisplayShowCustomEnabled(true)
        supportActionBar?.setCustomView(R.layout.actionbarnew)
        val view1: View =  supportActionBar!!.customView
        val toolbar: Toolbar = view1.findViewById(R.id.toolbar)

        toolbar.setNavigationOnClickListener {
            finish()
        }
        val headertext: TextView = view1.findViewById(R.id.headertext)
        headertext.text = getString(R.string.process)
        val profile: ImageView = view1.findViewById(R.id.profile)
       // val back: ImageView = view1.findViewById(R.id.back)

        // back.visibility = View.GONE
         profile.setImageResource(R.drawable.syncnew)


        profile.setOnClickListener {



        }
    }

}