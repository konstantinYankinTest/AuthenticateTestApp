package com.lookout.authenticatetestapp.presentation.github

import android.app.Application
import android.content.Intent
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.lookout.data.AccessToken
import com.lookout.domain.models.GithubUser
import com.lookout.domain.usecases.GetEndSessionRequestUseCase
import com.lookout.domain.usecases.GetUserInfoUseCase
import com.lookout.domain.usecases.LogoutUseCase
import com.lookout.domain.usecases.UpdateAccessTokenUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import net.openid.appauth.AuthorizationService
import javax.inject.Inject

@HiltViewModel
class GithubViewModel @Inject constructor(
    application: Application,
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

    fun logout() {
        val customTabsIntent = CustomTabsIntent.Builder().build()
        val logoutPageIntent = authService.getEndSessionRequestIntent(
            getEndSessionRequestUseCase(),
            customTabsIntent
        )
        _state.value = UiState.OpenLogout(logoutPageIntent)
    }

    fun webLogoutComplete() {
        logoutUseCase()
        _state.value = UiState.CloseGithubActivity
    }

    fun updateTokens() {
        viewModelScope.launch {
            runCatching {
                updateAccessTokenUseCase(authService)
            }.onSuccess {
                _state.value = UiState.ReadyForRequest
            }.onFailure {
                _state.value = UiState.Error(it.message.orEmpty())
            }
        }
    }

    fun loadUserInfo() {
        viewModelScope.launch {
            _state.value = UiState.Loading
            runCatching {
                getUserInfoUseCase()
            }.onSuccess {
                _state.value = UiState.Loaded(it)
            }.onFailure {
                _state.value = UiState.Error(it.message.orEmpty())
            }
        }
    }

    fun checkEnableAccessToken() {
        if (AccessToken.accessToken != null) {
            _state.value = UiState.ReadyForRequest
        } else {
            _state.value = UiState.UpdateToken
        }
    }

    override fun onCleared() {
        super.onCleared()

        authService.dispose()
    }

    sealed class UiState {
        object Empty : UiState()
        object Loading : UiState()
        object UpdateToken : UiState()
        object ReadyForRequest : UiState()
        data class Loaded(
            val user: GithubUser
        ) : UiState()

        data class Error(val message: String) : UiState()
        data class OpenLogout(val intent: Intent) : UiState()
        object CloseGithubActivity : UiState()
    }
}