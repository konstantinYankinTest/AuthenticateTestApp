package com.lookout.authenticatetestapp.presentation.main

import android.app.Application
import android.content.Intent
import androidx.browser.customtabs.CustomTabsIntent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.lookout.data.AccessToken
import com.lookout.data.local.Preferences
import com.lookout.domain.usecases.AuthGithubUseCase
import com.lookout.domain.usecases.GetAuthRequestUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
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

    private val _state = MutableStateFlow<State>(State.Empty)
    val state: StateFlow<State> = _state

    private val _sideEffects = MutableSharedFlow<SideEffect>()
    val sideEffects: SharedFlow<SideEffect> = _sideEffects


    fun onAuthCodeReceived(tokenRequest: TokenRequest) {
        viewModelScope.launch {
            runCatching {
                saveGitHubTokenUseCase(
                    authService = authService,
                    tokenRequest = tokenRequest
                )
            }.onSuccess {
                _sideEffects.emit(SideEffect.NavigateToDetails)
            }.onFailure {
                _state.emit(State.Error(it.message.orEmpty()))
            }
        }
    }

    fun onAuthCodeFailed(exception: AuthorizationException) {
        viewModelScope.launch {
            _state.emit(State.Error(exception.message.orEmpty()))
        }
    }

    fun start() {
        viewModelScope.launch {
            if (AccessToken.accessToken != null || preferences.refreshToken != null) {
                _sideEffects.emit(SideEffect.NavigateToDetails)
            } else {
                openLoginPage()
            }
        }

    }

    private suspend fun openLoginPage() {
            val customTabsIntent = CustomTabsIntent.Builder().build()
            val openAuthPageIntent = authService.getAuthorizationRequestIntent(
                getAuthRequestUseCase(),
                customTabsIntent
            )
            _sideEffects.emit(SideEffect.OpenLoginPage(openAuthPageIntent))
    }

    override fun onCleared() {
        super.onCleared()

        authService.dispose()
    }

    sealed class State {
        object Empty : State()
        object Loading : State()
        object Loaded : State()
        data class Error(val message: String) : State()
    }

    sealed class SideEffect {
        data class OpenLoginPage(val intent: Intent) : SideEffect()
        object NavigateToDetails : SideEffect()
    }
}

