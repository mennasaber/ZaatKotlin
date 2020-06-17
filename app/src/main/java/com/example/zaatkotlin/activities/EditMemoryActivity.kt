package com.example.zaatkotlin.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import com.example.zaatkotlin.R

class EditMemoryActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_memory)
        val memoryTitle = intent.getStringExtra("title")
        val memoryContent = intent.getStringExtra("content")
        initWidget(memoryTitle, memoryContent)
    }

    // ------------------------------ initialization ----------------------------
    private fun initWidget(memoryTitle: String?, memoryContent: String?) {
        val titleET = findViewById<EditText>(R.id.titleET)
        val memoryET = findViewById<EditText>(R.id.memoryET)
        memoryTitle?.let { titleET.setText(memoryTitle) }
        memoryContent?.let { memoryET.setText(memoryContent) }
        val backButton = findViewById<ImageView>(R.id.back)
        backButton.setOnClickListener {
            finish()
        }
    }
}