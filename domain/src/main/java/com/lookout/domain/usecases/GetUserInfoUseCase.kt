package com.lookout.domain.usecases

import com.lookout.domain.models.GithubUser
import com.lookout.domain.repositories.UserRepository
import javax.inject.Inject

class GetUserInfoUseCase @Inject constructor(
    private val gitHubRepository: UserRepository,
) {

    suspend operator fun invoke(): GithubUser {
        return gitHubRepository.getUserInfo()
    }
}