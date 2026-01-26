package com.example.grupo_pdm.ui.adapters

import androidx.recyclerview.widget.RecyclerView

/**
 * Main adapter to be used for all adapters.
 *
 * @param T Type of data
 * @param VH Type of ViewHolder
 * @param items Initial list
 * @param onItemClick event when pressed
 */
abstract class BaseAdapter<T, VH : RecyclerView.ViewHolder>(
    protected var items: List<T> = emptyList(),
    protected val onItemClick: (T) -> Unit = {}
) : RecyclerView.Adapter<VH>() {

    override fun getItemCount(): Int = items.size

    /**
     * Get item at position
     */
    protected fun getItem(position: Int): T = items[position]

    /**
     * new list of items to be displayed.
     * used after crud operations to update the adapter
     */
    fun submitList(newItems: List<T>) {
        items = newItems
        notifyDataSetChanged()
    }

    /**
     * Clear all items from the adapter.
     */
    fun clear() {
        items = emptyList()
        notifyDataSetChanged()
    }
}
