package com.example.grupo_pdm.ui.people.details

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import bindTopBarNavigation
import coil3.load
import coil3.request.crossfade
import coil3.request.placeholder
import com.example.grupo_pdm.R
import com.example.grupo_pdm.data.ApiResult
import com.example.grupo_pdm.databinding.FragmentActorBinding
import com.example.grupo_pdm.ui.adapters.DirectedMovieAdapter
import com.example.grupo_pdm.ui.adapters.RolesAdapter
import com.example.grupo_pdm.ui.components.TopBarView
import kotlinx.coroutines.launch

class PeopleDetailFragment : Fragment(R.layout.fragment_actor) {

    private val args: PeopleDetailFragmentArgs by navArgs()
    private var _binding: FragmentActorBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: PeopleDetailViewModel by viewModels()

    private val directedMovieAdapter = DirectedMovieAdapter { movie ->
        findNavController().navigate(
            PeopleDetailFragmentDirections.actionPeopleDetailFragmentToMovieDetailFragment(movie.id)
        )
    }

    private val rolesAdapter = RolesAdapter { role ->
        findNavController().navigate(
            PeopleDetailFragmentDirections.actionPeopleDetailFragmentToMovieDetailFragment(role.movieId)
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentActorBinding.bind(view)
        val topBar = view.findViewById<TopBarView>(R.id.topBar)
        bindTopBarNavigation(topBar)

        setupRecyclerViews()

        viewModel.loadPerson(args.personId)
        
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.person.collect { result ->
                when (result) {
                    is ApiResult.Success -> {
                        val person = result.data
                        binding.txtActorName.text = person.name
                        binding.txtActorInfo.text = "Born: ${person.dateOfBirth ?: "Unknown"}" 

                        // Load Image
                        val mainPic = person.pictures?.firstOrNull { it.mainPicture == true } ?: person.pictures?.firstOrNull()
                        
                        if (mainPic != null) {
                             binding.imgActor.load("http://10.0.2.2:8080/people/${person.id}/picture/${mainPic.id}") {
                                crossfade(true)
                                placeholder(android.R.drawable.ic_menu_report_image)
                            }
                        } else {
                            binding.imgActor.setImageResource(android.R.drawable.ic_menu_report_image)
                        }

                        // Directed Movies
                        if (!person.directedMovies.isNullOrEmpty()) {
                            binding.txtDirectedTitle.isVisible = true
                            binding.rvDirectedMovies.isVisible = true
                            directedMovieAdapter.submitList(person.directedMovies)
                        } else {
                            binding.txtDirectedTitle.isVisible = false
                            binding.rvDirectedMovies.isVisible = false
                        }

                        // Roles
                        if (!person.roles.isNullOrEmpty()) {
                            binding.txtRolesTitle.isVisible = true
                            binding.rvActorRoles.isVisible = true
                            rolesAdapter.submitList(person.roles)
                        } else {
                            binding.txtRolesTitle.isVisible = false
                            binding.rvActorRoles.isVisible = false
                        }
                    }
                    is ApiResult.Failure -> {
                        Log.e("PeopleDetailFragment", "Error loading person: ${result.error}")
                    }
                    else -> {}
                }
            }
        }
    }

    private fun setupRecyclerViews() {
        // Directed Movies - Horizontal
        binding.rvDirectedMovies.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = directedMovieAdapter
        }

        // Roles - Vertical
        binding.rvActorRoles.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = rolesAdapter
        }
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}