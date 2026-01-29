package com.example.grupo_pdm.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.grupo_pdm.data.CastMemberResponse
import com.example.grupo_pdm.databinding.ItemCastDetailBinding

/**
 * Adapter for showing cast members with name and character in a RecyclerView
 *
 * @param onCastClick event when a cast member is clicked.
 */
class CastAdapter(
    onCastClick: (CastMemberResponse) -> Unit = {}
) : BaseAdapter<CastMemberResponse, CastAdapter.CastViewHolder>(onItemClick = onCastClick) {

    inner class CastViewHolder(private val binding: ItemCastDetailBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(cast: CastMemberResponse) {
            val context = binding.root.context
            binding.tvCastName.text = cast.name ?: context.getString(com.example.grupo_pdm.R.string.text_unknown)
            binding.tvCastCharacter.text = context.getString(com.example.grupo_pdm.R.string.text_role_format, cast.character)
            binding.root.setOnClickListener {
                onItemClick(cast)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CastViewHolder {
        val binding = ItemCastDetailBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CastViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CastViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}
