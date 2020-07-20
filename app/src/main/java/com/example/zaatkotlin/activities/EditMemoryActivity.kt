package com.example.zaatkotlin.activities

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.zaatkotlin.R
import com.example.zaatkotlin.databinding.ActivityEditMemoryBinding
import com.example.zaatkotlin.databinding.LayoutTopEditToolbarBinding
import com.example.zaatkotlin.viewmodels.MemoriesViewModel

class EditMemoryActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var memoryID: String
    private lateinit var binding: ActivityEditMemoryBinding
    private lateinit var toolbarBinding: LayoutTopEditToolbarBinding
    private val viewModel: MemoriesViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditMemoryBinding.inflate(layoutInflater)
        toolbarBinding = LayoutTopEditToolbarBinding.bind(binding.root)
        setContentView(binding.root)

        memoryID = intent.getStringExtra("memoryID")!!
        val memoryTitle = intent.getStringExtra("title")
        val memoryContent = intent.getStringExtra("content")
        val memorySharing = intent.getBooleanExtra("isSharing", false)

        initWidget(memoryTitle, memoryContent, memorySharing)
    }

    /** ------------------------------ initialization ----------------------------**/
    private fun initWidget(memoryTitle: String?, memoryContent: String?, isSharing: Boolean) {
        memoryTitle?.let { binding.titleET.setText(memoryTitle) }
        memoryContent?.let { binding.memoryET.setText(memoryContent) }
        binding.makeMemoryPublicCB.isChecked = isSharing

        toolbarBinding.back.setOnClickListener(this)
        toolbarBinding.saveMemory.setOnClickListener(this)
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
        return binding.memoryET.text.toString().trim() != "" && binding.titleET.text.toString()
            .trim() != ""
    }

    /** -------------- update memory data by call viewModel and pass new data ----------------**/
    private fun updateMemory() {
        viewModel.updateMemory(
            memoryID = memoryID,
            memory = binding.memoryET.text.toString(),
            title = binding.titleET.text.toString(),
            isSharing = binding.makeMemoryPublicCB.isChecked
        )
        finish()
    }

}