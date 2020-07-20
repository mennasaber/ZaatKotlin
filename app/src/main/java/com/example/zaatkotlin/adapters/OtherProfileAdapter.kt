package com.example.zaatkotlin.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.zaatkotlin.R
import com.example.zaatkotlin.activities.MemoryActivity
import com.example.zaatkotlin.databinding.WorldItemBinding
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
    class WorldViewHolder(val binding: WorldItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorldViewHolder {
        return WorldViewHolder(
            WorldItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return memoriesList.count()
    }

    override fun onBindViewHolder(holder: WorldViewHolder, position: Int) {
        val user = usersList.find { it.userId == memoriesList[position].uID }
        if (user != null)
            holder.apply {
                binding.usernameTV.text = user.username
                Picasso.get().load(user.photoURL).into(binding.userIV)
                binding.memoryTV.text = memoriesList[position].memory
                binding.dateTV.text = memoriesList[position].date
                binding.lovesTV.text = memoriesList[position].lovesCount.toString()
                val commentsText =
                    memoriesList[position].commentsCount.toString() + " " + this.itemView.resources.getText(
                        R.string.comments
                    )
                binding.commentsCountTV.text = commentsText

                binding.commentButton.setOnClickListener {
                    if (viewModel != null) {
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
                holder.binding.loveButton.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.ic_love,
                    0,
                    0,
                    0
                )
            } else {
                holder.binding.loveButton.setCompoundDrawablesWithIntrinsicBounds(
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
                    viewModel.memoriesList[position].lovesCount--
                    holder.binding.loveButton.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.ic_dislove,
                        0,
                        0,
                        0
                    )
                } else {
                    viewModel.makeReact(memoriesList[position].memoryID)
                    viewModel.memoriesList[position].lovesCount++
                    holder.binding.loveButton.setCompoundDrawablesWithIntrinsicBounds(
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