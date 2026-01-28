package com.example.grupo_pdm.ui.admin.manage.movie

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.grupo_pdm.R
import com.example.grupo_pdm.data.ApiResult
import com.example.grupo_pdm.databinding.FragmentAdminManageMoviesBinding
import com.example.grupo_pdm.ui.adapters.AdminManageMoviesAdapter
import kotlinx.coroutines.launch

class AdminManageMoviesFragment
    : Fragment(R.layout.fragment_admin_manage_movies) {

    private var _binding: FragmentAdminManageMoviesBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AdminManageMoviesViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentAdminManageMoviesBinding.bind(view)

        val adapter = AdminManageMoviesAdapter(
            onEdit = {
                Toast.makeText(
                    requireContext(),
                    "Editar filme (simulado)",
                    Toast.LENGTH_SHORT
                ).show()
            },
            onDelete = {
                Toast.makeText(
                    requireContext(),
                    "Eliminar filme (simulado)",
                    Toast.LENGTH_SHORT
                ).show()
            }
        )

        binding.rvMovies.adapter = adapter

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.movies.collect { result ->
                if (result is ApiResult.Success) {
                    adapter.submitList(result.data)
                }
            }
        }

        binding.btnAddMovie.setOnClickListener {
            viewModel.createMovie(
                title = "Novo Filme"
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}