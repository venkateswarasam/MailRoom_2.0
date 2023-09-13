package com.xcarriermaterialdesign.activities.manual

import android.app.Dialog
import android.content.ContentResolver
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.media.RingtoneManager
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import android.widget.TextView.OnEditorActionListener
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.xcarriermaterialdesign.BottomNavigationActivity
import com.xcarriermaterialdesign.R
import com.xcarriermaterialdesign.databinding.ActivityManualProcessPackageBinding
import com.xcarriermaterialdesign.process.ProcessPackageActivity
import com.xcarriermaterialdesign.roomdatabase.ProcessDao
import com.xcarriermaterialdesign.roomdatabase.ProcessDatabase
import com.xcarriermaterialdesign.roomdatabase.ProcessPackage
import com.xcarriermaterialdesign.utils.CourseModal
import com.xcarriermaterialdesign.utils.DWUtilities
import com.xcarriermaterialdesign.utils.NetWorkService
import com.xcarriermaterialdesign.utils.NetworkChangeReceiver
import com.xcarriermaterialdesign.utils.NetworkConnection
import java.lang.reflect.Type
import java.util.*


class ManualProcessPackageActivity : AppCompatActivity(), NetworkChangeReceiver.NetCheckerReceiverListener {



    private var courseModalArrayList: ArrayList<CourseModal>? = null


    private lateinit var processDao: ProcessDao



    private lateinit var processPackage: List<ProcessPackage>



    private val EXTRA_SCANNERINPUTPLUGIN = "com.symbol.datawedge.api.SCANNER_INPUT_PLUGIN"
    private val ACTION_DATAWEDGE = "com.symbol.datawedge.api.ACTION"






    private lateinit var binding: ActivityManualProcessPackageBinding

    val model: ManualPackageViewModel by viewModels()



    override fun onBackPressed() {


        val intent = Intent(this, BottomNavigationActivity::class.java)

        startActivity(intent)

        finish()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
      //  setContentView(R.layout.activity_manual_process_package)

       supportActionBar?.hide()

        model.config(this)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_manual_process_package)


        DWUtilities.CreateDWProfile(this, resources.getString(R.string.activity_intent_filter_action),"true")



        startService(Intent(applicationContext, NetWorkService::class.java))

        if (!NetworkConnection().isNetworkAvailable(this)) {

            binding.profile.setImageResource(R.drawable.syncyellow)

        }
        else{

            binding.profile.setImageResource(R.drawable.syncnew)

        }


        binding.toolbar.setNavigationOnClickListener {

            val intent = Intent(this, BottomNavigationActivity::class.java)

            startActivity(intent)

            finish()

        }

        val db = Room.databaseBuilder(
            applicationContext,
            ProcessDatabase::class.java, "Process_database"
        ).allowMainThreadQueries().build()


        processDao = db.processDao()

        processPackage = processDao.getAllProcessPackages()


        courseModalArrayList = ArrayList()


       //  filledtextfiled = findViewById<EditText>(R.id.edit_text)


        binding.editText.setOnEditorActionListener(OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                // Some logic here.


                val input_text = binding.editText.text.toString()

                when(input_text){

                    ""->{

                        playNotificationSound()

                        binding.infoLayout.visibility = View.VISIBLE

                    }



                    else->{


                        if (!checkDuplicateTrackNo(binding.editText.text.toString())) {

                            processDao.insertProcessPackage(ProcessPackage(binding.editText.text.toString(),"",1))

                            savedata()
                        }





                            /* processPackage = processDao.isData(binding.editText.text.toString())

                             if (processPackage.isEmpty()){


                                 processDao.insertProcessPackage(ProcessPackage(binding.editText.text.toString(),"",1))

                                 savedata()
                             }

                             else{


                                 for (i in processPackage.indices){

                                     if (processPackage[i].trackingNumber == binding.editText.text.toString()){

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
                                         window.attributes = lp
                                         dialog.show()

                                         ok.setOnClickListener {

                                             processDao.insertProcessPackage(ProcessPackage(binding.editText.text.toString(),"", 1))


                                             dialog.dismiss()

                                             savedata()

                                         }

                                         cancel.setOnClickListener {

                                             dialog.dismiss()
                                         }



                                     }

                                 }



                             }*/

                    }
                }



                true // Focus will do whatever you put in the logic.
            } else false
            // Focus will change according to the actionId
        })











/*
        filledtextfiled!!.setOnKeyListener(View.OnKeyListener { v, keyCode, event ->


            when(event.action){

                KeyEvent.KEYCODE_NAVIGATE_NEXT->{





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

                }

                KeyEvent.KEYCODE_BACKSLASH->{

                    val intent = Intent(this, BottomNavigationActivity::class.java)

                    startActivity(intent)

                    finish()

                }

                KeyEvent.KEYCODE_BACK->{

                    val intent = Intent(this, BottomNavigationActivity::class.java)

                    startActivity(intent)

                    finish()

                }

*/
/*
                KeyEvent.FLAG_FALLBACK->{

                    val intent = Intent(this, BottomNavigationActivity::class.java)

                    startActivity(intent)

                    finish()

                }
*//*

            }



            false
        })
*/

        binding.oktext.setOnClickListener {


            binding.infoLayout.visibility = View.GONE
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
       // filledtextfiled?.setText(decodedData)


       // Toast.makeText(this, decodedData, Toast.LENGTH_SHORT).show()


        runOnUiThread(java.lang.Runnable {


            binding.editText.setText(decodedData)

            if (!checkDuplicateTrackNo(binding.editText.text.toString())) {

                processDao.insertProcessPackage(ProcessPackage(binding.editText.text.toString(),"",1))

                savedata()
            }





         /*   binding.editText.setText(decodedData)

            savedata()*/


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




    private fun savedata(){


        val intent = Intent(this, ProcessPackageActivity::class.java)
        startActivity(intent)

    }

    override fun onResume() {
        super.onResume()

        NetworkChangeReceiver.netConnectionCheckerReceiver = this
    }

    private fun showMessage(isConnected: Boolean) {

        if (!isConnected) {

            binding.profile.setImageResource(R.drawable.syncyellow)





        } else {


            binding.profile.setImageResource(R.drawable.syncnew)



            //  save!!.setImageDrawable(resources.getDrawable(R.drawable.savegreen))

        }


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


    private fun checkDuplicateTrackNo(barcode : String) : Boolean
    {

        processPackage = processDao.getAllProcessPackages()

        for (item in processPackage.indices)
        {
            var bar = processPackage[item]

            if (bar.trackingNumber == barcode)
            {


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
                window.attributes = lp
                dialog.show()

                ok.setOnClickListener {

                    //


                    bar.count = bar.count+1

                    //  processPackage.removeAt(item)

                    processDao.updateProcessPackageCount(bar.id.toString(), bar.count)


                    savedata()
                    dialog.dismiss()


                }

                cancel.setOnClickListener {

                    dialog.dismiss()
                }













                return true


            }


        }

        return false
    }

    companion object {
        private const val PROFILE_NAME = "MailroomDev"
    }
}