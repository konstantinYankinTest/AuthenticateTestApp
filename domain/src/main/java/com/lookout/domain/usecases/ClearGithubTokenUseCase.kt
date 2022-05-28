package com.lookout.domain.usecases

import com.lookout.domain.repositories.GithubRepository
import javax.inject.Inject

class ClearGithubTokenUseCase@Inject constructor(
    private val githubRepository: GithubRepository
) {
    operator fun invoke() = githubRepository.clearGithubToken()
}