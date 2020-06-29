package com.example.zaatkotlin.callbacks

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.zaatkotlin.interfaces.ItemTouchHelperAdapter
import com.example.zaatkotlin.interfaces.ItemTouchHelperViewHolder

class SimpleItemTouchHelperCallback(private val itemTouchHelperAdapter: ItemTouchHelperAdapter) :
    ItemTouchHelper.Callback() {
    var fromPosition: Int = -2
    var toPosition: Int = 0
    var startMovementIndex = -1
    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {
        val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN
        val swapFlags = ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        return makeMovementFlags(dragFlags, swapFlags)
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        if (startMovementIndex == -1)
            startMovementIndex = viewHolder.adapterPosition
        fromPosition = viewHolder.adapterPosition
        toPosition = target.adapterPosition
        if (fromPosition != toPosition)
            itemTouchHelperAdapter.onItemMove(fromPosition, toPosition)
        return true
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        toPosition = -1
        itemTouchHelperAdapter.onItemDismiss(viewHolder.adapterPosition)
    }

    override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
        super.onSelectedChanged(viewHolder, actionState)
        if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
            val itemTouchHelperViewHolder = viewHolder as ItemTouchHelperViewHolder
            itemTouchHelperViewHolder.onItemSelected()
        }
    }

    override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
        super.clearView(recyclerView, viewHolder)
        val itemTouchHelperViewHolder = viewHolder as ItemTouchHelperViewHolder
        itemTouchHelperViewHolder.onItemClear()
        if (toPosition != -1 && fromPosition != toPosition) {
            if (startMovementIndex > toPosition)
                startMovementIndex = toPosition.also { toPosition = startMovementIndex }
            itemTouchHelperAdapter.onItemMoveStop(startMovementIndex, toPosition)
        }
    }
}