package com.xcarriermaterialdesign.process

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.graphics.*
import android.graphics.drawable.ColorDrawable
import android.media.RingtoneManager
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.view.GestureDetector.SimpleOnGestureListener
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.xcarriermaterialdesign.BottomNavigationActivity
import com.xcarriermaterialdesign.R
import com.xcarriermaterialdesign.activities.editpackage.EditPackageActivity
import com.xcarriermaterialdesign.activities.manual.ManualProcessPackageActivity
import com.xcarriermaterialdesign.activities.processfinal.ProcessPackageFinalActivity
import com.xcarriermaterialdesign.databinding.ActivityProcessPackageBinding
import com.xcarriermaterialdesign.roomdatabase.ProcessDao
import com.xcarriermaterialdesign.roomdatabase.ProcessDatabase
import com.xcarriermaterialdesign.roomdatabase.ProcessPackage
import com.xcarriermaterialdesign.activities.scanner.SimpleScannerActivity
import com.xcarriermaterialdesign.utils.CourseModal
import com.xcarriermaterialdesign.utils.NetWorkService
import com.xcarriermaterialdesign.utils.NetworkChangeReceiver
import com.xcarriermaterialdesign.utils.NetworkConnection
import com.xcarriermaterialdesign.utils.SwipeHelper
import com.xcarriermaterialdesign.utils.*
import com.xcarriermaterialdesign.utils.SwipeHelper.UnderlayButtonClickListener
import java.lang.reflect.Type


class ProcessPackageActivity : AppCompatActivity(), NetworkChangeReceiver.NetCheckerReceiverListener {







    var check = false

    var buttonClicked = 0

    var adaptercount = 0



    private var mDetector: GestureDetector? = null


    private var courseModalArrayList: ArrayList<CourseModal>? = null

    private var adapter: ProcessAdapter_new? = null


    private lateinit var processDao: ProcessDao

    private lateinit var processPackage: List<ProcessPackage>


    private lateinit var binding: ActivityProcessPackageBinding

    val model: ProcessViewModel by viewModels()


    val arrayList = ArrayList<String>()



    private fun showMessage(isConnected: Boolean) {

        if (!isConnected) {

            binding.profileSync.setImageResource(R.drawable.syncyellow)





        } else {


            binding.profileSync.setImageResource(R.drawable.syncnew)



            //  save!!.setImageDrawable(resources.getDrawable(R.drawable.savegreen))

        }


    }


    override fun onBackPressed() {
        val intent = Intent(this,  BottomNavigationActivity::class.java)
        startActivity(intent)
        finish()
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
       // setContentView(R.layout.activity_process_package)


        model.config(this)

        supportActionBar?.hide()

        binding = DataBindingUtil.setContentView(this, R.layout.activity_process_package)


        DWUtilities.CreateDWProfile(this, resources.getString(R.string.activity_intent_filter_action3),"true")



        binding.toolbar.setNavigationOnClickListener {

            val intent = Intent(this,  BottomNavigationActivity::class.java)
            startActivity(intent)
            finish()
        }


       startService(Intent( applicationContext, NetWorkService::class.java))

        if (!NetworkConnection().isNetworkAvailable(this)) {

            binding.profileSync.setImageResource(R.drawable.syncyellow)

        }
        else{

            binding.profileSync.setImageResource(R.drawable.syncnew)

        }


        val db = Room.databaseBuilder(
            applicationContext,
            ProcessDatabase::class.java, "Process_database"
        ).allowMainThreadQueries().build()

        processDao = db.processDao()

        processPackage = processDao.getAllProcessPackages()


        if (processDao.getAllProcessPackages().isEmpty()){


            binding.nopendinglayout.visibility = View.VISIBLE
        }


         adapter =
            ProcessAdapter_new(this, processDao.getAllProcessPackages())

        val manager = LinearLayoutManager(this)
        binding.trackinglist.setHasFixedSize(true)

        binding.trackinglist.layoutManager = manager
        binding.trackinglist.adapter = adapter



        if (processDao.getAllProcessPackages().size>1){

            binding.conslidateLay.visibility = View.VISIBLE
        }


        binding.closeFab.setOnClickListener {


            binding.alertlayout.visibility =  View.GONE
            binding.fab.visibility =  View.VISIBLE
            binding.closeFab.visibility =  View.GONE

        }


        binding.checkConsle.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { compoundButton, b ->
            if ( binding.checkConsle.isChecked) {
                binding.checkConsle.isChecked = true

                binding.bulkidLayout.visibility = View.VISIBLE

            } else {
                binding.checkConsle.isChecked = false
                binding.bulkidLayout.visibility = View.GONE

            }
        })





        binding.keyupdown.setOnClickListener {

            binding.trackinglist.visibility = View.GONE
            binding.keyupdown.visibility = View.GONE
            binding.keyupdown1.visibility = View.VISIBLE
        }

        binding.keyupdown1.setOnClickListener {

            binding.trackinglist.visibility = View.VISIBLE
            binding.keyupdown.visibility = View.VISIBLE
            binding.keyupdown1.visibility = View.GONE
        }



