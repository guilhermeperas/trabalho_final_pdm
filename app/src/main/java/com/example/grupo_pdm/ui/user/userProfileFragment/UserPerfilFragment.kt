package com.example.grupo_pdm.ui.user.userProfileFragment

import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.View
import android.provider.OpenableColumns
import androidx.appcompat.app.AlertDialog
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.edit
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import bindTopBarNavigation
import coil3.load
import coil3.request.crossfade
import com.example.grupo_pdm.R
import com.example.grupo_pdm.data.ApiResult
import com.example.grupo_pdm.data.MovieServiceClient
import com.example.grupo_pdm.databinding.FragmentUserPerfilBinding
import kotlinx.coroutines.launch

class UserPerfilFragment : Fragment(R.layout.fragment_user_perfil) {

    private var _binding: FragmentUserPerfilBinding? = null
    private val binding get() = _binding!!

    private val viewModel: UserProfileViewModel by viewModels()
    private var pendingCameraUri: android.net.Uri? = null

    // Abre a galeria para escolher uma imagem.
    private val pickImageLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            if (uri == null) return@registerForActivityResult
            val filename = getDisplayName(uri) ?: "profile.jpg"
            val bytes = readBytes(uri) ?: return@registerForActivityResult
            updatePhotoFromBytes(bytes, filename)
        }

    // Abre a câmera e usa a URI temporária criada.
    private val takePictureLauncher =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (!success) return@registerForActivityResult
            val uri = pendingCameraUri ?: return@registerForActivityResult
            val bytes = readBytes(uri) ?: return@registerForActivityResult
            updatePhotoFromBytes(bytes, "profile.jpg")
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentUserPerfilBinding.bind(view)

        setupTopBar()
        if (!initSession()) return
        bindActions()
        observeViewModel()
        loadUserFromPrefs()
    }

    // Configura o TopBar com navegação padrão.
    private fun setupTopBar() {
        bindTopBarNavigation(binding.topBar)
    }

    // Garante sessão válida e aplica credenciais no client.
    private fun initSession(): Boolean {
        val prefs = requireActivity().getSharedPreferences("prefs", 0)
        val hasSession = prefs.contains("username") && prefs.contains("userId") && prefs.contains("password")
        if (!hasSession) {
            goToSignIn()
            return false
        }
        val username = prefs.getString("username", "") ?: ""
        val password = prefs.getString("password", "") ?: ""
        if (username.isNotBlank() && password.isNotBlank()) {
            MovieServiceClient.setCredentials(username, password)
        }
        return true
    }

    // Liga botões de UI com ações.
    private fun bindActions() {
        val prefs = requireActivity().getSharedPreferences("prefs", 0)
        binding.btnLogout.setOnClickListener {
            prefs.edit {
                remove("username")
                remove("password")
                remove("role")
                remove("userId")
            }
            MovieServiceClient.clearCredentials()
            goToSignIn()
        }
        binding.btnEditPhoto.setOnClickListener { showPhotoOptions() }
    }

    // Dispara o carregamento do utilizador com base nas prefs.
    private fun loadUserFromPrefs() {
        val prefs = requireActivity().getSharedPreferences("prefs", 0)
        val userId = prefs.getInt("userId", -1)
        if (userId > 0) {
            viewModel.loadUser(userId)
        }
    }

    // Observa os estados do ViewModel e atualiza a UI.
    private fun observeViewModel() {
        val prefs = requireActivity().getSharedPreferences("prefs", 0)
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.user.collect { result ->
                when (result) {
                    is ApiResult.Success -> renderUser(result.data, prefs)
                    is ApiResult.Failure -> renderUserFallback(prefs)
                    else -> Unit
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.picture.collect { result ->
                if (result is ApiResult.Success) {
                    binding.imgUserProfile.load(result.data) { crossfade(true) }
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.updatePicture.collect { result ->
                when (result) {
                    is ApiResult.Success -> Log.d("UserPerfil", "Foto atualizada com sucesso")
                    is ApiResult.Failure -> Log.e("UserPerfil", "Erro ao atualizar foto: ${result.error.detail}")
                    else -> Unit
                }
            }
        }
    }

    // Renderiza dados do utilizador a partir da API.
    private fun renderUser(user: com.example.grupo_pdm.data.UserSelfResponse, prefs: android.content.SharedPreferences) {
        val usernameText = user.username ?: prefs.getString("username", "") ?: ""
        val subtitle = user.email ?: user.description ?: ""
        val dob = user.dateOfBirth ?: "--"
        binding.txtUserName.text = usernameText
        binding.txtUserEmail.text = subtitle
        binding.txtUserDob.text = "Nascimento: $dob"
    }

    // Fallback com dados mínimos das prefs.
    private fun renderUserFallback(prefs: android.content.SharedPreferences) {
        val usernameText = prefs.getString("username", "") ?: ""
        binding.txtUserName.text = usernameText
        binding.txtUserEmail.text = ""
        binding.txtUserDob.text = "Nascimento: --"
    }

    private fun getDisplayName(uri: android.net.Uri): String? {
        val cursor = requireContext().contentResolver.query(
            uri,
            arrayOf(OpenableColumns.DISPLAY_NAME),
            null,
            null,
            null
        ) ?: return null
        cursor.use {
            return if (it.moveToFirst()) {
                it.getString(0)
            } else {
                null
            }
        }
    }

    private fun readBytes(uri: android.net.Uri): ByteArray? {
        val bytes = requireContext().contentResolver.openInputStream(uri)?.use { it.readBytes() }
        return if (bytes == null || bytes.isEmpty()) null else bytes
    }

    // Mostra preview imediato e envia para a API.
    private fun updatePhotoFromBytes(bytes: ByteArray, filename: String) {
        binding.imgUserProfile.load(bytes) {
            crossfade(true)
        }
        val base64 = Base64.encodeToString(bytes, Base64.NO_WRAP)
        viewModel.updatePicture(filename, base64)
    }

    // Menu simples para editar/remover foto.
    private fun showPhotoOptions() {
        val options = arrayOf("Tirar foto", "Escolher da galeria", "Remover foto")
        AlertDialog.Builder(requireContext())
            .setTitle("Editar foto de perfil")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> launchCamera()
                    1 -> pickImageLauncher.launch("image/*")
                    2 -> {
                        viewModel.removePicture()
                        binding.imgUserProfile.setImageResource(R.drawable.ic_user_profile_placeholder)
                    }
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    // Cria URI temporária para a câmera.
    private fun launchCamera() {
        val file = java.io.File.createTempFile("profile_", ".jpg", requireContext().cacheDir)
        val uri = FileProvider.getUriForFile(
            requireContext(),
            "${requireContext().packageName}.fileprovider",
            file
        )
        pendingCameraUri = uri
        takePictureLauncher.launch(uri)
    }

    private fun goToSignIn() {
        findNavController().navigate(
            R.id.signInFragment,
            null,
            NavOptions.Builder()
                .setPopUpTo(R.id.nav_graph, true)
                .build()
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
