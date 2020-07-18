package com.example.zaatkotlin.activities

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.zaatkotlin.R
import com.example.zaatkotlin.adapters.CommentsAdapter
import com.example.zaatkotlin.models.Comment
import com.example.zaatkotlin.models.Memory
import com.example.zaatkotlin.models.User
import com.example.zaatkotlin.viewmodels.MemoryViewModel
import com.google.firebase.auth.FirebaseAuth
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.util.*

class MemoryActivity : AppCompatActivity() {
    lateinit var memory: Memory
    lateinit var user: User
    lateinit var isFollow: String
    var isReact: Boolean = false

    lateinit var memoryTV: TextView
    lateinit var usernameTV: TextView
    lateinit var dateTV: TextView
    lateinit var lovesTV: TextView
    lateinit var commentsTV: TextView
    lateinit var userIV: ImageView
    lateinit var loveB: Button
    lateinit var commentB: Button
    lateinit var shareB: Button
    lateinit var commentET: EditText
    lateinit var sendCommentB: Button
    lateinit var recyclerView: RecyclerView
    lateinit var commentsAdapter: CommentsAdapter
    val viewModel: MemoryViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_memory)

        memory = intent.getParcelableExtra("memoryObject")!!
        memory.lovesCount = intent.getLongExtra("lovesCount", 0)
        memory.commentsCount = intent.getLongExtra("commentsCount", 0)
        memory.memoryID = intent.getStringExtra("memoryID")!!
        user = intent.getParcelableExtra("userObject")!!
        isFollow = intent.getStringExtra("isFollow")!!
        isReact = intent.getBooleanExtra("isReact", false)

        initWidget()
        setupReact()
        setupMemory()
        getComments()
    }

    private fun setupReact() {
        if (isReact) {
            loveB.setCompoundDrawablesWithIntrinsicBounds(
                R.drawable.ic_love,
                0,
                0,
                0
            )
        } else {
            loveB.setCompoundDrawablesWithIntrinsicBounds(
                R.drawable.ic_dislove,
                0,
                0,
                0
            )
        }
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
        userIV = findViewById(R.id.userIV)
        usernameTV = findViewById(R.id.usernameTV)
        memoryTV = findViewById(R.id.memoryTV)
        lovesTV = findViewById(R.id.lovesTV)
        commentsTV = findViewById(R.id.commentsCountTV)
        dateTV = findViewById(R.id.dateTV)

        loveB = findViewById(R.id.loveButton)
        commentB = findViewById(R.id.commentButton)
        shareB = findViewById(R.id.shareButton)

        commentET = findViewById(R.id.commentET)
        sendCommentB = findViewById(R.id.sendCommentB)

        commentsAdapter =
            CommentsAdapter(commentsList = viewModel.commentsList, usersList = viewModel.usersList)

        recyclerView = findViewById(R.id.commentsRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = commentsAdapter

        sendCommentB.setOnClickListener {
            if (commentET.text.isNotBlank()) {
                val date = getCurrentDateTime()
                val dateInString = date.toString("K:mm a dd-MM-yyyy")
                val comment = Comment(
                    memoryID = memory.memoryID,
                    userID = FirebaseAuth.getInstance().uid!!,
                    commentContent = commentET.text.trim().toString(),
                    date = dateInString,
                    timestamp = System.currentTimeMillis()
                )
                viewModel.makeComment(comment)
                commentET.text.clear()
            }
        }
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
        val commentsText =
            memory.commentsCount.toString() + " " + this.resources.getText(R.string.comments)
        Picasso.get().load(user.photoURL).into(userIV)
        usernameTV.text = user.username
        memoryTV.text = memory.memory
        dateTV.text = memory.date
        lovesTV.text = memory.lovesCount.toString()
        commentsTV.text = commentsText
    }
}