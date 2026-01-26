package com.example.grupo_pdm.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.grupo_pdm.data.Person
import com.example.grupo_pdm.databinding.ItemActorHomeBinding

/**
 * Adapter for displaying actors in a horizontal RecyclerView on the home page.
 */
class ActorHomeAdapter(
    onActorClick: (Person) -> Unit = {}
) : BaseAdapter<Person, ActorHomeAdapter.ActorViewHolder>(onItemClick = onActorClick) {

    inner class ActorViewHolder(private val binding: ItemActorHomeBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(actor: Person) {
            binding.tvActorName.text = actor.name

            binding.root.setOnClickListener {
                onItemClick(actor)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActorViewHolder {
        val binding = ItemActorHomeBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ActorViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ActorViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}
