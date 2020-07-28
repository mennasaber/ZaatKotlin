package com.example.zaatkotlin.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.zaatkotlin.datalisteners.FirebaseQueryLiveData
import com.example.zaatkotlin.models.Notification
import com.example.zaatkotlin.models.User
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class NotificationsViewModel : ViewModel() {
    var notificationsList = ArrayList<Notification>()
    var usersList = hashMapOf<String, User>()
    fun getNotifications(userID: String): LiveData<QuerySnapshot> {
        val query = Firebase.firestore.collection("Notifications").whereEqualTo("userID", userID)
        return FirebaseQueryLiveData(query)
    }

    fun getUser(userID: String): LiveData<QuerySnapshot> {
        val query = Firebase.firestore.collection("Users").whereEqualTo("userId", userID)
        return FirebaseQueryLiveData(query)
    }

    fun updateNotification(notificationID: String) {
        Firebase.firestore.collection("Notifications").document(notificationID).update("seen", true)
    }
}