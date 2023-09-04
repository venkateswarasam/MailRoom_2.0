package com.xcarriermaterialdesign

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import com.xcarriermaterialdesign.databinding.ActivityTabNavigationsBinding
import com.xcarriermaterialdesign.ui.main.SectionsPagerAdapter

class TabNavigations : AppCompatActivity() {

    private lateinit var binding: ActivityTabNavigationsBinding

    var viewPagerAdapter: ViewPagerAdapter? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityTabNavigationsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val viewPager: ViewPager = binding.viewPager



        viewPagerAdapter = ViewPagerAdapter(
            supportFragmentManager
        );
        viewPager.adapter = viewPagerAdapter;


        val tabs: TabLayout = binding.tabs
        tabs.setupWithViewPager(viewPager)





      /*  val sectionsPagerAdapter = SectionsPagerAdapter(this, supportFragmentManager)
        viewPager.adapter = sectionsPagerAdapter*/



        val fab: FloatingActionButton = binding.fab

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }
    }
}