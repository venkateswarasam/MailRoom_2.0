package com.xcarriermaterialdesign.process

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.*
import android.graphics.drawable.ColorDrawable
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




    // viewmodel

    private lateinit var binding: ActivityProcessPackageBinding

    val model: ProcessViewModel by viewModels()


    private fun getdata(){

        val sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE)

        val gson = Gson()
        val json = sharedPreferences.getString("trackingnumbers", null)

        val type: Type = object : TypeToken<ArrayList<CourseModal?>?>() {}.type

        courseModalArrayList = gson.fromJson(json, type)

        if (courseModalArrayList == null) {
            // if the array list is empty
            // creating a new array list.
            courseModalArrayList = ArrayList()
        }
    }

    private fun showMessage(isConnected: Boolean) {

        if (!isConnected) {

            binding.profileSync.setImageResource(R.drawable.syncyellow)





        } else {


            binding.profileSync.setImageResource(R.drawable.syncnew)



            //  save!!.setImageDrawable(resources.getDrawable(R.drawable.savegreen))

        }


    }





    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
       // setContentView(R.layout.activity_process_package)


        model.config(this)

        supportActionBar?.hide()

        binding = DataBindingUtil.setContentView(this, R.layout.activity_process_package)


        //getdata()


        binding.toolbar.setNavigationOnClickListener {

            finish()
        }


       startService(Intent( applicationContext, NetWorkService::class.java))

        if (!NetworkConnection().isNetworkAvailable(this)) {

            binding.profileSync.setImageResource(R.drawable.syncyellow)

        }
        else{

            binding.profileSync.setImageResource(R.drawable.syncnew)

        }




        mDetector = GestureDetector(this, Gesture())




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


     //   swipe(processPackage)





    }






    // swipe helper





    private fun swipe(mList: List<ProcessPackage>){


        val itemTouchHelper = ItemTouchHelper(object : SwipeHelper(this, binding.trackinglist, false) {


            override fun instantiateUnderlayButton(
                viewHolder: RecyclerView.ViewHolder?,
                underlayButtons: MutableList<UnderlayButton>?
            ) {
                underlayButtons?.add(
                    SwipeHelper.UnderlayButton(

                    AppCompatResources.getDrawable(
                        this@ProcessPackageActivity,
                        R.drawable.ic_baseline_create_24
                    ),
                    Color.parseColor("#BABABA"),
                    UnderlayButtonClickListener { pos: Int ->

                        for (i in 0 until mList.size){

                            //processDao.deleteProcessPackages(mList.get(i).trackingNumber)



                            val intent = Intent(this@ProcessPackageActivity, EditPackageActivity::class.java)

                            intent.putExtra("trackingId", mList.get(i).trackingNumber)
                            intent.putExtra("id", mList.get(i).id)
                            intent.putExtra("carriername", mList.get(i).carriername)

                            startActivity(intent)

                        }



                        // adapter.modelList.removeAt(pos);
                      //  adapter!!.notifyItemRemoved(pos)
                    }
                ))


                underlayButtons?.add(
                    SwipeHelper.UnderlayButton(
                    AppCompatResources.getDrawable(
                        this@ProcessPackageActivity,
                        R.drawable.ic_outline_delete_outline_24
                    ),
                    Color.parseColor("#B00020"),
                    UnderlayButtonClickListener { pos: Int ->


                        // adapter.modelList.removeAt(pos);
                      //  adapter!!.notifyItemRemoved(pos)



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

                            for (i in 0 until mList.size){

                                processDao.deleteProcessPackages(mList.get(i).trackingNumber)

                            }


                            //arrayList.remove(itemsViewModel)


                            dialog.dismiss()


                            finish()
                            overridePendingTransition(0, 0)
                            startActivity(intent)
                            overridePendingTransition(0, 0)


                        }











                    }
                ))



            }
        })

        itemTouchHelper.attachToRecyclerView(binding.trackinglist)

    }










    internal class Gesture : SimpleOnGestureListener() {
        override fun onSingleTapUp(ev: MotionEvent): Boolean {
            return true
        }
        override fun onLongPress(ev: MotionEvent) {}
        override fun onScroll(
            e1: MotionEvent, e2: MotionEvent, distanceX: Float,
            distanceY: Float
        ): Boolean {

            return true
        }

        override fun onFling(
            e1: MotionEvent, e2: MotionEvent, velocityX: Float,
            velocityY: Float
        ): Boolean {

            return true
        }
    }

    inner class ProcessAdapter(val context : Context, private val mList: ArrayList<String>) : RecyclerView.Adapter<ProcessAdapter.ViewHolder>() {



        // create new views
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            // inflates the card_view_design view
            // that is used to hold list item
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.process_item_list, parent, false)

            return ViewHolder(view)
        }

        // binds the list items to a viewh


        // return the number of the items in the list
        override fun getItemCount(): Int {
            return mList.size
        }

        // Holds the views for adding it to image and text
        inner class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {

            val trackingNo: TextView = itemView.findViewById(R.id.trackingTV)
            val delete_img: ImageView = itemView.findViewById(R.id.delete_img)
            val create_img: ImageView = itemView.findViewById(R.id.create_img)
            val item_layout: RelativeLayout = itemView.findViewById(R.id.item_layout)

            val options_layout:LinearLayout = itemView.findViewById(R.id.options_layout)



        }



        @SuppressLint("ClickableViewAccessibility")
        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val itemsViewModel = mList[position]

            // sets the image to the imageview from our itemHolder class


            holder.trackingNo.text = itemsViewModel ?: ""



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



