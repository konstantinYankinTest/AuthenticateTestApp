package com.lookout.data.api

import com.lookout.data.models.GitHubUserDTO
import retrofit2.http.GET

interface GithubApi {
    @GET("user")
    suspend fun getCurrentUser(
    ): GitHubUserDTO
}
