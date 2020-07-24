package com.example.zaatkotlin.adapters

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.zaatkotlin.R
import com.example.zaatkotlin.fragments.FollowersFragment
import com.example.zaatkotlin.fragments.FollowingFragment
import com.example.zaatkotlin.fragments.MemoriesFragment

class ProfileViewPagerAdapter(fm: FragmentManager, behavior: Int, private val _context: Context) :
    FragmentPagerAdapter(fm, behavior) {
    override fun getItem(position: Int): Fragment {
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

    override fun getPageTitle(position: Int): CharSequence? {
        when (position) {
            0 -> {
                return _context.resources.getString(R.string.memories)
            }
            1 -> {
                return _context.resources.getString(R.string.followers)
            }
            2 -> {
                return _context.resources.getString(R.string.following)
            }
        }
        return _context.resources.getString(R.string.memories)
    }

    override fun getCount(): Int {
        return 3
    }
}