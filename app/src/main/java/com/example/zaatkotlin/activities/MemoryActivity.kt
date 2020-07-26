package com.example.zaatkotlin.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.zaatkotlin.R
import com.example.zaatkotlin.adapters.CommentsAdapter
import com.example.zaatkotlin.databinding.ActivityMemoryBinding
import com.example.zaatkotlin.databinding.LayoutTopMemoryToolbarBinding
import com.example.zaatkotlin.models.Comment
import com.example.zaatkotlin.models.Memory
import com.example.zaatkotlin.models.User
import com.example.zaatkotlin.sendNotifications.*
import com.example.zaatkotlin.viewmodels.MemoryViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import retrofit2.Call
import retrofit2.Callback
import java.text.SimpleDateFormat
import java.util.*

class MemoryActivity : AppCompatActivity() {
    private lateinit var memoryID: String
    private lateinit var userID: String
    private lateinit var isFollow: String
    private lateinit var commentsAdapter: CommentsAdapter
    private lateinit var api: APIService
    private lateinit var binding: ActivityMemoryBinding
    private lateinit var toolbarBinding: LayoutTopMemoryToolbarBinding
    private val viewModel: MemoryViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMemoryBinding.inflate(layoutInflater)
        //we need to bind the root layout with our binder for external layout (merge is not a view so we can't inflate it)
        toolbarBinding = LayoutTopMemoryToolbarBinding.bind(binding.root)
        val view = binding.root
        setContentView(view)

