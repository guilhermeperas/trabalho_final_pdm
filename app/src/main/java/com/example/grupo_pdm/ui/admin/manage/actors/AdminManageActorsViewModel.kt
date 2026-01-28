package com.example.grupo_pdm.ui.admin.manage.actors

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.grupo_pdm.data.ApiResult
import com.example.grupo_pdm.data.MovieServiceClient
import com.example.grupo_pdm.data.PersonResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AdminManageActorsViewModel(app: Application)
    : AndroidViewModel(app) {

    private val _actors =
        MutableStateFlow<ApiResult<List<PersonResponse>>?>(null)
    val actors = _actors.asStateFlow()

    init {
        loadActors()
    }

    fun loadActors() {
        viewModelScope.launch {
            MovieServiceClient.getActors().collect {
                _actors.value = it
            }
        }
    }

    // --- CRUD (API) ---

    fun createActor(name: String, photo: String) {
        // TODO POST /people
    }

    fun updateActor(id: Int, name: String, photo: String) {
        // TODO PUT /people/{id}
    }

    fun deleteActor(id: Int) {
        // TODO DELETE /people/{id}
    }
}