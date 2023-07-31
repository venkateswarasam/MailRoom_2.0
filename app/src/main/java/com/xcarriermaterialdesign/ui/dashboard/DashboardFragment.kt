package com.xcarriermaterialdesign.ui.dashboard

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.*
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.xcarriermaterialdesign.R
import com.xcarriermaterialdesign.activities.settings.SettingsActivity
import com.xcarriermaterialdesign.databinding.FragmentDashboardBinding
import com.xcarriermaterialdesign.model.LstResponse
import com.xcarriermaterialdesign.model.PendingRequest
import com.xcarriermaterialdesign.model.PendingResponse
import com.xcarriermaterialdesign.pending.PendingDeliveriesActvity
import com.xcarriermaterialdesign.activities.manual.ManualProcessPackageActivity
import com.xcarriermaterialdesign.roomdatabase.*
import com.xcarriermaterialdesign.activities.scanner.SimpleScannerActivity
import com.xcarriermaterialdesign.trackreport.TrackReportActivty
import com.xcarriermaterialdesign.utils.ApplicationSharedPref
import com.xcarriermaterialdesign.utils.LoadingView
import com.xcarriermaterialdesign.utils.NetWorkService
import com.xcarriermaterialdesign.utils.NetworkChangeReceiver
import com.xcarriermaterialdesign.utils.NetworkConnection
import com.xcarriermaterialdesign.utils.ServiceDialog
import java.text.SimpleDateFormat
import java.util.*

class DashboardFragment : Fragment(), NetworkChangeReceiver.NetCheckerReceiverListener {

    private var _binding: FragmentDashboardBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    var buttonClicked = 0

    private lateinit var processDao: ProcessDao
    private lateinit var bulkDao: BulkDao

    private var adapter: ProcessAdapter_new? = null

    lateinit var rowlist:List<BulkPackage>

    var lastDays = ""

    val model: DashboardViewModel by viewModels()


    private val ctegroieslist: MutableList<LstResponse> = mutableListOf()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val dashboardViewModel =
            ViewModelProvider(this).get(DashboardViewModel::class.java)

        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root

        model.config(activity as AppCompatActivity)

        (activity as AppCompatActivity).supportActionBar?.hide()

    /*    val textView: TextView = binding.textDashboard
        dashboardViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }*/


        (activity as AppCompatActivity).startService(Intent( (activity as AppCompatActivity), NetWorkService::class.java))



        if (!NetworkConnection().isNetworkAvailable((activity as AppCompatActivity))) {

            binding.profile!!.setImageResource(R.drawable.syncyellow)

        }
        else{

            binding.profile!!.setImageResource(R.drawable.syncnew)

        }


        val db = Room.databaseBuilder(
            (activity as AppCompatActivity),
            ProcessDatabase::class.java, "Process_database"
        ).allowMainThreadQueries().build()

        processDao = db.processDao()
        bulkDao = db.bulkDao()

     /*   if (bulkDao.getAllBulkPackages().isEmpty()){


            binding.pendingimage.visibility = View.VISIBLE
            binding.pendingtext.visibility = View.VISIBLE
            binding.pendinglist.visibility = View.GONE
        }


        if (bulkDao.getAllBulkPackages().size > 50){

            binding.loadmore.visibility = View.VISIBLE
        }*/


        binding.loadmore.setOnClickListener {


           /* bulkDao.getAllBulkPackages()

            adapter =
                ProcessAdapter_new((activity as AppCompatActivity), bulkDao.getAllBulkPackages())

            val manager = LinearLayoutManager((activity as AppCompatActivity))
            binding.pendinglist!!.setHasFixedSize(true)

            binding.pendinglist!!.layoutManager = manager
            binding.pendinglist!!.adapter = adapter*/

        }

       // AnalyticsApplication.instance?.setPlantId("")

    /*    if (AnalyticsApplication.instance?.getPlantId()?.isNotEmpty()!!){




            rowlist = bulkDao.isData(AnalyticsApplication.instance!!.getPlantId())

            println("==rowlist==$rowlist")


            adapter =
                ProcessAdapter_new((activity as AppCompatActivity), rowlist)

            val manager = LinearLayoutManager((activity as AppCompatActivity))
            binding.pendinglist!!.setHasFixedSize(true)

            binding.pendinglist!!.layoutManager = manager
            binding.pendinglist!!.adapter = adapter




        }

        else{


            adapter =
                ProcessAdapter_new((activity as AppCompatActivity), bulkDao.getAllBulkPackages())

            val manager = LinearLayoutManager((activity as AppCompatActivity))
            binding.pendinglist!!.setHasFixedSize(true)

            binding.pendinglist!!.layoutManager = manager
            binding.pendinglist!!.adapter = adapter

        }
*/









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


