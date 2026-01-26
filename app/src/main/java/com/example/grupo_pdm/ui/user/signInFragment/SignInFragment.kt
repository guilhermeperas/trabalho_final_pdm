package com.example.grupo_pdm.ui.user.signInFragment

import android.graphics.Paint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.edit
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.example.grupo_pdm.R
import com.example.grupo_pdm.data.MovieServiceClient
import com.example.grupo_pdm.data.httpClient
import com.example.grupo_pdm.databinding.FragmentSignInBinding
import io.ktor.client.request.basicAuth
import io.ktor.client.request.get
import io.ktor.client.request.head
import io.ktor.http.isSuccess
import kotlinx.coroutines.launch

class SignInFragment : Fragment(R.layout.fragment_sign_in) {

    private var _binding: FragmentSignInBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        _binding = FragmentSignInBinding.bind(view)

        binding.loginBtn.setOnClickListener {
            setLoading(true)

            val username = binding.username.text.toString()
            val password = binding.password.text.toString()

            lifecycleScope.launch {

                val authResponse = httpClient.get("/users/login") {
                    basicAuth(username, password)
                }

                if (authResponse.status.isSuccess()) {

                    MovieServiceClient.setCredentials(username, password)

                    requireActivity()
                        .getSharedPreferences("prefs", 0)
                        .edit {
                            putString("username", username)
                            putString("password", password)
                        }
                    goToMain()
                } else {
                    // error
                    setLoading(false)
                }
            }
        }
    }

    private fun setLoading(loading: Boolean) {
        binding.loadingLayout.isVisible = loading
        binding.loginBtn.isEnabled = !loading
        binding.usernameLayout.isEnabled = !loading
        binding.passwordLayout.isEnabled = !loading

    }

    private fun goToMain() {
        findNavController()
            .navigate(
                SignInFragmentDirections.actionSignInFragmentToMainPage(),
                NavOptions.Builder().apply {
                    this.setPopUpTo(R.id.signInFragment, true)
                }.build()
            )
    }
}
