package com.example.zaatkotlin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import com.example.zaatkotlin.fragments.ChatFragment
import com.example.zaatkotlin.fragments.HomeFragment
import com.example.zaatkotlin.fragments.NotificationFragment
import com.example.zaatkotlin.fragments.SettingFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
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
        supportFragmentManager.beginTransaction()
            .add(R.id.frameLayoutContainer, HomeFragment(), "home").commit() //Start fragment
    }

    private fun setupBottomNavigationView() {
        val bottomNavViewBar: BottomNavigationView = findViewById(R.id.bottomNavView_bar)
        bottomNavViewBar.setOnNavigationItemSelectedListener { item ->
            var fragment: Fragment? = null //Nullable
            var index = "home"
            when (item.itemId) {
                R.id.homeMenu -> {
                    fragment = HomeFragment()
                    index = "home"
                }
                R.id.chatMenu -> {
                    fragment = ChatFragment()
                    index = "chat"
                }
                R.id.settingMenu -> {
                    fragment = SettingFragment()
                    index = "setting"
                }
                R.id.notificationMenu -> {
                    fragment = NotificationFragment()
                    index = "notification"
                }
            }
            fragment?.let {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.frameLayoutContainer, fragment, index).commit() //Start fragment
            }
            true
        }
    }
}