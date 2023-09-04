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
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.xcarriermaterialdesign.BottomNavigationActivity
import com.xcarriermaterialdesign.R
import com.xcarriermaterialdesign.databinding.ActivitySearchPackagesBinding
import com.xcarriermaterialdesign.pending.PendingDeliveriesActivity
import com.xcarriermaterialdesign.activities.manual.ManualProcessPackageActivity
import com.xcarriermaterialdesign.roomdatabase.BulkDao
import com.xcarriermaterialdesign.roomdatabase.BulkPackage
import com.xcarriermaterialdesign.roomdatabase.ProcessDao
import com.xcarriermaterialdesign.roomdatabase.ProcessDatabase
import com.xcarriermaterialdesign.activities.scanner.SimpleScannerActivity
import com.xcarriermaterialdesign.model.LstResponse
import com.xcarriermaterialdesign.model.PendingRequest
import com.xcarriermaterialdesign.model.PendingResponse
import com.xcarriermaterialdesign.trackreport.TrackReportActivty
import com.xcarriermaterialdesign.utils.AnalyticsApplication
import com.xcarriermaterialdesign.utils.ApplicationSharedPref
import com.xcarriermaterialdesign.utils.LoadingView
import com.xcarriermaterialdesign.utils.ServiceDialog
import java.text.SimpleDateFormat
import java.util.Date

class SearchPackagesActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchPackagesBinding

    val model: SearchPackageViewModel by viewModels()


    var buttonClicked = 0

    private lateinit var processDao: ProcessDao
    private lateinit var bulkDao: BulkDao

    private var adapter: ProcessAdapter_new? = null
    private var adapter1: ProcessAdapter_new? = null

    lateinit var rowlist:List<BulkPackage>




    var rowcount = 0

    var lastDays = ""
    var itemcount = 0

    private val ctegroieslist: MutableList<LstResponse> = mutableListOf()



    override fun onBackPressed() {

        val intent = Intent(this, BottomNavigationActivity::class.java)

        intent.putExtra("navcheck","true")

        startActivity(intent)

        finish()
    }


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


            val intent = Intent(this, PendingDeliveriesActivity::class.java)
            startActivity(intent)
        }



        binding.last60.setOnClickListener {

            AnalyticsApplication.instance?.setPlantId("")
            AnalyticsApplication.instance?.setCompanyId("")
            rowcount = 0
            bulkDao.deleteAllBulkPackages()
            ctegroieslist.clear()

            binding.last60.setBackgroundResource(R.drawable.text_bg_new)
            binding.last90.setBackgroundResource(R.drawable.text_bg)
            binding.last30.setBackgroundResource(R.drawable.text_bg)


            lastDays = model.getBeforeDate(true,true,true,59)

            getpackageactivedetails()

            /*adapter =
                ProcessAdapter_new((this), bulkDao.getAllBulkPackages())

            val manager = LinearLayoutManager(this)
            binding.pendinglist!!.setHasFixedSize(true)

            binding.pendinglist!!.layoutManager = manager
            binding.pendinglist!!.adapter = adapter*/

        }


        binding.last30.setOnClickListener {

            rowcount = 0
            bulkDao.deleteAllBulkPackages()
            ctegroieslist.clear()

            AnalyticsApplication.instance?.setPlantId("")
            AnalyticsApplication.instance?.setCompanyId("")
            binding.last30.setBackgroundResource(R.drawable.text_bg_new)
            binding.last90.setBackgroundResource(R.drawable.text_bg)
            binding.last60.setBackgroundResource(R.drawable.text_bg)

            lastDays = model.getBeforeDate(true,true,true,29)


            getpackageactivedetails()




        }


        binding.last90.setOnClickListener {

            rowcount = 0
            bulkDao.deleteAllBulkPackages()
            ctegroieslist.clear()

            AnalyticsApplication.instance?.setPlantId("")
            AnalyticsApplication.instance?.setCompanyId("")

            binding.last30.setBackgroundResource(R.drawable.text_bg)
            binding.last90.setBackgroundResource(R.drawable.text_bg_new)
            binding.last60.setBackgroundResource(R.drawable.text_bg)

            lastDays = model.getBeforeDate(true,true,true,89)

            getpackageactivedetails()


        }




        model.pendingResponse.observe(this, Observer<PendingResponse> { item ->

            LoadingView.hideLoading()

            println("==list==${item}")


            if (item.StatusCode == 200){

                println("==list==${item.Result.lstResponse}")

//                Toast.makeText(activity as AppCompatActivity, item.Result.TotalCount, Toast.LENGTH_SHORT).show()


                if (item.Result.lstResponse.isNotEmpty()){


                  //  ctegroieslist.addAll(item.Result.lstResponse)


                    itemcount = item.Result.TotalCount

                    println("==itemcount==$itemcount")


                    if (itemcount> 50){


                        binding.loadmore.visibility = View.VISIBLE

                        //  rowcount = 50


                    }






                    ctegroieslist.addAll(item.Result.lstResponse)



                   val adapter = ProcessAdapter_new1(this, ctegroieslist)



                    val manager = LinearLayoutManager(this)
                    binding.pendinglist!!.setHasFixedSize(true)

                    binding.pendinglist!!.layoutManager = manager
                    binding.pendinglist!!.adapter = adapter

                 /*   for (i in 0 until item.Result.lstResponse.size){


                        val itemviewmodel = item.Result.lstResponse[i]


                        bulkDao.insertBulkPackage(BulkPackage(itemviewmodel.InternalTrackNo,
                            itemviewmodel.CurrentStatus,
                            itemviewmodel.UpdatedOn,
                            "",itemviewmodel.BuildingId,
                            itemviewmodel.CarrierDescription))
                    }*/






                }


                else{

                    binding.pendingimage.visibility = View.VISIBLE
                    binding.pendingtext.visibility = View.VISIBLE
                    binding.pendinglist.visibility = View.GONE
                }












            }

            else if (item.Result.ReturnMsg == "No data to display"){

                ServiceDialog.ShowDialog(this, item.Result.ReturnMsg)

                binding.loadmore.visibility = View.INVISIBLE

            }

            else{

                ServiceDialog.ShowDialog(this, item.Result.ReturnMsg)
            }











        });



        binding.last30.setBackgroundResource(R.drawable.text_bg)

        binding.loadmore.setOnClickListener {

            rowcount += 50

            println("==rowcount==$rowcount")

            getpackageactivedetails()
        }




    }





    fun getpackageactivedetails(){



        println("==lastdate==$lastDays")

        val pendingRequest = PendingRequest("",
            ApplicationSharedPref.read(ApplicationSharedPref.COMPANY_ID,"")!!,
            lastDays,"",
            ApplicationSharedPref.read(ApplicationSharedPref.LOGINID,"")!!.toInt(),
            ApplicationSharedPref.read(ApplicationSharedPref.PLANT_ID,"")!!,"","","",
            SimpleDateFormat("yyyy-MM-dd").format(
                Date()
            ),rowcount)

        model.loadingpendingdeliveries(pendingRequest)


    }


    inner class ProcessAdapter_new1(val context : Context, private val mList: List<LstResponse>) : RecyclerView.Adapter<ProcessAdapter_new1.ViewHolder>() {



        // create new views
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProcessAdapter_new1.ViewHolder {
            // inflates the card_view_design view
            // that is used to hold list item
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.pending_deliveries, parent, false)

            return ViewHolder(view)
        }

        @SuppressLint("SimpleDateFormat")
        override fun onBindViewHolder(holder: ViewHolder, position: Int) {




            val itemsViewModel = mList[position]


            holder.trackingNo.text = itemsViewModel.InternalTrackNo ?: ""
            holder.status.text = itemsViewModel.CurrentStatus ?: ""

            //  val df: DateFormat = SimpleDateFormat("EEE MMM d yyyy • HH:mm aa")

            /*  val enDate = SimpleDateFormat("EEE MMM d yyyy • HH:mm aa", Locale("en"))

              val date: String = enDate.format(Calendar.getInstance().time)*/

            holder.dateandtime.text = itemsViewModel.UpdatedOn

            when(itemsViewModel.CurrentStatus){

                "Delivered"->{

                    holder.status.setTextColor(Color.parseColor("#007CBB"))



                }
                "Cancelled"->{

                    holder.status.setTextColor(Color.parseColor("#B00020"))

                }

                else->{

                    holder.status.setTextColor(Color.parseColor("#99000000"))




                }

            }









            holder.trackinglayout.setOnClickListener {

                val intent = Intent(context, TrackReportActivty::class.java)
                intent.putExtra("trackingno", itemsViewModel.InternalTrackNo)

                startActivity(intent)
            }



            val carrier = itemsViewModel.CarrierDescription



            when(carrier){

                "ACTION FREIGHT SYSTEM"->{


                    holder.pen_image.setImageResource(R.drawable.box)

                }

                "Amazon"->{


                    holder.pen_image.setImageResource(R.drawable.amazonlogo)

                }
                "CEVA"->{


                    holder.pen_image.setImageResource(R.drawable.cevalogo)

                }
                "DB Schenker"->{


                    holder.pen_image.setImageResource(R.drawable.dbschenkerlogo)

                }
                "EAGLE TRANSPORT INC."->{


                    holder.pen_image.setImageResource(R.drawable.eagletransportlogo)

                }
                "EZXPEDITORS"->{


                    holder.pen_image.setImageResource(R.drawable.expeditorslogo)

                }
                "Hard Drives Northwest"->{


                    holder.pen_image.setImageResource(R.drawable.harddriveslogo)

                }
                "lone star"->{


                    holder.pen_image.setImageResource(R.drawable.lonestarlogo)

                }
                "LOWES"->{


                    holder.pen_image.setImageResource(R.drawable.loweslogo)

                }
                "LYNDEN INTERNATIONAL"->{


                    holder.pen_image.setImageResource(R.drawable.lyndenlogo)

                }
                "OAK HARBOR FREIGHT LINES"->{


                    holder.pen_image.setImageResource(R.drawable.oakhlogo)

                }
                "OFFICE DEPOT"->{


                    holder.pen_image.setImageResource(R.drawable.officedepotlogo)

                }


                "DHL"->{


                    holder.pen_image.setImageResource(R.drawable.dhllogo)


                }
                "OnTrac"->{


                    holder.pen_image.setImageResource(R.drawable.ontracklogo)


                }
                "Overnight Express"->{


                    holder.pen_image.setImageResource(R.drawable.overnitenet)


                }
                "SAIA MOTOR FREIGHT"->{


                    holder.pen_image.setImageResource(R.drawable.saialogo)


                }
                "SHO AIR"->{


                    holder.pen_image.setImageResource(R.drawable.shoairlogo)


                }
                "STAPLES"->{



                    holder.pen_image.setImageResource(R.drawable.stapleslogo)


                }
                "United States Postal Service"->{


                    holder.pen_image.setImageResource(R.drawable.uspslogo)


                }
                "UPS"->{


                    holder.pen_image.setImageResource(R.drawable.ups)


                }


                "USF Reddaway"->{


                    holder.pen_image.setImageResource(R.drawable.reddawaylogo)


                }

                "WORLD WIDE TECH"->{

                    holder.pen_image.setImageResource(R.drawable.wwtlogo)

                }

                "YRC Worldwide"->{

                    holder.pen_image.setImageResource(R.drawable.yrclogo)

                }
                "Zones"->{

                    holder.pen_image.setImageResource(R.drawable.zoneslogo)

                }



                "FedEx"->{

                    holder.pen_image.setImageResource(R.drawable.fedexlogo)


                }


                else->{

                    holder.pen_image.setImageResource(R.drawable.box)

                }

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


            holder.dateandtime.text = itemsViewModel.datetime


            if (itemsViewModel.carriername == "DHL"){

                holder.pen_image.setImageResource(R.drawable.dhllogo)
            }

            else if (itemsViewModel.carriername == "USPS"){

                //   holder.status.setTextColor(Color.parseColor("#007CBB"))

                holder.pen_image.setImageResource(R.drawable.usps)

            }


            else if (itemsViewModel.carriername == "FedEx"){

                //   holder.status.setTextColor(Color.parseColor("#007CBB"))

                holder.pen_image.setImageResource(R.drawable.fedexlogo)

            }

            else if (itemsViewModel.carriername == "UPS"){

                //   holder.status.setTextColor(Color.parseColor("#007CBB"))

                holder.pen_image.setImageResource(R.drawable.ups)

            }




            else{

                //    holder.status.setTextColor(Color.parseColor("#007CBB"))

                holder.pen_image.setImageResource(R.drawable.box)

            }







            holder.trackinglayout.setOnClickListener {

                val intent = Intent(context, TrackReportActivty::class.java)
                 intent.putExtra("trackingno", itemsViewModel.trackingNumber)

                 startActivity(intent)
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