package com.example.zaatkotlin.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.zaatkotlin.datalisteners.FirebaseQueryLiveData
import com.example.zaatkotlin.models.Follow
import com.example.zaatkotlin.models.Memory
import com.example.zaatkotlin.models.Notification
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class OtherProfileViewModel : ViewModel() {
    var isFollow: Boolean = true
    var memoriesList = ArrayList<Memory>()
    val reactMap = hashMapOf<String, Boolean>()
    val userID = FirebaseAuth.getInstance().uid!!

    fun isFollow(userID: String): LiveData<QuerySnapshot> {
        val query = Firebase.firestore.collection("Follow").whereEqualTo("followingId", userID)
            .whereEqualTo("followerId", this.userID)
        return FirebaseQueryLiveData(query = query)
    }

    fun getMemories(userID: String): LiveData<QuerySnapshot> {
        val query = Firebase.firestore.collection("Memories")
            .whereEqualTo("uid", userID)
            .whereEqualTo("sharing", true)
        return FirebaseQueryLiveData(query)
    }

    fun deleteFollow(userID: String) {
        val query = Firebase.firestore.collection("Follow")
        query.whereEqualTo("followingId", userID).whereEqualTo("followerId", this.userID).get()
            .addOnSuccessListener { querySnapshot ->
                for (document in querySnapshot) {
                    query.document(document.id).delete()
                }
            }
    }

    fun makeFollow(userID: String) {
        val query = Firebase.firestore.collection("Follow")
        val follow = Follow()
        follow.followerId = this.userID
        follow.followingId = userID
        val id = follow.followerId + follow.followingId
        query.document(id).set(follow)
    }

    fun makeReact(memoryID: String) {
        val dataMap = hashMapOf<String, String>()
        dataMap["userID"] = userID
        dataMap["memoryID"] = memoryID
        Firebase.firestore.collection("Reacts").document(userID + memoryID).set(dataMap)
            .addOnSuccessListener {
                increaseReactsCount(memoryID)
            }
    }

    fun deleteReact(memoryID: String) {
        Firebase.firestore.collection("Reacts").document(userID + memoryID).delete()
            .addOnSuccessListener {
                decreaseReactsCount(memoryID)
            }
    }

    fun getUserReact(memoryID: String): LiveData<QuerySnapshot> {
        val query = Firebase.firestore.collection("Reacts").whereEqualTo("userID", userID)
            .whereEqualTo("memoryID", memoryID)
        return FirebaseQueryLiveData(query)
    }

    private fun increaseReactsCount(memoryID: String) {
        Firebase.firestore.collection("Reacts").whereEqualTo("memoryID", memoryID).get()
            .addOnSuccessListener {
                Firebase.firestore.collection("Memories").document(memoryID)
                    .update("lovesCount", it.count()).addOnCompleteListener {
                        reactMap[memoryID] = true
                    }
            }
    }

    private fun decreaseReactsCount(memoryID: String) {
        Firebase.firestore.collection("Reacts").whereEqualTo("memoryID", memoryID).get()
            .addOnSuccessListener {
                Firebase.firestore.collection("Memories").document(memoryID)
                    .update("lovesCount", it.count()).addOnCompleteListener {
                        reactMap[memoryID] = false
                    }
            }
    }

    fun addNotification(notification: Notification) {
        Log.d("TAG", "addNotification: ")
        val db = Firebase.firestore
        notification.notificationID = db.collection("Notifications").document().id
        db.collection("Notifications").document(notification.notificationID).set(notification)
    }
}