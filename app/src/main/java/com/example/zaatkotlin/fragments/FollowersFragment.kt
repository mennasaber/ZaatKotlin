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
import com.example.zaatkotlin.databinding.FragmentFollowersBinding
import com.example.zaatkotlin.models.User
import com.example.zaatkotlin.viewmodels.ProfileViewModel
import com.google.firebase.auth.FirebaseAuth

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
        getUser(FirebaseAuth.getInstance().uid!!, 1)
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
        followersAdapter = ProfileAdapter(viewModel.followersList, viewModel)
        binding.followersRecyclerView.adapter = followersAdapter
    }

    private fun getUser(userID: String, type: Int = 0) {
        viewModel.getUserData(userID).observe(viewLifecycleOwner, Observer { querySnapShot ->
            if (querySnapShot != null) {
                for (document in querySnapShot) {
                    val user = User(
                        email = document["email"] as String,
                        photoURL = document["photoURL"] as String,
                        username = document["username"] as String,
                        userId = document["userId"] as String
                    )
                    if (type == 0) {
                        if (viewModel.followersList.find { it.userId == userID } == null)
                            viewModel.followersList.add(user)
                        followersAdapter.notifyDataSetChanged()
                    } else
                        viewModel.currentUser = user
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