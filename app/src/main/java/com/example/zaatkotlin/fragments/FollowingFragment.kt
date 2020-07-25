package com.example.zaatkotlin.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.zaatkotlin.adapters.ProfileAdapter
import com.example.zaatkotlin.databinding.FragmentFollowingBinding
import com.example.zaatkotlin.models.User
import com.example.zaatkotlin.viewmodels.ProfileViewModel

class FollowingFragment : Fragment() {
    private lateinit var binding: FragmentFollowingBinding
    val viewModel: ProfileViewModel by viewModels()
    private lateinit var followingAdapter: ProfileAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFollowingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        initWidget()
        binding.Progress.visibility = View.VISIBLE
        binding.followingRecyclerView.visibility = View.INVISIBLE
        getFollowers()
    }

    private fun getFollowers() {
        viewModel.getFollowing().observe(viewLifecycleOwner, Observer { querySnapShot ->
            if (querySnapShot != null) {
                for (document in querySnapShot) {
                    getUser(document["followingId"] as String)
                }
            }
            binding.Progress.visibility = View.INVISIBLE
            binding.followingRecyclerView.visibility = View.VISIBLE
        })
    }

    private fun initWidget() {
        binding.followingRecyclerView.layoutManager = LinearLayoutManager(context)
        followingAdapter = ProfileAdapter(viewModel.followingList)
        binding.followingRecyclerView.adapter = followingAdapter
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
                    if (viewModel.followingList.find { it.userId == userID } == null)
                        viewModel.followingList.add(user)
                    followingAdapter.notifyDataSetChanged()
                }
            }
        })
    }

    private fun imageVisibility() {
        if (viewModel.followingList.isEmpty())
            binding.connectionIV.visibility = View.VISIBLE
        else
            binding.connectionIV.visibility = View.GONE
    }
}