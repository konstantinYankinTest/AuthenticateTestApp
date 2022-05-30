package com.lookout.authenticatetestapp.presentation.github

import android.app.Application
import android.content.Intent
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.lookout.domain.models.GithubUser
import com.lookout.domain.usecases.GetEndSessionRequestUseCase
import com.lookout.domain.usecases.GetUserInfoUseCase
import com.lookout.domain.usecases.LogoutUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import net.openid.appauth.AuthorizationService
import javax.inject.Inject

@HiltViewModel
class GithubViewModel @Inject constructor(
    application: Application,
    private val getUserInfoUseCase: GetUserInfoUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val getEndSessionRequestUseCase: GetEndSessionRequestUseCase
) : AndroidViewModel(application) {

    @Inject lateinit var authService: AuthorizationService

    private val _state: MutableState<UiState> = mutableStateOf(UiState.Empty)
    val state: State<UiState> = _state

    private val _sideEffects = MutableSharedFlow<SideEffect>()
    val sideEffects: SharedFlow<SideEffect> = _sideEffects


    init {
        loadUserInfo()
    }

    fun logout() {
        viewModelScope.launch {
            val customTabsIntent = CustomTabsIntent.Builder().build()

            val logoutPageIntent = authService.getEndSessionRequestIntent(
                getEndSessionRequestUseCase(),
                customTabsIntent
            )
            _sideEffects.emit(SideEffect.OpenLogout(logoutPageIntent))
        }
    }

    fun webLogoutComplete() {
        viewModelScope.launch {
            logoutUseCase()
            _sideEffects.emit(SideEffect.CloseGithubActivity)
        }
    }

    private fun loadUserInfo() {
        viewModelScope.launch {
            runCatching {
                getUserInfoUseCase()
            }.onSuccess {
                _state.value = UiState.Loaded(it)
            }.onFailure {
                _state.value = UiState.Error(it.message.orEmpty())
            }
        }
    }

    override fun onCleared() {
        super.onCleared()

        authService.dispose()
    }

    sealed class UiState {
        object Empty : UiState()
        object Loading : UiState()
        data class Loaded(
            val user: GithubUser
        ) : UiState()
        data class Error(val message: String) : UiState()
    }

    sealed class SideEffect {
        data class OpenLogout(val intent: Intent) : SideEffect()
        object CloseGithubActivity : SideEffect()
    }
}