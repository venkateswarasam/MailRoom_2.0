package com.xcarriermaterialdesign.pending

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.xcarriermaterialdesign.R
import com.xcarriermaterialdesign.adapter.ContactAdapterNew
import com.xcarriermaterialdesign.databinding.ActivityPendingDeliveriesActvityBinding
import com.xcarriermaterialdesign.pendingnew.SearchPackagesActivity
import com.xcarriermaterialdesign.roomdatabase.*
import com.xcarriermaterialdesign.trackreport.TrackReportViewModel
import com.xcarriermaterialdesign.utils.AnalyticsApplication
import com.xcarriermaterialdesign.utils.NetworkChangeReceiver
import com.xcarriermaterialdesign.utils.NetworkConnection
import java.util.*


class PendingDeliveriesActvity : AppCompatActivity(), ContactsAdapter.ContactsAdapterListener,
ContactAdapterNew.ContactsAdapterBinListener, NetworkChangeReceiver.NetCheckerReceiverListener{


    private lateinit var binding: ActivityPendingDeliveriesActvityBinding

    val model: TrackReportViewModel by viewModels()


    private var adapter: ContactsAdapter? = null
    private var adapter_bin: ContactAdapterNew? = null


    private lateinit var processDao: ProcessDao
    private lateinit var bulkDao: BulkDao

    private lateinit var processPackage: List<ProcessPackage>
    private lateinit var bulkPackage: List<BulkPackage>


    val filterdNames: ArrayList<String> = ArrayList()

    lateinit var listAdapter: ArrayAdapter<String>

    lateinit var programmingLanguagesList: ArrayList<String>;
    lateinit var programmingLanguagesList_new: ArrayList<String>;

    lateinit var searchnameslist: ArrayList<String>;


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
      //  setContentView(R.layout.activity_pending_deliveries_actvity)
        supportActionBar?.hide()

    //    AnalyticsApplication.instance.s

        model.config(this)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_pending_deliveries_actvity)

        programmingLanguagesList = ArrayList()
        programmingLanguagesList_new = ArrayList()
        searchnameslist = ArrayList()


        val db = Room.databaseBuilder(
            applicationContext,
            ProcessDatabase::class.java, "Process_database"
        ).allowMainThreadQueries().build()

        processDao = db.processDao()
        bulkDao = db.bulkDao()

        processPackage = processDao.getAllProcessPackages()
        bulkPackage = bulkDao.getAllBulkPackages()

        searchnameslist.add("TrackingNo.")
        searchnameslist.add("Bin No.")


        if (!NetworkConnection().isNetworkAvailable(this)) {

            binding.profile!!.setImageResource(R.drawable.syncyellow)

        }
        else{

            binding.profile!!.setImageResource(R.drawable.syncnew)

        }

        binding.toolbar.setNavigationOnClickListener {

            finish()
        }

        binding.searchImg.setOnClickListener {

            binding.searchview.setQuery("", false);

            binding.searchview.clearFocus();

        }








       binding.searchlist.setHasFixedSize(true);
        binding.searchlist.layoutManager =
         LinearLayoutManager(this@PendingDeliveriesActvity);
        binding.binlistNew.setHasFixedSize(true);
        binding.binlistNew.layoutManager =
            LinearLayoutManager(this@PendingDeliveriesActvity);



        adapter = ContactsAdapter(this,bulkPackage,this@PendingDeliveriesActvity)

        binding.searchlist.adapter = adapter


        adapter_bin = ContactAdapterNew(this, bulkPackage, this)

        binding.binlistNew.adapter = adapter_bin



        binding.searchview.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            override fun onQueryTextSubmit(query: String?): Boolean {
                // on below line we are checking
                // if query exist or not.

                adapter?.filter?.filter(query)

                adapter_bin?.filter?.filter(query)


                /*  if (query != null) {
                      checking(newText = query)
                  }*/


                // filter recycler view when query submitted
             //   adapter.getFilter().filter(query)

              /* if (programmingLanguagesList.contains(query)) {
                    // if query exist within list we
                    // are filtering our list adapter.

                    binding.trackingList.visibility = View.VISIBLE

                 *//*  for (s in processPackage){


                       programmingLanguagesList.add(s.trackingNumber)
                   }*//*




               *//*   adapter = CustomAdapter(programmingLanguagesList)

                   binding.searchlist.adapter = adapter*//*

                   // filter recycler view when query submitted


                   //calling a method of the adapter class and passing the filtered list
                //   adapter?.filterList(programmingLanguagesList)





                    listAdapter = ArrayAdapter<String>(
                        this@PendingDeliveriesActvity,
                        android.R.layout.simple_list_item_1,
                        programmingLanguagesList
                    )
                    listAdapter.filter.filter(query)



                    binding.pendinglist.adapter = listAdapter


                } else {
                    // if query is not present we are displaying
                    // a toast message as no  data found..

                    //  binding.tarck.visibility = View.GONE

                    Toast.makeText(this@PendingDeliveriesActvity, "No data found..", Toast.LENGTH_LONG)
                        .show()
                }



               if (programmingLanguagesList_new.contains(query)) {
                    // if query exist within list we
                    // are filtering our list adapter.




                  // adapter_bin = CustomAdapter_bin(programmingLanguagesList_new)


                   //calling a method of the adapter class and passing the filtered list
                  // adapter_bin?.filterList(programmingLanguagesList_new)

                   //binding.binlistNew.adapter = adapter




                    listAdapter = ArrayAdapter<String>(
                        this@PendingDeliveriesActvity,
                        android.R.layout.simple_list_item_1,
                        programmingLanguagesList_new
                    )
                    listAdapter.filter.filter(query)



                    binding.pendinglistBin.adapter = listAdapter


                } else {
                    // if query is not present we are displaying
                    // a toast message as no  data found..


                    Toast.makeText(this@PendingDeliveriesActvity, "No data found..", Toast.LENGTH_LONG)
                        .show()
                }
*/








                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // if query text is change in that case we
                // are filtering our adapter with
                // new text on below line.


                when(newText){

                    ""->{

                        binding.searchImg.setImageResource(R.drawable.searchactive)

                        binding.searchMainLayout.setBackgroundResource(R.drawable.searchbg)
                        binding.trackingList.visibility = View.GONE
                        binding.binlist.visibility = View.GONE

                    }

                    else->{

                        binding.searchImg.setImageResource(R.drawable.close_new)


                        binding.searchMainLayout.setBackgroundResource(R.drawable.pendingbg)
                        binding.trackingList.visibility = View.VISIBLE
                        binding.binlist.visibility = View.VISIBLE


                    }
                }




                adapter?.filter?.filter(newText)

                adapter_bin?.filter?.filter(newText)









                /* if (newText != null) {
                     checking(newText = newText)
                 }*/


                // filter recycler view when query submitted
              //  adapter.getFilter()?.filter(newText)





              /*  for (s in processPackage){


                    programmingLanguagesList.add(s.trackingNumber)

                }*/




              /*  adapter = CustomAdapter(programmingLanguagesList)

                binding.searchlist.adapter = adapter*/



                //calling a method of the adapter class and passing the filtered list
              //  adapter?.filterList(programmingLanguagesList)






            /*    adapter_bin = CustomAdapter_bin(programmingLanguagesList_new)


                //calling a method of the adapter class and passing the filtered list
                adapter_bin?.filterList(programmingLanguagesList_new)

                binding.binlistNew.adapter = adapter_bin*/





                listAdapter = ArrayAdapter<String>(
                    this@PendingDeliveriesActvity,
                    android.R.layout.simple_list_item_1,
                    programmingLanguagesList
                )
                listAdapter.filter.filter(newText)



                binding.pendinglist.adapter = listAdapter


                listAdapter = ArrayAdapter<String>(
                    this@PendingDeliveriesActvity,
                    android.R.layout.simple_list_item_1,
                    programmingLanguagesList_new
                )
                listAdapter.filter.filter(newText)

                binding.pendinglistBin.adapter = listAdapter















                // track





                return false
            }
        })










