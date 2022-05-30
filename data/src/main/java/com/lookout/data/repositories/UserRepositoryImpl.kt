package com.lookout.data.repositories

import com.lookout.data.api.GithubApi
import com.lookout.data.local.Preferences
import com.lookout.data.models.toDomain
import com.lookout.domain.models.GithubUser
import com.lookout.domain.repositories.UserRepository
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val gitHubService: GithubApi
) : UserRepository {

    override suspend fun getUserInfo(): GithubUser {
        return gitHubService.getCurrentUser().toDomain()
    }
}