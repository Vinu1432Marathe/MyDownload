package com.video.download.vidlink.Adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.video.download.vidlink.Fragment.DownloadFragment
import com.video.download.vidlink.Fragment.HomeFragment

class ViewPagerAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {
    override fun getItemCount(): Int = 2

    // Number of tabs
    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> HomeFragment()
            1 -> DownloadFragment()
            else -> HomeFragment()
        }
    }
}