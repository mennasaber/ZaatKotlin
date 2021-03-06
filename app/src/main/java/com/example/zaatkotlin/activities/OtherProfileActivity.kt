package com.example.zaatkotlin.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.zaatkotlin.R
import com.example.zaatkotlin.adapters.OtherProfileAdapter
import com.example.zaatkotlin.databinding.ActivityOtherProfileBinding
import com.example.zaatkotlin.databinding.LayoutTopOtherProfileToolbarBinding
import com.example.zaatkotlin.databinding.LayoutTopProfileToolbarBinding
import com.example.zaatkotlin.models.Memory
import com.example.zaatkotlin.models.User
import com.example.zaatkotlin.viewmodels.OtherProfileViewModel
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class OtherProfileActivity : AppCompatActivity() {
    val viewModel: OtherProfileViewModel by viewModels()
    private lateinit var profileAdapter: OtherProfileAdapter
    private lateinit var binding: ActivityOtherProfileBinding
    private lateinit var toolbarBinding: LayoutTopOtherProfileToolbarBinding
    private val usersList = ArrayList<User>()
    private lateinit var userID: String
    private lateinit var currentUsername: String
    private lateinit var username: String
    private lateinit var photoURL: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOtherProfileBinding.inflate(layoutInflater)
        toolbarBinding = LayoutTopOtherProfileToolbarBinding.bind(binding.root)
        setContentView(binding.root)

        userID = intent.getStringExtra("userID")!!
        username = intent.getStringExtra("username")!!
        photoURL = intent.getStringExtra("photoURL")!!
        currentUsername = intent.getStringExtra("currentUsername")!!

        setupFollow()
        initWidget()
        getMemories(userID)
    }

    private fun setupFollow() {
        viewModel.isFollow(userID = userID).observe(this, Observer {
            viewModel.isFollow = !it.isEmpty
            if (viewModel.isFollow)
                binding.followB.text = this.resources.getString(R.string.unfollow)
            else
                binding.followB.text = this.resources.getString(R.string.follow)
        })
    }

    private fun initWidget() {
        Picasso.get().load(photoURL).into(binding.userIV)
        binding.usernameTV.text = username
        usersList.add(User("", photoURL, username, userID))
        profileAdapter =
            OtherProfileAdapter(
                memoriesList = viewModel.memoriesList,
                usersList = usersList,
                viewModel = viewModel,
                username = currentUsername
            )
        binding.profileRecyclerView.adapter = profileAdapter
        binding.profileRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.followB.setOnClickListener {
            if (!viewModel.isFollow) {
                viewModel.makeFollow(userID)
                viewModel.isFollow = true
                binding.followB.text = this.resources.getString(R.string.unfollow)
            } else {
                viewModel.deleteFollow(userID)
                viewModel.isFollow = false
                binding.followB.text = this.resources.getString(R.string.follow)
            }
        }
        toolbarBinding.back.setOnClickListener { finish() }
        binding.userIV.setOnClickListener { goToImage() }
    }

    private fun goToImage() {
        val intent = Intent(this, ImageActivity::class.java)
        intent.putExtra("imageURI", photoURL)
        startActivity(intent)
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
                    memory.date = convertDate(memory.date)
                    memory.timestamp = document.data["timestamp"] as Long
                    memory.memoryID = document.data["memoryID"] as String
                    memory.lovesCount = document.data["lovesCount"] as Long
                    memory.commentsCount = document.data["commentsCount"] as Long
                    val tempMemory = viewModel.memoriesList.find { it.memoryID == memory.memoryID }
                    if (tempMemory != null) {
                        viewModel.memoriesList.remove(tempMemory)
                    }
                    viewModel.memoriesList.add(memory)
                    isReact(memoryID = memory.memoryID)
                }
                viewModel.memoriesList.sortByDescending { it.timestamp }
                //profileAdapter.notifyDataSetChanged()
            }
        })

    }

    private fun convertDate(date: String): String {
        val formatterDefault = SimpleDateFormat("K:mm a dd-MM-yyyy", Locale.US)
        val dateTemp = formatterDefault.parse(date)
        val formatter = SimpleDateFormat("K:mm a dd-MM-yyyy", Locale.getDefault())
        return formatter.format(dateTemp!!)
    }

    private fun isReact(memoryID: String) {
        viewModel.getUserReact(memoryID).observe(this, Observer {
            viewModel.reactMap[memoryID] = it.size() != 0
            profileAdapter.notifyDataSetChanged()
        })
    }
}