package com.lookout.data.repositories

import com.lookout.data.local.Preferences
import com.lookout.data.models.toDomain
import com.lookout.data.remote.GitHubService
import com.lookout.domain.models.GithubUser
import com.lookout.domain.repositories.GithubRepository
import javax.inject.Inject

class GitHubRepositoryImpl @Inject constructor(
    private val preferences: Preferences,
    private val gitHubService: GitHubService
): GithubRepository {

    override fun addGitHubToken(token: String) {
        preferences.setGitHubToken(token)
    }

    override suspend fun getUserInfo(): GithubUser {
        return gitHubService.getProfile().toDomain()
    }
}