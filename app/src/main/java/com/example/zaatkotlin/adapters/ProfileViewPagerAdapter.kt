package com.example.zaatkotlin.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.zaatkotlin.fragments.FollowersFragment
import com.example.zaatkotlin.fragments.FollowingFragment
import com.example.zaatkotlin.fragments.MemoriesFragment

class ProfileViewPagerAdapter(fa: FragmentActivity) :
    FragmentStateAdapter(fa) {
    override fun getItemCount(): Int {
        return 3
    }

    override fun createFragment(position: Int): Fragment {
        when (position) {
            0 -> {
                return MemoriesFragment()
            }
            1 -> {
                return FollowersFragment()
            }
            2 -> {
                return FollowingFragment()
            }
        }
        return MemoriesFragment()
    }
}