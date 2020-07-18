package com.example.zaatkotlin.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.zaatkotlin.R
import com.example.zaatkotlin.activities.AddMemoryActivity
import com.example.zaatkotlin.adapters.RecyclerViewAdapter
import com.example.zaatkotlin.callbacks.SimpleItemTouchHelperCallback
import com.example.zaatkotlin.models.Memory
import com.example.zaatkotlin.viewmodels.MemoriesViewModel


class HomeFragment : Fragment() {

    private lateinit var memoriesList: ArrayList<Memory>
    private lateinit var memoriesAdapter: RecyclerViewAdapter
    private lateinit var recyclerView: RecyclerView

    private val viewModel: MemoriesViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_home, container, false)
        val addMemoryButton = view.findViewById<ImageView>(R.id.addMemory)
        addMemoryButton.setOnClickListener {
            val intent = Intent(context, AddMemoryActivity::class.java)
            startActivity(intent)
        }
        initWidget(view)
        getMemories()
        return view
    }

    // ------------------------------ initialization ----------------------------
    private fun initWidget(view: View?) {

        memoriesList = ArrayList()

        memoriesAdapter = RecyclerViewAdapter(memoriesList = memoriesList, viewModel = viewModel)

        recyclerView = view?.findViewById(R.id.memoriesRecyclerView)!!
        recyclerView.adapter = memoriesAdapter
        recyclerView.layoutManager =
            LinearLayoutManager(context)
        val itemTouchHelperCallback = SimpleItemTouchHelperCallback(memoriesAdapter)
        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }

    // ------------------------------ Get memories from viewModel that return liveData<QuerySnapShot> ----------------------------
    private fun getMemories() {
        viewModel.getDataLive()
            .observe(viewLifecycleOwner, Observer { QuerySnapshot ->
                if (QuerySnapshot != null) {
                    memoriesList.clear()
                    for (document in QuerySnapshot) {
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
                        //memoriesAdapter.notifyDataSetChanged()
                    }
                }
                memoriesAdapter.notifyDataSetChanged()
            })
    }

    private fun imageVisibility(view: View?) {
        val vectorIV: ImageView = view?.findViewById(R.id.vector)!!
        if (memoriesList.isEmpty())
            vectorIV.visibility = View.VISIBLE
        else
            vectorIV.visibility = View.GONE
    }
}