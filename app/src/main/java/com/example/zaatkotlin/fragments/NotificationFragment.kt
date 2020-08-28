package com.example.zaatkotlin.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.zaatkotlin.adapters.NotificationsAdapter
import com.example.zaatkotlin.databinding.FragmentNotificationBinding
import com.example.zaatkotlin.models.Notification
import com.example.zaatkotlin.models.User
import com.example.zaatkotlin.viewmodels.NotificationsViewModel
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.*

class NotificationFragment : Fragment() {
    lateinit var binding: FragmentNotificationBinding
    val viewModel: NotificationsViewModel by viewModels()
    lateinit var notificationsAdapter: NotificationsAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNotificationBinding.inflate(inflater, container, false)
        binding.Progress.visibility = View.VISIBLE
        binding.notificationRecyclerView.visibility = View.INVISIBLE
        getNotifications()
        // initWidget()
        return binding.root
    }

    private fun initWidget() {
        notificationsAdapter = NotificationsAdapter(
            viewModel = viewModel, context = context
        )
        binding.notificationRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.notificationRecyclerView.adapter = notificationsAdapter
    }

    private fun getNotifications() {
        viewModel.getNotifications(FirebaseAuth.getInstance().uid!!)
            .observe(viewLifecycleOwner, Observer { querySnapShot ->
                if (querySnapShot != null) {
                    for (document in querySnapShot) {
                        val notification = Notification(
                            userID = document["userID"] as String,
                            senderID = document["senderID"] as String,
                            message = document["message"] as String,
                            seen = document["seen"] as Boolean,
                            memoryID = document["memoryID"] as String,
                            date = document["date"] as String,
                            type = document["type"] as Long,
                            timestamp = document["timestamp"] as Long
                        )
                        notification.date = convertDate(notification.date)
                        notification.notificationID = document["notificationID"] as String
                        val tempNoti =
                            viewModel.notificationsList.find { it.notificationID == notification.notificationID }
                        if (tempNoti != null) {
                            viewModel.notificationsList.remove(tempNoti)
                        } else {
                            getUser(notification.senderID, notification.notificationID)
                        }
                        viewModel.notificationsList.add(notification)
                        viewModel.notificationsList.sortByDescending { it.timestamp }
                    }
                    initWidget()
                }
                binding.Progress.visibility = View.GONE
                binding.notificationRecyclerView.visibility = View.VISIBLE
            })
    }

    private fun convertDate(date: String): String {
        val formatterDefault = SimpleDateFormat("K:mm a dd-MM-yyyy", Locale.US)
        val dateTemp = formatterDefault.parse(date)
        val formatter = SimpleDateFormat("K:mm a dd-MM-yyyy", Locale.getDefault())
        return formatter.format(dateTemp!!)
    }

    private fun getUser(senderID: String, notificationID: String) {
        viewModel.getUser(senderID).observe(viewLifecycleOwner, Observer { querySnapShot ->
            if (querySnapShot != null) {
                for (document in querySnapShot) {
                    val user = User(
                        email = document["email"] as String,
                        photoURL = document["photoURL"] as String,
                        username = document["username"] as String,
                        userId = document["userId"] as String
                    )
                    viewModel.usersList[notificationID] = user
                }
            }
        })
    }
}