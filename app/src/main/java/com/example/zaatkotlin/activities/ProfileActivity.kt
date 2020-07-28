package com.example.zaatkotlin.activities

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.example.zaatkotlin.adapters.ProfileViewPagerAdapter
import com.example.zaatkotlin.databinding.ActivityProfileBinding
import com.example.zaatkotlin.databinding.LayoutTopProfileToolbarBinding
import com.example.zaatkotlin.models.User
import com.example.zaatkotlin.viewmodels.ProfileViewModel
import com.google.firebase.auth.FirebaseAuth
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.layout_profile_tabs.view.*
import kotlinx.android.synthetic.main.layout_top_profile.view.*


class ProfileActivity : AppCompatActivity() {
    private val viewModel: ProfileViewModel by viewModels()
    private lateinit var binding: ActivityProfileBinding
    private lateinit var toolbarBinding: LayoutTopProfileToolbarBinding
    private lateinit var profileViewPagerAdapter: ProfileViewPagerAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        toolbarBinding = LayoutTopProfileToolbarBinding.bind(binding.root)
        setContentView(binding.root)
        initWidget()
    }

    private fun initWidget() {
        getUser(FirebaseAuth.getInstance().uid!!)
        profileViewPagerAdapter = ProfileViewPagerAdapter(supportFragmentManager, 1, this)
        binding.bottomProfile.profileViewPager.adapter = profileViewPagerAdapter
        binding.bottomProfile.tab_layout.setupWithViewPager(binding.bottomProfile.profileViewPager)
        toolbarBinding.back.setOnClickListener { finish() }
    }

    private fun getUser(userID: String) {
        viewModel.getUserData(userID).observe(this, Observer { querySnapShot ->
            if (querySnapShot != null) {
                for (document in querySnapShot) {
                    val user = User(
                        email = document["email"] as String,
                        photoURL = document["photoURL"] as String,
                        username = document["username"] as String,
                        userId = document["userId"] as String
                    )
                    Picasso.get().load(user.photoURL).into(binding.topProfile.userIV)
                    binding.topProfile.usernameTV.text = user.username
                }
            }
        })
    }
}