        //memory = intent.getParcelableExtra("memoryObject")!!
        //memory.lovesCount = intent.getLongExtra("lovesCount", 0)
        //memory.commentsCount = intent.getLongExtra("commentsCount", 0)
        memoryID = intent.getStringExtra("memoryID")!!
        userID = intent.getStringExtra("userID")!!
        getMemory(memoryID)
        getUser(userID, 1)
        //isFollow = intent.getStringExtra("isFollow")!!
        // reset when phone rotate ,should solve it
        //viewModel.isReact = intent.getBooleanExtra("isReact", false)
        initWidget()
        // updateMemory()
//        setupReact()
//        setupMemory()
//        getComments()
    }

    private fun getMemory(memoryID: String) {
        viewModel.getMemory(memoryID).observe(this, Observer { querySnapShot ->
            if (querySnapShot != null) {
                for (document in querySnapShot) {
                    viewModel.memory = Memory(
                        title = document.data["title"] as String,
                        memory = document.data["memory"] as String,
                        uID = document.data["uid"] as String,
                        isSharing = document.data["sharing"] as Boolean,
                        date = document.data["date"] as String
                    )
                    viewModel.memory.date = convertDate(viewModel.memory.date)
                    viewModel.memory.timestamp = document.data["timestamp"] as Long
                    viewModel.memory.memoryID = document.data["memoryID"] as String
                    viewModel.memory.lovesCount = document.data["lovesCount"] as Long
                    viewModel.memory.commentsCount = document.data["commentsCount"] as Long
                    //we write that here so when reacts or comments count change UI update
                    val commentsText =
                        viewModel.memory.commentsCount.toString() + " " + this.resources.getText(
                            R.string.comments
                        )
                    binding.memoryInclude.lovesTV.text = viewModel.memory.lovesCount.toString()
                    binding.memoryInclude.commentsCountTV.text = commentsText
                }
            }
        })
    }

    private fun convertDate(date: String): String {
        val formatterDefault = SimpleDateFormat("K:mm a dd-MM-yyyy", Locale.US)
        val dateTemp = formatterDefault.parse(date)
        val formatter = SimpleDateFormat("K:mm a dd-MM-yyyy", Locale.getDefault())
        return formatter.format(dateTemp!!);
    }

    private fun setupReact() {
        viewModel.getUserReact(memoryID = viewModel.memory.memoryID).observe(this, Observer {
            viewModel.isReact = it.size() != 0
            if (viewModel.isReact) {
                binding.memoryInclude.loveButton.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.ic_love,
                    0,
                    0,
                    0
                )
            } else {
                binding.memoryInclude.loveButton.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.ic_dislove,
                    0,
                    0,
                    0
                )
            }
        })
    }

    private fun getComments() {
        viewModel.getComments(memoryID = viewModel.memory.memoryID)
            .observe(this, Observer { querySnapShot ->
                if (querySnapShot != null) {
                    viewModel.usersList.clear()
                    viewModel.commentsList.clear()
                    for (document in querySnapShot) {
                        val comment = document.toObject(Comment::class.java)
                        if (viewModel.usersList.find { it.userId == comment.userID } == null)
                            getUser(comment.userID)
                        viewModel.commentsList.add(comment)
                    }
                    viewModel.commentsList.sortBy { it.timestamp }
                }
            })
    }

    private fun getUser(userID: String, type: Int = 0) {
        viewModel.getUser(userID = userID).observe(this, Observer { querySnapShot ->
            if (querySnapShot != null) {
                for (document in querySnapShot) {
                    val user = User(
                        email = document["email"] as String,
                        photoURL = document["photoURL"] as String,
                        username = document["username"] as String,
                        userId = document["userId"] as String
                    )
                    if (type == 0) {
                        viewModel.usersList.add(user)
                        commentsAdapter.notifyDataSetChanged()
                    } else {
                        viewModel.user = user
                        setupReact()
                        setupMemory()
                        getComments()
                    }
                }
            }
        })
    }

    private fun initWidget() {
        commentsAdapter =
            CommentsAdapter(commentsList = viewModel.commentsList, usersList = viewModel.usersList)

        binding.commentsRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.commentsRecyclerView.adapter = commentsAdapter

        binding.commentInclude.sendCommentB.setOnClickListener {
            if (binding.commentInclude.commentET.text.isNotBlank()) {
                val date = getCurrentDateTime()
                val dateInString = date.toString("K:mm a dd-MM-yyyy")
                val comment = Comment(
                    memoryID = viewModel.memory.memoryID,
                    userID = FirebaseAuth.getInstance().uid!!,
                    commentContent = binding.commentInclude.commentET.text.trim().toString(),
                    date = dateInString,
                    timestamp = System.currentTimeMillis()
                )
                viewModel.makeComment(comment)
                binding.commentInclude.commentET.text.clear()
                if (viewModel.user.userId != viewModel.memory.uID) {
                    val message = "${viewModel.user.username} commented on your memory"
                    sendNotification(
                        message = message,
                        memoryID = viewModel.memory.memoryID,
                        ownerMemoryID = viewModel.memory.uID
                    )
                }
            }
        }
        binding.memoryInclude.userIV.setOnClickListener {
            goToProfile(
                context = this,
                user = viewModel.user
            )
        }
        binding.memoryInclude.usernameTV.setOnClickListener {
            goToProfile(
                context = this,
                user = viewModel.user
            )
        }
        binding.memoryInclude.loveButton.setOnClickListener {
            if (viewModel.isReact) {
                viewModel.deleteReact(memoryID = viewModel.memory.memoryID)
                viewModel.isReact = false
                binding.memoryInclude.loveButton.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.ic_dislove,
                    0,
                    0,
                    0
                )
            } else {
                viewModel.makeReact(memoryID = viewModel.memory.memoryID)
                if (viewModel.user.userId != viewModel.memory.uID) {
                    val message = "${viewModel.user.username} reacted love on your memory"
                    sendNotification(
                        message = message,
                        memoryID = viewModel.memory.memoryID,
                        ownerMemoryID = viewModel.memory.uID
                    )
                }
                viewModel.isReact = true
                binding.memoryInclude.loveButton.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.ic_love,
                    0,
                    0,
                    0
                )
            }
        }
        toolbarBinding.back.setOnClickListener { finish() }
    }

    /** ------------------------------ Convert date to specific format 'extension function' ---------**/
    private fun Date.toString(format: String, locale: Locale = Locale.US): String {
        val formatter = SimpleDateFormat(format, locale)
        return formatter.format(this)
    }

    /** ------------------------------- Get current date -------------------------**/
    private fun getCurrentDateTime(): Date {
        return Calendar.getInstance().time
    }

    private fun setupMemory() {
        Picasso.get().load(viewModel.user.photoURL).into(binding.memoryInclude.userIV)
        binding.memoryInclude.usernameTV.text = viewModel.user.username
        binding.memoryInclude.memoryTV.text = viewModel.memory.memory
        binding.memoryInclude.dateTV.text = viewModel.memory.date

    }

    private fun sendNotification(
        message: String,
        memoryID: String,
        ownerMemoryID: String
    ) {
        api =
            Client.getClient(url = "https://fcm.googleapis.com/").create(APIService::class.java)
        Firebase.firestore.collection("Token").document(ownerMemoryID).get().addOnSuccessListener {
            val usertoken = it.data?.get("token") as String
            send(usertoken, "ZAAT", message, memoryID, ownerMemoryID)
        }
    }

    private fun send(
        usertoken: String,
        s: String,
        message: String,
        memoryID: String,
        ownerMemoryID: String
    ) {
        val data =
            Data(Title = s, Message = message, MemoryID = memoryID, OwnerMemoryID = ownerMemoryID)
        val notificationSender = NotificationSender(data = data, to = usertoken)
        api.sendNotification(notificationSender).enqueue(object : Callback<Response> {
            override fun onFailure(call: Call<Response>?, t: Throwable?) {
                Log.d("TAG", "onFailure: ")
            }

            override fun onResponse(
                call: Call<Response>?,
                response: retrofit2.Response<Response>?
            ) {
            }

        })
    }

    private fun goToProfile(
        context: Context,
        user: User
    ) {
        val intent = Intent(context, OtherProfileActivity::class.java)
        intent.putExtra("userID", user.userId)
        intent.putExtra("username", user.username)
        intent.putExtra("photoURL", user.photoURL)
        context.startActivity(intent)
    }
}