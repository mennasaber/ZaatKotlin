package com.example.zaatkotlin.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.zaatkotlin.activities.OtherProfileActivity
import com.example.zaatkotlin.activities.ProfileActivity
import com.example.zaatkotlin.databinding.CommentItemBinding
import com.example.zaatkotlin.models.Comment
import com.example.zaatkotlin.models.User
import com.example.zaatkotlin.viewmodels.MemoryViewModel
import com.google.firebase.auth.FirebaseAuth
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.layout_top_profile.view.*
import java.lang.Exception

class CommentsAdapter(
    var commentsList: ArrayList<Comment>,
    var usersList: ArrayList<User>, val viewModel: MemoryViewModel
) : RecyclerView.Adapter<CommentsAdapter.CommentsViewHolder>() {
    class CommentsViewHolder(val binding: CommentItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentsViewHolder {
        return CommentsViewHolder(
            CommentItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return commentsList.count()
    }

    override fun onBindViewHolder(holder: CommentsViewHolder, position: Int) {
        val user = usersList.find { it.userId == commentsList[position].userID }
        if (user != null)
            holder.apply {
                binding.commentTV.text = commentsList[position].commentContent
                binding.dateTV.text = commentsList[position].date
                binding.usernameTV.text = user.username
                binding.progressBar.visibility = View.VISIBLE
                Picasso.get().load(user.photoURL).into(binding.userIV,
                    object : Callback {
                        override fun onSuccess() {
                            binding.progressBar.visibility = View.INVISIBLE
                        }

                        override fun onError(e: Exception?) {

                        }

                    })

                binding.userIV.setOnClickListener {
                    goToProfile(
                        context = holder.itemView.context,
                        user = user
                    )
                }
                binding.usernameTV.setOnClickListener {
                    goToProfile(
                        context = holder.itemView.context,
                        user = user
                    )
                }
            }
    }

    private fun goToProfile(
        context: Context,
        user: User
    ) {
        if (user.userId != FirebaseAuth.getInstance().uid) {
            val intent = Intent(context, OtherProfileActivity::class.java)
            intent.putExtra("userID", user.userId)
            intent.putExtra("username", user.username)
            intent.putExtra("photoURL", user.photoURL)
            intent.putExtra("currentUsername", viewModel.currentUser.username)
            context.startActivity(intent)
        } else {
            context.startActivity(Intent(context, ProfileActivity::class.java))
        }
    }
}