/*
        binding.editTextSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence?, i: Int, i1: Int, i2: Int) {




            }
            override fun onTextChanged(charSequence: CharSequence?, i: Int, i1: Int, i2: Int) {



            }
            override fun afterTextChanged(editable: Editable) {
                //after the change calling the method and passing the search input


              */
/*  if (editable.isEmpty()){

                    binding.searchImg.setImageResource(R.drawable.searchactive)


               //     binding.pendinglist.visibility = View.GONE


                }

                binding.searchImg.setImageResource(R.drawable.close_new)
*//*



                filter(editable.toString())











            }
        })
*/
    }



    private fun checking(newText:String){

        println("==text==$newText")
        println("==tist==$programmingLanguagesList")
        println("==testlist==$programmingLanguagesList")
        println("==testlist1==${programmingLanguagesList.contains(newText)}")





        if (programmingLanguagesList.contains(newText)){

            println("==text1==$newText")


            binding.trackingList.visibility = View.VISIBLE

            listAdapter = ArrayAdapter<String>(
                this@PendingDeliveriesActvity,
                android.R.layout.simple_list_item_1,
                programmingLanguagesList
            )
            listAdapter.filter.filter(newText)



            binding.pendinglist.adapter = listAdapter
        }
        else{

            binding.trackingList.visibility = View.GONE
        }




        // bin

        if (programmingLanguagesList_new.contains(newText)){

            binding.binlist.visibility = View.VISIBLE

            listAdapter = ArrayAdapter<String>(
                this@PendingDeliveriesActvity,
                android.R.layout.simple_list_item_1,
                programmingLanguagesList_new
            )
            listAdapter.filter.filter(newText)

            binding.pendinglistBin.adapter = listAdapter

        }

        else{

            binding.binlist.visibility = View.GONE

        }

    }













    private fun filter(text: String) {
        //new array list that will hold the filtered data

        //looping through existing elements
        for (s in processPackage) {
            //if the existing elements contains the search input

            if (s.trackingNumber.toLowerCase().contains(text.toLowerCase())) {


                filterdNames.add(s.trackingNumber)

            }



        }

     //  adapter = CustomAdapter(names = filterdNames)


        //calling a method of the adapter class and passing the filtered list
      //  adapter?.filterList(filterdNames)

       // binding.pendinglist.adapter = adapter

    }


    private inner class CustomAdapter(private var names: List<BulkPackage>) :
        RecyclerView.Adapter<CustomAdapter.ViewHolder>(), Filterable {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val v: View = LayoutInflater.from(parent.context)
                .inflate(R.layout.list_layout, parent, false)
            return ViewHolder(v)
        }

        @SuppressLint("SuspiciousIndentation")
        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
          holder.textViewName.text = names[position].trackingNumber

            holder.list_layout.setOnClickListener {

                AnalyticsApplication.instance?.setPlantId(names[position].trackingNumber)

                println("==plantid==${AnalyticsApplication.instance?.getPlantId()}")

              //  finish()

                val intent = Intent(this@PendingDeliveriesActvity, SearchPackagesActivity::class.java)

              //  intent.putExtra("trackingnumber", names[position])

                startActivity(intent)
            }


        }

        override fun getItemCount(): Int {
            return names.size
        }



         inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            var textViewName: TextView
            var list_layout: LinearLayout

            init {
                textViewName = itemView.findViewById(R.id.text1) as TextView
                list_layout = itemView.findViewById(R.id.list_layout) as LinearLayout
            }
        }

     /* fun filterList(filterdNames: ArrayList<String>) {
            names = filterdNames
            notifyDataSetChanged()
        }*/

        override fun getFilter(): Filter {

            return object : Filter() {
                override fun performFiltering(charSequence: CharSequence): FilterResults {
                    val charString = charSequence.toString()
                    if (charString.isEmpty()) {
                        bulkPackage = bulkDao.getAllBulkPackages()
                    } else {
                        val filteredList: MutableList<BulkPackage> = ArrayList<BulkPackage>()
                        for (row in bulkPackage) {
                            if (row.trackingNumber.toLowerCase()
                                    .contains(charString.lowercase(Locale.getDefault())) || row.trackingNumber
                                    .contains(charSequence)
                            ) {
                                filteredList.add(row)
                            }
                        }
                        bulkPackage = filteredList
                    }
                    val filterResults = FilterResults()
                    filterResults.values = bulkPackage
                    return filterResults
                }

                override fun publishResults(
                    charSequence: CharSequence,
                    filterResults: FilterResults
                ) {
                    bulkPackage = filterResults.values as List<BulkPackage>
                    notifyDataSetChanged()
                }
            }

        }


