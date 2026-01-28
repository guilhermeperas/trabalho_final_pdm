package com.example.grupo_pdm.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil3.load
import coil3.request.crossfade
import coil3.request.placeholder
import com.example.grupo_pdm.data.PersonResponse
import com.example.grupo_pdm.databinding.ItemActorHomeBinding
import kotlinx.coroutines.CoroutineScope

/**
 * Adapter for displaying people (actors) in a horizontal RecyclerView on the home page.
 */
class PersonAdapter(
    private val scope: CoroutineScope? = null,
    onPersonClick: (PersonResponse) -> Unit = {}
) : BaseAdapter<PersonResponse, PersonAdapter.PersonViewHolder>(onItemClick = onPersonClick) {
    inner class PersonViewHolder(private val binding: ItemActorHomeBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(person: PersonResponse) {
            binding.tvActorName.text = "BODA"

            binding.root.setOnClickListener {
                onItemClick(person)
            }
            val pictureId = person.picture?.id
            if (pictureId != null) {
                binding.ivActorPhoto.load("http://10.0.2.2:8080/people/${person.id}/picture/${pictureId}") {
                    crossfade(true)
                    placeholder(android.R.drawable.ic_menu_report_image)
                }
            } else {
                binding.ivActorPhoto.setImageResource(android.R.drawable.ic_menu_report_image)
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
