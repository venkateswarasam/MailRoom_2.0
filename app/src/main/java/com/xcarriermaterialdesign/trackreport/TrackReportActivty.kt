package com.xcarriermaterialdesign.trackreport

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.xcarriermaterialdesign.BottomNavigationActivity
import com.xcarriermaterialdesign.R
import com.xcarriermaterialdesign.databinding.ActivityTrackReportActivtyBinding
import com.xcarriermaterialdesign.model.LstHistory
import com.xcarriermaterialdesign.model.TrackReportResponse
import com.xcarriermaterialdesign.ui.dashboard.DashboardFragment
import com.xcarriermaterialdesign.utils.LoadingView
import com.xcarriermaterialdesign.utils.NetworkChangeReceiver
import com.xcarriermaterialdesign.utils.NetworkConnection
import com.xcarriermaterialdesign.utils.ServiceDialog
import org.jetbrains.anko.toast

class TrackReportActivty : AppCompatActivity(), NetworkChangeReceiver.NetCheckerReceiverListener {

    private lateinit var binding: ActivityTrackReportActivtyBinding

    val model: TrackReportViewModel by viewModels()

//    lateinit var searchnameslist: ArrayList<String>


    var adapter:ProcessAdapter_new?=null


    var retrun = false


    override fun onBackPressed() {

        val intent = Intent(this, BottomNavigationActivity::class.java)

        intent.putExtra("navcheck","true")

        startActivity(intent)

        finish()
    }




    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar?.hide()

        model.config(this)


        binding = DataBindingUtil.setContentView(this, R.layout.activity_track_report_activty)

       // setContentView(R.layout.activity_track_report_activty)

        val trackingno = intent.getStringExtra("trackingno")


        trackreportapicall(trackingno!!)

        binding.toolbar.setNavigationOnClickListener {



            val intent = Intent(this, BottomNavigationActivity::class.java)

            intent.putExtra("navcheck","true")

            startActivity(intent)

            finish()
        }

        if (!NetworkConnection().isNetworkAvailable(this)) {

            binding.profile.setImageResource(R.drawable.syncyellow)

        }
        else{

            binding.profile.setImageResource(R.drawable.syncnew)

        }





        binding.headertext.text = "Tracking #$trackingno"
        binding.trackText.text = "#$trackingno"






        binding.actionupandown.setOnClickListener {

            binding.actionupandown1.visibility = View.VISIBLE
            binding.indetailsLay.visibility = View.VISIBLE
            binding.actionupandown.visibility = View.GONE
        }

        binding.actionupandown1.setOnClickListener {

            binding.actionupandown1.visibility = View.GONE
            binding.indetailsLay.visibility = View.GONE
            binding.actionupandown.visibility = View.VISIBLE

        }

        binding.copypaste.setOnClickListener {

            val clipboard: ClipboardManager =
                getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
            val clip: ClipData = ClipData.newPlainText("text", trackingno)
            clipboard.setPrimaryClip(clip)

            toast("Copied")

          //  Toast.makeText(this, "Copied", Toast.LENGTH_SHORT).show()
        }