/*
        override fun getFilter(): Filter? {
            return object : Filter() {
                override fun performFiltering(charSequence: CharSequence): FilterResults {
                    val charString = charSequence.toString()
                    if (charString.isEmpty()) {
                        contactListFiltered = contactList
                    } else {
                        val filteredList: MutableList<BulkPackage> = java.util.ArrayList<BulkPackage>()
                        for (row in BulkPackage) {
                            if (row.getName().toLowerCase()
                                    .contains(charString.lowercase(Locale.getDefault())) || row.getPhone()
                                    .contains(charSequence)
                            ) {
                                filteredList.add(row)
                            }
                        }
                        contactListFiltered = filteredList
                    }
                    val filterResults = FilterResults()
                    filterResults.values = contactListFiltered
                    return filterResults
                }

                override fun publishResults(
                    charSequence: CharSequence,
                    filterResults: FilterResults
                ) {
                    contactListFiltered = filterResults.values as java.util.ArrayList<BulkPackage?>
                    notifyDataSetChanged()
                }
            }
        }
*/



    }




    private inner class CustomAdapter_bin(private var names: List<String>) :
        RecyclerView.Adapter<CustomAdapter_bin.ViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val v: View = LayoutInflater.from(parent.context)
                .inflate(R.layout.list_layout, parent, false)
            return ViewHolder(v)
        }

        override fun onBindViewHolder(holder: CustomAdapter_bin.ViewHolder, position: Int) {

            holder.textViewName.text = names[position]

            holder.list_layout.setOnClickListener {

                AnalyticsApplication.instance?.setCompanyId(names[position])

                println("==bin==${AnalyticsApplication.instance?.getCompanyId()}")

                //  finish()

                val intent = Intent(this@PendingDeliveriesActvity, SearchPackagesActivity::class.java)

                //  intent.putExtra("trackingnumber", names[position])

                startActivity(intent)
            }
        }



        override fun getItemCount(): Int {
            return names.size
        }



        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            var textViewName: TextView
            var list_layout: LinearLayout

            init {
                textViewName = itemView.findViewById(R.id.text1) as TextView
                list_layout = itemView.findViewById(R.id.list_layout) as LinearLayout
            }
        }

        fun filterList(filterdNames: ArrayList<String>) {
            names = filterdNames
            notifyDataSetChanged()
        }


