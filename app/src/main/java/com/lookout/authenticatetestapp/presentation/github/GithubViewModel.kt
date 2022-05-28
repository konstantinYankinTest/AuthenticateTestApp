package com.lookout.authenticatetestapp.presentation.github

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lookout.domain.models.GithubUser
import com.lookout.domain.usecases.ClearGithubTokenUseCase
import com.lookout.domain.usecases.GetUserInfoUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GithubViewModel @Inject constructor(
    private val getUserInfoUseCase: GetUserInfoUseCase,
    private val clearGithubTokenUseCase: ClearGithubTokenUseCase
) : ViewModel() {

    private val _user: MutableState<GithubUser?> = mutableStateOf(null)
    val user: State<GithubUser?> = _user

    init {
        getUser()
    }

    private fun getUser() {
        viewModelScope.launch {
            val user = getUserInfoUseCase()
            _user.value = user
        }
    }

    fun signOut() {
        clearGithubTokenUseCase.invoke()
    }
}