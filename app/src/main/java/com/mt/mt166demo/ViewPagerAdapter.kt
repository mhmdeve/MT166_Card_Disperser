package com.mt.mt166demo

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class ViewPagerAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {
    override fun createFragment(position: Int): Fragment {
        return when (position) {
            1 -> TypeABFragment()
            2 -> Mf1Fragment()
            3 -> ULFragment()
            else -> BaseFragment()
        }
    }

    override fun getItemCount(): Int {
        return 4 // number of tabs
    }
}
