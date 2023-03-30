package com.xcarriermaterialdesign.adapter

import android.R
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.xcarriermaterialdesign.utils.CourseModal


class CourseAdapter(// creating a variable for array list and context.
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