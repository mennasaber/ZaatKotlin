package com.example.zaatkotlin.activities

import android.os.Bundle
import android.view.View
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.example.zaatkotlin.R
import com.example.zaatkotlin.models.Memory
import com.example.zaatkotlin.viewmodels.AddMemoryViewModel
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.*

class AddMemoryActivity : AppCompatActivity(), View.OnClickListener {
    lateinit var saveButton: ImageView
    private lateinit var backButton: ImageView
    private lateinit var makeMemoryPublicCB: CheckBox
    private lateinit var titleET: EditText
    private lateinit var memoryET: EditText

    private val viewModel: AddMemoryViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_memory)
        initWidget()
    }

    /** ------------------------------ initialization ----------------------------**/
    private fun initWidget() {

        saveButton = findViewById(R.id.saveMemory)
        backButton = findViewById(R.id.back)
        makeMemoryPublicCB = findViewById(R.id.makeMemoryPublicCB)
        titleET = findViewById<EditText>(R.id.titleET)
        memoryET = findViewById<EditText>(R.id.memoryET)

        titleET.setText(viewModel.title)
        memoryET.setText(viewModel.content)
        makeMemoryPublicCB.isChecked = viewModel.isSharing

        backButton.setOnClickListener(this)
        saveButton.setOnClickListener(this)

        titleET.addTextChangedListener { viewModel.title = titleET.text.toString() }
        memoryET.addTextChangedListener { viewModel.content = memoryET.text.toString() }
        makeMemoryPublicCB.setOnCheckedChangeListener { _, _ ->
            viewModel.isSharing = makeMemoryPublicCB.isChecked
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
        val dateInString = date.toString("yyyy-MM-dd HH:mm")
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
        val formatter = SimpleDateFormat(format, locale)
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
                        titleET.text.toString().trim(),
                        memoryET.text.toString().trim(),
                        makeMemoryPublicCB.isChecked
                    )
                    memoryObject.timestamp = System.currentTimeMillis()
                    saveMemoryInFireStore(memoryObject)
                }
            }
        }
    }

    /** -------------- make sure of memory title or content empty ----------------**/
    private fun isValid(): Boolean {
        return memoryET.text.toString().trim() != "" && titleET.text.toString().trim() != ""
    }
}