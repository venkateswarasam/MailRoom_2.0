package com.xcarriermaterialdesign.process

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import android.view.GestureDetector.SimpleOnGestureListener
import android.widget.*
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.xcarriermaterialdesign.R
import com.xcarriermaterialdesign.adapter.CourseAdapter
import com.xcarriermaterialdesign.roomdatabase.ProcessDao
import com.xcarriermaterialdesign.roomdatabase.ProcessDatabase
import com.xcarriermaterialdesign.roomdatabase.ProcessPackage
import com.xcarriermaterialdesign.scanner.SimpleScannerActivity
import com.xcarriermaterialdesign.utils.*
import com.xcarriermaterialdesign.utils.SwipeHelper.UnderlayButtonClickListener
import com.zerobranch.layout.SwipeLayout
import com.zerobranch.layout.SwipeLayout.SwipeActionsListener
import java.lang.reflect.Type


class ProcessPackageActivity : AppCompatActivity(), NetworkChangeReceiver.NetCheckerReceiverListener {


    var arrayList = ArrayList<String>()

    var trackinglist:RecyclerView?= null
    var conslidate_lay:LinearLayout?= null
    var sub_consolidate:LinearLayout?= null
    var fab:FloatingActionButton?= null
    var close_fab:FloatingActionButton?= null
    var check_consle:CheckBox?= null

    var mainmenu_layout:RelativeLayout?= null
    var alertlayout:LinearLayout?= null
    var manual_entry:LinearLayout?= null
    var scanner_entry:LinearLayout?= null
    var info_layout:LinearLayout?= null
    var options_layout:LinearLayout?= null
    internal var step1:TextView?= null
    internal var oktext:TextView?= null
    internal var nextbutton:TextView?= null
    internal var info_button:ImageView?= null

    var check = false

    var buttonClicked = 0

    var adaptercount = 0

    internal var bulkid_layout:RelativeLayout?= null
    internal var nopendinglayout:LinearLayout?= null
    internal var keyupdown:ImageView?= null
    internal var keyupdown1:ImageView?= null
    internal var nopending_image:ImageView?= null

    private var mDetector: GestureDetector? = null

    //var adapter:ProcessAdapter?= null

    private var courseModalArrayList: ArrayList<CourseModal>? = null

    private var adapter: ProcessAdapter_new? = null


    private lateinit var processDao: ProcessDao

    private lateinit var processPackage: List<ProcessPackage>
    internal var profile:ImageView?= null

    internal var scannerfab:FloatingActionButton?= null
    internal var manualfab:FloatingActionButton?= null



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

