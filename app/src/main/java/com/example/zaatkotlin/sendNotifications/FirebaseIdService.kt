package com.example.zaatkotlin.sendNotifications

import android.util.Log
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessagingService

class FirebaseIdService : FirebaseMessagingService() {
    override fun onNewToken(s: String) {
        super.onNewToken(s)
        FirebaseInstanceId.getInstance().instanceId
            .addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.d("TAG", "getInstanceId failed")
                    return@OnCompleteListener
                }

                // Get new Instance ID token
                val token = task.result?.token
                val tokenObject = Token(token!!)
                Firebase.firestore.collection("Token").document(FirebaseAuth.getInstance().uid!!)
                    .set(tokenObject)
                Log.d("TAG", "saveUserDataInFireStore: $token")
            })
    }
}