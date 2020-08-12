package com.example.zaatkotlin.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.zaatkotlin.R
import com.example.zaatkotlin.activities.LovesActivity
import com.example.zaatkotlin.activities.MemoryActivity
import com.example.zaatkotlin.activities.OtherProfileActivity
import com.example.zaatkotlin.databinding.WorldItemBinding
import com.example.zaatkotlin.models.Memory
import com.example.zaatkotlin.models.Notification
import com.example.zaatkotlin.models.User
import com.example.zaatkotlin.sendNotifications.*
import com.example.zaatkotlin.viewmodels.WorldViewModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import retrofit2.Call
import retrofit2.Callback
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class WorldAdapter(
    private val memoriesList: ArrayList<Memory>,
    val usersList: ArrayList<User>,
    val viewModel: WorldViewModel?
) :
    RecyclerView.Adapter<WorldAdapter.WorldViewHolder>() {
    private lateinit var api: APIService

    class WorldViewHolder(val binding: WorldItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorldViewHolder {
        return WorldViewHolder(
            WorldItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int {
        return memoriesList.count()
    }

    override fun onBindViewHolder(holder: WorldViewHolder, position: Int) {
        val commentsText =
            memoriesList[position].commentsCount.toString() + " " + holder.itemView.resources.getText(
                R.string.comments
            )
        val user = usersList.find { it.userId == memoriesList[position].uID }
        holder.binding.usernameTV.text = user?.username
        holder.binding.progressBar.visibility = View.VISIBLE
        Picasso.get().load(user?.photoURL).into(holder.binding.userIV,
            object : com.squareup.picasso.Callback {
                override fun onSuccess() {
                    holder.binding.progressBar.visibility = View.INVISIBLE
                }

                override fun onError(e: Exception?) {
                    TODO("Not yet implemented")
                }

            })
        holder.binding.memoryTV.text = memoriesList[position].memory
        holder.binding.dateTV.text = memoriesList[position].date
        holder.binding.lovesTV.text = memoriesList[position].lovesCount.toString()
        holder.binding.commentsCountTV.text = commentsText

        if (viewModel != null && viewModel.reactMap.size == memoriesList.size)
            if (viewModel.reactMap[memoriesList[position].memoryID]!!) {
                holder.binding.loveButton.setCompoundDrawablesRelativeWithIntrinsicBounds(
                    R.drawable.ic_love,
                    0,
                    0,
                    0
                )
            } else {
                holder.binding.loveButton.setCompoundDrawablesRelativeWithIntrinsicBounds(
                    R.drawable.ic_dislove,
                    0,
                    0,
                    0
                )
            }

        holder.binding.loveButton.setOnClickListener {
            if (viewModel != null) {
                if (viewModel.reactMap[memoriesList[position].memoryID]!!) {
                    viewModel.deleteReact(memoriesList[position].memoryID)
                } else {
                    viewModel.makeReact(memoriesList[position].memoryID)
                    val message = viewModel.currentUser.username
                    sendNotification(
                        message!!,
                        memoriesList[position].memoryID,
                        ownerMemoryID = memoriesList[position].uID,
                        type = "0"
                    )
                    saveNotificationToDB(
                        Notification(
                            userID = memoriesList[position].uID,
                            senderID = viewModel.userID,
                            message = message,
                            seen = false,
                            memoryID = memoriesList[position].memoryID,
                            date = getCurrentDateTime().toString("K:mm a dd-MM-yyyy"),
                            type = 0
                        )
                    )
                }
            }
        }
        holder.binding.userIV.setOnClickListener {
            if (user != null) {
                goToProfile(
                    holder.itemView.context,
                    user
                )
            }
        }
        holder.binding.usernameTV.setOnClickListener {
            if (user != null) {
                goToProfile(
                    context = holder.itemView.context,
                    user = user
                )
            }
        }
        holder.binding.commentButton.setOnClickListener {
            if (user != null && viewModel != null) {
                goToMemory(
                    context = holder.itemView.context,
                    user = user,
                    memory = memoriesList[position]
                )
            }
        }
        holder.binding.lovesTV.setOnClickListener {
            val intent = Intent(holder.binding.root.context, LovesActivity::class.java)
            intent.putExtra("memoryID", memoriesList[position].memoryID)
            holder.binding.root.context.startActivity(intent)
        }
    }

    /** ------------------------------ Convert date to specific format 'extension function' ---------**/
    private fun Date.toString(format: String, locale: Locale = Locale.US): String {
        val formatter = SimpleDateFormat(format, locale)
        return formatter.format(this)
    }

    /** ------------------------------- Get current date -------------------------**/
    private fun getCurrentDateTime(): Date {
        return Calendar.getInstance().time
    }

    private fun sendNotification(
        message: String,
        memoryID: String,
        ownerMemoryID: String,
        type: String
    ) {
        api =
            Client.getClient(url = "https://fcm.googleapis.com/").create(APIService::class.java)
        Firebase.firestore.collection("Token").document(ownerMemoryID).get().addOnSuccessListener {
            val usertoken = it.data?.get("token") as String
            send(usertoken, "ZAAT", message, memoryID, ownerMemoryID, type)
        }
    }

    private fun send(
        usertoken: String,
        s: String,
        message: String,
        memoryID: String,
        ownerMemoryID: String,
        type: String
    ) {
        val data =
            Data(
                Title = s,
                Message = message,
                MemoryID = memoryID,
                OwnerMemoryID = ownerMemoryID,
                Type = type
            )
        val notificationSender = NotificationSender(data = data, to = usertoken)
        api.sendNotification(notificationSender).enqueue(object : Callback<Response> {
            override fun onFailure(call: Call<Response>?, t: Throwable?) {
            }

            override fun onResponse(
                call: Call<Response>?,
                response: retrofit2.Response<Response>?
            ) {
            }

        })
    }

    private fun saveNotificationToDB(notification: Notification) {
        viewModel?.addNotification(notification)
    }

    private fun goToProfile(
        context: Context,
        user: User
    ) {
        val intent = Intent(context, OtherProfileActivity::class.java)
        intent.putExtra("userID", user.userId)
        intent.putExtra("username", user.username)
        intent.putExtra("photoURL", user.photoURL)
        intent.putExtra("currentUsername", viewModel?.currentUser?.username)
        context.startActivity(intent)
    }

    private fun goToMemory(
        context: Context,
        user: User,
        memory: Memory
    ) {
        val intent = Intent(context, MemoryActivity::class.java)
        intent.putExtra("memoryID", memory.memoryID)
        intent.putExtra("userID", user.userId)
        context.startActivity(intent)
    }
}