package com.example.grupo_pdm.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.grupo_pdm.data.UserResponse
import com.example.grupo_pdm.databinding.ItemAdminUserBinding

class AdminManageUsersAdapter : ListAdapter<UserResponse, AdminManageUsersAdapter.ViewHolder>(
    object : DiffUtil.ItemCallback<UserResponse>() {
        override fun areItemsTheSame(a: UserResponse, b: UserResponse) =
            a.id == b.id

        override fun areContentsTheSame(a: UserResponse, b: UserResponse) =
            a == b
    }
) {

    inner class ViewHolder(
        private val binding: ItemAdminUserBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(user: UserResponse) {
            binding.txtUsername.text = user.username
            val meta = when {
                !user.dateOfBirth.isNullOrBlank() -> "Nascimento: ${user.dateOfBirth}"
                !user.createdAt.isNullOrBlank() -> "Criado: ${user.createdAt}"
                else -> ""
            }
            binding.txtRole.text = meta
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(
            ItemAdminUserBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}
