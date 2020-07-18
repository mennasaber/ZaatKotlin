package com.example.zaatkotlin.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.zaatkotlin.datalisteners.FirebaseQueryLiveData
import com.example.zaatkotlin.models.Comment
import com.example.zaatkotlin.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class MemoryViewModel : ViewModel() {
    val userID = FirebaseAuth.getInstance().uid!!
    var commentsList = ArrayList<Comment>()
    var usersList = ArrayList<User>()
    var isReact = false
    fun getComments(memoryID: String): LiveData<QuerySnapshot> {
        val query = Firebase.firestore.collection("Comments").whereEqualTo("memoryID", memoryID)
        return FirebaseQueryLiveData(query = query)
    }

    fun getUser(userID: String): LiveData<QuerySnapshot> {
        val query = Firebase.firestore.collection("Users").whereEqualTo("userId", userID)
        return FirebaseQueryLiveData(query = query)
    }

    fun makeComment(comment: Comment) {
        comment.commentID = Firebase.firestore.collection("Comments").document().id
        Firebase.firestore.collection("Comments").document(comment.commentID).set(comment)
        increaseCommentsCount(comment.memoryID)
    }

    fun getMemory(memoryID: String): LiveData<QuerySnapshot> {
        val query = Firebase.firestore.collection("Memories").whereEqualTo("memoryID", memoryID)
        return FirebaseQueryLiveData(query)
    }

    private fun increaseCommentsCount(memoryID: String) {
        Firebase.firestore.collection("Memories").document(memoryID).get().addOnSuccessListener {
            Firebase.firestore.collection("Memories").document(memoryID)
                .update("commentsCount", it["commentsCount"] as Long + 1)
        }
    }

    fun makeReact(memoryID: String) {
        val dataMap = hashMapOf<String, String>()
        dataMap["userID"] = userID
        dataMap["memoryID"] = memoryID
        Firebase.firestore.collection("Reacts").document(userID + memoryID).set(dataMap)
        increaseReactsCount(memoryID)
    }

    fun deleteReact(memoryID: String) {
        Firebase.firestore.collection("Reacts").document(userID + memoryID).delete()
        decreaseReactsCount(memoryID)
    }

    fun getUserReact(memoryID: String): LiveData<QuerySnapshot> {
        val query = Firebase.firestore.collection("Reacts").whereEqualTo("userID", userID)
            .whereEqualTo("memoryID", memoryID)
        return FirebaseQueryLiveData(query)
    }

    private fun increaseReactsCount(memoryID: String) {
        Firebase.firestore.collection("Memories").document(memoryID).get().addOnSuccessListener {
            Firebase.firestore.collection("Memories").document(memoryID)
                .update("lovesCount", it["lovesCount"] as Long + 1)
        }
    }

    private fun decreaseReactsCount(memoryID: String) {
        Firebase.firestore.collection("Memories").document(memoryID).get().addOnSuccessListener {
            Firebase.firestore.collection("Memories").document(memoryID)
                .update("lovesCount", it["lovesCount"] as Long - 1)
        }
    }


}