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


class ProfileActivity : AppCompatActivity(), View.OnClickListener {
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
        //initLists()
    }

    /* private fun initLists() {
         viewModel.getFollowing().observe(this, Observer { querySnapShot ->
             if (querySnapShot != null) {
                 for (document in querySnapShot) {
                     getUser(document["followingId"] as String, 1)
                 }
             }
         })
         viewModel.getFollowers().observe(this, Observer { querySnapShot ->
             if (querySnapShot != null) {
                 for (document in querySnapShot) {
                     getUser(document["followerId"] as String, 0)
                 }
             }

         })
     }*/

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
//                    when (type) {
//                        0 -> if (viewModel.followersList.find { it.userId == userID } == null) {
//                            viewModel.followersList.add(user)
//                        }
//                        1 -> if (viewModel.followingList.find { it.userId == userID } == null) {
//                            viewModel.followingList.add(user)
//                        }
//                        2 -> {
//                            Picasso.get().load(user.photoURL).into(binding.topProfile.userIV)
//                            binding.topProfile.usernameTV.text = user.username
//                        }
//                    }
                }
            }
            //imageVisibility(0)
            //imageVisibility(1)
            //followersAdapter.notifyDataSetChanged()
            //followingAdapter.notifyDataSetChanged()
        })
    }

    override fun onClick(view: View?) {
        /*when (view?.id) {
            R.id.followerB -> {
                imageVisibility(0)
                followingB.setBackgroundResource(R.color.colorWhite)
                followerB.setBackgroundResource(R.color.colorLightGray)
                viewModel.index = 0
                binding.profileRecyclerView.adapter = followersAdapter
            }
            R.id.followingB -> {
                imageVisibility(1)
                followerB.setBackgroundResource(R.color.colorWhite)
                followingB.setBackgroundResource(R.color.colorLightGray)
                viewModel.index = 1
                binding.profileRecyclerView.adapter = followingAdapter
            }
            R.id.back ->
                finish()
        }*/
    }

    /*private fun imageVisibility(type: Int) {
        when (type) {
            0 -> {
                if (viewModel.followersList.isEmpty())
                    binding.connectionIV.visibility = View.VISIBLE
                else
                    binding.connectionIV.visibility = View.GONE
            }
            1 -> {
                if (viewModel.followingList.isEmpty())
                    binding.connectionIV.visibility = View.VISIBLE
                else
                    binding.connectionIV.visibility = View.GONE
            }
        }
    }*/
}