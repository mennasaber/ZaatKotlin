package com.example.zaatkotlin.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.example.zaatkotlin.R
import com.example.zaatkotlin.adapters.ProfileViewPagerAdapter
import com.example.zaatkotlin.databinding.ActivityProfileBinding
import com.example.zaatkotlin.databinding.LayoutTopProfileToolbarBinding
import com.example.zaatkotlin.models.User
import com.example.zaatkotlin.viewmodels.ProfileViewModel
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.layout_profile_tabs.view.*
import kotlinx.android.synthetic.main.layout_top_profile.view.*


class ProfileActivity : AppCompatActivity() {
    private val viewModel: ProfileViewModel by viewModels()
    private lateinit var binding: ActivityProfileBinding
    private lateinit var toolbarBinding: LayoutTopProfileToolbarBinding
    private lateinit var profileViewPagerAdapter: ProfileViewPagerAdapter
    private lateinit var tabsTitle: ArrayList<String>
    private lateinit var user: User
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        toolbarBinding = LayoutTopProfileToolbarBinding.bind(binding.root)
        setContentView(binding.root)
        initWidget()
    }

    private fun initWidget() {
        tabsTitle = ArrayList()
        tabsTitle.add(resources.getString(R.string.memories))
        tabsTitle.add(resources.getString(R.string.followers))
        tabsTitle.add(resources.getString(R.string.following))
        getUser(FirebaseAuth.getInstance().uid!!)

        profileViewPagerAdapter = ProfileViewPagerAdapter(this)
        binding.bottomProfile.profileViewPager.adapter = profileViewPagerAdapter
        TabLayoutMediator(
            binding.bottomProfile.tab_layout,
            binding.bottomProfile.profileViewPager
        ) { tab, position ->
            tab.text = tabsTitle[position]
        }.attach()
        toolbarBinding.back.setOnClickListener { finish() }
        toolbarBinding.edit.setOnClickListener {
            goToEdit()
        }
    }

    private fun goToEdit() {
        val intent = Intent(this, EditActivity::class.java)
        intent.putExtra("userImage", user.photoURL)
        intent.putExtra("username", user.username)
        startActivity(intent)
    }

    private fun getUser(userID: String) {
        viewModel.getUserData(userID).observe(this, Observer { querySnapShot ->
            if (querySnapShot != null) {
                for (document in querySnapShot) {
                    user = User(
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