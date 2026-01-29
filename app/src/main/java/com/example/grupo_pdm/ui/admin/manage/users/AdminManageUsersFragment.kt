package com.example.grupo_pdm.ui.admin.manage.users

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.grupo_pdm.R
import com.example.grupo_pdm.data.ApiResult
import com.example.grupo_pdm.databinding.FragmentAdminManageUsersBinding
import com.example.grupo_pdm.ui.adapters.AdminManageUsersAdapter
import kotlinx.coroutines.launch

class AdminManageUsersFragment
    : Fragment(R.layout.fragment_admin_manage_users) {

    private var _binding: FragmentAdminManageUsersBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AdminManageUsersViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentAdminManageUsersBinding.bind(view)

        val adapter = AdminManageUsersAdapter()

        binding.rvUsers.layoutManager = LinearLayoutManager(requireContext())
        binding.rvUsers.adapter = adapter

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.users.collect { result ->
                when (result) {
                    is ApiResult.Success -> adapter.submitList(result.data)
                    is ApiResult.Failure -> {
                        Toast.makeText(
                            requireContext(),
                            "Falha ao carregar utilizadores: ${result.error.detail}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    else -> {}
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
