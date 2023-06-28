package com.xcarriermaterialdesign.process

import android.app.Dialog
import android.content.ContentResolver
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.media.RingtoneManager
import android.net.Uri
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
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
import com.xcarriermaterialdesign.utils.*
import java.lang.reflect.Type
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class ManualProcessPackageActivity : AppCompatActivity(), NetworkChangeReceiver.NetCheckerReceiverListener {

    val arrayList = ArrayList<String>()


    private var courseModalArrayList: ArrayList<CourseModal>? = null


    private lateinit var processDao: ProcessDao

    var filledtextfiled:EditText?= null

    internal var profile:ImageView?= null

    private lateinit var processPackage: List<ProcessPackage>

    internal var info_layout:LinearLayout?= null
    internal var ok_text:TextView?= null

    private val EXTRA_SCANNERINPUTPLUGIN = "com.symbol.datawedge.api.SCANNER_INPUT_PLUGIN"
    private val ACTION_DATAWEDGE = "com.symbol.datawedge.api.ACTION"

    override fun onBackPressed() {

        finish()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manual_process_package)

      //  initactionbar()
       supportActionBar?.hide()

        DWUtilities.CreateDWProfile(this, resources.getString(R.string.activity_intent_filter_action),"true")

        profile = findViewById(R.id.profile)
        ok_text = findViewById(R.id.oktext)
        info_layout = findViewById(R.id.info_layout)

        startService(Intent( applicationContext, NetWorkService::class.java))

        if (!NetworkConnection().isNetworkAvailable(this)) {

            profile!!.setImageResource(R.drawable.syncyellow)

        }
        else{

            profile!!.setImageResource(R.drawable.syncnew)

        }


        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        toolbar.setNavigationOnClickListener {

            finish()
        }

        val db = Room.databaseBuilder(
            applicationContext,
            ProcessDatabase::class.java, "Process_database"
        ).allowMainThreadQueries().build()


        processDao = db.processDao()

        processPackage = processDao.getAllProcessPackages()


        courseModalArrayList = ArrayList()


         filledtextfiled = findViewById<EditText>(R.id.edit_text)

        filledtextfiled!!.setOnKeyListener(View.OnKeyListener { v, keyCode, event ->




                if (event.action == KeyEvent.ACTION_UP) {


                    //Perform Code

                    val input_text = filledtextfiled?.text.toString()

                    when(input_text){

                        ""->{

                            playNotificationSound()

                            info_layout?.visibility = View.VISIBLE

                        }

                        else->{




                            processPackage = processDao.isData(filledtextfiled?.text.toString())

                            if (processPackage.isEmpty()){


                                processDao.insertProcessPackage(ProcessPackage(filledtextfiled?.text.toString(),""))

                                 savedata()
                            }

                            else{


                                for (i in processPackage.indices){

                                    if (processPackage[i].trackingNumber == filledtextfiled?.text.toString()){

                                        playNotificationSound()

                                        val dialog = Dialog(this@ManualProcessPackageActivity)
                                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                                        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

                                        dialog.setCancelable(false)
                                        dialog.setContentView(R.layout.info_layout_new)

                                        val ok = dialog.findViewById<TextView>(R.id.oktext)
                                        val cancel = dialog.findViewById<TextView>(R.id.cancel)

                                        val info_msg = dialog.findViewById<TextView>(R.id.info_msg)

                                        info_msg.text = "This barcode already scanned.Do you want to continue?"


                                        val lp = WindowManager.LayoutParams()
                                        val window = dialog.window
                                        lp.copyFrom(window!!.attributes)
                                        lp.width = WindowManager.LayoutParams.MATCH_PARENT
                                        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
                                        val isFinishing = false
                                        window.attributes = lp
                                        dialog.show()

                                        ok.setOnClickListener {

                                            processDao.insertProcessPackage(ProcessPackage(filledtextfiled?.text.toString(),""))


                                            dialog.dismiss()

                                            savedata()

                                        }

                                        cancel.setOnClickListener {

                                            dialog.dismiss()
                                        }



                                    }

                                }



                            }

                        }
                    }



























                /*    courseModalArrayList!!.add(
                        CourseModal(
                            filledtextfiled!!.text.toString(),
                        )
                    )*/





                  /*  val intent = Intent(this, ProcessPackageActivity::class.java)
                 //  intent.putExtra("list", courseModalArrayList)
                    startActivity(intent)*/

                    return@OnKeyListener true





            }
            false
        })

        ok_text?.setOnClickListener {


            info_layout?.visibility = View.GONE
        }


    }




    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        displayScanResult(intent)
    }

    private fun displayScanResult(scanIntent: Intent) {
        val decodedSource =
            scanIntent.getStringExtra(resources.getString(R.string.datawedge_intent_key_source))
        val decodedData =
            scanIntent.getStringExtra(resources.getString(R.string.datawedge_intent_key_data))
        val decodedLabelType =
            scanIntent.getStringExtra(resources.getString(R.string.datawedge_intent_key_label_type))
        val scan = "$decodedData [$decodedLabelType]\n\n"
        //  output.text = scan
        // binding.tracking.setText(decodedData)
        DWUtilities.sendDataWedgeIntentWithExtras(this, ACTION_DATAWEDGE,EXTRA_SCANNERINPUTPLUGIN,"SUSPEND_PLUGIN")


        runOnUiThread(java.lang.Runnable {

            filledtextfiled?.setText(decodedData)

            DWUtilities.sendDataWedgeIntentWithExtras(this, ACTION_DATAWEDGE,EXTRA_SCANNERINPUTPLUGIN,"RESUME_PLUGIN")


        })



    }
















    fun playNotificationSound() {
        try {
            val alarmSound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + applicationContext.packageName + "/" + R.raw.scanrepeated) //Here is FILE_NAME is the name of file that you want to play

            val r = RingtoneManager.getRingtone(applicationContext, alarmSound)
            r.play()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }



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

    override fun onResume() {
        super.onResume()

        NetworkChangeReceiver.netConnectionCheckerReceiver = this
    }

    private fun showMessage(isConnected: Boolean) {

        if (!isConnected) {

            profile!!.setImageResource(R.drawable.syncyellow)





        } else {


            profile!!.setImageResource(R.drawable.syncnew)



            //  save!!.setImageDrawable(resources.getDrawable(R.drawable.savegreen))

        }


    }

    override fun onNetworkConnectionChanged(isConnected: Boolean) {

        showMessage(isConnected)

        if (isConnected){



            profile!!.setImageResource(R.drawable.syncnew)


        }
        else{


            profile!!.setImageResource(R.drawable.syncyellow)



        }
    }

    companion object {
        private const val PROFILE_NAME = "MailroomDev"
    }
}