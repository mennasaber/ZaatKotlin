package com.example.zaatkotlin.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.zaatkotlin.adapters.WorldAdapter
import com.example.zaatkotlin.databinding.FragmentWorldBinding
import com.example.zaatkotlin.models.Memory
import com.example.zaatkotlin.models.User
import com.example.zaatkotlin.viewmodels.WorldViewModel
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.*

class ChatFragment : Fragment() {
    private lateinit var binding: FragmentWorldBinding
    private val viewModel: WorldViewModel by viewModels()
    private lateinit var worldAdapter: WorldAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentWorldBinding.inflate(inflater, container, false)
        getCurrentUser()
        initWidget()
        getFollowing()
        return binding.root
    }

    private fun getCurrentUser() {
        viewModel.getUserData(userID = FirebaseAuth.getInstance().uid!!)
            .observe(viewLifecycleOwner, Observer {
                if (!it.isEmpty && it != null) {
                    for (document in it) {
                        viewModel.currentUser = User(
                            email = document["email"] as String,
                            photoURL = document["photoURL"] as String,
                            username = document["username"] as String,
                            userId = document["userId"] as String
                        )
                    }
                }
            })
    }

    private fun getMemories(userID: String) {
        viewModel.getMemories(userID).observe(viewLifecycleOwner, Observer { documents ->
            if (documents != null) {
                for (document in documents) {
                    val memory = Memory(
                        title = document.data["title"] as String,
                        memory = document.data["memory"] as String,
                        uID = document.data["uid"] as String,
                        isSharing = document.data["sharing"] as Boolean,
                        date = document.data["date"] as String
                    )
                    memory.date = convertDate(memory.date)
                    memory.timestamp = document.data["timestamp"] as Long
                    memory.memoryID = document.data["memoryID"] as String
                    memory.lovesCount = document.data["lovesCount"] as Long
                    memory.commentsCount = document.data["commentsCount"] as Long

                    if (viewModel.memoriesList.find { it.memoryID == memory.memoryID } == null
                        && viewModel.followingList.find { it.userId == memory.uID } != null) {
                        viewModel.memoriesList.add(memory)
                        isReact(memory.memoryID)
                    }
                }
                viewModel.memoriesList.sortByDescending { it.timestamp }
                worldAdapter.notifyDataSetChanged()
            }
        })
    }

    private fun convertDate(date: String): String {
        val formatterDefault = SimpleDateFormat("K:mm a dd-MM-yyyy", Locale.US)
        val dateTemp = formatterDefault.parse(date)
        val formatter = SimpleDateFormat("K:mm a dd-MM-yyyy", Locale.getDefault())
        return formatter.format(dateTemp!!);
    }

    private fun isReact(memoryID: String) {
        viewModel.getUserReact(memoryID).observe(viewLifecycleOwner, Observer {
            viewModel.reactMap[memoryID] = it.size() != 0
            worldAdapter.notifyDataSetChanged()
        })
    }

    private fun initWidget() {
        worldAdapter =
            WorldAdapter(
                memoriesList = viewModel.memoriesList,
                usersList = viewModel.followingList,
                viewModel = viewModel
            )
        binding.memoriesRecyclerView.adapter = worldAdapter
        binding.memoriesRecyclerView.layoutManager = LinearLayoutManager(context)
    }

    private fun getFollowing() {
        viewModel.getUserFollowing().observe(viewLifecycleOwner, Observer { querySnapshot ->
            if (querySnapshot != null) {
                viewModel.memoriesList.clear()
                viewModel.followingList.clear()
                viewModel.listIDs.clear()
                for (document in querySnapshot) {
                    viewModel.listIDs.add(document["followingId"] as String)
                    getUser(document["followingId"] as String)
                }
            }
        })
    }

    //may be we don't need observer here we want only user data once
    // but what will happen if thus user change his photo??!!
    // so i thing we need observer
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
                    if (viewModel.followingList.find { it.userId == user.userId } == null &&
                        viewModel.listIDs.find { it == user.userId } != null) {
                        viewModel.followingList.add(user)
                        getMemories(userID)
                    }
                }
            }
        })
    }
}

