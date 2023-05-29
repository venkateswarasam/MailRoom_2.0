package com.xcarriermaterialdesign.trackreport

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.xcarriermaterialdesign.R
import com.xcarriermaterialdesign.databinding.ActivityTrackReportActivtyBinding
import com.xcarriermaterialdesign.utils.AnalyticsApplication
import com.xcarriermaterialdesign.utils.NetworkChangeReceiver
import com.xcarriermaterialdesign.utils.NetworkConnection

class TrackReportActivty : AppCompatActivity(), NetworkChangeReceiver.NetCheckerReceiverListener {

    private lateinit var binding: ActivityTrackReportActivtyBinding

    val model: TrackReportViewModel by viewModels()

    lateinit var searchnameslist: ArrayList<String>;

    lateinit var listAdapter: ArrayAdapter<String>

    var adapter:ProcessAdapter_new?=null




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar?.hide()

        model.config(this)


        binding = DataBindingUtil.setContentView(this, R.layout.activity_track_report_activty)

       // setContentView(R.layout.activity_track_report_activty)

        val trackingno = intent.getStringExtra("trackingno")

        binding.toolbar.setNavigationOnClickListener {

            finish()
        }

        if (!NetworkConnection().isNetworkAvailable(this)) {

            binding.profile!!.setImageResource(R.drawable.syncyellow)

        }
        else{

            binding.profile!!.setImageResource(R.drawable.syncnew)

        }





        binding.headertext.text = "Tracking #$trackingno"
        binding.trackText.text = "#$trackingno"

        searchnameslist = ArrayList()

        searchnameslist.add("On Rack")
        searchnameslist.add("Received")
        searchnameslist.add("See 3 more Updates")
       // searchnameslist.add("Routed")

      //  searchnameslist.add("Location Scan")

        binding.statuslist.setHasFixedSize(true);
        binding.statuslist.layoutManager =
            LinearLayoutManager(this@TrackReportActivty);

        adapter = ProcessAdapter_new(this, searchnameslist)

        binding.statuslist.adapter = adapter

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

            Toast.makeText(this, "Copied", Toast.LENGTH_SHORT).show()
        }


        binding.signImage.setImageBitmap(AnalyticsApplication.instance?.getBitmapSign())





    }


    inner class ProcessAdapter_new(val context : Context, private val mList: ArrayList<String>) : RecyclerView.Adapter<ProcessAdapter_new.ViewHolder>()

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

            if (itemsViewModel == "See 3 more Updates"){

              /*  holder.trackingNo.setTextColor(resources.getColor(R.color.purple_700))

              //  holder.trackingNo.textSize = 11.85F
                holder.trackingNo.textSize = 12F
*/
                holder.date_tv.visibility = View.GONE
                holder.trackingNo.visibility = View.GONE
                holder.seemore.visibility = View.VISIBLE







            }


            holder.seemore.setOnClickListener {


                if (itemsViewModel == "See 3 more Updates"){

                    searchnameslist.remove("See 3 more Updates")

                  searchnameslist.add("Routed")
                  searchnameslist.add("Location Scan")
                    searchnameslist.add("Returned")

                    adapter = ProcessAdapter_new(this@TrackReportActivty, searchnameslist)

                    binding.statuslist.adapter = adapter


                }
            }


            holder.trackingNo.text = itemsViewModel ?: ""





        }




        // binds the list items to a viewh


        // return the number of the items in the list
        override fun getItemCount(): Int {
            return mList.size
        }

        // Holds the views for adding it to image and text
        inner class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {

            val trackingNo: TextView = itemView.findViewById(R.id.statusTv)
            val date_tv: TextView = itemView.findViewById(R.id.date_tv)
            val seemore: TextView = itemView.findViewById(R.id.seemore)


        }






    }

    override fun onResume() {
        super.onResume()

        NetworkChangeReceiver.netConnectionCheckerReceiver = this
    }


    private fun showMessage(isConnected: Boolean) {

        if (!isConnected) {

            binding.profile!!.setImageResource(R.drawable.syncyellow)





        } else {


            binding.profile!!.setImageResource(R.drawable.syncnew)



            //  save!!.setImageDrawable(resources.getDrawable(R.drawable.savegreen))

        }


    }


    override fun onNetworkConnectionChanged(isConnected: Boolean) {

        showMessage(isConnected)
    }

}