            val intent = Intent(context, PendingDeliveriesActvity::class.java)
            startActivity(intent)
        }

        binding.manualEntry.setOnClickListener {

            val intent = Intent((activity as AppCompatActivity), ManualProcessPackageActivity::class.java)
            startActivity(intent)
        }

        binding.scannerEntry.setOnClickListener {

            val intent = Intent((activity as AppCompatActivity), SimpleScannerActivity::class.java)
            startActivity(intent)
        }


        binding.manualfab.setOnClickListener {

            val intent = Intent((activity as AppCompatActivity), ManualProcessPackageActivity::class.java)
            startActivity(intent)
        }

        binding.scannerfab.setOnClickListener {

            val intent = Intent((activity as AppCompatActivity), SimpleScannerActivity::class.java)
            startActivity(intent)
        }



        binding.last60.setOnClickListener {

            binding.last60.setBackgroundResource(R.drawable.text_bg_new)
            binding.last90.setBackgroundResource(R.drawable.text_bg)
            binding.last30.setBackgroundResource(R.drawable.text_bg)
        }


        binding.last30.setOnClickListener {


            binding.last30.setBackgroundResource(R.drawable.text_bg_new)
            binding.last90.setBackgroundResource(R.drawable.text_bg)
            binding.last60.setBackgroundResource(R.drawable.text_bg)
        }


        binding.last90.setOnClickListener {


            binding.last30.setBackgroundResource(R.drawable.text_bg)
            binding.last90.setBackgroundResource(R.drawable.text_bg_new)
            binding.last60.setBackgroundResource(R.drawable.text_bg)
        }


        lastDays = model.getBeforeDate(true,true,true,6)


        val pendingRequest = PendingRequest("",
            ApplicationSharedPref.read(ApplicationSharedPref.COMPANY_ID,"")!!,
            lastDays,"",
            ApplicationSharedPref.read(ApplicationSharedPref.LOGINID,"")!!.toInt(),
            ApplicationSharedPref.read(ApplicationSharedPref.PLANT_ID,"")!!,"","","", SimpleDateFormat("yyyy-MM-dd").format(Date()),1)

        model.loadingpendingdeliveries(pendingRequest)

        // response

        model.pendingResponse.observe(this, Observer<PendingResponse> { item ->

            LoadingView.hideLoading()

            println("==list==${item}")





            if (item.StatusCode == 200){

                println("==list==${item.Result.lstResponse}")

//                Toast.makeText(activity as AppCompatActivity, item.Result.TotalCount, Toast.LENGTH_SHORT).show()


                if (item.Result.lstResponse.isNotEmpty()){


                    adapter = ProcessAdapter_new(activity as AppCompatActivity, item.Result.lstResponse)



                    val manager = LinearLayoutManager((activity as AppCompatActivity))
                    binding.pendinglist!!.setHasFixedSize(true)

                    binding.pendinglist!!.layoutManager = manager
                    binding.pendinglist!!.adapter = adapter


                }


                else{

                    binding.pendingimage.visibility = View.VISIBLE
                    binding.pendingtext.visibility = View.VISIBLE
                    binding.pendinglist.visibility = View.GONE
                }












            }

            else{

                ServiceDialog.ShowDialog(activity as AppCompatActivity, item.Result.ReturnMsg)
            }











        });















        return root
    }



    inner class ProcessAdapter_new(val context : Context, private val mList: List<LstResponse>) : RecyclerView.Adapter<ProcessAdapter_new.ViewHolder>() {



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


            holder.trackingNo.text = itemsViewModel.InternalTrackNo ?: ""
            holder.status.text = itemsViewModel.CurrentStatus ?: ""

          //  val df: DateFormat = SimpleDateFormat("EEE MMM d yyyy • HH:mm aa")

          /*  val enDate = SimpleDateFormat("EEE MMM d yyyy • HH:mm aa", Locale("en"))

            val date: String = enDate.format(Calendar.getInstance().time)*/

            holder.dateandtime.text = itemsViewModel.UpdatedOn




            if (itemsViewModel.CarrierDescription == "DHL"){

                holder.pen_image.setImageResource(R.drawable.dhll)
            }

            else if (itemsViewModel.CarrierDescription == "USPS"){

            //    holder.status.setTextColor(Color.parseColor("#007CBB"))

                holder.pen_image.setImageResource(R.drawable.usps)

            }


            else if (itemsViewModel.CarrierDescription == "FedEx"){

             //   holder.status.setTextColor(Color.parseColor("#007CBB"))

                holder.pen_image.setImageResource(R.drawable.fedex)

            }

            else if (itemsViewModel.CarrierDescription == "UPS"){

                //   holder.status.setTextColor(Color.parseColor("#007CBB"))

                holder.pen_image.setImageResource(R.drawable.ups)

            }




            else{

            //    holder.status.setTextColor(Color.parseColor("#007CBB"))

                holder.pen_image.setImageResource(R.drawable.fedex)

            }





            holder.trackinglayout.setOnClickListener {

                val intent = Intent(context, TrackReportActivty::class.java)
                intent.putExtra("trackingno", itemsViewModel.InternalTrackNo)

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




    override fun onResume() {
        super.onResume()
        NetworkChangeReceiver.netConnectionCheckerReceiver = this
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initactionbar(){

        (activity as AppCompatActivity).supportActionBar?.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
        (activity as AppCompatActivity).supportActionBar?.setDisplayShowCustomEnabled(true)
        (activity as AppCompatActivity).supportActionBar?.setCustomView(R.layout.actionbarnew)
        val view1: View =  (activity as AppCompatActivity).supportActionBar!!.customView
        val toolbar: Toolbar = view1.parent as Toolbar
        toolbar.setContentInsetsAbsolute(0, 0)
      //  toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24)
        val headertext:TextView = view1.findViewById(R.id.headertext)
        headertext.text = getString(R.string.searchpack)
        val profile: ImageView = view1.findViewById(R.id.profile)
     //   val back: ImageView = view1.findViewById(R.id.back)

       // back.visibility = View.GONE
        //  profile.setImageResource(R.drawable.profilewhite)
        profile.visibility = View.GONE


        profile.setOnClickListener {

            val intent = Intent(activity, SettingsActivity::class.java)

            startActivity(intent)

        }
    }


    private fun showMessage(isConnected: Boolean) {
        if (!isConnected) {


            binding.profile.setImageResource(R.drawable.syncnew)

        } else {


            binding.profile.setImageResource(R.drawable.syncyellow)





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


}