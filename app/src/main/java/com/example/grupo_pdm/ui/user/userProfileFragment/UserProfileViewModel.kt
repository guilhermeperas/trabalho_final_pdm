package com.example.grupo_pdm.ui.user.userProfileFragment

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.grupo_pdm.data.ApiResult
import com.example.grupo_pdm.data.CreatePictureRequest
import com.example.grupo_pdm.data.MovieServiceClient
import com.example.grupo_pdm.data.UserSelfResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class UserProfileViewModel(app: Application) : AndroidViewModel(app) {

    // Estado do utilizador
    private val _user = MutableStateFlow<ApiResult<UserSelfResponse>?>(null)
    val user = _user.asStateFlow()

    // Estado da foto (bytes)
    private val _picture = MutableStateFlow<ApiResult<ByteArray>?>(null)
    val picture = _picture.asStateFlow()

    // Estado das operações de update/delete da foto
    private val _updatePicture = MutableStateFlow<ApiResult<Unit>?>(null)
    val updatePicture = _updatePicture.asStateFlow()

    private var currentUserId: Int? = null

    // Carrega dados do utilizador e foto com base no userId
    fun loadUser(userId: Int) {
        currentUserId = userId
        viewModelScope.launch {
            MovieServiceClient.getCurrentUser().collect { result ->
                _user.value = result
            }
        }
        viewModelScope.launch {
            _picture.value = ApiResult.Loading(0)
            _picture.value = MovieServiceClient.getUserPictureBytes(userId)
        }
    }

    // Atualiza a foto do utilizador (base64) e faz refresh da imagem
    fun updatePicture(filename: String, base64: String) {
        viewModelScope.launch {
            _updatePicture.value = ApiResult.Loading(0)
            val req = CreatePictureRequest(
                filename = filename,
                data = base64
            )
            val result = MovieServiceClient.setCurrentUserPicture(req)
            _updatePicture.value = result
            if (result is ApiResult.Success) {
                currentUserId?.let { id ->
                    _picture.value = MovieServiceClient.getUserPictureBytes(id)
                }
            }
        }
    }

    // Remove a foto do utilizador
    fun removePicture() {
        viewModelScope.launch {
            _updatePicture.value = ApiResult.Loading(0)
            val result = MovieServiceClient.deleteCurrentUserPicture()
            _updatePicture.value = result
            if (result is ApiResult.Success) {
                _picture.value = ApiResult.Failure(
                    com.example.grupo_pdm.data.ProblemDetails(
                        "info",
                        "No Picture",
                        204,
                        "Picture removed"
                    )
                )
            }
        }
    }
}
