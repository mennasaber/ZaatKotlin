package com.example.zaatkotlin.activities

import android.os.Bundle
import android.view.View
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.zaatkotlin.R
import com.example.zaatkotlin.viewmodels.MemoriesViewModel

class EditMemoryActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var memoryID: String
    private lateinit var titleET: EditText
    private lateinit var memoryET: EditText
    private lateinit var memorySharingCB: CheckBox
    private val viewModel: MemoriesViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_memory)

        memoryID = intent.getStringExtra("memoryID")!!
        val memoryTitle = intent.getStringExtra("title")
        val memoryContent = intent.getStringExtra("content")
        val memorySharing = intent.getBooleanExtra("isSharing", false)

        initWidget(memoryTitle, memoryContent, memorySharing)
    }

    /** ------------------------------ initialization ----------------------------**/
    private fun initWidget(memoryTitle: String?, memoryContent: String?, isSharing: Boolean) {
        titleET = findViewById(R.id.titleET)
        memoryET = findViewById(R.id.memoryET)
        memorySharingCB = findViewById(R.id.makeMemoryPublicCB)
        val backButton = findViewById<ImageView>(R.id.back)
        val saveButton = findViewById<ImageView>(R.id.saveMemory)

        memoryTitle?.let { titleET.setText(memoryTitle) }
        memoryContent?.let { memoryET.setText(memoryContent) }
        memorySharingCB.isChecked = isSharing

        backButton.setOnClickListener(this)
        saveButton.setOnClickListener(this)
    }

    /** ------------------------------ setup Click listener ----------------------**/
    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.back -> {
                finish()
            }
            R.id.saveMemory -> {
                if (isValid()) {
                    updateMemory()
                }
            }
        }
    }

    /** -------------- make sure of memory title or content empty ----------------**/
    private fun isValid(): Boolean {
        return memoryET.text.toString().trim() != "" && titleET.text.toString().trim() != ""
    }

    /** -------------- update memory data by call viewModel and pass new data ----------------**/
    private fun updateMemory() {
        viewModel.updateMemory(
            memoryID = memoryID,
            memory = memoryET.text.toString(),
            title = titleET.text.toString(),
            isSharing = memorySharingCB.isChecked
        )
        finish()
    }

}