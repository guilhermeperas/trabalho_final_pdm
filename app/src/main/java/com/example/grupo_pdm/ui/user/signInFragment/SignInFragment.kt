package com.example.grupo_pdm.ui.user.signInFragment

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.content.edit
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.example.grupo_pdm.R
import com.example.grupo_pdm.data.ApiResult
import com.example.grupo_pdm.data.MovieServiceClient
import com.example.grupo_pdm.data.SessionManager
import com.example.grupo_pdm.databinding.FragmentSignInBinding
import kotlinx.coroutines.launch

class SignInFragment : Fragment(R.layout.fragment_sign_in) {

    private var _binding: FragmentSignInBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentSignInBinding.bind(view)

        // Check for existing session
        val prefs = requireActivity().getSharedPreferences("prefs", 0)
        if (prefs.contains("username") && prefs.contains("userId") && prefs.contains("password")) {
            val username = prefs.getString("username", "") ?: ""
            val password = prefs.getString("password", "") ?: ""
            
            // Re-authenticate client
            MovieServiceClient.setCredentials(username, password)
            goToMain()
        }

        binding.createBtn.setOnClickListener {
            findNavController().navigate(
                SignInFragmentDirections.actionSignInFragmentToCreateAccountFragment()
            )
        }

        binding.loginBtn.setOnClickListener {
            val username = binding.username.text.toString()
            val password = binding.password.text.toString()

            if (username.isBlank() || password.isBlank()) {
                Toast.makeText(requireContext(), "Please enter username and password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            setLoading(true)

            lifecycleScope.launch {
                val result = MovieServiceClient.login(username, password)
                // Check binding safely inside coroutine as view might be destroyed
                if (_binding != null) {
                    setLoading(false)

                    when (result) {
                        is ApiResult.Success -> {
                            requireActivity().getSharedPreferences("prefs", 0).edit {
                                putString("username", username)
                                putString("password", password)
                                putString("role",result.data.role)
                                putInt("userId", result.data.id)
                            }
                            SessionManager.currentUser = result.data
                            goToMain()
                        }
                        is ApiResult.Failure -> {
                            android.util.Log.e("SignInFragment", "Login failed: ${result.error}")
                        }

                        is ApiResult.Loading -> setLoading(true)
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setLoading(loading: Boolean) {
        binding.loadingLayout.isVisible = loading
        binding.loginBtn.isEnabled = !loading
        binding.usernameLayout.isEnabled = !loading
        binding.passwordLayout.isEnabled = !loading
    }

    private fun goToMain() {
        findNavController().navigate(
            SignInFragmentDirections.actionSignInFragmentToMainPage(),
            NavOptions.Builder().apply {
                this.setPopUpTo(R.id.signInFragment, true)
            }.build()
        )
    }
}
