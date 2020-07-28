package com.example.zaatkotlin.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.example.zaatkotlin.R
import com.example.zaatkotlin.databinding.ActivityMainBinding
import com.example.zaatkotlin.databinding.LayoutBottomNavigationViewBinding
import com.example.zaatkotlin.fragments.*
import com.example.zaatkotlin.viewmodels.FragmentsViewModel
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
    private val viewModel: FragmentsViewModel by viewModels()
    private lateinit var binding: ActivityMainBinding
    private lateinit var navigationViewBinding: LayoutBottomNavigationViewBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        navigationViewBinding = LayoutBottomNavigationViewBinding.bind(binding.root)
        setContentView(binding.root)
        checkUser()
        getNotificationsCount()
        initFrameLayout()
        setupBottomNavigationView()
    }

    private fun getNotificationsCount() {
        viewModel.getNotificationsCount().observe(this, Observer { querySnapShot ->
            viewModel.notificationsCount = querySnapShot.count()
            if (viewModel.notificationsCount != 0) {
                val badge =
                    navigationViewBinding.bottomNavViewBar.getOrCreateBadge(R.id.notificationMenu)
                badge.isVisible = true
                badge.badgeTextColor = ContextCompat.getColor(this, android.R.color.holo_red_dark)
                badge.backgroundColor = ContextCompat.getColor(this, android.R.color.white)
                badge.maxCharacterCount = 99
                badge.number = viewModel.notificationsCount
            } else {
                val badge =
                    navigationViewBinding.bottomNavViewBar.getOrCreateBadge(R.id.notificationMenu)
                badge.isVisible = false
                badge.clearNumber()
            }
        })
    }

    private fun checkUser() {
        val auth = FirebaseAuth.getInstance().currentUser
        if (auth == null) {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun initFrameLayout() {
        val fragment = supportFragmentManager.findFragmentByTag(viewModel.fragmentID)
        fragment?.let {
            supportFragmentManager.beginTransaction()
                .replace(R.id.frameLayoutContainer, fragment, viewModel.fragmentID)
                .addToBackStack(viewModel.fragmentID)
                .commit() //Start exist fragment
        } ?: run {
            supportFragmentManager.beginTransaction()
                .add(R.id.frameLayoutContainer, HomeFragment(), "home").addToBackStack("home")
                .commit() //Start new fragment
        }
    }

    private fun setupBottomNavigationView() {

        navigationViewBinding.bottomNavViewBar.setOnNavigationItemSelectedListener { item ->
            var index = "home"
            lateinit var newFragment: Fragment
            when (item.itemId) {
                R.id.homeMenu -> {
                    index = "home"
                    newFragment = HomeFragment()
                }
                R.id.searchMenu -> {
                    index = "search"
                    newFragment = SearchFragment()
                }
                R.id.worldMenu -> {
                    index = "chat"
                    newFragment = ChatFragment()
                }
                R.id.settingMenu -> {
                    index = "setting"
                    newFragment = SettingFragment()
                }
                R.id.notificationMenu -> {
                    index = "notification"
                    newFragment = NotificationFragment()
                }
            }
            viewModel.fragmentID = index
            val fragment = supportFragmentManager.findFragmentByTag(index)
            fragment?.let {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.frameLayoutContainer, fragment, index)
                    .commit() //Start exist fragment
            } ?: run {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.frameLayoutContainer, newFragment, index).addToBackStack(index)
                    .commit() //Start new fragment
            }

            true
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val currentFragment = supportFragmentManager.findFragmentById(R.id.frameLayoutContainer)
        if (supportFragmentManager.backStackEntryCount == 0)
            finish()
        if (currentFragment is HomeFragment)
            navigationViewBinding.bottomNavViewBar.menu[0].isChecked = true
        if (currentFragment is SearchFragment)
            navigationViewBinding.bottomNavViewBar.menu[1].isChecked = true
        if (currentFragment is ChatFragment)
            navigationViewBinding.bottomNavViewBar.menu[2].isChecked = true
        if (currentFragment is NotificationFragment)
            navigationViewBinding.bottomNavViewBar.menu[3].isChecked = true
        if (currentFragment is SettingFragment)
            navigationViewBinding.bottomNavViewBar.menu[4].isChecked = true
    }
}