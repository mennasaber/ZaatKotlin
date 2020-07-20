package com.example.zaatkotlin.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.zaatkotlin.adapters.SearchAdapter
import com.example.zaatkotlin.databinding.FragmentSearchBinding
import com.example.zaatkotlin.databinding.LayoutTopSearchToolbarBinding
import com.example.zaatkotlin.models.Follow
import com.example.zaatkotlin.models.User
import com.example.zaatkotlin.viewmodels.SearchViewModel
import com.google.firebase.auth.FirebaseAuth
import java.util.*
import kotlin.collections.ArrayList

class SearchFragment : Fragment() {
    private lateinit var binding: FragmentSearchBinding
    private lateinit var toolbarbinding: LayoutTopSearchToolbarBinding
    private lateinit var searchAdapter: SearchAdapter
    private var followList: ArrayList<Follow> = ArrayList()
    private val viewModel: SearchViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        toolbarbinding = LayoutTopSearchToolbarBinding.bind(binding.root)
        initWidget()
        return binding.root
    }

    private fun initWidget() {
        getFollowing()
        viewModel.usersList.clear()
        searchAdapter = SearchAdapter(viewModel.usersList, viewModel.followList, viewModel)
        binding.usersRecyclerView.adapter = searchAdapter
        binding.usersRecyclerView.layoutManager = LinearLayoutManager(binding.root.context)
        toolbarbinding.searchImage.setOnClickListener {
            if (toolbarbinding.searchET.text.toString().trim() != "") {
                binding.progressLayout.visibility = View.VISIBLE
                getUsers(toolbarbinding.searchET.text.toString().trim())
            }
        }
        toolbarbinding.searchET.addTextChangedListener {
            if (toolbarbinding.searchET.text.toString().isEmpty()) {
                viewModel.usersList.clear()
                searchAdapter.notifyDataSetChanged()
            }
        }
    }

    private fun getFollowing() {
        viewModel.getUserFollowing().observe(viewLifecycleOwner, Observer { querySnapshot ->
            followList.clear()
            if (querySnapshot != null) {
                for (document in querySnapshot)
                    followList.add(document.toObject(Follow::class.java))
            }
        })
    }

    private fun getUsers(searchContent: String) {
        viewModel.getUsersFromDB().observe(viewLifecycleOwner, Observer { querySnapshot ->
            if (querySnapshot != null) {
                viewModel.usersList.clear()
                viewModel.followList.clear()
                for (document in querySnapshot) {
                    if (document.data["username"].toString().toLowerCase(Locale.ROOT).contains(
                            searchContent.toLowerCase(
                                Locale.ROOT
                            )
                        ) && document.data["userId"].toString() != FirebaseAuth.getInstance().uid.toString()
                    ) {

                        val user = User(
                            username = document.data["username"] as String,
                            photoURL = document.data["photoURL"] as String,
                            userId = document.data["userId"] as String,
                            email = document.data["email"] as String
                        )
                        val isFollow = isFollowing(user.userId)
                        viewModel.followList[user.userId!!] = isFollow
                        viewModel.usersList.add(user)
                    }
                }
                searchAdapter.notifyDataSetChanged()
            }
        })
        binding.progressLayout.visibility = View.GONE
    }

    private fun isFollowing(userId: String?): Boolean {
        return followList.find { it.followingId == userId } != null
    }
}