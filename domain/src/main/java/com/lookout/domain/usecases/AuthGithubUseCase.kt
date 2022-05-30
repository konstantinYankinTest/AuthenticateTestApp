package com.lookout.domain.usecases

import com.lookout.domain.repositories.AuthRepository
import net.openid.appauth.AuthorizationService
import net.openid.appauth.TokenRequest
import javax.inject.Inject

class AuthGithubUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(
        authService: AuthorizationService,
        tokenRequest: TokenRequest
    ) = authRepository.performTokenRequest(authService, tokenRequest)
}