package com.example.zaatkotlin.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
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
import com.example.zaatkotlin.viewmodels.MemoryViewModel
import com.google.firebase.auth.FirebaseAuth
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.util.*

class MemoryActivity : AppCompatActivity() {
    private lateinit var memory: Memory
    private lateinit var user: User
    private lateinit var isFollow: String
    private lateinit var commentsAdapter: CommentsAdapter

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

        memory = intent.getParcelableExtra("memoryObject")!!
        memory.lovesCount = intent.getLongExtra("lovesCount", 0)
        memory.commentsCount = intent.getLongExtra("commentsCount", 0)
        memory.memoryID = intent.getStringExtra("memoryID")!!
        user = intent.getParcelableExtra("userObject")!!
        isFollow = intent.getStringExtra("isFollow")!!
        // reset when phone rotate ,should solve it
        //viewModel.isReact = intent.getBooleanExtra("isReact", false)
        initWidget()
        updateMemory()
        setupReact()
        setupMemory()
        getComments()
    }

    private fun setupReact() {
        viewModel.getUserReact(memoryID = memory.memoryID).observe(this, Observer {
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
        viewModel.getComments(memoryID = memory.memoryID).observe(this, Observer { querySnapShot ->
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

    private fun getUser(userID: String) {
        viewModel.getUser(userID = userID).observe(this, Observer { querySnapShot ->
            if (querySnapShot != null) {
                for (document in querySnapShot) {
                    val user = User(
                        email = document["email"] as String,
                        photoURL = document["photoURL"] as String,
                        username = document["username"] as String,
                        userId = document["userId"] as String
                    )
                    viewModel.usersList.add(user)
                    commentsAdapter.notifyDataSetChanged()
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
                    memoryID = memory.memoryID,
                    userID = FirebaseAuth.getInstance().uid!!,
                    commentContent = binding.commentInclude.commentET.text.trim().toString(),
                    date = dateInString,
                    timestamp = System.currentTimeMillis()
                )
                viewModel.makeComment(comment)
                binding.commentInclude.commentET.text.clear()
            }
        }
        binding.memoryInclude.userIV.setOnClickListener {
            goToProfile(
                context = this,
                user = user
            )
        }
        binding.memoryInclude.usernameTV.setOnClickListener {
            goToProfile(
                context = this,
                user = user
            )
        }
        binding.memoryInclude.loveButton.setOnClickListener {
            if (viewModel.isReact) {
                viewModel.deleteReact(memoryID = memory.memoryID)
                viewModel.isReact = false
                binding.memoryInclude.loveButton.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.ic_dislove,
                    0,
                    0,
                    0
                )
            } else {
                viewModel.makeReact(memoryID = memory.memoryID)
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
    private fun Date.toString(format: String, locale: Locale = Locale.getDefault()): String {
        val formatter = SimpleDateFormat(format, locale)
        return formatter.format(this)
    }

    /** ------------------------------- Get current date -------------------------**/
    private fun getCurrentDateTime(): Date {
        return Calendar.getInstance().time
    }

    private fun setupMemory() {
        Picasso.get().load(user.photoURL).into(binding.memoryInclude.userIV)
        binding.memoryInclude.usernameTV.text = user.username
        binding.memoryInclude.memoryTV.text = memory.memory
        binding.memoryInclude.dateTV.text = memory.date
    }

    private fun updateMemory() {
        viewModel.getMemory(memory.memoryID).observe(this, Observer {
            if (!it.isEmpty) {
                for (document in it) {
                    val commentsText =
                        (document["commentsCount"] as Long).toString() + " " + this.resources.getText(
                            R.string.comments
                        )
                    binding.memoryInclude.lovesTV.text = (document["lovesCount"] as Long).toString()
                    binding.memoryInclude.commentsCountTV.text = commentsText
                }
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