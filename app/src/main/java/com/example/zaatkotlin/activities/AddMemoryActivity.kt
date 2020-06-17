package com.example.zaatkotlin.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import androidx.activity.viewModels
import androidx.core.widget.addTextChangedListener
import com.example.zaatkotlin.R
import com.example.zaatkotlin.models.Memory
import com.example.zaatkotlin.viewmodels.AddMemoryViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.*

class AddMemoryActivity : AppCompatActivity() {
    lateinit var saveButton: ImageView
    lateinit var backButton: ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_memory)
        initWidget()
    }

    // ------------------------------ initialization ----------------------------
    private fun initWidget() {
        val viewModel: AddMemoryViewModel by viewModels()
        saveButton = findViewById(R.id.saveMemory)
        backButton = findViewById(R.id.back)
        val titleET = findViewById<EditText>(R.id.titleET)
        val memoryET = findViewById<EditText>(R.id.memoryET)
        titleET.setText(viewModel.title)
        memoryET.setText(viewModel.content)

        backButton.setOnClickListener {
            finish()
        }
        saveButton.setOnClickListener {

            if (titleET.text.toString().trim() != "" && memoryET.text.toString().trim() != "") {
                val memoryObject = initMemoryObject(
                    titleET.text.toString().trim(),
                    memoryET.text.toString().trim()
                )
                saveMemoryInFireStore(memoryObject)
            }
        }
        titleET.addTextChangedListener { viewModel.title = titleET.text.toString() }
        memoryET.addTextChangedListener { viewModel.content = memoryET.text.toString() }
    }

    // ------------------------------ Save Memory in FireStore ------------------
    private fun saveMemoryInFireStore(memoryObject: Memory) {
        val db = Firebase.firestore
        memoryObject.memoryID = db.collection("Memories").document().id
        db.collection("counter").document("value").get().addOnSuccessListener { document ->
            if (document != null) {
                memoryObject.index = document.data!!["last"] as Long
                db.collection("counter").document("value").update("last", memoryObject.index + 1)
                db.collection("Memories").document(memoryObject.memoryID).set(memoryObject)
                finish()
            }
        }


    }

    // ------------------------------ Set Memory data ---------------------------
    private fun initMemoryObject(title: String, memory: String): Memory {
        val date = getCurrentDateTime()
        val dateInString = date.toString("yyyy-MM-dd HH:mm")
        return Memory(
            title = title,
            memory = memory,
            date = dateInString,
            uID = FirebaseAuth.getInstance().uid.toString()
        )
    }

    // ------------------------------ Convert date to specific format 'extension function' ---------
    private fun Date.toString(format: String, locale: Locale = Locale.getDefault()): String {
        val formatter = SimpleDateFormat(format, locale)
        return formatter.format(this)
    }

    // ------------------------------- Get current date -------------------------
    private fun getCurrentDateTime(): Date {
        return Calendar.getInstance().time
    }
}