package com.example.grupo_pdm.ui.admin.manage.actors

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.grupo_pdm.R
import com.example.grupo_pdm.data.ApiResult
import com.example.grupo_pdm.databinding.FragmentAdminManageActorsBinding
import com.example.grupo_pdm.ui.adapters.AdminManageActorsAdapter
import kotlinx.coroutines.launch

class AdminManageActorsFragment
    : Fragment(R.layout.fragment_admin_manage_actors) {

    private var _binding: FragmentAdminManageActorsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AdminManageActorsViewModel by viewModels()
    private val adapter = AdminManageActorsAdapter(
        onEdit = { actor ->
            // SIMULA edição (por agora não faz nada)
            android.util.Log.d("ADMIN", "Editar ator: ${actor.name}")
        },
        onDelete = { actor ->
            // SIMULA delete (por agora não faz nada)
            android.util.Log.d("ADMIN", "Apagar ator: ${actor.name}")
        }
    )


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentAdminManageActorsBinding.bind(view)

        binding.rvActors.layoutManager = LinearLayoutManager(requireContext())
        binding.rvActors.adapter = adapter

        binding.fabAddActor.setOnClickListener {
            showCreateActorDialog()
        }

        observeData()
    }

    private fun observeData() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.actors.collect { result ->
                when (result) {
                    is ApiResult.Success -> adapter.submitList(result.data)
                    is ApiResult.Failure -> {
                        Toast.makeText(
                            requireContext(),
                            "Falha ao carregar atores: ${result.error.detail}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    else -> {}
                }
            }
        }
    }

    private fun showCreateActorDialog() {
        val container = LinearLayout(requireContext())
        container.orientation = LinearLayout.VERTICAL
        val padding = (16 * resources.displayMetrics.density).toInt()
        container.setPadding(padding, padding, padding, padding)

        val nameInput = EditText(requireContext())
        nameInput.hint = "Nome do ator"
        container.addView(nameInput)

        val dobInput = EditText(requireContext())
        dobInput.hint = "Data de nascimento (YYYY-MM-DD)"
        container.addView(dobInput)

        val pictureNameInput = EditText(requireContext())
        pictureNameInput.hint = "Nome do ficheiro da foto (opcional)"
        container.addView(pictureNameInput)

        val pictureBase64Input = EditText(requireContext())
        pictureBase64Input.hint = "Foto em Base64 (opcional)"
        pictureBase64Input.minLines = 2
        pictureBase64Input.maxLines = 6
        container.addView(pictureBase64Input)

        AlertDialog.Builder(requireContext())
            .setTitle("Adicionar ator")
            .setView(container)
            .setPositiveButton("Criar") { _, _ ->
                val name = nameInput.text.toString().trim()
                if (name.isEmpty()) {
                    Toast.makeText(
                        requireContext(),
                        "Nome não pode estar vazio",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setPositiveButton
                }
                val dob = dobInput.text.toString().trim().ifEmpty { null }
                val pictureName = pictureNameInput.text.toString().trim()
                val pictureBase64 = pictureBase64Input.text.toString().trim()
                val pictures = if (pictureName.isNotEmpty() && pictureBase64.isNotEmpty()) {
                    listOf(com.example.grupo_pdm.data.CreatePictureRequest(pictureName, pictureBase64))
                } else {
                    null
                }
                viewModel.createActor(
                    com.example.grupo_pdm.data.CreatePersonRequest(
                        name = name,
                        dateOfBirth = dob,
                        pictures = pictures
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
