package com.lookout.domain.usecases

import com.lookout.domain.repositories.AuthRepository
import javax.inject.Inject

class AuthGithubUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(
      code: String
    ) = authRepository.performTokenRequest(code)
}