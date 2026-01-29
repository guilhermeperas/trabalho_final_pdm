package com.example.grupo_pdm.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.grupo_pdm.data.PersonResponse
import com.example.grupo_pdm.databinding.ItemAdminManageActorBinding

class AdminManageActorsAdapter(
    private val onEdit: (PersonResponse) -> Unit,
    private val onDelete: (PersonResponse) -> Unit
) : ListAdapter<PersonResponse, AdminManageActorsAdapter.ViewHolder>(
    object : DiffUtil.ItemCallback<PersonResponse>() {
        override fun areItemsTheSame(a: PersonResponse, b: PersonResponse) = a.id == b.id
        override fun areContentsTheSame(a: PersonResponse, b: PersonResponse) = a == b
    }
) {

    inner class ViewHolder(
        private val binding: ItemAdminManageActorBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(actor: PersonResponse) {
            binding.txtTitle.text = actor.name
            binding.btnEdit.setOnClickListener { onEdit(actor) }
            binding.btnDelete.setOnClickListener { onDelete(actor) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(
            ItemAdminManageActorBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}
