package com.example.zaatkotlin.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.zaatkotlin.R
import com.example.zaatkotlin.activities.EditMemoryActivity
import com.example.zaatkotlin.models.Memory

class RecyclerViewAdapter(var memoriesList: ArrayList<Memory>) :
    RecyclerView.Adapter<RecyclerViewAdapter.MemoryViewHolder>() {
    // View holder take view of item
    class MemoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private lateinit var memoryObject: Memory
        private val titleTV: TextView = itemView.findViewById(R.id.memoryTitle)
        private val memoryTV: TextView = itemView.findViewById(R.id.memoryText)

        init {
            itemView.setOnClickListener {
                val intent = Intent(
                    itemView.context,
                    EditMemoryActivity::class.java
                )
                intent.putExtra("memoryID", memoryObject.memoryID)
                intent.putExtra("title", memoryObject.title)
                intent.putExtra("content", memoryObject.memory)
                intent.putExtra("isSharing", memoryObject.isSharing)
                itemView.context.startActivity(intent)
            }
        }

        fun setDataOfMemory(memoryObject: Memory) {
            this.memoryObject = memoryObject
            titleTV.text = memoryObject.title
            memoryTV.text = memoryObject.memory
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
    }
}