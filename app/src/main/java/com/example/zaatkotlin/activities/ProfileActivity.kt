package com.example.zaatkotlin.activities

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.zaatkotlin.R
import com.example.zaatkotlin.adapters.ProfileAdapter
import com.example.zaatkotlin.models.User
import com.example.zaatkotlin.viewmodels.ProfileViewModel
import com.google.firebase.auth.FirebaseAuth
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_profile.*


class ProfileActivity : AppCompatActivity(), View.OnClickListener {
    private val viewModel: ProfileViewModel by viewModels()
    lateinit var userIV: ImageView
    lateinit var usernameTV: TextView
    lateinit var followersB: Button
    lateinit var followingB: Button
    lateinit var recyclerView: RecyclerView
    lateinit var followingAdapter: ProfileAdapter
    lateinit var followersAdapter: ProfileAdapter
    lateinit var backB: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        initWidget()
        initLists()
    }

    private fun initLists() {
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
    }

    private fun getUser(userID: String, type: Int) {
        viewModel.getUserData(userID).observe(this, Observer { querySnapShot ->
            if (querySnapShot != null) {
                for (document in querySnapShot) {
                    val user = User(
                        email = document["email"] as String,
                        photoURL = document["photoURL"] as String,
                        username = document["username"] as String,
                        userId = document["userId"] as String
                    )
                    when (type) {
                        0 -> if (viewModel.followersList.find { it.userId == userID } == null) {
                            viewModel.followersList.add(user)
                        }
                        1 -> if (viewModel.followingList.find { it.userId == userID } == null) {
                            viewModel.followingList.add(user)
                        }
                        2 -> {
                            Picasso.get().load(user.photoURL).into(userIV)
                            usernameTV.text = user.username
                        }
                    }
                }
            }
            imageVisibility(0)
            imageVisibility(1)
            followersAdapter.notifyDataSetChanged()
            followingAdapter.notifyDataSetChanged()
        })
    }

    private fun initWidget() {
        followersAdapter = ProfileAdapter(viewModel.followersList)
        followingAdapter = ProfileAdapter(viewModel.followingList)

        backB = findViewById(R.id.back)
        userIV = findViewById(R.id.userIV)
        usernameTV = findViewById(R.id.usernameTV)
        followersB = findViewById(R.id.followerB)
        followingB = findViewById(R.id.followingB)
        recyclerView = findViewById(R.id.profileRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        followersB.setOnClickListener(this)
        followingB.setOnClickListener(this)
        backB.setOnClickListener(this)

        getUser(FirebaseAuth.getInstance().uid!!, 2)
        if (viewModel.index == 1)
            followingB.callOnClick()
        else
            followersB.callOnClick()
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.followerB -> {
                imageVisibility(0)
                followingB.setBackgroundResource(R.color.colorWhite)
                followerB.setBackgroundResource(R.color.colorLightGray)
                viewModel.index = 0
                recyclerView.adapter = followersAdapter
            }
            R.id.followingB -> {
                imageVisibility(1)
                followerB.setBackgroundResource(R.color.colorWhite)
                followingB.setBackgroundResource(R.color.colorLightGray)
                viewModel.index = 1
                recyclerView.adapter = followingAdapter
            }
            R.id.back ->
                finish()
        }
    }

    private fun imageVisibility(type: Int) {
        val connectionIV: ImageView = findViewById(R.id.connectionIV)
        when (type) {
            0 -> {
                if (viewModel.followersList.isEmpty())
                    connectionIV.visibility = View.VISIBLE
                else
                    connectionIV.visibility = View.GONE
            }
            1 -> {
                if (viewModel.followingList.isEmpty())
                    connectionIV.visibility = View.VISIBLE
                else
                    connectionIV.visibility = View.GONE
            }
        }
    }
}