package com.example.zaatkotlin.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.zaatkotlin.R
import com.example.zaatkotlin.activities.EditMemoryActivity
import com.example.zaatkotlin.databinding.MemoryItemBinding
import com.example.zaatkotlin.interfaces.ItemTouchHelperAdapter
import com.example.zaatkotlin.interfaces.ItemTouchHelperViewHolder
import com.example.zaatkotlin.models.Memory
import com.example.zaatkotlin.viewmodels.MemoriesViewModel
import java.util.*

class RecyclerViewAdapter(
    var memoriesList: ArrayList<Memory>,
    var viewModel: MemoriesViewModel
) :
    RecyclerView.Adapter<RecyclerViewAdapter.MemoryViewHolder>(), ItemTouchHelperAdapter {

    class MemoryViewHolder(val binding: MemoryItemBinding) : RecyclerView.ViewHolder(binding.root),
        ItemTouchHelperViewHolder {
        fun setDataOfMemory(memoryObject: Memory) {
            binding.memoryTitle.text = memoryObject.title
            binding.memoryText.text = memoryObject.memory
            binding.memoryDate.text = memoryObject.date
        }

        override fun onItemSelected() {
            binding.root.setBackgroundResource(R.drawable.border_rounded_background)
        }

        override fun onItemClear() {
            binding.root.setBackgroundColor(0)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemoryViewHolder {
        return MemoryViewHolder(
            MemoryItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int {
        return memoriesList.size
    }

    override fun onBindViewHolder(holder: MemoryViewHolder, position: Int) {
        holder.setDataOfMemory(
            memoryObject = memoriesList[position]
        )
        holder.binding.root.setOnClickListener {
            val intent = Intent(
                holder.itemView.context,
                EditMemoryActivity::class.java
            )
            intent.putExtra("memoryID", memoriesList[position].memoryID)
            intent.putExtra("title", memoriesList[position].title)
            intent.putExtra("content", memoriesList[position].memory)
            intent.putExtra("isSharing", memoriesList[position].isSharing)
            intent.putExtra("date", memoriesList[position].date)
            holder.binding.root.context.startActivity(intent)
        }
        holder.itemView.setOnLongClickListener { true }
    }

    override fun onItemMove(fromPosition: Int, toPosition: Int) {
        val temp = memoriesList[fromPosition].timestamp
        memoriesList[fromPosition].timestamp = memoriesList[toPosition].timestamp
        memoriesList[toPosition].timestamp = temp
        Collections.swap(memoriesList, fromPosition, toPosition)
        notifyItemMoved(fromPosition, toPosition)
    }

    override fun onItemDismiss(position: Int) {
        viewModel.removeMemory(memoryID = memoriesList[position].memoryID)
        memoriesList.removeAt(position)
        notifyItemRemoved(position)
    }

    override fun onItemMoveStop(fromPosition: Int, toPosition: Int) {
        if (fromPosition != -1)
            for (i in fromPosition until toPosition + 1) {
                viewModel.updateMemoryTimestamp(memoriesList[i].memoryID, memoriesList[i].timestamp)
            }
    }
}