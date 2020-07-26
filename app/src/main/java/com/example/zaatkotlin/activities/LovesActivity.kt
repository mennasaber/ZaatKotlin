package com.example.zaatkotlin.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.zaatkotlin.R
import com.example.zaatkotlin.adapters.LovesAdapter
import com.example.zaatkotlin.adapters.ProfileAdapter
import com.example.zaatkotlin.databinding.ActivityLovesBinding
import com.example.zaatkotlin.databinding.LayoutTopLovesToolbarBinding
import com.example.zaatkotlin.models.User
import com.example.zaatkotlin.viewmodels.LovesViewModel
import com.google.firebase.auth.FirebaseAuth

class LovesActivity : AppCompatActivity() {
    lateinit var binding: ActivityLovesBinding
    lateinit var toolbarBinding: LayoutTopLovesToolbarBinding
    lateinit var usersAdapter: LovesAdapter
    val viewModel: LovesViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLovesBinding.inflate(layoutInflater)
        toolbarBinding = LayoutTopLovesToolbarBinding.bind(binding.root)
        setContentView(binding.root)

        binding.Progress.visibility = View.VISIBLE
        binding.lovesRecyclerView.visibility = View.INVISIBLE

        val memoryID = intent.getStringExtra("memoryID")
        getUser(FirebaseAuth.getInstance().uid!!, 1)
        getUsersLove(memoryID!!)
        initWidget()

    }

    private fun initWidget() {
        usersAdapter = LovesAdapter(usersList = viewModel.usersList, viewModel = viewModel)
        binding.lovesRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.lovesRecyclerView.adapter = usersAdapter
        toolbarBinding.back.setOnClickListener { finish() }
    }

    private fun getUsersLove(memoryID: String) {
        viewModel.getUsersLoveMemory(memoryID).observe(this, Observer { querySnapShot ->
            if (querySnapShot != null) {
                for (document in querySnapShot) {
                    getUser(document["userID"] as String)
                }
            }
            binding.Progress.visibility = View.INVISIBLE
            binding.lovesRecyclerView.visibility = View.VISIBLE
        })
    }

    private fun getUser(userID: String, type: Int = 0) {
        viewModel.getUserData(userID).observe(this, Observer { querySnapShot ->
            if (querySnapShot != null) {
                for (document in querySnapShot) {
                    val user = User(
                        email = document["email"] as String,
                        photoURL = document["photoURL"] as String,
                        username = document["username"] as String,
                        userId = document["userId"] as String
                    )
                    if (type == 0) {
                        if (viewModel.usersList.find { it.userId == user.userId } == null)
                            viewModel.usersList.add(user)
                        usersAdapter.notifyDataSetChanged()
                    } else
                        viewModel.currentUser = user
                }
            }
        })
    }
}