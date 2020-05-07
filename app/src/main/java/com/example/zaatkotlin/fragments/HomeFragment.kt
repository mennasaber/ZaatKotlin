package com.example.zaatkotlin.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.GridView
import android.widget.ImageView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.zaatkotlin.AddMemoryActivity
import com.example.zaatkotlin.R
import com.example.zaatkotlin.adapters.GridViewAdapter
import com.example.zaatkotlin.models.Memory
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.MetadataChanges
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.layout_top_home_toolbar.*

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class HomeFragment : Fragment() {
    private val numOfItems = 2

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
        setupGridView(view)
        return view
    }

    private fun setupGridView(view: View?) {
        val db = Firebase.firestore
        val memoriesList = ArrayList<Memory>()
        val gridViewAdapter = GridViewAdapter(memoriesList = memoriesList)
        val gridView = view?.findViewById<GridView>(R.id.memoriesGridView)
        val gridWidth = resources.displayMetrics.widthPixels
        val itemWidth = gridWidth / numOfItems
        gridView?.columnWidth = itemWidth
        gridView?.adapter = gridViewAdapter

        db.collection("Memories").whereEqualTo("uid", FirebaseAuth.getInstance().uid.toString())
            .addSnapshotListener(MetadataChanges.INCLUDE) { value, e ->
                if (e != null)
                    return@addSnapshotListener
                memoriesList.clear()
                for (document in value!!) {
                    val memory = Memory(
                        title = document.data["title"] as String,
                        memory = document.data["memory"] as String,
                        uID = document.data["uid"] as String,
                        isSharing = document.data["sharing"] as Boolean,
                        date = document.data["date"] as String
                    )
                    memory.memoryID = document.data["memoryID"] as String
                    memoriesList.add(memory)
                    gridViewAdapter.notifyDataSetChanged()
                }
            }
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

    override fun onStart() {
        super.onStart()

    }
}