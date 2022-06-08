package com.lookout.authenticatetestapp.presentation.main

import android.app.Application
import android.content.Intent
import androidx.browser.customtabs.CustomTabColorSchemeParams
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.lookout.authenticatetestapp.R
import com.lookout.data.AccessToken
import com.lookout.data.local.Preferences
import com.lookout.domain.models.GithubUser
import com.lookout.domain.usecases.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import net.openid.appauth.AuthorizationException
import net.openid.appauth.AuthorizationResponse
import net.openid.appauth.AuthorizationService
import net.openid.appauth.TokenRequest
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    application: Application,
    private val saveGitHubTokenUseCase: AuthGithubUseCase,
    private val getAuthRequestUseCase: GetAuthRequestUseCase,
    private val preferences: Preferences,
    private val getUserInfoUseCase: GetUserInfoUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val getEndSessionRequestUseCase: GetEndSessionRequestUseCase,
    private val updateAccessTokenUseCase: UpdateAccessTokenUseCase
) : AndroidViewModel(application) {

    @Inject
    lateinit var authService: AuthorizationService

    private val _state: MutableState<UiState> = mutableStateOf(UiState.Empty)
    val state: State<UiState> = _state

    init {
        checkEnableAccessToken()
    }

    override fun onCleared() {
        super.onCleared()

        authService.dispose()
    }

    fun handleAuthResponseIntent(intent: Intent) {
        val exception = AuthorizationException.fromIntent(intent)
        val tokenExchangeRequest = AuthorizationResponse.fromIntent(intent)
            ?.createTokenExchangeRequest()
        when {
            exception != null -> onAuthCodeFailed(exception)
            tokenExchangeRequest != null -> onAuthCodeReceived(tokenExchangeRequest)
        }
    }

    fun openLoginPage() {
        val openAuthPageIntent = authService.getAuthorizationRequestIntent(
            getAuthRequestUseCase(),
            getCustomTabIntent()
        )
        _state.value = UiState.OpenLoginPage(openAuthPageIntent)
    }

    fun openLogoutPage() {
        val logoutPageIntent = authService.getEndSessionRequestIntent(
            getEndSessionRequestUseCase(),
            getCustomTabIntent()
        )
        _state.value = UiState.OpenLogout(logoutPageIntent)
    }

    fun webLogoutComplete() {
        logoutUseCase()
        _state.value = UiState.Empty
    }

    fun updateTokens() {
        viewModelScope.launch {
            _state.value = UiState.Loading
            runCatching {
                updateAccessTokenUseCase(authService)
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

    private fun onAuthCodeReceived(tokenRequest: TokenRequest) {
        viewModelScope.launch {
            _state.value = UiState.Loading
            runCatching {
                saveGitHubTokenUseCase(
                    authService = authService,
                    tokenRequest = tokenRequest
                )
            }.onSuccess {
                loadUserInfo()
            }.onFailure {
                _state.value = UiState.Error(it.message.orEmpty())
            }
        }
    }

    private fun onAuthCodeFailed(exception: AuthorizationException) {
        _state.value = UiState.Error(exception.message.orEmpty())
    }

    private fun getCustomTabIntent(): CustomTabsIntent {
        val colorSchemeParams = CustomTabColorSchemeParams.Builder()
            .setToolbarColor(R.color.mainColor)
            .build()
        return CustomTabsIntent.Builder()
            .setColorSchemeParams(CustomTabsIntent.COLOR_SCHEME_LIGHT, colorSchemeParams)
            .setColorSchemeParams(CustomTabsIntent.COLOR_SCHEME_DARK, colorSchemeParams)
            .setShowTitle(true)
            .build()
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
        data class OpenLogout(val intent: Intent) : UiState()
        data class OpenLoginPage(val intent: Intent) : UiState()
    }
}

