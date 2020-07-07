package com.example.zaatkotlin.fragments

import android.os.Bundle
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
import com.example.zaatkotlin.models.Follow
import com.example.zaatkotlin.models.Memory
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
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    val viewModel: WorldViewModel by viewModels()
    private var followList: ArrayList<Follow> = ArrayList()
    private var memoriesList: ArrayList<Memory> = ArrayList()
    lateinit var worldAdapter: WorldAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_world, container, false)
        getFollowing(view)
        return view
    }

    private fun getMemories(view: View) {
        viewModel.getMemories().observe(viewLifecycleOwner, Observer { documents ->
            if (documents != null) {
                memoriesList.clear()
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
                    memoriesList.add(memory)

                }
                initWidget(view)
            }
        })
    }

    private fun initWidget(view: View?) {
        worldAdapter = WorldAdapter(memoriesList = memoriesList)
        val recyclerView = view?.findViewById<RecyclerView>(R.id.memoriesRecyclerView_)
        recyclerView?.adapter = worldAdapter
        recyclerView?.layoutManager = LinearLayoutManager(context)
    }

    /*** ------------------ Get all users that user follow ----------------------------*/
    private fun getFollowing(view: View) {
        viewModel.getUserFollowing().observe(viewLifecycleOwner, Observer { querySnapshot ->
            if (querySnapshot != null) {
                followList.clear()
                viewModel.followingList.clear()
                for (document in querySnapshot)
                    viewModel.followingList.add(document["followingId"] as String)
                getMemories(view)
            }
        })
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ChatFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ChatFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}