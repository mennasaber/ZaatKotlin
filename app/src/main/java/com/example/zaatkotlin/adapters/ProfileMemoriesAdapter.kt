package com.example.zaatkotlin.adapters

import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.zaatkotlin.R
import com.example.zaatkotlin.activities.LovesActivity
import com.example.zaatkotlin.activities.MemoryActivity
import com.example.zaatkotlin.databinding.WorldItemBinding
import com.example.zaatkotlin.models.Memory
import com.example.zaatkotlin.models.User
import com.example.zaatkotlin.viewmodels.ProfileViewModel
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import java.lang.Exception

class ProfileMemoriesAdapter(
    private val memoriesList: ArrayList<Memory>,
    val user: User,
    val viewModel: ProfileViewModel?
) :
    RecyclerView.Adapter<ProfileMemoriesAdapter.WorldViewHolder>() {
    private val TAG = "ProfileMemoriesAdapter"

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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            Log.d(TAG, "onBindViewHolder: ${holder.binding.root.layoutDirection}")
        }
        holder.apply {
            binding.usernameTV.text = user.username
            binding.progressBar.visibility = View.VISIBLE
            Picasso.get().load(user.photoURL).into(binding.userIV,
                object : Callback {
                    override fun onSuccess() {
                        binding.progressBar.visibility = View.INVISIBLE
                    }

                    override fun onError(e: Exception?) {
                        TODO("Not yet implemented")
                    }

                })
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
                        memory = memoriesList[position]
                    )
                }
            }
        }

        if (viewModel != null && viewModel.reactMap.size == memoriesList.size)
            if (viewModel.reactMap[memoriesList[position].memoryID]!!) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    holder.binding.loveButton.setCompoundDrawablesRelativeWithIntrinsicBounds(
                        ContextCompat.getDrawable(
                            holder.binding.root.context,
                            R.drawable.ic_love
                        ),
                        null,
                        null,
                        null
                    )
                } else {
                    holder.binding.loveButton.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.ic_love,
                        0,
                        0,
                        0
                    )
                }
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    holder.binding.loveButton.setCompoundDrawablesRelativeWithIntrinsicBounds(
                        ContextCompat.getDrawable(
                            holder.binding.root.context,
                            R.drawable.ic_dislove
                        ),
                        null,
                        null,
                        null
                    )
                } else {
                    holder.binding.loveButton.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.ic_dislove,
                        0,
                        0,
                        0
                    )
                }
            }
        holder.binding.loveButton.setOnClickListener {
            if (viewModel != null) {
                if (viewModel.reactMap[memoriesList[position].memoryID]!!) {
                    viewModel.deleteReact(memoriesList[position].memoryID)
                } else {
                    viewModel.makeReact(memoriesList[position].memoryID)
                }
            }
        }
        holder.binding.lovesTV.setOnClickListener {
            val intent = Intent(holder.binding.root.context, LovesActivity::class.java)
            intent.putExtra("memoryID", memoriesList[position].memoryID)
            holder.binding.root.context.startActivity(intent)
        }
    }

    private fun isLayoutRtl(view: View): Boolean =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            view.layoutDirection == View.LAYOUT_DIRECTION_RTL
        } else {
            false
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
