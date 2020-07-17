package com.example.zaatkotlin.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.zaatkotlin.R
import com.example.zaatkotlin.adapters.WorldAdapter
import com.example.zaatkotlin.models.Memory
import com.example.zaatkotlin.models.User
import com.example.zaatkotlin.viewmodels.WorldViewModel
import com.google.firebase.auth.FirebaseAuth

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ChatFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ChatFragment : Fragment() {
    private val TAG = "WorldFragment"
    private val viewModel: WorldViewModel by viewModels()
    lateinit var worldAdapter: WorldAdapter
    lateinit var recyclerView: RecyclerView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_world, container, false)
        getCurrentUser()
        initWidget(view)
        getFollowing(view)
        return view
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
                    memory.timestamp = document.data["timestamp"] as Long
                    memory.memoryID = document.data["memoryID"] as String
                    memory.lovesCount = document.data["lovesCount"] as Long

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

    private fun isReact(memoryID: String) {
        viewModel.getUserReact(memoryID).observe(viewLifecycleOwner, Observer {
            viewModel.reactMap[memoryID] = it.size() != 0
            Log.d("TAG", "isReact: ${viewModel.reactMap.size}   ${viewModel.memoriesList.size} ")
            worldAdapter.notifyDataSetChanged()
        })
    }

    private fun initWidget(view: View?) {
        worldAdapter =
            WorldAdapter(
                memoriesList = viewModel.memoriesList,
                usersList = viewModel.followingList,
                viewModel = viewModel
            )
        recyclerView = view?.findViewById(R.id.memoriesRecyclerView_)!!
        recyclerView.adapter = worldAdapter
        recyclerView.layoutManager = LinearLayoutManager(context)
    }

    /*** ------------------ Get all users that user follow ----------------------------*/
    private fun getFollowing(view: View?) {
        viewModel.getUserFollowing().observe(viewLifecycleOwner, Observer { querySnapshot ->
            if (querySnapshot != null) {
                viewModel.memoriesList.clear()
                viewModel.followingList.clear()
                viewModel.listIDs.clear()
                Log.d(TAG, "getFollowing: ${querySnapshot.size()}")
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

