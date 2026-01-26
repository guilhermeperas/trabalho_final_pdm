package com.example.grupo_pdm.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.grupo_pdm.data.Person
import com.example.grupo_pdm.databinding.ItemActorHomeBinding

/**
 * Adapter for displaying people (actors) in a horizontal RecyclerView on the home page.
 */
class PersonAdapter(
    onPersonClick: (Person) -> Unit = {}
) : BaseAdapter<Person, PersonAdapter.PersonViewHolder>(onItemClick = onPersonClick) {

    inner class PersonViewHolder(private val binding: ItemActorHomeBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(person: Person) {
            binding.tvActorName.text = person.name

            binding.root.setOnClickListener {
                onItemClick(person)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PersonViewHolder {
        val binding = ItemActorHomeBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PersonViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PersonViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}
