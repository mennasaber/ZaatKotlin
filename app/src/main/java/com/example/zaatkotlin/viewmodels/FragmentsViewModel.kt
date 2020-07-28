package com.example.zaatkotlin.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.zaatkotlin.datalisteners.FirebaseQueryLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class FragmentsViewModel : ViewModel() {
    var fragmentID: String = "home"
    var notificationsCount = 0
    val user = FirebaseAuth.getInstance().currentUser
    fun getNotificationsCount(): LiveData<QuerySnapshot> {
        val query = Firebase.firestore.collection("Notifications").whereEqualTo("userID", user?.uid)
            .whereEqualTo("seen", false)
        return FirebaseQueryLiveData(query)
    }
}