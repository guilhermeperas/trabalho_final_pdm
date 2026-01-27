package com.example.grupo_pdm.ui.user.createAccountFragment

import android.Manifest
import android.app.DatePickerDialog
import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.grupo_pdm.R
import com.example.grupo_pdm.data.ApiResult
import com.example.grupo_pdm.data.CreatePictureRequest
import com.example.grupo_pdm.data.MovieServiceClient
import com.example.grupo_pdm.data.RegisterUserRequest
import com.example.grupo_pdm.databinding.FragmentCreateAccountBinding
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.util.Base64
import java.util.Calendar

class CreateAccountFragment : Fragment(R.layout.fragment_create_account) {
    
    private var _binding: FragmentCreateAccountBinding? = null
    private val binding get() = _binding!!

    private var selectedDateOfBirth: String? = null
    private var selectedImageBase64: String? = null

    private val takePictureLauncher = registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
        if (bitmap != null) {
            binding.profileImageView.setImageBitmap(bitmap)
            selectedImageBase64 = encodeImageToBase64(bitmap)
            binding.selectImageText.visibility = View.GONE
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
        if (isGranted) {
            takePictureLauncher.launch(null)
        } else {
            Toast.makeText(requireContext(), "Camera permission required to take photo", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentCreateAccountBinding.bind(view)

        binding.dobEditText.setOnClickListener {
            showDatePicker()
        }

        binding.profileImageView.setOnClickListener {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA)
        }

        binding.createAccountButton.setOnClickListener {
            handleRegister()
        }
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        DatePickerDialog(requireContext(), { _, selectedYear, selectedMonth, selectedDay ->
            // Format: YYYY-MM-DD
            val formattedDate = String.format("%04d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay)
            selectedDateOfBirth = formattedDate
            binding.dobEditText.setText(formattedDate)
        }, year, month, day).show()
    }

    private fun encodeImageToBase64(bitmap: Bitmap): String {
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, outputStream)
        val bytes = outputStream.toByteArray()
        return Base64.getEncoder().encodeToString(bytes)
    }

    private fun handleRegister() {
        val username = binding.usernameEditText.text.toString()
        val password = binding.passwordEditText.text.toString()

        if (username.isBlank() || password.isBlank()) {
            Toast.makeText(requireContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show()
            return
        }
        
        val pictureRequest = if (selectedImageBase64 != null) {
            CreatePictureRequest(
                filename = "profile_${System.currentTimeMillis()}.jpg",
                data = selectedImageBase64!!
            )
        } else {
            null
        }

        val request = RegisterUserRequest(
            username = username,
            password = password,
            dateOfBirth = selectedDateOfBirth,
            picture = pictureRequest
        )

        binding.loadingLayout.visibility = View.VISIBLE
        setLoading(true)

        lifecycleScope.launch {
            val result = MovieServiceClient.register(request)
            // Check binding safely inside coroutine
            if (_binding != null) {
                setLoading(false)
                
                when (result) {
                    is ApiResult.Success -> {
                        Toast.makeText(requireContext(), "Account created!", Toast.LENGTH_SHORT).show()
                        findNavController().popBackStack()
                    }
                    is ApiResult.Failure -> {
                        android.util.Log.e("CreateAccountFragment", "Registration failed: ${result.error}")
                    }
                    else -> {}
                }
            }
        }
    }

    private fun setLoading(isLoading: Boolean) {
        binding.loadingLayout.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.createAccountButton.isEnabled = !isLoading
        binding.usernameEditText.isEnabled = !isLoading
        binding.passwordEditText.isEnabled = !isLoading
        binding.dobEditText.isEnabled = !isLoading
        binding.profileImageView.isEnabled = !isLoading
    }
}