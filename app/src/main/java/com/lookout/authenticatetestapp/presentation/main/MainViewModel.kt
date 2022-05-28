package com.lookout.authenticatetestapp.presentation.main

import androidx.lifecycle.ViewModel
import com.lookout.domain.usecases.SaveGithubTokenUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val saveGitHubTokenUseCase: SaveGithubTokenUseCase
) : ViewModel() {

    fun setToken(token: String) = saveGitHubTokenUseCase(token)
}