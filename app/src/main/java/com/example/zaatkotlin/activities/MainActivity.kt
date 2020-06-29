package com.example.zaatkotlin.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.get
import androidx.fragment.app.Fragment
import com.example.zaatkotlin.R
import com.example.zaatkotlin.fragments.*
import com.example.zaatkotlin.viewmodels.FragmentsViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
    private val viewModel: FragmentsViewModel by viewModels()
    lateinit var bottomNavViewBar: BottomNavigationView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        bottomNavViewBar = findViewById(R.id.bottomNavView_bar)
        checkUser()
        initFrameLayout()
        setupBottomNavigationView()
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

        bottomNavViewBar.setOnNavigationItemSelectedListener { item ->
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
                R.id.chatMenu -> {
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
            bottomNavViewBar.menu[0].isChecked = true
        if (currentFragment is SearchFragment)
            bottomNavViewBar.menu[1].isChecked = true
        if (currentFragment is ChatFragment)
            bottomNavViewBar.menu[2].isChecked = true
        if (currentFragment is NotificationFragment)
            bottomNavViewBar.menu[3].isChecked = true
        if (currentFragment is SettingFragment)
            bottomNavViewBar.menu[4].isChecked = true

    }
}