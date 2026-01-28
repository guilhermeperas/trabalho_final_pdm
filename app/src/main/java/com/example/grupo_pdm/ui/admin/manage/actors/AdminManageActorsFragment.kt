package com.example.grupo_pdm.ui.admin.manage.actors

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.grupo_pdm.R
import com.example.grupo_pdm.data.ApiResult
import com.example.grupo_pdm.databinding.FragmentAdminManageActorsBinding
import com.example.grupo_pdm.ui.adapters.AdminManageActorsAdapter
import kotlinx.coroutines.launch

class AdminManageActorsFragment
    : Fragment(R.layout.fragment_admin_manage_actors) {

    private var _binding: FragmentAdminManageActorsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AdminManageActorsViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _binding = FragmentAdminManageActorsBinding.bind(view)

        val adapter = AdminManageActorsAdapter(
            onEdit = {
                Toast.makeText(
                    requireContext(),
                    "Editar ator (simulado)",
                    Toast.LENGTH_SHORT
                ).show()
            },
            onDelete = {
                Toast.makeText(
                    requireContext(),
                    "Eliminar ator (simulado)",
                    Toast.LENGTH_SHORT
                ).show()
            }
        )

        binding.rvActors.adapter = adapter

        lifecycleScope.launch {
            viewModel.actors.collect {
                if (it is ApiResult.Success) {
                    adapter.submitList(it.data)
                }
            }
        }

        binding.btnAddActor.setOnClickListener {
            viewModel.createActor("Novo Ator")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}