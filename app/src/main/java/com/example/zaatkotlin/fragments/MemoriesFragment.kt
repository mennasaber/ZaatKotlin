package com.example.zaatkotlin.fragments

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.zaatkotlin.adapters.ProfileMemoriesAdapter
import com.example.zaatkotlin.databinding.FragmentMemoriesBinding
import com.example.zaatkotlin.models.Memory
import com.example.zaatkotlin.models.User
import com.example.zaatkotlin.viewmodels.ProfileViewModel
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.*

class MemoriesFragment : Fragment() {
    private lateinit var binding: FragmentMemoriesBinding
    val viewModel: ProfileViewModel by viewModels()
    lateinit var user: User
    private val TAG = "MemoriesFragment"
    private lateinit var memoriesAdapter: ProfileMemoriesAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMemoriesBinding.inflate(inflater, container, false)
        Log.d(TAG, "onCreateView: ")
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            Log.d(TAG, "onResume: ${binding.root.layoutDirection}")
        }

        binding.Progress.visibility = View.VISIBLE
        binding.memoriesRecyclerView.visibility = View.VISIBLE
        getUser(FirebaseAuth.getInstance().uid!!)
        getMemories()
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause: ")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate: ")
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart: ")
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop: ")
    }

    private fun getMemories() {
        viewModel.getMemories().observe(viewLifecycleOwner, Observer { querySnapShot ->
            Log.d("TAG", "getMemories:")
            if (querySnapShot != null) {
                for (document in querySnapShot) {
                    Log.d("TAG", "getMemories: here")
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
                    val tempMem = viewModel.memoriesList.find { it.memoryID == memory.memoryID }
                    if (tempMem != null) {
                        viewModel.memoriesList.remove(tempMem)
                    }
                    viewModel.memoriesList.add(memory)
                    isReact(memoryID = memory.memoryID)
                }
                viewModel.memoriesList.sortByDescending { it.timestamp }
            }
            binding.Progress.visibility = View.INVISIBLE
            binding.memoriesRecyclerView.visibility = View.VISIBLE
        })
    }

    private fun convertDate(date: String): String {
        val formatterDefault = SimpleDateFormat("K:mm a dd-MM-yyyy", Locale.US)
        val dateTemp = formatterDefault.parse(date)
        val formatter = SimpleDateFormat("K:mm a dd-MM-yyyy", Locale.getDefault())
        return formatter.format(dateTemp!!)
    }

    private fun initWidget() {
        binding.memoriesRecyclerView.layoutManager = LinearLayoutManager(context)
        memoriesAdapter =
            ProfileMemoriesAdapter(
                memoriesList = viewModel.memoriesList,
                viewModel = viewModel,
                user = viewModel.user
            )
        Log.d("TAG", "initWidget: ${viewModel.memoriesList.count()}")
        binding.memoriesRecyclerView.adapter = memoriesAdapter
    }

    private fun getUser(userID: String) {
        viewModel.getUserData(userID).observe(viewLifecycleOwner, Observer { querySnapShot ->
            if (querySnapShot != null) {
                for (document in querySnapShot) {
                    viewModel.user = User(
                        email = document["email"] as String,
                        photoURL = document["photoURL"] as String,
                        username = document["username"] as String,
                        userId = document["userId"] as String
                    )
                    initWidget()
                }
            }
        })
    }

    private fun isReact(memoryID: String) {
        viewModel.getUserReact(memoryID).observe(viewLifecycleOwner, Observer {
            viewModel.reactMap[memoryID] = it.size() != 0
            memoriesAdapter.notifyDataSetChanged()
        })
    }
}