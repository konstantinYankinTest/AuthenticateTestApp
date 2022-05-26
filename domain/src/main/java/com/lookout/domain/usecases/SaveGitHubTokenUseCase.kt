package com.lookout.domain.usecases

import com.lookout.domain.repositories.GithubRepository
import javax.inject.Inject

class SaveGitHubTokenUseCase @Inject constructor(
    private val githubRepository: GithubRepository
) {
    operator fun invoke(token: String) = githubRepository.addGitHubToken(token)
}