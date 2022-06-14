package com.lookout.authenticatetestapp.presentation.main

import android.app.Application
import android.net.Uri
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.lookout.data.AccessToken
import com.lookout.data.local.Preferences
import com.lookout.domain.models.GithubUser
import com.lookout.domain.usecases.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    application: Application,
    private val saveGitHubTokenUseCase: AuthGithubUseCase,
    private val getAuthUrlUseCase: GetAuthUrlUseCase,
    private val preferences: Preferences,
    private val getUserInfoUseCase: GetUserInfoUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val getEndSessionUrlUseCase: GetEndSessionUrlUseCase,
    private val updateAccessTokenUseCase: UpdateAccessTokenUseCase
) : AndroidViewModel(application) {

    private val _state: MutableState<UiState> = mutableStateOf(UiState.Empty)
    val state: State<UiState> = _state

    init {
        checkEnableAccessToken()
    }

    fun openLoginPage() {
        _state.value = UiState.OpenLoginPage(getAuthUrlUseCase())
    }

    fun openLogoutPage() {
        _state.value = UiState.OpenLogout(getEndSessionUrlUseCase())
    }

    fun webLogoutComplete() {
        logoutUseCase()
        _state.value = UiState.Empty
    }

    fun updateTokens() {
        viewModelScope.launch {
            _state.value = UiState.Loading
            runCatching {
                updateAccessTokenUseCase()
            }.onSuccess {
                loadUserInfo()
            }.onFailure {
                _state.value = UiState.Error(it.message.orEmpty())
            }
        }
    }

    fun onAuthCodeReceived(url: String) {
        val uri = Uri.parse(url)
        val githubCode = uri.getQueryParameter("code") ?: ""
        viewModelScope.launch {
            _state.value = UiState.Loading
            runCatching {
                saveGitHubTokenUseCase(githubCode)
            }.onSuccess {
                loadUserInfo()
            }.onFailure {
                _state.value = UiState.Error(it.message.orEmpty())
            }
        }
    }

    private fun checkEnableAccessToken() {
        if (AccessToken.accessToken != null) {
            loadUserInfo()
        } else if (preferences.refreshToken == null) {
            _state.value = UiState.Empty
        } else {
            _state.value = UiState.ShowBiometricDialog
        }
    }

    private fun loadUserInfo() {
        viewModelScope.launch {
            _state.value = UiState.Loading
            runCatching {
                getUserInfoUseCase()
            }.onSuccess {
                _state.value = UiState.ShowProfile(it)
            }.onFailure {
                _state.value = UiState.Error(it.message.orEmpty())
            }
        }
    }

    sealed class UiState {

        object Empty : UiState()
        object Loading : UiState()
        object ShowBiometricDialog : UiState()

        data class ShowProfile(val user: GithubUser) : UiState()
        data class Error(val message: String) : UiState()
        data class OpenLogout(val url: String) : UiState()
        data class OpenLoginPage(val url: String) : UiState()
    }
}

