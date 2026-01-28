package com.example.grupo_pdm.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.grupo_pdm.data.PersonResponse
import com.example.grupo_pdm.databinding.ItemAdminManageBinding

class AdminManageActorsAdapter(
    private var actors: List<PersonResponse>,
    private val onEdit: (PersonResponse) -> Unit,
    private val onDelete: (PersonResponse) -> Unit
) : RecyclerView.Adapter<AdminManageActorsAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemAdminManageBinding)
        : RecyclerView.ViewHolder(binding.root) {

        fun bind(actor: PersonResponse) {
            binding.txtItemName.text = actor.name
            binding.btnEdit.setOnClickListener { onEdit(actor) }
            binding.btnDelete.setOnClickListener { onDelete(actor) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemAdminManageBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(actors[position])
    }

    override fun getItemCount() = actors.size

    fun update(newActors: List<PersonResponse>) {
        actors = newActors
        notifyDataSetChanged()
    }
}