package com.example.zaatkotlin.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.zaatkotlin.R
import com.example.zaatkotlin.models.Memory
import com.example.zaatkotlin.models.User
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso

class WorldAdapter(private val memoriesList: ArrayList<Memory>) :
    RecyclerView.Adapter<WorldAdapter.WorldViewHolder>() {
    class WorldViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val userIV: ImageView = itemView.findViewById(R.id.userIV)
        val usernameTV: TextView = itemView.findViewById(R.id.usernameTV)
        val memoryTV: TextView = itemView.findViewById(R.id.memoryTV)
        val memoryDateTV = itemView.findViewById<TextView>(R.id.dateTV)
        val lovesTV = itemView.findViewById<TextView>(R.id.lovesTV)
        val loveB = itemView.findViewById<Button>(R.id.loveButton)
        val shareB = itemView.findViewById<Button>(R.id.shareButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorldViewHolder {
        return WorldViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.world_item, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return memoriesList.count()
    }

    override fun onBindViewHolder(holder: WorldViewHolder, position: Int) {
        Firebase.firestore.collection("Users").document(memoriesList[position].uID).get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot != null) {
                    val user = User(
                        email = documentSnapshot["email"] as String,
                        userId = documentSnapshot["userId"] as String,
                        photoURL = documentSnapshot["photoURL"] as String,
                        username = documentSnapshot["username"] as String
                    )
                    holder.usernameTV.text = user.username
                    Picasso.get().load(user.photoURL).into(holder.userIV)
                    holder.memoryTV.text = memoriesList[position].memory
                    holder.memoryDateTV.text = memoriesList[position].date
                }
            }
    }
}