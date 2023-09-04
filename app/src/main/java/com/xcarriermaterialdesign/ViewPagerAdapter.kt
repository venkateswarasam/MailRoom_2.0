package com.xcarriermaterialdesign

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.xcarriermaterialdesign.ui.dashboard.DashboardFragment
import com.xcarriermaterialdesign.ui.home.HomeFragment


class ViewPagerAdapter(
    fm: FragmentManager
) : FragmentPagerAdapter(fm) {
    override fun getItem(position: Int): Fragment {
        var fragment: Fragment? = null
        if (position == 0) fragment = HomeFragment() else if (position == 1) fragment =
            DashboardFragment() else if (position == 2) fragment = CheckFragment()
        return fragment!!
    }

    override fun getCount(): Int {
        return 3
    }

    override fun getPageTitle(position: Int): CharSequence? {
        var title: String? = null
        if (position == 0) title = "Home" else if (position == 1) title =
            "Search" else if (position == 2) title = "CheckIn"
        return title
    }
}

