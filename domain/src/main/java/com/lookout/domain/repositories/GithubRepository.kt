package com.lookout.domain.repositories

import com.lookout.domain.models.GithubUser

interface GithubRepository {

    fun addGithubToken(token: String)

    fun clearGithubToken()

    suspend fun getUserInfo(): GithubUser
}