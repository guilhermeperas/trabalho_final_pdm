package com.example.grupo_pdm.ui.admin.manage.users

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.grupo_pdm.data.ApiResult
import com.example.grupo_pdm.data.UserResponse
import com.example.grupo_pdm.data.MovieServiceClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AdminManageUsersViewModel(app: Application)
    : AndroidViewModel(app) {

    private val _users =
        MutableStateFlow<ApiResult<List<UserResponse>>?>(null)
    val users = _users.asStateFlow()

    init {
        loadUsers()
    }

    private fun loadUsers() {
        viewModelScope.launch {
            MovieServiceClient
                .getUsers()
                .collect {
                    _users.value = it
                }
        }
    }
}
