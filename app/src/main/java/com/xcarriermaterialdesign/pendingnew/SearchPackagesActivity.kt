package com.xcarriermaterialdesign.pendingnew

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.xcarriermaterialdesign.R
import com.xcarriermaterialdesign.databinding.ActivitySearchPackagesBinding
import com.xcarriermaterialdesign.pending.PendingDeliveriesActvity
import com.xcarriermaterialdesign.activities.manual.ManualProcessPackageActivity
import com.xcarriermaterialdesign.roomdatabase.BulkDao
import com.xcarriermaterialdesign.roomdatabase.BulkPackage
import com.xcarriermaterialdesign.roomdatabase.ProcessDao
import com.xcarriermaterialdesign.roomdatabase.ProcessDatabase
import com.xcarriermaterialdesign.activities.scanner.SimpleScannerActivity
import com.xcarriermaterialdesign.utils.AnalyticsApplication

class SearchPackagesActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchPackagesBinding

    val model: SearchPackageViewModel by viewModels()


    var buttonClicked = 0

    private lateinit var processDao: ProcessDao
    private lateinit var bulkDao: BulkDao

    private var adapter: ProcessAdapter_new? = null

    lateinit var rowlist:List<BulkPackage>







    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar?.hide()
        model.config(this)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_search_packages)


      //  val trackingnumber = intent.getStringExtra("trackingnumber")

        val db = Room.databaseBuilder(
            (this@SearchPackagesActivity),
            ProcessDatabase::class.java, "Process_database"
        ).allowMainThreadQueries().build()

        processDao = db.processDao()
        bulkDao = db.bulkDao()


        if (bulkDao.getAllBulkPackages().isEmpty()){


            binding.pendingimage.visibility = View.VISIBLE
            binding.pendingtext.visibility = View.VISIBLE
            binding.pendinglist.visibility = View.GONE
        }


        if (bulkDao.getAllBulkPackages().size > 50){

            binding.loadmore.visibility = View.VISIBLE
        }

        if (AnalyticsApplication.instance?.getPlantId()?.isNotEmpty()!!){




            rowlist = bulkDao.isData(AnalyticsApplication.instance?.getPlantId())

            println("==rowlist==$rowlist")


            adapter =
                ProcessAdapter_new(this@SearchPackagesActivity, rowlist)

            val manager = LinearLayoutManager(this@SearchPackagesActivity)
            binding.pendinglist!!.setHasFixedSize(true)

            binding.pendinglist!!.layoutManager = manager
            binding.pendinglist!!.adapter = adapter




        }

        else{


            rowlist = bulkDao.isbinData(AnalyticsApplication.instance?.getCompanyId())

            println("==rowlist==$rowlist")


            adapter =
                ProcessAdapter_new(this@SearchPackagesActivity, rowlist)

            val manager = LinearLayoutManager(this@SearchPackagesActivity)
            binding.pendinglist!!.setHasFixedSize(true)

            binding.pendinglist!!.layoutManager = manager
            binding.pendinglist!!.adapter = adapter

        }






        binding.fab.setOnClickListener {


            when(buttonClicked){

                0->{


                    binding.alertlayout.visibility = View.VISIBLE
                    binding.fab.setImageResource(R.drawable.ic_baseline_close_24)
                   binding.searchLayout!!.setBackgroundColor(Color.parseColor("#979797"));
                    binding.horizontalscroll!!.setBackgroundColor(Color.parseColor("#979797"));

                    /*   binding.last30.setBackgroundResource(R.drawable.text_bg1)
                       binding.last60.setBackgroundResource(R.drawable.text_bg1)
                       binding.last90.setBackgroundResource(R.drawable.text_bg1)
   */

                    //  binding.searchLayouts.setBackgroundColor(Color.parseColor("#979797"))
                    binding.searchLayouts.setBackgroundResource(R.drawable.searchbg1)

                    //

                    binding.pendingimage.visibility = View.GONE
                    binding.pendingtext.visibility =  View.GONE

                    binding.pendingimage!!.setBackgroundColor(Color.parseColor("#979797"));



                    binding.manualEntry.setOnClickListener {

                        val intent = Intent(this, ManualProcessPackageActivity::class.java)
                        startActivity(intent)
                    }

                    binding.scannerEntry!!.setOnClickListener {

                        val intent = Intent(this, SimpleScannerActivity::class.java)
                        startActivity(intent)
                    }

                    buttonClicked = 1
                }

                1->{

                    buttonClicked = 0

                    binding.alertlayout.visibility = View.GONE
                    binding.fab.setImageResource(R.drawable.addsymbol)

                    if (bulkDao.getAllBulkPackages().isEmpty()){


                        binding.pendingimage.visibility = View.VISIBLE
                        binding.pendingtext.visibility =  View.VISIBLE
                    }



                    binding.searchLayout!!.setBackgroundColor(Color.parseColor("#FFFFFF"));
                    binding.searchLayouts.setBackgroundColor(Color.parseColor("#FFFFFF"))
                    binding.searchLayouts.setBackgroundResource(R.drawable.searchbg)


                    /*binding.last30.setBackgroundResource(R.drawable.text_bg)
                    binding.last60.setBackgroundResource(R.drawable.text_bg)
                    binding.last90.setBackgroundResource(R.drawable.text_bg)
*/

                    //  binding.search_layouts!!.setBackgroundColor(Color.parseColor("#FFFFFF"));
                    binding.horizontalscroll!!.setBackgroundColor(Color.parseColor("#FFFFFF"));
                    binding.pendingimage!!.setBackgroundColor(Color.parseColor("#FFFFFF"));



                }
            }


        }


        binding.searchLayouts.setOnClickListener {


            val intent = Intent(this, PendingDeliveriesActvity::class.java)
            startActivity(intent)
        }



        binding.last60.setOnClickListener {

            AnalyticsApplication.instance?.setPlantId("")
            AnalyticsApplication.instance?.setCompanyId("")

            binding.last60.setBackgroundResource(R.drawable.text_bg_new)
            binding.last90.setBackgroundResource(R.drawable.text_bg)
            binding.last30.setBackgroundResource(R.drawable.text_bg)



            adapter =
                ProcessAdapter_new((this), bulkDao.getAllBulkPackages())

            val manager = LinearLayoutManager(this)
            binding.pendinglist!!.setHasFixedSize(true)

            binding.pendinglist!!.layoutManager = manager
            binding.pendinglist!!.adapter = adapter

        }


        binding.last30.setOnClickListener {

            AnalyticsApplication.instance?.setPlantId("")
            AnalyticsApplication.instance?.setCompanyId("")
            binding.last30.setBackgroundResource(R.drawable.text_bg_new)
            binding.last90.setBackgroundResource(R.drawable.text_bg)
            binding.last60.setBackgroundResource(R.drawable.text_bg)



            adapter =
                ProcessAdapter_new((this), bulkDao.getAllBulkPackages())

            val manager = LinearLayoutManager(this)
            binding.pendinglist!!.setHasFixedSize(true)

            binding.pendinglist!!.layoutManager = manager
            binding.pendinglist!!.adapter = adapter
        }


        binding.last90.setOnClickListener {

            AnalyticsApplication.instance?.setPlantId("")
            AnalyticsApplication.instance?.setCompanyId("")

            binding.last30.setBackgroundResource(R.drawable.text_bg)
            binding.last90.setBackgroundResource(R.drawable.text_bg_new)
            binding.last60.setBackgroundResource(R.drawable.text_bg)


            adapter =
                ProcessAdapter_new((this), bulkDao.getAllBulkPackages())

            val manager = LinearLayoutManager(this)
            binding.pendinglist!!.setHasFixedSize(true)

            binding.pendinglist!!.layoutManager = manager
            binding.pendinglist!!.adapter = adapter

        }





        //setContentView(R.layout.activity_search_packages)
    }

    inner class ProcessAdapter_new(val context : Context, private val mList: List<BulkPackage>) : RecyclerView.Adapter<ProcessAdapter_new.ViewHolder>() {



        // create new views
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProcessAdapter_new.ViewHolder {
            // inflates the card_view_design view
            // that is used to hold list item
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.pending_deliveries, parent, false)

            return ViewHolder(view)
        }

        @SuppressLint("SimpleDateFormat")
        override fun onBindViewHolder(holder: ViewHolder, position: Int) {




            val itemsViewModel = mList[position]


            holder.trackingNo.text = itemsViewModel.trackingNumber ?: ""
            holder.status.text = itemsViewModel.packagestatus ?: ""

            //  val df: DateFormat = SimpleDateFormat("EEE MMM d yyyy • HH:mm aa")

            /*  val enDate = SimpleDateFormat("EEE MMM d yyyy • HH:mm aa", Locale("en"))

              val date: String = enDate.format(Calendar.getInstance().time)*/

            holder.dateandtime.text = itemsViewModel.datetime




            if (itemsViewModel.packagestatus == "Received"){

                holder.pen_image.setImageResource(R.drawable.dhll)
            }

            else if (itemsViewModel.packagestatus == "Delivered"){

                holder.status.setTextColor(Color.parseColor("#007CBB"))

                holder.pen_image.setImageResource(R.drawable.usps)

            }

            else{

                //    holder.status.setTextColor(Color.parseColor("#007CBB"))

                holder.pen_image.setImageResource(R.drawable.fedex)

            }





            holder.trackinglayout.setOnClickListener {

                /* val intent = Intent(context, TrackReportActivty::class.java)
                 intent.putExtra("trackingno", itemsViewModel.trackingNumber)

                 startActivity(intent)*/
            }








        }




        // binds the list items to a viewh


        // return the number of the items in the list
        override fun getItemCount(): Int {
            return mList.size
        }

        // Holds the views for adding it to image and text
        inner class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {

            val trackingNo: TextView = itemView.findViewById(R.id.trackingTV)
            val trackinglayout: LinearLayout = itemView.findViewById(R.id.trackinglayout)
            val status: TextView = itemView.findViewById(R.id.status_tv)
            val dateandtime: TextView = itemView.findViewById(R.id.dateandtime)
            val pen_image: ImageView = itemView.findViewById(R.id.pen_image)


        }






    }

}