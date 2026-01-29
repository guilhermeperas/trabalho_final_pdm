package com.example.grupo_pdm.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.grupo_pdm.data.PersonRole
import com.example.grupo_pdm.databinding.ItemRoleBinding

class RolesAdapter(
    private val onRoleClick: (PersonRole) -> Unit = {}
) : BaseAdapter<PersonRole, RolesAdapter.RoleViewHolder>(onItemClick = onRoleClick) {

    inner class RoleViewHolder(private val binding: ItemRoleBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(role: PersonRole) {
            binding.tvMovieTitle.text = role.title
            binding.tvCharacter.text = role.character
            binding.tvYear.text = role.releaseDate ?: ""
            
            // Optional click listener if you want to navigate to the movie
             binding.root.setOnClickListener {
                 onRoleClick(role)
             }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RoleViewHolder {
        val binding = ItemRoleBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return RoleViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RoleViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}
