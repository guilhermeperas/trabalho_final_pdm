package com.example.grupo_pdm.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.grupo_pdm.data.LoginResponse
import com.example.grupo_pdm.databinding.ItemAdminUserBinding

class AdminManageUsersAdapter(
    private val onDelete: (LoginResponse) -> Unit
) : ListAdapter<LoginResponse, AdminManageUsersAdapter.ViewHolder>(
    object : DiffUtil.ItemCallback<LoginResponse>() {
        override fun areItemsTheSame(a: LoginResponse, b: LoginResponse) =
            a.id == b.id

        override fun areContentsTheSame(a: LoginResponse, b: LoginResponse) =
            a == b
    }
) {

    inner class ViewHolder(
        private val binding: ItemAdminUserBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(user: LoginResponse) {
            binding.txtUsername.text = user.username
            binding.txtRole.text = user.role

            binding.btnDelete.setOnClickListener {
                onDelete(user)
            }
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