/*
        override fun getFilter(): Filter? {
            return object : Filter() {
                override fun performFiltering(charSequence: CharSequence): FilterResults {
                    val charString = charSequence.toString()
                    if (charString.isEmpty()) {
                        contactListFiltered = contactList
                    } else {
                        val filteredList: MutableList<BulkPackage> = java.util.ArrayList<BulkPackage>()
                        for (row in BulkPackage) {
                            if (row.getName().toLowerCase()
                                    .contains(charString.lowercase(Locale.getDefault())) || row.getPhone()
                                    .contains(charSequence)
                            ) {
                                filteredList.add(row)
                            }
                        }
                        contactListFiltered = filteredList
                    }
                    val filterResults = FilterResults()
                    filterResults.values = contactListFiltered
                    return filterResults
                }

                override fun publishResults(
                    charSequence: CharSequence,
                    filterResults: FilterResults
                ) {
                    contactListFiltered = filterResults.values as java.util.ArrayList<BulkPackage?>
                    notifyDataSetChanged()
                }
            }
        }
*/



    }

    override fun onContactSelected(contact: BulkPackage?) {

        AnalyticsApplication.instance?.setPlantId(contact?.trackingNumber)

        AnalyticsApplication.instance?.setCompanyId("")

        val intent = Intent(this@PendingDeliveriesActvity, SearchPackagesActivity::class.java)

        //  intent.putExtra("trackingnumber", contact?.trackingNumber)

        startActivity(intent)


      /*  Toast.makeText(
            applicationContext,
            "Selected: " + contact?.trackingNumber + ", " + contact?.binnumber,
            Toast.LENGTH_LONG
        ).show()*/

    }

    override fun onContactbinSelected(contact: BulkPackage?) {

        AnalyticsApplication.instance?.setCompanyId(contact?.binnumber)
        AnalyticsApplication.instance?.setPlantId("")

        println("==bin==${AnalyticsApplication.instance?.getCompanyId()}")

        //  finish()

        val intent = Intent(this@PendingDeliveriesActvity, SearchPackagesActivity::class.java)

      //   intent.putExtra("trackingnumber", contact?.binnumber)

        startActivity(intent)
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


    // new change




}

class ContactsAdapter(
    private val context: Context,
    contactList: List<BulkPackage>,
    private val listener: ContactsAdapterListener
) :
    RecyclerView.Adapter<ContactsAdapter.MyViewHolder>(), Filterable {
    private val contactList: List<BulkPackage>
    private var contactListFiltered: List<BulkPackage>

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var name: TextView


        init {
            name = view.findViewById<TextView>(R.id.text1)

            view.setOnClickListener { view1: View? ->
                // send selected contact in callback
                listener.onContactSelected(contactListFiltered[adapterPosition])
            }
        }
    }

    init {
        this.contactList = contactList
        contactListFiltered = contactList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_layout, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val contact: BulkPackage = contactListFiltered[position]
        holder.name.setText(contact.trackingNumber)

    }

    override fun getItemCount(): Int {
        return contactListFiltered.size
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence): FilterResults {
                val charString = charSequence.toString()
                contactListFiltered = if (charString.isEmpty()) {
                    contactList
                } else {
                    val filteredList: MutableList<BulkPackage> = ArrayList<BulkPackage>()
                    for (row in contactList) {
                        if (row.trackingNumber.toLowerCase()
                                .contains(charString.lowercase(Locale.getDefault())) || row.trackingNumber
                                .contains(charSequence)
                        ) {
                            filteredList.add(row)
                        }
                    }
                    filteredList
                }
                val filterResults = FilterResults()
                filterResults.values = contactListFiltered
                return filterResults
            }

            override fun publishResults(
                charSequence: CharSequence,
                filterResults: FilterResults
            ) {
                contactListFiltered = filterResults.values as ArrayList<BulkPackage>
                notifyDataSetChanged()
            }
        }
    }

    interface ContactsAdapterListener {
        fun onContactSelected(contact: BulkPackage?)
    }
}
