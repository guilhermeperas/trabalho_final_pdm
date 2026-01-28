package com.example.grupo_pdm.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.grupo_pdm.data.PersonResponse
import com.example.grupo_pdm.databinding.ItemActorHomeBinding
import androidx.lifecycle.lifecycleScope
import coil3.load
import coil3.request.crossfade
import coil3.request.placeholder
import kotlinx.coroutines.launch

/**
 * Adapter for displaying actors in a horizontal RecyclerView on the home page.
 */
class ActorHomeAdapter(
    onActorClick: (PersonResponse) -> Unit = {}
) : BaseAdapter<PersonResponse, ActorHomeAdapter.ActorViewHolder>(onItemClick = onActorClick) {

    inner class ActorViewHolder(private val binding: ItemActorHomeBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(actor: PersonResponse) {
            binding.tvActorName.text = actor.name
            binding.root.setOnClickListener {
                onItemClick(actor)
            }
            val pictureId = actor.picture?.id
            if (pictureId != null) {
                binding.ivActorPhoto.load("http://10.0.2.2:8080/people/${actor.id}/picture/${pictureId}") {
                    crossfade(true)
                    placeholder(android.R.drawable.ic_menu_report_image)
                }
            } else {
                binding.ivActorPhoto.setImageResource(android.R.drawable.ic_menu_report_image)
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
