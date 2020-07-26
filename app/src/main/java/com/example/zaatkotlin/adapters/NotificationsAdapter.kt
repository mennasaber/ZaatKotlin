package com.example.zaatkotlin.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.zaatkotlin.R
import com.example.zaatkotlin.activities.MemoryActivity
import com.example.zaatkotlin.databinding.NotificationItemBinding
import com.example.zaatkotlin.viewmodels.NotificationsViewModel
import com.google.firebase.auth.FirebaseAuth
import com.squareup.picasso.Picasso

class NotificationsAdapter(
    val viewModel: NotificationsViewModel,
    val context: Context?
) :
    RecyclerView.Adapter<NotificationsAdapter.NotificationViewHolder>() {
    class NotificationViewHolder(val binding: NotificationItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        return NotificationViewHolder(
            NotificationItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return viewModel.notificationsList.count()
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        holder.apply {
            if (!viewModel.notificationsList[position].seen)
                binding.root.setBackgroundColor(
                    ContextCompat.getColor(
                        holder.binding.root.context,
                        R.color.colorLightOrange
                    )
                )
            binding.dateTV.text = viewModel.notificationsList[position].date
            Picasso.get()
                .load(viewModel.usersList[viewModel.notificationsList[position].notificationID]?.photoURL)
                .into(binding.userIV)
            var message = context?.resources!!.getString(R.string.notification_react)
            if (viewModel.notificationsList[position].type == 1L)
                message = context.resources!!.getString(R.string.notification_comment)
            binding.messageTV.text = (viewModel.notificationsList[position].message + " " + message)
        }
        holder.binding.root.setOnClickListener {
            val intent = Intent(holder.binding.root.context, MemoryActivity::class.java)
            intent.putExtra("memoryID", viewModel.notificationsList[position].memoryID)
            intent.putExtra("userID", FirebaseAuth.getInstance().uid)
            holder.binding.root.context.startActivity(intent)
            viewModel.updateNotification(viewModel.notificationsList[position].notificationID)
        }
    }
}