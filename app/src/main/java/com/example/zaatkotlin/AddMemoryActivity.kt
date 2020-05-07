package com.example.zaatkotlin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import com.example.zaatkotlin.models.Memory
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
        saveButton = findViewById(R.id.saveMemory)
        backButton = findViewById(R.id.back)
        backButton.setOnClickListener { finish() }
        saveButton.setOnClickListener {
            val titleET = findViewById<EditText>(R.id.titleET)
            val memoryET = findViewById<EditText>(R.id.memoryET)
            if (titleET.text.toString().trim() != "" && memoryET.text.toString().trim() != "") {
                val memoryObject = initMemoryObject(titleET.text.toString().trim(), memoryET.text.toString().trim())
                saveMemoryInFireStore(memoryObject)
            }
        }
    }

    // ------------------------------ Save Memory in FireStore ------------------
    private fun saveMemoryInFireStore(memoryObject: Memory) {
        val db = Firebase.firestore
        memoryObject.memoryID = db.collection("Memories").document().id
        db.collection("Memories").document(memoryObject.memoryID).set(memoryObject)
        finish()
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