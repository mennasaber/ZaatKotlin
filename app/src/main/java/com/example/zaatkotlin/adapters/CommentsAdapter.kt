package com.example.zaatkotlin.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.zaatkotlin.R
import com.example.zaatkotlin.activities.OtherProfileActivity
import com.example.zaatkotlin.activities.ProfileActivity
import com.example.zaatkotlin.models.Comment
import com.example.zaatkotlin.models.User
import com.google.firebase.auth.FirebaseAuth
import com.squareup.picasso.Picasso

class CommentsAdapter(
    var commentsList: ArrayList<Comment>,
    var usersList: ArrayList<User>
) : RecyclerView.Adapter<CommentsAdapter.CommentsViewHolder>() {
    class CommentsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val usernameTV: TextView = itemView.findViewById(R.id.usernameTV)
        val userIV: ImageView = itemView.findViewById(R.id.userIV)
        val commentTV: TextView = itemView.findViewById(R.id.commentTV)
        val dateTV: TextView = itemView.findViewById(R.id.dateTV)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentsViewHolder {
        return CommentsViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.comment_item,
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
                this.commentTV.text = commentsList[position].commentContent
                this.dateTV.text = commentsList[position].date
                this.usernameTV.text = user.username
                Picasso.get().load(user.photoURL).into(this.userIV)
            }
        holder.userIV.setOnClickListener {
            if (user != null) {
                goToProfile(
                    context = holder.itemView.context,
                    user = user
                )
            }
        }
        holder.usernameTV.setOnClickListener {
            if (user != null) {
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
            context.startActivity(intent)
        } else {
            context.startActivity(Intent(context, ProfileActivity::class.java))
        }
    }
}