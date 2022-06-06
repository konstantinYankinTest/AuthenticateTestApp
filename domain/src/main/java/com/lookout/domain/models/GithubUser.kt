package com.lookout.domain.models

data class GithubUser(
    val repos: Int,
    val gists: Int,
    val name: String,
    val image: String,
    val followers: Int,
    val following: Int
) {
    companion object {
        fun test() = GithubUser(
            repos = 3,
            gists = 3,
            name = "Test",
            image = "https://picsum.photos/id/237/200/300",
            followers = 4,
            following = 5
        )
    }

}
