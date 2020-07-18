package com.example.zaatkotlin.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.zaatkotlin.R
import com.example.zaatkotlin.activities.MemoryActivity
import com.example.zaatkotlin.models.Memory
import com.example.zaatkotlin.models.User
import com.example.zaatkotlin.viewmodels.OtherProfileViewModel
import com.squareup.picasso.Picasso

class OtherProfileAdapter(
    private val memoriesList: ArrayList<Memory>,
    val usersList: ArrayList<User>,
    val viewModel: OtherProfileViewModel?
) :
    RecyclerView.Adapter<OtherProfileAdapter.WorldViewHolder>() {
    class WorldViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val userIV: ImageView = itemView.findViewById(R.id.userIV)
        val usernameTV: TextView = itemView.findViewById(R.id.usernameTV)
        val memoryTV: TextView = itemView.findViewById(R.id.memoryTV)
        val memoryDateTV: TextView = itemView.findViewById(R.id.dateTV)
        val lovesTV: TextView = itemView.findViewById(R.id.lovesTV)
        val commentsTV: TextView = itemView.findViewById(R.id.commentsCountTV)
        val loveB: Button = itemView.findViewById(R.id.loveButton)
        val commentB: Button = itemView.findViewById(R.id.commentButton)
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
        val user = usersList.find { it.userId == memoriesList[position].uID }
        holder.apply {
            this.usernameTV.text = user?.username
            Picasso.get().load(user?.photoURL).into(this.userIV)
            this.memoryTV.text = memoriesList[position].memory
            this.memoryDateTV.text = memoriesList[position].date
            this.lovesTV.text = memoriesList[position].lovesCount.toString()
            val commentsText =
                memoriesList[position].commentsCount.toString() + " " + this.itemView.resources.getText(
                    R.string.comments
                )
            this.commentsTV.text = commentsText

            this.commentB.setOnClickListener {
                if (user != null && viewModel != null) {
                    goToMemory(
                        context = this.itemView.context,
                        user = user,
                        memory = memoriesList[position],
                        follow = "Unfollow",
                        react = viewModel.reactMap[memoriesList[position].memoryID]
                    )
                }
            }
        }

        if (viewModel != null && viewModel.reactMap.size == memoriesList.size)
            if (viewModel.reactMap[memoriesList[position].memoryID]!!) {
                holder.loveB.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.ic_love,
                    0,
                    0,
                    0
                )
            } else {
                holder.loveB.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.ic_dislove,
                    0,
                    0,
                    0
                )
            }

        holder.loveB.setOnClickListener {
            if (viewModel != null) {
                if (viewModel.reactMap[memoriesList[position].memoryID]!!) {
                    viewModel.deleteReact(memoriesList[position].memoryID)
                    viewModel.memoriesList[position].lovesCount--
                    holder.loveB.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.ic_dislove,
                        0,
                        0,
                        0
                    )
                } else {
                    viewModel.makeReact(memoriesList[position].memoryID)
                    viewModel.memoriesList[position].lovesCount++
                    holder.loveB.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.ic_love,
                        0,
                        0,
                        0
                    )
                }
            }
        }
    }

    private fun goToMemory(
        context: Context,
        user: User,
        memory: Memory,
        follow: String,
        react: Boolean?
    ) {
        val intent = Intent(context, MemoryActivity::class.java)
        intent.putExtra("userObject", user)
        intent.putExtra("isFollow", follow)
        intent.putExtra("isReact", react)
        intent.putExtra("memoryObject", memory)
        //Parcel not work for those fields so we pass them
        intent.putExtra("memoryID", memory.memoryID)
        intent.putExtra("lovesCount", memory.lovesCount)
        intent.putExtra("commentsCount", memory.commentsCount)
        context.startActivity(intent)
    }
}