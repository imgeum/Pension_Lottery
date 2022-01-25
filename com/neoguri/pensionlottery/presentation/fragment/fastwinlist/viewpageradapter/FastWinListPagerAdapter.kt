package com.neoguri.pensionlottery.presentation.fragment.fastwinlist.viewpageradapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.neoguri.pensionlottery.presentation.fragment.fastwinlist.viewpageradapter.pagerfragment.FastWinBonusFragment
import com.neoguri.pensionlottery.presentation.fragment.fastwinlist.viewpageradapter.pagerfragment.FastWinFirstFragment

class FastWinListPagerAdapter (fragmentActivity: FragmentActivity, private var numPages: Int) : FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int {
        return numPages
    }

    override fun createFragment(position: Int): Fragment {
        return if(position == 0) {
            FastWinFirstFragment()
        } else {
            FastWinBonusFragment()
        }
    }

}