        binding.fab.setOnClickListener {




            when(buttonClicked){

               0->

               {




                   binding.trackinglist.setBackgroundColor(Color.parseColor("#979797"))
                 binding.alertlayout.setBackgroundColor(Color.parseColor("#979797"))
                 binding.mainmenuLayout.setBackgroundColor(Color.parseColor("#979797"))
                 binding.conslidateLay.setBackgroundColor(Color.parseColor("#979797"))
                   binding.subConsolidate.setBackgroundColor(Color.parseColor("#979797"))

                   binding.nopendinglayout.visibility = View.GONE

                   binding.nopendingImage.setBackgroundColor(Color.parseColor("#00000000"))



                   binding.fab.setImageResource(R.drawable.ic_baseline_close_24)
                   binding.alertlayout.visibility =  View.VISIBLE


                   buttonClicked = 1






               }

                1->{

                    buttonClicked = 0

                   binding.mainmenuLayout.setBackgroundColor(Color.parseColor("#FFFFFF"))


                    binding.trackinglist.setBackgroundColor(Color.parseColor("#FFFFFF"))
                    binding.alertlayout.setBackgroundColor(Color.parseColor("#FFFFFF"))
                    binding.conslidateLay.setBackgroundColor(Color.parseColor("#FFFFFF"))
                    binding.subConsolidate.setBackgroundColor(Color.parseColor("#FFFFFF"))
                    binding.nopendingImage.setBackgroundColor(Color.parseColor("#FFFFFF"))

                    if (processDao.getAllProcessPackages().isEmpty()){

                        binding.nopendinglayout.visibility = View.VISIBLE

                    }



                    binding.fab.setImageResource(R.drawable.addsymbol)

                    binding.alertlayout.visibility =  View.GONE
                }
               else -> {

                   binding.fab.setImageResource(R.drawable.addsymbol)

                   binding.alertlayout.visibility =  View.GONE

               }
           }












        }


        binding.manualEntry.setOnClickListener {

            // onBackPressed()

            val intent = Intent(this, ManualProcessPackageActivity::class.java)
            startActivity(intent)
        }


        binding.scannerEntry.setOnClickListener {

            val intent = Intent(this, SimpleScannerActivity::class.java)
            startActivity(intent)
        }


        binding.manualfab.setOnClickListener {

            val intent = Intent(this, ManualProcessPackageActivity::class.java)
            startActivity(intent)
        }

        binding.scannerfab.setOnClickListener {


            val intent = Intent(this, SimpleScannerActivity::class.java)
            startActivity(intent)

        }



        binding.nextbutton.setOnClickListener {

            val intent = Intent(this, ProcessPackageFinalActivity::class.java)

            startActivity(intent)
        }


        binding.infoButton.setOnClickListener {



            binding.infoLayout.visibility = View.VISIBLE

        }

