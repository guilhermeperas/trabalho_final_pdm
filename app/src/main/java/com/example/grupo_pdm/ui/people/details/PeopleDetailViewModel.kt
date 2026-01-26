package com.example.grupo_pdm.ui.people.details

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

class PeopleDetailViewModel(app: Application) : AndroidViewModel(app) {

    private val _person = MutableStateFlow<ApiResult<PersonResponse>?>(null)
    val person: StateFlow<ApiResult<PersonResponse>?> = _person.asStateFlow()

    fun loadPerson(id: Int) {
        viewModelScope.launch {
            MovieServiceClient.getPerson(id).collect { result ->
                _person.value = result
            }
        }
    }
}