    private fun showMessage(isConnected: Boolean) {

        if (!isConnected) {

            profile!!.setImageResource(R.drawable.syncyellow)





        } else {


            profile!!.setImageResource(R.drawable.syncnew)



            //  save!!.setImageDrawable(resources.getDrawable(R.drawable.savegreen))

        }


    }





    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_process_package)

        supportActionBar?.hide()

        //getdata()


        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        toolbar.setNavigationOnClickListener {

            finish()
        }

        profile = findViewById(R.id.profile_sync)

       startService(Intent( applicationContext, NetWorkService::class.java))

        if (!NetworkConnection().isNetworkAvailable(this)) {

            profile!!.setImageResource(R.drawable.syncyellow)

        }
        else{

            profile!!.setImageResource(R.drawable.syncnew)

        }





        conslidate_lay = findViewById(R.id.conslidate_lay)
        sub_consolidate = findViewById(R.id.sub_consolidate)
        check_consle = findViewById(R.id.check_consle)
        mainmenu_layout = findViewById(R.id.mainmenu_layout)
        alertlayout = findViewById(R.id.alertlayout)
        nopendinglayout = findViewById(R.id.nopendinglayout)

        manualfab = findViewById<FloatingActionButton>(R.id.manualfab)
        scannerfab = findViewById(R.id.scannerfab)

        trackinglist = findViewById(R.id.trackinglist)
        info_button = findViewById(R.id.info_button)
        fab = findViewById(R.id.fab)
        close_fab = findViewById(R.id.close_fab)
        step1 = findViewById(R.id.step1)
        oktext = findViewById(R.id.oktext)
        nextbutton = findViewById(R.id.nextbutton)
        scanner_entry = findViewById(R.id.scanner_entry)
        info_layout = findViewById(R.id.info_layout)
        manual_entry = findViewById(R.id.manual_entry)
        bulkid_layout = findViewById(R.id.bulkid_layout)
        keyupdown = findViewById(R.id.keyupdown)
        keyupdown1 = findViewById(R.id.keyupdown1)
        nopending_image = findViewById(R.id.nopending_image)

        mDetector = GestureDetector(this, Gesture())

        TypefaceUtil.overrideFont(applicationContext, "SERIF", "fonts/hel.otf")



        val db = Room.databaseBuilder(
            applicationContext,
            ProcessDatabase::class.java, "Process_database"
        ).allowMainThreadQueries().build()

        processDao = db.processDao()

        processPackage = processDao.getAllProcessPackages()


        if (processDao.getAllProcessPackages().isEmpty()){


            nopendinglayout!!.visibility = View.VISIBLE
        }


         adapter =
            ProcessAdapter_new(this, processDao.getAllProcessPackages())

        val manager = LinearLayoutManager(this)
        trackinglist!!.setHasFixedSize(true)

        trackinglist!!.layoutManager = manager
        trackinglist!!.adapter = adapter









        //  arrayList = intent.getSerializableExtra("list") as ArrayList<String>


        if (processDao.getAllProcessPackages().size>1){

            conslidate_lay!!.visibility = View.VISIBLE
        }

       // println("==arraylist$courseModalArrayList")


     //   trackinglist!!.layoutManager = LinearLayoutManager(this)


        //adapter = CourseAdapter(courseModalArrayList!!, this)


        /* adapter =
            ProcessAdapter(this, arrayList)

        trackinglist!!.adapter = adapter*/


        close_fab!!.setOnClickListener {


            alertlayout!!.visibility =  View.GONE
            fab!!.visibility =  View.VISIBLE
            close_fab!!.visibility =  View.GONE

        }


        check_consle!!.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { compoundButton, b ->
            if (check_consle!!.isChecked) {
                check_consle!!.isChecked = true

                bulkid_layout!!.visibility = View.VISIBLE

            } else {
                check_consle!!.isChecked = false
                bulkid_layout!!.visibility = View.GONE

            }
        })





        keyupdown!!.setOnClickListener {

            trackinglist!!.visibility = View.GONE
            keyupdown!!.visibility = View.GONE
            keyupdown1!!.visibility = View.VISIBLE
        }

        keyupdown1!!.setOnClickListener {

            trackinglist!!.visibility = View.VISIBLE
            keyupdown!!.visibility = View.VISIBLE
            keyupdown1!!.visibility = View.GONE
        }



        fab!!.setOnClickListener {




            when(buttonClicked){

               0->

               {



                  // trackinglist!!.setBackgroundColor(resources.getColor(android.R.color.transparent));
                 //  alertlayout!!.setBackgroundColor(resources.getColor(android.R.color.transparent));
                   trackinglist!!.setBackgroundColor(Color.parseColor("#979797"));
                 alertlayout!!.setBackgroundColor(Color.parseColor("#979797"));
                 mainmenu_layout!!.setBackgroundColor(Color.parseColor("#979797"));
                 conslidate_lay!!.setBackgroundColor(Color.parseColor("#979797"));
                   sub_consolidate!!.setBackgroundColor(Color.parseColor("#979797"));

                   nopendinglayout!!.visibility = View.GONE

                   nopending_image!!.setBackgroundColor(Color.parseColor("#00000000"));
                //   nopendinglayout!!.setBackgroundColor(Color.parseColor("#979797"));



                   fab!!.setImageResource(R.drawable.ic_baseline_close_24)
                   alertlayout!!.visibility =  View.VISIBLE
                   // close_fab!!.visibility =  View.VISIBLE
                   //  fab!!.visibility =  View.GONE

                   buttonClicked = 1






               }

                1->{

                    buttonClicked = 0

                   mainmenu_layout!!.setBackgroundColor(Color.parseColor("#FFFFFF"));

                  /*  trackinglist!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                    alertlayout!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                    mainmenu_layout!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))*/

                    trackinglist!!.setBackgroundColor(Color.parseColor("#FFFFFF"));
                    alertlayout!!.setBackgroundColor(Color.parseColor("#FFFFFF"));
                    conslidate_lay!!.setBackgroundColor(Color.parseColor("#FFFFFF"));
                    sub_consolidate!!.setBackgroundColor(Color.parseColor("#FFFFFF"));
                    nopending_image!!.setBackgroundColor(Color.parseColor("#FFFFFF"));
                  //  nopendinglayout!!.setBackgroundColor(Color.parseColor("#FFFFFF"));

                    if (processDao.getAllProcessPackages().isEmpty()){

                        nopendinglayout!!.visibility = View.VISIBLE

                    }



                    fab!!.setImageResource(R.drawable.addsymbol)

                    alertlayout!!.visibility =  View.GONE
                }
               else -> {

                   fab!!.setImageResource(R.drawable.addsymbol)

                   alertlayout!!.visibility =  View.GONE

               }
           }








             //   fab!!.setImageResource(R.drawable.ic_baseline_close_24)








             //   fab!!.setImageResource(R.drawable.addsymbol)








         //   trackinglist!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))


           /* val dialog = Dialog(this)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)


            dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            dialog.setCancelable(true)
            dialog.setContentView(R.layout.servicedialog)*/

           /* val manual_entry = dialog.findViewById<LinearLayout>(R.id.manual_entry)
            val scanner_entry = dialog.findViewById<LinearLayout>(R.id.scanner_entry)
            val close_fab = dialog.findViewById<FloatingActionButton>(R.id.close_fab)
*/

          /*  close_fab.setOnClickListener {

               dialog.dismiss()
            }

            //   tts!!.speak(message, TextToSpeech.QUEUE_FLUSH, null)

            val lp = WindowManager.LayoutParams()
            val window = dialog.window
            lp.copyFrom(window!!.attributes)
            lp.width = WindowManager.LayoutParams.MATCH_PARENT
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT
            val isFinishing = false
            window.attributes = lp
            dialog.show()
*/
        }


        manual_entry!!.setOnClickListener {

            // onBackPressed()

            val intent = Intent(this, ManualProcessPackageActivity::class.java)
            startActivity(intent)
        }


        scanner_entry!!.setOnClickListener {

            val intent = Intent(this, SimpleScannerActivity::class.java)
            startActivity(intent)
        }


        manualfab?.setOnClickListener {

            val intent = Intent(this, ManualProcessPackageActivity::class.java)
            startActivity(intent)
        }

        scannerfab?.setOnClickListener {


            val intent = Intent(this, SimpleScannerActivity::class.java)
            startActivity(intent)

        }



        nextbutton!!.setOnClickListener {

            val intent = Intent(this, ProcessPackageFinalActivity::class.java)

            startActivity(intent)
        }






        info_button!!.setOnClickListener {



            info_layout!!.visibility = View.VISIBLE












           /* val dialog = Dialog(this)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
           dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            dialog.setCancelable(false)
            dialog.setContentView(R.layout.info_layout)

            val ok = dialog.findViewById<TextView>(R.id.oktext)



            //   tts!!.speak(message, TextToSpeech.QUEUE_FLUSH, null)

            val lp = WindowManager.LayoutParams()
            val window = dialog.window
            lp.copyFrom(window!!.attributes)
            lp.width = WindowManager.LayoutParams.MATCH_PARENT
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT
            val isFinishing = false
            window.attributes = lp
            dialog.show()

            ok.setOnClickListener {

                dialog.dismiss()
            }
            */






        }

        oktext!!.setOnClickListener {

            info_layout!!.visibility = View.GONE
        }


     //   swipe(processPackage)



    }






    // swipe helper



    private fun swipe(mList: List<ProcessPackage>){


        val itemTouchHelper = ItemTouchHelper(object : SwipeHelper(this, trackinglist, false) {


            override fun instantiateUnderlayButton(
                viewHolder: RecyclerView.ViewHolder?,
                underlayButtons: MutableList<UnderlayButton>?
            ) {
                underlayButtons?.add(SwipeHelper.UnderlayButton(

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


                underlayButtons?.add(SwipeHelper.UnderlayButton(
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
                        val isFinishing = false
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


                            finish();
                            overridePendingTransition(0, 0);
                            startActivity(intent);
                            overridePendingTransition(0, 0);

                            /*finish();
                            startActivity(intent);*/
                        }











                    }
                ))



            }
        })

        itemTouchHelper.attachToRecyclerView(trackinglist)

    }






    private fun initactionbar(){

       supportActionBar?.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
        supportActionBar?.setDisplayShowCustomEnabled(true)
        supportActionBar?.setCustomView(R.layout.actionbarnew)
        val view1: View =  supportActionBar!!.customView
        val toolbar: Toolbar = view1.parent as Toolbar
        toolbar.setContentInsetsAbsolute(0, 0)
        //  toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24)
        val headertext: TextView = view1.findViewById(R.id.headertext)
        headertext.text = getString(R.string.process)
        val profile: ImageView = view1.findViewById(R.id.profile)
      //  val back: ImageView = view1.findViewById(R.id.back)



        profile.setOnClickListener {



        }
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

            var check = false

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
                val isFinishing = false
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


        private val SHOW_MENU = 1
        private val HIDE_MENU = 2
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
                val isFinishing = false
                window.attributes = lp
                dialog.show()

                cancel.setOnClickListener {

                    dialog.dismiss()
                }


                ok.setOnClickListener {

                    processDao.deleteProcessPackages(itemsViewModel.trackingNumber)

                    //arrayList.remove(itemsViewModel)


                    dialog.dismiss()


                    finish();
                    overridePendingTransition(0, 0);
                    startActivity(intent);
                    overridePendingTransition(0, 0);

                    /*finish();
                    startActivity(intent);*/
                }
                // alert()
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



    inner class CourseAdapter(// creating a variable for array list and context.
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



         profile!!.setImageResource(R.drawable.syncnew)


        }
        else{


            profile!!.setImageResource(R.drawable.syncyellow)



        }
    }

}