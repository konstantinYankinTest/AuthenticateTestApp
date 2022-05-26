package com.lookout.domain.usecases

import com.lookout.domain.models.GithubUser
import com.lookout.domain.repositories.GithubRepository
import javax.inject.Inject

class GetUserInfoUseCase @Inject constructor(
    private val gitHubRepository: GithubRepository,
) {

    suspend operator fun invoke(): GithubUser {
        return gitHubRepository.getUserInfo()
    }
}