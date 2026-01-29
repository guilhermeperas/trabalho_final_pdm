package com.example.grupo_pdm.ui.admin.manage.movie

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.grupo_pdm.R
import com.example.grupo_pdm.data.ApiResult
import com.example.grupo_pdm.databinding.FragmentAdminManageMoviesBinding
import com.example.grupo_pdm.ui.adapters.AdminManageMoviesAdapter
import kotlinx.coroutines.launch

class AdminManageMoviesFragment :
    Fragment(R.layout.fragment_admin_manage_movies) {

    private var _binding: FragmentAdminManageMoviesBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AdminManageMoviesViewModel by viewModels()

    private val adapter = AdminManageMoviesAdapter(
        onEdit = { movie ->
            Toast.makeText(
                requireContext(),
                "Editar ${movie.title} (simulado)",
                Toast.LENGTH_SHORT
            ).show()
        },
        onDelete = { movie ->
            Toast.makeText(
                requireContext(),
                "Apagar ${movie.title} (simulado)",
                Toast.LENGTH_SHORT
            ).show()
        }
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentAdminManageMoviesBinding.bind(view)

        binding.rvMovies.layoutManager = LinearLayoutManager(requireContext())
        binding.rvMovies.adapter = adapter

        binding.fabAddMovie.setOnClickListener {
            showCreateMovieDialog()
        }

        observeData()
    }

    private fun observeData() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.movies.collect { result ->
                when (result) {
                    is ApiResult.Success -> adapter.submitList(result.data)
                    is ApiResult.Failure -> {
                        android.util.Log.e(
                            "AdminManageMovies",
                            "Failed to load movies: ${result.error}"
                        )
                        Toast.makeText(
                            requireContext(),
                            "Falha ao carregar filmes: ${result.error.detail}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    else -> {}
                }
            }
        }
    }

    private fun showCreateMovieDialog() {
        val container = LinearLayout(requireContext())
        container.orientation = LinearLayout.VERTICAL
        val padding = (16 * resources.displayMetrics.density).toInt()
        container.setPadding(padding, padding, padding, padding)

        val titleInput = EditText(requireContext())
        titleInput.hint = "Título do filme"
        container.addView(titleInput)

        val synopsisInput = EditText(requireContext())
        synopsisInput.hint = "Sinopse (opcional)"
        container.addView(synopsisInput)

        val releaseDateInput = EditText(requireContext())
        releaseDateInput.hint = "Data de lançamento (YYYY-MM-DD)"
        container.addView(releaseDateInput)

        val minimumAgeInput = EditText(requireContext())
        minimumAgeInput.hint = "Idade mínima (opcional)"
        minimumAgeInput.inputType = android.text.InputType.TYPE_CLASS_NUMBER
        container.addView(minimumAgeInput)

        val directorIdInput = EditText(requireContext())
        directorIdInput.hint = "ID do diretor (opcional)"
        directorIdInput.inputType = android.text.InputType.TYPE_CLASS_NUMBER
        container.addView(directorIdInput)

        val genresInput = EditText(requireContext())
        genresInput.hint = "Gêneros (ids separados por vírgula, opcional)"
        container.addView(genresInput)

        AlertDialog.Builder(requireContext())
            .setTitle("Adicionar filme")
            .setView(container)
            .setPositiveButton("Criar") { _, _ ->
                val title = titleInput.text.toString().trim()
                if (title.isEmpty()) {
                    Toast.makeText(
                        requireContext(),
                        "Título não pode estar vazio",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setPositiveButton
                }
                val synopsis = synopsisInput.text.toString().trim().ifEmpty { null }
                val releaseDate = releaseDateInput.text.toString().trim().ifEmpty { null }
                val minimumAge = minimumAgeInput.text.toString().trim().toIntOrNull()
                val directorId = directorIdInput.text.toString().trim().toIntOrNull()
                val genres = genresInput.text.toString()
                    .split(",")
                    .mapNotNull { it.trim().toIntOrNull() }
                    .ifEmpty { null }

                viewModel.createMovie(
                    com.example.grupo_pdm.data.CreateMovieRequest(
                        title = title,
                        synopsis = synopsis,
                        genres = genres,
                        releaseDate = releaseDate,
                        directorId = directorId,
                        minimumAge = minimumAge
                    )
                )
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
