package com.example.zaatkotlin.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.zaatkotlin.adapters.ProfileAdapter
import com.example.zaatkotlin.databinding.FragmentFollowersBinding
import com.example.zaatkotlin.models.User
import com.example.zaatkotlin.viewmodels.ProfileViewModel

class FollowersFragment : Fragment() {
    private lateinit var binding: FragmentFollowersBinding
    val viewModel: ProfileViewModel by viewModels()
    private lateinit var followersAdapter: ProfileAdapter
    private val TAG = "FollowersFragment"
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFollowersBinding.inflate(inflater, container, false)
        Log.d(TAG, "onCreateView: ")
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        initWidget()
        binding.Progress.visibility = View.VISIBLE
        binding.followersRecyclerView.visibility = View.VISIBLE
        getFollowers()
    }

    private fun getFollowers() {
        viewModel.getFollowers().observe(viewLifecycleOwner, Observer { querySnapShot ->
            if (querySnapShot != null) {
                for (document in querySnapShot) {
                    getUser(document["followerId"] as String)
                }
            }
            binding.Progress.visibility = View.INVISIBLE
            binding.followersRecyclerView.visibility = View.VISIBLE
        })
    }

    private fun initWidget() {
        binding.followersRecyclerView.layoutManager = LinearLayoutManager(context)
        followersAdapter = ProfileAdapter(viewModel.followersList)
        binding.followersRecyclerView.adapter = followersAdapter
    }

    private fun getUser(userID: String) {
        viewModel.getUserData(userID).observe(viewLifecycleOwner, Observer { querySnapShot ->
            if (querySnapShot != null) {
                for (document in querySnapShot) {
                    val user = User(
                        email = document["email"] as String,
                        photoURL = document["photoURL"] as String,
                        username = document["username"] as String,
                        userId = document["userId"] as String
                    )
                    if (viewModel.followersList.find { it.userId == userID } == null)
                        viewModel.followersList.add(user)
                    followersAdapter.notifyDataSetChanged()
                }
            }
        })
    }

    private fun imageVisibility() {
        if (viewModel.followersList.isEmpty())
            binding.connectionIV.visibility = View.VISIBLE
        else
            binding.connectionIV.visibility = View.GONE
    }
}