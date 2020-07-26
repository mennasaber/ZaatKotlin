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
import com.example.zaatkotlin.R
import com.example.zaatkotlin.activities.AddMemoryActivity
import com.example.zaatkotlin.adapters.RecyclerViewAdapter
import com.example.zaatkotlin.callbacks.SimpleItemTouchHelperCallback
import com.example.zaatkotlin.databinding.FragmentHomeBinding
import com.example.zaatkotlin.databinding.LayoutTopHomeToolbarBinding
import com.example.zaatkotlin.models.Memory
import com.example.zaatkotlin.viewmodels.MemoriesViewModel
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var toolbarbinding: LayoutTopHomeToolbarBinding
    private lateinit var memoriesList: ArrayList<Memory>
    private lateinit var memoriesAdapter: RecyclerViewAdapter

    private val viewModel: MemoriesViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        toolbarbinding = LayoutTopHomeToolbarBinding.bind(binding.root)
        toolbarbinding.addMemory.setOnClickListener {
            val intent = Intent(context, AddMemoryActivity::class.java)
            startActivity(intent)
        }
        binding.Progress.visibility = View.VISIBLE
        binding.memoriesRecyclerView.visibility = View.INVISIBLE
        initWidget()
        getMemories()
        return binding.root
    }

    private fun initWidget() {
        memoriesList = ArrayList()
        memoriesAdapter = RecyclerViewAdapter(memoriesList = memoriesList, viewModel = viewModel)
        binding.memoriesRecyclerView.adapter = memoriesAdapter
        binding.memoriesRecyclerView.layoutManager =
            LinearLayoutManager(context)
        val itemTouchHelperCallback = SimpleItemTouchHelperCallback(memoriesAdapter)
        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(binding.memoriesRecyclerView)
    }

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
                        memory.date = convertDate(memory.date)
                        memory.timestamp = document.data["timestamp"] as Long
                        memory.memoryID = document.data["memoryID"] as String
                        memoriesList.add(memory)
                        //memoriesAdapter.notifyDataSetChanged()
                    }
                }
                memoriesAdapter.notifyDataSetChanged()
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

    private fun imageVisibility(view: View?) {
        val vectorIV: ImageView = view?.findViewById(R.id.vector)!!
        if (memoriesList.isEmpty())
            vectorIV.visibility = View.VISIBLE
        else
            vectorIV.visibility = View.GONE
    }
}