/*
            holder.item_layout.setOnTouchListener { view, motionEvent ->



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







                true
















            }
*/













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

                    processDao.deleteProcessPackages(itemsViewModel)

                    //arrayList.remove(itemsViewModel)


                    dialog.dismiss()
                }
               // alert()
            }

            holder.create_img.setOnClickListener {

                val intent = Intent(this@ProcessPackageActivity, ManualProcessPackageActivity::class.java)

                startActivity(intent)
            }

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


            holder.trackingNo.text = itemsViewModel.trackingNumber ?: ""
            holder.carruername.text = itemsViewModel.carriername ?: ""

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
            val delete_img: ImageView = itemView.findViewById(R.id.delete_img)
            val create_img: ImageView = itemView.findViewById(R.id.create_img)
           val item_layout: RelativeLayout = itemView.findViewById(R.id.item_layout)

            val options_layout:LinearLayout = itemView.findViewById(R.id.options_layout)
          //  val swipelayout:SwipeLayout = itemView.findViewById(R.id.swipelayout)



        }






    }



    inner class CourseAdapter(
        // creating a variable for array list and context.
        private val courseModalArrayList: ArrayList<CourseModal>, context: Context
    ) :
        RecyclerView.Adapter<CourseAdapter.ViewHolder>() {
        private val context: Context

        // creating a constructor for our variables.
        init {
            this.context = context
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            // below line is to inflate our layout.

            val view = LayoutInflater.from(parent.context)
                .inflate(com.xcarriermaterialdesign.R.layout.process_item_list, parent, false)

            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            // setting data to our views of recycler view.
            val modal = courseModalArrayList[position]
            holder.trackingNo.text = modal.trackingNo

            // holder.courseDescTV.setText(modal.getCourseDescription())
        }

        override fun getItemCount(): Int {
            // returning the size of array list.
            return courseModalArrayList.size
        }

        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            // creating variables for our views.
            val trackingNo: TextView = itemView.findViewById(com.xcarriermaterialdesign.R.id.trackingTV)
            val delete_img: ImageView = itemView.findViewById(com.xcarriermaterialdesign.R.id.delete_img)
            val create_img: ImageView = itemView.findViewById(com.xcarriermaterialdesign.R.id.create_img)
            val item_layout: RelativeLayout = itemView.findViewById(com.xcarriermaterialdesign.R.id.item_layout)

            val options_layout: LinearLayout = itemView.findViewById(com.xcarriermaterialdesign.R.id.options_layout)


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