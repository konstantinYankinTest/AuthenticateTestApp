package com.lookout.domain.repositories

import com.lookout.domain.models.GithubUser

interface GithubRepository {

    fun addGitHubToken(token: String)

    suspend fun getUserInfo(): GithubUser
}