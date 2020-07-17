package com.example.zaatkotlin.activities

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.zaatkotlin.R
import com.example.zaatkotlin.models.Memory
import com.example.zaatkotlin.models.User
import com.squareup.picasso.Picasso

class MemoryActivity : AppCompatActivity() {
    lateinit var memory: Memory
    lateinit var user: User
    lateinit var isFollow: String

    lateinit var memoryTV: TextView
    lateinit var usernameTV: TextView
    lateinit var dateTV: TextView
    lateinit var lovesTV: TextView
    lateinit var userIV: ImageView
    lateinit var loveB: Button
    lateinit var commentB: Button
    lateinit var shareB: Button
    lateinit var commentET: EditText
    lateinit var sendCommentB: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_memory)
        memory = intent.getParcelableExtra("memoryObject")!!
        memory.lovesCount = intent.getLongExtra("lovesCount", 0)
        memory.memoryID = intent.getStringExtra("memoryID")!!
        user = intent.getParcelableExtra("userObject")!!
        isFollow = intent.getStringExtra("isFollow")!!
        initWidget()
        setupMemory()
    }

    private fun initWidget() {
        userIV = findViewById(R.id.userIV)
        usernameTV = findViewById(R.id.usernameTV)
        memoryTV = findViewById(R.id.memoryTV)
        lovesTV = findViewById(R.id.lovesTV)
        dateTV = findViewById(R.id.dateTV)

        loveB = findViewById(R.id.loveButton)
        commentB = findViewById(R.id.commentButton)
        shareB = findViewById(R.id.shareButton)

        commentET = findViewById(R.id.commentET)
        sendCommentB = findViewById(R.id.sendCommentB)

        sendCommentB.setOnClickListener {
            if (commentET.text.isNotBlank()) {
            }
        }
    }

    private fun setupMemory() {
        Picasso.get().load(user.photoURL).into(userIV)
        usernameTV.text = user.username
        memoryTV.text = memory.memory
        dateTV.text = memory.date
        Toast.makeText(this, memory.memoryID, Toast.LENGTH_SHORT).show()
        lovesTV.text = memory.lovesCount.toString()
    }
}