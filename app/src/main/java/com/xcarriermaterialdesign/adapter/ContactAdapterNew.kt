package com.xcarriermaterialdesign.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.xcarriermaterialdesign.R
import com.xcarriermaterialdesign.pending.ContactsAdapter
import com.xcarriermaterialdesign.roomdatabase.BulkPackage
import java.util.*

class ContactAdapterNew (
    private val context: Context,
    contactList: List<BulkPackage>,
    private val listener: ContactsAdapterBinListener
) :
    RecyclerView.Adapter<ContactAdapterNew.MyViewHolder>(), Filterable {
    private val contactList: List<BulkPackage>
    private var contactListFiltered: List<BulkPackage>

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var name: TextView


        init {
            name = view.findViewById<TextView>(R.id.text1)

            view.setOnClickListener { view1: View? ->
                // send selected contact in callback
                listener.onContactbinSelected(contactListFiltered[adapterPosition])
            }
        }
    }

    init {
        this.contactList = contactList
        contactListFiltered = contactList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactAdapterNew.MyViewHolder {
        val itemView: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_layout, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        val contact: BulkPackage = contactListFiltered[position]
        holder.name.setText(contact.binnumber)
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
                        if (row.binnumber.toLowerCase()
                                .contains(charString.lowercase(Locale.getDefault())) || row.binnumber
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

    interface ContactsAdapterBinListener {
        fun onContactbinSelected(contact: BulkPackage?)
    }
}