package com.example.zaatkotlin.activities

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.zaatkotlin.R
import com.example.zaatkotlin.adapters.OtherProfileAdapter
import com.example.zaatkotlin.models.Memory
import com.example.zaatkotlin.models.User
import com.example.zaatkotlin.viewmodels.OtherProfileViewModel
import com.squareup.picasso.Picasso

class OtherProfileActivity : AppCompatActivity() {
    val viewModel: OtherProfileViewModel by viewModels()
    private val usersList = ArrayList<User>()
    private lateinit var profileAdapter: OtherProfileAdapter
    lateinit var userID: String
    lateinit var username: String
    lateinit var photoURL: String
    lateinit var isFollow: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_other_profile)
        userID = intent.getStringExtra("userID")!!
        username = intent.getStringExtra("username")!!
        photoURL = intent.getStringExtra("photoURL")!!
        isFollow = intent.getStringExtra("isFollow")!!
        viewModel.isFollow = isFollow
        initWidget()
        getMemories(userID)
    }

    private fun getMemories(userID: String) {
        viewModel.getMemories(userID).observe(this, Observer { querySnapShot ->
            if (querySnapShot != null) {
                for (document in querySnapShot) {
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

                    if (viewModel.memoriesList.find { it.memoryID == memory.memoryID } == null) {
                        viewModel.memoriesList.add(memory)
                        isReact(memoryID = memory.memoryID)
                    }
                }
                viewModel.memoriesList.sortByDescending { it.timestamp }
                profileAdapter.notifyDataSetChanged()
                imageVisibility()
            }
        })

    }

    private fun isReact(memoryID: String) {
        viewModel.getUserReact(memoryID).observe(this, Observer {
            viewModel.reactMap[memoryID] = it.size() != 0
            Log.d("TAG", "isReact: ${viewModel.reactMap.size}   ${viewModel.memoriesList.size} ")
            profileAdapter.notifyDataSetChanged()
            imageVisibility()
        })
    }

    private fun imageVisibility() {
        val publicIV: ImageView = findViewById(R.id.publicIV)
        if (viewModel.memoriesList.size == 0)
            publicIV.visibility = View.VISIBLE
        else
            publicIV.visibility = View.INVISIBLE
    }

    private fun initWidget() {
        val userIV = findViewById<ImageView>(R.id.userIV)
        val usernameTV = findViewById<TextView>(R.id.usernameTV)
        val followB = findViewById<Button>(R.id.followB)
        val recyclerView = findViewById<RecyclerView>(R.id.profileRecyclerView)
        val backB = findViewById<ImageView>(R.id.back)

        Picasso.get().load(photoURL).into(userIV)
        usernameTV.text = username
        followB.text = viewModel.isFollow

        usersList.add(User("", photoURL, username, userID))

        profileAdapter =
            OtherProfileAdapter(
                memoriesList = viewModel.memoriesList, usersList = usersList,
                viewModel = viewModel
            )
        recyclerView.adapter = profileAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        followB.setOnClickListener {
            if (viewModel.isFollow == "Follow") {
                viewModel.makeFollow(userID)
                viewModel.isFollow = "Unfollow"
            } else {
                viewModel.deleteFollow(userID)
                viewModel.isFollow = "Follow"
            }
            followB.text = viewModel.isFollow
        }
        backB.setOnClickListener { finish() }
    }
}