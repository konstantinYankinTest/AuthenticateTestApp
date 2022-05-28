package com.lookout.data.models

import com.lookout.domain.models.GithubUser
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GitHubUserDTO(
    val avatar_url: String,
    val followers: Int,
    val following: Int,
    val id: Int,
    val login: String,
    val owned_private_repos: Int,
    val private_gists: Int,
    val public_gists: Int,
    val public_repos: Int,
    val total_private_repos: Int
)

fun GitHubUserDTO.toDomain(): GithubUser =
    GithubUser(
        name = login,
        image = avatar_url,
        followers = followers,
        following = following,
        gists = private_gists + public_gists,
        repos = total_private_repos + public_repos
    )
