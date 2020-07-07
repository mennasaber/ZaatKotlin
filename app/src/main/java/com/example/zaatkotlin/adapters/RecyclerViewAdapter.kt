package com.example.zaatkotlin.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.zaatkotlin.R
import com.example.zaatkotlin.activities.EditMemoryActivity
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

    // View holder take view of item
    class MemoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        ItemTouchHelperViewHolder {
        private lateinit var memoryObject: Memory
        private val titleTV: TextView = itemView.findViewById(R.id.memoryTitle)
        private val memoryTV: TextView = itemView.findViewById(R.id.memoryText)
        fun setDataOfMemory(memoryObject: Memory) {
            this.memoryObject = memoryObject
            titleTV.text = memoryObject.title
            memoryTV.text = memoryObject.memory
        }

        override fun onItemSelected() {
            itemView.setBackgroundResource(R.drawable.border_rounded_background)
        }

        override fun onItemClear() {
            itemView.setBackgroundColor(0)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemoryViewHolder {

        return MemoryViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.memory_item, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return memoriesList.size
    }

    override fun onBindViewHolder(holder: MemoryViewHolder, position: Int) {
        holder.setDataOfMemory(
            memoryObject = memoriesList[position]
        )
        holder.itemView.setOnClickListener {
            val intent = Intent(
                holder.itemView.context,
                EditMemoryActivity::class.java
            )
            intent.putExtra("memoryID", memoriesList[position].memoryID)
            intent.putExtra("title", memoriesList[position].title)
            intent.putExtra("content", memoriesList[position].memory)
            intent.putExtra("isSharing", memoriesList[position].isSharing)
            holder.itemView.context.startActivity(intent)
        }
        holder.itemView.setOnLongClickListener { view ->
            true
        }
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