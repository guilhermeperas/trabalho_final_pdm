package com.example.grupo_pdm.ui.people.details

import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import bindTopBarNavigation
import com.example.grupo_pdm.R
import com.example.grupo_pdm.data.ApiResult
import com.example.grupo_pdm.databinding.FragmentActorBinding
import kotlinx.coroutines.launch

class PeopleDetailFragment : Fragment(R.layout.fragment_actor) {

    private val args: PeopleDetailFragmentArgs by navArgs()
    private var _binding: FragmentActorBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: PeopleDetailViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentActorBinding.bind(view)
        bindTopBarNavigation(binding.topBar)

        viewModel.loadPerson(args.personId)
        
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.person.collect { result ->
                when (result) {
                    is ApiResult.Success -> {
                        val person = result.data
                        binding.txtActorName.text = person.name
                        binding.txtActorBio.text = person.dateOfBirth


                    }
                    is ApiResult.Failure -> {
                        Log.e("PeopleDetailFragment", "Error loading person: ${result.error}")
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
