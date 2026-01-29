package com.example.grupo_pdm.ui.admin.manage.actors

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.grupo_pdm.data.ApiResult
import com.example.grupo_pdm.data.CreatePersonRequest
import com.example.grupo_pdm.data.MovieServiceClient
import com.example.grupo_pdm.data.PersonResponse
import com.example.grupo_pdm.data.UpdatePersonRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AdminManageActorsViewModel(app: Application)
    : AndroidViewModel(app) {

    private val _actors =
        MutableStateFlow<ApiResult<List<PersonResponse>>?>(null)
    val actors: StateFlow<ApiResult<List<PersonResponse>>?> = _actors

    init {
        loadActors()
    }

    private fun loadActors() {
        viewModelScope.launch {
            MovieServiceClient.getActors().collect {
                _actors.value = it
            }
        }
    }

    fun createActor(request: CreatePersonRequest) {
        viewModelScope.launch {
            MovieServiceClient
                .createPerson(request)
                .collect {
                    loadActors()
                }
        }
    }
}
