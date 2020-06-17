package com.example.zaatkotlin.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.zaatkotlin.activities.AddMemoryActivity
import com.example.zaatkotlin.R
import com.example.zaatkotlin.adapters.RecyclerViewAdapter
import com.example.zaatkotlin.models.Memory
import com.example.zaatkotlin.viewmodels.MemoriesViewModel


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class HomeFragment : Fragment() {
    private val numOfItems = 2
    lateinit var memoriesList: ArrayList<Memory>
    lateinit var memoriesAdapter: RecyclerViewAdapter
    lateinit var recyclerView: RecyclerView

    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

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

        memoriesAdapter = RecyclerViewAdapter(memoriesList = memoriesList)

        recyclerView = view?.findViewById(R.id.memoriesRecyclerView)!!

        recyclerView.adapter = memoriesAdapter
        recyclerView.layoutManager =
            StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
    }

    // ------------------------------ Get memories from viewModel that return liveData<QuerySnapShot> ----------------------------
    private fun getMemories() {
        val viewModel: MemoriesViewModel by viewModels()
        viewModel.getDataLive()
            .observe(viewLifecycleOwner, Observer { QuerySnapshot ->
                memoriesList.clear()
                for (document in QuerySnapshot) {
                    val memory = Memory(
                        title = document.data["title"] as String,
                        memory = document.data["memory"] as String,
                        uID = document.data["uid"] as String,
                        isSharing = document.data["sharing"] as Boolean,
                        date = document.data["date"] as String
                    )
                    memory.memoryID = document.data["memoryID"] as String
                    memoriesList.add(memory)
                    memoriesAdapter.notifyDataSetChanged()
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
         * @return A new instance of fragment HomeFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HomeFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

}