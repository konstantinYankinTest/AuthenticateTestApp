package com.lookout.domain.repositories

import com.lookout.domain.models.GithubUser

interface UserRepository {

    suspend fun getUserInfo(): GithubUser
}