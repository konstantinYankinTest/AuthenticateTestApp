package com.lookout.authenticatetestapp.presentation.main

import android.app.Application
import android.content.Intent
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.lookout.data.AccessToken
import com.lookout.data.local.Preferences
import com.lookout.domain.usecases.AuthGithubUseCase
import com.lookout.domain.usecases.GetAuthRequestUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import net.openid.appauth.AuthorizationException
import net.openid.appauth.AuthorizationService
import net.openid.appauth.TokenRequest
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    application: Application,
    private val saveGitHubTokenUseCase: AuthGithubUseCase,
    private val getAuthRequestUseCase: GetAuthRequestUseCase,
    private val preferences: Preferences
) : AndroidViewModel(application) {

    @Inject
    lateinit var authService: AuthorizationService

    private val _state: MutableState<UiState> = mutableStateOf(UiState.Empty)
    val state: State<UiState> = _state

    fun onAuthCodeReceived(tokenRequest: TokenRequest) {
        viewModelScope.launch {
            runCatching {
                saveGitHubTokenUseCase(
                    authService = authService,
                    tokenRequest = tokenRequest
                )
            }.onSuccess {
                _state.value = UiState.Loaded
            }.onFailure {
                _state.value = UiState.Error(it.message.orEmpty())
            }
        }
    }

    fun onAuthCodeFailed(exception: AuthorizationException) {
        _state.value = UiState.Error(exception.message.orEmpty())
    }

    fun start() {
        if (AccessToken.accessToken != null || preferences.refreshToken != null) {
            _state.value = UiState.Loaded
        } else {
            openLoginPage()
        }
    }

    private fun openLoginPage() {
        val customTabsIntent = CustomTabsIntent.Builder().build()
        val openAuthPageIntent = authService.getAuthorizationRequestIntent(
            getAuthRequestUseCase(),
            customTabsIntent
        )
        _state.value = UiState.OpenLoginPage(openAuthPageIntent)
    }

    override fun onCleared() {
        super.onCleared()

        authService.dispose()
    }

    sealed class UiState {
        object Empty : UiState()
        object Loading : UiState()
        object Loaded : UiState()
        data class Error(val message: String) : UiState()
        data class OpenLoginPage(val intent: Intent) : UiState()
    }
}