        binding.oktext.setOnClickListener {

            binding.infoLayout.visibility = View.GONE
        }


    }


    // data wedge  implementation

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


     //   Toast.makeText(this, decodedData, Toast.LENGTH_SHORT).show()


        runOnUiThread(java.lang.Runnable {



            if (!checkDuplicateTrackNo(decodedData!!)) {


                try {
                    val notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
                    val r = RingtoneManager.getRingtone(applicationContext, notification)
                    r.play()
                } catch (e: Exception) {
                }

                processDao.insertProcessPackage(ProcessPackage(decodedData,"",1))


               adapter =
                    ProcessAdapter_new(this, processDao.getAllProcessPackages())

                val manager = LinearLayoutManager(this)
                binding.trackinglist.setHasFixedSize(true)

                binding.trackinglist.layoutManager = manager
                binding.trackinglist.adapter = adapter

                finish()
                overridePendingTransition(0, 0)
                startActivity(intent)
                overridePendingTransition(0, 0)


                //  barcodeList.add(BarcodesList(barcode1,1))
            }





        })



    }


    // check duplicate number

    private fun checkDuplicateTrackNo(barcode : String) : Boolean
    {

        processPackage = processDao.getAllProcessPackages()

        for (item in processPackage.indices)
        {
            var bar = processPackage[item]

            if (bar.trackingNumber == barcode)
            {


                playNotificationSound()


                val dialog = Dialog(this@ProcessPackageActivity)
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


                   // processDao.insertProcessPackage(ProcessPackage(barcode,"", 1))
                   // processPackage.add(item,bar)

                  //  binding.count.text = getCount()


                    adapter =
                        ProcessAdapter_new(this, processDao.getAllProcessPackages())

                    val manager = LinearLayoutManager(this)
                    binding.trackinglist.setHasFixedSize(true)

                    binding.trackinglist.layoutManager = manager
                    binding.trackinglist.adapter = adapter

                    dialog.dismiss()


                }

                cancel.setOnClickListener {

                    dialog.dismiss()
                }




                // DWUtilities.CreateDWProfile(this, resources.getString(R.string.activity_intent_filter_action), "false")

              //  DWUtilities.sendDataWedgeIntentWithExtras(this, ACTION_DATAWEDGE,EXTRA_SCANNERINPUTPLUGIN,"SUSPEND_PLUGIN")











                return true


            }


        }

        return false
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





    inner class ProcessAdapter_new(val context : Context, private val mList: List<ProcessPackage>) : RecyclerView.Adapter<ProcessAdapter_new.ViewHolder>() {



        // create new views
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProcessAdapter_new.ViewHolder {
            // inflates the card_view_design view
            // that is used to hold list item
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.process_item_list, parent, false)

            return ViewHolder(view)
        }


        override fun onBindViewHolder(holder: ViewHolder, @SuppressLint("RecyclerView") position: Int) {




            val itemsViewModel = mList[position]


            holder.trackingNo.text = itemsViewModel.trackingNumber ?:""
            holder.carruername.text = itemsViewModel.carriername ?: ""
            holder.trackingcount.text = itemsViewModel.count.toString()?:""

            if (itemsViewModel.carriername == ""){

                holder.carruername.text = "Carrier Name"
            }
            else{

                holder.carruername.text = itemsViewModel.carriername ?: ""

            }












            holder.item_layout.setOnClickListener {

                when(adaptercount){

                    0->{

                        holder.options_layout.visibility = View.VISIBLE

                        adaptercount = 1
                    }

                    1->{

                        adaptercount = 0


                        holder.options_layout.visibility = View.GONE



                    }
                }
            }







            holder.delete_img.setOnClickListener {




                val dialog = Dialog(this@ProcessPackageActivity)
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

                dialog.setCancelable(false)
                dialog.setContentView(R.layout.info_layout)

                val ok = dialog.findViewById<TextView>(R.id.oktext)
                val cancel = dialog.findViewById<TextView>(R.id.cancel)



                //   tts!!.speak(message, TextToSpeech.QUEUE_FLUSH, null)

                val lp = WindowManager.LayoutParams()
                val window = dialog.window
                lp.copyFrom(window!!.attributes)
                lp.width = WindowManager.LayoutParams.MATCH_PARENT
                lp.height = WindowManager.LayoutParams.WRAP_CONTENT
                window.attributes = lp
                dialog.show()

                cancel.setOnClickListener {

                    dialog.dismiss()
                }


                ok.setOnClickListener {

                    processDao.deleteProcessPackages(itemsViewModel.trackingNumber)

                    //arrayList.remove(itemsViewModel)


                    dialog.dismiss()


                    finish()
                    overridePendingTransition(0, 0)
                    startActivity(intent)
                    overridePendingTransition(0, 0)


                }
            }

            holder.create_img.setOnClickListener {

                val intent = Intent(this@ProcessPackageActivity, EditPackageActivity::class.java)

                intent.putExtra("trackingId", itemsViewModel.trackingNumber)
                intent.putExtra("id", itemsViewModel.id.toString())
                intent.putExtra("carriername", itemsViewModel.carriername)

                startActivity(intent)
            }



/*

            if (holder.options_layout != null) {
                holder.options_layout.setOnClickListener(View.OnClickListener {
                    if (position != RecyclerView.NO_POSITION) {
                       // remove(itemView.getContext(), getAdapterPosition())
                    }
                })
            }




            holder.swipelayout.setOnActionsListener(object : SwipeActionsListener {
                override fun onOpen(direction: Int, isContinuous: Boolean) {
                    if (direction == SwipeLayout.LEFT && isContinuous) {

                        holder.options_layout.visibility = View.VISIBLE

                    }

                }

                override fun onClose() {

                    holder.options_layout.visibility = View.INVISIBLE

                }
            })
*/


        }




        // binds the list items to a viewh


        // return the number of the items in the list
        override fun getItemCount(): Int {
            return mList.size
        }

        // Holds the views for adding it to image and text
        inner class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {

            val trackingNo: TextView = itemView.findViewById(R.id.trackingTV)
            val carruername: TextView = itemView.findViewById(R.id.carruername)
            val trackingcount: TextView = itemView.findViewById(R.id.trackingcount)
            val delete_img: ImageView = itemView.findViewById(R.id.delete_img)
            val create_img: ImageView = itemView.findViewById(R.id.create_img)
           val item_layout: RelativeLayout = itemView.findViewById(R.id.item_layout)

            val options_layout:LinearLayout = itemView.findViewById(R.id.options_layout)
          //  val swipelayout:SwipeLayout = itemView.findViewById(R.id.swipelayout)



        }






    }






    override fun onResume() {
        super.onResume()

        NetworkChangeReceiver.netConnectionCheckerReceiver = this
    }
    override fun onNetworkConnectionChanged(isConnected: Boolean) {

        showMessage(isConnected)

        if (isConnected){



            binding.profileSync.setImageResource(R.drawable.syncnew)


        }
        else{


            binding.profileSync.setImageResource(R.drawable.syncyellow)



        }
    }

}