package com.lookout.data.repositories

import com.lookout.data.api.GithubApi
import com.lookout.data.local.Preferences
import com.lookout.data.models.toDomain
import com.lookout.domain.models.GithubUser
import com.lookout.domain.repositories.GithubRepository
import javax.inject.Inject

class GitHubRepositoryImpl @Inject constructor(
    private val preferences: Preferences,
    private val gitHubService: GithubApi
) : GithubRepository {

    override fun addGithubToken(token: String) {
        preferences.accessToken = token
    }

    override fun clearGithubToken() {
        preferences.accessToken = null
    }

    override suspend fun getUserInfo(): GithubUser {
        return gitHubService.getCurrentUser().toDomain()
    }
}