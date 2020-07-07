package com.example.zaatkotlin.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.zaatkotlin.R
import com.example.zaatkotlin.models.User
import com.squareup.picasso.Picasso

class ProfileAdapter(
    private val usersList: ArrayList<User>
) : RecyclerView.Adapter<ProfileAdapter.ProfileViewHolder>() {

    class ProfileViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val userIV: ImageView = itemView.findViewById(R.id.userIV)
        val usernameTV: TextView = itemView.findViewById(R.id.usernameTV)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProfileViewHolder {
        return ProfileViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.profile_item, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return usersList.count()
    }

    override fun onBindViewHolder(holder: ProfileViewHolder, position: Int) {
        Picasso.get().load(usersList[position].photoURL).into(holder.userIV)
        holder.usernameTV.text = usersList[position].username
    }
}