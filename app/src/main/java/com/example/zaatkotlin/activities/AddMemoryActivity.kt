package com.example.zaatkotlin.activities

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.example.zaatkotlin.R
import com.example.zaatkotlin.databinding.ActivityAddMemoryBinding
import com.example.zaatkotlin.databinding.LayoutTopAddToolbarBinding
import com.example.zaatkotlin.models.Memory
import com.example.zaatkotlin.viewmodels.AddMemoryViewModel
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.*

class AddMemoryActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding: ActivityAddMemoryBinding
    private lateinit var toolbarBinding: LayoutTopAddToolbarBinding
    private val viewModel: AddMemoryViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddMemoryBinding.inflate(layoutInflater)
        toolbarBinding = LayoutTopAddToolbarBinding.bind(binding.root)
        setContentView(binding.root)
        initWidget()
    }

    /** ------------------------------ initialization ----------------------------**/
    private fun initWidget() {
        binding.titleET.setText(viewModel.title)
        binding.memoryET.setText(viewModel.content)
        binding.makeMemoryPublicCB.isChecked = viewModel.isSharing

        toolbarBinding.back.setOnClickListener(this)
        toolbarBinding.saveMemory.setOnClickListener(this)

        binding.titleET.addTextChangedListener { viewModel.title = binding.titleET.text.toString() }
        binding.memoryET.addTextChangedListener {
            viewModel.content = binding.memoryET.text.toString()
        }
        binding.makeMemoryPublicCB.setOnCheckedChangeListener { _, _ ->
            viewModel.isSharing = binding.makeMemoryPublicCB.isChecked
        }
    }

    /** ------------------------------ call viewModel function to add the memory ------------------**/
    private fun saveMemoryInFireStore(memoryObject: Memory) {
        viewModel.addMemory(memoryObject)
        finish()
    }

    /** ------------------------------ Set Memory data ---------------------------**/
    private fun initMemoryObject(title: String, memory: String, isSharing: Boolean): Memory {
        val date = getCurrentDateTime()
        val dateInString = date.toString("K:mm a dd-MM-yyyy")
        return Memory(
            title = title,
            memory = memory,
            date = dateInString,
            uID = FirebaseAuth.getInstance().uid.toString(),
            isSharing = isSharing
        )
    }

    /** ------------------------------ Convert date to specific format 'extension function' ---------**/
    private fun Date.toString(format: String, locale: Locale = Locale.getDefault()): String {
        val formatter = SimpleDateFormat(format, Locale.US)
        return formatter.format(this)
    }

    /** ------------------------------- Get current date -------------------------**/
    private fun getCurrentDateTime(): Date {
        return Calendar.getInstance().time
    }

    /** ------------------------------ setup Click listener ----------------------**/
    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.back -> {
                finish()
            }
            R.id.saveMemory -> {
                if (isValid()) {
                    val memoryObject = initMemoryObject(
                        binding.titleET.text.toString().trim(),
                        binding.memoryET.text.toString().trim(),
                        binding.makeMemoryPublicCB.isChecked
                    )
                    memoryObject.timestamp = System.currentTimeMillis()
                    saveMemoryInFireStore(memoryObject)
                }
            }
        }
    }

    /** -------------- make sure of memory title or content empty ----------------**/
    private fun isValid(): Boolean {
        return binding.memoryET.text.toString().trim() != "" && binding.titleET.text.toString()
            .trim() != ""
    }
}