       // binding.signImage.setImageBitmap(AnalyticsApplication.instance?.getBitmapSign())





    }







    // Trackreport

    fun trackreportapicall(trackno:String){



        model.trackReportResponse.observe(this, Observer<TrackReportResponse> { item ->

            LoadingView.hideLoading()

            println("==statuscode${item.StatusCode}")

            if (item.StatusCode == 200) {

                binding.tracklayout.visibility = View.VISIBLE


                for (i in 0 until item.Result.lstHistory.size){


                    binding.statusdate.text = item.Result.lstHistory[0].StatusDate


                }

                binding.carriertest.text = "Carrier #:"+item.Result.packageInformation.CarrierTrackingNo

                binding.shipemail.text = item.Result.packageAddressInfo.Email
                binding.shipfrom.text = item.Result.packageAddressInfo.ShipFromName

                val address = item.Result.packageAddressInfo.ShipToName+"\n "+item.Result.packageAddressInfo.Address1+" "+item.Result.packageAddressInfo.Address2+"\n "+item.Result.packageAddressInfo.City+" "+item.Result.packageAddressInfo.State+" "+item.Result.packageAddressInfo.Country+"" +
                        " "+item.Result.packageAddressInfo.ZipCode


                if (item.Result.packageAddressInfo.Address1 == ""){

                    binding.shipto.text = ""

                }

                else{

                    binding.shipto.text = address

                }



                println("==add==$address")








                binding.carriername.text = item.Result.packageInformation.Carrier
                binding.packagetype.text = item.Result.packageInformation.PackageType
                binding.packageweight.text = item.Result.packageInformation.Weight
                binding.carriertrackingnumber.text = item.Result.packageInformation.CarrierTrackingNo


                binding.statuslist.setHasFixedSize(true)
                binding.statuslist.layoutManager =
                    LinearLayoutManager(this@TrackReportActivty)


               // adapterfunction(item.Result.lstHistory as ArrayList<LstHistory>)

               adapter = ProcessAdapter_new(this, item.Result.lstHistory as ArrayList<LstHistory>)

                binding.statuslist.adapter = adapter




                return@Observer

            }

            else{



                ServiceDialog.ShowDialog(this, item.Result.ReturnMsg)





                return@Observer
            }






        })


        model.trackreport(trackno)


    }

    private fun adapterfunction(lstHistory: ArrayList<LstHistory>){

        adapter = ProcessAdapter_new(this, lstHistory)

        binding.statuslist.adapter = adapter
    }


    inner class ProcessAdapter_new(val context : Context, private val mList: ArrayList<LstHistory>) : RecyclerView.Adapter<ProcessAdapter_new.ViewHolder>()

    {



        // create new views
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProcessAdapter_new.ViewHolder {
            // inflates the card_view_design view
            // that is used to hold list item
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.statuslist_layout, parent, false)

            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {


            val itemsViewModel = mList[position]

            holder.statusTv.text = itemsViewModel.StatusDescription
            holder.latestatus.text = itemsViewModel.StatusDescription
            holder.date_tv.text = itemsViewModel.StatusDate
            holder.latest_date.text = itemsViewModel.StatusDate


            holder.binvalue.text = itemsViewModel.Bin
            holder.locationvalue.text = itemsViewModel.StorageLocation
            holder.signedby.text = itemsViewModel.SignByName
            holder.docvalue.text = itemsViewModel.DockNo






            when(position){

                0->{


                    holder.latestlayout.visibility = View.VISIBLE

                }

               4->{

                    holder.seemorelayout.visibility = View.VISIBLE
                }



                else->{

                    holder.defaultlayout.visibility = View.VISIBLE
                }
            }




            holder.actionupandown.setOnClickListener {

                holder.actionupandown1.visibility = View.VISIBLE
                holder.indetails_lay.visibility = View.VISIBLE
                holder.actionupandown.visibility = View.GONE
            }

            holder.actionupandown1.setOnClickListener {

                holder.actionupandown1.visibility = View.GONE
                holder.indetails_lay.visibility = View.GONE
                holder.actionupandown.visibility = View.VISIBLE

            }


            holder.seemorelayout.setOnClickListener {


               // Toast.makeText(this@TrackReportActivty, "click", Toast.LENGTH_SHORT).show()

                holder.seemorelayout.visibility = View.GONE

               // adapterfunction(mList)


                retrun = true

              //  adapterfunction(lstHistory = mList)

               // mList.size
            }









        }




        // binds the list items to a viewh


        // return the number of the items in the list
        override fun getItemCount(): Int {


            if (mList.size>5){


                if(!retrun){


                    return 5

                }
                else{

                    return mList.size
                }


            }

            else{


                return mList.size
            }

           /* if (!retrun){
                return 2

            }
            else{

                return mList.size
            }*/
        }

        // Holds the views for adding it to image and text
        inner class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {

            val statusTv: TextView = itemView.findViewById(R.id.statusTv)
            val date_tv: TextView = itemView.findViewById(R.id.date_tv)
            val latestatus: TextView = itemView.findViewById(R.id.latestatus)
            val latest_date: TextView = itemView.findViewById(R.id.latest_date)

            val binvalue: TextView = itemView.findViewById(R.id.binvalue)
            val locationvalue: TextView = itemView.findViewById(R.id.locationvalue)
            val docvalue: TextView = itemView.findViewById(R.id.docvalue)
            val signedby: TextView = itemView.findViewById(R.id.signedby)



            val actionupandown: ImageView = itemView.findViewById(R.id.actionupandown)
            val actionupandown1: ImageView = itemView.findViewById(R.id.actionupandown1)

            val defaultlayout:RelativeLayout = itemView.findViewById(R.id.defaultlayout)
            val seemorelayout:RelativeLayout = itemView.findViewById(R.id.seemorelayout)
            val latestlayout:LinearLayout = itemView.findViewById(R.id.latestlayout)
            val indetails_lay:LinearLayout = itemView.findViewById(R.id.indetails_lay)







        }






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
    }

}