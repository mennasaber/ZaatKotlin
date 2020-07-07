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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_world, container, false)
        Log.d(TAG, "onCreateView: ")
        initWidget(view)
        getFollowing(view)
        return view
    }

    private fun getMemories(userID: String) {
        Log.d(TAG, "getMemories: ")
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
                    if (viewModel.memoriesList.find { it.memoryID == memory.memoryID } == null)
                        viewModel.memoriesList.add(memory)
                    Log.d(TAG, "getMemories: ${viewModel.memoriesList.count()}")
                }
                viewModel.memoriesList.sortByDescending { it.timestamp }
                worldAdapter.notifyDataSetChanged()
            }
        })
    }

    private fun initWidget(view: View?) {
        Log.d(TAG, "initWidget: ")
        worldAdapter =
            WorldAdapter(memoriesList = viewModel.memoriesList, usersList = viewModel.followingList)
        val recyclerView = view?.findViewById<RecyclerView>(R.id.memoriesRecyclerView_)
        recyclerView?.adapter = worldAdapter
        recyclerView?.layoutManager = LinearLayoutManager(context)
    }

    /*** ------------------ Get all users that user follow ----------------------------*/
    private fun getFollowing(view: View) {
        Log.d(TAG, "getFollowing: ")
        viewModel.getUserFollowing().observe(viewLifecycleOwner, Observer { querySnapshot ->
            if (querySnapshot != null) {
                viewModel.followingList.clear()
                viewModel.memoriesList.clear()
                for (document in querySnapshot)
                    getUser(document["followingId"] as String)
                Log.d(TAG, "getFollowing: ${viewModel.memoriesList.count()}")
            }
        })
    }

    private fun getUser(userID: String) {
        Log.d(TAG, "getUser: ")
        viewModel.getUserData(userID).observe(viewLifecycleOwner, Observer { querySnapShot ->
            if (querySnapShot != null) {
                for (document in querySnapShot) {
                    val user = User(
                        email = document["email"] as String,
                        photoURL = document["photoURL"] as String,
                        username = document["username"] as String,
                        userId = document["userId"] as String
                    )
                    viewModel.followingList.add(user)
                    getMemories(userID)
                }
            }
        })
    }
}