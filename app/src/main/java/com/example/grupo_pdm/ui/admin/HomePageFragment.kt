package com.example.grupo_pdm.ui.admin

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.fragment.findNavController
import com.example.grupo_pdm.R
import com.example.grupo_pdm.databinding.FragmentAdminHomePageBinding

class AdminHomeFragment : Fragment(R.layout.fragment_admin_home_page) {

    private var _binding: FragmentAdminHomePageBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentAdminHomePageBinding.bind(view)

        binding.btnActors.setOnClickListener {
            findNavController().navigate(R.id.action_adminHome_to_manageActors)
        }

        binding.btnMovies.setOnClickListener {
            findNavController().navigate(R.id.action_adminHome_to_manageMovies)
        }

        binding.btnUsers.setOnClickListener {
            findNavController().navigate(R.id.action_adminHome_to_manageUsers)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}