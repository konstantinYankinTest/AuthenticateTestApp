package com.lookout.data.models

import com.google.gson.annotations.SerializedName
import com.lookout.domain.models.GithubUser


data class GitHubUserDTO(
    @SerializedName("avatar_url") val avatar_url: String,
    @SerializedName("followers") val followers: Int,
    @SerializedName("following") val following: Int,
    @SerializedName("id") val id: Int,
    @SerializedName("login") val login: String,
    @SerializedName("owned_private_repos") val owned_private_repos: Int,
    @SerializedName("private_gists") val private_gists: Int,
    @SerializedName("public_gists") val public_gists: Int,
    @SerializedName("public_repos") val public_repos: Int,
    @SerializedName("total_private_repos") val total_private_repos: Int
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
